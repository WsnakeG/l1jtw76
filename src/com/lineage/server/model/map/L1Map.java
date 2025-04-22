package com.lineage.server.model.map;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.types.Point;

/**
 * Map位置資訊取回
 */
public abstract class L1Map {
	private static L1NullMap _nullMap = new L1NullMap();
	
	public abstract L1V1Map copyMap(int paramInt);

	protected L1Map() {
	}

	/**
	 * 傳回地圖編號
	 * 
	 * @return 地圖編號
	 */
	public abstract int getId();

	/**
	 * 起點X
	 * 
	 * @return
	 */
	public abstract int getX();

	/**
	 * 起點Y
	 * 
	 * @return
	 */
	public abstract int getY();

	/**
	 * 地圖寬度(終點X - 起點X)
	 * 
	 * @return
	 */
	public abstract int getWidth();

	/**
	 * 地圖高度(終點Y - 起點Y)
	 * 
	 * @return
	 */
	public abstract int getHeight();

	/**
	 * 指定された座標の値を返す。 推奨されていません。このメソッドは、既存コードとの互換性の為に提供されています。 L1Mapの利用者は通常、マップにどのような値が格納されているかを知る必要はありません。 また、格納されている値に依存するようなコードを書くべきではありません。 デバッグ等の特殊な場合に限り、このメソッドを利用できます。
	 * 
	 * @param x
	 *            座標のX値
	 * @param y
	 *            座標のY値
	 * @return 指定された座標の値
	 */
	public abstract int getTile(int x, int y);

	/**
	 * 指定された座標の値を返す。 推奨されていません。このメソッドは、既存コードとの互換性の為に提供されています。 L1Mapの利用者は通常、マップにどのような値が格納されているかを知る必要はありません。 また、格納されている値に依存するようなコードを書くべきではありません。 デバッグ等の特殊な場合に限り、このメソッドを利用できます。
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @return 指定座標的値 0 : 無法通過 15: 一般區域 16: 裝飾物件 31: 安全區域 47: 戰鬥區域
	 */
	public abstract int getOriginalTile(int x, int y);

	/**
	 * 傳回座標是否在地圖指定可用位置
	 * 
	 * @param pt
	 *            座標Point
	 * @return 位置可用傳回 true
	 */
	public abstract boolean isInMap(Point pt);

	/**
	 * 傳回座標是否在地圖指定可用位置
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @return 位置可用傳回 true
	 */
	public abstract boolean isInMap(int x, int y);

	/**
	 * 指定座標通行可能
	 * 
	 * @param pt
	 *            座標Point
	 * @param cha
	 * @return true:可以通過 false:不能通過
	 */
	public abstract boolean isPassable(Point pt, final L1Character cha);

	/**
	 * 指定座標通行可能
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @param cha
	 * @return true:可以通過 false:不能通過
	 */
	public abstract boolean isPassable(int x, int y, final L1Character cha);

	/**
	 * 指定座標通行可能
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @param cha
	 * @return true:可以通過 false:不能通過
	 */
	public abstract boolean isPassable(int x, int y, int heading);

	/**
	 * 指定座標heading方向通行可能
	 * 
	 * @param pt
	 *            座標Point
	 * @param heading
	 * @param cha
	 * @return true:可以通過 false:不能通過
	 */
	public abstract boolean isPassable(Point pt, int heading, final L1Character cha);

	/**
	 * 指定座標heading方向通行可能
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @param heading
	 * @param cha
	 * @return true:可以通過 false:不能通過
	 */
	public abstract boolean isPassable(int x, int y, int heading, final L1Character cha);

	/**
	 * 指定座標heading方向通行可能
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @param heading
	 * @return
	 */
	public abstract boolean isPassableDna(final int x, final int y, final int heading);

	/**
	 * 該座標門的判斷
	 * 
	 * @param x
	 * @param y
	 * @param heading
	 * @param npc
	 * @return
	 */
	public abstract boolean isDoorPassable(final int x, final int y, final int heading, final L1NpcInstance npc);

	/**
	 * 設定座標障礙宣告
	 * 
	 * @param pt
	 *            座標Point
	 * @param isPassable
	 *            true:可以通過 false:不能通過
	 */
	public abstract void setPassable(Point pt, boolean isPassable);

