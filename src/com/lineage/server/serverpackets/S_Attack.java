package com.lineage.server.serverpackets;

import java.util.concurrent.atomic.AtomicInteger;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1PcInstance;


/**
 * 物件攻擊
 * @author admin
 *
 */
public class S_Attack extends ServerBasePacket
{
	private static AtomicInteger aInteger = new AtomicInteger(0);
	
	private byte[] _byte = null;
	
	/*
	 * [Length:32] S -> C [S_Attack.java] <Skills>
	 * 0000    18 12 4A 17 49 01 C2 24 00 00 16 02 D1 9F 49 01    ..J.I..$......I.
	 * 0010    A7 00 06 36 80 F5 7F 37 80 F5 7F 00 00 00 F4 EB    ...6..7.......
	 * 
	 * [Length:32] S -> C [S_Attack.java] <Bow>
	 * 0000    18 01 15 23 E4 00 00 00 00 00 00 00 3A 01 00 00    ...#........:...
	 * 0010    42 00 00 3F 81 65 82 40 81 61 82 00 00 00 D5 65    B..?.e.@.a.....e
	 * 
	 * [Length:24] S -> C [S_Attack.java] <Normal>
	 * 0000    18 01 E1 AF A1 00 00 00 00 00 00 05 00 00 00 00    ................
	 * 0010    00 26 78 0A 00 06 08 00                            .&x.....
	 * 
	 */
	
	public S_Attack(L1PcInstance pc, int objid, int type,
			int attacktype, int gfx) {
		writeC(S_OPCODE_ATTACKPACKET);
		writeC(type);
		writeD(pc.getId());
		writeD(objid);
		writeC(0x01); // damage
		writeC(0x00);
		writeC(pc.getHeading());
		writeH(0x0000); // target x
		writeH(0x0000); // target y
		writeC(attacktype); // 0:none 2:크로우 4:이도류 0x08:CounterMirror
		writeH(gfx);
		writeH(0x00);
		writeH(0x00);
	}
	
	/**
	 * 物件攻擊
	 * @param src
	 * @param dst
	 * data0, 攻擊動作
	 * data1, 傷害值
	 * data2, 輸出動畫 (無動畫 設為-1)
	 * data3, 特殊效果
	 */
	public S_Attack(final L1Character src, final L1Character dst, final int[] data) {
		writeC(S_OPCODE_ATTACKPACKET); // 封包位址
		if (src.getTempCharGfx() == 816) {// 妖魔城堡塔上弓箭手
			writeC(0x15); // 來源物件之攻擊動作
		} else {
			writeC(data[0]); // 來源物件之攻擊動作
		}
		writeD(src.getId()); // 來源物件編號
		if (dst == null) {// 目標為空視為空擊
			writeD(0x00000000);
		} else {
			writeD(dst.getId()); // 目標物件編號
		}		
		writeH(data[1]); // 來源物件之傷害
		writeC(src.getHeading()); // 來源物件之面向
		if (data[2] != -1) {
			ShowGfx(src, dst, data[2]); // 來源物件之輸出動畫
			return;
		}		
		writeD(0x00000000); // 區分用的數值
		writeC(data[3]); // 來源物件之輸出特別效果 [0x00,無效果 0x02,爪痕 0x04,雙擊 0x08,鏡反射]
	}
	
