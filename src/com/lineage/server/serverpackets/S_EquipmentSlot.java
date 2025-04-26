package com.lineage.server.serverpackets;

import java.io.IOException;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 裝備道具載入
 * 
 * @author simlin
 */
public class S_EquipmentSlot extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 裝備穿脫
	 * 
	 * @param itemObjId
	 * @param index
	 * @param isEq
	 */
	public S_EquipmentSlot(final int itemObjId, final int index, final boolean isEq) {
		writeC(S_OPCODE_CHARRESET);
		writeC(0x42);
		writeD(itemObjId);
		writeC(index);
		writeC(isEq ? 0x01 : 0x00);
	}

	/**
	 * 登入遊戲
	 * 
	 * @param pc
	 * @param count
	 */
	public S_EquipmentSlot(final L1PcInstance pc, final int count) {
		writeC(S_OPCODE_CHARRESET);
		writeC(0x41);
		writeC(count);
		int index = 0;
		int ring = 19;
		int earring = 13;
		int aid = 23;
		boolean weaponck = false;
		for (final L1ItemInstance item : pc.getInventory().getItems()) {
			if (item.isEquipped()) {
				if (item.getItem().getType2() == 1) {
					if (pc.isWarrior()) {
						if (weaponck) {
							index = 8;
						} else {
							index = 9;
							weaponck = true;
						}
					} else {
						index = 9;
					}
				} else if (item.getItem().getType2() == 2) {
					switch (item.getItem().getType()) {
					case 5:
						index = 7;
						break;
					case 6:
						index = 6;
						break;
					case 8:
						index = 11;
						break;
					case 9:
						index = ring;
						ring += 1;
						break;
					case 10:
						index = 12;
						break;
					case 12:
						index = earring;
						earring += 13;
						break;
					case 7:
					case 13:
						index = 8;
						break;
					case 14:
						index = aid;
// up2 to 27			aid += 4;
						break;
					case 16:
						index = 5;
						break;
					case 17:
						index = 15;
						break;
					case 18:
						index = 16;
						break;
					case 19:
						index = 18;
						break;
					case 23:
						index = 27;
						break;
					default:
						index = item.getItem().getType();
						break;
					}
				}
				writeD(item.getId());
				writeD(index);
			}			
		}
		
	}

	/**
	 * 圖示解鎖
	 * 
	 * @param kind
	 * @param count
	 */
	public S_EquipmentSlot(final int kind, final int count) {
		writeC(S_OPCODE_CHARRESET);
		writeC(0x43);
		writeD(kind);
		writeD(count);
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}

	@Override
	public byte[] getContent() throws IOException {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
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
