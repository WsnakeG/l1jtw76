package com.lineage.server.templates;

/**
 * 管理者命令緩存
 * 
 * @author daien
 */
public class L1Command {

	private final String _name;

	private final boolean _system;

	private final int _level;

	private final String _executorClassName;

	private final String _note;

	/**
	 * 管理者命令緩存
	 * 
	 * @param name
	 * @param system
	 * @param level
	 * @param executorClassName
	 * @param note
	 */
	public L1Command(final String name, final boolean system, final int level, final String executorClassName,
			final String note) {
		_name = name;
		_system = system;
		_level = level;
		_executorClassName = executorClassName;
		_note = note;
	}

	/**
	 * 命令
	 * 
	 * @return
	 */
	public String getName() {
		return _name;
	}

	/**
	 * 系統可執行命令
	 * 
	 * @return
	 */
	public boolean isSystem() {
		return _system;
	}

	/**
	 * 執行管理等級
	 * 
	 * @return
	 */
	public int getLevel() {
		return _level;
	}

	/**
	 * 命令執行類
	 * 
	 * @return
	 */
	public String getExecutorClassName() {
		return _executorClassName;
	}

	/**
	 * 備註
	 * 
	 * @return
	 */
	public String get_note() {
		return _note;
	}
}
