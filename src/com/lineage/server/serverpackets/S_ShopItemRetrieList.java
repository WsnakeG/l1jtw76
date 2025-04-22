package com.lineage.server.serverpackets;

import java.io.IOException;
import java.util.List;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.T_ShopWarehouseModel;

/**
 * 個人商城清單
 * 
 * @author simlin
 */
public class S_ShopItemRetrieList extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ShopItemRetrieList(final L1PcInstance pc) {
		final List<T_ShopWarehouseModel> warehouse = pc.getDwarfForGameMall().getWareHouseList();
		if ((warehouse != null) && !warehouse.isEmpty()) {
			final int size = warehouse.size();
			if (size > 0) {
				writeC(S_OPCODE_SHOWRETRIEVELIST);
				writeD(0);
				writeH(size);
				writeC(25);
				L1Item item = null;
				final L1ItemInstance dummy = new L1ItemInstance();
				T_ShopWarehouseModel shopWarehouseModel = null;
				for (int index = 0; index < size; index++) {
					shopWarehouseModel = warehouse.get(index);
					item = ItemTable.get().getTemplate(shopWarehouseModel.getItemId());
					dummy.setItem(item);
					dummy.setCount(shopWarehouseModel.getItemCount());
					writeD(index);
					writeC(item.getUseType());
					writeH(item.getGfxId());
					writeC(shopWarehouseModel.getItemBless());
					writeD(shopWarehouseModel.getItemCount());
					int statusX = 0;
					if (dummy.isIdentified()) {
						statusX |= 1;
					}
					if (!dummy.getItem().isTradable()) {
						statusX |= 2;
					}
					if (dummy.getItem().isCantDelete()) {
						statusX |= 4;
					}
					if ((dummy.getItem().get_safeenchant() < 0) || (dummy.getItem().getUseType() == -3)
							|| (dummy.getItem().getUseType() == -2)) {
						statusX |= 8;
					}
					if (dummy.getBless() >= 128) {
						statusX = 32;
						if (dummy.isIdentified()) {
							statusX |= 1;
							statusX |= 2;
							statusX |= 4;
							statusX |= 8;
						} else {
							statusX |= 2;
							statusX |= 4;
							statusX |= 8;
						}
					}
					writeC(statusX);
					writeS(dummy.getViewName());
				}
				writeD(0);
			} else {
				pc.sendPackets(new S_ServerMessage(2746));
			}
		} else {
			pc.sendPackets(new S_ServerMessage(2746));
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