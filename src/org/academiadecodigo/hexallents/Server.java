package org.academiadecodigo.hexallents;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created on 13/03/2018.
 * Good Luck, Have Fun codecadet
 */
public class Server {

    /**
     * Default port to run the server on
     */
    public final static int DEFAULT_PORT = 6666;

    public final String LIST_CMD = "/LIST";

    /**
     * Synchronized List of worker threads, locked by itself
     */
    private List<ServerWorker> workers = Collections.synchronizedList(new ArrayList<ServerWorker>());


    /**
     * Bootstraps the chat server
     *
     * @param args Optional port number as command line argument
     */
    public static void main(String[] args) {

        int port = DEFAULT_PORT;

        try {

            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }

            Server server = new Server();
            server.start(port);

        } catch (NumberFormatException ex) {

            System.out.println("Usage: java ChatServer [port_number]");
            System.exit(1);

        }

    }

    /**
     * Starts the chat server on a specified port
     *
     * @param port the tcp port to bind to
     */
    public void start(int port) {

        System.out.println("DEBUG: Server instance is : " + this);

        int connectionCount = 0;

        try {

            // Bind to local port
            System.out.println("Binding to port " + port + ", please wait  ...");
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started: " + serverSocket);

            while (true) {

                // Block waiting for client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client accepted: " + clientSocket);

                try {

                    // Create a new Server Worker
                    connectionCount++;
                    String name = "Client-" + connectionCount;

                    ServerWorker worker = new ServerWorker(name, clientSocket);
                    workers.add(worker);

                    // se se conectarem mais do que 2, não adiciono à lista






                    // Serve the client connection with a new Thread
                    Thread thread = new Thread(worker);
                    thread.setName(name);
                    thread.start();

                } catch (IOException ex) {
                    System.out.println("Error receiving client connection: " + ex.getMessage());
                }

            }

        } catch (IOException e) {
            System.out.println("Unable to start server on port " + port);
        }
    }

    public List<ServerWorker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<ServerWorker> workers) {
        this.workers = workers;
    }

    public String listClients() {

        StringBuilder builder = new StringBuilder("\n\n");

        synchronized (workers) {

            Iterator<ServerWorker> it = workers.iterator();
            while (it.hasNext()) {
                builder.append("\t");
                builder.append(it.next().getName());
                builder.append("\n");
            }
        }

        return builder.toString();

    }

    /**
     * Broadcast a message to all server connected clients
     *
     * @param origClient name of the client thread that the message originated from
     * @param message    the message to broadcast
     */
    public void sendAll(String origClient, String message) {

        // Acquire lock for safe iteration
        synchronized (workers) {

            Iterator<ServerWorker> it = workers.iterator();
            while (it.hasNext()) {
                it.next().send(origClient, message);
            }

        }

    }

    // quando te ligas -> gera uma carta
    // sendCard();



    /**
     * Handles client connections
     */
    private class ServerWorker implements Runnable {

        // Immutable state, no need to lock
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

                            send("Clients Connected", listClients());

                        } else {

                            // Broadcast message to all other clients
                            sendAll(name, line);
                        }

                    }

                }

                workers.remove(this);

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

}
