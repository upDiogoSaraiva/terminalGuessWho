package org.academiadecodigo.hexallents.HelperClasses;

/**
 * Created by codecadet on 18/03/2018.
 */
public class Messages {

    // Who is who?
    public static final String GAME_NAME =
            " __        ___             _                _          ___ \n" +
            " \\ \\      / / |__   ___   (_)___  __      _| |__   ___|__ \\\n" +
            "  \\ \\ /\\ / /| '_ \\ / _ \\  | / __| \\ \\ /\\ / / '_ \\ / _ \\ / /\n" +
            "   \\ V  V / | | | | (_) | | \\__ \\  \\ V  V /| | | | (_) |_| \n" +
            "    \\_/\\_/  |_| |_|\\___/  |_|___/   \\_/\\_/ |_| |_|\\___/(_) \n" +
            "                                                           ";

    // Menu Options
    public static final String GAME_OPTIONS = "Select an option";

    public static final String GAME_OPTIONS_START = "Start";
    public static final String GAME_OPTIONS_INSTRUCTIONS = "Instructions";
    public static final String GAME_OPTIONS_QUIT = "Quit";

    public static final String GAME_STARTED =
            "\n\n" +
            "   ____                        ____  _             _           _ \n" +
            "  / ___| __ _ _ __ ___   ___  / ___|| |_ __ _ _ __| |_ ___  __| |\n" +
            " | |  _ / _` | '_ ` _ \\ / _ \\ \\___ \\| __/ _` | '__| __/ _ \\/ _` |\n" +
            " | |_| | (_| | | | | | |  __/  ___) | || (_| | |  | ||  __/ (_| |\n" +
            "  \\____|\\__,_|_| |_| |_|\\___| |____/ \\__\\__,_|_|   \\__\\___|\\__,_|\n" +
            "                                                                 \n\n";

    public static final String INSTRUCTIONS = "** Instructions **";
    public static final String GAME_OVER = "** Game Over **";


    // Player
    public static final String YOUR_CARD_IS = "Your card is: ";

    public static final String TRYING_ESTABLISH_CONNECTION = "Trying to establish connection";
    public static final String INVALID_PORT = "Invalid port number ";
    public static final String UNKNOWN_HOST= "Unknown host: ";

    public static final String SENDING_TO_SERVER_ERROR = "Error sending message to server: ";
    public static final String READING_FROM_SERVER_ERROR = "Error reading from server: ";
    public static final String READING_ERROR = "Error reading from console: ";
    public static final String RECEIVING_ERROR = "Receiving error on ";

    public static final String CONNECTION_CLOSED = "Connection closed, exiting...";
    public static final String CLOSING_CONNECTION_ERROR = "Error closing connection: ";


    // Server
    public static final String USAGE_ERROR = "Usage: java ChatServer [port_number]";
    public static final String DEBUG = "DEBUG: Server instance is : ";
    public static final String BINDING_PORT = "Binding to port ";

    public static final String PLAYER_ACCEPTED= "Player accepted: ";
    public static final String PLAYER = "Player-";
    public static final String RECEIVING_CONNECTION_ERROR = "Error receiving client connection: ";

    public static final String UNABLE_SERVER_START = "Unable to start server on port ";


    // ServerWorker
    public static final String SENDING_MESSAGE_ERROR = "Error sending message to Player ";
}
