package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * GM指定移動座標(參數:座標編號)
 * 
 * @author dexc
 */
public class L1GMRoom implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1GMRoom.class);

	private L1GMRoom() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GMRoom();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final int roomNo = Integer.parseInt(arg);

			switch (roomNo) {
			case 1:// GM房間
				L1Teleport.teleport(pc, 32737, 32796, (short) 99, 5, false);
				break;

			case 2:// 說話島海洋
				L1Teleport.teleport(pc, 32734, 32799, (short) 17100, 5, false); // 17100!?
				break;

			case 3:// 潘朵拉
				L1Teleport.teleport(pc, 32644, 32955, (short) 0, 5, false);
				break;

			case 4:// 奇岩村莊
				L1Teleport.teleport(pc, 33429, 32814, (short) 4, 5, false);
				break;

			case 5:// 亞丁宮殿
				L1Teleport.teleport(pc, 32894, 32535, (short) 300, 5, false);
				break;

			case 6:// 沙漠(測試圖形)
				L1Teleport.teleport(pc, 32679, 33169, (short) 4, 5, false);
				break;

			case 7:// 英雄領地
				L1Teleport.teleport(pc, 32863, 32936, (short) 630, 5, false);
				break;

			case 8:// 歐瑞商店村
				L1Teleport.teleport(pc, 32734, 32802, (short) 360, 5, false);
				break;

			case 9:// 空白地圖
				L1Teleport.teleport(pc, 32737, 32789, (short) 997, 5, false);
				break;

			case 10:// 空白地圖
				L1Teleport.teleport(pc, 32959, 32874, (short) 68, 5, false);
				break;

			case 11:// 空白地圖
				L1Teleport.teleport(pc, 32707, 32846, (short) 9000, 5, false);
				break;

			default:
				L1Teleport.teleport(pc, 32863, 32936, (short) 630, 5, false);
				break;
			}
		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
