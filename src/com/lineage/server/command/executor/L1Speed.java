package com.lineage.server.command.executor;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_Liquor;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 賦予GM加速狀態
 * 
 * @author dexc
 */
public class L1Speed implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Speed.class);

	private L1Speed() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Speed();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			L1BuffUtil.haste(pc, 3600 * 1000);

			L1BuffUtil.brave(pc, 3600 * 1000);
			
			thirdSpeed(pc);

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
	
	private void thirdSpeed(L1PcInstance pc) {
		if (pc.hasSkillEffect(STATUS_BRAVE3)) {
			pc.killSkillEffectTimer(STATUS_BRAVE3);
		}
		// L1BuffUtil.cancelAbsoluteBarrier(pc);
//		pc.sendPacketsAll(new S_Liquor(pc.getId(), 8)); // 人物 * 1.15
		pc.sendPacketsAll(new S_Liquor(pc.getId(), 0x08));
		pc.sendPackets(new S_ServerMessage(1065)); // 將發生神秘的奇蹟力量。
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), 8031));// 7976=煙花
		pc.setSkillEffect(STATUS_BRAVE3, 3600 * 1000);
	}
}
