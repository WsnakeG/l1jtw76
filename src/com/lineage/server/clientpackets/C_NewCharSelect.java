package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.quest.Chapter01;
import com.lineage.data.quest.Chapter02;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_PacketBoxSelect;

/**
 * 要求切換角色
 * 
 * @author dexc
 */
public class C_NewCharSelect extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_NewCharSelect.class);

	/*
	 * public C_NewCharSelect() { } public C_NewCharSelect(final byte[] abyte0,
	 * final ClientExecutor client) { super(abyte0); try { this.start(abyte0,
	 * client); } catch (final Exception e) {
	 * _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			// Thread.sleep(5000);
			if (pc == null) {
				return;
			}
			// 副本任務進行中 by terry0412
			if ((pc.getMapId() == Chapter01.MAPID) || (pc.getMapId() == Chapter02.MAPID)) {
				return;
			}

			// 改變顯示(復原正常)
			pc.sendPackets(new S_ChangeName(pc.getId(), pc.getName()));

			Thread.sleep(250);
			client.quitGame();

			// 返回人物選取畫面
			client.out().encrypt(new S_PacketBoxSelect());

			_log.info("角色切換: " + pc.getName());

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
