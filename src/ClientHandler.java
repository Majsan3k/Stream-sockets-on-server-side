/**
 * A tread that always is listening for new messages from a server.
 *
 * @author Maja Lund
 */

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Server server;
    private Socket socket;
    private BufferedReader inDataFromClient;

    /**
     * Open up a new BufferedReader
     * @param clientSocket socket to open new BufferedReader
     * @param server
     */
    public ClientHandler(Socket clientSocket, Server server){
        this.server = server;
        this.socket = clientSocket;
        try {
            inDataFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            server.removeClient(socket);
            System.out.println(e.getMessage());
        }
    }

    /**
     * Listen after new messages from the BufferedReader.
     */
    @Override
    public void run() {
        String message;
        try {
            while ((message = inDataFromClient.readLine()) != null) {
                if(message.equals("wwhhoo")){
                    server.sendWwhhooMessage(socket);
                }else {
                    server.broadcastMessage(socket, message);
                }
            }
            inDataFromClient.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        server.removeClient(socket);
    }
}
