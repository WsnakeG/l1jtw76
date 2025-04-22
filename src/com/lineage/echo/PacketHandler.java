package com.lineage.echo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.echo.encryptions.PacketPrint;
import com.lineage.server.clientpackets.C_AddBookmark;
import com.lineage.server.clientpackets.C_AddBuddy;
import com.lineage.server.clientpackets.C_Amount;
import com.lineage.server.clientpackets.C_Attack;
import com.lineage.server.clientpackets.C_AttackBow;
import com.lineage.server.clientpackets.C_AttackHandler;
import com.lineage.server.clientpackets.C_Attr;
import com.lineage.server.clientpackets.C_AuthLogin;
import com.lineage.server.clientpackets.C_AutoLogin;
import com.lineage.server.clientpackets.C_BanClan;
import com.lineage.server.clientpackets.C_BanParty;
import com.lineage.server.clientpackets.C_Board;
import com.lineage.server.clientpackets.C_BoardBack;
import com.lineage.server.clientpackets.C_BoardDelete;
import com.lineage.server.clientpackets.C_BoardRead;
import com.lineage.server.clientpackets.C_BoardWrite;
import com.lineage.server.clientpackets.C_Buddy;
import com.lineage.server.clientpackets.C_CallPlayer;
import com.lineage.server.clientpackets.C_ChangeHeading;
import com.lineage.server.clientpackets.C_CharReset;
import com.lineage.server.clientpackets.C_CharcterConfig;
import com.lineage.server.clientpackets.C_Chat;
import com.lineage.server.clientpackets.C_ChatGlobal;
import com.lineage.server.clientpackets.C_ChatWhisper;
import com.lineage.server.clientpackets.C_CheckPK;
import com.lineage.server.clientpackets.C_Clan;
import com.lineage.server.clientpackets.C_ClientReady;
import com.lineage.server.clientpackets.C_CommonClick;
import com.lineage.server.clientpackets.C_CreateChar;
import com.lineage.server.clientpackets.C_CreateClan;
import com.lineage.server.clientpackets.C_CreateParty;
import com.lineage.server.clientpackets.C_DelBuddy;
import com.lineage.server.clientpackets.C_DeleteBookmark;
import com.lineage.server.clientpackets.C_DeleteChar;
import com.lineage.server.clientpackets.C_DeleteInventoryItem;
import com.lineage.server.clientpackets.C_Deposit;
import com.lineage.server.clientpackets.C_Disconnect;
import com.lineage.server.clientpackets.C_Door;
import com.lineage.server.clientpackets.C_Drawal;
import com.lineage.server.clientpackets.C_DropItem;
import com.lineage.server.clientpackets.C_Emblem;
import com.lineage.server.clientpackets.C_EnterPortal;
import com.lineage.server.clientpackets.C_Exclude;
import com.lineage.server.clientpackets.C_ExitGhost;
import com.lineage.server.clientpackets.C_ExtraCommand;
import com.lineage.server.clientpackets.C_Fight;
import com.lineage.server.clientpackets.C_FishClick;
import com.lineage.server.clientpackets.C_FixWeaponList;
import com.lineage.server.clientpackets.C_GiveItem;
import com.lineage.server.clientpackets.C_ItemCraft;
import com.lineage.server.clientpackets.C_ItemUSe;
import com.lineage.server.clientpackets.C_JoinClan;
import com.lineage.server.clientpackets.C_KeepALIVE;
import com.lineage.server.clientpackets.C_LeaveClan;
import com.lineage.server.clientpackets.C_LeaveParty;
import com.lineage.server.clientpackets.C_LoginToServer;
import com.lineage.server.clientpackets.C_LoginToServerOK;
import com.lineage.server.clientpackets.C_Mail;
import com.lineage.server.clientpackets.C_MoveChar;
import com.lineage.server.clientpackets.C_NPCAction;
import com.lineage.server.clientpackets.C_NPCTalk;
import com.lineage.server.clientpackets.C_NewCharSelect;
import com.lineage.server.clientpackets.C_Party;
import com.lineage.server.clientpackets.C_PetMenu;
import com.lineage.server.clientpackets.C_PickUpItem;
import com.lineage.server.clientpackets.C_Pledge;
import com.lineage.server.clientpackets.C_PledgeContent;
import com.lineage.server.clientpackets.C_Propose;
import com.lineage.server.clientpackets.C_Rank;
import com.lineage.server.clientpackets.C_Restart;
import com.lineage.server.clientpackets.C_Result;
import com.lineage.server.clientpackets.C_SelectList;
import com.lineage.server.clientpackets.C_SelectTarget;
import com.lineage.server.clientpackets.C_ServerVersion;
import com.lineage.server.clientpackets.C_Ship;
import com.lineage.server.clientpackets.C_Shop;
import com.lineage.server.clientpackets.C_ShopList;
import com.lineage.server.clientpackets.C_SkillBuy;
import com.lineage.server.clientpackets.C_SkillBuyItem;
import com.lineage.server.clientpackets.C_SkillBuyItemOK;
import com.lineage.server.clientpackets.C_SkillBuyOK;
import com.lineage.server.clientpackets.C_TaxRate;
import com.lineage.server.clientpackets.C_Teleport;
import com.lineage.server.clientpackets.C_Title;
import com.lineage.server.clientpackets.C_Trade;
import com.lineage.server.clientpackets.C_TradeAddItem;
import com.lineage.server.clientpackets.C_TradeCancel;
import com.lineage.server.clientpackets.C_TradeOK;
import com.lineage.server.clientpackets.C_UnLock;
import com.lineage.server.clientpackets.C_Unkonwn;
import com.lineage.server.clientpackets.C_UsePetItem;
import com.lineage.server.clientpackets.C_UseSkill;
import com.lineage.server.clientpackets.C_War;
import com.lineage.server.clientpackets.C_Who;
import com.lineage.server.clientpackets.C_Windows;
import com.lineage.server.clientpackets.ClientBasePacket;

