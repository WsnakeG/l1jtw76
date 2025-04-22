package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.william.ItemUse;

/**
 * william ItemUse 改
 * 
 * @author roy
 */
public class Itemuse extends ItemExecutor {

	public Itemuse() {

	}

	public static ItemExecutor get() {
		return new Itemuse();
	}

	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		ItemUse.forItemUSe(pc, item);

	}

}
