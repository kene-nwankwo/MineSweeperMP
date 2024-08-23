package MPMS;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

public class Game {
	private int rows;
	private int cols;
	private int mines;
	private int cleared;
	private int gameState = 0;
	public boolean firstMove = true;
	public ArrayList<ArrayList<Box>> grid = null;
	
	
	public Game(int row, int col, int mines) {
		this.rows = row;
		this.cols = col;
		this.mines = mines;
		this.cleared = 0;
		this.grid = createGrid();
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

	// Perform a Move
	public ArrayList<ArrayList<Box>> move(int index){
		// Calculate row and column from index		
		int row = index / this.cols;
		int col = index % this.cols;
		
		// Set up board if if is the first move
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
		if(!clearSpaces(row, col)) {
			// Loss 
			gameState = 1;
		} else if (this.cleared == ((this.rows*this.cols) - this.mines)){
			// Win
			gameState = 2;
		}
		return grid;
	}
	
	// Returns the gamesState
	public int gameState() {
		return this.gameState;
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
	
	
	// Function that handles clearing a area after a move is made DFS
	private boolean clearSpaces(int row_move, int col_move) {
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
								clearSpaces(row_move+t, col_move+w);
	    				}
					}
				}
			}
		}
		this.cleared++;	
		
		return true;
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
}

