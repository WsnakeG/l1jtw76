package com.lineage.server.types;

/**
 * 座標左上の点(left, top)、及び右下の点(right, bottom)によって囲まれる座標の領域を指定するクラス。
 */
public class Rectangle {

	private int _left;
	private int _top;
	private int _right;
	private int _bottom;

	public Rectangle(final Rectangle rect) {
		this.set(rect);
	}

	public Rectangle(final int left, final int top, final int right, final int bottom) {
		this.set(left, top, right, bottom);
	}

	public Rectangle() {
		this(0, 0, 0, 0);
	}

	public void set(final Rectangle rect) {
		this.set(rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight());
	}

	public void set(final int left, final int top, final int right, final int bottom) {
		_left = left;
		_top = top;
		_right = right;
		_bottom = bottom;
	}

	public int getLeft() {
		return _left;
	}

	public int getTop() {
		return _top;
	}

	public int getRight() {
		return _right;
	}

	public int getBottom() {
		return _bottom;
	}

	public int getWidth() {
		return _right - _left;
	}

	public int getHeight() {
		return _bottom - _top;
	}

	/**
	 * 指定された点(x, y)が、このRectangleの範囲内にあるかを判定する。
	 * 
	 * @param x 判定する点のX座標
	 * @param y 判定する点のY座標
	 * @return 点(x, y)がこのRectangleの範囲内にある場合、true。
	 */
	public boolean contains(final int x, final int y) {
		return ((_left <= x) && (x <= _right)) && ((_top <= y) && (y <= _bottom));
	}

	/**
	 * 指定されたPointが、このRectangleの範囲内にあるかを判定する。
	 * 
	 * @param pt 判定するPoint
	 * @return ptがこのRectangleの範囲内にある場合、true。
	 */
	public boolean contains(final Point pt) {
		return this.contains(pt.getX(), pt.getY());
	}
}
