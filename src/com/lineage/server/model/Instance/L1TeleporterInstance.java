package com.lineage.server.model.Instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1NpcTalkData;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 傳送師控制項
 * 
 * @author daien
 */
public class L1TeleporterInstance extends L1NpcInstance {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1TeleporterInstance.class);

	/**
	 * 傳送師
	 * 
	 * @param template
	 */
	public L1TeleporterInstance(final L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			final L1AttackMode attack = new L1AttackPc(pc, this);
			// attack.calcHit();
			attack.action();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onTalkAction(final L1PcInstance player) {
		final int objid = getId();
		final L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(getNpcTemplate().get_npcId());
		final int npcid = getNpcTemplate().get_npcId();
		// final L1PcQuest quest = player.getQuest();
		String htmlid = null;

		if (talking != null) {
			// 巴尼亞
			if (npcid == 50001) {
				if (player.isElf()) {
					htmlid = "barnia3";
				} else if (player.isKnight() || player.isCrown()) {
					htmlid = "barnia2";
				} else if (player.isWizard() || player.isDarkelf()) {
					htmlid = "barnia1";
				}
			}

			// html表示
			if (htmlid != null) { // htmlidが指定されている場合
				player.sendPackets(new S_NPCTalkReturn(objid, htmlid));

			} else {
				if (player.getLawful() < -1000) { // プレイヤーがカオティック
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));

				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		} else {
			/*
			 * _log.finest((new StringBuilder()) .append(
			 * "No actions for npc id : ").append(objid) .toString());
			 */
		}
	}

	@Override
	public void onFinalAction(final L1PcInstance player, final String action) {
		final int objid = getId();
		final L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(getNpcTemplate().get_npcId());
		if (action.equalsIgnoreCase("teleportURL")) {
			final L1NpcHtml html = new L1NpcHtml(talking.getTeleportURL());
			player.sendPackets(new S_NPCTalkReturn(objid, html));

		} else if (action.equalsIgnoreCase("teleportURLA")) {
			final L1NpcHtml html = new L1NpcHtml(talking.getTeleportURLA());
			player.sendPackets(new S_NPCTalkReturn(objid, html));
		}

		if (action.startsWith("teleport ")) {
			doFinalAction(player, action);
		}
	}

	private void doFinalAction(final L1PcInstance player, final String action) {
		final int objid = getId();

		final int npcid = getNpcTemplate().get_npcId();
		final String htmlid = null;
		boolean isTeleport = true;

		if (npcid == 50043) { // 拉姆塔
			if (_isNowDely) { // テレポートディレイ中
				isTeleport = false;
			}

		} else if (npcid == 50625) { // 古代人（Lv50クエスト古代の空間2F）
			if (_isNowDely) { // テレポートディレイ中
				isTeleport = false;
			}
		}

		if (isTeleport) { // テレポート実行
			try {
				// 試練のダンジョン（ウィザードLv30クエスト）
				if (action.equalsIgnoreCase("teleport 29")) { // ラムダ
					final L1PcInstance kni = null;
					final L1PcInstance elf = null;
					final L1PcInstance wiz = null;

					L1Teleport.teleport(player, 32723, 32850, (short) 2000, 2, true);
					L1Teleport.teleport(kni, 32750, 32851, (short) 2000, 6, true);
					L1Teleport.teleport(elf, 32878, 32980, (short) 2000, 6, true);
					L1Teleport.teleport(wiz, 32876, 33003, (short) 2000, 0, true);
					final TeleportDelyTimer timer = new TeleportDelyTimer();
					GeneralThreadPool.get().execute(timer);
				} else if (action.equalsIgnoreCase("teleport barlog")) { // 古代人（Lv50クエスト古代の空間2F）
					L1Teleport.teleport(player, 32755, 32844, (short) 2002, 5, true);
					final TeleportDelyTimer timer = new TeleportDelyTimer();
					GeneralThreadPool.get().execute(timer);
				}
			} catch (final Exception e) {
			}
		}
		player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
	}

	class TeleportDelyTimer implements Runnable {

		public TeleportDelyTimer() {
		}

		@Override
		public void run() {
			try {
				_isNowDely = true;
				Thread.sleep(900000); // 15分

			} catch (final Exception e) {
				_isNowDely = false;
			}
			_isNowDely = false;
		}
	}

	private boolean _isNowDely = false;
}