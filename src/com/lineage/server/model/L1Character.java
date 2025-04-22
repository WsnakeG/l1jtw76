package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.BLIND_HIDING;
import static com.lineage.server.model.skill.L1SkillId.BONE_BREAK;
import static com.lineage.server.model.skill.L1SkillId.CURSE_PARALYZE;
import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.ERASE_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BREATH;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.INVISIBILITY;
import static com.lineage.server.model.skill.L1SkillId.LIGHT;
import static com.lineage.server.model.skill.L1SkillId.SHOCK_STUN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1FollowerInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.poison.L1Poison;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillStop;
import com.lineage.server.model.skill.L1SkillTimerCreator;
import com.lineage.server.serverpackets.S_HPMeter;
import com.lineage.server.serverpackets.S_Light;
import com.lineage.server.serverpackets.S_PetCtrlMenu;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.types.Point;
import com.lineage.server.utils.RangeInt;
import com.lineage.server.world.World;

/**
 * L1Character
 * 
 * @author daien
 */
public class L1Character extends L1Object {

	private static final Log _log = LogFactory.getLog(L1Character.class);

	private static final long serialVersionUID = 1L;

	private L1Poison _poison = null;

	private boolean _sleeped;

	private final Map<Integer, L1NpcInstance> _petlist = new HashMap<Integer, L1NpcInstance>();

	private final ConcurrentHashMap<Integer, Integer> _skillEffect = new ConcurrentHashMap<Integer, Integer>();

	private ScheduledFuture<?> _future;

	private final Map<Integer, L1ItemDelay.ItemDelayTimer> _itemdelay = new HashMap<Integer, L1ItemDelay.ItemDelayTimer>();

	private final Map<Integer, L1FollowerInstance> _followerlist = new HashMap<Integer, L1FollowerInstance>();

	public L1Character() {
		_level = 1;
	}
	
