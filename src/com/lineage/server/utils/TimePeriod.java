package com.lineage.server.utils;

import java.sql.Time;

import com.lineage.server.model.gametime.L1GameTime;

public class TimePeriod {

	private final Time _timeStart;

	private final Time _timeEnd;

	public TimePeriod(final Time timeStart, final Time timeEnd) {
		if (timeStart.equals(timeEnd)) {
			throw new IllegalArgumentException("timeBegin must not equals timeEnd");
		}

		_timeStart = timeStart;
		_timeEnd = timeEnd;
	}

	private boolean includes(final L1GameTime time, final Time timeStart, final Time timeEnd) {
		final Time when = time.toTime();
		return (timeStart.compareTo(when) <= 0) && (0 < timeEnd.compareTo(when));
	}

	public boolean includes(final L1GameTime time) {
		/*
		 * 分かりづらいロジック・・・ timeStart after timeEndのとき(例:18:00~06:00)
		 * timeEnd~timeStart(06:00~18:00)の範囲内でなければ、
		 * timeStart~timeEnd(18:00~06:00)の範囲内と見なせる
		 */
		return _timeStart.after(_timeEnd) ? !this.includes(time, _timeEnd, _timeStart)
				: this.includes(time, _timeStart, _timeEnd);
	}
}
