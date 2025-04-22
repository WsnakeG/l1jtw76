package com.lineage.data.item_etcitem.extra;

import java.sql.Timestamp;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1ItemBuff;

/**
 * 道具狀態系統
 * 
 */
public class ItemBuff extends ItemExecutor {
	
	/**
	 *
	 */
	private ItemBuff() {
		// TODO Auto-generated constructor stub
	}
	
    public static ItemExecutor get() {
        return new ItemBuff();
    }
    
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
//        if (!ItemBuffSet.START) {
//            pc.sendPackets(new S_ServerMessage("道具狀態系統未開啟。", 11));
//            return;
//        }
    	
    	final int itemid = item.getItem().getItemId();
    	final L1ItemBuff etcitem = ItemBuffTable.get().getItem(itemid);
        
        final Timestamp ts = new Timestamp(System.currentTimeMillis());
		// 設置使用時間
        item.setLastUsed(ts);
		pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
		pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);
        
        if (ItemBuffTable.get().add(pc, item.getItemId(), 0)) {
        	if(etcitem._deleteafteruse == true){
        		pc.getInventory().removeItem(item, 1L);
        	}
        }
    }
}
