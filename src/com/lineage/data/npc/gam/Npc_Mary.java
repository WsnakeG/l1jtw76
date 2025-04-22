package com.lineage.data.npc.gam;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.Shutdown;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.MaryReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 小瑪莉管理員<BR>
 * id:無作用 all_stake:累積賭注 all_user_prize:累積中獎金額 out_prize:輸出彩金百分比 count:已使用次數
 * _x_a:BAR _x_b:半瓜 _x_c:蘋果 _x_d:西瓜 _x_e:香蕉 _x_f:檸檬 _x_g:橘子 (1:大的2:小的)
 * 
 * @author daien
 */
public class Npc_Mary extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Mary.class);

	private static final Random _random = new Random();

	private static int _itemid = L1ItemId.ADENA;

	private static int _count;

	// BAR
	private static int _x_a1 = 100;

	private static int _x_a2 = 50;

	// 半瓜
	private static int _x_b1 = 40;

	private static int _x_b2 = 2;

	// 蘋果
	private static int _x_c1 = 30;

	private static int _x_c2 = 2;

	// 西瓜
	private static int _x_d1 = 20;

	private static int _x_d2 = 2;

	// 香蕉
	private static int _x_e1 = 15;

	private static int _x_e2 = 2;

	// 檸檬
	private static int _x_f1 = 10;

	private static int _x_f2 = 2;

	// 橘子
	private static int _x_g1 = 5;

	private static int _x_g2 = 2;

	private static int _out_prize; // 輸出彩金百分比

	private static long _all_stake; // 累積賭注

	private static long _all_user_prize; // 累積中獎金額

	public static void set_itemid(final int itemid) {
		_itemid = itemid;
	}

	public static void set_count(final int count) {
		_count = count;
	}

	public static void set_x_a1(final int x_a1) {
		_x_a1 = x_a1;
	}

	public static void set_x_a2(final int x_a2) {
		_x_a2 = x_a2;
	}

	public static void set_x_b1(final int x_b1) {
		_x_b1 = x_b1;
	}

	public static void set_x_b2(final int x_b2) {
		_x_b2 = x_b2;
	}

	public static void set_x_c1(final int x_c1) {
		_x_c1 = x_c1;
	}

	public static void set_x_c2(final int x_c2) {
		_x_c2 = x_c2;
	}

	public static void set_x_d1(final int x_d1) {
		_x_d1 = x_d1;
	}

	public static void set_x_d2(final int x_d2) {
		_x_d2 = x_d2;
	}

	public static void set_x_e1(final int x_e1) {
		_x_e1 = x_e1;
	}

	public static void set_x_e2(final int x_e2) {
		_x_e2 = x_e2;
	}

	public static void set_x_f1(final int x_f1) {
		_x_f1 = x_f1;
	}

	public static void set_x_f2(final int x_f2) {
		_x_f2 = x_f2;
	}

	public static void set_x_g1(final int x_g1) {
		_x_g1 = x_g1;
	}

	public static void set_x_g2(final int x_g2) {
		_x_g2 = x_g2;
	}

	public static void set_out_prize(final int out_prize) {
		_out_prize = out_prize;
	}

	public static void set_all_stake(final long all_stake) {
		_all_stake = all_stake;
	}

	public static void set_all_user_prize(final long all_user_prize) {
		_all_user_prize = all_user_prize;
	}

	public static void update() {
		MaryReading.get().update(_all_stake, _all_user_prize, _count);
	}

	// 人物OBJID/賭場紀錄
	private static final Map<Integer, MaryTemp> _maryUsers = new HashMap<Integer, MaryTemp>();

	private class MaryTemp {
		private long _prize_all = 0; // 本次彩金暫存
		private int _count = 0; // 累計
		private int _x_a = 0; // BAR
		private int _x_b = 0; // 半瓜
		private int _x_c = 0; // 蘋果
		private int _x_d = 0; // 西瓜
		private int _x_e = 0; // 香蕉
		private int _x_f = 0; // 檸檬
		private int _x_g = 0; // 橘子
		private boolean _x_h_i = false; // true:大 false:小
		private int _x_h_i_count = 0; // 比大小連續勝利次數
		private long _prize = 0; // 獎金
		private boolean _stop = false; // true:按鈕禁用 false:按鈕可用
		private int _x10 = 1; // 10:一次下注10 1:一次下注1
		// TEMP
		private int _x_a_t = 0; // BAR
		private int _x_b_t = 0; // 半瓜
		private int _x_c_t = 0; // 蘋果
		private int _x_d_t = 0; // 西瓜
		private int _x_e_t = 0; // 香蕉
		private int _x_f_t = 0; // 檸檬
		private int _x_g_t = 0; // 橘子
	}

	/**
	 *
	 */
	private Npc_Mary() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 計算可輸出彩金
	 */
	private static long all_prize() {
		final long out = (_all_stake * _out_prize) / 100; // 取回輸出彩金比率
		final long out_prize = out - _all_user_prize; // 剩餘可輸出賠率
		if (out_prize <= 0) {
			return 0;
		}
		final long out_prize1 = (int) (out_prize * 0.5D); // 剩餘可輸出彩金
		if (_random.nextInt(100) <= 10) {
			return out_prize1 + _random.nextInt(20);
		}
		return out_prize1;
	}

	public static NpcExecutor get() {
		return new Npc_Mary();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		_log.warn("小瑪莉管理員 紀錄 輸出百分比:" + _out_prize + "% 累積賭注:" + _all_stake + " 累積中獎金額:" + _all_user_prize);
		MaryTemp tmp = _maryUsers.get(pc.getId());
		if (tmp == null) {
			tmp = new MaryTemp();
			_maryUsers.put(pc.getId(), tmp);
		}
		tmp._x_a = 0; // BAR
		tmp._x_b = 0; // 半瓜
		tmp._x_c = 0; // 蘋果
		tmp._x_d = 0; // 西瓜
		tmp._x_e = 0; // 香蕉
		tmp._x_f = 0; // 檸檬
		tmp._x_g = 0; // 橘子
		// TEMP
		tmp._x_a_t = 0; // BAR
		tmp._x_b_t = 0; // 半瓜
		tmp._x_c_t = 0; // 蘋果
		tmp._x_d_t = 0; // 西瓜
		tmp._x_e_t = 0; // 香蕉
		tmp._x_f_t = 0; // 檸檬
		tmp._x_g_t = 0; // 橘子

		tmp._x_h_i = false; // true:大 false:小
		// 計算彩金
		tmp._prize_all = all_prize(); // 計算彩金
		// 開場動畫
		final MaryTimer maryTimer = new MaryTimer(pc, npc, 0, -1);
		maryTimer.startCmd();
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		MaryTemp tmp = _maryUsers.get(pc.getId());
		if (tmp == null) {
			tmp = new MaryTemp();
		}

		if (tmp._stop) { // 按鈕禁用
			return;
		}
		if (cmd.equalsIgnoreCase("start")) { // 開始
			if (Shutdown.SHUTDOWN) {
				pc.sendPackets(new S_ServerMessage(166, "系統暫停接受下注，請儘快取回您的獎金"));
				return;
			}
			final long down = tmp._x_a + tmp._x_b + tmp._x_c + tmp._x_d + tmp._x_e + tmp._x_f + tmp._x_g;
			if (down != 0) {
				final L1ItemInstance tgitem = pc.getInventory().checkItemX(_itemid, down);
				if (tgitem != null) {
					final MaryTimer maryTimer = new MaryTimer(pc, npc, 1, -1);
					maryTimer.startCmd();
					tmp._count = tmp._count + 1;

				} else {
					// 原始物件資料
					final L1Item tgItem = ItemTable.get().getTemplate(_itemid);
					// \f1%0不足%s。
					pc.sendPackets(new S_ServerMessage(337, tgItem.getNameId()));
					// 關閉對話窗
					pc.sendPackets(new S_CloseList(pc.getId()));
				}
			} else {
				pc.sendPackets(new S_ServerMessage("\\fR您還沒有下注!!"));
			}
			return;

		} else if (cmd.equalsIgnoreCase("re")) { // 使用上次的設置
			tmp._x_a = tmp._x_a_t; // BAR
			tmp._x_b = tmp._x_b_t; // 半瓜
			tmp._x_c = tmp._x_c_t; // 蘋果
			tmp._x_d = tmp._x_d_t; // 西瓜
			tmp._x_e = tmp._x_e_t; // 香蕉
			tmp._x_f = tmp._x_f_t; // 檸檬
			tmp._x_g = tmp._x_g_t; // 橘子

		} else if (cmd.equalsIgnoreCase("get")) { // 取款
			if (tmp._prize != 0) {
				_all_user_prize += tmp._prize; // 紀錄輸出獎金數量
				_log.warn("小瑪莉管理員 紀錄：" + pc.getName() + " 領取獎金(" + tmp._prize + ")");
				// 給錢
				CreateNewItem.createNewItem(pc, _itemid, tmp._prize);
				tmp._prize = 0;
			}
			// TEMP
			tmp._x_a_t = tmp._x_a; // BAR
			tmp._x_b_t = tmp._x_b; // 半瓜
			tmp._x_c_t = tmp._x_c; // 蘋果
			tmp._x_d_t = tmp._x_d; // 西瓜
			tmp._x_e_t = tmp._x_e; // 香蕉
			tmp._x_f_t = tmp._x_f; // 檸檬
			tmp._x_g_t = tmp._x_g; // 橘子

			tmp._x_a = 0; // BAR
			tmp._x_b = 0; // 半瓜
			tmp._x_c = 0; // 蘋果
			tmp._x_d = 0; // 西瓜
			tmp._x_e = 0; // 香蕉
			tmp._x_f = 0; // 檸檬
			tmp._x_g = 0; // 橘子
			tmp._x_h_i = false; // true:大 false:小
			tmp._prize = 0;

			final long tgitem_count = pc.getInventory().countItems(_itemid);
			tmp._prize_all = all_prize(); // 計算彩金
			final String[] info = new String[] { "資本:" + tgitem_count + "  彩金:" + tmp._prize_all, "00", "00",
					"00", "00", "00", "00", "00", (tmp._x10 == 1 ? "一次下注10點" : "一次下注1點") };
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bar_00", info));
			return;

		} else if (cmd.equalsIgnoreCase("a")) { // BAR
			tmp._x_a += (1 * tmp._x10);

		} else if (cmd.equalsIgnoreCase("b")) { // 半瓜
			tmp._x_b += (1 * tmp._x10);

		} else if (cmd.equalsIgnoreCase("c")) { // 蘋果
			tmp._x_c += (1 * tmp._x10);

		} else if (cmd.equalsIgnoreCase("d")) { // 西瓜
			tmp._x_d += (1 * tmp._x10);

		} else if (cmd.equalsIgnoreCase("e")) { // 香蕉
			tmp._x_e += (1 * tmp._x10);

		} else if (cmd.equalsIgnoreCase("f")) { // 檸檬
			tmp._x_f += (1 * tmp._x10);

		} else if (cmd.equalsIgnoreCase("g")) { // 橘子
			tmp._x_g += (1 * tmp._x10);

		} else if (cmd.equalsIgnoreCase("XL99")) { // XL99
			tmp._x_a += 99;
			tmp._x_b += 99;
			tmp._x_c += 99;
			tmp._x_d += 99;
			tmp._x_e += 99;
			tmp._x_f += 99;
			tmp._x_g += 99;

		} else if (cmd.equalsIgnoreCase("XL50")) { // XL50
			tmp._x_a += 50;
			tmp._x_b += 50;
			tmp._x_c += 50;
			tmp._x_d += 50;
			tmp._x_e += 50;
			tmp._x_f += 50;
			tmp._x_g += 50;

		} else if (cmd.equalsIgnoreCase("XL10")) {// XL10
			tmp._x_a += 10;
			tmp._x_b += 10;
			tmp._x_c += 10;
			tmp._x_d += 10;
			tmp._x_e += 10;
			tmp._x_f += 10;
			tmp._x_g += 10;

		} else if (cmd.equalsIgnoreCase("XL05")) { // XL05
			tmp._x_a += 5;
			tmp._x_b += 5;
			tmp._x_c += 5;
			tmp._x_d += 5;
			tmp._x_e += 5;
			tmp._x_f += 5;
			tmp._x_g += 5;

		} else if (cmd.equalsIgnoreCase("XL01")) { // XL01
			tmp._x_a += 1;
			tmp._x_b += 1;
			tmp._x_c += 1;
			tmp._x_d += 1;
			tmp._x_e += 1;
			tmp._x_f += 1;
			tmp._x_g += 1;

		} else if (cmd.equalsIgnoreCase("h")) { // 大
			if (Shutdown.SHUTDOWN) {
				pc.sendPackets(new S_ServerMessage(166, "系統暫停接受下注，請儘快取回您的獎金"));
				return;
			}
			if (tmp._prize <= 0) {
				return;
			}
			if (tmp._x_h_i_count >= 5) { // 比大小最多只能連勝五次
				pc.sendPackets(new S_ServerMessage("\\fS比大小最多只能連勝五次!"));
				return;
			}
			tmp._x_h_i = true;
			final MaryTimer maryTimer = new MaryTimer(pc, npc, 2, -1);
			maryTimer.startCmd();
			return;

		} else if (cmd.equalsIgnoreCase("i")) { // 小
			if (Shutdown.SHUTDOWN) {
				pc.sendPackets(new S_ServerMessage(166, "系統暫停接受下注，請儘快取回您的獎金"));
				return;
			}
			if (tmp._prize <= 0) {
				return;
			}
			if (tmp._x_h_i_count >= 5) { // 比大小最多只能連勝五次
				pc.sendPackets(new S_ServerMessage("\\fS比大小最多只能連勝五次!"));
				return;
			}
			tmp._x_h_i = false;
			final MaryTimer maryTimer = new MaryTimer(pc, npc, 2, -1);
			maryTimer.startCmd();
			return;

		} else if (cmd.equalsIgnoreCase("x10")) { // 一次下注10 / 一次下注1
			if (tmp._x10 == 1) {
				tmp._x10 = 10;
			} else {
				tmp._x10 = 1;
			}

		} else if (cmd.equalsIgnoreCase("HL")) { // 一次下注10 / 一次下注1
			final String[] info = new String[] {
					// BAR
					String.valueOf(_x_a1), String.valueOf(_x_a2),
					// 半瓜
					String.valueOf(_x_b1), String.valueOf(_x_b2),
					// 蘋果
					String.valueOf(_x_c1), String.valueOf(_x_c2),
					// 西瓜
					String.valueOf(_x_d1), String.valueOf(_x_d2),
					// 香蕉
					String.valueOf(_x_e1), String.valueOf(_x_e2),
					// 檸檬
					String.valueOf(_x_f1), String.valueOf(_x_f2),
					// 橘子
					String.valueOf(_x_g1), String.valueOf(_x_g2), };
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bar_hl", info));
			return;
		}

		if (tmp._x_a > 99) {
			tmp._x_a = 99;
		}
		if (tmp._x_b > 99) {
			tmp._x_b = 99;
		}
		if (tmp._x_c > 99) {
			tmp._x_c = 99;
		}
		if (tmp._x_d > 99) {
			tmp._x_d = 99;
		}
		if (tmp._x_e > 99) {
			tmp._x_e = 99;
		}
		if (tmp._x_f > 99) {
			tmp._x_f = 99;
		}
		if (tmp._x_g > 99) {
			tmp._x_g = 99;
		}
		final long tgitem_count = pc.getInventory().countItems(_itemid);
		tmp._prize_all = all_prize(); // 計算彩金
		final String[] info = new String[] { "資本:" + tgitem_count + "  彩金:" + tmp._prize_all,
				tmp._x_a < 10 ? String.valueOf("0" + tmp._x_a) : String.valueOf(tmp._x_a),
				tmp._x_b < 10 ? String.valueOf("0" + tmp._x_b) : String.valueOf(tmp._x_b),
				tmp._x_c < 10 ? String.valueOf("0" + tmp._x_c) : String.valueOf(tmp._x_c),
				tmp._x_d < 10 ? String.valueOf("0" + tmp._x_d) : String.valueOf(tmp._x_d),
				tmp._x_e < 10 ? String.valueOf("0" + tmp._x_e) : String.valueOf(tmp._x_e),
				tmp._x_f < 10 ? String.valueOf("0" + tmp._x_f) : String.valueOf(tmp._x_f),
				tmp._x_g < 10 ? String.valueOf("0" + tmp._x_g) : String.valueOf(tmp._x_g),
				(tmp._x10 == 1 ? "一次下注10點" : "一次下注1點") };
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bar_00", info));
	}

	// 最小值
	private static final int[] _min = new int[] { 2, // 小半瓜
			4, // 小西瓜
			6, // 小香蕉
			7, // 紅色ONCE MORE
			10, // 小橘子
			12, // 小蘋果
			14, // 小檸檬
			15, // 藍色ONCE MORE
	};

	private class MaryTimer implements Runnable {

		private final L1PcInstance _pc;

		private final L1NpcInstance _npc;

		private final int _mode;

		private final MaryTemp _tmp;

		private int _html_id = -1;

		public MaryTimer(final L1PcInstance pc, final L1NpcInstance npc, final int mode, final int src_html) {
			_pc = pc;
			_npc = npc;
			_mode = mode;
			_tmp = _maryUsers.get(pc.getId());
			_tmp._stop = true; // 按鈕禁用
			_html_id = src_html;
		}

		public void startCmd() {
			GeneralThreadPool.get().schedule(this, 10);
		}

		@Override
		public void run() {
			int src_html = -1;
			try {
				if (_tmp == null) {
					_log.error("小瑪莉管理員 異常:找不到 人物" + _pc.getName() + " 的小瑪莉記錄!");
					return;
				}
				// 人物身上賭金
				final long tgitem_count = _pc.getInventory().countItems(_itemid);

				// String[] info = null;
				switch (_mode) {
				case 0: // 開場
					if (_tmp._prize != 0) {
						_pc.sendPackets(new S_ServerMessage("\\fT未領取彩金(" + _tmp._prize + ")，現在發還給您"));
						_all_user_prize += _tmp._prize; // 紀錄輸出獎金數量
						_log.warn("小瑪莉管理員 紀錄：" + _pc.getName() + " 發還未領取獎金(" + _tmp._prize + ")");
						// 給錢
						CreateNewItem.createNewItem(_pc, _itemid, _tmp._prize);
						_tmp._prize = 0;
					}
					_tmp._prize_all = all_prize(); // 計算彩金
					final String[] info0 = new String[] { "資本:" + tgitem_count + "  彩金:" + _tmp._prize_all,
							"00", "00", "00", "00", "00", "00", "00", (_tmp._x10 == 1 ? "一次下注10" : "一次下注1") };
					for (int i = 6; i > 0; i--) {
						Thread.sleep(250);
						if ((i % 2) == 0) {
							_pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), "bar_00", info0));
						} else {
							_pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), "bar_20", info0));
						}
					}
					break;

				case 1: // 轉盤
					_count++;
					_tmp._x_h_i_count = 0; // 比大小連續勝利次數規零
					// 計算下注額
					final long count = _tmp._x_a + _tmp._x_b + _tmp._x_c + _tmp._x_d + _tmp._x_e + _tmp._x_f
							+ _tmp._x_g;
					if (_html_id == -1) {
						final L1ItemInstance tgitem = _pc.getInventory().checkItemX(_itemid, count);
						if (tgitem != null) {
							_all_stake += count; // 計算賭金總收入
							// 扣除
							_pc.getInventory().removeItem(tgitem.getId(), count);

						} else {
							// 原始物件資料
							final L1Item tgItem = ItemTable.get().getTemplate(_itemid);
							// \f1%0不足%s。
							_pc.sendPackets(new S_ServerMessage(337, tgItem.getNameId()));
							// 關閉對話窗
							_pc.sendPackets(new S_CloseList(_pc.getId()));
							break;
						}
					}

					_tmp._prize_all = all_prize(); // 計算彩金
					final String[] info1 = new String[] {
							"資本:" + (tgitem_count - count) + "  彩金:" + _tmp._prize_all,
							_tmp._x_a < 10 ? String.valueOf("0" + _tmp._x_a) : String.valueOf(_tmp._x_a),
							_tmp._x_b < 10 ? String.valueOf("0" + _tmp._x_b) : String.valueOf(_tmp._x_b),
							_tmp._x_c < 10 ? String.valueOf("0" + _tmp._x_c) : String.valueOf(_tmp._x_c),
							_tmp._x_d < 10 ? String.valueOf("0" + _tmp._x_d) : String.valueOf(_tmp._x_d),
							_tmp._x_e < 10 ? String.valueOf("0" + _tmp._x_e) : String.valueOf(_tmp._x_e),
							_tmp._x_f < 10 ? String.valueOf("0" + _tmp._x_f) : String.valueOf(_tmp._x_f),
							_tmp._x_g < 10 ? String.valueOf("0" + _tmp._x_g) : String.valueOf(_tmp._x_g), };

					int index = 1; // 轉盤HTML代號
					final int win = check(_tmp._prize_all); // 預計停下的次數
					int count1 = 32 + win;// 次數
					if (_html_id != -1) {
						if (_html_id == 15) { // 藍色ONCE MORE
							count1 = count1 + 2; // 次數

						} else if (_html_id == 7) { // 紅色ONCE MORE
							count1 = count1 + 10; // 次數
						}
						index = _html_id;
					}

					int html_id = -1;
					for (int i = 0; i < count1; i++) {
						Thread.sleep(i * 3);
						html_id = index;
						// _pc.sendPackets(new S_Sound(9990)); // XXX 音效
						_pc.sendPackets(new S_NPCTalkReturn(_npc.getId(),
								"bar_" + (html_id < 10 ? "0" + html_id : html_id), info1));
						index++;
						if (index > 16) {
							index = 1;
						}
					}

					int add = 0;
					String out = "";
					switch (html_id) {
					case 1: // 大半瓜
						add += _tmp._x_b * _x_b1;
						out = "(大半瓜:" + html_id + " 下注:" + _tmp._x_b + ")";
						break;
					case 2: // 小半瓜
						add += _tmp._x_b * _x_b2;
						out = "(小半瓜:" + html_id + " 下注:" + _tmp._x_b + ")";
						break;
					case 3: // 大BAR
						add += _tmp._x_a * _x_a1;
						out = "(大BAR:" + html_id + " 下注:" + _tmp._x_a + ")";
						break;
					case 4: // 小西瓜
						add += _tmp._x_d * _x_d2;
						out = "(小西瓜:" + html_id + " 下注:" + _tmp._x_d + ")";
						break;
					case 5: // 大西瓜
						add += _tmp._x_d * _x_d1;
						out = "(大西瓜:" + html_id + " 下注:" + _tmp._x_d + ")";
						break;
					case 6: // 小香蕉
						add += _tmp._x_e * _x_e2;
						out = "(小香蕉:" + html_id + " 下注:" + _tmp._x_e + ")";
						break;
					case 7: // 紅色ONCE MORE
						src_html = html_id;
						Thread.sleep(1000);
						break;
					case 8: // 大檸檬
						add += _tmp._x_f * _x_f1;
						out = "(大檸檬:" + html_id + " 下注:" + _tmp._x_f + ")";
						break;
					case 9: // 大橘子
						add += _tmp._x_g * _x_g1;
						out = "(大橘子:" + html_id + " 下注:" + _tmp._x_g + ")";
						break;
					case 10: // 小橘子
						add += _tmp._x_g * _x_g2;
						out = "(小橘子:" + html_id + " 下注:" + _tmp._x_g + ")";
						break;
					case 11: // 小BAR
						add += _tmp._x_a * _x_a2;
						out = "(小BAR:" + html_id + " 下注:" + _tmp._x_a + ")";
						break;
					case 12: // 小蘋果
						add += _tmp._x_c * _x_c2;
						out = "(小蘋果:" + html_id + " 下注:" + _tmp._x_c + ")";
						break;
					case 13: // 大蘋果
						add += _tmp._x_c * _x_c1;
						out = "(大蘋果:" + html_id + " 下注:" + _tmp._x_c + ")";
						break;
					case 14: // 小檸檬
						add += _tmp._x_f * _x_f2;
						out = "(小檸檬:" + html_id + " 下注:" + _tmp._x_f + ")";
						break;
					case 15: // 藍色ONCE MORE
						src_html = html_id;
						Thread.sleep(1000);
						break;
					case 16: // 大香蕉
						add += _tmp._x_e * _x_e1;
						out = "(大香蕉:" + html_id + " 下注:" + _tmp._x_e + ")";
						break;
					}
					if (src_html != -1) {
						break;
					}

					_tmp._prize += add; // 計算獎金
					_tmp._prize_all = all_prize(); // 計算彩金

					if (add > 0) {
						String bar = "";
						if ((_tmp._x_a == 99) && (html_id == 3)) {
							_tmp._prize += _tmp._prize_all; // 加入彩金
							bar = " 拉彩金:" + _tmp._prize_all;
							_pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 5763));
							// 服務器公告 XXX
							World.get().broadcastPacketToAll(new S_ServerMessage(
									"\\fV恭喜玩家" + _pc.getName() + "BAR拉彩金 獎金:" + _tmp._prize));
						}
						_log.warn("小瑪莉管理員 紀錄：" + _pc.getName() + " 下注:" + count + " 中獎:" + add + " 當時彩金:"
								+ _tmp._prize_all + "下注細項:" + _tmp._x_a + "/" + _tmp._x_b + "/" + _tmp._x_c
								+ "/" + _tmp._x_d + "/" + _tmp._x_e + "/" + _tmp._x_f + "/" + _tmp._x_g
								+ " 開出" + out + bar + " (" + _count + ")");
					} else {
						_log.warn("小瑪莉管理員 紀錄：" + _pc.getName() + " 下注:" + count + " 未中獎!" + " 當時彩金:"
								+ _tmp._prize_all + "下注細項:" + _tmp._x_a + "/" + _tmp._x_b + "/" + _tmp._x_c
								+ "/" + _tmp._x_d + "/" + _tmp._x_e + "/" + _tmp._x_f + "/" + _tmp._x_g
								+ " 開出" + out + " (" + _count + ")");
					}

					final String[] info1_1 = new String[] { "得獎:" + _tmp._prize + "  彩金:" + _tmp._prize_all,
							"00", "00", "00", "00", "00", "00", "00" };
					_pc.sendPackets(new S_NPCTalkReturn(_npc.getId(),
							"bar_" + (html_id < 10 ? "0" + html_id : html_id), info1_1));
					_pc.sendPackets(new S_ServerMessage(166,
							"下注" + _tmp._x_a + "/" + _tmp._x_b + "/" + _tmp._x_c + "/" + _tmp._x_d + "/"
									+ _tmp._x_e + "/" + _tmp._x_f + "/" + _tmp._x_g + " 開出:" + out + " ("
									+ _count + ")"));
					break;

				case 2: // 大小
					int count2 = 0; // 是否讓玩家贏 (20次出小/19次出大)
					String hi = "";
					// 預設不給玩家贏
					if (_tmp._x_h_i) { // true: user選大
						hi = "您目前選大 ";
						count2 = 20;
					} else { // false: user選小
						hi = "您目前選小 ";
						count2 = 19;
					}

					final String[] info2 = new String[] { hi + "得獎:" + _tmp._prize, "00", "00", "00", "00",
							"00", "00", "00" };

					final int random = random();
					if (random != 0) {
						final int r = (int) (Math.random() * 1000) + 1;
						if (r < random) {// 機率決定
							if (_tmp._x_h_i) {// true: user選大
								count2 = 19;
							} else {// false: user選小
								count2 = 20;
							}
						}
					}

					// _pc.sendPackets(new S_Sound(9991)); // XXX 音效

					for (int i = 0; i < count2; i++) {
						Thread.sleep(i * 15);
						if ((i % 2) == 0) {
							_pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), "bar_18", info2));// 大
						} else {
							_pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), "bar_19", info2));// 小
						}
					}

					String htmlid = "";
					if (_tmp._x_h_i) { // true: user選大
						switch (count2) {
						case 19: // 19次出大
							_tmp._prize *= 2;
							htmlid = "bar_21";
							hi = "您贏了 ";
							_tmp._x_h_i_count += 1;
							switch (_tmp._x_h_i_count) {
							case 1:
							case 2:
								_pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7476));
								break;
							case 3:
							case 4:
								_pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7473));
								break;
							}
							break;
						case 20: // 20次出小
							_tmp._prize = 0;
							htmlid = "bar_19";
							hi = "您輸了 ";
							break;
						}

					} else if (!_tmp._x_h_i) { // false: user選小
						switch (count2) {
						case 19: // 19次出大
							_tmp._prize = 0;
							htmlid = "bar_18";
							hi = "您輸了 ";
							break;
						case 20: // 20次出小
							_tmp._prize *= 2;
							htmlid = "bar_22";
							hi = "您贏了 ";
							_tmp._x_h_i_count += 1;
							switch (_tmp._x_h_i_count) {
							case 1:
							case 2:
								_pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7476));
								break;
							case 3:
							case 4:
								_pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7473));
								break;
							}
							break;
						}
					}

					if (_tmp._prize > 0) {
						if (_tmp._x_h_i_count >= 5) {
							_tmp._prize *= 2;
							_pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7470));
							// 服務器公告 XXX
							World.get().broadcastPacketToAll(
									new S_ServerMessage("\\fV恭喜玩家" + _pc.getName() + "比大小過五關"));
						}
						_log.warn("小瑪莉管理員 紀錄：" + _pc.getName() + " 比大小獲勝 金額:" + _tmp._prize + " 次數:"
								+ _tmp._x_h_i_count + " (" + _count + ")");

					} else {
						_log.warn("小瑪莉管理員 紀錄：" + _pc.getName() + " 比大小失敗 次數:" + _tmp._x_h_i_count + " ("
								+ _count + ")");
					}

					final String[] info2_1 = new String[] { hi + "得獎:" + _tmp._prize, "00", "00", "00", "00",
							"00", "00", "00" };
					_pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), htmlid, info2_1));
					_pc.sendPackets(new S_ServerMessage("\\fT比大小"
							+ (_tmp._prize > 0 ? "獲勝 獎金:" + _tmp._prize : "失敗") + " (" + _count + ")"));
					break;
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				_tmp._stop = false; // 按鈕可用
				if (src_html != -1) {
					final MaryTimer maryTimer = new MaryTimer(_pc, _npc, 1, src_html);
					maryTimer.startCmd();
				}
			}
		}

		/*
		 * 計算大小機率規則 先取回可輸出彩金比率 如此得到可放出的金額((累積賭注 * 輸出獎金百分比) / 100) 將可放出的金額 減去
		 * 累積中獎金額 得到剩餘可輸出獎金 當可輸出獎金小於等於0 5%機率大小可以獲勝 下注金額 X 2 超過可放出的金額 獲勝機率是0%
		 * 通過上述條件 計算機率 基礎計算公式(((10000 - (((賭金 * 10000) + 50000) / 10000)) * 30)
		 * / 1000); 當比大小獲勝次數為0 維持機率 當比大小獲勝次數為1 機率-20 當比大小獲勝次數為2 機率-40 當比大小獲勝次數為3
		 * 機率-60 當比大小獲勝次數為4 機率-80 獎金 * 2 之後 小於等於 可輸出獎金1/10 機率提高200~400之間
		 * 最後輸出機率(1/1000)
		 */

		// 計算大小機率
		private int random() {
			try {
				// 取回輸出彩金比率
				final long out_prize1 = (_all_stake * _out_prize) / 100;
				// 剩餘可輸出賠率
				final long out_prize2 = out_prize1 - _all_user_prize;
				if (out_prize2 <= 0) {
					if (_random.nextInt(100) < 5) {
						return 1000;
					}
					return 0;
				}
				if ((_tmp._prize << 1) > out_prize2) {
					return 0;
				}
				// 調整 *30 這個數字可以加減基礎機率
				int lostRate = (int) (((10000 - (((_tmp._prize * 10000) + 50000) / 10000)) * 30) / 1000);
				switch (_tmp._x_h_i_count) { // 大小勝利次數
				case 0://
					break;
				case 1://
					lostRate -= 20;
					break;
				case 2://
					lostRate -= 40;
					break;
				case 3://
					lostRate -= 60;
					break;
				case 4://
					lostRate -= 80;
					break;
				case 5://
					lostRate -= 100;
					break;
				}

				// 獎金 * 2 之後 小於等於 可輸出獎金 1/10
				if ((_tmp._prize << 1) <= (out_prize2 / 10)) {
					final int add = (lostRate + 200 + _random.nextInt(200));
					return add; // 提高機率
				}
				return lostRate;

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
				return 0;
			}
		}

		/*
		 * 取回可放出的金額 剩餘可輸出金額 = 將可放出的金額 減去 累積中獎金額 在減去 本次彩金金額 得到剩餘可輸出獎金
		 * 當計算獲得的可輸出獎金小於等於0時 輸出小質 2,// 小半瓜 4,// 小西瓜 6,// 小香蕉 7,// 紅色ONCE MORE
		 * 10,// 小橘子 12,// 小蘋果 14,// 小檸檬 15,// 藍色ONCE MORE 通過上述條件 隨機取出1~16的停止次數
		 * 數字 1:// 大半瓜 賭金 * 賠率 < 剩餘可輸出金額 通過計算 否則2%機率通過計算 數字 2:// 小半瓜 通過計算 數字
		 * 3:// 大BAR 賭金 * 賠率 < 剩餘可輸出金額 假設下注金額99 5%機率獲得彩金 否則通過計算 上述均不成立否則0%機率通過計算
		 * 數字 4:// 小西瓜 通過計算 數字 5:// 大西瓜 賭金 * 賠率 < 剩餘可輸出金額 通過計算 否則6%機率通過計算 數字
		 * 6:// 小香蕉 通過計算 數字 7:// 紅色ONCE MORE 通過計算 數字 8:// 大檸檬 賭金 * 賠率 < 剩餘可輸出金額
		 * 通過計算 否則8%機率通過計算 數字 9:// 大橘子 賭金 * 賠率 < 剩餘可輸出金額 通過計算 否則15%機率通過計算 數字
		 * 10:// 小橘子 通過計算 數字 11:// 小BAR 賭金 * 賠率 < 剩餘可輸出金額 通過計算 數字 12:// 小蘋果 通過計算
		 * 數字 13:// 大蘋果 賭金 * 賠率 < 剩餘可輸出金額 通過計算 否則4%機率通過計算 數字 14:// 小檸檬 通過計算 數字
		 * 15:// 藍色ONCE MORE 通過計算 數字 16:// 大香蕉 賭金 * 賠率 < 剩餘可輸出金額 通過計算 否則6%機率通過計算
		 * 上述條件回圈50次都無法成立 輸出小質 2,// 小半瓜 4,// 小西瓜 6,// 小香蕉 7,// 紅色ONCE MORE 10,//
		 * 小橘子 12,// 小蘋果 14,// 小檸檬 15,// 藍色ONCE MORE
		 */

		// 計算預計停下的次數
		private int check(final long prize_all) {
			try {
				// 取回輸出彩金比率
				final long out_prize1 = (_all_stake * _out_prize) / 100;
				// 剩餘可輸出賠率(扣除彩金)
				final long out_prize2 = (out_prize1 - _all_user_prize) - prize_all;
				if (out_prize2 <= 0) {
					return _min[_random.nextInt(_min.length)]; // 輸出小值
				}

				boolean is_out = false;
				int html_id = _min[_random.nextInt(_min.length)]; // 預計停下的次數
				int count = 0;
				while (!is_out) {
					count++;
					html_id = (_random.nextInt(16) + 1); // 預計停下的次數
					switch (html_id) {
					case 1: // 大半瓜
						if ((_tmp._x_b * _x_b1) < out_prize2) {
							is_out = true;
						} else {
							if (_random.nextInt(100) <= 2) {
								is_out = true;
							}
						}
						break;
					case 2: // 小半瓜
						is_out = true;
						break;
					case 3: // 大BAR
						if ((_tmp._x_a * _x_a1) < out_prize2) {
							if (_tmp._x_a == 99) {
								if (_random.nextInt(100) <= 5) {
									is_out = true;
								}

							} else {
								is_out = true;
							}
						}
						break;
					case 4: // 小西瓜
						is_out = true;
						break;
					case 5: // 大西瓜
						if ((_tmp._x_d * _x_d1) < out_prize2) {
							is_out = true;
						} else {
							if (_random.nextInt(100) <= 6) {
								is_out = true;
							}
						}
						break;
					case 6: // 小香蕉
						is_out = true;
						break;
					case 7: // 紅色ONCE MORE
						is_out = true;
						break;
					case 8: // 大檸檬
						if ((_tmp._x_f * _x_f1) < out_prize2) {
							is_out = true;
						} else {
							if (_random.nextInt(100) <= 10) {
								is_out = true;
							}
						}
						break;
					case 9: // 大橘子
						if ((_tmp._x_g * _x_g1) < out_prize2) {
							is_out = true;
						} else {
							if (_random.nextInt(100) <= 15) {
								is_out = true;
							}
						}
						break;
					case 10: // 小橘子
						is_out = true;
						break;
					case 11: // 小BAR
						if ((_tmp._x_a * _x_a2) < out_prize2) {
							is_out = true;
						}
						break;
					case 12: // 小蘋果
						is_out = true;
						break;
					case 13: // 大蘋果
						if ((_tmp._x_c * _x_c1) < out_prize2) {
							is_out = true;
						} else {
							if (_random.nextInt(100) <= 4) {
								is_out = true;
							}
						}
						break;
					case 14: // 小檸檬
						is_out = true;
						break;
					case 15: // 藍色ONCE MORE
						is_out = true;
						break;
					case 16: // 大香蕉
						if ((_tmp._x_e * _x_e1) < out_prize2) {
							is_out = true;
						} else {
							if (_random.nextInt(100) <= 8) {
								is_out = true;
							}
						}
						break;
					}
					if (count >= 50) {
						break;
					}
				}
				if (is_out) {
					return html_id; // 輸出
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
			return _min[_random.nextInt(_min.length)]; // 輸出小值
		}
	}
}
