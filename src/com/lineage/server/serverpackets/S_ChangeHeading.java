package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;

/**
 * 物件面向
 * 
 * @author terry0412
 */
public class S_ChangeHeading extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件面向
	 * 
	 * @param cha
	 */
	public S_ChangeHeading(final L1Character cha) {
		buildPacket(cha.getId(), cha.getHeading());
	}

	/**
	 * 物件面向
	 * 
	 * @param objid
	 * @param heading
	 */
	public S_ChangeHeading(final int objid, final int heading) {
		buildPacket(objid, heading);
	}

	private void buildPacket(final int objid, final int heading) {
		writeC(S_OPCODE_CHANGEHEADING);
		writeD(objid);
		writeC(heading);
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
