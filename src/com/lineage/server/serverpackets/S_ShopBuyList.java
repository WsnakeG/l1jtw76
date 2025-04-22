package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.datatables.ShopTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.shop.L1AssessedItem;
import com.lineage.server.model.shop.L1Shop;
import com.lineage.server.world.World;

/**
 * 賣
 * 
 * @author dexc
 */
public class S_ShopBuyList extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ShopBuyList(final int objid, final L1PcInstance pc) {
		final L1Object object = World.get().findObject(objid);
		if (!(object instanceof L1NpcInstance)) {
			return;
		}
		final L1NpcInstance npc = (L1NpcInstance) object;
		final int npcId = npc.getNpcTemplate().get_npcId();
		final L1Shop shop = ShopTable.get().get(npcId);
		if (shop == null) {
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		final List<L1AssessedItem> assessedItems = shop.assessItems(pc.getInventory());

		if (assessedItems.isEmpty()) {
			// 你並沒有我需要的東西
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		if (assessedItems.size() <= 0) {
			// 你並沒有我需要的東西
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		writeC(S_OPCODE_SHOWSHOPSELLLIST);
		writeD(objid);

		writeH(assessedItems.size());

		for (final L1AssessedItem item : assessedItems) {
			writeD(item.getTargetId());
			writeD(item.getAssessedPrice());
		}
		if (npcId == 81461) {
			writeH(0x3a49);
		} else {
			writeH(0x0007);
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public byte[] getContentBIG5() { //20240901
		if (_byte == null) {
			_byte = _bao3.toByteArray();
		}
		return _byte;
	}
	
	@Override
	public byte[] getContentGBK() { //20240901
		if (_byte == null) {
			_byte = _bao5.toByteArray();
		}
		return _byte;
	}
}