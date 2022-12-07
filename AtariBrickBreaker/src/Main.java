
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

public class Main extends JPanel{ //여기서 이제 서버랑 통신을 합니다.
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
	
	public int width,height;

	Lobby lobby;
	GamePanel panel;

	String userName;
	
	public Main(String userName, String ip_addr, String port_no) {
		this.userName = userName;
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());
			
			InteractMsg obcm = new InteractMsg(userName, "100"); //sendUserName
			SendObject(obcm);
			
			lobby = new Lobby();
			InteractMsg obcm2 = new InteractMsg(userName, "201"); //sendRoomInfoRequest
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
						System.out.println("cm.code = " + cm.code);
					} else
						continue;

						switch (cm.code) {
						case "200": // 서버로부터 게임방 응답 오면, 방 그리기.
							System.out.println("Main 200 받음");
							lobby.drawRoomUI(cm);

							break;
						case "201": //서버로부터 로비에 표시할 방 정보들이 오면
							System.out.println("Main 201 받음");
								lobby.drawRoomUI(cm);
								
							break;
						case "300":
							System.out.println("Main 300 받음");
							//로비 다시 그리기
							lobby.drawRoomUI(cm);
							//게임룸 UI 그리기
							drawGameRoomUI(cm);
						}
					}
				 catch (IOException e) {
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

	public class Lobby extends JPanel { //로비
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (int)dimension.getWidth();
		int h = (int)dimension.getHeight();
		
		JButton btnCreateRoom;
		JButton btnBack;
		/**
		 * Create the panel.
		 */
		public Lobby() {
			width = w;
			height = h;

			//로비 UI 그리기
			setBackground(new Color(0, 128, 255));
			setLayout(null);

			JLabel lblLobby = new JLabel("Lobby");
			lblLobby.setForeground(new Color(255, 255, 255));
			lblLobby.setFont(new Font("배달의민족 도현", Font.PLAIN, 52));
			lblLobby.setBounds((int)(0.125*w),(int)(0.125*h),300,60);
			add(lblLobby);

			btnCreateRoom = new JButton("Create");
			btnCreateRoom.setBackground(new Color(255, 255, 128));
			btnCreateRoom.setForeground(new Color(0, 0, 0));
			btnCreateRoom.setFont(new Font("배달의민족 도현", Font.PLAIN, 36));
			btnCreateRoom.setBounds((int)(0.125*w),(int)(0.75*h), 200,50);
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

			btnBack = new JButton(backIcon1); //TODO: 뒤로가기는 여러 번 쓰일 가능성 있으니까 따로 빼두는 편이 좋을지도
			btnBack.setRolloverIcon(backIcon2);
			btnBack.setBorderPainted(false);
			btnBack.setContentAreaFilled(false);
			btnBack.setPreferredSize(new Dimension(56,56));
			btnBack.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
			btnBack.setBounds((int)(0.875*w-60),(int)(0.125*h), 60,60);
			btnBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					acm.getFrame().getContentPane().add(acm.getGameTitle()); //게임 타이틀 화면
					lobby.setVisible(false);
				}
			});
			add(btnBack);

	}
		//방 그리는 메소드
		public void drawRoomUI(InteractMsg cm) {
			System.out.println("drawRoomUI 실행");
			String[] roomInfoList = cm.roomInfo.split(",",5); //방번호 방제 방상태 인원수 입장인원1 2 3 4
			int receivedRoomNumber = Integer.parseInt(roomInfoList[0]); //방번호
			String receivedRoomName = roomInfoList[1];//방제
			String receivedRoomStatus = roomInfoList[2];//방상태
			int receivedEnteredUser = Integer.parseInt(roomInfoList[3]);//인원수
			String receivedEnteredUserList = roomInfoList[4];//user1 user2 user3 user4
			System.out.println("drawRoomUI" + receivedRoomNumber +receivedRoomName + receivedRoomStatus + receivedEnteredUser + receivedEnteredUserList);
			RoomUI roomUI = new RoomUI(receivedRoomNumber,receivedRoomName,receivedRoomStatus,receivedEnteredUser); //
			add(roomUI);
		}
		
			public class RoomUI extends JButton{
				int roomNumber;
				String roomName;
				int people;
				int index;
				String roomStatus; //게임중, 준비중
				
				public RoomUI(int roomNumber,String roomName, String roomStatus,int people) { //UI에 방상태 JLabel 추가해야함
					//방 정보 표시할 것들. 서버에서 정보 받아와야함.
					this.roomNumber = roomNumber;
					this.roomName = roomName;
					this.roomStatus = roomStatus;
					this.people = people;
					
					if(roomStatus == "CLOSED" || roomStatus == "STARTED") {
						this.setEnabled(false);
					}
					
					this.setBounds((int)(0.125*w+250),(int)(0.25*h)+roomNumber*100, 770,70);
					lobby.add(this);
					this.setBackground(Color.white);
					this.setLayout(null);
					
					JLabel lblRoomName = new JLabel(roomName);
					lblRoomName.setFont(new Font("배달의민족 도현", Font.PLAIN, 32));
					lblRoomName.setBounds(35,20, 330, 40);
					this.add(lblRoomName);
					
					JLabel lblRoomStatus = new JLabel(roomStatus);
					lblRoomStatus.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
					lblRoomStatus.setBounds(550, 20, 100, 40);
					this.add(lblRoomStatus);
					
					JLabel lblEnteredUsers = new JLabel(Integer.toString(people)); //인원수도 서버에서 넘겨받은 정보로 바꾸기
					lblEnteredUsers.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
					lblEnteredUsers.setBounds(660, 20, 15, 40);
					this.add(lblEnteredUsers);
					
					JLabel lblUnit = new JLabel("/ 4 명");
					lblUnit.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
					lblUnit.setBounds(680, 20, 60, 40);
					this.add(lblUnit);
					
					this.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {

							InteractMsg obcm = new InteractMsg(userName, "300");
							obcm.roomInfo = roomNumber +","+ roomName +","+ roomStatus+"," + people +","+ userName;
							SendObject(obcm);
							System.out.println("게임방 클릭되었음");
						}
					});
					
				}

			}
			
		}
	
	public class GameRoomUI extends JFrame{

		String gameRoomName; //방 이름
		String[] userNameList;
		String[] userPositionList;
		String myName;
		/**
		 * Create the panel.
		 */
		public GameRoomUI(int width,int height, String roomName, String[] userList,String userName) { //게임방 틀
			this.gameRoomName = roomName;
			this.userNameList = userList;
			for(int i=0;i<userList.length;i++) {
				if(i==0)
					this.userPositionList[i] = "M";
				else
					this.userPositionList[i] = "P";
			}
			this.myName = userName;
			
			setSize(width, height);
			getContentPane().setBackground(new Color(0, 128, 255));
			setLayout(null);
			
			JLabel lblRoomName = new JLabel(gameRoomName);
			lblRoomName.setForeground(Color.WHITE);
			lblRoomName.setFont(new Font("배달의민족 도현", Font.PLAIN, 30));
			lblRoomName.setBounds(39, 26, 347, 60);
			add(lblRoomName);
		
			JPanel slot_1 = new JPanel();
			slot_1.setBackground(new Color(213, 234, 255));
			slot_1.setBounds(39, 110, 300, 150);
			add(slot_1);
			slot_1.setLayout(null);
			
			JLabel lblUserPosition = new JLabel("Master");
			lblUserPosition.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
			lblUserPosition.setBounds(12, 10, 80, 20);
			slot_1.add(lblUserPosition);
			
			JLabel lblUseName = new JLabel(userNameList[0]);
			lblUseName.setHorizontalAlignment(SwingConstants.CENTER);
			lblUseName.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
			lblUseName.setBounds(30, 56, 240, 40);
			slot_1.add(lblUseName);
			
			
			JButton btnStartButton = new JButton("START");
			btnStartButton.setBounds(195, 111, 93, 29);
			slot_1.add(btnStartButton);
			btnStartButton.setForeground(new Color(0, 0, 0));
			btnStartButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("멀티 게임이 시작되었습니다.");
				}
			});
			btnStartButton.setBackground(new Color(255, 255, 128));
			btnStartButton.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
			
			if(myName != userNameList[0]) //내가 방장이 아니면 Start버튼 비활성화
				btnStartButton.setEnabled(false);
			
			JPanel slot_2 = new JPanel();
			slot_2.setLayout(null);
			slot_2.setBackground(new Color(213, 234, 255));
			slot_2.setBounds(379, 110, 300, 150);
			add(slot_2);
			
			JLabel lblUserPosition_1 = new JLabel("Player");
			lblUserPosition_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
			lblUserPosition_1.setBounds(12, 10, 80, 20);
			slot_2.add(lblUserPosition_1);
			
			JLabel lblUseName_1 = new JLabel(userNameList[1]);
			lblUseName_1.setHorizontalAlignment(SwingConstants.CENTER);
			lblUseName_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
			lblUseName_1.setBounds(30, 56, 240, 40);
			slot_2.add(lblUseName_1);
			
			JPanel slot_4 = new JPanel();
			slot_4.setLayout(null);
			slot_4.setBackground(new Color(213, 234, 255));
			slot_4.setBounds(379, 299, 300, 150);
			add(slot_4);
			
			JLabel lblUserPosition_2 = new JLabel("Player");
			lblUserPosition_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
			lblUserPosition_2.setBounds(12, 10, 80, 20);
			slot_4.add(lblUserPosition_2);
			
			JLabel lblUseName_2 = new JLabel(userNameList[2]);
			lblUseName_2.setHorizontalAlignment(SwingConstants.CENTER);
			lblUseName_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
			lblUseName_2.setBounds(30, 56, 240, 40);
			slot_4.add(lblUseName_2);

			JPanel slot_3 = new JPanel();
			slot_3.setLayout(null);
			slot_3.setBackground(new Color(213, 234, 255));
			slot_3.setBounds(39, 299, 300, 150);
			add(slot_3);
			
			JLabel lblUserPosition_1_1 = new JLabel("Player");
			lblUserPosition_1_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
			lblUserPosition_1_1.setBounds(12, 10, 80, 20);
			slot_3.add(lblUserPosition_1_1);
			
			JLabel lblUseName_1_1 = new JLabel(userNameList[3]);
			lblUseName_1_1.setHorizontalAlignment(SwingConstants.CENTER);
			lblUseName_1_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
			lblUseName_1_1.setBounds(30, 56, 240, 40);
			slot_3.add(lblUseName_1_1);

			JButton btnBack = new JButton(backIcon1); //TODO: 뒤로가기는 여러 번 쓰일 가능성 있으니까 따로 빼두는 편이 좋을지도
			btnBack.setRolloverIcon(backIcon2);
			btnBack.setBorderPainted(false);
			btnBack.setContentAreaFilled(false);
			btnBack.setPreferredSize(new Dimension(56,56));
			btnBack.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
			btnBack.setBounds((int)(0.875*width-60),(int)(0.125*height), 60,60);
			btnBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { //나가기
					setVisible(false);
					acm.getFrame().getContentPane().add(lobby); //게임 타이틀 화면
					lobby.setVisible(true);
				}
			});
			add(btnBack);
			
			
		}
		
	}
	
	public void drawGameRoomUI(InteractMsg cm) { //서버로부터 받은 InteractMsg cm
		String[] roomInfoList = cm.roomInfo.split(",",5); //방번호 방제 방상태 인원수 입장인원1 2 3 4
		int receivedRoomNumber = Integer.parseInt(roomInfoList[0]); //방번호
		String receivedRoomName = roomInfoList[1];//방제
		String receivedEnteredUserList = roomInfoList[4];//user1/user2/user3/user4
		String[] userList = receivedEnteredUserList.split("/");
		
		//게임방으로 화면 전환
		GameRoomUI gameroomUI = new GameRoomUI(width,height,receivedRoomName,userList,cm.userName);
		gameroomUI.setVisible(true);
		lobby.setVisible(false);
		acm.getFrame().getContentPane().setVisible(false);
	}

}
