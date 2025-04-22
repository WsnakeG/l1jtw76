package com.lineage.server.datatables.storage;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lineage.server.templates.L1ClanRecommend;

public interface ClanRecommendStorage {

	public void load();

	public void insertRecommend(final int clan_id, final String clan_name, final String leader_name,
			final int type_id, final String type_message);

	public void insertRecommendApply(final int clan_id, final String clan_name, final int applicant_id,
			final String applicant_name);

	public void updateRecommend(final int clan_id, final int type_id, final String type_message);

	public void deleteRecommend(final int clan_id);

	public void deleteRecommendApply(final int chan_id, final int char_id);

	public Map<Integer, L1ClanRecommend> getRecommendsList();

	public Map<Integer, CopyOnWriteArrayList<Integer>> getApplyList();

}
