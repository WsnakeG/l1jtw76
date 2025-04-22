package com.lineage.server.serverpackets;

/**
 * 狀態圖示顯示
 * 
 * @author jeLiu
 *
 */
public class S_InventoryIcon extends ServerBasePacket {

	public static final int SHOW_INVEN_BUFFICON = 110;
	
	private byte[] _byte = null;

	/**
	 * 自訂狀態圖示
	 * 
	 * @param iconid    狀態圖示編號
	 * @param on        開關true/false
	 * @param stringid  狀態圖示string.tbl訊息內容編號
	 * @param time      時間 (-1= 無限時間)
	 */
//	public S_InventoryIcon(int iconid, boolean on, int stringid, int time) {
//		writeC(S_OPCODE_EXTENDED_PROTOBUF);
//		writeH(SHOW_INVEN_BUFFICON);
//		writeC(0x08);
//		writeC(on ? 2 : 3);
//		writeC(0x10);
//		writeBit(iconid);
//		writeC(0x18);
//		writeBit(time);
//		writeC(0x20);
//		writeC(0x08);
//		writeC(0x28);
//		writeBit(iconid);
//		writeH(0x30);
//		writeC(0x38);
//		writeC(0x03);
//		writeC(0x40);
//		writeBit(stringid);
//		writeC(0x48);
//		writeC(0x00);
//		writeH(0x0050);
//		writeC(0x58);
//		writeC(0x01);
//		writeC(0x60);
//		writeC(0x00);
//		writeC(0x68);
//		writeC(0x00);
//		writeC(0x70);
//		writeC(0x00);
//		writeH(0x00);
//	}
	
	/**
	 * 自訂狀態圖示
	 * 
	 * @param iconid    狀態圖示編號
	 * @param on        開關true/false
	 * @param stringid  狀態圖示string.tbl訊息內容編號
	 * @param time      時間 (-1= 無限時間)
	 */
	public S_InventoryIcon(int iconid, boolean on, int stringid, int time) {
//		writeC(S_OPCODE_EXTENDED_PROTOBUF);
		writeC(S_OPCODE_CRAFTSYSTEM);
		writeH(SHOW_INVEN_BUFFICON);
		writeC(0x08);
		writeC(on ? 2 : 3);
		writeC(0x10);
		writeBit(iconid);
		writeC(0x18);
		writeBit(time);
		writeC(0x20);
		writeC(0x08);
		writeC(0x28);
		writeBit(iconid);
		writeH(0x30);
		writeC(0x38);
		writeC(0x03);
		writeC(0x40);
		writeBit(stringid);
		writeC(0x48);
		writeC(0x00);
		writeH(0x0050);
		writeC(0x58);
		writeC(0x01);
		writeC(0x60);
		writeC(0x00);
		writeC(0x68);
		writeC(0x00);
		writeC(0x70);
		writeC(0x00);
		writeH(0x00);
	}
	
	

	/**
	 * 自訂狀態圖示
	 * 
	 * @param iconid         狀態圖示編號
	 * @param on             開關true/false
	 * @param stringid       狀態圖示string.tbl訊息內容編號
	 * @param loginstringid  狀態圖示string.tbl訊息內容編號 重登時會有的訊息
	 * @param time           時間 (-1= 無限時間)
	 */
//	public S_InventoryIcon(int iconid, boolean on, int stringid, int loginstringid, int time) {
//		writeC(S_OPCODE_EXTENDED_PROTOBUF);
//		writeH(SHOW_INVEN_BUFFICON);
//		writeC(0x08);
//		writeC(on ? 2 : 3);
//		writeC(0x10);
//		writeBit(iconid);
//		writeC(0x18);
//		writeBit(time);
//		writeC(0x20);
//		writeC(0x08);
//		writeC(0x28);
//		writeBit(iconid);
//		writeH(0x30);
//		writeC(0x38);
//		writeC(0x03);
//		writeC(0x40);
//		writeBit(stringid);
//		writeC(0x48);
//		//writeC(0x00);
//		writeBit(loginstringid);
//		writeH(0x0050);
//		writeC(0x58);
//		writeC(0x01);
//		writeC(0x60);
//		writeC(0x00);
//		writeC(0x68);
//		writeC(0x00);
//		writeC(0x70);
//		writeC(0x00);
//		writeH(0x00);
//	}
	
	public S_InventoryIcon(int iconid, boolean on, int stringid, int loginstringid, int time) {
//		writeC(S_OPCODE_EXTENDED_PROTOBUF);
		writeC(S_OPCODE_CRAFTSYSTEM);
		writeH(SHOW_INVEN_BUFFICON);
		writeC(0x08);
		writeC(on ? 2 : 3);
		writeC(0x10);
		writeBit(iconid);
		writeC(0x18);
		writeBit(time);
		writeC(0x20);
		writeC(0x08);
		writeC(0x28);
		writeBit(iconid);
		writeH(0x30);
		writeC(0x38);
		writeC(0x03);
		writeC(0x40);
		writeBit(stringid);
		writeC(0x48);
		//writeC(0x00);
		writeBit(loginstringid);
		writeH(0x0050);
		writeC(0x58);
		writeC(0x01);
		writeC(0x60);
		writeC(0x00);
		writeC(0x68);
		writeC(0x00);
		writeC(0x70);
		writeC(0x00);
		writeH(0x00);
	}

	/**
	 * 自訂狀態圖示
	 * 
	 * @param boxid        ----> 常駐型狀態圖示編號-700開始可自設使用
	 * @param time         ----> 時間 (-1= 無限時間)
	 * @param o            ----> 應該是相同圖示編號時的類別 1-127有效
	 * @param iconid       ----> 狀態圖示編號
	 * @param stringid     ----> 狀態圖示string.tbl訊息內容編號
	 */
    public S_InventoryIcon(int boxid, int time, int o, int iconid, int stringid) {
//		writeC(S_OPCODE_EXTENDED_PROTOBUF);
    	writeC(S_OPCODE_CRAFTSYSTEM);
    	writeC(SHOW_INVEN_BUFFICON);
    	writeC(0x00);
    	writeC(0x08);
    	writeC(0x02);
    	writeC(0x10);
    	write7B(boxid);
    	writeC(0x18);
    	write7B(time);
    	writeC(0x20);
    	writeC(0x09);
    	writeC(0x28);
    	write7B(iconid);
    	writeC(0x30);
    	writeC(0x00);
    	writeC(0x38);
    	writeC(o);
    	writeC(0x40);
    	write7B(stringid);
    	writeC(0x48);
    	writeC(0x00);
    	writeC(0x50);
    	writeC(0x00);
    	writeC(0x58);
    	writeC(0x01);
    	writeH(0);
    }

    /**
     * 未測試
     * 
     * @param type
     * @param time
     * @param invgfx
     * @param desc
     */
	public S_InventoryIcon(int type, int time, int invgfx, int desc) {
//		writeC(S_OPCODE_EXTENDED_PROTOBUF);
		writeC(S_OPCODE_CRAFTSYSTEM);
		writeH(SHOW_INVEN_BUFFICON);
		writeC(0x08);
		writeC(0x02);
		writeC(0x10);
		writeH(type);
		writeC(0x18);
		writeBit(time);
		writeC(0x28);
		writeBit(invgfx);
		writeC(0x40);
		writeBit(desc);
		writeH(0x00);
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
