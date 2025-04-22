package com.lineage.server.clientpackets;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_WhoCharinfo;
import com.lineage.server.timecontroller.server.ServerRestartTimer;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;

/**
 * 要求查詢玩家
 * 
 * @author daien
 */
public class C_Who extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Who.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final String s = readS();
			L1Character find = World.get().getPlayer(s);
			final L1PcInstance pc = client.getActiveChar();
			if (find == null) {
				find = getDe(s);
			}

			if (find != null) {
				final S_WhoCharinfo whoChar = new S_WhoCharinfo(find);
				pc.sendPackets(whoChar);

			} else {
				final int count = World.get().getAllPlayers().size();
				final int de = deCount();
				final String amount = String.valueOf((int) (count * ConfigAlt.ALT_WHO_COUNT) + de);

				// \f1【目前線上有: %0 人 】
				//pc.sendPackets(new S_ServerMessage("\\aD目前線上人數有: " + amount));

				if (ConfigAlt.ALT_WHO_COMMANDX) {
					final String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
					switch (ConfigAlt.ALT_WHO_TYPE) {
					case 0:// 對話視窗顯示
						pc.sendPackets(new S_ServerMessage(
								"\\fV啟動時間: " + String.valueOf(ServerRestartTimer.get_startTime())));
						pc.sendPackets(new S_ServerMessage(
								"\\fV經驗倍率: " + (ConfigRate.RATE_XP * ConfigOther.RATE_XP_WHO)));
						pc.sendPackets(new S_ServerMessage("\\fV金錢倍率: " + ConfigRate.RATE_DROP_ADENA));
						pc.sendPackets(new S_ServerMessage("\\fV衝武倍率: " + ConfigRate.ENCHANT_CHANCE_WEAPON));
						pc.sendPackets(new S_ServerMessage("\\fV衝防倍率: " + ConfigRate.ENCHANT_CHANCE_ARMOR));
						pc.sendPackets(new S_ServerMessage("\\fV現實時間: " + nowDate));
						pc.sendPackets(
								new S_ServerMessage("\\fV重啟時間: " + ServerRestartTimer.get_restartTime()));
						break;

					case 1:// 視窗顯示
						pc.sendPackets(
								new S_ServerMessage("\\aH您的陣營積分目前有:\\aL " + pc.get_other().get_score()));
						pc.sendPackets(
								new S_ServerMessage("\\aI使用永久HP藥水已提升上限:\\aL " + pc.get_other().get_addhp()));
						pc.sendPackets(
								new S_ServerMessage("\\aI使用永久MP藥水已提升上限:\\aL " + pc.get_other().get_addmp()));
						String type = "無";
						if ((pc.get_c_power() != null) && (pc.get_c_power().get_c1_type() != 0)) {
							type = C1_Name_Table.get().get(pc.get_c_power().get_c1_type());
						}

						final String[] info = new String[] { Config.SERVERNAME, // 伺服器資訊:
																				// 0
								String.valueOf(amount), // 人數 1
								String.valueOf((ConfigRate.RATE_XP * ConfigOther.RATE_XP_WHO)), // 經驗
																								// 2
								String.valueOf(ConfigRate.RATE_DROP_ITEMS), // 掉寶//
																			// 3
								String.valueOf(ConfigRate.RATE_DROP_ADENA), // 金幣4
								String.valueOf(ConfigRate.ENCHANT_CHANCE_WEAPON), // 武器5
								String.valueOf(ConfigRate.ENCHANT_CHANCE_ARMOR), // 防具6

								String.valueOf(ConfigAlt.POWER), // 手點上限 7
								String.valueOf(ConfigAlt.POWERMEDICINE), // 單項萬能藥上限
																			// 8
								String.valueOf(ConfigAlt.MEDICINE), // 總和萬能藥瓶數 9
								nowDate, // 目前時間 10
								ServerRestartTimer.get_restartTime(), // 重啟時間 11
								type, // 12
								pc.getMeteAbility() == null ? "尚未轉生" : pc.getMeteAbility().getTitle(), // 13

								String.valueOf(pc.getRegistBlind()), // 暗黑耐性14
								String.valueOf(pc.getRegistFreeze()), // 寒冰耐性15
								String.valueOf(pc.getRegistSleep()), // 睡眠耐性16
								String.valueOf(pc.getRegistStone()), // 石化耐性17
								String.valueOf(pc.getRegistStun()), // 暈眩耐性18
								String.valueOf(pc.getRegistSustain()), // 支撐耐性19

								String.valueOf(pc.getPhysicsDmgUp()), // 物理傷害增加+%
																		// 20
								String.valueOf(pc.getMagicDmgUp()), // 魔法傷害增加+%
																	// 21
								String.valueOf(pc.getPhysicsDmgDown()), // 物理傷害減免+%
																		// 22
								String.valueOf(pc.getMagicDmgDown()), // 魔法傷害減免+%
																		// 23
								String.valueOf(pc.getMagicHitUp()), // 有害魔法成功率+%
																	// 24
								String.valueOf(pc.getMagicHitDown()), // 抵抗有害魔法成功率+%
																		// 25
								String.valueOf(pc.getPhysicsDoubleHit()), // 物理暴擊發動機率+%
																			// (發動後普攻傷害*1.5倍)26
								String.valueOf(pc.getMagicDoubleHit()), }; // 魔法暴擊發動機率+%
																			// (發動後技能傷害*1.5倍)27

						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_who", info));
						break;
					}
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	/**
	 * 虛擬人物數量
	 * 
	 * @return
	 */
	public static int deCount() {
		int count = 0;
		final Collection<L1NpcInstance> allObj = WorldNpc.get().all();
		// 不包含元素
		if (allObj.isEmpty()) {
			return count;
		}

		for (final L1NpcInstance obj : allObj) {
			if (!(obj instanceof L1DeInstance)) {
				continue;
			}
			count++;
		}
		return count;
	}

	/**
	 * 虛擬人物資料
	 * 
	 * @return
	 */
	private L1DeInstance getDe(final String s) {
		final Collection<L1NpcInstance> allObj = WorldNpc.get().all();
		// 不包含元素
		if (allObj.isEmpty()) {
			return null;
		}

		for (final L1NpcInstance obj : allObj) {
			if (obj instanceof L1DeInstance) {
				final L1DeInstance de = (L1DeInstance) obj;
				// System.out.println("de:" + de.getNameId());
				if (de.getNameId().equalsIgnoreCase(s)) {
					return de;
				}
			}
		}
		return null;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
