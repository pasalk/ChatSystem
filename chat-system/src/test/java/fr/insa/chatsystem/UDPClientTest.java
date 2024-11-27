package fr.insa.chatsystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UDPClientTest {

    private static final String TEST_MESSAGE = "TEST-MESSAGE";
    private static final String BROADCAST_MESSAGE = "BROADCAST-MESSAGE";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testSendMessage() throws IOException, InterruptedException {
        InetAddress localhost = InetAddress.getLocalHost();
        UDPClient.sendMessage(TEST_MESSAGE, localhost);

        assertEquals("******* Sent " +  TEST_MESSAGE + " to address " + localhost + "\n", outContent.toString());
    }

    @Test
    void testSendBroadcastMessage() throws IOException, InterruptedException {
        UDPClient.sendBroadcastMessage(BROADCAST_MESSAGE);

        assertEquals("******* Sent " +  BROADCAST_MESSAGE + " to all addresses\n", outContent.toString());
    }
}


