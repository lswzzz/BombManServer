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
	/**用于分配处理业务线程的线程组个数 */
	protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors()*2;
	protected static final int BIZTHREADSIZE = 4; 
	/* 
     * NioEventLoopGroup实际上就是个线程池, 
     * NioEventLoopGroup在后台启动了n个NioEventLoop来处理Channel事件, 
     * 每一个NioEventLoop负责处理m个Channel, 
     * NioEventLoopGroup从NioEventLoop数组里挨个取出NioEventLoop来处理Channel 
     */  
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);  
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE); 
    
	public void start() throws InterruptedException {
	    ServerBootstrap b = new ServerBootstrap();// 引导辅助程序
	    try {
	      b.group(bossGroup, workerGroup);
	      //NioDatagramChannel UDP
	      b.channel(NioServerSocketChannel.class);// 设置nio类型的channel
	      b.childHandler(new ProtoBufServerInitializer());
	      b.option(ChannelOption.SO_BACKLOG, 128);
	      b.childOption(ChannelOption.SO_KEEPALIVE, true);
	      ChannelFuture f = b.bind(port).sync();// 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
	      System.out.println(BombManServer.class.getName() + " started and listen on " + f.channel().localAddress());
	      f.channel().closeFuture().sync();// 应用程序会一直等待，直到channel关闭
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	    	shutdown();//关闭EventLoopGroup，释放掉所有资源包括创建的线程
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
