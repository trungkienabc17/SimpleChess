package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece {

	public Bishop(int color, int col, int row) {
		super(color, col, row);
		type = Type.bishop;

		if (color == GamePanel.white) {
			image = getImage("/piece/w-bishop.png");
		} else {
			image = getImage("/piece/b-bishop.png");
		}
	}

	public boolean canMove(int targetCol, int targetRow) {

		if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			// Bishop movement is a 1:1 col and row ratio
			if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
				if (isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		return false;
	}
}
