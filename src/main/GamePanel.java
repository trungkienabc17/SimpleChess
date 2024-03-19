package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable {

	public static final int width = 1200;
	public static final int height = 800;
	final int FPS = 60;
	Thread gameThread;
	Board board = new Board();
	Mouse mouse = new Mouse();

	// Piece
	public static ArrayList<Piece> pieces = new ArrayList<>();
	public static ArrayList<Piece> simPieces = new ArrayList<>();
	public static ArrayList<Piece> promoPieces = new ArrayList<>();
	Piece activeP, checkingP;
	public static Piece castlingP;

	// Pieces color
	public static final int white = 0;
	public static final int black = 1;
	int currentColor = white;

	// Booleans
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	boolean gameover;

	public GamePanel() {

		setPreferredSize(new Dimension(width, height));
		setBackground(new Color(64, 64, 64));
		addMouseMotionListener(mouse);
		addMouseListener(mouse);

		setPieces();
		// testPromotion();
		// testIllegal();
		copyPieces(pieces, simPieces);
	}

	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	public void setPieces() {

		// White team
		pieces.add(new Pawn(white, 0, 6));
		pieces.add(new Pawn(white, 1, 6));
		pieces.add(new Pawn(white, 2, 6));
		pieces.add(new Pawn(white, 3, 6));
		pieces.add(new Pawn(white, 4, 6));
		pieces.add(new Pawn(white, 5, 6));
		pieces.add(new Pawn(white, 6, 6));
		pieces.add(new Pawn(white, 7, 6));
		pieces.add(new Rook(white, 0, 7));
		pieces.add(new Rook(white, 7, 7));
		pieces.add(new Knight(white, 1, 7));
		pieces.add(new Knight(white, 6, 7));
		pieces.add(new Bishop(white, 2, 7));
		pieces.add(new Bishop(white, 5, 7));
		pieces.add(new Queen(white, 3, 7));
		pieces.add(new King(white, 4, 7));

		// Black team
		pieces.add(new Pawn(black, 0, 1));
		pieces.add(new Pawn(black, 1, 1));
		pieces.add(new Pawn(black, 2, 1));
		pieces.add(new Pawn(black, 3, 1));
		pieces.add(new Pawn(black, 4, 1));
		pieces.add(new Pawn(black, 5, 1));
		pieces.add(new Pawn(black, 6, 1));
		pieces.add(new Pawn(black, 7, 1));
		pieces.add(new Rook(black, 0, 0));
		pieces.add(new Rook(black, 7, 0));
		pieces.add(new Knight(black, 1, 0));
		pieces.add(new Knight(black, 6, 0));
		pieces.add(new Bishop(black, 2, 0));
		pieces.add(new Bishop(black, 5, 0));
		pieces.add(new Queen(black, 3, 0));
		pieces.add(new King(black, 4, 0));
	}

	// TESTING
	public void testPromotion() {
		pieces.add(new Pawn(white, 0, 4));
		pieces.add(new Pawn(black, 4, 4));
	}

	public void testIllegal() {
		pieces.add(new Pawn(white, 2, 5));
		pieces.add(new King(white, 3, 7));
		pieces.add(new King(black, 0, 3));
		pieces.add(new Queen(white, 1, 4));
		pieces.add(new Queen(black, 4, 5));
	}

	// END TESTING

	public void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {

		target.clear();
		for (int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}

	// Game Loop
	@Override
	public void run() {
		double drawInterval = 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;

		while (gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;

			if (delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}
	}

	private void update() {

		if (promotion == true) {
			promoting();
		} else {

			// Mouse pressed
			if (mouse.pressed) {
				if (activeP == null) {
					// If not holding a piece, can pick up one
					for (Piece piece : simPieces) {
						// If mouse is on ally piece, can pick it up as activeP
						if (piece.color == currentColor && piece.col == mouse.x / Board.square_size
								&& piece.row == mouse.y / Board.square_size) {

							activeP = piece;
						}
					}
				} else {
					// If holding a piece, enter simulation phase
					simulate();
				}
			}

			// Mouse released
			if (mouse.pressed == false) {
				if (activeP != null) {

					if (validSquare) {

						// Move confirmed
						// Update the piece list in case a piece has been captured and removed in
						// simulation phase
						copyPieces(simPieces, pieces);
						activeP.updatePosition();
						if (castlingP != null) {
							castlingP.updatePosition();
						}

						if (canPromote()) {
							promotion = true;
						} else {

							// Change turn if the move is confirmed
							changePlayer();
						}

					} else {

						// Move canceled, reset everything
						copyPieces(pieces, simPieces);
						activeP.resetPosition();
						activeP = null;
					}
				}
			}
		}
	}

	private void simulate() {

		canMove = false;
		validSquare = false;

		// Reset the piece list in every loop
		// To restore the removed pieces during simulation phase
		copyPieces(pieces, simPieces);

		// Reset the castling piece's position
		if (castlingP != null) {
			castlingP.col = castlingP.preCol;
			castlingP.x = castlingP.getX(castlingP.col);
			castlingP = null;
		}

		// If a piece is held, update its position to the mouse
		activeP.x = mouse.x - Board.half_square_size;
		activeP.y = mouse.y - Board.half_square_size;
		activeP.col = activeP.getCol(activeP.x);
		activeP.row = activeP.getRow(activeP.y);

		// Check if the piece is hovering on a reachable square
		if (activeP.canMove(activeP.col, activeP.row)) {

			canMove = true;
			// If captured a piece, remove it from the list
			if (activeP.hittingP != null) {
				simPieces.remove(activeP.hittingP.getIndex());
			}

			checkCastling();

			if (isIllegal(activeP) == false) {
				validSquare = true;
			}
		}
	}

	private void checkCastling() {
		if (castlingP != null) {
			if (castlingP.col == 0) {
				castlingP.col += 3;
			} else if (castlingP.col == 7) {
				castlingP.col -= 2;
			}
			castlingP.x = castlingP.getX(castlingP.col);
		}
	}

	private boolean isIllegal(Piece king) {

		if (king.type == Type.king) {
			for (Piece piece : simPieces) {
				if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
		return false;
	}

	private void changePlayer() {
		if (currentColor == white) {
			currentColor = black;

			// Reset black two step status
			for (Piece piece : pieces) {
				if (piece.color == black) {
					piece.twoStepped = false;
				}
			}
		} else {
			currentColor = white;

			// Reset white two step status
			for (Piece piece : pieces) {
				if (piece.color == white) {
					piece.twoStepped = false;
				}
			}
		}
		activeP = null;

	}

	private boolean canPromote() {

		if (activeP.type == Type.pawn) {
			if (currentColor == white && activeP.row == 0 || currentColor == black && activeP.row == 7) {
				promoPieces.clear();
				promoPieces.add(new Rook(currentColor, 9, 2));
				promoPieces.add(new Knight(currentColor, 9, 3));
				promoPieces.add(new Bishop(currentColor, 9, 4));
				promoPieces.add(new Queen(currentColor, 9, 5));
				return true;
			}
		}
		return false;
	}

	private void promoting() {
		if (mouse.pressed) {
			for (Piece piece : promoPieces) {
				if (piece.col == mouse.x / Board.square_size && piece.row == mouse.y / Board.square_size) {
					switch (piece.type) {
						case rook:
							simPieces.add(new Rook(currentColor, activeP.col, activeP.row));
							break;
						case knight:
							simPieces.add(new Knight(currentColor, activeP.col, activeP.row));
							break;
						case queen:
							simPieces.add(new Queen(currentColor, activeP.col, activeP.row));
							break;
						case bishop:
							simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));
							break;
						default:
							break;
					}
					simPieces.remove(activeP.getIndex());
					copyPieces(simPieces, pieces);
					activeP = null;
					promotion = false;
					changePlayer();
				}
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Board
		board.draw(g2);

		// Pieces
		for (Piece p : simPieces) {
			p.draw(g2);
		}
		if (activeP != null) {

			if (canMove) {

				if (isIllegal(activeP)) {
					g2.setColor(Color.red);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col * Board.square_size, activeP.row * Board.square_size, Board.square_size,
							Board.square_size);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				} else {
					g2.setColor(Color.white);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col * Board.square_size, activeP.row * Board.square_size, Board.square_size,
							Board.square_size);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			}

			activeP.draw(g2);
		}

		// Status messages
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.WHITE);

		if (promotion) {
			g2.drawString("Promote to: ", 840, 150);
			for (Piece piece : promoPieces) {
				g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.square_size,
						Board.square_size, null);
			}
		} else {
			if (currentColor == white) {
				g2.drawString("White's Turn", 860, 650);
			} else {
				g2.drawString("Black's Turn", 860, 150);
			}
		}
	}
}
