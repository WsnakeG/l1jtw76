package com.lineage.server.model.weaponskill;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 武器技能接口 _level 內容: 發動等級<BR>
 * <BR>
 * _type1 數值1<BR>
 * <BR>
 * _type2 數值2<BR>
 * <BR>
 * _type3 數值3<BR>
 * <BR>
 * _attr 內容: 具備攻擊屬性 0:無属性 1:地 2:火 4:水 8:風<BR>
 * <BR>
 * _ac_mr 內容: 受到抗魔或是防禦力影響 0:無視 1:防禦 2:抗魔<BR>
 * <BR>
 * _gfxid1 內容: 第一個出現的動畫 設置為0無作用 這個動畫出現在施展者身上<BR>
 * <BR>
 * _gfxid2 內容: 第二個出現的動畫 設置為0無作用 這個動畫出現在被攻擊者身上<BR>
 * <BR>
 * _gfxid3 內容: 第三個出現的動畫 設置為0無作用 這個動畫由施展者往被攻擊者<BR>
 * <BR>
 * _gfxid4 內容: 第四個出現的動畫 設置為0無作用 這個動畫由施展者周邊隨機座標出現指定次數<BR>
 * <BR>
 * _gfxid4_count GFXID4 指定次數 設置0 GFXID4無作用<BR>
 * <BR>
 * _power 內容: 受影響的人物屬性 1:力 2:敏 4:智 8:精 16:魅 32:體(傷害會增加 指定屬性總合平均後/2的數字)<BR>
 * 範例:power設置3 受到力敏影響 假設力為20 敏為18 得到的數字為 ((力20 + 敏18) / 2種屬性) /2<BR>
 * <BR>
 * _srcdmg 內容: 基礎攻擊質(隨機) 設置50 則隨機質為 1~50之間<BR>
 * <BR>
 * _addsrcdmg 內容: 是否加入武器產生的物理傷害質(1~100) 0:無作用 設置為10 加入武器產生的物理傷害質 10/100<BR>
 * <BR>
 * _random1 內容: 發動機率(1/1000)<BR>
 * <BR>
 * _random2 內容: 受到強化質的機率影響(1/1000)<BR>
 * <BR>
 * _boss_holdout 內容: 設置為1該技能對spawnlist_boss召喚表內怪物不起作用<BR>
 * 
 * @author daien
 */
public abstract class L1WeaponSkillType {

	private static final Log _log = LogFactory.getLog(L1WeaponSkillType.class);

	private static final Random _random = new Random();

	private double _level = 1.0D;// 內容: 發動等級(最終傷害值會在乘上這倍率)

	protected int _type1;// 數值1

	protected int _type2;// 數值2

	protected int _type3;// 數值3

	private int _attr;// 內容: 具備攻擊屬性 0:無属性 1:地 2:火 4:水 8:風

	protected int _ac_mr;// 內容: 受到抗魔或是防禦力影響 0:無視 1:防禦 2:抗魔

	private int _gfxid1;// 內容: 第一個出現的動畫 設置為0無作用 這個動畫出現在施展者身上

	private int _gfxid2;// 內容: 第二個出現的動畫 設置為0無作用 這個動畫出現在被攻擊者身上

	private int _gfxid3;// 內容: 第三個出現的動畫 設置為0無作用 這個動畫由施展者往被攻擊者

	private int _gfxid4;// 內容: 第四個出現的動畫 設置為0無作用 這個動畫由施展者周邊隨機座標出現指定次數

	private int _gfxid4_count;// 內容: GFXID4 指定次數 設置0 GFXID4無作用

	private int _power;// 內容: 受影響的人物屬性 1:力 2:敏 4:智 8:精 16:魅 32:體

	private int _srcdmg;// 內容: 基礎攻擊質(隨機) 設置50 則隨機質為 1~50之間

	private int _addsrcdmg;// 內容: 是否加入武器產生的物理傷害質(1~100) 0:無作用 設置為10 加入武器產生的物理傷害質
							// 10/100

	protected int _random1;// 內容: 發動機率(1/1000)

	protected int _random2;// 內容: 受到強化質的機率影響(1/1000) 假設武器強化+2 增加設定質 * 2

