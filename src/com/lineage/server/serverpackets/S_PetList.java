package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;

/**
 * 寵物領取清單
 * 
 * @author daien
 */
public class S_PetList extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PetList(final int npcObjId, final L1PcInstance pc) {
		buildPacket(npcObjId, pc);
	}

	private void buildPacket(final int npcObjId, final L1PcInstance pc) {
		final List<L1ItemInstance> amuletList = new ArrayList<L1ItemInstance>();
		for (final Object itemObject : pc.getInventory().getItems()) {
			final L1ItemInstance item = (L1ItemInstance) itemObject;
			switch (item.getItem().getItemId()) {
			case 40314: // 項圈
			case 40316: // 高等寵物項圈
				if (!isWithdraw(pc, item)) {
					amuletList.add(item);
				}
				continue;
			}
		}
		if (amuletList.size() != 0) {
			writeC(S_OPCODE_SELECTLIST);
			writeD(0x00000046); // Price
			writeH(amuletList.size());
			for (final L1ItemInstance item : amuletList) {
				writeD(item.getId());
				writeC((int) Math.min(item.getCount(), 2000000000));
			}
		}
	}

	private boolean isWithdraw(final L1PcInstance pc, final L1ItemInstance item) {
		final Object[] petlist = pc.getPetList().values().toArray();
		for (final Object petObject : petlist) {
			if (petObject instanceof L1PetInstance) {
				final L1PetInstance pet = (L1PetInstance) petObject;
				if (item.getId() == pet.getItemObjId()) {
					return true;
				}
			}
		}
		return false;
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
