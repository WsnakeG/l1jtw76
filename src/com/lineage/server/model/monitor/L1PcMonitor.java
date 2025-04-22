package com.lineage.server.model.monitor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;

/**
 * L1PcInstanceの定期処理、監視処理等を行う為の共通的な処理を実装した抽象クラス 各タスク処理は{@link #run()}ではなく
 * {@link #execTask(L1PcInstance)}にて実装する。
 * PCがログアウトするなどしてサーバ上に存在しなくなった場合、run()メソッドでは即座にリターンする。
 * その場合、タスクが定期実行スケジューリングされていたら、ログアウト処理等でスケジューリングを停止する必要がある。
 * 停止しなければタスクは止まらず、永遠に定期実行されることになる。 定期実行でなく単発アクションの場合はそのような制御は不要。
 * L1PcInstanceの参照を直接持つことは望ましくない。
 * 
 * @author frefre
 */
public abstract class L1PcMonitor implements Runnable {

	/** モニター対象L1PcInstanceのオブジェクトID */
	protected int _id;

	/**
	 * 指定されたパラメータでL1PcInstanceに対するモニターを作成する。
	 * 
	 * @param oId {@link L1PcInstance#getId()}で取得できるオブジェクトID
	 */
	public L1PcMonitor(final int oId) {
		_id = oId;
	}

	@Override
	public final void run() {
		final L1PcInstance pc = (L1PcInstance) World.get().findObject(_id);
		if ((pc == null) || (pc.getNetConnection() == null)) {
			return;
		}
		execTask(pc);
	}

	/**
	 * タスク実行時の処理
	 * 
	 * @param pc モニター対象のPC
	 */
	public abstract void execTask(L1PcInstance pc);
}
