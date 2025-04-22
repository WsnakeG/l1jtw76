package com.lineage.server.templates;

import java.util.Collections;
import java.util.List;

import com.lineage.server.utils.collections.Lists;

/**
 * NPC隊伍資訊暫存
 * 
 * @author daien
 */
public class L1MobGroup {

	private final int _id;

	private final int _leaderId;

	private final List<L1NpcCount> _minions = Lists.newArrayList();

	private final boolean _isRemoveGroupIfLeaderDie;// 是否解散隊伍

	/**
	 * NPC隊伍資訊暫存
	 * 
	 * @param id 隊伍編號
	 * @param leaderId 隊長NPCID
	 * @param minions 隊員組
	 * @param isRemoveGroupIfLeaderDie 是否解散隊伍
	 */
	public L1MobGroup(final int id, final int leaderId, final List<L1NpcCount> minions,
			final boolean isRemoveGroupIfLeaderDie) {
		_id = id;
		_leaderId = leaderId;
		_minions.addAll(minions);
		_isRemoveGroupIfLeaderDie = isRemoveGroupIfLeaderDie;
	}

	/**
	 * 隊伍編號
	 * 
	 * @return
	 */
	public int getId() {
		return _id;
	}

	/**
	 * 隊長NPCID
	 * 
	 * @return
	 */
	public int getLeaderId() {
		return _leaderId;
	}

	/**
	 * 隊員組
	 * 
	 * @return
	 */
	public List<L1NpcCount> getMinions() {
		return Collections.unmodifiableList(_minions);
	}

	/**
	 * 是否解散隊伍
	 * 
	 * @return
	 */
	public boolean isRemoveGroupIfLeaderDie() {
		return _isRemoveGroupIfLeaderDie;
	}

}