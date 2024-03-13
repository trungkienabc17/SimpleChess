package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece {

	public Pawn(int color, int col, int row) {
		super(color, col, row);
		type = Type.pawn;

		if (color == GamePanel.white) {
			image = getImage("/piece/w-pawn.png");
		} else {
			image = getImage("/piece/b-pawn.png");
		}
	}

	public boolean canMove(int targetCol, int targetRow) {

		if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {

			// Define its move value based on its color, white can only move up, black can
			// only move down
			int moveValue;
			if (color == GamePanel.white) {
				moveValue = -1;
			} else {
				moveValue = 1;
			}

			// Check the hitting piece
			hittingP = getHittingP(targetCol, targetRow);

			// 1 square movement
			if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
				return true;
			}
			// 2 squares movement at its first move
			if (targetCol == preCol && targetRow == preRow + 2 * moveValue && hittingP == null && moved == false
					&& pieceIsOnStraightLine(targetCol, targetRow) == false) {
				return true;
			}
			// Diagonal movement and Capture if there is an enemy piece 1 square diagonally
			// in front of it
			if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null
					&& hittingP.color != color) {
				return true;
			}

			// En passant
			if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue) {
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == targetCol && piece.row == preRow && piece.twoStepped == true) {
						hittingP = piece;
						return true;
					}
				}
			}
		}
		return false;
	}
}
