package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece {

	public Rook(int color, int col, int row) {
		super(color, col, row);
		type = Type.rook;

		if (color == GamePanel.white) {
			image = getImage("/piece/w-rook.png");
		} else {
			image = getImage("/piece/b-rook.png");
		}
	}

	public boolean canMove(int targetCol, int targetRow) {
		if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			// Rook can move on the same row or same col
			if (targetCol == preCol || targetRow == preRow) {
				if (isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		return false;
	}

}
