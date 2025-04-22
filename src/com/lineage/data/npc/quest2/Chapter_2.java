package com.lineage.data.npc.quest2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.Chapter02;
import com.lineage.data.quest.Chapter02R;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldQuest;

/**
 * @author terry0412
 */
public class Chapter_2 extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Chapter_2.class);

	private Chapter_2() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Chapter_2();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id1"));
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("enter")) {
			if (isError(pc, npc)) {
				return;
			}
			startQuest(pc);
		}
		pc.sendPackets(new S_CloseList(pc.getId()));
	}

	/**
	 * @param pc
	 * @return
	 */
	private void startQuest(final L1PcInstance pc) {
		try {
			final int questid = Chapter02.QUEST.get_id();

			final int mapid = Chapter02.MAPID;

			final int showId = WorldQuest.get().nextId();

			int users = QuestMapTable.get().getTemplate(mapid);
			if (users == -1) {
				users = Byte.MAX_VALUE;
			}

			final L1Location loc = new L1Location(32798, 32803, mapid);
			final L1Party party = pc.getParty();
			if (party != null) {
				int i = 0;
				for (final L1PcInstance otherPc : party.getMemberList()) {
					if (i <= (users - 1)) {
						if ((otherPc.getId() != party.getLeaderID())
								&& (otherPc.getMapId() == pc.getMapId())) {
							WorldQuest.get().put(showId, mapid, questid, otherPc);
							L1BuffUtil.cancelAbsoluteBarrier(otherPc);
							final L1Location new_loc = loc.randomLocation(5, true);
							L1Teleport.teleport(otherPc, new_loc.getX(), new_loc.getY(), (short) mapid,
									pc.getHeading(), true);
							L1PolyMorph.undoPoly(otherPc);

							QuestClass.get().startQuest(otherPc, Chapter02.QUEST.get_id());
						}
					}
					i++;
				}
			} else {
				return;
			}

			final L1QuestUser quest = WorldQuest.get().put(showId, mapid, questid, pc);
			if (quest == null) {
				_log.error("");
				return;
			}

			quest.set_info(false);

			final Integer time = QuestMapTable.get().getTime(mapid);
			if (time != null) {
				quest.set_time(time.intValue());
			}

			final L1NpcInstance door = L1SpawnUtil.spawn(97108, new L1Location(32799, 32806, mapid), 4,
					showId);
			door.setStatus(ActionCodes.ACTION_DoorAction1);

			final L1NpcInstance npc1 = L1SpawnUtil.spawn(97102, new L1Location(32792, 32809, mapid), 4,
					showId);
			final L1NpcInstance npc2 = L1SpawnUtil.spawn(97102, new L1Location(32803, 32809, mapid), 4,
					showId);

			L1BuffUtil.cancelAbsoluteBarrier(pc);

			final L1Location new_loc = loc.randomLocation(5, true);
			L1Teleport.teleport(pc, new_loc.getX(), new_loc.getY(), (short) mapid, pc.getHeading(), true);
			L1PolyMorph.undoPoly(pc);

			final Chapter02R chapter02R = new Chapter02R(quest, party, door, npc1, npc2);
			quest.set_orimR(chapter02R);
			chapter02R.startR();

			QuestClass.get().startQuest(pc, Chapter02.QUEST.get_id());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * @param pc
	 * @param npc
	 * @return
	 */
	private final boolean isError(final L1PcInstance pc, final L1NpcInstance npc) {
		if (pc.isGm()) {
			return false;
		}
		final L1Party party = pc.getParty();
		if (party == null) {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id1_3"));
			return true;
		}
		if (!party.isLeader(pc)) {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id1_2"));
			return true;
		}
		if ((party.getNumOfMembers() < 3) || (party.getNumOfMembers() > 5)) {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id1_1"));
			return true;
		}
		return false;
	}
}