	/**
	 * 設定座標障礙宣告
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @param isPassable
	 *            true:可以通過 false:不能通過
	 * @param door
	 *            0:門／ 1:門＼ 2:一般
	 */
	public abstract void setPassable(int x, int y, boolean isPassable, int door);

	/**
	 * 指定座標位置是安全區域。
	 * 
	 * @param pt
	 * @return 安全區域返回true
	 */
	public abstract boolean isSafetyZone(Point pt);

	/**
	 * 指定座標位置是安全區域。
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @return 安全區域返回true
	 */
	public abstract boolean isSafetyZone(int x, int y);

	/**
	 * 指定座標位置是戰鬥區域。
	 * 
	 * @param pt
	 *            座標資料
	 * @return 戰鬥區域返回true
	 */
	public abstract boolean isCombatZone(Point pt);

	/**
	 * 指定座標位置是戰鬥區域。
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @return 戰鬥區域返回true
	 */
	public abstract boolean isCombatZone(int x, int y);

	/**
	 * 指定座標位置是一般區域。
	 * 
	 * @param pt
	 *            座標資料
	 * @return 一般區域返回true
	 */
	public abstract boolean isNormalZone(Point pt);

	/**
	 * 指定座標位置是一般區域。
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @return 一般區域返回true
	 */
	public abstract boolean isNormalZone(int x, int y);

	/**
	 * 指定座標遠程攻擊是否能通過。
	 * 
	 * @param x
	 *            座標X値
	 * @param y
	 *            座標Y値
	 * @param heading
	 *            方向
	 * @return 遠程攻擊能通過返回true
	 */
	public abstract boolean isArrowPassable(int x, int y, int heading);

	/**
	 * 水中
	 * 
	 * @return 地圖在水中 返回true
	 */
	public abstract boolean isUnderwater();

	/**
	 * 記憶座標
	 * 
	 * @return 可使用記憶座標 返回true
	 */
	public abstract boolean isMarkable();

	/**
	 * 傳送
	 * 
	 * @return 可使用傳送 返回true
	 */
	public abstract boolean isTeleportable();

	/**
	 * 回捲
	 * 
	 * @return 可使用回捲 返回true
	 */
	public abstract boolean isEscapable();

	/**
	 * 復活可能
	 * 
	 * @return 可復活
	 */
	public abstract boolean isUseResurrection();

	/**
	 * 魔杖使用
	 * 
	 * @return 可使用魔杖
	 */
	public abstract boolean isUsePainwand();

	/**
	 * 死亡逞罰
	 * 
	 * @return 具有死亡逞罰
	 */
	public abstract boolean isEnabledDeathPenalty();

	/**
	 * 攜帶寵物
	 * 
	 * @return 可攜帶寵物 返回true
	 */
	public abstract boolean isTakePets();

	/**
	 * 呼叫寵物
	 * 
	 * @return 可呼叫寵物 返回true
	 */
	public abstract boolean isRecallPets();

	/**
	 * 使用物品
	 * 
	 * @return 可使用物品 返回true
	 */
	public abstract boolean isUsableItem();

	/**
	 * 使用技能
	 * 
	 * @return 可使用技能 返回true
	 */
	public abstract boolean isUsableSkill();

	/**
	 * 指定された座標が釣りゾーンであるかを返す。
	 * 
	 * @param x
	 *            座標のX値
	 * @param y
	 *            座標のY値
	 * @return 釣りゾーンであればtrue
	 */
	public abstract boolean isFishingZone(int x, int y);

	public abstract boolean isExistDoor(int x, int y);

	public abstract int isUsableShop();

	public static L1Map newNull() {
		return _nullMap;
	}

	/**
	 * 指定されたptのタイルの文字列表現を返す。
	 */
	public abstract String toString(Point pt);

	/**
	 * このマップがnullであるかを返す。
	 * 
	 * @return nullであれば、true
	 */
	public boolean isNull() {
		return false;
	}

	/**
	 * 是否可以使用血盟支援令
	 * 
	 * @return
	 */
	public abstract boolean isArrows();

	/**
	 * Astar專用
	 * 
	 * @author Hpc20207
	 * @return 通行可能true
	 */
	public abstract boolean isPassable3(int x, int y, int heading);

	/**
	 * Astar專用
	 * 
	 * @author Hpc20207
	 * @return 通行可能true
	 */
	public abstract boolean isPassable3(Point pt);

