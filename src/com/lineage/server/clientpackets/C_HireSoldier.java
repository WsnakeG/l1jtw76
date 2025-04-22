package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;

/**
 * 要求顧用傭兵
 * 
 * @author daien
 */
public class C_HireSoldier extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_HireSoldier.class);

	@SuppressWarnings("unused")
	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final int something1 = readH(); // S_HireSoldierパケットの引数
			final int something2 = readH(); // S_HireSoldierパケットの引数
			final int something3 = readD(); // 1以外入らない？
			final int something4 = readD(); // S_HireSoldierパケットの引数
			final int number = readH(); // 雇用する数

			// < 傭兵雇用処理

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
