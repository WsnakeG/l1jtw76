package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.CharShiftingStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.SQLUtil;

/**
 * 裝備移轉紀錄資料
 * 
 * @author dexc
 */
public class CharShiftingTable implements CharShiftingStorage {

	private static final Log _log = LogFactory.getLog(CharShiftingTable.class);

	/**
	 * 增加裝備移轉紀錄
	 * 
	 * @param pc 執行人物
	 * @param tgId 目標objid
	 * @param tgName 目標名稱
	 * @param srcObjid 原始objid
	 * @param srcItem 原始物件
	 * @param newItem 新物件
	 * @param mode 模式<BR>
	 *            0: 交換裝備<BR>
	 *            1: 裝備升級<BR>
	 *            2: 轉移裝備<BR>
	 */
	@Override
	public void newShifting(final L1PcInstance pc, final int tgId, final String tgName, final int srcObjid,
			final L1Item srcItem, final L1ItemInstance newItem, final int mode) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("INSERT INTO `other_shifting` SET "
					+ "`srcObjid`=?,`srcItemid`=?,`srcName`=?," + "`newObjid`=?,`newItemid`=?,`newName`=?,"
					+ "`enchantLevel`=?,`attrEnchant`=?,`weaponSkill`=?," + "`pcObjid`=?,`pcName`=?,"
					+ "`tgPcObjid`=?,`tgPcName`=?," + "`time`=?,`note`=?");

			int i = 0;
			if (srcItem != null) {
				ps.setInt(++i, srcObjid);
				ps.setInt(++i, srcItem.getItemId());
				ps.setString(++i, srcItem.getName());

			} else {
				ps.setInt(++i, 0);
				ps.setInt(++i, 0);
				ps.setString(++i, "");

			}

			ps.setInt(++i, newItem.getId());
			ps.setInt(++i, newItem.getItemId());

			ps.setString(++i, newItem.getItem().getName());
			// 強化值
			ps.setInt(++i, newItem.getEnchantLevel());
			// 物件地水火風 屬性/強化值
			ps.setString(++i, newItem.getAttrEnchantKind() + "/" + newItem.getAttrEnchantLevel());

			/*
			 * if (newItem.isCnSkill()) { L1WeaponCnSkill cnSkill =
			 * newItem.get_CnSkill(); ps.setString(++i, cnSkill.get_none() + "/"
			 * + cnSkill.get_mode() + "/" + cnSkill.get_level()); } else {
			 * ps.setString(++i, ""); }
			 */
			final StringBuilder cnSkillInfo = new StringBuilder();

			if (cnSkillInfo.length() > 0) {
				ps.setString(++i, cnSkillInfo.toString());

			} else {
				ps.setString(++i, "無效果");
			}

			ps.setInt(++i, pc.getId());
			ps.setString(++i, pc.getName());

			// 取回時間
			final Timestamp lastactive = new Timestamp(System.currentTimeMillis());

			// 取得模式
			switch (mode) {
			case 0:// 交換裝備
				ps.setInt(++i, 0);
				ps.setString(++i, "");
				ps.setTimestamp(++i, lastactive);
				ps.setString(++i, "交換裝備");
				break;

			case 1:// 裝備升級
				ps.setInt(++i, 0);
				ps.setString(++i, "");
				ps.setTimestamp(++i, lastactive);
				ps.setString(++i, "裝備升級");
				break;

			case 2:// 轉移裝備
				ps.setInt(++i, tgId);
				ps.setString(++i, tgName);
				ps.setTimestamp(++i, lastactive);
				ps.setString(++i, "轉移裝備");
				break;
			}

			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

}
