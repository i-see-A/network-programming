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

	Lobby lobby = new Lobby();
	GamePanel panel;

	String userName;
	
	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			System.out.println("Main.java의 SendObjec 메소드: 메시지 송신 에러");
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

			System.out.println("로비 생성자 시작");
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
					SendObject(obcm);
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
					AtariClientMain acm = new AtariClientMain();
					acm.getFrame().getContentPane().add(acm.getGameTitle()); //게임 타이틀 화면
				}
			});
			add(btnBack);


		 //TODO:해당 방으로 넘어간다. 입장한 유저 정보를 서버에 보내고, 방정보도 update...인원수 추가되었으니까
			// GameRoom으로 넘어간다.
			// 인원수가 추가되는 update가 일어나기때문에 updateRoom()
			
			//로비에 들어가면 유저 정보(내 정보) 보이게 JLabel 추가하기
			JLabel lblUserInfo = new JLabel("YOU");
			JLabel lblUserName = new JLabel(userName);
			
			//서버에 방정보 요청			
//			InteractMsg obcm = new InteractMsg(userName, "201");
//			SendObject(obcm); //로비에 방 그려야하니까 서버에 방 정보 요청하기
			
			//방 그리기
//			for(int i = 0;i<4;i++) { //최대 4개까지만 생성 가능
//				drawRoom();
//			}
			
	 //생성자 종료
	}
		//방 그리는 메소드
		public Room drawRoom() {
			Room room = new Room("",0,0); //
			
			return room;
		}
			//로비에 보이는 방 하나
			public class Room extends JButton{
				String roomName;
				int people;
				int index;
				String roomStatus; //게임중, 준비중
				
				public Room(String roomName, int people, int index) { //방 정보 표시할 것들. 서버에서 정보 받아와야함.
					this.roomName = roomName;
					this.people = people;
					this.index = index;
					
					this.setBounds((int)(0.25*w),(int)(0.25*h), 500,50);
					add(this);
					this.setLayout(null);
					this.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							getIntoRoom();//방 클릭 시 해당 게임 방으로 넘어가야함.
						}
					});
					
					JLabel lblRoomName = new JLabel(roomName);
					lblRoomName.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
					lblRoomName.setBounds(12, 10, 187, 40);
					this.add(lblRoomName);
					
					JLabel lblEnteredUsers = new JLabel("0"); //인원수도 서버에서 넘겨받은 정보로 바꾸기
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
				
				public void updateRoom(InteractMsg cm) { //인원수나 방 상태 같은 방 정보가 바뀌는 경우

				}
				
				public void getIntoRoom() { //해당 방으로 넘어간다.(Room에서 GameRoom으로)
					//GameRoom으로 넘어간다.
					//인원수가 추가되는 update가 일어나기때문에 updateRoom()
				}
			}
			
		}

	public Main(String userName, String ip_addr, String port_no) {
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			InteractMsg obcm = new InteractMsg(userName, "100");
			SendObject(obcm); // 서버에 로그인 정보 보내기
			this.userName = userName;

			ListenNetwork net = new ListenNetwork();
			net.start();
		} catch (NumberFormatException | IOException e) {
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
		
						if (obcm instanceof InteractMsg) {
							cm = (InteractMsg) obcm;
							msg = String.format("[%s]\n", cm.userName);
						} else
							continue;
						switch (cm.code) {
						case "200": // 서버로부터 게임방 응답 오면, 방 그리기.
							//게임방이 생성되었다는 메시지만 띄우기
							break;
						case "201": //서버로부터 로비에 표시할 방 정보들이 오면
							//lobby에 방 정보 표시하고, 방 활성화(?)
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

			
		//방 그리는 메소드
		public Room drawRoom() {
			Room room = new Room("",0,0); //
			
			return room;
		}
			//로비에 보이는 방 하나, 각각은 JPanel 위에 버튼, 위에 JLabel? class Lobby의 서브클래스 Room
			public class Room extends JButton{ //lobby에 입장할 때...먼저 방정보를 로딩해야되겠구나...
				String roomName;
				int people;
				int index;
				String roomStatus; //게임중, 준비중
				
				public Room(String roomName, int people, int index) { //방 정보 표시할 것들. 서버에서 정보 받아와야함.
					this.roomName = roomName;
					this.people = people;
					this.index = index;
					
					this.setBounds((int)(0.25*width),(int)(0.25*height), 500,50);
					add(this);
					this.setLayout(null);
					this.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							getIntoRoom();//방 클릭 시 해당 게임 방으로 넘어가야함.
						}
					});
					
					JLabel lblRoomName = new JLabel(roomName);
					lblRoomName.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
					lblRoomName.setBounds(12, 10, 187, 40);
					this.add(lblRoomName);
					
					JLabel lblEnteredUsers = new JLabel("0");
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
				
				public void updateRoom(InteractMsg cm) { //인원수나 방 상태 같은 방 정보가 바뀌는 경우
					//ob에는 방에 대한 정보가 들어가있다.
					//G
				}
				
				public void getIntoRoom() { //해당 방으로 넘어간다.(Room에서 GameRoom으로) 입장한 유저 정보를 서버에 보내고, 방정보도 update...인원수 추가되었으니까
					//GameRoom으로 넘어간다.
					//인원수가 추가되는 update가 일어나기때문에 updateRoom()
				}
			}
			

		}
