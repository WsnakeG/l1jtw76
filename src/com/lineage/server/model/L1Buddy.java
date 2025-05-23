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
 * Author: ChrisLiu.2007.07.20
 */
package com.lineage.server.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldDe;


public class L1Buddy {
	private final int _charId;

	private final LinkedHashMap<Integer, String> _buddys = new LinkedHashMap<Integer, String>();

	public L1Buddy(int charId) {
		_charId = charId;
	}

	public int getCharId() {
		return _charId;
	}

	public boolean add(int objId, String name) {
		if (_buddys.containsKey(objId)) {
			return false;
		}
		_buddys.put(objId, name);
		return true;
	}

	public boolean remove(int objId) {
		String result = _buddys.remove(objId);
		return (result != null ? true : false);
	}

	public boolean remove(String name) {
		int id = 0;
		for (Map.Entry<Integer, String> buddy : _buddys.entrySet()) {
			if (name.equalsIgnoreCase(buddy.getValue())) {
				id = buddy.getKey();
				break;
			}
		}
		if (id == 0) {
			return false;
		}
		_buddys.remove(id);
		return true;
	}

	public String getOnlineBuddyListString() {
		String result = new String("");
//		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
//		更換迭代方式  
		Collection<?> al = World.get().getAllPlayers();
		Iterator<?> it = al.iterator();
		while(it.hasNext()){//更換迭代方式  
			L1PcInstance pc = (L1PcInstance)it.next();//更換迭代方式  
			if (_buddys.containsKey(pc.getId())) {
				result += pc.getName() + " ";
			}
		}

		// De虛擬假人部分
		Collection<?> allde = WorldDe.get().all();
		Iterator<?> ittt = allde.iterator();
		while(ittt.hasNext()){//更換迭代方式  
			L1DeInstance fakepc = (L1DeInstance)ittt.next();//更換迭代方式  
			if (_buddys.containsKey(fakepc.getId())) {
				result += fakepc.getNameId() + " ";
			}
		}
		
		return result;
	}
	
	public String getBuddyListString() {
		String result = new String("");
		for (String name : _buddys.values()) {
			result += name + " ";
		}
		return result;
	}

	public boolean containsId(int objId) {
		return _buddys.containsKey(objId);
	}

	public boolean containsName(String name) {
		for (String buddyName : _buddys.values()) {
			if (name.equalsIgnoreCase(buddyName)) {
				return true;
			}
		}
		return false;
	}

	public int size() {
		return _buddys.size();
	}
	
	public String[] getBuddyListNames() {
		return _buddys.values().toArray(new String[0]);
	}
}
