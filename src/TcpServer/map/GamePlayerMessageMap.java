package TcpServer.map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import TcpServer.map.object.PlayerMessage;

public class GamePlayerMessageMap {
	public static Map<Integer, PlayerMessage> map = new ConcurrentHashMap<Integer, PlayerMessage>();
	public static void add(int id, PlayerMessage message){
		map.put(id, message);
	}
	
	public static void remove(int id){
		map.remove(id);
	}
	
	public static PlayerMessage get(int id){
		return map.get(id);
	}
	
	public static void clear(){
		map.clear();
	}
}
