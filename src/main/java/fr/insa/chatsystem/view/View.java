package fr.insa.chatsystem.view;

import fr.insa.chatsystem.controller.CommandController;
import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.contact.ContactList;
import fr.insa.chatsystem.model.logger.message.ErrorMessages;
import fr.insa.chatsystem.model.message.Message;
import fr.insa.chatsystem.model.repository.ContactRepository;
import fr.insa.chatsystem.model.repository.MessageRepository;
import fr.insa.chatsystem.model.service.ContactService;
import fr.insa.chatsystem.model.service.MessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static fr.insa.chatsystem.Main.self;

/**
 * The main view class for the ChatSystem program.
 */
public class View extends JFrame implements MessageRepository.Observer, ContactList.Observer {

    private static final Logger LOGGER = LogManager.getLogger(View.class);

    private final ContactService contactService = new ContactService(new ContactRepository());
    private final MessageService messageService = new MessageService(MessageRepository.getInstance());

    private boolean isInContactListView = false;
    private boolean isInChatView = false;
    private String usernameOfTheContactYouAreChattingWith = "";

    @Override
    public void messageInserted(Message message) {
        if (isInChatView) {
            if (message.getSenderContact().getUsername().equals(usernameOfTheContactYouAreChattingWith)) {
                generateChatView(message.getSenderContact().getUsername());
            } else if (message.getReceiverContact().getUsername().equals(usernameOfTheContactYouAreChattingWith)) {
                generateChatView(message.getReceiverContact().getUsername());
            }
        }
    }

    @Override
    public void contactAdded(Contact contact) {
        if (isInContactListView) {
            cleanView();
            generateContactListView();
        }
    }

    @Override
    public void contactRemoved(Contact contact) {
        if (isInContactListView) {
            cleanView();
            generateContactListView();
        }
    }

    @Override
    public void contactRenamed(Contact contact) {
        if (isInContactListView) {
            cleanView();
            generateContactListView();
        }
    }

    /**
     * Interface for classes that want to observe window events.
     */
    public interface Observer {
        /**
         * Called when the connect button is clicked.
         */
        void connectButtonClicked();

        /**
         * Called when a new username is set.
         *
         * @param username The selected username.
         */
        void usernameSet(String username);

        /**
         * Called when a username is changed.
         *
         * @param username The selected username.
         */
        void usernameChanged(String username);

