package com.lineage.server.command.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;
// import java.util.logging.Logger;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

// Referenced classes of package l1j.server.server.command.executor:
// L1CommandExecutor

public class L1Search implements L1CommandExecutor {

	// private static final Log _log = LogFactory.getLog(L1Search.class);

	private L1Search() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Search();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			String type = "";
			String name = "";
			String add = "";
			boolean simpleS = true;
			for (int itCount = 0; st.hasMoreTokens(); itCount++) {
				if (itCount == 1) {
					add = "%";
				}
				final String tempVar = st.nextToken();
				if ((itCount == 0) && (tempVar.equals("\\aK防具") || tempVar.equals("\\aE武器")
						|| tempVar.equals("\\aH道具") || tempVar.equals("變身") || tempVar.equals("\\aENPC"))) {
					simpleS = false;
					type = tempVar;
				} else {
					name = (new StringBuilder(String.valueOf(name))).append(add).append(tempVar).toString();
				}
			}

			if (!simpleS) {
				find_object(pc, type, name);
			} else {
				find_object(pc, name);
			}
		} catch (final Exception e) {
			pc.sendPackets(new S_SystemMessage("導入 .find [\\aK防具,\\aE武器,\\aH道具,變身,\\aENPC] [物品名稱]"));
		}
	}

	private void find_object(final L1PcInstance pc, final String type, final String name) {
		try {
			String str1 = null;
			String str2 = null;
			int bless = 0;
			int count = 0;
			Connection con = null;
			con = DatabaseFactory.get().getConnection();
			PreparedStatement statement = null;
			boolean error = false;
			pc.sendPackets(new S_SystemMessage(" "));
			if (type.equals("\\aK防具")) {
				statement = con.prepareStatement(
						(new StringBuilder("SELECT item_id,name,bless FROM armor WHERE name Like '%"))
								.append(name).append("%'").toString());
			} else if (type.equals("\\aE武器")) {
				statement = con.prepareStatement(
						(new StringBuilder("SELECT item_id,name,bless FROM weapon WHERE name Like '%"))
								.append(name).append("%'").toString());
			} else if (type.equals("\\aH道具")) {
				statement = con.prepareStatement(
						(new StringBuilder("SELECT item_id,name,bless FROM etcitem WHERE name Like '%"))
								.append(name).append("%'").toString());
			} else if (type.equals("變身")) {
				statement = con.prepareStatement(
						(new StringBuilder("SELECT polyid,name FROM polymorphs WHERE name Like '%"))
								.append(name).append("%'").toString());
			} else if (type.equals("\\aENPC")) {
				statement = con
						.prepareStatement((new StringBuilder("SELECT npcid,name FROM npc WHERE name Like '%"))
								.append(name).append("%'").toString());
			} else {
				error = true;
				pc.sendPackets(new S_SystemMessage("導入 .find [\\aK防具,\\aE武器,\\aH道具,變身,\\aENPC] [物品名稱]"));
			}
			String blessed = null;
			if (!error) {
				final ResultSet rs = statement.executeQuery();
				pc.sendPackets(
						new S_SystemMessage((new StringBuilder("正在搜索符合 '")).append(name.replace("%", " "))
								.append(" ' 的").append(type).append("名稱...").toString()));
				while (rs.next()) {
					str1 = rs.getString(1);
					str2 = rs.getString(2);
					if (type.equals("\\aK防具") || type.equals("\\aE武器") || type.equals("\\aH道具")) {
						bless = rs.getInt(3);
						if (bless == 1) {
							blessed = "";
						} else if (bless == 0) {
							blessed = "\\aI";
						} else {
							blessed = "\\aI";
						}
						pc.sendPackets(new S_SystemMessage((new StringBuilder(String.valueOf(blessed)))
								.append("導入: ").append(str1).append(", ").append(str2).toString()));
					} else {
						pc.sendPackets(new S_SystemMessage((new StringBuilder("??: ")).append(str1)
								.append(", ").append(str2).toString()));
					}
					count++;
				}
				rs.close();
				statement.close();
				con.close();
				pc.sendPackets(new S_SystemMessage((new StringBuilder("找到 ")).append(count).append("導入物品符合")
						.append(type).append("類型。").toString()));
			}
		} catch (final Exception exception) {
		}
	}

	private void find_object(final L1PcInstance pc, final String name) {
		try {
			String str1 = null;
			String str2 = null;
			int bless = 0;
			String blessed = null;
			Connection con = null;
			con = DatabaseFactory.get().getConnection();
			PreparedStatement statement = null;
			pc.sendPackets(new S_SystemMessage(" "));
			pc.sendPackets(new S_SystemMessage((new StringBuilder("正在搜索符合 '")).append(name.replace("%", " "))
					.append(" ' 的物品名稱:").toString()));
			statement = con.prepareStatement(
					(new StringBuilder("SELECT item_id,name,bless FROM armor WHERE name Like '%"))
							.append(name).append("%'").toString());
			int count1 = 0;
			ResultSet rs;
			for (rs = statement.executeQuery(); rs.next();) {
				if (count1 == 0) {
					pc.sendPackets(new S_SystemMessage("\\aK防具:"));
				}
				str1 = rs.getString(1);
				str2 = rs.getString(2);
				bless = rs.getInt(3);
				if (bless == 1) {
					blessed = "";
				} else if (bless == 0) {
					blessed = "\\aI";
				} else {
					blessed = "\\aI";
				}
				pc.sendPackets(new S_SystemMessage((new StringBuilder(String.valueOf(blessed))).append("導入: ")
						.append(str1).append(", ").append(str2).toString()));
				count1++;
			}

			rs.close();
			statement.close();
			statement = con.prepareStatement(
					(new StringBuilder("SELECT item_id,name,bless FROM weapon WHERE name Like '%"))
							.append(name).append("%'").toString());
			int count2 = 0;
			for (rs = statement.executeQuery(); rs.next();) {
				if (count2 == 0) {
					pc.sendPackets(new S_SystemMessage("\\aE武器:"));
				}
				str1 = rs.getString(1);
				str2 = rs.getString(2);
				bless = rs.getInt(3);
				if (bless == 1) {
					blessed = "";
				} else if (bless == 0) {
					blessed = "\\aI";
				} else {
					blessed = "\\aI";
				}
				pc.sendPackets(new S_SystemMessage((new StringBuilder(String.valueOf(blessed))).append("導入: ")
						.append(str1).append(", ").append(str2).toString()));
				count2++;
			}

			rs.close();
			statement.close();
			statement = con.prepareStatement(
					(new StringBuilder("SELECT item_id,name,bless FROM etcitem WHERE name Like '%"))
							.append(name).append("%'").toString());
			int count3 = 0;
			for (rs = statement.executeQuery(); rs.next();) {
				if (count3 == 0) {
					pc.sendPackets(new S_SystemMessage("\\aH道具:"));
				}
				str1 = rs.getString(1);
				str2 = rs.getString(2);
				bless = rs.getInt(3);
				if (bless == 1) {
					blessed = "";
				} else if (bless == 0) {
					blessed = "\\aI";
				} else {
					blessed = "\\aI";
				}
				pc.sendPackets(new S_SystemMessage((new StringBuilder(String.valueOf(blessed))).append("導入: ")
						.append(str1).append(", ").append(str2).toString()));
				count3++;
			}

			rs.close();
			statement.close();
			statement = con.prepareStatement(
					(new StringBuilder("SELECT polyid,name FROM polymorphs WHERE name Like '%")).append(name)
							.append("%'").toString());
			int count4 = 0;
			for (rs = statement.executeQuery(); rs.next();) {
				if (count4 == 0) {
					pc.sendPackets(new S_SystemMessage("變身:"));
				}
				str1 = rs.getString(1);
				str2 = rs.getString(2);
				pc.sendPackets(new S_SystemMessage(
						(new StringBuilder("導入: ")).append(str1).append(", ").append(str2).toString()));
				count4++;
			}

			rs.close();
			statement.close();
			statement = con
					.prepareStatement((new StringBuilder("SELECT npcid,name FROM npc WHERE name Like '%"))
							.append(name).append("%'").toString());
			int count5 = 0;
			for (rs = statement.executeQuery(); rs.next();) {
				if (count5 == 0) {
					pc.sendPackets(new S_SystemMessage("\\aENPC:"));
				}
				str1 = rs.getString(1);
				str2 = rs.getString(2);
				pc.sendPackets(new S_SystemMessage(
						(new StringBuilder("導入: ")).append(str1).append(", ").append(str2).toString()));
				count5++;
			}

			rs.close();
			statement.close();
			con.close();
			pc.sendPackets(new S_SystemMessage("搜索結果:"));
			String found = "";
			if (count1 > 0) {
				found = (new StringBuilder(String.valueOf(found))).append("\\aK防具: ").append(count1)
						.append("、").toString();
			}
			if (count2 > 0) {
				found = (new StringBuilder(String.valueOf(found))).append("\\aE武器: ").append(count2)
						.append("、").toString();
			}
			if (count3 > 0) {
				found = (new StringBuilder(String.valueOf(found))).append("\\aH道具: ").append(count3)
						.append("、").toString();
			}
			if (count4 > 0) {
				found = (new StringBuilder(String.valueOf(found))).append("變身: ").append(count4).append("、")
						.toString();
			}
			if (count5 > 0) {
				found = (new StringBuilder(String.valueOf(found))).append("\\aENPC: ").append(count5)
						.append("。").toString();
			}
			if (found.length() > 0) {
				found = (new StringBuilder(String.valueOf(found.substring(0, found.length() - 1))))
						.append("。").toString();
			} else {
				found = "找到 0 個物品";
			}
			pc.sendPackets(new S_SystemMessage(found));
		} catch (final Exception exception) {
		}
	}

}