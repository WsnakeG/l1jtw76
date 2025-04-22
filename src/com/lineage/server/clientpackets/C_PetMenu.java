package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_PetInventory;
import com.lineage.server.world.WorldPet;

/**
 * 要求寵物回報選單
 * 
 * @author daien
 */
public class C_PetMenu extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_PetMenu.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc == null) {
				return;
			}

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			if (pc.isPrivateShop()) { // 商店村模式
				return;
			}

			final int petId = readD();

			final L1PetInstance pet = WorldPet.get().get(petId);
			if (pet == null) {
				return;
			}
			if (pc.getPetList().get(petId) == null) {
				return;
			}

			pc.sendPackets(new S_PetInventory(pet, true));

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
