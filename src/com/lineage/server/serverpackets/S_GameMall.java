package com.lineage.server.serverpackets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.T_GameMallModel;

/**
 * 商城清單
 * 
 * @author chung
 */
public class S_GameMall extends ServerBasePacket {

	private byte[] _byte = null;

	public S_GameMall(final HashMap<Integer, T_GameMallModel> BuyList, final int maxPagination,
			final int Pagination) {
		writeC(S_OPCODE_CHARRESET);
		writeC(36);
		writeC(1);
		writeC(maxPagination);
		writeC(Pagination);
		writeH(BuyList.size());
		T_GameMallModel gameMallModel = null;
		L1ItemInstance item = null;
		final Set<Integer> keys = BuyList.keySet();

		for (final Integer id : keys) {
			gameMallModel = BuyList.get(id);
			item = gameMallModel.getMallItem();
			writeD(id.intValue());
			writeH(item.getItem().getGfxId());
			writeH(gameMallModel.getShopItemDesc());
			writeC(0);
			writeD(gameMallModel.getPrice());
			writeS(item.getLogName());

			if (item.getItem().getType2() == 0) {
				writeC(23);
				writeC(item.getItem().getMaterial());
				if (item.getWeight() > 0) {
					final int weigth = item.getItem().getWeight() / 1000;
					writeC(weigth <= 0 ? 1 : weigth);
				}
			} else if (item.getItem().getType2() == 1) {
				writeC(1);
				writeC(item.getItem().getDmgSmall());
				writeC(item.getItem().getDmgLarge());
				writeC(item.getItem().getMaterial());
				writeC(257);
			} else {
				writeC(19);
				int ac = item.getItem().get_ac();
				if (ac != 0) {
					if (ac < 0) {
						ac = ac - ac - ac;
					}
					writeC(ac);
					writeC(item.getItem().getMaterial());
					if (item.getWeight() > 0) {
						final int weigth = item.getItem().getWeight() / 1000;
						writeC(weigth <= 0 ? 1 : weigth);
					}
				}
			}
			writeC(0);
			writeC(gameMallModel.getSort());
			writeC(gameMallModel.isNewItem() ? 1 : 0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeC(gameMallModel.getVipLevel());
			writeC(gameMallModel.isHotItem() ? 1 : 0);
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
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