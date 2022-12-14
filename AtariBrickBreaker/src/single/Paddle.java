package single;


import java.awt.*;

public class Paddle extends Rectangle {

	int id;
	int dy;
	int dx;
	int paddleSpeed = 4;

	Paddle(int x, int y, int PADDLE_WIDTH, int PADDLE_HEIGHT) {

		super(x, y, PADDLE_WIDTH, PADDLE_HEIGHT); // costruttore di Rectangle

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

	public void draw(Graphics g, Color color) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}

}