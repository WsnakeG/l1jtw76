package com.lineage.data.item_etcitem.wand;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.command.GmHtml;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.DeAiThreadPool;
import com.lineage.server.world.World;

/**
 * <font color=#00800>GM用指揮棒</font><BR>
 * 41214 DELETE FROM `etcitem` WHERE `item_id`='41214'; INSERT INTO `etcitem`
 * VALUES (41214, 'GM用指揮棒', 'wand.GM_Blink', 'GM用指揮棒', 'wand', 'spell_long',
 * 'none', 0, 118, 3963, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1);
 * 
 * @author dexc
 */
public class GM_Blink extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(GM_Blink.class);

	/**
	 *
	 */
	private GM_Blink() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new GM_Blink();
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
		try {
			// 對象OBJID
			final int targObjId = data[0];

			final L1Object target = World.get().findObject(targObjId);
			if (target == null) {
				final L1DeInstance de = pc.get_outChat();
				if (de != null) {
					final int spellsc_x = data[1];
					final int spellsc_y = data[2];
					if (de.isShop()) {
						pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "正在商店模式"));
						return;
					}
					if (de.isFishing()) {
						pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "正在釣魚模式"));
						return;
					}
					if (_list.get(de.getId()) != null) {
						pc.sendPackets(new S_ServerMessage("該NPC還未完成上一次移動命令"));
						return;
					}
					if ((de.getX() != spellsc_x) && (de.getY() != spellsc_y)) {
						final MoveTimer moveTimer = new MoveTimer(de, spellsc_x, spellsc_y);
						moveTimer.start();
					}

				} else {
					// 79 沒有任何事情發生
					pc.sendPackets(new S_ServerMessage(79));
				}
				return;
			}

			if (target instanceof L1PcInstance) {
				final L1PcInstance tgpc = (L1PcInstance) target;
				if (tgpc.equals(pc)) {
					pc.set_outChat(null);
					pc.sendPackets(new S_ServerMessage("\\fY解除控制"));
					// 解除GM管理狀態
					pc.get_other().set_gmHtml(null);
					return;
				}
			}

			if (target instanceof L1DeInstance) {
				final L1DeInstance de = (L1DeInstance) target;
				pc.set_outChat(de);// 設置控制對象
				pc.sendPackets(new S_ServerMessage("\\fY控制對象:" + de.getNameId()));

				final GmHtml gmHtml = new GmHtml(pc);
				gmHtml.show(de);
				return;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static final Map<Integer, L1DeInstance> _list = new HashMap<Integer, L1DeInstance>();

	private class MoveTimer implements Runnable {

		private final L1DeInstance _de;

		private final int _spellsc_x;

		private final int _spellsc_y;

		private MoveTimer(final L1DeInstance de, final int spellsc_x, final int spellsc_y) {
			_list.put(de.getId(), de);
			_de = de;
			_spellsc_x = spellsc_x;
			_spellsc_y = spellsc_y;
		}

		private void start() {
			DeAiThreadPool.get().execute(this);
		}

		@Override
		public void run() {
			try {
				int i = 10;
				while ((_de.getX() != _spellsc_x) && (_de.getY() != _spellsc_y)) {
					if (_de == null) {
						_list.remove(_de.getId());
						break;
					}
					final int moveDirection = _de.getMove().moveDirection(_spellsc_x, _spellsc_y);
					final int dir = _de.getMove().checkObject(moveDirection);

					if (dir != -1) {
						_de.getMove().setDirectionMove(dir);
						_de.setNpcSpeed();
					}
					Thread.sleep(_de.calcSleepTime(_de.getPassispeed(), 0));
					i--;
					if (i <= 0) {
						_list.remove(_de.getId());
						break;
					}
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				_list.remove(_de.getId());
			}
		}
	}
}
