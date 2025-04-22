package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物品名單(精靈倉庫)
 * 
 * @author dexc
 */
public class S_RetrieveElfList extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品名單(精靈倉庫)
	 * 
	 * @param objid
	 * @param pc
	 */
	public S_RetrieveElfList(final int objid, final L1PcInstance pc) {
		if (pc.getInventory().getSize() < 180) {
			final int size = pc.getDwarfForElfInventory().getSize();
			if (size > 0) {
				writeC(S_OPCODE_SHOWRETRIEVELIST);
				writeD(objid);
				writeH(size);
				writeC(0x09); // 精靈倉庫
				for (final Object itemObject : pc.getDwarfForElfInventory().getItems()) {
					final L1ItemInstance item = (L1ItemInstance) itemObject;
					writeD(item.getId());
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
				writeH(0x02);
				writeD(0x00000000);
				writeD(0x00000000);
			}
		} else {
			pc.sendPackets(new S_ServerMessage(263)); // 263 \f1一個角色最多可攜帶180個道具。
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
