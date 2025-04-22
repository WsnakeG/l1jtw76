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
 * 虛擬人物喊話
 * 
 * @author dexc
 */
public class DeGlobalChatTable {

	private static final Log _log = LogFactory.getLog(DeGlobalChatTable.class);

	private static final ArrayList<String> _chatList = new ArrayList<String>();

	private static DeGlobalChatTable _instance;

	public static DeGlobalChatTable get() {
		if (_instance == null) {
			_instance = new DeGlobalChatTable();
		}
		return _instance;
	}

	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `de_shopds_global`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				String detitle = "";
				if (Config.CLIENT_LANGUAGE == 3) {
					detitle = rs.getString("chatinfobig5");
				} else {
					detitle = rs.getString("chatinfo");
				}
				_chatList.add(detitle);
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

	/**
	 * 隨機虛擬人物喊話
	 */
	public String getChat() {
		final int index = _random.nextInt(_chatList.size());
		return _chatList.get(index);
	}
}
