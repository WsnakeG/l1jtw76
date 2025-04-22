package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物品名單(個人倉庫)
 * 
 * @author dexc
 */
public class S_RetrieveList extends ServerBasePacket {

	private byte[] _byte = null;

	private byte[] status = null;

	public boolean NonValue = false;

	/**
	 * 物品名單(個人倉庫)
	 * 
	 * @param objid
	 * @param pc
	 */
	public S_RetrieveList(final int objid, final L1PcInstance pc) {
		if (pc.getInventory().getSize() < 180) {
			final int size = pc.getDwarfInventory().getSize();
			if (size > 0) {
				writeC(S_OPCODE_SHOWRETRIEVELIST);
				this.writeD(objid);
				this.writeH(size);
				this.writeC(0x03); // 個人倉庫
				for (final Object itemObject : pc.getDwarfInventory().getItems()) {
					final L1ItemInstance item = (L1ItemInstance) itemObject;
					this.writeD(item.getId());
					int i = item.getItem().getUseType();
					if (i < 0) {
						i = 0;
					}
					this.writeC(i);
					this.writeH(item.get_gfxid());
					this.writeC(item.getBless());
					this.writeD((int) Math.min(item.getCount(), 2000000000));
					this.writeC(item.isIdentified() ? 0x01 : 0x00);
					this.writeS(item.getViewName());
					if (!item.isIdentified()) {
						this.writeC(0);
					} else {
						/*status = item.getStatusBytes();
						this.writeC(status.length);
						for (byte b : status) {
							this.writeC(b);
						}*/
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
				/*this.writeH(0x001e);
				this.writeD(0x00);
				this.writeH(0x00);
				this.writeH(0x08);*/
				this.writeD(30); // 金幣30
				this.writeH(0x0000); // 不使用
				this.writeD(0x00000000); // 固定的BYTE
			} else
				NonValue = true;
		} else {
			pc.sendPackets(new S_ServerMessage(263)); // 263 \f1一個角色最多可攜帶180個道具。
		}
	}

	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
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
