package MPMS;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

public class Game {
	private int rows;
	private int cols;
	private int mines;
	private int cleared;
	private int gameState = 0;
	private int playerNumber;
	public boolean firstMove = true;
	public ArrayList<ArrayList<Box>> grid = null;
	private GUI gui;
	private boolean singlePlayer;
	private boolean isHost;
	private int[] scores;
	
	
	
	public Game(int row, int col, int mines, GUI g, int playerNumber, boolean singlePlayer, boolean isHost, int numberOfPlayers) {
		this.rows = row;
		this.cols = col;
		this.mines = mines;
		this.cleared = 0;
		this.gui = g;
		this.grid = createGrid();
		this.playerNumber = playerNumber;
		this.singlePlayer = singlePlayer;
		this.isHost = isHost;
		this.scores = new int [numberOfPlayers];
		return;
	}

	// Create Empty Grid
	private ArrayList<ArrayList<Box>> createGrid() {
		ArrayList<ArrayList<Box>> grid = new ArrayList<ArrayList<Box>>();
		for(int i = 0; i < this.rows; i++) {
            ArrayList<Box> new_colum = new ArrayList<Box>();
        	for(int k = 0; k < this.cols; k++) {
        		new_colum.add(new Box(false, 0, false));
        	}
        	grid.add(new_colum);
        }	
		return grid;
	}
	
	public ArrayList<ArrayList<Box>> move(int index, int playerNum){
		if (singlePlayer) {
			singleMove(index, playerNum);
		} else if (isHost) {
			hostMove(index, playerNum);
	        //for (int i = 0; i < scores.length; i++) {
	        //    System.out.println(scores[i]);
	        //}
		} else {
			clientMove(index, playerNum);
		}
		
		return grid;
	}
	

	private void singleMove(int index, int playerNum) {
		// Calculate row and column from index		
		int row = index / this.cols;
		int col = index % this.cols;
		
		if(this.firstMove) {
			// Toggle boolean
			this.firstMove = false;
			
			// Get set of places to avoid placing mines
	      	Set<Integer> avoid_set = avoidSet(row, col);
	      	//System.out.println(avoid_set.size());
	        
	        // Get set of randomized mines positions
	      	Set<Integer> mines_set = randomSelect(avoid_set);

	      	// Create board and add mines, updates grid
			addMines(mines_set);
			
			// Calculate proximity value for each position in the grid update the grid values
			grid = calcProximity(grid);
		}
		
		// Test to see if the game is over
		if(!singleClearSpaces(row, col, playerNum)) {
			// Loss 
			gameState = 1;
		} else if (this.cleared == ((this.rows*this.cols) - this.mines)){
			// Win
			gameState = 2;
		}
	}
	

	private boolean singleClearSpaces(int row_move, int col_move, int playerNum) {
		// Convert row, column move to a index
		int space_value = grid.get(row_move).get(col_move).value;
		
		// Return true if already found or if flagged stopping search
		if (grid.get(row_move).get(col_move).revealed == true || grid.get(row_move).get(col_move).flagged) {
			return true;
		}
		
		// Return false if you hit a mine
		if (space_value == -1) {
			return false;
		}
		
		// Updates box to revealed and updates Bevel
		grid.get(row_move).get(col_move).revealed = true;
		grid.get(row_move).get(col_move).button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		if (!gui.singlePlayer) {
			setBackground(row_move, col_move, playerNum);
		}

		if (grid.get(row_move).get(col_move).value != 0){
			// Display number
			grid.get(row_move).get(col_move).button.setText(String.valueOf(grid.get(row_move).get(col_move).value));
			// Update color of displayed number
			int val = grid.get(row_move).get(col_move).value;
			if (val == 1) {
				Color customColor = Color.decode("#0400fb"); // Hex color code
				grid.get(row_move).get(col_move).button.setForeground(customColor);	
			} else if (val == 2) {
				Color customColor = Color.decode("#028000"); // Hex color code
				grid.get(row_move).get(col_move).button.setForeground(customColor);	
			} else if (val == 3) {
				Color customColor = Color.decode("#fd0003"); // Hex color code
				grid.get(row_move).get(col_move).button.setForeground(customColor);	
			} else if (val == 4) {
				Color customColor = Color.decode("#010180"); // Hex color code
				grid.get(row_move).get(col_move).button.setForeground(customColor);	
			} else if (val == 5) {
				Color customColor = Color.decode("#7e0002"); // Hex color code
				grid.get(row_move).get(col_move).button.setForeground(customColor);	
			} else if (val == 6) {
				Color customColor = Color.decode("#00807e"); // Hex color code
				grid.get(row_move).get(col_move).button.setForeground(customColor);	
			} else if (val == 7) {
				Color customColor = Color.decode("#000000"); // Hex color code
				grid.get(row_move).get(col_move).button.setForeground(customColor);	
			} else {
				Color customColor = Color.decode("#808080"); // Hex color code
				grid.get(row_move).get(col_move).button.setForeground(customColor);	
			}		
		}
				
		// If space_value == 0 recurse on adjacent cells
		if (space_value == 0) {
			for(int t = -1; t <= 1; t++) {
				for(int w = -1; w <= 1; w++) {
					if ((row_move+t) >= 0 && (row_move+t) < grid.size()) {					
						if ((col_move+w) >= 0 && (col_move+w) < grid.get(0).size()) {
							singleClearSpaces(row_move+t, col_move+w, playerNum);
	    				}
					}
				}
			}
		}
		this.cleared++;	
		
		return true;
	}
	

