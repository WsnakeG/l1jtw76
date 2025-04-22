package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CheckUtil;

/**
 * NPC 死亡傳送玩家<BR>
 * 設置範例:<BR>
 * LOCX LOCY MAPID 是否傳送隊員<BR>
 * classname: mob.MobTeleport 設置範例: mob.MobTeleport 33425 32827 4 false<BR>
 * 
 * @author dexc
 */
public class MobTeleport extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(MobTeleport.class);

	private MobTeleport() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new MobTeleport();
	}

	@Override
	public int type() {
		return 8;
	}

	@Override
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		try {
			// 判斷主要攻擊者
			final L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
			if (pc != null) {
				if ((_locx != 0) && (_locy != 0)) {
					final M_teleport teleport = new M_teleport(pc);
					teleport.start_cmd();
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	private int _locx;

	private int _locy;

	private int _mapid;

	private boolean _party;

	@Override
	public void set_set(final String[] set) {
		try {
			_locx = Integer.parseInt(set[1]);

		} catch (final Exception e) {
		}
		try {
			_locy = Integer.parseInt(set[2]);

		} catch (final Exception e) {
		}
		try {
			_mapid = Integer.parseInt(set[3]);

		} catch (final Exception e) {
		}
		try {
			_party = Boolean.parseBoolean(set[4]);

		} catch (final Exception e) {
		}
	}

	private class M_teleport implements Runnable {

		private final L1PcInstance _pc;

		private M_teleport(final L1PcInstance pc) {
			_pc = pc;
		}

		private void start_cmd() {
			GeneralThreadPool.get().execute(this);
		}

		@Override
		public void run() {
			try {
				_pc.sendPackets(new S_PacketBoxGree(0x01));
				_pc.sendPackets(new S_PacketBoxGree(0x02, "5秒後將會被傳送!"));
				Thread.sleep(1000);
				_pc.sendPackets(new S_PacketBoxGree(0x02, "4秒後將會被傳送!"));
				Thread.sleep(1000);
				_pc.sendPackets(new S_PacketBoxGree(0x02, "3秒後將會被傳送!"));
				Thread.sleep(1000);
				_pc.sendPackets(new S_PacketBoxGree(0x02, "2秒後將會被傳送!"));
				Thread.sleep(1000);
				_pc.sendPackets(new S_PacketBoxGree(0x02, "1秒後將會被傳送!"));
				Thread.sleep(1000);

				if (_party) {
					final L1Party party = _pc.getParty();
					if (party != null) {
						// 隊伍成員
						for (final L1PcInstance otherPc : party.getMemberList()) {
							// 相同地圖
							if (otherPc.getMapId() == _pc.getMapId()) {
								// 傳送成員
								L1Teleport.teleport(otherPc, _locx, _locy, (short) _mapid, 5, true);
							}
						}
						L1Teleport.teleport(party.getLeader(), _locx, _locy, (short) _mapid, 5, true);
						return;
					} else {
						// 自身的傳送
						L1Teleport.teleport(_pc, _locx, _locy, (short) _mapid, 5, true);
					}

				} else {
					// 自身的傳送
					L1Teleport.teleport(_pc, _locx, _locy, (short) _mapid, 5, true);
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}