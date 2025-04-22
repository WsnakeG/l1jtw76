package com.lineage.data.npc.quest;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv50_1;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.Instance.L1FollowerInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.L1SpawnUtil;

/**
 * 迪嘉勒廷的女間諜<BR>
 * 80012<BR>
 * <BR>
 * 說明:協助間諜大逃亡 (妖精50級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Dspy extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Dspy.class);

	private Npc_Dspy() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Dspy();
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
					// 呼，託你的幫忙讓我獲救了，真的很謝謝你。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy3"));

				} else {
					// 啊啊！！該怎麼辦才好呢...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
				}
				return;
			}

			// 對話動作
			npc.onTalkAction(pc);

			if (pc.isCrown()) {// 王族
				// 啊啊！！該怎麼辦才好呢...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));

			} else if (pc.isKnight()) {// 騎士
				// 啊啊！！該怎麼辦才好呢...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));

			} else if (pc.isElf()) {// 精靈
				// 任務已經完成
				if (pc.getQuest().isEnd(ElfLv50_1.QUEST.get_id())) {
					// 啊啊！！該怎麼辦才好呢...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= ElfLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(ElfLv50_1.QUEST.get_id())) {
					case 3:// 達到3
					case 4:// 達到4
							// 我受到迪嘉勒廷公爵的指示來保護你，我會帶你到安全的地方。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy2"));
						break;

					default:// 其他
						// 啊啊！！該怎麼辦才好呢...
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
						break;
					}

				} else {
					// 啊啊！！該怎麼辦才好呢...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
				}

			} else if (pc.isWizard()) {// 法師
				// 啊啊！！該怎麼辦才好呢...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 啊啊！！該怎麼辦才好呢...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 啊啊！！該怎麼辦才好呢...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 啊啊！！該怎麼辦才好呢...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));

			} else {
				// 啊啊！！該怎麼辦才好呢...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy1"));
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
		if (pc.isElf()) {// 精靈
			// 任務已經完成
			if (pc.getQuest().isEnd(ElfLv50_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("start")) {// 我受到迪嘉勒廷公爵的指示來保護你，我會帶你到安全的地方。
				final L1Npc l1npc = NpcTable.get().getTemplate(ElfLv50_1._npcId);
				// 召喚跟隨者
				new L1FollowerInstance(l1npc, npc, pc);
				// 提升任務進度
				pc.getQuest().set_step(ElfLv50_1.QUEST.get_id(), 4);
				// 呼，託你的幫忙讓我獲救了，真的很謝謝你。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspy3"));
			}

		} else {
			isCloseList = true;
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	@Override
	public void attack(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (npc.getNpcId() != ElfLv50_1._npcId) {// 迪嘉勒廷的女間諜
				return;
			}
			if (pc == null) {// 主人為空
				return;
			}

			if (((pc.getX() >= 32557) && (pc.getX() <= 32576)) // 抵抗軍村莊
					&& ((pc.getY() >= 32656) && (pc.getY() <= 32687)) && (pc.getMapId() == 400)) {
				// 提升任務進度
				pc.getQuest().set_step(ElfLv50_1.QUEST.get_id(), 5);
				// 取得任務道具
				if (!pc.getInventory().checkItem(49163)) {
					CreateNewItem.getQuestItem(pc, npc, 49163, 1);// 密封的情報書
				}
				npc.setParalyzed(true);
				npc.deleteMe();

			} else {
				spawnAssassin(pc, npc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static Random _random = new Random();

	/**
	 * 召喚魔族暗殺團
	 * 
	 * @param npc
	 */
	private void spawnAssassin(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (_random.nextInt(100) <= 2) {
				final L1Location loc = npc.getLocation().randomLocation(4, false);
				// 登場效果
				pc.sendPacketsX8(new S_EffectLocation(loc, 3992));
				// 魔族暗殺團
				final L1MonsterInstance mob = L1SpawnUtil.spawnX(80011, loc, npc.get_showId());
				mob.setLink(npc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
