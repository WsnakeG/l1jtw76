package com.lineage.echo;

/**
 * 伺服器封包編譯7.6C_TW
 * 
 * @version 160128200
 * @author Roy,Smile
 */
public class OpcodesClient {

	/**
	 * 初始化封包
	 */
	protected static final byte _firstPacket[] = { (byte) 0xf2, (byte) 0x9d, (byte) 0x32, (byte) 0x02,
			(byte) 0xb0, (byte) 0x42, (byte) 0xb8, (byte) 0x40 };

	public static final int C_OPCODE_TELEPORT = 2;

	public static final int C_OPCODE_FISHCLICK = 3;

	public static final int C_OPCODE_EXCLUDE = 6;

	public static final int C_OPCODE_DELBUDDY = 7;

	public static final int C_OPCODE_CHATWHISPER = 12;

	public static final int C_OPCODE_PRIVATESHOPLIST = 13;

	public static final int C_OPCODE_AUTOLOGIN = 14;

	public static final int C_OPCODE_DOOR = 17;

	public static final int C_OPCODE_CHATGLOBAL = 19;

	public static final int C_OPCODE_EXTCOMMAND = 20;

	public static final int C_OPCODE_LOGINTOSERVEROK = 22;

	public static final int C_OPCODE_CLIENT_READY = 23;

	public static final int C_OPCODE_FIGHT = 27;

	public static final int C_OPCODE_BUDDYLIST = 28;

	public static final int C_OPCODE_AMOUNT = 32;

	public static final int C_OPCODE_LEAVECLANE = 33;

	public static final int C_OPCODE_SHOP = 35;

	public static final int C_OPCODE_DRAWAL = 36;

	public static final int C_OPCODE_PLEDGE_WATCH = 37;

	public static final int C_OPCODE_USEPETITEM = 41;

	public static final int C_OPCODE_PARTYLIST = 43;

	public static final int C_OPCODE_MAIL = 44;

	public static final int C_OPCODE_DELETEINVENTORYITEM = 45;

	public static final int C_OPCODE_LOGINTOSERVER = 48;

	public static final int C_OPCODE_RESTART = 49;

	public static final int C_OPCODE_RANK = 51;

	public static final int C_OPCODE_RESULT = 55;

	public static final int C_OPCODE_CHANGECHAR = 56;

	public static final int C_OPCODE_MAPSYSTEM = 58;

	public static final int C_OPCODE_NPCTALK = 59;

	public static final int C_OPCODE_AUTHLOGIN = 62;

	public static final int C_OPCODE_DUNGEONTELEPORT = 63;

	public static final int C_OPCODE_PLEDGE = 76;

	public static final int C_OPCODE_CHANGEHEADING = 78;

	public static final int C_OPCODE_MATCH_MAKING = -1;

	public static final int C_OPCODE_WAR = 86;

	public static final int C_OPCODE_MOVECHAR = 88;

	public static final int C_OPCODE_DELETECHAR = 89;

	public static final int C_OPCODE_KEEPALIVE = 91;

	public static final int C_OPCODE_TRADE = 94;

	public static final int C_OPCODE_TAXRATE = 101;

	public static final int C_OPCODE_ATTACKRUNING = 103;

	public static final int C_OPCODE_USEITEM = 104;

	public static final int C_OPCODE_BANPARTY = 106;

	public static final int C_OPCODE_PROPOSE = 107;

	public static final int C_OPCODE_NEWCHAR = 110;

	public static final int C_OPCODE_ARROWATTACK = 116;

	public static final int C_OPCODE_CHARACTERCONFIG = 124;

	public static final int C_OPCODE_UPDATEPLEDGE_INFO = 130;

	public static final int C_OPCODE_WHO = 133;

	public static final int C_OPCODE_USESKILL = 134;

	public static final int C_OPCODE_BOARD = 137;

	public static final int C_OPCODE_SELECTTARGET = 140;

	public static final int C_OPCODE_JOINCLAN = 141;

	public static final int C_OPCODE_CHECKPK = 142;

	public static final int C_OPCODE_ATTR = 144;

	public static final int C_OPCODE_WAREHOUSELOCK = 145;

	public static final int C_OPCODE_QUITGAME = 147;

	public static final int C_OPCODE_EMBLEM = 149;

	public static final int C_OPCODE_FIX_WEAPON_LIST = 154;

	public static final int C_OPCODE_TITLE = 162;

