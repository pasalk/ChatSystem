package fr.insa.chatsystem.model.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Runtime exception thrown when attempting to perform an operation on a contact that does not exist.
 */
public class ContactDoesNotExistRuntimeException extends RuntimeException {

    private static final Logger LOGGER = LogManager.getLogger(ContactDoesNotExistRuntimeException.class);

    /**
     * Constructs a new ContactDoesNotExistRuntimeException with the specified error message.
     *
     * @param message The detail message describing the error.
     */
    public ContactDoesNotExistRuntimeException(String message) {
        super(message);
        LOGGER.error(message);
    }

}
