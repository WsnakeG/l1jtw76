package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.SILENCE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;
import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON_SILENCE;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRecord;
import com.lineage.echo.ClientExecutor;
import com.lineage.echo.to.TServer;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.LogChatReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_ChatGlobal;
import com.lineage.server.serverpackets.S_ChatTransaction;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.world.World;

/**
 * 要求使用廣播聊天頻道
 * 
 * @author daien
 */
public class C_ChatGlobal extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ChatGlobal.class);

	/*
	 * public C_ChatGlobal() { } public C_ChatGlobal(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			boolean isStop = false;// 停止輸出

			boolean errMessage = false;// 異常訊息

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

			if (isStop) {
				if (errMessage) {
					pc.sendPackets(new S_ServerMessage(242));
				}
				return;
			}

			if (!pc.isGm()) {
				// 等級 %0 以下的角色無法使用公頻或買賣頻道。
				if (pc.getLevel() < ConfigAlt.GLOBAL_CHAT_LEVEL) {
					pc.sendPackets(new S_ServerMessage(195, String.valueOf(ConfigAlt.GLOBAL_CHAT_LEVEL)));
					return;
				}

				// 管理者有非常重要的事項公告，請見諒。
				if (!World.get().isWorldChatElabled()) {
					pc.sendPackets(new S_ServerMessage(510));
					return;
				}
			}

			if ((ConfigOther.SET_GLOBAL_TIME > 0) && !pc.isGm()) {
				final Calendar cal = Calendar.getInstance();
				final long time = cal.getTimeInMillis() / 1000;// 換算為秒
				if ((time - pc.get_global_time()) < ConfigOther.SET_GLOBAL_TIME) {
					return;
				}
				pc.set_global_time(time);
			}

			// 取回對話內容
			final int chatType = readC();
			String chatText = readS();
			// 修正對話出現太長的字串會斷線 start
			if ((chatText != null) && (chatText.length() > 52)) {
				chatText = chatText.substring(0, 52);
				// 修正對話出現太長的字串會斷線 end
			}

			switch (chatType) {
			case 0x03: // 廣播頻道
				chatType_3(pc, chatText);
				break;

			case 0x0c: // 交易頻道
				chatType_12(pc, chatText);
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
	 * 交易頻道($)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_12(final L1PcInstance pc, final String chatText) {
		S_ChatTransaction packet = null;
		String name = pc.getName();
		if (pc.get_outChat() == null) {
			packet = new S_ChatTransaction(pc, chatText);

		} else {
			packet = new S_ChatTransaction(pc.get_outChat(), chatText);
			name = pc.get_outChat().getNameId();
		}

		for (final L1PcInstance listner : World.get().getAllPlayers()) {
			// 拒絕接收該人物訊息
			if (listner.getExcludingList().contains(name)) {
				continue;
			}
			// 拒絕接收廣播頻道
			if (!listner.isShowTradeChat()) {
				continue;
			}
			listner.sendPackets(packet);
		}

		if (ConfigRecord.LOGGING_CHAT_BUSINESS) {
			LogChatReading.get().noTarget(pc, chatText, 12);
		}
	}

	/**
	 * 廣播頻道(&)
	 * 
	 * @param pc
	 * @param chatText
	 */
	private void chatType_3(final L1PcInstance pc, final String chatText) {
		// 畫面中央顯示訊息 by terry0412
		if (pc.isGm()) {
			World.get().broadcastPacketToAll(new S_BlueMessage(166, "\\f3" + chatText));
		}

		S_ChatGlobal packet = null;
		String name = pc.getName();
		if (pc.get_outChat() == null) {
			packet = new S_ChatGlobal(pc, chatText);
			if (pc.isGm()) {
				World.get().broadcastPacketToAll(packet);
				return;
			}

		} else {
			packet = new S_ChatGlobal(pc.get_outChat(), chatText);
			name = pc.get_outChat().getNameId();
		}

		if (!pc.isGm()) {
			// 廣播扣除金幣或是飽食度(0:飽食度 其他:指定道具編號)
			// 廣播扣除質(set_global設置0:扣除飽食度量 set_global設置其他:扣除指定道具數量)
			switch (ConfigOther.SET_GLOBAL) {
			case 0: // 飽食度
				if (pc.get_food() >= 6) {
					pc.set_food(pc.get_food() - ConfigOther.SET_GLOBAL_COUNT);
					pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));

				} else {
					// 你太過於饑餓以致於無法談話。
					pc.sendPackets(new S_ServerMessage(462));
					return;
				}
				break;

			default: // 指定道具
				final L1ItemInstance item = pc.getInventory().checkItemX(ConfigOther.SET_GLOBAL,
						ConfigOther.SET_GLOBAL_COUNT);
				if (item != null) {
					pc.getInventory().removeItem(item, ConfigOther.SET_GLOBAL_COUNT);// 刪除指定道具

				} else {
					// 找回物品
					final L1Item itemtmp = ItemTable.get().getTemplate(ConfigOther.SET_GLOBAL);
					pc.sendPackets(new S_ServerMessage(337, itemtmp.getNameId()));
					return;
				}
				break;
			}
		}

		for (final L1PcInstance listner : World.get().getAllPlayers()) {
			// 拒絕接收該人物訊息
			if (listner.getExcludingList().contains(name)) {
				continue;
			}
			// 拒絕接收廣播頻道
			if (!listner.isShowWorldChat()) {
				continue;
			}
			listner.sendPackets(packet);
		}

		try {
			// ConfigDescs.get(2) = 服務器名稱
			String text = null;
			if (pc.isGm()) {
				text = "[******] " + chatText;
			} else {
				text = "<" + Config.SERVERNAME + ">" + "[" + pc.getName() + "] " + chatText;
			}
			TServer.get().outServer(text.getBytes("utf-8"));

		} catch (final UnsupportedEncodingException e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		if (ConfigRecord.LOGGING_CHAT_WORLD) {
			LogChatReading.get().noTarget(pc, chatText, 3);
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