	public static final int C_OPCODE_CAHTPARTY = 164;

	public static final int C_OPCODE_SKILLBUY = 165;

	public static final int C_OPCODE_CALL = 169;

	public static final int C_OPCODE_BOARDREAD = 172;

	public static final int C_OPCODE_SELECTLIST = 173;

	public static final int C_OPCODE_LEAVEPARTY = 174;

	public static final int C_OPCODE_DEPOSIT = 180;

	public static final int C_OPCODE_COMMONCLICK = 181;

	public static final int C_OPCODE_ATTACK = 182;

	public static final int C_OPCODE_ENTERPORTAL = 183;

	public static final int C_OPCODE_SHIP = 185;

	public static final int C_OPCODE_PETMENU = 189;

	public static final int C_OPCODE_PICKUPITEM = 193;

	public static final int C_OPCODE_SKILLBUYOK = 202;

	public static final int C_OPCODE_GIVEITEM = 203;

	public static final int C_OPCODE_CLAN = 204;

	public static final int C_OPCODE_CHAT = 205;

	public static final int C_OPCODE_ADDBOOKMARK = 207;

	public static final int C_OPCODE_BOARDBACK = 210;

	public static final int C_OPCODE_SKILLBUYITEM = 216;

	public static final int C_OPCODE_CHARRESET = 217;

	public static final int C_OPCODE_TELEPORTLOCK = 218;

	public static final int C_OPCODE_ADDBUDDY = 224;

	public static final int C_OPCODE_CLIENTVERSION = 225;

	public static final int C_OPCODE_BOOKMARKDELETE = 229;

	public static final int C_OPCODE_DROPITEM = 233;

	public static final int C_OPCODE_TRADEADDCANCEL = 234;

	public static final int C_OPCODE_CRAFTSYSTEM = 235;

	public static final int C_OPCODE_TRADEADDOK = 236;

	public static final int C_OPCODE_CREATECLAN = 238;

	public static final int C_OPCODE_EXIT_GHOST = 240;

	public static final int C_OPCODE_CREATEPARTY = 241;

	public static final int C_OPCODE_BANCLAN = 242;

	public static final int C_OPCODE_BOARDWRITE = 243;

	public static final int C_OPCODE_NPCACTION = 249;

	public static final int C_OPCODE_BOARDDELETE = 251;

	public static final int C_OPCODE_TRADEADDITEM = 252;

