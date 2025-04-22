/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.lineage.server.datatables;

import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.SQLUtil;

public class LogAcceleratorTable {
	private static Logger _log = Logger.getLogger(LogAcceleratorTable.class.getName());

	public void storeLogAccelerator(final L1PcInstance pc) {

		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO log_accelerator SET account_name=?, objid=?, char_name=?, ClanID=?, Clanname=?, LocX=?, LocY=?, MapID=?, datetime=SYSDATE()");
			pstm.setString(1, pc.getAccountName());
			pstm.setInt(2, pc.getId());
			pstm.setString(3, pc.getName());
			pstm.setInt(4, pc.getClanid());
			pstm.setString(5, pc.getClanname());
			pstm.setInt(6, pc.getX());
			pstm.setInt(7, pc.getY());
			pstm.setInt(8, pc.getMapId());
			pstm.execute();

		} catch (final Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}

}