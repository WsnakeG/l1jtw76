package com.lineage.server.clientpackets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_RetrieveElfList;
import com.lineage.server.serverpackets.S_RetrieveList;
import com.lineage.server.serverpackets.S_RetrievePledgeList;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Account;
import com.lineage.server.world.World;

/**
 * 要求變更與使用倉庫密碼
 * 
 * @author dexc
 */
public class C_Password extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Password.class);

	/*
	 * public C_Password() { } public C_Password(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	private static final List<Integer> password = new ArrayList<Integer>();

	static {
		password.add(0, 994303243);
		password.add(1, 994303242);
		password.add(2, 994303241);
		password.add(3, 994303240);
		password.add(4, 994303247);
		password.add(5, 994303246);
		password.add(6, 994303245);
		password.add(7, 994303244);
		password.add(8, 994303235);
		password.add(9, 994303234);
	}

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			// 取得角色物件
			final L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}

			final int type = readC(); // 模式

			// 舊密碼
			final int pass1 = (password.indexOf(readD()) * 100000) + (password.indexOf(readD()) * 10000)
					+ (password.indexOf(readD()) * 1000) + (password.indexOf(readD()) * 100)
					+ (password.indexOf(readD()) * 10) + (password.indexOf(readD()));
			/*
			 * _log.info("1this.readD() = " + this.readD()); _log.info(
			 * "2this.readD() = " + this.readD()); _log.info("3this.readD() = "
			 * + this.readD()); _log.info("4this.readD() = " + this.readD());
			 * _log.info("5this.readD() = " + this.readD()); _log.info(
			 * "6this.readD() = " + this.readD());
			 */

			final L1Account account = client.getAccount();

			// 更改新密碼 / 款項提取
			if (type == 0) {
				final int pass2 = (password.indexOf(readD()) * 100000) + (password.indexOf(readD()) * 10000)
						+ (password.indexOf(readD()) * 1000) + (password.indexOf(readD()) * 100)
						+ (password.indexOf(readD()) * 10) + (password.indexOf(readD()));

				// 不明的2個位元組
				readH();

				// 兩次皆直接跳過密碼輸入
				if ((pass1 < 0) && (pass2 < 0)) {
					pc.sendPackets(new S_ServerMessage(79));
				}

				// 進行新密碼的設定
				else if ((pass1 < 0) && (account.get_warehouse() == 0)) {
					// 進行密碼變更
					account.set_warehouse(pass2);
					AccountReading.get().updateWarehouse(account.get_login(), pass2);
					pc.sendPackets(new S_SystemMessage("倉庫密碼設定完成，請牢記您的新密碼。"));
				}

				// 進行密碼變更
				else if ((pass1 > 0) && (pass1 == account.get_warehouse())) {
					// 進行密碼變更
					if (pass1 == pass2) {
						// [342:你不能使用舊的密碼當作新的密碼。請再次輸入密碼。]
						pc.sendPackets(new S_ServerMessage(342));
						// 消除
						stopAction(client, pc);
						return;
					}
					account.set_warehouse(pass2);
					AccountReading.get().updateWarehouse(account.get_login(), pass2);
				} else {
					// 送出密碼錯誤的提示訊息[835:密碼錯誤。]
					pc.sendPackets(new S_ServerMessage(835));
					final int error = client.get_error();
					client.set_error(error + 1);
					_log.error(pc.getName() + " 倉庫密碼輸入錯誤!!( " + client.get_error() + " 次)");
				}
			}
			// 密碼驗證
			else {
				if (account.get_warehouse() == pass1) {
					// 對話 NPCID
					final int objid = readD();
					if (pc.getLevel() >= 5) {// 判斷玩家等級
						if (type == 1) {
							final L1Object obj = World.get().findObject(objid);
							if (obj != null) {
								if (obj instanceof L1NpcInstance) {
									final L1NpcInstance npc = (L1NpcInstance) obj;
									// 判斷npc所屬倉庫類別
									switch (npc.getNpcId()) {
									case 60028:// 倉庫-艾爾(妖森)
										// 密碼吻合 輸出倉庫視窗
										if (pc.isElf()) {
											pc.sendPackets(new S_RetrieveElfList(objid, pc));
										}
										break;
									default:
										// 密碼吻合 輸出倉庫視窗
										pc.sendPackets(new S_RetrieveList(objid, pc));
										break;
									}
								}
							}
						} else if (type == 2) {
							if (pc.getClanid() == 0) {
								// \f1若想使用血盟倉庫，必須加入血盟。
								pc.sendPackets(new S_ServerMessage(208));
								return;
							}
							// changed by terry0412
							if (pc.getClanRank() == L1Clan.CLAN_RANK_PUBLIC) {
								// 只有收到稱謂的人才能使用血盟倉庫。
								pc.sendPackets(new S_ServerMessage(728));
								return;
							}
							pc.sendPackets(new S_RetrievePledgeList(objid, pc));
						}
					}
				} else {
					// 送出密碼錯誤的提示訊息
					pc.sendPackets(new S_ServerMessage(835));
				}
			}
			// 消除
			stopAction(client, pc);
		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);
		} finally {
			over();
		}
	}

	private void stopAction(final ClientExecutor client, final L1PcInstance pc) {
		// 消除
		pc.setTempID(0);
		// 解除錯誤次數
		client.set_error(0);
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
