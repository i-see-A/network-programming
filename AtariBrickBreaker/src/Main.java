import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JFrame;

public class Main extends JFrame{ //여기서 이제 서버랑 통신을 합니다.
	private static final long serialVersionUID = 1L;
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public Main(String userName, String ip_addr, String port_no) { //여기서 서버에다가 보내야 하는거지...
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// SendMessage("/login " + UserName);
			InteractMsg obcm = new InteractMsg(userName, "100");
			SendObject(obcm); //서버에 로그인 정보 보내기

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		JFrame obj = new JFrame();//빈 배경 만들기
		GamePlay gamePlay = new GamePlay(); //게임 플레이 화면 넣어주기
		
		obj.setBounds(10,10,700,600); //background 크기
		obj.setTitle("Atari Break-out");
		obj.setResizable(false); //크기 바꾸는거 disable
		obj.setVisible(true); //보이게
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //ㅡㅁx
		
		obj.add(gamePlay);
	}

}
