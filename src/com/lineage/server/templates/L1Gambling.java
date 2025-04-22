package com.lineage.server.templates;

/**
 * 妖精競賽紀錄緩存
 * 
 * @author dexc
 */
public class L1Gambling {

	private int _id;

	private long _adena;

	private double _rate;

	private String _gamblingno;

	private int _outcount;

	/**
	 * 場次編號
	 * 
	 * @return the _id
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * 場次編號
	 * 
	 * @param id the _id to set
	 */
	public void set_id(final int id) {
		_id = id;
	}

	/**
	 * 本場總下注金額
	 * 
	 * @return the _adena
	 */
	public long get_adena() {
		return _adena;
	}

	/**
	 * 本場總下注金額
	 * 
	 * @param adena the _adena to set
	 */
	public void set_adena(final long adena) {
		_adena = adena;
	}

	/**
	 * 獲勝NPC賠率
	 * 
	 * @return the _rate
	 */
	public double get_rate() {
		return _rate;
	}

	/**
	 * 獲勝NPC賠率
	 * 
	 * @param rate the _rate to set
	 */
	public void set_rate(final double rate) {
		_rate = rate;
	}

	/**
	 * 場次編號-獲勝NPCID
	 * 
	 * @return the _gamblingno
	 */
	public String get_gamblingno() {
		return _gamblingno;
	}

	/**
	 * 場次編號-獲勝NPCID
	 * 
	 * @param gamblingno the _gamblingno to set
	 */
	public void set_gamblingno(final String gamblingno) {
		_gamblingno = gamblingno;
	}

	/**
	 * 獲勝下注數量
	 * 
	 * @return the _outcount
	 */
	public int get_outcount() {
		return _outcount;
	}

	/**
	 * 獲勝下注數量
	 * 
	 * @param outcount the _outcount to set
	 */
	public void set_outcount(final int outcount) {
		_outcount = outcount;
	}

}
