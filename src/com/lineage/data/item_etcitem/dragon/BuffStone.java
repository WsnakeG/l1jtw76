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
 * 附魔石<BR>
 * classname: BuffStone<BR>
 * <BR>
 * 設置對象:道具(etcitem)<BR>
 * <BR>
 * 設置範例:dragon.BuffStone 0 600 8939 41246 360<BR>
 * 近戰0階段 時效600秒 動畫(動畫設置小於等於0 不顯示動畫) 需要物品 數量<BR>
 * <BR>
 * 參數設定<BR>
 * 1:近戰階段1<BR>
 * 2:近戰階段2<BR>
 * 3:近戰階段3<BR>
 * 4:近戰階段4<BR>
 * 5:近戰階段5<BR>
 * 6:近戰階段6<BR>
 * 7:近戰階段7<BR>
 * 8:近戰階段8<BR>
 * 9:近戰階段9<BR>
 * <BR>
 * 11:遠攻階段1<BR>
 * 12:遠攻階段2<BR>
 * 13:遠攻階段3<BR>
 * 14:遠攻階段4<BR>
 * 15:遠攻階段5<BR>
 * 16:遠攻階段6<BR>
 * 17:遠攻階段7<BR>
 * 18:遠攻階段8<BR>
 * 19:遠攻階段9<BR>
 * <BR>
 * 21:恢復階段1<BR>
 * 22:恢復階段2<BR>
 * 23:恢復階段3<BR>
 * 24:恢復階段4<BR>
 * 25:恢復階段5<BR>
 * 26:恢復階段6<BR>
 * 27:恢復階段7<BR>
 * 28:恢復階段8<BR>
 * 29:恢復階段9<BR>
 * <BR>
 * 31:防禦階段1<BR>
 * 32:防禦階段2<BR>
 * 33:防禦階段3<BR>
 * 34:防禦階段4<BR>
 * 35:防禦階段5<BR>
 * 36:防禦階段6<BR>
 * 37:防禦階段7<BR>
 * 38:防禦階段8<BR>
 * 39:防禦階段9<BR>
 * 
 * @author dexc
 */