        /**
         * Called when the disconnect button is clicked.
         */
        void disconnectButtonClicked();
        /**
         * Called when the send message button is clicked.
         */
        void sendButtonClicked(String message, String username);
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
     * Initializes and displays the main view.
     */
    public static void initialize() {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            CommandController commandController = new CommandController(new ContactService(new ContactRepository()), new MessageService(MessageRepository.getInstance()));
            view.addObserver(commandController);
            MessageRepository messageRepository = MessageRepository.getInstance();
            messageRepository.addObserver(view);
            ContactList contactList = ContactList.getInstance();
            contactList.addObserver(view);
            view.setVisible(true);
        });
    }

    /**
     * Constructs a new View instance.
     */
    public View() {
        super();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ChatSystem");
        setLocation(20, 20);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                for (Observer observer : View.this.observers) {
                    observer.disconnectButtonClicked();
                }
                dispose();
            }
        });

        initGUI();
        pack();
    }

    /**
     * Initializes the graphical user interface.
     */
    private void initGUI() {

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        generateConnectView();

    }

    /**
     * Generates a view for connection.
     */
    private void generateConnectView() {

        generateAndAddTitle();

        JPanel mainPanel = generateAndAddMainPanel();
        mainPanel.setLayout(new GridBagLayout());

        JButton connectButton = generateBigButton("Connect", 100);
        addButtonToGridBag(connectButton, mainPanel, 0);
        connectButton.addActionListener(e -> {

            for (Observer observer : View.this.observers) {
                observer.connectButtonClicked();
            }

            cleanView();
            generateSetUsernameView();

        });

    }

    /**
     * Generates a view for initially selecting a username.
     */
    private void generateSetUsernameView() {

        AtomicBoolean isSelfUserPresentInTheDatabase = new AtomicBoolean((contactService.tableContactsExists() && contactService.getSelf().isPresent()));
        if (isSelfUserPresentInTheDatabase.get() && !CommandController.isUsernameChangeNeeded.get()) {
            generateCommandSelectionView();
            return;
        }
        CommandController.isUsernameChangeNeeded.set(false);

        JPanel mainPanel = generateAndAddMainPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel innerPanel = new JPanel();
        mainPanel.add(innerPanel, BorderLayout.CENTER);
        innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel noteLabel = new JLabel("Please select a username:");
        innerPanel.add(noteLabel);
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel inputPanel = new JPanel();
        innerPanel.add(inputPanel);
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JTextField usernameField = new JTextField(15);
        inputPanel.add(usernameField);

        JButton selectButton = new JButton("Select");
        inputPanel.add(selectButton);
        selectButton.addActionListener(e -> {

            String selectedUsername = usernameField.getText();

            boolean usernameAvailable = contactService.contactExistsInActiveContacts(selectedUsername) || contactService.contactExistsInDatabase(selectedUsername);
            if (!usernameAvailable) {

                for (Observer observer : observers) {
                    observer.usernameSet(selectedUsername);
                }

                cleanView();
                generateCommandSelectionView();

            } else {
                showUsernameTakenErrorNote(innerPanel);
                usernameField.setText("");
            }

        });

        JPanel buttonPanel = new JPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        JButton backButton = generateBigButton("Back", 100);
        addButtonToGridBag(backButton, buttonPanel, 0);
        backButton.addActionListener(e -> {

            cleanView();
            generateConnectView();

        });

    }

    /**
     * Generates a view for selecting a command.
     */
    private void generateCommandSelectionView() {

        JPanel mainPanel = generateAndAddMainPanel();
        mainPanel.setLayout(new GridBagLayout());

        JButton contactListButton = generateBigButton("Chat", 200);
        addButtonToGridBag(contactListButton, mainPanel, -100);
        contactListButton.addActionListener(e -> {

            cleanView();
            generateContactListView();

        });

        JButton changeUsernameButton = generateBigButton("Change username", 200);
        addButtonToGridBag(changeUsernameButton, mainPanel, 0);
        changeUsernameButton.addActionListener(e -> {

            cleanView();
            generateChangeUsernameView();

        });

        JButton disconnectButton = generateBigButton("Disconnect", 200);
        addButtonToGridBag(disconnectButton, mainPanel, 100);
        disconnectButton.addActionListener(e -> {

            for (Observer observer : observers) {
                observer.disconnectButtonClicked();
            }

            cleanView();
            generateConnectView();

        });

    }

    /**
     * Generates a view for displaying the contact list.
     */
    private void generateContactListView() {

        JPanel mainPanel = generateAndAddMainPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel noteLabel = new JLabel("Contact list:");
        mainPanel.add(noteLabel, BorderLayout.NORTH);
        noteLabel.setBorder(new EmptyBorder(10, 40, 5, 40));

        ContactList activeContacts = ContactList.getInstance();
        String[] usernames = activeContacts.getAllContacts().stream().map(Contact::getUsername).toArray(String[]::new);

        JList<String> stringList = new JList<>(usernames);
        JScrollPane scrollPane = new JScrollPane(stringList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setBorder(new CompoundBorder
                (new EmptyBorder(5, 40, 20, 40),
                BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK)));
        stringList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {

                isInContactListView = false;

                cleanView();
                generateChatView(stringList.getSelectedValue());

            }
        });

        JPanel buttonPanel = new JPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        JButton backButton = generateBigButton("Back", 100);
        addButtonToGridBag(backButton, buttonPanel, 0);
        backButton.addActionListener(e -> {

            isInContactListView = false;

            cleanView();
            generateCommandSelectionView();

        });

        isInContactListView = true;

    }

    /**
     * Generates a view for selecting a username subsequently.
     */
    private void generateChangeUsernameView() {

        JPanel mainPanel = generateAndAddMainPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel innerPanel = new JPanel();
        mainPanel.add(innerPanel, BorderLayout.CENTER);
        innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel noteLabel1 = new JLabel("Your current username is: " + self.getUsername() + ".");
        innerPanel.add(noteLabel1);
        noteLabel1.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel noteLabel2 = new JLabel("Please select a username:");
        innerPanel.add(noteLabel2);
        noteLabel2.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel inputPanel = new JPanel();
        innerPanel.add(inputPanel);
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JTextField usernameField = new JTextField(15);
        inputPanel.add(usernameField);

        JButton selectButton = new JButton("Select");
        inputPanel.add(selectButton);
        selectButton.addActionListener(e -> {

            String selectedUsername = usernameField.getText();

            boolean usernameAvailable = contactService.contactExistsInActiveContacts(selectedUsername) || contactService.contactExistsInDatabase(selectedUsername);
            if (!usernameAvailable) {

                for (Observer observer : observers) {
                    observer.usernameChanged(selectedUsername);
                }

                cleanView();
                generateCommandSelectionView();

            } else {
                showUsernameTakenErrorNote(innerPanel);
                usernameField.setText("");
            }

        });

        JPanel backButtonPanel = new JPanel();
        mainPanel.add(backButtonPanel, BorderLayout.SOUTH);
        JButton backButton = generateBigButton("Back", 100);
        addButtonToGridBag(backButton, backButtonPanel, 0);
        backButton.addActionListener(e -> {

            cleanView();
            generateCommandSelectionView();

        });

    }

    /**
     * Generates a view for chatting with a contact.
     */
    private void generateChatView(String username) {

        Container cp = getContentPane();

        Component[] cpComponents = cp.getComponents();
        if (cpComponents.length < 2) {
            JPanel mainPanel = generateAndAddMainPanel();
            mainPanel.setLayout(new BorderLayout());
            generateChatViewTopPanel(mainPanel, username);
            generateChatViewCenterPanel(mainPanel, username);
            generateChatViewBottomPanel(mainPanel, username);
        } else {
            JPanel mainPanel = (JPanel) cp.getComponents()[1];
            Component[] mainPanelComponents = mainPanel.getComponents();
            mainPanel.remove(mainPanelComponents[1]);
            generateChatViewCenterPanel(mainPanel, username);
        }

        usernameOfTheContactYouAreChattingWith = username;
        isInChatView = true;

    }

    /**
     * Generates top part of the view for chatting with a contact.
     */
    private void generateChatViewTopPanel(JPanel mainPanel, String username) {

        JPanel topPanel = new JPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel noteLabel = new JLabel("You are now chatting with " + username + ".");
        topPanel.add(noteLabel);
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 14));

    }

    /**
     * Generates middle part of the view for chatting with a contact.
     */
    private void generateChatViewCenterPanel(JPanel mainPanel, String username) {

        Optional<Contact> contact = contactService.getContactByUsername(username);
        if (contact.isEmpty()) {
            LOGGER.error(String.format(ErrorMessages.CONTACT_DOES_NOT_EXIST_USERNAME, username));
            System.exit(1);
        }
        List<Message> chatHistory = messageService.getMessageHistory(contact.get().getContactId());

        List<String> messages = new ArrayList<>();

        for (Message message : chatHistory) {
            String senderUsername = message.getSenderContact().getUsername();
            String text = message.getContent();
            messages.add(senderUsername + ": " + text);
        }

        JList<String> stringList = new JList<>(messages.toArray(new String[0]));
        JScrollPane scrollPane = new JScrollPane(stringList);

        if (mainPanel.getComponentCount() == 1) {
            mainPanel.add(scrollPane, BorderLayout.CENTER);
        } else {
            mainPanel.add(scrollPane, BorderLayout.CENTER, mainPanel.getComponentCount() - 1);
        }

        scrollPane.setBorder(new CompoundBorder
                (new EmptyBorder(5, 40, 0, 40),
                        BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK)));
        revalidate();
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

    }

    /**
     * Generates bottom part of the view for chatting with a contact.
     */
    private void generateChatViewBottomPanel(JPanel mainPanel, String username) {

        JPanel bottomPanel = new JPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setLayout(new BorderLayout());

        JPanel bottomMessagePanel = new JPanel();
        bottomPanel.add(bottomMessagePanel, BorderLayout.CENTER);
        bottomMessagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel usernameLabel = new JLabel(self.getUsername() + ":");
        bottomMessagePanel.add(usernameLabel);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextField usernameField = new JTextField(15);
        bottomMessagePanel.add(usernameField);

        JButton sendButton = new JButton("Send");
        bottomMessagePanel.add(sendButton);
        sendButton.addActionListener(e -> {
            for (Observer observer : View.this.observers) {
                observer.sendButtonClicked(usernameField.getText(), username);
                usernameField.setText("");
            }
        });

        JPanel bottomBackButtonPanel = new JPanel();
        bottomPanel.add(bottomBackButtonPanel, BorderLayout.SOUTH);

        JButton backButton = generateBigButton("Back", 100);
        addButtonToGridBag(backButton, bottomBackButtonPanel, 0);
        backButton.addActionListener(e -> {

            isInChatView = false;
            usernameOfTheContactYouAreChattingWith = "";

            cleanView();
            generateCommandSelectionView();

        });

    }

    /**
     * Generates and adds the title panel to the container's NORTH position.
     * The title panel includes a label welcoming the user to the ChatSystem program.
     */
    private void generateAndAddTitle() {
        Container cp = getContentPane();
        JPanel titlePanel = new JPanel();
        cp.add(titlePanel, BorderLayout.NORTH);
        JLabel titleLabel = new JLabel("Welcome to the ChatSystem program");
        titlePanel.add(titleLabel);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    }

    /**
     * Generates and adds the main panel to the container's CENTER position.
     * The main panel is a basic JPanel with a predefined preferred size.
     *
     * @return The generated main panel.
     */
    private JPanel generateAndAddMainPanel() {
        Container cp = getContentPane();
        JPanel mainPanel = new JPanel();
        cp.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setPreferredSize(new Dimension(400, 300));
        return mainPanel;
    }

    /**
     * Cleans the view by removing the specified panel.
     */
    private void cleanView() {
        Container cp = getContentPane();
        cp.removeAll();
        cp.revalidate();
        cp.repaint();
        generateAndAddTitle();
    }

    /**
     * Generates a button in a desired style.
     */
    private JButton generateBigButton(String text, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(46, 133, 233));
        button.setPreferredSize(new Dimension(width, 30));
        button.setFocusPainted(false);
        return button;
    }

    /**
     * Adds a button to a grid bag layout of a panel.
     */
    private void addButtonToGridBag(JButton button, JPanel panel, int top) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(top, 0, 0, 0);
        panel.add(button, gbc);
    }

    /**
     * Displays an error note in the specified panel.
     *
     * @param innerPanel The panel in which to display the error note.
     */
    private void showUsernameTakenErrorNote(JPanel innerPanel) {

        Component[] components = innerPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                innerPanel.remove(component);
            }
        }

        JLabel errorLabel = new JLabel("Username is taken. Please choose another one.");
        innerPanel.add(errorLabel, innerPanel.getComponentCount() - 1);
        errorLabel.setForeground(Color.RED);

        innerPanel.revalidate();
        innerPanel.repaint();
    }

}
