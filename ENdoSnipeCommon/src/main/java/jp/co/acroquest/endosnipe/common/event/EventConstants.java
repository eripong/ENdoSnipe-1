/*******************************************************************************
 * ENdoSnipe 5.0 - (https://github.com/endosnipe)
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package jp.co.acroquest.endosnipe.common.event;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;

/**
 * イベント名、パラメータ名を定義するインタフェース。<br />
 * 
 * 定数名は、以下の命名規則に従うものとする。
 * <ul>
 * <li>NAME_イベント名</li>
 * <li>PARAM_イベント名_パラメータ名</li>
 * </ul>
 * 
 * @author eriguchi
 */
public interface EventConstants
{
    /** SQL実行回数超過イベントのイベント名。 */
    String NAME_SQLCOUNT = "SqlCountOver";

    /** SQL実行回数超過イベントでの呼び出した回数の検出値のパラメータ名。 */
    String PARAM_SQLCOUNT_ACTUAL = JavelinConfig.JAVELIN_PREFIX + "jdbc.sqlcount.acutual";

    /** SQL実行回数超過イベントでの呼び出した回数の閾値のパラメータ名。 */
    String PARAM_SQLCOUNT_THRESHOLD = JavelinConfig.JAVELIN_PREFIX + "jdbc.sqlcount.threshold";

    /** SQL実行回数超過イベントでの呼び出したSQLの検出値のパラメータ名。 */
    String PARAM_SQLCOUNT_SQL = JavelinConfig.JAVELIN_PREFIX + "jdbc.sqlcount.sql";

    /** SQL実行回数超過イベントでのスタックトレースのパラメータ名。 */
    String PARAM_SQLCOUNT_STACKTRACE = JavelinConfig.JAVELIN_PREFIX + "jdbc.sqlcount.stackTrace";

    /** SQLのFull Scanイベントのイベント名。 */
    String NAME_FULL_SCAN = "FullScan";

    /** SQLのFull Scanを行っているテーブル名。 */
    String PARAM_FULL_SCAN_TABLE_NAME = JavelinConfig.JAVELIN_PREFIX + "jdbc.fullScan.tableName";

    /** SQLのFull Scanを行った時のStack Trace。 */
    String PARAM_FULL_SCAN_STACK_TRACE = JavelinConfig.JAVELIN_PREFIX + "jdbc.fullScan.stackTrace";

    /** SQLのFull Scanを行った時のSQL文。 */
    String PARAM_FULL_SCAN_SQL = JavelinConfig.JAVELIN_PREFIX + "jdbc.fullScan.sql";

    /** SQLのFull Scanを行った時の実行計画。 */
    String PARAM_FULL_SCAN_EXEC_PLAN = JavelinConfig.JAVELIN_PREFIX + "jdbc.fullScan.execPlan";

    /** SQLのFull Scanを行った時のSQL実行時間。 */
    String PARAM_FULL_SCAN_DURATION = JavelinConfig.JAVELIN_PREFIX + "jdbc.fullScan.duration";

    /** メソッド呼び出し間隔超過イベントのイベント名。 */
    String NAME_INTERVALERROR = "IntervalError";

    /** メソッド呼び出し間隔超過イベントのクラス名。 */
    String PARAM_INTERVALERROR_CLASSNAME = "javelin.interval.classname";

    /** メソッド呼び出し間隔超過イベントのメソッド名。 */
    String PARAM_INTERVALERROR_METHODNAME = "javelin.interval.methodname";

    /** メソッド呼び出し間隔超過イベントの引数の値（末尾に "1" 、 "2" 等がつく）。 */
    String PARAM_INTERVALERROR_ARGUMENTS = "javelin.interval.arguments.";

    /** メソッド呼び出し間隔超過イベントの呼び出し間隔検出値(ms)。 */
    String PARAM_INTERVALERROR_ACTUAL_INTERVAL = "javelin.interval.actual";

    /** メソッド呼び出し間隔超過イベントの呼び出し間隔閾値(ms)。 */
    String PARAM_INTERVALERROR_THRESHOLD = "javelin.interval.threshold";

    /** メソッド呼び出し間隔超過イベントの呼び出し時のスタックトレース。 */
    String PARAM_INTERVALERROR_STACKTRACE = "javelin.interval.stackTrace";

    /** メモリリーク検出イベントのメソッド名。 */
    String NAME_LEAK_DETECTED = "LeakDetected";

    /** メモリリーク検出イベントの識別子のパラメータ名。 */
    String PARAM_LEAK_IDENTIFIER = "javelin.leak.identifier";

