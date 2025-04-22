package com.lineage.server.serverpackets;

import java.text.SimpleDateFormat;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 創造角色
 * 
 * @author dexc
 */
public class S_NewCharPacket extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 創造角色
	 * 
	 * @param pc
	 */
	public S_NewCharPacket(final L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(final L1PcInstance pc) {
		writeC(S_OPCODE_NEWCHARPACK);
		writeS(pc.getName());
		writeS("");
		writeC(pc.getType());
		writeC(pc.get_sex());
		writeH(pc.getLawful());
		writeH(pc.getMaxHp());
		writeH(pc.getMaxMp());
		writeC(pc.getAc());
		writeC(pc.getLevel());
		writeC(pc.getStr());
		writeC(pc.getDex());
		writeC(pc.getCon());
		writeC(pc.getWis());
		writeC(pc.getCha());
		writeC(pc.getInt());

		// 大於0為GM權限
		writeC(0x00);

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final int time = Integer.parseInt(sdf.format(System.currentTimeMillis()).replace("-", ""));
		writeD(time);

		final int checkcode = pc.getLevel() ^ pc.getStr() ^ pc.getDex() ^ pc.getCon() ^ pc.getWis()
				^ pc.getCha() ^ pc.getInt();
		writeC(checkcode & 0xFF);// 12070601 add

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
