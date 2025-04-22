package com.lineage.server.templates;

/**
 * 快速鍵紀錄
 * 
 * @author dexc
 */
public class L1Config {

	private int objid = 0;

	private int length = 0;

	private byte[] data = null;

	/**
	 * @return the objid
	 */
	public int getObjid() {
		return objid;
	}

	/**
	 * @param objid the objid to set
	 */
	public void setObjid(final int objid) {
		this.objid = objid;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(final int length) {
		this.length = length;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(final byte[] data) {
		this.data = data;
	}

}
