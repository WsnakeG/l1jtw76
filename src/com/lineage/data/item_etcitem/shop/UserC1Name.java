package com.lineage.data.item_etcitem.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.datatables.lock.CharacterC1Reading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 陣營變更卡 180000 陣營變更卡 A shop.UserC1Name 1 189 7 127 0 10 陣營變更卡 A questitem normal none 0 2604 3963 0 1 0 0 0 0 0 0 1 1 0 0 0 0 0 0 _c1type = 陣營別 _gfxid = 使用後自訂效果圖
 * 
 * @author Roy
 */
public class UserC1Name extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(UserC1Name.class);

	/**
	 *
	 */
	private UserC1Name() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new UserC1Name();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data
	 *            參數
	 * @param pc
	 *            執行者
	 * @param item
	 *            物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {

		// 移除道具
		pc.getInventory().removeItem(item, 1);

		if (_gfxid > 0) { // 具備動畫
			pc.sendPackets(new S_SkillSound(pc.getId(), _gfxid));
		}

		pc.get_c_power().set_c1_type(_c1type);

		final String type = C1_Name_Table.get().get(pc.get_c_power().get_c1_type());
		pc.get_c_power().set_power(pc, true);
//		CharacterC1Reading.get().storeCharacterC1(pc);
		CharacterC1Reading.get().updateCharacterC1(pc.getId(), _c1type, type);
		pc.sendPacketsAll(new S_ChangeName(pc, true));
		pc.sendPackets(new S_ServerMessage("\\aG陣營" + type + "\\aG更換成功。"));
		pc.sendPackets(new S_ServerMessage("\\aE您目前所屬的陣營: " + "\\aD" + type));

		try {
			pc.save();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

	}

	private int _gfxid;
	private int _c1type;

	@Override
	public void set_set(final String[] set) {
		try {
			_c1type = Integer.parseInt(set[1]);

			if (_c1type <= 0) {
				_c1type = 1;
				_log.error("設置錯誤:陣營不能等於0! 使用預設1");
			}

		} catch (final Exception e) {
		}
		try {
			_gfxid = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
	}

}