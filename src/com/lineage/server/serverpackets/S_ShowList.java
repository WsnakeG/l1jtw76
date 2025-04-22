package com.lineage.server.serverpackets;

import java.util.ArrayList;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemStatus;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;

/**
 * 道具顯示
 * 
 * @author simlin
 */
public class S_ShowList extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ShowList(final L1PcInstance pc, final ArrayList<Integer> showItems) {
		buildPacket(pc, showItems);
	}

	private void buildPacket(final L1PcInstance pc, final ArrayList<Integer> showItems) {
		writeC(S_OPCODE_SHOWSHOPBUYLIST);
		writeD(pc.getId());

		if (showItems.size() <= 0) {
			writeH(0x0000);
			return;
		}

		writeH(showItems.size());

		int i = 0;
		for (final Integer itemid : showItems) {
			i++;
			final L1Item item = ItemTable.get().getTemplate(itemid);
			writeD(i);
			writeH(item.getGfxId());
			writeD(0);
			writeS(item.getNameId());
			final L1ItemStatus itemInfo = new L1ItemStatus(item, 0);
			// 取回物品資訊
			final byte[] status = itemInfo.getStatusBytes().getBytes();
			writeC(status.length);
			for (final byte b : status) {
				writeC(b);
			}
		}
		writeH(0x0000);
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
