package com.lineage.server.clientpackets;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.list.PcLvSkillList;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_AddSkill;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBuyCN;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Skills;

/**
 * 要求完成學習魔法(金幣)
 * 
 * @author daien
 */
public class C_SkillBuyOK extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_SkillBuyOK.class);

	// 初始化價格計算表(技能等級區分 0~27)
	private static final int[] PRICE = new int[] { 1, // 0
			4, // 1
			9, // 2
			16, // 3
			25, // 4
			36, // 5
			49, // 6
			64, // 7
			81, // 8
			100, // 9
			121, // 10
			144, // 11
			169, // 12
			196, // 13
			225, // 14
			0, // 15
			289, // 16
			324, // 17
			361, // 18
			400, // 19
			441, // 20
			484, // 21
			529, // 22
			576, // 23
			625, // 24
			676, // 25
			729, // 26
			784, // 27
	};

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}

			if (pc.isDead()) { // 死亡
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			if (pc.isPrivateShop()) { // 商店村模式
				return;
			}

			final int count = readH();

			ArrayList<Integer> skillList = null;

			// 各職業等級可學技能清單
			if (pc.isCrown()) {// 王族
				skillList = PcLvSkillList.isCrown(pc);

			} else if (pc.isKnight()) {// 騎士
				skillList = PcLvSkillList.isKnight(pc);

			} else if (pc.isElf()) {// 精靈
				skillList = PcLvSkillList.isElf(pc);

			} else if (pc.isWizard()) {// 法師
				skillList = PcLvSkillList.isWizard(pc);

			} else if (pc.isDarkelf()) {// 黑妖
				skillList = PcLvSkillList.isDarkelf(pc);

			} else if (pc.isDragonKnight()) {// 龍騎
				skillList = PcLvSkillList.isDragonKnight(pc);

			} else if (pc.isIllusionist()) {// 幻術
				skillList = PcLvSkillList.isIllusionist(pc);

			} else if (pc.isWarrior()) {// 戰士
				skillList = PcLvSkillList.isWarrior(pc);
			}

			if (skillList == null) {
				return;
			}

			// 產生動畫
			boolean isGfx = false;

			boolean shopSkill = false;
			if (pc.get_other().get_shopSkill()) {
				shopSkill = true;
			}

			for (int i = 0; i < count; i++) {
				final int skillId = readD() + 1;

				// 檢查是否已學習該法術
				if (!CharSkillReading.get().spellCheck(pc.getId(), (skillId))) {
					if (skillList.contains(new Integer(skillId - 1))) {
						// 取回技能資料
						final L1Skills l1skills = SkillsTable.get().getTemplate(skillId);

						// 技能等級價格計算表
						final int skillLvPrice = PRICE[l1skills.getSkillLevel() - 1];

						// 耗用金幣(shopSkill=true以價格表分類 : shopSkill=false以6000計價)
						final int price = (shopSkill ? S_SkillBuyCN.PCTYPE[pc.getType()] : 6000)
								* skillLvPrice;

						if (pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
							pc.getInventory().consumeItem(L1ItemId.ADENA, price);
							// 加入資料庫
							CharSkillReading.get().spellMastery(pc.getId(), l1skills.getSkillId(),
									l1skills.getName(), 0, 0);
							pc.sendPackets(new S_AddSkill(pc, skillId));
							isGfx = true;

						} else {
							// 金幣不足
							pc.sendPackets(new S_ServerMessage(189));
						}
					}
				}
			}

			if (isGfx) {
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), 224));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
