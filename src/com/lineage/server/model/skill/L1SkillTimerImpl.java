package com.lineage.server.model.skill;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.lineage.server.model.L1Character;

/**
 * @author terry0412
 */
public final class L1SkillTimerImpl implements Runnable {

	private final L1Character _cha;

	private final ConcurrentHashMap<Integer, Integer> _skillsList;

	public L1SkillTimerImpl(final L1Character cha) {
		_cha = cha;
		_skillsList = cha.getSkillEffectList();
	}

	@Override
	public void run() {
		if (_cha == null) {
			return;
		}

		// _cha.setSkillProcess(true);

		for (final Entry<Integer, Integer> skill : _skillsList.entrySet()) {
			if (skill.getValue() == 0) {
				continue;
			}

			final int value = skill.getValue() - 1;
			if (value <= 0) {
				_cha.removeSkillEffect(skill.getKey());

			} else {
				_skillsList.put(skill.getKey(), value);

				/*
				 * if (skill.getKey() == 36) {
				 * _cha.removeSkillEffect(skill.getKey()); }
				 */
			}
		}

		// _cha.setSkillProcess(false);
	}
}