	protected boolean _boss_holdout;// 內容: 設置為1該技能對spawnlist_boss召喚表內怪物不起作用

	public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
			final L1ItemInstance weapon, final double srcdmg) {
		return 0;
	}

	public double get_level() {
		return _level;
	}

	public void set_level(final int level) {
		_level = (level / 100D);
	}

	public int get_type1() {
		return _type1;
	}

	public void set_type1(final int _type1) {
		this._type1 = _type1;
	}

	public int get_type2() {
		return _type2;
	}

	public void set_type2(final int _type2) {
		this._type2 = _type2;
	}

	public int get_type3() {
		return _type3;
	}

	public void set_type3(final int _type3) {
		this._type3 = _type3;
	}

	public int get_attr() {
		return _attr;
	}

	public void set_attr(final int _attr) {
		this._attr = _attr;
	}

	public int get_ac_mr() {
		return _ac_mr;
	}

	public void set_ac_mr(final int _ac_mr) {
		this._ac_mr = _ac_mr;
	}

	public int get_gfxid1() {
		return _gfxid1;
	}

	public void set_gfxid1(final int _gfxid1) {
		this._gfxid1 = _gfxid1;
	}

	public int get_gfxid2() {
		return _gfxid2;
	}

	public void set_gfxid2(final int _gfxid2) {
		this._gfxid2 = _gfxid2;
	}

	public int get_gfxid3() {
		return _gfxid3;
	}

	public void set_gfxid3(final int _gfxid3) {
		this._gfxid3 = _gfxid3;
	}

	public int get_gfxid4() {
		return _gfxid4;
	}

	public void set_gfxid4(final int _gfxid4) {
		this._gfxid4 = _gfxid4;
	}

	public int get_gfxid4_count() {
		return _gfxid4_count;
	}

	public void set_gfxid4_count(final int _gfxid4_count) {
		this._gfxid4_count = _gfxid4_count;
	}

	/**
	 * 內容: 受影響的人物屬性 1:力 2:敏 4:智 8:精 16:魅 32:體
	 * 
	 * @return
	 */
	public int get_power() {
		return _power;
	}

	/**
	 * 內容: 受影響的人物屬性 1:力 2:敏 4:智 8:精 16:魅 32:體
	 * 
	 * @param _power
	 */
	public void set_power(final int _power) {
		this._power = _power;
	}

	/**
	 * 內容: 基礎攻擊質(隨機) 設置50 則隨機質為 1~50之間
	 * 
	 * @return
	 */
	public int get_srcdmg() {
		return _srcdmg;
	}

	/**
	 * 內容: 基礎攻擊質(隨機) 設置50 則隨機質為 1~50之間
	 * 
	 * @param _srcdmg
	 */
	public void set_srcdmg(final int _srcdmg) {
		this._srcdmg = _srcdmg;
	}

	/**
	 * 內容: 是否加入武器產生的物理傷害質(1~100) 0:無作用 設置為10 加入武器產生的物理傷害質 10/100
	 * 
	 * @return
	 */
	public int get_addsrcdmg() {
		return _addsrcdmg;
	}

	/**
	 * 內容: 是否加入武器產生的物理傷害質(1~100) 0:無作用 設置為10 加入武器產生的物理傷害質 10/100
	 * 
	 * @param _addsrcdmg
	 */
	public void set_addsrcdmg(int _addsrcdmg) {
		if (_addsrcdmg > 100) {
			_addsrcdmg = 100;
		}
		this._addsrcdmg = _addsrcdmg;
	}

	/**
	 * 內容: 發動機率(1/1000)
	 * 
	 * @return
	 */
	public int get_random1() {
		return _random1;
	}

	/**
	 * 內容: 發動機率(1/1000)
	 * 
	 * @param _random1
	 */
	public void set_random1(int _random1) {
		if (_random1 > 1000) {
			_random1 = 1000;
		}
		this._random1 = _random1;
	}

	/**
	 * 內容: 受到強化質的機率影響(1/1000)
	 * 
	 * @return
	 */
	public int get_random2() {
		return _random2;
	}

	/**
	 * 內容: 受到強化質的機率影響(1/1000)
	 * 
	 * @param _random2
	 */
	public void set_random2(int _random2) {
		if (_random2 > 1000) {
			_random2 = 1000;
		}
		this._random2 = _random2;
	}

	public void set_boss_holdout(final boolean boss_holdout) {
		_boss_holdout = boss_holdout;
	}

	public boolean get_boss_holdout() {
		return _boss_holdout;
	}

	/**
	 * 傳回發動機率
	 */
	public int random(final L1ItemInstance weapon) {
		try {
			int int1 = _random1;
			if (_random2 != 0) {
				if (weapon.getEnchantLevel() > 0) {
					int1 += weapon.getEnchantLevel() * _random2;
				}
			}
			if (weapon.get_extra_random() != 0) {
				int1 += weapon.get_extra_random();
			}
			return int1;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	public int random_for_show(final L1ItemInstance weapon) {
		try {
			int int1 = _random1;
			if (_random2 != 0) {
				if (weapon.getEnchantLevel() > 0) {
					int1 += weapon.getEnchantLevel() * _random2;
				}
			}
			return int1;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	/**
	 * 傷害初始計算<BR>
	 * 基礎攻擊質(隨機) 設置50 則隨機質為 1~50之間
	 */
	protected int dmg1() {
		try {
			if (_srcdmg != 0) {
				final int int1 = _random.nextInt(_srcdmg) + 1;
				return int1;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	/**
	 * 傷害結尾計算<BR>
	 * 是否加入武器產生的物理傷害質(1~100%)
	 */
	protected double dmg2(final double srcdmg) {
		try {
			if (_addsrcdmg != 0) {
				final double double1 = (srcdmg * _addsrcdmg) / 100;
				return double1;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	/**
	 * 傷害受影響的人物屬性 1:力 2:敏 4:智 8:精 16:魅 32:體
	 */
	protected int dmg3(final L1PcInstance pc) {
		try {
			if (_power > 0) {// （所需角色能力值-5）* leveldmg
				int int1 = 0;
				int tmp = _power;
				int i = 0;
				if (tmp >= 32) {// 32:體
					i += 1;
					tmp -= 32;
					int1 += pc.getCon();
				}
				if (tmp >= 16) {// 16:魅
					i += 1;
					tmp -= 16;
					int1 += pc.getCha();
				}
				if (tmp >= 8) {// 8:精
					i += 1;
					tmp -= 8;
					int1 += pc.getWis();
				}
				if (tmp >= 4) {// 4:智
					i += 1;
					tmp -= 4;
					int1 += pc.getInt();
				}
				if (tmp >= 2) {// 2:敏
					i += 1;
					tmp -= 2;
					int1 += pc.getDex();
				}
				if (tmp >= 1) {// 1:力
					i += 1;
					tmp -= 1;
					int1 += pc.getStr();
				}
				int1 = (int1 / i) >> 1;// (>> 1: 除) (<< 1: 乘)
				return int1;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	/**
	 * 傷害最終計算
	 * 
	 * @param target
	 * @param srcdmg
	 * @return
	 */
	protected double calc_dmg(final L1PcInstance pc, final L1Character target, final double srcdmg) {
		try {
			double dmg = srcdmg;
			switch (_ac_mr) {
			case 0:// 無視
				break;

			case 1:// 防禦
				dmg -= DmgAcMr.calcDefense(target);// 被攻擊者防禦力傷害減低
				break;

			case 2:// 抗魔
				dmg = DmgAcMr.calcMrDefense(pc, target, srcdmg);// 被攻擊者防禦力傷害減低
				break;

			case 3:// 防禦 + 抗魔
				dmg -= DmgAcMr.calcDefense(target);// 被攻擊者防禦力傷害減低
				dmg = DmgAcMr.calcMrDefense(pc, target, srcdmg);// 被攻擊者防禦力傷害減低
				break;
			}

			// 屬性影響
			int resist = 0;
			switch (_attr) {
			case L1Skills.ATTR_EARTH:// 地
				resist = target.getEarth();
				break;

			case L1Skills.ATTR_FIRE:// 火
				resist = target.getFire();
				break;

			case L1Skills.ATTR_WATER:// 水
				resist = target.getWater();
				break;

			case L1Skills.ATTR_WIND:// 風
				resist = target.getWind();
				break;
			}
			if (resist != 0) {
				int resistFloor = (int) (0.32 * Math.abs(resist));
				if (resist >= 0) {
					resistFloor *= 1;
				} else {
					resistFloor *= -1;
				}
				// (>> 1: 除) (<< 1: 乘)
				final double attrDeffence = resistFloor >> 5;// / 32.0
				dmg = (1.0 - attrDeffence) * dmg;
			}
			return dmg * _level;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	/**
	 * 動畫發送
	 */
	protected void show(final L1PcInstance pc, final L1Character target) {
		try {
			if (_gfxid1 != 0) {// 第一個出現的動畫 設置為0無作用 這個動畫出現在施展者身上
				if (pc.is_send_weapon_gfxid()) {
					pc.sendPackets(new S_SkillSound(pc.getId(), _gfxid1));// single
				}
				pc.broadcastPacketForWeapon(new S_SkillSound(pc.getId(), _gfxid1));// other
			}

			if (_gfxid2 != 0) {// 第二個出現的動畫 設置為0無作用 這個動畫出現在被攻擊者身上
				target.broadcastPacketForWeapon(new S_SkillSound(target.getId(), _gfxid2));
				if (target instanceof L1PcInstance) {
					final L1PcInstance targetPc = (L1PcInstance) target;
					if (targetPc.is_send_weapon_gfxid()) {
						targetPc.sendPackets(new S_SkillSound(target.getId(), _gfxid2));
					}
				}
			}

			if (_gfxid3 != 0) {// 第三個出現的動畫 設置為0無作用 這個動畫由施展者往被攻擊者
				if (pc.is_send_weapon_gfxid()) {
					pc.sendPackets(new S_UseAttackSkill(pc, target.getId(), _gfxid3, target.getX(),
							target.getY(), 0, 0x0a));// single
				}
				pc.broadcastPacketForWeapon(new S_UseAttackSkill(pc, target.getId(), _gfxid3, target.getX(),
						target.getY(), 0, 0x0a));// other
			}

			if (_gfxid4 != 0) {
				if (_gfxid4_count > 0) {
					final WanponSkill wanponSkill = new WanponSkill(pc);
					wanponSkill.start_skill();
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private class WanponSkill implements Runnable {

		private final L1PcInstance _pc;

		private WanponSkill(final L1PcInstance pc) {
			_pc = pc;
		}

		public void start_skill() {
			GeneralThreadPool.get().execute(this);
		}

		@Override
		public void run() {
			try {
				final int x = _pc.getX();
				final int y = _pc.getY();
				final int mapId = _pc.getMapId();
				for (int i = 0; i < _gfxid4_count; i++) {
					Thread.sleep(30);
					final int i1 = _random.nextInt(4);
					final int i2 = _random.nextInt(4);

					switch (_random.nextInt(4)) {
					case 0:
						if (_pc.is_send_weapon_gfxid()) {
							_pc.sendPackets(
									new S_EffectLocation(new L1Location(x + i1, y - i2, mapId), _gfxid4));// single
						}
						_pc.broadcastPacketForWeapon(
								new S_EffectLocation(new L1Location(x + i1, y - i2, mapId), _gfxid4));// other
						break;

					case 1:
						if (_pc.is_send_weapon_gfxid()) {
							_pc.sendPackets(
									new S_EffectLocation(new L1Location(x - i1, y + i2, mapId), _gfxid4));// single
						}
						_pc.broadcastPacketForWeapon(
								new S_EffectLocation(new L1Location(x - i1, y + i2, mapId), _gfxid4));// other
						break;

					case 2:
						if (_pc.is_send_weapon_gfxid()) {
							_pc.sendPackets(
									new S_EffectLocation(new L1Location(x + i1, y + i2, mapId), _gfxid4));// single
						}
						_pc.broadcastPacketForWeapon(
								new S_EffectLocation(new L1Location(x + i1, y + i2, mapId), _gfxid4));// other
						break;

					case 3:
						if (_pc.is_send_weapon_gfxid()) {
							_pc.sendPackets(
									new S_EffectLocation(new L1Location(x - i1, y - i2, mapId), _gfxid4));// single
						}
						_pc.broadcastPacketForWeapon(
								new S_EffectLocation(new L1Location(x - i1, y - i2, mapId), _gfxid4));// other
						break;
					}
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}