package com.lineage.server.command.executor;

import java.util.Random;
//import java.util.StringTokenizer;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.lineage.commons.system.LanSecurityManager;
import com.lineage.data.event.EffectAISet;
import com.lineage.server.datatables.ServerAIMapIdTable;
import com.lineage.server.model.L1CastleLocation;
//import com.lineage.echo.ClientExecutor;
//import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BlueMessage;
//import com.lineage.server.serverpackets.S_PacketBoxGm;
import com.lineage.server.serverpackets.S_ServerMessage;
//import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 執行驗證 懲罰 1傳送至指定座標 2封鎖IP剔除下線 3即刻執行驗證
 * 
 * @author dexc
 */
public class L1AIstartGo implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1AIstartGo.class);

	private L1AIstartGo() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AIstartGo();
	}

	public void broadcastPacketWorld(L1PcInstance pc) {
		try {
			if (pc != null) {
				if (!pc.hasSkillEffect(7020) && !pc.hasSkillEffect(7021)) {
					if (!pc.isSafetyZone()
							|| !ServerAIMapIdTable.get().checkCantAI(
									pc.getMapId()) || !pc.isSafetyZone()
							|| !pc.isDead() || !pc.isTeleport()) {
						if (pc.getHellTime() <= 0) {
							if (L1CastleLocation.getCastleIdByArea(pc) == 0) {
								pc.sendPackets(new S_BlueMessage(166,
										"\\f3請 注 意 A I 驗 證 準 備 開 始 ！"));
								pc.setSkillEffect(7020, 1000);
								if (EffectAISet.AI_TIME_RANDOM != 0) {
									Random _random = new Random();
									pc.setAITimer(_random
											.nextInt(EffectAISet.AI_TIME_RANDOM)
											+ EffectAISet.AI_TIME);
								} else {
									pc.setAITimer(EffectAISet.AI_TIME);
								}
							}
						}
					}
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			// 玩家名字
			String char_name = tok.nextToken();
			L1PcInstance tg;
			tg = World.get().getPlayer(char_name);
			broadcastPacketWorld(tg);
		} catch (final Exception e) {
			if (pc == null) {
				_log.error("錯誤的命令格式: " + this.getClass().getSimpleName());
			} else {
				_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName()
						+ " 執行的GM:" + pc.getName());
				// 261 \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
			}
		}
	}
}
