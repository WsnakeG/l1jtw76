package com.lineage.data.npc.other;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 鐵匠^巴特爾<BR>
 * 85025<BR>
 * 
 * @author dexc
 */
public class Npc_Batr extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Batr.class);

	private Npc_Batr() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Batr();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 歡迎光臨～請問有什麼事呢？
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "batr1"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (cmd.equalsIgnoreCase("request sapphire kiringku")) {// 提供希蓮恩的推薦書與特別的原石/兌換藍寶石奇古獸
			/*
			 * 高品質藍寶石3個 特別的原石 49205 希蓮恩的推薦書 49181
			 */
			final int[] items = new int[] { 40054, 49205, 49181 };
			final int[] counts = new int[] { 3, 1, 1 };
			final int[] gitems = new int[] { 270 };
			final int[] gcounts = new int[] { 1 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 高品質藍寶石</font>3個
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "batr4"));

			} else {// 需要物件充足
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予藍寶石奇古獸 270
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("request obsidian kiringku")) {// 製作黑曜石奇古獸
			/*
			 * 高品質鑽石10個 高品質紅寶石10個 高品質藍寶石10個 高品質綠寶石10個 龜裂之核2個 原石碎片30個 精靈粉末30個
			 * 金幣1000000個
			 */
			final int[] items = new int[] { 40052, 40053, 40054, 40055, 40520, 49092, 40308 };
			final int[] counts = new int[] { 10, 10, 10, 10, 30, 2, 1000000 };
			final int[] gitems = new int[] { 271 };
			final int[] gcounts = new int[] { 1 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				isCloseList = true;

			} else {// 需要物件充足
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				isCloseList = true;
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
