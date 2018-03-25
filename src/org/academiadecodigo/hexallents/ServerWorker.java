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

    private final List<ServerWorker> playersList;
    private int maxQuestions = 5;

    private CardType playersCard;
    private PlayerState playerState = PlayerState.WAITING;

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

                // PROMPT MESSAGES TO PLAYER
                promptMessages();

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
                    checkAnswer(lineArray, currentPlayerIndex);
                    checkIfCanSend(lineArray, line);
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
            this.setPlayerState(PlayerState.CAN_ASK);
        }

        if (playersList.size() == 2) {
            this.setPlayerState(PlayerState.WAITING);
        }
    }

    private void promptMessages() {

        switch (this.getPlayerState()) {
            case CAN_ASK:
                messageToUser(ASK_A_QUESTION);
                break;
            case WAITING:
                messageToUser(WAITING_FOR_OPONNENT);
                break;
            case CAN_ANSWER:
                messageToUser(WRITE_YOUR_ANSWER);
                break;
        }
    }

    private void checkPlayerAnswer(CardType[] cardTypes, String[] lineArray, int currentPlayerIndex) {

        for (CardType cardType : cardTypes) {

            if (lineArray[1].toLowerCase().trim().contains(cardType.getName().toLowerCase())) {

                if (currentPlayerIndex == 0) {

                    if (cardType == playersList.get(1).getPlayersCard()) {

                        this.setPlayerState(PlayerState.WON);
                        playersList.get(1).setPlayerState(PlayerState.LOST);

                    } else {

                        this.setPlayerState(PlayerState.LOST);
                        playersList.get(1).setPlayerState(PlayerState.WON);

                    }
                }

                if (currentPlayerIndex == 1) {

                    if (cardType == playersList.get(0).getPlayersCard()) {

                        this.setPlayerState(PlayerState.WON);
                        playersList.get(0).setPlayerState(PlayerState.LOST);

                    } else {

                        this.setPlayerState(PlayerState.LOST);
                        playersList.get(0).setPlayerState(PlayerState.WON);

                    }
                    return;
                }
            }
        }
    }

    private void sendWonLostMessage() {

        if (this.getPlayerState() == PlayerState.LOST || this.getPlayerState() == PlayerState.WON) {

            if (this.getPlayerState() == PlayerState.LOST) {
                messageToUser(LOSE);
            }

            if (this.getPlayerState() == PlayerState.WON) {
                messageToUser(WIN);
            }

            // CLOSE CONNECTION
            System.exit(0);
        }
    }

    private void changePlayerState(int currentPlayerIndex, PlayerState playerState) {

        if (currentPlayerIndex == 0) {
            playersList.get(1).setPlayerState(playerState);
        }

        if (currentPlayerIndex == 1) {
            playersList.get(0).setPlayerState(playerState);
        }
    }

    private void checkAnswer(String[] lineArray, int currentPlayerIndex) {

        if (lineArray[0].equals("/ask")
                && this.getPlayerState() == PlayerState.CAN_ASK) {

            CardType[] cardTypes = CardType.values();

            checkPlayerAnswer(cardTypes, lineArray, currentPlayerIndex);
            sendWonLostMessage();

            this.setMaxQuestions(this.getMaxQuestions() - 1);

            changePlayerState(currentPlayerIndex, PlayerState.CAN_ANSWER);
            this.setPlayerState(PlayerState.WAITING);
        }

        if ((lineArray[0].equals("/yes")
                || lineArray[0].equals("/no"))
                && this.getPlayerState() == PlayerState.CAN_ANSWER) {

            changePlayerState(currentPlayerIndex, PlayerState.WAITING);
            this.setPlayerState(PlayerState.CAN_ASK);
        }
    }


    private void checkIfCanSend(String[] lineArray, String line) {

        if (this.getPlayerState() == PlayerState.CAN_ASK
                && !lineArray[0].equals("/ask")) {

            messageToUser(ASK_ERROR);

        } else if (this.getPlayerState() == PlayerState.CAN_ANSWER
                && (!lineArray[0].equals("/yes")
                || !lineArray[0].equals("/no"))) {

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

    public String getName() {
        return name;
    }

    private CardType getPlayersCard() {
        return playersCard;
    }
}