package org.academiadecodigo.hexallents;

import org.academiadecodigo.hexallents.HelperClasses.Random;
import java.io.*;
import java.net.Socket;
import java.util.List;

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

    private CardType playersCard;

    public enum PlayerState { CAN_ASK, CAN_ANSWER, WAITING}
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

    public void printCard() {

        try {

            out.write(YOUR_CARD_IS + playersCard.getName() + "\n\n" + playersCard.getAsci());
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
        }
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public int getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(int maxQuestions) {
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

        System.out.println(playersList.get(playersList.size()-1).getPlayerState());

        System.out.println("Thread " + name + " started");

        try {

            int currentPlayerIndex = playersList.indexOf(this);
            System.out.println("index: " + currentPlayerIndex);


            /* SET INITIAL PLAYER STATES */
            if (playersList.size() == 1) {
                this.setPlayerState(PlayerState.CAN_ASK);
                System.out.println("Player 1 ---> " + this.getPlayerState());
            }

            if (playersList.size() == 2) {
                this.setPlayerState(PlayerState.WAITING);
                System.out.println("Player 2 ---> " + this.getPlayerState());
            }


            while (!playerSocket.isClosed()) {

                /* MESSAGES TO PLAYER */
                if (this.getPlayerState() == PlayerState.CAN_ASK) {
                    messageToUser("Ask a question: ");
                }

                if (this.getPlayerState() == PlayerState.WAITING) {
                    messageToUser("Waiting for your opponent: ");
                }

                if (this.getPlayerState() == PlayerState.CAN_ANSWER) {
                    messageToUser("Write your answer: ");
                }


                System.out.println("player 1 state: " + playersList.get(0).getPlayerState());
                System.out.println("player 2 state: " + playersList.get(playersList.size() -1).getPlayerState());

                String line = null;

                // Blocks waiting for client messages
                line = in.readLine();

                if (line == null) {

                    System.out.println("Player " + name + " closed, exiting...");

                    in.close();
                    playerSocket.close();
                    continue;

                } else if (!line.isEmpty()) {

                    String[] lineArray = line.split(" ");

                    if (maxQuestions == 3) {
                        System.out.println("3");
                    }

                    System.out.println("maxQuestions " + maxQuestions);

                    // MESSAGE PLAYER SENDS
                    if (lineArray[0].equals("/ask") && this.getPlayerState() == PlayerState.CAN_ASK) { // recebe /ask é porque está Waiting

                        CardType[] cardTypes = CardType.values();




                        for (CardType cardType: cardTypes) {
                            if(lineArray[1].toLowerCase().contains(cardType.getName().toLowerCase())){
                                if (currentPlayerIndex == 0) {

                                    if(cardType == playersList.get(1).getPlayersCard()){
                                        // todo victory
                                        System.out.println("ganhou!!!!!!!!");
                                    } else {
                                        System.out.println("perdeu crl");
                                        //todo defeat
                                    }
                                }

                                if (currentPlayerIndex == 1) {

                                    if(cardType == playersList.get(0).getPlayersCard()){
                                        // todo victory
                                        System.out.println("3 amigos e uma conhecida");
                                    } else {
                                        System.out.println("fica a dica");
                                        //todo defeat
                                    }
                                }

                            }
                        }

                        //TODO avaliar lineArray[1] -> percorrer o enum e ver se contains
                        //TODO avaliar tudo lowercase
                        // se for um dos nomes ganha e sai do while / perde


                        System.out.println(this.getMaxQuestions());
                        this.setMaxQuestions(this.getMaxQuestions()-1);
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

                        // Broadcast message to all other clients
                        server.sendAll(name, line);
                    }
                }
            }


            // mensagem acabou o jogo





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


    // print messages to player console
/*    public void sendWaitingMessage() {

        try {

            out.write("Waiting for an opponent to start the game.");
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
        }
    }*/



    public void messageToUser(String message) {

        try {

            out.write(message);
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
        }
    }

    public CardType getPlayersCard() {
        return playersCard;
    }
}
