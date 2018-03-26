package org.academiadecodigo.hexallents;

import static org.academiadecodigo.hexallents.HelperClasses.Messages.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by GuessWho on 13/03/2018.
 */
public class Server {

    private final static int DEFAULT_PORT = 6666;
    private final List<ServerWorker> workers = Collections.synchronizedList(new ArrayList<ServerWorker>());

    private int maxPlayers = 2;

    public static void main(String[] args) {

        int port = DEFAULT_PORT;

        try {

            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }

            Server server = new Server();
            server.start(port);

        } catch (NumberFormatException ex) {

            System.out.println(USAGE_ERROR);
            System.exit(1);

        }
    }

    private void start(int port) {

        System.out.println(DEBUG + this);

        int connectionCount = 0;

        try {

            // Bind to local port
            System.out.println(BINDING_PORT + port + ", please wait  ...");
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started: " + serverSocket);

            while (maxPlayers > 0) {

                // Block waiting for client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println(PLAYER_ACCEPTED + clientSocket);

                try {

                    // Create a new Server Worker
                    connectionCount++;
                    String name = PLAYER + connectionCount;

                    ServerWorker worker = new ServerWorker(name, clientSocket, this);

                    workers.add(worker);

                    // Serve the client connection with a new Thread
                    Thread thread = new Thread(worker);
                    thread.setName(name);
                    thread.start();

                    maxPlayers--;

                } catch (IOException ex) {
                    System.out.println(RECEIVING_CONNECTION_ERROR + ex.getMessage());
                }
            }
            serverSocket.close();

        } catch (IOException e) {
            System.out.println(UNABLE_SERVER_START + port);
        }
    }

    public void sendAll(String origClient, String message) {

        // Acquire lock for safe iteration
        synchronized (workers) {

            for (ServerWorker serverWorker : workers) {
                serverWorker.send(origClient, message);
            }
        }
    }

    public List<ServerWorker> getWorkers() {
        return workers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}