package com.lineage.server.templates;

/**
 * 血盟排行榜暫存
 * 
 * @author simlin
 */
public class T_ClanRankModel {

	private String _clanName;

	private String _leaderName;

	private int _memberCount;

	private int _clanFraction;

	public T_ClanRankModel(final String clanName, final String leaderName, final int memberCount,
			final int clanFraction) {
		_clanName = clanName;
		_leaderName = leaderName;
		_memberCount = memberCount;
		_clanFraction = clanFraction;
	}

	public String getClanName() {
		return _clanName;
	}

	public void setClanName(final String clanName) {
		_clanName = clanName;
	}

	public String getLeaderName() {
		return _leaderName;
	}

	public void setLeaderName(final String leaderName) {
		_leaderName = leaderName;
	}

	public int getMemberCount() {
		return _memberCount;
	}

	public void setMemberCount(final int menberCount) {
		_memberCount = menberCount;
	}

	public int getClanFraction() {
		return _clanFraction;
	}

	public void setClanFraction(final int clanFraction) {
		_clanFraction = clanFraction;
	}
}