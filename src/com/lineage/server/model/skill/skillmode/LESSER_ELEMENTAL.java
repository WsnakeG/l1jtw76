package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;

/**
 * 召喚屬性精靈
 * 
 * @author daien
 */
public class LESSER_ELEMENTAL extends SkillMode {

	public LESSER_ELEMENTAL() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;// magic.calcMagicDamage(L1SkillId.LESSER_ELEMENTAL);

		final L1PcInstance pc = (L1PcInstance) cha;
		final int attr = pc.getElfAttr();
		if (attr != 0) { // 必須具有屬性
			if (!pc.getMap().isRecallPets()) {
				// 353：在這附近無法召喚怪物。
				pc.sendPackets(new S_ServerMessage(353));
				return dmg;
			}

			int petcost = 0;
			final Object[] petlist = pc.getPetList().values().toArray();
			for (final Object pet : petlist) {
				// 已耗用召換點數
				petcost += ((L1NpcInstance) pet).getPetcost();
			}

			if (petcost == 0) { // 並未召換任何寵物
				int summonid = 0;
				// ElfAttr:0.無属性,1.地属性,2.火属性,4.水属性,8.風属性
				switch (attr) {
				case 1:// 1.地属性
					summonid = 45306;// 土之精靈
					break;
				case 2:// 2.火属性
					summonid = 45303;// 火之精靈
					break;
				case 4:// 4.水属性
					summonid = 45304;// 水之精靈
					break;
				case 8:// 8.風属性
					summonid = 45305;// 風之精靈
					break;
				}
				if (summonid != 0) {
					final L1Npc npcTemp = NpcTable.get().getTemplate(summonid);
					final L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
					summon.setPetcost(pc.getCha() + 7);
				}
			}
		}
		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;

		return dmg;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		// TODO Auto-generated method stub
	}
}
