package org.academiadecodigo.hexallents;

import static org.academiadecodigo.hexallents.HelperClasses.Messages.*;
import org.academiadecodigo.hexallents.menu.Menu;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by GuessWho on 13/03/2018.
 */
public class Player {

    private final static int DEFAULT_PORT = 6666;
    private final static String DEFAULT_HOST = "localhost";
    private Menu menu;

    // The client socket
    private Socket socket;

    public static void main(String args[]) {

        String host = DEFAULT_HOST;

        try {

            System.out.println(GAME_NAME);

            new Menu();

            new Player(DEFAULT_HOST, DEFAULT_PORT);

        } catch (NumberFormatException ex) {

            System.out.println(INVALID_PORT + host);
            System.out.print(1);
        }
    }

    public Player(String serverName, int serverPort) {

        try {

            // Connect to server
            socket = new Socket(serverName, serverPort);
            start();

        } catch (UnknownHostException ex) {

            System.out.println(UNKNOWN_HOST + ex.getMessage());
            System.exit(1);

        } catch (IOException ex) {

            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    // Starts handling messages
    private void start() {

        // Creates a new thread to handle incoming server messages
        Thread thread = new Thread(new ChatRunnable());
        thread.start();

        try {

            BufferedWriter sockOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

            while (!socket.isClosed()) {

                String consoleMessage = null;

                try {
                    // Blocks waiting for user input
                    consoleMessage = consoleIn.readLine();

                } catch (IOException ex) {
                    System.out.println(READING_ERROR + ex.getMessage());
                    break;
                }

                if (consoleMessage == null || consoleMessage.equals("/quit")) {
                    break;
                }

                sockOut.write(consoleMessage);
                sockOut.newLine();
                sockOut.flush();
            }

            try {

                consoleIn.close();
                sockOut.close();
                socket.close();

            } catch (IOException ex) {
                System.out.println(CLOSING_CONNECTION_ERROR + ex.getMessage());
            }

        } catch (IOException ex) {

            System.out.println(SENDING_TO_SERVER_ERROR + ex.getMessage());

        }
    }

    // Runnable to handle incoming messages from the server
    private class ChatRunnable implements Runnable {

        @Override
        public void run() {

            try {

                BufferedReader sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (!socket.isClosed()) {

                    // Block waiting for incoming messages from server
                    String incomingMessage = sockIn.readLine();

                    if (incomingMessage != null) {

                        System.out.println(incomingMessage);

                    } else {

                        try {

                            System.out.println(CONNECTION_CLOSED);
                            sockIn.close();
                            socket.close();

                        } catch (IOException ex) {
                            System.out.println(CLOSING_CONNECTION_ERROR + ex.getMessage());
                        }

                    }

                }
            } catch (SocketException ex) {
                // Socket closed by other thread, no need for special handling
            } catch (IOException ex) {
                System.out.println(READING_FROM_SERVER_ERROR + ex.getMessage());
            }

            // Server closed, but main thread blocked in console readline
            System.exit(0);
        }
    }
}
