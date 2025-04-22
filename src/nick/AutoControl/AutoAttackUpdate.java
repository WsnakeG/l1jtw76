package nick.AutoControl;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import nick.forMYSQL.N1AutoMaticConfigNumber;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Skills;

/**
 * 選單內掛訊息
 * 
 * @author Nick
 *
 */
public class AutoAttackUpdate {

	private static Logger _log = Logger.getLogger(AutoAttackUpdate.class.getName());

	private static AutoAttackUpdate _instance;

	public static AutoAttackUpdate get() {
		if (_instance == null) {
			_instance = new AutoAttackUpdate();
		}
		return _instance;
	}

	/**
	 * 基本 訊息更新
	 * 
	 * @author Nick
	 *
	 */
	public void StartMsg(final L1PcInstance pc) {
		try {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append((pc.getOutRange() == 0) ? "0格" : pc.getOutRange() + "格");// 0 定點巡邏 格數
			final String[] clientStrAry = stringBuilder.toString().split(",");
			pc.setTalkPage("home");
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "N_Action", clientStrAry));
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 技能 訊息更新
	 * 
	 * @author Nick
	 *
	 */
	public void SkillMsg(final L1PcInstance pc) {
		try {
			final StringBuilder stringBuilder1 = new StringBuilder();
			stringBuilder1.append((pc.getOpenskill_id() == 0) ? "未設定" : getskill_Name(pc, 1));// 0 開怪技能
			stringBuilder1.append(",");
			stringBuilder1.append((pc.getOpenskill_mp() == 0) ? "0%" : pc.getOpenskill_mp() + "%");// 1 開怪技能 MP
			stringBuilder1.append(",");
			stringBuilder1.append((pc.getOpenskill_cd() == 0) ? "0秒" : pc.getOpenskill_cd() + "秒");// 2 開怪技能 CD
			stringBuilder1.append(",");
			stringBuilder1.append((pc.getAtkskill_id() == 0) ? "未設定" : getskill_Name(pc, 2));// 3 攻擊技能
			stringBuilder1.append(",");
			stringBuilder1.append((pc.getAtkskill_mp() == 0) ? "0%" : pc.getAtkskill_mp() + "%");// 4 攻擊技能 MP
			stringBuilder1.append(",");
			stringBuilder1.append((pc.getAtkskill_cd() == 0) ? "0秒" : pc.getAtkskill_cd() + "秒");// 5 攻擊技能 CD
			stringBuilder1.append(",");
			stringBuilder1.append((pc.getRngskill_id() == 0) ? "未設定" : getskill_Name(pc, 3));// 6 範圍技能
			stringBuilder1.append(",");
			stringBuilder1.append((pc.getRngskill_rng() == 0) ? "0格" : pc.getRngskill_rng() + "格");// 7 範圍技能 幾格內
			stringBuilder1.append(",");
			stringBuilder1.append((pc.getRngskill_mob() == 0) ? "0隻" : pc.getRngskill_mob() + "隻");// 8 範圍技能 怪物數量
			stringBuilder1.append(",");
			stringBuilder1.append((pc.getRngskill_mp() == 0) ? "0%" : pc.getRngskill_mp() + "%");// 9 範圍技能 MP
			stringBuilder1.append(",");
			stringBuilder1.append((pc.getRngskill_cd() == 0) ? "0秒" : pc.getRngskill_cd() + "秒");// 10 範圍技能 CD
			stringBuilder1.append(",");

			final String[] clientStrAry = stringBuilder1.toString().split(",");
			pc.setTalkPage("skill");
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "N_Skill", clientStrAry));
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 內掛選單判斷
	 * 
	 * @param _pc
	 * @param cmd
	 * @return
	 */
	public boolean PcCommand(final L1PcInstance _pc, final String cmd) {
		if (cmd.equalsIgnoreCase("AutoStart1") && _pc.IsAuto() == false) { // 開啟自動練功(定點)
			if (checkStartAuto(_pc, 0)) {
				_pc.setRestartAuto(N1AutoMaticConfigNumber.RESTART_AUTO * (60 / N1AutoMaticConfigNumber.RESTART_AUTO_START)); // add 定時重置
				_pc.setRestartAutoStartSec(N1AutoMaticConfigNumber.RESTART_AUTO);
				// 增加定點巡航功能 紀錄開始位置
				_pc.setOutReturnX(_pc.getX());
				_pc.setOutReturnY(_pc.getY());
				_pc.setOutReturnMap(_pc.getMapId());
				for (Integer list : _pc.BuffSkillList()) {
					if (list != 0) {
						if (!_pc.hasSkillEffect(list)) {
							final L1Skills skill = SkillsTable.get().getTemplate(list);
							final L1SkillUse skillUse = new L1SkillUse();
							skillUse.handleCommands(_pc, list, _pc.getId(), _pc.getX(), _pc.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
						}
					}
				}
				_pc.setIsAuto(true);
				AutoAttack auto = new AutoAttack(_pc);
				auto.begin();
				_pc.sendPackets(new S_SystemMessage("\\fU【啟動】內掛！"));
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			}
		} else if (cmd.equalsIgnoreCase("AutoStart2") && _pc.IsAuto() == false) { // 開啟自動練功(順移)
			_pc.minusOutRange(99999999); // 清除定點範圍
			if (checkStartAuto(_pc, 1)) {
				_pc.setRestartAuto(N1AutoMaticConfigNumber.RESTART_AUTO * (60 / N1AutoMaticConfigNumber.RESTART_AUTO_START)); // add 定時重置
				_pc.setRestartAutoStartSec(N1AutoMaticConfigNumber.RESTART_AUTO);
				_pc.setIsAuto(true);
				AutoAttack auto = new AutoAttack(_pc);
				auto.begin();
				_pc.sendPackets(new S_SystemMessage("\\fU【啟動】內掛！"));
				_pc.sendPackets(new S_CloseList(_pc.getId()));

			}
		} else if (cmd.equalsIgnoreCase("AutoStop") && _pc.IsAuto()) { // 關閉自動練功
			_pc.setIsAuto(false);
			_pc.setRestartAuto(0);
			_pc.setRestartAutoStartSec(0);
			L1Teleport.teleport(_pc, _pc.getX(), _pc.getY(), _pc.getMapId(), 5, true);
			_pc.minusOutRange(99999999); // 清除定點範圍
			_pc.sendPackets(new S_SystemMessage("\\fU【關閉】內掛！"));
			_pc.sendPackets(new S_CloseList(_pc.getId()));
		} else if (cmd.equalsIgnoreCase("FixedPointReduce")) { // 自動練功(定點)範圍減少
			_pc.minusOutRange(5);
			StartMsg(_pc);
		} else if (cmd.equalsIgnoreCase("FixedPointAdd")) { // 自動練功(定點)範圍增加
			_pc.addOutRange(5);
			StartMsg(_pc);
		} else if (cmd.equalsIgnoreCase("FixedPointReturnToZero") && _pc.IsAuto() == false) {// 定點巡邏歸零
			_pc.minusOutRange(99999999);
			StartMsg(_pc);
		}

		else if (cmd.equalsIgnoreCase("N_Action")) { // 切換至基本設定選單
			StartMsg(_pc);
		} else if (cmd.equalsIgnoreCase("N_Skill")) { // 切換至技能設定選單
			SkillMsg(_pc);
		}

		else if (cmd.equalsIgnoreCase("FirstSkill")) {// 開怪技能設定
			_pc.setSkillType(1);
			set_skill_page(_pc, 0);
		} else if (cmd.equalsIgnoreCase("FirstSkillClear")) {// 開怪技能清除
			_pc.setOpenskill_id(0);
			SkillMsg(_pc);
		} else if (cmd.equalsIgnoreCase("FirstSkillMp")) {// 開怪MP設置
			_pc.setInfoType(1);
			set_skill_page1(_pc, 0);
		} else if (cmd.equalsIgnoreCase("FirstSkillCd")) {// 開怪CD設置
			_pc.setInfoType(2);
			set_skill_page1(_pc, 1);
		} else if (cmd.equalsIgnoreCase("AttackSkill")) {// 攻擊技能設定
			_pc.setSkillType(2);
			set_skill_page(_pc, 1);
		} else if (cmd.equalsIgnoreCase("AttackSkillClear")) {// 攻擊技能清除
			_pc.setAtkskill_id(0);
			SkillMsg(_pc);
		} else if (cmd.equalsIgnoreCase("AttackSkillMp")) {// 攻擊MP設置
			_pc.setInfoType(3);
			set_skill_page1(_pc, 0);
		} else if (cmd.equalsIgnoreCase("AttackSkillCd")) {// 攻擊CD設置
			_pc.setInfoType(4);
			set_skill_page1(_pc, 1);
		} else if (cmd.equalsIgnoreCase("AreaSkill")) {// 範圍技能設置
			_pc.setSkillType(3);
			set_skill_page(_pc, 2);
		} else if (cmd.equalsIgnoreCase("AreaSkillClear")) {// 範圍技能清除
			_pc.setRngskill_id(0);
			SkillMsg(_pc);
		} else if (cmd.equalsIgnoreCase("AreaSkillRange")) {// 範圍技能格數
			_pc.setInfoType(5);
			set_skill_page1(_pc, 2);
		} else if (cmd.equalsIgnoreCase("AreaSkillCount")) {// 範圍技能怪物數量
			_pc.setInfoType(6);
			set_skill_page1(_pc, 3);
		} else if (cmd.equalsIgnoreCase("AreaSkillMp")) {// 範圍MP設置
			_pc.setInfoType(7);
			set_skill_page1(_pc, 0);
		} else if (cmd.equalsIgnoreCase("AreaSkillCd")) {// 範圍CD設置
			_pc.setInfoType(8);
			set_skill_page1(_pc, 1);
		}
		// 特殊項 技能組設置回傳
		else if (cmd.contains("nick.auto.skillid.")) {// 內掛詳細設置回傳指令
			String recall = cmd.replace("nick.auto.skillid.", ""); // 點擊的位置
			set_skill_login(_pc, Integer.valueOf(recall));
			SkillMsg(_pc);
		}
		// 特殊項 命令包含指令則 技能類
		else if (cmd.contains("xauto.set.")) {// 內掛詳細設置回傳指令
			String recall = cmd.replace("xauto.set.", ""); // 點擊的位置
			int[] mplist = new int[] { 10, 20, 30, 40, 50, 60, 70, 80, 90 };
			int[] cdlist = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
			int number = Integer.valueOf(recall);
			if (number < 9) {
				switch (_pc.getInfoType()) {
				case 1: // 開怪MP
					_pc.setOpenskill_mp(mplist[number]);
					break;
				case 2: // 開怪CD
					_pc.setOpenskill_cd(cdlist[number]);
					break;
				case 3: // 攻擊MP
					_pc.setAtkskill_mp(mplist[number]);
					break;
				case 4: // 攻擊CD
					_pc.setAtkskill_cd(cdlist[number]);
					break;
				case 5: // 範圍格數
					_pc.setRngskill_rng(cdlist[number]);
					break;
				case 6: // 怪物數量
					_pc.setRngskill_mob(cdlist[number]);
					break;
				case 7: // 範圍MP
					_pc.setRngskill_mp(mplist[number]);
					break;
				case 8: // 範圍CD
					_pc.setRngskill_cd(cdlist[number]);
					break;
				}
			}
			SkillMsg(_pc);
		}
		else {
			return false;
		}
		return true;
	}

	/**
	 * 系統可設置技能展示 - 動態 <br>
	 * 
	 * @param pc
	 * @param mode
	 * @author hpc20207
	 */
	public void set_page_show_skill(final L1PcInstance pc, int mode) {
		final StringBuilder msg = new StringBuilder();
		String[] typename = new String[] { "開怪", "攻擊", "範圍" };
		int[][] listtype = new int[][] { _openmob_skill, _autoskill_attack, _rng_skill };
		msg.append(typename[mode - 1]); // 先加入標題
		msg.append(",");
		int i = 0;
		for (int j = 0; j < listtype[mode - 1].length; j++) {
			final L1Skills skill = SkillsTable.get().getTemplate(listtype[mode - 1][j]);
			msg.append(skill.getName() + ",");
			i++;
		}
		// 補足對話檔
		for (int k = i; k < 50; k++) {
			msg.append(" ");
			msg.append(",");
		}

		final String[] clientStrAry = msg.toString().split(",");
		pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "x_skshow", clientStrAry));
	}

	/**
	 * 顯示技能名稱 - 選單顯示作用
	 * 
	 * @param mode
	 *            1:開怪 2:攻擊 3:範圍
	 */
	public String getskill_Name(final L1PcInstance pc, final int mode) {
		String Name = "";
		if (mode == 1) {
			final L1Skills skill = SkillsTable.get().getTemplate(pc.getOpenskill_id());
			Name = skill.getName();
		} else if (mode == 2) {
			final L1Skills skill = SkillsTable.get().getTemplate(pc.getAtkskill_id());
			Name = skill.getName();
		} else if (mode == 3) {
			final L1Skills skill = SkillsTable.get().getTemplate(pc.getRngskill_id());
			Name = skill.getName();
		}
		return Name;
	}

	/**
	 * 技能紀錄使用 - 選單顯示作用
	 * 
	 * @param mode
	 *            回傳項
	 */
	public void set_skill_login(final L1PcInstance pc, int mode) {
		String[] temp_menu;
		if (pc.getSkillType() == 1) {
			temp_menu = getskill_list(pc, 0, 2);
			pc.setOpenskill_id(Integer.valueOf(temp_menu[mode]));
			// 設置開怪技能可施放距離
			final L1Skills skill = SkillsTable.get().getTemplate(Integer.valueOf(temp_menu[mode]));
			pc.setOpenskill_rng(skill.getRanged());
		} else if (pc.getSkillType() == 2) {
			temp_menu = getskill_list(pc, 1, 2);
			pc.setAtkskill_id(Integer.valueOf(temp_menu[mode]));
			// 設置攻擊技能可施放距離
			final L1Skills skill = SkillsTable.get().getTemplate(Integer.valueOf(temp_menu[mode]));
			pc.setAtkskill_rng(skill.getRanged());
		} else if (pc.getSkillType() == 3) {
			temp_menu = getskill_list(pc, 2, 2);
			pc.setRngskill_id(Integer.valueOf(temp_menu[mode]));
			// 設置範圍技能可施放距離
			final L1Skills skill = SkillsTable.get().getTemplate(Integer.valueOf(temp_menu[mode]));
			pc.set_rngskill_skillrng(skill.getRanged());
		}
	}

	/**
	 * 已學習技能遍歷
	 * 
	 * @param pc
	 * @param mode
	 *            0:開怪 1:攻擊 2:範圍 3:輔助
	 * @param type
	 *            1:返回Name 2:返回ID
	 * @return
	 */
	public String[] getskill_list(final L1PcInstance pc, int mode, final int type) {
		int[][] listtype = new int[][] { _openmob_skill, _autoskill_attack, _rng_skill };
		final StringBuilder msg = new StringBuilder();
		int i = 0;
		for (int j = 0; j < listtype[mode].length; j++) {
			// 檢查是否已學習該法術
			if (SkillsTable.get().spellCheck(pc.getId(), listtype[mode][j])) {
				final L1Skills skill = SkillsTable.get().getTemplate(listtype[mode][j]);
				if (type == 1) {
					msg.append(skill.getName() + ",");
				} else {
					msg.append(skill.getSkillId() + ",");
				}
				i++;
			}
		}
		// 補足對話檔
		for (int k = i; k < 50; k++) {
			msg.append(" ");
			msg.append(",");
		}
		final String[] clientStrAry = msg.toString().split(",");
		return clientStrAry;
	}

	/**
	 * 顯示已學習並可設定清單 - 動態 <br>
	 * 
	 * @param pc
	 * @param mode
	 *            0:開怪 1:攻擊 2:範圍
	 * @author hpc20207
	 */
	public void set_skill_page(final L1PcInstance pc, int mode) {
		final StringBuilder msg = new StringBuilder();
		String[] typename = new String[] { "開怪", "攻擊", "範圍" };
		msg.append(typename[mode]); // 先加入標題
		msg.append(",");
		int i = 0;

		if (mode == 0) {
			for (int j = 0; j < _openmob_skill.length; j++) {
				// 檢查是否已學習該法術
				if (SkillsTable.get().spellCheck(pc.getId(), _openmob_skill[j])) {
					final L1Skills skill = SkillsTable.get().getTemplate(_openmob_skill[j]);
					msg.append(skill.getName() + ",");
					i++;
				}
			}
		} else if (mode == 1) {
			for (int j = 0; j < _autoskill_attack.length; j++) {
				if (SkillsTable.get().spellCheck(pc.getId(), _autoskill_attack[j])) {
					final L1Skills skill = SkillsTable.get().getTemplate(_autoskill_attack[j]);
					msg.append(skill.getName() + ",");
					i++;
				}
			}
		} else if (mode == 2) {
			for (int j = 0; j < _rng_skill.length; j++) {
				if (SkillsTable.get().spellCheck(pc.getId(), _rng_skill[j])) {
					final L1Skills skill = SkillsTable.get().getTemplate(_rng_skill[j]);
					msg.append(skill.getName() + ",");
					i++;
				}
			}
		}

		// 補足對話檔測試
		for (int k = i; k < 50; k++) {
			msg.append(" ");
			msg.append(",");
		}

		final String[] clientStrAry = msg.toString().split(",");
		pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "N_UseSkill", clientStrAry));
		// pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "N_Skill1", clientStrAry));
	}

	/**
	 * 展開詳細設置選單 - 動態(技能) <br>
	 * 
	 * @param pc
	 * @param mode
	 *            1:MP 2:CD
	 * @author hpc20207
	 */
	public void set_skill_page1(final L1PcInstance pc, int mode) {
		final StringBuilder msg = new StringBuilder();
		int[] typelist = new int[] { 10, 1, 1, 1 };
		String[] namelist = new String[] { "%", "秒", "格", "隻" };

		for (int i = 1; i < 10; i++) {
			msg.append(i * typelist[mode] + namelist[mode]);
			msg.append(",");
		}
		final String[] clientStrAry = msg.toString().split(",");
		pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "x_atset", clientStrAry));
	}

	/**
	 * 查詢可記錄技能清單
	 * 
	 * @param pc
	 */
	public void CanSkillList(final L1PcInstance pc, int mode) {
		final StringBuilder msg = new StringBuilder();
		if (mode == 1) {
			for (int i = 0; i < _autoskill_attack.length; i++) {
				if (CharSkillReading.get().spellCheck(pc.getId(), _autoskill_attack[i])) {
					final L1Skills skill = SkillsTable.get().getTemplate(_autoskill_attack[i]);
					msg.append(skill.getName() + ",");
				}
			}
		}
		final String[] clientStrAry = msg.toString().split(",");
		pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "x_autolist3", clientStrAry));
	}

	/**
	 * 查詢已登錄技能清單
	 * 
	 * @param pc
	 */
	public void SkillList(final L1PcInstance pc, int mode) {
		final StringBuilder msg = new StringBuilder();
		if (mode == 1) {
			for (Integer id : pc.AttackSkillList()) {
				final L1Skills skill = SkillsTable.get().getTemplate(id);
				msg.append(skill.getName() + ",");
			}
		}
		final String[] clientStrAry = msg.toString().split(",");
		pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "x_autolist3", clientStrAry));
	}

	/**
	 * 可紀錄的攻擊技能
	 */
	private static final int[] _autoskill_attack = {
			// 王族
			// 騎士
			// 法師
			VAMPIRIC_TOUCH, MANA_DRAIN, ENERGY_BOLT, ICE_DAGGER, WIND_CUTTER, CHILL_TOUCH, FIRE_ARROW, STALAC, FROZEN_CLOUD, FIREBALL, CALL_LIGHTNING, CONE_OF_COLD, ERUPTION, SUNBURST,
			TORNADO, BLIZZARD, LIGHTNING_STORM, FIRE_STORM, METEOR_STRIKE, DISINTEGRATE,
			// 妖精
			TRIPLE_ARROW, };

	/** 可紀錄的範圍技能 */
	private static final int[] _rng_skill = { TORNADO, BLIZZARD, FIRE_WALL, EARTHQUAKE, LIGHTNING_STORM, FIRE_STORM, METEOR_STRIKE, ICE_SPIKE, FIREBALL };

	/** 可紀錄的開怪技能 */
	private static final int[] _openmob_skill = { VAMPIRIC_TOUCH, MANA_DRAIN, ENERGY_BOLT, ICE_DAGGER, WIND_CUTTER, STALAC, FIREBALL, CONE_OF_COLD };


	/**
	 * 檢測是否為可以記錄的技能
	 * 
	 * @param Skillid
	 * @return
	 */
	public boolean isAttackSkill(int Skillid) {
		for (final int skillId : _autoskill_attack) {
			if (skillId == Skillid) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 檢測是否可以執行自動練功(定點及順移) checkStartAuto1(pc, type) </pre> type 0: 定點, type 1: 順移 </pre>
	 * 
	 * @author Nick
	 */
	public boolean checkStartAuto(final L1PcInstance pc, final int type) {
		if (!pc.getMap().isAutoBot()) {
			pc.sendPackets(new S_ServerMessage("此地圖無法使用 \\aE自動狩獵系統。"));
			return false;
		}
		if (pc.getWeapon() == null) {
			pc.sendPackets(new S_ServerMessage("手上並未裝備任何武器無法開始自動練功。"));
			return false;
		}

		switch (type) {
		case 0:
			if (pc.getOutRange() == 0) {
				pc.sendPackets(new S_ServerMessage("請增加範圍。"));
				return false;
			}
			break;
		case 1:
			if (pc.getInventory().checkItem(40100)) {
				return true;
			} else {
				if (pc.getInventory().checkItem(40308, 7000)) {
					pc.getInventory().consumeItem(40308, 7000);
					pc.getInventory().storeItem(40100, 100);
					pc.sendPackets(new S_ServerMessage("自動購入 瞬間移動卷軸 100張 花費7000。"));
					return true;
				} else {
					pc.sendPackets(new S_ServerMessage("你沒有足夠的錢購買瞬間移動卷軸。"));
					return false;
				}
			}
		}
		return true;
	}
}