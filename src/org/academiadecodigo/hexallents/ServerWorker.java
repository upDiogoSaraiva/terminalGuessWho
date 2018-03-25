package org.academiadecodigo.hexallents;

import static org.academiadecodigo.hexallents.HelperClasses.Messages.*;

import org.academiadecodigo.hexallents.HelperClasses.Random;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Created by GuessWho on 13/03/2018.
 */
public class ServerWorker implements Runnable {

    final private Server server;
    final private String name;
    final private Socket playerSocket;
    final private BufferedReader in;
    final private BufferedWriter out;

    private CardType playersCard;

    private PlayerState playerState = PlayerState.WAITING;

    private final List<ServerWorker> playersList;
    private int maxQuestions = 5;


    /**
     * @param name         the name of the thread handling this client connection
     * @param playerSocket the client socket connection
     * @throws IOException upon failure to open socket input and output streams
     */
    public ServerWorker(String name, Socket playerSocket, Server server) throws IOException {

        this.server = server;
        this.name = name;
        this.playerSocket = playerSocket;

        playersCard = CardType.values()[Random.generateRandomCard()];

        in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));

        playersList = server.getWorkers();

        printCard();
    }

    private void printCard() {

        try {

            out.write(YOUR_CARD_IS + playersCard.getName() + "\n\n" + playersCard.getAsci());
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
        }
    }

    private void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    private PlayerState getPlayerState() {
        return playerState;
    }

    private int getMaxQuestions() {
        return maxQuestions;
    }

    private void setMaxQuestions(int maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public String getName() {
        return name;
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {

        System.out.println(playersList.size());

        // sw(swList.size()-1);

        System.out.println(playersList.get(playersList.size() - 1).getPlayerState());

        System.out.println("Thread " + name + " started");

        try {

            int currentPlayerIndex = playersList.indexOf(this);

            /* SET INITIAL PLAYER STATES */
            if (playersList.size() == 1) {
                this.setPlayerState(PlayerState.CAN_ASK);
                System.out.println("Player 1 ---> " + this.getPlayerState());
            }

            if (playersList.size() == 2) {
                this.setPlayerState(PlayerState.WAITING);
                System.out.println("Player 2 ---> " + this.getPlayerState());
            }


            while (maxQuestions != 0) {

                /* MESSAGES TO PLAYER */
                if (this.getPlayerState() == PlayerState.CAN_ASK) {
                    messageToUser("Ask a question (/ask): ");
                }

                if (this.getPlayerState() == PlayerState.WAITING) {
                    messageToUser("Waiting for your opponent: ");
                }

                if (this.getPlayerState() == PlayerState.CAN_ANSWER) {
                    messageToUser("Write your answer (/yes or /no): ");
                }

                System.out.println("player 1 state: " + playersList.get(0).getPlayerState());
                System.out.println("player 2 state: " + playersList.get(playersList.size() - 1).getPlayerState());

                String line = null;

                // Blocks waiting for client messages
                line = in.readLine();

                if (line == null) {

                    System.out.println("Player " + name + " closed, exiting...");

                    in.close();
                    playerSocket.close();
                    continue;

                } else if (!line.isEmpty()) {

                    String[] lineArray = line.split(" ", 2);

                    // MESSAGE PLAYER SENDS
                    if (lineArray[0].equals("/ask") && this.getPlayerState() == PlayerState.CAN_ASK) { // recebe /ask é porque está Waiting

                        CardType[] cardTypes = CardType.values();

                        for (CardType cardType : cardTypes) {

                            System.out.println(lineArray[1]);
                            System.out.println(lineArray[1].trim());


                            if (lineArray[1].toLowerCase().trim().contains(cardType.getName().toLowerCase())) {

                                if (currentPlayerIndex == 0) {

                                    if (cardType == playersList.get(1).getPlayersCard()) {
                                        this.setPlayerState(PlayerState.WON);
                                        playersList.get(1).setPlayerState(PlayerState.LOST);
                                        /*messageToUser(WIN);
                                        System.exit(0);*/

                                    } else {
                                        this.setPlayerState(PlayerState.LOST);
                                        playersList.get(1).setPlayerState(PlayerState.WON);
                                        /*messageToUser(LOSE);
                                        System.exit(0);*/
                                    }
                                }

                                if (currentPlayerIndex == 1) {

                                    if (cardType == playersList.get(0).getPlayersCard()) {
                                        this.setPlayerState(PlayerState.WON);
                                        playersList.get(0).setPlayerState(PlayerState.LOST);
                                        /*messageToUser(WIN);
                                        System.exit(0);*/
                                    } else {
                                        this.setPlayerState(PlayerState.LOST);
                                        playersList.get(0).setPlayerState(PlayerState.WON);
                                        /*messageToUser(LOSE);
                                        System.exit(0);*/
                                    }
                                    return;
                                }
                            }
                        }


                        if (this.getPlayerState() == PlayerState.LOST || this.getPlayerState() == PlayerState.WON) {

                            if (this.getPlayerState() == PlayerState.LOST) {
                                messageToUser(LOSE);
                            }

                            if (this.getPlayerState() == PlayerState.WON) {
                                messageToUser(WIN);
                            }
                            System.exit(0);
                        }

                        System.out.println(this.getMaxQuestions());
                        this.setMaxQuestions(this.getMaxQuestions() - 1);
                        System.out.println(this.getMaxQuestions());

                        if (currentPlayerIndex == 0) {
                            playersList.get(1).setPlayerState(PlayerState.CAN_ANSWER);
                        }

                        if (currentPlayerIndex == 1) {
                            playersList.get(0).setPlayerState(PlayerState.CAN_ANSWER);
                        }

                        this.setPlayerState(PlayerState.WAITING);
                    }


                    if ((lineArray[0].equals("/yes") || lineArray[0].equals("/no")) && this.getPlayerState() == PlayerState.CAN_ANSWER) {

                        if (currentPlayerIndex == 0) {
                            playersList.get(1).setPlayerState(PlayerState.WAITING);
                        }

                        if (currentPlayerIndex == 1) {
                            playersList.get(0).setPlayerState(PlayerState.WAITING);
                        }
                        this.setPlayerState(PlayerState.CAN_ASK);
                    }


                    String LIST_CMD = "/LIST";
                    if (line.toUpperCase().equals(LIST_CMD)) {

                        send("Clients Connected", server.listClients());

                    } else {


                        if(this.getPlayerState() == PlayerState.CAN_ASK && !lineArray[0].equals("/ask")){
                            messageToUser("To ask questions please use the format: /ask <your question>");
                        } else if (this.getPlayerState() == PlayerState.CAN_ANSWER &&
                                (!lineArray[0].equals("/yes") || !lineArray[0].equals("/no"))){
                            messageToUser("To answer your opponent's question please use: /yes or /no");
                        } else {
                            // Broadcast message to all other clients
                            server.sendAll(name, line);
                        }




                    }
                }
            }

            System.exit(0);

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

    private void messageToUser(String message) {

        try {
            out.write(message);
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
        }
    }

    private CardType getPlayersCard() {
        return playersCard;
    }
}
