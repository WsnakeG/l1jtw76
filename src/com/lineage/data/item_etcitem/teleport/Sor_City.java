package com.lineage.data.item_etcitem.teleport;

import java.util.Random;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>指定傳送卷軸(歌唱之島)</font><BR>
 * Scroll of Return - Singing Island<BR>
 * <font color=#00800>指定傳送卷軸(隱藏之谷)</font><BR>
 * Scroll of Return - Hidden Valley<BR>
 * <font color=#00800>說話之島指定傳送卷軸</font><BR>
 * Scroll of Return - Talking Island Village<BR>
 * <font color=#00800>古魯丁村莊指定傳送卷軸</font><BR>
 * Scroll of Return - Gludin Town<BR>
 * <font color=#00800>肯特村莊指定傳送卷軸</font><BR>
 * Scroll of Return - Kent Village<BR>
 * <font color=#00800>風木村莊指定傳送卷軸</font><BR>
 * Scroll of Return - Woodbec Village<BR>
 * <font color=#00800>燃柳村莊指定傳送卷軸</font><BR>
 * Scroll of Return - Orctown<BR>
 * <font color=#00800>妖森指定傳送卷軸</font><BR>
 * Scroll of Return - Elven Forest<BR>
 * <font color=#00800>銀騎士村莊指定傳送卷軸</font><BR>
 * Scroll of Return - Silver Knight Town<BR>
 * <font color=#00800>奇岩村莊指定傳送卷軸</font><BR>
 * Scroll of Return - Giran City<BR>
 * <font color=#00800>海音村莊指定傳送卷軸 </font><BR>
 * Scroll of Return - Heine City<BR>
 * <font color=#00800>歐瑞村莊指定送卷軸</font><BR>
 * Scroll of Return - Ivory Tower Town<BR>
 * <font color=#00800>威頓村莊指定傳送卷軸 </font><BR>
 * Scroll of Return - Werldern Town<BR>
 * <font color=#00800>亞丁村莊指定傳送卷軸</font><BR>
 * Scroll of Return - Aden City<BR>
 * <font color=#00800>沉默洞穴指定傳送卷軸</font><BR>
 * Scroll of Return - Silent Cavern<BR>
 * <font color=#00800>大洞穴移動卷軸</font><BR>
 * Scroll of Teleportation - Large Cave<BR>
 * <font color=#00800>抵抗軍村莊指定傳送卷軸</font><BR>
 * Scroll of Return - Resistance Village<BR>
 * <font color=#00800>隱遁者村莊指定傳送卷軸</font><BR>
 * Scroll of Return - Recluse Village<BR>
 * <font color=#00800>狄亞得移動卷軸</font><BR>
 * Scroll of Teleportation - Diad Fortress<BR>
 * 
 * @author dexc
 */
public class Sor_City extends ItemExecutor {

	private final Random _random = new Random();

