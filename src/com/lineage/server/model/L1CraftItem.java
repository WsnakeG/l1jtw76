package com.lineage.server.model;

import java.util.ArrayList;

import com.lineage.server.templates.L1Item;

/**
 * 新製作系統-道具配置
 * 
 * @author sudawei
 */
public class L1CraftItem {
	/** 物品ID */
	private  int _itemId;
	/** 需要物品的數量 */
	private long _amount;
	/** 強化等級 */
	private int enchantLevel;
	/** 祝福狀態[0:祝福 1:普通 2:詛咒 3:不驗證] */
	private int bless;
	private L1Item item;
	private int random;
	/** 道具顯示位置 */
	private int windowLattice;
	/** 可替代道具 */
	private ArrayList<L1CraftItem> substituteList;

	public L1CraftItem(int _itemId, long _amount, int enchantLevel, int bless,int windowLattice,L1Item item) {
		this._itemId = _itemId;
		this._amount = _amount;
		this.enchantLevel = enchantLevel;
		this.bless = bless;
		this.item=item;
	}

	public L1CraftItem(int _itemId, long _amount, int enchantLevel, int bless, int random, int windowLattice,L1Item item) {
		this._itemId = _itemId;
		this._amount = _amount;
		this.enchantLevel = enchantLevel;
		this.bless = bless;
		this.random = random;
		this.windowLattice = windowLattice;
		this.item=item;
	}
	
	public L1Item getItems() {
		return item;
	}

	public void setItems(L1Item items) {
		this.item = items;
	}
	
	/** 成功機率 */
	public int getRandom() {
		return random;
	}
	
	/** 成功機率 */
	public void setRandom(int random) {
		this.random = random;
	}

	/** 返回強化等級 */
	public int getEnchantLevel() {
		return enchantLevel;
	}

	/** 設置強化等級 */
	public void setEnchantLevel(int enchantLevel) {
		this.enchantLevel = enchantLevel;
	}

	/** 道具祝福狀態 */
	public int getBless() {
		return bless;
	}

	/** 道具祝福狀態 */
	public void setBless(int bless) {
		this.bless = bless;
	}

	public L1CraftItem(int _itemId, final long amount) {
		this._itemId = _itemId;
		this._amount = amount;
	}

	/** 返回物品ID */
	public int getItemId() {
		return this._itemId;
	}

	/** 物品數量 */
	public long getAmount() {
		return this._amount;
	}

	/** 道具顯示位置 */
	public int getWindowLattice() {
		return windowLattice;
	}

	/** 道具顯示位置 */
	public void setWindowLattice(int windowLattice) {
		this.windowLattice = windowLattice;
	}
	/** 可替代道具列表 */
	public ArrayList<L1CraftItem> getSubstituteList() {
		return substituteList;
	}
	/** 可替代道具列表 */
	public void setSubstituteList(ArrayList<L1CraftItem> substituteList) {
		this.substituteList = substituteList;
	}

}
