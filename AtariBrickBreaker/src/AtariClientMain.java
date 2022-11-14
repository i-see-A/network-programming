import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class AtariClientMain { //게임타이틀화면

	private JFrame frame;
	private JTextField txtUserName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AtariClientMain window = new AtariClientMain();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AtariClientMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setBackground(new Color(255, 255, 255));
		frame.setBounds(100, 100, 720, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTitle = new JLabel("Atari Brick Breaker");
		lblTitle.setBounds(147, 10, 414, 263);
		lblTitle.setForeground(new Color(0, 0, 0));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("배달의민족 도현", Font.BOLD, 35));
		frame.getContentPane().add(lblTitle);
		
		JLabel lblPhrase = new JLabel("Enter your name below");
		lblPhrase.setForeground(new Color(0, 128, 255));
		lblPhrase.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
		lblPhrase.setBounds(254, 207, 225, 30);
		frame.getContentPane().add(lblPhrase);
		
		//UserName 입력
		txtUserName = new JTextField();
		txtUserName.setBounds(247, 234, 232, 41);
		frame.getContentPane().add(txtUserName);
		txtUserName.setColumns(10);
		
		//single play
		JButton btnSingle = new JButton("Single");
		btnSingle.setBounds(167, 338, 157, 41);
		btnSingle.setVerticalAlignment(SwingConstants.TOP);
		btnSingle.setFont(new Font("CookieRun Bold", Font.PLAIN, 24));
		btnSingle.setBackground(new Color(192, 192, 192));
		Myaction action = new Myaction();
		btnSingle.addActionListener(action);
		frame.getContentPane().add(btnSingle);
		
		JButton btnMulti = new JButton("Multi");
		btnMulti.setVerticalAlignment(SwingConstants.TOP);
		btnMulti.setFont(new Font("CookieRun Bold", Font.PLAIN, 24));
		btnMulti.setBackground(Color.LIGHT_GRAY);
		btnMulti.setBounds(386, 338, 157, 41);
		
		frame.getContentPane().add(btnMulti);
		
		
	}
	
	class Myaction implements ActionListener { //유저 입력 받으면 서버로, singleButton 누르면 싱글플레이.

		@Override
		public void actionPerformed(ActionEvent e) {
			Main game = new Main(); //Atari single game play

		}
		
	}

}
