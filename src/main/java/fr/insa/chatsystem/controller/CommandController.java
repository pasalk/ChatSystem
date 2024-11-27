package fr.insa.chatsystem.controller;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.contact.ContactList;
import fr.insa.chatsystem.model.exception.ContactDoesNotExistException;
import fr.insa.chatsystem.model.logger.message.ErrorMessages;
import fr.insa.chatsystem.model.logger.message.InfoMessages;
import fr.insa.chatsystem.model.network.UDPListener;
import fr.insa.chatsystem.model.repository.ContactRepository;
import fr.insa.chatsystem.model.repository.MessageRepository;
import fr.insa.chatsystem.model.service.ContactService;
import fr.insa.chatsystem.model.service.MessageService;
import fr.insa.chatsystem.view.View;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static fr.insa.chatsystem.Main.RECEIVE_PORT;
import static fr.insa.chatsystem.Main.self;
import static fr.insa.chatsystem.model.network.UDPSenderWrapper.*;

/**
 * Controller class responsible for managing user commands.
 */
public class CommandController implements View.Observer {

    private final Logger LOGGER = LogManager.getLogger(CommandController.class);

    /**
     * Indicator if the current user's old password is still unique, or it needs changing.
     */
    public static AtomicBoolean isUsernameChangeNeeded = new AtomicBoolean(false);

    private final ContactService contactService;
    private final MessageService messageService;

    private UDPListener udpListener;

    /**
     * The duration to wait for responses in milliseconds.
     */
    public final int RESPONSE_WAITING_MILIS = 5000;

    /**
     * Constructor for the CommandController class.
     * @param contactService
     * @param messageService
     */
    public CommandController(ContactService contactService, MessageService messageService) {
        this.contactService = contactService;
        this.messageService = messageService;
    }

    /**
     * Handles the connection process. Sends a connect message and waits for responses.
     * If the database file does not exist, it creates it.
     * If the self user already exists in the database, it fetches it and sends a set username message.
     */
    @Override
    public void connectButtonClicked() {

        LOGGER.info(InfoMessages.CONNECTING_INFO);

        findMyAddress();
        self.setIsMe(true);

        boolean tablesInitialized = contactService.tableContactsExists() && messageService.tableMessagesExists();
        if (!tablesInitialized) {
            initializeTables();
        }

        try {
            if (udpListener == null) {
                udpListener = new UDPListener(RECEIVE_PORT);
                udpListener.addObserver(new NetworkController(new ContactService(new ContactRepository()), new MessageService(MessageRepository.getInstance())));
                udpListener.start();
            } else {
                udpListener.startRunning();
            }
        } catch (SocketException e) {
            LOGGER.error(ErrorMessages.NETWORK_CONNECTION_MESSAGE_ERROR + e.getMessage());
            System.exit(1);
        }

        sendConnectMessage();

        try {
            Thread.sleep(RESPONSE_WAITING_MILIS);   // assume that all response messages should be received at this point
        } catch (InterruptedException ignored) {
        }

        boolean selfExists = contactService.tableContactsExists() && contactService.getSelf().isPresent();
        if (selfExists) {

            fetchSelfFromDatabase();

            if (contactService.contactExistsInActiveContacts(self.getUsername())) {
                CommandController.isUsernameChangeNeeded.set(true);
            } else {
                sendSetUsernameMessage();
                LOGGER.info(InfoMessages.CONNECTED_INFO + self.getAddress());
            }

        }

    }

    /**
     * Handles the event when a new username is set.
     * Checks if the selected username is available and creates the user accordingly.
     *
     * @param username The selected username.
     */
    @Override
    public void usernameSet(String username) {
        self.setUsername(username);
        self.setIsMe(true);
        InetAddress address = self.getAddress();

        Optional<Contact> insertedSelf = contactService.insertContact(self);
        insertedSelf.ifPresent(contact -> self = contact);

        self.setAddress(address);
        sendSetUsernameMessage();
        LOGGER.info(InfoMessages.CONNECTED_INFO + self.getAddress());
    }

    /**
     * Handles the event when the username is changed.
     * Checks if the selected username is available and updates the user accordingly.
     *
     * @param username The selected username.
     */
    @Override
    public void usernameChanged(String username) {

        String previousUsername = self.getUsername();
        self.setUsername(username);
        InetAddress address = self.getAddress();

        Optional<Contact> updatedSelf = contactService.updateContact(self);
        updatedSelf.ifPresent(contact -> self = contact);

        self.setPreviousUsername(previousUsername);
        self.setAddress(address);

        sendChangeUsernameMessage();
        LOGGER.info(InfoMessages.CONNECTED_INFO + self.getAddress());

    }

    /**
     * Handles the disconnection process. Sends a disconnect message and stops udp listener.
     */
    @Override
    public void disconnectButtonClicked() {
        sendDisconnectMessage();
        if (udpListener != null) {
            udpListener.stopRunning();
        }
    }

    /**
     * Handles the message sending process.
     */
    @Override
    public void sendButtonClicked(String message, String username) {
        Optional<Contact> databaseContact = contactService.getContactByUsername(username);
        ContactList activeContacts = ContactList.getInstance();
        Contact activeContact = activeContacts.getContactByUsernameIfExists(username);
        if (databaseContact.isEmpty() || activeContact == null) {
            LOGGER.error(String.format(ErrorMessages.CONTACT_DOES_NOT_EXIST_USERNAME, username));
            System.exit(1);
        }
        Contact contact = databaseContact.get();
        contact.setAddress(activeContact.getAddress());
        messageService.sendChatMessage(contact, message);
    }

    /**
     * Finds and sets the local address of the system using a UDP connection to a remote server.
     * The method creates a DatagramSocket, connects to a remote address (e.g., Google's public DNS server),
     * and retrieves the local address from the connected socket. The obtained local address is then set for the current
     * instance.
     * Note: This method may throw SocketException or UnknownHostException if there are issues with socket operations
     * or resolving the remote server's address. If an exception occurs, the method logs an error message and exits the
     * system.
     */
    public void findMyAddress() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            self.setAddress(InetAddress.getByName(socket.getLocalAddress().getHostAddress()));
        } catch (SocketException | UnknownHostException e) {
            LOGGER.error(ErrorMessages.NETWORK_CONNECTION_MESSAGE_ERROR + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Creates database tables for contacts and messages.
     */
    private void initializeTables() {
        contactService.createContactsTable();
        messageService.createMessagesTable();
    }

    /**
     * Retrieves the current user's username from the database and sets its IP address.
     */
    private void fetchSelfFromDatabase() {
        try {
            InetAddress address = self.getAddress();
            self = contactService.getSelf()
                    .orElseThrow(() -> new ContactDoesNotExistException(String.format(ErrorMessages.CONTACT_DOES_NOT_EXIST_ID, self.getContactId())));
            self.setAddress(address);
        } catch (ContactDoesNotExistException e) {
            LOGGER.error(ErrorMessages.DATABASE_CORRUPTION);

            Path path = Paths.get("database-" + self.getAddress().getHostAddress() + ".db");
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                LOGGER.error(ErrorMessages.UNABLE_TO_DELETE_DATABASE);
            }
            System.exit(1);
        }
    }

}
