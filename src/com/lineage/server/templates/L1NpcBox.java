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
 * @author terry0412
 */
public final class L1NpcBox {

	private final int _needKeyId;

	private final int _resetTimeSecsMin;

	private final int _resetTimeSecsMax;

	private final List<L1ItemBox> _createItemBoxes;

	private final List<Integer> _mobNpcIdList;

	public L1NpcBox(final int needKeyId, final int resetTimeSecsMin, final int resetTimeSecsMax,
			final List<L1ItemBox> createItemBoxes, final List<Integer> mobNpcIdList) {
		_needKeyId = needKeyId;
		_resetTimeSecsMin = resetTimeSecsMin;
		_resetTimeSecsMax = resetTimeSecsMax;
		_createItemBoxes = createItemBoxes;
		_mobNpcIdList = mobNpcIdList;
	}

	public final int get_needKeyId() {
		return _needKeyId;
	}

	public final int get_resetTimeSecsMin() {
		return _resetTimeSecsMin;
	}

	public final int get_resetTimeSecsMax() {
		return _resetTimeSecsMax;
	}

	public final List<L1ItemBox> get_createItemBoxes() {
		return _createItemBoxes;
	}

	public final List<Integer> get_mobNpcIdList() {
		return _mobNpcIdList;
	}
}
