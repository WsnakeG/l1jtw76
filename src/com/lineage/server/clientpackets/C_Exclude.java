/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package com.lineage.server.clientpackets;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1ExcludingList;
import com.lineage.server.model.L1ExcludingMailList;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxExclude;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 處理來自客戶端要求斷絕他人訊息的操作
 * 
 * @author admin
 * 
 */
public class C_Exclude extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_Exclude.class.getName());

	/**
	 * 要求加入移除斷絕清單
	 * 
	 * @param decrypt
	 * @param client
	 */
	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			
			final L1PcInstance pc = client.getActiveChar();
			
			final String name = this.readS();
			
			if (name.isEmpty()) {
				return;
			}
			
			final int type = this.readC();
			
			if (type == 0) {// 斷絕指定名稱談話
				try {
					final L1ExcludingList exList = pc.getExcludingList();
					if (exList.isFull()) {
						pc.sendPackets(new S_ServerMessage(472)); // \f1遮断されたユーザーが多すぎます。
						return;
					}
					
					if (exList.contains(name)) {
						final String temp = exList.remove(name);
						pc.sendPackets(new S_PacketBoxExclude(S_PacketBox.REM_EXCLUDE, temp, 0));
					} else {
						exList.add(name);
						pc.sendPackets(new S_PacketBoxExclude(S_PacketBox.ADD_EXCLUDE, name, 0));
					}
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			} else if (type == 1) {// 信件黑名單(XXX 可能需要一個DB TABLE來記錄 尚待實做)
				try {
					final L1ExcludingMailList exList = pc.getExcludingMailList();
					if (exList.isFull()) {
						pc.sendPackets(new S_ServerMessage(472)); // \f1遮断されたユーザーが多すぎます。
						return;
					}
					
					if (exList.contains(name)) {
						final String temp = exList.remove(name);
						pc.sendPackets(new S_PacketBoxExclude(S_PacketBox.REM_EXCLUDE, temp, 1));
					} else {
						exList.add(name);
						pc.sendPackets(new S_PacketBoxExclude(S_PacketBox.ADD_EXCLUDE, name, 1));
					}
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}			
		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			this.over();
		}
	}

	@Override
	public String getType() {
		return "[C] " + this.getClass().getSimpleName();
	}
}
