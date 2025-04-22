package com.lineage.server.clientpackets;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.MailReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_Mail;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Mail;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 要求使用信件系統
 * 
 * @author terry0412
 */
public class C_Mail extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Mail.class);

	/*
	 * public C_Mail() { } public C_Mail(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	/** 一般 */
	private static int TYPE_NORMAL_MAIL = 0x00;

	/** 血盟 */
	private static int TYPE_CLAN_MAIL = 0x01;

	/** 保管箱 */
	private static int TYPE_MAIL_BOX = 0x02;

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}

			// 處理類型
			final int type = readC();

			switch (type) {
			// 開く
			case 0x00:
				if (pc != null) {
					clientPackA(pc, type/* , 40 */);
				}
				break;

			case 0x01:
				if (pc != null) {
					clientPackA(pc, type/* , 80 */);
				}
				break;

			case 0x02:
				if (pc != null) {
					clientPackA(pc, type/* , 10 */);
				}
				break;

			// 讀取
			case 0x10:
				if (pc != null) {
					final int id = readD();
					clientPackB(pc, type, id/* , TYPE_NORMAL_MAIL */);
				}
				break;

			case 0x11:
				if (pc != null) {
					final int id = readD();
					clientPackB(pc, type, id/* , TYPE_CLAN_MAIL */);
				}
				break;

			case 0x12:
				if (pc != null) {
					final int id = readD();
					clientPackB(pc, type, id/* , TYPE_MAIL_BOX */);
				}
				break;

			case 0x20: // 一般信件寄出
				if (pc != null) {
					readH(); // 世界寄信次數紀錄
					final String receiverName = readS();
					final byte[] text = readByte();

					clientPackD(pc, type, receiverName, text);
				}
				break;

			case 0x21: // 血盟信件寄出
				if (pc != null) {
					readH(); // 世界寄信次數紀錄
					final String clanName = readS();
					final byte[] text = readByte();

					clientPackE(pc, type, clanName, text);
				}
				break;

			// 削除
			case 0x30:
			case 0x31:
			case 0x32:
				if (pc != null) {
					final int delid = readD();

					clientPackF(pc, type, delid);
				}
				break;

			case 0x40: // 保管箱に保存
				if (pc != null) {
					final int saveid = readD();

					clientPackG(pc, type, saveid);
				}
				break;

			case 0x60: // 刪除多數信件 (壓著shift一次刪除) by terry0412
				if (pc != null) {
					final int changeCount = readD();
					for (int i = 0; i < changeCount; i++) {
						// 索引碼值 (index)
						final int delid = readD();

						final L1Mail mail = MailReading.get().getMail(delid);
						if (mail != null) {
							MailReading.get().deleteMail(delid);
							pc.sendPackets(new S_Mail(mail, mail.getType() + 0x30));
						}
					}
				}
				break;
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	private void clientPackG(final L1PcInstance pc, final int type, final int saveid) {
		try {
			// 超過信箱可容納數量 10筆 (保管箱)
			if (MailReading.get().getMailSize(pc.getName(), TYPE_MAIL_BOX) >= 10) {
				// 無法再儲存信件。
				pc.sendPackets(new S_ServerMessage(1242));
				return;
			}
			final L1Mail mail = MailReading.get().getMail(saveid);

			if (mail != null) {
				MailReading.get().setMailType(saveid, TYPE_MAIL_BOX);
				pc.sendPackets(new S_Mail(mail, type));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void clientPackF(final L1PcInstance pc, final int type, final int delid) {
		try {
			final L1Mail mail = MailReading.get().getMail(delid);

			if (mail != null) {
				MailReading.get().deleteMail(delid);
				pc.sendPackets(new S_Mail(mail, type));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void clientPackE(final L1PcInstance pc, final int type, final String clanName,
			final byte[] text) {
		try {
			// 每次發送訊息需消耗 1000金幣 by terry0412
			if (!pc.getInventory().consumeItem(L1ItemId.ADENA, 1000)) {
				// 金幣不足。
				pc.sendPackets(new S_ServerMessage(189));
				return;
			}

			final L1Clan clan = WorldClan.get().getClan(clanName);
			if (clan != null) {
				for (final String name : clan.getAllMembers()) {
					// 超過信箱可容納數量 80筆
					final int size = MailReading.get().getMailSize(name, TYPE_CLAN_MAIL);
					if (size >= 80) {
						continue;
					}

					// 收件者 - 收到信件處理
					MailReading.get().writeMail(TYPE_CLAN_MAIL, name, pc.getName(), text, 0);

					final L1PcInstance clanPc = World.get().getPlayer(name);
					if (clanPc != null) { // 連線中
						final ArrayList<L1Mail> mails = MailReading.get().getMails(name, TYPE_CLAN_MAIL);

						if (!mails.isEmpty()) {
							clanPc.sendPackets(new S_Mail(mails, TYPE_CLAN_MAIL));

							// 您收到鴿子信差給你的信件。 by terry0412
							clanPc.sendPackets(new S_SkillSound(clanPc.getId(), 1091));
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void clientPackD(final L1PcInstance pc, final int type, final String receiverName,
			final byte[] textr) {
		try {
			// 超過信箱可容納數量 40筆 (對方)
			if (MailReading.get().getMailSize(receiverName, TYPE_NORMAL_MAIL) >= 40) {
				// 對方的信箱已經滿了，所以無法再寄信到對方信箱。
				// pc.sendPackets(new S_ServerMessage(1240));
				pc.sendPackets(new S_Mail(type, false));
				return;
			}

			// 超過信箱可容納數量 40筆 (自己)
			if (MailReading.get().getMailSize(pc.getName(), TYPE_NORMAL_MAIL) >= 40) {
				// 信箱已滿，無法再收信件。
				pc.sendPackets(new S_ServerMessage(1261));
				return;
			}

			// 找尋玩家物件
			L1PcInstance receiver = World.get().getPlayer(receiverName);
			if (receiver == null) { // 不在連線中
				try {
					receiver = CharacterTable.get().restoreCharacter(receiverName);
					// 資料庫不存在該玩家名稱
					if (receiver == null) {
						// 沒有叫%0的人。
						pc.sendPackets(new S_ServerMessage(109, receiverName));
						return;
					}

				} catch (final Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}
			}

			// 信件黑名單 (暫時只判斷線上玩家, 待修正) by terry0412
			if (receiver.getExcludingList().contains(pc.getName())) {
				// 信件遭阻擋無法寄出
				pc.sendPackets(new S_ServerMessage(3082));
				return;
			}

			// 每次發送訊息需消耗 50金幣 by terry0412
			if (!pc.getInventory().consumeItem(L1ItemId.ADENA, 50)) {
				// 金幣不足。
				pc.sendPackets(new S_ServerMessage(189));
				return;
			}

			// 寄件者 - 收到信件處理
			final L1Mail mail_1 = MailReading.get().writeMail(TYPE_NORMAL_MAIL, receiverName, pc.getName(),
					textr, 0);
			if (mail_1 != null) {
				// 更新信件封包
				pc.sendPackets(new S_Mail(receiverName, mail_1.getId(), true));

				// 您收到鴿子信差給你的信件。 by terry0412
				pc.sendPackets(new S_SkillSound(pc.getId(), 1091));
			}

			// 收件者 - 收到信件處理
			final L1Mail mail_2 = MailReading.get().writeMail(TYPE_NORMAL_MAIL, pc.getName(), receiverName,
					textr, 1);

			if (mail_2 != null) {
				// 收件者 在線上
				if (receiver.getOnlineStatus() == 1) {
					// 更新信件封包
					receiver.sendPackets(new S_Mail(pc.getName(), mail_2.getId(), false));

					// 您收到鴿子信差給你的信件。 by terry0412
					receiver.sendPackets(new S_SkillSound(receiver.getId(), 1091));
				}
			}

			// 已將信件送出了。 by terry0412
			pc.sendPackets(new S_ServerMessage(1239));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 讀取指定id的信件
	 * 
	 * @param pc
	 * @param type
	 * @param id
	 */
	private final void clientPackB(final L1PcInstance pc, final int type, final int id) {
		try {
			final L1Mail mail = MailReading.get().getMail(id);

			if (mail != null) {
				if (mail.getReadStatus() == 0) {
					MailReading.get().setReadStatus(id);
				}
				pc.sendPackets(new S_Mail(mail, type, id));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 讀取全信件標題
	 * 
	 * @param pc
	 * @param type
	 * @param count
	 */
	private final void clientPackA(final L1PcInstance pc,
			final int type/*
							 * , final int count
							 */) {
		try {
			final ArrayList<L1Mail> mails = MailReading.get().getMails(pc.getName(),
					type/*
						 * , count
						 */);
			if (mails != null) {
				if (!mails.isEmpty()) {
					pc.sendPackets(new S_Mail(mails, type));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
