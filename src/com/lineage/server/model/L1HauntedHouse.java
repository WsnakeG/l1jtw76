package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 鬼屋控制項
 * 
 * @author daien
 */
public class L1HauntedHouse {
	/*
	 * private static final Log _log = LogFactory.getLog(L1HauntedHouse.class
	 * .getName());
	 */

	public static final int STATUS_NONE = 0;
	public static final int STATUS_READY = 1;
	public static final int STATUS_PLAYING = 2;

	private final ArrayList<L1PcInstance> _members = new ArrayList<L1PcInstance>();
	private int _hauntedHouseStatus = STATUS_NONE;
	private int _winnersCount = 0;
	private int _goalCount = 0;

	private static L1HauntedHouse _instance;

	public static L1HauntedHouse getInstance() {
		if (_instance == null) {
			_instance = new L1HauntedHouse();
		}
		return _instance;
	}

	private void readyHauntedHouse() {
		setHauntedHouseStatus(STATUS_READY);
		final L1HauntedHouseReadyTimer hhrTimer = new L1HauntedHouseReadyTimer();
		hhrTimer.begin();
	}

	private void startHauntedHouse() {
		setHauntedHouseStatus(STATUS_PLAYING);
		final int membersCount = getMembersCount();
		if (membersCount <= 4) {
			setWinnersCount(1);
		} else if ((5 >= membersCount) && (membersCount <= 7)) {
			setWinnersCount(2);
		} else if ((8 >= membersCount) && (membersCount <= 10)) {
			setWinnersCount(3);
		}
		for (final L1PcInstance pc : getMembersArray()) {
			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, CANCELLATION, pc.getId(), pc.getX(), pc.getY(), 0,
					L1SkillUse.TYPE_LOGIN);
			L1PolyMorph.doPoly(pc, 6284, 300, L1PolyMorph.MORPH_BY_NPC);
		}

		for (final L1Object object : World.get().getObject()) {
			if (object instanceof L1DoorInstance) {
				final L1DoorInstance door = (L1DoorInstance) object;
				if (door.getMapId() == 5140) {
					door.open();
				}
			}
		}
	}

	public void endHauntedHouse() {
		setHauntedHouseStatus(STATUS_NONE);
		setWinnersCount(0);
		setGoalCount(0);
		for (final L1PcInstance pc : getMembersArray()) {
			if (pc.getMapId() == 5140) {
				final L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(pc, CANCELLATION, pc.getId(), pc.getX(), pc.getY(), 0,
						L1SkillUse.TYPE_LOGIN);
				L1Teleport.teleport(pc, 32624, 32813, (short) 4, 5, true);
			}
		}
		clearMembers();
		for (final L1Object object : World.get().getObject()) {
			if (object instanceof L1DoorInstance) {
				final L1DoorInstance door = (L1DoorInstance) object;
				if (door.getMapId() == 5140) {
					door.close();
				}
			}
		}
	}

	public void removeRetiredMembers() {
		final L1PcInstance[] temp = getMembersArray();
		for (int i = 0; i < temp.length; i++) {
			if (temp[i].getMapId() != 5140) {
				removeMember(temp[i]);
			}
		}
	}

	public void sendMessage(final int type, final String msg) {
		for (final L1PcInstance pc : getMembersArray()) {
			pc.sendPackets(new S_ServerMessage(type, msg));
		}
	}

	public void addMember(final L1PcInstance pc) {
		if (!_members.contains(pc)) {
			_members.add(pc);
		}
		if ((getMembersCount() == 1) && (getHauntedHouseStatus() == STATUS_NONE)) {
			readyHauntedHouse();
		}
	}

	public void removeMember(final L1PcInstance pc) {
		_members.remove(pc);
	}

	public void clearMembers() {
		_members.clear();
	}

	public boolean isMember(final L1PcInstance pc) {
		return _members.contains(pc);
	}

	public L1PcInstance[] getMembersArray() {
		return _members.toArray(new L1PcInstance[_members.size()]);
	}

	public int getMembersCount() {
		return _members.size();
	}

	private void setHauntedHouseStatus(final int i) {
		_hauntedHouseStatus = i;
	}

	public int getHauntedHouseStatus() {
		return _hauntedHouseStatus;
	}

	private void setWinnersCount(final int i) {
		_winnersCount = i;
	}

	public int getWinnersCount() {
		return _winnersCount;
	}

	public void setGoalCount(final int i) {
		_goalCount = i;
	}

	public int getGoalCount() {
		return _goalCount;
	}

	public class L1HauntedHouseReadyTimer extends TimerTask {

		public L1HauntedHouseReadyTimer() {
		}

		@Override
		public void run() {
			startHauntedHouse();
			final L1HauntedHouseTimer hhTimer = new L1HauntedHouseTimer();
			hhTimer.begin();
		}

		public void begin() {
			final Timer timer = new Timer();
			timer.schedule(this, 90000); // 90秒くらい？
		}

	}

	public class L1HauntedHouseTimer extends TimerTask {

		public L1HauntedHouseTimer() {
		}

		@Override
		public void run() {
			endHauntedHouse();
			cancel();
		}

		public void begin() {
			final Timer timer = new Timer();
			timer.schedule(this, 300000); // 5分
		}
	}
}
