package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.SHOCK_STUN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.DeNameTable;
import com.lineage.server.datatables.DeShopItemTable;
import com.lineage.server.datatables.DeTitleTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_ChangeShape;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_Chat;
import com.lineage.server.serverpackets.S_ChatGlobal;
import com.lineage.server.serverpackets.S_ChatTransaction;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_DoActionShop;
import com.lineage.server.serverpackets.S_Fishing;
import com.lineage.server.serverpackets.S_NPCPack_De;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.DeName;
import com.lineage.server.templates.L1EmblemIcon;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.DeAiThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.utils.Teleportation;
import com.lineage.server.world.World;

/**
 * 對象:虛擬玩家 控制項
 * 
 * @author daien
 */
public class L1DeInstance extends L1NpcInstance { // AI修正 by terry0412

	/** 完整修復假人問題 by terry0412 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1DeInstance.class);

	private static final Random _random = new Random();

	// 召喚過的清單
	private static final ArrayList<DeName> _denameList = new ArrayList<DeName>();

	// 賣出清單
	private final Map<L1ItemInstance, Integer> _sellList = new HashMap<L1ItemInstance, Integer>();

	// 買入清單
	private final Map<Integer, int[]> _buyList = new HashMap<Integer, int[]>();

	private String _shop_chat1 = null;// 商店村對話

	private String _shop_chat2 = null;// 商店村對話

	private boolean _is_shop = false;

	private int _clanid = 0;

	private String _clan_name = null;

	private L1EmblemIcon _emblem = null;

	private DeName _de_name = null;

	private int _classId;// 設置種族

	// 人物外型決定
	private static final int[][] _class_list = new int[][] { new int[] { 0, 61, 138, 734, 2786, 6658, 6671 }, // 男性
			new int[] { 1, 48, 37, 1186, 2796, 6661, 6650 } // 女性
	};

	// 最大思考週期計數器
	private static final int maxThinkingCycle = 40;

	// 判斷附近怪物的距離
	private int thinkingCounter;

	/**
	 * @param template
	 */
	public L1DeInstance(final L1Npc template) {
		super(template);
		// startNpc();
	}

	/*
	 * public L1DeInstance(L1Npc template, DeName de) { super(template);
	 * _de_name = de; startNpc(); }
	 */

	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			if (!_is_shop) {
				start_shop();
			}

			perceivedFrom.addKnownObject(this);
			perceivedFrom.sendPackets(new S_NPCPack_De(this));

			if (isShop()) {
				perceivedFrom.sendPackets(new S_DoActionShop(getId(), _shop_chat1));
				return;
			}

			if (isFishing()) {
				perceivedFrom.sendPackets(new S_Fishing(getId(), ActionCodes.ACTION_Fishing, _fishX, _fishY));
				return;
			}

