package com.lineage.server.templates;

import java.util.List;

/**
 * @author terry0412
 */
public final class L1Rank {

	private String _partyLeader;

	private final List<String> _partyMember;

	private final int _score;

	public L1Rank(final String partyLeader, final List<String> partyMember, final int score) {
		_partyLeader = partyLeader;
		_partyMember = partyMember;
		_score = score;
	}

	public final String getPartyLeader() {
		return _partyLeader;
	}

	public final void setPartyLeader(final String partyLeader) {
		_partyLeader = partyLeader;
	}

	public final List<String> getPartyMember() {
		return _partyMember;
	}

	public final int getScore() {
		return _score;
	}

	public final int getMemberSize() {
		return _partyMember.size() + 1;
	}
}
