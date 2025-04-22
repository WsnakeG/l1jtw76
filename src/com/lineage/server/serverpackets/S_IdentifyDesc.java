package com.lineage.server.serverpackets;

import java.util.Map.Entry;

import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 物品資訊訊息(使用String-c.tbl)
 * 
 * @author dexc
 */
public class S_IdentifyDesc extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品資訊訊息(使用ItemDesc-c.tbl)
	 */
	public S_IdentifyDesc(final L1ItemInstance item) {
		buildPacket(item);
	}

	private void buildPacket(final L1ItemInstance item) {
		writeC(S_OPCODE_IDENTIFYDESC);
		writeH(item.getItem().getItemDescId());

		final StringBuilder name = new StringBuilder();

		switch (item.getItem().getBless()) {
		case 0:// 祝福
			name.append("$227 ");
			break;

		case 2:// 詛咒
			name.append("$228 ");
			break;
		}

		name.append(item.getItem().getNameId());

		// 額外顯示字串 by terry0412
		final StringBuilder extra_str = new StringBuilder();

		extra_str.append("使用職業: ");

		if (item.getItem().isUseRoyal() && item.getItem().isUseKnight() && item.getItem().isUseElf()
				&& item.getItem().isUseMage() && item.getItem().isUseDarkelf()
				&& item.getItem().isUseDragonknight() && item.getItem().isUseIllusionist()
				&& item.getItem().isUseWarrior()) {
			extra_str.append("[全職業]");

		} else {
			if (item.getItem().isUseRoyal()) {
				extra_str.append("[王族]");
			}
			if (item.getItem().isUseKnight()) {
				extra_str.append("[騎士]");
			}
			if (item.getItem().isUseElf()) {
				extra_str.append("[妖精]");
			}
			if (item.getItem().isUseMage()) {
				extra_str.append("[法師]");
			}
			if (item.getItem().isUseDarkelf()) {
				extra_str.append("[黑妖]");
			}
			if (item.getItem().isUseDragonknight()) {
				extra_str.append("[龍騎]");
			}
			if (item.getItem().isUseIllusionist()) {
				extra_str.append("[幻術]");
			}
			if (item.getItem().isUseWarrior()) {
				extra_str.append("[戰士]");
			}
		}

		final int use_camp = item.getItem().getCampSet();
		if (use_camp > 0) {
			extra_str.append("\n使用陣營: ");

			// 取得陣營列表
			for (final Entry<Integer, String> value : C1_Name_Table.get().getMapList().entrySet()) {
				if ((use_camp & value.getKey()) == value.getKey()) {
					extra_str.append("[").append(value.getValue()).append("]");
				}
			}
		}

		switch (item.getItem().getType2()) {
		case 1: // weapon
			writeH(0x0086); // 134 \f1%0：小さなモンスター打撃%1 大きなモンスター打撃%2
			writeC(0x03);
			writeS(name.toString());
			writeS(item.getItem().getDmgSmall() + "+" + item.getEnchantLevel());
			writeS(item.getItem().getDmgLarge() + "+" + item.getEnchantLevel() + "\n" + extra_str.toString());
			break;

		case 2: // armor
			switch (item.getItem().getItemId()) {
			default: // 其餘防具
				writeH(0x0087); // 135 \f1%0：防御力%1 防御具
				writeC(0x02);
				writeS(name.toString());
				writeS(Math.abs(item.getItem().get_ac()) + "+" + item.getEnchantLevel() + "\n"
						+ extra_str.toString());
				break;
			}
			break;

		case 0: // etcitem
			switch (item.getItem().getType()) {
			case 1: // wand
				writeH(0x0089); // 137 \f1%0：使用可能回数%1［重さ%2］
				writeC(0x03);
				writeS(name.toString());
				writeS(String.valueOf(item.getChargeCount()));
				break;

			case 2: // light系アイテム
				writeH(0x008a);// 138 \f1%0：［重さ%1］
				writeC(0x02);
				name.append(": $231 "); // 残りの燃料
				name.append(String.valueOf(item.getRemainingTime()));
				writeS(name.toString());
				break;

			case 7: // food
				writeH(0x0088); // 136 \f1%0：満腹度%1［重さ%2］
				writeC(0x03);
				writeS(name.toString());
				writeS(String.valueOf(item.getItem().getFoodVolume()));
				break;

			default:
				writeH(0x008a); // 138 \f1%0：［重さ%1］
				writeC(0x02);
				writeS(name.toString());
				break;
			}
			writeS(String.valueOf(item.getWeight()) + "\n" + extra_str.toString());
			break;
		}
	}

	/**
	 * 物品資訊訊息(使用String-c.tbl) 測試
	 */
	public S_IdentifyDesc() {
		// 骰子匕首
		final L1ItemInstance item = ItemTable.get().createItem(2);

		writeC(S_OPCODE_IDENTIFYDESC);
		writeH(item.getItem().getItemDescId());

		writeH(134); // \f1%0：小さなモンスター打撃%1 大きなモンスター打撃%2
		writeC(3);
		writeS(item.getName());
		writeS(item.getItem().getDmgSmall() + "+" + item.getEnchantLevel());
		writeS(item.getItem().getDmgLarge() + "+" + item.getEnchantLevel());
		writeS(String.valueOf(item.getWeight()));
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public byte[] getContentBIG5() { //20240901
		if (_byte == null) {
			_byte = _bao3.toByteArray();
		}
		return _byte;
	}
	
	@Override
	public byte[] getContentGBK() { //20240901
		if (_byte == null) {
			_byte = _bao5.toByteArray();
		}
		return _byte;
	}
}
