package com.lineage.server.clientpackets;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.list.PcLvSkillList;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SkillBuy;
import com.lineage.server.serverpackets.S_SkillBuyCN;
import com.lineage.server.world.World;

/**
 * 要求學習魔法(金幣)
 * 
 * @author daien
 */
public class C_SkillBuy extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_SkillBuy.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}

			if (pc.isDead()) { // 死亡
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			if (pc.isPrivateShop()) { // 商店村模式
				return;
			}

			final int objid = readD();

			final L1Object obj = World.get().findObject(objid);
			if (obj == null) {
				return;
			}

			L1NpcInstance npc = null;

			if (obj instanceof L1NpcInstance) {
				npc = (L1NpcInstance) obj;
			}

			if (npc == null) {
				return;
			}

			if (npc.getNpcId() == 70754) {// 全職技能導師
				pc.get_other().set_shopSkill(true);
				pc.sendPackets(new S_SkillBuyCN(pc, npc));

			} else {
				pc.get_other().set_shopSkill(false);

				final ArrayList<Integer> skillList = PcLvSkillList.scount(pc);
				final ArrayList<Integer> newSkillList = new ArrayList<Integer>();
				// 判斷可以學習的魔法
				for (final Integer integer : skillList) {
					// 檢查是否已學習該法術
					if (!CharSkillReading.get().spellCheck(pc.getId(), (integer + 1))) {
						newSkillList.add(integer);
					}
				}
				switch (npc.getNpcId()) {
				case 70009:// 吉倫
				case 70997:// 費依哈斯
					if (pc.getLawful() < 0) {// 邪惡
						// 像你一樣如此強壯的人是不會需要它的
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengEv2"));
						return;
					}
					if (newSkillList.size() <= 0) {
						// 你已經學完這個等級所有的課程。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengEv3"));
						return;
					}
					pc.sendPackets(new S_SkillBuy(pc, newSkillList));
					break;

				default:// 其他
					pc.sendPackets(new S_SkillBuy(pc, newSkillList));
					break;
				}

			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
