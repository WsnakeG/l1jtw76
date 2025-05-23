package com.lineage.data.npc.other;

import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.lock.BoardReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_Board;
import com.lineage.server.serverpackets.S_BoardRead;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Board;

/**
 * 80006 佈告欄 81126 佈告欄 81127 佈告欄 81128 佈告欄 81129 佈告欄 81130 佈告欄 81201 結婚式 揭示板
 * 
 * @author loli
 */
public class Npc_Board extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Board.class);

	/**
	 *
	 */
	private Npc_Board() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Board();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			pc.sendPackets(new S_Board(npc));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {

			final boolean isCloseList = false;

			if (cmd.equalsIgnoreCase("n")) { // 清單(下一頁)
				final L1Board boardInfo = BoardReading.get().getBoardTable((int) amount);
				if (boardInfo != null) {
					pc.sendPackets(new S_Board(npc, (int) amount));
				}

			} else if (cmd.equalsIgnoreCase("r")) { // 觀看
				final L1Board boardInfo = BoardReading.get().getBoardTable((int) amount);
				if (boardInfo != null) {
					pc.sendPackets(new S_BoardRead((int) amount));

				} else {
					// 1,243：信件已被刪除了。
					pc.sendPackets(new S_ServerMessage(1243));
				}

			} else if (cmd.equalsIgnoreCase("d")) { // 刪除
				final L1Board boardInfo = BoardReading.get().getBoardTable((int) amount);
				if (boardInfo != null) {
					BoardReading.get().deleteTopic((int) amount);

				} else {
					// 1,243：信件已被刪除了。
					pc.sendPackets(new S_ServerMessage(1243));
				}

			} else if (cmd.equalsIgnoreCase("w")) { // 書寫
				final String title = pc.get_board_title();
				final String content = pc.get_board_content();
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, 300)) {
					final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					BoardReading.get().writeTopic(pc, sdf.format(System.currentTimeMillis()), title, content);
					pc.set_board_title(null);
					pc.set_board_content(null);

				} else {
					// 189 \f1金幣不足。
					pc.sendPackets(new S_ServerMessage(189));
				}
			}

			if (isCloseList) {
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
