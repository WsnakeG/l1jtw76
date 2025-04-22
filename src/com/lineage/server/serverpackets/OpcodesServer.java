package com.lineage.server.serverpackets;

/**
 * 7.0C_TW
 * 
 * @version 150416200
 * @author Roy,Smile
 */
public class OpcodesServer {

	public static final int S_OPCODE_WAR = 5;

	public static final int S_OPCODE_DEXUP = 6;

	public static final int S_OPCODE_LOGINTOGAME = 7;

	public static final int S_OPCODE_PACKETBOX = 8;

	public static final int S_OPCODE_LIGHT = 9;

	public static final int S_OPCODE_COMMONNEWS2 = 10;

	public static final int S_OPCODE_CHARAMOUNT = 11;

	public static final int S_OPCODE_DRAWAL = 15;

	public static final int S_OPCODE_SHOWSHOPBUYLIST = 16;
	
	// 火神需要
	// 3345678 S_OPCODE_EXTENDED_PROTOBUF 24 錯 註解
//	public static final int  S_OPCODE_EXTENDED_PROTOBUF  = 24;

	public static final int S_OPCODE_COMMONNEWS = 28;

	public static final int S_OPCODE_ATTRIBUTE = 30;

	public static final int S_OPCODE_GLOBALCHAT = 34;

	public static final int S_OPCODE_SPMR = 36;

	public static final int S_OPCODE_DOACTIONGFX = 38;

	public static final int S_OPCODE_PRIVATESHOPLIST = 40;

	public static final int S_OPCODE_BOOKMARKS = 44;

	public static final int S_OPCODE_STRUP = 46;

	public static final int S_OPCODE_ITEMCOLOR = 47;

	public static final int S_OPCODE_CHARRESET = 49;

	public static final int S_OPCODE_NEWCHARWRONG = 50;

	public static final int S_OPCODE_CHANGENAME = 51;

	public static final int S_OPCODE_RESURRECTION = 53;

	public static final int S_OPCODE_ADDITEM = 58;

	public static final int S_OPCODE_SELECTTARGET = 59;

	public static final int S_OPCODE_SKILLBRAVE = 60;

	public static final int S_OPCODE_TELEPORT = 61;

	public static final int S_OPCODE_POLY = 62;

	public static final int S_OPCODE_UPDATELEVELRANGE = 64;

	public static final int S_OPCODE_HOUSELIST = 66;

	public static final int S_OPCODE_INPUTAMOUNT = 69;

	public static final int S_OPCODE_TRADE = 71;

	public static final int S_OPCODE_YES_NO = 74;

	public static final int S_OPCODE_LETTER = 75;

	public static final int S_OPCODE_DETELECHAROK = 76;

	public static final int S_OPCODE_BLESSOFEVA = 77;

	public static final int S_OPCODE_RANGESKILLS = 78;

	public static final int S_OPCODE_USEMAP = 79;

	public static final int S_OPCODE_LOGINRESULT = 80;

	public static final int S_OPCODE_POISON = 81;

	public static final int S_OPCODE_EMBLEM = 85;

	public static final int S_OPCODE_MPUPDATE = 86;

	public static final int S_OPCODE_SHOWHTML = 87;

	public static final int S_OPCODE_LIQUOR = 89;

	public static final int S_OPCODE_SKILLBUY = 90;

	public static final int S_OPCODE_SPOLY = 92;

	public static final int S_OPCODE_RESTART = 93;

	public static final int S_OPCODE_SURVIALCALL_NEEDTIME = 95;

	public static final int S_OPCODE_HOUSEMAP = 98;

	public static final int S_OPCODE_HPUPDATE = 99;

	public static final int S_OPCODE_SELECTLIST = 100;

	public static final int S_OPCODE_HPMETER = 103;

	public static final int S_OPCODE_CURSEBLIND = 104;

	public static final int S_OPCODE_NEWCHARPACK = 109;

	public static final int S_OPCODE_CHARTITLE = 112;

	public static final int S_OPCODE_ABILITY = 120;

	public static final int S_OPCODE_MATCH_MAKING = -1;

	public static final int S_OPCODE_MAPID = 124;

	public static final int S_OPCODE_INVIS = 125;

	public static final int S_OPCODE_TRUETARGET = 127;

	public static final int S_OPCODE_CRAFTSYSTEM = 130; // S_OPCODE_EXTENDED_PROTOBUF 屍魂塔

	public static final int S_OPCODE_LAWFUL = 131;

	public static final int S_OPCODE_TRADESTATUS = 132;

	public static final int S_OPCODE_CASTLEMASTER = 134;

	public static final int S_OPCODE_SERVERMSG = 137;

	public static final int S_OPCODE_MOVEOBJECT = 139;

	public static final int S_OPCODE_DEPOSIT = 140;

	public static final int S_OPCODE_MAIL = 142;

	public static final int S_OPCODE_OWNCHARSTATUS = 143;

	public static final int S_OPCODE_EFFECTLOCATION = 148;

	public static final int S_OPCODE_CHANGEHEADING = 150;

	public static final int S_OPCODE_OBJECTPACK = 158;

