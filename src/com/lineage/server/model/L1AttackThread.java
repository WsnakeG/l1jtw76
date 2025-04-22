package com.lineage.server.model;

import java.math.BigDecimal;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

public class L1AttackThread implements Runnable {

	private final LinkedBlockingQueue<L1Object> _attack;
	private L1PcInstance _pc;
	private L1Object _target = null;

	private BigDecimal a;
	private final BigDecimal b = new BigDecimal("4");
	private final BigDecimal c = new BigDecimal("2");
	private final BigDecimal d = new BigDecimal("1.15");
	private final BigDecimal e = new BigDecimal("1.1");

	public L1AttackThread(final L1PcInstance pc) {
		_pc = pc;
		_attack = new LinkedBlockingQueue<L1Object>();
		GeneralThreadPool.get().execute(this);
	}

	@Override
	public void run() {
		while (_pc.getOnlineStatus() == 1) {
			try {
				final L1Object object = _attack.poll(3000, TimeUnit.MILLISECONDS);
				if (object != null) {
					while (_pc.getAttackTargetId() != 0) {
						if (_pc.hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
							Thread.sleep((getAttackSpeed() / 100) * 50);
						}
						if (!checkTarget(object) || !checkPc()) {
							_pc.setAttackTargetId(0);
							_target = null;
							break;
						}
						object.onAction(_pc);
						Thread.sleep(getAttackSpeed());
					}
				}

			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		_pc = null;
		_target = null;
		_attack.clear();
	}

	private int getAttackSpeed() {
		int speed = 0;
		byte speedcount = 0;

		if (_pc.getBraveSpeed() == 1) {
			++speedcount;
		}
		if (_pc.isHaste() || (_pc.getHasteItemEquipped() != 0)) {
			++speedcount;
		}

		double framecount = 0;
		int framerate = 24;

		if (_pc.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
			framecount = getFrameCount();
			if (speedcount == 0) {
				a = new BigDecimal(framecount);
				framecount += a.divide(b, 2, BigDecimal.ROUND_UP).doubleValue()
						+ a.divide(c, 2, BigDecimal.ROUND_UP).doubleValue();
			} else if (speedcount == 1) {
				a = new BigDecimal(framecount);
				framecount += a.divide(b, 2, BigDecimal.ROUND_UP).doubleValue() + 1.5;
			}
			framecount -= 0.25;
		} else {
			final int[] interval = SprTable.get().getFrame(_pc.getTempCharGfx(), _pc.getCurrentWeapon() + 1);
			framecount = interval[0];
			framerate = interval[1];
			if (speedcount == 0) {
				a = new BigDecimal(framecount);
				framecount += a.divide(b, 2, BigDecimal.ROUND_UP).doubleValue()
						+ a.divide(c, 2, BigDecimal.ROUND_UP).doubleValue();
			} else if (speedcount == 1) {
				a = new BigDecimal(framecount);
				framecount += a.divide(b, 2, BigDecimal.ROUND_UP).doubleValue() + 1;
			}
		}
		speed = (int) (framecount * framerate);

		if (_pc.isElfBrave()) {
			a = new BigDecimal(speed);
			speed = (int) a.divide(d, 2, BigDecimal.ROUND_UP).doubleValue();
		}
		if (_pc.isBraveX()) {
			a = new BigDecimal(speed);
			speed = (int) a.divide(e, 2, BigDecimal.ROUND_UP).doubleValue();
		}
		a = null;
		return speed;
	}

	private double getFrameCount() {
		double frame = 0;
		switch (_pc.getCurrentWeapon()) {
		case 0:
		case 46:
			frame = 19;
			break;
		case 4:
		case 40:
		case 54:
		case 58:
		case 88:
			frame = 20;
			break;
		case 24:
			frame = 21;
			break;
		case 11:
		case 50:
			frame = 22;
			break;
		case 20:
		case 62:
			frame = 24;
			break;
		}
		if (_pc.getLevel() >= 30) {
			double poly = 0;
			if (_pc.getLevel() <= 44) {
				poly = 1;
			} else if ((_pc.getLevel() >= 45) && (_pc.getLevel() <= 49)) {
				poly = 2;
			} else if (_pc.getLevel() == 50) {
				poly = 3;
			} else if (_pc.getLevel() == 51) {
				poly = 4;
			} else if ((_pc.getLevel() >= 52) && (_pc.getLevel() <= 54)) {
				poly = 5;
			} else if ((_pc.getLevel() >= 55) && (_pc.getLevel() <= 74)) {
				poly = 6;
			} else if ((_pc.getLevel() >= 75) && (_pc.getLevel() <= 79)) {
				poly = 7;
			} else if (_pc.getLevel() >= 80) {
				poly = 8;
			}
			frame -= poly;
		}
		return frame;
	}

	private boolean checkTarget(final L1Object target) {
		if (target instanceof L1Character) {
			if (((L1Character) target).isDead()) {
				return false;
			}
			if ((target.getMapId() != _pc.getMapId())
					|| ((int) _pc.getLocation().getLineDistance(target.getLocation()) > _pc.getRange())) {
				return false;
			}
			if (target instanceof L1PcInstance) {
				if (((L1PcInstance) target).isTeleport()) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkPc() {
		if (_pc == null) {
			return false;
		}
		if (_pc.isParalyzedX()) {
			return false;
		}
		if (_pc.isInvisble() || _pc.isInvisDelay()) {
			return false;
		}
		if (_pc.isDead() || (_pc.getAttackTargetId() == 0) || (_pc.getOnlineStatus() == 0)) {
			return false;
		}
		if (_pc.getNetConnection().getActiveChar() == null) {
			return false;
		}
		return true;
	}

	public void putObject() {
		_target = World.get().findObject(_pc.getAttackTargetId());
		if (_target == null) {
			_pc.setAttackTargetId(0);
			return;
		}
		_attack.offer(_target);
	}
}