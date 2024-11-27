package fr.insa.chatsystem.model.network;

import com.google.gson.Gson;

import java.net.InetAddress;

/**
 * Represents a UDP message in the chat system.
 */
public class UDPMessage {

    private final InetAddress senderAddress;
    private final String senderUsername;
    private final UDPMessageType type;
    private final String content;
    private String senderPreviousUsername;

    /**
     * Constructs a UDPMessage object with specified parameters.
     *
     * @param senderAddress         the InetAddress of the message sender
     * @param senderUsername        the username of the message sender
     * @param type                  the type of the UDP message (e.g., CHAT, JOIN, LEAVE)
     * @param content               the content of the message
     */
    public UDPMessage(InetAddress senderAddress, String senderUsername, UDPMessageType type, String content) {
        this.senderAddress = senderAddress;
        this.senderUsername = senderUsername;
        this.type = type;
        this.content = content;
    }

    /**
     * Constructs a UDPMessage object with specified parameters, including the previous username of the sender.
     *
     * @param senderAddress         the InetAddress of the message sender
     * @param senderUsername        the username of the message sender
     * @param senderPreviousUsername the previous username of the message sender
     * @param type                  the type of the UDP message (e.g., CHAT, JOIN, LEAVE)
     * @param content               the content of the message
     */
    public UDPMessage(InetAddress senderAddress, String senderUsername, String senderPreviousUsername, UDPMessageType type, String content) {
        this.senderAddress = senderAddress;
        this.senderUsername = senderUsername;
        this.type = type;
        this.content = content;
        this.senderPreviousUsername = senderPreviousUsername;
    }

    /**
     * Constructs a UDPMessage object from packet data received in string format.
     *
     * @param senderAddress the InetAddress of the message sender
     * @param packetData    the packet data received in string format
     */
    public UDPMessage(InetAddress senderAddress, String packetData) {
        Gson gson = new Gson();
        UDPMessage udpMessage = gson.fromJson(packetData, UDPMessage.class);
        this.senderAddress = senderAddress;
        this.senderUsername = udpMessage.senderUsername;
        this.type = udpMessage.type;
        this.content = udpMessage.content;
        this.senderPreviousUsername = udpMessage.senderPreviousUsername;
    }

    /**
     * Returns a string representation of the UDPMessage.
     *
     * @return a string representation of the UDPMessage
     */
    @Override
    public String toString() {
        return type.name() + " " + senderUsername + " " + content;
    }

    /**
     * Gets the InetAddress of the message sender.
     *
     * @return the InetAddress of the message sender
     */
    public InetAddress getSenderAddress() {
        return senderAddress;
    }

    /**
     * Gets the username of the message sender.
     *
     * @return the username of the message sender
     */
    public String getSenderUsername() {
        return senderUsername;
    }

    /**
     * Gets the type of the UDP message.
     *
     * @return the type of the UDP message
     */
    public UDPMessageType getType() {
        return type;
    }

    /**
     * Gets the content of the message.
     *
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the previous username of the message sender.
     *
     * @return the previous username of the message sender
     */
    public String getSenderPreviousUsername() {
        return senderPreviousUsername;
    }

}
