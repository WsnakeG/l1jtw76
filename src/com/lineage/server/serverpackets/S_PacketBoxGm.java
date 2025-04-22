package com.lineage.server.serverpackets;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Account;
import com.lineage.server.world.World;

/**
 * GM管理選單
 * 
 * @author DaiEn
 */
public class S_PacketBoxGm extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * writeByte(count) writeInt(time) writeString(name) writeString(info):<br>
	 * [CALL] ボタンのついたウィンドウが表示される。これはBOTなどの不正者チェックに
	 * 使われる機能らしい。名前をダブルクリックするとC_RequestWhoが飛び、クライアントの
	 * フォルダにbot_list.txtが生成される。名前を選択して+キーを押すと新しいウィンドウが開く。
	 */
	public static final int CALL_SOMETHING = 0x2d;// 45;

	/**
	 * GM管理選單
	 * 
	 * @param srcpc 執行的GM
	 * @param mode 模式
	 */
	public S_PacketBoxGm(final L1PcInstance srcpc, final int mode) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(CALL_SOMETHING);
		if (srcpc.isGm()) {
			callSomething(srcpc, mode);
		}
	}

	private void callSomething(final L1PcInstance srcpc, final int mode) {
		final Iterator<L1PcInstance> itr = World.get().getAllPlayers().iterator();

		// 顯示數量
		writeC(World.get().getAllPlayers().size());

		while (itr.hasNext()) {
			final L1PcInstance pc = itr.next();
			// final L1Account acc =
			// AccountReading.get().getAccount(pc.getAccountName());
			final ClientExecutor client = pc.getNetConnection();
			if (client == null) {
				writeD(0);
			} else {
				final L1Account acc = client.getAccount();
				if (acc == null) {
					writeD(0);

				} else {
					final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
					final long lastactive = acc.get_lastactive().getTime();// 最後登入時間
					cal.setTimeInMillis(lastactive);
					cal.set(Calendar.YEAR, 1970);
					final int time = (int) (cal.getTimeInMillis() / 1000);
					writeD(time); // JST 1970 1/1 09:00 が基準
				}
			}

			// 人物資料
			writeS(mode + ":" + pc.getName());
			// this.writeS(pc.getClanname());
			writeS(String.valueOf(pc.getLevel()));
		}

		String xmode = null;
		switch (mode) {
		case 0:// 刪除已存人物保留技能
			xmode = "刪除已存人物保留技能";
			break;

		case 1:// 移動座標至指定人物身邊
			xmode = "移動座標至指定人物身邊";
			break;

		case 2:// 召回指定人物
			xmode = "召回指定人物";
			break;

		case 3:// 召回指定隊伍
			xmode = "召回指定隊伍";
			break;

		case 4:// 全技能
			xmode = "全技能";
			break;

		case 5:// 踢除下線
			xmode = "踢除下線";
			break;

		case 6:// 封鎖IP/MAC
			xmode = "封鎖IP/MAC";
			break;

		case 7:// 帳號封鎖
			xmode = "帳號封鎖";
			break;

		case 8:// 殺死指定人物
			xmode = "殺死指定人物";
			break;
		}

		if (xmode != null) {
			srcpc.setTempID(mode);
			srcpc.sendPackets(new S_ServerMessage(166, "請注意目前模式為: " + xmode));
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
