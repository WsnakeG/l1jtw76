package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.lineage.config.Config;
import com.lineage.server.datatables.ClanMembersTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.WorldWar;



/**
 * 血盟UI (370ADD)
 * @author user
 *
 */
public class S_PledgeUI extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	/**
	 * 血盟資訊UI:血盟基本資訊 3.7TW 新增啟用 EventName:ES_PLEDGE_INFO
	 */
	public static final int ES_PLEDGE_INFO = 167;
	
	/**
	 * 血盟資訊UI:更新血盟公告 EventName:ES_PLEDGE_NOTI
	 */
	public static final int ES_PLEDGE_NOTI = 168;
	
	/**
	 * 血盟資訊UI:更新個人血盟備註 EventName:ES_PLEDGE_MEMBER_MEMO
	 */
	public static final int ES_PLEDGE_MEMBER_MEMO = 169;
	
	/**
	 * 血盟資訊UI:所有血盟成員的資訊(包含離線) 3.7TW 新增啟用 EventName:ES_PLEDGE_MEMBER_INFO
	 */
	public static final int ES_PLEDGE_MEMBER_INFO = 170;
	
	/**
	 * 血盟資訊UI:所有在線的血盟成員名稱資訊 EventName:ES_ONLINE_MEMBER_INFO
	 */
	public static final int ES_ONLINE_MEMBER_INFO = 171;
	
	/**
	 * 血盟成員UI資訊
	 * @param clan 資訊對象
	 * @param type 輸出屬性
	 */
	public S_PledgeUI(final L1Clan clan, final int type) {
		this.writeC(S_OPCODE_PACKETBOX);
		switch (type) {
		case ES_PLEDGE_INFO:// 血盟資訊 (血盟公告 血盟創立日 血盟名稱 血盟君主名稱 盟徽等..)
			this.writeC(ES_PLEDGE_INFO);// type
			this.writeS(clan.getClanName());// 血盟名稱
			this.writeS(clan.getLeaderName());// 血盟君主名稱
			this.writeD(clan.getClanId());// 血盟徽章號碼
			this.writeC(clan.getHouseId() > 0 ? 1 : 0);// 有小屋
			this.writeC(clan.getCastleId() > 0 ? 1 : 0);// 有城
			boolean ret = false;
			// 目前全部戰爭資訊取得
			for (final L1War war : WorldWar.get().getWarList()) {
				ret = war.checkClanInWar(clan.getClanName());
				if (ret) { // 是否正在戰爭中
					break;
				}
			}
			this.writeB(ret);// 是否戰爭中
			this.writeD((int) (clan.getBirthDay().getTime() / 1000));// 血盟創造日期(王族自身生日)
			try {
				byte[] clanText = clan.getClanShowNote().getBytes(Config.CLIENT_LANGUAGE_CODE);
				byte[] text = Arrays.copyOf(clanText, 478); // Arrays.copy(要被複製的陣列，新陣列的長度);
				this.writeByte(text);
			} catch (Exception e) {
				this.writeByte(new byte[478]);
			}
			/*this.writeS(clan.get_clan_info());// 血盟公告
			int infolen = 1;
			if (clan.get_clan_info() != null) {
				infolen += clan.get_clan_info().getBytes().length;
			}
			if (470 - infolen > 0) {
				final byte[]nullbytes = new byte[470 - infolen];
				this.writeByte(nullbytes);
			}*/			
			break;
			
		case ES_PLEDGE_NOTI:// 更新血盟資訊公告
			this.writeC(ES_PLEDGE_NOTI);// type
			try {
				byte[] clanText = clan.getClanShowNote().getBytes(Config.CLIENT_LANGUAGE_CODE);
				byte[] text = Arrays.copyOf(clanText, 478); // Arrays.copy(要被複製的陣列，新陣列的長度);
				this.writeByte(text);
			} catch (Exception e) {
				this.writeByte(new byte[478]);
			}
			/*this.writeS(clan.get_clan_info());// 血盟公告
			int upinfolen = 1;
			if (clan.get_clan_info() != null) {
				upinfolen += clan.get_clan_info().getBytes().length;
			}
			if (470 - upinfolen > 0) {
				final byte[]nullbytes = new byte[470 - upinfolen];
				this.writeByte(nullbytes);
			}*/			
			break;
			
		case ES_PLEDGE_MEMBER_INFO:// 所有成員資訊
			this.writeC(ES_PLEDGE_MEMBER_INFO);// type 0xaa
			this.writeC(0x01);// ??
			this.writeC(0x00);// ??
			this.writeC(clan.getAllMembers().length);// 輸出成員數量
			for (final L1PcInstance memberInfo : clan.getAllMembersRank()) {
				this.writeS(memberInfo.getName());// 成員名稱
				this.writeC(memberInfo.getClanRank());// 成員階級
				this.writeC(memberInfo.getLevel());// 成員等級
				this.writeS(memberInfo.getClanMemberNotes());
				int memberinfolen = 1;
				if (memberInfo.getClanMemberNotes() != null) {
					memberinfolen += memberInfo.getClanMemberNotes().getBytes().length;
				}
				if (62 - memberinfolen > 0) {
					final byte[]nullmemberbytes = new byte[62 - memberinfolen];
					this.writeByte(nullmemberbytes);
				}			
				this.writeD(memberInfo.getId());// 一個int的整數 類似角色的實體編號 這邊用objid來實作
				this.writeC(memberInfo.getType());// 成員職業type
				this.writeD((int) (System.currentTimeMillis() / 1000));// 血盟成員加入時間
			}
			break;
			
		//[Server] opcode:211 info:S_OPCODE_EVENT len:36 time:10:30:04.335 6.1tw 修正
		//0000: d3 ab 03 00 49 63 65 51 75 65 65 6e 00 01 a6 77    ....IceQueen...w
		//0010: b5 e1 a7 4a b4 b5 00 01 bb 42 a7 4a ad d7 b4 b5    ...J.....B.J....
		//0020: 00 01 50 02                                        ..P.	
		case ES_ONLINE_MEMBER_INFO:// ES_ONLINE_MEMBER_INFO
			this.writeC(ES_ONLINE_MEMBER_INFO);// type 0xab
			L1PcInstance[] online_list = clan.getOnlineClanMember();
			this.writeH(online_list.length);// 在線名稱數量
			// 在線名稱數量多少就writeS幾個名稱
			for (final Object name : online_list) {
				final L1PcInstance pc = (L1PcInstance) name;
				writeS(pc.getName());
				writeC(1);
			}
			break;
		}
	}
	
	public S_PledgeUI(final ArrayList<L1PcInstance> list, final int maxPagination, final int Pagination) {
		this.writeC(S_OPCODE_PACKETBOX);
		this.writeC(ES_PLEDGE_MEMBER_INFO);// type 0xaa
		this.writeC(maxPagination);
		this.writeC(Pagination);
		this.writeC(list.size());// 輸出成員數量
		for (final Iterator<L1PcInstance> iter = list.iterator(); iter.hasNext();) {
			final L1PcInstance memberInfo = iter.next();// 返回迭代的下一个元素。
			this.writeS(memberInfo.getName());// 成員名稱
			this.writeC(memberInfo.getClanRank());// 成員階級
			this.writeC(memberInfo.getLevel());// 成員等級
			this.writeS(memberInfo.getClanMemberNotes());
			int memberinfolen = 1;
			if (memberInfo.getClanMemberNotes() != null) {
				memberinfolen += memberInfo.getClanMemberNotes().getBytes().length;
			}
			if (62 - memberinfolen > 0) {
				final byte[]nullmemberbytes = new byte[62 - memberinfolen];
				this.writeByte(nullmemberbytes);
			}			
			this.writeD(memberInfo.getId());// 一個int的整數 類似角色的實體編號 這邊用objid來實作
			this.writeC(memberInfo.getType());// 成員職業type
			this.writeD((int) (System.currentTimeMillis() / 1000));// 血盟成員加入時間
		}
	}
	
	/**
	 * 更新個人血盟備註資訊
	 * @param pc
	 */
	public S_PledgeUI(final L1PcInstance pc) {
		this.writeC(S_OPCODE_PACKETBOX);
		this.writeC(ES_PLEDGE_MEMBER_MEMO);// type
		this.writeS(pc.getName());// 名稱
		this.writeS(pc.getClanMemberNotes());// 個人備註字串
		int upinfolen = 1;
		if (pc.getClanMemberNotes() != null) {
			upinfolen += pc.getClanMemberNotes().getBytes().length;
		}
		if (62 - upinfolen > 0) {
			final byte[]nullbytes = new byte[62 - upinfolen];
			this.writeByte(nullbytes);
		}
	}
	
	@Override
	public byte[] getContent() {
		return getBytes();
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