    /** メモリリーク検出イベントの閾値のパラメータ名 */
    String PARAM_LEAK_THRESHOLD = "javelin.leak.threshold";

    /** メモリリーク検出イベントの数のパラメータ名 */
    String PARAM_LEAK_COUNT = "javelin.leak.count";

    /** メモリリーク検出イベントのサイズのパラメータ名 */
    String PARAM_LEAK_SIZE = "javelin.leak.size";

    /** メモリリーク検出イベントの追加されたオブジェクトのクラス名 */
    String PARAM_LEAK_CLASS_NAME = "javelin.leak.className";

    /** メモリリーク検出イベントのスタックトレースのパラメータ名。 */
    String PARAM_LEAK_STACK_TRACE = "javelin.leak.stackTrace";

    /** 複数スレッドからのアクセスイベントのイベント名 */
    String NAME_CONCURRENT_ACCESS = "ConcurrentAccess";

    /** 複数スレッドからのアクセスイベントの識別子のパラメータ名 */
    String PARAM_CONCURRENT_IDENTIFIER = "javelin.concurrent.identifier";

    /** 複数スレッドからのアクセスイベントのスレッド名のパラメータ名 */
    String PARAM_CONCURRENT_THREAD = "javelin.concurrent.thread";

    /** 複数スレッドからのアクセスイベントの時刻のパラメータ名 */
    String PARAM_CONCURRENT_TIME = "javelin.concurrent.time";

    /** 複数スレッドからのアクセスイベントのスレッド名のパラメータ名 */
    String PARAM_CONCURRENT_LOCK = "javelin.concurrent.lock";

    /** 複数スレッドからのアクセスイベントのエラーのパラメータ名 */
    String PARAM_CONCURRENT_ERROR = "javelin.concurrent.error";

    /** 複数スレッドからのアクセスイベントのスタックトレースのパラメータ名 */
    String PARAM_CONCURRENT_STACKTRACE = "javelin.concurrent.stackTrace";

    /** タイムアウト未設定検出イベントのメソッド名 */
    String NAME_NOTIMEOUT_DETECTED = "NoTimeoutDetected";

    /** タイムアウト未設定検出イベントの識別子のパラメータ名。 */
    String NOTIMEOUT_IDENTIFIER = "javelin.notimeout.identifier";

    /** タイムアウト未設定検出イベントのタイムアウト値のパラメータ名。 */
    String NOTIMEOUT_TIMEOUT = "javelin.notimeout.timeout";

    /** タイムアウト未設定検出イベントのアドレスのパラメータ名。 */
    String NOTIMEOUT_ADDRESS = "javelin.notimeout.address";

    /** タイムアウト未設定検出イベントのポート番号のパラメータ名。 */
    String NOTIMEOUT_PORT = "javelin.notimeout.port";

    /** 複数スレッドからのアクセスイベントのスタックトレースのパラメータ名 */
    String NOTIMEOUT_STACKTRACE = "javelin.notimeout.stackTrace";

    /** フルスレッドダンプ取得のイベント名 */
    String NAME_THREAD_DUMP = "FullThreadDump";

    /** フルスレッドダンプ取得イベントのパラメータ名 */
    String PARAM_THREAD_DUMP = "javelin.thread.dump";

    /** フルスレッドダンプ取得時のCPU使用率のパラメータ名 */
    String PARAM_THREAD_DUMP_CPU_TOTAL = "javelin.thread.dump.cpu.total";

    /** フルスレッドダンプ取得時のCPU使用率のパラメータ名 */
    String PARAM_THREAD_DUMP_CPU = "javelin.thread.dump.cpu";

    /** フルスレッドダンプ取得時のスレッド数のパラメータ名 */
    String PARAM_THREAD_DUMP_THREADNUM = "javelin.thread.dump.threadNum";

    /** フルスレッドダンプ取得イベントのスタックトレースのパラメータ名 */
    String PARAM_THREAD_DUMP_STACKTRACE = "javelin.thread.dump.stackTrace";

    /** クラスヒストグラムダンプ取得のイベント名 */
    String NAME_CLASSHISTOGRAM = "ClassHistogram";

    /** クラスヒストグラムダンプ取得のイベント名 */
    String PARAM_CLASSHISTOGRAM = "ClassHistogram";

    /** フルGC取得のイベント名 */
    String NAME_FULLGC = "FullGCDetected";

