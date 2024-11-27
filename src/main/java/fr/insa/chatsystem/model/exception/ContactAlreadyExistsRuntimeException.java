package fr.insa.chatsystem.model.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Exception thrown when attempting to create a contact with a username that already exists.
 */
public class ContactAlreadyExistsRuntimeException extends RuntimeException {

    private static final Logger LOGGER = LogManager.getLogger(ContactAlreadyExistsRuntimeException.class);

    /**
     * Constructs a new ContactAlreadyExistsRuntimeException with the specified error message.
     *
     * @param message The detail message describing the error.
     */
    public ContactAlreadyExistsRuntimeException(String message) {
        super(message);
        LOGGER.error(message);
    }

}
