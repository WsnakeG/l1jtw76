package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillBuyItem;
import com.lineage.server.world.World;

/**
 * 要求學習魔法清單(材料)
 * 
 * @author daien
 */
public class C_SkillBuyItem extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_SkillBuyItem.class);

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

			switch (npc.getNpcId()) {
			/*
			 * case 000: pc.get_other().set_shopSkill(true); pc.sendPackets(new
			 * S_SkillBuyItemCN(pc, npc)); break;
			 */

			default:// 何侖(70080)等等
				pc.get_other().set_shopSkill(false);
				pc.sendPackets(new S_SkillBuyItem(pc, npc));
				break;
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
