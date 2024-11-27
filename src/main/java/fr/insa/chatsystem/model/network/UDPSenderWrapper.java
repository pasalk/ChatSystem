package fr.insa.chatsystem.model.network;

import fr.insa.chatsystem.Main;
import fr.insa.chatsystem.model.logger.message.ErrorMessages;
import fr.insa.chatsystem.model.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static fr.insa.chatsystem.Main.self;

/**
 * The UDPMessageSenderWrapper class provides a set of static methods for sending specific types of UDP messages.
 * Each method encapsulates the creation of a UDPMessage and delegates the message sending to the UDPSender class.
 * In case of an IOException during message sending, the method logs an error message and exits the system.
 */
public class UDPSenderWrapper {
    private static final Logger LOGGER = LogManager.getLogger(UDPSenderWrapper.class);

    /**
     * Sends a connect message to discover other peers in the network.
     * The method creates a CONNECT type UDPMessage and broadcasts it using UDPSender.
     * In case of an IOException, the method logs an error message and exits the system.
     */
    public static void sendConnectMessage() {
        UDPMessage connectUdpMessage = new UDPMessage(self.getAddress(), "placeholder", UDPMessageType.CONNECT, "content");
        try {
            UDPSender.sendBroadcastMessage(connectUdpMessage, Main.RECEIVE_PORT);
        } catch (IOException e) {
            LOGGER.error(String.format(ErrorMessages.SEND_MESSAGE_ERROR, UDPMessageType.CONNECT, e.getMessage()));
            System.exit(1);
        }
    }

    /**
     * Sends a response message to acknowledge a received message.
     * The method creates a RESPONSE type UDPMessage and sends it to the original sender's address using UDPSender.
     * In case of an IOException, the method logs an error message and exits the system.
     *
     * @param udpMessage The original UDPMessage to which the response is sent.
     */
    public static void sendResponseMessage(UDPMessage udpMessage) {
        UDPMessage responseUdpMessage = new UDPMessage(self.getAddress(), self.getUsername(), UDPMessageType.RESPONSE, "content");
        try {
            UDPSender.sendMessage(responseUdpMessage, udpMessage.getSenderAddress(), Main.RECEIVE_PORT);
        } catch (IOException e) {
            LOGGER.error(String.format(ErrorMessages.SEND_MESSAGE_ERROR, UDPMessageType.RESPONSE, e.getMessage()));
            System.exit(1);
        }
    }

    /**
     * Sends a set username message to broadcast the user's current username to other peers.
     * The method creates a SET_USERNAME type UDPMessage and broadcasts it using UDPSender.
     * In case of an IOException, the method logs an error message and exits the system.
     */
    public static void sendSetUsernameMessage() {
        UDPMessage usernameUdpMessage = new UDPMessage(self.getAddress(), self.getUsername(), UDPMessageType.SET_USERNAME, "content");
        try {
            UDPSender.sendBroadcastMessage(usernameUdpMessage, Main.RECEIVE_PORT);
        } catch (IOException e) {
            LOGGER.error(String.format(ErrorMessages.SEND_MESSAGE_ERROR, UDPMessageType.SET_USERNAME, e.getMessage()));
            System.exit(1);
        }
    }

    /**
     * Sends a change username message to broadcast the user's new and previous usernames to other peers.
     * The method creates a CHANGE_USERNAME type UDPMessage and broadcasts it using UDPSender.
     * In case of an IOException, the method logs an error message and exits the system.
     */
    public static void sendChangeUsernameMessage() {
        UDPMessage usernameUdpMessage = new UDPMessage(self.getAddress(), self.getUsername(), self.getPreviousUsername(), UDPMessageType.CHANGE_USERNAME, "content");
        try {
            UDPSender.sendBroadcastMessage(usernameUdpMessage, Main.RECEIVE_PORT);
        } catch (IOException e) {
            LOGGER.error(String.format(ErrorMessages.SEND_MESSAGE_ERROR, UDPMessageType.CHANGE_USERNAME, e.getMessage()));
            System.exit(1);
        }
    }

    /**
     * Sends a disconnect message to inform other peers about the user's intention to disconnect.
     * The method creates a DISCONNECT type UDPMessage and broadcasts it using UDPSender.
     * In case of an IOException, the method logs an error message and exits the system.
     */
    public static void sendDisconnectMessage() {
        UDPMessage disconnectUdpMessage = new UDPMessage(self.getAddress(), self.getUsername(), UDPMessageType.DISCONNECT, "content");
        try {
            UDPSender.sendBroadcastMessage(disconnectUdpMessage, Main.RECEIVE_PORT);
        } catch (IOException e) {
            LOGGER.error(String.format(ErrorMessages.SEND_MESSAGE_ERROR, UDPMessageType.DISCONNECT, e.getMessage()));
            System.exit(1);
        }
    }

    /**
     * Sends a chat message to a specific receiver.
     * The method creates a CHAT_MESSAGE type UDPMessage and sends it to the receiver's address using UDPSender.
     * In case of an IOException, the method logs an error message and exits the system.
     *
     * @param message The chat message to be sent.
     */
    public static void sendChatMessage(Message message) {
        UDPMessage chatMessage = new UDPMessage(self.getAddress(), self.getUsername(), UDPMessageType.CHAT_MESSAGE, message.getContent());
        try {
            UDPSender.sendMessage(chatMessage, message.getReceiverContact().getAddress(), Main.RECEIVE_PORT);
        } catch (IOException e) {
            LOGGER.error(String.format(ErrorMessages.SEND_MESSAGE_ERROR, UDPMessageType.CHAT_MESSAGE, e.getMessage()));
            System.exit(1);
        }
    }

}
