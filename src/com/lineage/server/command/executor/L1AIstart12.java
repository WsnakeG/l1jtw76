package com.lineage.server.command.executor;

import java.util.Random;
//import java.util.StringTokenizer;

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
import com.lineage.server.serverpackets.S_SystemMessage;
//import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 執行驗證 懲罰 1傳送至指定座標 2封鎖IP剔除下線 3即刻執行驗證
 * 
 * @author dexc
 */
public class L1AIstart12 implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1AIstart12.class);

	private L1AIstart12() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AIstart12();
	}

	public void broadcastPacketWorld() {
		try {
			for (L1PcInstance pc : World.get().getAllPlayers()) {
				if (pc != null) {
					if (!pc.hasSkillEffect(7020) && !pc.hasSkillEffect(7021)) {
						if (!ServerAIMapIdTable.get()
								.checkCantAI(pc.getMapId())) {
							if ((!pc.isSafetyZone())
									||

									(ServerAIMapIdTable.get().checkCanAI(pc
											.getMapId()))) {
								if (!pc.isDead()) {
									if (!pc.isTeleport()) {
										
											if (pc.getHellTime() <= 0) {
												if (L1CastleLocation
														.getCastleIdByArea(pc) == 0) {
													pc.sendPackets(new S_BlueMessage(
															166,
															"\\f3A I 驗 證 5 秒 後 開 始 ！"));

													pc.setSkillEffect(7020,
															5000);
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
							}
						}
					}
				}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName,
			final String arg) {
		try {
			String gmName = "";
			if (pc == null) {
				_log.warn("系統命令執行: " + cmdName + " " + arg + " 執行驗證。");
				gmName = "系統命令";

			} else {
				gmName = pc.getName() + "命令";
			}

			// XXX 解除
			if (arg.indexOf("1") != -1) {
				EffectAISet.AI_ERROR_TYPE = 1;
				pc.sendPackets(new S_SystemMessage("\\aH變更驗證懲罰(1-傳送指定座標)"));
			} else if (arg.indexOf("2") != -1) {
				EffectAISet.AI_ERROR_TYPE = 2;
				pc.sendPackets(new S_SystemMessage("\\aH變更驗證懲罰(2-封鎖IP剔除下線)"));
			} else if (arg.indexOf("3") != -1) {
				EffectAISet.AI_ERROR_TYPE = 3;
				pc.sendPackets(new S_SystemMessage("\\aH變更驗證懲罰(3-封鎖帳號剔除下線)"));
			}else if (arg.indexOf("4") != -1) {
				broadcastPacketWorld();
				pc.sendPackets(new S_SystemMessage("\\aH立即啟動驗證！(4-所有玩家)"));
			}

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
