package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.FaceToFace;
import com.lineage.server.world.WorldClan;

/**
 * 要求加入血盟
 * 
 * @author daien
 */
public class C_JoinClan extends ClientBasePacket {

	/*
	 * public C_JoinClan() { } public C_JoinClan(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			if (pc.isGhost()) {
				return;
			}

			final L1PcInstance target = FaceToFace.faceToFace(pc);
			if (target != null) {
				joinClan(pc, target);
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	private void joinClan(final L1PcInstance pc, final L1PcInstance target) {
		/*
		 * if (!target.isCrown()) { // 92 \f1%0%d 不是王子或公主。 pc.sendPackets(new
		 * S_ServerMessage(92, target.getName())); return; }
		 */

		final int clan_id = target.getClanid();
		final String clan_name = target.getClanname();
		if (clan_id == 0) {
			// 90 \f1%0%d 尚未創立血盟。
			pc.sendPackets(new S_ServerMessage(90, target.getName()));
			return;
		}

		final L1Clan clan = WorldClan.get().getClan(clan_name);
		if (clan == null) {
			return;
		}

		if ((target.getClanRank() != 3) && (target.getClanRank() != 4) && (target.getClanRank() != 6)
				&& (target.getClanRank() != 9) && (target.getClanRank() != 10)) {
			pc.sendPackets(new S_ServerMessage(92, target.getName()));
			return;
		}

		if (pc.getClanid() != 0) { // 既にクランに加入済み
			if (pc.isCrown()) { // 自分が君主
				final String player_clan_name = pc.getClanname();
				final L1Clan player_clan = WorldClan.get().getClan(player_clan_name);
				if (player_clan == null) {
					return;
				}

				if (pc.getId() != player_clan.getLeaderId()) {
					// 89 \f1你已經有血盟了。
					pc.sendPackets(new S_ServerMessage(89));
					return;
				}

				if ((player_clan.getCastleId() != 0) || // 自分が城主・アジト保有
						(player_clan.getHouseId() != 0)) {
					// 665 \f1擁有城堡與血盟小屋的狀態下無法解散血盟。
					pc.sendPackets(new S_ServerMessage(665));
					return;
				}

			} else {
				// 89 \f1你已經有血盟了。
				pc.sendPackets(new S_ServerMessage(89));
				return;
			}
		}

		target.setTempID(pc.getId());
		// 97 \f3%0%s 想加入你的血盟。你接受嗎。(Y/N)
		target.sendPackets(new S_Message_YN(97, pc.getName()));
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
