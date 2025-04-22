package com.lineage.server.types;

/**
 * 座標點
 * 
 * @author daien
 */
public class Point {

	protected int _x = 0;
	protected int _y = 0;

	public Point() {
	}

	public Point(final int x, final int y) {
		_x = x;
		_y = y;
	}

	public Point(final Point pt) {
		_x = pt._x;
		_y = pt._y;
	}

	/**
	 * X點座標
	 * 
	 * @return
	 */
	public int getX() {
		return _x;
	}

	/**
	 * 設置X點座標
	 * 
	 * @param x
	 */
	public void setX(final int x) {
		_x = x;
	}

	/**
	 * Y點座標
	 * 
	 * @return
	 */
	public int getY() {
		return _y;
	}

	/**
	 * 設置Y點座標
	 * 
	 * @param y
	 */
	public void setY(final int y) {
		_y = y;
	}

	/**
	 * 設置座標點
	 * 
	 * @param pt 座標點
	 */
	public void set(final Point pt) {
		_x = pt._x;
		_y = pt._y;
	}

	/**
	 * 設置座標點
	 * 
	 * @param x 座標點X
	 * @param y 座標點Y
	 */
	public void set(final int x, final int y) {
		_x = x;
		_y = y;
	}

	private static final int HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final int HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/**
	 * 指定面向前進位置座標
	 * 
	 * @param heading 0~7 面向
	 */
	public void forward(final int heading) {
		_x += HEADING_TABLE_X[heading];
		_y += HEADING_TABLE_Y[heading];
	}

	/**
	 * 指定面向反向前進位置座標
	 * 
	 * @param heading 0~7 面向
	 */
	public void backward(final int heading) {
		_x -= HEADING_TABLE_X[heading];
		_y -= HEADING_TABLE_Y[heading];
	}

	/**
	 * 指定座標直線距離
	 * 
	 * @param pt
	 * @return 距離質
	 */
	public double getLineDistance(final Point pt) {
		final long diffX = pt.getX() - getX();
		final long diffY = pt.getY() - getY();
		return Math.sqrt((diffX * diffX) + (diffY * diffY));
	}

	/**
	 * 指定座標直線距離(相對位置最大距離)
	 * 
	 * @param pt
	 * @return 距離質
	 */
	/*
	 * public double getLineDistanceX(final Point pt) { final long diffX =
	 * pt.getX() - this.getX(); final long diffY = pt.getY() - this.getY();
	 * return Math.max(Math.abs(diffX), Math.abs(diffY)); }
	 */

	/**
	 * 指定座標直線距離
	 * 
	 * @param x
	 * @param y
	 * @return 距離質
	 */
	public double getLineDistance(final int x, final int y) {
		final long diffX = x - getX();
		final long diffY = y - getY();
		return Math.sqrt((diffX * diffX) + (diffY * diffY));
	}

	/**
	 * 指定座標直線距離(相對位置最大距離)
	 * 
	 * @param pt
	 * @return 距離質
	 */
	public int getTileLineDistance(final Point pt) {
		return Math.max(Math.abs(pt.getX() - getX()), Math.abs(pt.getY() - getY()));
	}

	/**
	 * 指定座標距離(XY距離總合)
	 * 
	 * @param pt
	 * @return 距離質
	 */
	public int getTileDistance(final Point pt) {
		return Math.abs(pt.getX() - getX()) + Math.abs(pt.getY() - getY());
	}

	/**
	 * 指定座標19格範圍內
	 * 
	 * @param pt
	 * @return 指定座標在19格範圍內 返回true
	 */
	public boolean isInScreen(final Point pt) {
		final int dist = getTileDistance(pt);
		// 3.5c可見範圍再度修正
		if (dist > 19) { // 當tile距離 > 19 的時候，判定為不在畫面內(false)
			return false;

		} else if (dist <= 18) { // 當tile距離 <= 18 的時候，判定為位於同一個畫面內(true)
			return true;

		} else {
			// 顯示區的座標系統 (18, 18)
			// 3.5c可見範圍再度修正
			final int dist2 = Math.abs(pt.getX() - (getX() - 18)) + Math.abs(pt.getY() - (getY() - 18));
			if ((19 <= dist2) && (dist2 <= 52)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 是否與指定座標位置重疊
	 * 
	 * @param pt
	 * @return TRUE是 FALSE否
	 */
	public boolean isSamePoint(final Point pt) {
		return ((pt.getX() == getX()) && (pt.getY() == getY()));
	}

	@Override
	public int hashCode() {
		return (7 * getX()) + getY();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Point)) {
			return false;
		}
		final Point pt = (Point) obj;
		return (getX() == pt.getX()) && (getY() == pt.getY());
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", _x, _y);
	}
	
	/**
	 * 指定座標x格範圍內 內掛 hpc20207
	 * 
	 * @param pt
	 * @param arange
	 * @return 指定座標在x格範圍內 返回true
	 */
	public boolean isInScreenRange(final Point pt,final int arange) {
		// 如果等於0等於未設置 直接返回true
		if(arange == 0){return true;}
		int dist = getTileDistance(pt);
		// 3.5c可見範圍再度修正
		if (dist > arange) { // 當tile距離 > 19 的時候，判定為不在畫面內(false)
			return false;
		} else { // 當tile距離 <= 18 的時候，判定為位於同一個畫面內(true)
			return true;
		}
	}
}
