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

public class ControlBuffTable {

	private static final Log _log = LogFactory.getLog(ControlBuffTable.class);

	private static ControlBuffTable _instance;

	private final Map<Integer, ControlBuff> _ConfigIndex = Maps.newHashMap();

	public final Map<String, ControlBuff> _ConfigIndex1 = Maps.newHashMap();

	public static ControlBuffTable get() {
		if (_instance == null) {
			_instance = new ControlBuffTable();
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
			pstm = con.prepareStatement("SELECT * FROM 群版_control_buff");
			rs = pstm.executeQuery();
			fillSystemMessage(rs);
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("Load--->群版_control_buff設定, " + "資料共" + _ConfigIndex.size() + "筆, " + "花費(" + timer.get() + "ms) OK");
	}

	private void fillSystemMessage(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int Id = rs.getInt("id");
			String data = rs.getString("勿動");
			String Message = rs.getString("參數");

			ControlBuff System_Message = new ControlBuff(Id, Message);
			ControlBuff System_Message1 = new ControlBuff(data, Message);

			_ConfigIndex.put(Id, System_Message);
			_ConfigIndex1.put(data, System_Message1);
		}
	}

	public ControlBuff getTemplate(int Id) {
		return _ConfigIndex.get(Integer.valueOf(Id));
	}

	public ControlBuff getTemplate2(String data) {
		return _ConfigIndex1.get(String.valueOf(data));
	}
}
