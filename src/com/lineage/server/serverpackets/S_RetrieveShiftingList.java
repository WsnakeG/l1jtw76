package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 移轉物品名單
 * 
 * @author dexc
 */
public class S_RetrieveShiftingList extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 移轉物品名單
	 * 
	 * @param pc
	 * @param items
	 */
	public S_RetrieveShiftingList(final L1PcInstance pc, final List<L1ItemInstance> items) {
		writeC(S_OPCODE_SHOWRETRIEVELIST);
		writeD(pc.getId());
		writeH(items.size());
		writeC(0x02); // 移轉物品名單
		for (final L1ItemInstance item : items) {
			final int itemid = item.getId();
			writeD(itemid);
			// System.out.println("itemid:" + itemid);
			writeC(0x00);
			writeH(item.get_gfxid());
			writeC(item.getBless());
			writeD(1);
			writeC(item.isIdentified() ? 0x01 : 0x00);
			writeS(item.getViewName());
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
