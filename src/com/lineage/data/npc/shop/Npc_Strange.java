package com.lineage.data.npc.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.TimeLimit.S_TimeLimitBuyList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ShopBuyListCn;
import com.lineage.server.serverpackets.S_ShopSellListCn;

/**
 * 奇怪的商人<BR>
 * 86121 Npc_Strange CLASS異動 44070 元寶 60152 英雄貨幣 60151 靈魂碎片 60242 推文幣 NPC
 * CLASSNAME 增加後參數 範例: shop.Npc_Strange 44070 shop.Npc_Strange 60152
 * shop.Npc_Strange 60151 shop.Npc_Strange 60242
 * 
 * @author dexc
 */
public class Npc_Strange extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Strange.class);

	/**
	 *
	 */
	private Npc_Strange() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Strange();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		if (_htmlid != null) {
			pc.set_temp_adena(_itemid);
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), _htmlid));

		} else {
			pc.set_temp_adena(_itemid);
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_shop"));
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("a")) {
			pc.sendPackets(new S_ShopSellListCn(pc, npc));

		} else if (cmd.equalsIgnoreCase("sell")) {
			pc.sendPackets(new S_ShopBuyListCn(pc, npc));
		}
	}

	private int _itemid = 0;
	private String _htmlid = null;

	@Override
	public void set_set(final String[] set) {
		try {
			_itemid = Integer.parseInt(set[1]);

		} catch (final Exception e) {
			_log.error("NPC專屬貨幣設置錯誤:檢查CLASSNAME為Npc_Strange的NPC設置!");
		}
		try {
			_htmlid = set[2];

		} catch (final Exception e) {
		}
	}
}
