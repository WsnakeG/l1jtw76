package com.lineage.server.model.shop;

public class L1AssessedItem {
	private final int _targetId;
	private final int _assessedPrice;

	L1AssessedItem(final int targetId, final int assessedPrice) {
		_targetId = targetId;
		_assessedPrice = assessedPrice;
	}

	public int getTargetId() {
		return _targetId;
	}

	public int getAssessedPrice() {
		return _assessedPrice;
	}
}