	private void hostMove(int index, int playerNum) {
		// Calculate row and column from index		
		int row = index / this.cols;
		int col = index % this.cols;
		
		if(this.firstMove) {
			// Toggle boolean
			this.firstMove = false;
			playerNum = -1;
			
			// Get set of places to avoid placing mines
	      	Set<Integer> avoid_set = avoidSet(row, col);
	      	//System.out.println(avoid_set.size());
	        
	        // Get set of randomized mines positions
	      	Set<Integer> mines_set = randomSelect(avoid_set);

	      	// Create board and add mines, updates grid
			addMines(mines_set);
			
			// Calculate proximity value for each position in the grid update the grid values
			grid = calcProximity(grid);
		
            String message = "3," + Integer.toString(index);
            
            for (int i = 0; i < (this.rows*this.cols); i++) {
            	int r = i / this.cols;
        		int c = i % this.cols;
        		message = message + "," + grid.get(r).get(c).value;
            }
            
            gui.gameServer.broadcastMessage(message);
		} else {
			String message = "4," + Integer.toString(index) + "," + playerNum;
            gui.gameServer.broadcastMessage(message);
		}
		// Test to see if the game is over
		if(!multiClearSpaces(row, col, playerNum)) {
			// Loss 
			if (playerNum == playerNumber) {
				gameState = 1;
			}
		} else if (this.cleared == ((this.rows*this.cols) - this.mines)){
			// Win
			if (playerNum == playerNumber) {
				gameState = 2;
			}
		}
		return;
	}
	
	private void clientMove(int index, int playerNum) {
		// Calculate row and column from index		
		int row = index / this.cols;
		int col = index % this.cols;
		
		// Is client
		// Set up board if if is the first move
		if(this.firstMove) {
			// Toggle boolean
			this.firstMove = false;
			playerNum = -1;
            
            for (int i = 0; i < (this.rows*this.cols); i++) {
            	int r = i / this.cols;
        		int c = i % this.cols;
        		grid.get(r).get(c).value = Integer.parseInt(gui.split[i+2]);
            }
		}
		
		if (playerNum != playerNumber && grid.get(row).get(col).value == -1) {
			grid.get(row).get(col).revealed = true;
			grid.get(row).get(col).button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			
			// Reveal icon
			grid.get(row).get(col).button.setIcon(new ImageIcon(gui.scaledMine));
		}
		
		row = Integer.parseInt(gui.split[1]) / this.cols;
		col = Integer.parseInt(gui.split[1]) % this.cols;
		
		// Test to see if the game is over
		if(!multiClearSpaces(row, col, playerNum)) {
			// Loss 
			gameState = 1;
		} else if (this.cleared == ((this.rows*this.cols) - this.mines)){
			// Win
			gameState = 2;
		}
		return;
	}

	private boolean multiClearSpaces(int row_move, int col_move, int playerNum) {
		if (playerNum == playerNumber) {
			if (isHost) {
				System.out.println(271);
			}
			return ownNumberClearSpaces(row_move, col_move, playerNum);
		} else {
			return otherNumberClearSpaces(row_move, col_move, playerNum);
		}
	}
	
