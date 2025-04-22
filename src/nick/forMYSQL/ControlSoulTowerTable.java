package nick.forMYSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;

public class ControlSoulTowerTable {

	private static final Log _log = LogFactory.getLog(ControlSoulTowerTable.class);

	private static ControlSoulTowerTable _instance;

	private final Map<Integer, ControlSoulTower> _ConfigIndex = Maps.newHashMap();

	public final Map<String, ControlSoulTower> _ConfigIndex1 = Maps.newHashMap();

	public static ControlSoulTowerTable get() {
		if (_instance == null) {
			_instance = new ControlSoulTowerTable();
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
			pstm = con.prepareStatement("SELECT * FROM 群版_屍魂塔控制道具");
			rs = pstm.executeQuery();
			fillSystemMessage(rs);
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("Load--->群版_屍魂塔控制道具設定, " + "資料共" + _ConfigIndex.size() + "筆, " + "花費(" + timer.get() + "ms) OK");
	}

	private void fillSystemMessage(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int Id = rs.getInt("id");
			String data = rs.getString("勿動");
			String Message = rs.getString("參數");

			ControlSoulTower System_Message = new ControlSoulTower(Id, Message);
			ControlSoulTower System_Message1 = new ControlSoulTower(data, Message);

			_ConfigIndex.put(Id, System_Message);
			_ConfigIndex1.put(data, System_Message1);
		}
	}

	public ControlSoulTower getTemplate(int Id) {
		return _ConfigIndex.get(Integer.valueOf(Id));
	}

	public ControlSoulTower getTemplate2(String data) {
		return _ConfigIndex1.get(String.valueOf(data));
	}
}
