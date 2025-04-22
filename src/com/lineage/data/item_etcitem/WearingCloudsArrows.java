package com.lineage.data.item_etcitem;

import java.util.Collection;
import java.util.Map;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1TeleportLoc;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;

public class WearingCloudsArrows extends ItemExecutor {

	private WearingCloudsArrows() {

	}

	public static ItemExecutor get() {
		return new WearingCloudsArrows();
	}

	/**
	 * 開始執行
	 */
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {

		if (!pc.getMap().isArrows()) {
			pc.sendPackets(new S_ServerMessage("該地圖無法使用。"));
			return;
		}
		int castleId = 0;
		boolean isNowWar = false;
		castleId = L1CastleLocation.getCastleIdByArea(pc);
		if (castleId != 0) { // 戰爭範圍旗幟內城堡ID
			isNowWar = ServerWarExecutor.get().isNowWar(castleId);
		}

		if (isNowWar) {
			pc.sendPackets(new S_ServerMessage("城戰旗幟內無法使用."));
			return;
		}
		
		switch (_kind) {
		case 0: // 血盟穿雲箭
			if (pc.getClanid() == 0) {
				pc.sendPackets(new S_ServerMessage("未加入血盟無法使用."));
				return;
			}

			for (L1PcInstance tgpc : World.get().getAllPlayers()) {
				if (tgpc.getId() == pc.getId()) {
					continue;
				}
				if (tgpc.getClanid() == 0) {
					continue;
				}
				if(tgpc.get_showWearingCloudsArrows() == false){
					continue;
				}
				if (tgpc.getClanid() == pc.getClanid()) {
					tgpc.setTempID(pc.getId());
					tgpc.sendPackets(new S_Message_YN(4774, pc.getName()));
				}
			}

			pc.sendPacketsAll(new S_SkillSound(pc.getId(), 6780));
			final Collection<L1PcInstance> all = World.get().getAllPlayers();
			for (final L1PcInstance _tgpc : all) {
				_tgpc.sendPackets(new S_ServerMessage("\\fA系統訊息:\\fR[" + pc.getName() + "]\\fN使用了血盟穿雲箭,請[\\fR" + pc.getClanname() + "\\fN]血盟成員速度前往支援!!"));
			}
			pc.getInventory().removeItem(item, 1);
			break;
		case 1: // 隊伍穿雲箭

			final L1Party party = pc.getParty();
			if (party == null) {
				pc.sendPackets(new S_ServerMessage("你當前沒有隊伍。"));
				return;
			}

			for (L1PcInstance tgpc : World.get().getAllPlayers()) {
				if (tgpc.getId() == pc.getId()) {
					continue;
				}
				if(tgpc.get_showWearingCloudsArrows() == false){
					continue;
				}
				if (party.isMember(tgpc)) {
					tgpc.setTempID(pc.getId());
					tgpc.sendPackets(new S_Message_YN(4774, pc.getName()));
				}
			}

			pc.sendPacketsAll(new S_SkillSound(pc.getId(), 6780));
			final Collection<L1PcInstance> pratyall = World.get().getAllPlayers();
			for (final L1PcInstance _tgpc : pratyall) {
				_tgpc.sendPackets(new S_ServerMessage("\\f3系統訊息:\\fR[" + pc.getName() + "]\\fN使用了隊伍穿雲箭,請隊伍成員迅速前往支援!!"));
			}
			pc.getInventory().removeItem(item, 1);
		}

	}

	// 種類
	private int _kind;

	@Override
	public void set_set(final String[] set) {
		try {
			_kind = Integer.parseInt(set[1]);

			if (_kind > 1) {
				_kind = 1;
			}

		} catch (final Exception e) {
		}
	}
}