/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.serverpackets;

/**
 * 裝備魔法效果圖示
 * @author admin
 *
 */
public class S_MagicEquipment extends ServerBasePacket {
	
	private byte[] _byte = null;

	public S_MagicEquipment(final int time, final int type) {
		buildPacket(time, type);
	}
	
	/**
	 * 裝備魔法效果圖示
	 * @param time
	 * @param type 
	 * @return 
	 */
	public void buildPacket(final int time, final int type) {
		writeC(S_OPCODE_PACKETBOX);
		this.writeC(0x9a);
		this.writeH(time);// 秒
		this.writeD(type);// 神聖武器 2165  擬似魔法武器 747 鎧甲護持 748 祝福魔法武器 2176 暗影之牙 2951
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