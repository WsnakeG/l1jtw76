package com.lineage.server.model.skill.skillmode;

import java.util.List;

import com.lineage.server.datatables.lock.CharBookReading;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1BookMark;
import com.lineage.server.world.World;

/**
 * 集體傳送術69
 * 
 * @author dexc
 */
public class MASS_TELEPORT extends SkillMode {

	public MASS_TELEPORT() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;// magic.calcMagicDamage(L1SkillId.CURE_POISON);
		final L1PcInstance pc = (L1PcInstance) cha;
		final L1BookMark bookm = CharBookReading.get().getBookMark(pc, integer);
		if (bookm != null) { // 記憶座標取出
			if (pc.getMap().isEscapable() || pc.isGm()) {
				final int newX = bookm.getLocX();
				final int newY = bookm.getLocY();
				final short mapId = bookm.getMapId();

				final List<L1PcInstance> clanMember = World.get().getVisiblePlayer(pc);
				for (final L1PcInstance member : clanMember) {
					if ((pc.getLocation().getTileLineDistance(member.getLocation()) <= 3)
							&& (member.getClanid() == pc.getClanid()) && (pc.getClanid() != 0)
							&& (member.getId() != pc.getId())) {
						// 商店村模式
						if (!member.isPrivateShop()) {
							if (!member.is_ask_teleport()) {
								L1Teleport.teleport(member, newX, newY, mapId, 5, true);
							} else {
								member.setTeleportX(newX);
								member.setTeleportY(newY);
								member.setTeleportMapId(mapId);
								// 你的血盟成員想要傳送你。你答應嗎？(Y/N)
								member.sendPackets(new S_Message_YN(748));
							}
						}
					}
				}
				L1Teleport.teleport(pc, newX, newY, mapId, 5, true);

			} else {
				// 276 \f1在此無法使用傳送。
				pc.sendPackets(new S_ServerMessage(276));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
			}

		} else { // 任意地點
			if (pc.getMap().isTeleportable() || pc.isGm()) {
				final L1Location newLocation = pc.getLocation().randomLocation(200, true);
				final int newX = newLocation.getX();
				final int newY = newLocation.getY();
				final short mapId = (short) newLocation.getMapId();

				final List<L1PcInstance> clanMember = World.get().getVisiblePlayer(pc);
				for (final L1PcInstance member : clanMember) {
					if ((pc.getLocation().getTileLineDistance(member.getLocation()) <= 3)
							&& (member.getClanid() == pc.getClanid()) && (pc.getClanid() != 0)
							&& (member.getId() != pc.getId())) {
						// 商店村模式
						if (!member.isPrivateShop()) {
							if (!member.is_ask_teleport()) {
								L1Teleport.teleport(member, newX, newY, mapId, 5, true);
							} else {
								member.setTeleportX(newX);
								member.setTeleportY(newY);
								member.setTeleportMapId(mapId);
								// 你的血盟成員想要傳送你。你答應嗎？(Y/N)
								member.sendPackets(new S_Message_YN(748));
							}
						}
					}
				}
				L1Teleport.teleport(pc, newX, newY, mapId, 5, true);

			} else {
				// 276 \f1在此無法使用傳送。
				pc.sendPackets(new S_ServerMessage(276));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
			}
		}
		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;

		return dmg;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		// TODO Auto-generated method stub
	}
}
