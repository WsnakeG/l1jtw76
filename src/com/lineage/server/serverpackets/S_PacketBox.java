package com.lineage.server.serverpackets;

import java.util.ArrayList;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * スキルアイコンや遮断リストの表示など複数の用途に使われるパケットのクラス
 */
public class S_PacketBox extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * writeByte(id) writeShort(?): <font color=#00800>(639) %s的攻城戰開始。 </font>
	 * <BR>
	 * 1:Kent 2:Orc 3:WW 4:Giran 5:Heine 6:Dwarf 7:Aden 8:Diad 9:城名9 ...
	 */
	// public static final int MSG_WAR_BEGIN = 0;

	/**
	 * writeByte(id) writeShort(?): <font color=#00800>(640) %s的攻城戰結束。 </font>
	 */
	// public static final int MSG_WAR_END = 1;

	/**
	 * writeByte(id) writeShort(?): <font color=#00800>(641)
	 * %s的攻城戰正在進行中。 </font>
	 */
	// public static final int MSG_WAR_GOING = 2;

	/**
	 * <font color=#00800>(642) 已掌握了城堡的主導權。 </font>
	 */
	// public static final int MSG_WAR_INITIATIVE = 3;

	/**
	 * <font color=#00800>(643) 已佔領城堡。</font>
	 */
	// public static final int MSG_WAR_OCCUPY = 4;

	/**
	 * <font color=#00800>(646) 結束決鬥。 </font>
	 */
	public static final int MSG_DUEL = 5;

	/**
	 * writeByte(count): <font color=#00800>(648) 您沒有發送出任何簡訊。 </font>
	 */
	public static final int MSG_SMS_SENT = 6;

	/**
	 * <font color=#00800>(790) 倆人的結婚在所有人的祝福下完成 </font>
	 */
	public static final int MSG_MARRIED = 9;

	/**
	 * writeByte(weight): <font color=#00800>重量(30段階) </font>
	 */
	public static final int WEIGHT = 10;

	/**
	 * writeByte(food): <font color=#00800>満腹度(30段階) </font>
	 */
	public static final int FOOD = 11;

	/**
	 * <font color=#00800>UB情報HTML </font>
	 */
	public static final int HTML_UB = 14;

	/**
	 * writeByte(id)<br>
	 * <font color=#00800> 1: (978) 感覺到在身上有的精靈力量被空氣中融化。<br>
	 * 2: (679) 忽然全身充滿了%s的靈力。 680 火<br>
	 * 3: (679) 忽然全身充滿了%s的靈力。 681 水<br>
	 * 4: (679) 忽然全身充滿了%s的靈力。 682 風<br>
	 * 5: (679) 忽然全身充滿了%s的靈力。 683 地<br>
	 * </font>
	 */
	public static final int MSG_ELF = 15;

	/**
	 * writeByte(count) S(name)...: <font color=#00800>開啟拒絕名單 :</font>
	 */
	public static final int ADD_EXCLUDE2 = 17;

	/**
	 * writeString(name): <font color=#00800>增加到拒絕名單</font>
	 */
	public static final int ADD_EXCLUDE = 18;

	/**
	 * writeString(name): <font color=#00800>移除出拒絕名單</font>
	 */
	public static final int REM_EXCLUDE = 19;

	/** 技能圖示 */
	public static final int ICONS1 = 20;

	/** 技能圖示 */
	public static final int ICONS2 = 21;

	/** 技能圖示 */
	public static final int ICON_AURA = 22;

	/**
	 * writeString(name): <font color=#00800>(764) 新村長由%s選出</font>
	 */
	public static final int MSG_TOWN_LEADER = 23;

	/**
	 * writeByte(id): <font color=#00800>聯盟職位變更</font><br>
	 * id - 1:見習 2:一般 3:守護騎士
	 */
	public static final int MSG_RANK_CHANGED = 27;

	/**
	 * <font color=#00800>血盟線上人數(HTML)</font>
	 */
	public static final int MSG_CLANUSER = 29;

	/**
	 * writeInt(?) writeString(name) writeString(clanname):<br>
	 * <font color=#00800>(782) %s 血盟的 %s打敗了反王<br>
	 * (783) %s 血盟成為新主人。 </font>
	 */
	// public static final int MSG_WIN_LASTAVARD = 30;

	/**
	 * <font color=#00800>(77) \f1你覺得舒服多了。</font>
	 */
	public static final int MSG_FEEL_GOOD = 31;

	/** 不明。 客戶端會傳回一個封包 */
	// INFO - Not Set OP ID: 40
	// 0000: 28 58 02 00 00 fe b2 d4 c6 00 00 00 00 00 00 00 (X..............
	public static final int SOMETHING1 = 33;

	/**
	 * writeShort(time): <font color=#00800>藍水圖示</font>
	 */
	public static final int ICON_BLUEPOTION = 34;

	/**
	 * writeShort(time): <font color=#00800>變身圖示</font>
	 */
	public static final int ICON_POLYMORPH = 35;

	/**
	 * writeShort(time): <font color=#00800>禁言圖示 </font>
	 */
	public static final int ICON_CHATBAN = 36;

	/** 不明。C_7パケットが飛ぶ。C_7はペットのメニューを開いたときにも飛ぶ。 */
	public static final int SOMETHING2 = 37;

	/**
	 * <font color=#00800>血盟成員清單(HTML)</font>
	 */
	public static final int HTML_CLAN1 = 38;

	/** writeShort(time): 聖結界圖示 */
	public static final int ICON_I2H = 40;

	/**
	 * <font color=#00800>更新角色使用的快速鍵</font>
	 */
	public static final int CHARACTER_CONFIG = 41;

	/**
	 * <font color=#00800>角色選擇視窗</font> > 0000 : 39 2a e1 88 08 12 48 fa
	 * 9*....H.
	 */
	public static final int LOGOUT = 42;

	/**
	 * <font color=#00800>(130) \f1戰鬥中，無法重新開始。</font>
	 */
	public static final int MSG_CANT_LOGOUT = 43;

	/**
	 * <font color=#00800>風之枷鎖</font>
	 */
	// public static final int WIND_SHACKLE = 44;

	/**
	 * writeByte(count) writeInt(time) writeString(name) writeString(info):<br>
	 * [CALL] ボタンのついたウィンドウが表示される。これはBOTなどの不正者チェックに
	 * 使われる機能らしい。名前をダブルクリックするとC_RequestWhoが飛び、クライアントの
	 * フォルダにbot_list.txtが生成される。名前を選択して+キーを押すと新しいウィンドウが開く。
	 */
	// public static final int CALL_SOMETHING = 45;

	/**
	 * <font color=#00800>writeByte(id): 大圓形競技場，混沌的大戰<br>
	 * id - 1:開始(1045) 2:取消(1047) 3:結束(1046)</font>
	 */
	public static final int MSG_COLOSSEUM = 49;

	/**
	 * <font color=#00800>血盟情報(HTML)</font>
	 */
	public static final int HTML_CLAN2 = 51;

	/**
	 * <font color=#00800>料理選單</font>
	 */
	// public static final int COOK_WINDOW = 52;

	/** writeByte(type) writeShort(time): 料理アイコンが表示される */
	public static final int ICON_COOKING = 53;

	/** 魚上鉤的圖形表示 */
	public static final int FISHING = 55;

	/** 魔法娃娃圖示 */
	public static final int DOLL = 56;

	/** 慎重藥水 */
	public static final int WISDOM_POTION = 57;

	/** 刪除水之元氣圖標 */
	public static final int DEL_ICON = 59;

	/** 三段加速圖標 */
	// public static final int THIRD_SPEED = 60;

	/** 玩家 遊戲點數 */
	// public static final int ACC_TIME = 61;

	/** 同盟目錄 (0/3) */
	public static final int ALLIANCE_LIST = 62;

	/** 比賽視窗(倒數開始) */
	// public static final int GAMESTART = 64;

	/** 開始正向計時 */
	// public static final int TIMESTART = 65;

	/** 顯示資訊 */
	// public static final int GAMEINFO = 66;

	/** 比賽視窗(倒數結束/停止計時) */
	// public static final int GAMEOVER = 69;

	/** 移除比賽視窗 */
	// public static final int GAMECLEAR = 70;

	/** 開始反向計時 */
	// public static final int STARTTIME = 71;

	/** 移除開始反向計時視窗 */
	// public static final int STARTTIMECLEAR = 72;

	/** 手臂受傷攻擊力下降(350秒) */
	public static final int DMG = 74;

	/** 弱點曝光(16秒) */
	public static final int TGDMG = 75;

	/** 攻城戰訊息 */
	// public static final int WAR_START = 78;
	/**
	 * 戰爭結束佔領公告<br>
	 * C(count) S(name)<br>
	 */
	public static final int MSG_WAR_OCCUPY_ALL = 79;

	/** 攻城戰進行中 */
	public static final int MSG_WAR_IS_GOING_ALL = 80; // TODO

	/** 受到殷海薩的祝福，增加了些許的狩獵經驗值。 */
	public static final int LEAVES = 82;

	/** 畫面特效 */
	// public static final int SECRETSTORY_GFX = 83;
	
	/** 螢幕上的效果 */
	public static final int GUI_VISUAL_EFFECT = 83;

	/** 畫面中央顏色對話訊息 */
	// public static final int GREEN_MESSAGE = 84;
	
	/** 螢幕上的字 */
	public static final int MSG_COLOR_MESSAGE = 84;
	// 1:取消頭上的字 2:頭上有字 3:取消得分框框 4:左上角有得分框框

	/** 龍之綠寶石 (藍色殷海薩) */
	public static final int BLUE_EXPREPLY = 86;

	/** 不明 綠色葉子 */
	// public static final int LEAVES200 = 87;

	/** 龍之血痕印記 */
	public static final int DRAGON_BLOOD_ICON = 100;

	/** 顯示龍座標選單 */
	public static final int DRAGON = 101;

	/**
	 * 組隊系統-新加入隊伍的玩家
	 */
	public static final int PARTY_ADD_NEWMEMBER = 104;
	
	/**
	 * 組隊系統-舊的隊員
	 */
	public static final int PARTY_OLD_MEMBER = 105;
	
	/**
	 * 組隊系統-隊長委任
	 */
	public static final int PARTY_CHANGE_LEADER = 106;
	
	/**
	 * 組隊系統-更新死亡隊員名稱UI顏色
	 */
	public static final int PARTY_DEATH_REFRESHNAME = 108;
	
	/**
	 * 組隊系統-更新隊伍
	 */
	public static final int PARTY_REFRESH = 110;
	
	/** 龍之棲息地選單 */
	// public static final int DRAGON_UI = 102;

	/** UI地圖座標傳送 */
	// public static final int SEND_LOC = 111;

	/** 歐林佈告欄 (顯示排名及分數) */
	// public static final int SHOW_BOARD = 112;

	/** 血盟倉庫存取紀錄 */
	// public static final int SHOW_BOARD = 117;

	/** 查詢血盟成員 */
	// public static final int SHOW_BOARD = 119;

	/** 迴避率更新封包 */
	public static final int EVASION_UPDATE = 132;

	/**
	 * 更新迴避率
	 */
	public static final int UPDATE_ER = 132;
	
	/** 武器損壞度更新 */
	public static final int WEAPON_DURABILITY = 138;

	/** 擴張最大記憶空間 */
	public static final int BOOKMARK_EXPAND = 141;

	/** 守護者系統 - 狀態圖示 */
	public static final int PROTECTOR_ICON = 144;

	/** 師徒系統 - 顯示清單 */
	public static final int APPRENTICE_HTML = 146;

	/** 師徒系統 - 狀態圖示 */
	public static final int APPRENTICE_ICON = 147;

	/** 立即更新道具狀態 - 鑑定道具用 (ex.無法強化, 無法刪除) */
	public static final int ITEM_STATUS_RENEW = 149;

	/** 地圖限制時間 - 左上角藍色倒數計時器 00:00 (單位:秒) */
	public static final int MAP_LIMIT_TIMER = 153;

	/** 武器加持狀態圖示 (ex.擬似魔法武器, 暗影之牙) */
	public static final int WEAPON_BUFF_ICON = 154;

	/** 武器攻擊範圍 */
	public static final int WEAPON_RANGE = 160;

	/** 中毒狀態 C(type)=1 H(time) (單位:秒) */
	public static final int POISON_ICON = 161;

	/** 3.8 血盟查詢盟友 (顯示公告) */
	public static final int HTML_PLEDGE_ANNOUNCE = 167;

	/** 3.8 血盟查詢盟友 (寫入公告) */
	public static final int HTML_PLEDGE_REALEASE_ANNOUNCE = 168;

	/** 3.8 血盟查詢盟友 (寫入備註) */
	public static final int HTML_PLEDGE_WRITE_NOTES = 169;

	/** 3.8 血盟查詢盟友 (顯示盟友) */
	public static final int HTML_PLEDGE_MEMBERS = 170;

	/** 3.8 血盟查詢盟友 (顯示上線盟友) */
	public static final int HTML_PLEDGE_ONLINE_MEMBERS = 171;

	/** 3.8 血盟 識別盟徽狀態 */
	public static final int PLEDGE_EMBLEM_STATUS = 173;

	/** 3.8 村莊便利傳送 */
	public static final int TOWN_TELEPORT = 176;

	public static final int TOMAHAWK = 180;
	
	/** 屍魂塔下層開始 */
	public static final int SOULTOWERSTART = 195;

	/** 屍魂塔下層結束 */
	public static final int SOULTOWEREND = 196;

	public S_PacketBox(final int subCode, final L1PcInstance pc) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);
		switch (subCode) {
		case TOWN_TELEPORT:
			writeC(0x01);
			writeH(pc.getX());
			writeH(pc.getY());
			break;
		}
	}

	/**
	 * 殷海薩的祝福
	 * 
	 * @param subCode
	 * @param msg
	 * @param value
	 */
	public S_PacketBox(final int subCode, final String msg, final int value) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(LEAVES);
		writeS(msg);
		writeH(value);
	}

	/**
	 * 師徒系統 - 顯示清單 by terry0412
	 * 
	 * @param master
	 * @param totalList
	 */
	public S_PacketBox(final L1PcInstance master, final ArrayList<L1PcInstance> totalList) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(APPRENTICE_HTML);
		writeC(totalList.size()); // 弟子數量 (最高4人)

		// 弟子資料
		for (final L1PcInstance l1Char : totalList) {
			writeS(l1Char.getName());
			writeC(l1Char.getLevel());
			writeC(l1Char.getType()); // 職業類型
		}

		// 師傅資料
		writeS(master.getName());
		writeC(master.getLevel());
		writeC(master.getType()); // 職業類型
	}

	public S_PacketBox(final int subCode) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case 32:
			writeC(16);
			writeD(0);
			writeD(0);
			writeD(0);
			writeC(0);
			writeC(32);
			writeH(0);
			break;
		// case MSG_WAR_INITIATIVE:
		// case MSG_WAR_OCCUPY:
		case MSG_MARRIED:
		case MSG_FEEL_GOOD:
		case MSG_CANT_LOGOUT:
		case LOGOUT:
		case FISHING:
			break;

		/*
		 * case CALL_SOMETHING: this.callSomething();
		 */

		case DEL_ICON: // 消除圖示
			writeH(0x0000);
			break;

		default:
			break;
		}
	}

	public S_PacketBox(final int subCode, final int value, final String name) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case MSG_RANK_CHANGED: // 你的階級變更為%s
			writeC(value);
			writeS(name);
			break;
		}
	}

	public S_PacketBox(final int subCode, final int value) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case DMG:
		case TGDMG:
			final int time = value >> 2;
			writeC(time); // time >> 2
			break;
		case SOULTOWERSTART:// 屍魂塔下層開始
			writeD(value); // time
			writeH(0x34dc); // 定值不用改
			writeH(0x00);
			break;
		case SOULTOWEREND:// 屍魂塔下層結束
			writeD(0);
			writeH(value);// end time
			writeH(0x00);
			break;
		case ICON_BLUEPOTION:
		case ICON_CHATBAN:
		case ICON_I2H:
		case ICON_POLYMORPH:
			writeH(value); // time
			break;

		/*
		 * case MSG_WAR_BEGIN: case MSG_WAR_END: case MSG_WAR_GOING:
		 * this.writeC(value); // castle id this.writeH(0); // ? break;
		 */

		case WISDOM_POTION:
			writeC(0x2c);
			writeH(value); // time
			break;

		case MSG_SMS_SENT:
		case WEIGHT:
		case FOOD:
		case PROTECTOR_ICON: // 守護者系統 by terry0412
		case EVASION_UPDATE: // 迴避率更新封包 by terry0412
			writeC(value);
			break;

		case MSG_ELF:
			// case MSG_RANK_CHANGED: // 變更位置 by terry0412
		case MSG_COLOSSEUM:
			writeC(value); // msg id
			break;
		case 88:
			writeC(value);
			writeC(0);
			break;
		case 101:
			writeC(value);
			break;

		/*
		 * case MSG_LEVEL_OVER: writeC(0); // ? writeC(value); // 0-49以外は表示されない
		 * break;
		 */

		/*
		 * case COOK_WINDOW: this.writeC(0xdb); // ? this.writeC(0x31);
		 * this.writeC(0xdf); this.writeC(0x02); this.writeC(0x01);
		 * this.writeC(value); // level break;
		 */

		case DOLL:
			writeH(value);
			break;

		case MAP_LIMIT_TIMER: // 地圖限制時間 by terry0412
			writeD(value);
			break;

		case WEAPON_DURABILITY: // 武器損壞度更新 by terry0412
			writeC(value);
			break;

		case 121: // ???
			writeD(value);
			break;

		case PLEDGE_EMBLEM_STATUS:
			writeC(1);
			if (value == 0) { // 0:關閉 1:開啟
				writeC(0);
			} else if (value == 1) {
				writeC(1);
			}
			writeD(0x00);
			break;

		default:
			// this.writeH(value); // time
			break;
		}
	}

	public S_PacketBox(final int subCode, final int type, final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ICON_COOKING:
			writeC(0x12);
			writeC(0x0c);
			writeC(0x0c);
			writeC(0x07);
			writeC(0x12);
			writeC(0x08);
			writeH(0x0000); // 饱和度 值:2000，饱和度100%
			writeC(type); // 类型
			writeC(0x2a);
			writeH(time); // 时间
			writeC(0x0); // 负重度 值:242，负重度100%
			break;
		/*
		 * case ICON_COOKING: if (type != 7) { this.writeC(0x0c);
		 * this.writeC(0x0c); this.writeC(0x0c); this.writeC(0x12);
		 * this.writeC(0x0c); this.writeC(0x09); this.writeC(0x00);
		 * this.writeC(0x00); this.writeC(type); this.writeC(0x24);
		 * this.writeH(time); this.writeH(0x00); } else { this.writeC(0x0c);
		 * this.writeC(0x0c); this.writeC(0x0c); this.writeC(0x12);
		 * this.writeC(0x0c); this.writeC(0x09); this.writeC(0xc8);
		 * this.writeC(0x00); this.writeC(type); this.writeC(0x26);
		 * this.writeH(time); this.writeC(0x3e); this.writeC(0x87); } break;
		 */

		case MSG_DUEL:
			writeD(type); // 相手のオブジェクトID
			writeD(time); // 自分のオブジェクトID
			break;

		case 12:
			if (type == 0) {
				writeC(time);
				writeC(100);
				writeC(type);
			} else if (type == 1) {
				writeC(0);
				writeC(time);
				writeC(type);
			}
			break;

		case 20:
			writeC(type);
			writeH(time);
			break;

		case 21:
			writeC(time);
			writeC(type);
			break;

		case 22:
			writeH(type);
			writeH(time);
			break;

		case BLUE_EXPREPLY: // 藍色殷海薩 by terry0412
			writeC(0x70);
			writeC(0x01); // ?
			writeC(type); // 0x01 or 0x02
			writeH(time);
			break;

		case DRAGON_BLOOD_ICON: // 龍之血痕印記
			writeC(type);
			writeH(time);
			break;

		case 114:
			writeD(type);
			writeD(time);
			break;

		case APPRENTICE_ICON: // 師徒系統 - 狀態圖示
			writeC(type);
			writeC(time);
			break;

		case WEAPON_BUFF_ICON: // 武器加持狀態圖示 (ex.擬似魔法武器, 暗影之牙)
			writeH(time);
			writeH(type);
			break;

		case ITEM_STATUS_RENEW: // 立即更新道具狀態 - 鑑定道具用 (ex.無法強化, 無法刪除)
			writeD(type);
			writeC(time);
			break;

		default:
			break;
		}
	}

	public S_PacketBox(final int subCode, final String name) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ADD_EXCLUDE:
		case REM_EXCLUDE:
		case MSG_TOWN_LEADER:
		case ALLIANCE_LIST: // 同盟系統 - 顯示清單 by terry0412
		case HTML_PLEDGE_REALEASE_ANNOUNCE: // 3.8 血盟查詢盟友 (寫入公告)
			writeS(name);
			break;

		default:
			break;
		}
	}

	public S_PacketBox(final int subCode, final Object[] names) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ADD_EXCLUDE2:
			writeC(names.length);
			for (final Object name : names) {
				writeS(name.toString());
			}
			break;
		case MSG_WAR_OCCUPY_ALL:
			writeC(names.length);
			for (final Object name : names) {
				writeS(name.toString());
			}
			break;
		case MSG_WAR_IS_GOING_ALL:
			writeC(names.length);
			for (final Object name : names) {
				writeS(name.toString());
			}
			break;
		case HTML_PLEDGE_MEMBERS:
			writeH(names.length);
			for (final Object name : names) {
				final L1PcInstance pc = (L1PcInstance) name;
				writeS(pc.getName());
			}
			break;
		case HTML_PLEDGE_ONLINE_MEMBERS:
			writeH(names.length);
			for (final Object name : names) {
				final L1PcInstance pc = (L1PcInstance) name;
				writeS(pc.getName());
				writeC(1);
			}
			break;
		default:
			break;
		}
	}

	public S_PacketBox(final int subCode, final int value, final boolean show) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case TOMAHAWK:
			writeC(show ? 0x01 : 0x00); // On Off
			writeD(value);
			break;
		}
	}

	public S_PacketBox(final int subCode, final int range, final int type, final boolean check) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(subCode);
		switch (subCode) {
		case WEAPON_RANGE:
			writeC(range);
			writeC(type);
			writeC(check ? 1 : 0);
			writeH(0);
			break;
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public byte[] getContentBIG5() { //20240901
		if (_byte == null) {
			_byte = _bao3.toByteArray();
		}
		return _byte;
	}
	
	@Override
	public byte[] getContentGBK() { //20240901
		if (_byte == null) {
			_byte = _bao5.toByteArray();
		}
		return _byte;
	}
}
