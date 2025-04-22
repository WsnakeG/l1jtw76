package com.lineage.server.datatables.storage;

/**
 * 買入個人商店物品紀錄
 * 
 * @author dexc
 */
public interface OtherUserBuyStorage {

	/**
	 * 增加紀錄
	 * 
	 * @param itemname 買入物品名稱
	 * @param itemobjid 買入物品OBJID
	 * @param itemadena 單件物品買入金額
	 * @param itemcount 買入數量
	 * @param pcobjid 買入者OBJID
	 * @param pcname 買入者名稱
	 * @param srcpcobjid 賣出者OBJID(個人商店)
	 * @param srcpcname 賣出者名稱(個人商店)
	 */
	public void add(final String itemname, final int itemobjid, final int itemadena, final long itemcount,
			final int pcobjid, final String pcname, final int srcpcobjid, final String srcpcname);
}
