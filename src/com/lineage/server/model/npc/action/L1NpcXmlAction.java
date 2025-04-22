package com.lineage.server.model.npc.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.RangeInt;

public abstract class L1NpcXmlAction implements L1NpcAction {
	public String _name;
	public String _npcids = null;
	private final int _npcIds[];
	private final RangeInt _level;
	private final int _questId;
	private final int _questStep;
	private final int _classes[];

	public L1NpcXmlAction(final Element element) {
		_name = element.getAttribute("Name");
		_name = _name.equals("") ? null : _name;
		_npcids = element.getAttribute("NpcId");
		// _npcids = this._npcids.equals("") ? null : this._npcids;
		_npcIds = parseNpcIds(_npcids);
		_level = parseLevel(element);
		_questId = L1NpcXmlParser.parseQuestId(element.getAttribute("QuestId"));
		_questStep = L1NpcXmlParser.parseQuestStep(element.getAttribute("QuestStep"));

		_classes = parseClasses(element);
	}

	private int[] parseClasses(final Element element) {
		final String classes = element.getAttribute("Class").toUpperCase();
		final int result[] = new int[classes.length()];
		int idx = 0;
		for (final Character cha : classes.toCharArray()) {
			result[idx++] = _charTypes.get(cha);
		}
		Arrays.sort(result);
		return result;
	}

	private RangeInt parseLevel(final Element element) {
		final int level = L1NpcXmlParser.getIntAttribute(element, "Level", 0);
		final int min = L1NpcXmlParser.getIntAttribute(element, "LevelMin", 1);
		final int max = L1NpcXmlParser.getIntAttribute(element, "LevelMax", ExpTable.MAX_LEVEL);
		return level == 0 ? new RangeInt(min, max) : new RangeInt(level, level);
	}

	private final static Map<Character, Integer> _charTypes = new HashMap<Character, Integer>();

	static {
		_charTypes.put('P', 0);
		_charTypes.put('K', 1);
		_charTypes.put('E', 2);
		_charTypes.put('W', 3);
		_charTypes.put('D', 4);
		_charTypes.put('R', 5);
		_charTypes.put('I', 6);
	}

	private int[] parseNpcIds(final String npcIds) {
		final StringTokenizer tok = new StringTokenizer(npcIds.replace(" ", ""), ",");
		final int result[] = new int[tok.countTokens()];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(tok.nextToken());
		}
		Arrays.sort(result);
		return result;
	}

	private boolean acceptsNpcId(final L1Object obj) {
		if (0 < _npcIds.length) {
			if (!(obj instanceof L1NpcInstance)) {
				return false;
			}
			final int npcId = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();

			if (Arrays.binarySearch(_npcIds, npcId) < 0) {
				return false;
			}
		}
		return true;
	}

	private boolean acceptsLevel(final int level) {
		return _level.includes(level);
	}

	private boolean acceptsCharType(final int type) {
		if (0 < _classes.length) {
			if (Arrays.binarySearch(_classes, type) < 0) {
				return false;
			}
		}
		return true;
	}

	private boolean acceptsActionName(final String name) {
		if (_name == null) {
			return true;
		}
		return name.equals(_name);
	}

	private boolean acceptsQuest(final L1PcInstance pc) {
		if (_questId == -1) {
			return true;
		}
		if (_questStep == -1) {
			return 0 < pc.getQuest().get_step(_questId);
		}
		return pc.getQuest().get_step(_questId) == _questStep;
	}

	@Override
	public boolean acceptsRequest(final String actionName, final L1PcInstance pc, final L1Object obj) {
		if (!acceptsNpcId(obj)) {
			return false;
		}
		if (!acceptsLevel(pc.getLevel())) {
			pc.sendPackets(new S_SystemMessage("等級未達要求。"));
			return false;
		}
		if (!acceptsQuest(pc)) {
			return false;
		}
		if (!acceptsCharType(pc.getType())) {
			return false;
		} // */
		if (!acceptsActionName(actionName)) {
			return false;
		}
		return true;
	}

	@Override
	public abstract L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj, byte args[]);

	@Override
	public abstract void execute(final String actionName, final String npcid);

	@Override
	public L1NpcHtml executeWithAmount(final String actionName, final L1PcInstance pc, final L1Object obj,
			final long amount) {
		return null;
	}
}
