package piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece {

	public Knight(int color, int col, int row) {
		super(color, col, row);
		type = Type.knight;

		if (color == GamePanel.white) {
			image = getImage("/piece/w-knight.png");
		} else {
			image = getImage("/piece/b-knight.png");
		}
	}

	public boolean canMove(int targetCol, int targetRow) {

		if (isWithinBoard(targetCol, targetRow)) {
			// Knight movement is 1:2 or 2:1 ratio of col and row
			if (Math.abs(targetCol - preCol) * (Math.abs(targetRow - preRow)) == 2) {
				if (isValidSquare(targetCol, targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

}
