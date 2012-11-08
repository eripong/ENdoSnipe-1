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
package jp.co.acroquest.endosnipe.common.parser;

/**
 * Javelin ログのための定数インターフェースです。<br />
 * 
 * @author y-komori
 */
public interface JavelinLogConstants
{
    /**
     * 詳細情報取得キー:ThreadMXBean#getCurrentThreadCpuTimeパラメータ
     * 現在のスレッドの合計 CPU 時間をナノ秒単位で返します。
     */
    String JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME = "thread.currentThreadCpuTime";

    /**
     * 詳細情報取得キー:ThreadMXBean#getCurrentThreadCpuTimeパラメータの差分
     * 現在のスレッドの合計 CPU 時間の差分をナノ秒単位で返します。
     */
    String JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME_DELTA = "thread.currentThreadCpuTime.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getCurrentThreadUserTimeパラメータ
     * 現在のスレッドがユーザモードで実行した CPU 時間 (ナノ秒単位) を返します。
     */
    String JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME = "thread.currentThreadUserTime";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getCurrentThreadUserTimeパラメータの差分
     * 現在のスレッドがユーザモードで実行した CPU 時間 (ナノ秒単位) の差分を返します。
     */
    String JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME_DELTA = "thread.currentThreadUserTime.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getBlockedCountパラメータ
     * この ThreadInfo に関連するスレッドが、モニターに入るか、再入するのをブロックした合計回数を返します。
     */
    String JMXPARAM_THREAD_THREADINFO_BLOCKED_COUNT = "thread.threadInfo.blockedCount";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getBlockedCountパラメータの差分
     * この ThreadInfo に関連するスレッドが、モニターに入るか、再入するのをブロックした合計回数の差分を返します。
     */
    String JMXPARAM_THREAD_THREADINFO_BLOCKED_COUNT_DELTA = "thread.threadInfo.blockedCount.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getBlockedTimeパラメータ
     * スレッドコンテンション監視が有効になってから、この ThreadInfo に関連するスレッドがモニターに入るか
     * 再入するのをブロックしたおよその累積経過時間 (ミリ秒単位) を返します。
     */
    String JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME = "thread.threadInfo.blockedTime";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getBlockedTimeパラメータの差分
     * スレッドコンテンション監視が有効になってから、この ThreadInfo に関連するスレッドがモニターに入るか
     * 再入するのをブロックしたおよその累積経過時間 (ミリ秒単位) の差分を返します。
     */
    String JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME_DELTA = "thread.threadInfo.blockedTime.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getWaitedCountパラメータ
     * この ThreadInfo に関連するスレッドが通知を待機した合計回数を返します。
     */
    String JMXPARAM_THREAD_THREADINFO_WAITED_COUNT = "thread.threadInfo.waitedCount";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getWaitedCountパラメータの差分
     * この ThreadInfo に関連するスレッドが通知を待機した合計回数の差分を返します。
     */
    String JMXPARAM_THREAD_THREADINFO_WAITED_COUNT_DELTA = "thread.threadInfo.waitedCount.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getWaitedTimeパラメータ
     * スレッドコンテンション監視が有効になってから、この ThreadInfo に関連するスレッドが通知を待機した
     * およその累積経過時間 (ミリ秒単位) を返します。
     */
    String JMXPARAM_THREAD_THREADINFO_WAITED_TIME = "thread.threadInfo.waitedTime";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getWaitedTimeパラメータの差分
     * スレッドコンテンション監視が有効になってから、この ThreadInfo に関連するスレッドが通知を待機した
     * およその累積経過時間 (ミリ秒単位) の差分を返します。
     */
    String JMXPARAM_THREAD_THREADINFO_WAITED_TIME_DELTA = "thread.threadInfo.waitedTime.delta";

    /** 
     * 詳細情報取得キー:GarbageCollectorMXBean#getCollectionCountパラメータ
     *発生したコレクションの合計数を返します。
     */
    String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT = "garbageCollector.collectionCount";

    /** 
     * 詳細情報取得キー:GarbageCollectorMXBean#getCollectionCountパラメータの差分
     * 発生したコレクションの合計数を返します。
     */
    String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT_DELTA =
            "garbageCollector.collectionCount.delta";

    /** 
     * 詳細情報取得キー:GarbageCollectorMXBean#getCollectionTimeパラメータ
     * コレクションのおよその累積経過時間 (ミリ秒単位) を返します。
     */
    String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME = "garbageCollector.collectionTime";

    /** 
     * 詳細情報取得キー:GarbageCollectorMXBean#getCollectionTimeパラメータの差分
     * コレクションのおよその累積経過時間 (ミリ秒単位) を返します。
     */
    String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME_DELTA =
            "garbageCollector.collectionTime.delta";

    /** 
     * 詳細情報取得キー:MemoryPoolMXBean#getPeakUsage#getUsageパラメータ
     * Java 仮想マシンが起動されてから、またはピークがリセットされてからの、このメモリプールのピークメモリ使用量を返します
     */
    String JMXPARAM_MEMORYPOOL_PEAKUSAGE_USAGE = "memoryPool.peakUsage.usage";

    /** 
     * 詳細情報取得キー:MemoryPoolMXBean#getPeakUsage#getUsageパラメータの差分
     * Java 仮想マシンが起動されてから、またはピークがリセットされてからの、このメモリプールのピークメモリ使用量を返します
     */
    String JMXPARAM_MEMORYPOOL_PEAKUSAGE_USAGE_DELTA = "memoryPool.peakUsage.usage.delta";

