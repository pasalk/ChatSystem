package fr.insa.chatsystem.model.repository;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.logger.message.ErrorMessages;
import fr.insa.chatsystem.model.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The MessageRepository class provides methods to interact with the SQLite database for managing messages.
 * It includes methods for creating a messages table, inserting messages, and retrieving conversation history.
 */
public class MessageRepository extends Repository {
    private static final Logger LOGGER = LogManager.getLogger(MessageRepository.class);
    private static final MessageRepository INSTANCE = new MessageRepository();

    public static MessageRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Interface for classes that want to observe incoming messages.
     */
    public interface Observer {
        /**
         * Called when a new message is received and inserted into the database.
         */
        void messageInserted(Message message);
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
     * Creates the 'messages' table in the SQLite database to store message information.
     * The table includes columns for message_id (auto-incremented primary key), sender_contact_id,
     * receiver_contact_id, and content. Foreign key constraints reference the 'contacts' table.
     */
    public void createMessagesTable() {
        sqLiteConnector.connectDatabase();
        sqLiteConnector.createStatement();

        String query = """
                CREATE TABLE messages (
                message_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                sender_contact_id INTEGER,
                receiver_contact_id INTEGER,
                content TEXT,
                FOREIGN KEY(sender_contact_id) REFERENCES contacts(contact_id),
                FOREIGN KEY(receiver_contact_id) REFERENCES contacts(contact_id))
                """;

        sqLiteConnector.statementExecuteUpdate(query);
        sqLiteConnector.closeStatement();
        sqLiteConnector.disconnectDatabase();
    }

    /**
     * Inserts a new message into the 'messages' table.
     *
     * @param message The Message object to be inserted.
     */
    public void insertMessage(Message message) {
        sqLiteConnector.connectDatabase();

        String query = """
                INSERT INTO messages(sender_contact_id, receiver_contact_id, content)
                VALUES(?, ?, ?);
                """;

        sqLiteConnector.createPreparedStatement(query);
        sqLiteConnector.preparedStatementSetString(1, message.getSenderContact().getContactId().toString());
        sqLiteConnector.preparedStatementSetString(2, message.getReceiverContact().getContactId().toString());
        sqLiteConnector.preparedStatementSetString(3, message.getContent());

        sqLiteConnector.preparedStatementExecuteUpdate();
        sqLiteConnector.closePreparedStatement();
        sqLiteConnector.disconnectDatabase();

        for (Observer observer : observers) {
            observer.messageInserted(message);
        }
    }

    /**
     * Retrieves the conversation history between two contacts from the 'messages' table.
     *
     * @param selfId    The contact_id of the logged-in user.
     * @param contactId The contact_id of the other participant in the conversation.
     * @return A List of Message objects representing the conversation history.
     */
    public List<Message> getConversationHistory(Integer selfId, Integer contactId) {
        List<Message> conversationHistory = new ArrayList<>();
        try {
            sqLiteConnector.connectDatabase();

            String query = """
                    SELECT * FROM contacts WHERE contact_id == ?
                    """;
            sqLiteConnector.createPreparedStatement(query);
            sqLiteConnector.preparedStatementSetString(1, selfId.toString());

            ResultSet rs = sqLiteConnector.preparedStatementExecuteQuery();
            Contact self = new Contact();
            while (rs.next()) {
                self = new Contact(rs.getInt("contact_id"), rs.getString("username"), rs.getInt("is_me"));
            }
            rs.close();
            sqLiteConnector.closePreparedStatement();

            query = """
                    SELECT * FROM contacts WHERE contact_id == ?
                    """;
            sqLiteConnector.createPreparedStatement(query);
            sqLiteConnector.preparedStatementSetString(1, contactId.toString());

            rs = sqLiteConnector.preparedStatementExecuteQuery();
            Contact contact = new Contact();
            while (rs.next()) {
                contact = new Contact(rs.getInt("contact_id"), rs.getString("username"), rs.getInt("is_me"));
            }
            rs.close();
            sqLiteConnector.closePreparedStatement();

            query = """
                    SELECT * FROM messages
                    WHERE (sender_contact_id = ? OR sender_contact_id = ?)
                    AND (receiver_contact_id = ? OR receiver_contact_id = ?)
                    """;
            sqLiteConnector.createPreparedStatement(query);
            sqLiteConnector.preparedStatementSetString(1, selfId.toString());
            sqLiteConnector.preparedStatementSetString(2, contactId.toString());
            sqLiteConnector.preparedStatementSetString(3, selfId.toString());
            sqLiteConnector.preparedStatementSetString(4, contactId.toString());

            rs = sqLiteConnector.preparedStatementExecuteQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getString("content"));

                if (selfId == rs.getInt("sender_contact_id")) {
                    message.setSenderContact(self);
                    message.setReceiverContact(contact);
                } else {
                    message.setSenderContact(contact);
                    message.setReceiverContact(self);
                }

                conversationHistory.add(message);
            }
            rs.close();
            sqLiteConnector.closePreparedStatement();

            sqLiteConnector.disconnectDatabase();
        } catch (SQLException | NullPointerException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }

        return conversationHistory;
    }

    public List<Message> getMessagesByContactId(Integer contactId) {
        List<Message> messages = new ArrayList<>();
        try {
            sqLiteConnector.connectDatabase();

            String query = """
                    SELECT * FROM contacts WHERE contact_id == ?
                    """;
            sqLiteConnector.createPreparedStatement(query);
            sqLiteConnector.preparedStatementSetString(1, contactId.toString());

            ResultSet rs = sqLiteConnector.preparedStatementExecuteQuery();
            Contact contact = new Contact();
            while (rs.next()) {
                contact = new Contact(rs.getInt("contact_id"), rs.getString("username"), rs.getInt("is_me"));
            }
            rs.close();
            sqLiteConnector.closePreparedStatement();

            query = """
                    SELECT * FROM messages
                    WHERE sender_contact_id = ? OR receiver_contact_id = ?
                    """;
            sqLiteConnector.createPreparedStatement(query);
            sqLiteConnector.preparedStatementSetString(1, contactId.toString());
            sqLiteConnector.preparedStatementSetString(2, contactId.toString());

            rs = sqLiteConnector.preparedStatementExecuteQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getString("content"));
                messages.add(message);
            }
            rs.close();
            sqLiteConnector.closePreparedStatement();

            sqLiteConnector.disconnectDatabase();
        } catch (SQLException | NullPointerException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }

        return messages;
    }

    public void deleteMessage(Integer messageId) {
        sqLiteConnector.connectDatabase();

        String query = "DELETE FROM messages WHERE message_id = ?";
        sqLiteConnector.createPreparedStatement(query);
        sqLiteConnector.preparedStatementSetString(1, messageId.toString());

        sqLiteConnector.preparedStatementExecuteUpdate();
    }

}