	private boolean ownNumberClearSpaces(int row_move, int col_move, int playerNum) {
		// Convert row, column move to a index
		int space_value = grid.get(row_move).get(col_move).value;
		
		// Return true if already found or if flagged stopping search
		if (grid.get(row_move).get(col_move).revealed == true || grid.get(row_move).get(col_move).flagged) {
			return true;
		}
		
		// Return false if you hit a mine
		if (space_value == -1) {
			return false;
		}
		
		// Updates box to revealed and updates Bevel
		grid.get(row_move).get(col_move).revealed = true;
		grid.get(row_move).get(col_move).button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		if (grid.get(row_move).get(col_move).value != -1){
			setBackground(row_move, col_move, playerNum);
		}
				
		// If space_value == 0 recurse on adjacent cells
		if (space_value == 0) {
			for(int t = -1; t <= 1; t++) {
				for(int w = -1; w <= 1; w++) {
					if ((row_move+t) >= 0 && (row_move+t) < grid.size()) {					
						if ((col_move+w) >= 0 && (col_move+w) < grid.get(0).size()) {
							ownNumberClearSpaces(row_move+t, col_move+w, playerNum);
	    				}
					}
				}
			}
		}
		if (playerNum != -1) {
			this.scores[playerNum]++;
			if (playerNum == 0) {
				gui.sp0L.setText("Player 1: " + this.scores[0]);
			}
			if (playerNum == 1) {
				gui.sp1L.setText("Player 2: " + this.scores[1]);
			}
			if (playerNum == 2) {
				gui.sp2L.setText("Player 3: " + this.scores[2]);
			}
			if (playerNum == 3) {
				gui.sp3L.setText("Player 4: " + this.scores[3]);
			}
			if (playerNum == 4) {
				gui.sp4L.setText("Player 5: " + this.scores[4]);
			}
			if (playerNum == 5) {
				gui.sp5L.setText("Player 6: " + this.scores[5]);
			}
			if (playerNum == 6) {
				gui.sp6L.setText("Player 7: " + this.scores[6]);
			}
			if (playerNum == 7) {
				gui.sp7L.setText("Player 8: " + this.scores[7]);
			}
		}
		this.cleared++;	
		
		return true;
	}

	private boolean otherNumberClearSpaces(int row_move, int col_move, int playerNum) {
		// Convert row, column move to a index
		int space_value = grid.get(row_move).get(col_move).value;
		
		// Return true if already found or if flagged stopping search
		if (grid.get(row_move).get(col_move).revealed == true) {
			return true;
		}
		
		// Return false if you hit a mine
		if (space_value == -1) {
			return false;
		}
		
		if (grid.get(row_move).get(col_move).flagged) {
			gui.mineNumber++;
			gui.mineCount.setText("Mines: "+ gui.mineNumber);
		}
		
		// Updates box to revealed and updates Bevel
		grid.get(row_move).get(col_move).revealed = true;
		grid.get(row_move).get(col_move).button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		if (grid.get(row_move).get(col_move).value != -1){
			setBackground(row_move, col_move, playerNum);
		}
				
		// If space_value == 0 recurse on adjacent cells
		if (space_value == 0) {
			for(int t = -1; t <= 1; t++) {
				for(int w = -1; w <= 1; w++) {
					if ((row_move+t) >= 0 && (row_move+t) < grid.size()) {					
						if ((col_move+w) >= 0 && (col_move+w) < grid.get(0).size()) {
							otherNumberClearSpaces(row_move+t, col_move+w, playerNum);
	    				}
					}
				}
			}
		}
		if (playerNum != -1) {
			this.scores[playerNum]++;
			if (playerNum == 0) {
				gui.sp0L.setText("Player 1: " + this.scores[0]);
			}
			if (playerNum == 1) {
				gui.sp1L.setText("Player 2: " + this.scores[1]);
			}
			if (playerNum == 2) {
				gui.sp2L.setText("Player 3: " + this.scores[2]);
			}
			if (playerNum == 3) {
				gui.sp3L.setText("Player 4: " + this.scores[3]);
			}
			if (playerNum == 4) {
				gui.sp4L.setText("Player 5: " + this.scores[4]);
			}
			if (playerNum == 5) {
				gui.sp5L.setText("Player 6: " + this.scores[5]);
			}
			if (playerNum == 6) {
				gui.sp6L.setText("Player 7: " + this.scores[6]);
			}
			if (playerNum == 7) {
				gui.sp7L.setText("Player 8: " + this.scores[7]);
			}
		}
		this.cleared++;	
		
		return true;
	}
	
	
	// Returns the gamesState
	public int gameState() {
		return this.gameState;
	}	

