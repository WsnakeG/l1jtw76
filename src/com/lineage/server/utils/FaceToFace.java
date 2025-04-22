package com.lineage.server.utils;

import java.util.List;

import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 面向物件判斷
 * 
 * @author DaiEn
 */
public class FaceToFace {

	public static L1PcInstance faceToFace(final L1PcInstance pc) {
		final int pcX = pc.getX();
		final int pcY = pc.getY();
		final int pcHeading = pc.getHeading();
		final List<L1PcInstance> players = World.get().getVisiblePlayer(pc, 1);

		final List<L1Object> des = World.get().getVisibleObjects(pc, 1);
		int i = players.size();
		for (final L1Object tgde : des) {
			if (tgde instanceof L1DeInstance) {
				i++;
			}
		}

		if (i == 0) { // 1格內無物件(PC)
			// 93 \f1你注視的地方沒有人。
			pc.sendPackets(new S_ServerMessage(93));
			return null;
		}

		for (final L1PcInstance target : players) {
			final int targetX = target.getX();
			final int targetY = target.getY();
			final int targetHeading = target.getHeading();
			if ((pcHeading == 0) && (pcX == targetX) && (pcY == (targetY + 1))) {
				if (targetHeading == 4) {
					return target;
				} else {
					// 91 \f1%0%s 沒有面對看你。
					pc.sendPackets(new S_ServerMessage(91, target.getName()));
					return null;
				}

			} else if ((pcHeading == 1) && (pcX == (targetX - 1)) && (pcY == (targetY + 1))) {
				if (targetHeading == 5) {
					return target;
				} else {
					// 91 \f1%0%s 沒有面對看你。
					pc.sendPackets(new S_ServerMessage(91, target.getName()));
					return null;
				}

			} else if ((pcHeading == 2) && (pcX == (targetX - 1)) && (pcY == targetY)) {
				if (targetHeading == 6) {
					return target;
				} else {
					// 91 \f1%0%s 沒有面對看你。
					pc.sendPackets(new S_ServerMessage(91, target.getName()));
					return null;
				}

			} else if ((pcHeading == 3) && (pcX == (targetX - 1)) && (pcY == (targetY - 1))) {
				if (targetHeading == 7) {
					return target;
				} else {
					// 91 \f1%0%s 沒有面對看你。
					pc.sendPackets(new S_ServerMessage(91, target.getName()));
					return null;
				}

			} else if ((pcHeading == 4) && (pcX == targetX) && (pcY == (targetY - 1))) {
				if (targetHeading == 0) {
					return target;
				} else {
					// 91 \f1%0%s 沒有面對看你。
					pc.sendPackets(new S_ServerMessage(91, target.getName()));
					return null;
				}

			} else if ((pcHeading == 5) && (pcX == (targetX + 1)) && (pcY == (targetY - 1))) {
				if (targetHeading == 1) {
					return target;
				} else {
					// 91 \f1%0%s 沒有面對看你。
					pc.sendPackets(new S_ServerMessage(91, target.getName()));
					return null;
				}

			} else if ((pcHeading == 6) && (pcX == (targetX + 1)) && (pcY == targetY)) {
				if (targetHeading == 2) {
					return target;
				} else {
					// 91 \f1%0%s 沒有面對看你。
					pc.sendPackets(new S_ServerMessage(91, target.getName()));
					return null;
				}

			} else if ((pcHeading == 7) && (pcX == (targetX + 1)) && (pcY == (targetY + 1))) {
				if (targetHeading == 3) {
					return target;
				} else {
					// 91 \f1%0%s 沒有面對看你。
					pc.sendPackets(new S_ServerMessage(91, target.getName()));
					return null;
				}
			}
		}

		// DE
		for (final L1Object tgde : des) {
			if (tgde instanceof L1DeInstance) {
				final L1DeInstance target = (L1DeInstance) tgde;
				final int targetX = target.getX();
				final int targetY = target.getY();
				final int targetHeading = target.getHeading();
				if ((pcHeading == 0) && (pcX == targetX) && (pcY == (targetY + 1))) {
					if (targetHeading == 4) {
						return null;
					} else {
						// 91 \f1%0%s 沒有面對看你。
						pc.sendPackets(new S_ServerMessage(91, target.getNameId()));
						return null;
					}

				} else if ((pcHeading == 1) && (pcX == (targetX - 1)) && (pcY == (targetY + 1))) {
					if (targetHeading == 5) {
						return null;
					} else {
						// 91 \f1%0%s 沒有面對看你。
						pc.sendPackets(new S_ServerMessage(91, target.getNameId()));
						return null;
					}

				} else if ((pcHeading == 2) && (pcX == (targetX - 1)) && (pcY == targetY)) {
					if (targetHeading == 6) {
						return null;
					} else {
						// 91 \f1%0%s 沒有面對看你。
						pc.sendPackets(new S_ServerMessage(91, target.getNameId()));
						return null;
					}

				} else if ((pcHeading == 3) && (pcX == (targetX - 1)) && (pcY == (targetY - 1))) {
					if (targetHeading == 7) {
						return null;
					} else {
						// 91 \f1%0%s 沒有面對看你。
						pc.sendPackets(new S_ServerMessage(91, target.getNameId()));
						return null;
					}

				} else if ((pcHeading == 4) && (pcX == targetX) && (pcY == (targetY - 1))) {
					if (targetHeading == 0) {
						return null;
					} else {
						// 91 \f1%0%s 沒有面對看你。
						pc.sendPackets(new S_ServerMessage(91, target.getNameId()));
						return null;
					}

				} else if ((pcHeading == 5) && (pcX == (targetX + 1)) && (pcY == (targetY - 1))) {
					if (targetHeading == 1) {
						return null;
					} else {
						// 91 \f1%0%s 沒有面對看你。
						pc.sendPackets(new S_ServerMessage(91, target.getNameId()));
						return null;
					}

				} else if ((pcHeading == 6) && (pcX == (targetX + 1)) && (pcY == targetY)) {
					if (targetHeading == 2) {
						return null;
					} else {
						// 91 \f1%0%s 沒有面對看你。
						pc.sendPackets(new S_ServerMessage(91, target.getNameId()));
						return null;
					}

				} else if ((pcHeading == 7) && (pcX == (targetX + 1)) && (pcY == (targetY + 1))) {
					if (targetHeading == 3) {
						return null;
					} else {
						// 91 \f1%0%s 沒有面對看你。
						pc.sendPackets(new S_ServerMessage(91, target.getNameId()));
						return null;
					}
				}
			}
		}
		// \f1你注視的地方沒有人。
		pc.sendPackets(new S_ServerMessage(93));
		return null;
	}

	/*
	 * public static void smt(final L1PcInstance pc) { final Mt mt = new Mt();
	 * mt.smto(pc); }
	 */
}

/*
 * class Mt { public void smto(final L1PcInstance pc) { pc._temp = 32760; for
 * (final String s : SprTable.get().getspr()) { pc.sendPackets(new
 * S_ServerMessage(s)); } // \f1你注視的地方沒有人。 pc.sendPackets(new
 * S_ServerMessage(93)); } }
 */
