package com.lineage.server.command.executor;

import static com.lineage.server.model.skill.L1SkillId.HAPPY_TIME;

import java.util.StringTokenizer;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 快樂時光
 * 
 * @author Cerenis
 */
public class L1HappyTime implements L1CommandExecutor {
	private L1HappyTime() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1HappyTime();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			// 玩家名字
			final String char_name = tok.nextToken();

			final L1PcInstance tg;
			// 處罰自己，自high時用。
			if (char_name.equalsIgnoreCase("me"))
				tg = pc;
			// 玩家
			else
				tg = World.get().getPlayer(char_name);
			// SET 快樂時光
			if (!tg.hasSkillEffect(HAPPY_TIME))
				tg.setSkillEffect(HAPPY_TIME, 3000);
			// 移除
			else
				tg.killSkillEffectTimer(HAPPY_TIME);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("\\aE請輸入 : " + cmdName + " 玩家名字"));
		}
	}
}