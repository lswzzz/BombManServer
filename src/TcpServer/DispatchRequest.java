package TcpServer;

import java.util.ArrayList;
import java.util.Map.Entry;

import TcpServer.map.GamePlayerMessageMap;
import TcpServer.map.GameRoomMap;
import TcpServer.map.NettyChannelMap;
import TcpServer.map.TCPRemovePlayer;
import TcpServer.map.object.ChannelSession;
import TcpServer.map.object.PlayerMessage;
import TcpServer.map.object.RoomInfo;
import TcpUtils.GameMapGeneratorTCP;
import game.service.GameConst;
import io.netty.channel.ChannelId;
import proto.clientproto.ClientProtocol.ConnectToServer;
import proto.clientproto.ClientProtocol.CreateRoom;
import proto.clientproto.ClientProtocol.ExitRoom;
import proto.clientproto.ClientProtocol.GameResult;
import proto.clientproto.ClientProtocol.GetRoomList;
import proto.clientproto.ClientProtocol.JoinRoom;
import proto.clientproto.ClientProtocol.PlayerExitGame;
import proto.clientproto.ClientProtocol.PlayerReLoadScene;
import proto.clientproto.ClientProtocol.ProtocolNetRequest;
import proto.clientproto.ClientProtocol.RoomOwnerExit;
import proto.clientproto.ClientProtocol.RoomOwnerReadyStartGame;
import proto.clientproto.ClientProtocol.RoomOwnerStartGame;
import proto.serverproto.ServerProtocol.ConnectToServerResponse;
import proto.serverproto.ServerProtocol.CreateRoomResponse;
import proto.serverproto.ServerProtocol.ExitRoomResponse;
import proto.serverproto.ServerProtocol.GameResultResponse;
import proto.serverproto.ServerProtocol.GetRoomListResponse;
import proto.serverproto.ServerProtocol.JoinRoomResponse;
import proto.serverproto.ServerProtocol.PlayerGameInitInfo;
import proto.serverproto.ServerProtocol.PlayerReloadSceneResponse;
import proto.serverproto.ServerProtocol.ProtocolNetResponse;
import proto.serverproto.ServerProtocol.RoomOwnerExitResponse;
import proto.serverproto.ServerProtocol.RoomOwnerReadyStartGameResponse;
import proto.serverproto.ServerProtocol.RoomOwnerStartGameResponse;
import proto.serverproto.ServerProtocol.SimpleRoomInfo;
import proto.serverproto.ServerProtocol.SinglePlayerInfo;

public class DispatchRequest {
	
	public int[] roomFds;
	public ChannelId channelId;
	public boolean dontReport; 
	public DispatchRequest(){
		roomFds = null;
		channelId = null;
		dontReport = false;
	}
	
	public boolean NeedDeal(ProtocolNetRequest req){
		if(req.getCmd() == ProtocolCmdType.HEARTBEAT.value()){
			return false;
		}
		return true;
	}
	
