package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1Pet;

/**
 * 寵物資料表
 * 
 * @author dexc
 */
public interface PetStorage {

	public void load();

	public void storeNewPet(L1NpcInstance pet, int objid, int itemobjid);

	public void storePet(L1Pet pet);

	public void deletePet(int itemobjid);

	public boolean isNameExists(String nameCaseInsensitive);

	/**
	 * 寵物資料
	 * 
	 * @param itemobjid 項圈OBJID
	 * @return
	 */
	public L1Pet getTemplate(int itemobjid);

	/**
	 * 寵物資料
	 * 
	 * @param npcobjid 寵物OBJID
	 * @return
	 */
	public L1Pet getTemplateX(final int npcobjid);

	public L1Pet[] getPetTableList();
	
	public abstract void buyNewPet(int petNpcId, int i, int id, int upLv, long lvExp);
}