public class BuffStone extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(BuffStone.class);

	/**
	 *
	 */
	private BuffStone() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new BuffStone();
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
			case 1:
				_skillid = L1SkillId.BS_GX01;
				break;
			case 2:
				_skillid = L1SkillId.BS_GX02;
				break;
			case 3:
				_skillid = L1SkillId.BS_GX03;
				break;
			case 4:
				_skillid = L1SkillId.BS_GX04;
				break;
			case 5:
				_skillid = L1SkillId.BS_GX05;
				break;
			case 6:
				_skillid = L1SkillId.BS_GX06;
				break;
			case 7:
				_skillid = L1SkillId.BS_GX07;
				break;
			case 8:
				_skillid = L1SkillId.BS_GX08;
				break;
			case 9:
				_skillid = L1SkillId.BS_GX09;
				break;

			case 11:
				_skillid = L1SkillId.BS_AX01;
				break;
			case 12:
				_skillid = L1SkillId.BS_AX02;
				break;
			case 13:
				_skillid = L1SkillId.BS_AX03;
				break;
			case 14:
				_skillid = L1SkillId.BS_AX04;
				break;
			case 15:
				_skillid = L1SkillId.BS_AX05;
				break;
			case 16:
				_skillid = L1SkillId.BS_AX06;
				break;
			case 17:
				_skillid = L1SkillId.BS_AX07;
				break;
			case 18:
				_skillid = L1SkillId.BS_AX08;
				break;
			case 19:
				_skillid = L1SkillId.BS_AX09;
				break;

			case 21:
				_skillid = L1SkillId.BS_WX01;
				break;
			case 22:
				_skillid = L1SkillId.BS_WX02;
				break;
			case 23:
				_skillid = L1SkillId.BS_WX03;
				break;
			case 24:
				_skillid = L1SkillId.BS_WX04;
				break;
			case 25:
				_skillid = L1SkillId.BS_WX05;
				break;
			case 26:
				_skillid = L1SkillId.BS_WX06;
				break;
			case 27:
				_skillid = L1SkillId.BS_WX07;
				break;
			case 28:
				_skillid = L1SkillId.BS_WX08;
				break;
			case 29:
				_skillid = L1SkillId.BS_WX09;
				break;

			case 31:
				_skillid = L1SkillId.BS_ASX01;
				break;
			case 32:
				_skillid = L1SkillId.BS_ASX02;
				break;
			case 33:
				_skillid = L1SkillId.BS_ASX03;
				break;
			case 34:
				_skillid = L1SkillId.BS_ASX04;
				break;
			case 35:
				_skillid = L1SkillId.BS_ASX05;
				break;
			case 36:
				_skillid = L1SkillId.BS_ASX06;
				break;
			case 37:
				_skillid = L1SkillId.BS_ASX07;
				break;
			case 38:
				_skillid = L1SkillId.BS_ASX08;
				break;
			case 39:
				_skillid = L1SkillId.BS_ASX09;
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
		case 1:// 近戰【+1】HP+10
			os.writeC(0x0e);
			os.writeH(10);// HP+
			break;
		case 2:// 近戰【+2】HP+20
			os.writeC(0x0e);
			os.writeH(20);// HP+
			break;
		case 3:// 近戰【+3】HP+30
			os.writeC(0x0e);
			os.writeH(30);// HP+
			break;
		case 4:// 近戰【+4】HP+40
			os.writeC(0x0e);
			os.writeH(40);// HP+
			break;
		case 5:// 近戰【+5】HP+50、體力恢復量+1
			os.writeC(0x0e);
			os.writeH(50);// HP+
			os.writeC(0x27);
			os.writeS("$5539 +1");// 體力回復量
			break;
		case 6:// 近戰【+6】HP+60、體力恢復量+2
			os.writeC(0x0e);
			os.writeH(50);// HP+
			os.writeC(0x27);
			os.writeS("$5539 +2");// 體力回復量
			break;
		case 7:// 近戰【+7】HP+70、體力恢復量+3
			os.writeC(0x0e);
			os.writeH(70);// HP+
			os.writeC(0x27);
			os.writeS("$5539 +3");// 體力回復量
			break;
		case 8:// 近戰【+8】HP+80、體力恢復量+4、近距離命中率+1
			os.writeC(0x0e);
			os.writeH(80);// HP+
			os.writeC(0x05);
			os.writeC(1);// 攻擊成功
			os.writeC(0x27);
			os.writeS("$5539 +4");// 體力回復量
			break;
		case 9:// 近戰【+9】HP+100、體力恢復量+5、近距離攻擊力+2、近距離命中率+2、力量+1
			os.writeC(0x0e);
			os.writeH(100);// HP+
			os.writeC(0x05);
			os.writeC(2);// 攻擊成功
			os.writeC(0x06);
			os.writeC(2);// 額外攻擊
			os.writeC(0x08);
			os.writeC(1);// 力量
			os.writeC(0x27);
			os.writeS("$5539 +5");// 體力回復量
			break;

		case 11:// 遠攻【+1】HP+5、MP+3
			os.writeC(0x0e);
			os.writeH(5);// HP+
			os.writeC(0x20);
			os.writeC(3);// MP+
			break;
		case 12:// 遠攻【+2】HP+10、MP+6
			os.writeC(0x0e);
			os.writeH(10);// HP+
			os.writeC(0x20);
			os.writeC(6);// MP+
			break;
		case 13:// 遠攻【+3】HP+15、MP+10
			os.writeC(0x0e);
			os.writeH(15);// HP+
			os.writeC(0x20);
			os.writeC(10);// MP+
			break;
		case 14:// 遠攻【+4】HP+20、MP+15
			os.writeC(0x0e);
			os.writeH(20);// HP+
			os.writeC(0x20);
			os.writeC(15);// MP+
			break;
		case 15:// 遠攻【+5】HP+25、MP+20
			os.writeC(0x0e);
			os.writeH(25);// HP+
			os.writeC(0x20);
			os.writeC(20);// MP+
			break;
		case 16:// 遠攻【+6】HP+30、MP+20、體力恢復量+1
			os.writeC(0x0e);
			os.writeH(30);// HP+
			os.writeC(0x20);
			os.writeC(20);// MP+
			os.writeC(0x27);
			os.writeS("$5539 +1");// 體力回復量
			break;
		case 17:// 遠攻【+7】HP+35、MP+20、體力恢復量+1、魔力恢復量+1
			os.writeC(0x0e);
			os.writeH(35);// HP+
			os.writeC(0x20);
			os.writeC(20);// MP+
			os.writeC(0x27);
			os.writeS("$5539 +1");// 體力回復量
			os.writeC(0x27);
			os.writeS("$5541 +1");// 魔力恢復量
			break;
		case 18:// 遠攻【+8】HP+40、MP+25、體力恢復量+2、魔力恢復量+1
			os.writeC(0x0e);
			os.writeH(40);// HP+
			os.writeC(0x20);
			os.writeC(25);// MP+
			os.writeC(0x27);
			os.writeS("$5539 +2");// 體力回復量
			os.writeC(0x27);
			os.writeS("$5541 +1");// 魔力恢復量
			break;
		case 19:// 遠攻【+9】HP+50、MP+30、體力恢復量+2、魔力恢復量+2、遠距離攻擊力+2、遠距離命中率+2、敏捷+1
			os.writeC(0x0e);
			os.writeH(50);// HP+
			os.writeC(0x20);
			os.writeC(30);// MP+
			os.writeC(0x18);
			os.writeC(2);// 遠距離命中率
			os.writeC(0x23);
			os.writeC(2);// 遠距離攻擊力
			os.writeC(0x09);
			os.writeC(1);// 敏捷
			os.writeC(0x27);
			os.writeS("$5539 +2");// 體力恢復量
			os.writeC(0x27);
			os.writeS("$5541 +2");// 魔力恢復量
			break;

		case 21:// 恢復【+1】MP+5
			os.writeC(0x20);
			os.writeC(5);// MP+
			break;
		case 22:// 恢復【+2】MP+10
			os.writeC(0x20);
			os.writeC(10);// MP+
			break;
		case 23:// 恢復【+3】MP+15
			os.writeC(0x20);
			os.writeC(15);// MP+
			break;
		case 24:// 恢復【+4】MP+20
			os.writeC(0x20);
			os.writeC(20);// MP+
			break;
		case 25:// 恢復【+5】MP+25、魔力恢復量+1
			os.writeC(0x20);
			os.writeC(25);// MP+
			os.writeC(0x27);
			os.writeS("$5541 +1");// 魔力恢復量
			break;
		case 26:// 恢復【+6】MP+30、魔力恢復量+2
			os.writeC(0x20);
			os.writeC(30);// MP+
			os.writeC(0x27);
			os.writeS("$5541 +2");// 魔力恢復量
			break;
		case 27:// 恢復【+7】MP+35、魔力恢復量+3
			os.writeC(0x20);
			os.writeC(35);// MP+
			os.writeC(0x27);
			os.writeS("$5541 +3");// 魔力恢復量
			break;
		case 28:// 恢復【+8】MP+40、魔力恢復量+4
			os.writeC(0x20);
			os.writeC(40);// MP+
			os.writeC(0x27);
			os.writeS("$5541 +4");// 魔力恢復量
			break;
		case 29:// 恢復【+9】MP+50、魔力恢復量+5、魔法攻擊力+1、智力+1
			os.writeC(0x20);
			os.writeC(50);// MP+
			os.writeC(0x11);
			os.writeC(1);// 魔法攻擊
			os.writeC(0x0c);
			os.writeC(1);// 智力
			os.writeC(0x27);
			os.writeS("$5541 +5");// 魔力恢復量
			break;

		case 31:// 防禦【+1】魔防+2
			os.writeC(0x27);
			os.writeS("$5538 +2");// 額外魔法防禦
			break;
		case 32:// 防禦【+2】魔防+4
			os.writeC(0x27);
			os.writeS("$5538 +4");// 額外魔法防禦
			break;
		case 33:// 防禦【+3】魔防+6
			os.writeC(0x27);
			os.writeS("$5538 +6");// 額外魔法防禦
			break;
		case 34:// 防禦【+4】魔防+8
			os.writeC(0x27);
			os.writeS("$5538 +8");// 額外魔法防禦
			break;
		case 35:// 防禦【+5】魔防+10、防禦力-1
			os.writeC(0x27);
			os.writeS("$5569 +1");// 額外防禦
			os.writeC(0x27);
			os.writeS("$5538 +10");// 額外魔法防禦
			break;
		case 36:// 防禦【+6】魔防+10、防禦力-2
			os.writeC(0x27);
			os.writeS("$5569 +2");// 額外防禦
			os.writeC(0x27);
			os.writeS("$5538 +10");// 額外魔法防禦
			break;
		case 37:// 防禦【+7】魔防+10、防禦力-3
			os.writeC(0x27);
			os.writeS("$5569 +3");// 額外防禦
			os.writeC(0x27);
			os.writeS("$5538 +10");// 額外魔法防禦
			break;
		case 38:// 防禦【+8】魔防+15、防禦力-4、額外傷害減免+1
			os.writeC(0x27);
			os.writeS("$5569 +4");// 額外防禦
			os.writeC(0x27);
			os.writeS("$5538 +15");// 額外魔法防禦
			os.writeC(0x27);
			os.writeS("傷害減免 +1");// 額外魔法防禦
			break;
		case 39:// 防禦【+9】魔防+20、防禦力-5、額外傷害減免+3、體質+1
			os.writeC(0x27);
			os.writeS("$5569 +5");// 額外防禦
			os.writeC(0x27);
			os.writeS("$5538 +20");// 額外魔法防禦
			os.writeC(0x27);
			os.writeS("傷害減免 +3");// 額外魔法防禦
			os.writeC(0x0a);
			os.writeC(1);// 體質
			break;
		}
		return os;
	}
}