package TcpServer;

import InlineSocket.SocketServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import utils.GameSceneReader;

//http://www.tuicool.com/articles/rqua2iB
public class BombManServer {
	private static final int port = 21365;
	private static final String ip = "203.195.193.136";
	/**���ڷ��䴦��ҵ���̵߳��߳������ */
	protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors()*2;
	protected static final int BIZTHREADSIZE = 4; 
	/* 
     * NioEventLoopGroupʵ���Ͼ��Ǹ��̳߳�, 
     * NioEventLoopGroup�ں�̨������n��NioEventLoop������Channel�¼�, 
     * ÿһ��NioEventLoop������m��Channel, 
     * NioEventLoopGroup��NioEventLoop�����ﰤ��ȡ��NioEventLoop������Channel 
     */  
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);  
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE); 
    
	public void start() throws InterruptedException {
	    ServerBootstrap b = new ServerBootstrap();// ������������
	    try {
	      b.group(bossGroup, workerGroup);
	      //NioDatagramChannel UDP
	      b.channel(NioServerSocketChannel.class);// ����nio���͵�channel
	      b.childHandler(new ProtoBufServerInitializer());
	      b.option(ChannelOption.SO_BACKLOG, 128);
	      b.childOption(ChannelOption.SO_KEEPALIVE, true);
	      ChannelFuture f = b.bind(port).sync();// ������ɣ���ʼ��server��ͨ������syncͬ����������ֱ���󶨳ɹ�
	      System.out.println(BombManServer.class.getName() + " started and listen on " + f.channel().localAddress());
	      f.channel().closeFuture().sync();// Ӧ�ó����һֱ�ȴ���ֱ��channel�ر�
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	    	shutdown();//�ر�EventLoopGroup���ͷŵ�������Դ�����������߳�
	    }
	  }
	
	  public static void main(String[] args) {
	    try {
	    	GameSceneReader.LoadAllScene();
	    	SocketServer.Instance.start();
	    	new BombManServer().start();
	    } catch (InterruptedException e) {
	      e.printStackTrace();
	    }
	  }
	  
	  protected void shutdown() {  
	        workerGroup.shutdownGracefully();  
	        bossGroup.shutdownGracefully();  
	    }  
}
