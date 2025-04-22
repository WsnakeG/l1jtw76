package com.lineage.data;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Quest;

/**
 * Quest(任務) 模組相關
 * 
 * @author dexc
 */
public class QuestClass {

	private static final Log _log = LogFactory.getLog(QuestClass.class);

	// Quest 執行類清單<QuestId, 執行類位置>
	private static final Map<Integer, QuestExecutor> _classList = new HashMap<Integer, QuestExecutor>();

	private static QuestClass _instance;

	public static QuestClass get() {
		if (_instance == null) {
			_instance = new QuestClass();
		}
		return _instance;
	}

	/**
	 * 加入CLASS清單
	 * 
	 * @param npcid
	 * @param className
	 */
	public void addList(final int questid, final String className) {
		if (className.equals("0")) {
			return;
		}
		try {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("com.lineage.data.quest.");
			stringBuilder.append(className);

			final Class<?> cls = Class.forName(stringBuilder.toString());
			final QuestExecutor exe = (QuestExecutor) cls.getMethod("get").invoke(null);

			_classList.put(new Integer(questid), exe);

		} catch (final ClassNotFoundException e) {
			final String error = "發生[Quest(任務)檔案]錯誤, 檢查檔案是否存在:" + className + " QuestId:" + questid;
			_log.error(error);
			DataError.isError(_log, error, e);

		} catch (final IllegalArgumentException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final IllegalAccessException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final InvocationTargetException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final SecurityException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final NoSuchMethodException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Quest(任務) 設置啟用執行
	 * 
	 * @param event
	 */
	public void execute(final L1Quest quest) {
		try {
			// CLASS執行位置取回
			final QuestExecutor exe = _classList.get(new Integer(quest.get_id()));
			if (exe != null) {
				exe.execute(quest);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Quest(任務) 設置執行<BR>
	 * 設置任務開始執行<BR>
	 * 相關NPC可與執行者進行對話<BR>
	 * 
	 * @param pc
	 * @param questid
	 */
	public void startQuest(final L1PcInstance pc, final int questid) {
		try {
			// CLASS執行位置取回
			final QuestExecutor exe = _classList.get(new Integer(questid));
			if (exe != null) {
				exe.startQuest(pc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Quest(任務) 設置結束<BR>
	 * 假設該任務可以重複執行<BR>
	 * 在此設置任務狀態移除<BR>
	 * 
	 * @param pc
	 * @param questid
	 */
	public void endQuest(final L1PcInstance pc, final int questid) {
		try {
			// CLASS執行位置取回
			final QuestExecutor exe = _classList.get(new Integer(questid));
			if (exe != null) {
				exe.endQuest(pc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 展示任務說明<BR>
	 * 
	 * @param pc
	 * @param questid
	 */
	public void showQuest(final L1PcInstance pc, final int questid) {
		try {
			// CLASS執行位置取回
			final QuestExecutor exe = _classList.get(new Integer(questid));
			if (exe != null) {
				exe.showQuest(pc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 任務終止
	 * 
	 * @param pc
	 * @param questid
	 */
	public void stopQuest(final L1PcInstance pc, final int questid) {
		try {
			// CLASS執行位置取回
			final QuestExecutor exe = _classList.get(new Integer(questid));
			if (exe != null) {
				exe.stopQuest(pc);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
