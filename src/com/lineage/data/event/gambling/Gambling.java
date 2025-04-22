package com.lineage.data.event.gambling;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 比賽控制數據
 * 
 * @author dexc
 */
public class Gambling extends GamblingConfig {

	// 冠軍參賽者
	private GamblingNpc _onenpc;

	// private int _no;

	private long _adena;

	private long _upadena;

	public int WIN;

	private final Map<Integer, GamblingNpc> _npcidMap = new HashMap<Integer, GamblingNpc>();

	private final Random _random = new Random();

	/*
	 * public Gambling(int no) { _no = no; }
	 */

	/**
	 * 清除紀錄
	 */
	public void clear() {
		_npcidMap.clear();
		_onenpc = null;
		_adena = 0;
	}

	/**
	 * 傳回本場編號
	 * 
	 * @return
	 */
	/*
	 * public int no() { return _no; }
	 */

	/**
	 * 傳回本場總下注金額
	 * 
	 * @return
	 */
	public long get_allAdena() {
		return _adena;
	}

	/**
	 * 建立參賽NPC
	 * 
	 * @param previous
	 */
	public void set_gmaNpc(final long previous) {
		if (GamblingConfig.ISGFX) {// 賽狗外型
			GamblingConfig.ISGFX = false;

		} else {// 食人妖寶寶外型
			GamblingConfig.ISGFX = true;
		}

		WIN = _random.nextInt(5);

		int i = 0;
		while (_npcidMap.size() < 5) {
			final int npcid = NPCID[_random.nextInt(NPCID.length)];
			if (_npcidMap.get(new Integer(npcid)) == null) {
				final GamblingNpc gamnpc = new GamblingNpc(this);
				gamnpc.showNpc(npcid, i++);
				_npcidMap.put(new Integer(npcid), gamnpc);
			}
		}
		// 加入累積金
		_upadena = previous;
	}

	/**
	 * 目前下注金額
	 * 
	 * @return
	 */
	public long get_allRate() {
		long adena = 0;
		for (final GamblingNpc gamblingNpc : _npcidMap.values()) {
			adena += gamblingNpc.get_adena();
		}
		return adena + _upadena;
	}

	/**
	 * 計算賠率
	 */
	public void set_allRate() {
		long adena = _upadena;
		// 計算全部NPC累積押注
		for (final GamblingNpc gamblingNpc : _npcidMap.values()) {
			adena += gamblingNpc.get_adena();
		}

		_adena = adena;
		// System.out.println("總收入: "+_adena);
		/*
		 * adena *= GamblingSet.ODDS; //System.out.println("預計陪出: "+adena); if
		 * (adena <= 0) { adena = _random.nextInt(20000000) + 9500000; } for
		 * (final GamblingNpc gamblingNpc : _npcidMap.values()) {
		 * this.set_npcRate(gamblingNpc, adena); }
		 */
	}

	/**
	 * 計算每個NPC該獲取的賠率
	 * 
	 * @param npc
	 * @param adena
	 */
	/*
	 * private void set_npcRate(final GamblingNpc npc, final long adena) { //
	 * 每注金額 final int gamadena = GamblingSet.GAMADENA; // 賣出張數 double count =
	 * npc.get_adena() / gamadena; if (count == 0.0) { count =
	 * _random.nextInt(30) + 5; } //System.out.println("賣出張數: "+count); //
	 * 該NPC獲勝每注可領取金額 final double ex = adena / count; //System.out.println(
	 * "該NPC獲勝每注可領取金額: "+ex); // 賠率 double rate = ex / gamadena; // 設置最高賠率限制條件
	 * if (rate > 150D) { double ratex = rate - 150.0; if (ratex > 100D) { ratex
	 * = 100D; } if (ratex <= 1D) { ratex = 1D; } final double x =
	 * _random.nextInt((int) ratex); rate = 150D + x; } //System.out.println(
	 * "賠率: "+rate); npc.set_rate(rate); }
	 */

	/**
	 * 傳回全部參賽者
	 * 
	 * @return
	 */
	public Map<Integer, GamblingNpc> get_allNpc() {
		return _npcidMap;
	}

	/**
	 * 傳回冠軍
	 * 
	 * @return
	 */
	public GamblingNpc get_oneNpc() {
		return _onenpc;
	}

	/**
	 * 設置冠軍
	 * 
	 * @param npc
	 */
	public void set_oneNpc(final GamblingNpc onenpc) {
		_onenpc = onenpc;
	}

	/**
	 * 啟動線程
	 */
	public void startGam() {
		for (final GamblingNpc gamblingNpc : _npcidMap.values()) {
			gamblingNpc.getStart();
		}
	}

	/**
	 * 刪除參賽者
	 */
	public void delAllNpc() {
		for (final GamblingNpc gamblingNpc : _npcidMap.values()) {
			gamblingNpc.delNpc();
		}
	}
}
