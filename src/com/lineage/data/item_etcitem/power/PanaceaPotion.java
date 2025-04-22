package com.lineage.data.item_etcitem.power;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 極限藥 (STR/CON/DEX/CHA/INT/WIS) (直接調整數值)
 * 
 * @author terry0412
 */
public class PanaceaPotion extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(PanaceaPotion.class);

	/**
	 *
	 */
	private PanaceaPotion() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new PanaceaPotion();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		int checkPoint = 0;
		// 迴圈搜尋 (檢查是否能使用)
		for (final String value : _panaceaList) {
			// 個別檢查
			if (value.equalsIgnoreCase("STR")) {
				if (pc.getBaseStr() >= _value) {
					checkPoint++;
				}

			} else if (value.equalsIgnoreCase("CON")) {
				if (pc.getBaseCon() >= _value) {
					checkPoint++;
				}

			} else if (value.equalsIgnoreCase("DEX")) {
				if (pc.getBaseDex() >= _value) {
					checkPoint++;
				}

			} else if (value.equalsIgnoreCase("CHA")) {
				if (pc.getBaseCha() >= _value) {
					checkPoint++;
				}

			} else if (value.equalsIgnoreCase("INT")) {
				if (pc.getBaseInt() >= _value) {
					checkPoint++;
				}

			} else if (value.equalsIgnoreCase("WIS")) {
				if (pc.getBaseWis() >= _value) {
					checkPoint++;
				}
			}
		}

		if (checkPoint >= _panaceaList.length) {
			pc.sendPackets(new S_SystemMessage("你的能力值已經達到要求了。"));
			return;
		}
		pc.getInventory().removeItem(item, 1);

		final StringBuilder sbr = new StringBuilder();
		sbr.append("你的");

		// 迴圈搜尋 (開始附加能力值)
		for (final String value : _panaceaList) {
			// 個別附加
			if (value.equalsIgnoreCase("STR")) {
				pc.addBaseStr(_value - pc.getBaseStr());
				sbr.append("[力量]");

			} else if (value.equalsIgnoreCase("CON")) {
				pc.addBaseCon(_value - pc.getBaseCon());
				sbr.append("[體質]");

			} else if (value.equalsIgnoreCase("DEX")) {
				pc.addBaseDex(_value - pc.getBaseDex());
				sbr.append("[敏捷]");

			} else if (value.equalsIgnoreCase("CHA")) {
				pc.addBaseCha(_value - pc.getBaseCha());
				sbr.append("[魅力]");

			} else if (value.equalsIgnoreCase("INT")) {
				pc.addBaseInt(_value - pc.getBaseInt());
				sbr.append("[智力]");

			} else if (value.equalsIgnoreCase("WIS")) {
				pc.addBaseWis(_value - pc.getBaseWis());
				sbr.append("[精神]");
			}
		}

		// 更新封包
		pc.sendPackets(new S_OwnCharStatus2(pc));
		pc.sendDetails();
		sbr.append("已成功提昇至[").append(_value).append("]點。");
		// 發送訊息
		pc.sendPackets(new S_SystemMessage(sbr.toString()));

		try {
			pc.save();
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 增加能力 (STR/CON/DEX/CHA/INT/WIS)
	private String[] _panaceaList;

	// 直接調整數值
	private int _value;

	@Override
	public void set_set(final String[] set) {
		try {
			_panaceaList = set[1].split("/");

			_value = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
	}
}
