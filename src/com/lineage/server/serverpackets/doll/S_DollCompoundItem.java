package com.lineage.server.serverpackets.doll;

import java.util.ArrayList;

import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1Item;

/**
 * 
 * @author kyo
 *
 */
public class S_DollCompoundItem extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	public S_DollCompoundItem() {
		
	}
	
	public S_DollCompoundItem(int i, int level, final ArrayList<L1Item> levelList) {
		this.writeByteArray(3, new S_DollCompoundItem(level, levelList).getContent());
	}
	
	public S_DollCompoundItem(int level, final ArrayList<L1Item> levelList) {
		this.writeInt32(1, level);
		for (final L1Item tmp : levelList) {
			this.writeByteArray(2, build(tmp));
		}
	}
	
	private byte[] build(final L1Item tmp) {
		S_DollCompoundItem m = new S_DollCompoundItem();
		m.writeInt32(1, tmp.getItemDescId());
		m.writeInt32(2, tmp.getGfxId());
		return m.getContent();
	}
	
	

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName();
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
