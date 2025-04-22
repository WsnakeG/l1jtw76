package com.lineage.server.model;

import org.apache.commons.logging.Log;

import com.lineage.config.Config;

public class ModelError {

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
