package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 要求選取觀看頻道
 * 
 * @author daien
 */
public class C_LoginToServerOK extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_LoginToServerOK.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final int type = readC();
			final int button = readC();

			final L1PcInstance pc = client.getActiveChar();

			// System.out.println("type:"+type+"\nbutton:"+button);

			switch (type) {
			case 255: // 全体チャット && Whisper
				switch (button) {
				case 95:
				case 127:
					pc.setShowWorldChat(true); // open
					pc.setCanWhisper(true); // open
					break;

				case 91:
				case 123:
					pc.setShowWorldChat(true); // open
					pc.setCanWhisper(false); // close
					break;

				case 94:
				case 126:
					pc.setShowWorldChat(false); // close
					pc.setCanWhisper(true); // open
					break;

				case 90:
				case 122:
					pc.setShowWorldChat(false); // close
					pc.setCanWhisper(false); // close
					break;
				}
				break;

			case 0: // 全體聊天
				if (button == 0) { // close
					pc.setShowWorldChat(false);

				} else if (button == 1) { // open
					pc.setShowWorldChat(true);
				}
				break;

			case 2: // 密語頻道
				if (button == 0) { // close
					pc.setCanWhisper(false);

				} else if (button == 1) { // open
					pc.setCanWhisper(true);
				}
				break;

			case 6: // 買賣頻道
				if (button == 0) { // close
					pc.setShowTradeChat(false);

				} else if (button == 1) { // open
					pc.setShowTradeChat(true);
				}
				break;
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
