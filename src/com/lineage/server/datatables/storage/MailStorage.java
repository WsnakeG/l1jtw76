package com.lineage.server.datatables.storage;

import java.util.Map;

import com.lineage.server.templates.L1Mail;

/**
 * 信件資料
 * 
 * @author dexc
 */
public interface MailStorage {

	public void load();

	public void setReadStatus(int mailId);

	public void setMailType(int mailId, int type);

	public void deleteMail(int mailId);

	/**
	 * 寫入信件 by terry0412
	 * 
	 * @param type
	 * @param receiver
	 * @param writer
	 * @param text
	 * @param sendType 信件發送類型 (0:發信 1:收信)
	 * @return
	 */
	public L1Mail writeMail(int type, String receiver, String writer, byte[] text, final int sendType);

	public Map<Integer, L1Mail> getAllMail();

	public L1Mail getMail(int mailId);

	/**
	 * 信箱系統 - 更新玩家名稱 by terry0412
	 * 
	 * @param ori_name
	 * @param new_name
	 */
	public void renewPcName(String ori_name, String new_name);

}
