
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class AtariClientMain { // 게임타이틀화면
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JTextField txtUserName;
	private GameTitle gameTitle = new GameTitle(); // 게임 제목, 버튼들 들어있는 Jpanel
	final String IP_ADDRESS = "127.0.0.1";
	final String PORT_NUMBER = "30000";

	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice gd = ge.getDefaultScreenDevice();

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
		// create the font to use. Specify the size!
		Font BM_DOHYEON_BOLD = new Font("배달의민족 도현", Font.BOLD, 18);
		Font COOKIE_RUN_BOLD = new Font("CookieRun_Bold", Font.BOLD, 18);

		try {
			Path currentPath = Paths.get(System.getProperty("user.dir"));

			Path dohyeonFontPath = Paths.get(currentPath.toString().replace("AtariBrickBreaker", ""),
					"AtariBrickBreaker", "assets", "fonts",
					"BMDOHYEON.ttf");
			Path cookieRunFontPath = Paths.get(currentPath.toString().replace("AtariBrickBreaker", ""),
					"AtariBrickBreaker", "assets", "fonts",
					"CookieRun_Bold.ttf");

			BM_DOHYEON_BOLD = Font.createFont(Font.TRUETYPE_FONT, dohyeonFontPath.toFile())
					.deriveFont(Font.BOLD, 32f);
			COOKIE_RUN_BOLD = Font.createFont(Font.TRUETYPE_FONT, new File(cookieRunFontPath.toString()))
					.deriveFont(Font.PLAIN, 18f);
			// register the font
			ge.registerFont(BM_DOHYEON_BOLD);
			ge.registerFont(COOKIE_RUN_BOLD);
		} catch (Exception e) {
			e.printStackTrace();
		}

		frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setBackground(new Color(255, 255, 255));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
		frame.setResizable(true);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(gameTitle, BorderLayout.CENTER);
	}

	class GameTitle extends JPanel {
		Font BM_DOHYEON_BOLD = new Font("배달의민족 도현", Font.BOLD, 18);
		Font COOKIE_RUN_BOLD = new Font("CookieRun_Bold", Font.BOLD, 18);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (int) dimension.getWidth();
		int h = (int) dimension.getHeight();

		public GameTitle() {
			// JPanel 게임 제목과, 이름 입력, 버튼 넣을
			setBackground(Color.white);
			setLayout(null);
			setVisible(true);
			setSize(800, 800);

			JLabel lblTitle = new JLabel("ATARI BRICK BREAKER");
			lblTitle.setBounds((int) (0.5 * w - 300), (int) (0.5 * h - 200), 600, 60);
			lblTitle.setForeground(new Color(0, 0, 0));
			lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
			lblTitle.setFont(BM_DOHYEON_BOLD.deriveFont(Font.PLAIN, 52f));
			add(lblTitle);

			JLabel lblPhrase = new JLabel("Enter your name below");
			lblPhrase.setForeground(new Color(0, 128, 255));
			lblPhrase.setFont(BM_DOHYEON_BOLD.deriveFont(Font.PLAIN, 18f));
			lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
			lblPhrase.setBounds((int) (0.5 * w - 109), (int) (0.5 * h - 50), 250, 48);
			add(lblPhrase);

			// UserName 입력
			txtUserName = new JTextField();
			txtUserName.setBounds((int) (0.5 * w - 150), (int) (0.5 * h), 300, 50);
			txtUserName.setColumns(10);
			txtUserName.setFont(BM_DOHYEON_BOLD);
			add(txtUserName);

			// single play
			JButton btnSingle = new JButton("Single");
			btnSingle.setBounds((int) (0.5 * w - 200), (int) (0.5 * h + 150), 150, 40);
			btnSingle.setFont(COOKIE_RUN_BOLD.deriveFont(Font.PLAIN, 24f));
			btnSingle.setBackground(new Color(192, 192, 192));
			add(btnSingle);

			JButton btnMulti = new JButton("Multi");
			btnMulti.setFont(COOKIE_RUN_BOLD.deriveFont(Font.PLAIN, 24f));
			btnMulti.setBackground(Color.LIGHT_GRAY);
			btnMulti.setBounds((int) (0.5 * w + 50), (int) (0.5 * h + 150), 150, 40);
			add(btnMulti);

			Myaction action = new Myaction();
			btnSingle.addActionListener(action);
			btnMulti.addActionListener(action);
		}
	}

	class Myaction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String userName = txtUserName.getText().trim();
			JButton btn = (JButton) e.getSource();

			if (userName.length() == 0) { // userName 입력 안되었을 시
				JOptionPane.showMessageDialog(null, "Enter your name before starting the game");
			} else {
				Main game = new Main(userName, IP_ADDRESS, PORT_NUMBER); // Main 객체가 생성이 되면서, 로그인 정보 서버에 보냄.

				if (btn.getText().equals("Single")) {
					game.panel = new GamePanel();
					frame.getContentPane().add(game.panel);
					frame.setTitle("Atari Break-out");
					frame.setResizable(false);
					frame.setBackground(Color.black);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.pack();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null);

				} else if (btn.getText().equals("Multi")) {
					frame.getContentPane().add(game.lobby);
				}
				gameTitle.setVisible(false);
			}
		}
	}

	public JFrame getFrame() {
		return frame;
	}

	public JTextField getTxtUserName() {
		return txtUserName;
	}

	public GameTitle getGameTitle() {
		return gameTitle;
	}

}
