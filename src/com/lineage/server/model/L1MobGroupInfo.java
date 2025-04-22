package com.lineage.server.model;

import java.util.ArrayList;
import java.util.List;

import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * NPC隊伍資訊
 * 
 * @author daien
 */
public class L1MobGroupInfo {

	private final List<L1NpcInstance> _membersList = new ArrayList<L1NpcInstance>();// 隊員組

	private L1NpcInstance _leader;

	public L1MobGroupInfo() {
	}

	/**
	 * NPC隊長
	 * 
	 * @param npc
	 */
	public void setLeader(final L1NpcInstance npc) {
		_leader = npc;
	}

	/**
	 * NPC隊長
	 * 
	 * @return
	 */
	public L1NpcInstance getLeader() {
		return _leader;
	}

	/**
	 * 是隊伍的隊長
	 * 
	 * @param npc
	 * @return
	 */
	public boolean isLeader(final L1NpcInstance npc) {
		return npc.getId() == _leader.getId();
	}

	private L1Spawn _spawn;

	public void setSpawn(final L1Spawn spawn) {
		_spawn = spawn;
	}

	public L1Spawn getSpawn() {
		return _spawn;
	}

	/**
	 * 加入隊伍
	 * 
	 * @param npc
	 */
	public void addMember(final L1NpcInstance npc) {
		if (npc == null) {
			throw new NullPointerException();
		}

		// 隊員列表不具有元素
		if (_membersList.isEmpty()) {
			// 設置第一名為隊長
			setLeader(npc);
			// 保存隊長召喚資料
			if (npc.isReSpawn()) {
				setSpawn(npc.getSpawn());
			}
		}

		if (!_membersList.contains(npc)) {
			_membersList.add(npc);
		}
		npc.setMobGroupInfo(this);
		npc.setMobGroupId(_leader.getId());
	}

	/**
	 * 移出隊伍
	 * 
	 * @param npc
	 * @return
	 */
	public synchronized int removeMember(final L1NpcInstance npc) {
		if (npc == null) {
			throw new NullPointerException();
		}

		if (_membersList.contains(npc)) {
			_membersList.remove(npc);
		}
		npc.setMobGroupInfo(null);

		// 移除對象 是隊長
		if (isLeader(npc)) {
			// 解散隊伍
			if (isRemoveGroup() && (_membersList.size() != 0)) { // リーダーが死亡したらグループ解除する場合
				for (final L1NpcInstance minion : _membersList) {
					minion.setMobGroupInfo(null);
					minion.setSpawn(null);
					minion.setreSpawn(false);
				}
				return 0;
			}
			// 重新設置隊長
			if (_membersList.size() != 0) {
				setLeader(_membersList.get(0));
			}
		}

		// 傳回剩餘隊員數量
		return _membersList.size();
	}

	/**
	 * 隊員數量
	 * 
	 * @return
	 */
	public int getNumOfMembers() {
		return _membersList.size();
	}

	private boolean _isRemoveGroup;

	/**
	 * 是否解散隊伍
	 * 
	 * @return
	 */
	public boolean isRemoveGroup() {
		return _isRemoveGroup;
	}

	/**
	 * 是否解散隊伍
	 * 
	 * @param flag
	 */
	public void setRemoveGroup(final boolean flag) {
		_isRemoveGroup = flag;
	}

	/**
	 * 隊伍內成員
	 * 
	 * @return
	 */
	public List<L1NpcInstance> getList() {
		return _membersList;
	}
}
