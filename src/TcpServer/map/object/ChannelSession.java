package TcpServer.map.object;

import io.netty.channel.Channel;

public class ChannelSession {
	private static Object lock=new Object();
	public static int maxId = 0;
	
	public int id;
	public Channel channel;
	
	public ChannelSession(Channel channel){
		//lock�Ļ���ʾ���۶��ٸ�ChannelSessionһ��ʱ��Ƭ��ֻ����һ���������synchronized
		//this�Ļ���ʾ���ж������ͬʱ����ChannelSession��һ��ʵ����ʱ��һ��ʱ��Ƭ��ֻ��һ�������ܵ���
		synchronized (lock) {
			++maxId;
			this.id = maxId;
		}
		this.channel = channel;
	}
}
