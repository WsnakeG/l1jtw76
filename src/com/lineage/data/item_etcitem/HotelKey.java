package com.lineage.data.item_etcitem;

import static com.lineage.server.model.skill.L1SkillId.STATUS_FREEZE;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.InnTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Inn;
import com.lineage.server.utils.CheckUtil;

/**
 * 旅館鑰匙
 * @author Admin
 *
 */
public class HotelKey extends ItemExecutor {
	private static final Log _log = LogFactory.getLog(HotelKey.class);

	public static ItemExecutor get() {
		return new HotelKey();
	}

	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		try {
			if (item == null) {
				return;
			}

			if (pc == null) {
				return;
			}

			if (!CheckUtil.getUseItem(pc)) {
				return;
			}

			if (!pc.getMap().isEscapable()) { // src1005
				// 276 \f1在此無法使用傳送。
				pc.sendPackets(new S_ServerMessage(276));
				return;
			}
			if (pc.hasSkillEffect(STATUS_FREEZE)) {
				pc.sendPackets(new S_SystemMessage("束縛術的狀態下無法使用。"));
				return;
			}
			for (int i = 0; i < 16; i++) {
				L1Inn inn = InnTable.getInstance().getTemplate(item.getInnNpcId(), i);// 取回旅館資料
				if (inn.getKeyId() == item.getKeyId()) {// 鑰匙編號相同
					Timestamp dueTime = item.getDueTime();
					if (dueTime != null) {
						Calendar cal = Calendar.getInstance();
						if ((cal.getTimeInMillis() - dueTime.getTime()) / 1000L < 0L) {// 還未到退房時間
							int[] locdata = (int[]) null;
							switch (item.getInnNpcId()) {
							case 70012:
								locdata = new int[] { 32745, 32803, 16384, 32743, 32808, 16896 };
								break;
							case 70019:
								locdata = new int[] { 32743, 32803, 17408, 32744, 32807, 17920 };
								break;
							case 70031:
								locdata = new int[] { 32744, 32803, 18432, 32744, 32807, 18944 };
								break;
							case 70065:
								locdata = new int[] { 32744, 32803, 19456, 32744, 32807, 19968 };
								break;
							case 70070:
								locdata = new int[] { 32744, 32803, 20480, 32744, 32807, 20992 };
								break;
							case 70075:
								locdata = new int[] { 32744, 32803, 21504, 32744, 32807, 22016 };
								break;
							case 70084:
								locdata = new int[] { 32744, 32803, 22528, 32744, 32807, 23040 };
								break;
							case 70054:
								locdata = new int[] { 32744, 32803, 23552, 32744, 32807, 24064 };
								break;
							case 70096:
								locdata = new int[] { 32744, 32803, 24576, 32744, 32807, 25088 };
								break;
							}

							if (!item.checkRoomOrHall()) {// 一般房間
								pc.set_showId(item.getKeyId());// 設置副本編號
								L1Teleport.teleport(pc, locdata[0], locdata[1], (short) locdata[2], 6, true);
								break;
							} else {// 會議室
								pc.set_showId(item.getKeyId());// 設置副本編號
								L1Teleport.teleport(pc, locdata[3], locdata[4], (short) locdata[5], 6, true);
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

}
