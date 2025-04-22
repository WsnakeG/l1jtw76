package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;

/**
 * 角色資訊
 * 
 * @author dexc
 */
public class S_OwnCharStatus extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色資訊
	 * 
	 * @param pc
	 */
	public S_OwnCharStatus(final L1PcInstance pc) {
		int time = L1GameTimeClock.getInstance().currentTime().getSeconds();
		time = time - (time % 300);
		// _log.warning((new
		// StringBuilder()).append("送信時間:").append(i).toString());
		writeC(S_OPCODE_OWNCHARSTATUS);
		writeD(pc.getId());

		writeC(pc.getLevel());

		writeExp(pc.getExp());

		writeC(pc.getStr());
		writeC(pc.getInt());
		writeC(pc.getWis());
		writeC(pc.getDex());
		writeC(pc.getCon());
		writeC(pc.getCha());
		writeH(pc.getCurrentHp());
		writeH(pc.getMaxHp());
		writeH(pc.getCurrentMp());
		writeH(pc.getMaxMp());
		writeD(pc.getAc());// -211～44 value C => D by 7.6tw
		writeD(time);
		writeC(pc.get_food());
		writeC(pc.getInventory().getWeight240());
		writeH(pc.getLawful());
		writeH(pc.getFire());
		writeH(pc.getWater());
		writeH(pc.getWind());
		writeH(pc.getEarth());
		writeD(pc.getKillCount());
	}

	/**
	 * 角色資訊 測試用
	 * 
	 * @param pc 測試GM
	 * @param str 力量
	 */
	public S_OwnCharStatus(final L1PcInstance pc, final int str) {
		int time = L1GameTimeClock.getInstance().currentTime().getSeconds();
		time = time - (time % 300);

		writeC(S_OPCODE_OWNCHARSTATUS);
		writeD(pc.getId());

		writeC(pc.getLevel());

		writeExp(pc.getExp());

		writeC(str);
		writeC(pc.getInt());
		writeC(pc.getWis());
		writeC(pc.getDex());
		writeC(pc.getCon());
		writeC(pc.getCha());
		writeH(pc.getCurrentHp());
		writeH(pc.getMaxHp());
		writeH(pc.getCurrentMp());
		writeH(pc.getMaxMp());
		writeC(pc.getAc());
		writeD(time);
		writeC(pc.get_food());
		writeC(pc.getInventory().getWeight240());
		writeH(pc.getLawful());
		writeC(pc.getFire());
		writeC(pc.getWater());
		writeC(pc.getWind());
		writeC(pc.getEarth());
		writeD(pc.getKillCount());
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public byte[] getContentBIG5() { //20240901
		if (_byte == null) {
			_byte = _bao3.toByteArray();
		}
		return _byte;
	}
	
	@Override
	public byte[] getContentGBK() { //20240901
		if (_byte == null) {
			_byte = _bao5.toByteArray();
		}
		return _byte;
	}
}