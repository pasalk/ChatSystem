package fr.insa.chatsystem.model.repository;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.logger.message.ErrorMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The ContactRepository class provides methods to interact with the SQLite database for managing contacts.
 * It includes methods for creating a 'contacts' table, inserting, updating, and retrieving contacts.
 */
public class ContactRepository extends Repository {

    private static final Logger LOGGER = LogManager.getLogger(ContactRepository.class);

    /**
     * Creates the 'contacts' table in the SQLite database to store contact information.
     * The table includes columns for contact_id (auto-incremented primary key) and username.
     */
    public void createContactsTable() {
        sqLiteConnector.connectDatabase();
        sqLiteConnector.createStatement();

        String query = """
                CREATE TABLE contacts (
                contact_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                username TEXT NOT NULL,
                is_me INTEGER NOT NULL)
                """;

        sqLiteConnector.statementExecuteUpdate(query);
        sqLiteConnector.closeStatement();
        sqLiteConnector.disconnectDatabase();
    }

    /**
     * Inserts a new contact into the 'contacts' table.
     *
     * @param contact The Contact object to be inserted.
     * @return The Contact object representing the newly inserted contact.
     */
    public Optional<Contact> insertContact(Contact contact) {
        sqLiteConnector.connectDatabase();

        String query = """
                INSERT INTO contacts(username, is_me) VALUES(?, ?);
                """;
        sqLiteConnector.createPreparedStatement(query);
        sqLiteConnector.preparedStatementSetString(1, contact.getUsername());
        sqLiteConnector.preparedStatementSetString(2, contact.getIsMe().toString());

        sqLiteConnector.preparedStatementExecuteUpdate();
        sqLiteConnector.closePreparedStatement();

        sqLiteConnector.disconnectDatabase();

        return getContactByUsername(contact.getUsername());
    }

    /**
     * Updates the existing contact in the 'contacts' table.
     *
     * @param contact The Contact object with updated information.
     * @return The Contact object representing the updated contact.
     */
    public Optional<Contact> updateContact(Contact contact) {
        sqLiteConnector.connectDatabase();

        String query = """
                UPDATE contacts SET username = ? WHERE contact_id = ?
                """;
        sqLiteConnector.createPreparedStatement(query);
        sqLiteConnector.preparedStatementSetString(1, contact.getUsername());
        sqLiteConnector.preparedStatementSetString(2, contact.getContactId().toString());

        sqLiteConnector.preparedStatementExecuteUpdate();
        sqLiteConnector.closePreparedStatement();
        sqLiteConnector.disconnectDatabase();

        return getContactByContactId(contact.getContactId());
    }

    /**
     * Retrieves a list of all contacts from the 'contacts' table.
     *
     * @return A List of Contact objects representing all contacts.
     */
    public List<Contact> getAllContacts() {
        List<Contact> databaseContacts = new ArrayList<>();

        try {
            sqLiteConnector.connectDatabase();
            sqLiteConnector.createStatement();

            String query = "SELECT * FROM contacts";
            ResultSet rs = sqLiteConnector.statementExecuteQuery(query);

            while (rs.next()) {
                Contact contact = new Contact(rs.getInt("contact_id"), rs.getString("username"), rs.getInt("is_me"));
                databaseContacts.add(contact);
            }

            rs.close();
            sqLiteConnector.closeStatement();
            sqLiteConnector.disconnectDatabase();

        } catch (SQLException | NullPointerException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }

        return databaseContacts;
    }

    /**
     * Retrieves a contact by its contact_id from the 'contacts' table.
     *
     * @param contactId The contact_id of the desired contact.
     * @return An Optional containing the Contact object if found, otherwise an empty Optional.
     */
    public Optional<Contact> getContactByContactId(Integer contactId) {
        Contact contact = null;

        try {
            sqLiteConnector.connectDatabase();

            String query = """
                    SELECT * FROM contacts WHERE contact_id == ?
                    """;
            sqLiteConnector.createPreparedStatement(query);
            sqLiteConnector.preparedStatementSetString(1, contactId.toString());

            ResultSet rs = sqLiteConnector.preparedStatementExecuteQuery();
            while (rs.next()) {
                contact = new Contact(rs.getInt("contact_id"), rs.getString("username"), rs.getInt("is_me"));
            }
            rs.close();

            sqLiteConnector.closePreparedStatement();
            sqLiteConnector.disconnectDatabase();
        } catch (SQLException | NullPointerException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }

        if (contact == null) {
            return Optional.empty();
        }

        return Optional.of(contact);
    }

    /**
     * Retrieves a contact by its username from the 'contacts' table.
     *
     * @param username The username of the desired contact.
     * @return An Optional containing the Contact object if found, otherwise an empty Optional.
     */
    public Optional<Contact> getContactByUsername(String username) {
        Contact contact = null;

        try {
            sqLiteConnector.connectDatabase();

            String query = """
                    SELECT * FROM contacts WHERE username == ?
                    """;
            sqLiteConnector.createPreparedStatement(query);
            sqLiteConnector.preparedStatementSetString(1, username);

            ResultSet rs = sqLiteConnector.preparedStatementExecuteQuery();
            while (rs.next()) {
                contact = new Contact(rs.getInt("contact_id"), rs.getString("username"), rs.getInt("is_me"));
            }
            rs.close();

            sqLiteConnector.closePreparedStatement();
            sqLiteConnector.disconnectDatabase();
        } catch (SQLException | NullPointerException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }

        if (contact == null) {
            return Optional.empty();
        }

        return Optional.of(contact);
    }

    /**
     * Retrieves an optional self contact from the database.
     * Such object is recognized by having the value of is_me set to 1.
     *
     * @return An Optional containing the Contact object if found, otherwise an empty Optional.
     */
    public Optional<Contact> getSelf() {
        Contact contact = null;

        try {
            sqLiteConnector.connectDatabase();

            String query = "SELECT * FROM contacts WHERE is_me = ?";
            sqLiteConnector.createPreparedStatement(query);
            sqLiteConnector.preparedStatementSetString(1, String.valueOf(1));

            ResultSet rs = sqLiteConnector.preparedStatementExecuteQuery();
            while (rs.next()) {
                contact = new Contact(rs.getInt("contact_id"), rs.getString("username"), rs.getInt("is_me"));
            }

            rs.close();
            sqLiteConnector.closePreparedStatement();
            sqLiteConnector.disconnectDatabase();
        } catch (SQLException | NullPointerException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }

        if (contact == null) {
            return Optional.empty();
        }

        return Optional.of(contact);
    }

    /**
     * Deletes a contact from the 'contacts' table by its contact_id.
     *
     * @param contactId The id of the contact to be deleted.
     */
    public void deleteContact(Integer contactId) {
        sqLiteConnector.connectDatabase();

        String query = "DELETE FROM contacts WHERE contact_id = ?";
        sqLiteConnector.createPreparedStatement(query);
        sqLiteConnector.preparedStatementSetString(1, contactId.toString());

        sqLiteConnector.preparedStatementExecuteUpdate();
    }

}
