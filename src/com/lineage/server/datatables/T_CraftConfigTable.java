package com.lineage.server.datatables;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.lineage.server.model.L1ObjectAmount;
import com.lineage.server.model.npc.action.L1NpcAction;
import com.lineage.server.model.npc.action.L1NpcListedAction;
import com.lineage.server.model.npc.action.L1NpcXmlParser;
import com.lineage.server.serverpackets.S_CraftContent;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.utils.IntRange;
import com.lineage.server.utils.IterableElementList;

public class T_CraftConfigTable {

	private static final Log _log = LogFactory.getLog(T_CraftConfigTable.class);

	private static T_CraftConfigTable _instance = null;

	private static int _actionId = 1;

	private final HashMap<Integer, HashMap<Integer, T_CraftConfigTable.NewL1NpcMakeItemAction>> _action_list = new HashMap<Integer, HashMap<Integer, T_CraftConfigTable.NewL1NpcMakeItemAction>>();
	private HashMap<Integer, T_CraftConfigTable.NewL1NpcMakeItemAction> _make_list = new HashMap<Integer, T_CraftConfigTable.NewL1NpcMakeItemAction>();

	private String _security = null;

	private static final byte[] _key = { 8, 2 };
	
	private T_CraftConfigTable() {
		try {
			// _log.info("步驟1");
			final File file = new File("./data/xml/NewNpcActions/", "NewItemMaking.xml");
			// _log.info("步驟2");
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// _log.info("步驟3");
			final Document doc = builder.parse(file);
			// _log.info("步驟4");
			final Element element = doc.getDocumentElement();
			// _log.info("步驟5");
			if (!element.getNodeName().equalsIgnoreCase("NpcActionList")) {
				return;
			}
			// _log.info("步驟6");
			final NodeList list = element.getChildNodes();
			// _log.info("步驟7");
			// _log.info("開始準備遍歷樹形");
			for (final Element elem : new IterableElementList(list)) {
				for (final Element c_elem : new IterableElementList(elem.getChildNodes())) {
					putmakelist(new NewL1NpcMakeItemAction(c_elem, _actionId++));
				}
			}
			// _log.info("i:"+i+" j"+j);
			// _log.info("步驟8");

		} catch (final Exception e) {
			// _log.info("錯誤步驟");
			_log.error(e.getLocalizedMessage(), e);
		}
		// _log.info("步驟9");
		create();

		// _log.info("道具製做加載");
	}

	public static synchronized T_CraftConfigTable get() {
		if (_instance == null) {
			_instance = new T_CraftConfigTable();
		}
		return _instance;
	}

	public void putmakelist(final NewL1NpcMakeItemAction npcMakeItemAction) {
		_make_list.put(Integer.valueOf(npcMakeItemAction.getAmountActionID()), npcMakeItemAction);

		final int[] npcIds = npcMakeItemAction.getAmountNpcList();
		HashMap<Integer, NewL1NpcMakeItemAction> makeItems = null;
		for (int i = 0; i < npcIds.length; i++) {
			makeItems = _action_list.get(Integer.valueOf(npcIds[i]));
			if (makeItems == null) {
				makeItems = new HashMap<Integer, NewL1NpcMakeItemAction>();
				_action_list.put(Integer.valueOf(npcIds[i]), makeItems);
			}
			makeItems.put(Integer.valueOf(npcMakeItemAction.getAmountActionID()), npcMakeItemAction);
		}
	}

	public NewL1NpcMakeItemAction getNpcMakeItem(final int actionId) {
		return _make_list.get(Integer.valueOf(actionId));
	}

	public HashMap<Integer, T_CraftConfigTable.NewL1NpcMakeItemAction> getNpcMakeItemActionList(
			final int npcId) {
		return _action_list.get(Integer.valueOf(npcId));
	}

	public HashMap<Integer, T_CraftConfigTable.NewL1NpcMakeItemAction> getNpcMakeItemList() {
		return _make_list;
	}

