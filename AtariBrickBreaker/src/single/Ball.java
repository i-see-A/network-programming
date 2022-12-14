package single;


import java.awt.*;
import java.util.*;

public class Ball extends Rectangle {

	Random random;

	int dx;
	int dy;
	int ballSpeed = 2;
	int vectorX;
	int vectorY;

	Ball(int x, int y, int width, int height) {
		super(x, y, width, height);
		Random rand = new Random();

		int vectorX = rand.nextInt(2);
		if (vectorX == 0)
			vectorX = -1;
		setDX(vectorX);

		int vectorY = rand.nextInt(2);
		if (vectorY == 0)
			vectorY = -1;
		setDY(vectorY);

	} // end costruttore ----------------------------------

	public void setDX(double vectorX) {
		dx = (int) (vectorX * ballSpeed);
	}

	public void setDY(double vectorY) {
		dy = (int) (vectorY * ballSpeed);
	}

	public void move() {
		x = x + dx;
		y = y + dy;
	}

	public void draw(Graphics g, Color color) {
		g.setColor(color);
		g.fillOval(x, y, height, width);
	}

}