			onNpcAI(); // AI 開始
			if (getBraveSpeed() == 1) {// 具有勇水狀態
				perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 決定外型狀態 (repaired by terry0412)
	 * 
	 * @param de_name
	 */
	public void startNpc(final DeName de_name) {
		try {
			// 非指定狀態
			if (de_name == null) {
				final DeName[] des = DeNameTable.get().getDeNameList();
				final int length = des.length;
				DeName de = des[_random.nextInt(length)];

				while (_denameList.contains(de)) {
					de = des[_random.nextInt(length)];
				}
				_de_name = de;

			} else {
				_de_name = de_name;
			}

			// 設置為已經召喚過的假人
			_denameList.add(_de_name);

			// 設置名稱
			setNameId(_de_name.get_name());
			// 載入外型
			final int classid = _class_list[_de_name.get_sex()][_de_name.get_type()];
			int starus = 0;
			// 機率決定手持武器
			switch (classid) {
			case 0:// 王族
			case 1:
				switch (_random.nextInt(4)) {
				case 0:
				case 2:
					starus = ActionCodes.ACTION_SwordWalk;// 劍
					break;
				case 1:
					starus = ActionCodes.ACTION_SpearWalk;// 槍
					break;
				case 3:
					starus = ActionCodes.ACTION_TwoHandSwordWalk;// 雙手劍
					break;
				// case 4:
				// starus = ActionCodes.ACTION_Walk;// 空手
				// break;
				}
				break;

			case 61:// 騎士
			case 48:
				switch (_random.nextInt(4)) {
				case 0:
				case 2:
					starus = ActionCodes.ACTION_SwordWalk;// 劍
					break;
				case 1:
				case 3:
					starus = ActionCodes.ACTION_TwoHandSwordWalk;// 雙手劍
					break;
				// case 4:
				// starus = ActionCodes.ACTION_Walk;// 空手
				// break;
				}
				break;

			case 138:// 精靈
			case 37:
				// switch (_random.nextInt(4)) {
				// case 0:
				// case 2:
				// starus = ActionCodes.ACTION_SwordWalk;// 劍
				// break;
				// case 1:
				// case 3:
				starus = ActionCodes.ACTION_BowWalk;// 弓
				setBowActId(66);// 攻擊動畫
				set_ranged(10);// 重新設置攻擊距離
				// break;
				// case 4:
				// starus = ActionCodes.ACTION_Walk;// 空手
				// break;
				// }
				break;

			case 734:// 法師
			case 1186:
				switch (_random.nextInt(4)) {
				case 0:
				case 2:
					starus = ActionCodes.ACTION_SwordWalk;// 劍
					break;
				case 1:
				case 3:
					starus = ActionCodes.ACTION_StaffWalk;// 魔杖
					break;
				// case 4:
				// starus = ActionCodes.ACTION_Walk;// 空手
				// break;
				}
				break;

			case 2786:// 黑妖
			case 2796:
				switch (_random.nextInt(4)) {
				case 0:
					starus = ActionCodes.ACTION_SwordWalk;// 劍
					break;
				case 2:
					starus = ActionCodes.ACTION_ClawWalk;// 雙爪
					break;
				case 1:
				case 3:
					starus = ActionCodes.ACTION_EdoryuWalk;// 雙刀
					break;
				// case 4:
				// starus = ActionCodes.ACTION_Walk;// 空手
				// break;
				}
				break;

			case 6658:// 龍騎
			case 6661:
				switch (_random.nextInt(4)) {
				case 0:
				case 2:
					starus = ActionCodes.ACTION_SwordWalk;// 劍
					break;
				case 1:
				case 3:
					starus = ActionCodes.ACTION_SpearWalk;// 鎖鏈劍
					break;
				// case 4:
				// starus = ActionCodes.ACTION_Walk;// 空手
				// break;
				}
				break;

			case 6671:// 幻術
			case 6650:
				switch (_random.nextInt(4)) {
				case 0:
					starus = ActionCodes.ACTION_StaffWalk;// 魔杖
					break;
				case 2:
				case 1:
				case 3:
					starus = ActionCodes.ACTION_ClawWalk;// 奇古獸
					break;
				// case 4:
				// starus = ActionCodes.ACTION_Walk;// 空手
				// break;
				}
				break;
			}

			// 具有血盟編號
			if (_de_name.get_clanid() != 0) {
				final L1Clan clan = ClanReading.get().getTemplate(_de_name.get_clanid());
				_clanid = clan.getClanId();
				_clan_name = clan.getClanName();
				_emblem = ClanEmblemReading.get().get(_clanid);
			}

			setStatus(starus);
			setClassId(classid);
			setTempCharGfx(classid);
			setGfxId(classid);
			setTitle(DeTitleTable.get().getTitle());

			setBraveSpeed(1);
			setMoveSpeed(1);

			// 設置移動速度
			final int attack = SprTable.get().getAttackSpeed(classid, 1);
			final int move = SprTable.get().getMoveSpeed(classid, getStatus());

			setPassispeed(move);
			setAtkspeed(attack);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public int getClassId() {
		return _classId;
	}

	public void setClassId(final int i) {
		_classId = i;
	}

	/**
	 * 虛擬血盟資料
	 * 
	 * @return
	 */
	public DeName get_deName() {
		return _de_name;
	}

	private String _global_chat = null;// 喊話內容
	private int _chat_mode = 0;// 喊話模式
	private boolean _chat = false;// 是否一般頻道同時說出

	public int get_chat_mode() {
		return _chat_mode;
	}

	public String get_chat() {
		return _global_chat;
	}

	public void set_chat(final String chat, final int cmd) {
		_global_chat = chat;
		_chat_mode = cmd;
		if (_random.nextInt(100) <= 30) {
			_chat = true;
		}
	}

	public void globalChat() {
		try {
			if (_random.nextBoolean()) {
				return;
			}

			ServerBasePacket pack = null;
			if (_chat_mode != 27) {// 27 執行自動喊話(買賣)
				pack = new S_ChatGlobal(this, _global_chat);

			} else if (_chat_mode != 28) {// 28 執行自動喊話(廣播)
				pack = new S_ChatTransaction(this, _global_chat);
			}

			for (final Iterator<L1PcInstance> iter = World.get().getAllPlayers().iterator(); iter
					.hasNext();) {
				final L1PcInstance listner = iter.next();
				// 拒絕接收該人物訊息
				if (listner.getExcludingList().contains(getNameId())) {
					continue;
				}
				// 拒絕接收廣播頻道
				if (!listner.isShowTradeChat()) {
					continue;
				}
				listner.sendPackets(pack);
			}

			if (_chat) {// 是否一般頻道同時說出
				return;
			}

			pack = new S_Chat(this, _global_chat);
			for (final L1PcInstance listner : World.get().getRecognizePlayer(this)) {
				if (!listner.getExcludingList().contains(getNameId())) {
					// 副本ID相等
					if (get_showId() == listner.get_showId()) {
						listner.sendPackets(pack);
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private boolean _is_fishing; // 釣魚狀態

	public boolean isFishing() {
		return _is_fishing;
	}

	/**
	 * 自動執行釣魚
	 */
	public void start_fishingAI() {
		if (!_is_fishing) {
			final Fishing_Runnable runnable = new Fishing_Runnable(this);
			runnable.startCmd();
		}
	}

	/**
	 * 取消釣魚
	 */
	public void stop_fishing() {
		if (_is_fishing) {
			broadcastPacketAll(new S_CharVisualUpdate(getId(), ActionCodes.ACTION_Walk));
		}
		_is_fishing = false;
	}

	/**
	 * 找一個 可以釣魚的點
	 */
	public void start_fishing() {
		try {
			int locx = getX();
			int locy = getY();
			final int count = 3 + _random.nextInt(2);
			for (int i = 0; i < count; i++) {
				locx += HEADING_TABLE_X[getHeading()];
				locy += HEADING_TABLE_Y[getHeading()];
			}

			final int gab = getMap().getOriginalTile(locx, locy);
			if ((gab % 28) == 0) {
				_fishX = locx;
				_fishY = locy;

				setHeading(targetDirection(_fishX, _fishY));
				broadcastPacketAll(new S_ChangeHeading(this));

				broadcastPacketAll(new S_Fishing(getId(), ActionCodes.ACTION_Fishing, _fishX, _fishY));
				_is_fishing = true;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _fishX = -1;

	public int get_fishX() {
		return _fishX;
	}

	private int _fishY = -1;

	public int get_fishY() {
		return _fishY;
	}

	private class Fishing_Runnable implements Runnable {

		private L1DeInstance _npc = null;

		private Fishing_Runnable(final L1DeInstance npc) {
			_npc = npc;
		}

		private void startCmd() {
			DeAiThreadPool.get().execute(this);
		}

		@Override
		public void run() {
			try {
				final int x = _npc.getX();
				final int y = _npc.getY();

				final int x1 = x - 18;
				final int y1 = y - 18;

				final int x2 = x + 18;
				final int y2 = y + 18;

				final int rows = x2 - x1;// 高度
				final int columns = y2 - y1;// 寬度

				int tgx = 0;
				int tgy = 0;

				final L1Map map = _npc.getMap();
				for (int i = 0; i < rows; i++) {// X
					for (int j = 0; j < columns; j++) {// Y
						final int cx = x1 + i;
						final int cy = y1 + j;
						final int gab = map.getOriginalTile(cx, cy);
						if ((gab % 28) == 0) {
							tgx = cx;
							tgy = cy;
						}
					}
				}
				Thread.sleep(10);

				int i = 20;
				while ((_npc.getX() != tgx) && (_npc.getY() != tgy)) {
					if (_npc == null) {
						break;
					}

					final double d = _npc.getLocation().getLineDistance(new Point(tgx, tgy));
					if (d <= 1D) {
						break;
					}

					final int moveDirection = _npc.getMove().moveDirection(tgx, tgy);
					final int dir = _npc.getMove().checkObject(moveDirection);

					if (dir != -1) {
						_npc.getMove().setDirectionMove(dir);
						_npc.setNpcSpeed();
					}
					Thread.sleep(_npc.calcSleepTime(_npc.getPassispeed(), 0));
					i--;
					if (i <= 0) {
						break;
					}
				}

				Thread.sleep(5000);
				start_fishing();

			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * 娃娃跟隨主人變更移動/速度狀態
	 */
	public void setNpcSpeed() {
		try {
			// 取回娃娃
			if (!getDolls().isEmpty()) {
				for (final Object obj : getDolls().values().toArray()) {
					final L1DollInstance doll = (L1DollInstance) obj;
					if (doll != null) {
						doll.setNpcMoveSpeed();
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 目標搜尋
	 */
	@Override
	public void searchTarget() {
		try {
			if (isShop()) {
				return;
			}

			// 在安全區
			if (isSafetyZone()) {
				return;
			}

			// 加入假人變身處理 by terry0412
			final int classid;
			if (getBowActId() > 0) {
				classid = 8913;

			} else {
				classid = 9206;
			}
			if ((getTempCharGfx() != classid) && (_random.nextInt(100) < 70)) {
				setTempCharGfx(classid);
				setGfxId(classid);

				// 設置移動速度
				final int attack = SprTable.get().getAttackSpeed(classid, 1);
				final int move = SprTable.get().getMoveSpeed(classid, getStatus());

				setPassispeed(move);
				setAtkspeed(attack);
				try {
					Thread.sleep(500);
				} catch (final Exception e) {
				}

				// 更新物件圖像
				broadcastPacketAll(new S_ChangeShape(this, classid));
				try {
					Thread.sleep(2000);
				} catch (final Exception e) {
				}

				// 重新搜尋
				searchTarget();
				return;
			}

			final L1NpcInstance targetNpc = search_target();
			if (targetNpc != null) {
				_hateList.add(targetNpc, 0);
				_target = targetNpc;

				// 控制寵物
				if (!getPetList().isEmpty()) {
					for (final Iterator<L1NpcInstance> iter = getPetList().values().iterator(); iter
							.hasNext();) {
						final L1NpcInstance summon = iter.next();
						if (summon != null) {
							if (summon instanceof L1SummonInstance) {
								final L1SummonInstance su = (L1SummonInstance) summon;
								su.setMasterTarget(_target);
							}
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private L1NpcInstance search_target() {
		// 已死亡
		if (isDead()) {
			return null;
		}

		// 禁止角色控制的負面魔法
		if (isParalyzed() || isSleeped() || hasSkillEffect(ICE_LANCE) || hasSkillEffect(SHOCK_STUN)
				|| hasSkillEffect(EARTH_BIND)) {
			// thinkingCounter++;
			return null;
		}

		// 超過原地停留次數
		if (thinkingCounter >= maxThinkingCycle) {
			thinkingCounter = 10; // 重新計算
			// 隨機傳送
			final L1Location newLoc = getLocation().randomLocation(200, true);
			teleport(newLoc.getX(), newLoc.getY(), 5);
			return null;
		}

		/** 分段式搜索附近怪物 (超高AI機制) */

		// 攻擊目標
		L1NpcInstance attacker_target = null;

		// 排序路徑距離，越近的怪物優先攻擊
		if (_target == null) {
			// 最近範圍搜索 - 暫存變數
			L1NpcInstance next_target = null;

			int minDistance = Short.MAX_VALUE;
			int distance = 0;

			// 搜索範圍內的物件
			final ArrayList<L1Object> screenObjects = World.get().getVisibleObjects(this, thinkingCounter);
			// 先判斷是否有以假人本身為攻擊目標的怪物在附近
			for (final L1Object visible : screenObjects) {
				if (visible instanceof L1MonsterInstance) {
					final L1MonsterInstance npc = (L1MonsterInstance) visible;
					if ((npc.getCurrentHp() <= 0) || npc.isDead()
					// 異常狀態中
							|| (npc.isParalyzed() && !npc.hasSkillEffect(SHOCK_STUN))
							|| (npc.getHiddenStatus() != L1NpcInstance.HIDDEN_STATUS_NONE)) {
						continue;
					}

					// 此怪物還沒有目標
					if (npc.getHateList().isEmpty() || npc.getHateList().containsKey(this)) {
						// 變形探知
						if (getTempCharGfx() != getClassId()) { // 有變身
							if (npc.getNpcTemplate().is_agrososc()) {
								// 列入攻擊清單
								npc._hateList.add(this, 0);
								npc._target = this;
								continue;
							}
						}

						// 怪物是否為主動攻擊
						else if (npc.getNpcTemplate().is_agro()) {
							// 列入攻擊清單
							npc._hateList.add(this, 0);
							npc._target = this;
							continue;
						}
					}
				}
			}
			// 如果找不到目標
			if (next_target == null) {
				// 自由搜索想要攻擊的附近怪物
				for (final L1Object visible : screenObjects) {
					if (visible instanceof L1MonsterInstance) {
						final L1MonsterInstance npc = (L1MonsterInstance) visible;
						if ((npc.getCurrentHp() <= 0) || npc.isDead()
						// 異常狀態中
								|| (npc.isParalyzed() && !npc.hasSkillEffect(SHOCK_STUN))
								|| (npc.getHiddenStatus() != L1NpcInstance.HIDDEN_STATUS_NONE)
								// 最大血量少於XX則跳過
								|| (npc.getMaxHp() < 80)
								// 等級最少需要大於5級
								|| (npc.getLevel() <= 5)
								// 怪物血量不是滿的
								|| (npc.getCurrentHp() < npc.getMaxHp())
								// 直線上有障礙物
								|| !glanceCheck(npc.getX(), npc.getY())) {
							continue;
						}
						// 不支援搶怪
						if (npc.getHateList().isEmpty() || npc.getHateList().containsKey(this)) {
							distance = getLocation().getTileLineDistance(npc.getLocation());
							if (minDistance > distance) {
								minDistance = distance;
								next_target = npc;
								continue;
							}
						}
					}
				}
			}
			// 發現離自己最近的目標 -> 攻擊確認
			if (next_target != null) {
				attacker_target = next_target;
			}
		}

		// 判斷步驟
		if (attacker_target == null) {
			// 增加思考週期
			thinkingCounter += 5;
			return null;
		}
		return attacker_target;
	}

	/**
	 * 攻擊目標 by terry0412
	 */
	@Override
	public void attack(final L1Character target) {
		// 額外的判斷 by terry0412
		if ((target.getCurrentHp() <= 0) || target.isDead()) {
			tagertClear();
			thinkingCounter = 5; // 重新計算
			return;

		} else if (target instanceof L1MonsterInstance) {
			final L1MonsterInstance npc = (L1MonsterInstance) target;
			// 異常狀態中
			if ((npc.isParalyzed() && !npc.hasSkillEffect(SHOCK_STUN))
					|| (npc.getHiddenStatus() != L1NpcInstance.HIDDEN_STATUS_NONE)
					// 不支援搶怪
					|| (!npc.getHateList().isEmpty() && !npc.getHateList().containsKey(this))) {
				tagertClear();
				thinkingCounter = 10; // 重新計算
				return;

			} else if (_random.nextInt(100) < 30) {
				/*
				 * 搜索範圍內的物件, 並轉移攻擊目標, 優先攻擊視自己為目標的怪物
				 */
				for (final L1Object visible : World.get().getVisibleObjects(this)) {
					if (visible instanceof L1MonsterInstance) {
						final L1MonsterInstance mob = (L1MonsterInstance) visible;
						if ((npc._target != this) && (mob._target == this)
						// 非異常狀態中
								&& !(npc.isParalyzed() && !npc.hasSkillEffect(SHOCK_STUN))) {
							tagertClear();
							thinkingCounter = 10; // 重新計算
							// 加入攻擊目標
							_hateList.add(mob, 0);
							_target = mob;
							break;
						}
					}
				}
			}
		}

		// 攻撃可能位置
		if (isAttackPosition(target.getX(), target.getY(), get_ranged())) {
			// 攻擊對方
			setHeading(targetDirection(target.getX(), target.getY()));
			// 更新面向
			// broadcastPacketX8(new S_ChangeHeading(this));

			attackTarget(target);

			// 已殺死對方 (重新計算)
			if ((target == null) || (target.getCurrentHp() <= 0) || target.isDead()) {
				tagertClear();
				thinkingCounter = 5 + _random.nextInt(11);
			}

		} else { // 攻撃不可能位置
			if (_npcMove != null) {
				// 移動できるキャラ
				final int dir = _npcMove.moveDirection(target.getX(), target.getY());
				if (dir == -1) {
					tagertClear();
					thinkingCounter += 5;
				} else {
					_npcMove.setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
					setNpcSpeed();
				}
			}
		}
	}

	/**
	 * 沒有目標的處理 by terry0412
	 */
	@Override
	public boolean noTarget() {
		if (isShop()) {
			return true;
		}
		// 在安全區
		if (isSafetyZone()) {
			return true;
		}
		return false;
	}

	/**
	 * 攻擊目標設置
	 */
	@Override
	public void setLink(final L1Character cha) {
		try {
			// 副本ID不相等
			if (get_showId() != cha.get_showId()) {
				return;
			}
			if ((cha != null) && _hateList.isEmpty()) {
				_hateList.add(cha, 0);
				checkTarget();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onNpcAI() {
		try {
			if (isShop()) {
				return;
			}

			// 在安全區
			if (isSafetyZone()) {
				// 釣魚區跳過 by terry0412
				if ((getMapId() == 5124) || (getMapId() == 5490) || (getMapId() == 5301)
						|| (getMapId() == 5302)) {
					// 有變身
					if (getTempCharGfx() != getClassId()) {
						setTempCharGfx(getClassId());
						setGfxId(getClassId());

						// 設置移動速度
						final int attack = SprTable.get().getAttackSpeed(getClassId(), 1);
						final int move = SprTable.get().getMoveSpeed(getClassId(), getStatus());

						setPassispeed(move);
						setAtkspeed(attack);
						try {
							Thread.sleep(500);
						} catch (final Exception e) {
						}

						// 更新物件圖像
						broadcastPacketAll(new S_ChangeShape(this, getClassId()));
						try {
							Thread.sleep(2000);
						} catch (final Exception e) {
						}
					}
					return;
				}

				// 加入假人變身處理 by terry0412
				final int classid;
				if (getBowActId() > 0) {
					classid = 8913;

				} else {
					classid = 9206;
				}
				if ((getTempCharGfx() != classid) && (_random.nextInt(100) < 70)) {
					setTempCharGfx(classid);
					setGfxId(classid);

					// 設置移動速度
					final int attack = SprTable.get().getAttackSpeed(classid, 1);
					final int move = SprTable.get().getMoveSpeed(classid, getStatus());

					setPassispeed(move);
					setAtkspeed(attack);
					try {
						Thread.sleep(500);
					} catch (final Exception e) {
					}

					// 更新物件圖像
					broadcastPacketAll(new S_ChangeShape(this, classid));
					try {
						Thread.sleep(2000);
					} catch (final Exception e) {
					}
				}
				return;
			}

			if (isAiRunning()) {
				return;
			}

			setActived(false);
			startAI();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onTalkAction(final L1PcInstance pc) {

	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			// 任何一方處於安全區內
			if (isSafetyZone() || pc.isSafetyZone()) {
				final L1AttackMode attack_mortion = new L1AttackPc(pc, this);
				attack_mortion.action();
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

	/**
	 * 受攻擊Mp減少計算
	 */
	@Override
	public void ReceiveManaDamage(final L1Character attacker, final int mpDamage) {
		if ((mpDamage > 0) && !isDead()) {
			setHate(attacker, mpDamage);

			onNpcAI();

			// 互相幫助的判斷
			if (attacker instanceof L1PcInstance) {
				serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	/**
	 * 受攻擊hp減少計算
	 */
	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		if ((getCurrentHp() > 0) && !isDead()) {
			if ((getHiddenStatus() == HIDDEN_STATUS_SINK) || (getHiddenStatus() == HIDDEN_STATUS_FLY)) {
				return;
			}

			onNpcAI();

			if (attacker instanceof L1PcInstance) {
				if (damage > 0) {
					final L1PcInstance player = (L1PcInstance) attacker;
					player.setPetTarget(this);

					// 被玩家攻擊，一定機率瞬移飛走 by terry0412
					thinkingCounter += 15;
					return;
				}

			} else { // 怪物造成的傷害減半
				damage /= 2;
			}

			if (damage >= 0) {
				if (attacker instanceof L1EffectInstance) { // 效果不列入目標
					// this.setHate(attacker, damage);

				} else if (attacker instanceof L1IllusoryInstance) { // 攻擊者是分身不列入目標(設置主人為目標)
					final L1IllusoryInstance ill = (L1IllusoryInstance) attacker;
					attacker = ill.getMaster();
					setHate(attacker, damage);
					// this.setHate(ill.getMaster(), damage);

				} else {
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				removeSkillEffect(FOG_OF_SLEEPING);
			}

			if (_random.nextInt(100) < 25) {
				final int newHp = getCurrentHp() + 50;
				final int[] x = new int[] { 189, 194, 197 };
				setCurrentHp(newHp);
				broadcastPacketX8(new S_SkillSound(getId(), x[_random.nextInt(x.length)]));
			}

			final int newHp = getCurrentHp() - damage;
			if ((newHp <= 0) && !isDead()) {
				setCurrentHpDirect(0);
				setDead(true);
				setStatus(ActionCodes.ACTION_Die);
				final Death death = new Death(attacker);
				DeAiThreadPool.get().execute(death);
			}

			if (newHp > 0) {
				setCurrentHp(newHp);
			}

		} else if (!isDead()) { // 念のため
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			final Death death = new Death(attacker);
			DeAiThreadPool.get().execute(death);
		}
	}

	@Override
	public void setCurrentHp(final int i) {
		final int currentHp = Math.min(i, getMaxHp());

		if (getCurrentHp() == currentHp) {
			return;
		}

		setCurrentHpDirect(currentHp);
	}

	@Override
	public void setCurrentMp(final int i) {
		final int currentMp = Math.min(i, getMaxMp());

		if (getCurrentMp() == currentMp) {
			return;
		}

		setCurrentMpDirect(currentMp);
	}

	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(final L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			try {
				final L1DeInstance mob = L1DeInstance.this;

				_denameList.remove(_de_name);

				mob.setDeathProcessing(true);
				mob.setCurrentHpDirect(0);
				mob.setDead(true);
				mob.setStatus(ActionCodes.ACTION_Die);

				mob.broadcastPacketAll(new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Die));

				// 解除舊座標障礙宣告
				mob.getMap().setPassable(mob.getLocation(), true);

				mob.startChat(CHAT_TIMING_DEAD);

				mob.setDeathProcessing(false);

				mob.allTargetClear();

				final int deltime = 5;

				mob.startDeleteTimer(deltime);

				ListMapUtil.clear(_sellList);

				ListMapUtil.clear(_buyList);

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	public int getClanid() {
		return _clanid;
	}

	public String getClanname() {
		return _clan_name;
	}

	public L1EmblemIcon getEmblem() {
		return _emblem;
	}

	public void setShopChat(final String shopChat1, final String shopChat2) {
		_shop_chat1 = shopChat1;
		_shop_chat2 = shopChat2;
	}

	public void shopChat() {
		try {
			if (_random.nextBoolean()) {
				String info = "";
				if (_random.nextBoolean()) {
					info = _shop_chat1;
				} else {
					info = _shop_chat2;
				}
				broadcastPacketAll(new S_DoActionShop(getId(), info));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 停止商店模式
	 */
	public void stop_shop() {
		try {
			if (isShop()) {
				set_isShop(false);// 設置販賣狀態
				_sellList.clear();
				_buyList.clear();
				_shop_chat1 = null;
				_shop_chat2 = null;

				broadcastPacketAll(new S_CharVisualUpdate(getId(), ActionCodes.ACTION_Walk));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 啟動商店模式
	 */
	public void start_shop() {
		try {
			if (!isShop()) {
				boolean isShopMap = false;
				switch (getMapId()) {
				case 340:// 古鲁丁商店村
				case 350:// 奇岩商店村
				case 360:// 欧瑞商店村
				case 370:// 银骑士商店村
					isShopMap = true;
					break;
				}

				if (isShopMap) {
					set_isShop(true);// 設置販賣狀態
					DeShopItemTable.get().getItems(this);

					final int h = _random.nextInt(8);
					setHeading(h);// 設置面向
					broadcastPacketX8(new S_ChangeHeading(this));

					broadcastPacketX8(new S_DoActionShop(getId(), _shop_chat1));
				}
			}
			_is_shop = true;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 賣出 物品/價格
	 * 
	 * @return
	 */
	public Map<L1ItemInstance, Integer> get_sellList() {
		return _sellList;
	}

	/**
	 * 購入 Integer:物品編號 0:價格 1:強化質 2:購入數量
	 * 
	 * @return
	 */
	public Map<Integer, int[]> get_buyList() {
		return _buyList;
	}

	public void sellList(final Map<L1ItemInstance, Integer> sellList) {
		_sellList.putAll(sellList);
	}

	public void buyList(final Map<Integer, int[]> buyList) {
		_buyList.putAll(buyList);
	}

	public void updateBuyList(final Integer key, final int[] value) {
		_buyList.put(key, value);
	}

	/**
	 * 目標位置指定標傳送
	 * 
	 * @param nx
	 * @param ny
	 * @param dir
	 */
	@Override
	public void teleport(int nx, int ny, final int dir) {
		try {
			for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
				pc.sendPackets(new S_SkillSound(getId(), 169));
				pc.sendPackets(new S_RemoveObject(this));
				pc.removeKnownObject(this);
			}
			setX(nx);
			setY(ny);
			setHeading(dir);

			final HashSet<L1PcInstance> subjects = new HashSet<L1PcInstance>();

			// 可以攜帶寵物
			if (getMap().isTakePets()) {
				// 寵物的跟隨移動
				for (final L1NpcInstance petNpc : getPetList().values()) {
					// 主人身邊隨機座標取回
					final L1Location loc = getLocation().randomLocation(3, false);
					nx = loc.getX();
					ny = loc.getY();

					// 設置副本編號
					petNpc.set_showId(get_showId());

					Teleportation.teleport(petNpc, nx, ny, getMapId(), dir);

					for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(petNpc)) {
						// 畫面內可見人物 認識更新
						visiblePc.removeKnownObject(petNpc);
						if (visiblePc.get_showId() == petNpc.get_showId()) {
							subjects.add(visiblePc);
						}
					}
				}
			}

			// 娃娃的跟隨移動
			if (!getDolls().isEmpty()) {
				// 主人身邊隨機座標取回
				final L1Location loc = getLocation().randomLocation(3, false);
				nx = loc.getX();
				ny = loc.getY();

				final Object[] dolls = getDolls().values().toArray();
				for (final Object obj : dolls) {
					final L1DollInstance doll = (L1DollInstance) obj;
					Teleportation.teleport(doll, nx, ny, getMapId(), dir);
					// 設置副本編號
					doll.set_showId(get_showId());

					for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(doll)) {
						// 畫面內可見人物 認識更新
						visiblePc.removeKnownObject(doll);
						if (visiblePc.get_showId() == doll.get_showId()) {
							subjects.add(visiblePc);
						}
					}
				}
			}
			// 取回娃娃
			if (get_power_doll() != null) {
				// 主人身邊隨機座標取回
				final L1Location loc = getLocation().randomLocation(3, false);
				nx = loc.getX();
				ny = loc.getY();

				Teleportation.teleport(get_power_doll(), nx, ny, getMapId(), dir);
				// 設置副本編號
				get_power_doll().set_showId(get_showId());

				for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(get_power_doll())) {
					// 畫面內可見人物 認識更新
					visiblePc.removeKnownObject(get_power_doll());
					if (visiblePc.get_showId() == get_power_doll().get_showId()) {
						subjects.add(visiblePc);
					}
				}
			}

			for (final L1PcInstance updatePc : subjects) {
				updatePc.updateObject();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
