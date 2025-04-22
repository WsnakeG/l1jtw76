package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

public class ConfigDescs {

	// 文字串組<行號, 內容>
	private static final Map<Integer, String> _show_desc = new HashMap<Integer, String>();

	private static final String _show_desc_file = "./config/show_desc.txt";

	public static void load() throws ConfigErrorException {
		try {
			// 取回檔案
			final InputStream is = new FileInputStream(new File(_show_desc_file));
			// 指定檔案編碼
			final InputStreamReader isr = new InputStreamReader(is, "utf-8");
			final LineNumberReader lnr = new LineNumberReader(isr);

			boolean isWhile = false;
			int i = 1;
			String desc = null;
			while ((desc = lnr.readLine()) != null) {
				if (!isWhile) {// 忽略第一行
					isWhile = true;
					continue;
				}
				if ((desc.trim().length() == 0) || desc.startsWith("#")) {
					continue;
				}
				if (desc.startsWith("SERVER_NAME")) {
					desc = desc.replaceAll(" ", "");// 取代空白
					Config.SERVERNAME = desc.substring(12);

				} else {
					_show_desc.put(new Integer(i++), desc);
				}
			}

			is.close();
			isr.close();
			lnr.close();

		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + _show_desc_file);
		}
	}

	/**
	 * 傳回SHOW
	 * 
	 * @param nameid
	 * @return
	 */
	public static String getShow(final int nameid) {
		try {
			return _show_desc.get(new Integer(nameid));

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 傳回SHOW size
	 * 
	 * @return
	 */
	public static int get_show_size() {
		return _show_desc.size();
	}
}
