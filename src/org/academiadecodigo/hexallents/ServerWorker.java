package org.academiadecodigo.hexallents;

import java.io.*;
import java.net.Socket;
import static org.academiadecodigo.hexallents.HelperClasses.Messages.*;

/**
 * Created by GuessWho on 13/03/2018.
 */
public class ServerWorker implements Runnable {

    final private Server server;
    final private String name;
    final private Socket playerSocket;
    final private BufferedReader in;
    final private BufferedWriter out;

    private boolean playerCanAsk = false;

    /**
     * @param name         the name of the thread handling this client connection
     * @param playerSocket the client socket connection
     * @throws IOException upon failure to open socket input and output streams
     */
    public ServerWorker(String name, Socket playerSocket, Server server) throws IOException {

        this.server = server;
        this.name = name;
        this.playerSocket = playerSocket;

        in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
    }


    public String getName() {
        return name;
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {

        System.out.println("Thread " + name + " started");

        try {

            while (!playerSocket.isClosed()) {

                if (Thread.activeCount() < 4) {
                    System.out.println("só há um jogador"); // player 1 tem esperar

                    sendWaitingMessage("Clients Connected", server.listClients());
                    // enviar mensagem para o 1 a dizer que está à espera de oponent


                    while (!playerCanAsk) {
                        if (Thread.activeCount() > 3) {
                            playerCanAsk = true;
                        }

                        return;
                    }

                }

                // Blocks waiting for client messages
                String line = in.readLine();

                if (line == null) {

                    System.out.println("Player " + name + " closed, exiting...");

                    in.close();
                    playerSocket.close();
                    continue;

                } else if (!line.isEmpty()) {

                    String LIST_CMD = "/LIST";
                    if (line.toUpperCase().equals(LIST_CMD)) {

                        send("Clients Connected", server.listClients());

                    } else {

                        // Broadcast message to all other clients
                        server.sendAll(name, line);
                    }

                }

            }

            //workers.remove(this);



        } catch (IOException ex) {
            System.out.println(RECEIVING_ERROR + name + " : " + ex.getMessage());
        }
    }

    /**
     * Send a message to the client served by this thread
     *
     * @param origClient the name of the client thread the message originated from
     * @param message    the message to send
     */
    public void send(String origClient, String message) {

        try {

            out.write(origClient + ": " + message);
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
        }
    }

    public void sendWaitingMessage(String origClient, String message) {

        try {

            out.write("Waiting for opponent to start the game");
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
        }
    }
}
