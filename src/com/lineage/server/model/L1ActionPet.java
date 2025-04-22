package com.lineage.server.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_SelectTarget;

/**
 * 對話命令來自寵物的執行與判斷
 * 
 * @author daien
 */
public class L1ActionPet {

	private static final Log _log = LogFactory.getLog(L1ActionPet.class);

	private final L1PcInstance _pc;

	/**
	 * 對話命令來自PC的執行與判斷
	 * 
	 * @param pc 執行者
	 */
	public L1ActionPet(final L1PcInstance pc) {
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
	 * @param cmd
	 * @param amount
	 */
	public void action(final L1PetInstance npc, final String action) {
		try {
			// System.out.println("選單命令執行:"+action);
			String status = null;
			if (action.equalsIgnoreCase("attackchr")) {// 指定目標
				final int currentPetStatus = npc.getCurrentPetStatus();
				String type = "0";
				switch (currentPetStatus) {
				case 0:// 無
				case 4:// 配置
				case 7:// 召回
				case 6:// 解散
					type = "5";// 警戒
					break;

				case 1:// 攻擊
				case 2:// 防禦
				case 3:// 休息
				case 5:// 警戒
					type = String.valueOf(currentPetStatus);
					break;
				}
				npc.onFinalAction(_pc, type);
				_pc.sendPackets(new S_SelectTarget(npc.getId()));

			} else if (action.equalsIgnoreCase("aggressive")) { // 攻撃態勢
				status = "1";

			} else if (action.equalsIgnoreCase("defensive")) { // 防御態勢
				status = "2";

			} else if (action.equalsIgnoreCase("stay")) { // 休息
				status = "3";

			} else if (action.equalsIgnoreCase("extend")) { // 散開
				status = "4";

			} else if (action.equalsIgnoreCase("alert")) { // 警戒
				status = "5";

			} else if (action.equalsIgnoreCase("dismiss")) { // 解散
				status = "6";

			} else if (action.equalsIgnoreCase("getitem")) { // 収集
				npc.collect(false);

			} else if (action.equalsIgnoreCase("changename")) { // 命名
				_pc.rename(false);
				_pc.setTempID(npc.getId()); // ID暫存
				final S_Message_YN pack = new S_Message_YN(325);
				_pc.sendPackets(pack);
			}

			if (status != null) {
				npc.onFinalAction(_pc, status);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		}
	}
}
