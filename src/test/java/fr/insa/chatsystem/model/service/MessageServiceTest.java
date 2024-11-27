package fr.insa.chatsystem.model.service;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.message.Message;
import fr.insa.chatsystem.model.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.List;

import static fr.insa.chatsystem.Main.self;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * JUnit test class for the {@link MessageService} class.
 * This class tests various methods of the MessageService class.
 *
 * <p>
 * The tests cover different scenarios related to message retrieval, table creation, existence checks, and message insertion.
 * </p>
 *
 * <p>
 * The tests utilize Mockito to create a mock object for the MessageRepository, allowing controlled testing
 * of the MessageService's behavior.
 * </p>
 */
class MessageServiceTest {
    private MessageService messageService;
    @Mock
    private MessageRepository messageRepository;

    /**
     * Sets up the necessary instances and environment before each test.
     * Creates a new instance of MessageService and initializes the mock MessageRepository.
     */
    @BeforeEach
    void setUp() {
        self = new Contact(1, "self", 1);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            self.setAddress(InetAddress.getByName(socket.getLocalAddress().getHostAddress()));
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }

        MockitoAnnotations.openMocks(this);
        messageService = new MessageService(messageRepository);
    }

    /**
     * Tests the retrieval of message history for a given contact from the database.
     * Verifies that the MessageService correctly retrieves the message history.
     */
    @Test
    void testGetMessageHistory() {
        List<Message> conversationHistory = new java.util.ArrayList<>(List.of(new Message(1, "content"), new Message(2, "content")));

        when(messageRepository.getConversationHistory(self.getContactId(), 2)).thenReturn(conversationHistory);
        List<Message> retrievedConversationHistory = messageService.getMessageHistory(2);

        conversationHistory.sort(Comparator.comparing(Message::getMessageId));

        assertEquals(conversationHistory.size(), retrievedConversationHistory.size());
        assertEquals(conversationHistory.get(0).getMessageId(), retrievedConversationHistory.get(0).getMessageId());
        assertEquals(conversationHistory.get(1).getMessageId(), retrievedConversationHistory.get(1).getMessageId());
    }

    /**
     * Tests the creation of the 'messages' table in the database.
     * Verifies that the MessageService correctly creates the 'messages' table.
     */
    @Test
    void testCreateMessagesTable() {
        messageService.createMessagesTable();
        verify(messageRepository, times(1)).createMessagesTable();
    }

    /**
     * Tests the existence check of the 'messages' table in the database.
     * Verifies that the MessageService correctly checks if the 'messages' table exists.
     */
    @Test
    void testTableMessagesExists() {
        when(messageRepository.tableExistsByTableName("messages")).thenReturn(true);
        assertTrue(messageService.tableMessagesExists());
    }

    /**
     * Tests the insertion of a message into the database.
     * Verifies that the MessageService correctly inserts a message into the database.
     */
    @Test
    void testInsertMessage() {
        Message message = new Message(1, "content");
        messageService.insertMessage(message);
        verify(messageRepository, times(1)).insertMessage(message);
    }
}