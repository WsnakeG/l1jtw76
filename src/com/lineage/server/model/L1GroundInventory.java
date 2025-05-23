package com.lineage.server.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCPack_Item;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.timecontroller.server.ServerDeleteItemTimer;
import com.lineage.server.world.World;

/**
 * 遊戲世界地面背包
 * 
 * @author dexc
 */
public class L1GroundInventory extends L1Inventory {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1GroundInventory.class);

	private void setTimer(final L1ItemInstance item) {
		try {
			if (item.getItemId() == 40515) { // 元素石
				return;
			}

			// 血盟小屋內不加入清單
			if (L1HouseLocation.isInHouse(getX(), getY(), getMapId())) {
				return;
			}

			if (!ServerDeleteItemTimer.contains(item)) {
				// 加入(更新)自動清除地面物件清單
				ServerDeleteItemTimer.add(item);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 建立世界地面背包
	 * 
	 * @param objectId
	 * @param x
	 * @param y
	 * @param map
	 * @param showid
	 */
	public L1GroundInventory(final int objectId, final int x, final int y, final short map) {
		try {
			setId(objectId);
			setX(x);
			setY(y);
			this.setMap(map);

			World.get().addVisibleObject(this);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 物件接觸處理
	 */
	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			for (final L1ItemInstance item : getItems()) {
				// 副本ID不相等 不相護顯示
				if (perceivedFrom.get_showId() != item.get_showId()) {
					continue;
				}
				if (!perceivedFrom.knownsObject(item)) {
					perceivedFrom.addKnownObject(item);
					perceivedFrom.sendPackets(new S_NPCPack_Item(item)); // 地面物件封包
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 物件接觸處理
	 */
	@Override
	public void insertItem(final L1ItemInstance item) {
		if (item.getCount() <= 0) {
			return;
		}
		try {
			setTimer(item);

			for (final L1PcInstance pc : World.get().getRecognizePlayer(item)) {
				// 副本ID不相等 不相護顯示
				if (pc.get_showId() != item.get_showId()) {
					continue;
				}
				pc.sendPackets(new S_NPCPack_Item(item));
				pc.addKnownObject(item);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 可件範圍物件更新
	 */
	@Override
	public void updateItem(final L1ItemInstance item) {
		try {
			for (final L1PcInstance pc : World.get().getRecognizePlayer(item)) {
				// 副本ID不相等 不相護顯示
				if (pc.get_showId() != item.get_showId()) {
					continue;
				}
				pc.sendPackets(new S_NPCPack_Item(item));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 空インベントリ破棄及び見える範囲内にいるプレイヤーのオブジェクト削除
	@Override
	public void deleteItem(final L1ItemInstance item) {
		try {
			for (final L1PcInstance pc : World.get().getRecognizePlayer(item)) {
				pc.sendPackets(new S_RemoveObject(item));
				pc.removeKnownObject(item);
			}

			_items.remove(item);
			if (_items.size() == 0) {
				World.get().removeVisibleObject(this);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
