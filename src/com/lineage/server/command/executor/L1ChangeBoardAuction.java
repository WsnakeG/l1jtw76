package com.lineage.server.command.executor;

import java.util.Calendar;
import java.util.TimeZone;

import com.lineage.config.Config;
import com.lineage.server.datatables.lock.AuctionBoardReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1AuctionBoardTmp;
import com.lineage.server.world.World;

/**
 * GM指令：線上修改盟屋結標時間 usage：.endauction 10 (血盟小屋將在 10 分鐘後結標。)
 * Author：a6572517@yahoo.com.tw
 */

public class L1ChangeBoardAuction implements L1CommandExecutor {
	private L1ChangeBoardAuction() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ChangeBoardAuction();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final Calendar DeadTime = getRealTime();
			DeadTime.add(Calendar.MINUTE, Integer.parseInt(arg));

			for (final L1AuctionBoardTmp board : AuctionBoardReading.get().getAuctionBoardTableList()
					.values()) {
				board.setDeadline(DeadTime);
				AuctionBoardReading.get().updateAuctionBoard(board);
			}

			World.get().broadcastPacketToAll(new S_SystemMessage("血盟小屋將在" + arg + "分鐘後結標，需要購買的請盡快下標。"));

		} catch (final Exception e) {
			pc.sendPackets(new S_SystemMessage("請輸入 " + cmdName + " 分鐘數"));
		}
	}

	private Calendar getRealTime() {
		final TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		final Calendar cal = Calendar.getInstance(tz);
		return cal;
	}
}
