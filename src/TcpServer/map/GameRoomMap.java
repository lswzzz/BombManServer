package TcpServer.map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import TcpServer.map.object.PlayerMessage;
import TcpServer.map.object.RoomInfo;

public class GameRoomMap {
	public static Map<Integer, RoomInfo> map = new ConcurrentHashMap<Integer, RoomInfo>();
	public static Map<Integer, Integer> fdMap = new ConcurrentHashMap<Integer, Integer>();
	public static void add(Integer id, RoomInfo info){
		map.put(id, info);
		int manCount = info.playerList.size();
		for(int i=0; i<manCount; i++){
			PlayerMessage player = info.playerList.get(i);
			fdMap.put(player.id, id);
		}
	}
	
	public static void reset(Integer id){
		RoomInfo info = map.get(id);
		int manCount = info.playerList.size();
		for(int i=0; i<manCount; i++){
			PlayerMessage player = info.playerList.get(i);
			fdMap.put(player.id, id);
		}
	}
	
	public static void removeMan(Integer id){
		fdMap.remove(id);
	}
	
	public static void removeRoom(Integer id){
		RoomInfo room = map.get(id);
		int manCount = room.playerList.size();
		for(int i=0; i<manCount; i++){
			PlayerMessage player = room.playerList.get(i);
			fdMap.remove(player.id);
		}
		map.remove(id);
	}
	
	public static RoomInfo get(Integer id){
		return map.get(id);
	}
	
	public static Integer getRoomOwnerId(Integer id){
		return fdMap.get(id);
	}
	
	//判断一个玩家是否在房间中
	public static boolean containsFdInRoom(Integer fd){
		if(fdMap.containsKey(fd))return true;
		return false;
	}
	
	public static void clear(){
		map.clear();
		fdMap.clear();
	}


}
