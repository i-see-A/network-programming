
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Main extends JPanel{ //여기서 이제 서버랑 통신을 합니다.

	private static final long serialVersionUID = 1L;
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public int width,height;

	Lobby lobby;//= new Lobby();
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
			
			ListenNetwork net = new ListenNetwork();
			net.start();
			//여기 아래에 event랑 그런게 다 있는 것 같은데...?
			
			lobby = new Lobby();
			InteractMsg obcm2 = new InteractMsg(userName, "201"); //sendRoomInfoRequest
			SendObject(obcm2);
			
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob); //이게 null이라고?
		} catch (IOException e) {
			System.out.println("Main.java의 SendObjec 메소드: 메시지 송신 에러");
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
		
						if (obcm instanceof InteractMsg) {
							cm = (InteractMsg) obcm;
							msg = String.format("[%s]\n", cm.userName);
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
						}
					} catch (IOException e) {
						System.out.println("ois.readObject() error");
					}
						try {
							ois.close();
							oos.close();
							socket.close();
							break;
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

		ImageIcon backIcon1 = new ImageIcon("assets/image/back1.png");
		ImageIcon backIcon2 = new ImageIcon("assets/image/back2.png");
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

			JButton btnCreateRoom = new JButton("Create");
			btnCreateRoom.setBackground(new Color(255, 255, 128));
			btnCreateRoom.setForeground(new Color(0, 0, 0));
			btnCreateRoom.setFont(new Font("배달의민족 도현", Font.PLAIN, 36));
			btnCreateRoom.setBounds((int)(0.125*w),(int)(0.75*h), 200,50);
			add(btnCreateRoom);
			btnCreateRoom.addActionListener(new ActionListener() { // create 버튼 클릭 시
				public void actionPerformed(ActionEvent e) {
					String newRoomName;
					newRoomName = (String) JOptionPane.showInputDialog(null, "Room Name");
					InteractMsg obcm = new InteractMsg(userName, "200");
					obcm.roomName = newRoomName;
					SendObject(obcm); //서버에게 방 이름 보내면
				}
			});

			JButton btnBack = new JButton(backIcon1); //TODO: 뒤로가기는 여러 번 쓰일 가능성 있으니까 따로 빼두는 편이 좋을지도
			btnBack.setRolloverIcon(backIcon2);
			btnBack.setBorderPainted(false);
			btnBack.setContentAreaFilled(false);
			btnBack.setPreferredSize(new Dimension(56,56));
			btnBack.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
			btnBack.setBounds((int)(0.875*w-60),(int)(0.125*h), 60,60);
			btnBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					lobby.setVisible(false);
					AtariClientMain acm = new AtariClientMain();
					acm.getFrame().getContentPane().add(acm.getGameTitle()); //게임 타이틀 화면
					//로그아웃되도록...
				}
			});
			add(btnBack);
	}
		//방 그리는 메소드
		public RoomUI drawRoomUI(InteractMsg cm) {
			String[] roomInfoList = cm.roomInfo.split(" ",4); //방제 방상태 인원수 입장인원1 2 3 4
			String receivedRoomName = roomInfoList[0];//방제
			String receivedRoomStatus = roomInfoList[1];//방상태
			int receivedEnteredUser = Integer.parseInt(roomInfoList[2]);//인원수
			String receivedEnteredUserList = roomInfoList[3];//user1 user2 user3 user4
			System.out.println("drawRoomUI" + receivedRoomName + receivedRoomStatus + receivedEnteredUser + receivedEnteredUserList);
			RoomUI roomUI = new RoomUI(receivedRoomName,receivedRoomStatus,receivedEnteredUser); //
			
			return roomUI;
		}

			public class RoomUI extends JButton{
				String roomName;
				int people;
				int index;
				String roomStatus; //게임중, 준비중
				
				public RoomUI(String roomName, String roomStatus,int people) { //UI에 방상태 JLabel 추가해야함
					//방 정보 표시할 것들. 서버에서 정보 받아와야함.
					this.roomName = roomName;
					this.roomStatus = roomStatus;
					this.people = people;
					
					this.setBounds((int)(0.25*w),(int)(0.25*h), 500,50);
					add(this);
					this.setLayout(null);
					this.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							getIntoRoom();//방 클릭 시 해당 게임 방으로 넘어가야함.
							//서버에 이 유저가 방으로 넘어간다는 것을 알려야한다.
						}
					});
					
					JLabel lblRoomName = new JLabel(roomName);
					lblRoomName.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
					lblRoomName.setBounds(12, 10, 187, 40);
					this.add(lblRoomName);
					
					
					JLabel lblEnteredUsers = new JLabel(); //인원수도 서버에서 넘겨받은 정보로 바꾸기
					lblEnteredUsers.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
					lblEnteredUsers.setBounds(591, 10, 12, 40);
					this.add(lblEnteredUsers);
					
					JLabel lblUnit = new JLabel("명");
					lblUnit.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
					lblUnit.setBounds(605, 10, 23, 40);
					this.add(lblUnit);
					
					addActionListener(new ActionListener() { //Room이 클릭이 되면, 게임방으로 들어가게
						public void actionPerformed(ActionEvent e) {
							lobby.setVisible(false); //로비 안보이게 하고
							getIntoRoom(); //게임방으로 넘어간다.
						}
					});
				}
				
				public void updateRoomUI(InteractMsg cm) { //인원수나 방 상태 같은 방 정보가 바뀌는 경우
					
				}
				
				public void getIntoRoom() { //해당 방으로 넘어간다.(Room에서 GameRoom으로)
					//GameRoom으로 넘어간다.
					//인원수가 추가되는 update가 일어나기때문에 updateRoom()
				}
			}
			
		}

	
}
