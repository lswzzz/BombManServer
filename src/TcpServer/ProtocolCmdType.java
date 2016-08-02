package TcpServer;

public enum ProtocolCmdType {
	FORCEEXITGAME(-2),
	HEARTBEAT(-1),
	NONE(0),
	CONNECTTOSERVER(1),
	CREATEROOM(2),
	GETROOMLIST(3),
	JOINROOM(4),
	EXITROOM(5),
	ROOMOWNEREXIT(6),
	ROOMOWNERREADYSTARTGAME(7),
	ROOMOWNERSTARTGAME(8),
	PLAYERGAMEDATA(9),
	GAMERESULT(10),
	PLAYEREXITGAME(11),
	PLAYERRELOADSCENE(12);
	
	private int value = 0;
	
	public static ProtocolCmdType valueOf(int value) {
        switch (value) {
        case -2:
            return FORCEEXITGAME;
        case -1:
        	return HEARTBEAT;
        case 0:
        	return NONE;
        case 1:
        	return CONNECTTOSERVER;
        case 2:
        	return CREATEROOM;
        case 3:
        	return GETROOMLIST;
        case 4:
        	return JOINROOM;
        case 5:
        	return EXITROOM;
        case 6:
        	return ROOMOWNEREXIT;
        case 7:
        	return ROOMOWNERREADYSTARTGAME;
        case 8:
        	return ROOMOWNERSTARTGAME;
        case 9:
        	return PLAYERGAMEDATA;
        case 10:
        	return GAMERESULT;
        case 11:
        	return PLAYEREXITGAME;
        case 12:
        	return PLAYERRELOADSCENE;
        default:
            return NONE;
        }
    }
	
	 ProtocolCmdType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
