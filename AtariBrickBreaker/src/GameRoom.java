

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class GameRoom extends JPanel {
	
	

	/**
	 * Create the panel.
	 */
	public GameRoom() { //게임방 틀
		setBackground(new Color(0, 128, 255));
		setLayout(null);
		
		JLabel lblRoomName = new JLabel("Game Room Name");
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
		
		JLabel lblUseName = new JLabel("닉네임");
		lblUseName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUseName.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
		lblUseName.setBounds(30, 56, 240, 40);
		slot_1.add(lblUseName);
		
		JButton btnUserButton = new JButton("START");
		btnUserButton.setBounds(195, 111, 93, 29);
		slot_1.add(btnUserButton);
		btnUserButton.setForeground(new Color(0, 0, 0));
		btnUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnUserButton.setBackground(new Color(255, 255, 128));
		btnUserButton.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
		
		JPanel slot_2 = new JPanel();
		slot_2.setLayout(null);
		slot_2.setBackground(new Color(213, 234, 255));
		slot_2.setBounds(379, 110, 300, 150);
		add(slot_2);
		
		JLabel lblUserPosition_1 = new JLabel("Player");
		lblUserPosition_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
		lblUserPosition_1.setBounds(12, 10, 80, 20);
		slot_2.add(lblUserPosition_1);
		
		JLabel lblUseName_1 = new JLabel("닉네임");
		lblUseName_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblUseName_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
		lblUseName_1.setBounds(30, 56, 240, 40);
		slot_2.add(lblUseName_1);
		
		JButton btnUserButton_1 = new JButton("READY");
		btnUserButton_1.setForeground(Color.BLACK);
		btnUserButton_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
		btnUserButton_1.setBackground(new Color(255, 255, 128));
		btnUserButton_1.setBounds(195, 111, 93, 29);
		slot_2.add(btnUserButton_1);
		
		JPanel slot_4 = new JPanel();
		slot_4.setLayout(null);
		slot_4.setBackground(new Color(213, 234, 255));
		slot_4.setBounds(379, 299, 300, 150);
		add(slot_4);
		
		JLabel lblUserPosition_2 = new JLabel("Player");
		lblUserPosition_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
		lblUserPosition_2.setBounds(12, 10, 80, 20);
		slot_4.add(lblUserPosition_2);
		
		JLabel lblUseName_2 = new JLabel("닉네임");
		lblUseName_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblUseName_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
		lblUseName_2.setBounds(30, 56, 240, 40);
		slot_4.add(lblUseName_2);
		
		JButton btnUserButton_2 = new JButton("READY");
		btnUserButton_2.setForeground(Color.BLACK);
		btnUserButton_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
		btnUserButton_2.setBackground(new Color(255, 255, 128));
		btnUserButton_2.setBounds(195, 111, 93, 29);
		slot_4.add(btnUserButton_2);
		
		JPanel slot_3 = new JPanel();
		slot_3.setLayout(null);
		slot_3.setBackground(new Color(213, 234, 255));
		slot_3.setBounds(39, 299, 300, 150);
		add(slot_3);
		
		JLabel lblUserPosition_1_1 = new JLabel("Player");
		lblUserPosition_1_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
		lblUserPosition_1_1.setBounds(12, 10, 80, 20);
		slot_3.add(lblUserPosition_1_1);
		
		JLabel lblUseName_1_1 = new JLabel("닉네임");
		lblUseName_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblUseName_1_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 24));
		lblUseName_1_1.setBounds(30, 56, 240, 40);
		slot_3.add(lblUseName_1_1);
		
		JButton btnUserButton_1_1 = new JButton("READY");
		btnUserButton_1_1.setForeground(Color.BLACK);
		btnUserButton_1_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
		btnUserButton_1_1.setBackground(new Color(255, 255, 128));
		btnUserButton_1_1.setBounds(195, 111, 93, 29);
		slot_3.add(btnUserButton_1_1);
		
		JButton btnBack = new JButton("Back");
		btnBack.setForeground(Color.BLACK);
		btnBack.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
		btnBack.setBackground(new Color(255, 255, 128));
		btnBack.setBounds(586, 45, 93, 29);
		add(btnBack);
		
		
	}
	
}
