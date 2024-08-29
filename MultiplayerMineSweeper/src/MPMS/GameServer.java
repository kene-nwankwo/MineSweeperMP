package MPMS;

import java.net.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class GameServer {

    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();  // List to manage connected clients
    public GUI gui;

    // Constructor to initialize the server on a specified port
    public GameServer(int port, GUI g) {
    	this.gui = g;
        try {
            serverSocket = new ServerSocket(port);  // Create the server socket
        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions during server socket creation
        }
    }

    // Method to start the server and listen for client connections
    public void start() {
    	System.out.println("Server Started");
        while (true) {  // Infinite loop to keep the server running
            try {
            	if (gui.serverJoinable && gui.numberOfPlayers < 8) {
            		  Socket clientSocket = serverSocket.accept();  // Accept new client connections
                      ClientHandler clientHandler = new ClientHandler(clientSocket, this, gui.numberOfPlayers, gui);  // Create a handler for each client
                      clients.add(clientHandler);  // Add the client handler to the list
                      
                      System.out.println("New Client");
                      
                      // Add this to gui code, not here in the future
                      gui.numberOfPlayers++;// TODO change to thread locked example
                      //gui.joinedPlayers = new JLabel("New Player!\n test \n test", JLabel.CENTER);
              		//gui.hostPanel.add(joinedPlayer, BorderLayout.WEST);
                      JLabel label1 = new JLabel("New Player");
                      gui.joinedPlayers.add(label1);

                      gui.hostPanel.setPreferredSize(new Dimension(((36 * gui.colNumber)+10), 20 + (16*gui.numberOfPlayers)));
              		  gui.mainPanel.revalidate(); 
                      gui.mainPanel.repaint();  
                      
                      new Thread(clientHandler).start();  // Start a new thread for each client
            	}
            } catch (IOException e) {
                e.printStackTrace();  // Handle exceptions during client connection
            }
        }
    }

    // Method to broadcast messages to all connected clients
    public synchronized void broadcastMessage(String message) {
        for (ClientHandler client : clients) {  // Iterate over all clients
            client.sendMessage(message);  // Send the message to each client
        }
    }
    
    // Method to broadcast messages to all connected clients
    public synchronized void broadcastPlayerNumber() {
        for (ClientHandler client : clients) {  // Iterate over all clients
        	int playerNumber = client.playerNumber;
            client.sendMessage("5," + Integer.toString(playerNumber));  // Send the message to each client
        }
    }


    // Additional methods to handle specific game logic can be added here
}

// Class to handle individual client connections
class ClientHandler implements Runnable {

    private Socket clientSocket;
    private GameServer server;  // Reference to the server instance
    private PrintWriter out;  // Stream to send messages to the client
    public int playerNumber;
    public GUI gui;

    // Constructor to initialize the client handler with a socket and server reference
    public ClientHandler(Socket socket, GameServer server, int numPlayers, GUI gui) {
        this.clientSocket = socket;
        this.server = server;
        this.playerNumber = numPlayers;
        this.gui = gui;
    }

    @Override
    public void run() {
        
    	try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));)
        {
            String message;
            // Loop to continually read messages from the client
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);  // Print received message to the server console
                server.broadcastMessage(message);  // Broadcast the message to all clients
                processClientMessage(message);  // Process the client's message
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions during communication with the client
        } finally {
            try {
                clientSocket.close();  // Close the client socket when done
            } catch (IOException e) {
                e.printStackTrace();  // Handle exceptions during socket closure
            }
        }
    }
    
    private void processClientMessage(String message) {
    	String[] splitMessage = message.split(",");
    	
    	if (Integer.parseInt(splitMessage[0]) == 4) {
    		//server.gui.
    		int move = Integer.parseInt(splitMessage[1]);
    		int player = Integer.parseInt(splitMessage[2]);

        	server.gui.playMoveCheckForWin(move, player);
        	//server.broadcastMessage(message); 
        	
    	} else if (Integer.parseInt(splitMessage[0]) == 6) {
    		if (splitMessage[1].equals("-")) {
    			gui.stuckCount--;
    		} else {
    			gui.stuckCount++;
	    		gui.moveOnRandomBox();
    		}
    			
    	}

        // Additional commands to handle other actions
    }

    // Method to send messages to the client
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);  // Send a message to the client
        }
    }
}
