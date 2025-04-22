package com.lineage.server.serverpackets.ability;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 純能力值詳細加成資訊
 * @author kyo
 *
 */
public class S_BaseAbilityDetails extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	public S_BaseAbilityDetails() {
		
	}

	/**
	 * 純能力值詳細加成資訊
	 * @param statusCount 25 35 45 三階段
	 */
	public S_BaseAbilityDetails(final int statusCount) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01E7);
		
		S_BaseAbilityDetails str = new S_BaseAbilityDetails();
		S_BaseAbilityDetails intel = new S_BaseAbilityDetails();
		S_BaseAbilityDetails wis = new S_BaseAbilityDetails();
		S_BaseAbilityDetails dex = new S_BaseAbilityDetails();
		S_BaseAbilityDetails con = new S_BaseAbilityDetails();		
		S_BaseAbilityDetails cha = new S_BaseAbilityDetails();
		
		switch(statusCount) {
		case 25:
			str.writeInt32(1, statusCount);
			str.writeInt32(2, 1);// 近距離傷害
			str.writeInt32(3, 1);// 近距離命中
			
			intel.writeInt32(1, statusCount);
			intel.writeInt32(2, 1);// 魔法傷害
			intel.writeInt32(3, 1);// 魔法命中
			
			wis.writeInt32(1, statusCount);
			wis.writeInt32(2, 1);// MP回復
			wis.writeInt32(3, 1);// MP藥水回復
			wis.writeInt32(7, 50);// MP上限
			
			dex.writeInt32(1, statusCount);
			dex.writeInt32(2, 1);// 遠距離傷害
			dex.writeInt32(3, 1);// 遠距離命中
			
			con.writeInt32(1, statusCount);
			con.writeInt32(2, 1);// HP回復
			con.writeInt32(6, 50);// 體力上限
			
			cha.writeInt32(1, -1);
			
			this.writeByteArray(1, str.getContent());
			this.writeByteArray(2, intel.getContent());
			this.writeByteArray(3, wis.getContent());
			this.writeByteArray(4, dex.getContent());
			this.writeByteArray(5, con.getContent());
			this.writeByteArray(6, cha.getContent());
			break;
			
		case 35:
			str.writeInt32(1, statusCount);
			str.writeInt32(2, 1);// 近距離傷害
			str.writeInt32(3, 1);// 近距離命中
			
			intel.writeInt32(1, statusCount);
			intel.writeInt32(2, 1);// 魔法傷害
			intel.writeInt32(3, 1);// 魔法命中
			
			wis.writeInt32(1, statusCount);
			wis.writeInt32(2, 1);// MP回復
			wis.writeInt32(3, 1);// MP藥水回復
			wis.writeInt32(7, 100);// MP上限
			
			dex.writeInt32(1, statusCount);
			dex.writeInt32(2, 1);// 遠距離傷害
			dex.writeInt32(3, 1);// 遠距離命中

			con.writeInt32(1, statusCount);
			con.writeInt32(2, 1);// HP回復
			con.writeInt32(3, 1);// HP藥水恢復增加
			con.writeInt32(6, 100);// 體力上限
			
			cha.writeInt32(1, -1);
			
			this.writeByteArray(1, str.getContent());
			this.writeByteArray(2, intel.getContent());
			this.writeByteArray(3, wis.getContent());
			this.writeByteArray(4, dex.getContent());
			this.writeByteArray(5, con.getContent());
			this.writeByteArray(6, cha.getContent());
			break;
			
		case 45:
			str.writeInt32(1, statusCount);
			str.writeInt32(2, 3);// 近距離傷害
			str.writeInt32(3, 3);// 近距離命中
			str.writeInt32(4, 1);// 近距離暴擊
			

			intel.writeInt32(1, statusCount);
			intel.writeInt32(2, 3);// 魔法傷害
			intel.writeInt32(3, 3);// 魔法命中
			intel.writeInt32(4, 1);// 魔法暴擊
			
			wis.writeInt32(1, statusCount);
			wis.writeInt32(2, 3);// MP回復
			wis.writeInt32(3, 3);// MP藥水回復
			wis.writeInt32(7, 150);// MP上限
			
			dex.writeInt32(1, statusCount);
			dex.writeInt32(2, 3);// 遠距離傷害
			dex.writeInt32(3, 3);// 遠距離命中
			dex.writeInt32(4, 1);// 遠距離暴擊
			
			con.writeInt32(1, statusCount);
			con.writeInt32(2, 3);// HP回復
			con.writeInt32(3, 2);// HP藥水恢復增加
			con.writeInt32(6, 150);// 體力上限
			
			cha.writeInt32(1, -1);
			
			this.writeByteArray(1, str.getContent());
			this.writeByteArray(2, intel.getContent());
			this.writeByteArray(3, wis.getContent());
			this.writeByteArray(4, dex.getContent());
			this.writeByteArray(5, con.getContent());
			this.writeByteArray(6, cha.getContent());
			break;
		}
		
		this.randomShort();
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName();
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
