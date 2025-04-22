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

package com.lineage.server.model;

import java.util.TimerTask;
import java.util.Timer;

import com.lineage.server.datatables.RandomMobTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.utils.L1SpawnUtil;


public class RandomMobDeleteTimer extends TimerTask {
	private int _randomMobId;
	private L1NpcInstance _npc[];

	public RandomMobDeleteTimer(int randomMobId, L1NpcInstance[] npc) {
		_randomMobId = randomMobId;
		_npc = npc;
	}

	@Override
	public void run() {
		L1SpawnUtil.spawn(_randomMobId);
		for (L1NpcInstance npc : _npc) {
			npc.deleteMe();
		}
		this.cancel();
	}

	public void begin() {
		Timer timer = new Timer();
		if (!(RandomMobTable.getInstance().getTimeSecondToDelete(_randomMobId) <= 0)) {
			timer.schedule(this, RandomMobTable.getInstance()
					.getTimeSecondToDelete(_randomMobId) * 1000);
		}

	}
}