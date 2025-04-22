package com.lineage.server.model.npc.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1MapsLimitTime;

public class L1NpcTeleportAction extends L1NpcXmlAction {

	private static final Log _log = LogFactory.getLog(L1NpcTeleportAction.class);

	private final L1Location _loc;

	private final int _heading;

	private final int _price;

	private final boolean _effect;

	public L1NpcTeleportAction(final Element element) {
		super(element);

		final int x = L1NpcXmlParser.getIntAttribute(element, "X", -1);
		final int y = L1NpcXmlParser.getIntAttribute(element, "Y", -1);
		final int mapId = L1NpcXmlParser.getIntAttribute(element, "Map", -1);
		_loc = new L1Location(x, y, mapId);

		_heading = L1NpcXmlParser.getIntAttribute(element, "Heading", 5);

		_price = L1NpcXmlParser.getIntAttribute(element, "Price", 0);
		_effect = L1NpcXmlParser.getBoolAttribute(element, "Effect", true);
	}

	@Override
	public L1NpcHtml execute(final String actionName, final L1PcInstance pc, final L1Object obj,
			final byte[] args) {
		try {
			// 地圖群組設置資料 (入場時間限制) by terry0412
			final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(_loc.getMapId());
			if (mapsLimitTime != null) {
				// 入場地圖索引碼
				final int order_id = mapsLimitTime.getOrderId();
				// 已使用時間 (單位:秒)
				final int used_time = pc.getMapsTime(order_id);
				// 最大時間
				final int limit_time = mapsLimitTime.getLimitTime();

				// 允許時間已到
				if (used_time > limit_time) {
					pc.sendPackets(new S_ServerMessage("已超過該地圖的允許入場時間。"));
					return L1NpcHtml.HTML_CLOSE;
				}
			}

			// 檢查傳送金額
			if ((_price > 0) && !pc.getInventory().consumeItem(L1ItemId.ADENA, _price)) {
				pc.sendPackets(new S_ServerMessage(337, "$4"));
				return L1NpcHtml.HTML_CLOSE;
			}

			L1Teleport.teleport(pc, _loc.getX(), _loc.getY(), (short) _loc.getMapId(), _heading, _effect);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public void execute(final String actionName, final String npcid) {
		// System.out.println("NpcTeleportTable.get().set");
		NpcTeleportTable.get().set(actionName, _loc.getX(), _loc.getY(), _loc.getMapId(), _price, npcid);
	}
}
