package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1CnInstance;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1GamInstance;
import com.lineage.server.model.Instance.L1GamblingInstance;
import com.lineage.server.model.Instance.L1IllusoryInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldPet;

/**
 * 要求攻擊指定物件
 * 
 * @author daien
 */
public class C_SelectTarget extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_SelectTarget.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final int petId = readD();
			// System.out.println("type:"+petId);
			@SuppressWarnings("unused")
			final int type = readC();
			// System.out.println("type:"+type);
			final int targetId = readD();
			// System.out.println("type:"+targetId);

			final L1PetInstance pet = WorldPet.get().get(petId);

			if (pet == null) {
				return;
			}

			final L1Character target = (L1Character) World.get().findObject(targetId);

			if (target == null) {
				return;
			}

			boolean isCheck = false;

			if (target instanceof L1PcInstance) {// PC
				final L1PcInstance tgpc = (L1PcInstance) target;
				if (tgpc.checkNonPvP(tgpc, pet)) {
					return;
				}
				isCheck = true;

			} else if (target instanceof L1DeInstance) {// 虛擬人物
				isCheck = true;

			} else if (target instanceof L1PetInstance) {// 寵物
				isCheck = true;

			} else if (target instanceof L1SummonInstance) {// 召喚獸
				isCheck = true;

			} else if (target instanceof L1CnInstance) {// CN 專屬商人
				return;

			} else if (target instanceof L1GamblingInstance) {// 賭場NPC
				return;

			} else if (target instanceof L1GamInstance) {// 賭場NPC(參賽者)
				return;

			} else if (target instanceof L1IllusoryInstance) {// 分身
				return;
			}

			if (isCheck) {
				if (target.isSafetyZone()) {
					return;
				}
			}
			pet.setMasterSelectTarget(target);

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
