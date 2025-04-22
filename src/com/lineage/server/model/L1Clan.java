package com.lineage.server.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.world.World;

public class L1Clan {

	private static final Log _log = LogFactory.getLog(L1Clan.class);

	private final Lock _lock = new ReentrantLock(true);

	/** [聯盟 / 一般] */
	public static final int CLAN_RANK_PUBLIC = 2;

	/** [聯盟 /副君主] */
	public static final int CLAN_RANK_GUARDIAN = 3;

	/** [聯盟君主] */
	public static final int CLAN_RANK_PRINCE = 4;

	/** [聯盟 /見習] */
	public static final int ALLIANCE_CLAN_RANK_ATTEND = 5;

	/** [聯盟/ 守護] */
	public static final int ALLIANCE_CLAN_RANK_GUARDIAN = 6;

	/** 一般血盟[一般] */
	public static final int NORMAL_CLAN_RANK_GENERAL = 7;

	/** 一般血盟[見習騎士] */
	public static final int NORMAL_CLAN_RANK_ATTEND = 8;

	/** 一般血盟[守護騎士] */
	public static final int NORMAL_CLAN_RANK_GUARDIAN = 9;

	/** 一般血盟[君主] */
	public static final int NORMAL_CLAN_RANK_PRINCE = 10;

	private int _clanId;

	private String _clanName;

	private int _leaderId;

	private String _leaderName;

	private int _castleId;

	private int _houseId;

	private int _warehouse = 0;
	
	private boolean join_open_state;
	private int join_state = 0;
	private String join_password;

	private final L1DwarfForClanInventory _dwarfForClan = new L1DwarfForClanInventory(this);

	private final ArrayList<String> _membersNameList = new ArrayList<String>();

	// 全部血盟成員與階級資料
	// private static final HashMap<String, Integer> _membersNameList = new
	// HashMap<String, Integer>();

	public int getClanId() {
		return _clanId;
	}

	/**
	 * 設置血盟ID
	 * 
	 * @param clan_id
	 */
	public void setClanId(final int clan_id) {
		_clanId = clan_id;
	}

	/**
	 * 血盟名稱
	 * 
	 * @return
	 */
	public String getClanName() {
		return _clanName;
	}

	/**
	 * 設置血盟名稱
	 * 
	 * @param clan_name
	 */
	public void setClanName(final String clan_name) {
		_clanName = clan_name;
	}

	/**
	 * 盟主OBJID
	 * 
	 * @return
	 */
	public int getLeaderId() {
		return _leaderId;
	}

	/**
	 * 設置盟主OBJID
	 * 
	 * @param leader_id
	 */
	public void setLeaderId(final int leader_id) {
		_leaderId = leader_id;
	}

	/**
	 * 盟主名稱
	 * 
	 * @return
	 */
	public String getLeaderName() {
		return _leaderName;
	}

	/**
	 * 設置盟主名稱
	 * 
	 * @param leader_name
	 */
	public void setLeaderName(final String leader_name) {
		_leaderName = leader_name;
	}

	/**
	 * 擁有城堡ID
	 * 
	 * @return
	 */
	public int getCastleId() {
		return _castleId;
	}

	/**
	 * 設置擁有城堡ID
	 * 
	 * @param hasCastle
	 */
	public void setCastleId(final int hasCastle) {
		_castleId = hasCastle;
	}

	/**
	 * 擁有小屋ID
	 * 
	 * @return
	 */
	public int getHouseId() {
		return _houseId;
	}

	/**
	 * 設置擁有小屋ID
	 * 
	 * @param hasHideout
	 */
	public void setHouseId(final int hasHideout) {
		_houseId = hasHideout;
	}

