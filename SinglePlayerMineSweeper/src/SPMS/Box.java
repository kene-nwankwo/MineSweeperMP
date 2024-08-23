package SPMS;

import javax.swing.JButton;

public class Box {
		boolean revealed;
		int value;
		boolean flagged;
		JButton button;
		
		public Box(boolean revealed, int value, boolean flagged) {
			this.revealed = revealed;
			this.value = value;
			this.flagged = flagged;
			this.button = null;
		}
}
