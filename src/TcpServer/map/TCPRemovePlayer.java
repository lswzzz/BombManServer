package TcpServer.map;

import java.io.IOException;

import InlineSocket.SocketServer;
import TcpServer.map.object.RoomInfo;
import io.netty.channel.ChannelId;
import utils.ByteArray;

public class TCPRemovePlayer {
	public static void PlayerExitGame(int fd) throws IOException{
		SocketServer.Instance.addMessage(sendToClientData(fd));
		GamePlayerMessageMap.remove(fd);
		if(GameRoomMap.containsFdInRoom(fd)){
			int ownerfd = GameRoomMap.getRoomOwnerId(fd);
			RoomInfo room = GameRoomMap.get(ownerfd);
			for (int i = 0; i < room.playerList.size(); i++) {
				if(room.playerList.get(i).id == fd){
					room.playerList.remove(room.playerList.get(i));
					room.currentMan--;
					break;
				}
			}
			GameRoomMap.removeMan(fd);
			if(room.currentMan == 0){
				GameRoomMap.removeRoom(ownerfd);
			}
		}
		NettyChannelMap.remove(fd);
	}
	
	public static void PlayerExitGame(ChannelId channel) throws IOException{
		int fd = NettyChannelMap.get(channel).id;
		PlayerExitGame(fd);
	}
	
	public static byte[] sendToClientData(int fd) throws IOException{
		ByteArray array = new ByteArray();
		array.writeChar((char)2);
		array.writeInt(fd);
		return array.getWritedData();
	}
}