	// 肯特城牆弓箭手攻擊數據包
	//======================================================================
	//[Server] opcode = 7
	//0000: 07 01 52 8d 00 00 db 9b 77 00 00 00 04 50 29 00    ..R.....w....P).
	//0010: 00 42 00 00 59 81 f8 7f 57 81 02 80 00 00 00 21    .B..Y..W......!
	/*======================================================================
	//[Server] opcode = 7
	//0000: 07 01 52 8d 00 00 db 9b 77 00 00 00 04 50 29 00    ..R.....w....P).
	//0010: 00 42 00 00 59 81 f8 7f 57 81 02 80 00 00 00 de    .B..Y..W.......
	//======================================================================
	//[Server] opcode = 7
	//0000: 07 01 52 8d 00 00 db 9b 77 00 00 00 04 50 29 00    ..R.....w....P).
	//0010: 00 42 00 00 59 81 f8 7f 57 81 02 80 00 00 00 7d    .B..Y..W......}
	//======================================================================
	//[Server] opcode = 7
	//0000: 07 01 52 8d 00 00 db 9b 77 00 00 00 04 50 29 00    ..R.....w....P).
	//0010: 00 42 00 00 59 81 f8 7f 57 81 02 80 00 00 00 43    .B..Y..W......C
	//======================================================================
	//[Server] opcode = 7
	//0000: 07 01 52 8d 00 00 db 9b 77 00 00 00 04 50 29 00    ..R.....w....P).
	//0010: 00 42 00 00 59 81 f8 7f 57 81 02 80 00 00 00 b8    .B..Y..W.......
	//======================================================================
	//[Server] opcode = 7
	//0000: 07 01 52 8d 00 00 db 9b 77 00 00 00 04 50 29 00    ..R.....w....P)
	//0010: 00 42 00 00 59 81 f8 7f 57 81 02 80 00 00 00 df    .B..Y..W.......
	//======================================================================
	//[Server] opcode = 7
	//0000: 07 01 52 8d 00 00 db 9b 77 00 00 00 04 50 29 00    ..R.....w....P).
	//0010: 00 42 00 00 59 81 f8 7f 57 81 02 80 00 00 00 16    .B..Y..W.......
	//======================================================================*/
	private void ShowGfx(final L1Character src, final L1Character dst, final int outGfx) {
		if (outGfx == -1) {
			return;
		}
		// 以原子方式将当前值加 1。
		writeD(aInteger.incrementAndGet());
		writeH(outGfx);// 遠程動畫編號
		writeC(0x00); // use_type 0:弓箭 6:遠距離魔法 8:遠距離範圍魔法
			
		// 城牆弓手修改(尚需要確認)
		int x = src.getX();
		int y = src.getY();
		int gfx = src.getTempCharGfx();
		if (gfx == 267 || gfx == 3689) {
			x += 5;
			y -= 5;
		} else if (gfx == 816) {
			x += 3;
			y -= 3;
		}
			
		writeH(x);// 來源物件X座標
		writeH(y);// 來源物件Y座標
		writeH(dst.getX());// 目標物件X座標
		writeH(dst.getY());// 目標物件X座標
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
	}
	
	/**
	 * 物件攻擊(遠程不具有目標)
	 * @param src
	 * @param dst
	 * data0, 攻擊動作
	 * data1, 傷害值
	 * data2, 輸出動畫 (無動畫 設為-1)
	 * data3, 特殊效果
	 */
	public S_Attack(final L1Character src, final int[] data) {
		writeC(S_OPCODE_ATTACKPACKET); // 封包位址
		writeC(data[0]); // 來源物件之攻擊動作
		writeD(src.getId()); // 來源物件編號
		writeD(0x00000000);// 目標為空視為空擊				
		writeH(data[1]); // 來源物件之傷害
		writeC(src.getHeading()); // 來源物件之面向
		if (data[2] != -1) {
			showGfxNull(src, data[2], data[4], data[5]); // 來源物件之輸出動畫
			return;
		}		
		writeD(0x00000000); // 區分用的數值
		writeC(data[3]); // 來源物件之輸出特別效果 [0x00,無效果 0x02,爪痕 0x04,雙擊 0x08,鏡反射]
	}
	
	private void showGfxNull(final L1Character src, final int outGfx, final int x, final int y) {
		if (outGfx == -1) {
			return;
		}
		// 以原子方式将当前值加 1。
		writeD(aInteger.incrementAndGet());
		writeH(outGfx);// 遠程動畫編號
		writeC(0x00); // use_type 0:弓箭 6:遠距離魔法 8:遠距離範圍魔法			
		writeH(src.getX());// 來源物件X座標
		writeH(src.getY());// 來源物件Y座標
		writeH(x);// 目標物件X座標
		writeH(y);// 目標物件X座標
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
	}
	
	/**
	 * 反擊屏障攻擊MISS封包(PC)
	 * @param src 被反者
	 * @param dst 反人者
	 */
	public S_Attack(final L1Character src, final L1Character dst) {
		writeC(S_OPCODE_ATTACKPACKET); // 封包位址
		writeC(0x01); // 被反者之攻擊動作
		writeD(src.getId()); // 被反者物件編號
		writeD(dst.getId()); // 反人者物件編號
		writeH(0x00); // 反人者之傷害值
		writeC(src.getHeading()); // 被反者物件之面向		
		writeD(0x00000000); // 區分用的數值
		writeC(0x00);// 來源物件之輸出特別效果 [0x00,無效果 0x02,爪痕 0x04,雙擊 0x08,鏡反射]
	}
	
	/**
	 * 反擊屏障攻擊MISS封包(NPC)
	 * @param attacker
	 * @param targetId
	 * @param actId
	 */
	public S_Attack(final L1Character attacker, final int targetId, final int actId) {
		writeC(S_OPCODE_ATTACKPACKET);
		writeC(actId);
		writeD(attacker.getId());
		writeD(targetId);
		writeC(0x00);
		writeC(attacker.getHeading());
		writeD(0x00000000);
		writeC(0x00);
	}
	
	

	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
	}
	
	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName() + " [S->C 發送物件攻擊]";
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
