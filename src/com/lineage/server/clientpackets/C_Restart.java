package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.GetbackTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_MapID;
import com.lineage.server.serverpackets.S_OtherCharPacks;
import com.lineage.server.serverpackets.S_OwnCharPack;
import com.lineage.server.serverpackets.S_PacketBoxIcon1;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_Weather;
import com.lineage.server.world.World;

/**
 * 要求死亡後重新開始
 * 
 * @author daien
 */
public class C_Restart extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Restart.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc != null) {

				// 改變顯示(復原正常) ???????????????????????? added by terry0412
				pc.sendPackets(new S_ChangeName(pc.getId(), pc.getName()));

				int[] loc = new int[3];

				if (pc.getHellTime() > 0) {
					// 地獄坐標
					loc = new int[3];
					loc[0] = 32701;
					loc[1] = 32777;
					loc[2] = 666;

				} else if (pc.get_redbluejoin() > 0) {
					if (pc.get_redbluejoin() == 11) {
						loc = new int[3];
						loc[0] = ConfigOther.RedBlueRed_map1[0];
						loc[1] = ConfigOther.RedBlueRed_map1[1];
						loc[2] = ConfigOther.RedBlueRed_map1[2];
					} else if (pc.get_redbluejoin() == 12) {
						loc = new int[3];
						loc[0] = ConfigOther.RedBlueBlue_map1[0];
						loc[1] = ConfigOther.RedBlueBlue_map1[1];
						loc[2] = ConfigOther.RedBlueBlue_map1[2];
					} else if (pc.get_redbluejoin() == 21) {
						loc = new int[3];
						loc[0] = ConfigOther.RedBlueRed_map2[0];
						loc[1] = ConfigOther.RedBlueRed_map2[1];
						loc[2] = ConfigOther.RedBlueRed_map2[2];
					} else if (pc.get_redbluejoin() == 22) {
						loc = new int[3];
						loc[0] = ConfigOther.RedBlueBlue_map2[0];
						loc[1] = ConfigOther.RedBlueBlue_map2[1];
						loc[2] = ConfigOther.RedBlueBlue_map2[2];
					}

				} else {
					// 返回村莊
					loc = GetbackTable.GetBack_Location(pc, true);
				}

				pc.stopPcDeleteTimer();

				pc.removeAllKnownObjects();
				pc.broadcastPacketAll(new S_RemoveObject(pc));

				if (pc.get_redbluejoin() > 0) {
					pc.setCurrentHp(pc.getMaxHp());
				} else

					pc.setCurrentHp(pc.getLevel());
				pc.set_food(40);
				pc.setStatus(0);
				World.get().moveVisibleObject(pc, loc[2]);

				pc.setX(loc[0]);
				pc.setY(loc[1]);
				pc.setMap((short) loc[2]);

				// 設置副本編號
				pc.set_showId(-1);
				pc.sendPackets(new S_MapID(pc, pc.getMapId(), pc.getMap()
						.isUnderwater()));

				pc.broadcastPacketAll(new S_OtherCharPacks(pc));

				pc.sendPackets(new S_OwnCharPack(pc));
				pc.sendPackets(new S_CharVisualUpdate(pc));

				pc.startHpRegeneration();
				pc.startMpRegeneration();

				pc.sendPackets(new S_Weather(World.get().getWeather()));

				// 閃避率更新 修正 thatmystyle (UID: 3602)
				pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));

				if (pc.getHellTime() > 0) {
					pc.beginHell(false);
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