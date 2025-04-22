package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.OtherUserBuyStorage;
import com.lineage.server.utils.SQLUtil;

/**
 * 買入個人商店物品紀錄
 * 
 * @author dexc
 */
public class OtherUserBuyTable implements OtherUserBuyStorage {

	private static final Log _log = LogFactory.getLog(OtherUserBuyTable.class);

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
	@Override
	public void add(final String itemname, final int itemobjid, final int itemadena, final long itemcount,
			final int pcobjid, final String pcname, final int srcpcobjid, final String srcpcname) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("INSERT INTO `other_pcbuy` SET "
					+ "`itemname`=?,`itemobjid`=?,`itemadena`=?,`itemcount`=?," + "`pcobjid`=?,`pcname`=?,"
					+ "`srcpcobjid`=?,`srcpcname`=?," + "`datetime`=SYSDATE()");
			int i = 0;
			ps.setString(++i, itemname);
			ps.setInt(++i, itemobjid);
			ps.setInt(++i, itemadena);
			ps.setLong(++i, itemcount);
			ps.setInt(++i, pcobjid);
			ps.setString(++i, pcname + "(買家)");
			ps.setInt(++i, srcpcobjid);
			ps.setString(++i, srcpcname + "(賣家-商店)");
			ps.execute();

		} catch (final Exception e) {
			SqlError.isError(_log, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
	}
}
