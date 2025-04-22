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

import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;

/**
 * 角色列表
 * @author dexc
 *
 */
public class S_CharAmount extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色列表
	 * @param value 已創人物數量
	 * @param client
	 */
	public S_CharAmount(final int value, final ClientExecutor client) {
		this.buildPacket(value, client);
	}

	private void buildPacket(final int value, final ClientExecutor client) {
		final int characterSlot = client.getAccount().get_character_slot();
		
		final int maxAmount = ConfigAlt.DEFAULT_CHARACTER_SLOT + characterSlot;

		//0000: 0c 04 06 81 00 90 01 a3                            ........
		this.writeC(S_OPCODE_CHARAMOUNT);
		this.writeC(value);
		this.writeC(maxAmount); // max amount
		//this.writeD(0x4ce1e8b0); // unknown
	}

	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
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
