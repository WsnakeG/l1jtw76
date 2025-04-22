package com.lineage.server.templates;

import java.util.HashMap;
import java.util.Map;

import com.lineage.data.event.LeavesSet;
import com.lineage.server.command.GmHtml;
import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 人物其他相關設置表
 * 
 * @author DaiEn
 */
public class L1PcOther {

	private static final Map<Integer, StringBuilder> _titleList = new HashMap<Integer, StringBuilder>();

	private static boolean _isStart = false;

	public static final int CLEVLE0 = 1;
	public static final int CLEVLE1 = 2;
	public static final int CLEVLE2 = 4;
	public static final int CLEVLE3 = 8;
	public static final int CLEVLE4 = 16;
	public static final int CLEVLE5 = 32;
	public static final int CLEVLE6 = 64;
	public static final int CLEVLE7 = 128;
	public static final int CLEVLE8 = 256;
	public static final int CLEVLE9 = 512;
	public static final int CLEVLE10 = 1024;

	// 測試防天變
	public static final String[] ADDNAME = new String[] { "あ", "ア", "い", "イ", "う", "ウ", "え", "エ", "お", "オ",
			"か", "カ", "き", "キ", "く", "ク", "け", "ケ", "こ", "コ", "さ", "サ", "し", "シ", "す", "ス", "せ", "セ", "そ",
			"ソ", "た", "タ", "ち", "チ", "つ", "ツ", "て", "テ", "と", "ト", "な", "ナ", "に", "ニ", "ぬ", "ヌ", "ね", "ネ",
			"の", "ノ", "は", "ハ", "ひ", "ヒ", "ふ", "フ", "へ", "ヘ", "ほ", "ホ", "ま", "マ", "み", "ミ", "む", "ム", "め",
			"メ", "も", "モ", "や", "ヤ", "ゆ", "ユ", "よ", "ヨ", "ら", "ラ", "り", "リ", "る", "ル", "れ", "レ", "ろ", "ロ",
			"わ", "ワ", "を", "ヲ", "ん", "ン", };

	public static void load() {
		if (!_isStart) {

			_titleList.put(CLEVLE0, new StringBuilder(""));
			_titleList.put(CLEVLE1, new StringBuilder("\\fD"));// 淺藍\fD
			_titleList.put(CLEVLE2, new StringBuilder("\\f="));// 黃色\f=
			_titleList.put(CLEVLE3, new StringBuilder("\\fH"));// 米黃\fH
			_titleList.put(CLEVLE4, new StringBuilder("\\f_"));// 粉紅\f_
			_titleList.put(CLEVLE5, new StringBuilder("\\f2"));// 亮綠\f2
			_titleList.put(CLEVLE6, new StringBuilder("\\fF"));// 深綠\fF
			_titleList.put(CLEVLE7, new StringBuilder("\\fT"));// 淡綠\fT
			_titleList.put(CLEVLE8, new StringBuilder("\\fE"));// 暗綠\fE
			_titleList.put(CLEVLE9, new StringBuilder("\\f0"));// 黑色\f0
			_titleList.put(CLEVLE10, new StringBuilder("\\f?"));// 淺灰\f?

			/*
			 * \\f1 藍色 \\f2 亮綠 \\f3 紅色 \\f4 紫色 \\f5 紫紅 \\f6 暗紅 \\f7 灰色 \\f8 深灰
			 * \\f9 暗灰 \\f0 黑色
			 */

			_isStart = true;
		}
	}

	private L1ItemInstance _item = null;

	/**
	 * 物品暫存
	 * 
	 * @param item
	 */
	public void set_item(final L1ItemInstance item) {
		_item = item;
	}

	/**
	 * 物品暫存
	 * 
	 * @return
	 */
	public L1ItemInstance get_item() {
		return _item;
	}

	private boolean _shopSkill = false;

	/**
	 * true = 全職技能導師
	 * 
	 * @param shopSkill
	 */
	public void set_shopSkill(final boolean shopSkill) {
		_shopSkill = shopSkill;
	}

	/**
	 * true = 全職技能導師
	 * 
	 * @return
	 */
	public boolean get_shopSkill() {
		return _shopSkill;
	}

	// 所有人
	private int _objid = 0;

	public void set_objid(final int objid) {
		_objid = objid;
	}

	public int get_objid() {
		return _objid;
	}

	// 翻頁
	private int _page = 0;

	public void set_page(final int page) {
		_page = page;
	}

	public int get_page() {
		return _page;
	}

	// 計時地圖編號
	private int _usemap = 0;

	/**
	 * 計時地圖編號
	 * 
	 * @param usemap
	 */
	public void set_usemap(final int usemap) {
		_usemap = usemap;
	}

	/**
	 * 計時地圖編號
	 * 
	 * @return
	 */
	public int get_usemap() {
		return _usemap;
	}

	// 計時地圖可用時間
	private int _usemapTime = 0;

	/**
	 * 計時地圖可用時間
	 * 
	 * @param usemapTime
	 */
	public void set_usemapTime(final int usemapTime) {
		_usemapTime = usemapTime;
	}

	/**
	 * 計時地圖可用時間
	 * 
	 * @return
	 */
	public int get_usemapTime() {
		return _usemapTime;
	}

	// 玩家使用道具已增加的HP值
	private int _addhp = 0;

	/**
	 * 玩家使用道具已增加的HP值
	 * 
	 * @param addhp
	 */
	public void set_addhp(int addhp) {
		if (addhp < 0) {
			addhp = 0;
		}
		_addhp = addhp;
	}

	/**
	 * 玩家使用道具已增加的HP值
	 * 
	 * @return
	 */
	public int get_addhp() {
		return _addhp;
	}

