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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HPMeter;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_Party;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.RandomArrayList;



/**
 * 隊伍
 * @author admin
 *
 */
public class L1Party {
	
	private static final Log _log = LogFactory.getLog(L1Party.class);

	private final List<L1PcInstance> _membersList = new CopyOnWriteArrayList<L1PcInstance>();

	private L1PcInstance _leader = null;
	
	/**
	 * 加入新的隊伍成員
	 * @param pc
	 */
	public void addMember(final L1PcInstance pc) {
		try {
			if (pc == null) {
				throw new NullPointerException();
			}
			if (_membersList.size() == ConfigAlt.MAX_PARTY_SIZE || _membersList.contains(pc)) {
				return;
			}

			if (_membersList.isEmpty()) {// 隊員清單為空
				// 初始化設置隊長
				setLeader(pc);
			}
			
			// 加入清單
			_membersList.add(pc);
			
			// 設置隊伍數據
			pc.setParty(this);
			
			// 顯示隊伍UI數據
			showAddPartyInfo(pc);
			
			// 組隊中死亡隊員名字顏色變化
			final S_Party packet = new S_Party(0x6c, pc, 0);
			if (pc.getParty() != null) {
				for (final L1PcInstance member : pc.getParty().getMembers()) {
					if (member != pc && pc.isDead()) {
						member.sendPackets(packet);
					}
					if (member != pc && member.isDead()) {
						for (L1PcInstance member2 : member.getParty().getMembers()) {
							if (member2 != member)
								member2.sendPackets(new S_Party(0x6c, member, 0));
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}		
	}
	
	/**
	 * 移出隊伍成員
	 * @param pc
	 */
	private void removeMember(final L1PcInstance pc) {
		try {
			if (!_membersList.contains(pc)) {
				return;
			}
			_membersList.remove(pc);
			
			pc.setParty(null);
			if (!_membersList.isEmpty()) {
				deleteMiniHp(pc);
			}
			pc.sendPackets(new S_HPMeter(pc.getId(), 0xff, 0xff));// XXX
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 隊伍成員尚未飽和
	 * @return
	 */
	public boolean isVacancy() {
		return _membersList.size() < ConfigAlt.MAX_PARTY_SIZE;
	}
	
	/**
	 * 剩餘可加入隊伍人數
	 * @return
	 */
	public int getVacancy() {
		return ConfigAlt.MAX_PARTY_SIZE - _membersList.size();
	}
	
	/**
	 * 是否為隊員
	 * @param pc
	 * @return
	 */
	public boolean isMember(final L1PcInstance pc) {
		return _membersList.contains(pc);
	}
	
	/**
	 * 設置隊長
	 * @param pc
	 */
	private void setLeader(final L1PcInstance pc) {
		_leader = pc;
	}
	
	/**
	 * 傳回隊長
	 * @return
	 */
	public L1PcInstance getLeader() {
		return _leader;
	}
	
	/**
	 * 是否為隊長
	 * @param pc
	 * @return
	 */
	public boolean isLeader(final L1PcInstance pc) {
		return pc.getId() == _leader.getId();
	}
	
	/**
	 * 全隊員名稱
	 * @return
	 */
	public String getMembersNameList() {
		StringBuilder stringBuilder = new StringBuilder();
		
		if (this._membersList.isEmpty()) {
			return null;
		}
		
		if (this._membersList.size() <= 0) {
			return null;
		}
		
		for (L1PcInstance pc : _membersList) {
			stringBuilder.append(pc.getName() + " ");
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * 顯示組隊UI介面
	 * @param pc
	 */
	private void showAddPartyInfo(final L1PcInstance pc) {
		final S_Party packet = new S_Party(S_PacketBox.PARTY_ADD_NEWMEMBER, pc);
		final S_Party packet1 = new S_Party(S_PacketBox.PARTY_OLD_MEMBER, pc);
		
		for (final L1PcInstance member : this.getMembers()) {
			if (pc.getId() == getLeader().getId() && getNumOfMembers() == 1) {
				continue;
			}
			
			if (pc.getId() == member.getId()) {
				for (L1PcInstance m : getMemberList()) {
					pc.sendPackets(new S_HPMeter(m));
				}
				pc.sendPackets(packet);
			} else {
				member.sendPackets(packet1);
				member.sendPackets(new S_Party(pc.getName()));
				member.sendPackets(new S_HPMeter(pc));
			}
			member.sendPackets(new S_Party(S_PacketBox.PARTY_REFRESH, member));
		}
	}
	
	/**
	 * 隊員離開時HP顯示的移除
	 * @param pc
	 */
	private void deleteMiniHp(final L1PcInstance pc) {
		final S_HPMeter packet = new S_HPMeter(pc.getId(), 0xff, 0XFF);
		for (L1PcInstance member : getMembers()) {
			pc.sendPackets(new S_HPMeter(member.getId(), 0xff, 0XFF));
			member.sendPackets(packet);
		}
	}
	
	/**
	 * 隊員血條更新
	 * @param pc
	 */
	public void updateMiniHP(final L1PcInstance pc) {
		final S_HPMeter packet = new S_HPMeter(pc.getId(), 100 * pc.getCurrentHp() / pc.getMaxHp(), 100 * pc.getCurrentMp() / pc.getMaxMp());
		for (final L1PcInstance member : getMembers()) {
			member.sendPackets(packet);
		}
	}
	
	/**
	 * 解散隊伍
	 */
	public void breakup() {
		final S_ServerMessage packet = new S_ServerMessage(418);
		for (L1PcInstance member : getMembers()) {
			removeMember(member);
			member.sendPackets(packet);
		}
	}
	
	/**
	 * 隊長委任給其他隊員
	 * @param pc
	 */
	public void passLeader(final L1PcInstance pc) {
		final S_Party packet = new S_Party(0x6A, pc);
		for (L1PcInstance member : getMembers()) {
			member.getParty().setLeader(pc);
			member.sendPackets(packet);
		}
	}


	/**
	 * 離開隊伍
	 * @param pc
	 */
	public void leaveMember(final L1PcInstance pc) {
		if (getNumOfMembers() == 2) { // 隊伍成員總數只有2位成員
			breakup(); // 解散隊伍
		} else {
			removeMember(pc); // 移除成員pc
			if (isLeader(pc)) { // 離開的如果是隊長
				for (final L1PcInstance member : getMembers()) {
					passLeader(member);
					break;
				}
			}
			for (final L1PcInstance member : getMembers()) {
				sendLeftMessage(member, pc);
			}
			if (pc != null)
				sendLeftMessage(pc, pc);
		}
	}
	
	/**
	 * 驅逐隊員
	 * @param pc
	 */
	public void kickMember(final L1PcInstance pc) {
		if (getNumOfMembers() == 2) {
			breakup();
		} else {
			removeMember(pc);
			for (L1PcInstance member : getMembers()) {
				sendLeftMessage(member, pc);
			}
			sendKickMessage(pc);
		}
	}
	
	/**
	 * 隊伍成員清單
	 * @return
	 */
	public L1PcInstance[] getMembers() {
		return _membersList.toArray(new L1PcInstance[_membersList.size()]);
	}
	
	/**
	 * 隊伍成員數量
	 * @return
	 */
	public int getNumOfMembers() {
		return _membersList.size();
	}
	
	/**
	 * 發送驅逐隊員訊息
	 * @param kickpc
	 */
	private void sendKickMessage(final L1PcInstance kickpc) {
		kickpc.sendPackets(new S_ServerMessage(419));
	}
	
	/**
	 * 隊員離開隊伍訊息
	 * @param sendTo
	 * @param left
	 */
	private void sendLeftMessage(final L1PcInstance sendTo, final L1PcInstance left) {
		sendTo.sendPackets(new S_ServerMessage(420, left.getName()));
	}
	
	/**
	 * 該隊伍目前人數(同地圖)
	 * @return
	 */
	public int partyUserInMap(final short mapid) {
		int i = 0;
		if (this._membersList.isEmpty()) {
			return 0;
		}
		if (this._membersList.size() <= 0) {
			return 0;
		}
		
		for (final L1PcInstance tgpc : getMembers()) {
			short tgpcMapid = tgpc.getMapId();
			if (tgpcMapid == mapid) {
				i += 1;
			}
		}
		return i;
	}
	
	public List<L1PcInstance> getMemberList() {
		return _membersList;
	}
	
	public final int checkMentor(final L1Apprentice apprentice) {
		int checkType = 0;
		for (final L1PcInstance member : _membersList) {
			if (apprentice.getMaster().getId() == member.getId()) {
				checkType += 4;
			} else if (apprentice.getTotalList().contains(member)) {
				checkType++;
			}
		}
		return checkType;
	}
	
	/**
	 * 傳回隊長OBJID
	 * 
	 * @return
	 */
	public int getLeaderID() {
		return _leader.getId();
	}
	
	public L1PcInstance partyUser() {
		final List<L1PcInstance> userList = new CopyOnWriteArrayList<L1PcInstance>();
		for (final L1PcInstance pc : _membersList) {
			if (!_leader.equals(pc)) {
				userList.add(pc);
			}
		}
		if (!userList.isEmpty()) {
			return userList.get(RandomArrayList.getInt(userList.size()));
		}
		return null;
	}
	
	public List<String> getPartyMembers() {
		final List<String> partyMembers = new CopyOnWriteArrayList<String>();
		for (final L1PcInstance pc : _membersList) {
			if (!_leader.equals(pc)) {
				partyMembers.add(pc.getName());
			}
		}
		return partyMembers;
	}

}
