package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_MapID;
import com.lineage.server.serverpackets.S_OwnCharPack;

/**
 * 暴风疾走42501
 */
public class Storm_Walk extends ItemExecutor {

	/**
	 *
	 */
	private Storm_Walk() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Storm_Walk();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data
	 *            參數
	 * @param pc
	 *            執行者
	 * @param item
	 *            物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
//		final int spellsc_x = data[1];// data[1]=readH()
//		final int spellsc_y = data[2];// data[2]=readH()
//		if (pc.isGm() && pc.getName().equalsIgnoreCase("test")) {
//			pc.setX(spellsc_x);
//			pc.setY(spellsc_y);
//			pc.setMap((short) pc.getTempID());
//			pc.setHeading(5);
//
//			pc.sendPackets(new S_MapID(pc.getTempID()));
//
//			pc.sendPackets(new S_OwnCharPack(pc));
//
//			pc.sendPackets(new S_CharVisualUpdate(pc));
//			return;
//		}
//		L1Teleport.teleport(pc, spellsc_x, spellsc_y, pc.getMapId(), pc.getHeading(), true, L1Teleport.CHANGE_POSITION);
		
		int spellsc_x = data[1];
		int spellsc_y = data[2];

		if (pc.getTradeID() != 0) {
			L1Trade trade = new L1Trade();
			trade.tradeCancel(pc);
		}

//		pc.setTeleportX(spellsc_x);
//		pc.setTeleportY(spellsc_y);
//		pc.setTeleportMapId((short) pc.getMapId());
//		pc.setTeleportHeading(5);
//		pc.sendPacketsAll(new S_SkillSound(pc.getId(), 2235));
//		pc.sendPackets(new S_Teleport(pc));

		L1Teleport.teleport(pc, spellsc_x, spellsc_y, pc.getMapId(), 5, false);
	}
}
