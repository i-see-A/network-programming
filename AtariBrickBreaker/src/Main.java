import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		//빈 배경 만들기
		JFrame obj = new JFrame();
		GamePlay gamePlay = new GamePlay(); //게임 플레이 화면 넣어주기
		
		obj.setBounds(10,10,700,600); //background 크기
		obj.setTitle("Atari Break-out");
		obj.setResizable(false); //크기 바꾸는거 disable
		obj.setVisible(true); //보이게
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //ㅡㅁx
		
		obj.add(gamePlay);
	}

}
