package org.academiadecodigo.hexallents;

import static org.academiadecodigo.hexallents.HelperClasses.Messages.*;
import static org.academiadecodigo.hexallents.PlayerState.*;
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

    private final List<ServerWorker> playersList;
    private int maxQuestions = 5;

    private CardType playersCard;
    private PlayerState playerState = WAITING;

    public ServerWorker(String name, Socket playerSocket, Server server) throws IOException {

        this.server = server;
        this.name = name;
        this.playerSocket = playerSocket;

        in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));

        playersList = server.getWorkers();
        playersCard = CardType.values()[Random.generateRandomCard()];
        printCard();
    }

    @Override
    public void run() {

        System.out.println("Thread " + name + " started");

        try {

            int currentPlayerIndex = playersList.indexOf(this);

            // SET INITIAL PLAYER STATES
            setInitialStates();

            // QUESTIONS LOOP
            while (maxQuestions != 0) {

                // MESSAGES TO PLAYER
                promptMessages();

                // Blocks waiting for client messages
                String line = in.readLine();

                if (line == null) {

                    System.out.println("Player " + name + " closed, exiting...");

                    in.close();
                    playerSocket.close();
                    continue;

                } else if (!line.isEmpty()) {

                    String[] lineArray = line.split(" ", 2);

                    // MESSAGE PLAYER SENDS
                    if (this.getPlayerState() == CAN_ASK && lineArray[0].equals("/ask")) {

                        this.setMaxQuestions(this.getMaxQuestions() - 1);

                        if (currentPlayerIndex == 0) {
                            playersList.get(1).setPlayerState(CAN_ANSWER);
                        }

                        if (currentPlayerIndex == 1) {
                            playersList.get(0).setPlayerState(CAN_ANSWER);
                        }

                        checkIfCanSend(lineArray, line);
                        checkAnswer(lineArray,currentPlayerIndex);
                        this.setPlayerState(WAITING);
                    }

                    if (this.getPlayerState() == CAN_ANSWER &&
                            (lineArray[0].equals("/yes") || lineArray[0].equals("/no"))) {

                        if (currentPlayerIndex == 0) {
                            playersList.get(1).setPlayerState(WAITING);
                        }

                        if (currentPlayerIndex == 1) {
                            playersList.get(0).setPlayerState(WAITING);
                        }

                        checkIfCanSend(lineArray, line);
                        checkAnswer(lineArray,currentPlayerIndex);
                        this.setPlayerState(CAN_ASK);
                    }
                }
            }

            System.exit(0);

        } catch (IOException ex) {
            System.out.println(RECEIVING_ERROR + name + " : " + ex.getMessage());
        }
    }


    // PRIVATE METHODS
    private void printCard() {

        try {

            out.write(YOUR_CARD_IS + playersCard.getName() + "\n\n" + playersCard.getAsci());
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
        }
    }

    private void setInitialStates() {

        if (playersList.size() == 1) {
            this.setPlayerState(CAN_ASK);
        }

        if (playersList.size() == 2) {
            this.setPlayerState(WAITING);
        }
    }

    private void promptMessages() {

        if (this.getPlayerState() == CAN_ASK) {
            messageToUser("Ask a question (/ask): ");
        }

        if (this.getPlayerState() == CAN_ANSWER) {
            messageToUser("Write your answer (/yes or /no): ");
        }

        if (this.getPlayerState() == WAITING) {
            messageToUser("Waiting for your opponent: ");
        }
    }

    private void checkPlayerAnswer(CardType[] cardTypes, String[] lineArray, int currentPlayerIndex) {

        for (CardType cardType : cardTypes) {

            if (lineArray[1].toLowerCase().trim().contains(cardType.getName().toLowerCase())) {

                if (currentPlayerIndex == 0) {

                    if (cardType == playersList.get(1).getPlayersCard()) {

                        this.setPlayerState(WON);
                        playersList.get(1).setPlayerState(LOST);

                    } else {

                        this.setPlayerState(LOST);
                        playersList.get(1).setPlayerState(WON);

                    }
                }

                if (currentPlayerIndex == 1) {

                    if (cardType == playersList.get(0).getPlayersCard()) {

                        this.setPlayerState(WON);
                        playersList.get(0).setPlayerState(LOST);

                    } else {

                        this.setPlayerState(LOST);
                        playersList.get(0).setPlayerState(WON);

                    }
                    return;
                }
            }
        }
    }

    private void sendWonLostMessage() {

        if (this.getPlayerState() == LOST || this.getPlayerState() == WON) {

            if (this.getPlayerState() == LOST) {
                messageToUser(LOSE);
            }

            if (this.getPlayerState() == WON) {
                messageToUser(WIN);
            }

            // CLOSE CONNECTION
            System.exit(0);
        }
    }

    private void changePlayerStateAnswer(int currentPlayerIndex) {

        if (currentPlayerIndex == 0) {
            playersList.get(1).setPlayerState(CAN_ANSWER);
        }

        if (currentPlayerIndex == 1) {
            playersList.get(0).setPlayerState(CAN_ANSWER);
        }
    }

    private void changePlayerStateWaiting(int currentPlayerIndex) {

        if (currentPlayerIndex == 0) {
            playersList.get(1).setPlayerState(WAITING);
        }

        if (currentPlayerIndex == 1) {
            playersList.get(0).setPlayerState(WAITING);
        }
    }


    private void checkAnswer(String[] lineArray, int currentPlayerIndex) {

        if (lineArray[0].equals("/ask")
                && this.getPlayerState() == CAN_ASK) {

            CardType[] cardTypes = CardType.values();

            checkPlayerAnswer(cardTypes, lineArray, currentPlayerIndex);
            sendWonLostMessage();

            this.setMaxQuestions(this.getMaxQuestions() - 1);

            changePlayerStateAnswer(currentPlayerIndex);

            if (this == playersList.get(0)) {
                playersList.get(1).messageToUser("Write your answer (/yes or /no): ");
            }

            if (this == playersList.get(1)) {
                playersList.get(0).messageToUser("Write your answer (/yes or /no): ");
            }

            this.setPlayerState(WAITING);
        }

        if ((lineArray[0].equals("/yes")
                || lineArray[0].equals("/no"))
                && this.getPlayerState() == CAN_ANSWER) {

            changePlayerStateWaiting(currentPlayerIndex);
            this.setPlayerState(CAN_ASK);
        }
    }


    private void checkIfCanSend(String[] lineArray, String line) {

        if (this.getPlayerState() == CAN_ASK
                && !lineArray[0].equals("/ask")) {

            messageToUser(ASK_ERROR);

        } else if (this.getPlayerState() == CAN_ANSWER
                && (!lineArray[0].equals("/yes") && !lineArray[0].equals("/no"))) {

            messageToUser(QUESTION_ERROR);

        } else {

            // Broadcast message to all other clients
            server.sendAll(name, line);
        }
    }

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

    // SETTERS AND GETTERS
    private void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    private void setMaxQuestions(int maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    private PlayerState getPlayerState() {
        return playerState;
    }

    private int getMaxQuestions() {
        return maxQuestions;
    }

    private CardType getPlayersCard() {
        return playersCard;
    }
}