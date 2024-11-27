package fr.insa.chatsystem.model.network;

import fr.insa.chatsystem.Main;
import fr.insa.chatsystem.model.logger.message.ErrorMessages;
import fr.insa.chatsystem.model.logger.message.TraceMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * UDPListener is a threaded class responsible for listening to incoming UDP messages on a specified port.
 * It implements a simple observer pattern to notify registered observers when a new message is received.
 * <p>
 * Usage:
 * To use this class, create an instance by providing the port number to listen on. Register observers
 * using the addObserver method. Start the listener by calling the start() method.
 */
public class UDPListener extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(UDPListener.class);
    private boolean running;
    private final DatagramSocket receiveSocket;

    /**
     * Constructs a new UDPListener instance that listens on the specified port.
     *
     * @param port The port number to listen on.
     * @throws SocketException If an error occurs while creating the DatagramSocket.
     */
    public UDPListener(int port) throws SocketException {
        receiveSocket = new DatagramSocket(port);
        running = true;
    }

    /**
     * Stops the UDP listener.
     */
    public void stopRunning() {
        running = false;
    }

    /**
     * Starts the UDP listener.
     */
    public void startRunning() {
        running = true;
    }

    /**
     * Interface for classes that want to observe incoming UDP messages.
     */
    public interface Observer {
        void messageReceived(UDPMessage message);
    }

    private final List<Observer> observers = new ArrayList<>();

    /**
     * Adds an observer to the list of observers.
     *
     * @param observer The observer to be added.
     */
    public synchronized void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * Runs the UDP listener in a loop, continuously listening for incoming UDP messages.
     * When a message is received, it notifies all registered observers.
     * Exits the system in case of an IOException.
     */
    @Override
    public synchronized void run() {
        try {
            while (running) {
                DatagramPacket packet = receiveMessage();
                String packetData = new String(packet.getData(), 0, packet.getLength());
                UDPMessage udpMessage = new UDPMessage(packet.getAddress(), packetData);

                for (Observer observer : this.observers) {
                    observer.messageReceived(udpMessage);
                }

                if (!udpMessage.getSenderAddress().equals(Main.self.getAddress())) {
                    LOGGER.trace(String.format(TraceMessages.MESSAGE_RECEIVED, udpMessage.getType(), udpMessage.getSenderAddress()));
                }
            }
        } catch (IOException e) {
            LOGGER.error(ErrorMessages.BASIC_ERROR + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Receives a DatagramPacket containing incoming UDP message data.
     *
     * @return The received DatagramPacket.
     * @throws IOException If an error occurs while receiving the packet.
     */
    private DatagramPacket receiveMessage() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        receiveSocket.receive(packet);
        return packet;
    }

}
