package com.lineage.server.model.Instance.npcai;

import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 場景記錄
 * 
 * @author loli
 */
public class Square {

	private static final Log _log = LogFactory.getLog(Square.class);

	private final int _x;// X座標

	private final int _y;// Y座標

	private final CheckPath _maze;// 迷宮

	private final HashSet<Square> _adjacencies;// 鄰接點

	private Square _parent;// 場景

	private boolean _start;// 起點

	private boolean _end;// 終點

	private boolean _open = false;// 可否通行

	public Square(final int x, final int y, final CheckPath maze) {
		_x = x;
		_y = y;
		_maze = maze;
		_adjacencies = new HashSet<Square>();// 鄰接點
	}

	/**
	 * X座標
	 * 
	 * @return
	 */
	public int getX() {
		return _x;
	}

	/**
	 * Y座標
	 * 
	 * @return
	 */
	public int getY() {
		return _y;
	}

	/**
	 * 起點
	 * 
	 * @return
	 */
	public boolean isStart() {
		return _start;
	}

	/**
	 * 設置起點
	 * 
	 * @param start
	 */
	public void setStart(final boolean start) {
		_start = start;
	}

	/**
	 * 終點
	 * 
	 * @return
	 */
	public boolean isEnd() {
		return _end;
	}

	/**
	 * 設置終點
	 * 
	 * @param end
	 */
	public void setEnd(final boolean end) {
		_end = end;
	}

	/**
	 * 獲得鄰接
	 * 
	 * @return
	 */
	public HashSet<Square> getAdjacencies() {
		return _adjacencies;
	}

	/**
	 * 主點
	 * 
	 * @return
	 */
	public Square getParent() {
		return _parent;
	}

	/**
	 * 設置主點
	 * 
	 * @param parent
	 */
	public void setParent(final Square parent) {
		_parent = parent;
	}

	/**
	 * 建立點的鄰接點
	 */
	public void calculateAdjacencies() {
		try {
			final int bottom = _x + 1;
			final int right = _y + 1;

			if (bottom < _maze.getRows()) {
				if (_maze.getSquare(bottom, _y).is_open()) {
					_maze.getSquare(bottom, _y).addAdjacency(this);
					addAdjacency(_maze.getSquare(bottom, _y));
				}
			}

			if (right < _maze.getColumns()) {
				if (_maze.getSquare(_x, right).is_open()) {
					_maze.getSquare(_x, right).addAdjacency(this);
					addAdjacency(_maze.getSquare(_x, right));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 加入鄰接點
	 * 
	 * @param square
	 */
	public void addAdjacency(final Square square) {
		_adjacencies.add(square);
	}

	/**
	 * 移出鄰接點
	 * 
	 * @param square
	 */
	public void removeAdjacency(final Square square) {
		_adjacencies.remove(square);
	}

	/**
	 * 可通行
	 * 
	 * @return
	 */
	public boolean is_open() {
		return _open;
	}

	/**
	 * 不可通行
	 * 
	 * @param _open
	 */
	public void set_open(final boolean open) {
		_open = open;
	}
}
