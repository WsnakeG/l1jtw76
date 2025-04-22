package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.PetTable;
import com.lineage.server.datatables.storage.PetStorage;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1Pet;

/**
 * 寵物資料表
 * 
 * @author dexc
 */
public class PetReading {

	private final Lock _lock;

	private final PetStorage _storage;

	private static PetReading _instance;

	private PetReading() {
		_lock = new ReentrantLock(true);
		_storage = new PetTable();
	}

	public static PetReading get() {
		if (_instance == null) {
			_instance = new PetReading();
		}
		return _instance;
	}

	public void load() {
		_lock.lock();
		try {
			_storage.load();
		} finally {
			_lock.unlock();
		}
	}

	public void storeNewPet(final L1NpcInstance pet, final int objid, final int itemobjid) {
		_lock.lock();
		try {
			_storage.storeNewPet(pet, objid, itemobjid);
		} finally {
			_lock.unlock();
		}
	}

	public void storePet(final L1Pet pet) {
		_lock.lock();
		try {
			_storage.storePet(pet);
		} finally {
			_lock.unlock();
		}
	}

	public void deletePet(final int itemobjid) {
		_lock.lock();
		try {
			_storage.deletePet(itemobjid);
		} finally {
			_lock.unlock();
		}
	}

	public boolean isNameExists(final String nameCaseInsensitive) {
		_lock.lock();
		boolean tmp;
		try {
			tmp = _storage.isNameExists(nameCaseInsensitive);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	public L1Pet getTemplate(final int itemobjid) {
		_lock.lock();
		L1Pet tmp;
		try {
			tmp = _storage.getTemplate(itemobjid);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 寵物資料
	 * 
	 * @param npcobjid 寵物OBJID
	 * @return
	 */
	public L1Pet getTemplateX(final int npcobjid) {
		_lock.lock();
		L1Pet tmp;
		try {
			tmp = _storage.getTemplateX(npcobjid);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	public L1Pet[] getPetTableList() {
		_lock.lock();
		L1Pet[] tmp;
		try {
			tmp = _storage.getPetTableList();
		} finally {
			_lock.unlock();
		}
		return tmp;
	}
	
	public void buyNewPet(int petNpcId, int i, int id, int upLv, long lvExp) {
		_lock.lock();
	    try {
	      _storage.buyNewPet(petNpcId, i, id, upLv, lvExp);
	    }
	    finally
	    {
	      _lock.unlock();
	    }
	  }
}
