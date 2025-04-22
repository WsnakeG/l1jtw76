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

/**
 * 添加記憶座標
 * 
 * @author Nightwish790711
 */
public class S_Bookmarks extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	/**
	 * 添加記憶座標-建構式
	 * 
	 * @param name 記憶座標的名稱
	 * @param map 記憶座標的地圖
	 * @param x 記憶座標的X軸
	 * @param y 記憶座標的Y軸
	 * @param id 記憶座標的識別碼
	 */
	public S_Bookmarks(final String name, final int map, final int x, final int y, final int id) {
		// [S] [id:115] [GroupId:16] [Length:16] [Millis:1355454570352]
		// 0000 73 35 39 39 30 00 00 00 96 7f ba 80 7f 32 01 00 s5990......2..
		writeC(S_OPCODE_BOOKMARKS);
		writeS(name); // 記憶座標的名稱
		writeH(map); // 記憶座標的地圖
		writeH(x); // 記憶座標的X軸
		writeH(y); // 記憶座標的Y軸
		writeD(id); // 記憶座標的識別碼
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
	@Override
	public String getType() { //20240901
		return "S_ProtoBuffers";
	}
}