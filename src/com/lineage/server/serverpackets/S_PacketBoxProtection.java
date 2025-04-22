package com.lineage.server.serverpackets;

/**
 * 戰鬥特化
 * 
 * @author LoLi
 */
public class S_PacketBoxProtection extends ServerBasePacket {

	private byte[] _byte = null;

	// 正義的守護 Lv.1 善惡值 10,000 ~ 19,999 (防御：-2 / 魔防+3)
	public static final int JUSTICE_L1 = 0;

	// 正義的守護 Lv.2 善惡值 20,000 ~ 29,999 (防御：-4 / 魔防+6)
	public static final int JUSTICE_L2 = 1;

	// 正義的守護 Lv.3 善惡值 30,000 ~ 32,767 (防御：-6 / 魔防+9)
	public static final int JUSTICE_L3 = 2;

	// 邪惡的守護 Lv.1 善惡值 -10,000 ~ -19,999 (近/遠距離攻擊力+1 / 魔攻+1)
	public static final int EVIL_L1 = 3;

	// 邪惡的守護 Lv.2 善惡值 -20,000 ~ -29,999 (近/遠距離攻擊力+3 / 魔攻+2)
	public static final int EVIL_L2 = 4;

	// 邪惡的守護 Lv.3 善惡值 -30,000 ~ -32,767 (近/遠距離攻擊力+5 / 魔攻+3)
	public static final int EVIL_L3 = 5;

	// 遭遇的守護 20級以下角色 被超過10級以上的玩家攻擊而死亡時，不會失去經驗值，也不會掉落物品
	public static final int ENCOUNTER = 6;

	/**
	 * 戰鬥特化
	 * 
	 * @param model
	 * @param exp 0:啟用 1:關閉
	 */
	public S_PacketBoxProtection(final int model, final int type) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x72);
		writeD(model);
		writeD(type);
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
		return getClass().getSimpleName();
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
