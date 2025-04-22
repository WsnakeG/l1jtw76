/**
 * License THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). THE WORK IS PROTECTED
 * BY COPYRIGHT AND/OR OTHER APPLICABLE LAW. ANY USE OF THE WORK OTHER THAN AS
 * AUTHORIZED UNDER THIS LICENSE OR COPYRIGHT LAW IS PROHIBITED. BY EXERCISING
 * ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND AGREE TO BE BOUND BY THE
 * TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE MAY BE CONSIDERED TO BE A
 * CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED HERE IN CONSIDERATION
 * OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.serverpackets;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

/** 鋼鐵士氣，精靈法術 */
public class S_SkillIconAura extends ServerBasePacket {
	
	private byte[] _byte = null;

	public S_SkillIconAura(final int i, final int j) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x16);
		writeC(i);
		writeH(j);
	}

	public S_SkillIconAura(final int type, final int time, final int ac) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x16);
		writeC(type);
		writeH(time);
		writeC(ac);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
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
