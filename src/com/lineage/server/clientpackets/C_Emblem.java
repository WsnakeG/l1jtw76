package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1EmblemIcon;

/**
 * 要求上傳盟徽
 * 
 * @author daien
 */
public class C_Emblem extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Emblem.class);

	/*
	 * public C_Emblem() { } public C_Emblem(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

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

			final int clan_id = pc.getClanid();
			// 人物具有血盟
			if (clan_id != 0) {
				if (pc.getClan().getLeaderId() != pc.getId()) {
					// 219 \f1王子或公主才能上傳徽章。
					pc.sendPackets(new S_ServerMessage(219));
					return;
				}
				final L1Clan clan = pc.getClan();
				if (clan == null) {
					return;
				}

				final byte[] iconByte = readByte();
				L1EmblemIcon emblemIcon = ClanEmblemReading.get().get(clan_id);

				if (emblemIcon != null) {
					emblemIcon.set_clanIcon(iconByte);
					emblemIcon.set_update(emblemIcon.get_update() + 1);
					ClanEmblemReading.get().updateClanIcon(emblemIcon);
				} else {
					emblemIcon = ClanEmblemReading.get().storeClanIcon(clan_id, iconByte);
				}
				clan.setEmblemId(IdFactory.get().nextId());
				ClanReading.get().updateClan(pc.getClan());

				for (final L1PcInstance each : clan.getOnlineClanMember()) {
					L1Teleport.teleport(each, each.getX(), each.getY(), each.getMapId(), each.getHeading(),
							false);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
