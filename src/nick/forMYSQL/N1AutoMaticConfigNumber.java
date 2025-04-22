package nick.forMYSQL;

public class N1AutoMaticConfigNumber {

	// 20240421
	// 伺服器使用編號
	/** 自動狩獵系統 (True = 新版 / False = 舊版) */
	public static boolean _1 = N1AutoMaticConfigTable.get().getTemplate(1).getMessage().equals("true");

	/** 自動練功的搜尋半徑 20~150 */
	public static int _2 = Integer.valueOf(N1AutoMaticConfigTable.get().getTemplate(2).getMessage());

	/** PvP被攻擊強化瞬移消耗的物品 優先判斷使用 -1為不開啟 核心控制已完善 道具自身可設定時效性或不刪除 */
	public static int _3 = Integer.valueOf(N1AutoMaticConfigTable.get().getTemplate(3).getMessage());

	/** PvP被攻擊強化瞬移消耗的物品數量 */
	public static int _4 = Integer.valueOf(N1AutoMaticConfigTable.get().getTemplate(4).getMessage());
	
	/** 掛機多久沒有攻擊動作自動順移時間 */
	public static int _5 = Integer.valueOf(N1AutoMaticConfigTable.get().getTemplate(5).getMessage());
	
	/** PvP被攻擊強化瞬移消耗的物品 -1為不開啟 核心控制已完善 */
	public static int _6 = Integer.valueOf(N1AutoMaticConfigTable.get().getTemplate(6).getMessage());
	
	/** PvP被攻擊強化瞬移消耗的物品數量 */
	public static int _7 = Integer.valueOf(N1AutoMaticConfigTable.get().getTemplate(7).getMessage());
	
	/** 掛機幾分鐘後進行線程重置(請依照客戶系統自行調整) */
	public static int RESTART_AUTO = Integer.valueOf(N1AutoMaticConfigTable.get().getTemplate2("RESTART_AUTO").getMessage());
	
	/** 掛機重置處理秒數(2~5秒最佳) */
	public static int RESTART_AUTO_START = Integer.valueOf(N1AutoMaticConfigTable.get().getTemplate2("RESTART_AUTO_START").getMessage());
	
}
