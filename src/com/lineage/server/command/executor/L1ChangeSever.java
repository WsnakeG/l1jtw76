package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.item_etcitem.extra.Reward;
import com.lineage.server.datatables.CommandsTable;
import com.lineage.server.datatables.DropMapTable;
import com.lineage.server.datatables.DropTable;
import com.lineage.server.datatables.ItemBoxTable;
import com.lineage.server.datatables.ItemIntegrationTable;
import com.lineage.server.datatables.ItemLimitation;
import com.lineage.server.datatables.ItemMsgTable;
import com.lineage.server.datatables.ItemPowerTable;
import com.lineage.server.datatables.ItemPowerUpdateTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ItemUpdateTable;
import com.lineage.server.datatables.MapExpTable;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.datatables.NpcBoxTable;
import com.lineage.server.datatables.NpcScoreTable;
import com.lineage.server.datatables.NpcSpawnTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.datatables.ServerAIEffectTable;
import com.lineage.server.datatables.ServerAIMapIdTable;
import com.lineage.server.datatables.ShopCnTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;


/**
 * GM指令：線上重讀資料庫 (指令 須重讀的對應命令數字)
 * 
 * @author dexc
 * 
 */

public class L1ChangeSever implements L1CommandExecutor {
	private static final Log _log = LogFactory.getLog(L1ChangeSever.class);

	private L1ChangeSever() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ChangeSever();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName,
			final String arg) {
		try {
			final StringTokenizer stringtokenizer = new StringTokenizer(arg);
			final int mode = Integer.parseInt(stringtokenizer.nextToken());
			switch (mode) {
			case 1:
				DropTable.get().load();
				pc.sendPackets(new S_SystemMessage("\\aH掉落物重讀完畢。"));
				break;
			case 2:
				ShopTable.get().restshop();
				pc.sendPackets(new S_SystemMessage("\\aH商店資訊重讀完畢。"));
				break;
			case 3:
				ShopCnTable.get().restshopCn();
				pc.sendPackets(new S_SystemMessage("\\aH商城商店資訊重讀完畢。"));
				break;
			case 4:
				CommandsTable.get().restcommands();
				pc.sendPackets(new S_SystemMessage("\\aH管理員指令資訊重讀完畢。"));
				break;
			case 5:
				ItemPowerUpdateTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aH強化資料重讀完畢。"));
				break;
			case 6:
				ItemTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aH道具資料重讀完畢。"));
				break;
			case 7:
				NpcTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aHNPC資料重讀完畢。"));
				break;
			case 8:
				DropMapTable.get().load();
				pc.sendPackets(new S_SystemMessage("\\aH地圖指定掉落資訊重讀完畢。"));
				break;
			case 9:
				Reward.reloadReward();
				pc.sendPackets(new S_SystemMessage("\\aH等級獎勵系統重讀完畢。"));
				break;
			case 10:
				MapExpTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aH地圖經驗加倍重讀完畢。"));
				break;
			case 11:
				ItemBoxTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aHEtcitem_box重讀完畢。"));
				break;
			case 12:
				ItemMsgTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aH寶物公告資料表重讀完畢。"));
				break;
			case 13:
				NpcBoxTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aHNPC寶箱資料表重讀完畢。"));
				break;
			case 14:
				NpcScoreTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aH狩獵積分資料重讀完畢。"));
				break;
			case 15:
				NpcSpawnTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aHNPC召喚資料重讀完畢。"));
				break;
			case 16:
				ItemLimitation.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aH總量限制資料重讀完畢。"));
				break;
			case 17:
				NpcTeleportTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aHNPC傳送點資料重讀完畢。"));
				break;
			case 18:
				ItemIntegrationTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aH特殊融合資料重讀完畢。"));
				break;
			case 19:
				NPCTalkDataTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aHNPC對話資料重讀完畢。"));
				break;
			case 20:
				ItemPowerTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aH古文字機率資料重讀完畢。"));
				break;
			case 21:
				ItemUpdateTable.get().reload();
				pc.sendPackets(new S_SystemMessage("\\aHNPC轉換升級資料重讀完畢。"));
				break;
			case 22:
				ServerAIMapIdTable.reload();
				ServerAIEffectTable.reload();
				pc.sendPackets(new S_SystemMessage("\\aH驗證特效與地圖資料重讀完畢。"));
				break;
			default:
				break;
			}
		} catch (final Exception e) {
			_log.error("錯誤的 GM 指令格式: " + this.getClass().getSimpleName()
					+ " 執行 GM :" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_SystemMessage("\\F3【請輸入欲動態更新的資料值】：\n\r"
					+ "\\aG01:怪物掉落資訊重置\n\r" + "\\aD02:商店販售資訊重置\n\r"
					+ "\\aG03:商城販售資訊重置\n\r" + "\\aD04:管理指令資訊重置\n\r"
					+ "\\aG05:強化道具資料重置\n\r" + "\\aD06:道具相關資料重置(正式營運 禁用)\n\r"
					+ "\\aG07:NPC相關資料重置(正式營運 禁用)\n\r" + "\\aD08:指定地圖掉落重置\n\r"
					+ "\\aG09:等級獎勵資料重置\n\r" + "\\aD10:地圖經驗加倍重置\n\r"
					+ "\\aG11:Etcitem_box重置\n\r" + "\\aD12:寶物公告資料重置\n\r"
					+ "\\aG13:NPC寶箱資料重置\n\r" + "\\aD14:狩獵積分資料重置\n\r"
					+ "\\aG15:NPC召喚資料重置\n\r" + "\\aD16:總量限制資料重置\n\r"
					+ "\\aG17:NPC傳送點資料重置\n\r" + "\\aD18:特殊融合資料重置\n\r"
					+ "\\aG19:NPC對話資料重置\n\r" + "\\aD20:古文字機率資料重置\n\r"
					+ "\\aG21:NPC轉換升級資料重置\n\r" + "\\aD22:AI驗證資料重置\n\r"));
		}
	}
}