	private void setBackground(int row_move, int col_move, int playerNum) {
		
		ImageIcon colorImage = null;
		if (playerNum == -1) {
			colorImage = new ImageIcon(gui.transparent);
		} else if (playerNum == 0) {
			colorImage = new ImageIcon(gui.red);
		} else if (playerNum == 1) {
			colorImage = new ImageIcon(gui.green);
		} else if (playerNum == 2) {
			colorImage = new ImageIcon(gui.blue);
		} else if (playerNum == 3) {
			colorImage = new ImageIcon(gui.yellow);
		} else if (playerNum == 4) {
			colorImage = new ImageIcon(gui.cyan);
		} else if (playerNum == 5) {
			colorImage = new ImageIcon(gui.magenta);
		} else if (playerNum == 6) {
			colorImage = new ImageIcon(gui.orange);
		} else {
			colorImage = new ImageIcon(gui.purple);
		}
		
		int val = grid.get(row_move).get(col_move).value;
		ImageIcon numberImage = null;
		if (val == 0) {
			numberImage = new ImageIcon(gui.transparent);
		} else if (val == 1) {
			numberImage = new ImageIcon(gui.one);
		} else if (val == 2) {
			numberImage = new ImageIcon(gui.two);
		} else if (val == 3) {
			numberImage = new ImageIcon(gui.three);
		} else if (val == 4) {
			numberImage = new ImageIcon(gui.four);
		} else if (val == 5) {
			numberImage = new ImageIcon(gui.five);
		} else if (val == 6) {
			numberImage = new ImageIcon(gui.six);
		} else if (val == 7) {
			numberImage = new ImageIcon(gui.seven);
		} else {
			numberImage = new ImageIcon(gui.eight);
		}
		
		grid.get(row_move).get(col_move).button.setIcon(combineIcons(colorImage, numberImage));
		
	}
	
	// Take initial move and produces set of spaces to avoid placing mines around the move
	private Set<Integer> avoidSet(int row_move, int col_move) {
		Set<Integer> avoid_set = new HashSet<Integer>();
		for(int t = -1; t <= 1; t++) {
			for(int w = -1; w <= 1; w++) {
				if ((row_move+t) >= 0 && (row_move+t) < this.rows) {					
					if ((col_move+w) >= 0 && (col_move+w) < this.cols) {  					
						avoid_set.add(((row_move+t)*this.cols) + (col_move+w));
    				}
				}
			}
		}
		return avoid_set;
	}
	

	// Puts Second Icon on top of the first
    private static Icon combineIcons(Icon icon1, Icon icon2) {
        int width = icon1.getIconWidth();
        int height = icon1.getIconHeight();

        // Create a BufferedImage with the size of the first icon

        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combinedImage.createGraphics();

        // Draw the first icon (base)

        icon1.paintIcon(null, g2d, 0, 0);

        // Scale down the second icon

        int smallerWidth = icon2.getIconWidth() / 2;  // Adjust this factor as needed

        int smallerHeight = icon2.getIconHeight() / 2;

        // Position the smaller icon on top of the first

        int xPosition = (width - smallerWidth) / 2;
        int yPosition = (height - smallerHeight) / 2;

        // Draw the smaller second icon

        g2d.drawImage(((ImageIcon) icon2).getImage(), xPosition, yPosition, smallerWidth, smallerHeight, null);

        g2d.dispose();
        return new ImageIcon(combinedImage);
    }


	// Calculates numerical value for boxes  
	private ArrayList<ArrayList<Box>> calcProximity(ArrayList<ArrayList<Box>> grid){
		// For each box in grid
		for(int i = 0; i < this.rows; i++) {
        	for(int k = 0; k < this.cols; k++) {
        		// Get box
        		Box curr = grid.get(i).get(k);
        		// If mine do nothing
        		if (curr.value == -1) {
        			// Nothing
        		} else {
        			// Count number of mines in each adjacent cell with boundary checks
        			int count = 0;
        			for(int t = -1; t <= 1; t++) {
        				for(int w = -1; w <= 1; w++) {
            				if ((i+t) >= 0 && (i+t) < this.rows) {
            					if ((k+w) >= 0 && (k+w) < this.cols) {
            						if(grid.get(i+t).get(k+w).value == -1) {
            							count++;
            						}
                				}
            				}
            			}
        			}
        			// Update box's value with the mine count
        			grid.get(i).get(k).value = count;
        		}
        	}
        }
    	return grid;
	}
	
