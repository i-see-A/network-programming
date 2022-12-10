
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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
						case "204":
							drawGame(cm);
							break;

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
		gameroomUI.setVisible(false);
		if (jframe != null)
			jframe.remove(gameroomUI);
		jframe.getContentPane().add(panel);
		jframe.pack();
		jframe.setVisible(true);
	}

}
