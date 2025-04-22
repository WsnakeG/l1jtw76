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
public final class L1ItemBox {

	private final int _chance;

	private final int _itemId;

	private final int _itemCount;

	public L1ItemBox(final int chance, final int itemId, final int itemCount) {
		_chance = chance;
		_itemId = itemId;
		_itemCount = itemCount;
	}

	public final int getChance() {
		return _chance;
	}

	public final int getItemId() {
		return _itemId;
	}

	public final int getItemCount() {
		return _itemCount;
	}
}
