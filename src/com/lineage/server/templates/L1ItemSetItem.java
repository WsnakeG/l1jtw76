package com.lineage.server.templates;

public class L1ItemSetItem {
	private final int id;
	private final int amount;
	private final int enchant;

	public L1ItemSetItem(final int id, final int amount, final int enchant) {
		super();
		this.id = id;
		this.amount = amount;
		this.enchant = enchant;
	}

	public int getId() {
		return id;
	}

	public int getAmount() {
		return amount;
	}

	public int getEnchant() {
		return enchant;
	}
}