/**
 * 客戶端封包處理
 * 
 * @author dexc
 */
public class PacketHandler extends PacketHandlerExecutor {

	private static final Log _log = LogFactory.getLog(PacketHandler.class);

	// Map<K,V>
	private static final Map<Integer, ClientBasePacket> _opListClient = new HashMap<Integer, ClientBasePacket>();

	private final ClientExecutor _client;

	/**
	 * 客戶端封包處理
	 * 
	 * @param decrypt
	 * @param object
	 * @throws Exception
	 */
	@Override
	public void handlePacket(final byte[] decrypt) {
		ClientBasePacket basePacket = null;
		if (decrypt == null) {
			return;
		}
		if (decrypt.length <= 0) {
			return;
		}

		// 一般的處理封包方式
		final int i = decrypt[0] & 0xff;
		basePacket = _opListClient.get(i);
		if (Config.DEBUG) {
			// if (Config.DEBUG&&key==C_OPCODE_MAIL) {
			if (basePacket != null && i != 88 && i != 91) {
				_log.info("客戶端: " + basePacket.getType() + "\nOP ID: " + i
						+ " length:" + decrypt.length + "\nInfo:\n"
						+ PacketPrint.get().printData(decrypt, decrypt.length));
			}
		}

		// try {
		// System.out.println("[Client] opcode = " + i);
		if (i == C_OPCODE_CHARRESET) {
			new C_CharReset().start(decrypt, _client);
		} else if (i == C_OPCODE_EXCLUDE) {
			new C_Exclude().start(decrypt, _client);
		} else if (i == C_OPCODE_CHARACTERCONFIG) {
			new C_CharcterConfig().start(decrypt, _client);
		} else if (i == C_OPCODE_DOOR) {
			new C_Door().start(decrypt, _client);
		} else if (i == C_OPCODE_TITLE) {
			new C_Title().start(decrypt, _client);
		} else if (i == C_OPCODE_BOARDDELETE) {
			new C_BoardDelete().start(decrypt, _client);
		} else if (i == C_OPCODE_PLEDGE) {
			new C_Pledge().start(decrypt, _client);
		} else if (i == C_OPCODE_CHANGEHEADING) {
			new C_ChangeHeading().start(decrypt, _client);
		} else if (i == C_OPCODE_NPCACTION) {
			new C_NPCAction().start(decrypt, _client);
		} else if (i == C_OPCODE_USESKILL) {
			new C_UseSkill().start(decrypt, _client);
		} else if (i == C_OPCODE_CLAN) { // XXX C_OPCODE_EMBLEMDOWNLOAD 修正為
											// C_OPCODE_CLAN
			new C_Clan().start(decrypt, _client);
		} else if (i == C_OPCODE_EMBLEM) { // XXX C_OPCODE_EMBLEMUPLOAD 修正為
											// C_OPCODE_EMBLEM
			new C_Emblem().start(decrypt, _client);
		} else if (i == C_OPCODE_TRADEADDCANCEL) {
			new C_TradeCancel().start(decrypt, _client);
		} else if (i == C_OPCODE_ADDBOOKMARK) { // XXX C_OPCODE_BOOKMARK 修正為
												// C_OPCODE_ADDBOOKMARK
			new C_AddBookmark().start(decrypt, _client);
		} else if (i == C_OPCODE_CREATECLAN) {
			new C_CreateClan().start(decrypt, _client);
		} else if (i == C_OPCODE_CLIENTVERSION) {
			new C_ServerVersion().start(decrypt, _client);
		} else if (i == C_OPCODE_PROPOSE) {
			new C_Propose().start(decrypt, _client);
		} else if (i == C_OPCODE_BOARDBACK) {
			new C_BoardBack().start(decrypt, _client);
		} else if (i == C_OPCODE_SHOP) {
			new C_Shop().start(decrypt, _client);
		} else if (i == C_OPCODE_BOARDREAD) {
			new C_BoardRead().start(decrypt, _client);
		} else if (i == C_OPCODE_TRADE) {
			new C_Trade().start(decrypt, _client);
		} else if (i == C_OPCODE_DELETECHAR) {
			new C_DeleteChar().start(decrypt, _client);
		} else if (i == C_OPCODE_ATTR) {
			new C_Attr().start(decrypt, _client);
		} else if (i == C_OPCODE_AUTHLOGIN) { // XXX C_OPCODE_LOGINPACKET 修正為
												// C_OPCODE_AUTHLOGIN
			new C_AuthLogin().start(decrypt, _client);
		} else if (i == C_OPCODE_RESULT) {
			new C_Result().start(decrypt, _client);
		} else if (i == C_OPCODE_DEPOSIT) {
			new C_Deposit().start(decrypt, _client);
		} else if (i == C_OPCODE_DRAWAL) {
			new C_Drawal().start(decrypt, _client);
		} else if (i == C_OPCODE_LOGINTOSERVEROK) {
			new C_LoginToServerOK().start(decrypt, _client);
		} else if (i == C_OPCODE_SKILLBUY) {
			new C_SkillBuy().start(decrypt, _client);
		} else if (i == C_OPCODE_SKILLBUYOK) {
			new C_SkillBuyOK().start(decrypt, _client);
		} else if (i == C_OPCODE_SKILLBUYITEM) {
			new C_SkillBuyItem().start(decrypt, _client);
		} else if (i == C_OPCODE_SKILLBUYOKITEM) { // XXX
													// C_OPCODE_SKILLBUYITEMOK
													// 修正為
													// C_OPCODE_SKILLBUYOKITEM
			new C_SkillBuyItemOK().start(decrypt, _client);
		} else if (i == C_OPCODE_TRADEADDITEM) {
			new C_TradeAddItem().start(decrypt, _client);
		} else if (i == C_OPCODE_ADDBUDDY) {
			new C_AddBuddy().start(decrypt, _client);
		} else if (i == C_OPCODE_CHAT) {
			new C_Chat().start(decrypt, _client);
		} else if (i == C_OPCODE_TRADEADDOK) {
			new C_TradeOK().start(decrypt, _client);
		} else if (i == C_OPCODE_CHECKPK) {
			new C_CheckPK().start(decrypt, _client);
		} else if (i == C_OPCODE_TAXRATE) {
			new C_TaxRate().start(decrypt, _client);
		} else if (i == C_OPCODE_CHANGECHAR) {
			new C_NewCharSelect().start(decrypt, _client);
		} else if (i == C_OPCODE_BUDDYLIST) {
			new C_Buddy().start(decrypt, _client);
		} else if (i == C_OPCODE_DROPITEM) {
			new C_DropItem().start(decrypt, _client);
		} else if (i == C_OPCODE_LEAVEPARTY) {
			new C_LeaveParty().start(decrypt, _client);
		} else if (i == C_OPCODE_ATTACK) {
			new C_Attack().start(decrypt, _client);
		} else if (i == C_OPCODE_ARROWATTACK) {
			new C_AttackBow().start(decrypt, _client);
		} else if (i == C_OPCODE_BANCLAN) {
			new C_BanClan().start(decrypt, _client);
		} else if (i == C_OPCODE_BOARD) {
			new C_Board().start(decrypt, _client);
		} else if (i == C_OPCODE_DELETEINVENTORYITEM) {
			new C_DeleteInventoryItem().start(decrypt, _client);
		} else if (i == C_OPCODE_CHATWHISPER) {
			new C_ChatWhisper().start(decrypt, _client);
		} else if (i == C_OPCODE_PARTYLIST) {
			new C_Party().start(decrypt, _client);
		} else if (i == C_OPCODE_PICKUPITEM) {
			new C_PickUpItem().start(decrypt, _client);
		} else if (i == C_OPCODE_WHO) {
			new C_Who().start(decrypt, _client);
		} else if (i == C_OPCODE_GIVEITEM) {
			new C_GiveItem().start(decrypt, _client);
		} else if (i == C_OPCODE_MOVECHAR) {
			new C_MoveChar().start(decrypt, _client);
		} else if (i == C_OPCODE_BOOKMARKDELETE) {
			new C_DeleteBookmark().start(decrypt, _client);
		} else if (i == C_OPCODE_RESTART) {
			new C_Restart().start(decrypt, _client);
		} else if (i == C_OPCODE_LEAVECLANE) {
			new C_LeaveClan().start(decrypt, _client);
		} else if (i == C_OPCODE_NPCTALK) {
			new C_NPCTalk().start(decrypt, _client);
		} else if (i == C_OPCODE_BANPARTY) {
			new C_BanParty().start(decrypt, _client);
		} else if (i == C_OPCODE_DELBUDDY) {
			new C_DelBuddy().start(decrypt, _client);
		} else if (i == C_OPCODE_WAR) {
			new C_War().start(decrypt, _client);
		} else if (i == C_OPCODE_LOGINTOSERVER) {
			new C_LoginToServer().start(decrypt, _client);
		} else if (i == C_OPCODE_PRIVATESHOPLIST) {
			new C_ShopList().start(decrypt, _client);
		} else if (i == C_OPCODE_CHATGLOBAL) {
			new C_ChatGlobal().start(decrypt, _client);
		} else if (i == C_OPCODE_JOINCLAN) {
			new C_JoinClan().start(decrypt, _client);
		} else if (i == C_OPCODE_COMMONCLICK) {
			new C_CommonClick().start(decrypt, _client);
		} else if (i == C_OPCODE_NEWCHAR) {
			new C_CreateChar().start(decrypt, _client);
		} else if (i == C_OPCODE_EXTCOMMAND) {
			new C_ExtraCommand().start(decrypt, _client);
		} else if (i == C_OPCODE_BOARDWRITE) {
			new C_BoardWrite().start(decrypt, _client);
		} else if (i == C_OPCODE_USEITEM) {
			new C_ItemUSe().start(decrypt, _client);
		} else if (i == C_OPCODE_CREATEPARTY) {
			new C_CreateParty().start(decrypt, _client);
		} else if (i == C_OPCODE_ENTERPORTAL) {
			new C_EnterPortal().start(decrypt, _client);
		} else if (i == C_OPCODE_AMOUNT) {
			new C_Amount().start(decrypt, _client);
		} else if (i == C_OPCODE_FIX_WEAPON_LIST) {
			new C_FixWeaponList().start(decrypt, _client);
		} else if (i == C_OPCODE_SELECTLIST) {
			new C_SelectList().start(decrypt, _client);
		} else if (i == C_OPCODE_EXIT_GHOST) {
			new C_ExitGhost().start(decrypt, _client);
		} else if (i == C_OPCODE_CALL) {
			new C_CallPlayer().start(decrypt, _client);
		} else if (i == C_OPCODE_SELECTTARGET) {
			new C_SelectTarget().start(decrypt, _client);
		} else if (i == C_OPCODE_PETMENU) {
			new C_PetMenu().start(decrypt, _client);
		} else if (i == C_OPCODE_USEPETITEM) {
			new C_UsePetItem().start(decrypt, _client);
		} else if (i == C_OPCODE_FIGHT) {
			new C_Fight().start(decrypt, _client);
		} else if (i == C_OPCODE_MAIL) {
			new C_Mail().start(decrypt, _client);
		} else if (i == C_OPCODE_SHIP) {
			new C_Ship().start(decrypt, _client);
		} else if (i == C_OPCODE_RANK) {
			new C_Rank().start(decrypt, _client);
		} else if (i == C_OPCODE_TELEPORT) {
			new C_Teleport().start(decrypt, _client);
		} else if (i == C_OPCODE_TELEPORTLOCK) {
			new C_UnLock().start(decrypt, _client);
		} else if (i == C_OPCODE_KEEPALIVE) {
			new C_KeepALIVE().start(decrypt, _client);
		} else if (i == C_OPCODE_MAPSYSTEM) { // XXX C_OPCODE_SENDLOCATION 修正為
												// C_OPCODE_MAPSYSTEM
			new C_Windows().start(decrypt, _client);
		} else if (i == C_OPCODE_AUTOLOGIN) { // XXX C_OPCODE_BEANFUNLOGINPACKET
												// 修正為 C_OPCODE_AUTOLOGIN
			new C_AutoLogin().start(decrypt, _client);
		} else if (i == C_OPCODE_FISHCLICK) {
			new C_FishClick().start(decrypt, _client);
		} else if (i == C_OPCODE_QUITGAME) {
			new C_Disconnect().start(decrypt, _client);
		} else if (i == C_OPCODE_UPDATEPLEDGE_INFO) { // XXX
														// C_OPCODE_FIRE_SMITH_P
														// 修正為
														// C_OPCODE_UPDATEPLEDGE_INFO
			new C_PledgeContent().start(decrypt, _client);
		} else if (i == C_OPCODE_CRAFTSYSTEM) { // XXX
												// C_OPCODE_EXTENDED_PROTOBUF
												// 修正為 C_OPCODE_CRAFTSYSTEM
			new C_ItemCraft().start(decrypt, _client);
		} else if (i == C_OPCODE_ATTACKRUNING) { // XXX C_OPCODE_ATTACK_CONTINUE
													// 修正為 C_OPCODE_ATTACKRUNING
			new C_AttackHandler().start(decrypt, _client);
		} else if (i == C_OPCODE_CLIENT_READY) {
			new C_ClientReady().start(decrypt, _client);
			// } else if (i == C_TELEPORT) { // XXX 錯誤 沒有對應編號，所以刪除不用
			// new C_GMTeleport().start(decrypt, _client);
		} else {
			new C_Unkonwn().start(decrypt, _client);
		}
	}

	public PacketHandler(final ClientExecutor client) {
		_client = client;
	}

	/**
	 * 設置執行類
	 */
	public static void put(final Integer key, final ClientBasePacket value) {
		if (_opListClient.get(key) == null) {
			_opListClient.put(key, value);

		} else {
			if (!key.equals(-1)) {
				_log.error("重複標記的OPID: " + key + " " + value.getType());
			}
		}
	}
}