	public ProtocolNetResponse Dispatch(ProtocolNetRequest req, ChannelId id){
		channelId = id;
		ProtocolNetResponse resp;
		ProtocolNetResponse.Builder builder = ProtocolNetResponse.newBuilder();
		switch(ProtocolCmdType.valueOf(req.getCmd())){
		case CONNECTTOSERVER:
			builder.setConnectToServerResponse(ConnectToServerRequest(req.getConnectToServer()));
			builder.setCmd(ProtocolCmdType.CONNECTTOSERVER.value());
			break;
		case CREATEROOM:
			builder.setCreateRoomResponse(CreateRoomRequest(req.getCreateRoom()));
			builder.setCmd(ProtocolCmdType.CREATEROOM.value());
			break;
		case GETROOMLIST:
			builder.setGetRoomListResponse(GetRoomListRequest(req.getGetRoomList()));
			builder.setCmd(ProtocolCmdType.GETROOMLIST.value());
			break;
		case JOINROOM:
			builder.setJoinRoomResponse(JoinRoomRequest(req.getJoinRoom()));
			builder.setCmd(ProtocolCmdType.JOINROOM.value());
			break;
		case EXITROOM:
			builder.setExitRoomResponse(ExitRoomRequest(req.getExitRoom()));
			builder.setCmd(ProtocolCmdType.EXITROOM.value());
			break;
		case ROOMOWNEREXIT:
			builder.setRoomOwnerExitResponse(RoomOwnerExitRequest(req.getRoomOwnerExit()));
			builder.setCmd(ProtocolCmdType.ROOMOWNEREXIT.value());
			break;
		case ROOMOWNERREADYSTARTGAME:
			builder.setRoomOwnerReadyStartGameResponse(RoomOwnerReadyStartGameRequest(req.getRoomOwnerReadyStartGame()));
			builder.setCmd(ProtocolCmdType.ROOMOWNERREADYSTARTGAME.value());
			break;
		case ROOMOWNERSTARTGAME:
			builder.setRoomOwnerStartGameResponse(RoomOwnerStartGameRequest(req.getRoomOwnerStartGame()));
			builder.setCmd(ProtocolCmdType.ROOMOWNERSTARTGAME.value());
			break;
//		case PLAYERGAMEDATA:
//			builder.setPlayerGameDataResponse(PlayerGameDataRequest(req.getPlayerGameData()));
//			builder.setCmd(PLAYERGAMEDATA);
//			break;
		case GAMERESULT:
			builder.setGameResultResponse(GameResultRequest(req.getGameResult()));
			builder.setCmd(ProtocolCmdType.GAMERESULT.value());
			break;
		case PLAYERRELOADSCENE:
			builder.setPlayerReloadSceneResponse(PlayerReloadSceneRequest(req.getPlayerReloadScene()));
			builder.setCmd(ProtocolCmdType.PLAYERRELOADSCENE.value());
			break;
		default:
			builder.setCmd(ProtocolCmdType.NONE.value());
			break;
		}
		resp = builder.build();
		return resp;
	}
	
	//连接后的第一件事就是客户端请求获取他自身的fd这个fd控制着后面的操作流程，同时服务器保存他的名称，为了在后面方便操作
	public ConnectToServerResponse ConnectToServerRequest(ConnectToServer connectToServer){
		ChannelSession session = NettyChannelMap.get(channelId);
		//一个playerMessage只能在连接后发送此消息的情况下new防止生成多个重复的数据
		PlayerMessage message = new PlayerMessage();
		message.name = connectToServer.getName();
		message.id = session.id;
		GamePlayerMessageMap.add(message.id, message);
		ConnectToServerResponse.Builder builder = ConnectToServerResponse.newBuilder();
		builder.setFd(message.id);
		ConnectToServerResponse resp = builder.build();
		return resp;
	}
	
	//根据玩家的fd来创建一个房间
	public CreateRoomResponse CreateRoomRequest(CreateRoom createRoom){
		RoomInfo room = new RoomInfo();
		room.currentMan = 1;
		Integer id = createRoom.getFd();
		room.maxMan = createRoom.getMaxMan();
		room.roomName = createRoom.getRoomName();
		PlayerMessage roomOwner = GamePlayerMessageMap.get(id);
		room.roomOwner = roomOwner;
		room.playerList = new ArrayList<PlayerMessage>();
		room.playerList.add(roomOwner);
		room.sceneName = "scene1";
		GameRoomMap.add(id, room);
		CreateRoomResponse.Builder builder = CreateRoomResponse.newBuilder();
		builder.setResult(true);
		return builder.build();
	}
	
