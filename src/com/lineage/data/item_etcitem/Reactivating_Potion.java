package com.lineage.data.item_etcitem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 
 * 返生藥水43000
 * 
 */

public class Reactivating_Potion extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Reactivating_Potion.class);

	/**
	 *
	 */
	private Reactivating_Potion() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Reactivating_Potion();
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
		if (pc.getMeteLevel() >= ConfigAlt.METE_MAX_COUNT) {
			pc.sendPackets(new S_ServerMessage("超過允許轉生上限: [" + ConfigAlt.METE_MAX_COUNT + "次]"));
			return;
		}
		if (pc.getLevel() < ConfigAlt.METE_LEVEL) {
			pc.sendPackets(new S_ServerMessage("等級太低以至於無法轉生。"));
			return;
		}
		pc.getInventory().removeItem(item, 1);

		final int pcObjid = pc.getId();// HP MP保留10%
        final int randomHp = (pc.getMaxHp() * ConfigAlt.METE_REMAIN_HP) / 100;
        final int randomMp = (pc.getMaxMp() * ConfigAlt.METE_REMAIN_MP) / 100;

		pc.setExp(810000);//30等
		pc.onChangeExp();
		pc.resetLevel();
		pc.setHighLevel(1); // 歷史最高等級 重置為30級
		pc.setBonusStats(0);
		pc.setMeteLevel(pc.getMeteLevel() + 1);

		pc.resetBaseAc();
        pc.resetBaseMr();
        pc.resetBaseHitup();
        pc.resetBaseDmgup();
        
        pc.addBaseMaxHp((short) randomHp);
        pc.addBaseMaxMp((short) randomMp);

        pc.setCurrentHp(pc.getMaxHp());
        pc.setCurrentMp(pc.getMaxMp());

		pc.sendPacketsX8(new S_SkillSound(pcObjid, 191));

		// 轉生附加能力系統 by terry0412
		pc.resetMeteAbility();

		if (ConfigAlt.ReincarnationBroad) {
			World.get().broadcastPacketToAll(new S_SystemMessage(
					"\\aE恭喜 ★" + pc.getName() + "★百般鍛煉後終於『 " + pc.getMeteLevel() + "轉了！』 "));
		}

		// 更新狀態封包
		pc.sendPackets(new S_OwnCharStatus(pc));
		// 更新魔攻與魔防
		pc.sendPackets(new S_SPMR(pc));

		pc.sendPackets(new S_ServerMessage(822)); // 你感受到体内深处产生一股不明力量。
		// 強制返回村莊
		
		L1Teleport.teleport(pc, 33441, 32809, (short) 4, pc.getHeading(), true);
		try {
			pc.save();
		    pc.getNetConnection().kick();//重生後將人物斷線
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
