package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRecord;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.LogChatReading;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChatWhisperFrom;
import com.lineage.server.serverpackets.S_ChatWhisperTo;
import com.lineage.server.serverpackets.S_GmMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;

/**
 * 要求使用密語聊天頻道
 * 
 * @author daien
 */
public class C_ChatWhisper extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ChatWhisper.class);

	/*
	 * public C_ChatWhisper() { } public C_ChatWhisper(final byte[] abyte0,
	 * final ClientExecutor client) { super(abyte0); try { this.start(abyte0,
	 * client); } catch (final Exception e) {
	 * _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			// 來源對象
			final L1PcInstance whisperFrom = client.getActiveChar();

			// 你從現在被禁止閒談。
			if (whisperFrom.hasSkillEffect(STATUS_CHAT_PROHIBITED)) {
				whisperFrom.sendPackets(new S_ServerMessage(242));
				return;
			}

			// 等級 %0 以下無法使用密談。
			if ((whisperFrom.getLevel() < ConfigAlt.WHISPER_CHAT_LEVEL) && !whisperFrom.isGm()) {
				whisperFrom
						.sendPackets(new S_ServerMessage(404, String.valueOf(ConfigAlt.WHISPER_CHAT_LEVEL)));
				return;
			}

			// 取回對話內容
			final String targetName = readS();
			final String text = readS();

			if (text.length() > 52) {
				_log.warn("人物:" + whisperFrom.getName() + "對話長度超過限制:" + client.getIp().toString());
				client.set_error(client.get_error() + 1);
				return;
			}

			// 目標對象
			final L1PcInstance whisperTo = World.get().getPlayer(targetName);
			L1DeInstance de = null;

			final S_GmMessage gm = new S_GmMessage(whisperFrom, whisperTo, 1, text);

			for (final L1PcInstance pca : World.get().getAllPlayers()) {
				if (pca.isGm() && ((pca != whisperFrom) && (pca != whisperTo))) {
					pca.sendPackets(gm);
				}
			}

			if (whisperTo == null) {
				de = getDe(targetName);
			}

			if (de != null) {
				whisperFrom.sendPackets(new S_ChatWhisperTo(de, text));
				return;
			}

			// \f1%0%d 不在線上。
			if (whisperTo == null) {
				whisperFrom.sendPackets(new S_ServerMessage(73, targetName));
				return;
			}

			// 對象是自己
			if (whisperTo.equals(whisperFrom)) {
				return;
			}

			// %0%s 斷絕你的密語。
			if (whisperTo.getExcludingList().contains(whisperFrom.getName())) {
				whisperFrom.sendPackets(new S_ServerMessage(117, whisperTo.getName()));
				return;
			}

			// \f1%0%d 目前關閉悄悄話。
			if (!whisperTo.isCanWhisper()) {
				whisperFrom.sendPackets(new S_ServerMessage(205, whisperTo.getName()));
				return;
			}

			whisperFrom.sendPackets(new S_ChatWhisperTo(whisperTo, text));
			whisperTo.sendPackets(new S_ChatWhisperFrom(whisperFrom, text));

			if (ConfigRecord.LOGGING_CHAT_WHISPER) {
				LogChatReading.get().isTarget(whisperFrom, whisperTo, text, 9);
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	/**
	 * 虛擬人物資料
	 * 
	 * @return
	 */
	public static L1DeInstance getDe(final String s) {
		final Collection<L1NpcInstance> allNpc = WorldNpc.get().all();
		// 不包含元素
		if (allNpc.isEmpty()) {
			return null;
		}

		for (final L1NpcInstance npc : allNpc) {
			if (npc instanceof L1DeInstance) {
				final L1DeInstance de = (L1DeInstance) npc;
				// System.out.println("de:" + de.getNameId());
				if (de.getNameId().equalsIgnoreCase(s)) {
					return de;
				}
			}
		}
		return null;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
