package TcpServer.map.object;

import io.netty.channel.Channel;

public class ChannelSession {
	private static Object lock=new Object();
	public static int maxId = 0;
	
	public int id;
	public Channel channel;
	
	public ChannelSession(Channel channel){
		//lock的话表示无论多少个ChannelSession一个时间片内只能有一个对象调用synchronized
		//this的话表示当有多个对象同时调用ChannelSession的一个实例的时候，一个时间片内只有一个对象能调用
		synchronized (lock) {
			++maxId;
			this.id = maxId;
		}
		this.channel = channel;
	}
}
