package com.lineage.data.item_etcitem.extra;

import java.sql.Timestamp;
import java.util.Random;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ExtraMagicWeaponTable;
import com.lineage.server.datatables.lock.CharWeaponTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1MagicWeapon;
import com.lineage.server.datatables.sql.CharWeaponTimeTable;

/**
 * 武器魔法DIY系統(DB自製)
 * 
 * @author terry0412 MagicWeapon 失敗特效編號 成功特效編號
 */
public class MagicWeapon extends ItemExecutor {

	private static final Random _random = new Random();

	/**
	 *
	 */
	private MagicWeapon() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new MagicWeapon();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		final int targObjId = data[0];
		final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
		if (tgItem == null) {
			return;
		}

		// 目標對象不為 L1Weapon
		if (tgItem.getItem().getType2() != 1) {
			pc.sendPackets(new S_SystemMessage("\\aD注意！只能對武器附加，並且再次強化可覆蓋或更換其他附魔技能。"));
			return;
		}

		// 取得 魔法石資料
		int steps = CharWeaponTimeTable.get_steps(targObjId);
		if(steps>=0){
			steps += 1;
		}
		if(steps==-1){
			steps = 0;
		}
		if(tgItem.get_magic_weapon()!=null){
		if(tgItem.get_magic_weapon().getItemId()!=item.getItemId()){
			steps = 0;
		}
		}
		final L1MagicWeapon magicWeapon = ExtraMagicWeaponTable.getInstance().get(item.getItemId(),steps);
		if (magicWeapon == null && steps>=0) {
			pc.sendPackets(new S_SystemMessage("\\aD請注意！已達該屬性最高階級。"));
			return;
		}
		if (magicWeapon == null && steps==-1) {
			return;
		}

		// 移除一個道具
		pc.getInventory().removeItem(item, 1);

		// 成功機率判定
		if (_random.nextInt(1000) >= magicWeapon.getSuccessRandom()) {
			// 附魔失敗的訊息
			pc.sendPackets(new S_SystemMessage(magicWeapon.getFailureMsg()));
			// 失敗特效
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_e));
			return;
		}

		boolean update = false;
		// 可直接覆蓋武器原有的附魔效果
		if (tgItem.get_magic_weapon() != null) {
			update = true;
		} else {
			update = false;
		}
		// 附魔效果
		tgItem.set_magic_weapon(magicWeapon);

		// 附魔使用期限 (單位:秒)
		if (magicWeapon.getMaxUseTime() > 0) {
			final Timestamp ts = new Timestamp(
					System.currentTimeMillis() + (magicWeapon.getMaxUseTime() * 1000));
			tgItem.set_time(ts);
		} else {
			tgItem.set_time(null);
		}

		if (update) {
			// 更新資料
			if(tgItem.get_magic_weapon().getItemId()==item.getItemId()){
			CharWeaponTimeReading.get().updateTime(tgItem.getId(), tgItem.get_time(),
					tgItem.get_magic_weapon().getItemId(),steps,1);
			}else
			CharWeaponTimeReading.get().updateTime(tgItem.getId(), tgItem.get_time(),
					tgItem.get_magic_weapon().getItemId(),steps,0);
		} else {
			// 新建資料
			CharWeaponTimeReading.get().addTime(tgItem.getId(), tgItem.get_time(),
					tgItem.get_magic_weapon().getItemId());
		}

		// 更新道具狀態
		pc.sendPackets(new S_ItemStatus(tgItem));

		// 成功特效
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_s));
		// 附魔成功的訊息
		pc.sendPackets(new S_SystemMessage(magicWeapon.getSuccessMsg()));
	}

	private int _gfxid_e; // 失敗特效編號
	private int _gfxid_s; // 成功特效編號

	@Override
	public void set_set(String[] set) {
		try {
			_gfxid_e = Integer.parseInt(set[1]);
			_gfxid_s = Integer.parseInt(set[2]);

		} catch (Exception e) {
		}
	}
}