/*
 * Copyright (C) 2012 Nightwish790711 This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * @author Nightwish790711
 */
public class S_GmMessage extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	private final static String AM = "\\aE[%s] \\aF%s: \\aB%s";

	/**
	 * 
	 */
	public S_GmMessage(final L1PcInstance pc, final L1PcInstance target, final int type, final String text) {

		final String t;
		final String to;

		switch (type) {
		// 一般頻道
		case 0:
			t = "一般";
			break;
		// 私密頻道
		case 1:
			t = "私密";
			break;
		// 大喊頻道
		case 2:
			t = "大喊";
			break;
		// 全體頻道
		case 3:
			t = "全體";
			return;
		// 血盟頻道
		case 4:
			t = "血盟";
			break;
		// 隊伍頻道
		case 11:
		case 14:
			t = "隊伍";
			break;
		// 交易頻道
		case 12:
			t = "交易";
			return;
		// 聯盟頻道
		case 13:
			t = "聯盟";
			break;
		default:
			t = "未知";
			return;
		}

		to = target == null ? pc.getName() : (pc.getName() + "->" + target.getName());
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(9);
		writeS(String.format(AM, t, to, text));
	}

	private final static String[] TYPES = { "王族", "騎士", "妖精", "法師", "黑暗妖精", "龍騎士", "幻術師", "管理員" };

	private final static String LOGIN = "\\aE[%s][%s](%s) 登錄。";

	public S_GmMessage(final L1PcInstance pc) {
		final String type = pc.isGm() || pc.isMonitor() ? TYPES[7] : TYPES[pc.getType()];
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(9);
		writeS(String.format(LOGIN, pc.getName(), type, pc.getNetConnection().getIp()));
	}

	@Override
	public final byte[] getContent() {
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
