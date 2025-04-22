package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ClanUpdate;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 要求驅逐人物離開血盟
 * 
 * @author dexc
 */
public class C_BanClan extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_BanClan.class);

	/*
	 * public C_BanClan() { } public C_BanClan(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final String s = readS();

			final L1PcInstance pc = client.getActiveChar();
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());

			if (clan != null) {
				final String clanMemberName[] = clan.getAllMembers();
				int i;
				if (pc.isCrown() && (pc.getId() == clan.getLeaderId())) { // 君主、かつ、血盟主
					for (i = 0; i < clanMemberName.length; i++) {
						if (pc.getName().equalsIgnoreCase(s)) { // 君主自身
							return;
						}
					}

					final L1PcInstance tempPc = World.get().getPlayer(s);
					if (tempPc != null) { // オンライン中
						try {
							if (tempPc.getClanid() == pc.getClanid()) { // 同じクラン
								tempPc.setClanid(0);
								tempPc.setClanname("");
								tempPc.setClanRank(0);
								tempPc.save(); // 資料存檔
								clan.delMemberName(tempPc.getName());
								// 238 你被 %0 血盟驅逐了。
								tempPc.sendPackets(new S_ServerMessage(238, pc.getClanname()));
								// 被驅逐的血盟成員發送血盟數據更新包
								tempPc.sendPackets(new S_ClanUpdate(tempPc.getId()));
								// 取得在線的血盟成員 發送血盟數據更新包
								for (final L1PcInstance clanMembers : clan.getOnlineClanMember()) {
									clanMembers.sendPackets(new S_ClanUpdate(tempPc.getId()));// 在線上的血盟成員發送遭驅逐的血盟成員血盟數據更新
								}
								// 240 %0%o 被你從你的血盟驅逐了。
								pc.sendPackets(new S_ServerMessage(240, tempPc.getName()));

							} else {
								// 109 沒有叫%0的人。
								pc.sendPackets(new S_ServerMessage(109, s));
							}

						} catch (final Exception e) {
							_log.error(e.getLocalizedMessage(), e);
						}

					} else { // オフライン中
						try {
							final L1PcInstance restorePc = CharacterTable.get().restoreCharacter(s);
							if ((restorePc != null) && (restorePc.getClanid() == pc.getClanid())) { // 同じクラン
								restorePc.setClanid(0);
								restorePc.setClanname("");
								restorePc.setClanRank(0);
								restorePc.save(); // 資料存檔
								clan.delMemberName(restorePc.getName());
								// 240 %0%o 被你從你的血盟驅逐了。
								pc.sendPackets(new S_ServerMessage(240, restorePc.getName()));

							} else {
								// 109 沒有叫%0的人。
								pc.sendPackets(new S_ServerMessage(109, s));
							}

						} catch (final Exception e) {
							_log.error(e.getLocalizedMessage(), e);
						}
					}

				} else {
					// 518 血盟君主才可使用此命令。
					pc.sendPackets(new S_ServerMessage(518));
				}
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
