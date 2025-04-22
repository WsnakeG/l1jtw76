package com.lineage.server.clientpackets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_FixWeaponList;

/**
 * 要求維修物品清單
 * 
 * @author daien
 */
public class C_FixWeaponList extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_FixWeaponList.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			// 暫時清單
			final List<L1ItemInstance> weaponList = new ArrayList<L1ItemInstance>();

			// 背包物件
			final List<L1ItemInstance> itemList = pc.getInventory().getItems();
			for (final L1ItemInstance item : itemList) {

				// Find Weapon
				switch (item.getItem().getType2()) {
				case 1:
					if (item.get_durability() > 0) {
						weaponList.add(item);
					}
					break;
				}
			}

			pc.sendPackets(new S_FixWeaponList(weaponList));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

}
