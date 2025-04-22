package com.lineage.data.executor;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * NPC對話執行
 * 
 * @author dexc
 */
public abstract class NpcExecutor {

	/**
	 * 具有的方法數組<BR>
	 * 32:NPC召喚<BR>
	 * 16:NPC工作時間<BR>
	 * 8:NPC死亡<BR>
	 * 4:NPC受到攻擊<BR>
	 * 2:NPC對話執行<BR>
	 * 1:NPC對話判斷<BR>
	 */
	public abstract int type();

	/**
	 * NPC對話判斷(1)
	 * 
	 * @param pc
	 * @param npc
	 */
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		// TODO Auto-generated method stub
	}

	/**
	 * NPC對話執行(2)
	 * 
	 * @param pc
	 * @param npc
	 * @param cmd
	 */
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		// TODO Auto-generated method stub
	}

	/**
	 * NPC受到攻擊(4)<BR>
	 * 任務NPC作為抵達目標檢查的方法
	 * 
	 * @param pc
	 * @param npc
	 */
	public void attack(final L1PcInstance pc, final L1NpcInstance npc) {
		// TODO Auto-generated method stub
	}

	/**
	 * NPC死亡(8)<BR>
	 * 任務NPC作為給予任務道具的判斷
	 * 
	 * @param lastAttacker 攻擊者
	 * @param npc
	 */
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		return null;
	}

	/**
	 * NPC工作(16)<BR>
	 * 工作重複時間(秒)
	 */
	public int workTime() {
		return 0;
	}

	/**
	 * NPC工作執行
	 * 
	 * @param mode
	 */
	public void work(final L1NpcInstance npc) {
		// TODO Auto-generated method stub
	}

	/**
	 * NPC召喚(32)
	 * 
	 * @param mode
	 */
	public void spawn(final L1NpcInstance npc) {
		// TODO Auto-generated method stub
	}

	public String[] get_set() {
		return null;
	}

	public void set_set(final String[] set) {
	}
}
