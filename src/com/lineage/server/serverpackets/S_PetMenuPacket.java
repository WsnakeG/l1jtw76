package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;

/**
 * NPC對話視窗
 * 
 * @author dexc
 */
public class S_PetMenuPacket extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC對話視窗
	 * 
	 * @param npc
	 * @param exppercet
	 */
	public S_PetMenuPacket(final L1NpcInstance npc, final int exppercet) {
		buildpacket(npc, exppercet);
	}

	private void buildpacket(final L1NpcInstance npc, final int exppercet) {
		writeC(S_OPCODE_SHOWHTML);

		if (npc instanceof L1PetInstance) { // 寵物
			final L1PetInstance pet = (L1PetInstance) npc;
			writeD(pet.getId());
			writeS("anicom");
			writeC(0x00);
			writeH(0x0a);
			switch (pet.getCurrentPetStatus()) {
			case 1:
				writeS("$469"); // 攻撃態勢
				break;
			case 2:
				writeS("$470"); // 防御態勢
				break;
			case 3:
				writeS("$471"); // 休憩
				break;
			case 5:
				writeS("$472"); // 警戒
				break;
			default:
				writeS("$471"); // 休憩
				break;
			}
			writeS(Integer.toString(pet.getCurrentHp())); // 目前HP
			writeS(Integer.toString(pet.getMaxHp())); // 最大HP
			writeS(Integer.toString(pet.getCurrentMp())); // 目前MP
			writeS(Integer.toString(pet.getMaxMp())); // 最大MP
			writeS(Integer.toString(pet.getLevel())); // 等級
			writeS(pet.getName());
			writeS("$611"); // 飽食度
			writeS(Integer.toString(exppercet)); // 經驗值%
			writeS(Integer.toString(pet.getLawful())); // 善惡值
			// this.writeS("-20"); // 善惡值

		} else if (npc instanceof L1SummonInstance) { // 召喚獸
			final L1SummonInstance summon = (L1SummonInstance) npc;
			writeD(summon.getId());
			writeS("moncom");
			writeC(0x00);
			writeH(0x06); // 渡す引数文字の数の模様
			switch (summon.get_currentPetStatus()) {
			case 1:
				writeS("$469"); // 攻撃態勢
				break;
			case 2:
				writeS("$470"); // 防御態勢
				break;
			case 3:
				writeS("$471"); // 休憩
				break;
			case 5:
				writeS("$472"); // 警戒
				break;
			default:
				writeS("$471"); // 休憩
				break;
			}
			writeS(Integer.toString(summon.getCurrentHp())); // 目前HP
			writeS(Integer.toString(summon.getMaxHp())); // 最大HP
			writeS(Integer.toString(summon.getCurrentMp())); // 目前MP
			writeS(Integer.toString(summon.getMaxMp())); // 最大MP
			writeS(Integer.toString(summon.getLevel())); // 等級
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
