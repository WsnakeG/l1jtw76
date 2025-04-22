package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

import java.io.IOException;

public class S_HowManyKey extends ServerBasePacket
{
  public S_HowManyKey(L1NpcInstance npc, int price, int min, int max, String htmlId)
  {
    writeC(S_OPCODE_INPUTAMOUNT);
    writeD(npc.getId());
    writeD(price);
    writeD(min);
    writeD(min);
    writeD(max);
    writeH(0);
    writeS(htmlId);
    writeC(0);
    writeH(2);
    writeS(npc.getName());
    writeS(String.valueOf(price));
  }

//  public byte[] getContent() throws IOException
//  {
//    return getBytes();
//  }
  
  private byte[] _byte = null;
  
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