    /** リファレンスダンプ取得のイベント名 */
    String NAME_REFERENCE_DUMP = "ReferenceDump";

    /** リファレンスダンプ取得イベントのパラメータ名 */
    String PARAM_REFERENCE_DUMP = "javelin.reference.dump";

    /** フルGC発生回数のパラメータ名 */
    String PARAM_FULLGC_COUNT = "javelin.fullgc.count";

    /** フルGC実行時間を表すパラメータ名 */
    String PARAM_FULLGC_TIME = "javelin.fullgc.time";

    /** フルGCを検出したときのヒープメモリのパラメータ名 */
    String PARAM_FULLGC_HEAPMEMORY = "javelin.fullgc.heapMemory";

    /** 例外検出イベントのメソッド名 */
    String NAME_EXCEPTION_DETECTED = "ExceptionDetected";

    /** 例外検出イベントの識別子のパラメータ名。 */
    String EXCEPTION_IDENTIFIER = "javelin.exception.identifier";

    /** 例外検出イベントのスタックトレースのパラメータ名。 */
    String EXCEPTION_STACKTRACE = "javelin.exception.stackTrace";

    /** CallTreeが最大値に達したときのイベント名 */
    String NAME_CALLTREE_FULL = "CallTreeFull";

    /** Invocationが最大値に達したときのイベント名 */
    String NAME_INVOCATION_FULL = "InvocationFull";

    /** CallTreeの最大値 */
    String PARAM_CALLTREE = "javelin.call.tree";

    /** CallTreeが最大値に達したときの<クラス名>#<メソッド名> */
    String PARAM_CALLTREE_METHOD = "javelin.call.tree.method";

    /** CallTreeが最大値に達したときのスタックトレース */
    String PARAM_CALLTREE_STACKTRACE = "javelin.call.tree.stackTrace";

    /** Invocationの最大値 */
    String PARAM_INVOCATION = "javelin.invocation";

    /** Invocationが最大値に達したときの<クラス名> */
    String PARAM_INVOCATION_CLASS = "javelin.invocation.class";

    /** Invocationが最大値に達したときに追加した<メソッド名> */
    String PARAM_INVOCATION_METHOD_ADD = "javelin.invocation.method.add";

    /** Invocationが最大値に達したときに削除した<メソッド名> */
    String PARAM_INVOCATION_METHOD_REMOVE = "javelin.invocation.method.remove";

    /** Invocationが最大値に達したときのスタックトレース */
    String PARAM_INVOCATION_STACKTRACE = "javelin.invocation.stackTrace";

    /** イベント発生のクラス名 */
    String EVENT_CLASSNAME = "EVENT_JAVELIN_CLASS";

    /** イベント発生のメソッド名 */
    String EVENT_METHODNAME = "EVENT_JAVELIN_METHOD";

    /** ブロック継続検出時のイベント名 */
    String EVENT_THREAD_BLOCK_CONTINUE = "ThreadBlockContinue";

    /** ブロック継続検出時、ロック保持スレッド情報のパラメータ名 */
    String PARAM_THREAD_MONITOR_OWNER = "thread.monitor.owner";

    /** ブロック継続検出時、ロック待ちスレッド情報のパラメータ名 */
    String PARAM_THREAD_MONITOR_THREAD = "thread.monitor.thread";

    /** ブロック継続検出時、連続ブロック時間のパラメータ名 */
    String PARAM_THREAD_BLOCK_DURATION = "thread.block.duration";

    /** スレッドリーク検出時のイベント名 */
    String EVENT_THREAD_LEAK_CONTINUE = "ThreadLeakContinue";

    /** スレッドリーク検出タイミングのパラメータ名 */
    String PARAM_THREAD_LEAK_TIMING = "thread.leak.timing";

    /** スレッドリーク検出時のスレッドIDのパラメータ名 */
    String PARAM_THREAD_LEAK_ID = "thread.leak.id";

    /** スレッドリーク検出スレッドのパラメータ名 */
    String PARAM_THREAD_LEAK_NAME = "thread.leak.name";

    /** スレッド状態のパラメータ名 */
    String PARAM_THREAD_LEAK_STATE = "thread.leak.state";

    /** スレッドリーク検出時のスタックトレースのパラメータ名 */
    String PARAM_THREAD_LEAK_STACKTRACE = "thread.leak.stackTrace";

    /** 線形検索検時のイベント名 */
    String NAME_LINEARSEARCH_DETECTED = "LinearSearchDetected";

    /** 線形検索検出したリストのサイズ */
    String PARAM_LINEARSEARCH_SIZE = "linearsearch.size";

