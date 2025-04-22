package com.lineage.server.serverpackets;

/**
 * NPC物品販賣(測試用NPCID 50000)
 * 
 * @author dexc
 */
public class S_ShopSellListTmp extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * TEST
	 * 
	 * @param objId
	 * @param tmp
	 */
	public S_ShopSellListTmp(final int objId, int tmp) {
		System.out.println("objId: " + objId + " tmp: " + tmp);
		// System.out.println(12456);
		final int size = 100;
		writeC(S_OPCODE_SHOWSHOPBUYLIST);
		writeD(objId);

		writeH(size);

		for (int i = 0; i < size; i++) {
			final int gfx = tmp++;
			writeD(i);
			writeH(gfx);
			writeD(0x00);
			writeS("gfxid = " + gfx);
			System.out.println("gfxid = " + gfx);
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