	public void setNpcMakeItemList(
			final HashMap<Integer, T_CraftConfigTable.NewL1NpcMakeItemAction> allNpcMakeItem) {
		_make_list = allNpcMakeItem;
	}

	public void create() {
		try {
			S_CraftContent content = null;
			byte[] contbyte = null;
			final MessageDigest SHA1_digest = MessageDigest.getInstance("SHA-1");
			SHA1_digest.update(_key);
			// _log.info("list數量:" + _make_list.size());
			for (final NewL1NpcMakeItemAction npcMakeItemAction : _make_list.values()) {
				// _log.info("namid:" + npcMakeItemAction._nameId);
				content = new S_CraftContent(npcMakeItemAction);
				// _log.info("構造完畢:" + npcMakeItemAction._nameId);
				npcMakeItemAction.setPack(content);// 暫存S包"bm.java"
				// _log.info("存儲完畢:" + npcMakeItemAction._nameId);
				contbyte = content.getContent();// 自S包"bm.java"取回ByteArray
				// _log.info("取回byte:" + npcMakeItemAction._nameId);
				SHA1_digest.update(contbyte);
				// _log.info("更新秘鑰:" + npcMakeItemAction._nameId);

			}
			_security = update(SHA1_digest.digest());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public String getSHAkey() {
		return _security;
	}

	public void setSHAkey(final String sHA_1) {
		_security = sHA_1;
	}

	public static String update(final byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	public class NewL1NpcMakeItemAction {
		private final List<L1ObjectAmount<Integer>> _material_list = new ArrayList<L1ObjectAmount<Integer>>();
		private final List<L1ObjectAmount<Integer>> _aid_material_list = new ArrayList<L1ObjectAmount<Integer>>();

		private final List<L1ObjectAmount<Integer>> _item_list = new ArrayList<L1ObjectAmount<Integer>>();
		private final List<L1ObjectAmount<Integer>> _random_item_list = new ArrayList<L1ObjectAmount<Integer>>();

		private final List<L1ObjectAmount<Integer>> _fail_item_list = new ArrayList<L1ObjectAmount<Integer>>();
		private final List<L1ObjectAmount<Integer>> _fail_random_item_list = new ArrayList<L1ObjectAmount<Integer>>();
		private final int[] _npcId;
		private final int _actionId;
		@SuppressWarnings("unused")
		private final boolean _amount_inputable;
		private final boolean _broadcast;
		private int _nameId;
		private int _minlevel;
		private int _maxlevel;
		private int _minlawful;
		private int _maxlawful;
		private int _minkarma;
		private int _maxkarma;
		private int _onecechangecount;
		private int _successrandom;
		@SuppressWarnings("unused")
		private final int _withintime;
		@SuppressWarnings("unused")
		private final int _withincount;
		private int _random_tmp;
		private int _fail_random_tmp;
		private ArrayList<Integer> _polyIdsList;
		private IntRange _level_range;// k=IntRange.java
		private IntRange _lawful_range;// k=IntRange.java
		private IntRange _karma_range;// k=IntRange.java
		private final L1NpcAction _succeed_action;// g=L1NpcAction.java
		private final String _system_message;
		private final int _system_messageId;
		private final L1NpcAction _fail_action;// g=L1NpcAction.java
		private final String _fail_system_message;
		private final int _fail_system_messageId;
		private ServerBasePacket _serverbased;// cl=ServerBasePacket.java

		public NewL1NpcMakeItemAction(final Element element, final int actionid) {
			_amount_inputable = L1NpcXmlParser.getBoolAttribute(element, "AmountInputable", true);
			_actionId = actionid;
			_nameId = L1NpcXmlParser.getIntAttribute(element, "nameId", -1);
			// _log.info("nameid:"+_nameId+" actid:"+actionid);
			_minlevel = L1NpcXmlParser.getIntAttribute(element, "minLevel", 1);
			_maxlevel = L1NpcXmlParser.getIntAttribute(element, "maxLevel", 255);
			_level_range = new IntRange(_minlevel, _maxlevel);
			_minlawful = L1NpcXmlParser.getIntAttribute(element, "minLawful", -32768);
			_maxlawful = L1NpcXmlParser.getIntAttribute(element, "maxLawful", 32767);
			_lawful_range = new IntRange(_minlawful, _maxlawful);
			_minkarma = L1NpcXmlParser.getIntAttribute(element, "minKarma", -2147483648);
			_maxkarma = L1NpcXmlParser.getIntAttribute(element, "maxKarma", 2147483647);
			_karma_range = new IntRange(_minkarma, _maxkarma);
			_onecechangecount = L1NpcXmlParser.getIntAttribute(element, "onceChangeCount", 88);
			_successrandom = L1NpcXmlParser.getIntAttribute(element, "succeedRandom", 1000000);
			_polyIdsList = L1NpcXmlParser.jdMethod_do(element.getAttribute("polyIds"));// new-method
			_withintime = L1NpcXmlParser.getIntAttribute(element, "withinTime", -1);
			_withincount = L1NpcXmlParser.getIntAttribute(element, "withinCount", -1);
			_npcId = L1NpcXmlParser.jdMethod_for(element.getAttribute("NpcId"));// new-method
			_random_tmp = 0;
			_fail_random_tmp = 0;

			final NodeList list = element.getChildNodes();
			L1ObjectAmount<Integer> objectAmount = null;
			NodeList childList = null;

			for (final Element elem : new IterableElementList(list)) {
				if (elem.getNodeName().equalsIgnoreCase("Material")) {
					int itemId = Integer.valueOf(elem.getAttribute("ItemId")).intValue();
					int amount = Integer.valueOf(elem.getAttribute("Amount")).intValue();
					int enchantLevel = L1NpcXmlParser.getIntAttribute(elem, "enchantLevel", 0);
					int bless = L1NpcXmlParser.getIntAttribute(elem, "bless", 3);

					objectAmount = new L1ObjectAmount<Integer>(itemId, amount, enchantLevel, bless);

					childList = elem.getChildNodes();
					if (childList.getLength() > 0) {
						final ArrayList<L1ObjectAmount<Integer>> substituteItems = new ArrayList<L1ObjectAmount<Integer>>();
						for (final Element elem_1 : new IterableElementList(childList)) {
							if (elem_1.getNodeName().equalsIgnoreCase("Substitute")) {
								itemId = Integer.valueOf(elem_1.getAttribute("ItemId")).intValue();
								amount = Integer.valueOf(elem_1.getAttribute("Amount")).intValue();
								enchantLevel = L1NpcXmlParser.getIntAttribute(elem_1, "enchantLevel", 0);
								bless = L1NpcXmlParser.getIntAttribute(elem_1, "bless", 3);
								substituteItems.add(
										new L1ObjectAmount<Integer>(itemId, amount, enchantLevel, bless));
							}
						}
						objectAmount.setAmountList(substituteItems);
					}
					_material_list.add(objectAmount);

				} else if (elem.getNodeName().equalsIgnoreCase("AidMaterial")) {
					final int itemId = Integer.valueOf(elem.getAttribute("ItemId")).intValue();
					final int amount = Integer.valueOf(elem.getAttribute("Amount")).intValue();
					final int enchantLevel = L1NpcXmlParser.getIntAttribute(elem, "enchantLevel", 0);
					final int bless = L1NpcXmlParser.getIntAttribute(elem, "bless", 3);

					objectAmount = new L1ObjectAmount<Integer>(itemId, amount, enchantLevel, bless);

					_aid_material_list.add(objectAmount);
					_onecechangecount = 1;

				} else if (elem.getNodeName().equalsIgnoreCase("Item")) {
					final int itemId = Integer.valueOf(elem.getAttribute("ItemId")).intValue();
					final int amount = Integer.valueOf(elem.getAttribute("Amount")).intValue();
					final int enchantLevel = L1NpcXmlParser.getIntAttribute(elem, "enchantLevel", 0);
					final int bless = L1NpcXmlParser.getIntAttribute(elem, "bless", 1);
					_item_list.add(new L1ObjectAmount<Integer>(itemId, amount, enchantLevel, bless));

				} else if (elem.getNodeName().equalsIgnoreCase("RandomItem")) {
					childList = elem.getChildNodes();
					if (childList.getLength() > 0) {
						for (final Element elem_1 : new IterableElementList(childList)) {
							if (elem_1.getNodeName().equalsIgnoreCase("Item")) {
								final int itemId = Integer.valueOf(elem_1.getAttribute("ItemId")).intValue();
								final int random = Integer.valueOf(elem_1.getAttribute("random")).intValue();
								final int amount = Integer.valueOf(elem_1.getAttribute("Amount")).intValue();
								final int enchantLevel = L1NpcXmlParser.getIntAttribute(elem_1,
										"enchantLevel", 0);
								final int bless = L1NpcXmlParser.getIntAttribute(elem_1, "bless", 1);
								_random_item_list.add(new L1ObjectAmount<Integer>(itemId, amount,
										enchantLevel, bless, random));
								_random_tmp += random;
							}
						}

					}

				} else if (elem.getNodeName().equalsIgnoreCase("FailItem")) {
					final int itemId = Integer.valueOf(elem.getAttribute("ItemId")).intValue();
					final int amount = Integer.valueOf(elem.getAttribute("Amount")).intValue();
					final int enchantLevel = L1NpcXmlParser.getIntAttribute(elem, "enchantLevel", 0);
					final int bless = L1NpcXmlParser.getIntAttribute(elem, "bless", 1);
					_fail_item_list.add(new L1ObjectAmount<Integer>(itemId, amount, enchantLevel, bless));

				} else if (elem.getNodeName().equalsIgnoreCase("FailRandomItem")) {
					childList = elem.getChildNodes();
					if (childList.getLength() > 0) {
						for (final Element elem_1 : new IterableElementList(childList)) {
							if (elem_1.getNodeName().equalsIgnoreCase("Item")) {
								final int itemId = Integer.valueOf(elem_1.getAttribute("ItemId")).intValue();
								final int random = Integer.valueOf(elem_1.getAttribute("random")).intValue();
								final int amount = Integer.valueOf(elem_1.getAttribute("Amount")).intValue();
								final int enchantLevel = L1NpcXmlParser.getIntAttribute(elem_1,
										"enchantLevel", 0);
								final int bless = L1NpcXmlParser.getIntAttribute(elem_1, "bless", 1);
								_fail_random_item_list.add(new L1ObjectAmount<Integer>(itemId, amount,
										enchantLevel, bless, random));
								_fail_random_tmp += random;
							}
						}
					}
				}
			}

			if (((_item_list.isEmpty()) && (_random_item_list.isEmpty())) || (_material_list.isEmpty())) {
				throw new IllegalArgumentException("兌換系統配置錯誤.");
			}

			Element elem = L1NpcXmlParser.getFirstChildElementByTagName(element, "Succeed");
			if (elem != null) {
				_succeed_action = new L1NpcListedAction(elem);
				_system_message = elem.getAttribute("SystemMessage");
				_system_messageId = L1NpcXmlParser.getIntAttribute(elem, "ServerMessageId", -1);
				_broadcast = L1NpcXmlParser.getBoolAttribute(elem, "broadcast", false);
			} else {
				_succeed_action = null;
				_system_message = "";
				_system_messageId = -1;
				_broadcast = false;
			}

			elem = L1NpcXmlParser.getFirstChildElementByTagName(element, "Fail");
			if (elem != null) {
				_fail_action = new L1NpcListedAction(elem);
				_fail_system_message = elem.getAttribute("SystemMessage");
				_fail_system_messageId = L1NpcXmlParser.getIntAttribute(elem, "ServerMessageId", -1);
			} else {
				_fail_action = null;
				_fail_system_message = "";
				_fail_system_messageId = -1;
			}
		}

		public int getAmountNameId() {
			return _nameId;
		}

		public void setAmountNameId(final int nameId) {
			_nameId = nameId;
		}

		public int getAmountMinLevel() {
			return _minlevel;
		}

		public void setAmountMinLevel(final int minLevel) {
			_minlevel = minLevel;
		}

		public int getAmountMaxLevel() {
			return _maxlevel;
		}

		public void setAmountMaxLevel(final int maxLevel) {
			_maxlevel = maxLevel;
		}

		public int getAmountMinLawful() {
			return _minlawful;
		}

		public void setAmountMinLawful(final int minLawful) {
			_minlawful = minLawful;
		}

		public int getAmountMaxLawful() {
			return _maxlawful;
		}

		public void setAmountMaxLawful(final int maxLawful) {
			_maxlawful = maxLawful;
		}

		public int getAmountMinKarma() {
			return _minkarma;
		}

		public void setAmountMinKarma(final int minKarma) {
			_minkarma = minKarma;
		}

		public int getAmountMaxKarma() {
			return _maxkarma;
		}

		public void setAmountMaxKarma(final int maxKarma) {
			_maxkarma = maxKarma;
		}

		public int getOnceChangeCount() {
			return _onecechangecount;
		}

		public void setOnceChangeCount(final int onceChangeCount) {
			_onecechangecount = onceChangeCount;
		}

		public ArrayList<Integer> getCraftPolyList() {
			return _polyIdsList;
		}

		public void setCraftPolyList(final ArrayList<Integer> polyIds) {
			_polyIdsList = polyIds;
		}

		public List<L1ObjectAmount<Integer>> getAmountMeterialList() {
			return _material_list;
		}

		public List<L1ObjectAmount<Integer>> getAmountItemList() {
			return _item_list;
		}

		public List<L1ObjectAmount<Integer>> getAmountAidMeterialList() {
			return _aid_material_list;
		}

		public List<L1ObjectAmount<Integer>> getAmountRandomItemList() {
			return _random_item_list;
		}

		public int getAmountActionID() {
			return _actionId;
		}

		public L1NpcAction getAmountSuceedAction() {
			return _succeed_action;
		}

		public String getSystemMessage() {
			return _system_message;
		}

		public int getSystemMessageID() {
			return _system_messageId;
		}

		public L1NpcAction getFailAction() {
			return _fail_action;
		}

		public String getFailMessage() {
			return _fail_system_message;
		}

		public int getFailMessageID() {
			return _fail_system_messageId;
		}

		public int getSucceedRandom() {
			return _successrandom;
		}

		public void setSucceedRandom(final int succeedRandom) {
			_successrandom = succeedRandom;
		}

		public ServerBasePacket getPacket() {
			return _serverbased;
		}

		public void setPack(final ServerBasePacket sPacket) {
			_serverbased = sPacket;
		}

		public IntRange getAmountLevelRange() {
			return _level_range;
		}

		public void setAmountLevelRange(final IntRange levelIntRange) {
			_level_range = levelIntRange;
		}

		public IntRange getAmountLawfulRange() {
			return _lawful_range;
		}

		public void setAmountLawfulRange(final IntRange lawfulIntRange) {
			_lawful_range = lawfulIntRange;
		}

		public IntRange getAmountKarmaRange() {
			return _karma_range;
		}

		public void setAmountKarmaRange(final IntRange karmaIntRange) {
			_karma_range = karmaIntRange;
		}

		public boolean isAmountBroad() {
			return _broadcast;
		}

		public int getAmountRandom() {
			return _random_tmp;
		}

		public void setAmountRandom(final int randomValue) {
			_random_tmp = randomValue;
		}

		public int getFailRandom() {
			return _fail_random_tmp;
		}

		public void setFailRandom(final int failRandomValue) {
			_fail_random_tmp = failRandomValue;
		}

		public List<L1ObjectAmount<Integer>> getFailItemList() {
			return _fail_item_list;
		}

		public List<L1ObjectAmount<Integer>> getFailAmountRandomItemList() {
			return _fail_random_item_list;
		}

		public int[] getAmountNpcList() {
			return _npcId;
		}
	}
}