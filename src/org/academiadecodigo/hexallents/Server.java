package org.academiadecodigo.hexallents;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by GuessWho on 13/03/2018.
 */
public class Server {

    /**
     * Default port to run the server on
     */
    private final static int DEFAULT_PORT = 6666;

    /**
     * Synchronized List of worker threads, locked by itself
     */
    private final List<ServerWorker> workers = Collections.synchronizedList(new ArrayList<ServerWorker>());


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
    private void start(int port) {

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
                System.out.println("Player accepted: " + clientSocket);

                try {

                    // Create a new Server Worker
                    connectionCount++;
                    String name = "Player-" + connectionCount;


                    /*if (connectionCount <= 2) {

                    }*/
                    ServerWorker worker = new ServerWorker(name, clientSocket, this);
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

}
