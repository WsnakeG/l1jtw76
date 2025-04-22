package com.lineage.server.model.Instance.npcai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.map.L1Map;

/**
 * CheckPath
 * 
 * @author loli
 */
public class CheckPath {

	private static final Log _log = LogFactory.getLog(CheckPath.class);

	private Square _goal;// 目標

	private Square _start;// 起點

	private final Square[][] _elements;// 記錄

	private final int _cx;

	private final int _cy;

	private final int _rows;// 高度

	private final int _columns;// 寬度

	private final ArrayList<Square> _opened;// 可通行

	private final HashSet<Square> _closed;// 障礙

	private final ArrayList<int[]> _bestList;// 路徑清單

	public CheckPath(final int tx, final int ty, final int hc, final L1NpcInstance npc) {
		_opened = new ArrayList<Square>();// 可通行
		_closed = new HashSet<Square>();// 障礙
		_bestList = new ArrayList<int[]>();// 路徑清單

		final int x = npc.getX();
		final int y = npc.getY();

		final int x1 = x - hc;
		final int y1 = y - hc;

		final int x2 = x + hc;
		final int y2 = y + hc;

		_rows = x2 - x1;// 高度
		_columns = y2 - y1;// 寬度

		_cx = x1;
		_cy = y1;
		final int mx = x2 - _rows;
		final int my = y2 - _columns;

		_elements = new Square[_rows][_columns];

		createSquares(npc);
		setStartAndGoal(x - mx, y - my, tx - mx, ty - my);

		init();
	}

	/**
	 * 傳回定位質
	 * 
	 * @return
	 */
	public int[] cxy() {
		return new int[] { _cx, _cy };
	}

	/**
	 * 高度
	 * 
	 * @return
	 */
	protected int getRows() {
		return _rows;
	}

	/**
	 * 寬度
	 * 
	 * @return
	 */
	protected int getColumns() {
		return _columns;
	}

	/**
	 * 傳回該點資訊
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	protected Square getSquare(final int x, final int y) {
		return _elements[x][y];
	}

	private void init() {
		generateAdjacenies();
	}

	public void draw() {
		for (int i = 0; i < _rows; i++) {
			for (int j = 0; j < _columns; j++) {
				final Square square = _elements[i][j];
				drawLeft(square);
			}
			System.out.println();
		}
	}

	private void drawLeft(final Square square) {
		String out = null;
		for (final int[] i : _bestList) {
			if ((square.getX() == i[0]) && (square.getY() == i[1])) {
				if (square.isEnd()) {
					out = "PC";
				} else {
					out = "^^";
				}
			}
		}
		if (out == null) {
			if (square.isStart()) {
				out = "NP";
			} else if (square.isEnd()) {
				out = "PC";
			} else if (square.is_open()) {
				out = "  ";
			} else {
				out = "##";
			}
		}
		System.out.print(out);
	}

	/**
	 * 設置起點跟終點
	 */
	private void setStartAndGoal(final int x, final int y, final int tx, final int ty) {
		try {
			_start = _elements[x][y];
			_start.setStart(true);

			_goal = _elements[tx][ty];
			_goal.setEnd(true);

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 建立鄰接點資訊
	 */
	private void generateAdjacenies() {
		try {
			for (int i = 0; i < _rows; i++) {
				for (int j = 0; j < _columns; j++) {
					_elements[i][j].calculateAdjacencies();
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 場景建立
	 */
	private void createSquares(final L1NpcInstance npc) {
		try {
			final L1Map map = npc.getMap();
			for (int i = 0; i < _rows; i++) {// X
				for (int j = 0; j < _columns; j++) {// Y
					final Square square = _elements[i][j] = new Square(i, j, this);
					final int cx = _cx + i;
					final int cy = _cy + j;
					if (map.isPassableDna(cx, cy, 0)) {
						square.set_open(true);// 可通行
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public ArrayList<int[]> findBestPath() {
		try {
			final HashSet<Square> adjacencies = _start.getAdjacencies();// 起點的鄰接點
			for (final Iterator<Square> iter = adjacencies.iterator(); iter.hasNext();) {
				final Square adjacency = iter.next();
				// for (Square adjacency : adjacencies) {
				adjacency.setParent(_start);
				if (!adjacency.isStart()) {// 不是起點
					_opened.add(adjacency);
				}
			}

			while (_opened.size() > 0) {
				final Square best = findBestPassThrough();
				_opened.remove(best);
				_closed.add(best);
				if (best.isEnd()) {
					// 加入可通行路徑
					populateBestList(_goal);
					// draw();

					_opened.clear();// 可通行
					_closed.clear();// 障礙
					return _bestList;

				} else {
					final HashSet<Square> neighbors = best.getAdjacencies();
					for (final Iterator<Square> iter = neighbors.iterator(); iter.hasNext();) {
						final Square neighbor = iter.next();
						// for (Square neighbor : neighbors) {
						if (_opened.contains(neighbor)) {
							final Square tmpSquare = new Square(neighbor.getX(), neighbor.getY(), this);
							tmpSquare.setParent(best);
							if (!tmpSquare.is_open()) {
								_opened.remove(tmpSquare);
								continue;
							}
						}

						if (_closed.contains(neighbor)) {
							final Square tmpSquare = new Square(neighbor.getX(), neighbor.getY(), this);
							tmpSquare.setParent(best);
							if (!tmpSquare.is_open()) {
								_closed.remove(tmpSquare);
								continue;
							}
						}

						neighbor.setParent(best);

						_opened.remove(neighbor);
						_closed.remove(neighbor);
						_opened.add(0, neighbor);
					}
				}
			}

			_opened.clear();// 可通行
			_closed.clear();// 障礙
			_bestList.clear();// 路徑清單

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return _bestList;
	}

	private void populateBestList(final Square square) throws Exception {
		try {
			if (square == null) {
				return;
			}
			_bestList.add(0, new int[] { square.getX(), square.getY() });
			if (square.getParent().isStart() == false) {
				populateBestList(square.getParent());
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private Square findBestPassThrough() {
		try {
			Square best = null;
			for (final Iterator<Square> iter = _opened.iterator(); iter.hasNext();) {
				final Square square = iter.next();
				if ((best == null) || square.is_open()) {
					best = square;
				}
			}
			return best;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
