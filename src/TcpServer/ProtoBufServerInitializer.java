package TcpServer;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import proto.clientproto.ClientProtocol.ProtocolNetRequest;

public class ProtoBufServerInitializer  extends ChannelInitializer<SocketChannel>{
	private static final int READ_IDEL_TIME_OUT = 300;
	private static final int WRITE_IDLE_TIME_OUT = 150;
	private static final int ALL_IDLE_TIME_OUT = 500;
	
	//解码从上到下
	//编码 从下到上
	@Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("idleState", new IdleStateHandler(READ_IDEL_TIME_OUT, WRITE_IDLE_TIME_OUT, ALL_IDLE_TIME_OUT, TimeUnit.SECONDS))
        	.addLast(new LengthFieldBasedFrameDecoder(10240, 0, 2, 0, 2))
        	.addLast("encoder", new LengthFieldPrepender(2, false))
        	.addLast("pbcDecoder", new ProtobufDecoder(ProtocolNetRequest.getDefaultInstance()))
			.addLast("pbcEncoder", new ProtobufEncoder())
			.addLast("heart_handler", new HeartBeatHandler())
			.addLast("game_protobuf_handler", new GameProtobufHandler());
	}
}
