package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物品清單
 * 
 * @author daien
 */
public class S_PowerItemList extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品清單
	 * 
	 * @param pc
	 * @param items
	 */
	public S_PowerItemList(final L1PcInstance pc, final int objid, final List<L1ItemInstance> items) {
		writeC(S_OPCODE_SHOWRETRIEVELIST);
		writeD(objid);
		writeH(items.size());
		writeC(0x0a);
		for (final L1ItemInstance item : items) {
			final int itemobjid = item.getId();
			writeD(itemobjid);
			if (item.getItem().getUseType() >= 0) {
				this.writeC(item.getItem().getUseType());
			} else {
				this.writeC(0x00);
			}
			writeH(item.get_gfxid());
			writeC(item.getBless());
			writeD(0x01);
			writeC(item.isIdentified() ? 0x01 : 0x00);
			writeS(item.getViewName());
			if (!item.isIdentified()) {
				this.writeC(0);
			} else {
				final byte status[] = item.getStatusBytes();
				writeC(status.length);
				byte abyte0[];
				final int j = (abyte0 = status).length;
				for (int h = 0; h < j; h++) {
					final byte b = abyte0[h];
					writeC(b);
				}
			}
		}
		this.writeD(30); // 金幣30
		this.writeH(0x0000); // 不使用
		this.writeD(0x00000000); // 固定的BYTE
		items.clear();
	}

	public S_PowerItemList(final int objid, final List<L1ItemInstance> items) {
		writeC(S_OPCODE_SHOWRETRIEVELIST);
		writeD(objid);
		writeH(items.size());
		writeC(0x0c);
		for (final L1ItemInstance item : items) {
			final int itemobjid = item.getId();
			writeD(itemobjid);
			writeC(0x00);
			writeH(item.get_gfxid());
			writeC(item.getBless());
			writeD(0x01);
			writeC(item.isIdentified() ? 0x01 : 0x00);
			writeS(item.getViewName());
			if (!item.isIdentified()) {
				this.writeC(0);
			} else {
				final byte status[] = item.getStatusBytes();
				writeC(status.length);
				byte abyte0[];
				final int j = (abyte0 = status).length;
				for (int h = 0; h < j; h++) {
					final byte b = abyte0[h];
					writeC(b);
				}
			}
		}
		this.writeD(30); // 金幣30
		this.writeH(0x0000); // 不使用
		this.writeD(0x00000000); // 固定的BYTE
		items.clear();
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
		return getClass().getSimpleName();
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
