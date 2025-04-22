package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 託售 物品名單
 * 
 * @author dexc
 */
public class S_CnSRetrieve extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 託售 物品名單
	 * 
	 * @param pc
	 * @param items
	 */
	public S_CnSRetrieve(final L1PcInstance pc, final int objid, final List<L1ItemInstance> items) {
		writeC(S_OPCODE_SHOWRETRIEVELIST);
		writeD(objid);
		writeH(items.size());
		writeC(0x0c); // 提煉武器/託售
		for (final L1ItemInstance item : items) {
			final int itemobjid = item.getId();
			writeD(itemobjid);
			int i = item.getItem().getUseType();
			if (i < 0) {
				i = 0;
			}
			writeC(i);// this.writeC(0x00);
			writeH(item.get_gfxid());
			writeC(item.getBless());
			writeD((int) Math.min(item.getCount(), 2000000000));
			writeC(item.isIdentified() ? 0x01 : 0x00);
			writeS(item.getViewName());
		}
		items.clear();
		writeH(0x1e);
		writeD(0x00000000);
		writeD(0x00000000);
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