package com.lineage.server.serverpackets;

import java.util.Map;

import com.lineage.data.event.GamblingSet;
import com.lineage.data.event.gambling.Gambling;
import com.lineage.data.event.gambling.GamblingNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.timecontroller.event.GamblingTime;

/**
 * 買食人妖精競賽票
 * 
 * @author dexc
 */
public class S_ShopSellListGam extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 買食人妖精競賽票
	 * 
	 * @param npc
	 */
	public S_ShopSellListGam(final L1PcInstance pc, final L1NpcInstance npc) {
		writeC(S_OPCODE_SHOWSHOPBUYLIST);
		writeD(npc.getId());

		final Gambling gambling = GamblingTime.get_gambling();
		final Map<Integer, GamblingNpc> list = gambling.get_allNpc();

		if (list.size() <= 0) {
			writeH(0x0000);
			return;
		}

		writeH(list.size());

		// 食人妖精競賽票
		final L1Item item = ItemTable.get().getTemplate(40309);

		int i = 0;
		for (final GamblingNpc gamblingNpc : list.values()) {
			i++;
			pc.get_otherList().add_gamList(gamblingNpc, i);

			writeD(i);
			writeH(item.getGfxId());
			writeD(GamblingSet.GAMADENA);

			final int no = GamblingTime.get_gamblingNo();
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(gamblingNpc.get_npc().getNameId());
			stringBuilder.append(" [" + no + "-" + gamblingNpc.get_npc().getNpcId() + "]");

			writeS(stringBuilder.toString());
			writeC(0x00);
		}

		writeH(0x0007); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:貨幣
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
