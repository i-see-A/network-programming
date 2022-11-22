import java.io.Serializable;

//InteractMsg. 서버와 클라이언트간 주고받을 Object

class InteractMsg implements Serializable {
	private static final long serialVersionUID = 1L;

	public String userName; // 유저이름
	public String code; // 프로토콜 코드 100: 로그인
	// 필요에 따라 더 추가
	public String roomName; //게임방 이름

	public InteractMsg(String userName, String code) {
		this.userName = userName;
		this.code = code;
	}

}