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

    private int messageCount=0;

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


    public void sw(int count) {
        synchronized (playersList) {
            playersList.get(count).setPlayerState(PlayerState.WAITING);
        }
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

//        sw(swList.size()-1);

        System.out.println(playersList.get(playersList.size()-1).getPlayerState());

        System.out.println("Thread " + name + " started");

        try {

            int currentPlayerIndex = playersList.indexOf(this);
            System.out.println("index: " + currentPlayerIndex);

                /* SET INITIAL STATES */
                if (playersList.size() == 1) {
                    this.setPlayerState(PlayerState.CAN_ASK);
                    System.out.println("Player 1 ---> " + this.getPlayerState());
                }

                if (playersList.size() == 2) {
                    this.setPlayerState(PlayerState.WAITING);
                    System.out.println("Player 2 ---> " + this.getPlayerState());
                }
            while (!playerSocket.isClosed()) {

                // aqui só há um jogador
                //System.out.println("------->>>>> gfgdbhyd     " + Thread.activeCount());

  /*              if (Thread.activeCount() < 4) {


                    sendWaitingMessage();
                    // estado waiting -> mensagem "espera por oponnent"



                    while (Thread.activeCount() < 4) {
                        System.out.print("");
                    }

                    synchronized (swList) {
                        swList.get(0).setPlayerState(PlayerState.CAN_ASK);
                    }
                    sendMessage();


                } else {
                    sendMessage();

                    // Player 1 pode começar a fazer perguntas
                    // Player 1 state -> canAsk
                }


                while (this.getMaxQuestions() > 0) {
                    sendMessage();
                    this.setMaxQuestions(this.getMaxQuestions()-1);
                }
*/



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




                    // MENSAGEM QUE ENVIO
                    if (line.equals("/ask") && this.getPlayerState() == PlayerState.CAN_ASK) { // recebe /ask é porque está Waiting


                        if (currentPlayerIndex == 0) {
                            playersList.get(1).setPlayerState(PlayerState.CAN_ANSWER);
                        }

                        if (currentPlayerIndex == 1) {
                            playersList.get(0).setPlayerState(PlayerState.CAN_ANSWER);
                        }
                        this.setPlayerState(PlayerState.WAITING);


                        System.out.println("estado da thread " + name +" " + this.getPlayerState());
                        System.out.println(currentPlayerIndex);

                    }





                    if ((line.equals("/yes") || line.equals("/no")) && this.getPlayerState() == PlayerState.CAN_ANSWER) {


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

















  /*  public void sendMessage() {


        System.out.println(Thread.currentThread() + " " + this.getPlayerState());
        System.out.println("1----"+swList.get(1).getPlayerState());

        synchronized (swList) {


            if (swList.get(0).getPlayerState() == PlayerState.CAN_ASK && swList.get(1).getPlayerState() == PlayerState.WAITING
                    || swList.get(0).getPlayerState() == PlayerState.WAITING && swList.get(1).getPlayerState() == PlayerState.CAN_ASK) {

                if (this.getPlayerState() == PlayerState.CAN_ASK) {

                    try {
                        if (messageCount == 0) {
                            messageCount++;
                            out.write("Welcome!");
                        }

                        out.write(" Ask a question:");

                        out.newLine();

                        out.flush();
                        this.setPlayerState(PlayerState.WAITING);

                    } catch (IOException ex) {
                        System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
                    }
                    return;
                }


                if (this.getPlayerState() == PlayerState.WAITING) {

                    try {
                        if (messageCount == 0) {
                            messageCount++;
                            out.write("Welcome!");
                            out.newLine();
                            out.flush();
                        }

                        out.write("Waiting for your opponent's question...");
                        out.newLine();
                        out.flush();

                        this.setPlayerState(PlayerState.CAN_ANSWER);

                    } catch (IOException ex) {
                        System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
                    }
                    return;
                }
            }


            if (swList.get(0).getPlayerState() == PlayerState.WAITING && swList.get(1).getPlayerState() == PlayerState.CAN_ANSWER
                    || swList.get(0).getPlayerState() == PlayerState.CAN_ANSWER && swList.get(1).getPlayerState() == PlayerState.WAITING) {

                if (this.getPlayerState() == PlayerState.WAITING) {

                    try {

                        out.write("Waiting for opponent's answer:");
                        out.newLine();
                        out.flush();
                        this.setPlayerState(PlayerState.WAITING);

                    } catch (IOException ex) {
                        System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
                    }
                    return;
                }

                if (this.getPlayerState() == PlayerState.CAN_ANSWER) {

                    try {

                        out.write("Answer y/n");
                        out.newLine();
                        out.flush();

                        this.setPlayerState(PlayerState.CAN_ASK);
                    } catch (IOException ex) {
                        System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
                    }
                }
            }

        }
    }


    public void sendWaitingMessage() {

        try {

            out.write("Waiting for an opponent to start the game.");
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
        }
    }*/
}
