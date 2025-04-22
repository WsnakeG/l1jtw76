package com.lineage.server.serverpackets;

import java.io.IOException;
import java.util.ArrayList;

import com.lineage.server.datatables.T_RankTable;
import com.lineage.server.templates.T_WeaponRankModel;

public class S_RankedWeapon extends ServerBasePacket {

	private byte[] _byte = null;

	public S_RankedWeapon(final ArrayList<T_WeaponRankModel> weaponRanked) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(166);
		writeC(3);
		writeD(T_RankTable._basedTime);
		writeD(weaponRanked.size());

		int enchantKind = 0;
		for (int i = 0; i < weaponRanked.size(); i++) {
			final T_WeaponRankModel model = weaponRanked.get(i);
			if (model.getWeaponAttrKind() == 1) {
				enchantKind = 4;
			} else if (model.getWeaponAttrKind() == 2) {
				enchantKind = 1;
			} else if (model.getWeaponAttrKind() == 4) {
				enchantKind = 2;
			} else if (model.getWeaponAttrKind() == 8) {
				enchantKind = 3;
			}
			writeS(model.getWeaponNameId());
			writeC(model.getWeaponEnchantlevel());
			writeC(0);
			writeS(model.getWeaponMasterName());
		}
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