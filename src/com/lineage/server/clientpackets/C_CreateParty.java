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

package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 要求創立隊伍
 * @author admin
 *
 */
public class C_CreateParty extends ClientBasePacket {

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			
			if (pc.isGhost()) { // 鬼魂模式
				return;
			}
			
			if (pc.isDead()) { // 死亡
				return;
			}
			
			if (pc.isTeleport()) { // 傳送中
				return;
			}

			final int type = readC();
			if (type == 0 || type == 1) { // パーティー(パーティー自動分配ON/OFFで異なる)
				final int targetId = readD();
				final L1Object temp = World.get().findObject(targetId);
				if (temp instanceof L1PcInstance) {
					final L1PcInstance targetPc = (L1PcInstance) temp;

					if (pc.getId() == targetPc.getId()) {
						return;
					}

					if ((!pc.getLocation().isInScreen(targetPc.getLocation()) || (pc.getLocation().getTileLineDistance(targetPc.getLocation()) > 7))) {
						// 邀請組隊時，對象不再螢幕內或是7步內
						pc.sendPackets(new S_ServerMessage(952));
						return;
					}

					if (targetPc.isInParty()) {
						// 您無法邀請已經參加其他隊伍的人。
						pc.sendPackets(new S_ServerMessage(415));
						return;
					}

					if (pc.isInParty()) {
						if (pc.getParty().isLeader(pc)) {
							targetPc.setPartyID(pc.getId());
							// 玩家 %0%s 邀請您加入隊伍？(Y/N)
							targetPc.sendPackets(new S_Message_YN(953, pc.getName()));
						} else {
							// 只有領導者才能邀請其他的成員。
							pc.sendPackets(new S_ServerMessage(416));
						}
					} else {
						targetPc.setPartyID(pc.getId());
						// 玩家 %0%s 邀請您加入隊伍？(Y/N)
						targetPc.sendPackets(new S_Message_YN(953, pc.getName()));
					}
				}
			} else if (type == 2) { // 聊天組隊
				String name = readS();
				
				L1PcInstance targetPc = World.get().getPlayer(name);
				
				if (pc.getId() == targetPc.getId()) {
					return;
				}
				if ((!pc.getLocation().isInScreen(targetPc.getLocation()) || (pc
						.getLocation().getTileLineDistance(
								targetPc.getLocation()) > 7))) {
					// 邀請組隊時，對象不再螢幕內或是7步內
					pc.sendPackets(new S_ServerMessage(952));
					return;
				}

				if (targetPc.isInChatParty()) {
					// 您無法邀請已經參加其他隊伍的人。
					pc.sendPackets(new S_ServerMessage(415));
					return;
				}

				if (pc.isInChatParty()) {
					if (pc.getChatParty().isLeader(pc)) {
						targetPc.setPartyID(pc.getId());
						// 您要接受玩家 %0%s 提出的隊伍對話邀請嗎？(Y/N)
						targetPc.sendPackets(new S_Message_YN(951, pc.getName()));
					} else {
						// 只有領導者才能邀請其他的成員。
						pc.sendPackets(new S_ServerMessage(416));
					}
				} else {
					targetPc.setPartyID(pc.getId());
					// 您要接受玩家 %0%s 提出的隊伍對話邀請嗎？(Y/N)
					targetPc.sendPackets(new S_Message_YN(951, pc.getName()));
				}
			} else if (type == 3) {// 隊長委任
				L1Party part = pc.getParty();
				if (part == null) {
					return;
				}
				if (!part.isLeader(pc)) {
					// 不是隊長時, 不可使用
					pc.sendPackets(new S_ServerMessage(1697));
					return;
				}
				// 取得目標物件編號
				int targetId = readD();
				// 嘗試取得目標
				L1Object temp = World.get().findObject(targetId);

				if (temp == null) {
					pc.sendPackets(new S_ServerMessage(1694));// 没有选择目标
					return;
				}
				if (temp instanceof L1PcInstance) {
					L1PcInstance member = (L1PcInstance) temp;
					if (!part.isMember(member)) {// 不是是自己的队员
						pc.sendPackets(new S_ServerMessage(1696));
						return;
					}
					part.passLeader(member);
				}
			} else if (type == 4 || type == 5) {// 組隊type4 XXX 7.2新增 來源操作:行動視窗 封包傳遞邀請對象名稱  組隊type5組隊寶物自動分配 XXX 7.2新增 來源操作:行動視窗 封包傳遞邀請對象名稱
				String name = readS();
				
				if (name.isEmpty()) {
					return;
				}
				
				L1PcInstance targetPc = World.get().getPlayer(name);
				
				if (targetPc == null) {
					return;
				}
				
				// 邀請的對象與提出邀請的對象相等
				if (pc.getId() == targetPc.getId()) {
					return;
				}
				
				if ((!pc.getLocation().isInScreen(targetPc.getLocation()) || (pc.getLocation().getTileLineDistance(targetPc.getLocation()) > 7))) {
					// 邀請組隊時，對象不再螢幕內或是7步內
					pc.sendPackets(new S_ServerMessage(952));
					return;
				}

				if (targetPc.isInParty()) {
					// 您無法邀請已經參加其他隊伍的人。
					pc.sendPackets(new S_ServerMessage(415));
					return;
				}

				if (pc.isInParty()) {
					if (pc.getParty().isLeader(pc)) {
						targetPc.setPartyID(pc.getId());
						// 玩家 %0%s 邀請您加入隊伍？(Y/N)
						targetPc.sendPackets(new S_Message_YN(953, pc.getName()));
					} else {
						// 只有領導者才能邀請其他的成員。
						pc.sendPackets(new S_ServerMessage(416));
					}
				} else {
					targetPc.setPartyID(pc.getId());
					// 玩家 %0%s 邀請您加入隊伍？(Y/N)
					targetPc.sendPackets(new S_Message_YN(953, pc.getName()));
				}
			}
		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			this.over();
		}
	}

	@Override
	public String getType() {
		return "[C] " + this.getClass().getSimpleName();
	}

}
