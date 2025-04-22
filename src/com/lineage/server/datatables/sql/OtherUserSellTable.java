package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.OtherUserSellStorage;
import com.lineage.server.utils.SQLUtil;

/**
 * 賣出物品給個人商店紀錄
 * 
 * @author dexc
 */
public class OtherUserSellTable implements OtherUserSellStorage {

	private static final Log _log = LogFactory.getLog(OtherUserSellTable.class);

	/**
	 * 增加紀錄
	 * 
	 * @param itemname 回收物品名稱
	 * @param itemobjid 回收物品OBJID
	 * @param itemadena 單件物品回收金額
	 * @param itemcount 回收數量
	 * @param pcobjid 賣出者OBJID
	 * @param pcname 賣出者名稱
	 * @param srcpcobjid 買入者OBJID(個人商店)
	 * @param srcpcname 買入者名稱(個人商店)
	 */
	@Override
	public void add(final String itemname, final int itemobjid, final int itemadena, final long itemcount,
			final int pcobjid, final String pcname, final int srcpcobjid, final String srcpcname) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("INSERT INTO `other_pcsell` SET "
					+ "`itemname`=?,`itemobjid`=?,`itemadena`=?,`itemcount`=?," + "`pcobjid`=?,`pcname`=?,"
					+ "`srcpcobjid`=?,`srcpcname`=?," + "`datetime`=SYSDATE()");
			int i = 0;
			ps.setString(++i, itemname);
			ps.setInt(++i, itemobjid);
			ps.setInt(++i, itemadena);
			ps.setLong(++i, itemcount);
			ps.setInt(++i, pcobjid);
			ps.setString(++i, pcname + "(賣家)");
			ps.setInt(++i, srcpcobjid);
			ps.setString(++i, srcpcname + "(買家-商店)");
			ps.execute();

		} catch (final Exception e) {
			SqlError.isError(_log, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
	}
}
