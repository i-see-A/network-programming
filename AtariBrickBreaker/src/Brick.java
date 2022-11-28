//**********************************************************

import java.awt.*;

public class Brick extends Rectangle {

	int id;
	int dy;
	int dx;
	int paddleSpeed = 6;
	int row, column;

	Brick(int row, int column, int brickWidth, int brickHeight) {
		super(
				((row * brickWidth) + 1 * (row + 1)), // x pos
				(brickHeight * 3) + ((column * brickHeight) + 1 * (column + 1)), // y pos
				brickWidth, brickHeight);
		this.row = row;
		this.column = column;
	}

	public void setDeltaY(int yDirection) {
		dy = yDirection * paddleSpeed;
	}

	public void setDeltaX(int xDirection) {
		dx = xDirection * paddleSpeed;
	}

	public void move() {
		y = y + dy;
		x = x + dx;
	}

	public void draw(Graphics g) { // should be fixed

		if (this.column > -1 && this.column < 2) {
			g.setColor(Color.red);
		}

		if (this.column > 1 && this.column < 4) {
			g.setColor(Color.orange);
		}

		if (this.column > 3 && this.column < 5) {
			g.setColor(Color.green);
		}

		if (this.column > 5 && this.column < 8) {
			g.setColor(Color.yellow);
		}

		g.fillRect(x, y, width, height);
	}

}