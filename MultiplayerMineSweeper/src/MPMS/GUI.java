package MPMS;

import javax.swing.border.BevelBorder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


// Fix:
// Client auto-maticatlly saying connected to host when it is not
// Stuck not working when a player fails
public class GUI{
    private JFrame frame = new JFrame();
    private static final int MAX_MOUSE_MOVEMENT = 10;
    private static final int BOARDER_PADDING = 10;
    private static final int HEADER_SIZE = 40;
    
    private int rowNumber = 9;
    public int colNumber = 9;
    public int mineNumber = 10;
    public Game game = null;	
    
    // Load and scale the images
    public Image scaledFlag = new ImageIcon("Images/MineFlag.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image scaledMine = new ImageIcon("Images/MineMine.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    private Image scaledFalseMine = new ImageIcon("Images/MineMineFail.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    
    // Load Colors
    public Image transparent = new ImageIcon("Images/colors/transparent.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image red = new ImageIcon("Images/colors/red.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image green = new ImageIcon("Images/colors/green.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image blue = new ImageIcon("Images/colors/blue.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image yellow = new ImageIcon("Images/colors/yellow.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image cyan = new ImageIcon("Images/colors/cyan.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image magenta = new ImageIcon("Images/colors/magenta.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image orange = new ImageIcon("Images/colors/orange.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image purple = new ImageIcon("Images/colors/purple.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    
    // Load Numbers
    public Image one = new ImageIcon("Images/numbers/1.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image two = new ImageIcon("Images/numbers/2.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image three = new ImageIcon("Images/numbers/3.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image four = new ImageIcon("Images/numbers/4.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image five = new ImageIcon("Images/numbers/5.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image six = new ImageIcon("Images/numbers/6.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image seven = new ImageIcon("Images/numbers/7.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    public Image eight = new ImageIcon("Images/numbers/8.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36

    
    // For main Panel
    public JPanel mainPanel;
    
    // For top Panel
    private JPanel topPanel;
    private JPanel topLeftPanel;
    private JPanel topRightPanel;
    private JPanel topCenterPanel;
    public JLabel mineCount;
    private JLabel title;
    private TimerLabel time;
    
    // For Difficulty Panel
    private JPanel difficultyPanel;
    private ButtonGroup difficultyGroup;
    private JRadioButton beginnerButton;
    private JRadioButton intermediateButton;
    private JRadioButton expertButton;
    private JRadioButton customButton;
    private JTextField heightField = new JTextField("20");
    private JTextField widthField = new JTextField("30");
    private JTextField mineField = new JTextField("145");
    
    // For Start Panel
    private JPanel startPanel;
    private JButton start;
    
    // For Center Panel
    private JPanel centerPanel;
    
    // For Select Game Mode Panel
    private JPanel gameModePanel;
    
    // For Select Game Mode Panel
    private JPanel hostJoinPanel;
    
    // Join Panel
    private JPanel joinPanel;
    private JTextField IP_Address = new JTextField("10.0.1.7");
    public JLabel enterIP;
    
    // Host Panel
    public JPanel hostPanel;
    public JPanel joinedPlayers;
    
    // For multiplayer state
    public boolean singlePlayer = true;
    public boolean isHost = true;
    public int numberOfPlayers = 1;
    private int playerNumber = 0;
    
    // Stuck Button
    private ButtonGroup stuckGroup;
    private JRadioButton stuck;
    public int stuckCount = 0;
    
    // Game Server
    public GameServer gameServer;
    public boolean serverJoinable = true;
    
    // Game Client
    private GameClient gameClient;
    public boolean connectedToServer = false;
    public String[] split;
    
    // Score Panel
    private JPanel scorePanel;
    private JPanel sp0;
    private JPanel sp1;
    private JPanel sp2;
    private JPanel sp3;
    private JPanel sp4;
    private JPanel sp5;
    private JPanel sp6;
    private JPanel sp7;
    public JLabel sp0L;
    public JLabel sp1L;
    public JLabel sp2L;
    public JLabel sp3L;
    public JLabel sp4L;
    public JLabel sp5L;
    public JLabel sp6L;
    public JLabel sp7L;

    public GUI() {
    	
    	backgroundPanel();

    	topPanel();
    	
    	gameModePanel();
        
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLayout(null);
    	frame.setSize((colNumber*36+20), (HEADER_SIZE + ((rowNumber * 36) + 55)));
    	frame.add(mainPanel);
    	frame.setVisible(true); 
    }
    
    private void multiHostPanel() {
    	
    	isHost = true;

        // Start Server 
        // Start the server on a new thread if the player is hosting
        new Thread(() -> {
            gameServer = new GameServer(4950, this);
            gameServer.start();
        }).start();
  	
    	hostPanel = new JPanel(new BorderLayout());
    	
    	// TODO below need to be updated every time a player joins
    	hostPanel.setPreferredSize(new Dimension(((36 * colNumber)+10), 20 + (16*numberOfPlayers)));
		
		JLabel hostGame = new JLabel("Host Game", JLabel.CENTER);
		hostPanel.add(hostGame, BorderLayout.NORTH);
		
		joinedPlayers = new JPanel();
		joinedPlayers.setLayout(new BoxLayout(joinedPlayers, BoxLayout.Y_AXIS)); // Vertical stacking
		joinedPlayers.add(new JLabel("Joined Players:"));
		
		hostPanel.add(joinedPlayers, BorderLayout.WEST);
		
        // Create another panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Arrange buttons in a row
        
        // Create two buttons
        JButton startButton = new JButton("Start");

        // Add buttons to the button panel
        buttonPanel.add(startButton);
        
        // Mouse listener to detect clicks
        startButton.addMouseListener(new MouseAdapter() {
        	private Point initialClickPoint;
        	
        	// Measure initial click point
            @Override
            public void mousePressed(MouseEvent e) {
                initialClickPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	// Measure release point
            	// Make sure it is close to initial point
                Point releasePoint = e.getPoint();
                if (initialClickPoint.distance(releasePoint) < MAX_MOUSE_MOVEMENT) {
                    // Register this as a single click
                	
                	mainPanel.remove(buttonPanel);
                	mainPanel.remove(hostPanel);
                    mainPanel.revalidate(); 
                    mainPanel.repaint();  
                    
                    serverJoinable = false;
                    gameServer.broadcastMessage("0," + numberOfPlayers);

                	// TODO Close entrance to server
                    multiDifficultyPanel();
                    
                }
            }
        });

		mainPanel.add(hostPanel);
        // Add the button panel to the center of the main panel
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    	
    }
    
    private void multiJoinPanel() {
    	// Client
    	// Enter IP Address
    	// Joined waiting for host to start
    	
    	isHost = false;
    	
    	joinPanel = new JPanel(new BorderLayout());
    	
    	joinPanel.setPreferredSize(new Dimension(((36 * colNumber)+10), 50));
		
		JLabel joinGame = new JLabel("Join Game", JLabel.CENTER);
		joinPanel.add(joinGame, BorderLayout.NORTH);
		
		enterIP = new JLabel("Enter Host's IP Address", JLabel.CENTER);
		joinPanel.add(enterIP, BorderLayout.WEST);
		
		joinPanel.add(IP_Address);
        
        // Create another panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Arrange buttons in a row
        
        // Create two buttons
        JButton joinButton = new JButton("Join");

        // Add buttons to the button panel
        buttonPanel.add(joinButton);
        
        // Mouse listener to detect clicks
        joinButton.addMouseListener(new MouseAdapter() {
        	private Point initialClickPoint;
        	
        	// Measure initial click point
            @Override
            public void mousePressed(MouseEvent e) {
                initialClickPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	// Measure release point
            	// Make sure it is close to initial point
                Point releasePoint = e.getPoint();
                if (initialClickPoint.distance(releasePoint) < MAX_MOUSE_MOVEMENT) {
                    // Register this as a single click
                	
                	mainPanel.remove(buttonPanel);
                	joinPanel.remove(IP_Address);
                    mainPanel.revalidate(); 
                    mainPanel.repaint();  
                    
                	enterIP.setText("Connecting to Server...");

                    // Start Server Connection
                	multiConnectToServer();

                    // If connected update panel saying joined waiting for host to start
                	// else say failed to join reenter ip address
                	
                    // Start Game
                    
                }
            }
        });

		mainPanel.add(joinPanel);
        // Add the button panel to the center of the main panel
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    	
    }
    
    private void multiConnectToServer() {
    	gameClient = new GameClient("10.0.1.7", 4950, this);  // Use "localhost" if the server is on the same machine
        gameClient.listenForMessages();  // Start listening for messages from the server
        connectedToServer = true;
    }
    
    private void multiHostJoinPanel() {
		// Start Panel
    	hostJoinPanel = new JPanel(new BorderLayout());
		
    	hostJoinPanel.setPreferredSize(new Dimension(((36 * colNumber)+10), 50));
		
		JLabel gameMode = new JLabel("Host or Join Match", JLabel.CENTER);
		hostJoinPanel.add(gameMode, BorderLayout.NORTH);
        
        // Create another panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Arrange buttons in a row
        
        // Create two buttons
        JButton hostButton = new JButton("Host");
        JButton joinButton = new JButton("Join");

        // Add buttons to the button panel
        buttonPanel.add(hostButton);
        buttonPanel.add(joinButton);

        // Add the button panel to the center of the main panel
        hostJoinPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Mouse listener to detect clicks
        hostButton.addMouseListener(new MouseAdapter() {
        	private Point initialClickPoint;
        	
        	// Measure initial click point
            @Override
            public void mousePressed(MouseEvent e) {
                initialClickPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	// Measure release point
            	// Make sure it is close to initial point
                Point releasePoint = e.getPoint();
                if (initialClickPoint.distance(releasePoint) < MAX_MOUSE_MOVEMENT) {
                    // Register this as a single click
                	
                    mainPanel.remove(hostJoinPanel);
                    mainPanel.revalidate(); 
                    mainPanel.repaint();  

                    multiHostPanel();
                    
                }
            }
        });
        
        // Mouse listener to detect clicks
        joinButton.addMouseListener(new MouseAdapter() {
        	private Point initialClickPoint;
        	
        	// Measure initial click point
            @Override
            public void mousePressed(MouseEvent e) {
                initialClickPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	// Measure release point
            	// Make sure it is close to initial point
                Point releasePoint = e.getPoint();
                if (initialClickPoint.distance(releasePoint) < MAX_MOUSE_MOVEMENT) {
                    // Register this as a single click
                	
                	singlePlayer = false;
                	
                    mainPanel.remove(hostJoinPanel);
                    mainPanel.revalidate(); 
                    mainPanel.repaint();  
                    
                    multiJoinPanel();
                    
                }
            }
        });

		mainPanel.add(hostJoinPanel);
    	
    }
    
    // Select Single Player or Multiplayer
	private void gameModePanel() {
		// Start Panel
		gameModePanel = new JPanel(new BorderLayout());
		
		gameModePanel.setPreferredSize(new Dimension(((36 * colNumber)+10), 50));
		
		JLabel gameMode = new JLabel("Select Game Mode", JLabel.CENTER);
        gameModePanel.add(gameMode, BorderLayout.NORTH);
        
        // Create another panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Arrange buttons in a row
        
        // Create two buttons
        JButton button1 = new JButton("Single Player");
        JButton button2 = new JButton("Multiplayer");

        // Add buttons to the button panel
        buttonPanel.add(button1);
        buttonPanel.add(button2);

        // Add the button panel to the center of the main panel
        gameModePanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Mouse listener to detect clicks
        button1.addMouseListener(new MouseAdapter() {
        	private Point initialClickPoint;
        	
        	// Measure initial click point
            @Override
            public void mousePressed(MouseEvent e) {
                initialClickPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	// Measure release point
            	// Make sure it is close to initial point
                Point releasePoint = e.getPoint();
                if (initialClickPoint.distance(releasePoint) < MAX_MOUSE_MOVEMENT) {
                    // Register this as a single click
                	
                	singlePlayer = true;
                	playerNumber = -1;
                	
                    mainPanel.remove(gameModePanel);
                    mainPanel.revalidate(); 
                    mainPanel.repaint();  
                
                    singleDifficultyPanel();

                }
            }
        });
        
        // Mouse listener to detect clicks
        button2.addMouseListener(new MouseAdapter() {
        	private Point initialClickPoint;
        	
        	// Measure initial click point
            @Override
            public void mousePressed(MouseEvent e) {
                initialClickPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	// Measure release point
            	// Make sure it is close to initial point
                Point releasePoint = e.getPoint();
                if (initialClickPoint.distance(releasePoint) < MAX_MOUSE_MOVEMENT) {
                    // Register this as a single click
                	
                	singlePlayer = false;
                	
                    mainPanel.remove(gameModePanel);
                    mainPanel.revalidate(); 
                    mainPanel.repaint();  
                    
                    multiHostJoinPanel();
                
                    //createMultiDifficultyPanel();
                    
                    //createSingleStartPanel();    
                    
                    //createSingleCenterPanel();
                }
            }
        });

		mainPanel.add(gameModePanel);
	}
	
	private void multiDifficultyPanel() {
        // Start Panel
        difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new GridLayout(5, 3)); // 5 rows, 3 columns
        //startPanel.setMinimumSize(new Dimension(324, 324));
        difficultyPanel.setPreferredSize(new Dimension(((36 * colNumber)+10), 200));
        
        // Headers
        difficultyPanel.add(new JLabel("")); 
        difficultyPanel.add(new JLabel("Height"));
        difficultyPanel.add(new JLabel("Width"));
        difficultyPanel.add(new JLabel("Mines"));

        // Radio Buttons and Labels
        difficultyGroup = new ButtonGroup();
        beginnerButton = new JRadioButton("Easy");
        beginnerButton.setFont(new Font(beginnerButton.getFont().getName(), Font.PLAIN, 13));
        intermediateButton = new JRadioButton("Medium");
        intermediateButton.setFont(new Font(intermediateButton.getFont().getName(), Font.PLAIN, 13));
        expertButton = new JRadioButton("Hard");
        expertButton.setFont(new Font(expertButton.getFont().getName(), Font.PLAIN, 13));
        customButton = new JRadioButton("Custom");
        customButton.setFont(new Font(customButton.getFont().getName(), Font.PLAIN, 13));
        // Add buttons to button Group
        difficultyGroup.add(beginnerButton);
        difficultyGroup.add(intermediateButton);
        difficultyGroup.add(expertButton);
        difficultyGroup.add(customButton);
        // Beginner Button Labels
        difficultyPanel.add(beginnerButton);
        difficultyPanel.add(new JLabel("9"));
        difficultyPanel.add(new JLabel("9"));
        difficultyPanel.add(new JLabel("10"));
        beginnerButton.setSelected(true);
        // Medium Button Labels
        difficultyPanel.add(intermediateButton);
        difficultyPanel.add(new JLabel("16"));
        difficultyPanel.add(new JLabel("16"));
        difficultyPanel.add(new JLabel("40"));
        // Expert Button Labels 
        difficultyPanel.add(expertButton);
        difficultyPanel.add(new JLabel("16"));
        difficultyPanel.add(new JLabel("30"));
        difficultyPanel.add(new JLabel("99"));
        // Custom Button Labels
        difficultyPanel.add(customButton);
        difficultyPanel.add(heightField);
        difficultyPanel.add(widthField);
        difficultyPanel.add(mineField);
        
        if(isHost) {
        	String message = "1," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
        	gameServer.broadcastMessage(message);
        	gameServer.broadcastPlayerNumber();
        }
        
        // Listeners for each button
        beginnerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                // System.out.println("Beginner selected");
                rowNumber = 9;
                colNumber = 9;
                mineNumber = 10;
               
                if(isHost) {
                String message = "1," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
                gameServer.broadcastMessage(message);
                }
            }
        });
        
        intermediateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                // System.out.println("Intermediate selected");
                rowNumber = 16;
                colNumber = 16;
                mineNumber = 40;
                
                if(isHost) {
                String message = "1," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
                gameServer.broadcastMessage(message);
                }
            }
        });
        
        expertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                // System.out.println("Expert selected");
                rowNumber = 16;
                colNumber = 30;
                mineNumber = 99;
                
                if(isHost) {
                String message = "1," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
                gameServer.broadcastMessage(message);
                }
            }
        });
        
        customButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                rowNumber = Integer.parseInt(heightField.getText());
                colNumber = Integer.parseInt(widthField.getText());
                mineNumber = Integer.parseInt(mineField.getText());
                
                if(isHost) {
                String message = "1," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
                gameServer.broadcastMessage(message);
                }
            }
        });
        
        mainPanel.add(difficultyPanel);
        
		// Create button and Panel
        startPanel = new JPanel();
        start = new JButton();
        start.setText("Start");
        
        start.setBorderPainted(false);
        start.setContentAreaFilled(false);
        start.setFocusPainted(false);
        start.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        startPanel.add(start);
        // Add to main Panel
        if(isHost) {
        	mainPanel.add(startPanel);
        }
        
        // When start is clicked
        start.addMouseListener(new MouseAdapter() {
        	private Point initialClickPoint;
        	     	
            @Override
            public void mousePressed(MouseEvent e) {
            	// Get initial Click Press Point
                initialClickPoint = e.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
            	// Get Click Release Point
                Point releasePoint = e.getPoint();
                
                // If relasePoint is near press point and it was a left mouse click
                if (initialClickPoint.distance(releasePoint) < MAX_MOUSE_MOVEMENT && SwingUtilities.isLeftMouseButton(e)) {
                	
                    String message = "2," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
                    gameServer.broadcastMessage(message);
                    
                    // Register this as a single click
                	if (mineNumber <= (rowNumber*colNumber - 9)) {
                    	// Select difficulty group
                    	ButtonModel selectedButtonModel = difficultyGroup.getSelection();
                    	if (selectedButtonModel != null) {
                            if(customButton.isSelected()) {
                                rowNumber = Integer.parseInt(heightField.getText());
                                colNumber = Integer.parseInt(widthField.getText());
                                mineNumber = Integer.parseInt(mineField.getText());
                            }
                            
                            // Start Game
                            mainPanel.remove(difficultyPanel);
                            mainPanel.remove(startPanel);
                            startGame();
                    	}
                	}
                }
            }
        });
	}

	// Create background panel with color
	private void backgroundPanel() {
    	mainPanel = new JPanel();
    	// Sets how close inner panels can be to boarder
    	mainPanel.setBorder(BorderFactory.createEmptyBorder(BOARDER_PADDING, BOARDER_PADDING, BOARDER_PADDING, BOARDER_PADDING));
    	// Set Size of colored panel
    	mainPanel.setBounds(0,0,(colNumber*36+20),(HEADER_SIZE + ((rowNumber * 36) + 35)));
   
    	// Random Color for MineSweeper Background
    	Random rand = new Random();
    	int r = rand.nextInt(256);
    	int g = rand.nextInt(256);
    	int b = rand.nextInt(256);
    	mainPanel.setBackground(new Color(r, g, b));
	}
	
	// Logic for top bar on screen Contains game reset logic
	private void topPanel() {
    	// Top Panel
        topPanel = new JPanel();
        // Set layout and dimensions
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        topPanel.setMinimumSize(new Dimension((36 * colNumber), HEADER_SIZE));
        topPanel.setPreferredSize(new Dimension((36 * colNumber), HEADER_SIZE));
        
        // Create the left, right, and center sub-panels
        topLeftPanel = new JPanel();
        topRightPanel = new JPanel();
        topCenterPanel = new JPanel();
        // Set Borders
        topLeftPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        topCenterPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        topRightPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        // Add info to top panels
        mineCount = new JLabel("Mines:   ");
        topLeftPanel.add(mineCount);
        title = new JLabel("MineSweeper");
        topCenterPanel.add(title);
        time = new TimerLabel();
        topRightPanel.add(time);
        
        // Add sub-panels to the topPanel
        topPanel.add(topLeftPanel, BorderLayout.WEST);
        topPanel.add(topRightPanel, BorderLayout.EAST);
        topPanel.add(topCenterPanel, BorderLayout.CENTER);
        
        // If top panel is clicked; resets game
        topCenterPanel.addMouseListener(new MouseAdapter() {
        	private Point initialClickPoint;

            @Override
            public void mousePressed(MouseEvent e) {
            	// Get initial Click Press Point
                initialClickPoint = e.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
            	// Get Click Release Point
                Point releasePoint = e.getPoint();
                
                // If relasePoint is near press point and it was a left mouse click
                if (initialClickPoint.distance(releasePoint) < MAX_MOUSE_MOVEMENT && SwingUtilities.isLeftMouseButton(e)) {
                	if (game != null && game.gameState() != 0 && singlePlayer) {
                		
                		// Remove old panels add new ones and repaint
                        mainPanel.remove(topPanel);
                        mainPanel.remove(centerPanel);
                        mainPanel.revalidate(); 
                        mainPanel.repaint();  
                		
                    	// Reset game parameters
                		game = null;
                	    rowNumber = 9;
                	    colNumber = 9;
                	    mineNumber = 10;
                    	mainPanel.setBounds(0,0,(colNumber*36+20),(HEADER_SIZE + ((rowNumber * 36) + 35)));
                    	
                        // Reset frame size
                        frame.setSize((colNumber*36+20), (HEADER_SIZE + ((rowNumber * 36) + 55)));
                        frame.revalidate();
                        frame.repaint();

                    	topPanel();
                        
                        singleDifficultyPanel();

                	}
                }
            }
        });
        
        // Add top panel to main
        mainPanel.add(topPanel, BorderLayout.NORTH);
	}
	
	private void scorePanel() {
    	// Top Panel
        scorePanel = new JPanel();
        // Set layout and dimensions
        scorePanel.setLayout(new GridLayout(2, 4));
        scorePanel.setBorder(BorderFactory.createRaisedBevelBorder());
        scorePanel.setMinimumSize(new Dimension((36 * colNumber), HEADER_SIZE));
        scorePanel.setPreferredSize(new Dimension((36 * colNumber), HEADER_SIZE));
        
        // Create the left, right, and center sub-panels
        sp0 =  new JPanel();
        sp1 =  new JPanel();
        sp2 =  new JPanel();
        sp3 =  new JPanel();
        sp4 =  new JPanel();
        sp5 =  new JPanel();
        sp6 =  new JPanel();
        sp7 =  new JPanel();
        
        sp0L = new JLabel("Player 1: 0");
        sp1L = new JLabel("Player 2: 0");
        sp2L = new JLabel("Player 3: 0");
        sp3L = new JLabel("Player 4: 0");
        sp4L = new JLabel("Player 5: 0");
        sp5L = new JLabel("Player 6: 0");
        sp6L = new JLabel("Player 7: 0");
        sp7L = new JLabel("Player 8: 0");
        
        sp0L.setFont(new Font("Arial", Font.BOLD, 12));
        sp1L.setFont(new Font("Arial", Font.BOLD, 12));
        sp2L.setFont(new Font("Arial", Font.BOLD, 12));
        sp3L.setFont(new Font("Arial", Font.BOLD, 12));
        sp4L.setFont(new Font("Arial", Font.BOLD, 12));
        sp5L.setFont(new Font("Arial", Font.BOLD, 12));
        sp6L.setFont(new Font("Arial", Font.BOLD, 12));
        sp7L.setFont(new Font("Arial", Font.BOLD, 12));

        sp0L.setForeground(Color.RED);
        sp1L.setForeground(Color.GREEN);
        sp2L.setForeground(Color.BLUE);
        sp3L.setForeground(Color.YELLOW);
        sp4L.setForeground(Color.CYAN);
        sp5L.setForeground(Color.MAGENTA);
        sp6L.setForeground(Color.ORANGE);
        sp7L.setForeground(Color.PINK);
        
        sp0.add(sp0L);
        sp1.add(sp1L);
        sp2.add(sp2L);
        sp3.add(sp3L);
        sp4.add(sp4L);
        sp5.add(sp5L);
        sp6.add(sp6L);
        sp7.add(sp7L);
        
        if (numberOfPlayers >= 1) {
            scorePanel.add(sp0);
        }
        if (numberOfPlayers >= 2) {
            scorePanel.add(sp1);
        }
        if (numberOfPlayers >= 3) {
            scorePanel.add(sp2);
        }
        if (numberOfPlayers >= 4) {
            scorePanel.add(sp3);
        }
        if (numberOfPlayers >= 5) {
            scorePanel.add(sp4);
        }
        if (numberOfPlayers >= 6) {
            scorePanel.add(sp5);
        }
        if (numberOfPlayers >= 7) {
            scorePanel.add(sp6);
        }
        if (numberOfPlayers >= 8) {
            scorePanel.add(sp7);
        }
        
        // Add top panel to main
        mainPanel.add(scorePanel);
	}

	// Logic to select difficulty
	private void singleDifficultyPanel() {
        // Start Panel
        difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new GridLayout(5, 3)); // 5 rows, 3 columns
        //startPanel.setMinimumSize(new Dimension(324, 324));
        difficultyPanel.setPreferredSize(new Dimension(((36 * colNumber)+10), 200));
        
        // Headers
        difficultyPanel.add(new JLabel("")); 
        difficultyPanel.add(new JLabel("Height"));
        difficultyPanel.add(new JLabel("Width"));
        difficultyPanel.add(new JLabel("Mines"));

        // Radio Buttons and Labels
        difficultyGroup = new ButtonGroup();
        beginnerButton = new JRadioButton("Easy");
        beginnerButton.setFont(new Font(beginnerButton.getFont().getName(), Font.PLAIN, 13));
        intermediateButton = new JRadioButton("Medium");
        intermediateButton.setFont(new Font(intermediateButton.getFont().getName(), Font.PLAIN, 13));
        expertButton = new JRadioButton("Hard");
        expertButton.setFont(new Font(expertButton.getFont().getName(), Font.PLAIN, 13));
        customButton = new JRadioButton("Custom");
        customButton.setFont(new Font(customButton.getFont().getName(), Font.PLAIN, 13));
        // Add buttons to button Group
        difficultyGroup.add(beginnerButton);
        difficultyGroup.add(intermediateButton);
        difficultyGroup.add(expertButton);
        difficultyGroup.add(customButton);
        // Beginner Button Labels
        difficultyPanel.add(beginnerButton);
        difficultyPanel.add(new JLabel("9"));
        difficultyPanel.add(new JLabel("9"));
        difficultyPanel.add(new JLabel("10"));
        beginnerButton.setSelected(true);
        // Medium Button Labels
        difficultyPanel.add(intermediateButton);
        difficultyPanel.add(new JLabel("16"));
        difficultyPanel.add(new JLabel("16"));
        difficultyPanel.add(new JLabel("40"));
        // Expert Button Labels 
        difficultyPanel.add(expertButton);
        difficultyPanel.add(new JLabel("16"));
        difficultyPanel.add(new JLabel("30"));
        difficultyPanel.add(new JLabel("99"));
        // Custom Button Labels
        difficultyPanel.add(customButton);
        difficultyPanel.add(heightField);
        difficultyPanel.add(widthField);
        difficultyPanel.add(mineField);
        
        // Listeners for each button
        beginnerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                // System.out.println("Beginner selected");
                rowNumber = 9;
                colNumber = 9;
                mineNumber = 10;
            }
        });
        
        intermediateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                // System.out.println("Intermediate selected");
                rowNumber = 16;
                colNumber = 16;
                mineNumber = 40;
            }
        });
        
        expertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                // System.out.println("Expert selected");
                rowNumber = 16;
                colNumber = 30;
                mineNumber = 99;
            }
        });
        
        customButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                rowNumber = Integer.parseInt(heightField.getText());
                colNumber = Integer.parseInt(widthField.getText());
                mineNumber = Integer.parseInt(mineField.getText());
            }
        });
		
        mainPanel.add(difficultyPanel);
        
		// Create button and Panel
        startPanel = new JPanel();
        start = new JButton();
        start.setText("Start");
        
        start.setBorderPainted(false);
        start.setContentAreaFilled(false);
        start.setFocusPainted(false);
        start.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        startPanel.add(start);
        // Add to main Panel
        mainPanel.add(startPanel);
        
        // When start is clicked
        start.addMouseListener(new MouseAdapter() {
        	private Point initialClickPoint;
        	
        	
            @Override
            public void mousePressed(MouseEvent e) {
            	// Get initial Click Press Point
                initialClickPoint = e.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
            	// Get Click Release Point
                Point releasePoint = e.getPoint();
                
                // If relasePoint is near press point and it was a left mouse click
                if (initialClickPoint.distance(releasePoint) < MAX_MOUSE_MOVEMENT && SwingUtilities.isLeftMouseButton(e)) {
                    // Register this as a single click
                	if (mineNumber <= (rowNumber*colNumber - 9)) {
                    	// Select difficulty group
                    	ButtonModel selectedButtonModel = difficultyGroup.getSelection();
                    	if (selectedButtonModel != null) {
                            if(customButton.isSelected()) {
                                rowNumber = Integer.parseInt(heightField.getText());
                                colNumber = Integer.parseInt(widthField.getText());
                                mineNumber = Integer.parseInt(mineField.getText());
                            }
                            
                            // Start Game
                            mainPanel.remove(difficultyPanel);
                            mainPanel.remove(startPanel);
                            startGame();
                    	}
                	}
                }
            }
        });
        
        
	}
	
    // Game logic, repainting, etc
	private void startGame() {
		if(!singlePlayer) {
			scorePanel();
			stuckButton();
		}
		centerPanel = new JPanel();

		// Initialize game class
        game = new Game(rowNumber, colNumber, mineNumber, this, playerNumber, singlePlayer, isHost, numberOfPlayers); 
        // Remove old panels and update main panel

        centerPanel = new JPanel();
        mainPanel.add(centerPanel);
        mainPanel.revalidate(); 
        mainPanel.repaint();
        
        // Set number of mines counter
        mineCount.setText("Mines: "+ mineNumber);
        
        // Panel with game, center panel
        centerPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        centerPanel.setMinimumSize(new Dimension((colNumber * 36), (rowNumber * 36)));
        centerPanel.setPreferredSize(new Dimension((colNumber * 36), (rowNumber * 36)));
        
        // Resize main panel and frame
        mainPanel.setBounds(0,0,(colNumber*36+20),(HEADER_SIZE + ((rowNumber * 36) + 35)));
        frame.setSize((colNumber*36+20), (HEADER_SIZE + ((rowNumber * 36) + 55)));

        if(!singlePlayer) {
            mainPanel.setBounds(0,0,(colNumber*36+20),(HEADER_SIZE + HEADER_SIZE + ((rowNumber * 36) + 35)));
            frame.setSize((colNumber*36+20), (HEADER_SIZE + HEADER_SIZE + ((rowNumber * 36) + 55)));
        }
        
        // Creating mineSweeper GUI grid
        centerPanel.setLayout(new GridLayout(rowNumber, colNumber));
        
        for(int i = 0; i < (rowNumber * colNumber); i++) {
        	// Creating button, bevel, alignments
        	JButton a = new JButton();
        	a.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        	a.setHorizontalAlignment(SwingConstants.CENTER);
        	a.setVerticalAlignment(SwingConstants.CENTER);

        	final int finalI = i;
        	
        	// Adding button to game
        	game.getBox(finalI).button = a;
        	
            // Mouse listener to detect clicks
            a.addMouseListener(new MouseAdapter() {
            	private Point initialClickPoint;
            	
            	// Measure initial click point
                @Override
                public void mousePressed(MouseEvent e) {
                    initialClickPoint = e.getPoint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                	// Measure release point
                	// Make sure it is close to initial point
                    Point releasePoint = e.getPoint();
                    if (initialClickPoint.distance(releasePoint) < MAX_MOUSE_MOVEMENT) {
                        // Register this as a single click
                    	if (game.gameState() == 0) {
                    		time.setTimerRunning(true);
                    	}
                    	
                    	// If it is a right click
                    	if (SwingUtilities.isRightMouseButton(e)) {
                    		// If the game is ongoing and the box has not already been revealed
                    		// Toggle flag, update mine count, update game state
                        	if (!game.firstMove && !game.getBox(finalI).revealed && game.gameState() == 0) {
                        		if (!game.getBox(finalI).flagged) {
                            		a.setIcon(new ImageIcon(scaledFlag));
                            		mineNumber--;
                            	} else {
                            		a.setIcon(null);
                            		mineNumber++;
                            	}
                        		mineCount.setText("Mines: "+ mineNumber);
                            	game.setBoxFlag(finalI, !game.getBox(finalI).flagged);
                        	}
                        	
                        // If it is a left click
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                        	// If it is the first move or box is not flagged and game is ongoing
                        	if (game.firstMove || !game.getBox(finalI).flagged && game.gameState() == 0) {
                        		// Update game with move
                        		
                        		// Client sends message
                        		if (!singlePlayer && !isHost) {
                        			if (!game.firstMove) {
                        				gameClient.sendMessage("4," + Integer.toString(finalI) + "," + playerNumber);
                        			}
                        		} else {
                        			playMoveCheckForWin(finalI, 0);
	                        	}
                        	}
                        }
                    }
                }
            });
        	centerPanel.add(a);
        }
    }

    private void stuckButton() {
		title.setText("MineSweeper: Stuck?:");
		title.setFont(new Font(title.getFont().getName(), Font.PLAIN, 10));
		stuckGroup = new ButtonGroup();
		stuck = new JRadioButton();
		topCenterPanel.add(stuck);
		title.setFont(new Font(title.getFont().getName(), Font.PLAIN, 10));
		
		stuck.addItemListener(e -> {
			System.out.println(stuckCount);
		    if (e.getStateChange() == ItemEvent.SELECTED) {
		        // Code to run when the button is clicked
		        // System.out.println("Button clicked");
		    	// Send message in server to update button state
		    	if (!isHost) {
		    		// Send message in server to update button state
		    		gameClient.sendMessage("6,+");
		    	} else {
		    		// count update on own
		    		stuckCount++;
		    		if (stuckCount == numberOfPlayers) {
		    			moveOnRandomBox();
		    		}
		    	}
		    } else {
		        // Code to run when the button is unclicked
		        // System.out.println("Button unclicked");
		        // Send message in server to update button state
		    	if (!isHost) {
		    		// Send message in server to update button state
		    		gameClient.sendMessage("6,-");
		    	} else {
		    		// count update on own
		    		stuckCount--;
		    	}
		    }
		});

	}

	public void moveOnRandomBox() {
		if(game.gameState() == 0 && !game.firstMove && stuckCount == numberOfPlayers) {
			int max = rowNumber * colNumber;
			// Randomly generates mines avoiding avoid_set
			ArrayList<Integer> scrambledBoxes = createAndScrambleList(max);
			int i = 0;
			int row = scrambledBoxes.get(i) / colNumber;
			int col = scrambledBoxes.get(i) % colNumber;

			while (game.grid.get(row).get(col).revealed || game.grid.get(row).get(col).value == -1) {				
				i++;
				row = scrambledBoxes.get(i) / colNumber;
				col = scrambledBoxes.get(i) % colNumber;
				//System.out.println("1144: " + i + "and " + scrambledBoxes.get(i));
			}
			
			playMoveCheckForWin(scrambledBoxes.get(i), -1);
			String message = "6";
            gameServer.broadcastMessage(message);
            stuck.setSelected(false);
		}
	}
	
    public static ArrayList<Integer> createAndScrambleList(int n) {
        // Create a list to hold the numbers
    	ArrayList<Integer> numbers = new ArrayList<>();
        
        // Add numbers 0 to n to the list
        for (int i = 0; i < n; i++) {
            numbers.add(i);
        }
        // Scramble the list
        Collections.shuffle(numbers);
        return numbers;
    }

	// create one Frame
    public static void main(String[] args) {
        new GUI();
    }
    
    public void playMoveCheckForWin(int index, int player) {
		game.move(index, player);
		int r = index / colNumber;
		int c = index % colNumber;
		
		// Update board bevel
    	//a.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    	// If it was a mine update panel with the mine icon
    	if (game.getBox(index).value == -1) {
    		game.grid.get(r).get(c).button.setIcon(new ImageIcon(scaledMine));
    	}
    	
    	
    	// Check if game is over, update title with win or loss
    	if(game.gameState() == 2) {
    		time.setTimerRunning(false);
    		
    		String gameRest;
    		if (singlePlayer) {
    			gameRest = "WIN! Click to Restart";
        		title.setForeground(Color.GREEN);
    		} else {
    			gameRest = "Game Over!";
        		title.setForeground(Color.BLUE);
    		}

    		title.setText(gameRest);

  			
    	} else if(game.gameState() == 1) {
    		time.setTimerRunning(false);
    		
    		String gameRest;
    		if (singlePlayer) {
    			gameRest = "LOSS! Click to Restart";
        		title.setForeground(Color.RED);
    		} else {
    			gameRest = "Game Over!";
        		title.setForeground(Color.BLUE);
    		}

    		title.setText(gameRest);
    		
    		// Show all the false flags if it was a loss
    		for(int i = 0; i < (rowNumber * colNumber); i++) {
    			if (game.getBox(i).flagged && game.getBox(i).value != -1) {
    				game.getBox(i).button.setIcon(new ImageIcon(scaledFalseMine));
    			}
    		}
    	}
    	
	}
    
	public void serverComm(String[] split) {
		int valZero = Integer.parseInt(split[0]);
		
		this.split = split;
		
		if (valZero == 0) {
			// Exit join screen go to difficulty difficulty selection
			numberOfPlayers = Integer.parseInt(split[1]);;
			mainPanel.remove(joinPanel);
			multiDifficultyPanel();
			mainPanel.revalidate();
            mainPanel.repaint();
			
		} else if (valZero == 1) {
			// update values of row, col, mines
			// TODO
		} else if (valZero == 2) {
			// Game started final update of row col mines
			rowNumber = Integer.parseInt(split[1]);
			colNumber = Integer.parseInt(split[2]);
			mineNumber = Integer.parseInt(split[3]);
		
			mainPanel.remove(difficultyPanel);
            mainPanel.remove(startPanel);
			startGame();			
			
		} else if (valZero == 3) {
			// First move index and game board
			time.setTimerRunning(true);
			game.move(Integer.parseInt(split[1]), -1);
		} else if (valZero == 4) {
			// Perform move
			int move = Integer.parseInt(split[1]);
			int player = Integer.parseInt(split[2]);
			
			playMoveCheckForWin(move, player);
		} else if (valZero == 5) {
			// Set player Number
			playerNumber = Integer.parseInt(split[1]);
		} else if (valZero == 6) {
			if (split.length == 1) {
				stuck.setSelected(false);
				stuckCount = 0;
			}
		} else {
			System.out.println(split[0]);
		}
		
		
	}    

}

