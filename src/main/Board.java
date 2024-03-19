package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {

	final int max_col = 8;
	final int max_row = 8;
	public static final int square_size = 100;
	public static final int half_square_size = square_size / 2;

	public void draw(Graphics2D g2) {

		int board_color = 0;

		for (int row = 0; row < max_row; row++) {
			for (int col = 0; col < max_col; col++) {

				if (board_color == 0) {
					g2.setColor(new Color(210, 165, 125));
					board_color = 1;
				} else {
					g2.setColor(new Color(175, 115, 70));
					board_color = 0;
				}

				g2.fillRect(col * square_size, row * square_size, square_size, square_size);
			}
			if (board_color == 0) {
				board_color = 1;
			} else {
				board_color = 0;
			}
		}
	}
}
