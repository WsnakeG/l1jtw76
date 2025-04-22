package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 提煉 武器/防具 物品名單
 * 
 * @author dexc
 */
public class S_RetrieveDevilKingList extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 提煉 武器/防具 物品名單
	 * 
	 * @param pc
	 * @param items
	 */
	public S_RetrieveDevilKingList(final L1PcInstance pc, final int objid, final List<L1ItemInstance> items) {
		writeC(S_OPCODE_SHOWRETRIEVELIST);
		writeD(objid);
		writeH(items.size());
		writeC(0x0c); // 提煉武器
		for (final L1ItemInstance item : items) {
			final int itemobjid = item.getId();
			writeD(itemobjid);
			// System.out.println("itemobjid:" + itemobjid);
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
