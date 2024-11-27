package fr.insa.chatsystem.controller;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.contact.ContactList;
import fr.insa.chatsystem.model.message.Message;
import fr.insa.chatsystem.model.network.UDPMessage;
import fr.insa.chatsystem.model.network.UDPMessageType;
import fr.insa.chatsystem.model.service.ContactService;
import fr.insa.chatsystem.model.service.MessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Random;

import static fr.insa.chatsystem.Main.self;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * JUnit test class for the {@link NetworkController} class.
 * This class tests various methods of the NetworkController class.
 *
 * <p>
 * The tests cover different scenarios related to message handling, including connect, response, set username,
 * change username, disconnect, and chat message processing.
 * </p>
 *
 * <p>
 * The tests utilize Mockito to create mock objects for the ContactService and MessageService,
 * allowing controlled testing of the NetworkController's behavior.
 * </p>
 */
class NetworkControllerTest {
    private NetworkController networkController;

    @Mock
    private ContactService contactService;

    @Mock
    private MessageService messageService;

    /**
     * Sets up the test environment before each test method is executed.
     */
    @BeforeEach
    void setUp() {
        self = new Contact("self", true);
        MockitoAnnotations.openMocks(this);
        networkController = new NetworkController(contactService, messageService);
    }

    /**
     * Cleans up the test environment after each test method is executed.
     */
    @AfterEach
    void tearDown() {
        ContactList instance = ContactList.getInstance();
        for (Contact contact : instance.getAllContacts()) {
            instance.removeContact(contact.getUsername());
        }
    }

