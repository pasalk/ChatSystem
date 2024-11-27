package fr.insa.chatsystem.model.message;

import fr.insa.chatsystem.model.contact.Contact;

/**
 * Represents a message in the chat system.
 */
public class Message {

    private Integer messageId;
    private Contact senderContact;
    private Contact receiverContact;
    private final String content;

    /**
     * Constructor for creating a Message with a specified messageId and content.
     *
     * @param messageId The unique identifier for the message.
     * @param content   The content of the message.
     */
    public Message(Integer messageId, String content) {
        this.messageId = messageId;
        this.content = content;
    }

    /**
     * Constructor for creating a Message with specified senderContact, receiverContact, and content.
     *
     * @param senderContact   The contact who sent the message.
     * @param receiverContact The contact who will receive the message.
     * @param content         The content of the message.
     */
    public Message(Contact senderContact, Contact receiverContact, String content) {
        this.senderContact = senderContact;
        this.receiverContact = receiverContact;
        this.content = content;
    }

    /**
     * Getter method for retrieving the messageId.
     *
     * @return The messageId of the message.
     */
    public Integer getMessageId() {
        return messageId;
    }

    /**
     * Getter method for retrieving the senderContact.
     *
     * @return The senderContact of the message.
     */
    public Contact getSenderContact() {
        return senderContact;
    }

    /**
     * Setter method for setting the senderContact.
     *
     * @param senderContact The new senderContact for the message.
     */
    public void setSenderContact(Contact senderContact) {
        this.senderContact = senderContact;
    }

    /**
     * Getter method for retrieving the receiverContact.
     *
     * @return The receiverContact of the message.
     */
    public Contact getReceiverContact() {
        return receiverContact;
    }

    /**
     * Setter method for setting the receiverContact.
     *
     * @param receiverContact The new receiverContact for the message.
     */
    public void setReceiverContact(Contact receiverContact) {
        this.receiverContact = receiverContact;
    }

    /**
     * Getter method for retrieving the content.
     *
     * @return The content of the message.
     */
    public String getContent() {
        return content;
    }

}
