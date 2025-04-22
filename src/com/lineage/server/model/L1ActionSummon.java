package com.lineage.server.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;

/**
 * 對話命令來自召喚獸的執行與判斷
 * 
 * @author daien
 */
public class L1ActionSummon {

	private static final Log _log = LogFactory.getLog(L1ActionSummon.class);

	private final L1PcInstance _pc;

	/**
	 * 對話命令來自PC的執行與判斷
	 * 
	 * @param pc 執行者
	 */
	public L1ActionSummon(final L1PcInstance pc) {
		_pc = pc;
	}

	/**
	 * 傳回執行命令者
	 * 
	 * @return
	 */
	public L1PcInstance get_pc() {
		return _pc;
	}

	/**
	 * 選單命令執行
	 * 
	 * @param pet
	 * @param action
	 */
	public void action(final L1SummonInstance npc, final String action) {
		try {
			String status = null;
			if (action.equalsIgnoreCase("aggressive")) { // 攻撃態勢
				status = "1";

			} else if (action.equalsIgnoreCase("defensive")) { // 防御態勢
				status = "2";

			} else if (action.equalsIgnoreCase("stay")) { // 休憩
				status = "3";

			} else if (action.equalsIgnoreCase("extend")) { // 散開
				status = "4";

			} else if (action.equalsIgnoreCase("alert")) { // 警戒
				status = "5";

			} else if (action.equalsIgnoreCase("dismiss")) { // 解散
				status = "6";
			}

			if (status != null) {
				npc.onFinalAction(_pc, status);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		}
	}
}
