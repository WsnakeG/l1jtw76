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

import java.util.List;

import com.lineage.server.templates.L1BookMark;

/**
 * 記憶座標清單
 * 
 * @author Nightwish790711
 */
public class S_BookmarkList extends ServerBasePacket {
	
	private byte[] _byte = null;
	/**
	 * 記憶座標清單-建構式
	 * 
	 * @param bookmarks 記憶座標列隊
	 */
	public S_BookmarkList(final List<L1BookMark> bookmarks) {
		writeC(S_OPCODE_CHARRESET);
		writeC(0x2a);
		writeH(0x0080);
		writeC(0x02);

		for (int i = 1; i < 128; ++i) {
			writeC(0xff);
		}

		writeH(60);
		writeH(bookmarks.size());

		for (final L1BookMark book : bookmarks) {
			writeD(book.getId());
			writeS(book.getName());
			writeH(book.getMapId());
			writeH(book.getLocX());
			writeH(book.getLocY());
		}
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
