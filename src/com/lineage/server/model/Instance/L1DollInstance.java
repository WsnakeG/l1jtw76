package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.GREATER_HASTE;
import static com.lineage.server.model.skill.L1SkillId.HASTE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HASTE;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.doll.Doll_DmgDownR;
import com.lineage.server.model.doll.Doll_DmgR;
import com.lineage.server.model.doll.Doll_Skill;
import com.lineage.server.model.doll.L1DollExecutor;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.serverpackets.S_NPCPack_Doll;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;

/**
 * 對象:魔法娃娃 控制項
 * 
 * @author DaiEn
 */
public class L1DollInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1DollInstance.class);

	private static Random _random = new Random();

	private int _itemObjId;

	private boolean _power_doll = false;// 該娃娃具備輔助技能效果

	private L1Doll _type;

	/**
	 * 對象:魔法娃娃
	 * 
	 * @param template
	 * @param master
	 * @param dollType
	 * @param itemObjId
	 */
	public L1DollInstance(final L1Npc template, final L1PcInstance master, final int itemObjId,
			final L1Doll type, final boolean power_doll) {
		super(template);
		try {
			_power_doll = power_doll;
			setId(IdFactoryNpc.get().nextId());
			// 設置副本編號
			set_showId(master.get_showId());

			setItemObjId(itemObjId);

			_type = type;
			setGfxId(type.get_gfxid());
			setTempCharGfx(type.get_gfxid());
			setNameId(type.get_nameid());
			set_time(type.get_time());

			setMaster(master);
			setX((master.getX() + _random.nextInt(5)) - 2);
			setY((master.getY() + _random.nextInt(5)) - 2);
			setMap(master.getMapId());
			setHeading(5);
			setLightSize(template.getLightSize());

			World.get().storeObject(this);
			World.get().addVisibleObject(this);
			for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
				onPerceive(pc);
			}
			if (_power_doll) {
				master.add_power_doll(this);
			} else {
				master.addDoll(this);
			}

			L1PcInstance masterpc = null;
			if (_master instanceof L1PcInstance) {
				masterpc = (L1PcInstance) _master;
				// 設置能力
				if (!_type.getPowerList().isEmpty()) {
					for (final L1DollExecutor p : _type.getPowerList()) {
						if (p instanceof Doll_Skill) {
							final Doll_Skill vv = (Doll_Skill) p;
							set_skill(vv.get_int()[0], vv.get_int()[1]);

						} else {
							p.setDoll(masterpc);
						}
					}
				}
				master.sendPackets(new S_PacketBox(S_PacketBox.DOLL, type.get_time()));
			}

			// 娃娃登場
			broadcastPacketX10(new S_SkillSound(getId(), 5935));
			set_olX(getX());
			set_olY(getY());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 對象:魔法娃娃(DE)
	 * 
	 * @param template
	 * @param master
	 * @param type
	 */
	public L1DollInstance(final L1Npc template, final L1NpcInstance master, final L1Doll type) {
		super(template);
		try {
			setId(IdFactoryNpc.get().nextId());
			// 設置副本編號
			set_showId(master.get_showId());

			_type = type;
			setGfxId(type.get_gfxid());
			setTempCharGfx(type.get_gfxid());
			setNameId(type.get_nameid());
			set_time(type.get_time());

			set_time(1800);

			setMaster(master);
			setX((master.getX() + _random.nextInt(5)) - 2);
			setY((master.getY() + _random.nextInt(5)) - 2);
			setMap(master.getMapId());
			setHeading(5);
			setLightSize(template.getLightSize());

			World.get().storeObject(this);
			World.get().addVisibleObject(this);
			for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
				onPerceive(pc);
			}
			master.addDoll(this);

			// 娃娃登場
			broadcastPacketX10(new S_SkillSound(getId(), 5935));
			set_olX(getX());
			set_olY(getY());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 娃娃收回
	 */
	public void deleteDoll() {
		try {
			// 娃娃收回
			broadcastPacketX10(new S_SkillSound(getId(), 5936));

			L1PcInstance masterpc = null;
			if (_master instanceof L1PcInstance) {
				masterpc = (L1PcInstance) _master;
				// 移除能力
				if (!_type.getPowerList().isEmpty()) {
					for (final L1DollExecutor p : _type.getPowerList()) {
						p.removeDoll(masterpc);
					}
				}
				masterpc.sendPackets(new S_PacketBox(S_PacketBox.DOLL, 0));
			}

			if (_power_doll) {
				_master.remove_power_doll();
			} else {
				_master.removeDoll(this);
			}

			deleteMe();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * TODO 接觸資訊
	 */
	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			// 副本ID不相等 不相護顯示
			if (perceivedFrom.get_showId() != get_showId()) {
				return;
			}
			perceivedFrom.addKnownObject(this);
			perceivedFrom.sendPackets(new S_NPCPack_Doll(this, perceivedFrom));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 跟隨主人變更移動/速度狀態
	 */
	public void setNpcMoveSpeed() {
		try {
			if (!ConfigOther.WAR_DOLL) {
				for (int castle_id = 1; castle_id < 8; castle_id++) {
					if (ServerWarExecutor.get().isNowWar(castle_id)) { // 攻城戰期間內
						if (L1CastleLocation.checkInWarArea(castle_id, this)) {
							deleteDoll();
							return;
						}
					}
				}
			}

			// 主人隱身或解除則魔法娃娃消失
			if ((_master != null) && _master.isInvisble()) {
				deleteDoll();
				return;
			}
			// 主人隱身或解除則魔法娃娃消失
			if (_master.isDead()) {
				deleteDoll();
				return;
			}
			if (_master.getMoveSpeed() != _srcMoveSpeed) {
				set_srcMoveSpeed(_master.getMoveSpeed());
				setMoveSpeed(_srcMoveSpeed);
			}

			if (_master.getBraveSpeed() != _srcBraveSpeed) {
				set_srcBraveSpeed(_master.getBraveSpeed());
				setBraveSpeed(_srcBraveSpeed);
			}

			if ((_master != null) && (_master.getMapId() == getMapId())) {
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

			} else {
				deleteDoll();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _srcMoveSpeed; // 移動加速狀態(綠水)
	private int _srcBraveSpeed; // 攻擊加速狀態(勇水)

	/**
	 * 移動加速狀態(綠水)
	 * 
	 * @param srcMoveSpeed
	 */
	private void set_srcMoveSpeed(final int srcMoveSpeed) {
		try {
			_srcMoveSpeed = srcMoveSpeed;
			broadcastPacketAll(new S_SkillHaste(getId(), _srcMoveSpeed, 0));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 攻擊加速狀態(勇水)
	 * 
	 * @param srcBraveSpeed
	 */
	private void set_srcBraveSpeed(final int srcBraveSpeed) {
		try {
			_srcBraveSpeed = srcBraveSpeed;
			broadcastPacketAll(new S_SkillBrave(getId(), _srcBraveSpeed, 0));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
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

	/**
	 * 魔法娃娃特殊技能
	 * 
	 * @param target
	 * @param dmg
	 */
	public void startDollSkill(final L1Character target, final double dmg) {
		try {
			if (_skillid != -1) {
				if (_random.nextInt(1000) <= _r) {
					final L1Magic magic = new L1Magic(_master, target);
					final int magic_dmg = magic.calcMagicDamage(_skillid);
					magic.commit(magic_dmg, 0);
					final L1Skills skill = SkillsTable.get().getTemplate(_skillid);
					final int castgfx = skill.getCastGfx();

					target.broadcastPacketX10(new S_SkillSound(target.getId(), castgfx));
					if (target instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) target;
						pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
					}
					switch (_skillid) {
					case 4:// 光箭 1100
					case 6:// 冰箭 1100
					case 7:// 風刃 1100
					case 10:// 寒冷戰慄 1100
					case 15:// 火箭 1300
					case 16:// 地獄之牙 1300
					case 17:// 極光雷電 1500
					case 18:// 起死回生術 1500
					case 22:// 寒冰氣息 1300
					case 25:// 燃燒的火球 1600
					case 28:// 吸血鬼之吻 1250
					case 30:// 巖牢 1600
					case 34:// 極道落雷 1250
					case 38:// 冰錐 1100
					case 45:// 地裂術 1100
					case 46:// 烈炎術 1650
					case 50:// 冰矛圍籬 1600
					case 58:// 火牢 1100
					case 65:// 雷霆風暴 3000
					case 74:// 流星雨 4000
					case 77:// 究極光裂術 6000
					case 108:// 會心一擊 1800
					case 132:// 三重矢 1250
					case 184:// 岩漿噴吐 3300
					case 187:// 屠宰者 3250
					case 192:// 奪命之雷 5150
					case 194:// 寒冰噴吐
					case 202:// 混亂 4000
					case 203:// 暴擊 3100
					case 207:// 心靈破壞 4050
					case 208:// 骷髏毀壞 3100
					case 213:// 武器破壞者 3100
						broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Aggress));
						break;

					case 29:// 緩速術 1600
						switch (target.getMoveSpeed()) {
						case 0:
							if (target instanceof L1PcInstance) {
								final L1PcInstance pc = (L1PcInstance) target;
								pc.sendPackets(new S_SkillHaste(pc.getId(), 2, skill.getBuffDuration()));
							}
							target.broadcastPacketAll(
									new S_SkillHaste(target.getId(), 2, skill.getBuffDuration()));
							target.setMoveSpeed(2);
							target.setSkillEffect(L1SkillId.SLOW, skill.getBuffDuration() * 1000);
							break;

						case 1:
							int skillNum = 0;
							if (target.hasSkillEffect(HASTE)) {
								skillNum = HASTE;

							} else if (target.hasSkillEffect(GREATER_HASTE)) {
								skillNum = GREATER_HASTE;

							} else if (target.hasSkillEffect(STATUS_HASTE)) {
								skillNum = STATUS_HASTE;
							}

							if (skillNum != 0) {
								target.removeSkillEffect(skillNum);
								target.setMoveSpeed(0);
							}
							break;
						}
						break;

					case 1:// 初級治癒術 1300
					case 8:// 神聖武器 1500
					case 9:// 解毒術 1250
					case 11:// 毒咒 1650
					case 19:// 中級治癒術 1250
					case 20:// 闇盲咒術 1650
					case 23:// 能量感測 1100
					case 26:// 通暢氣脈術 1250
					case 27:// 壞物術 4200
					case 33:// 木乃伊的詛咒 6200
					case 35:// 高級治癒術 1500
					case 36:// 迷魅術 1250
					case 37:// 聖潔之光 1250
					case 39:// 魔力奪取 1650
					case 40:// 黑闇之影 1650
					case 41:// 造屍術
					case 42:// 體魄強健術 1650
					case 43:// 加速術 900
					case 44:// 魔法相消術 1450
					case 47:// 弱化術 1650
					case 48:// 祝福魔法武器 1250
					case 55:// 狂暴術 1250
					case 56:// 疾病術 1650
					case 57:// 全部治癒術 1650
					case 61:// 返生術
					case 63:// 治癒能量風暴 1100
					case 64:// 魔法封印 1650
					case 66:// 沉睡之霧 2750
					case 67:// 變形術
					case 68:// 聖結界 2750
					case 71:// 藥水霜化術 1650
					case 75:// 終極返生術
					case 76:// 集體緩速術 4200
					case 79:// 靈魂昇華 1250
					case 87:// 衝擊之暈 9500
					case 103:// 暗黑盲咒 2700
					case 113:// 精準目標 1600
					case 116:// 呼喚盟友
					case 118:// 援護盟友
					case 133:// 弱化屬性 1250
					case 145:// 釋放元素 1250
					case 148:// 火焰武器 1450
					case 149:// 風之神射 1450
					case 151:// 大地防護 1450
					case 152:// 地面障礙 1250
					case 153:// 魔法消除 1250
					case 157:// 大地屏障 1250
					case 158:// 生命之泉 1450
					case 160:// 水之防護 1450
					case 165:// 生命呼喚 1450
					case 167:// 風之枷鎖 1250
					case 170:// 水之元氣 1450
					case 173:// 污濁之水 2200
					case 174:// 精準射擊 4250
					case 188:// 恐懼無助 4250
					case 193:// 驚悚死神 5150
					case 204:// 幻覺：歐吉 3150
					case 206:// 專注 2050
					case 209:// 幻覺：巫妖 3100
					case 211:// 耐力 2050
					case 212:// 幻想 4050
					case 214:// 幻覺：鑽石高崙 3150
					case 216:// 洞察 2200
					case 217:// 恐慌 5150
					case 218:// 疼痛的歡愉 4050
					case 219:// 幻覺：化身 6100
						// type = Speed_Ch.ACT_TYPE.SPELL_DIR;
						break;

					case 2:// 日光術 1300
					case 3:// 保護罩 1300
					case 5:// 指定傳送 1100
					case 12:// 擬似魔法武器 1500
					case 13:// 無所遁形術 1650
					case 14:// 負重強化 1250
					case 21:// 鎧甲護持 1500
					case 31:// 魔法屏障 1250
					case 32:// 冥想術 1250
					case 49:// 體力回復術 1650
					case 51:// 召喚術 2200
					case 52:// 神聖疾走 1250
					case 53:// 龍捲風 2500
					case 54:// 強力加速術
					case 59:// 冰雪暴 2600
					case 60:// 隱身術 1250
					case 62:// 震裂術 2600
					case 69:// 集體傳送術 1200
					case 70:// 火風暴 3000
					case 72:// 強力無所遁形術 1650
					case 73:// 創造魔法武器 1500
					case 78:// 絕對屏障 13000
					case 80:// 冰雪颶風 4000
					case 88:// 增幅防禦 4500
					case 89:// 尖刺盔甲 6450
					case 90:// 堅固防護 4450
					case 91:// 反擊屏障 4450
					case 97:// 暗隱術 11700
					case 98:// 附加劇毒 2750
					case 99:// 影之防護 2750
					case 100:// 提煉魔石 2000
					case 101:// 行走加速 1750
					case 102:// 燃燒鬥志 4700
					case 104:// 毒性抵抗 1750
					case 105:// 雙重破壞 1750
					case 106:// 暗影閃避 1750
					case 107:// 暗影之牙 1700
					case 109:// 力量提升 1750
					case 110:// 敏捷提升 1750
					case 111:// 閃避提升 1750
					case 114:// 激勵士氣 1600
					case 115:// 鋼鐵士氣 1500
					case 117:// 衝擊士氣 1500
					case 129:// 魔法防禦 1300
					case 130:// 心靈轉換 1450
					case 131:// 世界樹的呼喚
					case 134:// 鏡反射 350
					case 137:// 淨化精神 1450
					case 138:// 屬性防禦 1450
					case 146:// 魂體轉換 2500
					case 147:// 單屬性防禦 1450
					case 150:// 風之疾走 1450
					case 154:// 召喚屬性精靈 1400
					case 155:// 烈炎氣息 1400
					case 156:// 暴風之眼 1400
					case 159:// 大地的祝福 1450
					case 161:// 封印禁地 10300
					case 162:// 召喚強力屬性精靈 1450
					case 163:// 烈炎武器 1450
					case 164:// 生命的祝福 1650
					case 166:// 暴風神射 1450
					case 168:// 鋼鐵防護 1450
					case 169:// 體能激發 1450
					case 171:// 屬性之火 1450
					case 172:// 暴風疾走 1450
					case 175:// 烈焰之魂
					case 176:// 能量激發
					case 181:// 龍之護鎧 1300
					case 182:// 燃燒擊砍 3300
					case 183:// 護衛毀滅 2200
					case 185:// 覺醒：安塔瑞斯 1250
					case 186:// 血之渴望
					case 189:// 衝擊之膚
					case 190:// 覺醒：法利昂 1250
					case 191:// 致命身軀 2350
					case 195:// 覺醒：巴拉卡斯 1250
					case 201:// 鏡像 1250
					case 205:// 立方：燃燒 6200
					case 210:// 立方：地裂 6250
					case 215:// 立方：衝擊 6200
					case 220:// 立方：和諧 6100
						// type = Speed_Ch.ACT_TYPE.SPELL_NODIR;
						break;
					}
					/*
					 * final L1SkillUse skillUse = new L1SkillUse();
					 * skillUse.handleCommands( null, _skillid, target.getId(),
					 * target.getX(), target.getX(), 0, L1SkillUse.TYPE_GMBUFF,
					 * this );
					 */
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _skillid = -1;// 值1

	private int _r = -1;// 值2

	public void set_skill(final int int1, final int int2) {
		_skillid = int1;// 值1
		if (_skillid != -1) {
			setLevel(_master.getLevel());
			setInt(_master.getInt());
		}
		_r = int2;// 值2
	}

	private int _olX = 0;// 上次站的位置X

	public void set_olX(final int x) {
		_olX = x;
	}

	public int get_olX() {
		return _olX;
	}

	private int _olY = 0;// 上次站的位置Y

	public void set_olY(final int y) {
		_olY = y;
	}

	public int get_olY() {
		return _olY;
	}

	// 輔助類型娃娃
	public void startDollSkill() {
		if (!_type.getPowerList().isEmpty()) {
			// int i = 0;
			if (_master instanceof L1PcInstance) {
				final L1PcInstance masterpc = (L1PcInstance) _master;
				for (final L1DollExecutor p : _type.getPowerList()) {
					if (p.is_reset()) {
						p.setDoll(masterpc);
					}
				}
			}
		}
	}

	/**
	 * 動作展示
	 * 
	 * @param i 1:機率增加攻擊力 2:機率增加傷害減免
	 */
	public void show_action(final int i) {
		if (!_type.getPowerList().isEmpty()) {
			for (final L1DollExecutor p : _type.getPowerList()) {
				if ((p instanceof Doll_DmgR) && (i == 1)) {
					broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Aggress));
				}
				if ((p instanceof Doll_DmgDownR) && (i == 2)) {
					broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Aggress));
				}
			}
		}
	}

	public boolean is_power_doll() {
		return _power_doll;
	}
}
