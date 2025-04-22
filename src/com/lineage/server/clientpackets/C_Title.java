package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharTitle;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 要求角色建立封號
 * 
 * @author daien
 */
public class C_Title extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Title.class);

	/*
	 * public C_Title() { } public C_Title(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			final String charName = readS();

			final StringBuilder title = new StringBuilder();
			title.append(readS());
			// final String title = this.readS();

			if (charName.isEmpty() || (title.length() <= 0)) {
				// \f1請以如下的格式輸入。: "/title \f0角色名稱 角色封號\f1"
				pc.sendPackets(new S_ServerMessage(196));
				return;
			}

			final L1PcInstance target = World.get().getPlayer(charName);
			if (target == null) {
				return;
			}

			// 有師傅 尚未 畢業
			/*
			 * if (target.get_other().get_apprentice_objid() != 0) { if
			 * (!target.get_other().get_award()) { return; } }
			 */

			if (pc.isGm()) {
				changeTitle(target, title);
				return;
			}

			if (isClanLeader(pc)) { // 血盟主
				if (pc.getId() == target.getId()) { // 自分
					if (pc.getLevel() < 10) {
						// \f1加入血盟之後等級10以上才可使用封號。
						pc.sendPackets(new S_ServerMessage(197));
						return;
					}
					changeTitle(pc, title);

				} else { // 他人
					if (pc.getClanid() != target.getClanid()) {
						// \f1除了王族以外的人，不能夠授與頭銜給其他人。
						pc.sendPackets(new S_ServerMessage(199));
						return;
					}
					if (target.getLevel() < 10) {
						// \f1%0的等級還不到10級，因此無法給封號。
						pc.sendPackets(new S_ServerMessage(202, charName));
						return;
					}
					changeTitle(target, title);
					final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
					if (clan != null) {
						for (final L1PcInstance clanPc : clan.getOnlineClanMember()) {
							// \f1%0%s 賦予%1 '%2'的封號。
							clanPc.sendPackets(
									new S_ServerMessage(203, pc.getName(), charName, title.toString()));
						}
					}
				}
			} else {
				if (pc.getId() == target.getId()) { // 自分
					if (!ConfigOther.CLANTITLE) {
						if (pc.getClanid() != 0) {
							// \f1王子或公主才可給血盟員封號。
							pc.sendPackets(new S_ServerMessage(198));
							return;
						}
					}
					if (target.getLevel() < 40) {
						// \f1若等級40以上，沒有加入血盟也可擁有封號。
						pc.sendPackets(new S_ServerMessage(200));
						return;
					}
					changeTitle(pc, title);

				} else { // 他人
					if (pc.isCrown()) { // 連合に所属した君主
						if (pc.getClanid() == target.getClanid()) {
							// \f1%0%d不是你的血盟成員。
							pc.sendPackets(new S_ServerMessage(201, pc.getClanname()));
							return;
						}
					}
				}
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	/**
	 * 執行封號變更
	 * 
	 * @param pc
	 * @param title
	 */
	private void changeTitle(final L1PcInstance pc, final StringBuilder title) {
		final int objectId = pc.getId();
		pc.setTitle(title.toString());
		pc.sendPacketsAll(new S_CharTitle(objectId, title));
		try {
			pc.save();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private boolean isClanLeader(final L1PcInstance pc) {
		boolean isClanLeader = false;
		if (pc.getClanid() != 0) { // クラン所属
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				if (pc.isCrown() && (pc.getId() == clan.getLeaderId())) { // 君主、かつ、血盟主
					isClanLeader = true;
				}
			}
		}
		return isClanLeader;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
