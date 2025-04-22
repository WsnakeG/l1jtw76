package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Buddy;

/**
 * 好友清單
 * 
 * @author dexc
 */
public class S_Buddy extends ServerBasePacket {

	private static final String _buddy = "buddy";

	private byte[] _byte = null;

	/**
	 * 好友清單
	 * 
	 * @param objId
	 */
	public S_Buddy(final int objId, final L1Buddy buddy) {
		buildPacket(objId, buddy);
	}

	private void buildPacket(final int objId, final L1Buddy buddy) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(objId);
		writeS(_buddy);
		writeH(0x02);
		writeH(0x02);

		writeS(buddy.getBuddyListString());
		writeS(buddy.getOnlineBuddyListString());
		
		/*String result = new String("");
		String onlineBuddy = new String("");

		final ArrayList<L1BuddyTmp> list = BuddyReading.get().userBuddy(objId);
		if (list != null) {
			for (final L1BuddyTmp buddyTmp : BuddyReading.get().userBuddy(objId)) {
				final String buddy_name = buddyTmp.get_buddy_name();
				result += buddy_name + " ";

				final L1PcInstance find = World.get().getPlayer(buddy_name);
				if (find != null) {
					onlineBuddy += find.getName() + " ";
				}

				final L1DeInstance de = WorldDe.get().getDe(buddy_name);
				if (de != null) {
					onlineBuddy += de.getNameId() + " ";
				}
			}
		}
		writeS(result);
		writeS(onlineBuddy);*/
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
