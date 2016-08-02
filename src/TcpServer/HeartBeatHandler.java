package TcpServer;

import TcpServer.map.NettyChannelMap;
import TcpServer.map.TCPRemovePlayer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import proto.serverproto.ServerProtocol.ProtocolNetResponse;

//http://www.oschina.net/question/139577_146101
public class HeartBeatHandler extends ChannelInboundHandlerAdapter{
	
	//ALL_IDLE : һ��ʱ����û�����ݽ��ջ��߷���
	//READER_IDLE �� һ��ʱ����û�����ݽ���
	//WRITER_IDLE �� һ��ʱ����û�����ݷ���
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent event = (IdleStateEvent)evt;
			//û�����ݽ��յ� ֱ�ӶϿ�����
			if(event.state().equals(IdleState.READER_IDLE)){
				System.out.println("READER_IDLE");
				TCPRemovePlayer.PlayerExitGame(ctx.channel().id());
				ProtocolNetResponse.Builder builder = ProtocolNetResponse.newBuilder();
				builder.setCmd(ProtocolCmdType.FORCEEXITGAME.value());
				ctx.writeAndFlush(builder.build()).addListener(ChannelFutureListener.CLOSE);
			//�����������ݣ�û�����ݷ���
			}else if(event.state().equals(IdleState.WRITER_IDLE)){
				System.out.println("WRITER_IDLE");
				ProtocolNetResponse.Builder builder = ProtocolNetResponse.newBuilder();
				builder.setCmd(ProtocolCmdType.HEARTBEAT.value());
				ctx.writeAndFlush(builder.build());
			}else if(event.state().equals(IdleState.ALL_IDLE)){
				TCPRemovePlayer.PlayerExitGame(ctx.channel().id());
				System.out.println("ALL_IDLE");
				ProtocolNetResponse.Builder builder = ProtocolNetResponse.newBuilder();
				builder.setCmd(ProtocolCmdType.FORCEEXITGAME.value());
				ctx.writeAndFlush(builder.build()).addListener(ChannelFutureListener.CLOSE);
			}
		}else{
			super.userEventTriggered(ctx, evt);
		}
	}
	
}
