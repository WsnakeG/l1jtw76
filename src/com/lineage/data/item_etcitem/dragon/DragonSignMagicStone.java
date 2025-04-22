package com.lineage.data.item_etcitem.dragon;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.BinaryOutputStream;

/**
 * 龍印魔石<BR>
 * classname: DragonSignMagicStone<BR>
 * <BR>
 * 設置對象:道具(etcitem)<BR>
 * <BR>
 * 設置範例:dragon.DragonSignMagicStone 0 600 8939<BR>
 * 鬥士0階段 時效600秒 動畫(動畫設置小於等於0 不顯示動畫)<BR>
 * <BR>
 * 參數設定<BR>
 * 0:鬥士階段0<BR>
 * 1:鬥士階段1<BR>
 * 2:鬥士階段2<BR>
 * 3:鬥士階段3<BR>
 * 4:鬥士階段4<BR>
 * 5:鬥士階段5<BR>
 * 6:鬥士階段6<BR>
 * 7:鬥士階段7<BR>
 * 8:鬥士階段8<BR>
 * 9:鬥士階段9<BR>
 * <BR>
 * 10:弓手階段0<BR>
 * 11:弓手階段1<BR>
 * 12:弓手階段2<BR>
 * 13:弓手階段3<BR>
 * 14:弓手階段4<BR>
 * 15:弓手階段5<BR>
 * 16:弓手階段6<BR>
 * 17:弓手階段7<BR>
 * 18:弓手階段8<BR>
 * 19:弓手階段9<BR>
 * <BR>
 * 20:賢者階段0<BR>
 * 21:賢者階段1<BR>
 * 22:賢者階段2<BR>
 * 23:賢者階段3<BR>
 * 24:賢者階段4<BR>
 * 25:賢者階段5<BR>
 * 26:賢者階段6<BR>
 * 27:賢者階段7<BR>
 * 28:賢者階段8<BR>
 * 29:賢者階段9<BR>
 * <BR>
 * 30:衝鋒階段0<BR>
 * 31:衝鋒階段1<BR>
 * 32:衝鋒階段2<BR>
 * 33:衝鋒階段3<BR>
 * 34:衝鋒階段4<BR>
 * 35:衝鋒階段5<BR>
 * 36:衝鋒階段6<BR>
 * 37:衝鋒階段7<BR>
 * 38:衝鋒階段8<BR>
 * 39:衝鋒階段9<BR>
 * 
 * @author dexc
 */