	//获取房间列表
	public GetRoomListResponse GetRoomListRequest(GetRoomList getRoomList){
		GetRoomListResponse.Builder builder = GetRoomListResponse.newBuilder();
		int count = 0;
		for (Entry<Integer, RoomInfo> entry : GameRoomMap.map.entrySet()) {  
			SimpleRoomInfo.Builder roomBuilder = SimpleRoomInfo.newBuilder();
			RoomInfo room = entry.getValue();
			roomBuilder.setCurrentManCount(room.currentMan);
			roomBuilder.setMaxManCount(room.maxMan);
			roomBuilder.setRoomOwner(room.roomOwner.name);
			roomBuilder.setRoomName(room.roomName);
			roomBuilder.setRoomOwnerFd(room.roomOwner.id);
			builder.addRoomInfo(roomBuilder.build());
			count++;
			if(count >=10)break;
		}
		if(count == 0)builder.setResult(false);
		else builder.setResult(true);
		builder.setRoomCount(count);
		return builder.build();
	}
	
	//加入一个房间 涉及到消息的转发
	public JoinRoomResponse JoinRoomRequest(JoinRoom joinRoom){
		JoinRoomResponse.Builder builder = JoinRoomResponse.newBuilder();
		int roomFd = joinRoom.getRoomOwnerFd();
		int fd = joinRoom.getFd();
		RoomInfo room = GameRoomMap.get(roomFd);
		if(room.currentMan >= room.maxMan){
			builder.setResult(false);
		}else{
			builder.setResult(true);
			int manCount = room.currentMan + 1;
			builder.setManCount(manCount);
			PlayerMessage owner = GamePlayerMessageMap.get(fd);
			room.playerList.add(owner);
			room.currentMan++;
			GameRoomMap.reset(roomFd);
			setRoomFds(room);
			for(int i=0; i<manCount; i++){
				PlayerMessage player = room.playerList.get(i);
				SinglePlayerInfo.Builder playerInfo = SinglePlayerInfo.newBuilder();
				playerInfo.setFd(player.id);
				playerInfo.setUserName(player.name);
				builder.addPlayerInfo(playerInfo.build());
			}
			builder.setRoomOwnerFd(room.roomOwner.id);
		}
		return builder.build();
	}
	
	//通过fd在转发到其他客户端
	//一个玩家退出房间
	public ExitRoomResponse ExitRoomRequest(ExitRoom exitRoom){
		ExitRoomResponse.Builder builder = ExitRoomResponse.newBuilder();
		int fd = exitRoom.getFd();
		int roomOwnerfd = exitRoom.getRoomOwnerFd();
		RoomInfo room = GameRoomMap.get(roomOwnerfd);
		setRoomFds(room);
		roomKickMan(room, fd); 
		GameRoomMap.removeMan(fd);
		builder.setResult(true);
		builder.setFd(fd);
		return builder.build();
	}
	
	//房主离开房间 解散该房间 转发
	public RoomOwnerExitResponse RoomOwnerExitRequest(RoomOwnerExit roomOwnerExit){
		RoomOwnerExitResponse.Builder builder = RoomOwnerExitResponse.newBuilder();
		int fd = roomOwnerExit.getFd();
		RoomInfo room = GameRoomMap.get(fd);
		setRoomFds(room);
		GameRoomMap.removeRoom(fd);
		builder.setResult(true);
		builder.setFd(fd);
		return builder.build();
	}
	
	//房主按下游戏开始
	public RoomOwnerReadyStartGameResponse RoomOwnerReadyStartGameRequest(RoomOwnerReadyStartGame roomOwnerReadyStartGame){
		RoomOwnerReadyStartGameResponse.Builder builder = RoomOwnerReadyStartGameResponse.newBuilder();
		int fd = roomOwnerReadyStartGame.getFd();
		RoomInfo room = GameRoomMap.get(fd);
		setRoomFds(room);
		builder.setResult(true);
		return builder.build();
	}
	
