package com.lineage.server.clientpackets;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_ATTACK;
import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.MEDITATION;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.clientpackets.AcceleratorChecker.ACT_TYPE;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_AttackPacketPc;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;

/**
 * 要求角色攻擊
 * 
 * @author dexc
 */
public class C_Attack extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Attack.class);

	/*
	 * public C_Attack() { } public C_Attack(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				// _log.error("要求角色攻擊:鬼魂模式");
				return;
			}
			if (pc.isDead()) { // 死亡
				// _log.error("要求角色攻擊:死亡");
				return;
			}
			if (pc.isTeleport()) { // 傳送中
				// _log.error("要求角色攻擊:傳送中");
				return;
			}

			if (pc.isPrivateShop()) { // 商店村模式
				// _log.error("要求角色攻擊:商店村模式");
				return;
			}

			if (pc.getInventory().getWeight240() >= 197) { // 重量過重
				// 110 \f1當負重過重的時候，無法戰鬥。
				pc.sendPackets(new S_ServerMessage(110));
				// _log.error("要求角色攻擊:重量過重");
				return;
			}

			if (ConfigOther.CHECK_ATTACK_INTERVAL) {
				final int result = pc.speed_Attack().checkInterval(ACT_TYPE.ATTACK);
				if (result == AcceleratorChecker.R_DISPOSED) {
					return;
				}
			}

			if (pc.isInvisble()) { // 隱身狀態
				// _log.error("要求角色攻擊:隱身狀態");
				return;
			}

			if (pc.isInvisDelay()) { // 隱身延遲
				// _log.error("要求角色攻擊:隱身延遲");
				return;
			}

			// 無法攻擊/使用道具/技能/回城的狀態
			if (pc.isParalyzedX()) {
				return;
			}

			if (pc.get_weaknss() != 0) {
				final long h_time = Calendar.getInstance().getTimeInMillis() / 1000;// 換算為秒
				if ((h_time - pc.get_weaknss_t()) > 16) {
					pc.set_weaknss(0, 0);
				}
			}

			int targetId = 0;
			int locx = 0;
			int locy = 0;

			try {
				// 攻擊點資訊
				targetId = readD();
				locx = readH();
				locy = readH();
				// _log.error("要求角色攻擊:攻擊點資訊取回");

			} catch (final Exception e) {
				// _log.error("要求角色攻擊:攻擊點資訊異常");
				return;
			}

			if (locx == 0) {
				// _log.error("要求角色攻擊:locx == 0");
				return;
			}
			if (locy == 0) {
				// _log.error("要求角色攻擊:locy == 0");
				return;
			}

			final L1Object target = World.get().findObject(targetId);

			if (target instanceof L1Character) {
				if (target.getMapId() != pc.getMapId()) { // 攻擊位置異常
					// _log.error("要求角色攻擊:攻擊位置異常target.getMapId() !=
					// pc.getMapId()");
					return;
				}
			}

			// 檢查地圖使用權
			CheckUtil.isUserMap(pc);

			if (target instanceof L1NpcInstance) {
				final int hiddenStatus = ((L1NpcInstance) target).getHiddenStatus();
				if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) { // 躲藏
					return;
				}
				if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) { // 空中
					return;
				}
			}

			// 絕對屏障解除
			if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
				pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
				pc.startHpRegeneration();
				pc.startMpRegeneration();
			}

			pc.killSkillEffectTimer(MEDITATION);

			pc.delInvis(); // 透明状態の解除

			pc.setRegenState(REGENSTATE_ATTACK);

			if ((target != null) && !((L1Character) target).isDead()) {
				if (target instanceof L1PcInstance) {
					final L1PcInstance tg = (L1PcInstance) target;
					pc.setNowTarget(tg);
				}
				target.onAction(pc);

			} else { // 空攻撃
				pc.setHeading(pc.targetDirection(locx, locy));
				pc.sendPacketsAll(new S_AttackPacketPc(pc));
			}

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