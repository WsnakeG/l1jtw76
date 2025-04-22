package com.lineage.server.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class L1ChatParty {

	private static final Log _log = LogFactory.getLog(L1ChatParty.class);

	private final List<L1PcInstance> _membersList = new ArrayList<L1PcInstance>();

	private L1PcInstance _leader = null;

	public void addMember(final L1PcInstance pc) {
		try {
			if (pc == null) {
				throw new NullPointerException();
			}
			if (_membersList.contains(pc)) {
				return;
			}

			if (_membersList.isEmpty()) {
				// 最初のPTメンバーであればリーダーにする
				setLeader(pc);
			}

			_membersList.add(pc);
			pc.setChatParty(this);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void removeMember(final L1PcInstance pc) {
		try {
			if (!_membersList.contains(pc)) {
				return;
			}

			_membersList.remove(pc);
			pc.setChatParty(null);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public boolean isVacancy() {
		return _membersList.size() < 8;
	}

	public int getVacancy() {
		return 8 - _membersList.size();
	}

	public boolean isMember(final L1PcInstance pc) {
		return _membersList.contains(pc);
	}

	private void setLeader(final L1PcInstance pc) {
		_leader = pc;
	}

	public L1PcInstance getLeader() {
		return _leader;
	}

	public boolean isLeader(final L1PcInstance pc) {
		return pc.getId() == _leader.getId();
	}

	public String getMembersNameList() {
		String _result = new String("");
		for (final L1PcInstance pc : _membersList) {
			_result = _result + pc.getName() + " ";
		}
		return _result;
	}

	private void breakup() {
		try {
			final L1PcInstance[] members = getMembers();

			for (final L1PcInstance member : members) {
				removeMember(member);
				member.sendPackets(new S_ServerMessage(418)); // パーティーを解散しました。
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void leaveMember(final L1PcInstance pc) {
		try {
			final L1PcInstance[] members = getMembers();
			if (isLeader(pc)) {
				// パーティーリーダーの場合
				breakup();
			} else {
				// パーティーリーダーでない場合
				if (getNumOfMembers() == 2) {
					// パーティーメンバーが自分とリーダーのみ
					removeMember(pc);
					final L1PcInstance leader = getLeader();
					removeMember(leader);

					sendLeftMessage(pc, pc);
					sendLeftMessage(leader, pc);
				} else {
					// 残りのパーティーメンバーが２人以上いる
					removeMember(pc);
					for (final L1PcInstance member : members) {
						sendLeftMessage(member, pc);
					}
					sendLeftMessage(pc, pc);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void kickMember(final L1PcInstance pc) {
		try {
			if (getNumOfMembers() == 2) {
				// パーティーメンバーが自分とリーダーのみ
				removeMember(pc);
				final L1PcInstance leader = getLeader();
				removeMember(leader);
			} else {
				// 残りのパーティーメンバーが２人以上いる
				removeMember(pc);
			}
			pc.sendPackets(new S_ServerMessage(419)); // パーティーから追放されました。

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public L1PcInstance[] getMembers() {
		return _membersList.toArray(new L1PcInstance[_membersList.size()]);
	}

	public int getNumOfMembers() {
		return _membersList.size();
	}

	private void sendLeftMessage(final L1PcInstance sendTo, final L1PcInstance left) {
		try {
			// %0がパーティーから去りました。
			sendTo.sendPackets(new S_ServerMessage(420, left.getName()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

}
