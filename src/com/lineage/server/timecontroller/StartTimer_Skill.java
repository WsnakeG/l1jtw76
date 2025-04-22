package com.lineage.server.timecontroller;

import com.lineage.server.timecontroller.skill.EffectCubeBurnTimer;
import com.lineage.server.timecontroller.skill.EffectCubeEruptionTimer;
import com.lineage.server.timecontroller.skill.EffectCubeHarmonizeTimer;
import com.lineage.server.timecontroller.skill.EffectCubeShockTimer;
import com.lineage.server.timecontroller.skill.EffectFirewallTimer;

/**
 * SKILL專用時間軸 初始化啟動
 * 
 * @author dexc
 */
public class StartTimer_Skill {

	public void start() {

		// 法師技能(火牢)
		final EffectFirewallTimer firewall = new EffectFirewallTimer();
		firewall.start();

		// 幻術師技能(立方：燃燒)
		final EffectCubeBurnTimer cubeBurn = new EffectCubeBurnTimer();
		cubeBurn.start();

		// 幻術師技能(立方：地裂)
		final EffectCubeEruptionTimer cubeEruption = new EffectCubeEruptionTimer();
		cubeEruption.start();

		// 幻術師技能(立方：衝擊)
		final EffectCubeShockTimer cubeShock = new EffectCubeShockTimer();
		cubeShock.start();

		// 幻術師技能(立方：和諧)
		final EffectCubeHarmonizeTimer cubeHarmonize = new EffectCubeHarmonizeTimer();
		cubeHarmonize.start();

	}
}
