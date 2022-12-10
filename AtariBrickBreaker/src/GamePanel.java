
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

	int lives = 10;
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

	Paddle paddle1, paddle2;
	Ball ball1, ball2;
	Brick[][] brick;
	Welcome welcome;
	Lives livesUI;
	Score scoreUI;
	Font atari;
	Color ballColor1, ballColor2, paddleColor1, paddleColor2;
	Random random;

	GamePanel() {
		random = new Random();

		brick = new Brick[rows][columns];
		livesUI = new Lives(GAME_WIDTH - 20, GAME_HEIGHT - 20, 20, 20);
		scoreUI = new Score(GAME_WIDTH - 20, GAME_HEIGHT - 20, 20, 20);
		ballColor1 = Color.white;
		ballColor2 = Color.blue;

		paddleColor1 = Color.ORANGE;
		paddleColor2 = Color.MAGENTA;

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
		paddle2 = new Paddle((GAME_WIDTH - PADDLE_WIDTH) / 2, GAME_HEIGHT - (PADDLE_HEIGHT - BORDER_OFFSET / 2) - 50,
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
		ball1 = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), (GAME_HEIGHT / 2) - (BALL_DIAMETER / 2), BALL_DIAMETER,
				BALL_DIAMETER);
		ball1.setDY(1);

		ball2 = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), (GAME_HEIGHT / 2) - (BALL_DIAMETER / 2), BALL_DIAMETER,
				BALL_DIAMETER);
		ball2.setDY(1);
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
					ballColor1 = Color.cyan;
					ballColor2 = Color.cyan;
					break;
				case 1:
					ballColor1 = Color.magenta;
					ballColor2 = Color.magenta;
					break;
				case 2:
					ballColor1 = Color.red;
					ballColor2 = Color.red;
					break;
				case 3:
					ballColor1 = Color.orange;
					ballColor2 = Color.orange;
					break;
				case 4:
					ballColor1 = Color.yellow;
					ballColor2 = Color.yellow;
					break;
				case 5:
					ballColor1 = Color.green;
					ballColor2 = Color.green;
					break;
				default:
					ballColor1 = Color.white;
					ballColor2 = Color.white;
					break;
			}

		}

		paddle1.draw(g, paddleColor1);
		paddle2.draw(g, paddleColor2);

		ball1.draw(g, ballColor1);
		ball2.draw(g, ballColor2);
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

	public void move1() {
		paddle1.move();
		ball1.move();
	}

	public void move2() {
		paddle2.move();
		ball2.move();
	}

	public void checkCollision() {

		if (paddle1.x <= 0 || paddle2.x <= 0) {
			paddle1.x = 0;
			paddle2.x = 0;
		}

		if (paddle1.x >= GAME_WIDTH - PADDLE_WIDTH)
			paddle1.x = GAME_WIDTH - PADDLE_WIDTH;

		if (paddle2.x >= GAME_WIDTH - PADDLE_WIDTH)
			paddle2.x = GAME_WIDTH - PADDLE_WIDTH;

		if (ball1.y <= 0) {
			ball1.dy = -ball1.dy;
		}

		if (ball2.y <= 0) {
			ball2.dy = -ball2.dy;
		}

		if (ball1.y >= GAME_HEIGHT - BALL_DIAMETER) {
			ball1.dy = -ball1.dy;

			if (lives > 0) {
				lives = lives - 1;
			}

			checkIfLost(lives);
			newBall();
		}

		if (ball2.y >= GAME_HEIGHT - BALL_DIAMETER) {
			ball2.dy = -ball2.dy;

			if (lives > 0) {
				lives = lives - 1;
			}

			checkIfLost(lives);
			newBall();
		}

		if (ball1.x <= 0) {
			ball1.dx = -ball1.dx;

			if (attractModeActive == true) {
				choice = random.nextInt(6);
			}
		}

		if (ball2.x <= 0) {
			ball2.dx = -ball2.dx;

			if (attractModeActive == true) {
				choice = random.nextInt(6);
			}
		}

		if (ball1.x >= GAME_WIDTH - BALL_DIAMETER) {
			ball1.dx = -ball1.dx;

			if (attractModeActive == true) {
				choice = random.nextInt(6);
			}
		}

		if (ball2.x >= GAME_WIDTH - BALL_DIAMETER) {
			ball2.dx = -ball2.dx;

			if (attractModeActive == true) {
				choice = random.nextInt(6);
			}
		}

		if (ball1.intersects(paddle1)) {
			double inclination;

			if (attractModeActive != true) {
				// This keeps track of how many times the Ball touched the Paddle.
				// It's going to be useful to set the speed.
				hits = hits + 1;

				// This awful if-else chain handles the inclination the Ball needs to take when
				// having a collision with the Paddle. This ensures the Ball does not go in the
				// same
				// places and keeps the game fun.
				if (ball1.x + (BALL_DIAMETER / 2) <= paddle1.x + PADDLE_WIDTH / 8) {
					inclination = -1.6;
				} else {
					if (ball1.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 2) {
						inclination = -1.4;
					} else {
						if (ball1.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 3) {
							inclination = -0.7;
						} else {
							if (ball1.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 5) {
								inclination = 0.55;

								if (random.nextInt(2) == 0) {
									inclination = inclination * -1;
								}

							} else {
								if (ball1.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 6) {
									inclination = 0.7;
								} else {
									if (ball1.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 7) {
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
				// Also, change the ball1's color.
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
				ball1.setDY(1);
			}

			if (hits >= 4 && hits < 12) {
				ball1.setDY(1.5);
			}

			if (hits >= 12) {
				ball1.setDY(2);
			}

			// Setting the values inside the class after calculating the inclination.
			ball1.dy = -ball1.dy;
			ball1.setDX(inclination);

		}

		if (ball2.intersects(paddle2)) {
			double inclination;

			if (attractModeActive != true) {
				// This keeps track of how many times the Ball touched the Paddle.
				// It's going to be useful to set the speed.
				hits = hits + 1;

				// This awful if-else chain handles the inclination the Ball needs to take when
				// having a collision with the Paddle. This ensures the Ball does not go in the
				// same
				// places and keeps the game fun.
				if (ball2.x + (BALL_DIAMETER / 2) <= paddle2.x + PADDLE_WIDTH / 8) {
					inclination = -1.6;
				} else {
					if (ball2.x + (BALL_DIAMETER / 2) <= paddle2.x + (PADDLE_WIDTH / 8) * 2) {
						inclination = -1.4;
					} else {
						if (ball2.x + (BALL_DIAMETER / 2) <= paddle2.x + (PADDLE_WIDTH / 8) * 3) {
							inclination = -0.7;
						} else {
							if (ball2.x + (BALL_DIAMETER / 2) <= paddle2.x + (PADDLE_WIDTH / 8) * 5) {
								inclination = 0.55;

								if (random.nextInt(2) == 0) {
									inclination = inclination * -1;
								}

							} else {
								if (ball2.x + (BALL_DIAMETER / 2) <= paddle2.x + (PADDLE_WIDTH / 8) * 6) {
									inclination = 0.7;
								} else {
									if (ball2.x + (BALL_DIAMETER / 2) <= paddle2.x + (PADDLE_WIDTH / 8) * 7) {
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
				// Also, change the ball2's color.
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
				ball2.setDY(1);
			}

			if (hits >= 4 && hits < 12) {
				ball2.setDY(1.5);
			}

			if (hits >= 12) {
				ball2.setDY(2);
			}

			// Setting the values inside the class after calculating the inclination.
			ball2.dy = -ball2.dy;
			ball2.setDX(inclination);

		}

		// This code takes care of Brick collisions.
		for (int r = 0; r < rows; r++) {
			for (int t = 0; t < columns; t++) {
				if (brick[r][t] != null) {
					if (ball1.intersects(brick[r][t])) {
						ball1.dy = -ball1.dy;

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

		for (int r = 0; r < rows; r++) {
			for (int t = 0; t < columns; t++) {
				if (brick[r][t] != null) {
					if (ball2.intersects(brick[r][t])) {
						ball2.dy = -ball2.dy;

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
				move1();
				move2();
				checkCollision();

				repaint();
				delta--;
			}
		}

	}

	public class Movement extends KeyAdapter {
		public void keyPressed(KeyEvent e) {

			if (e.getKeyCode() == KeyEvent.VK_LEFT && attractModeActive == false) {
				paddle1.setDeltaX(-1);
			}

			if (e.getKeyCode() == KeyEvent.VK_A && attractModeActive == false)
				paddle2.setDeltaX(-1);

			if (e.getKeyCode() == KeyEvent.VK_RIGHT && attractModeActive == false) {
				paddle1.setDeltaX(+1);
			}

			if (e.getKeyCode() == KeyEvent.VK_D && attractModeActive == false) {
				paddle2.setDeltaX(+1);
			}

			if (e.getKeyCode() == KeyEvent.VK_SPACE && attractModeActive == true) {
				attractModeActive = false;

				beginGame();
			}

		}

		public void keyReleased(KeyEvent e) {

			if (e.getKeyCode() == KeyEvent.VK_LEFT && attractModeActive == false) {
				paddle1.setDeltaX(0);
			}

			if (e.getKeyCode() == KeyEvent.VK_A && attractModeActive == false) {
				paddle2.setDeltaX(0);
			}

			if (e.getKeyCode() == KeyEvent.VK_RIGHT && attractModeActive == false) {
				paddle1.setDeltaX(0);
			}

			if (e.getKeyCode() == KeyEvent.VK_D && attractModeActive == false) {
				paddle2.setDeltaX(0);
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
		paddle2 = new Paddle(0, GAME_HEIGHT - (PADDLE_HEIGHT - BORDER_OFFSET / 2) - 50, GAME_WIDTH, PADDLE_HEIGHT);
	}

	public void beginGame() {
		newPaddles();
		newBall();
		newBricks();
		destroyWelcome();

		lives = 10;
		score = 0;

		ballColor1 = Color.white;
		ballColor2 = Color.blue;
	}

} // end GamePanel