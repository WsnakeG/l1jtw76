package com.lineage.server.serverpackets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.lineage.config.Config;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1LotteryWarehouse;
import com.lineage.server.utils.BinaryOutputStream;
import com.lineage.server.utils.RandomArrayList;

/**
 * 潘朵拉幸運抽獎倉庫清單
 * 
 * @author simlin
 */
public class S_ShowLotteryList extends ServerBasePacket {
	
	private byte[] _byte = null;

	public S_ShowLotteryList(final ArrayList<L1LotteryWarehouse> lotteryList) {
		writeC(S_OPCODE_CRAFTSYSTEM); // XXX S_OPCODE_EXTENDED_PROTOBUF 修改為 S_OPCODE_CRAFTSYSTEM
		writeH(101);
		writeC(8);
		writeC(99);
		writeC(16);
		writeC(0);

		final L1ItemInstance item = new L1ItemInstance();
		L1Item ItemTemp = null;
		for (int i = 0; i < lotteryList.size(); i++) {
			final BinaryOutputStream ops = new BinaryOutputStream();
			try {
				final L1LotteryWarehouse lottery = lotteryList.get(i);
				ItemTemp = ItemTable.get().getTemplate(lottery.getItemId());
				item.setItem(ItemTemp);

				ops.writeC(8);
				append(ops, item.getItem().getUseType());
				ops.writeC(16);
				append(ops, lottery.getItemCount());
				ops.writeC(24);
				append(ops, item.getItem().getGfxId());
				ops.writeC(32);
				append(ops, i);
				ops.writeC(42);

				final String tempName = item.getItem().getName();
				final byte[] bName = tempName.getBytes(Config.CLIENT_LANGUAGE_CODE);
				ops.writeC(bName.length);
				for (final byte b : bName) {
					ops.writeC(b);
				}

			} catch (final UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			writeC(26);
			writeC(ops.getBytes().length);
			for (final byte b : ops.getBytes()) {
				writeC(b);
			}
		}
		writeC(RandomArrayList.getInt(127));
		writeC(RandomArrayList.getInt(127));
	}

	private void append(final BinaryOutputStream byteos, long i) {
		while ((i / 128L) != 0L) {
			byteos.writeC((int) ((i % 128L) + 128L));
			i /= 128L;
		}
		byteos.writeC((int) i);
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

	@Override
	public byte[] getContent() throws IOException {
		return _bao.toByteArray();
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