public class DragonSignMagicStone extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(DragonSignMagicStone.class);

	/**
	 *
	 */
	private DragonSignMagicStone() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new DragonSignMagicStone();
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

			L1BuffUtil.cancelBuffStone(pc);
			final int time = L1BuffUtil.cancelDragonSign(pc);
			if (time != -1) {
				// 1,139：%0 分鐘之內無法使用。
				pc.sendPackets(
						new S_ServerMessage(1139, item.getLogName() + " " + String.valueOf(time / 60)));
				return;
			}

			if ((_count > 0) && (_itemid > 0)) {
				if (!pc.getInventory().checkItem(_itemid, _count)) {// 魔法結晶體(耗用物品)
					final L1Item temp = ItemTable.get().getTemplate(_itemid);
					pc.sendPackets(new S_ServerMessage(337, temp.getNameId()));
					return;

				} else {
					pc.getInventory().consumeItem(_itemid, _count);
				}
			}

			// 設置延遲使用機制
			final Timestamp ts = new Timestamp(System.currentTimeMillis());
			item.setLastUsed(ts);
			pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
			pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);

			if (_gfxid > 0) {// 具備動畫
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid));
			}
			// SKILL移轉
			final SkillMode mode = L1SkillMode.get().getSkill(_skillid);
			if (mode != null) {
				mode.start(pc, null, null, _skill_time);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _mode = -1;
	private int _skillid = 0;
	private int _gfxid = 0;
	private int _skill_time = -1;
	private int _itemid = 41246;
	private int _count = 0;

	@Override
	public void set_set(final String[] set) {
		try {
			_mode = Integer.parseInt(set[1]);
			switch (_mode) {
			case 0:
				_skillid = L1SkillId.DS_GX00;
				break;
			case 1:
				_skillid = L1SkillId.DS_GX01;
				break;
			case 2:
				_skillid = L1SkillId.DS_GX02;
				break;
			case 3:
				_skillid = L1SkillId.DS_GX03;
				break;
			case 4:
				_skillid = L1SkillId.DS_GX04;
				break;
			case 5:
				_skillid = L1SkillId.DS_GX05;
				break;
			case 6:
				_skillid = L1SkillId.DS_GX06;
				break;
			case 7:
				_skillid = L1SkillId.DS_GX07;
				break;
			case 8:
				_skillid = L1SkillId.DS_GX08;
				break;
			case 9:
				_skillid = L1SkillId.DS_GX09;
				break;
			case 10:
				_skillid = L1SkillId.DS_AX00;
				break;
			case 11:
				_skillid = L1SkillId.DS_AX01;
				break;
			case 12:
				_skillid = L1SkillId.DS_AX02;
				break;
			case 13:
				_skillid = L1SkillId.DS_AX03;
				break;
			case 14:
				_skillid = L1SkillId.DS_AX04;
				break;
			case 15:
				_skillid = L1SkillId.DS_AX05;
				break;
			case 16:
				_skillid = L1SkillId.DS_AX06;
				break;
			case 17:
				_skillid = L1SkillId.DS_AX07;
				break;
			case 18:
				_skillid = L1SkillId.DS_AX08;
				break;
			case 19:
				_skillid = L1SkillId.DS_AX09;
				break;
			case 20:
				_skillid = L1SkillId.DS_WX00;
				break;
			case 21:
				_skillid = L1SkillId.DS_WX01;
				break;
			case 22:
				_skillid = L1SkillId.DS_WX02;
				break;
			case 23:
				_skillid = L1SkillId.DS_WX03;
				break;
			case 24:
				_skillid = L1SkillId.DS_WX04;
				break;
			case 25:
				_skillid = L1SkillId.DS_WX05;
				break;
			case 26:
				_skillid = L1SkillId.DS_WX06;
				break;
			case 27:
				_skillid = L1SkillId.DS_WX07;
				break;
			case 28:
				_skillid = L1SkillId.DS_WX08;
				break;
			case 29:
				_skillid = L1SkillId.DS_WX09;
				break;
			case 30:
				_skillid = L1SkillId.DS_ASX00;
				break;
			case 31:
				_skillid = L1SkillId.DS_ASX01;
				break;
			case 32:
				_skillid = L1SkillId.DS_ASX02;
				break;
			case 33:
				_skillid = L1SkillId.DS_ASX03;
				break;
			case 34:
				_skillid = L1SkillId.DS_ASX04;
				break;
			case 35:
				_skillid = L1SkillId.DS_ASX05;
				break;
			case 36:
				_skillid = L1SkillId.DS_ASX06;
				break;
			case 37:
				_skillid = L1SkillId.DS_ASX07;
				break;
			case 38:
				_skillid = L1SkillId.DS_ASX08;
				break;
			case 39:
				_skillid = L1SkillId.DS_ASX09;
				break;
			}

		} catch (final Exception e) {
		}
		try {
			_skill_time = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
		try {
			_gfxid = Integer.parseInt(set[3]);

		} catch (final Exception e) {
		}
		try {
			_itemid = Integer.parseInt(set[4]);

		} catch (final Exception e) {
		}
		try {
			_count = Integer.parseInt(set[5]);

		} catch (final Exception e) {
		}
	}

	@Override
	public BinaryOutputStream itemStatus(final L1ItemInstance item) {
		final BinaryOutputStream os = new BinaryOutputStream();
		os.writeC(0x17); // 材質
		os.writeC(item.getItem().getMaterial());// 材質內容
		os.writeD(item.getWeight());// 重量

		switch (_mode) {
		case 0:// 鬥士階段0 體力上限+10
			os.writeC(0x0e);
			os.writeH(10);// HP+
			break;
		case 1:// 鬥士階段1 體力上限+20
			os.writeC(0x0e);
			os.writeH(20);// HP+
			break;
		case 2:// 鬥士階段2 體力上限+30
			os.writeC(0x0e);
			os.writeH(30);// HP+
			break;
		case 3:// 鬥士階段3 體力上限+40
			os.writeC(0x0e);
			os.writeH(40);// HP+
			break;
		case 4:// 鬥士階段4 體力上限+50
			os.writeC(0x0e);
			os.writeH(50);// HP+
			break;
		case 5:// 鬥士階段5 體力上限+50、近距離命中率+1
			os.writeC(0x0e);
			os.writeH(50);// HP+
			os.writeC(0x05);
			os.writeC(1);// 攻擊成功
			break;
		case 6:// 鬥士階段6 體力上限+70、近距離命中率+1、體力恢復量+1
			os.writeC(0x0e);
			os.writeH(70);// HP+
			os.writeC(0x05);
			os.writeC(1);// 攻擊成功
			os.writeC(0x27);
			os.writeS("$5539 +1");// 體力回復量
			break;
		case 7:// 鬥士階段7 體力上限+70、近距離命中率+1、體力恢復量+2、近距離攻擊力+1
			os.writeC(0x0e);
			os.writeH(70);// HP+
			os.writeC(0x05);
			os.writeC(1);// 攻擊成功
			os.writeC(0x06);
			os.writeC(1);// 額外攻擊
			os.writeC(0x27);
			os.writeS("$5539 +2");// 體力回復量
			break;
		case 8:// 鬥士階段8 體力上限+90、近距離命中率+2、體力恢復量+3、近距離攻擊力+1
			os.writeC(0x0e);
			os.writeH(90);// HP+
			os.writeC(0x05);
			os.writeC(2);// 攻擊成功
			os.writeC(0x06);
			os.writeC(1);// 額外攻擊
			os.writeC(0x27);
			os.writeS("$5539 +3");// 體力回復量
			break;
		case 9:// 鬥士階段9 體力上限+120、近距離命中率+3、體力恢復量+6、近距離攻擊力+3、力量+1
			os.writeC(0x0e);
			os.writeH(120);// HP+
			os.writeC(0x05);
			os.writeC(3);// 攻擊成功
			os.writeC(0x06);
			os.writeC(3);// 額外攻擊
			os.writeC(0x08);
			os.writeC(1);// 力量
			os.writeC(0x27);
			os.writeS("$5539 +6");// 體力回復量
			break;
		case 10:// 弓手階段0 體力上限+5、魔力上限+3
			os.writeC(0x0e);
			os.writeH(5);// HP+
			os.writeC(0x20);
			os.writeC(3);// MP+
			break;
		case 11:// 弓手階段1 體力上限+10、魔力上限+6
			os.writeC(0x0e);
			os.writeH(10);// HP+
			os.writeC(0x20);
			os.writeC(6);// MP+
			break;
		case 12:// 弓手階段2 體力上限+15、魔力上限+9
			os.writeC(0x0e);
			os.writeH(15);// HP+
			os.writeC(0x20);
			os.writeC(9);// MP+
			break;
		case 13:// 弓手階段3 體力上限+20、魔力上限+12
			os.writeC(0x0e);
			os.writeH(20);// HP+
			os.writeC(0x20);
			os.writeC(12);// MP+
			break;
		case 14:// 弓手階段4 體力上限+25、魔力上限+15
			os.writeC(0x0e);
			os.writeH(25);// HP+
			os.writeC(0x20);
			os.writeC(15);// MP+
			break;
		case 15:// 弓手階段5 體力上限+25、魔力上限+15、遠距離命中率+1
			os.writeC(0x0e);
			os.writeH(25);// HP+
			os.writeC(0x20);
			os.writeC(15);// MP+
			os.writeC(0x18);
			os.writeC(1);// 遠距離命中率
			break;
		case 16:// 弓手階段6 體力上限+35、魔力上限+20、遠距離命中率+1、體力恢復量+1
			os.writeC(0x0e);
			os.writeH(35);// HP+
			os.writeC(0x20);
			os.writeC(20);// MP+
			os.writeC(0x18);
			os.writeC(1);// 遠距離命中率
			os.writeC(0x27);
			os.writeS("$5539 +1");// 體力回復量
			break;
		case 17:// 弓手階段7 體力上限+35、魔力上限+22、遠距離命中率+1、體力恢復量+1、遠距離攻擊力+1
			os.writeC(0x0e);
			os.writeH(35);// HP+
			os.writeC(0x20);
			os.writeC(22);// MP+
			os.writeC(0x18);
			os.writeC(1);// 遠距離命中率
			os.writeC(0x23);
			os.writeC(1);// 遠距離攻擊力
			os.writeC(0x27);
			os.writeS("$5539 +1");// 體力回復量
			break;
		case 18:// 弓手階段8 體力上限+40、魔力上限+30、遠距離命中率+2、體力恢復量+2、魔力恢復量+2、遠距離攻擊力+1
			os.writeC(0x0e);
			os.writeH(40);// HP+
			os.writeC(0x20);
			os.writeC(30);// MP+
			os.writeC(0x18);
			os.writeC(2);// 遠距離命中率
			os.writeC(0x23);
			os.writeC(1);// 遠距離攻擊力
			os.writeC(0x27);
			os.writeS("$5539 +2");// 體力恢復量
			os.writeC(0x27);
			os.writeS("$5541 +2");// 魔力恢復量
			break;
		case 19:// 弓手階段9 體力上限+60、魔力上限+43、遠距離命中率+3、體力恢復量+3、魔力恢復量+3、遠距離攻擊力+3、敏捷+1
			os.writeC(0x0e);
			os.writeH(60);// HP+
			os.writeC(0x20);
			os.writeC(43);// MP+
			os.writeC(0x18);
			os.writeC(3);// 遠距離命中率
			os.writeC(0x23);
			os.writeC(3);// 遠距離攻擊力
			os.writeC(0x09);
			os.writeC(1);// 敏捷
			os.writeC(0x27);
			os.writeS("$5539 +3");// 體力恢復量
			os.writeC(0x27);
			os.writeS("$5541 +3");// 魔力恢復量
			break;
		case 20:// 賢者階段0 魔力上限+10
			os.writeC(0x20);
			os.writeC(10);// MP+
			break;
		case 21:// 賢者階段1 魔力上限+20
			os.writeC(0x20);
			os.writeC(20);// MP+
			break;
		case 22:// 賢者階段2 魔力上限+30
			os.writeC(0x20);
			os.writeC(30);// MP+
			break;
		case 23:// 賢者階段3 魔力上限+40
			os.writeC(0x20);
			os.writeC(40);// MP+
			break;
		case 24:// 賢者階段4 魔力上限+50
			os.writeC(0x20);
			os.writeC(50);// MP+
			break;
		case 25:// 賢者階段5 魔力上限+50、魔力恢復量+1
			os.writeC(0x20);
			os.writeC(50);// MP+
			os.writeC(0x27);
			os.writeS("$5541 +1");// 魔力恢復量
			break;
		case 26:// 賢者階段6 魔力上限+55、魔力恢復量+1
			os.writeC(0x20);
			os.writeC(55);// MP+
			os.writeC(0x27);
			os.writeS("$5541 +1");// 魔力恢復量
			break;
		case 27:// 賢者階段7 魔力上限+55、魔力恢復量+2、魔法攻擊+1
			os.writeC(0x20);
			os.writeC(55);// MP+
			os.writeC(0x11);
			os.writeC(1);// 魔法攻擊
			os.writeC(0x27);
			os.writeS("$5541 +2");// 魔力恢復量
			break;
		case 28:// 賢者階段8 魔力上限+60、魔力恢復量+3、魔法攻擊+1
			os.writeC(0x20);
			os.writeC(60);// MP+
			os.writeC(0x11);
			os.writeC(1);// 魔法攻擊
			os.writeC(0x27);
			os.writeS("$5541 +3");// 魔力恢復量
			break;
		case 29:// 賢者階段9 魔力上限+70、魔力恢復量+5、魔法攻擊+3、智力+1
			os.writeC(0x20);
			os.writeC(70);// MP+
			os.writeC(0x11);
			os.writeC(3);// 魔法攻擊
			os.writeC(0x0c);
			os.writeC(1);// 智力
			os.writeC(0x27);
			os.writeS("$5541 +5");// 魔力恢復量
			break;
		case 30:// 衝鋒階段0 防禦-1
			os.writeC(0x27);
			os.writeS("$5569 +1");// 額外防禦
			break;
		case 31:// 衝鋒階段1 防禦-2
			os.writeC(0x27);
			os.writeS("$5569 +2");// 額外防禦
			break;
		case 32:// 衝鋒階段2 防禦-3
			os.writeC(0x27);
			os.writeS("$5569 +3");// 額外防禦
			break;
		case 33:// 衝鋒階段3 防禦-4
			os.writeC(0x27);
			os.writeS("$5569 +4");// 額外防禦
			break;
		case 34:// 衝鋒階段4 防禦-5
			os.writeC(0x27);
			os.writeS("$5569 +5");// 額外防禦
			break;
		case 35:// 衝鋒階段5 防禦-5、魔法防禦+1
			os.writeC(0x27);
			os.writeS("$5569 +5");// 額外防禦
			os.writeC(0x27);
			os.writeS("$5538 +1");// 額外魔法防禦
			break;
		case 36:// 衝鋒階段6 防禦-5、魔法防禦+6
			os.writeC(0x27);
			os.writeS("$5569 +5");// 額外防禦
			os.writeC(0x27);
			os.writeS("$5538 +6");// 額外魔法防禦
			break;
		case 37:// 衝鋒階段7 防禦-5、魔法防禦額外點數+9
			os.writeC(0x27);
			os.writeS("$5569 +5");// 額外防禦
			os.writeC(0x27);
			os.writeS("$5538 +9");// 額外魔法防禦
			break;
		case 38:// 衝鋒階段8 防禦-5、魔法防禦額外點數+15、額外傷害減免+2、昏迷耐性+2
			os.writeC(0x27);
			os.writeS("$5569 +5");// 額外防禦
			os.writeC(0x27);
			os.writeS("$5538 +15");// 額外魔法防禦
			os.writeC(0x27);
			os.writeS("傷害減免 +2");// 額外魔法防禦
			// 昏迷耐性
			os.writeC(0x21);
			os.writeC(0xd6);
			os.writeC(0x0f);
			os.writeH(2);
			os.writeC(0x21);
			os.writeC(0x05);
			break;
		case 39:// 衝鋒階段9 防禦-5、魔法防禦額外點數+21、額外傷害減免+4、昏迷耐性+5、體質+1
			os.writeC(0x27);
			os.writeS("$5569 +5");// 額外防禦
			os.writeC(0x27);
			os.writeS("$5538 +21");// 額外魔法防禦
			os.writeC(0x27);
			os.writeS("傷害減免 +4");// 額外魔法防禦
			os.writeC(0x0a);
			os.writeC(1);// 體質
			// 昏迷耐性
			os.writeC(0x21);
			os.writeC(0xd6);
			os.writeC(0x0f);
			os.writeH(5);
			os.writeC(0x21);
			os.writeC(0x05);
			break;
		}
		return os;
	}
}