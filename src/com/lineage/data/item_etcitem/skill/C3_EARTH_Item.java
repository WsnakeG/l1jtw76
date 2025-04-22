package com.lineage.data.item_etcitem.skill;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Item;

/**
 * C3_EARTH [地氣術] 每次使用60秒 只對自身周圍玩家有效 使用地氣術之後，不會增加武器上的地系傷害， 但是他會對施法者周圍的玩家造成地系傷害，當
 * 角色被敵人包圍時正是使用地系術的大好機會 但敵方抗性越高受到的傷害越小 等級50-70 消耗三國貨幣X1 15-30傷害 等級70-90 消耗三國貨幣X1
 * 25-40傷害 等級90-110 消耗三國貨幣X1 35-50傷害 等級110-130 消耗三國貨幣X1 45-60傷害 等級130-150
 * 消耗三國貨幣X1 55-70傷害
 * 
 * @author dexc
 */
public class C3_EARTH_Item extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(C3_EARTH_Item.class);

	/**
	 *
	 */
	private C3_EARTH_Item() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new C3_EARTH_Item();
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
		try {
			// 例外狀況:物件為空
			if (item == null) {
				return;
			}
			// 例外狀況:人物為空
			if (pc == null) {
				return;
			}
			if (pc.getLevel() < 50) {
				pc.sendPackets(new S_ServerMessage("\\fV50級以下無法使用!"));
				return;
			}
			if (pc.hasSkillEffect(L1SkillId.C3_FIRE)) {// 火轉術
				pc.sendPackets(new S_ServerMessage("\\fV不能與[火轉術]共存!"));
				return;
			}
			if (pc.hasSkillEffect(L1SkillId.C3_WATER)) {// 水攻術
				pc.sendPackets(new S_ServerMessage("\\fV不能與[水攻術]共存!"));
				return;
			}
			if (pc.hasSkillEffect(L1SkillId.C3_WIND)) {// 風傷術
				pc.sendPackets(new S_ServerMessage("\\fV不能與[風傷術]共存!"));
				return;
			}
			if (pc.hasSkillEffect(L1SkillId.C3_EARTH)) {// 地氣術
				pc.sendPackets(
						new S_ServerMessage("\\fV[地氣術]效果尚存:" + pc.getSkillEffectTimeSec(L1SkillId.C3_EARTH)));
				return;
			}
			if (pc.hasSkillEffect(L1SkillId.C3_RESTART)) {// 尚未冷卻
				pc.sendPackets(new S_ServerMessage("\\fR屬性技能尚未冷卻"));
				return;
			}
			if (_itemid != 0) {
				final L1ItemInstance ned_item = pc.getInventory().checkItemX(_itemid, _count);// 需要啟動的物品
				if (ned_item != null) {
					pc.getInventory().removeItem(ned_item, _count);// 刪除道具

				} else {
					// 原始物件資料
					final L1Item tgItem = ItemTable.get().getTemplate(_itemid);
					// 337 \f1%0不足%s。
					pc.sendPackets(new S_ServerMessage(337, tgItem.getNameId()));
					return;
				}
			}
			// pc.getInventory().removeItem(item, 1);
			pc.setSkillEffect(L1SkillId.C3_RESTART, (_time + 30) * 1000);
			pc.setSkillEffect(L1SkillId.C3_EARTH, _time * 1000);
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 213));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _itemid = 44070;
	private int _count = 1;
	private int _time = 60;

	@Override
	public void set_set(final String[] set) {
		try {
			_itemid = Integer.parseInt(set[1]);

		} catch (final Exception e) {
		}
		try {
			_count = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
		try {
			_time = Integer.parseInt(set[3]);

		} catch (final Exception e) {
		}
	}
}
