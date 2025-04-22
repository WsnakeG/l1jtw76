package com.lineage.server.datatables.storage;

/**
 * 個人交易物品紀錄
 * 
 * @author dexc
 */
public interface OtherUserTradeStorage {

	/**
	 * 增加紀錄
	 * 
	 * @param itemname 物品名稱
	 * @param itemobjid 物品OBJID
	 * @param itemadena 0 暫無用途
	 * @param itemcount 數量
	 * @param pcobjid 移入人物OBJID
	 * @param pcname 移入人物名稱
	 * @param srcpcobjid 移出人物者OBJID
	 * @param srcpcname 移出人物名稱
	 */
	public void add(final String itemname, final int itemobjid, final int itemadena, final long itemcount,
			final int pcobjid, final String pcname, final int srcpcobjid, final String srcpcname);
}
