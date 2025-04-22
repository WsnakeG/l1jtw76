package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1NpcTalkData;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.templates.L1Item;

/**
 * NPC對話視窗
 * 
 * @author dexc
 */
public class S_NPCTalkReturn extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 可交換物件清單
	 * 
	 * @param objid
	 * @param htmlid
	 * @param pc
	 * @param list
	 */
	public S_NPCTalkReturn(final int objid, final String htmlid, final L1PcInstance pc,
			final List<Integer> list) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS(htmlid);
		writeH(0x01);
		writeH(0x0b); // 11 數量
		int t = 0;
		for (final Integer v : list) {
			t++;
			final L1Item datum = ItemTable.get().getTemplate(v);
			pc.get_otherList().add_sitemList2(t, v);
			writeS(datum.getNameId());
		}
		if (list.size() < 11) {
			final int x = 11 - list.size();
			for (int i = 0; i < x; i++) {
				writeS(" ");
			}
		}
	}

	/**
	 * NPC對話視窗
	 * 
	 * @param objid
	 * @param htmlid
	 * @param data List
	 * @param pc
	 */
	public S_NPCTalkReturn(final int objid, final String htmlid, final List<L1ItemInstance> list,
			final L1PcInstance pc) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS(htmlid);
		writeH(0x01);
		writeH(0x0b); // 數量
		int t = 0;
		for (final L1ItemInstance datum : list) {
			t++;
			pc.get_otherList().add_sitemList(t, datum);
			writeS(datum.getViewName());
		}
		if (list.size() < 11) {
			final int x = 11 - list.size();
			for (int i = 0; i < x; i++) {
				writeS(" ");
			}
		}
	}

	/**
	 * NPC對話視窗
	 * 
	 * @param npc
	 * @param objid
	 * @param action
	 * @param data
	 */
	public S_NPCTalkReturn(final L1NpcTalkData npc, final int objid, final int action, final String[] data) {

		String htmlid = "";

		if (action == 1) {
			htmlid = npc.getNormalAction();

		} else if (action == 2) {
			htmlid = npc.getCaoticAction();

		} else {
			throw new IllegalArgumentException();
		}

		buildPacket(objid, htmlid, data);
	}

	/**
	 * NPC對話視窗
	 * 
	 * @param npc
	 * @param objid
	 * @param action
	 */
	public S_NPCTalkReturn(final L1NpcTalkData npc, final int objid, final int action) {
		this(npc, objid, action, null);
	}

	/**
	 * NPC對話視窗
	 * 
	 * @param objid
	 * @param htmlid
	 * @param data
	 */
	public S_NPCTalkReturn(final int objid, final String htmlid, final String[] data) {
		buildPacket(objid, htmlid, data);
	}

	/**
	 * NPC對話視窗
	 * 
	 * @param objid
	 * @param htmlid
	 */
	public S_NPCTalkReturn(final int objid, final String htmlid) {
		buildPacket(objid, htmlid, null);
	}

	/**
	 * NPC對話視窗
	 * 
	 * @param objid
	 * @param html
	 */
	public S_NPCTalkReturn(final int objid, final L1NpcHtml html) {
		buildPacket(objid, html.getName(), html.getArgs());
	}

	private void buildPacket(final int objid, final String htmlid, final String[] data) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS(htmlid);
		if ((data != null) && (1 <= data.length)) {
			writeH(0x01); // 不明バイト 分かる人居たら修正願います
			writeH(data.length); // 數量
			for (final String datum : data) {
				writeS(datum);
			}

		} else {
			writeH(0x00);
			writeH(0x00);
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
