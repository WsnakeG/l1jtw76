package com.lineage.server.model.Instance;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_UseArrowSkill;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;

/**
 * 固定攻擊器
 * 
 * @author dexc
 */
public class L1BowInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1BowInstance.class);

	private int _bowid = 66;// 射出武器圖形代號

	private int _time = 1000;// 射出時間間隔

	private int _dmg = 15;// 基礎傷害力

	private int _out_x = 0;// 攻擊點X

	private int _out_y = 0;// 攻擊點Y

	private boolean _start = true;// 執行

	public L1BowInstance(final L1Npc template) {
		super(template);
	}

	public void set_info(final int bowid, final int h, final int dmg, final int time) {
		_bowid = bowid;
		// this._h = h;
		_dmg = dmg;
		_time = time;
	}

	public int get_dmg() {
		return _dmg;
	}

	public void set_dmg(final int dmg) {
		_dmg = dmg;
	}

	public int get_time() {
		return _time;
	}

	public void set_time(final int time) {
		_time = time;
	}

	public int get_bowid() {
		return _bowid;
	}

	public void set_bowid(final int bowid) {
		_bowid = bowid;
	}

	public boolean get_start() {
		return _start;
	}

	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			if ((_out_x == 0) && (_out_y == 0)) {
				set_atkLoc();
			}
			if (!_start) {
				_start = true;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void deleteMe() {
		try {
			_destroyed = true;
			World.get().removeVisibleObject(this);
			World.get().removeObject(this);
			for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
				pc.removeKnownObject(this);
				pc.sendPackets(new S_RemoveObject(this));
			}
			removeAllKnownObjects();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 攻擊目標
	 */
	public void atkTrag() {
		try {
			int out_x = _out_x;
			int out_y = _out_y;
			int tgid = 0;
			final L1Character tg = checkTg();
			if (tg != null) {
				tgid = tg.getId();
				switch (getHeading()) {// 武器射出方向(2:→,6:←,0:↑,4:↓)
				case 0:// 0:↑(Y-)
					out_y = tg.getY();
					break;
				case 2:// 2:→(X+)
					out_x = tg.getX();
					break;
				case 4:// 4:↓(Y+)
					out_y = tg.getY();
					break;
				case 6:// 6:←(X-)
					out_x = tg.getX();
					break;
				}

				if (tg instanceof L1PcInstance) {// PC
					final L1PcInstance trag = (L1PcInstance) tg;
					trag.receiveDamage(null, _dmg, false, true);

				} else if (tg instanceof L1PetInstance) {// 寵物
					final L1PetInstance trag = (L1PetInstance) tg;
					trag.receiveDamage(null, _dmg);

				} else if (tg instanceof L1SummonInstance) {// 召喚獸
					final L1SummonInstance trag = (L1SummonInstance) tg;
					trag.receiveDamage(null, _dmg);

				} else if (tg instanceof L1MonsterInstance) {// MOB
					final L1MonsterInstance trag = (L1MonsterInstance) tg;
					trag.receiveDamage(null, _dmg);
				}
			}

			// 攻擊資訊封包
			broadcastPacketAll(new S_UseArrowSkill(this, tgid, _bowid, out_x, out_y, _dmg));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 周邊PC物件檢查
	 * 
	 * @return
	 */
	public boolean checkPc() {
		try {
			if (World.get().getRecognizePlayer(this).size() <= 0) {
				_start = false;
				// _bowRunnable = null;
				return false;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return true;
	}

	/**
	 * 目標設置
	 * 
	 * @return
	 */
	private L1Character checkTg() {
		final ArrayList<L1Object> tgs = World.get().getVisibleObjects(this, -1);
		for (final L1Object object : tgs) {
			if (object instanceof L1Character) {
				final L1Character cha = (L1Character) object;
				boolean isCheck = false;
				if (cha instanceof L1PcInstance) {// PC
					isCheck = true;
				} else if (cha instanceof L1PetInstance) {// 寵物
					isCheck = true;
				} else if (cha instanceof L1SummonInstance) {// 召喚獸
					isCheck = true;
				} else if (cha instanceof L1MonsterInstance) {// MOB
					isCheck = true;
				}

				if (isCheck) {
					switch (getHeading()) {// 武器射出方向(2:→,6:←,0:↑,4:↓)
					case 0:// 0:↑(Y-)
						if ((object.getX() == getX())
								&& ((object.getY() <= getY()) && (object.getY() >= _out_y))) {
							return cha;
						}
						break;
					case 2:// 2:→(X+)
						if (((object.getX() >= getX()) && (object.getX() <= _out_x))
								&& (object.getY() == getY())) {
							return cha;
						}
						break;
					case 4:// 4:↓(Y+)
						if ((object.getX() == getX())
								&& ((object.getY() >= getY()) && (object.getY() <= _out_y))) {
							return cha;
						}
						break;
					case 6:// 6:←(X-)
						if (((object.getX() <= getX()) && (object.getX() >= _out_x))
								&& (object.getY() == getY())) {
							return cha;
						}
						break;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 設定攻擊點
	 */
	private void set_atkLoc() {
		try {
			// System.out.println("設定攻擊點:"+this.getHeading());
			boolean test = true;
			int x = getX();
			int y = getY();
			switch (getHeading()) {// 武器射出方向(2:→,6:←,0:↑,4:↓)
			case 0:// 0:↑(Y-)
				while (test) {
					final int gab = getMap().getOriginalTile(x, y--);
					if (gab == 0) {
						test = false;
					}
				}
				break;
			case 2:// 2:→(X+)
				while (test) {
					final int gab = getMap().getOriginalTile(x++, y);
					if (gab == 0) {
						test = false;
					}
				}
				break;
			case 4:// 4:↓(Y+)
				while (test) {
					final int gab = getMap().getOriginalTile(x, y++);
					if (gab == 0) {
						test = false;
					}
				}
				break;
			case 6:// 6:←(X-)
				while (test) {
					final int gab = getMap().getOriginalTile(x--, y);
					if (gab == 0) {
						test = false;
					}
				}
				break;
			}
			if (!test) {
				_out_x = x;
				_out_y = y;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
