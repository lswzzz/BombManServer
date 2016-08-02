package TcpServer;

import TcpServer.map.NettyChannelMap;
import TcpServer.map.object.ChannelSession;
import proto.serverproto.ServerProtocol.ProtocolNetResponse;

public class MulticastController {
	public int[] fds;
	public int exclude;
	public ProtocolNetResponse msg;
	
	public MulticastController(){
		fds = null;
		exclude = -1;
		msg = null;
	}
	
	public void multicast(){
		for(int i=0; i<fds.length; i++){
			int fd = fds[i];
			if(fd == exclude)continue;
			ChannelSession session = NettyChannelMap.get(fd);
			session.channel.writeAndFlush(msg);
		}
	}
}
