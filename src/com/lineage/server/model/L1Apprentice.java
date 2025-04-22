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
package com.lineage.server.model;

import java.util.ArrayList;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 師徒系統
 * 
 * @author terry0412
 */
public final class L1Apprentice {

	private static final int MAX_SIZE = 4;

	private ArrayList<L1PcInstance> totalList;

	public L1Apprentice(final L1PcInstance master, final L1PcInstance... mentorList) {
		_master = master;
		// 建立容量為四單位的ArrayList
		totalList = new ArrayList<L1PcInstance>(MAX_SIZE);
		for (final L1PcInstance apprentice : mentorList) {
			totalList.add(apprentice);
		}
	}

	private L1PcInstance _master; // 師父

	public final L1PcInstance getMaster() {
		return _master;
	}

	public final void setMaster(final L1PcInstance master) {
		_master = master;
	}

	public final ArrayList<L1PcInstance> getTotalList() {
		return totalList;
	}

	public final boolean addApprentice(final L1PcInstance l1char) {
		if (checkSize()) {
			return totalList.add(l1char);
		}
		return false;
	}

	public final boolean isApprentice(final int objid) {
		for (final L1PcInstance apprentice : totalList) {
			if (apprentice.getId() == objid) {
				return true;
			}
		}
		return false;
	}

	public final boolean checkSize() {
		return totalList.size() < MAX_SIZE;
	}

	public final void clear() {
		_master = null;
		totalList.clear();
		totalList = null;
	}
}
