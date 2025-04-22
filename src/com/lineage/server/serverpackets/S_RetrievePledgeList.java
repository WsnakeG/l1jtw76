package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 物品名單(血盟倉庫)
 * 
 * @author dexc
 */
public class S_RetrievePledgeList extends ServerBasePacket {

	private byte[] _byte = null;

	private byte[] status = null;

	public boolean NonValue = false;

	/**
	 * 物品名單(血盟倉庫)
	 * 
	 * @param objid
	 * @param pc
	 */
	public S_RetrievePledgeList(final int objid, final L1PcInstance pc) {
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		// final L1Clan clan = World.get().getClan(pc.getClanname());
		if (clan == null) {
			return;
		}

		if ((clan.getWarehouseUsingChar() != 0) && (clan.getWarehouseUsingChar() != pc.getId())) {
			// 目前無人使用或是使用者是自己
			// \f1血盟成員在使用倉庫。請稍後再使用。
			// pc.sendPackets(new S_ServerMessage(209));
			final L1Object obj = World.get().findObject(clan.getWarehouseUsingChar());
			if (obj != null && obj instanceof L1PcInstance) {
				final L1PcInstance target = (L1PcInstance) obj;
				// pc.sendPackets(new S_SystemMessage(
				// "正在使用血盟倉庫的玩家: " + target.getName()));
				// \f1血盟成員在使用倉庫。請稍後再使用。
				pc.sendPackets(new S_ServerMessage(209));
				// 王族強制領取
				if (clan.getLeaderId() == pc.getId()) {
					L1Teleport.teleport(target, target.getLocation(), target.getHeading(), false);
					pc.sendPackets(new S_SystemMessage("已強制結束該玩家的倉庫使用權。"));
					clan.setWarehouseUsingChar(0);
				}
			} else {
				clan.setWarehouseUsingChar(0);
				// \f1血盟成員在使用倉庫。請稍後再使用。
				pc.sendPackets(new S_ServerMessage(209));
			}
			return;
		}

		if (pc.getInventory().getSize() < 180) {
			final int size = clan.getDwarfForClanInventory().getSize();
			if (size > 0) {
				clan.setWarehouseUsingChar(pc.getId()); // 設置血盟倉庫目前使用者
				this.writeC(S_OPCODE_SHOWRETRIEVELIST);
				this.writeD(objid);
				this.writeH(size);
				this.writeC(0x05); // 血盟倉庫
				for (final Object itemObject : clan.getDwarfForClanInventory().getItems()) {
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
			pc.sendPackets(new S_ServerMessage(263));
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
