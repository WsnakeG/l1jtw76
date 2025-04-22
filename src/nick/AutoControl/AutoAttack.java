package nick.AutoControl;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_ATTACK;
import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_MOVE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_PARALYZED;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nick.forMYSQL.N1AutoMaticConfigNumber;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_MapID;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_NPCPack_Doll;
import com.lineage.server.serverpackets.S_OtherCharPacks;
import com.lineage.server.serverpackets.S_OwnCharPack;
import com.lineage.server.serverpackets.S_PacketBoxWindShackle;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.PcAutoThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.world.World;

/**
 * 自動練功
 * 
 * @author hpc20207
 * @版本:v1.3
 * @修改時間:2023/2/17 16:52
 *
 */
public class AutoAttack extends TimerTask {

	private static Log _log = LogFactory.getLog(AutoAttack.class);

	private ArrayList<Node> openList = new ArrayList<Node>();
	private ArrayList<Node> closeList = new ArrayList<Node>();

	boolean first = true;

	L1Character target = null;

	L1Character atk_target = null;

	private int tmp_traget_d = 999; // 上個目標D暫存

	private L1PcInstance pc = null;

	private int 更新畫面 = 0;

	private int h = -1;

	private ArrayList<Integer> _list = new ArrayList<Integer>();

	// PerformanceTimer timer_beattack; // 被攻擊名單計時器
	//
	// /** 被攻擊名單計時器 首次啟動 */
	// private boolean timer_beattack_first = true;
	//
	// /** 被攻擊名單計時器 幾秒重置一次 計算方式為 ms * 1000 */
	// private int timer_beattack_restart = 30 * 1000;

	public AutoAttack(L1PcInstance user) {
		pc = user;
	}

	Random random = new Random();

	protected static int _heading0[] = { 7, 0, 1, 2, 3, 4, 5, 6 };

	protected static int _heading1[] = { 1, 2, 3, 4, 5, 6, 7, 0 };

	protected int[][] DIR_TABLE = { { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 }, { -1, -1 } };

	protected int[] dirx = { 0, 1, 1, 1, 0, -1, -1, -1 };
	protected int[] diry = { -1, -1, 0, 1, 1, 1, 0, -1 };

	protected int[] dirx2 = { 1, 1, -1, -1 };
	protected int[] diry2 = { -1, 1, 1, -1 };

	private Timer _timeHandler = new Timer(true);
	
	private AutoAttackUpdate aaa = new AutoAttackUpdate();

	public void begin() {
		_timeHandler.schedule(this, 200, 200);
		// 未加入線程重置前
		PcAutoThreadPool.get().execute(this);
	}

