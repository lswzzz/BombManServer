package TcpServer;

import TcpServer.map.NettyChannelMap;
import TcpServer.map.TCPRemovePlayer;
import TcpServer.map.object.ChannelSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import proto.clientproto.ClientProtocol.ProtocolNetRequest;
import proto.serverproto.ServerProtocol.ProtocolNetResponse;

public class GameProtobufHandler extends ChannelInboundHandlerAdapter{
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		ChannelSession session = new ChannelSession(channel);
		NettyChannelMap.add(channel.id(), session);
        System.out.println("Client:"+channel.remoteAddress() +" comming");
    }
	
	@Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		if(channel == null)System.out.println("error，channel id is null");
		TCPRemovePlayer.PlayerExitGame(channel.id());
		channel.close();
        System.out.println("Client:"+channel.remoteAddress() +"removed");
    }
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Channel channel = ctx.channel();
		ProtocolNetRequest req = (ProtocolNetRequest)msg;
		DispatchRequest dispathReq = new DispatchRequest();
		if(dispathReq.NeedDeal(req)){
			ProtocolNetResponse resp = dispathReq.Dispatch(req, channel.id());
			if(dispathReq.roomFds == null){
				ctx.writeAndFlush(resp); 
			}else{
				MulticastController controller = new MulticastController();
				controller.fds = dispathReq.roomFds;
				if(dispathReq.dontReport)controller.exclude = NettyChannelMap.get(channel.id()).id;
				controller.msg = resp;
				controller.multicast();
			}
		}
		
		
	}   
	
	//这个触发的条件是发送数据或接受数据的时候发生异常
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("Unexpected exception from downstream." +  cause);
		//在异常调用发生后调用了CLOSE_ON_FAILURE后netty会调用handlerRemoved
		//可以选择不关闭，但这里设置了关闭
		ProtocolNetResponse.Builder builder = ProtocolNetResponse.newBuilder();
		builder.setCmd(ProtocolCmdType.FORCEEXITGAME.value());
		ctx.writeAndFlush(builder.build()).addListener(ChannelFutureListener.CLOSE);
    }
	
}