	// Takes a set of positions for the mines and adds mines to the game board
	private void addMines(Set<Integer> mines){
		int mine_counter = 0;
		// For each index, if it is in the set update the box's value
		for(int i = 0; i < this.rows; i++) {        	
        	for(int k = 0; k < this.cols; k++) {
        		if (mines.contains(mine_counter)) {
        			this.grid.get(i).get(k).value = -1;
        		} 
        		mine_counter++;
        	}
        }	
		return;
	}
	
	// Selects random location for mines avoiding cells adjacent the to first move location
	private Set<Integer> randomSelect(Set<Integer> avoid_set) {
		Set<Integer> mine_set = new HashSet<Integer>();
		// Number of index's in the board
		int max = this.rows * this.cols;
		// Randomly generates mines avoiding avoid_set
		while (mine_set.size() < mines) {
			int num = (int) (Math.random()*max);
			
			if(!avoid_set.contains(num) && !mine_set.contains(num)) {
				mine_set.add(num);
			}
		}
		return mine_set;
	}

	// Given an index return it associated box
	public Box getBox(int index) {
		int row = index / this.cols;
		int col = index % this.cols;
		
		return grid.get(row).get(col);
	}
	
	// Given an index and flag update box's flag
	public void setBoxFlag(int index, boolean flagged) {
		int row = index / this.cols;
		int col = index % this.cols;
		
		grid.get(row).get(col).flagged = flagged;
	}
	

