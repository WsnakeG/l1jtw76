package com.lineage.data;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import com.lineage.server.utils.SQLUtil;

/**
 * Event(活動設置) 模組相關
 * 
 * @author dexc
 */
public class EventClass {

	private static final Log _log = LogFactory.getLog(EventClass.class);

	// EVENT 執行類清單
	private static final Map<Integer, EventExecutor> _classList = new HashMap<Integer, EventExecutor>();

	private static EventClass _instance;

	public static EventClass get() {
		if (_instance == null) {
			_instance = new EventClass();
		}
		return _instance;
	}

	/**
	 * 加入CLASS清單
	 * 
	 * @param npcid
	 * @param className
	 */
	public void addList(final int eventid, final String className) {
		if (className.equals("0")) {
			return;
		}
		try {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("com.lineage.data.event.");
			stringBuilder.append(className);

			final Class<?> cls = Class.forName(stringBuilder.toString());
			final EventExecutor exe = (EventExecutor) cls.getMethod("get").invoke(null);

			_classList.put(new Integer(eventid), exe);

		} catch (final ClassNotFoundException e) {
			final String error = "發生[Event(活動設置)檔案]錯誤, 檢查檔案是否存在:" + className + " EventId:" + eventid;
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
	 * EVENT啟動
	 * 
	 * @param event
	 */
	public void startEvent(final L1Event event) {
		try {
			// CLASS執行位置取回
			final EventExecutor exe = _classList.get(new Integer(event.get_eventid()));
			if (exe != null) {
				exe.execute(event);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public final void updateEventNextTime(final int event_id, final Timestamp next_time) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE server_event SET next_time=? WHERE id=?");
			pstm.setTimestamp(1, next_time);
			pstm.setInt(2, event_id);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
