import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.sound.sampled.*;

import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {
	static final int GAME_WIDTH = 700;
	static final int GAME_HEIGHT = (int) (GAME_WIDTH * (0.8));
	static final Dimension SCREEN_SIZE1 = new Dimension(GAME_WIDTH, GAME_HEIGHT);

	static final int PADDLE_WIDTH = 55;
	static final int PADDLE_HEIGHT = 10;

	static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
	static final int BALL_DIAMETER = 15;

	int lives = 3;
	int score = 0;
	int hits = 0;
	int choice = 0;
	int inclinationSelection = 0;

	String welcomeMessage = "PRESS SPACE";

	boolean attractModeActive = true;
	boolean soundPlaying;
	boolean allCleared;

	static final int rows = 16;
	static final int columns = 8;

	static final int brickWidth = 43;
	static final int brickHeight = 20;

	static final int BORDER_OFFSET = 20;

	Thread gameThread;
	BufferedImage buffer;
	Graphics graphics;

	Paddle paddle1;
	Ball ball;
	Brick[][] brick;
	Welcome welcome;
	Lives livesUI;
	Score scoreUI;
	Font atari;
	Color ballColor;
	Random random;
	Clip sound;

	GamePanel() { // costruttore
		random = new Random();

		brick = new Brick[rows][columns];
		livesUI = new Lives(GAME_WIDTH - 20, GAME_HEIGHT - 20, 20, 20);
		scoreUI = new Score(GAME_WIDTH - 20, GAME_HEIGHT - 20, 20, 20);
		ballColor = Color.white;

		try {
			InputStream fontLocation = getClass().getResourceAsStream("fonts/Atari.ttf");
			atari = Font.createFont(Font.TRUETYPE_FONT, fontLocation).deriveFont(20f);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setFocusable(true);
		this.setPreferredSize(SCREEN_SIZE1);
		gameThread = new Thread(this);
		gameThread.start();

		attractModePaddles();
		newBricks();
		newBall();
		newWelcome();

		this.setFocusable(true);
		this.setPreferredSize(SCREEN_SIZE1);

		this.addKeyListener(new Movement());

		gameThread = new Thread(this);
		gameThread.start();
	}

	public void newPaddles() {
		paddle1 = new Paddle((GAME_WIDTH - PADDLE_WIDTH) / 2, GAME_HEIGHT - (PADDLE_HEIGHT - BORDER_OFFSET / 2) - 50,
				PADDLE_WIDTH, PADDLE_HEIGHT);

	}

	public void newBricks() {
		for (int p = 0; p < rows; p++) {
			for (int l = 0; l < columns; l++) {
				brick[p][l] = new Brick(p, l, brickWidth, brickHeight);
			}
		}
	}

	public void newBall() {
		ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), (GAME_HEIGHT / 2) - (BALL_DIAMETER / 2), BALL_DIAMETER,
				BALL_DIAMETER);
		ball.setDY(1);

		hits = 0;
	}

	public void newWelcome() {
		welcome = new Welcome(GAME_WIDTH / 2, GAME_HEIGHT / 2, GAME_WIDTH / 15, GAME_HEIGHT / 15);
	}

	public void destroyWelcome() {
		welcomeMessage = " ";
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		graphics = buffer.getGraphics();

		draw(graphics);

		g.drawImage(buffer, 0, 0, this);

	}

	public void draw(Graphics g) {
		allCleared = true;

		if (attractModeActive == true) {

			switch (choice) {
				case 0:
					ballColor = Color.cyan;
					break;
				case 1:
					ballColor = Color.magenta;
					break;
				case 2:
					ballColor = Color.red;
					break;
				case 3:
					ballColor = Color.orange;
					break;
				case 4:
					ballColor = Color.yellow;
					break;
				case 5:
					ballColor = Color.green;
					break;
				default:
					ballColor = Color.white;
					break;
			}

		}

		paddle1.draw(g);
		ball.draw(g, ballColor);
		welcome.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, welcomeMessage);

		for (int p = 0; p < rows; p++) {
			for (int l = 0; l < columns; l++) {
				if (brick[p][l] != null) {
					brick[p][l].draw(g);
					allCleared = false;
				}
			}
		}

		if (allCleared) {
			beginAttractMode();
			welcomeMessage = "YOU WON!";
		}

		livesUI.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, lives);
		scoreUI.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, score);

		Toolkit.getDefaultToolkit().sync();
	}

	public void move() {
		paddle1.move();
		ball.move();
	}

	public void checkCollision() {

		if (paddle1.x <= 0)
			paddle1.x = 0;

		if (paddle1.x >= GAME_WIDTH - PADDLE_WIDTH)
			paddle1.x = GAME_WIDTH - PADDLE_WIDTH;

		if (ball.y <= 0) {
			ball.dy = -ball.dy;
		}

		if (ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
			ball.dy = -ball.dy;

			if (lives > 0) {
				lives = lives - 1;
			}

			checkIfLost(lives);
			newBall();
		}

		if (ball.x <= 0) {
			ball.dx = -ball.dx;

			if (attractModeActive == true) {
				choice = random.nextInt(6);
			}
		}

		if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
			ball.dx = -ball.dx;

			if (attractModeActive == true) {
				choice = random.nextInt(6);
			}
		}

		if (ball.intersects(paddle1)) {
			double inclination;

			if (attractModeActive != true) {
				// This keeps track of how many times the Ball touched the Paddle.
				// It's going to be useful to set the speed.
				hits = hits + 1;

				// This awful if-else chain handles the inclination the Ball needs to take when
				// having a collision with the Paddle. This ensures the Ball does not go in the
				// same
				// places and keeps the game fun.
				if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + PADDLE_WIDTH / 8) {
					inclination = -1.6;
				} else {
					if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 2) {
						inclination = -1.4;
					} else {
						if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 3) {
							inclination = -0.7;
						} else {
							if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 5) {
								inclination = 0.55;

								if (random.nextInt(2) == 0) {
									inclination = inclination * -1;
								}

							} else {
								if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 6) {
									inclination = 0.7;
								} else {
									if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 7) {
										inclination = 1.4;
									} else {
										inclination = 1.6;
									}
								}
							}
						}
					}
				}

			} else {

				// If the game is in Attract Mode, choose a Random Inclination.
				// Also, change the ball's color.
				choice = random.nextInt(6);

				inclinationSelection = random.nextInt(3);

				switch (inclinationSelection) {
					case 0:
						inclination = 1.6;
						break;
					case 1:
						inclination = 1.4;
						break;
					case 2:
						inclination = 0.7;
						break;
					default:
						inclination = 0.55;
						break;
				}

				inclinationSelection = random.nextInt(2);

				if (inclinationSelection == 0) {
					inclination = inclination * -1;
				}

			}

			// Calculating the Ball's speed.
			if (hits < 4) {
				ball.setDY(1);
			}

			if (hits >= 4 && hits < 12) {
				ball.setDY(1.5);
			}

			if (hits >= 12) {
				ball.setDY(2);
			}

			// Setting the values inside the class after calculating the inclination.
			ball.dy = -ball.dy;
			ball.setDX(inclination);

		}

		// This code takes care of Brick collisions.
		for (int r = 0; r < rows; r++) {
			for (int t = 0; t < columns; t++) {
				if (brick[r][t] != null) {
					if (ball.intersects(brick[r][t])) {
						ball.dy = -ball.dy;

						if (attractModeActive != true) {
							brick[r][t] = null;

							// This Switch gives proper score based on the Brick's position,
							// just like the original game.
							switch (t) {
								case 0:
									score += 7;
									break;
								case 1:
									score += 7;
									break;
								case 2:
									score += 5;
									break;
								case 3:
									score += 5;
									break;
								case 4:
									score += 3;
									break;
								case 5:
									score += 3;
									break;
								default:
									score += 1;
									break;
							}

						} else {
							choice = random.nextInt(4);
						}
					}
				}
			}
		}
		// ---------------------------------------------------

	}

	public void run() {

		long lastTime = System.nanoTime();
		double amountOfFPS = 60.0;
		double duration = 1000000000 / amountOfFPS;
		double delta = 0;

		while (true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / duration;
			lastTime = now;

			if (delta >= 1) {
				move();
				checkCollision();

				repaint();
				delta--;
			}
		}

	}

	public class Movement extends KeyAdapter {
		public void keyPressed(KeyEvent e) {

			// paddle1.keyPressed(e);
			if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && attractModeActive == false) {
				paddle1.setDeltaX(-1);
			}

			if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
					&& attractModeActive == false) {
				paddle1.setDeltaX(+1);
			}

			if (e.getKeyCode() == KeyEvent.VK_SPACE && attractModeActive == true) {
				attractModeActive = false;

				beginGame();
			}

		}

		public void keyReleased(KeyEvent e) {

			if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) & attractModeActive == false) {
				paddle1.setDeltaX(0);
			}

			if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
					&& attractModeActive == false) {
				paddle1.setDeltaX(0);
			}

		}

	}

	public void checkIfLost(int lives) {
		int remainingLives = lives;

		if (remainingLives < 1) {
			beginAttractMode();
		}
	}

	public void beginAttractMode() {
		attractModePaddles();
		newWelcome();

		attractModeActive = true;
		welcomeMessage = "PRESS SPACE";
	}

	public void attractModePaddles() {
		paddle1 = new Paddle(0, GAME_HEIGHT - (PADDLE_HEIGHT - BORDER_OFFSET / 2) - 50, GAME_WIDTH, PADDLE_HEIGHT);

	}

	public void beginGame() {
		newPaddles();
		newBall();
		newBricks();
		destroyWelcome();

		lives = 3;
		score = 0;

		ballColor = Color.white;
	}

} // end GamePanel