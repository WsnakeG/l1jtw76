package com.lineage.server.datatables.lock;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.MailTable;
import com.lineage.server.datatables.storage.MailStorage;
import com.lineage.server.templates.L1Mail;

/**
 * 信件資料
 * 
 * @author dexc
 */
public class MailReading {

	private final Lock _lock;

	private final MailStorage _storage;

	private static MailReading _instance;

	private MailReading() {
		_lock = new ReentrantLock(true);
		_storage = new MailTable();
	}

	public static MailReading get() {
		if (_instance == null) {
			_instance = new MailReading();
		}
		return _instance;
	}

	public void load() {
		_lock.lock();
		try {
			_storage.load();

		} finally {
			_lock.unlock();
		}
	}

	public void setReadStatus(final int mailId) {
		_lock.lock();
		try {
			_storage.setReadStatus(mailId);

		} finally {
			_lock.unlock();
		}
	}

	public void setMailType(final int mailId, final int type) {
		_lock.lock();
		try {
			_storage.setMailType(mailId, type);

		} finally {
			_lock.unlock();
		}
	}

	public void deleteMail(final int mailId) {
		_lock.lock();
		try {
			_storage.deleteMail(mailId);

		} finally {
			_lock.unlock();
		}
	}

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
	public L1Mail writeMail(final int type, final String receiver, final String writer, final byte[] text,
			final int sendType) {
		_lock.lock();
		L1Mail tmp;
		try {
			tmp = _storage.writeMail(type, receiver, writer, text, sendType);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	public Map<Integer, L1Mail> getAllMail() {
		_lock.lock();
		Map<Integer, L1Mail> tmp;
		try {
			tmp = _storage.getAllMail();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	public L1Mail getMail(final int mailId) {
		_lock.lock();
		L1Mail tmp;
		try {
			tmp = _storage.getMail(mailId);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 取得全信件標題 by terry0412
	 * 
	 * @param pcName
	 * @param type
	 * @return
	 */
	public ArrayList<L1Mail> getMails(final String pcName, final int type) {
		final ArrayList<L1Mail> mailList = new ArrayList<L1Mail>();
		for (final L1Mail mail : getAllMail().values()) {
			if (mail.getType() == type) {
				if (mail.getReceiverName().equalsIgnoreCase(pcName)) {
					mailList.add(mail);
				}
			}
		}
		return mailList;
	}

	/**
	 * 取得信件總數量 by terry0412
	 * 
	 * @param pcName
	 * @param type
	 * @return
	 */
	public int getMailSize(final String pcName, final int type) {
		final ArrayList<L1Mail> mailList = getMails(pcName, type);
		if (mailList != null) {
			return mailList.size();
		}
		return 0;
	}

	/**
	 * 信箱系統 - 更新玩家名稱 by terry0412
	 * 
	 * @param ori_name
	 * @param new_name
	 */
	public void renewPcName(final String ori_name, final String new_name) {
		_lock.lock();
		try {
			_storage.renewPcName(ori_name, new_name);

		} finally {
			_lock.unlock();
		}
	}
}
