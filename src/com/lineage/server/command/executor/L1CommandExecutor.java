package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * コマンド実行処理インターフェース コマンド処理クラスは、このインターフェースメソッド以外に<br>
 * public static L1CommandExecutor getInstance()<br>
 * を実装しなければならない。
 * 通常、自クラスをインスタンス化して返すが、必要に応じてキャッシュされたインスタンスを返したり、他のクラスをインスタンス化して返すことができる。
 */
public interface L1CommandExecutor {

	/**
	 * このコマンドを実行する。
	 * 
	 * @param pc 実行者
	 * @param cmdName 実行されたコマンド名
	 * @param arg 引数
	 */
	public void execute(L1PcInstance pc, String cmdName, String arg);
}
