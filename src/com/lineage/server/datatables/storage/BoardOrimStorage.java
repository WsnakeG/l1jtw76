package com.lineage.server.datatables.storage;

import java.util.List;

import com.lineage.server.templates.L1Rank;

/**
 * @author terry0412
 */
public interface BoardOrimStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	public List<L1Rank> getTotalList();

	public int writeTopic(final int score, final String leader, final List<String> partyMember);

	public void renewPcName(final String oriName, final String newName);

}
