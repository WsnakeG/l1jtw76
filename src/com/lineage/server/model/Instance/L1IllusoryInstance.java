package com.lineage.server.model.Instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_NPCPack_Ill;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.templates.L1Npc;

/**
 * 對象:分身 控制項
 * 
 * @author dexc
 */
public class L1IllusoryInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1IllusoryInstance.class);

	/**
	 * @param template
	 */
	public L1IllusoryInstance(final L1Npc template) {
		super(template);
	}

	/**
	 * 接觸資訊 TODO
	 */
	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			// 副本ID不相等 不相護顯示
			if (perceivedFrom.get_showId() != get_showId()) {
				return;
			}
			perceivedFrom.addKnownObject(this);

			if (0 < getCurrentHp()) {
				perceivedFrom.sendPackets(new S_NPCPack_Ill(this));
				onNpcAI();
				if (getBraveSpeed() == 1) {// 具有勇水狀態
					perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
				}

			} else {
				perceivedFrom.sendPackets(new S_NPCPack_Ill(this));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 攻擊目標設置
	 */
	@Override
	public void setLink(final L1Character cha) {
		// 副本ID不相等
		if (get_showId() != cha.get_showId()) {
			return;
		}
		if (cha instanceof L1PcInstance) {
			if (cha.getMap().isSafetyZone(cha.getLocation())) {
				return;
			}
		}
		if ((cha != null) && _hateList.isEmpty()) {
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}

		setActived(false);
		startAI();
	}

	@Override
	public void onTalkAction(final L1PcInstance pc) {

	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			final L1AttackMode attack = new L1AttackPc(pc, this);
			attack.action();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 受到攻撃 MP 減少使用
	 */
	@Override
	public void ReceiveManaDamage(final L1Character attacker, final int mpDamage) {
		// 分身無視攻擊
	}

	/**
	 * 受到攻撃 HP 減少使用
	 */
	@Override
	public void receiveDamage(final L1Character attacker, final int damage) {
		// 分身無視攻擊
	}

	@Override
	public void setCurrentHp(final int i) {
		// 分身無HP
	}

	@Override
	public void setCurrentMp(final int i) {
		// 分身無MP
	}
}
