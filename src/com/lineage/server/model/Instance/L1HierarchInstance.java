package com.lineage.server.model.Instance;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_AttackPacketNpc;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_Liquor;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.serverpackets.S_NPCPack_Hierarch;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.world.World;

/**
 * 魔法祭司-控制項
 * 
 * @author admin
 */
public class L1HierarchInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static final int SUMMON_TIME = 3600;

	private final boolean _tamed;

	private int _itemObjId;

	/**
	 * 沒有目標的處理
	 */
	@Override
	public boolean noTarget() {
		// 主人隱身或解除則魔法娃娃消失
		if ((_master != null) && _master.isInvisble()) {
			deleteMe();
			return true;
		}
		if (getHierarch() == 2) {
			deleteMe();
		} else if ((_master != null) && (_master.getCurrentHp() > 0)
				&& (_master.getCurrentHp() <= ((_master.getMaxHp() * _master.getHierarch()) / 10))
				&& (getCurrentMp() > 15) && (getLocation().getTileLineDistance(_master.getLocation()) < 7)
				&& (_master.glanceCheck(_master.getX(), _master.getY()) == true) && (getHierarch() == 1)) { // 祭司主動補血
			if (_master instanceof L1PcInstance) {
				final byte chance = (byte) (RandomArrayList.getInt(75) + 1);
				final L1PcInstance player = (L1PcInstance) _master;
				broadcastPacketAll(new S_AttackPacketNpc(this, player, 19));
				broadcastPacketAll(new S_SkillSound(player.getId(), 830));
				player.setCurrentHp(player.getCurrentHp() + (getLawful() + chance));
				player.sendPackets(new S_ServerMessage(77, ""));// 你覺得舒服多了。
				player.sendPackets(new S_HPUpdate(player.getCurrentHp(), player.getMaxHp()));
				setCurrentMp(getCurrentMp() - 15);
				if (player.isInParty()) { // 在隊伍中
					player.getParty().updateMiniHP(player);
				}
				setSleepTime(calcSleepTime(getAtkspeed(), ATTACK_SPEED));
			}
		}
		// else if (_master != null
		// && ((!_master.hasSkillEffect(26) && getNpcTemplate().get_npcId() !=
		// 95401)
		// || (!_master.hasSkillEffect(42) && getNpcTemplate().get_npcId() !=
		// 95401)
		// || (!_master.hasSkillEffect(48) && getNpcTemplate().get_npcId() !=
		// 95401)
		// || (!_master.hasSkillEffect(68) && (getNpcTemplate().get_npcId() ==
		// 95432
		// || getMagicStatus1() == 1 || getMagicStatus2() == 1 ||
		// getMagicStatus3() == 1))
		// // || (!_master.hasSkillEffect(68) && (getMagicStatus1()
		// // == 1 || getMagicStatus2() == 1 || getMagicStatus3()
		// // == 1)) // 聖結界
		// || (!_master.hasSkillEffect(79) && (getMagicStatus1() == 2
		// || getMagicStatus2() == 2 || getMagicStatus3() == 2)) // 靈魂昇華
		// || (!_master.hasSkillEffect(151) && (getMagicStatus1() == 3
		// || getMagicStatus2() == 3 || getMagicStatus3() == 3)) // 大地防護
		// || (!_master.hasSkillEffect(168) && (getMagicStatus1() == 4
		// || getMagicStatus2() == 4 || getMagicStatus3() == 4)) // 鋼鐵防護
		// || (!_master.hasSkillEffect(160) && (getMagicStatus1() == 5
		// || getMagicStatus2() == 5 || getMagicStatus3() == 5)) // 水之防護
		// || (!_master.hasSkillEffect(55) && (getMagicStatus1() == 6
		// || getMagicStatus2() == 6 || getMagicStatus3() == 6)) // 狂暴術
		// || (!_master.hasSkillEffect(114) && (getMagicStatus1() == 7
		// || getMagicStatus2() == 7 || getMagicStatus3() == 7)) // 激勵士氣
		// || (!_master.hasSkillEffect(115) && (getMagicStatus1() == 8
		// || getMagicStatus2() == 8 || getMagicStatus3() == 8)) // 鋼鐵士氣
		// || (!_master.hasSkillEffect(117) && (getMagicStatus1() == 9
		// || getMagicStatus2() == 9 || getMagicStatus3() == 9)) // 衝擊士氣
		// || (!_master.hasSkillEffect(148) && (getMagicStatus1() == 10
		// || getMagicStatus2() == 10 || getMagicStatus3() == 10)) // 火焰武器
		// || (!_master.hasSkillEffect(163) && (getMagicStatus1() == 11
		// || getMagicStatus2() == 11 || getMagicStatus3() == 11)) // 烈炎武器
		// || (!_master.hasSkillEffect(117) && (getMagicStatus1() == 12
		// || getMagicStatus2() == 12 || getMagicStatus3() == 12)) // 烈焰之魂
		// || (!_master.hasSkillEffect(201) && (getMagicStatus1() == 13
		// || getMagicStatus2() == 13 || getMagicStatus3() == 13)) // 鏡像
		// || (!_master.hasSkillEffect(204) && (getMagicStatus1() == 14
		// || getMagicStatus2() == 14 || getMagicStatus3() == 14)) // 幻覺：歐吉
		// || (!_master.hasSkillEffect(209) && (getMagicStatus1() == 15
		// || getMagicStatus2() == 15 || getMagicStatus3() == 15)) // 幻覺：巫妖
		// || (!_master.hasSkillEffect(214) && (getMagicStatus1() == 16
		// || getMagicStatus2() == 16 || getMagicStatus3() == 16)) // 幻覺：鑽石高崙
		// || (!_master.hasSkillEffect(216) && (getMagicStatus1() == 17
		// || getMagicStatus2() == 17 || getMagicStatus3() == 17)) // 洞察
		// )
		// && (_master.getCurrentHp() > 0) && (getCurrentMp() > 20)
		// && (getHierarch() == 1)) {// 加輔助效果
		// if (_master instanceof L1PcInstance) {
		// final L1PcInstance player = (L1PcInstance) _master;
		// if (!player.hasSkillEffect(26)
		// && (getNpcTemplate().get_npcId() == 95402
		// || getNpcTemplate().get_npcId() == 95403 || getNpcTemplate()
		// .get_npcId() == 95432)) {
		// SkillScroll.DoMySkill(player, 26);
		//
		// } else if (!player.hasSkillEffect(42)
		// && (getNpcTemplate().get_npcId() == 95402
		// || getNpcTemplate().get_npcId() == 95403 || getNpcTemplate()
		// .get_npcId() == 95432)) {
		// SkillScroll.DoMySkill(player, 42);
		// } else
		// // 聖結界
		// if (!player.hasSkillEffect(68)
		// && (getNpcTemplate().get_npcId() == 95432 || getMagicStatus1() == 1
		// || getMagicStatus2() == 1 || getMagicStatus3() == 1)) {
		// SkillScroll.DoMySkill(player, 68);
		// player.setSkillEffect(68, 32 * 1000);
		// } else
		// /*
		// * // 聖結界 if (!player.hasSkillEffect(68) && (getMagicStatus1()
		// * == 1 || getMagicStatus2() == 1 || getMagicStatus3() == 1)) {
		// * SkillScroll.DoMySkill(player, 68); player.setSkillEffect(68,
		// * 32 * 1000); } else
		// */
		// // 靈魂昇華
		// if (!player.hasSkillEffect(79)
		// && (getMagicStatus1() == 2 || getMagicStatus2() == 2 ||
		// getMagicStatus3() == 2)) {
		// SkillScroll.DoMySkill(player, 79);
		// player.setSkillEffect(79, 1800 * 1000);
		// } else
		// // 大地防護
		// if (!player.hasSkillEffect(151)
		// && (getMagicStatus1() == 3 || getMagicStatus2() == 3 ||
		// getMagicStatus3() == 3)) {
		// SkillScroll.DoMySkill(player, 151);
		// player.setSkillEffect(151, 960 * 1000);
		// } else
		// // 鋼鐵防護
		// if (!player.hasSkillEffect(168)
		// && (getMagicStatus1() == 4 || getMagicStatus2() == 4 ||
		// getMagicStatus3() == 4)) {
		// SkillScroll.DoMySkill(player, 168);
		// player.setSkillEffect(168, 960 * 1000);
		// } else
		// // 水之防護
		// if (!player.hasSkillEffect(160)
		// && (getMagicStatus1() == 5 || getMagicStatus2() == 5 ||
		// getMagicStatus3() == 5)) {
		// SkillScroll.DoMySkill(player, 160);
		// player.setSkillEffect(160, 960 * 1000);
		// } else
		// // 狂暴術
		// if (!player.hasSkillEffect(55)
		// && (getMagicStatus1() == 6 || getMagicStatus2() == 6 ||
		// getMagicStatus3() == 6)) {
		// SkillScroll.DoMySkill(player, 55);
		// player.setSkillEffect(55, 320 * 1000);
		// } else
		// // 激勵士氣
		// if (!player.hasSkillEffect(114)
		// && (getMagicStatus1() == 7 || getMagicStatus2() == 7 ||
		// getMagicStatus3() == 7)) {
		// SkillScroll.DoMySkill(player, 114);
		// player.setSkillEffect(114, 640 * 1000);
		// } else
		// // 鋼鐵士氣
		// if (!player.hasSkillEffect(115)
		// && (getMagicStatus1() == 8 || getMagicStatus2() == 8 ||
		// getMagicStatus3() == 8)) {
		// SkillScroll.DoMySkill(player, 115);
		// player.setSkillEffect(115, 640 * 1000);
		// } else
		// // 衝擊士氣
		// if (!player.hasSkillEffect(117)
		// && (getMagicStatus1() == 9 || getMagicStatus2() == 9 ||
		// getMagicStatus3() == 9)) {
		// SkillScroll.DoMySkill(player, 117);
		// player.setSkillEffect(117, 640 * 1000);
		// } else
		// // 火焰武器
		// if (!player.hasSkillEffect(148)
		// && (getMagicStatus1() == 10 || getMagicStatus2() == 10 ||
		// getMagicStatus3() == 10)) {
		// SkillScroll.DoMySkill(player, 148);
		// player.setSkillEffect(148, 960 * 1000);
		// } else
		// // 烈炎武器
		// if (!player.hasSkillEffect(163)
		// && (getMagicStatus1() == 11 || getMagicStatus2() == 11 ||
		// getMagicStatus3() == 11)) {
		// SkillScroll.DoMySkill(player, 163);
		// player.setSkillEffect(163, 960 * 1000);
		// } else
		// // 烈焰之魂
		// if (!player.hasSkillEffect(175)
		// && (getMagicStatus1() == 12 || getMagicStatus2() == 12 ||
		// getMagicStatus3() == 12)) {
		// SkillScroll.DoMySkill(player, 175);
		// player.setSkillEffect(175, 64 * 1000);
		// } else
		// // 鏡像
		// if (!player.hasSkillEffect(201)
		// && (getMagicStatus1() == 13 || getMagicStatus2() == 13 ||
		// getMagicStatus3() == 13)) {
		// SkillScroll.DoMySkill(player, 201);
		// player.setSkillEffect(201, 1200 * 1000);
		// } else
		// // 幻覺：歐吉
		// if (!player.hasSkillEffect(204)
		// && (getMagicStatus1() == 14 || getMagicStatus2() == 14 ||
		// getMagicStatus3() == 14)) {
		// SkillScroll.DoMySkill(player, 204);
		// player.setSkillEffect(204, 32 * 1000);
		// } else
		// // 幻覺：巫妖
		// if (!player.hasSkillEffect(209)
		// && (getMagicStatus1() == 15 || getMagicStatus2() == 15 ||
		// getMagicStatus3() == 15)) {
		// SkillScroll.DoMySkill(player, 209);
		// player.setSkillEffect(209, 32 * 1000);
		// } else
		// // 幻覺：鑽石高崙
		// if (!player.hasSkillEffect(214)
		// && (getMagicStatus1() == 16 || getMagicStatus2() == 16 ||
		// getMagicStatus3() == 16)) {
		// SkillScroll.DoMySkill(player, 214);
		// player.setSkillEffect(214, 32 * 1000);
		// } else
		// // 洞察
		// if (!player.hasSkillEffect(216)
		// && (getMagicStatus1() == 17 || getMagicStatus2() == 17 ||
		// getMagicStatus3() == 17)) {
		// SkillScroll.DoMySkill(player, 216);
		// player.setSkillEffect(216, 300 * 1000);
		// } else if (!player.hasSkillEffect(48)
		// && (getNpcTemplate().get_npcId() == 95401
		// || getNpcTemplate().get_npcId() == 95402
		// || getNpcTemplate().get_npcId() == 95403 || getNpcTemplate()
		// .get_npcId() == 95432)) {
		// SkillScroll.DoMySkill(player, 48);
		// player.setSkillEffect(48, 1199 * 1000);
		// }
		// setCurrentMp(getCurrentMp() - 20);
		// broadcastPacketAll(new S_AttackPacketNpc(this, player, 19));
		// setSleepTime(calcSleepTime(getAtkspeed(), ATTACK_SPEED));
		// }
		else if ((_master != null) && (_master.getMapId() == getMapId())) {
			if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
				int dir = targetDirection(_master.getX(), _master.getY());

				for (final L1Object object : World.get().getVisibleObjects(this, 1)) {
					if ((dir >= 0) && (dir <= 7)) {
						final int locx = getX() + HEADING_TABLE_X[dir];
						final int locy = getY() + HEADING_TABLE_Y[dir];
						if (object instanceof L1DollInstance) {
							if ((locx == object.getX()) && (locy == object.getY())) {
								dir += 1;
							}
						}
					}
				}
				if ((dir >= 0) && (dir <= 7)) {
					setDirectionMoveSrc(dir);
					// 發送移動封包
					broadcastPacketAll(new S_MoveCharPacket(this));
				}
			}
		}
		return false;
	}

	/**
	 * 祭司生成
	 * 
	 * @param template
	 * @param master
	 * @param itemObjId
	 */
	public L1HierarchInstance(final L1Npc template, final L1Character master, final int itemObjId,
			final int magicStatus1, final int magicStatus2, final int magicStatus3) {
		super(template);
		setId(IdFactoryNpc.get().nextId());

		set_time(SUMMON_TIME);
		// 給予附魔能力
		setMagicStatus1(magicStatus1);
		setMagicStatus2(magicStatus2);
		setMagicStatus3(magicStatus3);

		setItemObjId(itemObjId);
		setMaster(master);
		setX((master.getX() + RandomArrayList.getInt(5)) - 2);
		setY((master.getY() + RandomArrayList.getInt(5)) - 2);
		setMap(master.getMapId());
		setHeading(5);
		setLightSize(template.getLightSize());

		broadcastPacketAll(new S_SkillHaste(getId(), 1, 0));
		setMoveSpeed(1);
		broadcastPacketAll(new S_SkillBrave(getId(), 1, 0));
		setBraveSpeed(1);
		final S_Liquor Liquor = new S_Liquor(getId(), 0x08);
		broadcastPacketAll(Liquor);

		startAI();

		_tamed = false;

		World.get().storeObject(this);
		World.get().addVisibleObject(this);
		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addPet(this);
	}

	/**
	 * 祭司消去處理
	 */
	@Override
	public synchronized void deleteMe() {
		// 取消判斷
		if (_master.hasSkillEffect(12345)) {
			_master.killSkillEffectTimer(12345);
		}
		if (_destroyed) {
			return;
		}
		if (!_tamed) {
			broadcastPacketAll(new S_SkillSound(getId(), 5936));
		}

		_master.getPetList().remove(getId());

		super.deleteMe();
	}

	@Override
	public void onAction(final L1PcInstance player) {
		final L1AttackPc attack = new L1AttackPc(player, this);
		// 命中判斷
		if (attack.calcHit()) {
			attack.calcDamage();
		}
		// 命中判斷
		attack.calcHit();
		attack.action();
	}

	@Override
	public void onTalkAction(final L1PcInstance player) {
		if (isDead()) {
			return;
		}
		if (_master.equals(player)) {
			String[] htmldata = null;
			String msg0 = "";
			final String msg1 = String.valueOf(player.getHierarch() * 10);
			if (getHierarch() == 1) {
				msg0 = "輔助";
			} else {
				msg0 = "跟隨";
			}
			htmldata = new String[] { getName(), String.valueOf(getCurrentMp()), String.valueOf(getMaxMp()),
					msg0, msg1 };
			player.sendPackets(new S_NPCTalkReturn(getId(), "Hierarch", htmldata));
		}
	}

	@Override
	public void onFinalAction(final L1PcInstance player, final String action) {
	}

	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_NPCPack_Hierarch(this, perceivedFrom));
	}

	@Override
	public void setCurrentMp(final int i) {
		int currentMp = i;

		if (currentMp >= getMaxMp()) {
			currentMp = getMaxMp();
		}

		setCurrentMpDirect(currentMp);

		// 回魔提示
		if (_master instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) _master;
			pc.sendPackets(new S_SkillSound(getId(), 6321));
		}

		// XXX 統一由一個時間軸控制
		// if (getMaxMp() > getCurrentMp()) {
		// startMpRegeneration();
		// }
	}

	public int getItemObjId() {
		return _itemObjId;
	}

	public void setItemObjId(final int i) {
		_itemObjId = i;
	}

	private int _time = 0;

	/**
	 * 設置剩餘使用時間
	 * 
	 * @return
	 */
	public int get_time() {
		return _time;
	}

	/**
	 * 剩餘使用時間
	 * 
	 * @param time
	 */
	public void set_time(final int time) {
		_time = time;
	}

	// 設置第一個附魔能力
	private int _magicStatus1 = 0;

	public int getMagicStatus1() {
		return _magicStatus1;
	}

	public void setMagicStatus1(final int time) {
		_magicStatus1 = time;
	}

	// 設置第二個附魔能力
	private int _magicStatus2 = 0;

	public int getMagicStatus2() {
		return _magicStatus2;
	}

	public void setMagicStatus2(final int time) {
		_magicStatus2 = time;
	}

	// 設置第三個附魔能力
	private int _magicStatus3 = 0;

	public int getMagicStatus3() {
		return _magicStatus3;
	}

	public void setMagicStatus3(final int time) {
		_magicStatus3 = time;
	}
}