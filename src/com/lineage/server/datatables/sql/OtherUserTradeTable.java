package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.OtherUserTradeStorage;
import com.lineage.server.utils.SQLUtil;

/**
 * 個人交易物品紀錄
 * 
 * @author dexc
 */
public class OtherUserTradeTable implements OtherUserTradeStorage {

	private static final Log _log = LogFactory.getLog(OtherUserTradeTable.class);

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
	@Override
	public void add(final String itemname, final int itemobjid, final int itemadena, final long itemcount,
			final int pcobjid, final String pcname, final int srcpcobjid, final String srcpcname) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("INSERT INTO `other_pctrade` SET "
					+ "`itemname`=?,`itemobjid`=?,`itemadena`=?,`itemcount`=?," + "`pcobjid`=?,`pcname`=?,"
					+ "`srcpcobjid`=?,`srcpcname`=?," + "`datetime`=SYSDATE()");

			int i = 0;
			ps.setString(++i, itemname);
			ps.setInt(++i, itemobjid);
			ps.setInt(++i, itemadena);
			ps.setLong(++i, itemcount);
			ps.setInt(++i, pcobjid);
			ps.setString(++i, "移入人物:" + pcname);
			ps.setInt(++i, srcpcobjid);
			ps.setString(++i, "移出人物:" + srcpcname);
			ps.execute();

		} catch (final Exception e) {
			SqlError.isError(_log, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
	}

}
