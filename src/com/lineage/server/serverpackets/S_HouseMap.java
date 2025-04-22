package com.lineage.server.serverpackets;

/**
 * 血盟小屋地圖(地點)
 * 
 * @author dexc
 */
public class S_HouseMap extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 血盟小屋地圖(地點)
	 * 
	 * @param objectId
	 * @param house_number
	 */
	public S_HouseMap(final int objectId, final String house_number) {
		buildPacket(objectId, house_number);
	}

	private void buildPacket(final int objectId, final String house_number) {
		final int number = Integer.valueOf(house_number);

		writeC(S_OPCODE_HOUSEMAP);
		writeD(objectId);
		writeD(number);
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

