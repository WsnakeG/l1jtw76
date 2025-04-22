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
package com.lineage.server.model.classes;

class L1DragonKnightClassFeature extends L1ClassFeature {
	@Override
	public int getAcDefenseMax(int ac) {
		return ac / 3;
	}

	@Override
	public int getMagicLevel(int playerLevel) {
		return Math.min(4, playerLevel / 9);
	}

	@Override
	public int getAttackLevel(final int playerLevel) {
		return playerLevel / 13;
	}

	@Override
	public int getHitLevel(final int playerLevel) {
		return playerLevel / 25;
	}
	
	@Override
	public int getAcLevel(int playerLevel) {
		// TODO 自動產生的方法 Stub
		return 0;
	}

	@Override
	public int getClassOriginalMr() {
		// TODO 自動產生的方法 Stub
		return 0;
	}
}