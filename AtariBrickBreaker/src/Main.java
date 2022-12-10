
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

//import GamePanel.Movement;

public class Main extends JPanel { // 여기서 이제 서버랑 통신을 합니다.
	AtariClientMain acm = new AtariClientMain();

	ImageIcon backIcon1 = new ImageIcon("assets/image/back1.png");
	ImageIcon backIcon2 = new ImageIcon("assets/image/back2.png");

	private static final long serialVersionUID = 1L;
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public int width, height;

	Lobby lobby;
	GamePanel panel;
	GameRoomUI gameroomUI;
	JFrame jframe;

	String userName;

	public Main(String userName, String ip_addr, String port_no) {
		this.userName = userName;
		jframe = new JFrame();
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			InteractMsg obcm = new InteractMsg(userName, "100"); // sendUserName
			SendObject(obcm);

			lobby = new Lobby();
			InteractMsg obcm2 = new InteractMsg(userName, "201"); // sendRoomInfoRequest
			SendObject(obcm2);

			ListenNetwork net = new ListenNetwork();
			net.start();

		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			System.out.println("Main.java의 SendObject 메소드: 메시지 송신 에러" + e);
			e.printStackTrace();
		}
	}

	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					InteractMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof InteractMsg) {
						cm = (InteractMsg) obcm;
						msg = String.format("[%s]\n", cm.userName);
					} else
						continue;

					switch (cm.code) {
						case "200": // 서버로부터 게임방 응답 오면, 방 그리기.
							lobby.drawRoomUI(cm);

							break;
						case "201": // 서버로부터 로비에 표시할 방 정보들이 오면
							lobby.drawRoomUI(cm);

							break;
						case "300":
							// 게임룸 UI 그리기
							drawGameRoomUI(cm);
							break;
						case "204": // 게임플레이창 그리기
							drawGame(cm);
							break;
						case "205":
							// 상대편 키코드 받아옴
							System.out.println("상대편 키코드 받아옴" + cm.keyCode);
							// cm.keyCode가 스페이스바면 게임 시작
							if (cm.keyCode == 32) {
								panel.attractModeActive = false;
								panel.beginGame();
							} else if (cm.keyCode == 37) {
								panel.paddle1.setDeltaX(-1);
							} else if (cm.keyCode == 39) {
								panel.paddle1.setDeltaX(+1);
							} else if (cm.keyCode == 65) {
								panel.paddle2.setDeltaX(-1);
							} else if (cm.keyCode == 67) {
								panel.paddle2.setDeltaX(+1);
							}

					}
				} catch (IOException e) {
					System.out.println("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}

	/*
	 * 게임 로비, 게임방들이 보인다. 게임방 생성할 수도 있다.
	 */

	public class Lobby extends JPanel { // 로비
		int w = acm.VIEW_WIDTH;
		int h = acm.VIEW_HEIGHT;

		JButton btnCreateRoom;
		JButton btnBack;

		/**
		 * Create the panel.
		 */
		public Lobby() {
			width = w;
			height = h;

			// 로비 UI 그리기
			setBackground(new Color(0, 128, 255));
			setLayout(null);

			JLabel lblLobby = new JLabel("Lobby");
			lblLobby.setForeground(new Color(255, 255, 255));
			lblLobby.setFont(new Font("배달의민족 도현", Font.PLAIN, 52));
			lblLobby.setBounds((int) (0.13 * w), (int) (0.125 * h), 300, 60);
			add(lblLobby);

			JLabel lblUserName = new JLabel("I'm " + userName);
			lblUserName.setForeground(new Color(255, 255, 255));
			lblUserName.setFont(new Font("배달의민족 도현", Font.PLAIN, 36));
			lblUserName.setBounds((int) (0.13 * w), (int) (0.4 * h), 300, 60);
			add(lblUserName);

			btnCreateRoom = new JButton("Create");
			btnCreateRoom.setBackground(new Color(255, 255, 128));
			btnCreateRoom.setForeground(new Color(0, 0, 0));
			btnCreateRoom.setFont(new Font("배달의민족 도현", Font.PLAIN, 28));
			btnCreateRoom.setBounds((int) (0.13 * w), (int) (0.75 * h), 150, 50);
			btnCreateRoom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String newRoomName;
					newRoomName = (String) JOptionPane.showInputDialog(null, "Room Name");
					InteractMsg obcm = new InteractMsg(userName, "200");
					obcm.roomName = newRoomName;
					SendObject(obcm);
				}
			});
			add(btnCreateRoom);

		}

		// 방 그리는 메소드
		public void drawRoomUI(InteractMsg cm) {
			for (int i = 0; i < cm.roomInfo.length; i++) {
				if (cm.roomInfo[i] == null)
					continue;

				String[] roomInfoList = cm.roomInfo[i].split(",", 5); // 방번호 방제 방상태 인원수 입장인원1 2 3 4
				int receivedRoomNumber = Integer.parseInt(roomInfoList[0]); // 방번호
				String receivedRoomName = roomInfoList[1];// 방제
				String receivedRoomStatus = roomInfoList[2];// 방상태
				int receivedEnteredUser = Integer.parseInt(roomInfoList[3]);// 인원수
				String receivedEnteredUserList = roomInfoList[4];// user1 user2 user3 user4

				String output = String.format("ROOM INFO = %d, %s, %s, %d, %s", receivedRoomNumber, receivedRoomName,
						receivedRoomStatus, receivedEnteredUser, receivedEnteredUserList);
				System.out.println(output);
				RoomUI roomUI = new RoomUI(receivedRoomNumber, receivedRoomName, receivedRoomStatus,
						receivedEnteredUser); //
				add(roomUI);
			}
		}

		public class RoomUI extends JButton {
			int roomNumber;
			String roomName;
			int people;
			int index;
			String roomStatus; // 게임중, 준비중

			public RoomUI(int roomNumber, String roomName, String roomStatus, int people) { // UI에 방상태 JLabel 추가해야함
				// 방 정보 표시할 것들. 서버에서 정보 받아와야함.
				this.roomNumber = roomNumber;
				this.roomName = roomName;
				this.roomStatus = roomStatus;
				this.people = people;

				if (roomStatus == "CLOSED" || roomStatus == "STARTED") {
					this.setEnabled(false);
				}

				this.setBounds((int) (0.125 * w + 250), (int) (0.25 * h) + roomNumber * 70, 350, 55);
				lobby.add(this);
				this.setBackground(Color.white);
				this.setLayout(null);

				JLabel lblRoomName = new JLabel(roomName);
				lblRoomName.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
				lblRoomName.setBounds(15, 7, 330, 40);
				this.add(lblRoomName);

				JLabel lblRoomStatus = new JLabel(roomStatus);
				lblRoomStatus.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
				lblRoomStatus.setBounds(260, 0, 100, 40);
				this.add(lblRoomStatus);

				JLabel lblEnteredUsers = new JLabel(Integer.toString(people)); // 인원수도 서버에서 넘겨받은 정보로 바꾸기
				lblEnteredUsers.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
				lblEnteredUsers.setBounds(260, 20, 15, 40);
				this.add(lblEnteredUsers);

				JLabel lblUnit = new JLabel("/ 2 명");
				lblUnit.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
				lblUnit.setBounds(280, 20, 60, 40);
				this.add(lblUnit);

				this.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						InteractMsg obcm = new InteractMsg(userName, "300");
						obcm.roomInfo[roomNumber] = roomNumber + "," + roomName + "," + roomStatus + "," + people
								+ "," + userName;
						SendObject(obcm);
						System.out.println("게임방 클릭되었음");
					}
				});

			}

		}

	}

	public class GameRoomUI extends JPanel {

		String gameRoomName; // 방 이름
		String[] userNameList = new String[] { "NO USER", "NO USER" };
		String myName;
		int roomNum;
		String userlist;
		int people;

		/**
		 * Create the panel.
		 */
		public GameRoomUI(int width, int height, int roomNum, String roomName, String[] userList, String userName) { // 게임방
			int w = height;
			int h = height; // 틀
			this.gameRoomName = roomName;
			this.myName = userName;
			this.roomNum = roomNum;
			this.userlist = "";
			this.people = userList.length;

			for (int i = 0; i < userList.length; i++) {
				userNameList[i] = userList[i];
				userlist += userList[i] + "/";
			}

			setSize(width, height);
			setBackground(new Color(0, 128, 255));
			setLayout(null);

			JLabel lblRoomName = new JLabel(gameRoomName);
			lblRoomName.setForeground(Color.WHITE);
			lblRoomName.setFont(new Font("배달의민족 도현", Font.PLAIN, 30));
			lblRoomName.setBounds((int) (0.13 * w), (int) (0.125 * h), 300, 60);
			add(lblRoomName);

			JButton btnStartButton = new JButton("START");
			btnStartButton.setBounds((int) (0.13 * w), (int) (0.75 * h), 150, 50);
			add(btnStartButton);
			btnStartButton.setForeground(new Color(0, 0, 0));
			btnStartButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("멀티 게임이 시작되었습니다.");
					InteractMsg obcm = new InteractMsg(userName, "204");
					obcm.roomInfo[roomNum] = roomNum + "," + roomName + "," + "OPENED" + "," + people
							+ "," + userlist;
					SendObject(obcm);
				}
			});
			btnStartButton.setBackground(new Color(255, 255, 128));
			btnStartButton.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));

			if (!myName.equals(userNameList[0])) // 내가 방장이 아니면 Start버튼 보이지 않음
				btnStartButton.setVisible(false);

			JPanel slot_1 = new JPanel();
			slot_1.setBackground(new Color(213, 234, 255));
			slot_1.setBounds((int) (0.13 * w), (int) (0.25 * h), 250, 200);
			add(slot_1);
			slot_1.setLayout(null);

			JLabel lblUserPosition = new JLabel("Master");
			lblUserPosition.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
			lblUserPosition.setBounds(20, 20, 100, 20);
			slot_1.add(lblUserPosition);

			JLabel lblUseName = new JLabel(userNameList[0]);
			lblUseName.setHorizontalAlignment(SwingConstants.CENTER);
			lblUseName.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
			lblUseName.setBounds(20, 100, 200, 30);
			slot_1.add(lblUseName);

			JPanel slot_2 = new JPanel();
			slot_2.setLayout(null);
			slot_2.setBackground(new Color(213, 234, 255));
			slot_2.setBounds((int) (0.13 * w + 300), (int) (0.25 * h), 250, 200);
			add(slot_2);

			JLabel lblUserPosition_1 = new JLabel("Player");
			lblUserPosition_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
			lblUserPosition_1.setBounds(20, 20, 100, 20);
			slot_2.add(lblUserPosition_1);

			JLabel lblUseName_1 = new JLabel(userNameList[1]);
			lblUseName_1.setHorizontalAlignment(SwingConstants.CENTER);
			lblUseName_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
			lblUseName_1.setBounds(20, 100, 200, 30);
			slot_2.add(lblUseName_1);

		}

	}

	public void drawGameRoomUI(InteractMsg cm) { // 서버로부터 받은 InteractMsg cm
		String[] roomInfoList = cm.roomInfo[cm.roomNum].split(",", 5); // 방번호 방제 방상태 인원수 입장인원1 2 3 4
		String receivedRoomName = roomInfoList[1];// 방제
		String receivedEnteredUserList = roomInfoList[4];// user1/user2/user3/user4
		String[] userList = receivedEnteredUserList.split("/");

		// 게임방으로 화면 전환
		// acm.game.remove(lobby);
		gameroomUI = new GameRoomUI(width, height, cm.roomNum, receivedRoomName, userList, cm.userName);
		if (jframe != null)
			jframe.remove(lobby);
		jframe.setSize(width, height);
		jframe.getContentPane().add(gameroomUI);
		jframe.setVisible(true);
	}

	public void drawGame(InteractMsg cm) {
		jframe = new JFrame();
		panel = new GamePanel();
		gameroomUI.setVisible(false);
		if (jframe != null)
			jframe.remove(gameroomUI);
		jframe.getContentPane().add(panel);
		jframe.pack();
		jframe.setVisible(true);
	}

	// 여기서부터 게임
	class GameFrame extends JFrame {

		GamePanel panel;

		GameFrame() { // costruttore

			panel = new GamePanel();

			this.getContentPane().add(panel); // finestra Swing

			this.setTitle("Bricks Crusher: Break the Bricks");
			this.setResizable(false);
			this.setBackground(Color.black);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.pack();
			this.setVisible(true);
			this.setLocationRelativeTo(null);

		} // end costruttore

	}

	class GamePanel extends JPanel implements Runnable {
		static final int GAME_WIDTH = 700;
		static final int GAME_HEIGHT = (int) (GAME_WIDTH * (0.8));
		static final Dimension SCREEN_SIZE1 = new Dimension(GAME_WIDTH, GAME_HEIGHT);

		static final int PADDLE_WIDTH = 55;
		static final int PADDLE_HEIGHT = 10;

		static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
		static final int BALL_DIAMETER = 15;

		int lives = 15;
		int score = 0;
		int hits = 0;
		int choice = 0;
		int inclinationSelection = 0;

		String welcomeMessage = "PRESS SPACE";

		boolean attractModeActive = true;
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

			paddleColor1 = Color.white;
			paddleColor2 = Color.blue;

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
			paddle1 = new Paddle((GAME_WIDTH - PADDLE_WIDTH) / 2 - 50,
					GAME_HEIGHT - (PADDLE_HEIGHT - BORDER_OFFSET / 2) - 50,
					PADDLE_WIDTH, PADDLE_HEIGHT);
			paddle2 = new Paddle((GAME_WIDTH - PADDLE_WIDTH) / 2 + 50,
					GAME_HEIGHT - (PADDLE_HEIGHT - BORDER_OFFSET / 2) - 50,
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
			ball1 = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2) - 50, (GAME_HEIGHT / 2) - (BALL_DIAMETER / 2),
					BALL_DIAMETER,
					BALL_DIAMETER);
			ball1.setDY(1);

			ball2 = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2) + 50, (GAME_HEIGHT / 2) - (BALL_DIAMETER / 2),
					BALL_DIAMETER,
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

				InteractMsg cm = new InteractMsg(userName, "205");
				cm.keyCode = e.getKeyCode(); // 37 left, 39 right, 65 a(left), 68 d(right)
				SendObject(cm); // keyCode서버로 보냄
				System.out.println(cm.keyCode + "서버로 보냄");
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

				// InteractMsg cm = new InteractMsg(userName, "205");
				// cm.keyCode = e.getKeyCode(); // 37 left, 39 right, 65 a(left), 68 d(right)
				// SendObject(cm); // keyCode서버로 보냄
				// System.out.println(cm.keyCode + "서버로 보냄");

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

			lives = 15;
			score = 0;

			ballColor1 = Color.white;
			ballColor2 = Color.blue;
		}

	} //

	class Ball extends Rectangle {

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

	class Brick extends Rectangle {

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

	class Lives extends Rectangle {

		Lives(int x, int y, int width, int height) {
			super(x, y, width, height);
		}

		public void draw(Graphics g, Font atari, int GAME_WIDTH, int GAME_HEIGHT, int lives) {
			int livesRemaining = lives;
			String messageToDisplay = Integer.toString(livesRemaining);
			FontMetrics fm = g.getFontMetrics();

			g.setFont(atari);
			g.setColor(Color.white);
			g.drawString("LIVES " + messageToDisplay, (GAME_WIDTH) - 15 - fm.stringWidth("LIVES " + messageToDisplay),
					GAME_HEIGHT - 15);
		}

	}

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

	class Score extends Rectangle {

		Score(int x, int y, int width, int height) {
			super(x, y, width, height);
		}

		public void draw(Graphics g, Font atari, int GAME_WIDTH, int GAME_HEIGHT, int score) {
			int currentScore = score;
			String messageToDisplay = Integer.toString(currentScore);

			g.setFont(atari);
			g.setColor(Color.white);
			g.drawString("SCORE " + messageToDisplay, 15, (GAME_HEIGHT) - 15);
		}

	}

	class Welcome extends Rectangle {

		Welcome(int x, int y, int welcomeWidth, int welcomeHeight) {
			super(x, y, welcomeWidth, welcomeHeight);
		}

		public void draw(Graphics g, Font atari, int GAME_WIDTH, int GAME_HEIGHT, String welcomeMessage) {
			FontMetrics fm = g.getFontMetrics();
			String messageToDisplay = welcomeMessage;

			g.setFont(atari);
			g.setColor(Color.white);
			g.drawString(messageToDisplay, (GAME_WIDTH / 2) - fm.stringWidth(messageToDisplay) - 20,
					(GAME_HEIGHT / 2) - fm.getHeight());
		}

	}

}
