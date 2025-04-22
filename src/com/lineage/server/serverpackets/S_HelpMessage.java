package com.lineage.server.serverpackets;

/**
 * 訊息通知
 * 
 * @author dexc
 */
public class S_HelpMessage extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 強化成功成功訊息
	 * 
	 * @param mode
	 */
	public S_HelpMessage(final String name, final String info) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x09);
		writeS(name + " --> \\f4" + info);
	}

	/**
	 * 訊息通知(使用NPC對話一般頻道)
	 * 
	 * @param mode
	 * @param color <br>
	 *            <font color="#bdaaa5">\\fR <b>顏色範例</b></font><br>
	 *            <font color="#739e84">\\fS <b>顏色範例</b></font><br>
	 *            <font color="#7b9e7b">\\fT <b>顏色範例</b></font><br>
	 *            <font color="#7b9aad">\\fU <b>顏色範例</b></font><br>
	 *            <font color="#a59ac6">\\fV <b>顏色範例</b></font><br>
	 *            <font color="#ad92b5">\\fW <b>顏色範例</b></font><br>
	 *            <font color="#b592ad">\\fX <b>顏色範例</b></font><br>
	 *            <font color="#bd9a94">\\fY <b>顏色範例</b></font><br>
	 */
	public S_HelpMessage(final String string) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x09);
		writeS(string);
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
