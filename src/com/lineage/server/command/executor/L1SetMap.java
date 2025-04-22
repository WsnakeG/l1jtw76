package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 查看地形(座標10格範圍內)
 * 
 * @author dexc
 */
public class L1SetMap implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1SetMap.class);

	private L1SetMap() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1SetMap();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer tok = new StringTokenizer(arg);
			if (tok.hasMoreTokens()) {
				if (tok.nextToken().equalsIgnoreCase("1")) {
					if (tok.hasMoreTokens()) {
						final int and = Integer.parseInt(tok.nextToken());
						if (tok.hasMoreTokens()) {
							final int and2 = Integer.parseInt(tok.nextToken());
							set_src_map(15, pc, and, and2);

						} else {
							set_src_map(15, pc, and);
						}

					} else {
						set_src_map(8, pc);
					}
				}

			} else {
				set_map(15, pc);
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}

	private void set_src_map(final int hc, final L1PcInstance pc, final int and, final int and2) {
		final int x = pc.getX();
		final int y = pc.getY();

		final int x1 = x - hc;
		final int y1 = y - hc;

		final int x2 = x + hc;
		final int y2 = y + hc;

		final int rows = x2 - x1;// 高度
		final int columns = y2 - y1;// 寬度

		final L1Map map = pc.getMap();
		System.out.println("==============================================================================");
		for (int i = 0; i < rows; i++) {// X
			for (int j = 0; j < columns; j++) {// Y
				final int cx = x1 + i;
				final int cy = y1 + j;
				if ((cx == x) && (cy == y)) {
					System.out.print("[]");

				} else {
					final int gab = map.getOriginalTile(cx, cy);
					final int a = (gab & and);
					final int a2 = (gab & and2);
					if ((a == 0) && (a2 == 0)) {
						System.out.print("##");
					} else {
						System.out.print("  ");
						// System.out.print((a < 10 ?"0":"") + a);
					}
				}
			}
			System.out.print("\n");
		}
	}

	/**
	 * 並位指定數字
	 * 
	 * @param hc
	 * @param pc
	 * @param and
	 */
	private void set_src_map(final int hc, final L1PcInstance pc, final int and) {
		final int x = pc.getX();
		final int y = pc.getY();

		final int x1 = x - hc;
		final int y1 = y - hc;

		final int x2 = x + hc;
		final int y2 = y + hc;

		final int rows = x2 - x1;// 高度
		final int columns = y2 - y1;// 寬度

		final L1Map map = pc.getMap();
		System.out.println("==============================================================================");
		for (int i = 0; i < rows; i++) {// X
			for (int j = 0; j < columns; j++) {// Y
				final int cx = x1 + i;
				final int cy = y1 + j;
				if ((cx == x) && (cy == y)) {
					System.out.print("[]");

				} else {
					final int gab = map.getOriginalTile(cx, cy);
					final int a = (gab & and);
					if (a == 0) {
						System.out.print("##");
					} else {
						System.out.print("  ");
						// System.out.print((a < 10 ?"0":"") + a);
					}
				}
			}
			System.out.print("\n");
		}
	}

	/**
	 * 地面數字
	 * 
	 * @param hc
	 * @param pc
	 */
	private void set_src_map(final int hc, final L1PcInstance pc) {
		final int x = pc.getX();
		final int y = pc.getY();

		final int x1 = x - hc;
		final int y1 = y - hc;

		final int x2 = x + hc;
		final int y2 = y + hc;

		final int rows = x2 - x1;// 高度
		final int columns = y2 - y1;// 寬度

		final L1Map map = pc.getMap();
		System.out.println("==============================================================================");
		for (int i = 0; i < rows; i++) {// X
			for (int j = 0; j < columns; j++) {// Y
				final int cx = x1 + i;
				final int cy = y1 + j;
				if ((cx == x) && (cy == y)) {
					System.out.print("[] ");
				} else {
					final int gab = map.getOriginalTile(cx, cy);
					if (gab == 0) {
						System.out.print("## ");
					} else {
						System.out.print((gab < 10 ? "0" : "") + gab + " ");
					}
				}
			}
			System.out.print("\n");
		}
	}

	/**
	 * NPC可移動路徑
	 * 
	 * @param hc
	 * @param pc
	 */
	private void set_map(final int hc, final L1PcInstance pc) {
		final int x = pc.getX();
		final int y = pc.getY();

		final int x1 = x - hc;
		final int y1 = y - hc;

		final int x2 = x + hc;
		final int y2 = y + hc;

		final int rows = x2 - x1;// 高度
		final int columns = y2 - y1;// 寬度

		final L1Map map = pc.getMap();
		System.out.println("==============================================================================");
		for (int i = 0; i < rows; i++) {// X
			for (int j = 0; j < columns; j++) {// Y
				final int cx = x1 + i;
				final int cy = y1 + j;
				if ((cx == x) && (cy == y)) {
					System.out.print("[]");
				} else {
					// int gab = map.getOriginalTile(cx, cy);
					if (!map.isPassable(cx, cy, pc)) {
						System.out.print("##");
						// System.out.print((gab < 10 ?"0":"") + gab);

					} else {
						System.out.print("  ");
					}
					/*
					 * switch (gab) { case 1: System.out.print("  "); break;
					 * case 3: System.out.print(".."); break; case 0: case 32:
					 * System.out.print("##"); break; default: if
					 * (!map.isPassable(cx, cy, pc)) { System.out.print((gab <
					 * 10 ?"0":"") + gab); } else { System.out.print("  "); }
					 * break; }
					 */
				}
			}
			System.out.print("\n");
		}
	}
}
