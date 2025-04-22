package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.EWLv40_1;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1FollowerInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;

/**
 * 羅伊-人形殭屍<BR>
 * 81209<BR>
 * <BR>
 * 羅伊<BR>
 * 70957<BR>
 * <BR>
 * 說明:幫助羅伊逃脫(等級40以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Roi extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Roi.class);

	private Npc_Roi() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Roi();
	}

	@Override
	public int type() {
		return 7;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (npc.getMaster() != null) {
				if (npc.getMaster().equals(pc)) {// 是主人
					// 請快點...不能再停頓了...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roi2"));

				} else {
					// 我們是為了救援亞丁公主，被派遣來的亞丁『秘密趕死隊』
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roi"));
				}
				return;
			}
			// 幫助羅伊逃脫-任務已經完成
			if (pc.getQuest().isEnd(EWLv40_1.QUEST.get_id())) {
				// 我們是為了救援亞丁公主，被派遣來的亞丁『秘密趕死隊』
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roi"));
				return;
			}
			// 等級達成要求
			if (pc.getLevel() >= EWLv40_1.QUEST.get_questlevel()) {
				// 將羅伊帶給巴休
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roi1"));

			} else {
				// 我們是為了救援亞丁公主，被派遣來的亞丁『秘密趕死隊』
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roi"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (npc.getMaster() != null) {
			return;
		}
		// 幫助羅伊逃脫-任務已經完成
		if (pc.getQuest().isEnd(EWLv40_1.QUEST.get_id())) {
			// 我們是為了救援亞丁公主，被派遣來的亞丁『秘密趕死隊』
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roi"));
			return;
		}
		// 等級達成要求
		if (pc.getLevel() >= EWLv40_1.QUEST.get_questlevel()) {
			if (cmd.equalsIgnoreCase("start")) {// 將羅伊帶給巴休
				final L1Npc l1npc = NpcTable.get().getTemplate(EWLv40_1._roi2id);
				// 召喚跟隨者
				new L1FollowerInstance(l1npc, npc, pc);
				// 將任務設置為啟動
				QuestClass.get().startQuest(pc, EWLv40_1.QUEST.get_id());
				isCloseList = true;
			}

		} else {
			// 我們是為了救援亞丁公主，被派遣來的亞丁『秘密趕死隊』
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roi"));
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	@Override
	public void attack(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (npc.getNpcId() != EWLv40_1._roi2id) {// 羅伊
				return;
			}
			if (pc == null) {// 主人為空
				return;
			}
			// 取回範圍物件
			for (final L1Object object : World.get().getVisibleObjects(npc)) {
				if (object instanceof L1NpcInstance) {
					final L1NpcInstance tgnpc = (L1NpcInstance) object;
					if (tgnpc.getNpcTemplate().get_npcId() == EWLv40_1._baschid) { // 找到巴休
						// 自己與主人距離小於3
						if (npc.getLocation().getTileLineDistance(pc.getLocation()) < 3) {
							// 主人與巴休距離小於3
							if (tgnpc.getLocation().getTileLineDistance(pc.getLocation()) < 3) {
								// 任務完成
								pc.getQuest().set_end(EWLv40_1.QUEST.get_id());
								// 取得任務道具
								CreateNewItem.getQuestItem(pc, npc, 41003, 1);// 羅伊的袋子
																				// x
																				// 1
								npc.setParalyzed(true);
								npc.deleteMe();
							}
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
