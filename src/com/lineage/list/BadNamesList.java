package com.lineage.list;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.utils.StreamUtil;

/**
 * 人物名稱禁用字元
 * 
 * @author dexc
 */
public class BadNamesList {

	private static final Log _log = LogFactory.getLog(BadNamesList.class);

	private static BadNamesList _instance;

	private final ArrayList<String> _nameList = new ArrayList<String>();

	public static BadNamesList get() {
		if (_instance == null) {
			_instance = new BadNamesList();
		}
		return _instance;
	}

	public void load() {
		LineNumberReader lnr = null;

		try {
			final File file = new File("data/badnames.txt");
			final FileInputStream fileIn = new FileInputStream(file);
			final InputStreamReader inputStream = new InputStreamReader(fileIn, "utf8");
			lnr = new LineNumberReader(inputStream);

			boolean isWhile = false;
			String line = null;
			while ((line = lnr.readLine()) != null) {
				if (!isWhile) {// 忽略第一行
					isWhile = true;
					continue;
				}
				if ((line.trim().length() == 0) || line.startsWith("#")) {
					continue;
				}
				final StringTokenizer st = new StringTokenizer(line, ";");
				final String ban = st.nextToken();
				_nameList.add(ban);
			}

			_log.info("載入禁止名稱數量: " + _nameList.size());

		} catch (final FileNotFoundException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			StreamUtil.close(lnr);
		}
	}

	public boolean isBadName(final String name) {
		final String checkName = name.toLowerCase();
		for (final String badName : _nameList) {
			if (checkName.indexOf(badName.toLowerCase()) != -1) {
				_log.info("新建人物名稱包含禁用字元: " + _nameList.size());
				return true;
			}
		}
		return false;
	}

	public String[] getAllBadNames() {
		return _nameList.toArray(new String[_nameList.size()]);
	}
}
