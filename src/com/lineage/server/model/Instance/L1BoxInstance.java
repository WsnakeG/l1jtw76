package com.lineage.server.model.Instance;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcBoxTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.serverpackets.S_BoxPack;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemBox;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1NpcBox;
import com.lineage.server.utils.L1SpawnUtil;

/**
 * @author terry0412
 */
public class L1BoxInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1BoxInstance.class);

	private static final Random _random = new Random();

	public L1BoxInstance(final L1Npc template) {
		super(template);
	}

	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			// 副本ID不相等 不相互顯示
			if (perceivedFrom.get_showId() != get_showId()) {
				return;
			}
			perceivedFrom.addKnownObject(this);
			perceivedFrom.sendPackets(new S_BoxPack(this));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			// 破壊不可能なドアは対象外
			if ((getMaxHp() == 0) || (getMaxHp() == 1)) {
				return;
			}

			if ((getCurrentHp() > 0) && !isDead()) {
				final L1AttackMode attack = new L1AttackPc(pc, this);
				if (attack.calcHit()) {
					attack.calcDamage();
				}
				attack.action();
				attack.commit();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onTalkAction(final L1PcInstance player) {
		if (isDead() || destroyed()) {
			return;
		}

		if (getOpenStatus() == ActionCodes.ACTION_Off) {
			final L1NpcBox npcBox = NpcBoxTable.get().getTemplate(getNpcId());
			if (npcBox == null) {
				return;
			}

			if (npcBox.get_createItemBoxes() == null) {
				return;
			}

			if ((npcBox.get_needKeyId() > 0)
					&& !player.getInventory().consumeItem(npcBox.get_needKeyId(), 1)) {
				player.sendPackets(new S_ServerMessage(337,
						ItemTable.get().getTemplate(npcBox.get_needKeyId()).getNameId()));
				return;
			}

			setOpenStatus(ActionCodes.ACTION_On);
			broadcastPacketAll(new S_DoActionGFX(getId(), getOpenStatus()));

			final L1ItemBox itemBox = npcBox.get_createItemBoxes()
					.get(_random.nextInt(npcBox.get_createItemBoxes().size()));
			if (itemBox == null) {
				return;
			}

			try {
				Thread.sleep(50);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

			if ((npcBox.get_mobNpcIdList() == null) || (_random.nextInt(100) < itemBox.getChance())) {
				CreateNewItem.createNewItem(player, itemBox.getItemId(), itemBox.getItemCount());

			} else {
				final L1NpcInstance npc = L1SpawnUtil.spawnRx(getLocation(),
						npcBox.get_mobNpcIdList().get(_random.nextInt(npcBox.get_mobNpcIdList().size())),
						get_showId(), 0, 60);
				if (npc != null) {
					npc._hateList.add(player, 0);
					npc._target = player;
				}
			}

			final int repair_time = _random
					.nextInt(Math.max(npcBox.get_resetTimeSecsMax() - npcBox.get_resetTimeSecsMin(), 0) + 1)
					+ npcBox.get_resetTimeSecsMin();

			set_repair_time(repair_time);
		}
	}

	public final void close() {
		if (isDead() || destroyed()) {
			return;
		}
		if (getOpenStatus() == ActionCodes.ACTION_Open) {
			setOpenStatus(ActionCodes.ACTION_Close);
			broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Close));
		}
	}

	private int _openStatus = ActionCodes.ACTION_Close;

	public final int getOpenStatus() {
		return _openStatus;
	}

	private final void setOpenStatus(final int i) {
		if ((i == ActionCodes.ACTION_Open) || (i == ActionCodes.ACTION_Close)) {
			_openStatus = i;
		}
	}

	private int _repair_time;

	public final synchronized int get_repair_time() {
		return _repair_time;
	}

	public final synchronized void set_repair_time(final int repair_time) {
		_repair_time = repair_time;
	}
}
