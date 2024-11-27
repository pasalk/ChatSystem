package fr.insa.chatsystem.model.service;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.message.Message;
import fr.insa.chatsystem.model.network.UDPSenderWrapper;
import fr.insa.chatsystem.model.repository.MessageRepository;

import java.util.Comparator;
import java.util.List;

import static fr.insa.chatsystem.Main.self;

/**
 * The MessageService class provides methods for managing messages,
 * including sending chat messages, retrieving message history, and interacting with the message repository.
 */
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Sends a chat message to a specified receiver contact.
     *
     * @param receiverContact The Contact object representing the message receiver.
     * @param content         The content of the chat message.
     */
    public void sendChatMessage(Contact receiverContact, String content) {
        Message message = new Message(self, receiverContact, content);
        messageRepository.insertMessage(message);
        UDPSenderWrapper.sendChatMessage(message);
    }

    /**
     * Retrieves the message history between the logged-in user and a specified contact.
     *
     * @param contactId The contact_id of the other participant in the conversation.
     * @return A List of Message objects representing the conversation history.
     */
    public List<Message> getMessageHistory(Integer contactId) {
        List<Message> conversationHistory = messageRepository.getConversationHistory(self.getContactId(), contactId);
        conversationHistory.sort(Comparator.comparing(Message::getMessageId));
        return conversationHistory;
    }

    /**
     * Creates the 'messages' table in the database for storing message information.
     */
    public void createMessagesTable() {
        messageRepository.createMessagesTable();
    }

    /**
     * Checks if the 'messages' table exists in the database.
     *
     * @return True if the 'messages' table exists, otherwise false.
     */
    public boolean tableMessagesExists() {
        return messageRepository.tableExistsByTableName("messages");
    }

    /**
     * Inserts a new message into the 'messages' table.
     *
     * @param message The Message object to be inserted.
     */
    public void insertMessage(Message message) {
        messageRepository.insertMessage(message);
    }

}
