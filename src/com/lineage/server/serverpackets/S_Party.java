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

package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 隊伍系統
 * @author admin
 *
 */
public class S_Party extends ServerBasePacket {

	private byte[] _byte = null;
	
	//[Client] opcode = 130 創立隊伍 隊長:超重音 隊員:超重低音 隊長發包部分
	//0000: 82 00 f5 4b 28 00 00 00                            ...K(...
	//======================================================================
	//[Server] opcode = 51
	//0000: 33 88 bb 26 00 00 24 04                            3..&..$.
	//======================================================================
	//[Server] opcode = 10
	//0000: 0a 69 f5 4b 28 00 b6 57 ad ab a7 43 ad b5 00 72    .i.K(..W...C...r
	//0010: 01 00 00 db 7f 12 80 67                            ......g
	//======================================================================
	//[Server] opcode = 17
	//0000: 11 a8 01 01 b6 57 ad ab a7 43 ad b5 00 24 4e a3    .....W...C...$N.
	//======================================================================
	//[Server] opcode = 51
	//0000: 33 f5 4b 28 00 64 5a 92                            3.K(.dZ.
	//======================================================================
	//[Server] opcode = 10
	//0000: 0a 6e 02 88 bb 26 00 72 01 00 00 dc 7f 13 80 f5    .n...&.r.......
	//0010: 4b 28 00 72 01 00 00 db 7f 12 80 e4 78 0f 02 14    K(.r.......x...
	//======================================================================
	
	
	//[Server] opcode = 70 創立隊伍  隊長:超重低音 隊員:超重音 隊員發包部分
	//0000: 46 00 00 13 01 00 00 b9 03 b6 57 ad ab a7 43 ad    F.........W...C.
	//0010: b5 00 28 00 c2 7f ea 7f                            ..(...
	//======================================================================
	//[Client] opcode = 129
	//0000: 81 00 00 13 01 00 00 b9 03 01 00 00                ............
	//======================================================================
	//[Server] opcode = 51
	//0000: 33 f5 4b 28 00 64 26 09                            3.K(.d&.
	//======================================================================
	//[Server] opcode = 17
	//0000: 11 a8 01 01 b6 57 ad ab ad b5 00 cd 1c 00 00 0d    .....W..........
	//======================================================================
	//[Server] opcode = 51
	//0000: 33 88 bb 26 00 00 00 7a                            3..&...z
	//======================================================================
	//[Server] opcode = 10
	//0000: 0a 68 01 f5 4b 28 00 b6 57 ad ab a7 43 ad b5 00    .h..K(..W...C...
	//0010: 64 72 01 00 00 db 7f 12 80 88 bb 26 00 b6 57 ad    dr........&..W.
	//0020: ab ad b5 00 64 72 01 00 00 dc 7f 13 80 00 26 80    ....dr.......&.
	//======================================================================
	//[Server] opcode = 10
	//0000: 0a 6e 02 f5 4b 28 00 72 01 00 00 db 7f 12 80 88    .n..K(.r.......
	//0010: bb 26 00 72 01 00 00 dc 7f 13 80 16 00 03 08 00    .&.r...........
	//======================================================================

	/**
	 * 隊伍系統
	 * @param type
	 * @param pc
	 */
	public S_Party(int type, L1PcInstance pc) {
		switch (type) {
		case S_PacketBox.PARTY_ADD_NEWMEMBER:
			newMember(pc);
			break;
		case S_PacketBox.PARTY_OLD_MEMBER:
			oldMember(pc);
			break;
		case S_PacketBox.PARTY_CHANGE_LEADER:
			changeLeader(pc);
		case S_PacketBox.PARTY_REFRESH:
			refreshParty(pc);
			break;
		default:
			break;
		}
	}

	public S_Party(String htmlid, int objid) {
		buildPacket(htmlid, objid, "", "", 0);
	}

	public S_Party(String htmlid, int objid, String partyname,
			String partymembers) {
		buildPacket(htmlid, objid, partyname, partymembers, 1);
	}

	public S_Party(int type, L1PcInstance pc, int live) {// 3.3C 組隊系統死亡更新
		refreshName(pc, live);
	}

