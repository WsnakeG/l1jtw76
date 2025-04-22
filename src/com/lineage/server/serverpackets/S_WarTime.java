package com.lineage.server.serverpackets;

import java.util.Calendar;

import com.lineage.config.Config;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.templates.L1Castle;

/**
 * 圍城時間設定
 * 
 * @author dexc
 */
public class S_WarTime extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 圍城時間設定
	 * 
	 * @param cal
	 */
	public S_WarTime(final Calendar cal) {
		// 1997/01/01 17:00を基点としている
		final Calendar base_cal = Calendar.getInstance();
		base_cal.set(1997, 0, 1, 17, 0);
		final long base_millis = base_cal.getTimeInMillis();
		final long millis = cal.getTimeInMillis();
		long diff = millis - base_millis;
		diff -= 1200 * 60 * 1000; // 誤差修正
		diff = diff / 60000; // 分以下切捨て
		// timeは1加算すると3:02（182分）進む
		final int time = (int) (diff / 182);

		// writeDの直前のwriteCで時間の調節ができる
		// 0.7倍した時間だけ縮まるが
		// 1つ調整するとその次の時間が広がる？
		// this.writeC(S_OPCODE_WARTIME);
		writeH(0x0006); // リストの数（6以上は無効）
		writeS(Config.TIME_ZONE); // 時間の後ろの（）内に表示される文字列
		writeC(0x00); // ?
		writeC(0x00); // ?
		writeC(0x00);
		writeD(time);
		writeC(0x00);
		writeD(time - 1);
		writeC(0x00);
		writeD(time - 2);
		writeC(0x00);
		writeD(time - 3);
		writeC(0x00);
		writeD(time - 4);
		writeC(0x00);
		writeD(time - 5);
		writeC(0x00);
	}

	/**
	 * 圍城時間設定 - 測試
	 * 
	 * @param cal
	 */
	public S_WarTime(final int op) {
		final L1Castle l1castle = CastleReading.get().getCastleTable(5);// 5 海音城
		final Calendar cal = l1castle.getWarTime();
		// 1997/01/01 17:00を基点としている
		final Calendar base_cal = Calendar.getInstance();
		base_cal.set(1997, 0, 1, 17, 0);
		final long base_millis = base_cal.getTimeInMillis();
		final long millis = cal.getTimeInMillis();
		long diff = millis - base_millis;
		diff -= 1200 * 60 * 1000; // 誤差修正
		diff = diff / 60000; // 分以下切捨て
		// timeは1加算すると3:02（182分）進む
		final int time = (int) (diff / 182);

		writeC(op);
		writeH(6); // リストの数（6以上は無効）
		writeS(Config.TIME_ZONE); // 時間の後ろの（）内に表示される文字列
		writeC(0); // ?
		writeC(0); // ?
		writeC(0);
		writeD(time);
		writeC(0);
		writeD(time - 1);
		writeC(0);
		writeD(time - 2);
		writeC(0);
		writeD(time - 3);
		writeC(0);
		writeD(time - 4);
		writeC(0);
		writeD(time - 5);
		writeC(0);

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
