package com.lineage.data.item_etcitem.card;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemPowerTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1ItemPower_name;

/**
 * 44143 伏曦易經[隨機] UPDATE `etcitem` SET `classname`=
 * 'card.C3_Card_00',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-隨機',`name_id`='伏曦 易 經 - 隨 機 ' WHERE `item_id`='44143';# UPDATE
 * `etcitem` SET `classname`=
 * 'card.C3_Card_01',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-陰',`name_id`='伏 曦 易 經 - 陰 ' WHERE `item_id`='44144';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_02',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-陽',`name_id`='伏 曦 易 經 - 陽 ' WHERE `item_id`='44145';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_03',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-金',`name_id`='伏 曦 易 經 - 金 ' WHERE `item_id`='44146';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_04',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-木',`name_id`='伏 曦 易 經 - 木 ' WHERE `item_id`='44147';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_05',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-水',`name_id`='伏 曦 易 經 - 水 ' WHERE `item_id`='44148';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_06',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-火',`name_id`='伏 曦 易 經 - 火 ' WHERE `item_id`='44149';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_07',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-土',`name_id`='伏 曦 易 經 - 土 ' WHERE `item_id`='44150';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_08',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-乾',`name_id`='伏 曦 易 經 - 乾 ' WHERE `item_id`='44151';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_09',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-坤',`name_id`='伏 曦 易 經 - 坤 ' WHERE `item_id`='44152';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_10',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-離',`name_id`='伏 曦 易 經 - 離 ' WHERE `item_id`='44153';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_11',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-震',`name_id`='伏 曦 易 經 - 震 ' WHERE `item_id`='44154';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_12',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-艮',`name_id`='伏 曦 易 經 - 艮 ' WHERE `item_id`='44155';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_13',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-兌',`name_id`='伏 曦 易 經 - 兌 ' WHERE `item_id`='44157';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_14',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-巽',`name_id`='伏 曦 易 經 - 巽 ' WHERE `item_id`='44158';# UPDATE `etcitem`
 * SET `classname`=
 * 'card.C3_Card_15',`use_type`='choice',`stackable`='1',`invgfx`='3907',`name`=
 * '伏曦易經-艮',`name_id`='伏 曦 易 經 - 艮 ' WHERE `item_id`='44159';#
 * 
 * @author dexc
 */
public class C3_Card_00 extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(C3_Card_00.class);

	private static final Random _random = new Random();

	/**
	 *
	 */
	private C3_Card_00() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new C3_Card_00();
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
			// 對象OBJID
			final int targObjId = data[0];

			// 目標物品
			final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

			if (tgItem == null) {
				return;
			}

			if (tgItem.isEquipped()) {
				pc.sendPackets(new S_ServerMessage("\\aE使用前必須先解除物品裝備..."));
				return;
			}

			// 古文字誕生
			switch (tgItem.getItem().getUseType()) {
			case 1:// 武器
			case 2:// 盔甲
			case 18:// T恤
			case 19:// 斗篷
			case 20:// 手套
			case 21:// 靴
			case 22:// 頭盔
			case 25:// 盾牌
				pc.getInventory().removeItem(item, 1);

				if (_random.nextInt(1000) <= 400) {
					tgItem.set_power_name(null);
					CharItemPowerReading.get().delItem(tgItem.getId());
					pc.sendPackets(new S_ItemStatus(tgItem));
					pc.sendPackets(new S_ServerMessage("發出強烈神秘的光芒...提煉失敗了"));
					pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_e)); // 失敗特效

				} else {
					int index = 0;
					L1ItemPower_name power = null;
					while (index <= 3) {
						final int key = _random.nextInt(ItemPowerTable.POWER_NAME.size()) + 1;
						final L1ItemPower_name v = ItemPowerTable.POWER_NAME.get(key);
						if (_random.nextInt(1000) <= v.get_dice()) {
							power = v;
						}
						index++;
					}
					if (power == null) {
						final int intx = ItemPowerTable.get().get_int();
						final ArrayList<L1ItemPower_name> list = new ArrayList<L1ItemPower_name>();
						for (final L1ItemPower_name v : ItemPowerTable.POWER_NAME.values()) {
							if (v.get_dice() >= intx) {
								list.add(v);
							}
						}
						final Object[] names = list.toArray();
						power = (L1ItemPower_name) names[_random.nextInt(names.length)];
					}

					tgItem.set_power_name(null);
					CharItemPowerReading.get().delItem(tgItem.getId());
					pc.sendPackets(new S_ServerMessage("成功灌注 [" + power.get_power_name() + "] 神秘的力量"));
					// 成功特效
					pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_s));
					tgItem.set_power_name(power);
					CharItemPowerReading.get().storeItem(tgItem.getId(), tgItem.get_power_name());
					pc.sendPackets(new S_ItemStatus(tgItem));
				}
				break;

			default:
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(79));
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _gfxid_e; // 失敗特效編號
	private int _gfxid_s; // 成功特效編號

	@Override
	public void set_set(String[] set) {
		try {
			_gfxid_e = Integer.parseInt(set[1]);
			_gfxid_s = Integer.parseInt(set[2]);

		} catch (Exception e) {
		}
	}
}