	/**
	 * 加入血盟成員清單
	 * 
	 * @param member_name
	 */
	public void addMemberName(final String member_name) {
		_lock.lock();
		try {
			if (!_membersNameList.contains(member_name)) {
				_membersNameList.add(member_name);
				final L1PcInstance pc = World.get().getPlayer(member_name);
				if (pc != null) {
					L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
					pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, 0));
				}
			}

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 移出血盟成員清單
	 * 
	 * @param member_name
	 */
	public void delMemberName(final String member_name) {
		_lock.lock();
		try {
			if (_membersNameList.contains(member_name)) {
				_membersNameList.remove(member_name);
				final L1PcInstance pc = World.get().getPlayer(member_name);
				if (pc != null) {
					L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
					pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, 0));
				}
			}

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 血盟線上成員數量
	 * 
	 * @return
	 */
	public int getOnlineClanMemberSize() {
		int count = 0;
		try {
			for (final String name : _membersNameList) {
				final L1PcInstance pc = World.get().getPlayer(name);
				// 人員在線上
				if (pc != null) {
					count++;
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return count;
	}

	/**
	 * 全部血盟成員數量
	 * 
	 * @return
	 */
	public int getAllMembersSize() {
		try {
			return _membersNameList.size();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	/**
	 * 對血盟線上成員發送封包
	 */
	public void sendPacketsAll(final ServerBasePacket packet) {
		try {
			for (final Object nameobj : _membersNameList.toArray()) {
				final String name = (String) nameobj;
				final L1PcInstance pc = World.get().getPlayer(name);
				if (pc != null) {
					pc.sendPackets(packet);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 血盟線上成員
	 * 
	 * @return
	 */
	public L1PcInstance[] getOnlineClanMember() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出清單
		final ArrayList<L1PcInstance> onlineMembers = new ArrayList<L1PcInstance>();
		try {
			temp.addAll(_membersNameList);

			for (final String name : temp) {
				final L1PcInstance pc = World.get().getPlayer(name);
				if ((pc != null) && !onlineMembers.contains(pc)) {
					onlineMembers.add(pc);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return onlineMembers.toArray(new L1PcInstance[onlineMembers.size()]);
	}

	/**
	 * 血盟線上成員名單
	 * 
	 * @return
	 */
	public StringBuilder getOnlineMembersFP() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出名單
		final StringBuilder result = new StringBuilder();
		try {
			temp.addAll(_membersNameList);

			for (final String name : temp) {
				final L1PcInstance pc = World.get().getPlayer(name);
				if (pc != null) {
					result.append(name + " ");
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * 全部血盟成員名單(包含離線)
	 * 
	 * @return
	 */
	public StringBuilder getAllMembersFP() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出名單
		final StringBuilder result = new StringBuilder();
		try {
			temp.addAll(_membersNameList);

			for (final String name : temp) {
				result.append(name + " ");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	private int _emblemId;

	public int getEmblemId() {
		return _emblemId;
	}

	public void setEmblemId(final int i) {
		_emblemId = i;
	}

	/**
	 * 血盟線上成員名單(包含階級)
	 * 
	 * @return
	 */
	/*
	 * public StringBuilder getOnlineMembersFPWithRank() { // 清單緩存 final
	 * ArrayList<String> temp = new ArrayList<String>(); // 輸出名單 final
	 * StringBuilder result = new StringBuilder(); try {
	 * temp.addAll(this._membersNameList); for (final String name : temp) {
	 * final L1PcInstance pc = World.get().getPlayer(name); if (pc != null) {
	 * result.append(name + this.getRankString(pc) + " "); } } } catch (final
	 * Exception e) { _log.error(e.getLocalizedMessage(), e); } return result; }
	 */

	/**
	 * 全部血盟成員名單(包含離線)
	 * 
	 * @return
	 */
	/*
	 * public StringBuilder getAllMembersFPWithRank() { // 清單緩存 final
	 * ArrayList<String> temp = new ArrayList<String>(); // 輸出名單 final
	 * StringBuilder result = new StringBuilder(); try {
	 * temp.addAll(this._membersNameList); for (final String name : temp) {
	 * final L1PcInstance pc = CharacterTable.get().restoreCharacter( name); if
	 * (pc != null) { result.append(name + " "); } } } catch (final Exception e)
	 * { _log.error(e.getLocalizedMessage(), e); } return result; } String[]
	 * _rank = new String[] { // 2:一般 3:副君主 4:聯盟君主 5:修習騎士 6:守護騎士 7:一般 8:修習騎士
	 * 9:守護騎士 10:聯盟君主 "", "", "[一般]", "[副君主]", "[聯盟君主]", "[修習騎士]", "[守護騎士]",
	 * "[一般]", "[修習騎士]", "[守護騎士]", "[聯盟君主]", };
	 */

	/**
	 * 血盟階級
	 * 
	 * @param pc
	 * @return
	 */
	/*
	 * private String getRankString(final L1PcInstance pc) { if (pc != null) {
	 * if (pc.getClanRank() > 0) { return _rank[pc.getClanRank()]; } } return
	 * ""; }
	 */

	public String[] getAllMembers() {
		return _membersNameList.toArray(new String[_membersNameList.size()]);
	}

	/**
	 * 血盟倉庫資料
	 * 
	 * @return
	 */
	public L1DwarfForClanInventory getDwarfForClanInventory() {
		return _dwarfForClan;
	}

	public synchronized int getWarehouseUsingChar() {// 血盟倉庫目前使用者
		return _warehouse;
	}

	public synchronized void setWarehouseUsingChar(final int objid) {
		_warehouse = objid;
	}

	// 血盟技能
	private boolean _clanskill = false;

	/**
	 * 設置是否能啟用血盟技能
	 * 
	 * @param boolean1
	 */
	public void set_clanskill(final boolean boolean1) {
		_clanskill = boolean1;
	}

	/**
	 * 是否能啟用血盟技能
	 * 
	 * @return true有 false沒有
	 */
	public boolean isClanskill() {
		return _clanskill;
	}

	// 血盟技能結束時間
	private Timestamp _skilltime = null;

	/**
	 * 設置血盟技能結束時間
	 * 
	 * @param skilltime
	 */
	public void set_skilltime(final Timestamp skilltime) {
		_skilltime = skilltime;
	}

	/**
	 * 血盟技能結束時間
	 * 
	 * @return _skilltime
	 */
	public Timestamp get_skilltime() {
		return _skilltime;
	}

	private short _loginLevel;

	public final short getLoginLevel() {
		return _loginLevel;
	}

	public void setLoginLevel(final short s) {
		_loginLevel = s;
	}

	private String _clanshowNote;

	public void setClanShowNote(final String text) {
		_clanshowNote = text;
	}

	public String getClanShowNote() {
		return _clanshowNote;
	}

	private String _clanNote;

	public void setClanNote(final String text) {
		_clanNote = text;
	}

	public String getClanNote() {
		return _clanNote;
	}

	private int _clanstep;

	public void setClanStep(final int step) {
		_clanstep = step;
	}

	public int getClanStep() {
		return _clanstep;
	}

	public Date getBirthDay() {
		Date date = new Date();
		try {
			final L1PcInstance pc = CharacterTable.get().restoreCharacter(_leaderName);
			date = pc.getBirthDay();

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			return date;
			// _log.error(e.getLocalizedMessage(), e);;
		}
		return date;

	}

	/**
	 * 全部血盟成員名單(包含離線)
	 * 
	 * @return
	 */
	public ArrayList<L1PcInstance> getAllMembersRank() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出名單
		final ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();
		try {
			temp.addAll(_membersNameList);

			for (final String name : temp) {
				L1PcInstance pc = World.get().getPlayer(name);
				if (pc == null) {
					pc = CharacterTable.get().restoreCharacter(name);
				}
				if (pc != null) {
					result.add(pc);
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}
	
	/** 是否顯示盟徽 */
	private int showEmblem = 0;

	/** 設置是否顯示盟徽 */
	public void setShowEmblem(int showEmblem) {
		this.showEmblem = showEmblem;
	}

	/** 是否顯示盟徽 */
	public int getShowEmblem() {
		return showEmblem;
	}
	
	/** 是否加入血盟 */
	public boolean getJoin_open_state() {
		return join_open_state;
	}

	/** 是否加入血盟 */
	public void setJoin_open_state(boolean join_open_state) {
		this.join_open_state = join_open_state;
	}

	/** 血盟加入種類 0：即時加入，1：允許加入，2：使用密碼加入 */
	public int getJoin_state() {
		return join_state;
	}

	/** 血盟加入種類 0：即時加入，1：允許加入，2：使用密碼加入 */
	public void setJoin_state(int join_state) {
		this.join_state = join_state;
	}

	/** 加入血盟密碼 */
	public String getJoin_password() {
		return join_password;
	}

	/** 加入血盟密碼 */
	public void setJoin_password(String join_password) {
		this.join_password = join_password;
	}
}
