package MPMS;

import java.net.*;
import java.io.*;

public class GameClient {

    private Socket socket;
    private PrintWriter out;  // Stream to send messages to the server
    private BufferedReader in;  // Stream to receive messages from the server
    private GUI gui;

    // Constructor to initialize the client with server address and port
    public GameClient(String serverAddress, int port, GUI g) {
        try {
            socket = new Socket(serverAddress, port);  // Create the client socket and connect to the server
            out = new PrintWriter(socket.getOutputStream(), true);  // Initialize output stream
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // Initialize input stream
        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions during socket creation
        }
        
        // TODO Add something here to allow it to retry connecting to host
        this.gui = g;
        System.out.println("Joined Server");
        
        gui.enterIP.setText("Connected to Host");
    }

    // Method to send a message to the server
    public void sendMessage(String message) {
        out.println(message);  // Send the message to the server
    }

    // Method to listen for messages from the server
    public void listenForMessages() {
        // Run the message listening on a separate thread to avoid blocking the main game loop
        new Thread(() -> {
            String message;
            try {
                // Loop to continually read messages from the server
                while ((message = in.readLine()) != null) {
                    handleServerMessage(message);  // Process the server's message
                }
            } catch (IOException e) {
                e.printStackTrace();  // Handle exceptions during communication with the server
            }
        }).start();  // Start the thread
    }

    // Method to handle incoming messages from the server
    private void handleServerMessage(String message) {
        // Update the local game state based on the server's message
    	
    	// Example
        if (message.startsWith("MOVE_UP")) {
            // game.moveOtherPlayerUp();  // Move the other player's character up

        }
        // Handle other types of updates
        // Parse the message and update the game state
        // Example: update player positions, health, scores, etc.
        System.out.println("Server: " + message);  // For now, just print the message (this can be expanded to game logic)
        gui.serverComm(message.split(","));
    }

    // Method to close the client socket when the game ends or disconnects
    public void close() {
        try {
            socket.close();  // Close the socket
        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions during socket closure
        }
    }
}