	private void buildPacket(String htmlid, int objid, String partyname,
			String partymembers, int type) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS(htmlid);
		writeH(type);
		writeH(0x02);
		writeS(partyname);
		writeS(partymembers);
	}

	/**
	 * 新加入隊伍的玩家
	 * 
	 * @param pc
	 */
	public void newMember(L1PcInstance pc) {
		L1PcInstance leader = pc.getParty().getLeader();
		L1PcInstance member[] = pc.getParty().getMembers();
		//double nowhp = 0.0d;
		//double maxhp = 0.0d;
		if (pc.getParty() == null) {
			return;
		} else {
			writeC(S_OPCODE_PACKETBOX);
			writeC(S_PacketBox.PARTY_ADD_NEWMEMBER);// 0x68
			//nowhp = leader.getCurrentHp();
			//maxhp = leader.getMaxHp();
			writeC(member.length - 1);
			writeD(leader.getId());
			writeS(leader.getName());
			
			//writeC((int) (nowhp / maxhp) * 100);
			writeC(leader.getType());
			writeC(0x00);
			writeC(0x00);
			
			writeC((leader.getCurrentHp() * 100) / leader.getMaxHp());
			writeC((leader.getCurrentMp() * 100) / leader.getMaxMp());
			//writeC(100);
			//writeC(100);
		      
			writeD(leader.getMapId());
			writeH(leader.getX());
			writeH(leader.getY());
			//writeC(0);
			writeC(0);
			writeC(0);
			writeC(0);
			writeC(0);
			writeC(1);
		      
			for (int i = 0, a = member.length; i < a; i++) {
				if (member[i].getId() == leader.getId() || member[i] == null) {
					continue;
				}

				//nowhp = member[i].getCurrentHp();
				//maxhp = member[i].getMaxHp();
				//writeD(i);//
				
				writeD(member[i].getId());
				writeS(member[i].getName());
				
				writeC(member[i].getType());
				writeC(0x00);// unknow
				writeC(0x00);// unknow
				
				//writeC((int) (nowhp / maxhp) * 100);
				writeC((member[i].getCurrentHp() * 100) / member[i].getMaxHp());
				writeC((member[i].getCurrentMp() * 100) / member[i].getMaxMp());
				//writeC(64);
				//writeC(64);
		        
				writeD(member[i].getMapId());
				writeH(member[i].getX());
				writeH(member[i].getY());
				//writeD(0);
		        writeC(0);
		        writeC(0);
		        writeC(0);
		        writeC(0);
		        writeC(0);
			}
			//writeC(0x00);
			writeC(0);
		}
	}

	/**
	 * 舊的隊員
	 * 
	 * @param pc
	 */
	//[Server] opcode = 10
	//0000: 0a 69 f5 4b 28 00 b6 57 ad ab a7 43 ad b5 00 72    .i.K(..W...C...r
	//0010: 01 00 00 db 7f 12 80 67                            ......g
	//======================================================================
	public void oldMember(L1PcInstance pc) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.PARTY_OLD_MEMBER);
		writeD(pc.getId());
		writeS(pc.getName());
		
		writeC(pc.getType());
		writeC(0x00);
		writeC(0x00);
		
		writeD(pc.getMapId());
		writeH(pc.getX());
		writeH(pc.getY());
	}

	/**
	 * 更換隊長
	 * 
	 * @param pc
	 */
	public void changeLeader(L1PcInstance pc) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.PARTY_CHANGE_LEADER);
		writeD(pc.getId());
		writeH(0x0000);
	}

	/**
	 * 更新隊伍
	 * 
	 * @param pc
	 */
	public void refreshParty(L1PcInstance pc) {
		L1PcInstance member[] = pc.getParty().getMembers();
		if (pc.getParty() == null) {
			return;
		} else {
			writeC(S_OPCODE_PACKETBOX);
			writeC(S_PacketBox.PARTY_REFRESH);
			writeC(member.length);
			for (int i = 0, a = member.length; i < a; i++) {
				writeD(member[i].getId());
				writeD(member[i].getMapId());
				writeH(member[i].getX());
				writeH(member[i].getY());
			}
			//writeC(0x00);
		}
	}

	/**
	 * 更新死亡隊員名稱UI顏色
	 * 
	 * @param pc
	 */
	//[Server] opcode = 10
	//0000: 0a 6c ba 2b 3b 00 00 96                            .l.+;...
	//[Server] opcode = 10
	//0000: 0a 6c ba 2b 3b 00 01 15                            .l.+;...
	public void refreshName(L1PcInstance pc, int live) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.PARTY_DEATH_REFRESHNAME);
		writeD(pc.getId());
		writeC(live);
	}
	

	public S_Party(final String name) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(539);
		this.writeInt32(1, 2);//??
		this.writeString(2, name);// 名稱
		this.randomShort();
	}
	
	public S_Party(int v1, int v2) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(339);
		this.writeInt32(1, v1);//??
		this.writeInt32(2, v2);//??
		this.randomShort();
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName();
	}
	
	@Override
	public byte[] getContentBIG5() { //20240901
		if (_byte == null) {
			_byte = _bao3.toByteArray();
		}
		return _byte;
	}
	
	@Override
	public byte[] getContentGBK() { //20240901
		if (_byte == null) {
			_byte = _bao5.toByteArray();
		}
		return _byte;
	}

}