package com.lineage.server.timecontroller.pc;

import static com.lineage.server.model.skill.L1SkillId.BLESSED_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.BLESS_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.HOLY_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.SHADOW_FANG;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_MagicEquipment;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

public class ItemTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(UpdatePcTimer.class);

	private ScheduledFuture<?> _timer;

	private L1ItemInstance _item;

	private int _skillid;

	private int _time;

	private boolean _isend = false;

	private void checkIcon(final L1ItemInstance item, final int skillid, final int time) {
		int gfx = 0;
		switch (skillid) {
		case HOLY_WEAPON:
			gfx = 2165;
			break;
		case ENCHANT_WEAPON:
			gfx = 747;
			break;
		case BLESS_WEAPON:
			gfx = 2176;
			break;
		case SHADOW_FANG:
			gfx = 2951;
			break;
		case BLESSED_ARMOR:
			gfx = 748;
			break;
		}
		if (gfx == 0) {
			return;
		}
		final L1PcInstance pc = World.get().getPlayer(item.get_char_objid());
		if (pc != null) {
			S_MagicEquipment packet = new S_MagicEquipment(time, gfx);
			pc.sendPacketsAll(packet);
			//pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_BUFF_ICON, gfx, time));
		}
	}

	public void start(final L1ItemInstance item, final int skillid, final int time) {
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000, 1000);
		_item = item;
		_skillid = skillid;
		_time = time;
		_item.addskill(_skillid, _time);
		checkIcon(_item, _skillid, _time);
	}

	@Override
	public void run() {
		try {
			if (_item == null) {
				_isend = true;
				return;
			}
			_time--;

			if (_time <= 0) {
				checkIcon(_item, _skillid, 0);
				_item.removeskill(_skillid);
				_isend = true;
			}

		} catch (final Exception e) {
			_item.removeskill(_skillid);
			GeneralThreadPool.get().cancel(_timer, false);
			final ItemTimer itemTimer = new ItemTimer();
			itemTimer.start(_item, _skillid, _time);
			_log.error("物品 (" + _item.getName() + ")技能計時器異常重啟", e);

		} finally {
			if (_isend) {
				GeneralThreadPool.get().cancel(_timer, false);
			}
		}
	}
}
