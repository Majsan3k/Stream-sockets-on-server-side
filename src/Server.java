/**
 * Chatt server that communicates over Stream Sockets.
 *
 * @author Maja Lund
 */

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Server extends JFrame implements Runnable {

    private ArrayList<Socket> clientSockets = new ArrayList<>();
    private ServerSocket serverSocket;
    private TextArea messageView;
    private int port;

    /**
     * Creates new server and new Socket server
     * @param port socket port
     */
    public Server(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Couldn't connect. " + e.getMessage());
            System.exit(1);
        }

        updateWindowTitle();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(500, 300));
        setVisible(true);
        add(messageView = new TextArea());
        pack();
    }

    /**
     * Update the windows title
     */
    private void updateWindowTitle(){
        try {
            setTitle("SERVER ON: DESKTOP" + serverSocket.getInetAddress().getLocalHost().getHostName() +
                    " - PORT: " + port + " - N CLIENTS: " + clientSockets.size());
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Send a message with all connected sockets to the specified socket.
     * @param socket socket to receive information about all connected sockets
     */
    public synchronized void sendWwhhooMessage(Socket socket){
        messageView.append("CLIENT: " + socket.getInetAddress().getHostName() + " ASK: wwhhoo" + "\n");
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"), true);
            for(Socket s : clientSockets){
                pw.println("WWHHOO: " + s.getInetAddress().getHostName());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Removes client and inform all other that the client has disconnected.
     * @param socket socket to be removed
     */
    public synchronized void removeClient(Socket socket){
        clientSockets.remove(socket);
        broadcastMessage(socket, "CLIENT DISCONNECTED: " + socket.getInetAddress().getHostName());
        updateWindowTitle();
    }

    /**
     * Send a message to all connected clients.
     * @param senderSocket the socket who send the message
     * @param message message to be send
     */
    public synchronized void broadcastMessage(Socket senderSocket, String message) {
        messageView.append("CLIENT: " + senderSocket.getInetAddress().getHostName() + " BROADCAST: " + message + "\n");
        PrintWriter pw;
        for (Socket s : clientSockets) {
            try {
                pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "ISO-8859-1"), true);
                pw.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Listens after new clients that will connect. If anyone connects a broadcast message will be send
     * to all connected sockets with information that a new client has been connected.
     */
    public void run(){
        try{
            while(true){
                Socket socket = serverSocket.accept();
                broadcastMessage(socket, "CLIENT CONNECTED: " + socket.getInetAddress().getHostName());
                clientSockets.add(socket);
                Thread clientThread = new Thread(new ClientHandler(socket, this));
                clientThread.start();
                updateWindowTitle();
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Starts program. Socket port will be 2000 if the user doesn't speecify anything else.
     * @param args
     */
    public static void main(String args[]){
        int port = 2000;
        if(args.length != 0){
            port = Integer.parseInt(args[0]);
        }
        if(args.length > 1){
            System.exit(1);
        }
        Server s = new Server(port);
        Thread serverThread = new Thread(s);
        serverThread.start();
    }
}
