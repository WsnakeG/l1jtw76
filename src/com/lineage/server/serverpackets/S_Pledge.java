package com.lineage.server.serverpackets;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 血盟成員清單
 * 
 * @author dexc
 */
public class S_Pledge extends ServerBasePacket {

	private byte[] _byte = null;

	private static final Log _log = LogFactory.getLog(S_Pledge.class);

	/**
	 * 盟友查詢 公告視窗
	 * 
	 * @param ClanId 血盟Id
	 */
	public S_Pledge(final L1Clan clan) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.HTML_PLEDGE_ANNOUNCE);
		writeS(clan.getClanName());
		writeS(clan.getLeaderName());
		writeD(clan.getClanId()); // 盟徽id
		writeD((int) (clan.getBirthDay().getTime() / 1000)); // 血盟創立日
		try {
			final byte[] text = new byte[478];
			Arrays.fill(text, (byte) 0);
			int i = 0;
			if (clan.getClanShowNote().length() > 0) {
				for (final byte b : clan.getClanShowNote().getBytes(CLIENT_LANGUAGE_CODE)) {
					text[i++] = b;
				}
			}
			writeByte(text);
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 盟友查詢 盟友清單
	 * 
	 * @param clanName
	 * @throws Exception
	 */
	public S_Pledge(final L1PcInstance pc) throws Exception {
		final L1Clan clan = pc.getClan();
		writeC(S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.HTML_PLEDGE_MEMBERS);
		writeH(1);
		writeC(clan.getAllMembers().length); // 血盟總人數

		// 血盟成員資料
		/** Name/Rank/Level/Notes/MemberId/ClassType */
		for (final L1PcInstance member : clan.getAllMembersRank()) {
			writeS(member.getName());
			writeC(member.getClanRank());
			writeC(member.getLevel());
			try {
				/** 產生全由0填充的byte陣列 */
				final byte[] text = new byte[62];
				Arrays.fill(text, (byte) 0);

				/** 將備註字串填入byte陣列 */
				if (member.getClanMemberNotes().length() != 0) {
					int i = 0;
					for (final byte b : member.getClanMemberNotes().getBytes(CLIENT_LANGUAGE_CODE)) {
						text[i++] = b;
					}
				}
				writeByte(text);
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
			writeD(member.getId());
			writeC(member.getType());
			writeD((int) (System.currentTimeMillis() / 1000));
		}
	}

	/**
	 * 盟友查詢 寫入備註
	 * 
	 * @param name 玩家名稱
	 * @param notes 備註文字
	 */
	public S_Pledge(final String name, final String notes) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.HTML_PLEDGE_WRITE_NOTES);
		writeS(name);

		/** 產生全由0填充的byte陣列 */
		try {
			final byte[] text = new byte[62];
			Arrays.fill(text, (byte) 0);

			/** 將備註字串填入byte陣列 */
			if (notes.length() != 0) {
				int i = 0;
				for (final byte b : notes.getBytes(CLIENT_LANGUAGE_CODE)) {
					text[i++] = b;
				}

			}
			writeByte(text);
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
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