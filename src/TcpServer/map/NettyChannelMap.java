package TcpServer.map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import TcpServer.map.object.ChannelSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

public class NettyChannelMap {
	private static Map<Integer, ChannelSession> idMap = new ConcurrentHashMap<Integer, ChannelSession>();
	private static Map<ChannelId, ChannelSession> channelIdMap = new ConcurrentHashMap<ChannelId, ChannelSession>();

	public static void add(ChannelId id, ChannelSession session){
		idMap.put(session.id, session);
		channelIdMap.put(id, session);
	}
	
	public static ChannelSession get(ChannelId id){
		return channelIdMap.get(id);
	}
	
	public static ChannelSession get(Integer id){
		return idMap.get(id);
	}
	
	public static void remove(ChannelId id){
		ChannelSession session = channelIdMap.get(id);
		idMap.remove(session.id);
		channelIdMap.remove(id);
	}
	
	public static void remove(Integer id){
		ChannelSession session = idMap.get(id);
		idMap.remove(session.id);
		channelIdMap.remove(session.channel.id());
	}
	
	public static void clear(){
		idMap.clear();
		channelIdMap.clear();
	}
}