    /** 
     * 詳細情報取得キー:MemoryMXBean#getHeapMemoryUsageパラメータ
     * メソッド呼び出し時のヒープメモリ使用量を取得します。
     */
    String JMXPARAM_MEMORY_HEAPMEMORYUSAGE_START = "memory.heapMemoryUsage.start";

    /** 
     * 詳細情報取得キー:MemoryMXBean#getHeapMemoryUsageパラメータ
     * メソッド終了時のヒープメモリ使用量を取得します。
     */
    String JMXPARAM_MEMORY_HEAPMEMORYUSAGE_END = "memory.heapMemoryUsage.end";

    /** 
     * 詳細情報取得キー:メソッドのTAT
     */
    String EXTRAPARAM_DURATION = "duration";

    /**
     * 詳細情報取得キー：メソッドの消費時間（子メソッド呼び出し時間を差し引いたメソッド実行時間）
     */
    String EXTRAPARAM_ELAPSEDTIME = "elapsedTime";

    /** 
     * 詳細情報取得キー:メソッドのCPU時間（子メソッド呼び出しCPU時間を差し引いたメソッドCPU時間）
     */
    String EXTRAPARAM_PURECPUTIME = "pureCpuTimeDelta";

    /** 
     * 詳細情報取得キー:メソッドのWait時間（子メソッド呼び出しWait時間を差し引いたメソッドCPU時間）
     */
    String EXTRAPARAM_PUREWAITEDTIME = "pureWaitedTimeDelta";

    /** 
     * 詳細情報取得キー:メソッドのUSER時間（子メソッド呼び出しUSER時間を差し引いたメソッドUSER時間）
     */
    String EXTRAPARAM_PUREUSERTIME = "pureUserTimeDelta";

    /** 
     * 詳細情報取得キー:CPUのアイドル時間（実行時間からCPU時間を引いた時間）
     */
    String EXTRAPARAM_IDLETIME = "IdleTime";

    /** 
     * 詳細情報取得キー:処理実施中のファイル入力量
     */
    String IOPARAM_DISK_INPUT = "file.currentFileReadLength";

    /** 
     * 詳細情報取得キー:処理実施中のファイル出力量
     */
    String IOPARAM_DISK_OUTPUT = "file.currentFileWriteLength";

    /** 
     * 詳細情報取得キー:処理実施中のネットワーク入力量
     */
    String IOPARAM_NETWORK_INPUT = "net.currentThreadReadLength";

    /** 
     * 詳細情報取得キー:処理実施中のネットワーク出力量
     */
    String IOPARAM_NETWORK_OUTPUT = "net.currentThreadWriteLength";

    /** 例外生成タグ。*/
    String JAVELIN_EXCEPTION = "<<javelin.Exception>>";

    /** スタックトレース出力の開始タグ。*/
    String JAVELIN_STACKTRACE_START = "<<javelin.StackTrace_START>>";

    /** スタックトレース出力の終了タグ。*/
    String JAVELIN_STACKTRACE_END = "<<javelin.StackTrace_END>>";

    /** フィールド値出力の開始タグ。*/
    String JAVELIN_FIELDVALUE_START = "<<javelin.FieldValue_START>>";

    /** フィールド値出力の終了タグ。*/
    String JAVELIN_FIELDVALUE_END = "<<javelin.FieldValue_END>>";

    /** 戻り値出力の開始タグ。*/
    String JAVELIN_RETURN_START = "<<javelin.Return_START>>";

    /** 戻り値出力の終了タグ。*/
    String JAVELIN_RETURN_END = "<<javelin.Return_END>>";

    /** 引数出力の開始タグ。*/
    String JAVELIN_ARGS_START = "<<javelin.Args_START>>";

    /** 引数出力の終了タグ。*/
    String JAVELIN_ARGS_END = "<<javelin.Args_END>>";

    /** JMXにより取得したVMの状態出力の開始タグ。*/
    String JAVELIN_JMXINFO_START = "<<javelin.JMXInfo_START>>";

    /** JMXにより取得したVMの状態出力の終了タグ。*/
    String JAVELIN_JMXINFO_END = "<<javelin.JMXInfo_END>>";

    /** 追加情報出力の開始タグ。*/
    String JAVELIN_EXTRAINFO_START = "<<javelin.ExtraInfo_START>>";

    /** 追加情報出力の終了タグ。*/
    String JAVELIN_EXTRAINFO_END = "<<javelin.ExtraInfo_END>>";

    /** イベントパラメータ出力の開始タグ。*/
    String JAVELIN_EVENTINFO_START = "<<javelin.EventInfo_START>>";

    /** イベントパラメータ出力の終了タグ。*/
    String JAVELIN_EVENTINFO_END = "<<javelin.EventInfo_END>>";

    /** 動作ログ出力日時のフォーマット。*/
    String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss.SSS";

    /** イベントの警告レベル：Info */
    String EVENT_INFO = "INFO";

    /** イベントの警告レベル：Warn */
    String EVENT_WARN = "WARN";

    /** イベントの警告レベル：Warn */
    String EVENT_ERROR = "ERROR";

    /** ログ出力時、parentNodeが存在しない場合のデフォルトメソッド名称 */
    String DEFAULT_LOGMETHOD = "unknown";

}
