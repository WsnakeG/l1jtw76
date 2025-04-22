package com.lineage.server.templates;

/**
 * @author XXX
 */
public class L1MobSkillGroup {

	private final int skillSize;

	public int getSkillSize() {
		return skillSize;
	}

	public L1MobSkillGroup(final int sSize) {
		skillSize = sSize;

		interval = new int[skillSize];
		chance = new int[skillSize];
		range = new int[skillSize];
		leverage = new int[skillSize];
		actNoList = new int[skillSize][];
	}

	private int mobid;

	public int get_mobid() {
		return mobid;
	}

	public void set_mobid(final int i) {
		mobid = i;
	}

	String chatId[];

	public String getChatId(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize()) || (chatId == null)) {
			return null;
		}
		return chatId[idx];
	}

	public void setChatId(final int idx, final String str) {
		if ((idx < 0) || (idx >= getSkillSize()) || (str == null) || str.isEmpty()) {
			return;
		}
		// 初始空值重設
		if (chatId == null) {
			chatId = new String[getSkillSize()];
		}
		chatId[idx] = str;
	}

	int interval[];

	public int getInterval(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return interval[idx];
	}

	public void setInterval(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		interval[idx] = i;
	}

	private final int chance[];

	public int getChance(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return chance[idx];
	}

	public void setChance(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		chance[idx] = i;
	}

	int range[];

	public int getRange(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return range[idx];
	}

	public void setRange(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		range[idx] = i;
	}

	int leverage[];

	public int getLeverage(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return 0;
		}
		return leverage[idx];
	}

	public void setLeverage(final int idx, final int i) {
		if ((idx < 0) || (idx >= getSkillSize())) {
			return;
		}
		leverage[idx] = i;
	}

	int actNoList[][];

	public int[] getActNoList(final int idx) {
		if ((idx < 0) || (idx >= getSkillSize()) || (actNoList[idx] == null)) {
			return null;
		}
		return actNoList[idx];
	}

	public void setActNoList(final int idx, final String str) {
		if ((idx < 0) || (idx >= getSkillSize()) || (str == null) || str.isEmpty()) {
			return;
		}
		final String[] newStr = str.trim().split(",");

		final int size = newStr.length;

		actNoList[idx] = new int[size];

		for (int i = 0; i < size; i++) {
			actNoList[idx][i] = Integer.parseInt(newStr[i]);
		}
	}

	public final int getActNoMaxSize() {
		int max_size = 0;
		for (final int[] actNo : actNoList) {
			if (max_size < actNo.length) {
				max_size = actNo.length;
			}
		}
		return max_size;
	}
}
