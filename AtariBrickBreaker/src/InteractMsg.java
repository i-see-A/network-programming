
import java.io.Serializable;

//InteractMsg. 서버와 클라이언트간 주고받을 Object

class InteractMsg implements Serializable {
	private static final long serialVersionUID = 1L;

	public String userName; // 유저이름
	public String code; // 프로토콜 코드 100: 로그인 200: 게임방 생성
	// 필요에 따라 더 추가

	public String roomName; //create로 방 만들었을 때 방 제목 보내기
	public String roomInfo; //방번호(room_vc의 몇번째인지,이거 추가해야함. UI때문에라도 해야함) 방제목 방상태 입장인원수 입장인원1 입장인원2 입장인원3 입장인원4
	public InteractMsg(String userName, String code) {
		this.userName = userName;
		this.code = code;
	}

}