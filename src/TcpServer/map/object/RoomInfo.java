package TcpServer.map.object;

import java.util.List;

public class RoomInfo {
	public int maxMan;
	public int currentMan;
	public String roomName;
	public List<PlayerMessage> playerList;
	public PlayerMessage roomOwner;
	public String sceneName;
}
