import java.awt.Color;
import java.awt.Font;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Main extends JFrame { // 여기서 이제 서버랑 통신을 합니다.
	private static final long serialVersionUID = 1L;
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	Lobby lobby = new Lobby();
	GamePanel panel;

	String userName;

	public Main(String userName, String ip_addr, String port_no) {
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// SendMessage("/login " + UserName);
			InteractMsg obcm = new InteractMsg(userName, "100");
			SendObject(obcm); // 서버에 로그인 정보 보내기
			this.userName = userName;

			ListenNetwork net = new ListenNetwork();
			net.start();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
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
						// TODO Auto-generated catch block
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
							lobby.updateRoom(cm);

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

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			System.out.println("Main.java의 SendObjec 메소드: 메시지 송신 에러");
		}
	}

	/*
	 * Atari 게임 시작
	 */
	public void runGame() {
		panel = new GamePanel();
		this.getContentPane().add(panel);

		this.setTitle("Atari Break-out");
		this.setResizable(false);
		this.setBackground(Color.black);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

	public void goToLobby() { // 게임 로비로 화면 전환
		setBounds(10, 10, 720, 480);
		setTitle("Atari Break-out");
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(lobby);
	}

	/*
	 * 게임 로비, 게임방들이 보인다. 게임방 생성할 수도 있다.
	 */
	public class Lobby extends JPanel { // 로비

		/**
		 * Create the panel.
		 */
		public Lobby() {
			setBackground(new Color(0, 128, 255));
			setLayout(null);

			JLabel lblLobby = new JLabel("Lobby");
			lblLobby.setForeground(new Color(255, 255, 255));
			lblLobby.setFont(new Font("배달의민족 도현", Font.PLAIN, 30));
			lblLobby.setBounds(39, 26, 116, 60);
			add(lblLobby);

			JButton btnCreateRoom = new JButton("Create");
			btnCreateRoom.setBackground(new Color(255, 255, 128));
			btnCreateRoom.setForeground(new Color(0, 0, 0));
			btnCreateRoom.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
			btnCreateRoom.setBounds(563, 424, 116, 29);
			add(btnCreateRoom);
			btnCreateRoom.addActionListener(new ActionListener() { // create 버튼 클릭 시
				public void actionPerformed(ActionEvent e) {
					String newRoomName;
					newRoomName = (String) JOptionPane.showInputDialog(null, "Room Name");
					InteractMsg obcm = new InteractMsg(userName, "200");
					obcm.roomName = newRoomName;
					SendObject(obcm);
				}
			});

			JButton btnBack = new JButton("Back");
			btnBack.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
			btnBack.setBounds(586, 45, 93, 29);
			add(btnBack);

			/*
			 * JPanel room_4 = new JPanel();
			 * room_4.setLayout(null);
			 * room_4.setBounds(39, 340, 640, 60);
			 * add(room_4);
			 * 
			 * JLabel lblRoomName_1 = new JLabel("Game Room Name");
			 * lblRoomName_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			 * lblRoomName_1.setBounds(12, 10, 187, 40);
			 * room_4.add(lblRoomName_1);
			 * 
			 * JLabel lblEnteredUsers_1 = new JLabel("0");
			 * lblEnteredUsers_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			 * lblEnteredUsers_1.setBounds(591, 10, 12, 40);
			 * room_4.add(lblEnteredUsers_1);
			 * 
			 * JLabel lblUnit_1 = new JLabel("명");
			 * lblUnit_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			 * lblUnit_1.setBounds(605, 10, 23, 40);
			 * room_4.add(lblUnit_1);
			 * 
			 * JPanel room_3 = new JPanel();
			 * room_3.setLayout(null);
			 * room_3.setBounds(39, 260, 640, 60);
			 * add(room_3);
			 * 
			 * JLabel lblRoomName_2 = new JLabel("Game Room Name");
			 * lblRoomName_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			 * lblRoomName_2.setBounds(12, 10, 187, 40);
			 * room_3.add(lblRoomName_2);
			 * 
			 * JLabel lblEnteredUsers_2 = new JLabel("0");
			 * lblEnteredUsers_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			 * lblEnteredUsers_2.setBounds(591, 10, 12, 40);
			 * room_3.add(lblEnteredUsers_2);
			 * 
			 * JLabel lblUnit_2 = new JLabel("명");
			 * lblUnit_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			 * lblUnit_2.setBounds(605, 10, 23, 40);
			 * room_3.add(lblUnit_2);
			 * 
			 * JPanel room_2 = new JPanel();
			 * room_2.setLayout(null);
			 * room_2.setBounds(39, 180, 640, 60);
			 * add(room_2);
			 * 
			 * JLabel lblRoomName_3 = new JLabel("Game Room Name");
			 * lblRoomName_3.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			 * lblRoomName_3.setBounds(12, 10, 187, 40);
			 * room_2.add(lblRoomName_3);
			 * 
			 * JLabel lblEnteredUsers_3 = new JLabel("0");
			 * lblEnteredUsers_3.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			 * lblEnteredUsers_3.setBounds(591, 10, 12, 40);
			 * room_2.add(lblEnteredUsers_3);
			 * 
			 * JLabel lblUnit_3 = new JLabel("명");
			 * lblUnit_3.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			 * lblUnit_3.setBounds(605, 10, 23, 40);
			 * room_2.add(lblUnit_3);
			 */
		}

		public void updateRoom(InteractMsg cm) { // 서버에서 받은 데이터를 토대로 로비에 게임방들을 그리거나, 인원수가 추가될 경우 update.
			// ob에는 방에 대한 정보가 들어가있다.
			JButton room_1 = new JButton();
			room_1.setBounds(40, 100, 640, 60);
			add(room_1);
			room_1.setLayout(null);
			room_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getIntoRoom();// 방 클릭 시 해당 게임 방으로 넘어가야함.
				}
			});

			JLabel lblRoomName = new JLabel(cm.roomName);
			lblRoomName.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			lblRoomName.setBounds(12, 10, 187, 40);
			room_1.add(lblRoomName);

			JLabel lblEnteredUsers = new JLabel("0");
			lblEnteredUsers.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			lblEnteredUsers.setBounds(591, 10, 12, 40);
			room_1.add(lblEnteredUsers);

			JLabel lblUnit = new JLabel("명");
			lblUnit.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
			lblUnit.setBounds(605, 10, 23, 40);
			room_1.add(lblUnit);

			// repaint?
		}

		public void getIntoRoom() { // 해당 방으로 넘어간다. 입장한 유저 정보를 서버에 보내고, 방정보도 update...인원수 추가되었으니까
			// GameRoom으로 넘어간다.
			// 인원수가 추가되는 update가 일어나기때문에 updateRoom()
		}
	}
}
