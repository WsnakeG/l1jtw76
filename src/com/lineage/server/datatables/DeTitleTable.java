package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.config.Config;
import com.lineage.server.utils.SQLUtil;

/**
 * 虛擬人物封號
 * 
 * @author dexc
 */
public class DeTitleTable {

	private static final Log _log = LogFactory.getLog(DeTitleTable.class);

	private static final ArrayList<String> _detitleList = new ArrayList<String>();

	private static DeTitleTable _instance;

	public static DeTitleTable get() {
		if (_instance == null) {
			_instance = new DeTitleTable();
		}
		return _instance;
	}

	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `de_title`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				String detitle = "";
				if (Config.CLIENT_LANGUAGE == 3) {
					detitle = rs.getString("detitlebig5");
				} else {
					detitle = rs.getString("detitle");
				}
				_detitleList.add(detitle);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private final static Random _random = new Random();

	private static final String[] _titleList = new String[] { "\\f2", "\\f3", "\\f4", "\\f5", "\\f6", "\\f7",
			"\\f8", "\\f9", "\\f0", "\\f:", "\\f<", "\\f>", "\\f?", "\\fA", "\\fB", "\\fC", "\\fD", "\\fE",
			"\\fF", "\\fG", "\\fH", "\\fI", "\\fJ", "\\fK", "\\fL", "\\fM", "\\fN", "\\fO", "\\fP", "\\fQ",
			"\\fR", "\\fS", "\\fT", "\\fU", "\\fV", "\\fW", "\\fX", "\\fY", "\\fZ", "\\f@" };

	/**
	 * 隨機封號
	 * 
	 * @param name
	 * @param deName
	 */
	public String getTitle() {
		final int index = _random.nextInt(_detitleList.size());
		final String c = _titleList[_random.nextInt(_titleList.length)];
		return c + _detitleList.get(index);
	}
}
