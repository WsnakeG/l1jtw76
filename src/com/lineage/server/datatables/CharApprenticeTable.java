/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Apprentice;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1CharName;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 師徒系統
 * 
 * @author terry0412
 */
public final class CharApprenticeTable {

	private static final Logger _log = Logger.getLogger(CharApprenticeTable.class.getName());

	// 全部師徒資料
	private final HashMap<Integer, L1Apprentice> _masterList = new HashMap<Integer, L1Apprentice>();

	private static CharApprenticeTable _instance;

	public static CharApprenticeTable getInstance() {
		if (_instance == null) {
			_instance = new CharApprenticeTable();
		}
		return _instance;
	}

	public final void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_apprentice ORDER BY master_id");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int master_id = rs.getInt("master_id");
				final int apprentice_id1 = rs.getInt("apprentice_id1");
				final int apprentice_id2 = rs.getInt("apprentice_id2");
				final int apprentice_id3 = rs.getInt("apprentice_id3");
				final int apprentice_id4 = rs.getInt("apprentice_id4");

				L1PcInstance master = null;
				L1PcInstance apprentice = null;

				final ArrayList<L1PcInstance> totalList = new ArrayList<L1PcInstance>(4);

				for (final L1CharName l1char : CharacterTable.get().getCharNameList()) {
					if ((master == null) && (l1char.getId() == master_id)) {
						master = CharacterTable.get().restoreCharacter(l1char.getName());
					} else if ((l1char.getId() == apprentice_id1) || (l1char.getId() == apprentice_id2)
							|| (l1char.getId() == apprentice_id3) || (l1char.getId() == apprentice_id4)) {
						apprentice = CharacterTable.get().restoreCharacter(l1char.getName());
						if (apprentice != null) {
							totalList.add(apprentice);
						}
					}
				}

				// 師父不存在 or 徒弟數量小於1
				if ((master == null) || (totalList.size() < 1)) {
					deleteApprentice(master_id);
					continue;
				}

				final L1Apprentice l1apprentice = new L1Apprentice(master,
						totalList.toArray(new L1PcInstance[totalList.size()]));
				_masterList.put(master_id, l1apprentice);
			}
		} catch (final SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入師徒系統資料數量: " + _masterList.size() + "(" + timer.get() + "ms)");
	}

	public final void insertApprentice(final L1Apprentice apprentice) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("INSERT INTO character_apprentice SET master_id=?, apprentice_id1=?");
			pstm.setInt(1, apprentice.getMaster().getId());
			pstm.setInt(2, apprentice.getTotalList().get(0).getId());
			pstm.execute();
			// 加入列表
			_masterList.put(apprentice.getMaster().getId(), apprentice);
		} catch (final SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public final void updateApprentice(final int master_id, final ArrayList<L1PcInstance> totalList) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"UPDATE character_apprentice SET apprentice_id1=?, apprentice_id2=?, apprentice_id3=?, apprentice_id4=? WHERE master_id=?");
			pstm.setInt(1, totalList.size() > 0 ? totalList.get(0).getId() : 0);
			pstm.setInt(2, totalList.size() > 1 ? totalList.get(1).getId() : 0);
			pstm.setInt(3, totalList.size() > 2 ? totalList.get(2).getId() : 0);
			pstm.setInt(4, totalList.size() > 3 ? totalList.get(3).getId() : 0);
			pstm.setInt(5, master_id);
			pstm.execute();
		} catch (final SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public final void deleteApprentice(final int master_id) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_apprentice WHERE master_id=?");
			pstm.setInt(1, master_id);
			pstm.execute();

			_masterList.remove(master_id);
		} catch (final SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public final L1Apprentice getApprentice(final L1PcInstance pc) {
		final int objid = pc.getId();
		final L1Apprentice charApprentice = _masterList.get(objid);
		if (charApprentice != null) {
			if (charApprentice.getMaster() != pc) {
				charApprentice.setMaster(pc);
			}
			return charApprentice;
		}
		for (final L1Apprentice apprentice : _masterList.values()) {
			if (apprentice.getTotalList().contains(pc)) {
				return apprentice;
			}
			for (final L1PcInstance l1char : apprentice.getTotalList()) {
				if (l1char.getId() == objid) {
					if (l1char != pc) {
						apprentice.getTotalList().remove(l1char);
						apprentice.getTotalList().add(pc);
					}
					return apprentice;
				}
			}
		}
		return null;
	}
}