	public static final int S_OPCODE_WEATHER = 159;

	public static final int S_OPCODE_ITEMSTATUS = 160;

	public static final int S_OPCODE_IDENTIFYDESC = 161;

	public static final int S_OPCODE_BOARDREAD = 166;

	public static final int S_OPCODE_SHOWSHOPSELLLIST = 168;

	public static final int S_OPCODE_SHOWRETRIEVELIST = 169;

	public static final int S_OPCODE_SERVERVERSION = 171;

	public static final int S_OPCODE_OWNCHARSTATUS2 = 176;

	public static final int S_OPCODE_BLUEMESSAGE = 177;

	public static final int S_OPCODE_SKILLICONSHIELD = 179;

	public static final int S_OPCODE_INVLIST = 184;

	public static final int S_OPCODE_DUNGEONTELEPORT = 186;

	public static final int S_OPCODE_CHARGECOUNT = 189;

	public static final int S_OPCODE_NPCSHOUT = 190;

	public static final int S_OPCODE_MATERIAL = 194;

	public static final int S_OPCODE_TRADEADDITEM = 195;

	public static final int S_OPCODE_TAXRATE = 196;

	public static final int S_OPCODE_NEWMASTER = 199;

	public static final int S_OPCODE_EXP = 202;

	public static final int S_OPCODE_ATTACKPACKET = 203;

	public static final int S_OPCODE_INITPACKET = 204;

	public static final int S_OPCODE_SKILLHASTE = 206;

	public static final int S_OPCODE_SOUND = 208;

	public static final int S_OPCODE_CHARVISUALUPDATE = 209;

	public static final int S_OPCODE_REMOVE_OBJECT = 210;

	public static final int S_OPCODE_NORMALCHAT = 212;

	public static final int S_OPCODE_ADDSKILL = 215;

	public static final int S_OPCODE_PLEDGE_WATCH = 216;

	public static final int S_OPCODE_BOARD = 221;

	public static final int S_OPCODE_SKILLSOUNDGFX = 223;

	public static final int S_OPCODE_GAMETIME = 225;

	public static final int S_OPCODE_TELEPORTLOCK = 233;

	public static final int S_OPCODE_DELETEINVENTORYITEM = 234;

	public static final int S_OPCODE_DISCONNECT = 239;

	public static final int S_OPCODE_CHARLIST = 240;

	public static final int S_OPCODE_DELSKILL = 241;

	public static final int S_OPCODE_WHISPERCHAT = 242;

	public static final int S_OPCODE_ITEMNAME = 244;

	public static final int S_OPCODE_SKILLBUYITEM = 245;

	public static final int S_OPCODE_OWNCHARATTRDEF = 246;

	public static final int S_OPCODE_PARALYSIS = 248;

	public static final int S_OPCODE_PINKNAME = 251;

	public static final int S_OPCODE_UPDATECLANID = 255;
	
