package com.lineage.server.serverpackets;

/**
 * 登入狀態
 * 
 * @author dexc
 */
public class S_LoginResult extends ServerBasePacket {

	/** 登入伺服器成功 ( 無訊息 ) */
	public static final int REASON_LOGIN_OK = 0x0000;

	/**
	 * 無法登入的原因如下： 1.您的帳號密碼輸入錯誤。 2.帳號受晶片卡保護但未使用晶片卡登入。 若仍有疑問請洽客服中心02-8024-2002
	 */
	public static final int REASON_ACCESS_FAILED = 0x0008;

	/**
	 * 已經使用中。
	 */
	public static final int REASON_ACCOUNT_IN_USE = 0x0016;

	/**
	 * 您的帳號目前無法使用，請至GASH會員中心 『大事紀』查詢原因。GASH會員中心網址： http://tw。gamania。com/
	 */
	public static final int EVENT_CANT_USE = 0x001a;

	/**
	 * 您無法順利登入，可能原因如下： 使用期間結束了。請在GASH會員中心 (http://tw。gamania。com/)延長使用時間 。
	 */
	public static final int EVENT_CANT_LOGIN = 0x001c;

	/**
	 * 用戶註冊介面
	 */
	public static final int EVENT_REGISTER_ACCOUNTS = 0x002f;

	/**
	 * 無法順利取得連線
	 */
	public static final int EVENT_LAN_ERROR = 0x0027;

	/**
	 * 您的電子郵件是無效的。請再輸入電子郵件的正確的地址。
	 */
	public static final int EVENT_MAIL_ERROR = 0x000b;

	/**
	 * 已經有同樣的帳號。請重新輸入。
	 */
	public static final int REASON_ACCOUNT_ALREADY_EXISTS = 0x0007;

	/**
	 * 國家名稱是無效的。請重新輸入國家名稱。
	 */
	public static final int EVENT_C_ERROR = 0x0011;

	/**
	 * 回到登入畫面
	 */
	public static final int EVENT_RE_LOGIN = 0x0004;

	/**
	 * 您的天堂點數已經使用完畢，請至開卡中心 確認剩餘點數並轉移點數至天堂遊戲中。
	 */
	public static final int EVENT_CLOCK_ERROR = 0x0032;

	/**
	 * 這個角色名稱是不合法的。
	 */
	public static final int EVENT_NAME_ERROR = 0x0034;

	/**
	 * 您無法在此變更密碼，請上天堂網頁開卡中心變更。
	 */
	public static final int EVENT_PASS_ERROR = 0x0035;

	/**
	 * 您輸入的密碼錯誤。再次確認您所輸入的密碼是否正確，或電洽遊戲橘子客服中心(02)8024-2002。
	 */
	public static final int EVENT_PASS_CHECK = 0x000a;

	/**
	 * 帳號不存在
	 */
	public static final int EVENT_ERROR_USER = 0x009b;

	/**
	 * 帳號或密碼錯誤
	 */
	public static final int EVENT_ERROR_PASS = 0x0095;

	// public static int REASON_SYSTEM_ERROR = 0x01;

	private byte[] _byte = null;

	/**
	 * 登入狀態
	 * 
	 * @param reason
	 */
	public S_LoginResult(final int reason) {
		buildPacket(reason);
	}

	private void buildPacket(final int reason) {
		// 0000: 76 08 00 00 00 00 00 00 00 00 00 00 00 00 90 c8
		// v...............
		writeC(S_OPCODE_LOGINRESULT);
		writeH(reason);

		writeD(0x00000000);
		writeD(0x00000000);
		writeD(0x00000000);
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
