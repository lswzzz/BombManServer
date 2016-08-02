package TcpUtils;

import InlineSocket.SocketServer;
import TcpServer.map.object.PlayerMessage;
import TcpServer.map.object.RoomInfo;
import game.main.PlayerMapData;
import utils.ByteArray;
import utils.GameTile;
import utils.GameTileMap;

public class GameMapGeneratorTCP {
	
	public static class Player_Game_Init_Info{
		public int fd;
		public float posx;
		public float posy;
		public String posName;
		public Player_Game_Init_Info(){
			
		}
	}
	
	public static Player_Game_Init_Info[] GenerateGameInitInfo(RoomInfo info){
		GameTile gameTile = (GameTile)GameTileMap.get(info.sceneName);
		Integer[] fds = new Integer[info.playerList.size()];
		Player_Game_Init_Info[] infos = new Player_Game_Init_Info[info.playerList.size()];
		for(int i=0; i<info.playerList.size(); i++){
			infos[i] = new Player_Game_Init_Info();
		}
		for(int i=0; i<info.playerList.size(); i++){
			PlayerMessage playerMessage = info.playerList.get(i);
			PlayerMapData data = gameTile.playerList.get(i);
			fds[i] = playerMessage.id;
			infos[i].fd = playerMessage.id;
			infos[i].posx = data.position.x;
			infos[i].posy = data.position.y;
			infos[i].posName = data.posName;
		}
		return infos;
	}
	
	public static void SendUdpClient(RoomInfo info){
		try{
			ByteArray array = new ByteArray();
			char c = 1;
			array.writeChar((char)c);
			array.writeUTFString(info.sceneName);
			array.writeInt(info.roomOwner.id);
			array.writeChar((char)info.playerList.size());
			for(PlayerMessage message : info.playerList){
				array.writeInt(message.id);
			}
			SocketServer.Instance.addMessage(array.getWritedData());
		}catch(Exception e){
			System.out.println("发送数据到UdpClient出错，错误信息是:" + e.getMessage());
		}
		
	}
	
	public static void PlayerReloadScene(int fd){
		try{
			ByteArray array = new ByteArray();
			array.writeChar((char)3);
			array.writeInt(fd);
			SocketServer.Instance.addMessage(array.getWritedData());
		}catch(Exception e){
			System.out.println("发送重新加载场景错误:" + e.getMessage());
		}
	}
}
