package com.lineage.server.datatables.sql;

import org.apache.commons.logging.Log;

import com.lineage.config.Config;

/**
 * SQL 錯誤訊息
 * 
 * @author dexc
 */
public class SqlError {

	private static boolean _debug = Config.DEBUG;

	/**
	 * 錯誤訊息
	 * 
	 * @param log
	 * @param string
	 * @param e
	 */
	public static void isError(final Log log, final String string, final Exception e) {
		if (_debug) {
			log.error(string, e);
		}
	}
}
