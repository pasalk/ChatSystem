package fr.insa.chatsystem.model.logger.message;

/**
 * Class containing static trace messages used in the logging system.
 */
public class TraceMessages {

    /**
     * Trace message indicating the reception of a message.
     * Placeholders %s will be replaced with the message type and address.
     */
    public static final String MESSAGE_RECEIVED = "Received message of type: %s from address: %s.";

    /**
     * Trace message indicating the sending of a message.
     * Placeholders %s will be replaced with the message type and address.
     */
    public static final String MESSAGE_SENT = "Sent message of type: %s to address: %s.";

    /**
     * Trace message indicating the broadcast of a message to all addresses.
     * Placeholder %s will be replaced with the message type.
     */
    public static final String MESSAGE_BROADCAST = "Sent message of type: %s to all addresses.";

}
