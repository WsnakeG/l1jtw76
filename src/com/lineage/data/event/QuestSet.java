package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.QuestTable;
import com.lineage.server.datatables.QuesttSpawnTable;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.quest.ChapterQuestTimer2;
import com.lineage.server.timecontroller.quest.QuestTimer;

/**
 * 任務/副本系統
 * 
 * @author daien
 */
public class QuestSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(QuestSet.class);

	public static boolean ISQUEST = false;// 任務/副本系統啟動

	/**
	 *
	 */
	private QuestSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new QuestSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			ISQUEST = true;

			// 任務設置啟動
			QuestTable.get().load();

			// 任務設置召喚啟動
			if (QuestTable.get().size() > 0) {
				QuesttSpawnTable.get().load();
			}

			// 啟動時間限制 時間軸
			final QuestTimer questTimer = new QuestTimer();
			questTimer.start();

			// 啟動副本任務檢查時間軸
			final ChapterQuestTimer2 questTimer2 = new ChapterQuestTimer2();
			questTimer2.start();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
