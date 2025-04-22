package com.lineage.data.npc.quest2;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigRate;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_Teleport;

/**
 * @author erics4179 競技場活動傳送
 */
public class Npc_dftactivity extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_dftactivity.class);

	private Npc_dftactivity() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_dftactivity();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.getLawful() < -1000) {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dftactivity5"));
				return;
			}
			final int day_of_week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			if (day_of_week == 1 // 星期日
					|| day_of_week == 2 // 星期一
					|| day_of_week == 3 // 星期二
					|| day_of_week == 4 // 星期三
					|| day_of_week == 5 // 星期四
					|| day_of_week == 6 // 星期五
					|| day_of_week == 7 // 星期六
			) {
				// 可以花金幣傳送入場
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dftactivity4"));

			} else { // 平常日
				// 只能透過沙塵暴龍捲風隨機傳送入場
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dftactivity"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("a")) { // 觀察沙蟲領域
			// 消耗金幣 (2000 * 掉落倍率)
			if (!pc.getInventory().consumeItem(L1ItemId.ADENA, (long) (1000 * ConfigRate.RATE_DROP_ADENA))) {
				// 金幣不夠
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dftactivity1"));
				return;
			}
			try {
				pc.save();
			} catch (final Exception e) {
				e.printStackTrace();
			}

			// 傳送至附近 5格範圍內
			// final L1Location loc =
			// new L1Location(32751, 33173, 4).randomLocation(5, true);

			// 進入觀察模式 - 持續 300秒
			pc.beginGhost(32814, 32792, (short) 526, true, 300);

		} else if (cmd.equalsIgnoreCase("1")) { // 前往競技活動場地
			// 取得週幾之值
			final int day_of_week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			if (day_of_week == 1 // 星期日
					|| day_of_week == 2 // 星期一
					|| day_of_week == 3 // 星期二
					|| day_of_week == 4 // 星期三
					|| day_of_week == 5 // 星期四
					|| day_of_week == 6 // 星期五
					|| day_of_week == 7 // 星期六
			) {
				// 消耗金幣 (10000 * 掉落倍率)
				if (!pc.getInventory().consumeItem(L1ItemId.ADENA,
						(long) (50000 * ConfigRate.RATE_DROP_ADENA))) {
					// 金幣不夠
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dftactivity1"));
					return;
				}

				// 傳送至附近 5格範圍內
				// final L1Location loc =
				// new L1Location(32751, 33173, 4).randomLocation(5, true);

				// 傳送鎖定 (有動畫) by terry0412
				pc.setTeleportX(32814);
				pc.setTeleportY(32792);
				pc.setTeleportMapId((short) 526);
				// 送出鎖定封包
				pc.sendPackets(new S_Teleport(pc));
			}
		}
	}
}