	public static final int C_OPCODE_SKILLBUYOKITEM = 253;
	
	
	// 下面註解為8.1C
	/*public static final int C_OPCODE_USEITEM = 0;

	public static final int C_OPCODE_FISHCLICK = 1;

	public static final int C_OPCODE_SELECTTARGET = 2;

	public static final int C_OPCODE_CHATGLOBAL = 4;

	public static final int C_OPCODE_RANK = 10;

	public static final int C_OPCODE_CLAN = 11;

	public static final int C_OPCODE_WHO = 12;

	public static final int C_OPCODE_UPDATEPLEDGE_INFO = 13;

	public static final int C_OPCODE_TELEPORTLOCK = 16;

	public static final int C_OPCODE_DUNGEONTELEPORT = 19;

	public static final int C_OPCODE_CHAT = 23;

	public static final int C_OPCODE_TRADE = 24;

	public static final int C_OPCODE_CHECKPK = 31;

	public static final int C_OPCODE_ATTR = 35;

	public static final int C_OPCODE_CHATWHISPER = 36;

	public static final int C_OPCODE_PRIVATESHOPLIST = 37;

	public static final int C_OPCODE_BOOKMARKDELETE = 40;

	public static final int C_OPCODE_ADDBUDDY = 42;

	public static final int C_OPCODE_SKILLBUYOK = 43;

	public static final int C_OPCODE_CHANGECHAR = 45;

	public static final int C_OPCODE_SELECTLIST = 47;

	public static final int C_OPCODE_BOARDDELETE = 48;

	public static final int C_OPCODE_EXTCOMMAND = 49;

	public static final int C_OPCODE_NEWCHAR = 52;

	public static final int C_OPCODE_PLEDGE = 60;

	public static final int C_OPCODE_EXIT_GHOST = 62;

	public static final int C_OPCODE_CREATECLAN = 64;

	public static final int C_OPCODE_MOVECHAR = 65;

	public static final int C_OPCODE_NPCACTION = 66;

	public static final int C_OPCODE_CHARACTERCONFIG = 68;

	public static final int C_OPCODE_BUDDYLIST = 69;

	public static final int C_OPCODE_AUTHLOGIN = 71;

	public static final int C_OPCODE_BOARDBACK = 75;

	public static final int C_OPCODE_PARTYLIST = 77;

	public static final int C_OPCODE_LOGINTOSERVEROK = 84;

	public static final int C_OPCODE_AMOUNT = 85;

	public static final int C_OPCODE_SKILLBUYOKITEM = 86;

	public static final int C_OPCODE_BOARD = 88;

	public static final int C_OPCODE_PETMENU = 90;

	public static final int C_OPCODE_ENTERPORTAL = 91;

	public static final int C_OPCODE_FIGHT = 94;

	public static final int C_OPCODE_SKILLBUY = 95;

	public static final int C_OPCODE_ARROWATTACK = 96;

	public static final int C_OPCODE_DELETEINVENTORYITEM = 99;

	public static final int C_OPCODE_LEAVECLANE = 102;

	public static final int C_OPCODE_BANPARTY = 103;

	public static final int C_OPCODE_JOINCLAN = 104;

	public static final int C_OPCODE_MATCH_MAKING = -1;

	public static final int C_OPCODE_DRAWAL = 113;

	public static final int C_OPCODE_DROPITEM = 114;

	public static final int C_OPCODE_TITLE = 116;

	public static final int C_OPCODE_PLEDGE_WATCH = 117;

	public static final int C_OPCODE_CREATEPARTY = 127;

	public static final int C_OPCODE_TAXRATE = 128;

	public static final int C_OPCODE_DEPOSIT = 130;

	public static final int C_OPCODE_LOGINTOSERVER = 131;

	public static final int C_OPCODE_QUITGAME = 134;

	public static final int C_OPCODE_WAREHOUSELOCK = 139;

	public static final int C_OPCODE_CLIENT_READY = 144;

	public static final int C_OPCODE_SKILLBUYITEM = 147;

	public static final int C_OPCODE_KEEPALIVE = 149;

	public static final int C_OPCODE_BANCLAN = 150;

	public static final int C_OPCODE_TRADEADDITEM = 152;

	public static final int C_OPCODE_ADDBOOKMARK = 153;

	public static final int C_OPCODE_TRADEADDCANCEL = 155;

	public static final int C_OPCODE_TELEPORT = 162;

	public static final int C_OPCODE_CAHTPARTY = 167;

	public static final int C_OPCODE_DELBUDDY = 169;

	public static final int C_OPCODE_ATTACK = 170;

	public static final int C_OPCODE_COMMONCLICK = 172;

	public static final int C_OPCODE_PICKUPITEM = 173;

	public static final int C_OPCODE_AUTOLOGIN = 174;

	public static final int C_OPCODE_GIVEITEM = 178;

	public static final int C_OPCODE_BOARDWRITE = 179;

	public static final int C_OPCODE_ATTACKRUNING = 183;

	public static final int C_OPCODE_RESTART = 186;

	public static final int C_OPCODE_RESULT = 187;

	public static final int C_OPCODE_SHOP = 191;

	public static final int C_OPCODE_NPCTALK = 199;

	public static final int C_OPCODE_CHANGEHEADING = 200;

	public static final int C_OPCODE_WAR = 202;

	public static final int C_OPCODE_BOARDREAD = 203;

	public static final int C_OPCODE_CHARRESET = 205;

	public static final int C_OPCODE_USEPETITEM = 206;

	public static final int C_OPCODE_DOOR = 208;

	public static final int C_OPCODE_FIX_WEAPON_LIST = 209;

	public static final int C_OPCODE_LEAVEPARTY = 216;

	public static final int C_OPCODE_MAPSYSTEM = 217;

	public static final int C_OPCODE_USESKILL = 221;

	public static final int C_OPCODE_MAIL = 223;

	public static final int C_OPCODE_DELETECHAR = 225;

	public static final int C_OPCODE_CRAFTSYSTEM = 230;

	public static final int C_OPCODE_TRADEADDOK = 232;

	public static final int C_OPCODE_SHIP = 233;

	public static final int C_OPCODE_PROPOSE = 237;

	public static final int C_OPCODE_EXCLUDE = 241;

	public static final int C_OPCODE_EMBLEM = 242;

	public static final int C_OPCODE_CLIENTVERSION = 245;

	public static final int C_OPCODE_CALL = 255;*/

}