	//房主收到服务器的信息后发送开始数据就可以开始游戏了
	public RoomOwnerStartGameResponse RoomOwnerStartGameRequest(RoomOwnerStartGame roomOwnerStartGame){
		RoomOwnerStartGameResponse.Builder builder = RoomOwnerStartGameResponse.newBuilder();
		int fd = roomOwnerStartGame.getFd();
		RoomInfo room = GameRoomMap.get(fd);
		setRoomFds(room);
		//生成一个游戏场景
		GameMapGeneratorTCP.Player_Game_Init_Info[] infos = GameMapGeneratorTCP.GenerateGameInitInfo(room);
		GameMapGeneratorTCP.SendUdpClient(room);
		for(int i=0; i<infos.length; i++){
			PlayerGameInitInfo.Builder player = PlayerGameInitInfo.newBuilder();
			player.setFd(infos[i].fd);
			player.setPosX(infos[i].posx);
			player.setPosY(infos[i].posy);
			player.setPosName(infos[i].posName);
			builder.addPlayerGameInitInfo(player.build());
		}
		builder.setMaxSpeed(GameConst.maxSpeed);
		builder.setMaxDistance(GameConst.maxDistance);
		builder.setSpeed(GameConst.speed);
		builder.setPower(GameConst.power);
		builder.setMaxPower(GameConst.maxPower);
		builder.setBubbleCount(GameConst.bubbleCount);
		builder.setMaxBubbleCount(GameConst.maxBubbleCount);
		builder.setBoomTime(GameConst.boomTime);
		builder.setJoystickPercent(GameConst.joystickPercent);
		builder.setResult(true);
		builder.setSeed((int)System.currentTimeMillis());
		return builder.build();
	}
	
//	public PlayerGameDataResponse PlayerGameDataRequest(PlayerGameData playerGameData){
//		PlayerGameDataResponse.Builder builder = PlayerGameDataResponse.newBuilder();
//		int fd = playerGameData.getFd();
//		ByteString bytes = playerGameData.getPlayerData();
//		GamePlayerInputController controller = GameInputControllerMap.get(fd);
//		controller.ReceiveInput(bytes, fd);
//		ByteString respBytes = controller.getResponseData();
//		builder.setFd(fd);
//		builder.setData(respBytes);
//		return builder.build();
//	}
	
	public GameResultResponse GameResultRequest(GameResult gameResult){
		GameResultResponse.Builder builder = GameResultResponse.newBuilder();
		int fd = gameResult.getFd();
		boolean result = gameResult.getResult();
		RoomInfo room = GameRoomMap.get(fd);
		setRoomFds(room);
		builder.setResult(result);
		return builder.build();
	}
	
	public void PlayerExitGameRequest(PlayerExitGame playerExitGame){
		int fd = playerExitGame.getFd();
		if(GameRoomMap.containsFdInRoom(fd)){
			int roomOwnerFd = GameRoomMap.getRoomOwnerId(fd);
			RoomInfo room = GameRoomMap.get(roomOwnerFd);
			setRoomFdsExculde(room, fd);
			roomKickMan(room, fd);
		}
		GamePlayerMessageMap.remove(fd);
	}
	
	public PlayerReloadSceneResponse PlayerReloadSceneRequest(PlayerReLoadScene reload){
		int fd = reload.getFd();
		PlayerReloadSceneResponse.Builder builder = PlayerReloadSceneResponse.newBuilder();
		GameMapGeneratorTCP.PlayerReloadScene(fd);
		builder.setResult(true);
		return builder.build();
	}
	
	public void setRoomFds(RoomInfo roomInfo){
		int count = roomInfo.currentMan;
		roomFds = new int[count];
		for(int i=0; i<count; i++){
			roomFds[i] = roomInfo.playerList.get(i).id;
		}
	}
	
	public void setRoomFdsExculde(RoomInfo roomInfo, int excludeFd){
		int count = roomInfo.currentMan;
		roomFds = new int[count - 1];
		int fd;
		int index = 0;
		for(int i=0; i<count; i++){
			fd = roomInfo.playerList.get(i).id;
			if(fd == excludeFd){
				continue;
			}
			roomFds[index++] = fd;
		}
	}
	
	public void roomKickMan(RoomInfo room, int fd){
		int manCount = room.currentMan;
		for(int i=0; i<manCount; i++){
			PlayerMessage player = room.playerList.get(i);
			if(player.id == fd){
				room.playerList.remove(i);
				room.currentMan--;
				break;
			}
		}
	}
	
}
