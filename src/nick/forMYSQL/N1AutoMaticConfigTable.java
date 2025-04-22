package nick.forMYSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;

public class N1AutoMaticConfigTable {

	// 20240421
	private static final Log _log = LogFactory.getLog(N1AutoMaticConfigTable.class);

	private static N1AutoMaticConfigTable _instance;

	private final Map<Integer, N1AutoMaticConfig> _ConfigIndex = Maps.newHashMap();
	
	// 20240524
	private final Map<String, N1AutoMaticConfig> _ConfigIndex1 = Maps.newHashMap();

	private static final Map<String, String> _extraSet = new HashMap<String, String>();

	public static N1AutoMaticConfigTable get() {
		if (_instance == null) {
			_instance = new N1AutoMaticConfigTable();
		}
		return _instance;
	}

	public void load() {
		PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM 群版_contron_auto");
			rs = pstm.executeQuery();
			fillSystemMessage(rs);
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("Load--->群版_contron_auto設定, " + "資料共" + _ConfigIndex.size() + "筆, " + "花費(" + timer.get() + "ms) OK");
	}

	private void fillSystemMessage(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int Id = rs.getInt("id");
			String data = rs.getString("勿動");
			String Message = rs.getString("參數");

			N1AutoMaticConfig System_Message = new N1AutoMaticConfig(Id, Message);
			
			// 20240524
			N1AutoMaticConfig System_Message1 = new N1AutoMaticConfig(data, Message);

			_ConfigIndex.put(Id, System_Message);
			// 20240524
			_ConfigIndex1.put(data, System_Message1);

		}
	}

	public N1AutoMaticConfig getTemplate(int Id) {
		return _ConfigIndex.get(Integer.valueOf(Id));
	}

	// 20240524
	public N1AutoMaticConfig getTemplate2(String data) {
		return _ConfigIndex1.get(String.valueOf(data));
	}

	// 對應寫入內掛額外增加
	/** 取回額外設置 */
	public String getSet(final String key) {
		return _extraSet.get(key);
	}

}