	public void broadcastPacket(ServerBasePacket packet) {

		ArrayList<?> objs = World.get().getVisiblePlayer(this);
		try {
			L1PcInstance pc = null;
			for (int i = 0; i < objs.size(); i++) {
				pc = (L1PcInstance) objs.get(i);

				if ((pc.getMapId() < 16384) || (pc.getMapId() > 25088) || (pc.getInnKeyId() == getInnKeyId()))
					pc.sendPackets(packet);
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			objs.clear();
			objs = null;
		}
	}

	/**
	 * 物件復活
	 * 
	 * @param hp 復活後的HP
	 */
	public void resurrect(int hp) {
		try {
			if (!isDead()) {
				return;
			}
			if (hp <= 0) {
				hp = 1;
			}
			// 設置為未死亡
			setDead(false);
			// 設置HP
			setCurrentHp(hp);
			// 設置狀態
			setStatus(0);
			// 解除變身
			L1PolyMorph.undoPoly(this);

			// 重新認識物件
			for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
				pc.sendPackets(new S_RemoveObject(this));
				pc.removeKnownObject(this);
				pc.updateObject();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _secHp = -1;// 上次HP異動前HP

	/**
	 * 發送該物件可見HP
	 * 
	 * @param pc
	 */
	public void broadcastPacketHP(final L1PcInstance pc) {
		try {
			// 副本ID相等 必須在這方法之前先判斷
			// 記錄HP不相等於 目前HP
			if (_secHp != getCurrentHp()) {
				_secHp = getCurrentHp();
				pc.sendPackets(new S_HPMeter(this));
				//pc.sendPackets(new S_HPMeter(getId(), (100 * getCurrentHp()) / getMaxHp(), 0xff));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _currentHp;

	/**
	 * 現在的HP
	 * 
	 * @return 現在HP
	 */
	public int getCurrentHp() {
		return _currentHp;
	}

	/**
	 * 設置新HP
	 * 
	 * @param i
	 */
	// 特殊な処理がある場合はこっちをオーバライド（パケット送信等）
	public void setCurrentHp(final int i) {
		_currentHp = i;
		if (_currentHp >= getMaxHp()) {
			_currentHp = getMaxHp();
		}
	}

	/**
	 * 設置登場物件HP
	 * 
	 * @param i
	 */
	public void setCurrentHpDirect(final int i) {
		_currentHp = i;
	}

	private int _currentMp;

	/**
	 * 現在的MP
	 * 
	 * @return 現在MP
	 */
	public int getCurrentMp() {
		return _currentMp;
	}

	/**
	 * 設置新MP
	 * 
	 * @param i
	 */
	public void setCurrentMp(final int i) {
		_currentMp = i;
		if (_currentMp >= getMaxMp()) {
			_currentMp = getMaxMp();
		}
	}

	/**
	 * 設置登場物件MP
	 * 
	 * @param i
	 */
	public void setCurrentMpDirect(final int i) {
		_currentMp = i;
	}

	/**
	 * 是否為睡眠狀態
	 * 
	 * @return true:麻痺狀態 false:無
	 */
	public boolean isSleeped() {
		return _sleeped;
	}

	/**
	 * 是否為睡眠狀態
	 * 
	 * @param sleeped true:睡眠狀態 false:無
	 */
	public void setSleeped(final boolean sleeped) {
		_sleeped = sleeped;
	}

	/**
	 * 無法攻擊/使用道具/技能/回城的狀態
	 * 
	 * @return true:狀態中 false:無
	 */
	public boolean isParalyzedX() {
		// 冰矛圍籬
		if (hasSkillEffect(ICE_LANCE)) {
			return true;
		}
		// 寒冰噴吐
		if (hasSkillEffect(FREEZING_BREATH)) {
			return true;
		}
		// 大地屏障
		if (hasSkillEffect(EARTH_BIND)) {
			return true;
		}
		// 衝擊之暈
		if (hasSkillEffect(SHOCK_STUN)) {
			return true;
		}
		// 骷髏毀壞
		if (hasSkillEffect(BONE_BREAK)) {
			return true;
		}
		// 木乃伊的詛咒
		if (hasSkillEffect(CURSE_PARALYZE)) {
			return true;
		}
		return false;
	}

	private boolean _paralyzed;// 麻痺狀態

	/**
	 * 是否為麻痺狀態
	 * 
	 * @return true:麻痺狀態 false:無
	 */
	public boolean isParalyzed() {
		return _paralyzed;
	}

	/**
	 * 設定麻痺狀態
	 * 
	 * @param paralyzed true:麻痺狀態 false:無
	 */
	public void setParalyzed(final boolean paralyzed) {
		_paralyzed = paralyzed;
	}

	L1Paralysis _paralysis;

	public L1Paralysis getParalysis() {
		return _paralysis;
	}

	public void setParalaysis(final L1Paralysis p) {
		_paralysis = p;
	}

	public void cureParalaysis() {
		if (_paralysis != null) {
			_paralysis.cure();
		}
	}

	/**
	 * 該物件全部可見範圍封包發送(不包含自己)
	 * 
	 * @param packet 封包
	 */
	public void broadcastPacketAll(final ServerBasePacket packet) {
		try {
			for (final L1PcInstance pc : World.get().getVisiblePlayer(this)) {
				// 副本ID相等
				if (pc.get_showId() == get_showId()) {
					pc.sendPackets(packet);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void broadcastPacketForWeapon(final ServerBasePacket packet) {
		try {
			for (final L1PcInstance pc : World.get().getVisiblePlayer(this)) {
				if (!pc.is_send_weapon_gfxid()) {
					continue;
				}
				if (!isInvisble()) {
					if (pc.get_showId() == get_showId()) {
						pc.sendPackets(packet);
					}
				}
				// 副本ID相等
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 該物件指定範圍封包發送(範圍10)
	 * 
	 * @param packet 封包
	 */
	public void broadcastPacketX10(final ServerBasePacket packet) {
		try {
			for (final L1PcInstance pc : World.get().getVisiblePlayer(this, 10)) {
				// 副本ID相等
				if (pc.get_showId() == get_showId()) {
					pc.sendPackets(packet);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 該物件指定範圍封包發送(範圍8)
	 * 
	 * @param packet 封包
	 */
	public void broadcastPacketX8(final ServerBasePacket packet) {
		try {
			for (final L1PcInstance pc : World.get().getVisiblePlayer(this, 10)) {
				// 副本ID相等
				if (pc.get_showId() == get_showId()) {
					pc.sendPackets(packet);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 該物件指定範圍封包發送(指定範圍)
	 * 
	 * @param packet 封包
	 * @param r 指定範圍
	 */
	public void broadcastPacketXR(final ServerBasePacket packet, final int r) {
		try {
			for (final L1PcInstance pc : World.get().getVisiblePlayer(this, r)) {
				// 副本ID相等
				if (pc.get_showId() == get_showId()) {
					pc.sendPackets(packet);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 該物件50格範圍封包發送
	 * 
	 * @param packet 封包
	 */
	public void wideBroadcastPacket(final ServerBasePacket packet) {
		for (final L1PcInstance pc : World.get().getVisiblePlayer(this, 50)) {
			// 副本ID相等
			if (pc.get_showId() == get_showId()) {
				pc.sendPackets(packet);
			}
		}
	}

	/**
	 * 該物件可見範圍封包發送, (指定物件)
	 * 
	 * @param packet 封包
	 * @param target 指定物件
	 */
	public void broadcastPacketExceptTargetSight(final ServerBasePacket packet, final L1Character target) {
		boolean isX8 = false;
		// 檢查城堡戰爭狀態
		if (ServerWarExecutor.get().checkCastleWar() > 0) {
			isX8 = true;
			;
		}
		if (isX8) {
			for (final L1PcInstance tgpc : World.get().getVisiblePlayerExceptTargetSight(this, target, 8)) {
				tgpc.sendPackets(packet);
			}

		} else {
			for (final L1PcInstance tgpc : World.get().getVisiblePlayerExceptTargetSight(this, target)) {
				tgpc.sendPackets(packet);
			}
		}
	}

	// 正向
	protected static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	protected static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/**
	 * キャラクターの正面の座標を返す。
	 * 
	 * @return 正面の座標
	 */
	public int[] getFrontLoc() {
		final int[] loc = new int[2];
		int x = getX();
		int y = getY();
		final int heading = getHeading();

		x += HEADING_TABLE_X[heading];
		y += HEADING_TABLE_Y[heading];

		loc[0] = x;
		loc[1] = y;
		return loc;
	}

	/**
	 * 指定座標對硬的面向
	 * 
	 * @param tx 座標 X値
	 * @param ty 座標 Y値
	 * @return 指定座標對硬的面向
	 */
	public int targetDirection(final int tx, final int ty) {

		final float dis_x = Math.abs(getX() - tx); // X點方向距離
		final float dis_y = Math.abs(getY() - ty); // Y點方向距離
		final float dis = Math.max(dis_x, dis_y); // 取回2者最大質
		if (dis == 0) {
			return getHeading(); // 距離為0表示不須改變面向
		}
		final int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 上下左右がちょっと優先な丸め
		final int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 上下左右がちょっと優先な丸め

		int dir_x = 0;
		int dir_y = 0;
		if (getX() < tx) {
			dir_x = 1;
		}
		if (getX() > tx) {
			dir_x = -1;
		}
		if (getY() < ty) {
			dir_y = 1;
		}
		if (getY() > ty) {
			dir_y = -1;
		}

		if (avg_x == 0) {
			dir_x = 0;
		}
		if (avg_y == 0) {
			dir_y = 0;
		}

		switch (dir_x) {
		case -1:
			switch (dir_y) {
			case -1:
				return 7; // 左
			case 0:
				return 6; // 左下
			case 1:
				return 5; // 下
			}
			break;
		case 0:
			switch (dir_y) {
			case -1:
				return 0; // 左上
			case 1:
				return 4; // 右下
			}
			break;
		case 1:
			switch (dir_y) {
			case -1:
				return 1; // 上
			case 0:
				return 2; // 右上
			case 1:
				return 3; // 右
			}
			break;
		}
		return getHeading(); // ここにはこない。はず
	}

	/**
	 * 指定された座標までの直線上に、障害物が存在*しないか*を返す。
	 * 
	 * @param tx 座標のX値
	 * @param ty 座標のY値
	 * @return 障害物が無ければtrue、あればfalseを返す。
	 */
	public boolean glanceCheck(final int tx, final int ty) {
		final L1Map map = getMap();
		int chx = getX();
		int chy = getY();
		// final int arw = 0;

		for (int i = 0; i < 15; i++) {
			if (((chx == tx) && (chy == ty)) || (((chx + 1) == tx) && ((chy - 1) == ty))
					|| (((chx + 1) == tx) && (chy == ty)) || (((chx + 1) == tx) && ((chy + 1) == ty))
					|| ((chx == tx) && ((chy + 1) == ty)) || (((chx - 1) == tx) && ((chy + 1) == ty))
					|| (((chx - 1) == tx) && (chy == ty)) || (((chx - 1) == tx) && ((chy - 1) == ty))
					|| ((chx == tx) && ((chy - 1) == ty))) {
				break;

			} else {
				final int th = targetDirection(tx, ty);
				if (!map.isArrowPassable(chx, chy, th)) {
					return false;
				}
				if (chx < tx) {
					if (chy == ty) {
						chx++;

					} else if (chy > ty) {
						chx++;
						chy--;

					} else if (chy < ty) {
						chx++;
						chy++;

					}

				} else if (chx == tx) {
					if (chy < ty) {
						chy++;

					} else if (chy > ty) {
						chy--;
					}

				} else if (chx > tx) {
					if (chy == ty) {
						chx--;

					} else if (chy < ty) {
						chx--;
						chy++;

					} else if (chy > ty) {
						chx--;
						chy--;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 指定された座標へ攻撃可能であるかを返す。
	 * 
	 * @param x 座標のX値。
	 * @param y 座標のY値。
	 * @param range 攻撃可能な範囲(タイル数)
	 * @return 攻撃可能であればtrue,不可能であればfalse
	 */
	public boolean isAttackPosition(final int x, final int y, final int range) {
		if (range >= 7) {// 遠隔武器（７以上の場合斜めを考慮すると画面外に出る)
			if (getLocation().getTileDistance(new Point(x, y)) > range) {
				return false;
			}

		} else {// 近接武器
			if (getLocation().getTileLineDistance(new Point(x, y)) > range) {
				return false;
			}
		}
		return glanceCheck(x, y);
	}

	/**
	 * 傳回背包資料
	 * 
	 * @return L1Inventory
	 */
	public L1Inventory getInventory() {
		return null;
	}

	/**
	 * キャラクターへ、新たにスキル効果を追加する。
	 * 
	 * @param skillId 追加する効果のスキルID。
	 * @param timeMillis 追加する効果の持続時間。無限の場合は0。
	 */
	private void addSkillEffect(final int skillId, final int timeMillis) {
		_skillEffect.put(skillId, timeMillis / 1000);

		if (_future == null) {
			_future = GeneralThreadPool.get().scheduleAtFixedRate(L1SkillTimerCreator.create(this), 1000,
					1000);
		}
	}

	/**
	 * キャラクターへ、スキル効果を設定する。<br>
	 * 重複するスキルがない場合は、新たにスキル効果を追加する。<br>
	 * 重複するスキルがある場合は、残り効果時間とパラメータの効果時間の長い方を優先して設定する。
	 * 
	 * @param skillId 設定する効果のスキルID。
	 * @param timeMillis 效果時間(單位:毫秒)
	 */
	public void setSkillEffect(final int skillId, int timeMillis) {
		if (this instanceof L1PcInstance) { // XXX 暫時性替代
			if ((skillId >= L1SkillId.DS_GX00) && (skillId <= L1SkillId.DS_ASX09)) {
				final L1PcInstance srcpc = (L1PcInstance) this;
				srcpc.sendPackets(new S_ServerMessage(skillId - 1343));
			}
		}

		if ((timeMillis != 0) && (timeMillis < 1000)) {
			timeMillis = 1000;
		}
		final Integer timer = _skillEffect.get(skillId);
		if (timer != null) {
			final int remainingTimeMills = timer;
			// 残り時間が有限で、パラメータの効果時間の方が長いか無限の場合は上書きする。
			if ((remainingTimeMills >= 0) && ((remainingTimeMills < timeMillis) || (timeMillis == 0))) {
				_skillEffect.put(skillId, timeMillis / 1000);
			}

		} else {
			addSkillEffect(skillId, timeMillis);
		}
	}

	/**
	 * 技能效果結束
	 * 
	 * @param skillId 技能編號
	 */
	public void removeSkillEffect(final int skillId) {
		final Integer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			L1SkillStop.stopSkill(this, skillId);
		}

		if ((_future != null) && _skillEffect.isEmpty()) {
			_future.cancel(true);
			_future = null;
		}
	}

	/**
	 * 刪除全部技能效果
	 */
	public void clearAllSkill() {
		for (final Integer timer : _skillEffect.values()) {
			L1SkillStop.stopSkill(this, timer.intValue());
		}
		_skillEffect.clear();

		if (_future != null) {
			_future.cancel(true);
			_future = null;
		}
	}

	/**
	 * 指定技能效果消除
	 * 
	 * @param skillId 技能編號
	 */
	public void killSkillEffectTimer(final int skillId) {
		_skillEffect.remove(skillId);

		if ((_future != null) && _skillEffect.isEmpty()) {
			_future.cancel(true);
			_future = null;
		}
	}

	/**
	 * 刪除全部技能效果
	 */
	public void clearSkillEffectTimer() {
		_skillEffect.clear();

		if (_future != null) {
			_future.cancel(true);
			_future = null;
		}
	}

	/**
	 * 是否有該技能效果
	 * 
	 * @param skillId
	 * @return 有true 無false。
	 */
	public boolean hasSkillEffect(final int skillId) {
		return _skillEffect.containsKey(skillId);
	}

	/**
	 * 該物件目前具有的技能編號
	 * 
	 * @return
	 */
	public Set<Integer> getSkillEffect() {
		return _skillEffect.keySet();
	}

	/**
	 * 該物件目前具有技能狀態
	 * 
	 * @return true:沒有 false:有
	 */
	public boolean getSkillisEmpty() {
		return _skillEffect.isEmpty();
	}

	/**
	 * 技能效果剩餘時間
	 * 
	 * @param skillId
	 * @return 剩餘秒數 無時間限制傳回-1
	 */
	public int getSkillEffectTimeSec(final int skillId) {
		final Integer timer = _skillEffect.get(skillId);
		return timer == null ? -1 : timer.intValue();
	}

	public final ConcurrentHashMap<Integer, Integer> getSkillEffectList() {
		return _skillEffect;
	}

	private boolean _isSkillDelay = false;

	/**
	 * 設定技能施放延遲中
	 * 
	 * @param flag true:是 false:否
	 */
	public void setSkillDelay(final boolean flag) {
		_isSkillDelay = flag;
	}

	/**
	 * 是否在技能施放延遲中
	 * 
	 * @return true:是 false:否
	 */
	public boolean isSkillDelay() {
		return _isSkillDelay;
	}

	/**
	 * 物件使用延遲編號設置
	 * 
	 * @param delayId 延遲編號
	 * @param timer 時間(毫秒)
	 */
	public void addItemDelay(final int delayId, final L1ItemDelay.ItemDelayTimer timer) {
		_itemdelay.put(delayId, timer);
	}

	/**
	 * 物件使用延遲編號移除
	 * 
	 * @param delayId 延遲編號
	 */
	public void removeItemDelay(final int delayId) {
		_itemdelay.remove(delayId);
	}

	/**
	 * 是否為延遲使用的物件
	 * 
	 * @param delayId 延遲編號
	 * @return true:是 false:否
	 */
	public boolean hasItemDelay(final int delayId) {
		return _itemdelay.containsKey(delayId);
	}

	/**
	 * 是否為延遲使用的物件
	 * 
	 * @param delayId 延遲編號
	 * @return 物件延遲設置
	 */
	public L1ItemDelay.ItemDelayTimer getItemDelayTimer(final int delayId) {
		return _itemdelay.get(delayId);
	}

	/**
	 * 加入寵物清單
	 * 
	 * @param npc
	 */
	public void addPet(final L1NpcInstance npc) {
		_petlist.put(npc.getId(), npc);
		// 加入寵物控制介面
		sendPetCtrlMenu(npc, true);
	}

	/**
	 * 移除寵物清單
	 * 
	 * @param npc
	 */
	public void removePet(final L1NpcInstance npc) {
		_petlist.remove(npc.getId());
		// 移除寵物控制介面
		sendPetCtrlMenu(npc, false);
	}

	/**
	 * 傳回寵物控制清單
	 * 
	 * @return
	 */
	public Map<Integer, L1NpcInstance> getPetList() {
		return _petlist;
	}

	/**
	 * 寵物選單控制
	 * 
	 * @param npc
	 * @param type true:顯示 false:關閉
	 */
	private final void sendPetCtrlMenu(final L1NpcInstance npc, final boolean type) {
		if (npc instanceof L1PetInstance) {
			final L1PetInstance pet = (L1PetInstance) npc;
			final L1Character cha = pet.getMaster();

			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PetCtrlMenu(pc, pet, type));

				if (type) {
					pc.sendPackets(new S_HPMeter(pet));
				}
			}

		} else if (npc instanceof L1SummonInstance) {
			final L1SummonInstance summon = (L1SummonInstance) npc;
			final L1Character cha = summon.getMaster();

			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PetCtrlMenu(pc, summon, type));

				if (type) {
					pc.sendPackets(new S_HPMeter(summon));
				}
			}
		}
	}

	private final Map<Integer, L1DollInstance> _dolls = new HashMap<Integer, L1DollInstance>();

	/**
	 * 設置娃娃
	 * 
	 * @param doll
	 */
	public void addDoll(final L1DollInstance doll) {
		_dolls.put(doll.getItemObjId(), doll);
	}

	/**
	 * 移除娃娃
	 */
	public void removeDoll(final L1DollInstance doll) {
		_dolls.remove(doll.getItemObjId());
	}

	/**
	 * 移除娃娃
	 * 
	 * @return
	 */
	public L1DollInstance getDoll(final int key) {
		return _dolls.get(key);
	}

	/**
	 * 目前攜帶的娃娃
	 * 
	 * @return 目前攜帶的娃娃
	 */
	public Map<Integer, L1DollInstance> getDolls() {
		return _dolls;
	}

	private L1DollInstance _power_doll = null;// 超級娃娃

	/**
	 * 設置超級娃娃
	 * 
	 * @param doll
	 */
	public void add_power_doll(final L1DollInstance doll) {
		_power_doll = doll;
	}

	/**
	 * 移除超級娃娃
	 */
	public void remove_power_doll() {
		_power_doll = null;
	}

	/**
	 * 目前攜帶的超級娃娃
	 * 
	 * @return 目前攜帶的超級娃娃
	 */
	public L1DollInstance get_power_doll() {
		return _power_doll;
	}

	/**
	 * 加入跟隨者
	 * 
	 * @param follower 跟隨者
	 */
	public void addFollower(final L1FollowerInstance follower) {
		_followerlist.put(follower.getId(), follower);
	}

	/**
	 * 移除跟隨者
	 * 
	 * @param follower 跟隨者
	 */
	public void removeFollower(final L1FollowerInstance follower) {
		_followerlist.remove(follower.getId());
	}

	/**
	 * 傳回跟隨者
	 * 
	 * @return 跟隨者清單
	 */
	public Map<Integer, L1FollowerInstance> getFollowerList() {
		return _followerlist;
	}

	/**
	 * キャラクターへ、毒を追加する。
	 * 
	 * @param poison 毒を表す、L1Poisonオブジェクト。
	 */
	public void setPoison(final L1Poison poison) {
		_poison = poison;
	}

	/**
	 * キャラクターの毒を治療する。
	 */
	public void curePoison() {
		if (_poison == null) {
			return;
		}
		_poison.cure();
	}

	/**
	 * キャラクターの毒状態を返す。
	 * 
	 * @return キャラクターの毒を表す、L1Poisonオブジェクト。
	 */
	public L1Poison getPoison() {
		return _poison;
	}

	/**
	 * キャラクターへ毒のエフェクトを付加する
	 * 
	 * @param effectId
	 * @see S_Poison#S_Poison(int, int)
	 */
	public void setPoisonEffect(final int effectId) {
		broadcastPacketX8(new S_Poison(getId(), effectId));
	}

	/**
	 * 所在位置區域屬性返回
	 * 
	 * @return 0一般區域 1安全區域 -1戰鬥區域
	 */
	public int getZoneType() {
		if (getMap().isSafetyZone(getLocation())) {
			return 1;
		} else if (getMap().isCombatZone(getLocation())) {
			return -1;
		} else { // ノーマルゾーン
			return 0;
		}
	}

	/**
	 * 位於安全區域中
	 * 
	 * @return
	 */
	public boolean isSafetyZone() {
		if (getMap().isSafetyZone(getLocation())) {
			return true;
		}
		return false;
	}

	/**
	 * 位於戰鬥區域中
	 * 
	 * @return
	 */
	public boolean isCombatZone() {
		if (getMap().isCombatZone(getLocation())) {
			return true;
		}
		return false;
	}

	/**
	 * 位於一般區域中
	 * 
	 * @return
	 */
	public boolean isNoneZone() {
		return getZoneType() == 0;
	}

	private long _exp; // ● 経験値

	/**
	 * キャラクターが保持している経験値を返す。
	 * 
	 * @return 経験値。
	 */
	public long getExp() {
		return _exp;
	}

	/**
	 * キャラクターが保持する経験値を設定する。
	 * 
	 * @param exp 経験値。
	 */
	public void setExp(final long exp) {
		_exp = exp;
	}

	// ■■■■■■■■■■ L1PcInstanceへ移動するプロパティ ■■■■■■■■■■
	private final List<L1Object> _knownObjects = new CopyOnWriteArrayList<L1Object>();
	private final List<L1PcInstance> _knownPlayer = new CopyOnWriteArrayList<L1PcInstance>();

	/**
	 * 是否為已認識物件
	 * 
	 * @param obj 判斷對象
	 * @return true:是 false:不是
	 */
	public boolean knownsObject(final L1Object obj) {
		return _knownObjects.contains(obj);
	}

	/**
	 * 全部認識物件(L1Object)清單
	 * 
	 * @return 全部認識物件(L1Object)清單
	 */
	public List<L1Object> getKnownObjects() {
		return _knownObjects;
	}

	/**
	 * 全部認識物件(PC)清單
	 * 
	 * @return 全部認識物件(PC)清單
	 */
	public List<L1PcInstance> getKnownPlayers() {
		return _knownPlayer;
	}

	/**
	 * 加入已認識物件
	 * 
	 * @param obj 加入對象
	 */
	public void addKnownObject(final L1Object obj) {
		if (!_knownObjects.contains(obj)) {
			_knownObjects.add(obj);
			if (obj instanceof L1PcInstance) {
				_knownPlayer.add((L1PcInstance) obj);
			}
		}
	}

	/**
	 * 移出已認識物件
	 * 
	 * @param obj 移出對象
	 */
	public void removeKnownObject(final L1Object obj) {
		_knownObjects.remove(obj);
		if (obj instanceof L1PcInstance) {
			_knownPlayer.remove(obj);
		}
	}

	/**
	 * 全部認識對象移除
	 */
	public void removeAllKnownObjects() {
		_knownObjects.clear();
		_knownPlayer.clear();
	}

	// ■■■■■■■■■■ プロパティ ■■■■■■■■■■

	private String _name; // ● 名前

	public String getName() {
		return _name;
	}

	public void setName(final String s) {
		_name = s;
	}

	private int _level; // ● レベル

	public synchronized int getLevel() {
		return _level;
	}

	public synchronized void setLevel(final int level) {
		_level = level;
	}

	private int _maxHp = 0; // 最大HP量(MOB 1~100000, 其他 1~32767)
	private int _trueMaxHp = 0; // ● 本当のＭＡＸＨＰ

	/**
	 * 最大HP量
	 * 
	 * @return
	 */
	public int getMaxHp() {
		return (_maxHp);
	}

	/**
	 * 最大HP
	 * 
	 * @param hp
	 */
	public void setMaxHp(final int hp) {
		_trueMaxHp = hp;
		if (this instanceof L1PcInstance) {
			_maxHp = (short) RangeInt.ensure(_trueMaxHp, 1, 32767);
		} else {
			_maxHp = RangeInt.ensure(_trueMaxHp, 1, 500000);
		}
		//_maxHp = RangeInt.ensure(_trueMaxHp, 1, 500000);
		_currentHp = Math.min(_currentHp, _maxHp);
	}

	/**
	 * 增加(減少)HP上限
	 * 
	 * @param i
	 */
	public void addMaxHp(final int i) {
		setMaxHp(_trueMaxHp + i);
	}

	private short _maxMp = 0; // ● ＭＡＸＭＰ（0〜32767）
	private int _trueMaxMp = 0; // ● 本当のＭＡＸＭＰ

	/**
	 * 最大MP量
	 * 
	 * @return
	 */
	public short getMaxMp() {
		return (_maxMp);
	}

	/**
	 * 最大MP
	 * 
	 * @param mp
	 */
	public void setMaxMp(final int mp) {
		_trueMaxMp = mp;
		_maxMp = (short) RangeInt.ensure(_trueMaxMp, 0, 32767);
		_currentMp = Math.min(_currentMp, _maxMp);
	}

	/**
	 * 增加(減少)MP上限
	 * 
	 * @param i
	 */
	public void addMaxMp(final int i) {
		setMaxMp(_trueMaxMp + i);
	}

	private int _ac = 10; // ● ＡＣ（-211〜44）
	private int _trueAc = 0; // ● 本当のＡＣ

	public int getAc() {
		int ac = _ac;
		if (this instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) this;
			switch (pc.guardianEncounter()) {
			case 0:// 正義的守護 Lv1
				ac -= 2;
				break;

			case 1:// 正義的守護 Lv2
				ac -= 4;
				break;

			case 2:// 正義的守護 Lv3
				ac -= 6;
				break;
			}
		}
		return RangeInt.ensure(ac, -211, 44);
	}

	public void setAc(final int i) {
		_trueAc = i;
		_ac = RangeInt.ensure(i, -211, 44);
	}

	/**
	 * 增加(減少)防禦力
	 * 
	 * @param i
	 */
	public void addAc(final int i) {
		setAc(_trueAc + i);
	}

	private short _str = 0; // ● ＳＴＲ（1〜127）
	private short _trueStr = 0; // ● 本当のＳＴＲ

	/**
	 * 力量
	 * 
	 * @return
	 */
	public short getStr() {
		return (_str);
	}

	public void setStr(final int i) {
		_trueStr = (short) i;
		_str = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)力量
	 * 
	 * @param i
	 */
	public void addStr(final int i) {
		setStr(_trueStr + i);
	}

	private short _con = 0; // ● ＣＯＮ（1〜127）
	private short _trueCon = 0; // ● 本当のＣＯＮ

	/**
	 * 體質
	 * 
	 * @return
	 */
	public short getCon() {
		return (_con);
	}

	public void setCon(final int i) {
		_trueCon = (short) i;
		_con = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)體質
	 * 
	 * @param i
	 */
	public void addCon(final int i) {
		setCon(_trueCon + i);
	}

	private short _dex = 0; // ● ＤＥＸ（1〜127）
	private short _trueDex = 0; // ● 本当のＤＥＸ

	/**
	 * 敏捷
	 * 
	 * @return
	 */
	public short getDex() {
		return (_dex);
	}

	public void setDex(final int i) {
		_trueDex = (short) i;
		_dex = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)敏捷
	 * 
	 * @param i
	 */
	public void addDex(final int i) {
		setDex(_trueDex + i);
	}

	private short _cha = 0; // ● ＣＨＡ（1〜127）
	private short _trueCha = 0; // ● 本当のＣＨＡ

	/**
	 * 魅力
	 * 
	 * @return
	 */
	public short getCha() {
		return (_cha);
	}

	public void setCha(final int i) {
		_trueCha = (short) i;
		_cha = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)魅力
	 * 
	 * @param i
	 */
	public void addCha(final int i) {
		setCha(_trueCha + i);
	}

	private short _int = 0; // ● ＩＮＴ（1〜127）
	private short _trueInt = 0; // ● 本当のＩＮＴ

	/**
	 * 智力
	 * 
	 * @return
	 */
	public short getInt() {
		return (_int);
	}

	public void setInt(final int i) {
		_trueInt = (short) i;
		_int = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)智力
	 * 
	 * @param i
	 */
	public void addInt(final int i) {
		setInt(_trueInt + i);
	}

	private short _wis = 0; // ● ＷＩＳ（1〜127）
	private short _trueWis = 0; // ● 本当のＷＩＳ

	/**
	 * 精神
	 * 
	 * @return
	 */
	public short getWis() {
		return (_wis);
	}

	public void setWis(final int i) {
		_trueWis = (short) i;
		_wis = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)精神
	 * 
	 * @param i
	 */
	public void addWis(final int i) {
		setWis(_trueWis + i);
	}

	private int _wind = 0; // ● 風防御（-128〜127）
	private int _trueWind = 0; // ● 本当の風防御

	/**
	 * 風屬性
	 * 
	 * @return
	 */
	public int getWind() {
		return _wind;
	} // 使用するとき

	/**
	 * 增加(減少)風屬性
	 * 
	 * @param i
	 */
	public void addWind(final int i) {
		_trueWind += i;
		if (_trueWind >= 127) {
			_wind = 127;
		} else if (_trueWind <= -128) {
			_wind = -128;
		} else {
			_wind = _trueWind;
		}
	}

	private int _water = 0; // ● 水防御（-128〜127）
	private int _trueWater = 0; // ● 本当の水防御

	/**
	 * 水屬性
	 * 
	 * @return
	 */
	public int getWater() {
		return _water;
	} // 使用するとき

	/**
	 * 增加(減少)水屬性
	 * 
	 * @param i
	 */
	public void addWater(final int i) {
		_trueWater += i;
		if (_trueWater >= 127) {
			_water = 127;
		} else if (_trueWater <= -128) {
			_water = -128;
		} else {
			_water = _trueWater;
		}
	}

	private int _fire = 0; // ● 火防御（-128〜127）
	private int _trueFire = 0; // ● 本当の火防御

	/**
	 * 火屬性
	 * 
	 * @return
	 */
	public int getFire() {
		return _fire;
	} // 使用するとき

	/**
	 * 增加(減少)火屬性
	 * 
	 * @param i
	 */
	public void addFire(final int i) {
		_trueFire += i;
		if (_trueFire >= 127) {
			_fire = 127;
		} else if (_trueFire <= -128) {
			_fire = -128;
		} else {
			_fire = _trueFire;
		}
	}

	private int _earth = 0; // ● 地防御（-128〜127）
	private int _trueEarth = 0; // ● 本当の地防御

	/**
	 * 地屬性
	 * 
	 * @return
	 */
	public int getEarth() {
		return _earth;
	} // 使用するとき

	/**
	 * 增加(減少)地屬性
	 * 
	 * @param i
	 */
	public void addEarth(final int i) {
		_trueEarth += i;
		if (_trueEarth >= 127) {
			_earth = 127;

		} else if (_trueEarth <= -128) {
			_earth = -128;

		} else {
			_earth = _trueEarth;
		}
	}

	private int _addAttrKind; // エレメンタルフォールダウンで減少した属性の種類

	public int getAddAttrKind() {
		return _addAttrKind;
	}

	public void setAddAttrKind(final int i) {
		_addAttrKind = i;
	}

	// 暈眩耐性
	private int _registStun = 0;
	private int _trueRegistStun = 0;

	/**
	 * 暈眩耐性
	 * 
	 * @return
	 */
	public int getRegistStun() {
		return _registStun;
	} // 使用するとき

	/**
	 * 暈眩耐性
	 * 
	 * @param i
	 */
	public void addRegistStun(final int i) {
		_trueRegistStun += i;
		if (_trueRegistStun > 127) {
			_registStun = 127;
		} else if (_trueRegistStun < -128) {
			_registStun = -128;
		} else {
			_registStun = _trueRegistStun;
		}
	}

	// 石化耐性
	private int _registStone = 0;
	private int _trueRegistStone = 0;

	/**
	 * 石化耐性
	 * 
	 * @return
	 */
	public int getRegistStone() {
		return _registStone;
	}

	/**
	 * 石化耐性
	 * 
	 * @param i
	 */
	public void addRegistStone(final int i) {
		_trueRegistStone += i;
		if (_trueRegistStone > 127) {
			_registStone = 127;
		} else if (_trueRegistStone < -128) {
			_registStone = -128;
		} else {
			_registStone = _trueRegistStone;
		}
	}

	// 睡眠耐性
	private int _registSleep = 0;
	private int _trueRegistSleep = 0;

	/**
	 * 睡眠耐性
	 * 
	 * @return
	 */
	public int getRegistSleep() {
		return _registSleep;
	}

	/**
	 * 睡眠耐性
	 * 
	 * @param i
	 */
	public void addRegistSleep(final int i) {
		_trueRegistSleep += i;
		if (_trueRegistSleep > 127) {
			_registSleep = 127;
		} else if (_trueRegistSleep < -128) {
			_registSleep = -128;
		} else {
			_registSleep = _trueRegistSleep;
		}
	}

	// 凍結耐性
	private int _registFreeze = 0;
	private int _trueRegistFreeze = 0;

	/**
	 * 寒冰耐性
	 * 
	 * @return
	 */
	public int getRegistFreeze() {
		return _registFreeze;
	}

	/**
	 * 寒冰耐性
	 * 
	 * @param i
	 */
	public void addRegistFreeze(final int i) {
		_trueRegistFreeze += i;
		if (_trueRegistFreeze > 127) {
			_registFreeze = 127;
		} else if (_trueRegistFreeze < -128) {
			_registFreeze = -128;
		} else {
			_registFreeze = _trueRegistFreeze;
		}
	}

	// 支撐耐性
	private int _registSustain = 0;
	private int _trueRegistSustain = 0;

	/**
	 * 支撐耐性
	 * 
	 * @return
	 */
	public int getRegistSustain() {
		return _registSustain;
	}

	/**
	 * 支撐耐性
	 * 
	 * @param i
	 */
	public void addRegistSustain(final int i) {
		_trueRegistSustain += i;
		if (_trueRegistSustain > 127) {
			_registSustain = 127;
		} else if (_trueRegistSustain < -128) {
			_registSustain = -128;
		} else {
			_registSustain = _trueRegistSustain;
		}
	}

	// 暗黑耐性
	private int _registBlind = 0;
	private int _trueRegistBlind = 0;

	/**
	 * 暗黑耐性
	 * 
	 * @return
	 */
	public int getRegistBlind() {
		return _registBlind;
	}

	/**
	 * 暗黑耐性
	 * 
	 * @param i
	 */
	public void addRegistBlind(final int i) {
		_trueRegistBlind += i;
		if (_trueRegistBlind > 127) {
			_registBlind = 127;
		} else if (_trueRegistBlind < -128) {
			_registBlind = -128;
		} else {
			_registBlind = _trueRegistBlind;
		}
	}

	private int _dmgup = 0; // ● ダメージ補正（-128〜127）
	private int _trueDmgup = 0; // ● 本当のダメージ補正

	/**
	 * 傷害增加
	 * 
	 * @return
	 */
	public int getDmgup() {
		return _dmgup;
	} // 使用するとき

	/**
	 * 傷害增加
	 * 
	 * @param i
	 */
	public void addDmgup(final int i) {
		_trueDmgup += i;
		if (_trueDmgup >= 127) {
			_dmgup = 127;
		} else if (_trueDmgup <= -128) {
			_dmgup = -128;
		} else {
			_dmgup = _trueDmgup;
		}
	}

	private int _bowDmgup = 0; // ● 弓ダメージ補正（-128〜127）
	private int _trueBowDmgup = 0; // ● 本当の弓ダメージ補正

	/**
	 * 遠距離傷害增加
	 * 
	 * @return
	 */
	public int getBowDmgup() {
		return _bowDmgup;
	} // 使用するとき

	/**
	 * 遠距離傷害增加
	 * 
	 * @param i
	 */
	public void addBowDmgup(final int i) {
		_trueBowDmgup += i;
		if (_trueBowDmgup >= 127) {
			_bowDmgup = 127;
		} else if (_trueBowDmgup <= -128) {
			_bowDmgup = -128;
		} else {
			_bowDmgup = _trueBowDmgup;
		}
	}

	private int _hitup = 0; // ● 命中補正（-128〜127）
	private int _trueHitup = 0; // ● 本当の命中補正

	/**
	 * 命中增加
	 * 
	 * @return
	 */
	public int getHitup() {
		return _hitup;
	} // 使用するとき

	/**
	 * 命中增加
	 * 
	 * @param i
	 */
	public void addHitup(final int i) {
		_trueHitup += i;
		if (_trueHitup >= 127) {
			_hitup = 127;
		} else if (_trueHitup <= -128) {
			_hitup = -128;
		} else {
			_hitup = _trueHitup;
		}
	}

	private int _bowHitup = 0; // ● 弓命中補正（-128〜127）
	private int _trueBowHitup = 0; // ● 本当の弓命中補正

	/**
	 * 遠距離命中增加
	 * 
	 * @return
	 */
	public int getBowHitup() {
		return _bowHitup;
	} // 使用するとき

	/**
	 * 遠距離命中增加
	 * 
	 * @param i
	 */
	public void addBowHitup(final int i) {
		_trueBowHitup += i;
		if (_trueBowHitup >= 127) {
			_bowHitup = 127;
		} else if (_trueBowHitup <= -128) {
			_bowHitup = -128;
		} else {
			_bowHitup = _trueBowHitup;
		}
	}

	private int _mr = 0; // ● 魔法防御（0〜）
	private int _trueMr = 0; // ● 本当の魔法防御

	/**
	 * 魔防
	 * 
	 * @return
	 */
	public int getMr() {
		if (hasSkillEffect(ERASE_MAGIC) == true) {// 魔法消除
			return _mr >> 2;// / 4;
		} else {
			return _mr;
		}
	} // 使用するとき

	public int getTrueMr() {
		return _trueMr;
	} // セットするとき

	/**
	 * 魔防
	 * 
	 * @param i
	 */
	public void addMr(final int i) {
		_trueMr += i;
		if (_trueMr <= 0) {
			_mr = 0;
		} else {
			_mr = _trueMr;
		}
	}

	private int _sp = 0; // ● 増加したＳＰ

	/**
	 * 魔功
	 * 
	 * @return
	 */
	public int getSp() {
		return getTrueSp() + _sp;
	}

	/**
	 * 魔功
	 * 
	 * @return
	 */
	public int getTrueSp() {
		return getMagicLevel() + getMagicBonus();
	}

	/**
	 * 增加魔功
	 * 
	 * @param i
	 */
	public void addSp(final int i) {
		_sp += i;
	}

	private boolean _isDead; // 死亡状態

	/**
	 * 死亡狀態
	 * 
	 * @return
	 */
	public boolean isDead() {
		return _isDead;
	}

	/**
	 * 死亡状態
	 * 
	 * @param flag
	 */
	public void setDead(final boolean flag) {
		_isDead = flag;
	}

	private int _status; // 初始化狀態

	/**
	 * 初始化狀態
	 * 
	 * @return
	 */
	public int getStatus() {
		return _status;
	}

	/**
	 * 初始化狀態
	 * 
	 * @param i
	 */
	public void setStatus(final int i) {
		_status = i;
	}

	private String _title; // 封號

	/**
	 * 封號
	 * 
	 * @return
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * 封號
	 * 
	 * @param s
	 */
	public void setTitle(final String s) {
		_title = s;
	}

	private int _lawful; // ● アライメント

	/**
	 * 傳回正義質
	 * 
	 * @return
	 */
	public int getLawful() {
		return _lawful;
	}

	/**
	 * 設定正義質
	 * 
	 * @param i
	 */
	public void setLawful(final int i) {
		_lawful = i;
	}

	/*
	 * public synchronized void addLawful(final int i) { this._lawful += i; if
	 * (this._lawful > 32767) { this._lawful = 32767; } else if (this._lawful <
	 * -32768) { this._lawful = -32768; } }
	 */

	private int _heading; // 面向： 0.左上 1.上 2.右上 3.右 4.右下 5.下 6.左下 7.左

	/**
	 * 面向
	 * 
	 * @return 0:左上 1:上 2:右上 3:右 4:右下 5:下 6:左下 7:左 41517276
	 */
	public int getHeading() {
		return _heading;
	}

	/**
	 * 面向
	 * 
	 * @param i 0:左上 1:上 2:右上 3:右 4:右下 5:下 6:左下 7:左
	 */
	public void setHeading(final int i) {
		_heading = i;
	}

	private int _moveSpeed; // 移動加速狀態(綠水)

	/**
	 * 移動加速狀態(綠水)
	 * 
	 * @return 0:無 1:加速 2:緩速
	 */
	public int getMoveSpeed() {
		return _moveSpeed;
	}

	/**
	 * 移動加速狀態(綠水)
	 * 
	 * @param i 0:無 1:加速 2:緩速
	 */
	public void setMoveSpeed(final int i) {
		_moveSpeed = i;
	}

	private int _braveSpeed; // 攻擊加速狀態(勇水)

	/**
	 * 攻擊加速狀態(勇水)
	 * 
	 * @return 0:無 1:勇水 5:強化勇水
	 */
	public int getBraveSpeed() {
		return _braveSpeed;
	}

	/**
	 * 攻擊加速狀態(勇水)
	 * 
	 * @param i 0:無 1:勇水 5:強化勇水
	 */
	public void setBraveSpeed(final int i) {
		_braveSpeed = i;
	}

	private int _tempCharGfx; // 顯示外型編號

	/**
	 * 傳回顯示外型編號
	 * 
	 * @return
	 */
	public int getTempCharGfx() {
		return _tempCharGfx;
	}

	/**
	 * 設置顯示外型編號
	 * 
	 * @param i
	 */
	public void setTempCharGfx(final int i) {
		_tempCharGfx = i;
	}

	private int _gfxid; // 原始外型編號

	/**
	 * 傳回原始外型編號
	 * 
	 * @return
	 */
	public int getGfxId() {
		return _gfxid;
	}

	/**
	 * 設置原始外型編號
	 * 
	 * @param i
	 */
	public void setGfxId(final int i) {
		_gfxid = i;
	}

	/**
	 * 魔法等級
	 * 
	 * @return
	 */
	public int getMagicLevel() {
		return getLevel() >> 2;// / 4;
	}

	/**
	 * 智力命中魔法追加
	 * 
	 * @return
	 */
	public int getMagicBonus() {
		switch (getInt()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return -2;

		case 6:
		case 7:
		case 8:
			return -1;

		case 9:
		case 10:
		case 11:
			return 0;

		case 12:
		case 13:
		case 14:
			return 1;

		case 15:
		case 16:
		case 17:
			return 2;

		case 18:
			return 3;
		case 19:
			return 4;
		case 20:
			return 5;
		case 21:
			return 6;
		case 22:
			return 7;
		case 23:
			return 8;
		case 24:
			return 9;
		case 25:
		case 26:
		case 27:
			return 10;
		case 28:
		case 29:
		case 30:
			return 11;
		case 31:
		case 32:
		case 33:
			return 12;
		case 34:
		case 35:
		case 36:
			return 13;
		case 37:
		case 38:
		case 39:
			return 14;
		case 40:
		case 41:
		case 42:
		case 43:
			return 15;
		/*
		 * case 44: return 17; case 45: return 18; case 46: return 19; case 47:
		 * return 20; case 48: return 21; case 49: return 22; case 50: return
		 * 23; case 51: return 24; case 52: return 25; case 53: return 26; case
		 * 54: return 27; case 55: return 28; case 56: return 29; case 57:
		 * return 30; case 58: return 31; case 59: return 32; case 60: return
		 * 33; case 61: return 34; case 62: return 35; case 63: return 36; case
		 * 64: return 37; case 65: return 38; case 66: return 39; case 67:
		 * return 40; case 68: return 41; case 69: return 42; case 70: return
		 * 43; case 71: return 44; case 72: return 45; case 73: return 46; case
		 * 74: return 47; case 75: return 48; case 76: return 49; case 77:
		 * return 50; case 78: return 51; case 79: return 52; case 80: return
		 * 53; case 81: return 54; case 82: return 55; case 83: return 56; case
		 * 84: return 57; case 85: return 58; case 86: return 59; case 87:
		 * return 60; case 88: return 61; case 89: return 62; case 90: return
		 * 63;
		 */
		default:
			return getInt() - 28;
		}
	}

	/**
	 * 是否在隱身狀態
	 * 
	 * @return
	 */
	public boolean isInvisble() {
		return (hasSkillEffect(INVISIBILITY) || hasSkillEffect(BLIND_HIDING));
	}

	/**
	 * 治療
	 * 
	 * @param pt 治療質
	 */
	public void healHp(final int pt) {
		setCurrentHp(getCurrentHp() + pt);
	}

	private int _karma;

	/**
	 * キャラクターが保持しているカルマを返す。
	 * 
	 * @return カルマ。
	 */
	public int getKarma() {
		return _karma;
	}

	/**
	 * キャラクターが保持するカルマを設定する。
	 * 
	 * @param karma カルマ。
	 */
	public void setKarma(final int karma) {
		_karma = karma;
	}

	public void setMr(final int i) {
		_trueMr = i;
		if (_trueMr <= 0) {
			_mr = 0;
		} else {
			_mr = _trueMr;
		}
	}

	/**
	 * 光
	 */
	public void turnOnOffLight() {
		int lightSize = 0x00;
		if (this instanceof L1NpcInstance) {
			final L1NpcInstance npc = (L1NpcInstance) this;
			lightSize = npc.getLightSize(); // npc.sqlのライトサイズ
		}

		for (final L1ItemInstance item : getInventory().getItems()) {
			if ((item.getItem().getType2() == 0) && (item.getItem().getType() == 2)) { // light系アイテム
				final int itemlightSize = item.getItem().getLightRange();
				if ((itemlightSize != 0) && item.isNowLighting()) {
					if (itemlightSize > lightSize) {
						lightSize = itemlightSize;
					}
				}
			}
		}
		// 照明法術
		if (hasSkillEffect(LIGHT)) {
			lightSize = 0x0e;
		}

		// 人物
		if (this instanceof L1PcInstance) {
			if (ConfigOther.LIGHT) {
				lightSize = 0x14;
			}
			final L1PcInstance pc = (L1PcInstance) this;
			pc.sendPackets(new S_Light(pc.getId(), lightSize));
		}

		if (!isInvisble()) {
			broadcastPacketAll(new S_Light(getId(), lightSize));
		}

		setOwnLightSize(lightSize); // S_OwnCharPackのライト範囲
		setChaLightSize(lightSize); // S_OtherCharPack, S_NPCPackなどのライト範囲
	}

	private int _chaLightSize; // ● ライトの範囲

	/**
	 * 物件原始亮度
	 * 
	 * @return
	 */
	public int getChaLightSize() {
		if (isInvisble()) {
			return 0;
		}
		if (ConfigOther.LIGHT) {
			return 14;
		}
		return _chaLightSize;
	}

	/**
	 * 設置原始亮度
	 * 
	 * @param i
	 */
	public void setChaLightSize(final int i) {
		_chaLightSize = i;
	}

	private int _ownLightSize; // ● ライトの範囲(S_OwnCharPack用)

	/**
	 * 傳回附加亮度
	 * 
	 * @return
	 */
	public int getOwnLightSize() {
		if (isInvisble()) {
			return 0;
		}
		if (ConfigOther.LIGHT) {
			return 14;
		}
		return _ownLightSize;
	}

	/**
	 * 設置附加亮度
	 * 
	 * @param i
	 */
	public void setOwnLightSize(final int i) {
		_ownLightSize = i;
	}

	private int _tmp; // 緩存數據

	/**
	 * 傳出 緩存數據
	 * 
	 * @return the _tmp
	 */
	public int get_tmp() {
		return _tmp;
	}

	/**
	 * 設置 緩存數據
	 * 
	 * @param tmp 對 _tmp 進行設置
	 */
	public void set_tmp(final int tmp) {
		_tmp = tmp;
	}

	private int _tmp_mr; // 暫存數據(MR)

	/**
	 * 傳出 暫存數據(MR)
	 * 
	 * @return the _tmp_mr
	 */
	public int get_tmp_mr() {
		return _tmp_mr;
	}

	/**
	 * 設置 暫存數據(MR)
	 * 
	 * @param tmp 對 _tmp_mr 進行設置
	 */
	public void set_tmp_mr(final int tmp) {
		_tmp_mr = tmp;
	}

	// 閃避率 +
	private int _dodge_up = 0;

	/**
	 * 閃避增加
	 * 
	 * @return
	 */
	public int get_dodge() {
		return _dodge_up;
	}

	/**
	 * 閃避增加
	 * 
	 * @param i
	 */
	public void add_dodge(final int i) {
		_dodge_up += i;
		if (_dodge_up > 10) {
			_dodge_up = 10;

		} else if (_dodge_up < 0) {
			_dodge_up = 0;
		}
	}

	// 閃避率 -
	private int _dodge_down = 0;

	/**
	 * 閃避減少
	 * 
	 * @return
	 */
	public int get_dodge_down() {
		return _dodge_down;
	}

	/**
	 * 閃避減少
	 * 
	 * @param i
	 */
	public void add_dodge_down(final int i) {
		_dodge_down += i;
		if (_dodge_down > 10) {
			_dodge_down = 10;

		} else if (_dodge_down < 0) {
			_dodge_down = 0;
		}
	}

	// 受到藥水霜化術
	private boolean _decay_potion = false;

	/**
	 * 受到藥水霜化術
	 * 
	 * @param b
	 */
	public void set_decay_potion(final boolean b) {
		_decay_potion = b;
	}

	/**
	 * 受到藥水霜化術
	 * 
	 * @return
	 */
	public boolean is_decay_potion() {
		return _decay_potion;
	}

	public String getViewName() {
		return _name;
	}

	// 修正技能疊加 objid
	private int _useitemobjid;

	public void setuseitemobjid(final int objid) {
		_useitemobjid = objid;
	}

	public int getuseitemobjid() {
		return _useitemobjid;
	}

	// 隨身祭司補血設定
	private int _hierarch = 0;

	public int getHierarch() {
		return _hierarch;
	}

	public void setHierarch(final int i) {
		_hierarch = i;
	}
	
	private int _innKeyId;

	public int getInnKeyId() {
		return this._innKeyId;
	}

	public void setInnKeyId(int i) {
		this._innKeyId = i;
	}
	
	private int _innRoomNumber;
	
	public int getInnRoomNumber() {
		return _innRoomNumber;
	}

	public void setInnRoomNumber(int i) {
		_innRoomNumber = i;
	}
	
	private boolean _isHall;
	
	public boolean checkRoomOrHall() {
		return _isHall;
	}

	public void setHall(boolean i) {
		_isHall = i;
	}
	
}
