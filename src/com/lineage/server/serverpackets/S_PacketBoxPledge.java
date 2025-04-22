package com.lineage.server.serverpackets;

import java.util.ArrayList;

import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.WorldClan;

public class S_PacketBoxPledge extends ServerBasePacket {
	private byte[] _byte = null;

	public S_PacketBoxPledge(final int type, final L1PcInstance pc, final String name, final int rank) {
		
		switch (type) {
		case 1:
			PledgeOne(pc);
			break;
		case 2:
			PledgeTwo(pc);
			break;
		case 3:
			PledgeAdd(name, rank);
			break;
		case 4:
			PledgeDel(name);
			break;
		case 5:
			Pledge(pc);
			break;
		case 6:
			PledgeCloseWindow();
			break;
		}
	}

	/**
	 * 初始化血盟清單(王族專用)
	 * 
	 * @param 0x18
	 * @param test
	 * @param pc 王族 -- 階級 -- [官方棄用?] 0, none 1, none 2, 一般 3, 副君主 4, 聯盟君主 5,
	 *            修習騎士 6, 守護騎士 [官方現用] 7, 一般 8, 修習騎士 9, 守護騎士 10, 聯盟君主 [Server]
	 *            opcode = 100 0000: 64 18 02 00 00 00 4b 5a 4b 00 0a b6 57 ad
	 *            ab ad d.....KZK...W... 0010: b5 00 07 01 00 00 00 4b 5a 4b 00
	 *            00 c9 05 4f 9c .......KZK....O.
	 */
	private void PledgeOne(final L1PcInstance pc) {
		final String clanName = pc.getClanname();
		final L1Clan clan = WorldClan.get().getClan(clanName);
		final ArrayList<L1PcInstance> clanMemberList = clan.getAllMembersRank();
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x18); // 初始化血盟清單
		writeD(clan.getAllMembersSize()); // 所有的血盟成員數量
		for (final L1PcInstance member : clanMemberList) {
			writeS(member.getName()); // 血盟成員名稱
			writeC(member.getClanRank()); // 血盟成員階級
		}
		final L1PcInstance[] onlineMember = clan.getOnlineClanMember();
		writeD(onlineMember.length); // 在線上的血盟成員數量
		for (final L1PcInstance onlinePc : onlineMember) {
			writeS(onlinePc.getName()); // 在線的血盟成員名稱
		}
		// 血盟創造日期(王族生日)
		writeD((int) (clan.getBirthDay().getTime() / 1000));
		writeS(clan.getLeaderName());
	}

	/**
	 * 第二次查詢血盟清單 0x1d
	 * 
	 * @param test
	 * @param pc 王族 -- 階級 -- [官方棄用?] 0, none 1, none 2, 一般 3, 副君主 4, 聯盟君主 5,
	 *            修習騎士 6, 守護騎士 [官方現用] 7, 一般 8, 修習騎士 9, 守護騎士 10, 聯盟君主
	 */
	private void PledgeTwo(final L1PcInstance pc) {
		final String clanName = pc.getClanname();
		final L1Clan clan = WorldClan.get().getClan(clanName);
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x1d); // 第二次查詢血盟清單
		writeD(clan.getOnlineClanMemberSize()); // 在線上的血盟成員數量
		// 使用迴圈取得在線上的血盟成員名稱與階級
		for (final L1PcInstance OnlinePc : clan.getOnlineClanMember()) {
			writeS(OnlinePc.getName()); // 在線的血盟成員名稱
			writeC(OnlinePc.getClanRank()); // 在線的血盟成員階級
			writeC(0x00);// 3.63 ADD
		}
		writeS(clan.getLeaderName());
	}

	/**
	 * 加入血盟更新列表王族專用(加入)
	 * 
	 * @param name
	 * @param rank 加入血盟的角色id:超重音 階級:一般 0x07
	 */
	private void PledgeAdd(final String name, final int rank) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x19);
		writeS(name);
		writeC(rank);
		// 以下六個byte未知用途每次都送不一樣填充用?
		// randomInt6();
	}

	/**
	 * 退出(驅逐)血盟更新列表
	 * 
	 * @param name
	 * @param rank 退出血盟的角色id:超重音
	 */
	private void PledgeDel(final String name) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x1a);
		writeS(name);
		// 以下7個byte 未知用途 每次都送不一樣 填充用?
		// randomByte(); // 填充封包
		// randomInt6(); // 填充封包
	}

	/**
	 * 血盟成員查詢血盟清單 0x77
	 * 
	 * @param test
	 * @param pc 血盟成員 -- 階級 -- [官方棄用?] 0, none 1, none 2, 一般 3, 副君主 4, 聯盟君主 5,
	 *            修習騎士 6, 守護騎士 [官方現用] 7, 一般 8, 修習騎士 9, 守護騎士 10, 聯盟君主 [Server]
	 *            opcode = 100 0000: 64 77 01 00 00 00 b6 57 ad ab ad b5 00 07
	 *            00 c9 dw.....W........ 0010: 05 4f ca 5d 7e 9e fa 94 .O.]~...
	 */
	private void Pledge(final L1PcInstance pc) {
		final String clanName = pc.getClanname();
		final L1Clan clan = WorldClan.get().getClan(clanName);
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x77); // 血盟成員查詢血盟清單
		writeD(clan.getOnlineClanMemberSize()); // 在線上的血盟成員數量
		// 使用迴圈取得在線上的血盟成員名稱與階級
		for (final L1PcInstance OnlinePc : clan.getOnlineClanMember()) {
			writeS(OnlinePc.getName()); // 在線的血盟成員名稱
			writeC(OnlinePc.getClanRank()); // 在線的血盟成員階級
		}
		// 血盟創造日期(暫時使用系統日期)
		writeD((int) (clan.getBirthDay().getTime() / 1000));
		writeS(clan.getLeaderName());
	}

	/**
	 * 關閉血盟清單視窗0x1c 只有王族使用
	 */
	private void PledgeCloseWindow() {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x1c); // 關閉血盟清單視窗
	}

	@Override
	public byte[] getContent() {
		return getBytes();
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
