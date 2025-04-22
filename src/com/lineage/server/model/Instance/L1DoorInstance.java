package com.lineage.server.model.Instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_Door;
import com.lineage.server.serverpackets.S_DoorPack;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 對象:門 控制項
 * 
 * @author dexc
 */
public class L1DoorInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;
	public static final int PASS = 0x00;// 可通行
	public static final int NOT_PASS = 0x41;// 不可通行

	private static final Log _log = LogFactory.getLog(L1DoorInstance.class);

	public L1DoorInstance(final L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			if ((getMaxHp() == 0) || (getMaxHp() == 1)) { // 破壊不可能なドアは対象外
				return;
			}

			if ((getCurrentHp() > 0) && !isDead()) {
				final L1AttackMode attack = new L1AttackPc(pc, this);
				if (attack.calcHit()) {
					attack.calcDamage();
				}
				attack.action();
				attack.commit();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			// 副本ID不相等 不相護顯示
			if (perceivedFrom.get_showId() != get_showId()) {
				return;
			}
			perceivedFrom.addKnownObject(this);

			if (getOpenStatus() == ActionCodes.ACTION_Open) {
				setOpenStatus(ActionCodes.ACTION_Open);
				setPassable(L1DoorInstance.PASS);

			} else {
				setOpenStatus(ActionCodes.ACTION_Close);
				setPassable(L1DoorInstance.NOT_PASS);
			}
			perceivedFrom.sendPackets(new S_DoorPack(this));
			sendDoorPacket(perceivedFrom);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void deleteMe() {
		try {
			setPassable(PASS);
			sendDoorPacket(null);

			_destroyed = true;
			if (getInventory() != null) {
				getInventory().clearItems();
			}
			allTargetClear();
			_master = null;
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

	@Override
	public void receiveDamage(final L1Character attacker, final int damage) {
		if ((getMaxHp() == 0) || (getMaxHp() == 1)) { // 破壊不可能なドアは対象外
			return;
		}

		if ((getCurrentHp() > 0) && !isDead()) {
			final int newHp = getCurrentHp() - damage;
			if ((newHp <= 0) && !isDead()) {
				setStatus(ActionCodes.ACTION_DoorDie);
				final Death death = new Death(attacker);
				GeneralThreadPool.get().execute(death);
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				if (((getMaxHp() * 1) / 6) > getCurrentHp()) {
					if (_crackStatus != 5) {
						broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction5));
						setStatus(ActionCodes.ACTION_DoorAction5);
						_crackStatus = 5;
					}
				} else if (((getMaxHp() * 2) / 6) > getCurrentHp()) {
					if (_crackStatus != 4) {
						broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction4));
						setStatus(ActionCodes.ACTION_DoorAction4);
						_crackStatus = 4;
					}
				} else if (((getMaxHp() * 3) / 6) > getCurrentHp()) {
					if (_crackStatus != 3) {
						broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction3));
						setStatus(ActionCodes.ACTION_DoorAction3);
						_crackStatus = 3;
					}
				} else if (((getMaxHp() * 4) / 6) > getCurrentHp()) {
					if (_crackStatus != 2) {
						broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction2));
						setStatus(ActionCodes.ACTION_DoorAction2);
						_crackStatus = 2;
					}
				} else if (((getMaxHp() * 5) / 6) > getCurrentHp()) {
					if (_crackStatus != 1) {
						broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction1));
						setStatus(ActionCodes.ACTION_DoorAction1);
						_crackStatus = 1;
					}
				}
			}
		} else if (!isDead()) { // 念のため
			setStatus(ActionCodes.ACTION_DoorDie);
			final Death death = new Death(attacker);
			GeneralThreadPool.get().execute(death);
		}
	}

	@Override
	public void setCurrentHp(final int i) {
		final int currentHp = Math.min(i, getMaxHp());

		if (getCurrentHp() == currentHp) {
			return;
		}

		setCurrentHpDirect(currentHp);
	}

	/**
	 * 門死亡過程
	 * 
	 * @author daien
	 */
	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(final L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			setCurrentHpDirect(0);
			setDead(true);
			setStatus(ActionCodes.ACTION_DoorDie);

			broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorDie));
			setPassable(PASS);
			setOpenStatus(ActionCodes.ACTION_Open);
			sendDoorPacket(null);
			set_open();
		}
	}

	private void sendDoorPacket(final L1PcInstance perceivedFrom) {
		final int entranceX = getEntranceX();
		final int entranceY = getEntranceY();
		final int leftEdgeLocation = getLeftEdgeLocation();
		final int rightEdgeLocation = getRightEdgeLocation();

		final int size = rightEdgeLocation - leftEdgeLocation;
		if (size == 0) { // 1マス分の幅のドア
			sendPacket(perceivedFrom, entranceX, entranceY);

		} else { // 2マス分以上の幅があるドア
			if (getDirection() == 0) { // ／向き
				if (getGfxId() == 12164) {
					for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
						sendPacket(perceivedFrom, x, entranceY - 1);
					}
				} else {
					for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
						sendPacket(perceivedFrom, x, entranceY);
					}
				}

			} else { // ＼向き
				for (int y = leftEdgeLocation; y <= rightEdgeLocation; y++) {
					sendPacket(perceivedFrom, entranceX, y);
				}
			}
		}
	}

	private void sendPacket(final L1PcInstance pc, final int x, final int y) {
		final S_Door packet = new S_Door(x, y, getDirection(), getPassable());
		if (pc != null) {
			pc.sendPackets(packet);
		} else {
			broadcastPacketAll(packet);
		}
	}

	private void set_open() {
		final int entranceX = getEntranceX();
		final int entranceY = getEntranceY();
		final int leftEdgeLocation = getLeftEdgeLocation();
		final int rightEdgeLocation = getRightEdgeLocation();
		if (getDirection() == 0) { // ／方向
			getMap().setPassable(entranceX, entranceY, true, 0);
			for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
				getMap().setPassable(x, entranceY, true, 0);
			}

		} else { // ＼方向
			getMap().setPassable(entranceX, entranceY, true, 1);
			for (int y = leftEdgeLocation; y <= rightEdgeLocation; y++) {
				getMap().setPassable(entranceX, y, true, 1);
			}
		}
	}

	/**
	 * 開門
	 */
	public void open() {
		if (isDead()) {
			return;
		}
		if (getOpenStatus() == ActionCodes.ACTION_Close) {
			setOpenStatus(ActionCodes.ACTION_Open);
			setPassable(L1DoorInstance.PASS);
			broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Open));
			sendDoorPacket(null);
			set_open();
		}
	}

	private void set_close() {
		final int entranceX = getEntranceX();
		final int entranceY = getEntranceY();
		final int leftEdgeLocation = getLeftEdgeLocation();
		final int rightEdgeLocation = getRightEdgeLocation();
		if (getDirection() == 0) { // ／方向
			getMap().setPassable(entranceX, entranceY, false, 0);
			for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
				getMap().setPassable(x, entranceY, false, 0);
			}

		} else { // ＼方向
			getMap().setPassable(entranceX, entranceY, false, 1);
			for (int y = leftEdgeLocation; y <= rightEdgeLocation; y++) {
				getMap().setPassable(entranceX, y, false, 1);
			}
		}
	}

	/**
	 * 關門
	 */
	public void close() {
		if (isDead()) {
			return;
		}
		if (getOpenStatus() == ActionCodes.ACTION_Open) {
			setOpenStatus(ActionCodes.ACTION_Close);
			setPassable(L1DoorInstance.NOT_PASS);
			broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Close));
			sendDoorPacket(null);
			set_close();
		}
	}

	public void repairGate() {
		if (getMaxHp() > 1) {
			setDead(false);
			setCurrentHp(getMaxHp());
			setStatus(0);
			setCrackStatus(0);
			setOpenStatus(ActionCodes.ACTION_Open);
			close();
		}
	}

	private int _doorId = 0;// 門的編號

	/**
	 * 門的編號<BR>
	 * 門編號 51~55 由賭場使用<BR>
	 * 門編號 10000~10003 由任務不死族的叛徒 (法師30級以上官方任務)使用<BR>
	 * 10004 蛇女房間<BR>
	 * 10005~10007 安塔瑞斯洞穴<BR>
	 * 10008~10013 法利昂洞穴<BR>
	 * 10014~10035 哈汀副本<BR>
	 * 
	 * @return
	 */
	public int getDoorId() {
		return _doorId;
	}

	/**
	 * 門的編號
	 * 
	 * @param i
	 */
	public void setDoorId(final int i) {
		_doorId = i;
	}

	private int _direction = 0; // 門的定位

	/**
	 * 門的定位
	 * 
	 * @return
	 */
	public int getDirection() {
		return _direction;
	}

	/**
	 * 門的定位
	 * 
	 * @param i 0:／ 1:＼
	 */
	public void setDirection(final int i) {
		if ((i == 0) || (i == 1)) {
			_direction = i;
		}
	}

	public int getEntranceX() {
		int entranceX = 0;
		if (getDirection() == 0) { // ／向き
			entranceX = getX();
		} else { // ＼向き
			entranceX = getX() - 1;
		}
		return entranceX;
	}

	public int getEntranceY() {
		int entranceY = 0;
		if (getDirection() == 0) { // ／向き
			entranceY = getY() + 1;
		} else { // ＼向き
			entranceY = getY();
		}
		return entranceY;
	}

	private int _leftEdgeLocation = 0; // ドアの左端の座標(ドアの向きからX軸orY軸を決定する)

	/**
	 * 門的左端
	 * 
	 * @return
	 */
	public int getLeftEdgeLocation() {
		return _leftEdgeLocation;
	}

	/**
	 * 門的左端
	 * 
	 * @param i
	 */
	public void setLeftEdgeLocation(final int i) {
		_leftEdgeLocation = i;
	}

	private int _rightEdgeLocation = 0; // ドアの右端の座標(ドアの向きからX軸orY軸を決定する)

	/**
	 * 門的右端
	 * 
	 * @return
	 */
	public int getRightEdgeLocation() {
		return _rightEdgeLocation;
	}

	/**
	 * 門的右端
	 * 
	 * @param i
	 */
	public void setRightEdgeLocation(final int i) {
		_rightEdgeLocation = i;
	}

	private int _openStatus = ActionCodes.ACTION_Close;

	public int getOpenStatus() {
		return _openStatus;
	}

	private void setOpenStatus(final int i) {
		if ((i == ActionCodes.ACTION_Open) || (i == ActionCodes.ACTION_Close)) {
			_openStatus = i;
		}
	}

	private int _passable = NOT_PASS;// 是否可通行

	/**
	 * 是否可通行
	 * 
	 * @return 0:可通行(PASS) 1:不可通行(NOT_PASS)
	 */
	public int getPassable() {
		return _passable;
	}

	/**
	 * 是否可通行
	 * 
	 * @param i 0:可通行(PASS) 1:不可通行(NOT_PASS)
	 */
	private void setPassable(final int i) {
		if ((i == PASS) || (i == NOT_PASS)) {
			_passable = i;
		}
	}

	private int _keeperId = 0;// 管理員編號

	/**
	 * 管理員編號
	 * 
	 * @return
	 */
	public int getKeeperId() {
		return _keeperId;
	}

	/**
	 * 管理員編號
	 * 
	 * @param i
	 */
	public void setKeeperId(final int i) {
		_keeperId = i;
	}

	private int _crackStatus;

	private void setCrackStatus(final int i) {
		_crackStatus = i;
	}

	/**
	 * 打開全部關閉的門 核心啟動時調用一次
	 */
	public static void openDoor() {
		final L1DoorInstance[] allDoor = DoorSpawnTable.get().getDoorList();
		// 不包含元素
		if (allDoor.length <= 0) {
			return;
		}

		for (final L1DoorInstance door : allDoor) {
			switch (door.getDoorId()) {
			case 5001:// 水晶洞穴 1樓
			case 5002:// 水晶洞穴 2樓
			case 5003:// 水晶洞穴 2樓
			case 5004:// 水晶洞穴 2樓
			case 5005:// 水晶洞穴 2樓
			case 5006:// 水晶洞穴 2樓
			case 5007:// 水晶洞穴 3樓
			case 5008:// 水晶洞穴 3樓
			case 5009:// 水晶洞穴 3樓
			case 5010:// 水晶洞穴 3樓
			case 6006:// 黃金鑰匙
			case 6007:// 銀鑰匙
			case 10000:// 不死族的鑰匙
			case 10001:// 僵屍鑰匙
			case 10002:// 骷髏鑰匙
			case 10003:// 機關門(說明:不死族的叛徒 (法師30級以上官方任務))
			case 10004:// 蛇女房間鑰匙
			case 10005:// 安塔瑞斯洞穴
			case 10006:// 安塔瑞斯洞穴
			case 10007:// 安塔瑞斯洞穴
			case 10008:// 法利昂洞穴
			case 10009:// 法利昂洞穴
			case 10010:// 法利昂洞穴
			case 10011:// 法利昂洞穴
			case 10012:// 法利昂洞穴
			case 10013:// 法利昂洞穴
			case 10019:// 魔法師．哈汀(故事) 禁開
			case 10036:// 魔法師．哈汀(故事) 禁開
			case 10015:// 魔法師．哈汀(故事)// NO 1
			case 10016:// 魔法師．哈汀(故事)// NO 2
			case 10017:// 魔法師．哈汀(故事)// NO 2
			case 10020:// 魔法師．哈汀(故事)// NO 4
				door.set_close();
				break;

			default:
				// HP大於1
				if (door.getMaxHp() > 1) {
					door.set_close();
					continue;
				}
				// 具有守門員
				if (door.getKeeperId() != 0) {
					door.set_close();
					continue;
				}
				// 開門
				door.open();
				break;
			}
		}
	}

	/**
	 * 控制賭場的門
	 * 
	 * @param isOpen true開 false關
	 */
	public static void openGam(final boolean isOpen) {
		final L1DoorInstance[] allDoor = DoorSpawnTable.get().getDoorList();
		// 不包含元素
		if (allDoor.length <= 0) {
			return;
		}

		for (final L1DoorInstance door : allDoor) {
			switch (door.getDoorId()) {
			case 51:
			case 52:
			case 53:
			case 54:
			case 55:
				if (isOpen) {
					door.open();

				} else {
					door.close();
				}
				break;
			}
		}
	}
}