	// 下面註解為8.1C
	/*public static final int S_OPCODE_TRADE = 2;

	public static final int S_OPCODE_SHOWSHOPBUYLIST = 4;

	public static final int S_OPCODE_OBJECTPACK = 5;

	public static final int S_OPCODE_DISCONNECT = 7;

	public static final int S_OPCODE_INITPACKET = 8;

	public static final int S_OPCODE_YES_NO = 12;

	public static final int S_OPCODE_RANGESKILLS = 16;

	public static final int S_OPCODE_ABILITY = 21;

	public static final int S_OPCODE_SKILLBRAVE = 23;

	public static final int S_OPCODE_SKILLSOUNDGFX = 25;

	public static final int S_OPCODE_DOACTIONGFX = 28;

	public static final int S_OPCODE_DEPOSIT = 30;

	public static final int S_OPCODE_CHARLIST = 32;

	public static final int S_OPCODE_MATERIAL = 35;

	public static final int S_OPCODE_TELEPORT = 37;

	public static final int S_OPCODE_EXP = 38;

	public static final int S_OPCODE_LETTER = 39;

	public static final int S_OPCODE_EFFECTLOCATION = 43;

	public static final int S_OPCODE_COMMONNEWS2 = 44;

	public static final int S_OPCODE_TRADEADDITEM = 45;

	public static final int S_OPCODE_SOUND = 51;

	public static final int S_OPCODE_ITEMSTATUS = 52;

	public static final int S_OPCODE_NEWCHARWRONG = 55;

	public static final int S_OPCODE_TAXRATE = 56;

	public static final int S_OPCODE_LIGHT = 57;

	public static final int S_OPCODE_WAR = 61;

	public static final int S_OPCODE_WHISPERCHAT = 62;

	public static final int S_OPCODE_DEXUP = 64;

	public static final int S_OPCODE_INVLIST = 65;

	public static final int S_OPCODE_MOVEOBJECT = 66;

	public static final int S_OPCODE_CHARTITLE = 68;

	public static final int S_OPCODE_SERVERVERSION = 69;

	public static final int S_OPCODE_CRAFTSYSTEM = 71;

	public static final int S_OPCODE_HPUPDATE = 72;

	public static final int S_OPCODE_INVIS = 75;

	public static final int S_OPCODE_BOARDREAD = 76;

	public static final int S_OPCODE_DELETEINVENTORYITEM = 77;

	public static final int S_OPCODE_CASTLEMASTER = 81;

	public static final int S_OPCODE_NORMALCHAT = 84;

	public static final int S_OPCODE_RESTART = 85;

	public static final int S_OPCODE_MPUPDATE = 86;

	public static final int S_OPCODE_MAPID = 91;

	public static final int S_OPCODE_SPMR = 92;

	public static final int S_OPCODE_CHARGECOUNT = 94;

	public static final int S_OPCODE_WEATHER = 96;

	public static final int S_OPCODE_BLESSOFEVA = 103;

	public static final int S_OPCODE_SURVIALCALL_NEEDTIME = 104;

	public static final int S_OPCODE_HOUSEMAP = 105;

	public static final int S_OPCODE_SPOLY = 114;

	public static final int S_OPCODE_SHOWRETRIEVELIST = 117;

	public static final int S_OPCODE_IDENTIFYDESC = 121;

	public static final int S_OPCODE_GAMETIME = 122;

	public static final int S_OPCODE_RESURRECTION = 124;

	public static final int S_OPCODE_NEWMASTER = 126;

	public static final int S_OPCODE_PACKETBOX = 127;

	public static final int S_OPCODE_SHOWSHOPSELLLIST = 128;

	public static final int S_OPCODE_PLEDGE_WATCH = 129;

	public static final int S_OPCODE_TRADESTATUS = 132;

	public static final int S_OPCODE_PINKNAME = 133;

	public static final int S_OPCODE_OWNCHARATTRDEF = 134;

	public static final int S_OPCODE_SERVERMSG = 135;

	public static final int S_OPCODE_TELEPORTLOCK = 137;

	public static final int S_OPCODE_CHARRESET = 139;

	public static final int S_OPCODE_OWNCHARSTATUS2 = 141;

	public static final int S_OPCODE_SHOWHTML = 142;

	public static final int S_OPCODE_EMBLEM = 143;

	public static final int S_OPCODE_CHANGEHEADING = 144;

	public static final int S_OPCODE_ADDSKILL = 145;

	public static final int S_OPCODE_GLOBALCHAT = 148;

	public static final int S_OPCODE_TRUETARGET = 151;

	public static final int S_OPCODE_UPDATECLANID = 152;

	public static final int S_OPCODE_CHARAMOUNT = 153;

	public static final int S_OPCODE_POISON = 155;

	public static final int S_OPCODE_ITEMNAME = 159;

	public static final int S_OPCODE_ATTRIBUTE = 163;

	public static final int S_OPCODE_DUNGEONTELEPORT = 166;

	public static final int S_OPCODE_CHARVISUALUPDATE = 169;

	public static final int S_OPCODE_SELECTLIST = 172;

	public static final int S_OPCODE_NPCSHOUT = 174;

	public static final int S_OPCODE_MAIL = 175;

	public static final int S_OPCODE_DETELECHAROK = 176;

	public static final int S_OPCODE_SKILLICONSHIELD = 178;

	public static final int S_OPCODE_HOUSELIST = 180;

	public static final int S_OPCODE_LOGINRESULT = 182;

	public static final int S_OPCODE_PRIVATESHOPLIST = 183;

	public static final int S_OPCODE_NEWCHARPACK = 188;

	public static final int S_OPCODE_BOARD = 195;

	public static final int S_OPCODE_ITEMCOLOR = 196;

	public static final int S_OPCODE_BLUEMESSAGE = 198;

	public static final int S_OPCODE_STRUP = 200;

	public static final int S_OPCODE_MATCH_MAKING = -1;

	public static final int S_OPCODE_PARALYSIS = 205;

	public static final int S_OPCODE_POLY = 208;

	public static final int S_OPCODE_COMMONNEWS = 209;

	public static final int S_OPCODE_OWNCHARSTATUS = 211;

	public static final int S_OPCODE_CURSEBLIND = 212;

	public static final int S_OPCODE_LIQUOR = 218;

	public static final int S_OPCODE_UPDATELEVELRANGE = 219;

	public static final int S_OPCODE_DELSKILL = 221;

	public static final int S_OPCODE_SKILLHASTE = 222;

	public static final int S_OPCODE_LAWFUL = 224;

	public static final int S_OPCODE_DRAWAL = 226;

	public static final int S_OPCODE_LOGINTOGAME = 229;

	public static final int S_OPCODE_SKILLBUY = 230;

	public static final int S_OPCODE_ADDITEM = 232;

	public static final int S_OPCODE_ATTACKPACKET = 233;

	public static final int S_OPCODE_SKILLBUYITEM = 235;

	public static final int S_OPCODE_SELECTTARGET = 237;

	public static final int S_OPCODE_INPUTAMOUNT = 239;

	public static final int S_OPCODE_REMOVE_OBJECT = 242;

	public static final int S_OPCODE_CHANGENAME = 244;

	public static final int S_OPCODE_HPMETER = 248;

	public static final int S_OPCODE_USEMAP = 252;

	public static final int S_OPCODE_BOOKMARKS = 253;*/

}
