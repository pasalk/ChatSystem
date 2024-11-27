package fr.insa.chatsystem.controller;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.contact.ContactList;
import fr.insa.chatsystem.model.exception.ContactDoesNotExistRuntimeException;
import fr.insa.chatsystem.model.logger.message.ErrorMessages;
import fr.insa.chatsystem.model.message.Message;
import fr.insa.chatsystem.model.network.UDPListener;
import fr.insa.chatsystem.model.network.UDPMessage;
import fr.insa.chatsystem.model.network.UDPMessageType;
import fr.insa.chatsystem.model.service.ContactService;
import fr.insa.chatsystem.model.service.MessageService;

import java.util.Optional;

import static fr.insa.chatsystem.Main.self;
import static fr.insa.chatsystem.model.network.UDPSenderWrapper.sendResponseMessage;

/**
 * NetworkController class responsible for handling network communication and updating the contact list.
 * Implements the UDPListener.Observer interface for handling received UDP messages.
 */
public class NetworkController implements UDPListener.Observer {

    private final ContactService contactService;
    private final MessageService messageService;

    public NetworkController(ContactService contactService, MessageService messageService) {
        this.contactService = contactService;
        this.messageService = messageService;
    }

    /**
     * Handles the reception of UDP messages and performs relevant actions based on the message type.
     *
     * @param udpMessage The UDP message received.
     */
    @Override
    public void messageReceived(UDPMessage udpMessage) {
        if (udpMessage.getSenderAddress().equals(self.getAddress())) {
            return;
        }

        if (!(udpMessage.getType().equals(UDPMessageType.CONNECT) ||
                udpMessage.getType().equals(UDPMessageType.RESPONSE) ||
                udpMessage.getType().equals(UDPMessageType.SET_USERNAME) ||
                udpMessage.getType().equals(UDPMessageType.CHANGE_USERNAME) ||
                udpMessage.getType().equals(UDPMessageType.DISCONNECT) ||
                udpMessage.getType().equals(UDPMessageType.CHAT_MESSAGE))) {
            return;   // message from another student group
        }

        ContactList activeContacts = ContactList.getInstance();

        if (udpMessage.getType().equals(UDPMessageType.CONNECT)) {
            sendResponseMessage(udpMessage);
        } else if (udpMessage.getType().equals(UDPMessageType.RESPONSE) || udpMessage.getType().equals(UDPMessageType.SET_USERNAME)) {
            Optional<Contact> contact = contactService.getContactByUsername(udpMessage.getSenderUsername());
            if (contact.isEmpty()) {
                contact = contactService.insertContact(new Contact(udpMessage.getSenderUsername(), false));
            }

            if (contact.isPresent()) {
                Contact activeContact = contact.get();
                activeContact.setAddress(udpMessage.getSenderAddress());
                activeContacts.addContact(activeContact);
            }

        } else if (udpMessage.getType().equals(UDPMessageType.CHANGE_USERNAME)) {
            Contact contact = contactService.getContactByUsername(udpMessage.getSenderPreviousUsername())
                    .orElseThrow(() -> new ContactDoesNotExistRuntimeException(String.format(ErrorMessages.CONTACT_DOES_NOT_EXIST_USERNAME, udpMessage.getSenderPreviousUsername())));
            contact.setUsername(udpMessage.getSenderUsername());

            Optional<Contact> updatedContact = contactService.updateContact(contact);
            updatedContact.ifPresent(activeContacts::renameContact);
        } else if (udpMessage.getType().equals(UDPMessageType.DISCONNECT)) {
            activeContacts.removeContact(udpMessage.getSenderUsername());
        } else if (udpMessage.getType().equals(UDPMessageType.CHAT_MESSAGE)) {
            Contact sender = contactService.getContactByUsername(udpMessage.getSenderUsername())
                    .orElseThrow(() -> new ContactDoesNotExistRuntimeException(String.format(ErrorMessages.CONTACT_DOES_NOT_EXIST_USERNAME, udpMessage.getSenderPreviousUsername())));
            Message message = new Message(sender, self, udpMessage.getContent());
            messageService.insertMessage(message);
        }
    }
}