	/**
	 * 停止動作釋放資源
	 */
	public void stop() {
		try {
			target = null;
			atk_target = null;
			更新畫面 = 0;
			h = -1;
			tmp_traget_d = 999;
			_list.clear();
			pc.setIsAuto(false);
			openList.clear(); // Astar
			closeList.clear(); // Astar
			pc.clear_parentList();// Astar
			if (this.cancel()) {
				// 停止時間軸
				_timeHandler.purge();
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void run() {
		try {
			if (pc == null || pc.isDead() || pc.getCurrentHp() <= 0 || !pc.IsAuto() || pc.getNetConnection().getIp() == null || pc.getWeapon() == null) {
				stop();
			} else {

				pc.setRegenState(REGENSTATE_MOVE); // 設置人物移動狀態

				// if (timer_beattack_first) { // 被攻擊名單計時器 首次啟動
				// timer_beattack_first = false;
				// timer_beattack = new PerformanceTimer();
				// }
				//
				// if (timer_beattack_restart < timer_beattack.get()) {
				// timer_beattack.reset();
				// pc.clearInAtkList();// 清空攻擊陣列
				// }
				//
				// if (pc.getonAtktimer()) { // 掛機被攻擊時間軸
				// pc.clearInAtkList();// 清空攻擊陣列
				// pc.setonAtktimer(false);
				// }

				// BOSS迴避
				if (pc.IsbossTeleport() && ConfirmTheBOSS(pc)) {
					return;
				}
				
				// 20240713
				if (pc.getWeapon().getItem().getRange() == -1 && checkArrowCount(pc) == false && pc.IsAuto()) { // 如果遠攻 沒箭矢 開啟自動練功時
					if (pc.getInventory().checkItem(40308, 1000)) {
						pc.getInventory().consumeItem(40308, 1000);
						pc.getInventory().storeItem(40743, 3000); // 40743 箭*3000
						pc.sendPackets(new S_ServerMessage("自動購入 箭 3000隻 花費1000。"));
					} else {
						pc.setIsAuto(false);
						pc.setRestartAuto(0);
						pc.setRestartAutoStartSec(0);
						L1Teleport.teleport(pc, 33444, 32797, (short) 4, 5, true);
						pc.sendPackets(new S_ServerMessage("你沒有足夠的錢買箭，先幫你傳回村了。"));
						pc.sendPackets(new S_ServerMessage("\\fU【關閉】內掛！"));
					}
				}

				// // PvP 瞬移檢測 增強型
				// if (CheckPvP(pc)) {
				// return;
				// }
				//
				// // x格內仇人檢測
				// if (ConfirmTheEnemy(pc, 5)) {
				// return;
				// }

				// 詛咒形狀態?
				if (CheckStatus(pc)) {
					return;
				}

				if (pc.getInventory().getWeight240() >= 197) { // 重量過重
					// 110 \f1當負重過重的時候，無法戰鬥。
					pc.sendPackets(new S_ServerMessage(110));
					return;
				}

				// 詛咒形狀態?
				if (CheckBuff(pc)) {
					return;
				}

				// 如果設置了範圍並且不在範圍內
				if (!pc.getLocation().isInScreenRange(new Point(pc.getOutReturnX(), pc.getOutReturnY()), pc.getOutRange())) {
					teleport_setpoint(pc);
					atk_target = null;
					target = null;
					tmp_traget_d = 999;
					return;
				}

				// 順移釋放目標
				if (pc.Istel()) {
					atk_target = null;
					target = null;
					tmp_traget_d = 999;
					pc.settel(false);
					return;
				}

				// 搜尋目標
				SearchTarget(pc);
				
				if (target != null) {
					if (target.isDead()) {// 目標已經死亡
						atk_target = null;
						target = null;
						tmp_traget_d = 999;
						return;
					}
					if (target instanceof L1MonsterInstance) {// 目標進入遁地或是飛空狀態
						L1MonsterInstance mob = (L1MonsterInstance) target;
						if (mob.getHiddenStatus() == 1 || mob.getHiddenStatus() == 2 || mob.getHiddenStatus() == 3) {
							target = null;
							atk_target = null;
							tmp_traget_d = 999;
							return;// 重新執行
						}
					}

					int Ranged = 1;// 武器最初攻擊距離
					if (pc.getWeapon() != null) {// 判定攻擊距離
						Ranged = pc.getWeapon().getItem().getRange();
						if (Ranged == -1) {// 拿遠距離武器狀態
							Ranged = 8; // 遠距離武器限制最遠距離
						}
					}
					// 開怪技能距離判斷
					int Openmob_Ranged = -1; // 開怪技能初始距離
					if (pc.getOpenskill_id() > 0) {
						Openmob_Ranged = pc.getOpenskill_rng(); // 開怪技能距離
					}
					// 攻擊技能判斷
					int Atkskill_Ranged = -1; // 攻擊技能初始距離
					if (pc.getAtkskill_id() > 0) {
						Atkskill_Ranged = pc.getAtkskill_rng(); // 攻擊技能距離
					}
					// 範圍技能判斷
					int Rngskill_Ranged = -1; // 範圍技能初始距離
					if (pc.getRngskill_id() > 0) {
						Rngskill_Ranged = pc.get_rngskill_skillrng(); // 攻擊技能距離
						if (Rngskill_Ranged <= 0)
							Rngskill_Ranged = 99;
					}

					// 與怪物的距離(直線)
					final int location = pc.getLocation().getTileLineDistance(target.getLocation());
					pc.setRegenState(REGENSTATE_ATTACK); // 設置人物為攻擊狀態
					// 開怪距離判定
					if (location <= Openmob_Ranged && pc.glanceCheck(this.target.getX(), this.target.getY())) {// 並且我與怪物距離內並無任何障礙物
						skill_open(pc, target);
					}
					// 攻擊技能距離判定
					if (pc.glanceCheck(this.target.getX(), this.target.getY())) {// 與怪物距離內並無任何障礙物
						attack_skill(pc, target, Atkskill_Ranged, location);
					}
					// 範圍技能距離判定
					if (location <= Rngskill_Ranged && pc.glanceCheck(this.target.getX(), this.target.getY())) {// 並且我與怪物距離內並無任何障礙物
						rng_skill(pc, target);
					}

					if (location <= Ranged && pc.glanceCheck(this.target.getX(), this.target.getY())) {// 並且我與怪物距離內並無任何障礙物
						if (AttackMon(pc, target)) {// 執行攻擊一下
							target = null;
							return;
						}
					} else {// 還未到可以攻擊的距離
						if (!Astar()) {
							// pc.addTargetList(target.getId());
							// System.out.println("A星失敗 釋放目標"+target.getName());
							target = null;
							atk_target = null;
							tmp_traget_d = 999;
							L1Teleport.randomTeleport(pc, true);
							Thread.sleep(1000);
							return;
						}
						Amove();

					}

				} else {
					// if (pc.IsTeleportAuto()) {
					if (pc.getOutRange() == 0) {
						NickFlyNoChecking(pc);
					} else {
						GoallessWalking(pc);
					}
				}
			}

		} catch (Exception e) {
			pc.setIsAuto(false);
			stop();
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 先確認是否有無敵人<br>
	 * true = 有敵人 (執行飛行) <br>
	 * false = 沒有敵人 (繼續往下執行)<br>
	 * 
	 * @param pc
	 * @return
	 */
	private boolean ConfirmTheBOSS(final L1PcInstance pc) {
		try {

			boolean ok = false;
			for (L1Object objid : World.get().getVisibleObjects(pc, 10)) {
				if (objid instanceof L1NpcInstance) {
					L1NpcInstance _npc = (L1NpcInstance) objid;
					if (_npc.getNpcTemplate().is_boss()) {
						NickFlyNoChecking(pc);
						ok = true;
						break;
					}
				}
			}
			if (ok) {
				return true;
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 確認是否受到異常狀態<br>
	 * true = 異常 (執行飛行) <br>
	 * false = 正常 (繼續往下執行)<br>
	 * 
	 * @param pc
	 * @return
	 */
	private boolean CheckStatus(final L1PcInstance pc) {
		try {
			if (pc.isParalyzed()) {
				return true;
			}

			if (pc.hasSkillEffect(L1SkillId.CURSE_PARALYZE)) {
				return true;
			}

			if (pc.hasSkillEffect(L1SkillId.STATUS_POISON_PARALYZED)) {
				return true;
			}

			if (pc.hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)) {
				return true;
			}

			if (pc.hasSkillEffect(L1SkillId.SHOCK_STUN)) {
				return true;
			}

			if (pc.hasSkillEffect(L1SkillId.ICE_LANCE)) {
				return true;
			}

			if (pc.hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)) {
				return true;
			}

			if (pc.hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
				return true;
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	public void clear_all() {
		target = null;
		atk_target = null;
		pc.clear_parentList();
	}

	public boolean Astar() {
		try {
			openList.clear();
			closeList.clear();
			pc.clear_parentList();
			Node point = new Node(pc.getX(), pc.getY());
			Node tg = new Node(target.getX(), target.getY());
			Node parent = findAPath(point, tg);

			// if(parent == null){ //mob存在不可通行的座標上
			// for(int i = 0; i < 4;i++){
			// tg = new Node(target.getX()+dirx2[i],target.getY()+diry2[i]);
			// parent = findAPath(point,tg);
			// if(parent!=null)
			// break;
			// }
			// }

			if (parent == null) { // mob存在不可通行的座標上
				for (int i = 0; i < 8; i++) {
					tg = new Node((target.getX() + dirx[i]), (target.getY() + diry[i]));
					parent = findAPath(point, tg);
					if (parent != null)
						break;
				}
			}

			while (parent != null) { // 父節點還原路徑
				pc.add_parentList(new Node(parent.x, parent.y));
				parent = parent.parent;

			}
			if (pc.get_parentListSize() != 0)
				pc.re_parentList();
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return (pc.get_parentListSize() > 0 ? true : false);
	}

	/** 怪物節點數量返回 */
	public int mob_parentSize(final int x, final int y) {
		int parent_num = 0;
		try {
			openList.clear();
			closeList.clear();
			Node point = new Node(pc.getX(), pc.getY());
			Node tg = new Node(x, y);
			Node parent = findAPath(point, tg);

			if (parent == null) { // mob存在不可通行的座標上
				for (int i = 0; i < 4; i++) {
					tg = new Node(x + dirx2[i], y + diry2[i]);
					parent = findAPath(point, tg);
					if (parent != null)
						break;
				}
			}

			while (parent != null) { // 父節點還原路徑
				parent_num++;
				parent = parent.parent;
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return parent_num;
	}

	public void Amove() {
		try {
			Node _next = pc.get_autoparent();
			if (_next.x == pc.getX() && _next.y == pc.getY()) {
				return;
			}
			int dir = NodeToDir(_next, pc.getX(), pc.getY());
			// dir = checkObject(pc, dir);
			if (dir != -1) {
				setDirectionMove(pc, dir);// 人物移動
				Thread.sleep(getRightInterval(2) + 20); // 延遲
			} else {
				System.out.println("NodeToDir返回-1");
			}

		} catch (Exception e) {
			pc.clear_parentList();
			openList.clear(); // Astar
			closeList.clear(); // Astar
			pc.addTargetList(target.getId());// 排除列表
			// _log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 開怪技能
	 */
	private void skill_open(final L1PcInstance pc, final L1Character targets) {
		try {
			// _log.error("aaa" + pc.getOpenskill_id());
			// _log.error("bbb" + pc.get_openskill_implement());
			// _log.error("ccc" + checkRatio(pc.getOpenskill_mp()));
			// _log.error("ddd" + pc.isSkillDelay());
			if (!pc.get_openskill_implement()) {
				// _log.error("1");
				return;
			}
			if (!checkRatio(pc.getOpenskill_mp())) { // MP
				// _log.error("2");
				return;
			}
			if (pc.isSkillDelay()) { // 技能延遲施放中
				// _log.error("3");
				return;
			}
			final L1Skills skill = SkillsTable.get().getTemplate(pc.getOpenskill_id());
			// _log.error("eee" + pc.getOpenskill_id());

			final L1SkillUse skillUse = new L1SkillUse();
			if (pc.getCurrentMp() >= skill.getMpConsume()) { // 施放該技能需要的魔力
				if (targets.isDead()) // 加入施放前判斷目標是否已死亡
					return;
				skillUse.handleCommands(pc, pc.getOpenskill_id(), targets.getId(), targets.getX(), targets.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_NORMAL);
				if (skill.getRanged() <= 0) { // 無方向施展
					Thread.sleep(getRightInterval(4)); // 延遲
				} else {
					Thread.sleep(getRightInterval(3)); // 延遲
				}
				pc.set_openskill_implement(false);
			}
		} catch (Exception e) {
			//
		}
	}

	/**
	 * 攻擊技能
	 */
	private void attack_skill(final L1PcInstance pc, final L1Character targets, final int skillrng, final int location) {
		try {
			if (skillrng > 0 && location > skillrng) { // 如果技能距離大於0 就判斷與目標的距離是否滿足技能距離
				return;
			}
			if (!pc.get_atkskill_implement()) { // CD未到
				return;
			}
			if (!checkRatio(pc.getAtkskill_mp())) { // MP
				return;
			}
			if (pc.isSkillDelay()) { // 技能延遲施放中
				return;
			}
			final L1Skills skill = SkillsTable.get().getTemplate(pc.getAtkskill_id());
			final L1SkillUse skillUse = new L1SkillUse();
			if (pc.getCurrentMp() >= skill.getMpConsume()) { // 施放該技能需要的魔力
				if (targets.isDead()) // 加入施放前判斷目標是否已死亡
					return;
				skillUse.handleCommands(pc, pc.getAtkskill_id(), targets.getId(), targets.getX(), targets.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_NORMAL);
				if (skill.getRanged() <= 0) { // 無方向施展
					Thread.sleep(getRightInterval(4)); // 延遲
				} else {
					Thread.sleep(getRightInterval(3)); // 延遲
				}
				pc.set_atkskill_implement(false);
			}
		} catch (Exception e) {
			//
		}
	}

	/**
	 * 範圍技能
	 */
	private void rng_skill(final L1PcInstance pc, final L1Character targets) {
		try {
			int mob_count = 0;
			for (L1Object objid : World.get().getVisibleObjects(pc, pc.getRngskill_rng())) {
				if (objid instanceof L1MonsterInstance) {
					L1MonsterInstance mob = (L1MonsterInstance) objid;
					if (mob.isDead()) {
						continue;
					}
					if (mob.getHiddenStatus() == 1 || mob.getHiddenStatus() == 2 || mob.getHiddenStatus() == 3) {
						continue;
					}
					mob_count++;
				}
			}
			if (mob_count < pc.getRngskill_mob()) { // 周圍怪物數量小於設定數量
				return;
			}
			if (!checkRatio(pc.getRngskill_mp())) { // MP設定條件不滿足
				return;
			}
			if (!pc.get_Rngskill_implement()) { // CD
				return;
			}
			if (pc.isSkillDelay()) { // 技能延遲施放中
				return;
			}
			final L1Skills skill = SkillsTable.get().getTemplate(pc.getRngskill_id());
			final L1SkillUse skillUse = new L1SkillUse();
			if (pc.getCurrentMp() >= skill.getMpConsume()) { // 施放該技能需要的魔力
				skillUse.handleCommands(pc, pc.getRngskill_id(), targets.getId(), targets.getX(), targets.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_NORMAL);
				if (skill.getRanged() <= 0) { // 無方向施展
					Thread.sleep(getRightInterval(4)); // 延遲
				} else {
					Thread.sleep(getRightInterval(3)); // 延遲
				}
				pc.set_Rngskill_implement(false);
			}
		} catch (Exception e) {
			//
		}
	}

	/**
	 * 輔助技能
	 */
	private void buff_skill(final L1PcInstance pc, final L1Character targets, final int skillrng, final int location) {
		try {
			final L1Skills skill = SkillsTable.get().getTemplate(pc.BuffSkillSize());
			final L1SkillUse skillUse = new L1SkillUse();
			if (pc.getCurrentMp() >= skill.getMpConsume()) { // 施放該技能需要的魔力
				if (targets.isDead()) // 加入施放前判斷目標是否已死亡
					return;
				skillUse.handleCommands(pc, pc.getAtkskill_id(), targets.getId(), targets.getX(), targets.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_NORMAL);
				pc.set_atkskill_implement(false);
			}
		} catch (Exception e) {
			//
		}
	}

	/** 座標轉換成面向 */
	public int NodeToDir(Node node, int x, int y) {
		int[] dirx = { 0, 1, 1, 1, 0, -1, -1, -1 };
		int[] diry = { -1, -1, 0, 1, 1, 1, 0, -1 };
		for (int i = 0; i < 8; i++) {
			int tmpx = x + dirx[i];
			int tmpy = y + diry[i];
			// System.out.println("當前遍歷面向:"+i+" 得到xy="+tmpx+","+tmpy);
			// System.out.println("當前節點xy="+node.x+","+node.y);
			if (tmpx == node.x && tmpy == node.y)
				return i;
		}
		return -1;
	}

	public Node findAPath(Node startNode, Node endNode) {
		// 把起點加入open list
		openList.add(startNode);
		int seach_tiem = 0;
		while (openList.size() > 0 && seach_tiem < 200) {
			// 遍歷 open list 。查找 F值最小的節點，把它作為當前要處理的節點
			Node currentNode = findMinFNodeInOpneList();
			// 從open list中移除
			openList.remove(currentNode);
			// 把這個節點移到 close list
			closeList.add(currentNode);

			ArrayList<Node> neighborNodes = findNodeDir(currentNode);
			for (Node node : neighborNodes) {
				if (exists(openList, node)) {
					foundPoint(currentNode, node);
				} else {
					notFoundPoint(currentNode, endNode, node);
				}
			}
			if (find(openList, endNode) != null) {
				return find(openList, endNode);
			}
			seach_tiem++;
		}
		return find(openList, endNode);
	}

	public ArrayList<Node> findNodeDir(Node currentNode) { // 遍歷8個面向
		ArrayList<Node> arrayList = new ArrayList<Node>();
		for (int i = 0; i < 8; i++) {
			int accX = (currentNode.x + dirx[i]);
			int accY = (currentNode.y + diry[i]);
			boolean found = false;
			found = pc.getMap().isPassable3(accX, accY, i);
			if (found && !exists(closeList, accX, accY)) { // 如果可以通行 加上不存在關閉列表
				arrayList.add(new Node(accX, accY));
			}
		}
		return arrayList;
	}

	public static final int STEP = 10;

	private int calcG(Node start, Node node) {
		int G = STEP;
		int parentG = node.parent != null ? node.parent.G : 0;
		return G + parentG;
	}

	private int calcH(Node end, Node node) {
		int step = Math.abs(node.x - end.x) + Math.abs(node.y - end.y);
		return step * STEP;
	}

	private void notFoundPoint(Node tempStart, Node end, Node node) {
		node.parent = tempStart;
		node.G = calcG(tempStart, node);
		node.H = calcH(end, node);
		node.calcF();
		openList.add(node);
	}

	private void foundPoint(Node tempStart, Node node) {
		int G = calcG(tempStart, node);
		if (G < node.G) {
			node.parent = tempStart;
			node.G = G;
			node.calcF();
		}
	}

	public Node findMinFNodeInOpneList() {
		Node tempNode = openList.get(0);
		for (Node node : openList) {
			if (node.F < tempNode.F) {
				tempNode = node;
			}
		}
		return tempNode;
	}

	public ArrayList<Node> findNeighborNodes(Node currentNode) {
		ArrayList<Node> arrayList = new ArrayList<Node>();
		// 僅僅考慮上下左右，不考慮斜對角
		int topX = currentNode.x;
		int topY = currentNode.y - 1;
		if (!exists(closeList, topX, topY)) {
			arrayList.add(new Node(topX, topY));
		}
		int bottomX = currentNode.x;
		int bottomY = currentNode.y + 1;
		if (!exists(closeList, bottomX, bottomY)) {
			arrayList.add(new Node(bottomX, bottomY));
		}
		int leftX = currentNode.x - 1;
		int leftY = currentNode.y;
		if (!exists(closeList, leftX, leftY)) {
			arrayList.add(new Node(leftX, leftY));
		}
		int rightX = currentNode.x + 1;
		int rightY = currentNode.y;
		if (!exists(closeList, rightX, rightY)) {
			arrayList.add(new Node(rightX, rightY));
		}
		return arrayList;
	}

	public static Node find(List<Node> nodes, Node point) {
		for (Node n : nodes)
			if ((n.x == point.x) && (n.y == point.y)) {
				return n;
			}
		return null;
	}

	public static boolean exists(List<Node> nodes, Node node) {
		for (Node n : nodes) {
			if ((n.x == node.x) && (n.y == node.y)) {
				return true;
			}
		}
		return false;
	}

	public static boolean exists(List<Node> nodes, int x, int y) {
		for (Node n : nodes) {
			if ((n.x == x) && (n.y == y)) {
				return true;
			}
		}
		return false;
	}

	public static class Node {
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int x;
		public int y;

		public int F;
		public int G;
		public int H;

		public void calcF() {
			this.F = this.G + this.H;
		}

		public Node parent;
	}

	/**
	 * 無目標行走 true = 移動結束 false = 無法移動
	 * 
	 * @param pc
	 * @return
	 */
	private boolean GoallessWalking(final L1PcInstance pc) {
		try {
			/*
			 * if (this.h == -1) { int rtime = 0; do { final Random r = new Random(); this.h = r.nextInt(7); rtime++; if(rtime > 20){ reUpdata(pc); } } while
			 * (!pc.getMap().isPassable(pc.getX() + this.DIR_TABLE[this.h][0], pc.getY() + this.DIR_TABLE[this.h][1], this.h)); } else { this.h = this.checkObject(pc, this.h); }
			 * //System.out.println("結果:"+this.h); if (this.h != -1) { setDirectionMove(pc, h);// 人物移動
			 * 
			 * return true; } else { this.h = -1; }
			 */
			if (this.h == -1) {
				do {
					final Random r = new Random();
					this.h = r.nextInt(7);
				} while (!pc.getMap().isPassable(pc.getX() + this.DIR_TABLE[this.h][0], pc.getY() + this.DIR_TABLE[this.h][1], this.h, pc));
			} else {
				this.h = this.checkObject(pc, this.h);
			}
			if (this.h != -1) {

				// 增加可否移動判斷
				// if(WalkingChecking(pc)){
				// return false;
				// }
				setDirectionMove(pc, h);// 人物移動

				/*
				 * int SPEED =SprTable.get().getMoveSpeed(this.pc.getTempCharGfx(), this.pc.getCurrentWeapon());
				 * 
				 * if (SPEED < ThreadPoolSetNew.WALK_SPEED) { SPEED = ThreadPoolSetNew.WALK_SPEED; } SPEED = (SPEED * ThreadPoolSetNew.REXT) / 100;
				 * Thread.sleep(ThreadPoolSetNew.WALK_SPEED);
				 */

				// 移動速度延遲
				// Thread.sleep(getRightInterval(2) + 20);

				return true;
			} else {
				this.h = -1;
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 傳送前先檢查
	 * 
	 * @param pc
	 */
	private void FlyChecking1(final L1PcInstance pc) {
		try {

			if (pc.getInventory().checkItem(40100, 1)) {
				pc.getInventory().consumeItem(40100, 1);
				// 直接調用真實呼叫
				L1Teleport.randomTeleport(pc, true);
				Thread.sleep(1000);
				target = null;
				atk_target = null;
			} else if (pc.IsBuyTeleport() || pc.IsTeleportAuto()) { // 自動買白瞬
				if (pc.getInventory().checkItem(40308, 7000)) {
					pc.getInventory().consumeItem(40308, 7000);
					pc.getInventory().storeItem(40100, 100);
					pc.sendPackets(new S_ServerMessage("自動購入 瞬間移動卷軸 100張 花費7000。"));
					return;
				} else {
					pc.setIsAuto(false);
					pc.setRestartAuto(0);
					pc.setRestartAutoStartSec(0);
					// L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), 5, true);
					L1Teleport.teleport(pc, 33444, 32797, (short) 4, 5, true);
					pc.sendPackets(new S_ServerMessage("你沒有足夠的錢購買瞬間移動卷軸。\\fU【關閉】內掛！"));
				}
			} else {
				target = null;
				atk_target = null;
				// 20240526 如果開啟無怪順移 沒開啟自動買順卷時 偵測到沒順卷 則關閉內掛 並傳送回村
				// GoallessWalking(pc);
				pc.setIsAuto(false);
				pc.setRestartAuto(0);
				pc.setRestartAutoStartSec(0);
				pc.setTeleportAuto(false);
				// L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), 5, true);
				L1Teleport.teleport(pc, 33444, 32797, (short) 4, 5, true);
				pc.sendPackets(new S_ServerMessage("身上沒有瞬間移動卷軸，\\fU【關閉】內掛！"));
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 傳送新版
	 * 
	 * @author Nick
	 */
	private void NickFlyNoChecking(final L1PcInstance pc) {
		try {
				if (pc.getInventory().checkItem(40100, 1)) {
					pc.getInventory().consumeItem(40100, 1);
					// 直接調用真實呼叫
					L1Teleport.randomTeleport(pc, true);
					Thread.sleep(1000);
					target = null;
					atk_target = null;
				} else {
					if (pc.getInventory().checkItem(40308, 7000)) {
						pc.getInventory().consumeItem(40308, 7000);
						pc.getInventory().storeItem(40100, 100);
						pc.sendPackets(new S_ServerMessage("自動購入 瞬間移動卷軸 100張 花費7000。"));
					} else {
						target = null;
						atk_target = null;
						pc.setIsAuto(false);
						pc.setRestartAuto(0);
						pc.setRestartAutoStartSec(0);
						L1Teleport.teleport(pc, 33444, 32797, (short) 4, 5, true);
						pc.sendPackets(new S_ServerMessage("你沒有足夠的錢購買瞬間移動卷軸，先幫你傳回村了。"));
						pc.sendPackets(new S_ServerMessage("\\fU【關閉】內掛！"));
					}
				}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 傳送回掛機起始點<br>
	 * true = 有敵人 (執行飛行) <br>
	 * false = 沒有敵人 (繼續往下執行)<br>
	 * 
	 * @param pc
	 * @return
	 */
	private void teleport_setpoint(final L1PcInstance pc) {
		try {
			pc.setTeleportX(pc.getOutReturnX());
			pc.setTeleportY(pc.getOutReturnY());
			pc.setTeleportMapId((short) pc.getMapId());
			final Random r = new Random();
			this.h = r.nextInt(7);
			pc.setTeleportHeading(this.h);
			teleportation(pc);
			target = null;
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 確認是否被攻擊 詛咒型 一般偵測 <br>
	 * true = (執行飛行) <br>
	 * false = (繼續往下執行)<br>
	 * 
	 * @param pc
	 * @return
	 */
	private boolean CheckBuff(final L1PcInstance pc) {
		try {
			if (pc.hasSkillEffect(STATUS_CURSE_PARALYZED)) {
				if (pc.IsAttackTeleport()) {
					NickFlyNoChecking(pc);
				}
				return true;
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 確認是否被攻擊 PvP 增強型 <br>
	 * true = (執行飛行) <br>
	 * false = (繼續往下執行)<br>
	 * 
	 * @param pc
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean CheckPvP(final L1PcInstance pc) {
		try {
			if (pc.hasSkillEffect(L1SkillId.ATTACK_PVP)) {
				if (pc.getAutoPvP()) { // 開啟了PvP順移
					boolean action = false;
					// 道具類型1 確認
					if (pc.getInventory().checkItem(N1AutoMaticConfigNumber._4, N1AutoMaticConfigNumber._5)) {
						action = pc.getInventory().consumeItem(N1AutoMaticConfigNumber._4, N1AutoMaticConfigNumber._5);
						// 不足 確認道具類型2
					} else if (pc.getInventory().checkItem(N1AutoMaticConfigNumber._6, N1AutoMaticConfigNumber._7)) {
						action = pc.getInventory().consumeItem(N1AutoMaticConfigNumber._6, N1AutoMaticConfigNumber._7);
					}
					if (action) {
						L1Location newLocation = pc.getLocation().randomLocation(25, true);
						final int newX = newLocation.getX();
						final int newY = newLocation.getY();
						pc.setTeleportX(newX);
						pc.setTeleportY(newY);
						pc.setTeleportMapId((short) pc.getMapId());
						pc.setTeleportHeading(pc.getHeading());
						teleportation(pc);
						target = null;
					} else {
						pc.sendPackets(new S_ServerMessage("\\aG很抱歉，您的道具不足無法使用此服務。"));
					}
				}
				return true;
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 確認範圍內是否有仇人<br>
	 * 
	 * @param pc
	 * @return true = 有敵人 (執行飛行) <br>
	 *         false = 沒有敵人 (繼續往下執行)<br>
	 */
	@SuppressWarnings("unused")
	private boolean ConfirmTheEnemy(final L1PcInstance pc, final int rng) {
		try {
			if (pc.IsEnemyTeleport()) {// 遇見仇人進行順移開啟
				boolean ok = false;
				for (L1Object objid : World.get().getVisibleObjects(pc, rng)) {
					if (objid instanceof L1PcInstance) {
						L1PcInstance _pc = (L1PcInstance) objid;
						if (pc.isInEnemyList(_pc.getName())) {
							NickFlyNoChecking(pc);
							ok = true;
							break;
						}
					}
				}
				if (ok) {
					return true;
				}
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 搜尋目標
	 * 
	 * @return true = 有搜尋到目標 false = 無搜尋到目標 搜尋怪物
	 */
	private boolean SearchTarget(final L1PcInstance pc) {
		try {
			L1MonsterInstance mob1 = null;
			int max_d = 200;
			for (int i = 10; i >= 1; i--) {
				for (L1Object objid : World.get().getVisibleObjects(pc, i)) {
					if (objid instanceof L1MonsterInstance) {
						L1MonsterInstance mob = (L1MonsterInstance) objid;

						if (mob.isDead()) {
							continue;
						}

						if (mob.getHiddenStatus() == 1 || mob.getHiddenStatus() == 2 || mob.getHiddenStatus() == 3) {
							continue;
						}

						// for (Integer id : pc.TargetList()) {
						// if (mob.getId() == id) {
						// continue;
						// }
						// }

						if (pc.TargetList().contains(mob.getId())) {
							continue;
						}

						// // 忽略活動怪物
						// if(EventSpawnMobTable.getInstance().getAutoignore(mob.getNpcId())) 20240524 有問題
						// continue;

						// // 內掛忽略清單
						// if(BotNpcSet.getInstance().getAutoignore(mob.getNpcId())) 20240524 有問題
						// continue;

						// A星怪物父節點數量
						// int d = mob_parentSize(mob.getX(),mob.getY());
						int d = pc.getLocation().getTileLineDistance(mob.getLocation());
						if (d <= max_d && d > 0 & d < tmp_traget_d) {
							max_d = d;
							mob1 = mob;
							// tmp_traget_d = mob_parentSize(mob.getX(),mob.getY());
							tmp_traget_d = d;
						}
					}
				}
			}

			if (mob1 == null) {
				// FlyChecking(pc);
				return false;
			} else {
				/*
				 * if (pc.getNA432() < 0) { pc.setNA432(3); }
				 */
				target = mob1;
				return true;

			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 確認怪物目標進行攻擊<br>
	 * true = 怪物已經死亡<br>
	 * false = 怪物尚未死亡<br>
	 * 
	 * @param pc
	 * @param target
	 */
	private boolean AttackMon(final L1PcInstance pc, final L1Character targets) {
		try {
			// pc.setNA432(-1);
			if (pc.TargetListSize() > 0) {
				pc.clearTargetList();
			}

			targets.onAction(pc);

			// 連擊
			if (pc.get_Add_ContAttackRnd() > 0 && _random.nextInt(1000) < pc.get_Add_ContAttackRnd()) {
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), 17737)); // 自訂動畫
				for (int i = 0; i < pc.get_Add_ContAttack(); i++) {
					target.onAction(pc);
					if (i % 3 == 2) {
						pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7020));
					}
				}
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7020));
			}
			atk_target = targets;

			Thread.sleep(getRightInterval(1) + 20); // 攻速延遲

			if (!targets.isDead()) {
				return false;
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	private static Random _random = new Random();

	/**
	 * 傳送後的處理(刷新專用)<br>
	 * pet doll 不移動
	 * 
	 * @param pc
	 */
	private static void pc_teleportation(L1PcInstance pc) {
		try {

			if (pc == null) {
				return;
			}

			if (pc.getOnlineStatus() == 0) {
				return;
			}

			if (pc.getNetConnection().getIp() == null) {
				return;
			}

			if (pc.getNetConnection() == null) {
				return;
			}

			if (pc.isDead()) {
				return;
			}

			if (pc.isTeleport()) {
				return;
			}

			pc.getMap().setPassable(pc.getLocation(), true);

			short mapId = pc.getTeleportMapId();

			if ((pc.isDead())) {
				return;
			}

			int x = pc.getTeleportX();
			int y = pc.getTeleportY();
			int head = pc.getTeleportHeading();
			// 玩家傳送前所在地圖 by terry0412
			L1Map map = L1WorldMap.get().getMap(mapId);

			if ((!map.isInMap(x, y)) && (!pc.isGm())) {
				x = pc.getX();
				y = pc.getY();
				mapId = pc.getMapId();
			}

			World.get().moveVisibleObject(pc, mapId);

			pc.setLocation(x, y, mapId);// 設置角色新座標
			pc.setHeading(head);

			pc.setOleLocX(x);
			pc.setOleLocY(y);

			// 更新角色所在的地圖
			boolean isUnderwater = pc.getMap().isUnderwater();
			pc.sendPackets(new S_MapID(pc, pc.getMapId(), isUnderwater));

			// 不是鬼魂 不是GM 不在隱身狀態 則發送自身資訊
			if ((!pc.isGhost()) && (!pc.isGmInvis()) && (!pc.isInvisble())) {
				pc.broadcastPacket(new S_OtherCharPacks(pc));
			}

			// if (pc.isReserveGhost()) {
			// pc.endGhost();
			// }

			pc.sendPackets(new S_OwnCharPack(pc));
			pc.removeAllKnownObjects();
			pc.updateObject();
			pc.sendVisualEffectAtTeleport();
			pc.sendPackets(new S_CharVisualUpdate(pc));

			pc.killSkillEffectTimer(32);// 冥想術
			pc.setCallClanId(0);

			final HashSet<L1PcInstance> subjects = new HashSet<L1PcInstance>();
			subjects.add(pc);

			/**
			 * 可見物更新處理
			 */
			for (L1PcInstance updatePc : subjects) {
				updatePc.updateObject();
			}

			if (pc.hasSkillEffect(167)) {// 風之枷鎖
				pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), (int) pc.getSkillEffectTimeSec(167)));
			}

			// 新增座標障礙宣告
			if (!pc.isGmInvis()) {// 排除GM隱身
				pc.getMap().setPassable(pc.getLocation(), false);
			}

			// pc.getPetModel();// 恢復寵物目前模式

		} catch (Exception e) {
			// 解除傳送鎖定
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			// 解除傳送鎖定
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		}
	}

	/**
	 * 隨機傳送處理
	 * 
	 * @param pc
	 */
	private static void teleportation(L1PcInstance pc) {
		try {

			if (pc == null) {
				return;
			}

			if (pc.getOnlineStatus() == 0) {
				return;
			}

			if (pc.getNetConnection().getIp() == null) {
				return;
			}

			if (pc.getNetConnection() == null) {
				return;
			}

			if (pc.isDead()) {
				return;
			}

			if (pc.isTeleport()) {
				return;
			}

			pc.getMap().setPassable(pc.getLocation(), true);

			short mapId = pc.getTeleportMapId();

			if ((pc.isDead())) {
				return;
			}

			int x = pc.getTeleportX();
			int y = pc.getTeleportY();
			int head = pc.getTeleportHeading();
			// 玩家傳送前所在地圖 by terry0412
			L1Map map = L1WorldMap.get().getMap(mapId);

			if ((!map.isInMap(x, y)) && (!pc.isGm())) {
				x = pc.getX();
				y = pc.getY();
				mapId = pc.getMapId();
			}

			World.get().moveVisibleObject(pc, mapId);

			pc.setLocation(x, y, mapId);// 設置角色新座標
			pc.setHeading(head);

			pc.setOleLocX(x);
			pc.setOleLocY(y);

			// 更新角色所在的地圖
			boolean isUnderwater = pc.getMap().isUnderwater();
			pc.sendPackets(new S_MapID(pc, pc.getMapId(), isUnderwater));

			// 不是鬼魂 不是GM 不在隱身狀態 則發送自身資訊
			if ((!pc.isGhost()) && (!pc.isGmInvis()) && (!pc.isInvisble())) {
				pc.broadcastPacket(new S_OtherCharPacks(pc));
			}

			// if (pc.isReserveGhost()) {
			// pc.endGhost();
			// }

			pc.sendPackets(new S_OwnCharPack(pc));
			pc.removeAllKnownObjects();
			pc.updateObject();
			pc.sendVisualEffectAtTeleport();
			pc.sendPackets(new S_CharVisualUpdate(pc));

			pc.killSkillEffectTimer(32);// 冥想術
			pc.setCallClanId(0);

			final HashSet<L1PcInstance> subjects = new HashSet<L1PcInstance>();
			subjects.add(pc);

			if (!pc.isGhost()) {
				// 可以攜帶寵物
				if (pc.getMap().isTakePets()) {
					// 寵物的跟隨移動
					for (final L1NpcInstance petNpc : pc.getPetList().values()) {
						// 主人身邊隨機座標取回
						final L1Location loc = pc.getLocation().randomLocation(3, false);
						int nx = loc.getX();
						int ny = loc.getY();

						if ((pc.getMapId() == 5125) || (pc.getMapId() == 5131) || (pc.getMapId() == 5132) || (pc.getMapId() == 5133) || (pc.getMapId() == 5134)) { // 寵物戰戰場
							nx = 32799 + _random.nextInt(5) - 3;
							ny = 32864 + _random.nextInt(5) - 3;
						}

						teleport(petNpc, nx, ny, mapId, head);

						if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
							final L1SummonInstance summon = (L1SummonInstance) petNpc;
							pc.sendPackets(new S_NPCPack(summon));

						} else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
							final L1PetInstance pet = (L1PetInstance) petNpc;
							pc.sendPackets(new S_NPCPack(pet));
						}

						for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(petNpc)) {
							// 畫面內可見人物 認識更新
							visiblePc.removeKnownObject(petNpc);
							subjects.add(visiblePc);

						}
					}

				}

				// 娃娃的跟隨移動
				if (!pc.getDolls().isEmpty()) {
					// 主人身邊隨機座標取回
					final L1Location loc = pc.getLocation().randomLocation(3, false);
					final int nx = loc.getX();
					final int ny = loc.getY();

					final Object[] dolls = pc.getDolls().values().toArray();
					for (final Object obj : dolls) {
						final L1DollInstance doll = (L1DollInstance) obj;
						teleport(doll, nx, ny, mapId, head);
						pc.sendPackets(new S_NPCPack_Doll(doll, pc));
						for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(doll)) {
							// 畫面內可見人物 認識更新
							visiblePc.removeKnownObject(doll);
						}
					}
				}
				// 超級娃娃
				/*
				 * if (pc.get_power_doll() != null) { // 主人身邊隨機座標取回 final L1Location loc = pc.getLocation().randomLocation(3, false); final int nx = loc.getX(); final int ny = loc.getY();
				 * 
				 * teleport(pc.get_power_doll(), nx, ny, mapId, head); pc.sendPackets(new S_DollPack(pc.get_power_doll(), pc));
				 * 
				 * for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(pc.get_power_doll())) { // 畫面內可見人物 認識更新 visiblePc.removeKnownObject(pc.get_power_doll()); } }
				 */
			}

			/**
			 * 可見物更新處理
			 */
			for (L1PcInstance updatePc : subjects) {
				updatePc.updateObject();
			}

			if (pc.hasSkillEffect(167)) {// 風之枷鎖
				pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), (int) pc.getSkillEffectTimeSec(167)));
			}

			// 新增座標障礙宣告
			if (!pc.isGmInvis()) {// 排除GM隱身
				pc.getMap().setPassable(pc.getLocation(), false);
			}

			// pc.getPetModel();// 恢復寵物目前模式

		} catch (Exception e) {
			// 解除傳送鎖定
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			// 解除傳送鎖定
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		}
	}

	private int courceRange = 200;

	protected int _serchCource(L1PcInstance pc, int x, int y) {
		int i;
		int locCenter = courceRange + 1;
		int diff_x = x - locCenter; // 與X實際位置的差異
		int diff_y = y - locCenter; // Ｙ實際差
		int[] locBace = { pc.getX() - diff_x, pc.getY() - diff_y, 0, 0 }; // Ｘ
																			// Ｙ
		// 方向
		// 初期方向
		int[] locNext = new int[4];
		int[] locCopy;
		int[] dirFront = new int[5];
		boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
		LinkedList<int[]> queueSerch = new LinkedList<int[]>();

		// 設置搜索地圖
		for (int j = courceRange * 2 + 1; j > 0; j--) {
			for (i = courceRange - Math.abs(locCenter - j); i >= 0; i--) {
				serchMap[j][locCenter + i] = true;
				serchMap[j][locCenter - i] = true;
			}
		}

		// 初期方向設置
		int[] firstCource = { 2, 4, 6, 0, 1, 3, 5, 7 };
		for (i = 0; i < 8; i++) {
			System.arraycopy(locBace, 0, locNext, 0, 4);
			_moveLocation(locNext, firstCource[i]);
			if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
				// 最短經路見場合:鄰
				return firstCource[i];
			}
			if (serchMap[locNext[0]][locNext[1]]) {
				int tmpX = locNext[0] + diff_x;
				int tmpY = locNext[1] + diff_y;
				boolean found = false;
				switch (i) {
				case 0:
					found = pc.getMap().isPassable(tmpX, tmpY + 1, i);
					break;
				case 1:
					found = pc.getMap().isPassable(tmpX - 1, tmpY + 1, i);
					break;
				case 2:
					found = pc.getMap().isPassable(tmpX - 1, tmpY, i);
					break;
				case 3:
					found = pc.getMap().isPassable(tmpX - 1, tmpY - 1, i);
					break;
				case 4:
					found = pc.getMap().isPassable(tmpX, tmpY - 1, i);
					break;
				case 5:
					found = pc.getMap().isPassable(tmpX + 1, tmpY - 1, i);
					break;
				case 6:
					found = pc.getMap().isPassable(tmpX + 1, tmpY, i);
					break;
				case 7:
					found = pc.getMap().isPassable(tmpX + 1, tmpY + 1, i);
					break;
				}
				if (found) {// 移動經路場合
					locCopy = new int[4];
					System.arraycopy(locNext, 0, locCopy, 0, 4);
					locCopy[2] = firstCource[i];
					locCopy[3] = firstCource[i];
					queueSerch.add(locCopy);
				}
				serchMap[locNext[0]][locNext[1]] = false;
			}
		}
		locBace = null;

		// 最短經路探索
		while (queueSerch.size() > 0) {
			locBace = queueSerch.removeFirst();
			_getFront(dirFront, locBace[2]);
			for (i = 4; i >= 0; i--) {
				System.arraycopy(locBace, 0, locNext, 0, 4);
				_moveLocation(locNext, dirFront[i]);
				if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
					return locNext[3];
				}
				if (serchMap[locNext[0]][locNext[1]]) {
					int tmpX = locNext[0] + diff_x;
					int tmpY = locNext[1] + diff_y;
					boolean found = false;
					switch (i) {
					case 0:
						found = pc.getMap().isPassable(tmpX, tmpY + 1, i);
						break;
					case 1:
						found = pc.getMap().isPassable(tmpX - 1, tmpY + 1, i);
						break;
					case 2:
						found = pc.getMap().isPassable(tmpX - 1, tmpY, i);
						break;
					case 3:
						found = pc.getMap().isPassable(tmpX - 1, tmpY - 1, i);
						break;
					case 4:
						found = pc.getMap().isPassable(tmpX, tmpY - 1, i);
						break;
					}
					if (found) {// 移動經路場合
						locCopy = new int[4];
						System.arraycopy(locNext, 0, locCopy, 0, 4);
						locCopy[2] = dirFront[i];
						queueSerch.add(locCopy);
					}
					serchMap[locNext[0]][locNext[1]] = false;
				}
			}
			locBace = null;
		}
		return -1; // 目標までの経路がない場合
	}

	private void _moveLocation(int[] ary, int dir) {
		// protected int[][] DIR_TABLE = { { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 },{ -1, -1 } };
		ary[0] += DIR_TABLE[dir][0];
		ary[1] += DIR_TABLE[dir][1];
		ary[2] = dir;
	}

	private void _getFront(int[] ary, int d) {
		switch (d) {
		case 1:
			ary[4] = 2;
			ary[3] = 0;
			ary[2] = 1;
			ary[1] = 3;
			ary[0] = 7;
			break;
		case 2:
			ary[4] = 2;
			ary[3] = 4;
			ary[2] = 0;
			ary[1] = 1;
			ary[0] = 3;
			break;
		case 3:
			ary[4] = 2;
			ary[3] = 4;
			ary[2] = 1;
			ary[1] = 3;
			ary[0] = 5;
			break;
		case 4:
			ary[4] = 2;
			ary[3] = 4;
			ary[2] = 6;
			ary[1] = 3;
			ary[0] = 5;
			break;
		case 5:
			ary[4] = 4;
			ary[3] = 6;
			ary[2] = 3;
			ary[1] = 5;
			ary[0] = 7;
			break;
		case 6:
			ary[4] = 4;
			ary[3] = 6;
			ary[2] = 0;
			ary[1] = 5;
			ary[0] = 7;
			break;
		case 7:
			ary[4] = 6;
			ary[3] = 0;
			ary[2] = 1;
			ary[1] = 5;
			ary[0] = 7;
			break;
		case 0:
			ary[4] = 2;
			ary[3] = 6;
			ary[2] = 0;
			ary[1] = 1;
			ary[0] = 7;
			break;
		}
	}

	/**
	 * 判斷前進方向
	 * 
	 * @param pc
	 * @param h
	 * @return
	 */
	private int checkObject(L1PcInstance pc, int h) {
		if ((h >= 0) && (h <= 7)) {
			int x = pc.getX();
			int y = pc.getY();
			int h2 = _heading0[h];
			int h3 = _heading1[h];

			// protected static int _heading0[] = { 7, 0, 1, 2, 3, 4, 5, 6 };
			// protected static int _heading1[] = { 1, 2, 3, 4, 5, 6, 7, 0 };

			if (pc.getMap().isPassable(x, y, h)) {
				return h;
			} else if (pc.getMap().isPassable(x, y, h2)) {
				return h2;
			} else if (pc.getMap().isPassable(x, y, h3)) {
				return h3;
			}
		}
		return -1;
	}

	protected static final byte[] HEADING_TABLE_X = { 0, 1, 1, 1, 0, -1, -1, -1 };
	protected static final byte[] HEADING_TABLE_Y = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/**
	 * 人物進行移動
	 * 
	 * @param pc
	 * @param dir
	 */
	private void setDirectionMove(final L1PcInstance pc, final int dir) {
		int locx = pc.getX();
		int locy = pc.getY();
		locx += HEADING_TABLE_X[dir];
		locy += HEADING_TABLE_Y[dir];

		更新畫面++;
		// pc.sendPackets(new S_SystemMessage("移動步數" + 更新畫面));
		if (更新畫面 > 7) {

			pc.setTeleportX(locx);
			pc.setTeleportY(locy);
			pc.setTeleportMapId((short) pc.getMapId());
			pc.setTeleportHeading(dir);
			pc_teleportation(pc);
			// //動畫更新
			// if(ConfigBot.EFFECT){
			// pc.getautoeffect().setX(locx);
			// pc.getautoeffect().setY(locy);
			// }
			更新畫面 = 0;
			// pc.sendPackets(new S_SystemMessage("移動步數" + 更新畫面 + "歸零"));
		} else {

			// 解除舊座標障礙宣告
			pc.getMap().setPassable(pc.getLocation(), true);

			// 設置新作標點
			pc.getLocation().set(locx, locy);

			// 設置新面向
			pc.setHeading(dir);

			// 動畫更新
			/*
			 * if(Config.EFFECT){ pc.getautoeffect().getMove().setDirectionMove(dir); }
			 */
			pc.broadcastPacket(new S_MoveCharPacket(pc));
			pc.sendPackets(new S_MoveCharPacket(pc));

			pc.getMap().setPassable(pc.getLocation(), false);

			// 設置娃娃移動
			pc.setNpcSpeed();
		}
	}

	@SuppressWarnings("unused")
	private void reUpdata(final L1PcInstance pc) {
		int locx = pc.getX();
		int locy = pc.getY();
		int dir = pc.getHeading();
		locx += HEADING_TABLE_X[dir];
		locy += HEADING_TABLE_Y[dir];
		pc.setTeleportX(locx);
		pc.setTeleportY(locy);
		pc.setTeleportMapId((short) pc.getMapId());
		pc.setTeleportHeading(dir);
		pc_teleportation(pc);
		// //動畫更新
		// if(ConfigBot.EFFECT){
		// pc.getautoeffect().setX(locx);
		// pc.getautoeffect().setY(locy);
		// }
		更新畫面 = 0;
	}

	/**
	 * 寵物的傳送
	 * 
	 * @param npc
	 * @param x
	 * @param y
	 * @param map
	 * @param head
	 */
	private static void teleport(L1NpcInstance npc, int x, int y, short map, int head) {
		try {
			World.get().moveVisibleObject(npc, map);
			L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), true, 2);
			npc.setX(x);
			npc.setY(y);
			npc.setMap(map);
			npc.setHeading(head);
			L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), false, 2);
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 正常的速度
	 * 
	 * @param type
	 *            檢測類型:<br>
	 *            1攻擊<br>
	 *            2移動 <br>
	 *            3有方向施法 <br>
	 *            4無方向施法
	 * @return 正常應該接收的速度(MS)
	 */
	private int getRightInterval(final int type) {
		int interval = 0;

		switch (type) {
		case 1:
			interval = SprTable.get().getAttackSpeed(this.pc.getTempCharGfx(), this.pc.getCurrentWeapon() + 1);
			// System.out.println("內掛spr Attackspeed:"+interval + "/"+ this.pc.getCurrentWeapon()+"/GFX:"+this.pc.getTempCharGfx());
			interval *= 1.05;
			break;

		case 2:
			interval = SprTable.get().getMoveSpeed(this.pc.getTempCharGfx(), 0);
			// System.out.println("內掛spr movespeed:"+interval + "/"+ this.pc.getCurrentWeapon()+"/GFX:"+this.pc.getTempCharGfx());
			interval *= 1.05;
			break;
		case 3:
			interval = SprTable.get().getDirSpellSpeed(this.pc.getTempCharGfx());
			interval *= 1.05;
			break;
		case 4:
			interval = SprTable.get().getNodirSpellSpeed(this.pc.getTempCharGfx());
			interval *= 1.05;
			break;
		default:
			return 0;
		}
		return intervalR(type, interval);
	}

	private int intervalR(final int type, int interval) {
		try {
			if (pc.getInventory().checkEquipped(20383)) {
				return interval * 2;
			}

			if (this.pc.isHaste()) {
				interval *= 0.755;// 0.755
			}

			if (type == 2 && this.pc.isFastMovable()) {
				interval *= 0.755;// 0.665
			}

			// 龍騎士 血之渴望
			/*
			 * if (type == 2 && this.pc.isFastAttackable()) { interval *= 0.665;// 0.775 }
			 */

			if (this.pc.isBrave()) {
				interval *= 0.755;// 0.755
			}

			if (this.pc.isBraveX()) {
				interval *= 0.755;// 0.755
			}

			if (this.pc.isElfBrave()) {
				interval *= 0.855;// 0.855
			}

			if (type == 1 && this.pc.isElfBrave()) {
				interval *= 0.9;// 0.9
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return interval;
	}

	/**
	 * 技能[設置]冷卻判斷
	 * 
	 * @param 要判斷的設置
	 * @return 大於返回true 小於返回false
	 */
	public boolean checkSkillCd(final PerformanceTimer time, final long type_cd) {
		if (type_cd != 0) {
			if ((time.get() <= type_cd * 1000)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 百分比計算
	 * 
	 * @param 要判斷的設置
	 * @return 大於返回true 小於返回false
	 */
	public boolean checkRatio(final int setratio) {
		try {
			// double wpTemp ;
			double max = pc.getMaxMp();
			double now = pc.getCurrentMp();
			double wpTemp = now / max * 100;
			if (wpTemp > setratio) {
				return true;
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}
	
	/**
	 * 檢測 拿遠距離武器時, 身上是否具備箭矢類道具
	 * 
	 * @author Nick
	 */
	public boolean checkArrowCount(final L1PcInstance pc) {
		// 奧里哈魯根鍍金骨箭, 古代之箭, 箭, 銀箭, 黃金箭, 米索莉箭, 黑色米索莉箭, 奧里哈魯根箭, 象牙塔的箭, 地靈的黑色米索莉箭, 水靈的黑色米索莉箭, 風靈的黑色米索莉箭, 火靈的黑色米索莉箭, 修練者 的箭
		int[] itemId = { 40741, 40742, 40743, 40744, 40745, 40746, 40747, 40748, 42007, 84077, 84078, 84079, 84080, 241210 };
		for (int i = 0; i < itemId.length; i++) {
			if (pc.getInventory().checkItem(itemId[i])) {
				return true;
			}
		}
		return false;
	}
	
}