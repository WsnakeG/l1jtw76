package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.CharacterConfigReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Config;

/**
 * 要求紀錄快速鍵
 * 
 * @author daien
 */
public class C_CharcterConfig extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CharcterConfig.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			// System.out.println(pc);
			if (pc == null) {
				return;
			}
			final int objid = pc.getId();

			final int length = readD() - 3;
			final byte data[] = readByte();

			final L1Config config = CharacterConfigReading.get().get(objid);

			// 新建
			if (config == null) {
				CharacterConfigReading.get().storeCharacterConfig(objid, length, data);

				// 更新
			} else {
				CharacterConfigReading.get().updateCharacterConfig(objid, length, data);
			}

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
