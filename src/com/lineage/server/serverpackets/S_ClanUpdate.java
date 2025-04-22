package com.lineage.server.serverpackets;

/**
 * 更新血盟數據
 * 
 * @author KZK
 */
public class S_ClanUpdate extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新血盟數據(加入 創立)
	 * 
	 * @param pc [Server] opcode = 97 0000: 61 c2 6b b1 00 a4 d1 b0 f3 31 00 00
	 *            00 00 00 00 a.k......1...... 0010: 07 25 cb 44 06 5a b4 3a
	 *            .%.D.Z.:
	 */
	public S_ClanUpdate(final int objid, final String Clanname, final int rank) {
		writeC(S_OPCODE_UPDATECLANID); // XXX S_OPCODE_CLANUPDATE 修改為 S_OPCODE_UPDATECLANID
		writeD(objid);// 角色物件
		writeS(Clanname);// 血盟名稱
		writeS(null);// 角色封號 官方預設為NULL writeS內容 新成員入盟會直接給予封號
		writeD(0x00000000);// 用途不明
		writeC(rank);// 角色階級

	}

	/**
	 * 更新血盟數據(驅逐退出解散血盟)
	 * 
	 * @param objid
	 */
	public S_ClanUpdate(final int objid) {
		writeC(S_OPCODE_UPDATECLANID); // XXX S_OPCODE_CLANUPDATE 修改為 S_OPCODE_UPDATECLANID
		writeD(objid);// 角色物件
		writeS(null);// 血盟名稱
		writeS(null);// 角色封號 官方預設為NULL
		writeD(0x00000000);// 用途不明
		writeC(0);// 角色階級
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
