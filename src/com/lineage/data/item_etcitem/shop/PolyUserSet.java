package com.lineage.data.item_etcitem.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 自定義變身捲軸 classname: shop.PolyUserSet 設置範例: shop.PolyUserSet 1080 1800 變身代號
 * 時間(秒) 等級使用限制設置在資料表中
 * 
 * @author dexc
 */
public class PolyUserSet extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(PolyUserSet.class);

	/**
	 *
	 */
	private PolyUserSet() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new PolyUserSet();
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
		if (_polyid == -1) {
			final int itemId = item.getItemId();
			_log.error("自定義變身捲軸 設定錯誤: " + itemId + " 沒有變身代號!");
			return;
		}
		if (pc.getTempCharGfx() == _polyid) { // 若有變身
			pc.sendPackets(new S_ServerMessage("\\aD變身效果時間未結束\\aG哈哈哈哈。"));
			return;
		}
		pc.getInventory().removeItem(item, 1);
		L1PolyMorph.doPoly(pc, _polyid, _time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), 6082)); // 新增指定變身眷軸特效
	}

	private int _polyid = -1;
	private int _time = 1800;

	@Override
	public void set_set(String[] set) {
		try {
			_polyid = Integer.parseInt(set[1]);

		} catch (Exception e) {
		}
		try {
			_time = Integer.parseInt(set[2]);

		} catch (Exception e) {
		}
	}
}
