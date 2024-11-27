package fr.insa.chatsystem.model.logger.message;

/**
 * Class containing static error messages used in the logging system.
 */
public class ErrorMessages {

    /**
     * Error message for a basic receive message error.
     */
    public static final String BASIC_ERROR = "Receive message error: ";

    /**
     * Error message for a database connection message error.
     */
    public static final String DATABASE_CONNECTION_MESSAGE_ERROR = "Database connection message error: ";

    /**
     * Error message for a network connection message error.
     */
    public static final String NETWORK_CONNECTION_MESSAGE_ERROR = "Network connection message error: ";

    /**
     * Error message for a send message error.
     * Placeholder %s will be replaced with the message type, and %s with the specific error message.
     */
    public static final String SEND_MESSAGE_ERROR = "Send %s message error: %s.";

    /**
     * Error message for a non-existent contact by ID.
     * Placeholder %d will be replaced with the contact ID.
     */
    public static final String CONTACT_DOES_NOT_EXIST_ID = "Contact with id: %d doesn't exist.";

    /**
     * Error message for a non-existent contact by username.
     * Placeholder %s will be replaced with the username.
     */
    public static final String CONTACT_DOES_NOT_EXIST_USERNAME = "Contact with username: %s doesn't exist.";

    /**
     * Error message for an already existing contact by username.
     * Placeholder %s will be replaced with the username.
     */
    public static final String CONTACT_ALREADY_EXISTS_USERNAME = "Contact with username: %s already exists.";

    /**
     * Error message for the inability to execute an SQL statement.
     */
    public static final String CAN_NOT_EXECUTE_SQL_STATEMENT = "Unable to execute a statement : ";

    /**
     * Error message for a corrupt database.
     */
    public static final String DATABASE_CORRUPTION = "Database is corrupt.";

    /**
     * Error message for the inability to delete a corrupt database.
     */
    public static final String UNABLE_TO_DELETE_DATABASE = "Unable to delete corrupt database. Please delete it manually.";

}
