package com.lineage.server.serverpackets;

import java.util.ArrayList;

import com.lineage.server.datatables.lock.MailReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Mail;

/**
 * 新版信箱系統
 * 
 * @author terry0412
 */
public class S_Mail extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 『來源:伺服器』<位址:186>{長度:216}(時間:1061159132) 0000: ba 00 08 00 63 78 00 00 01
	 * d8 dc 25 52 01 ae a9 ....cx.....%R... 0010: ac f5 b9 d0 00 35 00 35 00 35
	 * 00 35 00 35 00 35 .....5.5.5.5.5.5 0020: 00 00 00 65 78 00 00 01 c8 dd 25
	 * 52 01 ae a9 ac ...ex.....%R.... 0030: f5 b9 d0 00 32 00 32 00 32 00 32 00
	 * 32 00 32 00 ....2.2.2.2.2.2. 0040: 32 00 32 00 32 00 00 00 eb 78 00 00 00
	 * 50 2f 3d 2.2.2....x...P/= 0050: 52 00 ae a9 ac f5 b9 d0 00 33 00 33 00 33
	 * 00 00 R........3.3.3.. 0060: 00 ed 78 00 00 00 50 2f 3d 52 00 ae a9 ac f5
	 * b9 ..x...P/=R...... 0070: d0 00 33 00 33 00 00 00 ef 78 00 00 00 40 30 3d
	 * ..3.3....x...@0= 0080: 52 00 ae a9 ac f5 b9 d0 00 32 00 32 00 00 00 f1
	 * R........2.2.... 0090: 78 00 00 00 40 30 3d 52 00 ae a9 ac f5 b9 d0 00
	 * x...@0=R........ 00a0: 32 00 32 00 00 00 f3 78 00 00 00 d4 32 3d 52 00
	 * 2.2....x....2=R. 00b0: ae a9 ac f5 b9 d0 00 32 00 32 00 00 00 f5 78 00
	 * .......2.2....x. 00c0: 00 00 d0 36 3d 52 00 ae a9 ac f5 b9 d0 00 35 00
	 * ...6=R........5. 00d0: 35 00 00 00 45 1c c9 93 5...E...
	 */
	// 打開收信夾 ?封信件顯示標題
	public S_Mail(final L1PcInstance pc, final int type) {
		final ArrayList<L1Mail> mails = MailReading.get().getMails(pc.getName(), type);

		writeC(S_OPCODE_MAIL);
		writeC(type);
		writeH(mails.size());

		if (mails.isEmpty()) {
			return;
		}

		for (int i = 0; i < mails.size(); i++) {
			final L1Mail mail = mails.get(i);
			writeD(mail.getId());
			writeC(mail.getReadStatus());
			writeD((int) (mail.getDate().getTime() / 1000)); // 信件發送日期
			writeC(mail.getSendType()); // 信件發送類型 (0:發信 1:收信)
			writeS(mail.getSenderName());
			writeByte(mail.getSubject());
		}
	}

	/**
	 * 寄出信件
	 * 
	 * @param pcName 寄出信件: 寄信人name , 寄件備份: 收信人name
	 * @param isDraft 是否是寄件備份 ? true:備份 , false:寄出
	 */
	public S_Mail(final String pcName, final int mailId, final boolean isDraft) {
		final L1Mail mail = MailReading.get().getMail(mailId);
		writeC(S_OPCODE_MAIL);
		writeC(0x50);
		writeD(mailId);
		writeC(isDraft ? 1 : 0);
		writeS(pcName);
		writeByte(mail.getSubject());
	}

	/**
	 * 寄信結果通知
	 * 
	 * @param type 信件類別
	 * @param isDelivered 寄出:1 ,失敗:0
	 */
	public S_Mail(final int type, final boolean isDelivered) {
		writeC(S_OPCODE_MAIL);
		writeC(type);
		writeC(isDelivered ? 1 : 0);
	}

	/**
	 * 取回一般信件的標題
	 * 
	 * @param mails
	 * @param type
	 */
	public S_Mail(final ArrayList<L1Mail> mails, final int type) {
		writeC(S_OPCODE_MAIL);
		writeC(type);
		writeH(mails.size());

		for (int i = 0; i < mails.size(); i++) {
			final L1Mail mail = mails.get(i);
			writeD(mail.getId());
			writeC(mail.getReadStatus()); // 是否已讀取
			writeD((int) (mail.getDate().getTime() / 1000)); // 信件發送日期
			writeC(mail.getSendType()); // 信件發送類型 (0:發信 1:收信)
			writeS(mail.getSenderName());
			writeByte(mail.getSubject());
		}
	}

	/**
	 * 無法傳送信件
	 * 
	 * @param type
	 */
	public S_Mail(final int type) { // 受信者にメール通知
		writeC(S_OPCODE_MAIL);
		writeC(type);
	}

	/**
	 * 讀取一般信件 信件存到保管箱
	 * 
	 * @param mail
	 * @param type
	 */
	public S_Mail(final L1Mail mail, final int type) {
		switch (type) {
		case 0x30: // 刪除一般
		case 0x31: // 刪除血盟
		case 0x32: // 刪除保管箱
		case 0x40: // 存到保管箱
		case 0x60: // 刪除多數信件 (壓著shift一次刪除) by terry0412
			buildPacket_1(mail, type);
			break;

		default:
			buildPacket_2(mail, type);
			break;
		}
	}

	/**
	 * @param mail
	 * @param type
	 */
	private void buildPacket_1(final L1Mail mail, final int type) {
		writeC(S_OPCODE_MAIL);
		writeC(type);
		writeD(mail.getId());
		writeC(0x01); // 處理信件長度
		// this.writeC(0x00); // 0:一般信件 1:血盟信件
		// this.writeC(mail.getSendType()); // 信件發送類型 (0:發信 1:收信)
	}

	private void buildPacket_2(final L1Mail mail, final int type) {
		writeC(S_OPCODE_MAIL);
		writeC(type);
		writeD(mail.getId());
		writeByte(mail.getContent());
	}

	/**
	 * 讀取指定id的信件 by terry0412
	 * 
	 * @param mail
	 * @param type
	 * @param id
	 */
	public S_Mail(final L1Mail mail, final int type, final int id) {
		writeC(S_OPCODE_MAIL);
		writeC(type);
		writeD(mail.getId());
		writeByte(mail.getContent());
		writeC(id);
		writeS(mail.getSenderName());
		writeByte(mail.getSubject());
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