	/**
	 *
	 */
	private Sor_City() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Sor_City();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {

		int[][] locs = null;

		final int itemId = item.getItemId();
		switch (itemId) {
		case 40082:// 指定傳送卷軸(歌唱之島)
			locs = new int[][] { new int[] { 32782, 32756, 68 }, new int[] { 32785, 32764, 68 },
					new int[] { 32771, 32761, 68 } };
			break;

		case 40101:// 指定傳送卷軸(隱藏之谷)
			locs = new int[][] { new int[] { 32673, 32856, 69 }, new int[] { 32684, 32853, 69 },
					new int[] { 32679, 32871, 69 } };
			break;

		case 40085:// 說話之島指定傳送卷軸
			locs = new int[][] { new int[] { 32580, 32931, 0 }, new int[] { 32586, 32942, 0 },
					new int[] { 32571, 32943, 0 } };
			break;

		case 40080:// 古魯丁村莊指定傳送卷軸
			locs = new int[][] { new int[] { 32612, 32734, 4 }, new int[] { 32602, 32734, 4 },
					new int[] { 32608, 32726, 4 } };
			break;

		case 40122:// 肯特村莊指定傳送卷軸
			locs = new int[][] { new int[] { 33050, 32780, 4 }, new int[] { 33059, 32785, 4 },
					new int[] { 33047, 32789, 4 } };
			break;

		case 40115:// 風木村莊指定傳送卷軸
			locs = new int[][] { new int[] { 32622, 33179, 4 }, new int[] { 32618, 33187, 4 },
					new int[] { 32606, 33187, 4 } };
			break;

		case 40125:// 燃柳村莊指定傳送卷軸
			locs = new int[][] { new int[] { 32715, 32448, 4 }, new int[] { 32732, 32459, 4 },
					new int[] { 32738, 32441, 4 } };
			break;

		case 40114:// 妖森指定傳送卷軸
			locs = new int[][] { new int[] { 32872, 32506, 4 }, new int[] { 32864, 32514, 4 },
					new int[] { 32876, 32513, 4 } };
			break;

		case 40117:// 銀騎士村莊指定傳送卷軸
			locs = new int[][] { new int[] { 33080, 33392, 4 }, new int[] { 33090, 33399, 4 },
					new int[] { 33073, 33407, 4 } };
			break;

		case 40081:// 奇岩村莊指定傳送卷軸
			locs = new int[][] { new int[] { 33442, 32797, 4 }, new int[] { 33446, 32807, 4 },
					new int[] { 33430, 32809, 4 } };
			break;

		case 40123:// 海音村莊指定傳送卷軸
			locs = new int[][] { new int[] { 33605, 33259, 4 }, new int[] { 33616, 33242, 4 },
					new int[] { 33602, 33236, 4 } };
			break;

		case 40103:// 歐瑞村莊指定送卷軸
			locs = new int[][] { new int[] { 34061, 32276, 4 }, new int[] { 34051, 32278, 4 },
					new int[] { 34051, 32293, 4 } };
			break;

		case 40116:// 威頓村莊指定傳送卷軸
			locs = new int[][] { new int[] { 33705, 32504, 4 }, new int[] { 33707, 32493, 4 },
					new int[] { 33718, 32504, 4 } };
			break;

		case 40102:// 亞丁村莊指定傳送卷軸
			locs = new int[][] { new int[] { 33966, 33253, 4 }, new int[] { 33973, 33246, 4 },
					new int[] { 33962, 33254, 4 } };
			break;

		case 40845:// 沉默洞穴指定傳送卷軸
			locs = new int[][] { new int[] { 32864, 32895, 304 }, };
			break;

		case 40083:// 大洞穴移動卷軸
			locs = new int[][] { new int[] { 32549, 32801, 400 }, };
			break;

		case 40120:// 抵抗軍村莊指定傳送卷軸
			locs = new int[][] { new int[] { 32571, 32673, 400 }, };
			break;

		case 40118:// 隱遁者村莊指定傳送卷軸
			locs = new int[][] { new int[] { 32601, 32908, 400 }, };
			break;

		case 40084:// 狄亞得移動卷軸
			locs = new int[][] { new int[] { 32723, 32851, 320 }, };
			break;
		}

		if (pc.getMap().isEscapable()) {
			if (locs != null) {
				final int[] loc = locs[_random.nextInt(locs.length)];
				// 刪除道具
				pc.getInventory().removeItem(item, 1);

				// 解除魔法技能绝对屏障
				L1BuffUtil.cancelAbsoluteBarrier(pc);

				L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);

			} else {
				// 刪除道具
				pc.getInventory().removeItem(item, 1);
				// 解除魔法技能绝对屏障
				L1BuffUtil.cancelAbsoluteBarrier(pc);
				L1Teleport.teleport(pc, 33080, 33392, (short) 4, 5, true);
			}

			// 該地圖不允許使用回捲
		} else {
			// 276 \f1在此無法使用傳送。
			pc.sendPackets(new S_ServerMessage(276));
			// 解除傳送鎖定
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		}
	}
}
