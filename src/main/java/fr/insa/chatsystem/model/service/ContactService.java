package fr.insa.chatsystem.model.service;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.contact.ContactList;
import fr.insa.chatsystem.model.repository.ContactRepository;

import java.util.List;
import java.util.Optional;

import static fr.insa.chatsystem.Main.self;

/**
 * The ContactService class provides methods to interact with contacts,
 * including operations related to the contact repository and active contacts.
 */
public class ContactService {

    ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository){
        this.contactRepository = contactRepository;
    }

    /**
     * Creates the 'contacts' table in the database for storing contact information.
     */
    public void createContactsTable() {
        contactRepository.createContactsTable();
    }

    /**
     * Checks if a contact with the given username exists in the database.
     *
     * @param username The username to check.
     * @return True if the contact exists in the database and is not the logged-in user, otherwise false.
     */
    public boolean contactExistsInDatabase(String username) {
        List<Contact> databaseContacts = contactRepository.getAllContacts();
        return databaseContacts.stream().anyMatch(contact -> contact.getUsername().equals(username) && !contact.getUsername().equals(self.getUsername()));
    }

    /**
     * Checks if a contact with the given username exists in the active contacts list.
     *
     * @param username The username to check.
     * @return True if the contact exists in the active contacts list, otherwise false.
     */
    public boolean contactExistsInActiveContacts(String username) {
        ContactList activeContacts = ContactList.getInstance();
        return activeContacts.hasUsername(username);
    }

    /**
     * Checks if the 'contacts' table exists in the database.
     *
     * @return True if the 'contacts' table exists, otherwise false.
     */
    public boolean tableContactsExists() {
        return contactRepository.tableExistsByTableName("contacts");
    }

    /**
     * Deletes a contact from the 'contacts' table by its contact_id.
     *
     * @param contactId The id of the contact to be deleted.
     */
    public void deleteContact(Integer contactId) {
        contactRepository.deleteContact(contactId);
    }

    /**
     * Retrieves a contact by its username from the 'contacts' table.
     *
     * @param username The username of the desired contact.
     * @return An Optional containing the Contact object if found, otherwise an empty Optional.
     */
    public Optional<Contact> getContactByUsername(String username) {
        return contactRepository.getContactByUsername(username);
    }

    /**
     * Inserts a new contact into the 'contacts' table.
     *
     * @param contact The Contact object to be inserted.
     * @return An Optional containing the Contact object representing the newly inserted contact.
     */
    public Optional<Contact> insertContact(Contact contact) {
        return contactRepository.insertContact(contact);
    }

    /**
     * Retrieves an optional self contact from the database.
     *
     * @return An Optional containing the Contact object if found, otherwise an empty Optional.
     */
    public Optional<Contact> getSelf() {
        return contactRepository.getSelf();
    }

    /**
     * Updates an existing contact in the 'contacts' table.
     *
     * @param contact The Contact object with updated information.
     * @return An Optional containing the Contact object representing the updated contact.
     */
    public Optional<Contact> updateContact(Contact contact) {
        return contactRepository.updateContact(contact);
    }

}
