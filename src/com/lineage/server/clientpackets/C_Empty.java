package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;

public class C_Empty extends ClientBasePacket {

	public C_Empty(final byte[] abyte0) {
		// 資料載入
		read(abyte0);
		a(0);
	}

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) throws Exception {
		// TODO Auto-generated method stub
	}
}