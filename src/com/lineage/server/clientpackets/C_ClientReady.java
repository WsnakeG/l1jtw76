package com.lineage.server.clientpackets;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lineage.echo.ClientExecutor;
import com.lineage.echo.encryptions.PacketPrint;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.WorldClan;

/**
 * 血盟通知(3.63增加 )<BR>
 * 此操作是由客戶端發送了遊戲設置記錄的封包後才會產生的<BR>
 * 
 * @author user
 */
public class C_ClientReady extends ClientBasePacket {

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {

			final L1PcInstance pc = client.getActiveChar();

			if (pc == null) {
				client.kick();
				return;
			}

			final int type = readD();

			switch (type) {
			case 0:// 血盟通知:開(登入遊戲不通知其他血盟成員)

				break;

			case 1:// 血盟通知:關(登入遊戲通知其他血盟成員)
				if (pc.getClanid() == 0) {
					return;
				}

				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());

				if (clan == null) {
					return;
				}

				final L1PcInstance[] clanMembers = clan.getOnlineClanMember();
				for (final L1PcInstance clanMember : clanMembers) {
					if (clanMember.getId() != pc.getId()) {
						// 843 血盟成員%0%s剛進入遊戲。
						clanMember.sendPackets(new S_ServerMessage(843, pc.getName()));
					}
				}
				break;

			default:
				System.out.println("\n未處理 " + getType() + " Type: " + type + "\n"
						+ PacketPrint.get().printData(decrypt, decrypt.length) + getNow_YMDHMS());
				break;
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	/**
	 * <font color=#00800>取得系統時間</font>
	 * 
	 * @return 傳出標準時間格式 yyyy/MM/dd HH:mm:ss
	 */
	private final String getNow_YMDHMS() {
		final String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		return nowDate;
	}
}
