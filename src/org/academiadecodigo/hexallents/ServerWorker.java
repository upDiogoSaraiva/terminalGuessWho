package org.academiadecodigo.hexallents;

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

    private int messageCount=0;

    public enum PlayerState { CAN_ASK, CAN_ANSWER, WAITING}
    private PlayerState playerState = PlayerState.WAITING;
    private List<ServerWorker> swList;


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

        in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));

        swList = server.getWorkers();

    }


    public void sw(int count) {
        swList.get(count).setPlayerState(PlayerState.WAITING);
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

        System.out.println(swList.size());

        sw(swList.size()-1);
        System.out.println(swList.get(swList.size()-1).getPlayerState());


        System.out.println("Thread " + name + " started");

        try {

            while (!playerSocket.isClosed()) {

                // aqui só há um jogador
                if (Thread.activeCount() < 4) {

                    sendWaitingMessage();
                    // estado waiting -> mensagem "espera por oponnent"


                    while (Thread.activeCount() < 4) {
                        //System.out.println("counts" + Thread.activeCount());
                    }
                    swList.get(0).setPlayerState(PlayerState.CAN_ASK);
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

    public void sendMessage() {


        System.out.println(Thread.currentThread() + " " + this.getPlayerState());
        System.out.println("1----"+swList.get(1).getPlayerState());

        if (swList.get(0).getPlayerState() == PlayerState.CAN_ASK && swList.get(1).getPlayerState() == PlayerState.WAITING
                || swList.get(0).getPlayerState() == PlayerState.WAITING && swList.get(1).getPlayerState() == PlayerState.CAN_ASK) {

            if (this.getPlayerState() == PlayerState.CAN_ASK) {

                try {
                    if(messageCount == 0){
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
                    if(messageCount == 0){
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
                //return;
            }
        }














/*        if (this.getPlayerState() == PlayerState.CAN_ASK || this.getPlayerState() == PlayerState.CAN_ASK) {

            try {

                if(messageCount == 0){
                    messageCount++;
                    out.write("Game started! Ask your first question.");
                    System.out.println(swList.get(1).getPlayerState().toString());
                    out.newLine();
                    out.flush();
                    return;
                }

                out.write("Ask your question.");
                messageCount++;
                out.newLine();
                out.flush();

            } catch (IOException ex) {
                System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
            } finally {
                playerState = PlayerState.WAITING;
            }
        }*/

    }



    public void sendWaitingMessage() {

        try {

            out.write("Waiting for an opponent to start the game.");
            out.newLine();
            out.flush();

        } catch (IOException ex) {
            System.out.println(SENDING_MESSAGE_ERROR + name + " : " + ex.getMessage());
        }
    }
}
