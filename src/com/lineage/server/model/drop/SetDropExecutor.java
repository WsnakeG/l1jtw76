package com.lineage.server.model.drop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1Drop;
import com.lineage.server.templates.L1DropMap;

/**
 * NPC持有物品取回
 * 
 * @author dexc
 */
public interface SetDropExecutor {

	/**
	 * 設置MAP資料
	 * 
	 * @param droplists
	 */
	public void addDropMap(Map<Integer, ArrayList<L1Drop>> droplists);

	/**
	 * 設置MAP資料
	 * 
	 * @param droplists
	 */
	public void addDropMapX(Map<Integer, HashMap<Integer, ArrayList<L1DropMap>>> droplists);

	/**
	 * NPC持有物品資料取回
	 * 
	 * @param npc
	 * @param inventory
	 */
	public void setDrop(L1NpcInstance npc, L1Inventory inventory);

	/**
	 * NPC持有物品資料取回
	 * 
	 * @param npc
	 * @param inventory
	 * @param random
	 */
	public void setDrop(L1NpcInstance npc, L1Inventory inventory, double random);
}
