package fr.insa.chatsystem.model.network;

import fr.insa.chatsystem.model.logger.message.TraceMessages;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * The UDPSender class provides static methods for sending UDP messages.
 * It includes methods for sending messages to specific addresses and broadcasting messages to all available addresses.
 */
public class UDPSender {

    private static final Logger LOGGER = LogManager.getLogger(UDPSender.class);

    /**
     * Sends a UDP message to a specific address.
     *
     * @param udpMessage     The UDP message to be sent.
     * @param receiveAddress The address to which the message will be sent.
     * @param receivePort    The port to which the message will be sent.
     * @throws IOException If an I/O error occurs while sending the message.
     */
    public static void sendMessage(UDPMessage udpMessage, InetAddress receiveAddress, int receivePort) throws IOException {
        DatagramSocket sendSocket = new DatagramSocket();
        sendSocket.setBroadcast(false);

        sendMessage(sendSocket, udpMessage, receiveAddress, receivePort);

        LOGGER.trace(String.format(TraceMessages.MESSAGE_SENT, udpMessage.getType(), udpMessage.getSenderAddress()));
    }

    /**
     * Sends a UDP message to all addresses in the network (broadcast).
     *
     * @param udpMessage      The UDP message to be sent.
     * @param receivePort     The port to which the message will be sent.
     * @throws IOException    If an I/O error occurs while sending the message.
     */
    public static void sendBroadcastMessage(UDPMessage udpMessage, int receivePort) throws IOException {
        DatagramSocket sendSocket = new DatagramSocket();
        sendSocket.setBroadcast(true);
        InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");

        sendMessage(sendSocket, udpMessage, broadcastAddress, receivePort);

        LOGGER.trace(String.format(TraceMessages.MESSAGE_BROADCAST, udpMessage.getType()));
    }

    /**
     * Sends a UDP message using the specified DatagramSocket to the specified address and port.
     *
     * @param sendSocket      The DatagramSocket used for sending the message.
     * @param udpMessage      The UDP message to be sent.
     * @param receiveAddress  The address to which the message will be sent.
     * @param receivePort     The port to which the message will be sent.
     * @throws IOException    If an I/O error occurs while sending the message.
     */
    private static void sendMessage(DatagramSocket sendSocket, UDPMessage udpMessage, InetAddress receiveAddress, int receivePort) throws IOException {
        Gson gson = new Gson();
        String packetData = gson.toJson(udpMessage);
        byte[] buffer = packetData.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiveAddress, receivePort);
        sendSocket.send(packet);
        sendSocket.close();
    }

}
