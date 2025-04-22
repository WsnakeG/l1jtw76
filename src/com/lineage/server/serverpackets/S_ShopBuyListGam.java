package com.lineage.server.serverpackets;

import java.util.Map;

import com.lineage.data.event.GamblingSet;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Gambling;

/**
 * 賣
 * 
 * @author dexc
 */
public class S_ShopBuyListGam extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 賣中獎的票
	 * 
	 * @param pc
	 * @param npc
	 * @param sellList
	 */
	public S_ShopBuyListGam(final L1PcInstance pc, final L1NpcInstance npc,
			final Map<Integer, L1Gambling> sellList) {
		writeC(S_OPCODE_SHOWSHOPSELLLIST);
		writeD(npc.getId());

		if (sellList.isEmpty()) {
			writeH(0x0000);
			return;
		}

		if (sellList.size() <= 0) {
			writeH(0x0000);
			return;
		}

		writeH(sellList.size());

		for (final Integer itemobjid : sellList.keySet()) {
			writeD(itemobjid);
			final L1Gambling gam = sellList.get(itemobjid);
			final int adena = (int) (GamblingSet.GAMADENA * gam.get_rate());
			writeD(adena);
		}
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