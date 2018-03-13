package org.academiadecodigo.hexallents;

import java.io.*;
import java.net.Socket;

/**
 * Created by codecadet on 13/03/2018.
 */
public class ServerWorker implements Runnable {

    private Server server;

    public final String LIST_CMD = "/LIST";

    final private String name;
    final private Socket clientSocket;
    final private BufferedReader in;
    final private BufferedWriter out;

    /**
     * @param name         the name of the thread handling this client connection
     * @param clientSocket the client socket connection
     * @throws IOException upon failure to open socket input and output streams
     */
    private ServerWorker(String name, Socket clientSocket) throws IOException {

        this.name = name;
        this.clientSocket = clientSocket;

        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

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


            while (!clientSocket.isClosed()) {

                // Blocks waiting for client messages
                String line = in.readLine();

                if (line == null) {

                    System.out.println("Client " + name + " closed, exiting...");

                    in.close();
                    clientSocket.close();
                    continue;

                } else if (!line.isEmpty()) {

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
            System.out.println("Receiving error on " + name + " : " + ex.getMessage());
        }
    }

    /**
     * Send a message to the client served by this thread
     *
     * @param origClient the name of the client thread the message originated from
     * @param message    the message to send
     */
    private void send(String origClient, String message) {

        try {

            out.write(origClient + ": " + message);
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println("Error sending message to Client " + name + " : " + ex.getMessage());
        }
    }


    private void sendCard(String origClient, String message) {
        // get random name and send asci card
    }
}