    /** 線形検索検出したリストの検索回数 */
    String PARAM_LINEARSEARCH_COUNT = "linearsearch.count";

    /** 線形検索検出したリストのオブジェクトID */
    String PARAM_LINEARSEARCH_OBJECTID = "linearsearch.objectID";

    /** 線形検索検出時のスタックトレース */
    String PARAM_LINEARSEARCH_STACKTRACE = "linearsearch.stackTrace";

    /** Log4Jによるエラーログ出力時のイベント名 */
    String NAME_LOG4JERROR_DETECTED = "Log4jErrorDetected";

    /** Log4Jによるエラーログ出力時のログレベル */
    String PARAM_LOG4JERROR_LOGLEVEL = "log4jerror.logLevel";

    /** Log4Jによるエラーログ出力時のログメッセージ */
    String PARAM_LOG4JERROR_LOGMESSAGE = "log4jerror.logMessage";

    /** Log4Jによるエラーログ出力時の例外メッセージ */
    String PARAM_LOG4JERROR_EXCLASS = "log4jerror.exClass";

    /** Log4Jによるエラーログ出力時の例外メッセージ */
    String PARAM_LOG4JERROR_EXMESSAGE = "log4jerror.exMessage";

    /** Log4Jによるエラーログ出力時の例外スタックトレース */
    String PARAM_LOG4JERROR_EXSTACKTRACE = "log4jerror.exStackTrace";

    /** Log4Jによるエラーログ出力時のスタックトレース */
    String PARAM_LOG4JERROR_STACKTRACE = "log4jerror.stackTrace";

    /** デッドロック検出時のイベント名 */
    String NAME_DEADLOCK_DETECTED = "DeadLockDetected";

    /** デッドロック検出時のスレッド情報 */
    String PARAM_DEADLOCK_INFO = "thread.deadlock.info.";

    /** CommonsPool検出イベントのメソッド名。 */
    String NAME_COMMONSPOOL_INIT = "CommonsPoolInitialize";

    /** CommonsPoolのオブジェクトID */
    String PARAM_COMMONSPOOL_OBJECTID = "javelin.commons.pool.objectID";

    /** CommonsPoolのスタックトレース */
    String PARAM_COMMONSPOOL_STACKTRACE = "javelin.commons.pool.stackTrace";

    /** 強制切断検出時のイベント名 */
    String NAME_FORCE_DISCONNECTED = "ForceDisconnected";

    /** HTTPステータスエラーのイベント名 */
    String NAME_HTTP_STATUS_ERROR = "HttpStatusError";

    /**  HTTPステータスエラーのURL */
    String PARAM_HTTP_URL = "javelin.commons.pool.stackTrace";

    /**  HTTPステータスエラーのURL */
    String PARAM_HTTP_STATUS = "http.status";

    /**  HTTPステータスエラーのURL */
    String PARAM_HTTP_THROWABLE_MESSAGE = "http.throwable.message";

    /**  HTTPステータスエラーのURL */
    String PARAM_HTTP_THROWABLE_STACKTRACE = "http.throwable.stacktrace";

    /** ストールメソッド検出時のイベント名 */
    String EVENT_METHOD_STALL = "MethodStall";

    /** ストールメソッド検出イベントの閾値のパラメータ名 */
    String PARAM_METHOD_STALL_THRESHOLD = "javelin.method.stall.threshold";

    /** ストールメソッド検出イベントのクラス名のパラメータ名 */
    String PARAM_METHOD_STALL_CLASS_NAME = "javelin.method.stall.className";

    /** ストールメソッド検出イベントのメソッド名のパラメータ名 */
    String PARAM_METHOD_STALL_METHOD_NAME = "javelin.method.stall.methodName";

    /** ストールメソッド検出イベントのスレッドIDのパラメータ名 */
    String PARAM_METHOD_STALL_THREAD_ID = "javelin.method.stall.threadId";

    /** ストールメソッド検出イベントのスレッド名のパラメータ名 */
    String PARAM_METHOD_STALL_THREAD_NAME = "javelin.method.stall.threadName";

    /** ストールメソッド検出イベントのスレッド状態のパラメータ名 */
    String PARAM_METHOD_STALL_THREAD_STATE = "javelin.method.stall.threadState";

    /** ストールメソッド検出イベントのスタックトレースのパラメータ名 */
    String PARAM_METHOD_STALL_STACKTRACE = "javelin.method.stall.stackTrace";
}
