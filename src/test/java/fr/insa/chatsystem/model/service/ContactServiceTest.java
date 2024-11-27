package fr.insa.chatsystem.model.service;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.contact.ContactList;
import fr.insa.chatsystem.model.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

import static fr.insa.chatsystem.Main.self;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * JUnit test class for the {@link ContactService} class.
 * This class tests various methods of the ContactService class.
 *
 * <p>
 * The tests cover different scenarios related to contact creation, retrieval, updating, and existence checks.
 * </p>
 *
 * <p>
 * The tests utilize Mockito to create a mock object for the ContactRepository, allowing controlled testing
 * of the ContactService's behavior.
 * </p>
 */
public class ContactServiceTest {
    private ContactService contactService;

    @Mock
    private ContactRepository contactRepository;

    /**
     * Sets up the necessary instances and environment before each test.
     * Creates a new instance of ContactService and initializes the mock ContactRepository.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        self = new Contact("self", true);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            self.setAddress(InetAddress.getByName(socket.getLocalAddress().getHostAddress()));
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }

        contactService = new ContactService(contactRepository);
    }

    /**
     * Tests the creation of the 'contacts' table in the database.
     * Verifies that the corresponding method in the ContactRepository is called.
     */
    @Test
    void testCreateContactsTable() {
        contactService.createContactsTable();
        verify(contactRepository, times(1)).createContactsTable();
    }

    /**
     * Tests the existence check of a contact in the database.
     * Verifies that the ContactService correctly checks if a contact exists in the database.
     */
    @Test
    void testContactExistsInDatabase() {
        Contact contact = new Contact("Existing Contact", false);
        List<Contact> contacts = List.of(self, contact);

        when(contactRepository.getAllContacts()).thenReturn(contacts);

        assertTrue(contactService.contactExistsInDatabase("Existing Contact"));
        assertFalse(contactService.contactExistsInDatabase("NonExisting Contact"));
    }

    /**
     * Tests the existence check of a contact in the active contacts list.
     * Verifies that the ContactService correctly checks if a contact exists in the active contacts list.
     */
    @Test
    void testContactExistsInActiveContacts() {
        Contact contact = new Contact("Active Contact", false);
        ContactList contactList = ContactList.getInstance();
        contactList.addContact(contact);

        assertTrue(contactService.contactExistsInActiveContacts("Active Contact"));
        assertFalse(contactService.contactExistsInActiveContacts("Inactive Contact"));

        contactList.removeContact("Active Contact");
    }

    /**
     * Tests the existence check of the 'contacts' table in the database.
     * Verifies that the ContactService correctly checks if the 'contacts' table exists.
     */
    @Test
    void testTableContactsExists() {
        when(contactRepository.tableExistsByTableName("contacts")).thenReturn(true);
        assertTrue(contactService.tableContactsExists());
    }

    /**
     * Tests the retrieval of a contact by username from the database.
     * Verifies that the ContactService correctly retrieves a contact by username from the database.
     */
    @Test
    void testGetContactByUsername() {

        Contact contact = new Contact(2, "Found Contact", 0);
        Optional<Contact> repositoryReturn = Optional.of(contact);

        when(contactRepository.getContactByUsername(contact.getUsername())).thenReturn(repositoryReturn);

        Optional<Contact> foundContact = contactService.getContactByUsername("Found Contact");
        assertTrue(foundContact.isPresent());
        assertEquals(contact.getUsername(), foundContact.get().getUsername());

        repositoryReturn = Optional.empty();
        when(contactRepository.getContactByUsername(any())).thenReturn(repositoryReturn);
        Optional<Contact> notFoundContact = contactService.getContactByUsername(any());
        assertFalse(notFoundContact.isPresent());
    }

    /**
     * Tests the insertion of a contact into the database.
     * Verifies that the ContactService correctly inserts a contact into the database.
     */
    @Test
    void testInsertContact() {
        Contact contact = new Contact("Contact", false);
        Optional<Contact> repositoryReturn = Optional.of(new Contact(1, "Contact", 0));

        when(contactRepository.insertContact(contact)).thenReturn(repositoryReturn);
        Optional<Contact> insertedContact = contactService.insertContact(contact);

        assertTrue(insertedContact.isPresent());
        assertEquals(contact.getUsername(), insertedContact.get().getUsername());
    }

    /**
     * Tests the retrieval of the self contact from the database.
     * Verifies that the ContactService correctly retrieves the self contact from the database.
     */
    @Test
    void testGetSelf() {
        Optional<Contact> repositoryReturn = Optional.of(self);

        when(contactRepository.getSelf()).thenReturn(repositoryReturn);
        Optional<Contact> foundSelf = contactService.getSelf();

        assertTrue(foundSelf.isPresent());
        assertEquals(self.getUsername(), foundSelf.get().getUsername());
        assertEquals(foundSelf.get().getIsMe(), 1);
    }

    /**
     * Tests the updating of a contact in the database.
     * Verifies that the ContactService correctly updates a contact in the database.
     */
    @Test
    void testUpdateContact() {
        Contact contact = new Contact(2,"Contact", 0);
        contact.setUsername("Updated Contact");
        Optional<Contact> repositoryReturn = Optional.of(contact);

        when(contactRepository.updateContact(contact)).thenReturn(repositoryReturn);
        Optional<Contact> updatedContact = contactService.updateContact(contact);

        assertTrue(updatedContact.isPresent());
        assertEquals(contact.getUsername(), updatedContact.get().getUsername());
        assertEquals(contact.getContactId(), updatedContact.get().getContactId());
    }
}
