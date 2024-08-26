package MPMS;

import javax.swing.border.BevelBorder;

import java.awt.Color;
import java.util.Random;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI{
    private JFrame frame = new JFrame();
    private static final int MAX_MOUSE_MOVEMENT = 10;
    private static final int BOARDER_PADDING = 10;
    private static final int HEADER_SIZE = 40;
    
    private int rowNumber = 9;
    public int colNumber = 9;
    private int mineNumber = 10;
    private Game game = null;	
    
    // Load and scale the images
    private Image scaledFlag = new ImageIcon("Images/MineFlag.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    private Image scaledMine = new ImageIcon("Images/MineMine.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    private Image scaledFalseMine = new ImageIcon("Images/MineMineFail.png").getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale to 36x36
    
    // For main Panel
    public JPanel mainPanel;
    
    // For top Panel
    private JPanel topPanel;
    private JPanel topLeftPanel;
    private JPanel topRightPanel;
    private JPanel topCenterPanel;
    private JLabel mineCount;
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
    private JTextField IP_Address = new JTextField("000.000.0.000");
    
    // Host Panel
    public JPanel hostPanel;
    public JPanel joinedPlayers;
  

    
    // For multiplayer state
    private boolean singlePlayer = true;
    public boolean isHost = true;
    public int numberOfPlayers = 2;
    
    // Game Server
    public GameServer gameServer;
    public boolean serverJoinable = true;
    
    // Game Client
    private GameClient gameClient;

    public GUI() {
    	
    	createMainPanel();

    	createTopPanel();
    	
    	createGameModePanel();
        
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLayout(null);
    	frame.setSize((colNumber*36+20), (HEADER_SIZE + ((rowNumber * 36) + 55)));
    	frame.add(mainPanel);
    	frame.setVisible(true); 
    }
    
    private void hostPanel() {
    	
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
                    gameServer.broadcastMessage("0");

                	// TODO Close entrance to server
                    createMultiDifficultyPanel();
                    
                    createMultiStartPanel();    
                    
                    createMultiCenterPanel();
                    
                }
            }
        });

		mainPanel.add(hostPanel);
        // Add the button panel to the center of the main panel
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    	
    }
    
    private void joinPanel() {
    	// Client
    	// Enter IP Address
    	// Joined waiting for host to start
    	
    	isHost = false;
    	
    	joinPanel = new JPanel(new BorderLayout());
    	
    	joinPanel.setPreferredSize(new Dimension(((36 * colNumber)+10), 50));
		
		JLabel joinGame = new JLabel("Join Game", JLabel.CENTER);
		joinPanel.add(joinGame, BorderLayout.NORTH);
		
		JLabel enterIP = new JLabel("Enter Host's IP Address", JLabel.CENTER);
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
    
    private void createHostJoinPanel() {
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

                    hostPanel();
                    
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
                    
                    joinPanel();
                    
                }
            }
        });

		mainPanel.add(hostJoinPanel);
    	
    }
    
    
	private void createGameModePanel() {
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
                	
                    mainPanel.remove(gameModePanel);
                    mainPanel.revalidate(); 
                    mainPanel.repaint();  
                
                    createSingleDifficultyPanel();
                    
                    createSingleStartPanel();    
                    
                    createSingleCenterPanel();
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
                    
                    createHostJoinPanel();
                
                    //createMultiDifficultyPanel();
                    
                    //createSingleStartPanel();    
                    
                    //createSingleCenterPanel();
                }
            }
        });

		mainPanel.add(gameModePanel);
	}
	
	private void createMultiDifficultyPanel() {
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
        
        String message = "1," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
        gameServer.broadcastMessage(message);
        
        // Listeners for each button
        beginnerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                // System.out.println("Beginner selected");
                rowNumber = 9;
                colNumber = 9;
                mineNumber = 10;
                
                String message = "1," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
                gameServer.broadcastMessage(message);
            }
        });
        
        intermediateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                // System.out.println("Intermediate selected");
                rowNumber = 16;
                colNumber = 16;
                mineNumber = 40;
                
                String message = "1," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
                gameServer.broadcastMessage(message);
            }
        });
        
        expertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                // System.out.println("Expert selected");
                rowNumber = 16;
                colNumber = 30;
                mineNumber = 99;
                
                String message = "1," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
                gameServer.broadcastMessage(message);
            }
        });
        
        customButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle Beginner selection
                rowNumber = Integer.parseInt(heightField.getText());
                colNumber = Integer.parseInt(widthField.getText());
                mineNumber = Integer.parseInt(mineField.getText());
                
                String message = "1," + Integer.toString(rowNumber) + "," + Integer.toString(colNumber) + "," + Integer.toString(mineNumber);
                gameServer.broadcastMessage(message);
            }
        });
        
		
        mainPanel.add(difficultyPanel);
	}
	
    // Controls mouse pressing logic and starts the game 
    private void createMultiCenterPanel() {
        centerPanel = new JPanel();
        
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
                            startGame();
                    	}
                	}
                }
            }
        });
	}
	
	
	
    // Button that starts the game 
	private void createMultiStartPanel() {
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
	}

	// Create background panel with color
	private void createMainPanel() {
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
	private void createTopPanel() {
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
                	if (game != null && game.gameState() != 0) {
                		
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

                    	createTopPanel();
                        
                        createSingleDifficultyPanel();
                        
                        createSingleStartPanel();    
                        
                        createSingleCenterPanel();
                	}
                }
            }
        });
        
        // Add top panel to main
        mainPanel.add(topPanel, BorderLayout.NORTH);
	}

	// Logic to select difficulty
	private void createSingleDifficultyPanel() {
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
	}
	
	
    // Button that starts the game 
	private void createSingleStartPanel() {
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
	}
	
    // Controls mouse pressing logic and starts the game 
    private void createSingleCenterPanel() {
        centerPanel = new JPanel();
        
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
                            startGame();
                    	}
                	}
                }
            }
        });
	}
	
    // Game logic, repainting, etc
	private void startGame() {

		// Initialize game class
        game = new Game(rowNumber, colNumber, mineNumber, this); 
        // Remove old panels and update main panel
        mainPanel.remove(difficultyPanel);
        mainPanel.remove(startPanel);
        mainPanel.add(centerPanel);
        mainPanel.revalidate(); 
        mainPanel.repaint();
        
        // Set number of mines counter
        mineCount.setText("Mines: "+ mineNumber);
        time.setTimerRunning(true);
        
        // Panel with game, center panel
        centerPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        centerPanel.setMinimumSize(new Dimension((colNumber * 36), (rowNumber * 36)));
        centerPanel.setPreferredSize(new Dimension((colNumber * 36), (rowNumber * 36)));
        
        // Resize main panel and frame
        mainPanel.setBounds(0,0,(colNumber*36+20),(HEADER_SIZE + ((rowNumber * 36) + 35)));
        frame.setSize((colNumber*36+20), (HEADER_SIZE + ((rowNumber * 36) + 55)));

        
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
                        		game.move(finalI);
                        		// Update board bevel
                            	a.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                            	// If it was a mine update panel with the mine icon
                            	if (game.getBox(finalI).value == -1) {
                            		a.setIcon(new ImageIcon(scaledMine));
                            	}
                            	// Check if game is over, update title with win or loss
                            	if(game.gameState() == 2) {
                            		time.setTimerRunning(false);
                            		
                            		String gameRest = "WIN! Click to Restart";
                            		title.setText(gameRest);
                            		title.setForeground(Color.GREEN);
                          			
                            	} else if(game.gameState() == 1) {
                            		time.setTimerRunning(false);
                            		
                            		String gameRest = "LOSS! Click to Restart";
                            		title.setText(gameRest);
                            		title.setForeground(Color.RED);
                            		
                            		// Show all the false flags if it was a loss
                            		for(int i = 0; i < (rowNumber * colNumber); i++) {
                            			if (game.getBox(i).flagged && game.getBox(i).value != -1) {
                            				game.getBox(i).button.setIcon(new ImageIcon(scaledFalseMine));
                            			}
                            		}
                            	}
                        	}
                        }
                    }
                }
            });
        	centerPanel.add(a);
        }
    }

    // create one Frame
    public static void main(String[] args) {
        new GUI();
    }    

}