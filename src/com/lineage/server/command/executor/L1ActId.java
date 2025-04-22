package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;

/**
 * 測試Sprite Action
 * 
 * @author roy
 */
public class L1ActId implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Adena.class);

	private L1ActId() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ActId();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer stringtokenizer = new StringTokenizer(arg);
			final int actid = Integer.parseInt(stringtokenizer.nextToken());
			pc.sendPackets(new S_DoActionGFX(pc.getId(), actid));
			pc.broadcastPacketAll(new S_DoActionGFX(pc.getId(), actid));
		} catch (final Exception e) {
			_log.error("格式錯誤！");
		}

	}

}
