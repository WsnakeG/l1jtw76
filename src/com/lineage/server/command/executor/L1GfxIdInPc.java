package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 產生動畫物件(參數:動畫編號)
 * 
 * @author dexc
 */
public class L1GfxIdInPc implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1GfxIdInPc.class);

	private L1GfxIdInPc() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GfxIdInPc();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			int gfxid = 0;// 十進制

			final String next = st.nextToken();
			try {
				gfxid = Integer.parseInt(next, 10);// 十進制

			} catch (final Exception e) {
				// 261 \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
				return;
			}

			pc.sendPacketsX8(new S_SkillSound(pc.getId(), gfxid));

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
