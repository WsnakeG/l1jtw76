package com.lineage.server.clientpackets;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_MOVE;
import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.MEDITATION;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.clientpackets.AcceleratorChecker.ACT_TYPE;
import com.lineage.server.datatables.DungeonRTable;
import com.lineage.server.datatables.DungeonTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_Lock;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.WorldTrap;

/**
 * 要求角色移動 基本封包長度:
 * 
 * @author daien
 */
public class C_MoveChar extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_MoveChar.class);

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if ((pc == null)) { // PC為NULL
				return;
			}

			if (pc.isDead()) {// 死亡
				return;
			}

			if (pc.isTeleport()) { // 傳送處理中
				return;
			}

			if (pc.isPrivateShop()) { // 個人商店中
				return;
			}

			// 不可執行的條件
			if (pc.isParalyzed()) { // 木乃伊中
				return;
			}

			if (pc.isSleeped()) { // 睡眠中
				return;
			}

			if (pc.hasSkillEffect(L1SkillId.THUNDER_GRAB)) {
				return;
			}

			if (pc.isInCharReset()) {
				return;
			}
			
			int locx = 0;// 目前位置
			int locy = 0;
			int heading = 0;

			try {
				locx = readH();
				if (locx <= 0) {
					return;
				}
				locy = readH();
				if (locx <= 0) {
					return;
				}
				heading = readC();

			} catch (final Exception e) {

			}

			// 解除舊座標障礙宣告
			pc.getMap().setPassable(pc.getLocation(), true);

			if (heading > 7) { // Taiwan Only
				heading ^= 0x49;// 换位
				locx = pc.getX();
				locy = pc.getY();
			}

			heading = Math.min(heading, 7);

			final int oleLocx = pc.getX();
			final int oleLocy = pc.getY();

			// 移动后位置
			final int newlocx = locx + HEADING_TABLE_X[heading];
			final int newlocy = locy + HEADING_TABLE_Y[heading];

			// 设置新面向
			pc.setHeading(heading);
			pc.removeSkillEffect(MEDITATION);// 解除冥想術
			pc.setCallClanId(0); // 人物移動呼喚盟友無效

			if (!pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // 絕對屏障狀態
				pc.setRegenState(REGENSTATE_MOVE);
			}

			try {
				// 不允許穿過該點
				boolean isError = false;

				// 異位判斷(封包數據 與 核心數據 不吻合)
				if ((locx != oleLocx) || (locy != oleLocy)) {
					isError = true;
				}

				// 商店村模式
				if (pc.isPrivateShop()) {
					isError = true;
				}

				// 無法攻擊/使用道具/技能/回城的狀態
				if (pc.isParalyzedX()) {
					isError = true;
				}

				// 位置具有障礙
				if (!isError) {
					// 穿透判斷
					// boolean isPassable = pc.getMap().isPassable(newlocx,
					// newlocy);
					/*
					 * boolean isPassable = pc.getMap().isPassable(oleLocx,
					 * oleLocy, heading);
					 */
					// System.out.println("穿透判斷: " + isPassable);
					// 該點不可通行
					// if (!isPassable) {
					// System.out.println("該點不可通行");
					// 該座標點上具有物件
					if (CheckUtil.checkPassable(pc, newlocx, newlocy, pc.getMapId())) {
						// System.out.println("該座標點上具有物件");
						isError = true;
					}
					// }
				}

				if (isError) {
					// 送出座標異常
					// 記錄移動前座標
					pc.setOleLocX(oleLocx);
					pc.setOleLocY(oleLocy);
					pc.sendPackets(new S_Lock());
					// System.out.println("座標異常不執行移動送出回溯封包");
					return;
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}

			if (ConfigOther.CHECK_MOVE_INTERVAL) {
				final int result = pc.speed_Attack().checkInterval(ACT_TYPE.MOVE);
				if (result == AcceleratorChecker.R_DISPOSED) {
					_log.error("要求角色移動:速度異常(" + pc.getName() + ")");
					return;
				}
			}

			// 記錄移動前座標
			pc.setOleLocX(oleLocx);
			pc.setOleLocY(oleLocy);

			// 地圖切換
			if (DungeonTable.get().dg(newlocx, newlocy, pc.getMap().getId(), pc)) {
				return;
			}

			// 地圖切換(多點)
			if (DungeonRTable.get().dg(newlocx, newlocy, pc.getMap().getId(), pc)) {
				return;
			}
			// 設置新作標點
			pc.getLocation().set(newlocx, newlocy);

			// 設置新面向
			pc.setHeading(heading);

			if (!pc.isGmInvis() && !pc.isGhost() && !pc.isInvisble()) {
				// 送出移動封包
				pc.broadcastPacketAll(new S_MoveCharPacket(pc));
			}
			// 檢查地圖使用權
			CheckUtil.isUserMap(pc);

			// 設置娃娃移動
			pc.setNpcSpeed();

			// 新增座標障礙宣告
			if (!pc.isGm()) {
				pc.getMap().setPassable(pc.getLocation(), false);
			}

			// 踩到陷阱的處理
			WorldTrap.get().onPlayerMoved(pc);

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