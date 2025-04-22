package com.lineage.data.item_etcitem.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemBoxTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 保箱鑰匙 DELETE FROM `etcitem` WHERE `item_id`>='56069' AND `item_id`<='56071';
 * INSERT INTO `etcitem` VALUES ('56069', '保箱鑰匙', 'shop.Power_Key', '保箱鑰匙',
 * 'other', 'choice', 'mineral', '0', '1021', '3963', '0', '1', '0', '0', '0',
 * '0', '0', '1', '0', '0', '0', '0', '0', '0', '0'); INSERT INTO `etcitem`
 * VALUES ('56070', '保箱鑰匙', 'shop.Power_Key', '保箱鑰匙', 'other', 'choice',
 * 'mineral', '0', '1023', '3963', '0', '1', '0', '0', '0', '0', '0', '1', '0',
 * '0', '0', '0', '0', '0', '0'); INSERT INTO `etcitem` VALUES ('56071', '測試之袋',
 * '0', '測試之袋', 'treasure_box', 'normal', 'none', '10000', '957', '3963', '0',
 * '1', '0', '0', '0', '0', '0', '1', '1', '0', '0', '0', '0', '0', '0'); SET
 * FOREIGN_KEY_CHECKS=0; -- ---------------------------- -- 建立資料表
 * `etcitem_box_key` -- ---------------------------- DROP TABLE IF EXISTS
 * `etcitem_box_key`; CREATE TABLE `etcitem_box_key` ( `id` int(11) NOT NULL
 * AUTO_INCREMENT, `key_itemid` int(10) NOT NULL DEFAULT '0' COMMENT
 * '需要使用的開啟物件編號', `box_item_id` int(10) NOT NULL DEFAULT '0' COMMENT '物品編號',
 * `get_item_id` int(10) NOT NULL, `name` varchar(100) DEFAULT NULL COMMENT
 * '物品名稱', `randomint` int(10) NOT NULL DEFAULT '1000000' COMMENT '比對用機率',
 * `random` int(10) NOT NULL DEFAULT '1000' COMMENT '機率', `min_count` int(10)
 * NOT NULL DEFAULT '1' COMMENT '給予數量(最少)', `max_count` int(10) NOT NULL DEFAULT
 * '1' COMMENT '給予數量(最多)', `out` tinyint(1) NOT NULL DEFAULT '0' COMMENT '公告',
 * PRIMARY KEY (`id`) ) ENGINE=MyISAM AUTO_INCREMENT=1694 DEFAULT CHARSET=utf8;
 * -- ---------------------------- -- 建立資料表 etcitem_box_key 範例內容 --
 * ---------------------------- INSERT INTO `etcitem_box_key` VALUES ('385',
 * '56070', '56071', '40053', '高品質紅寶石', '1000000', '60000', '1', '1', '0');
 * INSERT INTO `etcitem_box_key` VALUES ('386', '56070', '56071', '40054',
 * '高品質藍寶石', '1000000', '60000', '1', '1', '0'); INSERT INTO `etcitem_box_key`
 * VALUES ('387', '56070', '56071', '40055', '高品質綠寶石', '1000000', '60000', '1',
 * '1', '0'); INSERT INTO `etcitem_box_key` VALUES ('407', '56069', '56071',
 * '40044', '鑽石', '1000000', '130000', '3', '3', '0'); INSERT INTO
 * `etcitem_box_key` VALUES ('408', '56069', '56071', '40045', '紅寶石', '1000000',
 * '130000', '3', '3', '0'); INSERT INTO `etcitem_box_key` VALUES ('409',
 * '56069', '56071', '40046', '藍寶石', '1000000', '130000', '3', '3', '0'); INSERT
 * INTO `etcitem_box_key` VALUES ('410', '56069', '56071', '40047', '綠寶石',
 * '1000000', '130000', '3', '3', '0'); INSERT INTO `etcitem_box_key` VALUES
 * ('411', '56069', '56071', '40048', '品質鑽石', '1000000', '80000', '1', '1',
 * '0'); INSERT INTO `etcitem_box_key` VALUES ('412', '56069', '56071', '40049',
 * '品質紅寶石', '1000000', '80000', '1', '1', '0'); INSERT INTO `etcitem_box_key`
 * VALUES ('413', '56069', '56071', '40050', '品質藍寶石', '1000000', '80000', '1',
 * '1', '0'); INSERT INTO `etcitem_box_key` VALUES ('414', '56069', '56071',
 * '40051', '品質綠寶石', '1000000', '80000', '1', '1', '0');
 */
public class Power_Key extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Power_Key.class);

	/**
	 *
	 */
	private Power_Key() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Power_Key();
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
			final int itemobj = data[0];
			final L1ItemInstance tgitem = pc.getInventory().getItem(itemobj);
			if (tgitem == null) {
				return;
			}

			final int itemid = item.getItemId();
			if (tgitem.getItem().getType() == 16) { // treasure_box
				if (ItemBoxTable.get().is_key(tgitem.getItemId(), itemid)) {
					if (pc.getInventory().removeItem(item, 1) != 1) {
						return;
					}
					ItemBoxTable.get().get_key(pc, tgitem, itemid);
				} else {
					// 79 沒有任何事情發生
					pc.sendPackets(new S_ServerMessage(79));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
