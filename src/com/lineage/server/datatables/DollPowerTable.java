package com.lineage.server.datatables;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.doll.L1DollExecutor;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.serverpackets.doll.S_DollCompoundInit;
import com.lineage.server.serverpackets.doll.S_DollCompoundItem;
import com.lineage.server.serverpackets.doll.S_DollCompoundMaterial;
import com.lineage.server.serverpackets.doll.S_DollCompoundRoll;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 娃娃能力資料
 * 
 * @author dexc
 */
public class DollPowerTable {

	private static final Log _log = LogFactory.getLog(DollPowerTable.class);

	private static DollPowerTable _instance;

	// 全部娃娃能力設置
	private static final HashMap<Integer, L1Doll> _powerMap = new HashMap<Integer, L1Doll>();

	// 全部娃娃能力設置
	private static final HashMap<Integer, L1DollExecutor> _classList = new HashMap<Integer, L1DollExecutor>();

	// 初始檢查用
	private static final ArrayList<String> _checkList = new ArrayList<String>();

	public static DollPowerTable get() {
		if (_instance == null) {
			_instance = new DollPowerTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = DatabaseFactory.get().getConnection();

			pstm = con.prepareStatement("SELECT * FROM `etcitem_doll_power`");

			rs = pstm.executeQuery();

			while (rs.next()) {
				final int id = rs.getInt("id");
				final String classname = rs.getString("classname");
				final int type1 = rs.getInt("type1");
				final int type2 = rs.getInt("type2");
				final int type3 = rs.getInt("type3");
				final String ch = classname + "=" + type1 + "=" + type2 + "=" + type3;
				if (_checkList.lastIndexOf(ch) == -1) {
					_checkList.add(ch);
					addList(id, classname, type1, type2, type3);

				} else {
					_log.error(
							"娃娃能力設置重複:id=" + id + " type1=" + type1 + " type2=" + type2 + " type3=" + type3);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

			_checkList.clear();
		}
		_log.info("載入娃娃能力資料數量: " + _classList.size() + "(" + timer.get() + "ms)");
		setDollType();
	}

	/**
	 * 設置娃娃能力資料
	 */
	private void setDollType() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = DatabaseFactory.get().getConnection();

			pstm = con.prepareStatement("SELECT * FROM `etcitem_doll_type`");

			rs = pstm.executeQuery();

			while (rs.next()) {
				final int itemid = rs.getInt("itemid");
				final String powers = rs.getString("powers").replaceAll(" ", "");// 取代空白
				final String need = rs.getString("need").replaceAll(" ", "");// 取代空白
				final String count = rs.getString("count").replaceAll(" ", "");// 取代空白
				final int time = rs.getInt("time");
				final int gfxid = rs.getInt("gfxid");
				final String nameid = rs.getString("nameid");
				
				final int level = rs.getInt("level"); // XXX 7.6 魔法娃娃合成等級

				boolean iserr = false;
				final ArrayList<L1DollExecutor> powerList = new ArrayList<L1DollExecutor>();
				if (powers != null) {
					if (!powers.equals("")) {
						final String[] set1 = powers.split(",");
						for (final String string : set1) {
							final L1DollExecutor e = _classList.get(Integer.parseInt(string));
							if (e != null) {
								powerList.add(e);

							} else {
								_log.error("娃娃能力取回錯誤-沒有這個編號:" + string);
								iserr = true;
							}
						}
					}
				}
				int[] needs = null;
				if (need != null) {
					if (!need.equals("")) {
						final String[] set2 = need.split(",");
						needs = new int[set2.length];
						for (int i = 0; i < set2.length; i++) {
							final int itemid_n = Integer.parseInt(set2[i]);
							// 找回物品資訊
							final L1Item temp = ItemTable.get().getTemplate(itemid_n);
							if (temp == null) {
								_log.error("物品資訊取回錯誤-沒有這個編號:" + itemid_n);
								iserr = true;
							}
							needs[i] = itemid_n;
						}
					}
				}
				int[] counts = null;
				if (count != null) {
					if (!count.equals("")) {
						final String[] set3 = count.split(",");
						counts = new int[set3.length];
						if (set3.length != needs.length) {
							_log.error("物品資訊對應錯誤-長度不吻合: itemid:" + itemid);
							iserr = true;
						}

						for (int i = 0; i < set3.length; i++) {
							final int count_n = Integer.parseInt(set3[i]);
							counts[i] = count_n;
						}
					}
				}

				if (!iserr) {
					final L1Doll doll_power = new L1Doll();
					doll_power.set_itemid(itemid);
					doll_power.setPowerList(powerList);
					doll_power.set_need(needs);
					doll_power.set_counts(counts);
					doll_power.set_time(time);
					doll_power.set_gfxid(gfxid);
					doll_power.set_nameid(nameid);
					
					doll_power.set_level(level);
					// 鎖定1~5級的娃娃 加入魔法娃娃合成系統
					if (doll_power.get_level() >= 1 && doll_power.get_level() <= 5) {
						this.addLevelMap(doll_power.get_level(), ItemTable.get().getTemplate(itemid));
					}
					
					_powerMap.put(itemid, doll_power);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
			
			if (!_levelList.isEmpty()) {
				this.buildData();
			}
			
			_classList.clear();
		}
		_log.info("載入娃娃能力資料數量: " + _classList.size() + "(" + timer.get() + "ms)");
	}

	private final TreeMap<Integer, ArrayList<L1Item>> _levelList = new TreeMap<Integer, ArrayList<L1Item>>();
	
	private static MessageDigest _alg;// sha-1校驗實例
	
	private static byte[] _digest;// sha-1校驗碼
	
	private static boolean _begin;// 輸出緩存是否起始輸出
	
	/**
	 * 比較道具製作數據的緩存校驗
	 * @param check
	 * @return
	 */
	public static boolean isEqual(final byte[] check) {
		if (_digest == null) {
			return false;
		}
		return MessageDigest.isEqual(_digest, check);
	}
	
	/**
	 * 加入娃娃等級群組
	 * @param level
	 * @param tmp
	 */
	private void addLevelMap(final int level, final L1Item tmp) {
		ArrayList<L1Item> levelList = _levelList.get(level);
		if (levelList == null) {
			levelList = new ArrayList<L1Item>();
			levelList.add(tmp);
			_levelList.put(level, levelList);

		} else {
			levelList.add(tmp);
		}
	}
	
	public static final CopyOnWriteArrayList<ServerBasePacket> DOLL_PACKET_CACHE = new CopyOnWriteArrayList<ServerBasePacket>();// 魔法娃娃合成數據封包緩存
	
	private void buildData() {	
		try {
			// 初始化sha1校驗實例
			_alg = MessageDigest.getInstance("SHA1");
			_alg.update(new byte[] {0x08, 0x02});// 校驗開頭
			
			this.buildMaterialData();
			
			this.buildDollItemData();
			
			this.buildRouletteData();
			
			// 生成效驗碼
			_digest = _alg.digest();
				
			// 緩存輸出 寫入完成
			DOLL_PACKET_CACHE.add(new S_DollCompoundInit(0x02));
			//PacketCache.ALCHEMY_PACKET_CACHE.add(new S_AlchemyDesignAck(0x02));

			//System.out.println("魔法娃娃合成緩存sha1雜湊碼:" + CodecUtil.bytesToHex(_digest).toUpperCase().replace(":", " "));
			
		} catch (final NoSuchAlgorithmException e0) {
			//_log.(Level.SEVERE, e0.getLocalizedMessage(), e0);
		} catch (final Exception e1) {
			//_log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
		}
	}
	

	
	private void buildMaterialData() {	
		try {
			for (int level = 1; level <= 4; level++) {
				final ArrayList<L1Item> levelList = _levelList.get(level);
				if (levelList != null) {
					byte[] data = new S_DollCompoundMaterial(2, level, levelList).getContent();
					_alg.update(data);
					// 生成封包緩存
					if (!_begin) {// 寫入數據緩存 開始
						_begin = true;
						DOLL_PACKET_CACHE.add(new S_DollCompoundInit(0, data));
					} else {// 寫入數據緩存 繼續
						DOLL_PACKET_CACHE.add(new S_DollCompoundInit(1, data));
					}
				}
			}
		} catch (final Exception e1) {
			//_log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
		}
	}
	
	private void buildDollItemData() {	
		try {
			for (int level = 1; level <= 5; level++) {
				final ArrayList<L1Item> levelList = _levelList.get(level);
				if (levelList != null) {
					byte[] data = new S_DollCompoundItem(3, level, levelList).getContent();
					_alg.update(data);
					
					// 生成封包緩存
					if (!_begin) {
						_begin = true;
						DOLL_PACKET_CACHE.add(new S_DollCompoundInit(0, data));
					} else {
						DOLL_PACKET_CACHE.add(new S_DollCompoundInit(1, data));
					}
				}
			}
		} catch (final Exception e1) {
			//_log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
		}
	}
	
	private void buildRouletteData() {	
		try {
			for (int level = 1; level <= 4; level++) {
				
				byte[] data = new S_DollCompoundRoll(4, level).getContent();
				_alg.update(data);
				// 生成封包緩存
				if (!_begin) {
					_begin = true;
					DOLL_PACKET_CACHE.add(new S_DollCompoundInit(0, data));
				} else {
					DOLL_PACKET_CACHE.add(new S_DollCompoundInit(1, data));
				}
			}
		} catch (final Exception e1) {
			//_log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
		}
	}
	
	/**
	 * 取回魔法娃娃等級群組
	 * @param level
	 * @return
	 */
	public ArrayList<L1Item> getDollLevelList(final int level) {
		return _levelList.get(level);
	}
	
	/**
	 * 是否有指定等級娃娃群組
	 * @param level
	 * @return
	 */
	public boolean isExistDollLevelList(final int level) {
		return _levelList.containsKey(level);
	}
	
	/**
	 * 加入CLASS清單
	 * 
	 * @param powerid
	 * @param className
	 * @param int1
	 * @param int2
	 * @param int3
	 */
	private void addList(final int powerid, final String className, final int int1, final int int2,
			final int int3) {
		if (className.equals("0")) {
			return;
		}
		try {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("com.lineage.server.model.doll.");
			stringBuilder.append(className);

			final Class<?> cls = Class.forName(stringBuilder.toString());
			final L1DollExecutor exe = (L1DollExecutor) cls.getMethod("get").invoke(null);
			exe.set_power(int1, int2, int3);

			_classList.put(new Integer(powerid), exe);

		} catch (final ClassNotFoundException e) {
			final String error = "發生[娃娃能力檔案]錯誤, 檢查檔案是否存在:" + className + " 娃娃能力編號:" + powerid;
			_log.error(error);

		} catch (final IllegalArgumentException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final IllegalAccessException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final InvocationTargetException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final SecurityException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final NoSuchMethodException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 傳回娃娃能力設置
	 * 
	 * @param key
	 * @return
	 */
	public L1Doll get_type(final int key) {
		return _powerMap.get(key);
	}

	/**
	 * 傳回娃娃能力設置
	 * 
	 * @return
	 */
	public HashMap<Integer, L1Doll> map() {
		return _powerMap;
	}
}