    /**
     * Test method for {@link NetworkController#messageReceived(UDPMessage)}.
     * Tests the scenario where a message is received from the same contact (self).
     *
     * <p>
     * This test verifies that the {@code messageReceived} method correctly handles the case where
     * a message is received from the same contact (self). It ensures that no interactions with ContactService
     * or MessageService occur.
     * </p>
     */
    @Test
    void testMessageReceivedFromMyself() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            self.setAddress(InetAddress.getByName(socket.getLocalAddress().getHostAddress()));
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }

        UDPMessage udpMessage = new UDPMessage(self.getAddress(), self.getUsername(), "test", UDPMessageType.CONNECT, "test");
        networkController.messageReceived(udpMessage);

        verify(contactService, times(0)).getContactByUsername(udpMessage.getSenderUsername());
        verify(contactService, times(0)).insertContact(any(Contact.class));
        verify(contactService, times(0)).updateContact(any(Contact.class));
        verify(messageService, times(0)).insertMessage(any(Message.class));

    }

    /**
     * Test method for {@link NetworkController#messageReceived(UDPMessage)}.
     * Tests the scenario where a connect message is received.
     *
     * <p>
     * This test verifies that the {@code messageReceived} method correctly handles the case where
     * a connect message is received. It ensures that the appropriate method invocations are made on the ContactService
     * and ContactList.
     * </p>
     *
     * @throws UnknownHostException If an error occurs while creating the InetAddress.
     */
    @Test
    void testConnectMessageReceived() throws UnknownHostException {
        UDPMessage udpMessage = new UDPMessage(generateRandomIPv4Address(), "test", "test", UDPMessageType.CONNECT, "test");
        networkController.messageReceived(udpMessage);
    }

    /**
     * Test method for {@link NetworkController#messageReceived(UDPMessage)}.
     * Tests the scenario where a response message is received, and the contact does not exist.
     *
     * <p>
     * This test verifies that the {@code messageReceived} method correctly handles the case where
     * a response message is received, and the contact does not exist. It ensures that the appropriate method invocations
     * are made on the ContactService and ContactList.
     * </p>
     *
     * @throws UnknownHostException If an error occurs while creating the InetAddress.
     */
    @Test
    void testResponseMessageReceivedContactDoesNotExist() throws UnknownHostException {
        Contact contact = new Contact(2, "username", 0);
        contact.setAddress(generateRandomIPv4Address());
        UDPMessage udpMessage = new UDPMessage(contact.getAddress(), contact.getUsername(), null, UDPMessageType.RESPONSE, "test");

        when(contactService.getContactByUsername(udpMessage.getSenderUsername())).thenReturn(Optional.empty());
        when(contactService.insertContact(new Contact(udpMessage.getSenderUsername(), false))).thenReturn(Optional.of(contact));
        networkController.messageReceived(udpMessage);

        verify(contactService, times(1)).getContactByUsername(udpMessage.getSenderUsername());
    }

    /**
     * Test method for {@link NetworkController#messageReceived(UDPMessage)}.
     * Tests the scenario where a set username message is received, and the contact already exists.
     *
     * <p>
     * This test verifies that the {@code messageReceived} method correctly handles the case where
     * a set username message is received, and the contact already exists. It ensures that the appropriate method invocations
     * are made on the ContactService and ContactList.
     * </p>
     *
     * @throws UnknownHostException If an error occurs while creating the InetAddress.
     */
    @Test
    void testSetUsernameMessageReceivedContactExists() throws UnknownHostException {
        Contact contact = new Contact(2, "username", 0);
        contact.setAddress(generateRandomIPv4Address());
        UDPMessage udpMessage = new UDPMessage(contact.getAddress(), contact.getUsername(), null, UDPMessageType.SET_USERNAME, "test");
        ContactList contactList = ContactList.getInstance();

        when(contactService.getContactByUsername(udpMessage.getSenderUsername())).thenReturn(Optional.of(contact));
        networkController.messageReceived(udpMessage);

        assertTrue(contactList.getAllContacts().contains(contact));
        verify(contactService, times(1)).getContactByUsername(udpMessage.getSenderUsername());
        verify(contactService, times(0)).insertContact(any(Contact.class));
    }

    /**
     * Test method for {@link NetworkController#messageReceived(UDPMessage)}.
     * Tests the scenario where a change username message is received, and the contact already exists.
     *
     * <p>
     * This test verifies that the {@code messageReceived} method correctly handles the case where
     * a change username message is received, and the contact already exists. It ensures that the appropriate method invocations
     * are made on the ContactService and ContactList.
     * </p>
     *
     * @throws UnknownHostException If an error occurs while creating the InetAddress.
     */
    @Test
    void testChangeUsernameMessageReceivedContactExists() throws UnknownHostException {
        Contact contact = new Contact(2, "username", 0);
        Contact updatedContact = new Contact(2, "renamed username", 0);
        contact.setAddress(generateRandomIPv4Address());
        UDPMessage udpMessage = new UDPMessage(contact.getAddress(), "renamed username", contact.getUsername(), UDPMessageType.CHANGE_USERNAME, "test");
        ContactList contactList = ContactList.getInstance();
        contactList.addContact(contact);

        when(contactService.getContactByUsername(udpMessage.getSenderPreviousUsername())).thenReturn(Optional.of(contact));
        when(contactService.updateContact(updatedContact)).thenReturn(Optional.of(updatedContact));
        networkController.messageReceived(udpMessage);

        assertTrue(contactList.getAllContacts().stream().anyMatch(c -> c.getUsername().equals(updatedContact.getUsername())));
        verify(contactService, times(1)).getContactByUsername(udpMessage.getSenderPreviousUsername());
    }

    /**
     * Test method for {@link NetworkController#messageReceived(UDPMessage)}.
     * Tests the scenario where a disconnect message is received.
     *
     * <p>
     * This test verifies that the {@code messageReceived} method correctly handles the case where
     * a disconnect message is received. It ensures that the appropriate method invocations are made on the ContactList.
     * </p>
     *
     * @throws UnknownHostException If an error occurs while creating the InetAddress.
     */
    @Test
    void testDisconnectMessageReceived() throws UnknownHostException {
        Contact contact = new Contact(2, "username", 0);
        contact.setAddress(generateRandomIPv4Address());
        UDPMessage udpMessage = new UDPMessage(contact.getAddress(), contact.getUsername(), null, UDPMessageType.DISCONNECT, "test");

        ContactList contactList = ContactList.getInstance();
        contactList.addContact(contact);
        assertTrue(contactList.getAllContacts().contains(contact));

        networkController.messageReceived(udpMessage);

        assertFalse(contactList.getAllContacts().stream().anyMatch(c -> c.getUsername().equals(contact.getUsername())));
    }

    /**
     * Test method for {@link NetworkController#messageReceived(UDPMessage)}.
     * Tests the scenario where a chat message is received.
     *
     * <p>
     * This test verifies that the {@code messageReceived} method correctly handles the case where
     * a chat message is received. It ensures that the appropriate method invocations are made on the ContactService
     * and MessageService.
     * </p>
     *
     * @throws UnknownHostException If an error occurs while creating the InetAddress.
     */
    @Test
    void testChatMessageReceived() throws UnknownHostException {
        Contact contact = new Contact(2, "username", 0);
        contact.setAddress(generateRandomIPv4Address());
        UDPMessage udpMessage = new UDPMessage(contact.getAddress(), contact.getUsername(), null, UDPMessageType.CHAT_MESSAGE, "test");

        when(contactService.getContactByUsername(udpMessage.getSenderUsername())).thenReturn(Optional.of(contact));
        networkController.messageReceived(udpMessage);

        verify(contactService, times(1)).getContactByUsername(udpMessage.getSenderUsername());
        verify(messageService, times(1)).insertMessage(any(Message.class));
    }

    /**
     * Helper method to generate a random IPv4 address.
     *
     * @return A randomly generated IPv4 address.
     * @throws UnknownHostException If an error occurs while creating the InetAddress.
     */
    private static InetAddress generateRandomIPv4Address() throws UnknownHostException {
        Random random = new Random();

        byte[] ipv4Bytes = new byte[4];
        random.nextBytes(ipv4Bytes);

        ipv4Bytes[0] = (byte) ((ipv4Bytes[0] & 0xFF) % 254 + 1);
        for (int i = 1; i < 4; i++) {
            ipv4Bytes[i] = (byte) (ipv4Bytes[i] & 0xFF);
        }

        return InetAddress.getByAddress(ipv4Bytes);
    }
}