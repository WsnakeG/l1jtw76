package com.lineage.server.serverpackets;

import java.util.Collection;

import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1MapsLimitTime;

/**
 * @author terry0412
 */
public class S_PacketBoxMapTimer extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PacketBoxMapTimer(final L1PcInstance pc) {
		final Collection<L1MapsLimitTime> mapLimitList = MapsGroupTable.get().getGroupMaps().values();

		writeC(S_OPCODE_PACKETBOX);
		writeC(0x9f);//159
		writeD(mapLimitList.size());

		for (final L1MapsLimitTime mapLimit : mapLimitList) {
			final int order_id = mapLimit.getOrderId();
			final int used_time = pc.getMapsTime(order_id);
			final int time_str = (mapLimit.getLimitTime() - used_time) / 60;
			// write
			writeD(order_id);
			writeS(mapLimit.getMapName());
			writeD(time_str);
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
