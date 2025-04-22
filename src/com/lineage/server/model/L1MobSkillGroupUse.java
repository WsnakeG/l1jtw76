package com.lineage.server.model;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.templates.L1MobSkillGroup;

/**
 * @author XXX
 */
public final class L1MobSkillGroupUse {

	private L1MobSkillGroup _mobSkillTemplate;

	private L1NpcInstance _attacker;

	private static final Random _rnd = new Random();

	private final Queue<Integer> _queue;

	private int _sleepTime;

	public L1MobSkillGroupUse(final L1NpcInstance npc, final L1MobSkillGroup mobSkillGroup) {
		_mobSkillTemplate = mobSkillGroup;
		_attacker = npc;
		_queue = new LinkedBlockingQueue<Integer>(_mobSkillTemplate.getActNoMaxSize());
	}

	public final L1MobSkillGroup getSkillGroup() {
		return _mobSkillTemplate;
	}

	public final void deleteAllTemplates() {
		_mobSkillTemplate = null;
		_attacker = null;
	}

	public final int pickActNo() {
		if (_queue.isEmpty()) {
			return -1;
		}
		return _queue.poll();
	}

	public final int getSleepTime() {
		return _sleepTime;
	}

	public final boolean skillUse(final L1Character tg) {
		final int skillSize = _mobSkillTemplate.getSkillSize();
		if (skillSize <= 0) {
			return false;
		}

		final int randomInt = _rnd.nextInt(skillSize);

		if (isSkillUseble(tg, randomInt)) {
			if (magicAttack(randomInt)) {
				return true;
			}
		}
		return false;
	}

	private final boolean magicAttack(final int idx) {
		final int[] actNoList = _mobSkillTemplate.getActNoList(idx);
		if (actNoList == null) {
			return false;
		}

		final String chatId = _mobSkillTemplate.getChatId(idx);
		if ((chatId != null) && !chatId.isEmpty()) {
			_attacker.broadcastPacketAll(new S_NpcChat(_attacker, chatId));
		}

		final int interval = _mobSkillTemplate.getInterval(idx);
		if (interval > 0) {
			_sleepTime = interval * _attacker.getHateList().toTargetArrayList().size();
		}

		for (final int actNo : actNoList) {
			_queue.offer(actNo);
		}
		return true;
	}

	private final boolean isSkillUseble(final L1Character tg, final int skillIdx) {
		if (_attacker.getLocation().getTileLineDistance(tg.getLocation()) > _mobSkillTemplate
				.getRange(skillIdx)) {
			return false;
		}

		if (_mobSkillTemplate.getChance(skillIdx) > 0) {
			final int chance = _rnd.nextInt(100) + 1;
			if (chance < _mobSkillTemplate.getChance(skillIdx)) {
				return true;
			}
		}
		return false;
	}
}