	// 玩家使用道具已增加的MP值
	private int _addmp = 0;

	/**
	 * 玩家使用道具已增加的MP值
	 * 
	 * @param addmp
	 */
	public void set_addmp(int addmp) {
		if (addmp < 0) {
			addmp = 0;
		}
		_addmp = addmp;
	}

	/**
	 * 玩家使用道具已增加的MP值
	 * 
	 * @return
	 */
	public int get_addmp() {
		return _addmp;
	}

	// 人物積分
	private int _score = 0;

	/**
	 * 設置積分
	 * 
	 * @param score
	 */
	public void set_score(int score) {
		if (score < 0) {
			score = 0;
		}
		_score = score;
	}

	/**
	 * 增加積分
	 * 
	 * @param score
	 */
	public void add_score(int score) {
		if (score < 0) {
			score = 0;
		}
		_score += score;
		if (_score < 0) {
			_score = 0;
		}
	}

	/**
	 * 傳回積分
	 * 
	 * @return
	 */
	public int get_score() {
		return _score;
	}

	// 名稱色彩
	private int _color = CLEVLE0;

	/**
	 * 名稱色彩
	 * 
	 * @param color
	 */
	public void set_color(final int color) {
		_color = color;
	}

	/**
	 * 名稱色彩編號
	 * 
	 * @param color
	 * @param tg
	 * @return
	 */
	public boolean set_color(final int color, final int tg) {
		set_score(tg);
		_color = color;
		return true;
	}

	/**
	 * 傳回名稱色彩編號
	 * 
	 * @return
	 */
	public int get_color() {
		return _color;
	}

	/**
	 * 名稱色彩文字代號
	 * 
	 * @return
	 */
	public String color() {
		final StringBuilder stringBuilder = _titleList.get(_color);
		if (stringBuilder != null) {
			return stringBuilder.toString();
		}
		return "";
	}

	// 血盟技能
	private int _clanskill = 0;

	/**
	 * 設置血盟技能
	 * 
	 * @param clanskill <BR>
	 *            0:無<BR>
	 *            1:狂暴(增加物理攻擊力)<BR>
	 *            2:寂靜(增加物理傷害減免)<BR>
	 *            4:魔擊(增加魔法攻擊力)<BR>
	 *            8:消魔(增加魔法傷害減免)<BR>
	 */
	public void set_clanskill(final int clanskill) {
		_clanskill = clanskill;
	}

	/**
	 * 傳回血盟技能
	 * @return<BR>
	 * 			0:無<BR>
	 *             1:狂暴(增加物理攻擊力)<BR>
	 *             2:寂靜(增加物理傷害減免)<BR>
	 *             4:魔擊(增加魔法攻擊力)<BR>
	 *             8:消魔(增加魔法傷害減免)<BR>
	 */
	public int get_clanskill() {
		return _clanskill;
	}

	// 殺人次數
	private int _killCount = 0;

	/**
	 * 增加殺人次數
	 * 
	 * @param i
	 */
	public void add_killCount(final int i) {
		_killCount += i;
	}

	/**
	 * 設置殺人次數
	 * 
	 * @param i
	 */
	public void set_killCount(final int i) {
		_killCount = i;
	}

	/**
	 * 傳回殺人次數
	 */
	public int get_killCount() {
		return _killCount;
	}

	// 被殺次數
	private int _deathCount = 0;

	/**
	 * 增加被殺次數
	 * 
	 * @param i
	 */
	public void add_deathCount(final int i) {
		_deathCount += i;
	}

	/**
	 * 設置被殺次數
	 * 
	 * @param i
	 */
	public void set_deathCount(final int i) {
		_deathCount = i;
	}

	/**
	 * 傳回被殺次數
	 */
	public int get_deathCount() {
		return _deathCount;
	}

	// 使用GM HTML選單
	private GmHtml _gmHtml = null;

	/**
	 * 使用GM HTML選單
	 * 
	 * @return
	 */
	public GmHtml get_gmHtml() {
		return _gmHtml;
	}

	/**
	 * 設置使用GM HTML選單
	 * 
	 * @return
	 */
	public void set_gmHtml(final GmHtml gmHtml) {
		_gmHtml = gmHtml;
	}

	private int _login_time;// 登出時間

	/**
	 * 登出時間
	 * 
	 * @param level_gift
	 */
	public void set_login_time(final int login_time) {
		_login_time = login_time;
	}

	/**
	 * 登出時間
	 * 
	 * @return
	 */
	public int get_login_time() {
		return _login_time;
	}

	private int _teaves_time = 0;// 殷海薩的祝福-休息系統(分鐘)

	/**
	 * 殷海薩的祝福-休息系統(分鐘)
	 * 
	 * @return
	 */
	public int get_teaves_time() {
		return _teaves_time;
	}

	/**
	 * 殷海薩的祝福-休息系統(分鐘)
	 * 
	 * @param teaves_time
	 */
	public void set_teaves_time(final int teaves_time) {
		_teaves_time = teaves_time;
	}

	private int _teaves_time_exp = 0;// 殷海薩的祝福-休息系統(EXP)

	/**
	 * 殷海薩的祝福-休息系統(EXP)
	 * 
	 * @return
	 */
	public int get_teaves_time_exp() {
		return _teaves_time_exp;
	}

	/**
	 * 殷海薩的祝福-休息系統(EXP)
	 * 
	 * @param teaves_time_exp
	 */
	public void set_teaves_time_exp(final int teaves_time_exp) {
		if (teaves_time_exp < 0) {
			_teaves_time_exp = 0;

		} else {
			_teaves_time_exp = Math.min(teaves_time_exp, LeavesSet.MAXEXP);
		}
	}
}
