package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {

	public Type type;
	public int x, y;
	public int col, row, preCol, preRow;
	public int color;
	public BufferedImage image;
	public Piece hittingP;
	public boolean moved, twoStepped;

	public Piece(int color, int col, int row) {
		this.color = color;
		this.col = col;
		this.row = row;
		x = getX(col);
		y = getY(row);
		preCol = col;
		preRow = row;
	}

	public BufferedImage getImage(String imagePath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}

	public int getX(int col) {
		return col * Board.square_size;
	}

	public int getY(int row) {
		return row * Board.square_size;
	}

	public int getCol(int x) {
		return (x + Board.half_square_size) / Board.square_size;
	}

	public int getRow(int y) {
		return (y + Board.half_square_size) / Board.square_size;
	}

	public int getIndex() {
		for (int index = 0; index < GamePanel.simPieces.size(); index++) {
			if (GamePanel.simPieces.get(index) == this) {
				return index;
			}
		}
		return 0;
	}

	public void updatePosition() {

		// CHECK En passant
		if (type == Type.pawn) {
			if (Math.abs(row - preRow) == 2) {
				twoStepped = true;
			}
		}

		x = getX(col);
		y = getY(row);
		preCol = getCol(x);
		preRow = getRow(y);
		moved = true;
	}

	public void resetPosition() {
		col = preCol;
		row = preRow;
		x = getX(col);
		y = getY(row);
	}

	public boolean canMove(int targetCol, int targetRow) {
		return false;
	}

	public boolean isWithinBoard(int targetCol, int targetRow) {

		if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
			return true;
		}
		return false;
	}

	// Check if the piece is moved by placing on the same square
	public boolean isSameSquare(int targetCol, int targetRow) {
		if (targetCol == preCol && targetRow == preRow) {
			return true;
		}
		return false;
	}

	// Check if there is a piece on a straight line blocking the active Piece from
	// moving to new location
	public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {

		// When moving left
		for (int c = preCol - 1; c > targetCol; c--) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == c && piece.row == targetRow) {
					hittingP = piece;
					return true;
				}
			}
		}
		// When moving right
		for (int c = preCol + 1; c < targetCol; c++) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == c && piece.row == targetRow) {
					hittingP = piece;
					return true;
				}
			}
		}
		// When moving up
		for (int r = preRow - 1; r > targetRow; r--) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == targetCol && piece.row == r) {
					hittingP = piece;
					return true;
				}
			}
		}
		// When moving down
		for (int r = preRow + 1; r < targetRow; r++) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == targetCol && piece.row == r) {
					hittingP = piece;
					return true;
				}
			}
		}

		return false;
	}

	// Check if there is a piece on a diagonal line blocking the active Piece from
	// moving to new location
	public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {

		if (targetRow < preRow) {
			// Up left
			for (int c = preCol - 1; c > targetCol; c--) {
				int diff = Math.abs(c - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return true;
					}
				}
			}
			// Up right
			for (int c = preCol + 1; c < targetCol; c++) {
				int diff = Math.abs(c - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return true;
					}
				}
			}
		}
		if (targetRow > preRow) {
			// Down left
			for (int c = preCol - 1; c > targetCol; c--) {
				int diff = Math.abs(c - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == c && piece.row == preRow + diff) {
						hittingP = piece;
						return true;
					}
				}
			}
			// Down right
			for (int c = preCol + 1; c < targetCol; c++) {
				int diff = Math.abs(c - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == c && piece.row == preRow + diff) {
						hittingP = piece;
						return true;
					}
				}
			}
		}

		return false;
	}

	public Piece getHittingP(int targetCol, int targetRow) {

		for (Piece piece : GamePanel.simPieces) {
			if (piece.col == targetCol && piece.row == targetRow && piece != this) {
				return piece;
			}
		}
		return null;
	}

	public boolean isValidSquare(int targetCol, int targetRow) {

		hittingP = getHittingP(targetCol, targetRow);

		if (hittingP == null) { // This square is vacant
			return true;
		} else { // This square is occupied
			if (hittingP.color != this.color) { // The color is different, captured the piece
				return true;
			} else {
				hittingP = null;
			}
		}

		return false;
	}

	public void draw(Graphics2D g2) {
		g2.drawImage(image, x, y, Board.square_size, Board.square_size, null);
	}
}
