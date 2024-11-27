package fr.insa.chatsystem.model.contact;

import java.net.InetAddress;

/**
 * Represents a contact in a chat system.
 */
public class Contact {

    private Integer contactId;
    private String username;
    private Integer isMe;
    private InetAddress address;
    private String previousUsername;

    /**
     * Default constructor for the Contact class.
     */
    public Contact() {
    }

    /**
     * Constructor for creating a Contact with a specified username and address.
     *
     * @param username The username of the contact.
     * @param address  The InetAddress representing the contact's address.
     */
    public Contact(String username, InetAddress address) {
        this.username = username;
        this.address = address;
    }

    /**
     * Constructor for creating a Contact with specified contactId, username, and isMe status.
     *
     * @param contactId The unique identifier for the contact.
     * @param username  The username of the contact.
     * @param isMe      The status indicating whether the contact represents the local user.
     */
    public Contact(Integer contactId, String username, Integer isMe) {
        this.contactId = contactId;
        this.username = username;
        this.isMe = isMe;
    }

    /**
     * Constructor for creating a Contact with only a username.
     *
     * @param username The username of the contact.
     */
    public Contact(String username) {
        this.username = username;
    }

    /**
     * Constructor for creating a Contact with only a contactId.
     *
     * @param contactId The unique identifier for the contact.
     */
    public Contact(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Constructor for creating a Contact with a username and specifying whether it represents the local user.
     *
     * @param username The username of the contact.
     * @param isMe     The status indicating whether the contact represents the local user.
     */
    public Contact(String username, boolean isMe) {
        this.username = username;
        setIsMe(isMe);
    }

    /**
     * Getter method for retrieving the contactId.
     *
     * @return The contactId of the contact.
     */
    public Integer getContactId() {
        return contactId;
    }

    /**
     * Getter method for retrieving the username.
     *
     * @return The username of the contact.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter method for setting the username.
     *
     * @param username The new username for the contact.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter method for retrieving the InetAddress representing the contact's address.
     *
     * @return The InetAddress representing the contact's address.
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Setter method for setting the InetAddress representing the contact's address.
     *
     * @param address The new InetAddress representing the contact's address.
     */
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    /**
     * Getter method for retrieving the previous username.
     *
     * @return The previous username of the contact.
     */
    public String getPreviousUsername() {
        return previousUsername;
    }

    /**
     * Setter method for setting the previous username.
     *
     * @param previousUsername The previous username of the contact.
     */
    public void setPreviousUsername(String previousUsername) {
        this.previousUsername = previousUsername;
    }

    /**
     * Getter method for retrieving the isMe status.
     *
     * @return The isMe status of the contact.
     */
    public Integer getIsMe() {
        return isMe;
    }

    /**
     * Setter method for setting the isMe status based on a boolean value.
     *
     * @param isMe The boolean value indicating whether the contact represents the local user.
     */
    public void setIsMe(Boolean isMe) {
        if (isMe) {
            this.isMe = 1;
        } else {
            this.isMe = 0;
        }
    }

}
