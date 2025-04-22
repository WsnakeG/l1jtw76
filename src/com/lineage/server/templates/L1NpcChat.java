package com.lineage.server.templates;

/**
 * NPC 說話資料
 * 
 * @author daien
 */
public class L1NpcChat {
	public L1NpcChat() {
	}

	private int _npcId;

	public int getNpcId() {
		return _npcId;
	}

	public void setNpcId(final int i) {
		_npcId = i;
	}

	private int _chatTiming;

	public int getChatTiming() {
		return _chatTiming;
	}

	public void setChatTiming(final int i) {
		_chatTiming = i;
	}

	private int _startDelayTime;

	public int getStartDelayTime() {
		return _startDelayTime;
	}

	public void setStartDelayTime(final int i) {
		_startDelayTime = i;
	}

	private String _chatId1;

	public String getChatId1() {
		return _chatId1;
	}

	public void setChatId1(final String s) {
		_chatId1 = s;
	}

	private String _chatId2;

	public String getChatId2() {
		return _chatId2;
	}

	public void setChatId2(final String s) {
		_chatId2 = s;
	}

	private String _chatId3;

	public String getChatId3() {
		return _chatId3;
	}

	public void setChatId3(final String s) {
		_chatId3 = s;
	}

	private String _chatId4;

	public String getChatId4() {
		return _chatId4;
	}

	public void setChatId4(final String s) {
		_chatId4 = s;
	}

	private String _chatId5;

	public String getChatId5() {
		return _chatId5;
	}

	public void setChatId5(final String s) {
		_chatId5 = s;
	}

	private int _chatInterval;

	public int getChatInterval() {
		return _chatInterval;
	}

	public void setChatInterval(final int i) {
		_chatInterval = i;
	}

	private boolean _isShout;

	public boolean isShout() {
		return _isShout;
	}

	public void setShout(final boolean flag) {
		_isShout = flag;
	}

	private boolean _isWorldChat;

	public boolean isWorldChat() {
		return _isWorldChat;
	}

	public void setWorldChat(final boolean flag) {
		_isWorldChat = flag;
	}

	private boolean _isRepeat;

	public boolean isRepeat() {
		return _isRepeat;
	}

	public void setRepeat(final boolean flag) {
		_isRepeat = flag;
	}

	private int _repeatInterval;

	public int getRepeatInterval() {
		return _repeatInterval;
	}

	public void setRepeatInterval(final int i) {
		_repeatInterval = i;
	}

	private int _gameTime;

	public int getGameTime() {
		return _gameTime;
	}

	public void setGameTime(final int i) {
		_gameTime = i;
	}

}
