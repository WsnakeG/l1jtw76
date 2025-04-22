package com.lineage.data.npc.other;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 侏儒鐵匠－庫普<BR>
 * 70904<BR>
 * 說明:糾正錯誤的觀念 (黑暗妖精45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Koup extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Koup.class);

	/**
	 *
	 */
	private Npc_Koup() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Koup();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 呵呵呵...我在這裡住了很久，好久沒見到黑暗妖精以外的種族了。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "koup2a"));

			} else if (pc.isKnight()) {// 騎士
				// 呵呵呵...我在這裡住了很久，好久沒見到黑暗妖精以外的種族了。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "koup2a"));

			} else if (pc.isElf()) {// 精靈
				// 呵呵呵...我在這裡住了很久，好久沒見到黑暗妖精以外的種族了。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "koup2a"));

			} else if (pc.isWizard()) {// 法師
				// 呵呵呵...我在這裡住了很久，好久沒見到黑暗妖精以外的種族了。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "koup2a"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 任務尚未開始
				if (!pc.getQuest().isStart(DarkElfLv45_1.QUEST.get_id())) {
					// 咦～像你們這樣尊貴的人怎麼會來找我，是不是有事情想要拜託我呢？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "koup1"));

				} else {// 任務已經開始
					// 又是"布魯迪卡"叫你來的嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "koup12"));
				}

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 呵呵呵...我在這裡住了很久，好久沒見到黑暗妖精以外的種族了。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "koup2a"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 呵呵呵...我在這裡住了很久，好久沒見到黑暗妖精以外的種族了。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "koup2a"));

			} else {
				// 呵呵呵...我在這裡住了很久，好久沒見到黑暗妖精以外的種族了。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "koup2a"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (cmd.equalsIgnoreCase("request dark dualblade")) {// 製作黑暗雙刀
			if (pc.isDarkelf()) {// 黑暗精靈
				final int[] items = new int[] { 40321, 40408, 40406 };
				final int[] counts = new int[] { 100, 10, 20 };
				final int[] gitems = new int[] { 75 };
				final int[] gcounts = new int[] { 1 };
				isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

			} else {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("request dark claw")) {// 製作黑暗鋼爪
			if (pc.isDarkelf()) {// 黑暗精靈
				final int[] items = new int[] { 40321, 40322, 40408, 40406 };
				final int[] counts = new int[] { 100, 5, 10, 10 };
				final int[] gitems = new int[] { 158 };
				final int[] gcounts = new int[] { 1 };
				isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

			} else {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("request dark crossbow")) {// 製作黑暗十字弓
			if (pc.isDarkelf()) {// 黑暗精靈
				final int[] items = new int[] { 40321, 40322, 40408, 40406 };
				final int[] counts = new int[] { 100, 10, 10, 30 };
				final int[] gitems = new int[] { 168 };
				final int[] gcounts = new int[] { 1 };
				isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

			} else {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("request blind dualblade")) {// 製作幽暗雙刀
			if (pc.isDarkelf()) {// 黑暗精靈
				final int[] items = new int[] { 75, 40408, 40406, 40322, 40323 };
				final int[] counts = new int[] { 1, 10, 20, 100, 5 };
				final int[] gitems = new int[] { 81 };
				final int[] gcounts = new int[] { 1 };
				isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

			} else {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("request blind claw")) {// 製造幽暗鋼爪
			if (pc.isDarkelf()) {// 黑暗精靈
				final int[] items = new int[] { 158, 40408, 40406, 40322, 40323 };
				final int[] counts = new int[] { 1, 10, 10, 100, 10 };
				final int[] gitems = new int[] { 162 };
				final int[] gcounts = new int[] { 1 };
				isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

			} else {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("request blind crossbow")) {// 製作幽暗十字弓
			if (pc.isDarkelf()) {// 黑暗精靈
				final int[] items = new int[] { 168, 40408, 40406, 40322, 40323 };
				final int[] counts = new int[] { 1, 10, 30, 100, 20 };
				final int[] gitems = new int[] { 177 };
				final int[] gcounts = new int[] { 1 };
				isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

			} else {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("request silver dualblade")) {// 製作銀光雙刀
			if (pc.isDarkelf()) {// 黑暗精靈
				final int[] items = new int[] { 75, 40408, 40406, 40321, 40323, 40044, 40467 };
				final int[] counts = new int[] { 1, 10, 20, 50, 1, 1, 20 };
				final int[] gitems = new int[] { 74 };
				final int[] gcounts = new int[] { 1 };
				isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

			} else {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("request silver claw")) {// 製作銀光鋼爪
			if (pc.isDarkelf()) {// 黑暗精靈
				final int[] items = new int[] { 158, 40408, 40406, 40321, 40323, 40044, 40467 };
				final int[] counts = new int[] { 1, 10, 10, 40, 1, 1, 30 };
				final int[] gitems = new int[] { 157 };
				final int[] gcounts = new int[] { 1 };
				isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

			} else {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("request black mithril")) {// 黑色米索莉
			final int[] items = new int[] { 40486, 40490, 40442, 40444, 40308 };
			final int[] counts = new int[] { 1, 1, 1, 5, 5000 };
			final int[] gitems = new int[] { 40443 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request black mithril arrow")) {// 黑色米索莉箭
			final int[] items = new int[] { 40507, 40443, 40440, 40308 };
			final int[] counts = new int[] { 10, 1, 1, 1000 };
			final int[] gitems = new int[] { 40747 };
			final int[] gcounts = new int[] { 5000 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request lump of steel")) {// 鋼鐵塊
			final int[] items = new int[] { 40899, 40408, 40308 };
			final int[] counts = new int[] { 5, 5, 500 };
			final int[] gitems = new int[] { 40779 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request silver bar")) {// 銀
			final int[] items = new int[] { 40468, 40308 };
			final int[] counts = new int[] { 10, 500 };
			final int[] gitems = new int[] { 40467 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request gold bar")) {// 黃金
			final int[] items = new int[] { 40489, 40308 };
			final int[] counts = new int[] { 10, 1000 };
			final int[] gitems = new int[] { 40488 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request platinum bar")) {// 白金
			final int[] items = new int[] { 40441, 40308 };
			final int[] counts = new int[] { 10, 5000 };
			final int[] gitems = new int[] { 40440 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request silver plate")) {// 銀金屬板
			final int[] items = new int[] { 40467, 40779, 40308 };
			final int[] counts = new int[] { 5, 3, 1000 };
			final int[] gitems = new int[] { 40469 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request gold plate")) {// 黃金金屬板
			final int[] items = new int[] { 40488, 40779, 40308 };
			final int[] counts = new int[] { 5, 3, 3000 };
			final int[] gitems = new int[] { 40487 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request platinum plate")) {// 白金金屬板
			final int[] items = new int[] { 40440, 40779, 40308 };
			final int[] counts = new int[] { 5, 3, 10000 };
			final int[] gitems = new int[] { 40439 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request black mithril plate")) {// 黑色米索莉金屬板
			final int[] items = new int[] { 40443, 40497, 40509, 40469, 40487, 40439, 40308 };
			final int[] counts = new int[] { 10, 1, 1, 1, 1, 1, 10000 };
			final int[] gitems = new int[] { 40445 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, amount);

		} else if (cmd.equalsIgnoreCase("request ancient blue dragon armor")) {// 請幫忙製作古代水龍鱗盔甲
			final int[] items = new int[] { 20127, 40445, 40051, 40413, 40308 };
			final int[] counts = new int[] { 1, 3, 30, 5, 50000 };
			final int[] gitems = new int[] { 20153 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

		} else if (cmd.equalsIgnoreCase("request ancient green dragon armor")) {// 製作古代地龍鱗盔甲
			final int[] items = new int[] { 20146, 40445, 40048, 40162, 40308 };
			final int[] counts = new int[] { 1, 3, 30, 5, 50000 };
			final int[] gitems = new int[] { 20130 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

		} else if (cmd.equalsIgnoreCase("request ancient red dragon armor")) {// 製作古代火龍鱗盔甲
			final int[] items = new int[] { 20159, 40445, 40049, 40409, 40308 };
			final int[] counts = new int[] { 1, 3, 30, 5, 50000 };
			final int[] gitems = new int[] { 20119 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);

		} else if (cmd.equalsIgnoreCase("request ancient azure dragon armor")) {// 製作古代風龍鱗盔甲
			final int[] items = new int[] { 20156, 40445, 40050, 40169, 40308 };
			final int[] counts = new int[] { 1, 3, 30, 5, 50000 };
			final int[] gitems = new int[] { 20108 };
			final int[] gcounts = new int[] { 1 };
			isCloseList = CreateNewItem.getItem(pc, npc, cmd, items, counts, gitems, gcounts, 1);
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
