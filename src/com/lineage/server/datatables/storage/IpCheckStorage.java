package com.lineage.server.datatables.storage;

/**
 * IP驗證庫
 * 
 * @author dexc
 */
public interface IpCheckStorage {

	// public void load();

	public void stsrt_cmd_tmp();

	public boolean check(final String ipaddr);
}
