package com.lineage.server.timecontroller.server;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.event.CardSet;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.datatables.sql.CharItemsTimeTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldItem;
import com.lineage.server.world.WorldPet;

/**
 * 物品使用期限計時軸
 * 
 * @author terry0412
 */
public class ServerItemUserTimer extends TimerTask {

	private static final Log _log = LogFactory
			.getLog(ServerItemUserTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 60 * 1000; // 1分鐘
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis,
				timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1ItemInstance> items = WorldItem.get().all();
			// 不包含元素
			if (items.isEmpty()) {
				return;
			}

			// 目前時間
			final Timestamp ts = new Timestamp(System.currentTimeMillis());

			for (final Iterator<L1ItemInstance> iter = items.iterator(); iter
					.hasNext();) {
				final L1ItemInstance item = iter.next();
				// 不具備使用期限 忽略 2014/08/05 by Roy 修復物品刪除BUG(不等於null改為等於null)
				if ((item.get_time() == null) && (item.get_card_use() == 2)) { // 到期
					continue;
				}
				if ((item.get_time() == null) && (item.get_card_use() == 1)) { // 使用中
					if (CardSet.START) {
						check_card_item(item, ts);
					}

				} else {
					// 魔法武器DIY系統(附魔時限) by terry0412
					if ((item.get_magic_weapon() != null)
							&& (item.get_magic_weapon().getMaxUseTime() > 0)) {
						final Timestamp date = item.get_time();
						if ((date != null) && date.before(ts)) {
							checkItem2(item, ts);
							continue;
						}
					}
					// 擁有使用期限
					if ((item.get_time() != null) && item.get_time().before(ts)) {
						checkItem(item, ts);
					}
				}
				Thread.sleep(5);
			}

		} catch (final Exception e) {
			_log.error("物品使用期限計時時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final ServerItemUserTimer userTimer = new ServerItemUserTimer();
			userTimer.start();
		}
	}

	private static void check_card_item(final L1ItemInstance item,
			final Timestamp ts) {
		try {
			if (item.get_time().before(ts)) {
				item.setEquipped(false);
				final L1Object object = World.get().findObject(
						item.get_char_objid());
				if (object != null) {
					final L1PcInstance pc = (L1PcInstance) object;
					item.set_card_use(2);

					CardSet.remove_card_mode(pc, item);
					pc.sendPackets(new S_ItemName(item));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static void checkItem(final L1ItemInstance item, final Timestamp ts)
			throws Exception {
		try {
			// 指示此 time 对象是否早于给定的 ts 对象。
			if (item.get_time().before(ts)) {
				final L1Object object = World.get().findObject(
						item.get_char_objid());
				if (object != null) {
					if (object instanceof L1PcInstance) {
						final L1PcInstance tgpc = (L1PcInstance) object;
						// pandora
						if (item.get_pandora_type() > 0) {
							if (item.isEquipped()) {
								item.set_pandora_buff(tgpc, false);
								if (item.get_pandora_mark() > 0) {
									item.set_pandora_markbuff(tgpc, false);
								}
							}
							item.set_pandora_type(tgpc, -1);
							item.set_pandora_mark(tgpc, -1);
							CharItemsReading.get().updateItemPandoraType(item);
							return;
						}
						// 160828 新增孵化系統 by Roy
						if (item.getItemId() == 5000600
								|| item.getItemId() == 5000601
								|| item.getItemId() == 5000602
								|| item.getItemId() == 5000603
								|| item.getItemId() == 5000604
								|| item.getItemId() == 5000605) {
							CreateNewItem.createNewItem(tgpc, 5000605, 1);
						}
						// 刪除物品
						tgpc.getInventory().removeItem(item);

						final L1Pet pet = PetReading.get().getTemplate(
								item.getId());
						if (pet != null) {
							final L1PetInstance tgpet = WorldPet.get().get(
									pet.get_objid());
							if (tgpet != null) {
								tgpet.dropItem();
								tgpet.deleteMe();
							}
						}
					}

				} else {
					// pandora
					if (item.get_pandora_type() > 0) {
						item.set_pandora_type(null, -1);
						item.set_pandora_mark(null, -1);
						CharItemsReading.get().updateItemPandoraType(item);
						return;
					}
					// 人物不在線上 刪除物品
					CharItemsReading.get().deleteItem(item.get_char_objid(),
							item);
					World.get().removeObject(item); // 2014/08/05 by Roy
													// 修復物品刪除BUG
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 檢查道具是否已超過時限2 超過則移除附魔效果 [魔法武器DIY系統(DB自製)]
	 * 
	 * @param item
	 * @param ts
	 * @throws Exception
	 */
	private static void checkItem2(final L1ItemInstance item, final Timestamp ts)
			throws Exception {
		item.set_magic_weapon(null);
		item.set_time(null);
		CharItemsTimeTable.delete(item.getId());
		// 取得道具主人
		final L1Object object = World.get().findObject(item.get_char_objid());
		if (object != null) {
			if (object instanceof L1PcInstance) {
				final L1PcInstance tgpc = (L1PcInstance) object;
				// 更新道具狀態
				tgpc.sendPackets(new S_ItemStatus(item));
			}
		}
	}
}
