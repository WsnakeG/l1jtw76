package nick.forMYSQL;

import com.lineage.DatabaseFactory;
import com.lineage.data.item_etcitem.extra.ItemBuffTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NpcBuffSkills {
	private static final ArrayList<ArrayList<Object>> datas = new ArrayList<ArrayList<Object>>();

	public static boolean giveBuff(L1PcInstance pc, L1NpcInstance npc, String args) {
		boolean f = false;

		for (ArrayList<?> os : datas) {
			int[] npcids = (int[]) os.get(0);

			for (int id : npcids) {
				if (id == npc.getNpcId()) {
					f = true;
					break;
				}
			}

			if (f) {
				f = false;
				String[] cmd = (String[]) os.get(1);

				for (String c : cmd) {
					if (c.equals(args)) {
						f = true;
						break;
					}
				}

				if (f) {
					int[] skills = (int[]) os.get(2);
					int[] m = (int[]) os.get(3);
					int[] mc = (int[]) os.get(4);

					for (int i = 0; i < m.length; i++) {
						if (!pc.getInventory().consumeItem(m[i], mc[i])) {
							L1Item aaa = ItemTable.get().getTemplate(m[i]);
							pc.sendPackets(new S_SystemMessage(aaa.getNameId() + " 不足，無法施放魔法輔助。"));
							return true;
						}
					}

					for (int i = 0; i < skills.length; i++) {
						new L1SkillUse().handleCommands(pc, skills[i], pc.getId(), pc.getX(), pc.getY(), 0, 4);
					}

					if (f) {
						return true;
					}
				}
			}
		}
		return f;
	}

	public static void load() {
		final Log _log = LogFactory.getLog(ItemBuffTable.class);
		PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `群版_npc_buff`");
			rs = pstm.executeQuery();
			
			while (rs.next()) {
				ArrayList<Object> data = new ArrayList<Object>();
				data.add(convert(rs.getString("npcid").split(",")));
				data.add(rs.getString("action").split(","));
				data.add(convert(rs.getString("skills").split(",")));
				data.add(convert(rs.getString("material").split(",")));
				data.add(convert(rs.getString("material_count").split(",")));
				datas.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		_log.info("Load--->群版_npc_buff設定, " + "資料共" + datas.size() + "筆, " + "花費(" + timer.get() + "ms) OK");
	}

	private static int[] convert(String[] data) {
		int[] i32 = new int[data.length];

		for (int i = 0; i < data.length; i++) {
			i32[i] = Integer.parseInt(data[i]);
		}

		return i32;
	}
}
