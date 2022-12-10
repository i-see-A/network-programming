
import java.io.Serializable;

//InteractMsg. 서버와 클라이언트간 주고받을 Object

class InteractMsg implements Serializable {
	private static final long serialVersionUID = 1L;

	public String userName; // 유저이름
	public String code; // 프로토콜 코드 100: 로그인 200: 게임방 생성
	// 필요에 따라 더 추가

	public String roomName; // create로 방 만들었을 때 방 제목 보내기
	public int roomNum;
	public String[] roomInfo = { null, null }; // 방번호 방제목 방상태 입장인원수
												// 입장인원1 입장인원2 입장인원3 입장인원4
	public int keyCode;
	char keyChar;
	int extendedKeyCode;
	
	public InteractMsg(String userName, String code) {
		this.userName = userName;
		this.code = code;
	}

	public InteractMsg(String userName, int roomNum, String code) {
		this.userName = userName;
		this.roomNum = roomNum;
		this.code = code;
	}

}