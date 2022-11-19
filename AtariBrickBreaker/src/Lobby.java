import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Lobby extends JPanel { //로비, 방 생성 여기서 한다.
	String roomName;
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
		btnCreateRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				roomName = (String)JOptionPane.showInputDialog(null, "Room Name");
				System.out.println(roomName);
			}
		});
		
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("배달의민족 도현", Font.PLAIN, 16));
		btnBack.setBounds(586, 45, 93, 29);
		add(btnBack);
		
		JButton room_1 = new JButton();
		room_1.setBounds(40, 100, 640, 60);
		add(room_1);
		room_1.setLayout(null);
		room_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//방 클릭 시 해당 게임 방으로 넘어가야함.
			}
		});
		
		JLabel lblRoomName = new JLabel("Game Room Name");
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
		
		JPanel room_4 = new JPanel();
		room_4.setLayout(null);
		room_4.setBounds(39, 340, 640, 60);
		add(room_4);
		
		JLabel lblRoomName_1 = new JLabel("Game Room Name");
		lblRoomName_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
		lblRoomName_1.setBounds(12, 10, 187, 40);
		room_4.add(lblRoomName_1);
		
		JLabel lblEnteredUsers_1 = new JLabel("0");
		lblEnteredUsers_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
		lblEnteredUsers_1.setBounds(591, 10, 12, 40);
		room_4.add(lblEnteredUsers_1);
		
		JLabel lblUnit_1 = new JLabel("명");
		lblUnit_1.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
		lblUnit_1.setBounds(605, 10, 23, 40);
		room_4.add(lblUnit_1);
		
		JPanel room_3 = new JPanel();
		room_3.setLayout(null);
		room_3.setBounds(39, 260, 640, 60);
		add(room_3);
		
		JLabel lblRoomName_2 = new JLabel("Game Room Name");
		lblRoomName_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
		lblRoomName_2.setBounds(12, 10, 187, 40);
		room_3.add(lblRoomName_2);
		
		JLabel lblEnteredUsers_2 = new JLabel("0");
		lblEnteredUsers_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
		lblEnteredUsers_2.setBounds(591, 10, 12, 40);
		room_3.add(lblEnteredUsers_2);
		
		JLabel lblUnit_2 = new JLabel("명");
		lblUnit_2.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
		lblUnit_2.setBounds(605, 10, 23, 40);
		room_3.add(lblUnit_2);
		
		JPanel room_2 = new JPanel();
		room_2.setLayout(null);
		room_2.setBounds(39, 180, 640, 60);
		add(room_2);
		
		JLabel lblRoomName_3 = new JLabel("Game Room Name");
		lblRoomName_3.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
		lblRoomName_3.setBounds(12, 10, 187, 40);
		room_2.add(lblRoomName_3);
		
		JLabel lblEnteredUsers_3 = new JLabel("0");
		lblEnteredUsers_3.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
		lblEnteredUsers_3.setBounds(591, 10, 12, 40);
		room_2.add(lblEnteredUsers_3);
		
		JLabel lblUnit_3 = new JLabel("명");
		lblUnit_3.setFont(new Font("배달의민족 도현", Font.PLAIN, 18));
		lblUnit_3.setBounds(605, 10, 23, 40);
		room_2.add(lblUnit_3);

	}
}
