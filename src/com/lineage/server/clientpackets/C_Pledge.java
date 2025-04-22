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
 * Author: ChrisLiu.2007.06.30
 */
package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Pledge;
import com.lineage.server.serverpackets.S_PledgeUI;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.WorldClan;

/**
 * 要求查看血盟成員清單
 * @author admin
 *
 */
public class C_Pledge extends ClientBasePacket {

	@Override
	public void start(final byte[] decrypt, final ClientExecutor clientthread) {
		try {
			L1PcInstance pc = clientthread.getActiveChar();

			if (pc.getClanid() > 0) {
				
				L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				pc.sendPackets(new S_PledgeUI(clan, S_PledgeUI.ES_PLEDGE_INFO));
				pc.sendPackets(new S_PledgeUI(clan, S_PledgeUI.ES_PLEDGE_MEMBER_INFO));
				pc.sendPackets(new S_PledgeUI(clan, S_PledgeUI.ES_ONLINE_MEMBER_INFO));
				
			} else {
				S_ServerMessage $1064 = new S_ServerMessage(1064);
				pc.sendPackets($1064); // 血盟に属していません。
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
