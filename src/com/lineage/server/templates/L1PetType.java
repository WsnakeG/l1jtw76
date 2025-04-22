package com.lineage.server.templates;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.utils.RangeInt;

public class L1PetType {

	private final int _baseNpcId;

	private final L1Npc _baseNpcTemplate;

	private final String _name;

	private final int _itemIdForTaming;

	private final RangeInt _hpUpRange;

	private final RangeInt _mpUpRange;

	private final int _itemIdForEvolving;

	private final int _npcIdForEvolving;

	private final int _msgIds[];

	private final int _defyMsgId;

	public L1PetType(final int baseNpcId, final String name, final int itemIdForTaming,
			final RangeInt hpUpRange, final RangeInt mpUpRange, final int itemIdForEvolving,
			final int npcIdForEvolving, final int msgIds[], final int defyMsgId) {
		_baseNpcId = baseNpcId;
		_baseNpcTemplate = NpcTable.get().getTemplate(baseNpcId);
		_name = name;
		_itemIdForTaming = itemIdForTaming;
		_hpUpRange = hpUpRange;
		_mpUpRange = mpUpRange;
		_itemIdForEvolving = itemIdForEvolving;
		_npcIdForEvolving = npcIdForEvolving;
		_msgIds = msgIds;
		_defyMsgId = defyMsgId;
	}

	public int getBaseNpcId() {
		return _baseNpcId;
	}

	public L1Npc getBaseNpcTemplate() {
		return _baseNpcTemplate;
	}

	public String getName() {
		return _name;
	}

	public int getItemIdForTaming() {
		return _itemIdForTaming;
	}

	public boolean canTame() {
		return _itemIdForTaming != 0;
	}

	public RangeInt getHpUpRange() {
		return _hpUpRange;
	}

	public RangeInt getMpUpRange() {
		return _mpUpRange;
	}

	public int getItemIdForEvolving() {
		return _itemIdForEvolving;
	}

	/**
	 * 進化後ID
	 * 
	 * @return
	 */
	public int getNpcIdForEvolving() {
		return _npcIdForEvolving;
	}

	/**
	 * 可以進化
	 * 
	 * @return
	 */
	public boolean canEvolve() {
		return _npcIdForEvolving != 0;
	}

	public int getMessageId(final int num) {
		if (num == 0) {
			return 0;
		}
		return _msgIds[num - 1];
	}

	public static int getMessageNumber(final int level) {
		if (50 <= level) {
			return 5;
		}
		if (48 <= level) {
			return 4;
		}
		if (36 <= level) {
			return 3;
		}
		if (24 <= level) {
			return 2;
		}
		if (12 <= level) {
			return 1;
		}
		return 0;
	}

	public int getDefyMessageId() {
		return _defyMsgId;
	}
}