	/*

	// Perform a Move
	public ArrayList<ArrayList<Box>> move(int index, int playerNum){
		// Calculate row and column from index		
		int row = index / this.cols;
		int col = index % this.cols;
		
		int playerN = playerNum;
		
		if(gui.singlePlayer || gui.isHost) {
			// Set up board if if is the first move
			if(gui.singlePlayer) {
				playerN = -1;
			}
			if(this.firstMove) {
				playerN = -1;
				// Toggle boolean
				this.firstMove = false;
				
				// Get set of places to avoid placing mines
		      	Set<Integer> avoid_set = avoidSet(row, col);
		      	//System.out.println(avoid_set.size());
		        
		        // Get set of randomized mines positions
		      	Set<Integer> mines_set = randomSelect(avoid_set);

		      	// Create board and add mines, updates grid
				addMines(mines_set);
				
				// Calculate proximity value for each position in the grid update the grid values
				grid = calcProximity(grid);
			
	            String message = "3," + Integer.toString(index);
	            
	            for (int i = 0; i < (this.rows*this.cols); i++) {
	            	int r = i / this.cols;
	        		int c = i % this.cols;
	        		message = message + "," + grid.get(r).get(c).value;
	            }
	            
	            if (!gui.singlePlayer && gui.isHost) {
	            	gui.gameServer.broadcastMessage(message);
	            }
	            	
			} else {
				String message = "4," + Integer.toString(index) + "," + playerNumber;
	            if (!gui.singlePlayer && gui.isHost) {
	            	gui.gameServer.broadcastMessage(message);
	            }
			}
			
			// Test to see if the game is over
			if(!clearSpaces(row, col, playerN)) {
				// Loss 
				gameState = 1;
			} else if (this.cleared == ((this.rows*this.cols) - this.mines)){
				// Win
				gameState = 2;
			}
			return grid;
		} else {
			// Is client
			// Set up board if if is the first move
			if(this.firstMove) {
				// Toggle boolean
				this.firstMove = false;
				playerN = -1;
			
	            //String message = "3," + Integer.toString(index);
	            
	            for (int i = 0; i < (this.rows*this.cols); i++) {
	            	int r = i / this.cols;
	        		int c = i % this.cols;
	        		grid.get(r).get(c).value = Integer.parseInt(gui.split[i+2]);
	            }
	            
	            //gui.gameServer.broadcastMessage(message);
			} else {
				//String message = "4," + Integer.toString(index) + "," + playerNumber;
				//gui.gameServer.broadcastMessage(message);
			}
			
			if (playerNum != playerNumber && grid.get(row).get(col).value == -1) {
				grid.get(row).get(col).revealed = true;
				grid.get(row).get(col).button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				
				// Reveal icon
				grid.get(row).get(col).button.setIcon(new ImageIcon(gui.scaledMine));
			}
			
			row = Integer.parseInt(gui.split[1]) / this.cols;
			col = Integer.parseInt(gui.split[1]) % this.cols;
			
			
			// Test to see if the game is over
			if(!clearSpaces(row, col, playerN)) {
				// Loss 
				gameState = 1;
			} else if (this.cleared == ((this.rows*this.cols) - this.mines)){
				// Win
				gameState = 2;
			}
			return grid;
		}
	}
	
	// Function that handles clearing a area after a move is made DFS
	private boolean clearSpaces(int row_move, int col_move, int playerNum) {
		
		// Acting on own number
		if (playerNum == playerNumber) {
			
			// Convert row, column move to a index
			int space_value = grid.get(row_move).get(col_move).value;
			
			// Return true if already found or if flagged stopping search
			if (grid.get(row_move).get(col_move).revealed == true || grid.get(row_move).get(col_move).flagged) {
				return true;
			}
			
			// Return false if you hit a mine
			if (space_value == -1) {
				return false;
			}
			
			// Updates box to revealed and updates Bevel
			grid.get(row_move).get(col_move).revealed = true;
			grid.get(row_move).get(col_move).button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			if (!gui.singlePlayer) {
				setBackground(row_move, col_move, playerNum);
			}

			if (grid.get(row_move).get(col_move).value != 0){
				// Display number
				grid.get(row_move).get(col_move).button.setText(String.valueOf(grid.get(row_move).get(col_move).value));
				// Update color of displayed number
				int val = grid.get(row_move).get(col_move).value;
				if (val == 1) {
					Color customColor = Color.decode("#0400fb"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 2) {
					Color customColor = Color.decode("#028000"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 3) {
					Color customColor = Color.decode("#fd0003"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 4) {
					Color customColor = Color.decode("#010180"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 5) {
					Color customColor = Color.decode("#7e0002"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 6) {
					Color customColor = Color.decode("#00807e"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 7) {
					Color customColor = Color.decode("#000000"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else {
					Color customColor = Color.decode("#808080"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				}		
			}
					
			// If space_value == 0 recurse on adjacent cells
			if (space_value == 0) {
				for(int t = -1; t <= 1; t++) {
					for(int w = -1; w <= 1; w++) {
						if ((row_move+t) >= 0 && (row_move+t) < grid.size()) {					
							if ((col_move+w) >= 0 && (col_move+w) < grid.get(0).size()) {
									clearSpaces(row_move+t, col_move+w, playerNum);
		    				}
						}
					}
				}
			}
			this.cleared++;	
			
			return true;
			
		} else {
			
			// Convert row, column move to a index
			int space_value = grid.get(row_move).get(col_move).value;
			
			// Return true if already found or if flagged stopping search
			if (grid.get(row_move).get(col_move).revealed == true || grid.get(row_move).get(col_move).flagged) {
				return true;
			}
			
//			// Return false if you hit a mine
//			if (space_value == -1) {
//				grid.get(row_move).get(col_move).revealed = true;
//				grid.get(row_move).get(col_move).button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
//				
//				// Reveal icon
//				grid.get(row_move).get(col_move).button.setIcon(new ImageIcon(gui.scaledMine));
//				// TODO add other persons color - add color its imported images 
//				// look up if you can add two icons to the same button to have the mine and the col
//				// This section has an error should only revel the mine if it was the origninal/ actual click
//				return true;
//			}
			
			// Updates box to revealed and updates Bevel
			grid.get(row_move).get(col_move).revealed = true;
			grid.get(row_move).get(col_move).button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

			if (grid.get(row_move).get(col_move).value != 0){
				// Display number
				grid.get(row_move).get(col_move).button.setText(String.valueOf(grid.get(row_move).get(col_move).value));
				// Update color of displayed number
				int val = grid.get(row_move).get(col_move).value;
				if (val == 1) {
					Color customColor = Color.decode("#0400fb"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 2) {
					Color customColor = Color.decode("#028000"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 3) {
					Color customColor = Color.decode("#fd0003"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 4) {
					Color customColor = Color.decode("#010180"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 5) {
					Color customColor = Color.decode("#7e0002"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 6) {
					Color customColor = Color.decode("#00807e"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else if (val == 7) {
					Color customColor = Color.decode("#000000"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				} else {
					Color customColor = Color.decode("#808080"); // Hex color code
					grid.get(row_move).get(col_move).button.setForeground(customColor);	
				}		
			}
					
			// If space_value == 0 recurse on adjacent cells
			if (space_value == 0) {
				for(int t = -1; t <= 1; t++) {
					for(int w = -1; w <= 1; w++) {
						if ((row_move+t) >= 0 && (row_move+t) < grid.size()) {					
							if ((col_move+w) >= 0 && (col_move+w) < grid.get(0).size()) {
									clearSpaces(row_move+t, col_move+w, playerNum);
		    				}
						}
					}
				}
			}
			this.cleared++;	
			
			return true;
		}
	}
	
	*/
	
}


