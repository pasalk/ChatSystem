package fr.insa.chatsystem.model.contact;

import fr.insa.chatsystem.model.exception.ContactAlreadyExistsRuntimeException;
import fr.insa.chatsystem.model.exception.ContactDoesNotExistRuntimeException;
import fr.insa.chatsystem.model.logger.message.ErrorMessages;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of active contacts in a chat system.
 */
public class ContactList {

    private static final ContactList INSTANCE = new ContactList();

    /**
     * Returns the singleton instance of ContactList.
     *
     * @return The singleton instance of ContactList.
     */
    public static ContactList getInstance() {
        return INSTANCE;
    }

    private final List<Contact> activeContacts;

    /**
     * Default constructor for the ContactList class.
     * Initializes the list of active contacts.
     */
    public ContactList() {
        activeContacts = new ArrayList<>();
    }

    /**
     * Interface for classes that want to observe changes of the contact list.
     */
    public interface Observer {
        void contactAdded(Contact contact);
        void contactRemoved(Contact contact);
        void contactRenamed(Contact contact);
    }

    private final List<Observer> observers = new ArrayList<>();

    /**
     * Adds an observer to the list of observers.
     *
     * @param observer The observer to be added.
     */
    public synchronized void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * Adds a contact to the list of active contacts.
     * Notifies observers about the addition.
     *
     * @param contact The contact to be added.
     * @throws ContactAlreadyExistsRuntimeException if a contact with the same username already exists.
     */
    public synchronized void addContact(Contact contact) {
        if (hasUsername(contact.getUsername())) {
            throw new ContactAlreadyExistsRuntimeException(String.format(ErrorMessages.CONTACT_ALREADY_EXISTS_USERNAME, contact.getUsername()));
        } else {
            activeContacts.add(contact);
            for (Observer observer : observers) {
                observer.contactAdded(contact);
            }
        }
    }

    /**
     * Removes a contact from the list of active contacts.
     * Notifies observers about the removal.
     *
     * @param username The username of the contact to be removed.
     */
    public synchronized void removeContact(String username) {
        Contact contact = getContactByUsernameIfExists(username);
        if (contact != null) {
            activeContacts.remove(contact);
            for (Observer observer : observers) {
                observer.contactRemoved(contact);
            }
        }
    }

    /**
     * Renames a contact in the list of active contacts.
     * Notifies observers about the renaming.
     *
     * @param renamedContact The contact with the new username.
     * @throws ContactDoesNotExistRuntimeException if the contact to be renamed does not exist.
     */
    public synchronized void renameContact(Contact renamedContact) {
        Contact contact = getContactByContactIdIfExists(renamedContact.getContactId());
        if (contact == null) {
            throw new ContactDoesNotExistRuntimeException(String.format(ErrorMessages.CONTACT_DOES_NOT_EXIST_ID, renamedContact.getContactId()));
        } else {
            contact.setUsername(renamedContact.getUsername());
            for (Observer observer : observers) {
                observer.contactRenamed(contact);
            }
        }
    }

    /**
     * Checks if a username already exists in the list of active contacts.
     *
     * @param username The username to check.
     * @return True if the username already exists, false otherwise.
     */
    public synchronized boolean hasUsername(String username) {
        return activeContacts.stream().anyMatch(contact -> contact.getUsername().equals(username));
    }

    /**
     * Gets a contact by username if it exists in the list of active contacts.
     *
     * @param username The username of the contact to retrieve.
     * @return The contact with the specified username, or null if not found.
     */
    public synchronized Contact getContactByUsernameIfExists(String username) {
        return activeContacts.stream().filter(contact -> contact.getUsername().equals(username)).findFirst().orElse(null);
    }

    /**
     * Gets a contact by contactId if it exists in the list of active contacts.
     *
     * @param contactId The contactId of the contact to retrieve.
     * @return The contact with the specified contactId, or null if not found.
     */
    public synchronized Contact getContactByContactIdIfExists(Integer contactId) {
        return activeContacts.stream().filter(contact -> contact.getContactId().equals(contactId)).findFirst().orElse(null);
    }

    /**
     * Gets a copy of the list of all active contacts.
     *
     * @return A copy of the list of all active contacts.
     */
    public synchronized List<Contact> getAllContacts() {
        return new ArrayList<>(this.activeContacts);
    }

}
