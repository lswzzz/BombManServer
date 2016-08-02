package InlineSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import TcpServer.map.object.PlayerMessage;
import TcpServer.map.object.RoomInfo;
import TcpUtils.GameMapGeneratorTCP;
import utils.ByteArray;

public class SocketServer {
	private static final int PORT = 21020;
	private Socket udpClient = null;
	public static SocketServer Instance = new SocketServer();
	private Queue<byte[]> messageQueue = null;
	private ServerSocket serverSocket = null;
	private SocketServer(){ 
		
	}
	
	public void addMessage(byte[] data) throws IOException{
		ByteArray array = new ByteArray();
		array.writeBytes(data);
		messageQueue.add(array.getWritedData());
	}
	
	public void start(){
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				init();
				DealWith();
			}	
		});
		thread.start();
//		Thread thread2 = new Thread(new Runnable(){
//			@Override
//			public void run() {
//				test();
//			}	
//		});
//		thread2.start();
	}
	
//	public void test(){
//		try{
//			while(true){
//				if(udpClient != null){
//					
//					RoomInfo info = new RoomInfo();
//					info.sceneName = "scene1";
//					info.roomOwner = new PlayerMessage();
//					info.roomOwner.id = 1;
//					info.playerList = new ArrayList<PlayerMessage>();
//					for(int i=0; i<4; i++){
//						PlayerMessage message = new PlayerMessage();
//						message.id = i + 10;
//						info.playerList.add(message);
//					}
//					GameMapGeneratorTCP.SendUdpClient(info);
//				}else{
//					Thread.sleep(1);
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	public void init(){
		try{
			messageQueue = new LinkedBlockingQueue<byte[]>();
			serverSocket = new ServerSocket(PORT);
			System.out.println("Inline UdpServer started and listen on " + serverSocket.getInetAddress().getHostAddress() + "  port:" + PORT);
			while(true){
				udpClient = serverSocket.accept();
				System.out.println("UDPSocket arealy connected ");
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void DealWith(){
		try{
			while(true){
				if(!messageQueue.isEmpty()){
					System.out.println("start send Message");
					DataOutputStream out = new DataOutputStream(udpClient.getOutputStream());
					byte[] data = messageQueue.poll();
					out.write(data, 0, data.length);
					System.out.println("send Message to inline client");
				}else{
					Thread.sleep(1);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(udpClient != null){
				try{
					udpClient.close();
				}catch(Exception e){
					udpClient = null;
					System.out.println("server inline socket exception " + e.getMessage());
				}
			}
		}
	}
}
