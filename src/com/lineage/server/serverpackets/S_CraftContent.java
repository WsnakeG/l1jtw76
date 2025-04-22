package com.lineage.server.serverpackets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.T_CraftConfigTable.NewL1NpcMakeItemAction;
import com.lineage.server.model.L1ObjectAmount;
import com.lineage.server.model.Instance.L1ItemInstance;

public class S_CraftContent extends ServerBasePacket {

	private byte[] _byte = null;

	private static final Log _log = LogFactory.getLog(S_CraftContent.class);

	private static final byte[] cp = { 8, 0, 16, 0 };

	private static final byte[] cq = { 8, 1, 16, 0 };

	private static final byte[] co = { 8, 0, 16, 0 };

	private static final byte[] cn = { 8, -1, -1, -1, -1, 15, 16, -1, -1, -1, -1, 15 };

	public S_CraftContent() {
	}

	public S_CraftContent(final NewL1NpcMakeItemAction npcMakeItemAction) {
		try {
			a(18, makeaction(npcMakeItemAction));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private byte[] makeaction(final NewL1NpcMakeItemAction makeItemAction) throws IOException {
		final S_CraftContent bao = new S_CraftContent();
		bao.a(8, (makeItemAction.getAmountActionID()));
		bao.a(18, jdMethod_new(makeItemAction));
		bao.a(24, 0L);
		bao.a(34, jdMethod_do(makeItemAction));
		bao.a(42, jdMethod_int(makeItemAction));
		bao.a(50, jdMethod_for(makeItemAction));
		bao.a(58, jdMethod_if(makeItemAction));
		bao.a(66, jdMethod_try(makeItemAction));
		return bao.getContent();
	}

	public byte[] jdMethod_new(final NewL1NpcMakeItemAction makeItemAction) throws IOException {
		final S_CraftContent bao = new S_CraftContent();
		bao.a(8, (makeItemAction.getAmountNameId()));
		bao.a(16, (makeItemAction.getAmountMinLevel()));
		bao.a(24, (makeItemAction.getAmountMaxLevel()));
		bao.a(32, 2L);
		bao.a(40, (makeItemAction.getAmountMinLawful()));
		bao.a(48, (makeItemAction.getAmountMaxLawful()));
		bao.a(56, (makeItemAction.getAmountMinKarma()));
		bao.a(64, (makeItemAction.getAmountMaxKarma()));
		bao.a(72, (makeItemAction.getOnceChangeCount()));
		return bao.getContent();
	}

	public byte[] jdMethod_do(final NewL1NpcMakeItemAction makeItemAction) throws IOException {
		final S_CraftContent bao = new S_CraftContent();
		bao.a(8, 1L);
		bao.a(16, 0L);
		bao.a(26, cp);
		return bao.getContent();
	}

	public byte[] jdMethod_int(final NewL1NpcMakeItemAction makeItemAction) throws IOException {
		final S_CraftContent bao = new S_CraftContent();
		final ArrayList<Integer> polyIds = makeItemAction.getCraftPolyList();
		if (polyIds != null) {
			final int length = makeItemAction.getCraftPolyList().size();
			bao.a(8, (length));
			for (final Integer polyId : polyIds) {
				bao.a(16, (polyId.intValue()));
			}
		} else {
			bao.a(8, 0L);
		}
		return bao.getContent();
	}

	public byte[] jdMethod_for(final NewL1NpcMakeItemAction makeItemAction) throws IOException {
		return cq;
	}

	public byte[] jdMethod_if(final NewL1NpcMakeItemAction makeItemAction) throws IOException {
		final S_CraftContent bao = new S_CraftContent();

		final List<L1ObjectAmount<Integer>> materials = makeItemAction.getAmountMeterialList();
		if ((materials == null) || (materials.size() <= 0)) {
			throw new IOException("材料列表至少需要一個.");
		}
		List<L1ObjectAmount<Integer>> substituteMaterials = null;
		int index = 1;
		L1ObjectAmount<Integer> substituteObjectAmount;
		for (final L1ObjectAmount<Integer> objectAmount : materials) {
			bao.a(10, a(index, objectAmount));

			substituteMaterials = objectAmount.getAmountList();
			if (substituteMaterials != null) {
				for (final Iterator<L1ObjectAmount<Integer>> localIterator2 = substituteMaterials
						.iterator(); localIterator2.hasNext();) {
					substituteObjectAmount = localIterator2.next();
					bao.a(10, a(index, substituteObjectAmount));
				}
			}
			index++;
		}

		final List<L1ObjectAmount<Integer>> aidMaterials = makeItemAction.getAmountAidMeterialList();
		for (final L1ObjectAmount<Integer> objectAmount : aidMaterials) {
			bao.a(18, a(index, objectAmount));

			substituteMaterials = objectAmount.getAmountList();
			if (substituteMaterials != null) {
				for (final L1ObjectAmount<Integer> substituteObjectAmount2 : substituteMaterials) {
					bao.a(18, a(index, substituteObjectAmount2));
				}
			}
			index++;
		}

		return bao.getContent();
	}

	public byte[] a(final int index, final L1ObjectAmount<Integer> objectAmount) throws IOException {
		final S_CraftContent bao = new S_CraftContent();
		final int itemId = objectAmount.getObject().intValue();
		final L1ItemInstance item = ItemTable.get().createItem(itemId, false);
		if (item != null) {
			bao.a(8, (item.getItem().getItemDescId()));
			bao.a(16, (objectAmount.getAmount()));
			bao.a(24, (index));
			bao.a(32, (objectAmount.getAmountEnchantLevel()));
			bao.a(40, (objectAmount.getAmountBless()));
			bao.as(50, item.getLogName());
			bao.a(56, (item.get_gfxid()));
		} else {
			bao.a(8, 0L);
			bao.a(16, 0L);
			bao.a(24, (index));
			bao.a(32, 0L);
			bao.a(40, 1L);
			bao.as(50, null);
			bao.a(56, 0L);
		}
		return bao.getContent();
	}

	public byte[] jdMethod_try(final NewL1NpcMakeItemAction makeItemAction) throws IOException {
		final S_CraftContent bao = new S_CraftContent();
		bao.a(10, a(makeItemAction.getAmountRandomItemList(), makeItemAction.getAmountItemList()));
		bao.a(18, a(makeItemAction.getFailAmountRandomItemList(), makeItemAction.getFailItemList()));
		bao.a(24, (makeItemAction.getSucceedRandom()));
		return bao.getContent();
	}

	public byte[] a(final List<L1ObjectAmount<Integer>> randomItems,
			final List<L1ObjectAmount<Integer>> items) throws IOException {
		final S_CraftContent bao = new S_CraftContent();
		bao.a(10, co);
		bao.a(18, cn);
		bao.a(24, (randomItems.size()));
		bao.a(32, (items.size()));
		for (final L1ObjectAmount<Integer> randomObjectAmount : randomItems) {
			bao.a(42, a(randomObjectAmount));
		}
		for (final L1ObjectAmount<Integer> itemObjectAmount : items) {
			bao.a(50, a(itemObjectAmount));
		}
		bao.a(64, 0L);
		return bao.getContent();
	}

	public byte[] a(final L1ObjectAmount<Integer> objectAmount) throws IOException {
		final S_CraftContent bao = new S_CraftContent();
		final int itemId = objectAmount.getObject().intValue();
		final L1ItemInstance item = ItemTable.get().createItem(itemId, false);
		if (item != null) {
			bao.a(8, (item.getItem().getItemDescId()));
			bao.a(16, (objectAmount.getAmount()));
			bao.a(24, -1L);
			bao.a(32, (objectAmount.getAmountEnchantLevel()));
			bao.a(40, (objectAmount.getAmountBless()));
			bao.a(48, 0L);
			bao.a(56, 0L);
			bao.as(66, item.getLogName());
			bao.a(72, 0L);
			bao.a(80, 0L);
			bao.a(88, (item.get_gfxid()));
			bao.as(98, null);

			final ByteArrayOutputStream itemStatusByte = new ByteArrayOutputStream();
			itemStatusByte.write(item.getStatusBytes());
			for (int i = itemStatusByte.size(); i < 255; i++) {
				itemStatusByte.write(0);
			}
			bao.a(106, itemStatusByte.toByteArray());
			bao.a(112, 0L);
			bao.a(120, 0L);
		} else {
			bao.a(8, 0L);
			bao.a(16, 0L);
			bao.a(24, -1L);
			bao.a(32, 0L);
			bao.a(40, 1L);
			bao.a(48, 0L);
			bao.a(56, 1L);
			bao.as(66, null);
			bao.a(72, 0L);
			bao.a(80, 0L);
			bao.a(88, 0L);
			bao.as(98, null);
			bao.a(106, new byte[255]);
			bao.a(112, 0L);
			bao.a(120, 0L);
		}
		return bao.getContent();
	}

	public byte[] jdMethod_if(final L1ItemInstance item) throws IOException {
		a(8, (item.getItem().getItemDescId()));
		a(16, (item.getCount()));
		a(24, -1L);
		a(32, (item.getEnchantLevel()));
		a(40, (item.getBless()));
		a(48, 0L);
		a(56, 0L);
		as(66, item.getName());
		a(72, 0L);
		a(80, 0L);
		a(88, (item.get_gfxid()));
		as(98, null);
		return getContent();
	}

	public byte[] jdMethod_if(final int actionId, final int checkCount, final int useCount)
			throws IOException {
		a(8, (actionId));
		a(16, (checkCount));
		a(24, (useCount));
		return getContent();
	}

	@Override
	public byte[] getContent() {
		return _bao.toByteArray();
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