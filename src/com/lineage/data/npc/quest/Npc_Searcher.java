package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv45_1;
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
 * 調查員-巨人<BR>
 * 71092<BR>
 * <BR>
 * 調查員<BR>
 * 71093<BR>
 * <BR>
 * 騎士的證明 (騎士45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Searcher extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Searcher.class);

	private Npc_Searcher() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Searcher();
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
					// 如果到達歐瑞村時，我必需要馬上去找迪嘉勒廷公爵
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk2"));

				} else {
					// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));
				}
				return;
			}

			// 對話動作
			npc.onTalkAction(pc);

			if (pc.isCrown()) {// 王族
				// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));

			} else if (pc.isKnight()) {// 騎士
				// 任務已經完成
				if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
					// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= KnightLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(KnightLv45_1.QUEST.get_id())) {
					case 4:// 達到4
					case 5:// 達到5
							// 接受調查員的要求
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk1"));
						break;

					default:// 其他
						// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));
						break;
					}

				} else {
					// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));
				}

			} else if (pc.isElf()) {// 精靈
				// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));

			} else if (pc.isWizard()) {// 法師
				// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));

			} else {
				// 我必須趕緊去找迪嘉勒廷公爵...真是糟糕！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk4"));
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
		if (pc.isKnight()) {// 騎士
			// 任務已經完成
			if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("start")) {// 接受調查員的要求
				final L1Npc l1npc = NpcTable.get().getTemplate(KnightLv45_1._searcher2id);
				// 召喚跟隨者
				new L1FollowerInstance(l1npc, npc, pc);
				// 提升任務進度
				pc.getQuest().set_step(KnightLv45_1.QUEST.get_id(), 5);
				// 如果到達歐瑞村時，我必需要馬上去找迪嘉勒廷公爵
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "searcherk2"));
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
			if (npc.getNpcId() != KnightLv45_1._searcher2id) {// 調查員
				return;
			}
			if (pc == null) {// 主人為空
				return;
			}
			// 取回範圍物件
			for (final L1Object object : World.get().getVisibleObjects(npc)) {
				if (object instanceof L1NpcInstance) {
					final L1NpcInstance tgnpc = (L1NpcInstance) object;
					if (tgnpc.getNpcTemplate().get_npcId() == KnightLv45_1._guardid) { // 找到公爵的士兵
						// 自己與主人距離小於3
						if (npc.getLocation().getTileLineDistance(pc.getLocation()) < 3) {
							// 主人與公爵的士兵距離小於3
							if (tgnpc.getLocation().getTileLineDistance(pc.getLocation()) < 3) {
								// 提升任務進度
								pc.getQuest().set_step(KnightLv45_1.QUEST.get_id(), 6);
								// 取得任務道具
								if (!pc.getInventory().checkItem(40593)) {
									CreateNewItem.getQuestItem(pc, npc, 40593, 1);// 調查簿的缺頁
																					// x
																					// 1
								}
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
