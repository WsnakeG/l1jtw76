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

/**
 * @author terry0412
 */
public final class L1ItemSteal {

	private final int _item_id;

	private final int _level;

	private final int _mete_level;

	private final int _steal_chance;

	private final int _min_steal_count;

	private final int _max_steal_count;

	private final boolean _is_broadcast;

	private final boolean _drop_on_floor;

	private final int _anti_steal_item_id;

	public L1ItemSteal(final int item_id, final int level, final int mete_level, final int steal_chance,
			final int min_steal_count, final int max_steal_count, final boolean is_broadcast,
			final boolean drop_on_floor, final int anti_steal_item_id) {
		_item_id = item_id;
		_level = level;
		_mete_level = mete_level;
		_steal_chance = steal_chance;
		_min_steal_count = min_steal_count;
		_max_steal_count = max_steal_count;
		_is_broadcast = is_broadcast;
		_drop_on_floor = drop_on_floor;
		_anti_steal_item_id = anti_steal_item_id;
	}

	public final int getItemId() {
		return _item_id;
	}

	public final int getLevel() {
		return _level;
	}

	public final int getMeteLevel() {
		return _mete_level;
	}

	public final int getStealChance() {
		return _steal_chance;
	}

	public final int getMinStealCount() {
		return _min_steal_count;
	}

	public final int getMaxStealCount() {
		return _max_steal_count;
	}

	public final boolean isBroadcast() {
		return _is_broadcast;
	}

	public final boolean isDropOnFloor() {
		return _drop_on_floor;
	}

	public final int getAntiStealItemId() {
		return _anti_steal_item_id;
	}
}
