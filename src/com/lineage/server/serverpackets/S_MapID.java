package com.lineage.server.serverpackets;

import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.WorldQuest;

/**
 * 更新角色所在的地圖
 * 
 * @author dexc
 */
public class S_MapID extends ServerBasePacket {
	private byte[] _byte = null;
	/**
	 * 更新角色所在的地圖
	 * 
	 * @param pc 更新角色
	 * @param mapid 地圖編號
	 * @param isUnderwater 是否在水裏
	 */
	public S_MapID(final L1PcInstance pc, final int mapid, final boolean isUnderwater) {
		// 副本地圖中判斷
		if (QuestMapTable.get().isQuestMap(pc.getMapId())) {
			// 是副本專用地圖

		} else {// 離開副本地圖
			// 正在參加副本
			if (pc.get_showId() != -1) {
				// 副本編號 是執行中副本
				if (WorldQuest.get().isQuest(pc.get_showId())) {
					// 移出副本
					WorldQuest.get().remove(pc.get_showId(), pc);
				}
			}
			// 重置副本編號
			pc.set_showId(-1);
		}

		// 0000: 20 00 50 00 10 f8 00 00 00 00 00 00 10 0d 35 c5 .P...........5.
		writeC(S_OPCODE_MAPID);
		writeH(mapid);
		writeC(isUnderwater ? 0x01 : 0x00);

		/*
		 * this.writeC(0x10); this.writeH(0x00f8); this.writeC(0x00);
		 * this.writeC(0x00); this.writeC(0x00); this.writeC(0x00);
		 * this.writeC(0x00); this.writeC(0x10); this.writeC(0x0d);
		 * this.writeC(0x35); this.writeC(0xc5);
		 */
	}

	/**
	 * GM 移動專用
	 * 
	 * @param mapid
	 */
	public S_MapID(final int mapid) {
		// 0000: 20 00 50 00 10 f8 00 00 00 00 00 00 10 0d 35 c5 .P...........5.
		// System.out.println("GM 移動專用 MAPID:"+mapid);
		writeC(S_OPCODE_MAPID);
		writeH(mapid);
		writeC(0x00);

		writeC(0x10);
		writeH(0x00f8);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x10);
		writeC(0x0d);
		writeC(0x35);
		writeC(0xc5);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
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