	/**
	 * Astar專用
	 * 
	 * @author Hpc20207
	 * @return 通行可能true
	 */
	public abstract boolean isPassable3(int x, int y);

	/**
	 * Astar專用
	 * 
	 * @author Hpc20207
	 * @return 通行可能true
	 */
	public abstract boolean isPassable3(Point pt, int heading);
	
	/**
	 * 是否可以掛機
	 * 
	 * @return 0: 不能, 1: 可以
	 */
	public abstract boolean isAutoBot();
	
	// 屍魂塔
	public boolean isPassable(final int x, final int y) {
		// TODO Auto-generated method stub
		return false;
	}
}

/**
 * 何もしないMap。
 */
class L1NullMap extends L1Map {
	public L1NullMap() {
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getTile(final int x, final int y) {
		return 0;
	}

	@Override
	public int getOriginalTile(final int x, final int y) {
		return 0;
	}

	@Override
	public boolean isInMap(final int x, final int y) {
		return false;
	}

	@Override
	public boolean isInMap(final Point pt) {
		return false;
	}

	@Override
	public boolean isPassable(final int x, final int y, final L1Character cha) {
		return false;
	}

	@Override
	public boolean isPassable(final Point pt, final L1Character cha) {
		return false;
	}

	@Override
	public boolean isPassable(final int x, final int y, final int heading, final L1Character cha) {
		return false;
	}

	public boolean isPassable(final int x, final int y, final int heading) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPassableDna(final int x, final int y, final int heading) {
		return false;
	}

	@Override
	public boolean isPassable(final Point pt, final int heading, final L1Character cha) {
		return false;
	}

	@Override
	public boolean isDoorPassable(final int x, final int y, final int heading, final L1NpcInstance npc) {
		return false;
	}

	@Override
	public void setPassable(final int x, final int y, final boolean isPassable, final int door) {
	}

	@Override
	public void setPassable(final Point pt, final boolean isPassable) {
	}

	@Override
	public boolean isSafetyZone(final int x, final int y) {
		return false;
	}

	@Override
	public boolean isSafetyZone(final Point pt) {
		return false;
	}

	@Override
	public boolean isCombatZone(final int x, final int y) {
		return false;
	}

	@Override
	public boolean isCombatZone(final Point pt) {
		return false;
	}

	@Override
	public boolean isNormalZone(final int x, final int y) {
		return false;
	}

	@Override
	public boolean isNormalZone(final Point pt) {
		return false;
	}

	@Override
	public boolean isArrowPassable(final int x, final int y, final int heading) {
		return false;
	}

	@Override
	public boolean isUnderwater() {
		return false;
	}

	@Override
	public boolean isMarkable() {
		return false;
	}

	@Override
	public boolean isTeleportable() {
		return false;
	}

	@Override
	public boolean isEscapable() {
		return false;
	}

	@Override
	public boolean isUseResurrection() {
		return false;
	}

	@Override
	public boolean isUsePainwand() {
		return false;
	}

	@Override
	public boolean isEnabledDeathPenalty() {
		return false;
	}

	@Override
	public boolean isTakePets() {
		return false;
	}

	@Override
	public boolean isRecallPets() {
		return false;
	}

	@Override
	public boolean isUsableItem() {
		return false;
	}

	@Override
	public boolean isUsableSkill() {
		return false;
	}

	@Override
	public boolean isFishingZone(final int x, final int y) {
		return false;
	}

	/*
	 * public void set_door(int x, int y, boolean door) { }
	 */

	@Override
	public boolean isExistDoor(final int x, final int y) {
		return false;
	}

	@Override
	public int isUsableShop() {
		return 0;
	}

	@Override
	public String toString(final Point pt) {
		return "null";
	}

	@Override
	public boolean isNull() {
		return true;
	}

	@Override
	public boolean isArrows() {
		return false;
	}

	// //@Override
	public boolean isPassable3(int x, int y) {
		return false;
	}

	// //@Override
	public boolean isPassable3(Point pt) {
		return false;
	}

	// //@Override
	public boolean isPassable3(int x, int y, int heading) {
		return false;
	}

	// //@Override
	public boolean isPassable3(Point pt, int heading) {
		return false;
	}
	
	@Override
	public boolean isAutoBot() {
		return false;
	}
	
	@Override
	public L1V1Map copyMap(final int paramInt) {
		// TODO 自動生成的方法存根
		return null;
	}
}