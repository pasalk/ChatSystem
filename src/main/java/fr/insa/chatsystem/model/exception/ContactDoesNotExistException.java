package fr.insa.chatsystem.model.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Exception thrown when attempting to perform an operation on a contact that does not exist.
 */
public class ContactDoesNotExistException extends Exception {

    private static final Logger LOGGER = LogManager.getLogger(ContactDoesNotExistException.class);

    /**
     * Constructs a new ContactDoesNotExistException with the specified error message.
     *
     * @param message The detail message describing the error.
     */
    public ContactDoesNotExistException(String message) {
        super(message);
        LOGGER.error(message);
    }

}
