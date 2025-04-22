package com.lineage.server.templates;

/**
 * 地圖資料暫存
 * 
 * @author daien
 */
public class MapData {

	public int mapId = 0;

	public String location = "";// 怪物隨機地圖產生位置資訊
	
	public int startX = 0;

	public int endX = 0;

	public int startY = 0;

	public int endY = 0;

	public double monster_amount = 1;

	public double dropRate = 1;

	public boolean isUnderwater = false;

	public boolean markable = false;

	public boolean teleportable = false;

	public boolean escapable = false;

	public boolean isUseResurrection = false;

	public boolean isUsePainwand = false;

	public boolean isEnabledDeathPenalty = false;

	public boolean isTakePets = false;

	public boolean isRecallPets = false;

	public boolean isUsableItem = false;

	public boolean isUsableSkill = false;

	public int isUsableShop;
	
	public boolean isArrows = false;
	
	public boolean isAutoBot = false;
	
}
