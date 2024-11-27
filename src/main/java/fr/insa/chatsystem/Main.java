package fr.insa.chatsystem;

import fr.insa.chatsystem.model.contact.Contact;
import fr.insa.chatsystem.model.logger.message.InfoMessages;
import fr.insa.chatsystem.view.View;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * The main class of the ChatSystem application responsible for initializing and managing the program flow.
 * It sets up the necessary components, listens for user input, and communicates with the network controllers.
 */
public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static final int RECEIVE_PORT = 9377;
    public static Contact self = new Contact();

    /**
     * The main method that initializes and runs the ChatSystem application.
     * It configures the logging level, logs the start message, and initializes the main view of the program.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Configurator.setRootLevel(Level.TRACE);
        LOGGER.info(InfoMessages.START_MESSAGE);

        // Initialize the main view of the ChatSystem program
        View.initialize();
    }

}
