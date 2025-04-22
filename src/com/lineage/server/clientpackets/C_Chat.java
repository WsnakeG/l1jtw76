package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.BROADCAST_CARD;
import static com.lineage.server.model.skill.L1SkillId.CHAT_STOP;
import static com.lineage.server.model.skill.L1SkillId.SILENCE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;
import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON_SILENCE;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigRecord;
import com.lineage.data.event.BroadcastSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.BroadcastController;
import com.lineage.server.command.GMCommands;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.ClanAllianceReading;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.datatables.lock.LogChatReading;
import com.lineage.server.model.L1Alliance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_CharTitle;
import com.lineage.server.serverpackets.S_Chat;
import com.lineage.server.serverpackets.S_ChatClan;
import com.lineage.server.serverpackets.S_ChatClanAlliance;
import com.lineage.server.serverpackets.S_ChatClanUnion;
import com.lineage.server.serverpackets.S_ChatParty;
import com.lineage.server.serverpackets.S_ChatParty2;
import com.lineage.server.serverpackets.S_ChatShouting;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_GmMessage;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_NpcChatShouting;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 要求使用一般聊天頻道
 * 
 * @author daien
 */
public class C_Chat extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Chat.class);

	private int _error = 3;

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			// 取回對話內容
			final int chatType = readC();
			String chatText = readS();
			final S_GmMessage gm = new S_GmMessage(pc, null, chatType, chatText);
			// 修正對話出現太長的字串會斷線 start
			if ((chatText != null) && (chatText.length() > 52)) {
				chatText = chatText.substring(0, 52);
				// 修正對話出現太長的字串會斷線 end
			}

			for (final L1PcInstance pca : World.get().getAllPlayers()) {
				if (pca.isGm() && (pca != pc)) {
					pca.sendPackets(gm);
				}
			}

			boolean isStop = false;// 停止輸出

			boolean errMessage = false;// 異常訊息

			// AI驗證
			checkAI(pc, chatText, client);

			// 中毒狀態
			if (pc.hasSkillEffect(SILENCE)) {
				if (!pc.isGm()) {
					isStop = true;
				}
			}

			// 中毒狀態
			if (pc.hasSkillEffect(AREA_OF_SILENCE)) {
				if (!pc.isGm()) {
					isStop = true;
				}
			}

			// 中毒狀態
			if (pc.hasSkillEffect(STATUS_POISON_SILENCE)) {
				if (!pc.isGm()) {
					isStop = true;
				}
			}

			// 你從現在被禁止閒談。
			if (pc.hasSkillEffect(STATUS_CHAT_PROHIBITED)) {
				isStop = true;
				errMessage = true;
			}

			// 你從現在被禁止閒談。
			if (pc.hasSkillEffect(CHAT_STOP)) {
				isStop = true;
				errMessage = true;
			}

			if (isStop) {
				if (errMessage) {
					pc.sendPackets(new S_ServerMessage(242));
				}
				return;
			}

			switch (chatType) {
			case 0:// 一般頻道
				if (pc.is_retitle()) {
					re_title(pc, chatText.trim());
					return;
				}
				if (pc.is_repass() != 0) {
					re_repass(pc, chatText.trim());
					return;
				}
				if (pc.is_avenger()) { // 復仇卷軸
					re_avenger(pc, chatText.trim());
					return;
				}
				// 廣播卡判斷時間 by terry0412
				if (pc.hasSkillEffect(BROADCAST_CARD)) {
					pc.killSkillEffectTimer(BROADCAST_CARD);
					check_broadcast(pc, chatText); // 保留空格
					return;
				}
				chatType_0(pc, chatText);
				break;

			case 2: // 大叫頻道(!)
				chatType_2(pc, chatText);
				break;

			case 4: // 血盟頻道(@)
				chatType_4(pc, chatText);
				break;

			case 11: // 隊伍頻道(#)
				chatType_11(pc, chatText);
				break;

			case 13: // 連盟頻道(%)
				chatType_13(pc, chatText);
				break;

			case 14: // 隊伍頻道(聊天)
				chatType_14(pc, chatText);
				break;

			case 15: // 同盟頻道(~) by terry0412
				chatType_15(pc, chatText);
				break;
			}

			if (!pc.isGm()) {
				pc.checkChatInterval();
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	/**
	 * 復仇卷軸
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void re_avenger(final L1PcInstance pc, final String chatText) {
		try {
			final String newchatText = chatText.trim();
			if (newchatText.isEmpty() || (newchatText.length() <= 0)) {
				pc.sendPackets(new S_ServerMessage("\\aE請輸入欲復仇的玩家名稱"));
				return;
			}

			// 清除
			pc.re_avenger(false);

			final L1PcInstance find_pc = World.get().getPlayer(newchatText);
			if (find_pc == null) {
				pc.sendPackets(new S_ServerMessage("\\aG該玩家不在線上或是不存在此ID"));
				return;
			}

			if (!find_pc.getMap().isNormalZone(find_pc.getLocation())) {
				pc.sendPackets(new S_ServerMessage("\\aG該玩家在非一般區域內無法傳送"));
				return;
			}

			if (L1CastleLocation.checkInAllWarArea(find_pc.getX(), find_pc.getY(), find_pc.getMapId())) {
				pc.sendPackets(new S_ServerMessage("\\aG該玩家在攻城區域內無法傳送"));
				return;
			}

			if (L1HouseLocation.isInHouse(find_pc.getX(), find_pc.getY(), find_pc.getMapId())) {
				pc.sendPackets(new S_ServerMessage("\\aG該玩家在盟屋區域內無法傳送"));
				return;
			}

			/*
			 * if (!find_pc.getMap().isMarkable()) { pc.sendPackets(new
			 * S_ServerMessage("\\fU該玩家在不能到達的區域")); return; }
			 */

			pc.sendPackets(new S_ServerMessage("\\aI傳送至[" + find_pc.getName() + "]身邊成功"));
			find_pc.sendPackets(new S_ServerMessage("\\aI警告! 有人使用[復仇卷軸]飛至你身邊"));

			L1Teleport.teleport(pc, find_pc.getLocation().randomLocation(3, false), pc.getHeading(), true);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static final String _check_pwd = "abcdefghijklmnopqrstuvwxyz0123456789!_=+-?.#";

	private void re_repass(final L1PcInstance pc, final String password) {
		try {
			switch (pc.is_repass()) {
			case 1:// 輸入舊密碼
				if (!pc.getNetConnection().getAccount().get_password().equals(password)) {
					// 1,744：密碼錯誤
					pc.sendPackets(new S_ServerMessage(1744));
					return;
				}
				pc.repass(2);
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_pass_01", new String[] { "請輸入您的新密碼" }));
				break;

			case 2:// 輸入新密碼
				boolean iserr = false;
				for (int i = 0; i < password.length(); i++) {
					final String ch = password.substring(i, i + 1);
					if (!_check_pwd.contains(ch.toLowerCase())) {
						// 1,742：帳號或密碼中有無效的字元
						pc.sendPackets(new S_ServerMessage(1742));
						iserr = true;
						break;
					}
				}
				if (password.length() > 13) {
					// 1,742：帳號或密碼中有無效的字元
					pc.sendPackets(new S_ServerMessage(166, "密碼長度過長"));
					iserr = true;
				}
				if (password.length() < 3) {
					// 1,742：帳號或密碼中有無效的字元
					pc.sendPackets(new S_ServerMessage(166, "密碼長度過長"));
					iserr = true;
				}
				if (iserr) {
					return;
				}
				pc.setText(password);
				pc.repass(3);
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_pass_01", new String[] { "請確認您的新密碼" }));
				break;

			case 3:// 確認新密碼
				if (!pc.getText().equals(password)) {
					// 1,982：所輸入的密碼不一致.請重新輸入.
					pc.sendPackets(new S_ServerMessage(1982));
					return;
				}
				pc.sendPackets(new S_CloseList(pc.getId()));
				// 1,985：角色密碼成功地變更.(忘記密碼時請至天堂網站詢問)
				pc.sendPackets(new S_ServerMessage(1985));
				AccountReading.get().updatePwd(pc.getAccountName(), password);
				pc.setText(null);
				pc.repass(0);
				break;
			}

		} catch (final Exception e) {
			pc.sendPackets(new S_CloseList(pc.getId()));
			// 未知的錯誤%d
			pc.sendPackets(new S_ServerMessage(45));
			pc.setText(null);
			pc.repass(0);
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * AI驗證
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void checkAI(final L1PcInstance pc, final String chatText, final ClientExecutor client) {
		if (pc.getAIsum() == -1) {
			return;
		}
		int sum = -1;
		String msg = "";
		try {
			sum = Integer.parseInt(chatText);
		} catch (final Exception e) {
			msg = "請輸入數字！";
			pc.sendPackets(new S_SystemMessage(msg));
			return;
		}
		if (pc.getAIsum() != sum) {
			_error--;
			msg = "您的答案是 " + sum + " 還有 " + _error + " 次機會，請輸入正確答案。";
		} else {
			pc.setAIsum(-1);
			pc.setCheckAI("");
			pc.setAImsg("");
			pc.setSec(0);
			_error = 3;
			msg = "恭喜您答對了！";
			pc.killSkillEffectTimer(L1SkillId.AICHECK);
		}
		pc.sendPackets(new S_SystemMessage(msg));
		if (_error <= 0) {
			IpReading.get().add(pc.getAccountName(), "AI系統自動封鎖");
			client.kick();
			return;
		}

	}

	/**
	 * 變更封號
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void re_title(final L1PcInstance pc, final String chatText) {
		try {
			final String newchatText = chatText.trim();
			if (newchatText.isEmpty() || (newchatText.length() <= 0)) {
				pc.sendPackets(new S_ServerMessage("\\aI請輸入封號內容"));
				return;
			}
			final int length = Config.LOGINS_TO_AUTOENTICATION ? 18 : 13;
			if (newchatText.getBytes().length > length) {
				pc.sendPackets(new S_ServerMessage("\\aI封號長度過長"));
				return;
			}
			final StringBuilder title = new StringBuilder();
			title.append(newchatText);

			pc.setTitle(title.toString());
			pc.sendPacketsAll(new S_CharTitle(pc.getId(), title));
			pc.save();
			pc.retitle(false);
			pc.sendPackets(new S_ServerMessage("\\aH封號變更完成"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 同盟頻道(~) by terry0412
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_15(final L1PcInstance pc, final String chatText) {
		if (pc.getClanid() != 0) {
			final L1Clan clan = pc.getClan();
			if (clan == null) {
				return;
			}

			// 取得指定同盟資料
			final L1Alliance alliance = ClanAllianceReading.get().getAlliance(clan.getClanId());
			if (alliance == null) {
				return;
			}

			final S_ChatClanAlliance chatpacket = new S_ChatClanAlliance(pc, clan.getClanName(), chatText);

			// 對所有締結血盟線上成員發送封包 (遮蔽特定玩家)
			alliance.sendPacketsAll(pc.getName(), chatpacket);

			if (ConfigRecord.LOGGING_CHAT_COMBINED) {
				LogChatReading.get().noTarget(pc, chatText, 15);
			}
		}
	}

	/**
	 * 隊伍頻道(聊天)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_14(final L1PcInstance pc, final String chatText) {
		if (pc.isInChatParty()) {
			final S_ChatParty2 chatpacket = new S_ChatParty2(pc, chatText);
			final L1PcInstance[] partyMembers = pc.getChatParty().getMembers();
			for (final L1PcInstance listner : partyMembers) {
				if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(chatpacket);
				}
			}

			if (ConfigRecord.LOGGING_CHAT_CHAT_PARTY) {
				LogChatReading.get().noTarget(pc, chatText, 14);
			}
		}
	}

	/**
	 * 連盟頻道(%)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_13(final L1PcInstance pc, final String chatText) {
		if (pc.getClanid() != 0) {
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan == null) {
				return;
			}
			switch (pc.getClanRank()) {
			case L1Clan.ALLIANCE_CLAN_RANK_GUARDIAN:// 6:守護騎士
			case L1Clan.NORMAL_CLAN_RANK_GUARDIAN:// 9:守護騎士
			case L1Clan.CLAN_RANK_GUARDIAN:// 3:副君主
			case L1Clan.CLAN_RANK_PRINCE:// 4:聯盟君主
			case L1Clan.NORMAL_CLAN_RANK_PRINCE:// 10:聯盟君主
				final S_ChatClanUnion chatpacket = new S_ChatClanUnion(pc, chatText);
				final L1PcInstance[] clanMembers = clan.getOnlineClanMember();
				for (final L1PcInstance listner : clanMembers) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						switch (listner.getClanRank()) {
						case L1Clan.ALLIANCE_CLAN_RANK_GUARDIAN:// 6:守護騎士
						case L1Clan.NORMAL_CLAN_RANK_GUARDIAN:// 9:守護騎士
						case L1Clan.CLAN_RANK_GUARDIAN:// 3:副君主
						case L1Clan.CLAN_RANK_PRINCE:// 4:聯盟君主
						case L1Clan.NORMAL_CLAN_RANK_PRINCE:// 10:聯盟君主
							listner.sendPackets(chatpacket);
							break;
						}
					}
				}

				if (ConfigRecord.LOGGING_CHAT_COMBINED) {
					LogChatReading.get().noTarget(pc, chatText, 13);
				}
				break;
			}
		}
	}

	/**
	 * 隊伍頻道(#)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_11(final L1PcInstance pc, final String chatText) {
		if (pc.isInParty()) {
			final S_ChatParty chatpacket = new S_ChatParty(pc, chatText);

			final List<L1PcInstance> pcs = pc.getParty().getMemberList();

			if (pcs.isEmpty()) {
				return;
			}
			if (pcs.size() <= 0) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = pcs.iterator(); iter.hasNext();) {
				final L1PcInstance listner = iter.next();
				if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(chatpacket);
				}
			}

			if (ConfigRecord.LOGGING_CHAT_PARTY) {
				LogChatReading.get().noTarget(pc, chatText, 11);
			}
		}
	}

	/**
	 * 血盟頻道(@)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_4(final L1PcInstance pc, final String chatText) {
		if (pc.getClanid() != 0) {
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				final S_ChatClan chatpacket = new S_ChatClan(pc, chatText);
				final L1PcInstance[] clanMembers = clan.getOnlineClanMember();
				for (final L1PcInstance listner : clanMembers) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						listner.sendPackets(chatpacket);
					}
				}

				if (ConfigRecord.LOGGING_CHAT_CLAN) {
					LogChatReading.get().noTarget(pc, chatText, 4);
				}
			}
		}
	}

	/**
	 * 大叫頻道(!)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_2(final L1PcInstance pc, final String chatText) {
		if (pc.isGhost()) {
			return;
		}
		S_ChatShouting chatpacket = null;

		String name = pc.getName();
		if (pc.get_outChat() == null) {
			chatpacket = new S_ChatShouting(pc, chatText);

		} else {
			chatpacket = new S_ChatShouting(pc.get_outChat(), chatText);
			name = pc.get_outChat().getNameId();
		}
		pc.sendPackets(chatpacket);
		for (final L1PcInstance listner : World.get().getVisiblePlayer(pc, 50)) {
			if (!listner.getExcludingList().contains(name)) {
				// 副本ID相等
				if (pc.get_showId() == listner.get_showId()) {
					listner.sendPackets(chatpacket);
				}
			}
		}

		if (ConfigRecord.LOGGING_CHAT_SHOUT) {
			LogChatReading.get().noTarget(pc, chatText, 2);
		}
		// 變形怪重複對話
		doppelShouting(pc, chatText);
	}

	/**
	 * 一般頻道
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_0(final L1PcInstance pc, final String chatText) {
		if (pc.isGhost() && !(pc.isGm() || pc.isMonitor())) {
			return;
		}
		if (pc.getAccessLevel() > 0) {
			// GM命令
			if (chatText.startsWith(".")) {
				final String cmd = chatText.substring(1);
				GMCommands.getInstance().handleCommands(pc, cmd);
				return;
			}
		}

		// 產生封包
		S_Chat chatpacket = null;

		String name = pc.getName();
		if (pc.get_outChat() == null) {
			chatpacket = new S_Chat(pc, chatText);

		} else {
			chatpacket = new S_Chat(pc.get_outChat(), chatText);
			name = pc.get_outChat().getNameId();
		}
		pc.sendPackets(chatpacket);

		for (final L1PcInstance listner : World.get().getRecognizePlayer(pc)) {
			if (!listner.getExcludingList().contains(name)) {
				// 副本ID相等
				if (pc.get_showId() == listner.get_showId()) {
					listner.sendPackets(chatpacket);
				}
			}
		}

		// 對話紀錄
		if (ConfigRecord.LOGGING_CHAT_NORMAL) {
			LogChatReading.get().noTarget(pc, chatText, 0);
		}
		// 變形怪重複對話
		doppelGenerally(pc, chatText);
	}

	/**
	 * 變形怪重複對話(一般頻道)
	 * 
	 * @param pc
	 * @param chatType
	 * @param chatText
	 */
	private void doppelGenerally(final L1PcInstance pc, final String chatText) {
		// 變形怪重複對話
		for (final L1Object obj : pc.getKnownObjects()) {
			if (obj instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) obj;
				if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
					mob.broadcastPacketX8(new S_NpcChat(mob, chatText));
				}
			}
		}
	}

	/**
	 * 變形怪重複對話(大喊頻道)
	 * 
	 * @param pc
	 * @param chatType
	 * @param chatText
	 */
	private void doppelShouting(final L1PcInstance pc, final String chatText) {
		// 變形怪重複對話
		for (final L1Object obj : pc.getKnownObjects()) {
			if (obj instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) obj;
				if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
					mob.broadcastPacketX8(new S_NpcChatShouting(mob, chatText));
				}
			}
		}
	}

	/**
	 * 廣播卡判斷時間 by terry0412
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void check_broadcast(final L1PcInstance pc, final String chatText) {
		try {
			if (chatText.isEmpty() || (chatText.length() <= 0)) {
				pc.sendPackets(new S_SystemMessage("請輸入訊息內容。"));
				return;
			}

			// GM可使用指令進行開關
			if (pc.isGm()) {
				if (chatText.equals("開啟")) {
					BroadcastController.getInstance().setStop(false);
					pc.sendPackets(new S_SystemMessage("廣播系統已開啟。"));
					return;

				} else if (chatText.equals("關閉")) {
					BroadcastController.getInstance().setStop(true);
					pc.sendPackets(new S_SystemMessage("廣播系統已關閉。"));
					return;
				}
			}

			if (chatText.getBytes().length > 50) {
				pc.sendPackets(new S_SystemMessage("廣播訊息長度過長 (不能超過25個中文字)"));
				return;
			}

			// 連結字串
			final StringBuilder message = new StringBuilder();
			message.append("[").append(pc.getName()).append("] ").append(chatText);

			// 檢查背包是否有廣播卡
			final L1ItemInstance item = pc.getInventory().checkItemX(BroadcastSet.ITEM_ID, 1);
			if (item == null) {
				pc.sendPackets(new S_SystemMessage("不具有廣播卡，因此無法發送訊息。"));
				return;
			}

			// 將元素放入佇列
			if (BroadcastController.getInstance().requestWork(message.toString())) {
				// 刪除一個廣播卡道具
				pc.getInventory().removeItem(item, 1);

				pc.sendPackets(new S_SystemMessage("已成功發布廣播訊息。"));

			} else {
				pc.sendPackets(new S_SystemMessage("目前有太多等待訊息，請稍後再嘗試一次。"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}