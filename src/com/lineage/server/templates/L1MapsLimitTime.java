/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.templates;

import java.util.List;

/**
 * 限時地圖入場時間 暫存組
 * 
 * @author terry0412
 */
public final class L1MapsLimitTime {

	private final int _orderId; // 地圖排序編碼

	private final List<Integer> _mapList; // 地圖列表

	private final String _mapName; // 地圖名稱

	private final int _limitTime; // 限制時間

	private final int _outMapX; // 地圖X軸 (時間到強制傳送回村)

	private final int _outMapY; // 地圖Y軸 (時間到強制傳送回村)

	private final short _outMapId; // 地圖ID (時間到強制傳送回村)

	public L1MapsLimitTime(final int orderId, final List<Integer> mapList, final String mapName,
			final int limitTime, final int outMapX, final int outMapY, final short outMapId) {
		_orderId = orderId;
		_mapList = mapList;
		_mapName = mapName;
		_limitTime = limitTime;
		_outMapX = outMapX;
		_outMapY = outMapY;
		_outMapId = outMapId;
	}

	public final int getOrderId() {
		return _orderId;
	}

	public final List<Integer> getMapList() {
		return _mapList;
	}

	public final String getMapName() {
		return _mapName;
	}

	public final int getLimitTime() {
		return _limitTime;
	}

	public final int getOutMapX() {
		return _outMapX;
	}

	public final int getOutMapY() {
		return _outMapY;
	}

	public final short getOutMapId() {
		return _outMapId;
	}
}
