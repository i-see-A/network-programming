import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.GraphicsEnvironment;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePlay extends JPanel implements KeyListener, ActionListener{
	private boolean play = false; //게임이 지 혼자 플레이 되지 않기 하기 위해
	private int score = 0; //우리 게임에서는 점수 기록 안하기 때문에 추후에 없애줘야함.
	
	private int totalBricks = 21; //전체 벽돌 수도 나중에 바꿔줘야함.
	
	private Timer timer;
	private int delay = 8; //
	
	private int playerX = 310; //bar의 위치
	
	private int ballposX = 10; //ball 위치
	private int ballposY = 30;
	
	private int ballXdir = -1; //
	private int ballYdir = -2;
	
	private Rectangle bar, ball;
	private Font BM_DOHYEON_BOLD, COOKIE_RUN_BOLD;
	
	private MapGenerator map;
	
	public GamePlay() { //
		BM_DOHYEON_BOLD = new Font("배달의민족 도현", Font.BOLD, 18);
		COOKIE_RUN_BOLD = new Font("CookieRun_Bold", Font.BOLD, 18);

		try {
			Path currentPath = Paths.get(System.getProperty("user.dir"));

			Path dohyeonFontPath = Paths.get(currentPath.toString().replace("AtariBrickBreaker", ""), "AtariBrickBreaker", "assets", "fonts",
					"BMDOHYEON.ttf");
			Path cookieRunFontPath = Paths.get(currentPath.toString().replace("AtariBrickBreaker", ""), "AtariBrickBreaker", "assets", "fonts",
					"CookieRun_Bold.ttf");

			BM_DOHYEON_BOLD = Font.createFont(Font.TRUETYPE_FONT, dohyeonFontPath.toFile())
					.deriveFont(Font.BOLD, 32f);
			COOKIE_RUN_BOLD = Font.createFont(Font.TRUETYPE_FONT, new File(cookieRunFontPath.toString()))
					.deriveFont(Font.PLAIN, 18f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

			// register the font
			ge.registerFont(BM_DOHYEON_BOLD);
			ge.registerFont(COOKIE_RUN_BOLD);
		} catch (Exception e) {
			e.printStackTrace();
		}

		map = new MapGenerator(3,7);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		timer = new Timer(delay, this);
		timer.start();

		
	}
	
	public void paint(Graphics g) {
		//background
		g.setColor(Color.black); //배경색깔
		g.fillRect(1, 1, 700, 600); //크기 추후에 바꿔줘야함.
		
		//drawing map
		map.draw((Graphics2D)g);
		
		
		//scores
		g.setColor(Color.white);
		g.setFont(BM_DOHYEON_BOLD.deriveFont(Font.PLAIN, 25f));
		g.drawString(""+score, 650, 50);
		
		//the paddle
		g.setColor(Color.green);
		g.fillRect(playerX, 550, 100, 8);
		
		//the ball 나중에 이미지로 바꾸기
		g.setColor(Color.yellow);
		g.fillOval(ballposX, ballposY, 20, 20);
		
		if(totalBricks <= 0 || ballposY>570) {
			play = false;
			ballXdir = 0;
			ballYdir = 0;
			g.setColor(Color.green);
			g.setFont(BM_DOHYEON_BOLD.deriveFont(Font.BOLD, 25f));
			g.drawString("game clear Score: "+score, 230, 300);
			
			g.setFont(BM_DOHYEON_BOLD.deriveFont(Font.BOLD, 20f));
			g.drawString("Press Enter to Restart" , 230, 350);
			
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		timer.start();
		if(play) { //플레이가 되면
			ball = new Rectangle(ballposX, ballposY, 20, 20);
			bar = new Rectangle(playerX, 550, 100, 8);

			if(ball.intersects(bar))
				ballYdir = -ballYdir;
			
			// A: for(int i = 0; i<map.map.length; i++ ) { //첫번째 map은 Mapgenerator map, 두번째 map은 map[][]
			// 	for(int j=0;j<map.map[0].length;j++) {
			// 		if(map.map[i][j] > 0) {
			// 			int brickX = j * map.WIDTH+ 80;
			// 			int brickY = i * map.HEIGHT+ 50;
			// 			int brickWidth = map.WIDTH;
			// 			int brickHeight = map.HEIGHT;
						
			// 			Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
			// 			Rectangle ballRect = new Rectangle(ballposX,ballposY,20,20);
			// 			Rectangle brickRect = rect;
						
			// 			if(ballRect.intersects(brickRect)) {
			// 				map.setBrickValue(0, i, j);
			// 				totalBricks--;
			// 				score += 5; //점수는 필요없으니 나중에 지우기
							
			// 				if(ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width) {
			// 					ballXdir = -ballXdir;
			// 				} else {
			// 					ballYdir = -ballYdir;
			// 				}
							
			// 				break A;
			// 			}
			// 		}
			// 	}
			// }
			ballposX += ballXdir;
			ballposY += ballYdir;
			if(ballposX <0) {
				ballXdir = -ballXdir;
			}
			if(ballposY <0) {
				ballYdir = -ballYdir;
			}
			if(ballposX >670) {
				ballXdir = -ballXdir;
			}
			
		}
		repaint(); //actionPerformed되면 timer 시작, 위의 게임을 다시 그린다.
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		playerX = playerX>=600?600:playerX;
		playerX = playerX<10?10:playerX;

		if(e.getKeyCode()!=KeyEvent.VK_ENTER)
			moveBar(e.getKeyCode(), 0);
		
		else {
			if(!play) {
				play = true;
				ballposX = 120;
				ballposY = 350;
				ballXdir = -1;
				ballYdir = -2;
				playerX = 310;
				score = 0;
				totalBricks = 21;
				map = new MapGenerator(3,7);
				
				repaint();
			}
		}
		
	}
	
	public void moveBar(int position, float speed) { 
		play = true;
		playerX = position==KeyEvent.VK_LEFT?playerX-20:playerX+20;
	}
}
