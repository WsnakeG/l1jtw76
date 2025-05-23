package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 產生動畫物件(參數:動畫編號)
 * 
 * @author dexc
 */
public class L1GfxId implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1GfxId.class);

	private L1GfxId() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GfxId();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			int gfxid = 0;// 十進制
			int count = 1;// 十進制
			gfxid = Integer.parseInt(st.nextToken(), 10);// 十進制
			try {
				count = Integer.parseInt(st.nextToken(), 10);// 十進制

			} catch (final Exception e) {
				count = 1;
			}

			if (count == 1) {
				final L1NpcInstance npc = NpcTable.get().newNpcInstance(50000);
				spawn(pc, npc, gfxid, 0, count);

			} else {
				for (int i = 0; i < count; i++) {
					final L1NpcInstance npc = NpcTable.get().newNpcInstance(50000);
					spawn(pc, npc, gfxid, i, count);
				}
			}

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}

	private void spawn(final L1PcInstance pc, final L1NpcInstance npc, final int gfxid, final int i,
			final int count) {
		if (npc != null) {
			final int tempgfxid = gfxid + i;
			npc.setId(IdFactoryNpc.get().nextId());
			npc.setGfxId(tempgfxid);
			npc.setTempCharGfx(tempgfxid);
			npc.setNameId("GFXID:" + npc.getGfxId());
			npc.setMap(pc.getMapId());
			npc.setX(pc.getX() + (i * 2));
			npc.setY(pc.getY() + (i * 2));
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(4);

			npc.setGfxidInStatus(tempgfxid);

			// 存在時間(秒)
			npc.set_spawnTime(300);

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			startDelMe(npc, count);
		}
	}

	private void startDelMe(final L1NpcInstance npc, final int count) {
		final DelMe delMe = new DelMe(npc, count);
		GeneralThreadPool.get().execute(delMe);
	}

	private class DelMe implements Runnable {

		private final L1NpcInstance _npc;

		// private boolean _isDel = false;

		private int _isTest = 0;

		private DelMe(final L1NpcInstance npc, final int mode) {
			_npc = npc;
			_isTest = mode;
		}

		@Override
		public void run() {
			int i = 0;
			try {
				while (!_npc.isDead()) {
					Thread.sleep(2000);
					i++;
					if (_isTest == 1) {
						if ((i != 8) && (i <= 40)) {
							_npc.broadcastPacketX10(new S_DoActionGFX(_npc.getId(), i));
							_npc.broadcastPacketX10(new S_NpcChat(_npc, "ACID: " + i));

						} else if ((i >= 41) && (i <= 48)) {
							final int h = i - 41;
							_npc.setHeading(h);
							_npc.broadcastPacketX10(new S_ChangeHeading(_npc));
							_npc.broadcastPacketX10(new S_NpcChat(_npc, "HEAD: " + i));
						}
					} else {
						_npc.broadcastPacketX10(new S_NpcChat(_npc, " "));
					}
					/*
					 * if (i >= 300) { this._isDel = true; }
					 */
				}

			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

	}
}
