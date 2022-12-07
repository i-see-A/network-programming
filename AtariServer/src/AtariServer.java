

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;


public class AtariServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector<UserService> UserVec = new Vector<UserService>(); // 연결된 사용자를 저장할 벡터
	private Vector<AtariServer.RoomManager.GameRoom> RoomVec = new Vector<AtariServer.RoomManager.GameRoom>(); // 생성된 방을 저장할 벡터
	private RoomManager roomManager = new RoomManager(); //방 관리자
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AtariServer frame = new AtariServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AtariServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Atari Server Running..");
				btnServerStart.setText("Atari Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public void AppendText(String str) {
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(InteractMsg msg) {
		textArea.append("code = " + msg.code + "\n");
		textArea.append("username = " + msg.userName + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread { // 한명 한명의 유저
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector<UserService> user_vc;
		public String userName = ""; // 타이틀 화면에서 입력한 이름
		
		private Vector<AtariServer.RoomManager.GameRoom> room_vc;
		public String UserStatus;
		public int roomNumber; //몇 번째 방에 들어가있는지

		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {

				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());

			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		public void Login() {
			AppendText("새로운 참가자 " + userName + " 입장.");
			String msg = "[" + userName + "]님이 입장 하였습니다.\n";
			WriteOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다.
		}

		public void Logout() {
			String msg = "[" + userName + "]님이 퇴장 하였습니다.\n";
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			WriteAll(msg); // 나를 제외한 다른 User들에게 전송
			AppendText("사용자 " + "[" + userName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}
		
		//유저는 방에 들어가
		public void enterRoom() {
			
		}
		
		//유저는 게임을 시작할수있어

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOne(str);
			}
		}

		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOneObject(ob);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.UserStatus == "O")
					user.WriteOne(str);
			}
		}

		// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
		public byte[] MakePacket(String msg) {
			byte[] packet = new byte[BUF_LEN];
			byte[] bb = null;
			int i;
			for (i = 0; i < BUF_LEN; i++)
				packet[i] = 0;
			try {
				bb = msg.getBytes("euc-kr");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public void WriteOne(String msg) {
			try {
				InteractMsg obcm = new InteractMsg("SERVER", "400");
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		// 귓속말 전송
		public void WritePrivate(String msg) {
			try {
				InteractMsg obcm = new InteractMsg("귓속말", "401");
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		public void WriteOneObject(Object ob) {
			try {
				oos.writeObject(ob);
			} catch (IOException e) {
				AppendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}

		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					Object obcm = null;
					String msg = null;
					InteractMsg cm = null;
					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null) {
						break;
					}
					if (obcm instanceof InteractMsg) {
						cm = (InteractMsg) obcm;
						AppendObject(cm);
					} else
						continue;

					switch(cm.code) {
					case "100" : //Login(), 초기화면에서 본인 이름 입력했을 때
						userName = cm.userName;
						UserStatus = "O"; // Online 상태
						Login();
						break; 
					case "200" : //클라이언트에서 create 버튼으로 서버에 방 생성 요청
						 // 서버에서 게임방 하나 생성
						roomManager.createGameRoom(cm);
						msg = String.format("[CODE %s] %s(이)가 게임방 %s 생성.", cm.code, cm.userName, cm.roomName);
						AppendText(msg);
						if (roomManager.room_vc.size() <= 4) {
							for(int i = 0;i<roomManager.room_vc.size();i++) {
								//oos 통신 시 Vector, List,Array로 통신하면 안된다고 함. String으로 받고 split으로 나눠야함.
								int newEnteredUser = 0; //방에 들어온 인원
								String newRoomStatus = ""; //방 상태
								String newUserInfo = ""; //방에 있는 유저들 이름 인원1 인원2 인원3 인원4
								String newRoomName = ""; //방이름
								int newRoomNumber; //방 번호
								if(roomManager.room_vc.elementAt(i) == null) {
									System.out.println("룸벡터 null");
									break;
								}
								else {
									newRoomNumber = i;
									newRoomName = roomManager.room_vc.elementAt(i).roomName;
									newRoomStatus = roomManager.room_vc.elementAt(i).roomStatus;
									newEnteredUser = (int)roomManager.room_vc.elementAt(i).enteredUser;
									for(int j=0;j<4;j++) {
										if( roomManager.room_vc.elementAt(i).currentUserList == null )
											break;
										newUserInfo = roomManager.room_vc.elementAt(i).currentUserList;
									}
									
									cm.roomInfo =  newRoomNumber+","+newRoomName+"," + newRoomStatus+"," + Integer.toString(newEnteredUser)+"," + newUserInfo;
								}
								WriteAllObject(cm); //여기서 보내는 cm은 방 정보가 되어야한다...
							}
							msg = String.format("[CODE %s] %s에게 전체 방 정보를 보냈습니다.\n", cm.code, cm.userName);
							AppendText(msg);
						} else {
							msg = String.format("[CODE %s] 방은 최대 4개까지만 생성할 수 있습니다.\n", cm.code);
							AppendText(msg);
						}
							
						break;
					case "201" : //클라이언트에서 lobby로 진입 시, 서버에 전체 방 정보 요청
						msg = String.format("[CODE %s] %s(이)가 로비에 입장했습니다.\n", cm.code, cm.userName);
						AppendText(msg);
							for(int i = 0;i<roomManager.room_vc.size();i++) {
								//oos 통신 시 Vector, List,Array로 통신하면 안된다고 함. String으로 받고 split으로 나눠야함.
								int newEnteredUser = 0; //방에 들어온 인원
								String newRoomStatus = ""; //방 상태
								String newUserInfo = ""; //방에 있는 유저들 이름 인원1 인원2 인원3 인원4
								String newRoomName = ""; //방이름
								int newRoomNumber; //방 번호
								if(roomManager.room_vc.elementAt(i) == null) {
									System.out.println("룸벡터 null");
									break;
								}
								else {
									newRoomNumber = i;
									newRoomName = roomManager.room_vc.elementAt(i).roomName;
									newRoomStatus = roomManager.room_vc.elementAt(i).roomStatus;
									newEnteredUser = (int)roomManager.room_vc.elementAt(i).enteredUser;
									for(int j=0;j<4;j++) {
										if( roomManager.room_vc.elementAt(i).currentUserList == null )
											continue;
										newUserInfo = roomManager.room_vc.elementAt(i).currentUserList;
									}
									
									cm.roomInfo =  Integer.toString(newRoomNumber) + "," + newRoomName+ "," + newRoomStatus + "," + Integer.toString(newEnteredUser) + "," + newUserInfo;
								}
								System.out.println("서버" + cm.roomInfo);
								WriteOneObject(cm); //여기서 보내는 cm은 방 정보가 되어야한다
							}
							System.out.println("Server 201: "+cm.roomInfo + cm.roomName);
								msg = String.format("[CODE %s] %s에게 전체 방 정보를 보냈습니다.\n", cm.code, cm.userName);
								AppendText(msg);
						break;
					case "300" : //클라이언트가 게임방에 입장해서 유저정보,방 정보 업데이트 후 다시 클라이언트에게 보낸다.
						
						String[] roomInfoList = cm.roomInfo.split(",",5); //방번호 방제 방상태 인원수 입장인원1 2 3 4
						
						int newRoomNumber = Integer.parseInt(roomInfoList[0]); //방번호
						String newRoomName = roomInfoList[1];//방제
						String newRoomStatus = roomInfoList[2];//방상태
						int newEnteredUser = Integer.parseInt(roomInfoList[3]);//인원수
						String newEnteredUserName = roomInfoList[4];//게임방으로 들어온 유저 이름
						
						System.out.println("SERVER 300 입니다"+newRoomNumber+ newRoomName+newRoomStatus+newEnteredUser+newEnteredUserName);
						
						for(int i = 0;i<roomManager.room_vc.size();i++) {
							//oos 통신 시 Vector, List,Array로 통신하면 안된다고 함. String으로 받고 split으로 나눠야함.
							if(roomManager.room_vc.elementAt(i) == null) {
								System.out.println("룸벡터 null");
								break;
							}
							else {
								if( newRoomNumber == i ) { //요청 들어온 방 번호로 룸벡터에서 방 찾고
									newEnteredUser = newEnteredUser + 1; //유저 수 업데이트
									roomManager.room_vc.elementAt(i).enteredUser = newEnteredUser;
									if(newEnteredUser == 4) {
										newRoomStatus = "CLOSED"; //방상태 업데이트
										roomManager.room_vc.elementAt(i).roomStatus = newRoomStatus;
									}
									for(int j=0;j<user_vc.size();j++) { //유저 벡터 업데이트
										if( user_vc.elementAt(j).userName == newEnteredUserName) {
											roomManager.room_vc.elementAt(i).currentUserList = roomManager.room_vc.elementAt(i).currentUserList + newEnteredUserName+"/";
											user_vc.elementAt(j).roomNumber = newRoomNumber; //유저벡터에 입장한 방번호 입력
											String[] userList = roomManager.room_vc.elementAt(i).currentUserList.split("/");
											for(int k=0;k<userList.length;k++) {
												System.out.println("server 300 게임방 유저이름: "+userList[k]);
												if(userList[0] == user_vc.elementAt(j).userName) //요청 들어온 유저의 이름이 게임방 유저리스트 맨 처음이면
													user_vc.elementAt(j).UserStatus = "M"; //Master 방장
												else
													user_vc.elementAt(j).UserStatus = "R"; //Regular 일반
											}
											
										}
									}
									

									
									}
								}

						}
						
						WriteAllObject(cm); //업데이트된 방 정보를 모두에게 보낸다.
						msg = String.format("[CODE %s] %s(이)가 게임방 %s에 입장했습니다. 현재 인원은 %s명.\n", cm.code, cm.userName, newRoomName, Integer.toString(newEnteredUser));
						AppendText(msg);
						
					case "400" :
						Logout();
						break;
					default:
						WriteAllObject(cm);
					}

				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}
	
	

	public class RoomManager { // 방 관리
		private Vector<GameRoom> room_vc;
		private GameRoom gameRoom;
		
		public RoomManager() {
			room_vc = new Vector<GameRoom>();
		}
		
		//방 관리자는 게임방을 생성한다.
		public void createGameRoom(InteractMsg cm) {
			gameRoom = new GameRoom(cm);
			room_vc.add(gameRoom);
		}
		
		//게임방 삭제
		public void removeGameGroom(InteractMsg cm) {
			for(int i=0;i<room_vc.size();i++) {
				if (cm.roomName == room_vc.elementAt(i).roomName ) {
					room_vc.remove(i);
				}
			}
			
		}
		
		//방 관리자가 관리하는 게임방 하나하나의 클래스
		public class GameRoom{
			//방
			private String roomName; //방 이름
			public String roomStatus; // 방 상태 "OPENED" 입장가능 , "STARTED" 게임 시작함 , "CLOSED" 인원이 다 참
			
			//입장한 유저관련
			private final int maxUser = 4; // 최대 4명까지만
			private int enteredUser; //현재 입장한 유저 명수
			private String currentUserList; // 현재 방에 입장한 유저 목록

			public GameRoom(InteractMsg cm) {
				this.roomName = cm.roomName;
				this.roomStatus = "OPENED";
				this.enteredUser = 0;
				this.currentUserList = "";
			}

		}
		

	}

}
