package com.lineage.server.command.executor;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 顯示人物附加屬性(參數:人物名稱) XXX 待加入顯示背包
 * 
 * @author dexc
 */
public class L1Describe implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Describe.class);

	private L1Describe() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Describe();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			if (pc == null) {
				_log.warn("系統命令執行: " + cmdName + " " + arg + " 顯示人物附加屬性。");
			}

			final ArrayList<String> msg = new ArrayList<String>();

			L1PcInstance target = World.get().getPlayer(arg);

			if (pc == null) {
				if (target == null) {
					_log.error("指令異常: 指定人物不在線上，這個命令必須輸入正確人物名稱才能執行。");
					return;
				}

			} else {
				if (target == null) {
					target = pc;
				}
			}

			msg.add("-- 顯示資訊人物: " + target.getName() + " --");
			msg.add("近距傷害附加(防具): +" + target.getDmgModifierByArmor());
			msg.add("遠距傷害附加(防具): +" + target.getBowDmgModifierByArmor());
			msg.add("近距命中附加(防具): +" + target.getHitModifierByArmor());
			msg.add("遠距傷害附加(防具): +" + target.getBowHitModifierByArmor());
			msg.add("減傷(防具): +" + target.getDamageReductionByArmor());
			msg.add("抗魔: " + target.getMr() + "%");
			msg.add("陣營積分: " + target.get_other().get_score() + "分");

			final int hpr = target.getHpr() + target.getInventory().hpRegenPerTick();
			final int mpr = target.getMpr() + target.getInventory().mpRegenPerTick();
			msg.add("HP額外回復量: " + hpr);
			msg.add("MP額外回復量: " + mpr);
			msg.add("友好度: " + target.getKarma());
			msg.add("背包物品數量: " + target.getInventory().getSize());
			msg.add("地屬抗性: " + target.getEarth());
			msg.add("水屬抗性: " + target.getWater());
			msg.add("火屬抗性: " + target.getFire());
			msg.add("風屬抗性: " + target.getWind());
			msg.add("暈眩耐性: " + target.getRegistStun());
			msg.add("石化耐性: " + target.getRegistStone());
			msg.add("支撐耐性: " + target.getRegistSustain());
			msg.add("睡眠耐性: " + target.getRegistSleep());
			msg.add("寒冰抗性: " + target.getRegistFreeze());
			msg.add("暗黑抗性: " + target.getRegistBlind());

			if (pc == null) {
				String items = "";
				for (final L1ItemInstance item : target.getInventory().getItems()) {
					items += "[" + item.getNumberedName(item.getCount(), false) + "]";
				}
				msg.add(items);
			}

			for (final String info : msg) {
				if (pc == null) {
					_log.info(info);

				} else {
					pc.sendPackets(new S_ServerMessage(166, info));
				}
			}

		} catch (final Exception e) {
			if (pc == null) {
				_log.error("錯誤的命令格式: " + this.getClass().getSimpleName());

			} else {
				_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
				// 261 \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
			}
		}
	}
}
