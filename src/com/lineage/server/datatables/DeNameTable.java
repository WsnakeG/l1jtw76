package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.config.Config;
import com.lineage.server.templates.DeClan;
import com.lineage.server.templates.DeName;
import com.lineage.server.utils.SQLUtil;

/**
 * 虛擬人物
 * 
 * @author dexc
 */
public class DeNameTable {

	private static final Log _log = LogFactory.getLog(DeNameTable.class);

	private static final Map<String, DeName> _denameList = new HashMap<String, DeName>();

	private static final Map<Integer, DeName> _denames = new HashMap<Integer, DeName>();

	private static DeNameTable _instance;

	// 重建人物血盟資料
	private static boolean _reclanInfo = false;

	public static DeNameTable get() {
		if (_instance == null) {
			_instance = new DeNameTable();
		}
		return _instance;
	}

	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `de_name`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int deobjid = rs.getInt("deobjid");
				String name = "";
				if (Config.CLIENT_LANGUAGE == 3) {
					name = rs.getString("denamebig5");
				} else {
					name = rs.getString("dename");
				}
				final int type = rs.getInt("type");
				final int sex = rs.getInt("sex");
				final int clanid = rs.getInt("clanid");

				final DeName deName = new DeName(deobjid, name, type, sex, clanid);
				if (_denameList.get(name) == null) {
					_denameList.put(name, deName);
					_denames.put(deobjid, deName);
					CharObjidTable.get().addChar(deobjid, name);

				} else {
					del(deobjid);
				}

				if (_reclanInfo) {
					// XXX 重建人物血盟資料
					reclanInfo(name, deName);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 刪除重複名稱資料
	 * 
	 * @param name_id
	 */
	private void del(final int deobjid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM `de_name` WHERE `deobjid`=?");
			pstm.setInt(1, deobjid);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private final static Random _random = new Random();

	/**
	 * 重建虛擬人物血盟資料
	 * 
	 * @param name
	 * @param deName
	 */
	private void reclanInfo(final String name, final DeName deName) {
		final DeClan deClan = DeClanTable.get().get(name);
		if (deClan != null) {
			deName.set_clanid(deClan.get_clan_id());

			deName.set_type(0);

			if (_random.nextBoolean()) {
				deName.set_sex(0);

			} else {
				deName.set_sex(1);
			}
		} else {
			if (_random.nextInt(100) < 40) {
				final Collection<DeClan> list = DeClanTable.get().getList();
				final int i = _random.nextInt(list.size());
				int x = 0;
				for (final DeClan dxeClan : list) {
					x++;
					if (x != i) {
						continue;
					}
					deName.set_clanid(dxeClan.get_clan_id());

				}
			} else {
				deName.set_clanid(0);
			}

			final int itype = _random.nextInt(6) + 1;
			deName.set_type(itype);

			if (_random.nextBoolean()) {
				deName.set_sex(0);

			} else {
				deName.set_sex(1);
			}

		}
		updata_deName(deName);
	}

	/**
	 * 更新虛擬人物血盟資料
	 * 
	 * @param deName
	 */
	private void updata_deName(final DeName deName) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactoryLogin.get().getConnection();
			ps = cn.prepareStatement("UPDATE `de_name` SET `type`=?,`sex`=?,`clanid`=? WHERE `deobjid`=?");
			int i = 0;
			ps.setInt(++i, deName.get_type());
			ps.setInt(++i, deName.get_sex());
			ps.setInt(++i, deName.get_clanid());
			ps.setInt(++i, deName.get_deobjid());
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 指定虛擬人物資料
	 * 
	 * @param objid
	 * @return
	 */
	public DeName getDeName(final int objid) {
		return _denames.get(objid);
	}

	/**
	 * 指定虛擬人物資料
	 * 
	 * @param name
	 * @return
	 */
	public DeName getDeName(final String name) {
		return _denameList.get(name);
	}

	/**
	 * 虛擬人物清單
	 * 
	 * @return
	 */
	public Collection<DeName> getList() {
		return _denameList.values();
	}

	/**
	 * 虛擬人物清單陣列
	 * 
	 * @return
	 */
	public DeName[] getDeNameList() {
		return _denameList.values().toArray(new DeName[_denameList.size()]);
	}
}
