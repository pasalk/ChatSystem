package fr.insa.chatsystem.controller;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.contact.ContactList;
import fr.insa.chatsystem.model.service.ContactService;
import fr.insa.chatsystem.model.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;

import static fr.insa.chatsystem.Main.self;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit test class for the {@link CommandController} class.
 * This class tests various methods of the CommandController class.
 *
 *  <p>
 *  The tests cover different scenarios related to the connection process, username setting and changing,
 *  disconnection, and message sending functionality.
 *  </p>
 *
 *  <p>
 *  The tests utilize Mockito to create mock objects for the ContactService and MessageService,
 *  allowing controlled testing of the CommandController's behavior.
 *  </p>
 *
 *  <p>
 *  Note: Due to the implementation of CommandController, each test needs to be run separately.
 *  </p>
 */
@Disabled("Because of the implementation of CommandController.class, each test needs to be run separately.")
class CommandControllerTest {
    private CommandController commandController;

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
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            self.setAddress(InetAddress.getByName(socket.getLocalAddress().getHostAddress()));
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }

        MockitoAnnotations.openMocks(this);
        commandController = new CommandController(contactService, messageService);
    }

    /**
     * Test method for the {@link CommandController#connectButtonClicked()} method.
     * Tests the scenario where tables do not exist.
     *
     * <p>
     * This test verifies that the {@code connectButtonClicked} method correctly handles the case where
     * database tables do not exist. It mocks the behavior of the ContactService and MessageService to simulate
     * the absence of tables and validates the expected method invocations.
     *  </p>
     */
    @Test
    void testConnectButtonClickedTablesDoNotExist() {
        when(contactService.tableContactsExists()).thenReturn(false);
        when(contactService.tableContactsExists()).thenReturn(true);
        when(contactService.getSelf()).thenReturn(Optional.empty());

        commandController.connectButtonClicked();

        verify(contactService, times(2)).tableContactsExists();
        verify(contactService, times(1)).createContactsTable();
        verify(messageService, times(1)).createMessagesTable();
        verify(contactService, times(1)).getSelf();
    }

    /**
     * Test method for the {@link CommandController#connectButtonClicked()} method.
     * Tests the scenario where tables exist, but self does not exist.
     *
     * <p>
     * This test verifies that the {@code connectButtonClicked} method correctly handles the case where
     * database tables exist, but the self user does not exist. It mocks the behavior of the ContactService and
     * MessageService to simulate table existence and a missing self user, and validates the expected method invocations.
     * </p>
     */
    @Test
    void testConnectButtonClickedTablesExistSelfDoesNotExist() {
        when(contactService.tableContactsExists()).thenReturn(true);
        when(messageService.tableMessagesExists()).thenReturn(true);
        when(contactService.getSelf()).thenReturn(Optional.empty());

        commandController.connectButtonClicked();

        verify(contactService, times(2)).tableContactsExists();
        verify(messageService, times(1)).tableMessagesExists();
        verify(contactService, times(1)).getSelf();
    }

    /**
     * Test method for the {@link CommandController#connectButtonClicked()} method.
     * Tests the scenario where tables and self exist.
     *
     * <p>
     * This test verifies that the {@code connectButtonClicked} method correctly handles the case where
     * database tables and the self user already exist. It mocks the behavior of the ContactService and MessageService
     * to simulate the existence of tables and self user. The test validates the expected method invocations.
     * </p>
     */
    @Test
    void testConnectButtonClickedTablesExistSelfExists() {
        when(contactService.tableContactsExists()).thenReturn(true);
        when(messageService.tableMessagesExists()).thenReturn(true);
        when(contactService.getSelf()).thenReturn(Optional.of(self));
        when(contactService.contactExistsInActiveContacts(self.getUsername())).thenReturn(false);

        commandController.connectButtonClicked();

        verify(contactService, times(2)).tableContactsExists();
        verify(messageService, times(1)).tableMessagesExists();
        verify(contactService, times(2)).getSelf();
        verify(contactService, times(1)).contactExistsInActiveContacts(self.getUsername());
    }


    /**
     * Test method for the {@link CommandController#usernameSet(String)} method.
     * Tests the scenario where a new username is set.
     *
     * <p>
     * This test verifies that the {@code usernameSet} method correctly handles the case where a new username is set.
     * It mocks the behavior of the ContactService to simulate the insertion of the self user with a new username
     * and validates the expected method invocations.
     * </p>
     */
    @Test
    void testUsernameSet() {
        String username = self.getUsername();

        when(contactService.insertContact(self)).thenReturn(Optional.of(self));
        commandController.usernameSet("username");

        assertNotEquals(username, self.getUsername());
        verify(contactService, times(1)).insertContact(self);
    }

    /**
     * Test method for the {@link CommandController#usernameChanged(String)} method.
     * Tests the scenario where the username is changed.
     *
     * <p>
     * This test verifies that the {@code usernameChanged} method correctly handles the case where the username is changed.
     * It mocks the behavior of the ContactService to simulate the update of the self user's username and validates
     * the expected method invocations.
     * </p>
     */
    @Test
    void testUsernameChanged() {
        String username = self.getUsername();

        when(contactService.updateContact(self)).thenReturn(Optional.of(self));
        commandController.usernameChanged("changed username");

        assertEquals(username, self.getPreviousUsername());
        verify(contactService, times(1)).updateContact(self);
    }

    /**
     * Test method for the {@link CommandController#sendButtonClicked(String, String)} method.
     * Tests the scenario where the contact exists and is active.
     *
     * <p>
     * This test verifies that the {@code sendButtonClicked} method correctly handles the case where the contact
     * exists and is active. It creates a test contact, adds it to the ContactList, mocks the behavior of the
     * ContactService to simulate getting the contact by username, and validates the expected method invocations.
     * </p>
     */
    @Test
    void testSendButtonClickedContactExistsAndActive() {
        Contact contact = new Contact("username", false);
        ContactList contactList = ContactList.getInstance();
        contactList.addContact(contact);

        when(contactService.getContactByUsername(contact.getUsername())).thenReturn(Optional.of(contact));
        commandController.sendButtonClicked("message", contact.getUsername());

        verify(contactService, times(1)).getContactByUsername(contact.getUsername());
        verify(messageService, times(1)).sendChatMessage(contact, "message");
    }
}