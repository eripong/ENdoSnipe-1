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
package jp.co.acroquest.endosnipe.common.config;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;

/**
 * Javelinの設定を保持するためのクラスです。<br />
 * 
 * @author eriguchi
 */
public class JavelinConfigBase
{
    /** スレッドモデルの値：スレッドID */
    public static final int TM_THREAD_ID = 1;

    /** スレッドモデルの値：スレッド名 */
    public static final int TM_THREAD_NAME = 2;

    /** スレッドモデルの値： */
    public static final int TM_CONTEXT_PATH = 3;

    /** Javelin系パラメータの接頭辞 */
    public static final String JAVELIN_PREFIX = "javelin.";

    /**
     * ConcurrentAccessMonitorやCollectionMonitor動作中にクラスロードが起ったときに、
     * バイトコード変換を行うかどうかを設定するプロパティ（バイトコード変換を行わない場合は <code>true</code> ） */
    public static final String SKIPCLASS_ONPROCESSING_KEY = JAVELIN_PREFIX
            + "skipClassOnProcessing";

    /** メソッド平均時間を出力するために記録するInvocation数のプロパティ */
    public static final String INTERVALMAX_KEY = JAVELIN_PREFIX + "intervalMax";

    /** 例外の数を記録するためのInvocation数のプロパティ */
    public static final String THROWABLEMAX_KEY = JAVELIN_PREFIX + "throwableMax";

    /** Javelinの情報取得対象から除外する、最大Bytecode長のプロパティ */
    public static final String BYTECODE_EXCLUDE_LENGTH_KEY = JAVELIN_PREFIX
            + "bytecode.exclude.length";

    /** Javelinの情報取得対象から除外する、最大の制御命令数のプロパティ */
    public static final String BYTECODE_EXCLUDE_CONTROLCOUNT_MAX_KEY = JAVELIN_PREFIX
            + "bytecode.exclude.controlCount";

    /** Bytecodeの内容を元に計測対象から除外する際のポリシー。0:除外しない 1:BCIしない */
    public static final String BYTECODE_EXCLUDE_POLICY_KEY = JAVELIN_PREFIX
            + "bytecode.exclude.policy";

    /** メモリに保存する閾値のプロパティ */
    public static final String STATISTICSTHRESHOLD_KEY = JAVELIN_PREFIX + "statistics"
            + "Threshold";

    /** アラームを通知するTATの閾値のプロパティ */
    public static final String ALARMTHRESHOLD_KEY = JAVELIN_PREFIX + "alarmThreshold";

    /** Javelinログを出力するファイル名のプロパティ */
    public static final String JAVELINFILEDIR_KEY = JAVELIN_PREFIX + "javelinFileDir";

    /** スタックトレースを出力するかどうかを決定するプロパティ */
    public static final String LOG_STACKTRACE_KEY = JAVELIN_PREFIX + "log.stacktrace";

    /** 引数情報を出力するかどうかを決定するプロパティ */
    public static final String LOG_ARGS_KEY = JAVELIN_PREFIX + "log.args";

    /** スレッドコンテンション監視を行うかどうかを決定するプロパティ */
    public static final String THREAD_CONTENTION_KEY = JAVELIN_PREFIX + "thread.contention.monitor";

    /** JMXInfoを出力するかどうか決定するプロパティ */
    public static final String LOG_MBEANINFO_KEY = JAVELIN_PREFIX + "log.mbeaninfo";

    /** 端点で、JMXInfoを出力するかどうか決定するプロパティ */
    public static final String LOG_MBEANINFO_ROOT_KEY = JAVELIN_PREFIX + "log.mbeaninfo.root";

    /** 戻り値を出力するかどうかを決定するプロパティ */
    public static final String LOG_RETURN_KEY = JAVELIN_PREFIX + "log.return";

    /** 引数の詳細情報を出力するかどうかを決定するプロパティ */
    public static final String ARGS_DETAIL_KEY = JAVELIN_PREFIX + "log.args.detail";

    /** 戻り値の詳細情報を出力するかどうかを決定するプロパティ */
    public static final String RETURN_DETAIL_KEY = JAVELIN_PREFIX + "log.return.detail";

    /** 引数の詳細情報の深さを表すプロパティ */
    public static final String ARGS_DETAIL_DEPTH_KEY = JAVELIN_PREFIX + "log.args."
            + "detail.depth";

    /** 戻り値の詳細情報の深さを表すプロパティ */
    public static final String RETURN_DETAIL_DEPTH_KEY = JAVELIN_PREFIX + "log.return."
            + "detail.depth";

    /** 呼び出し元が不明のときに設定する名前のプロパティ */
    public static final String ROOTCALLERNAME_KEY = JAVELIN_PREFIX + "rootCallerName";

    /** 最も深い呼び出し先が不明のときに設定する名前のプロパティ */
    public static final String ENDCALLEENAME_KEY = JAVELIN_PREFIX + "endCalleeName";

    /** スレッドの名称の決定方法を表すプロパティ */
    public static final String THREADMODEL_KEY = JAVELIN_PREFIX + "threadModel";

    /** BottleNeckEye/DataCollectorとの通信用ポートのプロパティ名 */
    public static final String ACCEPTPORT_KEY = JAVELIN_PREFIX + "acceptPort";

    /** BottleNeckEye/DataCollectorとの通信用ポートを範囲指定するか、のプロパティ名称 */
    public static final String ACCEPTPORT_ISRANGE = ACCEPTPORT_KEY + ".isRange";

    /** BottleNeckEye/DataCollectorとの通信用ポートを範囲指定する際の最大値、のプロパティ名称 */
    public static final String ACCEPTPORT_RANGEMAX = ACCEPTPORT_KEY + ".rangeMax";

    /** JavelinAcceptThreadでのaccept処理の開始を遅らせる時間(ミリ秒)プロパティ名称(隠しパラメータ) */
    public static final String ACCEPT_DELAY_KEY = JAVELIN_PREFIX + "accept.delay";

    /** JavelinからBottleNeckEye/DataCollectorへの通信用ホスト名のプロパティ名 */
    public static final String CONNECTHOST_KEY = JAVELIN_PREFIX + "connectHost";

    /** JavelinからBottleNeckEye/DataCollectorへの通信用ポートのプロパティ名 */
    public static final String CONNECTPORT_KEY = JAVELIN_PREFIX + "connectPort";

    /** DataCollectorのデータベース名 */
    public static final String DATABASENAME_KEY = JAVELIN_PREFIX + "databaseName";

    /** 属性、戻り値情報の文字列長 */
    public static final String STRINGLIMITLENGTH_KEY = JAVELIN_PREFIX + "stringLimitLength";

    /** システムトレースファイルのプロパティ名 */
    public static final String SYSTEMLOG_KEY = JAVELIN_PREFIX + "system.log";

    /** ヒープダンプのプロパティ名 */
    public static final String HEAPDUMPDIR_KEY = JAVELIN_PREFIX + "heapDumpDir";

    /** 利用するAlarmListener名 */
    public static final String ALARM_LISTENERS_KEY = JAVELIN_PREFIX + "alarmListeners";

    /** JMX通信による情報公開を行うかどうかを表すプロパティ名 */
    public static final String RECORD_JMX_KEY = JAVELIN_PREFIX + "record.jmx";

    /** jvnログファイルの最大数を表すプロパティ名 */
    public static final String LOG_JVN_MAX_KEY = JAVELIN_PREFIX + "log.jvn.max";

    /** jvnログファイルを圧縮したzipファイルの最大数を表すプロパティ名 */
    public static final String LOG_ZIP_MAX_KEY = JAVELIN_PREFIX + "log.zip.max";

    /** 記録条件判定クラス */
    public static final String RECORDSTRATEGY_KEY = JAVELIN_PREFIX + "recordStrategy";

    /** 利用するTelegramListener名 */
    public static final String TELERAM_LISTENERS_KEY = JAVELIN_PREFIX + "telegramListeners";

    /** Javelinのシステムログの最大ファイル数のキー */
    private static final String SYSTEM_LOG_NUM_MAX_KEY = JAVELIN_PREFIX + "system.log.num.max";

    /** Javelinのシステムログの最大ファイルサイズのキー */
    private static final String SYSTEM_LOG_SIZE_MAX_KEY = JAVELIN_PREFIX + "system.log."
            + "size.max";

    /** Javelinのシステムログのログレベルのキー */
    private static final String SYSTEM_LOG_LEVEL_KEY = JAVELIN_PREFIX + "system.log.level";

    /** Javelinのイベントレベルのキー */
    public static final String EVENT_LEVEL_KEY = JAVELIN_PREFIX + "event.level";

    /** MBeanManagerが持つ情報をシリアライズするファイル名 */
    public static final String SERIALIZE_FILE_KEY = JAVELIN_PREFIX + "serializeFile";

    /** 保存するCallTree数のプロパティ */
    public static final String CALL_TREE_MAX_KEY = JAVELIN_PREFIX + "call.tree.max";

    /** アプリケーション実行時の例外を出力するかどうかを決定するプロパティ */
    public static final String ALARM_EXCEPTION_KEY = JAVELIN_PREFIX + "alarmException";

    /** HTTPステータスエラーを出力するかどうかを決定するプロパティ */
    public static final String HTTP_STATUS_ERROR_KEY = JAVELIN_PREFIX + "httpStatusError";

    /** １クラス辺り保持するInvocation（メソッド呼び出し）最大数のキー */
    public static final String RECORD_INVOCATION_MAX_KEY = JAVELIN_PREFIX
            + "record.invocation.num.max";

    /** Turn Around Timeを計測するかどうかを決定するプロパティのキー */
    public static final String TAT_ENABLED_KEY = JAVELIN_PREFIX + "tat.monitor";

    /** Turn Around Timeの保持期間を表すプロパティのキー */
    public static final String TAT_KEEP_TIME_KEY = JAVELIN_PREFIX + "tat.keepTime";

    /** Turn Around Timeの値が 0 の場合に、0 の出力を */
    public static final String TAT_ZERO_KEEP_TIME_KEY = JAVELIN_PREFIX + "tat.zeroKeepTime";

    /** アラーム送信間隔の最小値。前回アラーム送信・Javelinログ出力を行った際から
     * 経過した時間がこの閾値を超えていた場合のみアラーム送信・Javelinログ出力を行う。*/
    public static final String ALARM_MINIMUM_INTERVAL_KEY = JAVELIN_PREFIX + "minimumAlarmInterval";

    /** 同一のイベントを検出する間隔。前回のイベントからこの時間経過している場合のみ、イベントを検出する。 */
    public static final String EVENT_INTERVAL_KEY = JAVELIN_PREFIX + "eventInterval";

    /** 複数スレッド同時アクセスを計測するかどうかを決定するプロパティのキー */
    public static final String CONCURRENT_ENABLED_KEY = JAVELIN_PREFIX + "concurrent.monitor";

    /** タイムアウト値設定の監視を行うかどうか */
    public static final String TIMEOUT_MONITOR = JAVELIN_PREFIX + "timeout.monitor";

    /** jvnログファイルを出力するかどうか。 */
    public static final String LOG_JVN_FILE = JAVELIN_PREFIX + "log.enable";

    /** BottleNeckEyeとの通信に使用するポートを再取得する間隔 */
    public static final String JAVELIN_BIND_INTERVAL = JAVELIN_PREFIX + "bind.interval";

    /** システムのリソースデータを取得するかどうか。 */
    public static final String COLLECT_SYSTEM_RESOURCES = JAVELIN_PREFIX
            + "resource.collectSystemResources";

    /** システムのリソースデータを取得するかどうか。 */
    public static final String ITEMNAME_PREFIX = JAVELIN_PREFIX + "resource.itemName.prefix";

    /** システムのリソースデータを取得するかどうか。 */
    public static final String ITEMNAME_NOPREFIX_LIST = JAVELIN_PREFIX
            + "resource.itemName.noPrefixList";

    /** システムのリソースデータを取得するかどうか。 */
    public static final String COLLECT_HADOOP_AGENT_RESOURCES = JAVELIN_PREFIX
            + "resource.collectHadoopAgentResources";

    /** HBaseのリソースデータを取得するかどうか。 */
    public static final String COLLECT_HBASE_AGENT_RESOURCES = JAVELIN_PREFIX
            + "resource.collectHBaseAgentResources";

    /** InvocationFullEventを送信するかどうか。 */
    public static final String SEND_INVOCATION_FULL_EVENT = JAVELIN_PREFIX
            + "record.invocation.sendFullEvent";

    /** JMXのリソースデータを取得するかどうか。 */
    public static final String COLLECT_JMX_RESOURCES = JAVELIN_PREFIX
            + "resource.collectJmxResources";

    /** ストールメソッドを監視するかどうか */
    private static final String METHOD_STALL_MONITOR = JAVELIN_PREFIX + "method.stall.monitor";

    /** ストールメソッドを監視する周期 */
    private static final String METHOD_STALL_INTERVAL = JAVELIN_PREFIX + "method.stall.interval";

    /** ストールメソッドと判断する閾値 */
    private static final String METHOD_STALL_THRESHOLD = JAVELIN_PREFIX + "method.stall.threshold";

    /** ストールメソッド検出時に出力するスタックトレースの深さ */
    private static final String METHOD_STALL_TRACE_DEPTH = JAVELIN_PREFIX
            + "method.stall.traceDepth";

    /** MBeanサーバのホスト名 */
    public static final String JMX_HOST = "javelin.jmx.host";

    /** MBeanサーバのポート番号 */
    public static final String JMX_PORT = "javelin.jmx.port";

    /** MBeanサーバの認証ユーザ名 */
    public static final String JMX_USER_NAME = "javelin.jmx.user";

    /** MBeanサーバの認証パスワード */
    public static final String JMX_PASSWORD = "javelin.jmx.password";

    /** 接続モード(server/client) */
    private static final String CONNECTION_MODE_KEY = JAVELIN_PREFIX + "connection.mode";

    /**
     * ConcurrentAccessMonitorやCollectionMonitor動作中にクラスロードが起ったときに、
     * バイトコード変換を行うかどうかを設定するプロパティのデフォルト値 */
    private static final boolean DEFAULT_SKIPCLASS_ONPROCESSING = true;

    /** 保存するCallTreeNode数のデフォルト値 */
    private static final int DEFAULT_CALL_TREE_MAX = 5000;

    /** CallTreeNodeの計測値保存閾値のデフォルト値 */
    public static final int DEFAULT_CALL_TREE_MAX_MEASURE = 2500;

    /** メソッド平均時間を出力するために記録するInvocation数のデフォルト値 */
    private static final int DEFAULT_INTERVALMAX = 500;

    /** 例外の数を記録するためのInvocation数のデフォルト値 */
    private static final int DEFAULT_THROWABLEMAX = 100;

    /** Bytecodeの内容を元に計測対象から除外する、最大bytecode長。 */
    public static final int DEFAULT_BYTECODE_LENGTH_MIN_KEY = 12;

    /** Javelinの情報取得対象から除外する、最大の制御命令数のプロパティ */
    public static final int DEFAULT_BYTECODE_EXCLUDE_CONTROLCOUNT_MAX_KEY = 0;

    /** Bytecodeの内容を元に計測対象から除外する際のポリシー。0:除外しない 1:BCIしない */
    public static final int DEFAULT_BYTECODE_EXCLUDE_POLICY_KEY = 1;

    /** メモリに保存する閾値のプロパティ */
    private static final long DEFAULT_STATISTICSTHRESHOLD = 0;

    /** アラームを通知するTATの閾値のプロパティ */
    private static final long DEFAULT_ALARMTHRESHOLD = 5000;

    /** 同一のイベントを検出する間隔のデフォルト値 */
    public static final long DEFAULT_EVENT_INTERVAL = 1000 * 60 * 60;

    /** Javelinログを出力するファイル名のプロパティ */
    private static final String DEFAULT_JAVELINFILEDIR = "../logs";

    /** スタックトレースを出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_STACKTRACE = false;

    /** 引数情報を出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_ARGS = true;

    /** 引数情報にセッション情報を出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_HTTP_SESSION = true;

    /** スレッドコンテンション監視を行うかどうか決定するデフォルト値 */
    private static final boolean DEFAULT_THREAD_CONTENTION = true;

    /** JMXInfoを出力するかどうか決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_MBEANINFO = true;

    /** JMXInfoを出力するかどうか決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_MBEANINFO_ROOT = true;

    /** 戻り値を出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_RETURN = true;

    /** 引数の詳細情報を出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_ARGS_DETAIL = false;

    /** 引数の詳細情報の深さのデフォルト値 */
    private static final int DEFAULT_ARGS_DETAIL_DEPTH = 1;

    /** 戻り値の詳細情報を出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_RETURN_DETAIL = false;

    /** 戻り値の詳細情報の深さのデフォルト値 */
    private static final int DEFAULT_RETURN_DETAIL_DEPTH = 1;

    /** HTTPセッションの詳細情報を出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_HTTP_SESSION_DETAIL = false;

    /** HTTPセッションの詳細情報の深さのデフォルト値 */
    private static final int DEFAULT_HTTP_SESSION_DETAIL_DEPTH = 1;

    /** 呼び出し元が不明のときに設定する名前のプロパティ */
    private static final String DEFAULT_ROOTCALLERNAME = "root";

    /** 最も深い呼び出し先が不明のときに設定する名前のプロパティ */
    private static final String DEFAULT_ENDCALLEENAME = "unknown";

    /** スレッドの名称の決定方法を表すプロパティ */
    private static final int DEFAULT_THREADMODEL = 0;

    /** 属性、戻り値情報の文字列長のデフォルト値 */
    private static final int DEFAULT_STRINGLIMITLENGTH = 102400;

    /** BottleNeckEye/DataCollectorとの通信用ポートのデフォルト値 */
    public static final int DEFAULT_ACCEPTPORT = 18000;

    /** JavelinAcceptThreadでのaccept処理の開始を遅らせる時間(ミリ秒)のデフォルト値 */
    public static final long DEFAULT_ACCEPT_DELAY = 0;

    /** BottleNeckEye/DataCollectorとの通信用ポートを範囲指定するか、のデフォルト値 */
    public static final boolean DEF_ACCEPTPORT_ISRANGE = false;

    /** BottleNeckEye/DataCollectorとの通信用ポートを範囲指定する際の最大値のデフォルト値 */
    public static final int DEF_ACCEPTPORT_RANGEMAX = 18010;

    /** BottleNeckEye/DataCollectorとの通信用ポートを範囲指定する際の最小値のデフォルト値 */
    public static final int DEF_ACCEPTPORT_RANGEMIN = 18000;

    /** JavelinからBottleNeckEye/DataCollectorへの通信用ホスト名のデフォルト値 */
    public static final String DEFAULT_CONNECTHOST = "localhost";

    /** JavelinからBottleNeckEye/DataCollectorへの通信用ポートのデフォルト値 */
    public static final int DEFAULT_CONNECTPORT = 19000;

    /** DataCollectorのデータベース名のデフォルト値 */
    public static final String DEFAULT_DATABASENAME = "endosnipedb";

    /** Javelinシステムログの出力先パスのデフォルト値 */
    public static final String DEFAULT_SYSTEMLOG = "../traces";

    /** ヒープダンプの出力先パスのデフォルト値 */
    public static final String DEFAULT_HEAPDUMP_DIR = "../heapdump";

    /** デフォルトで利用するAlarmListener名 */
    private static final String DEFAULT_ALARM_LISTENERS = "";

    /** デフォルトでJMX通信による情報公開を行うかどうか */
    private static final boolean DEFAULT_RECORD_JMX = true;

    /** jvnログファイルの最大数のデフォルト */
    private static final int DEFAULT_LOG_JVN_MAX = 256;

    /** jvnログファイルを圧縮したzipファイルの最大数のデフォルト */
    private static final int DEFAULT_LOG_ZIP_MAX = 256;

    /** 記録条件判定クラスのデフォルト */
    public static final String DEFAULT_RECORDSTRATEGY =
            "jp.co.acroquest.endosnipe.javelin.record.CpuTimeRecordStrategy";

    /** デフォルトで利用するTelegramListener名 */
    private static final String DEFAULT_TELEGEAM_LISTENERS =
            "jp.co.acroquest.endosnipe.javelin.communicate.GetRequestTelegramListener,"
                    + "jp.co.acroquest.endosnipe.javelin.communicate.ResetRequestTelegramListener";

    /** Javelinのシステムログの最大ファイル数のデフォルト */
    private static final int DEFAULT_SYSTEM_LOG_NUM_MAX = 16;

    /** Javelinのシステムログの最大ファイルサイズのデフォルト */
    private static final int DEFAULT_SYSTEM_LOG_SIZE_MAX = 1000000;

    /** MBeanManagerが持つ情報をシリアライズするファイル名のデフォルト */
    public static final String DEFAULT_SERIALIZE_FILE = "../data/serialize.dat";

    /** Javelinのシステムログのログレベルのデフォルト */
    private static final String DEFAULT_SYSTEM_LOG_LEVEL = "INFO";

    /** Javelinのイベントレベルのデフォルト */
    private static final String DEFAULT_EVENT_LEVEL = "WARN";

    /** アプリケーション実行時の例外時にアラーム通知するデフォルト値 */
    private static final boolean DEFAULT_ALARM_EXCEPTION = true;

    /** HTTPステータスエラーのアラーム通知するデフォルト値 */
    private static final boolean DEFAULT_HTTP_STATUS_ERROR = true;

    /** １クラス辺り保持するInvocation（メソッド呼び出し）最大数のデフォルト値 */
    private static final int DEFAULT_REC_INVOCATION_MAX = 1024;

    /** アラーム送信間隔の最小値のデフォルト値。*/
    public static final long DEFAULT_ALARM_MINIMUM_INTERVAL = 60000;

    /** Turn Around Timeを計測するかどうかのデフォルト値 */
    private static final boolean DEFAULT_TAT_ENABLED = true;

    /** Turn Around Timeの保持期間のデフォルト値。*/
    public static final long DEFAULT_TAT_KEEP_TIME = 15000;

    /** Turn Around Timeの値が0の場合に、0の出力を継続する時間のデフォルト値。 */
    public static final long DEFAULT_TAT_ZERO_KEEP_TIME = 10000;

    /** jvnログファイルを出力するかどうかのデフォルト値。 */
    private static final boolean DEFAULT_LOG_JVN_FILE = true;

    /** 複数スレッドアクセスを監視するかどうかのデフォルト値 */
    private static final boolean DEFAULT_CONCURRENT_ENABLED = true;

    /** タイムアウト値の設定が行われているかどうかのデフォルト値 */
    private static final boolean DEF_TIMEOUT_MONITOR = true;

    /** MBeanサーバのホスト名のデフォルト値 */
    public static final String DEF_JMX_HOST = "localhost";

    /** MBeanサーバのポート番号のデフォルト値 */
    public static final int DEF_JMX_PORT = 0;

    /** MBeanサーバの認証ユーザ名のデフォルト値 */
    public static final String DEF_JMX_USER_NAME = "";

    /** MBeanサーバの認証パスワードのデフォルト値 */
    public static final String DEF_JMX_PASSWORD = "";

    /** ログ出力対象パターンのプロパティ名 */
    public static final String INCLUDE = JAVELIN_PREFIX + "include";

    /** ログ出力除外パターンのプロパティ名 */
    public static final String EXCLUDE = JAVELIN_PREFIX + "exclude";

    /** JVNファイルダウンロード時の最大バイト数のプロパティ名 */
    public static final String JVN_DOWNLOAD_MAX = JAVELIN_PREFIX + "log.download.max";

    /** 警告を発生させるCPU時間の閾値名 */
    public static final String ALARM_CPUTHRESHOLD = JAVELIN_PREFIX + "alarmCpuThreshold";

    /** ライセンスファイルパスのプロパティ名 */
    public static final String LICENSEPATH = JAVELIN_PREFIX + "license.path";

    /** クラス名を簡略化するプロパティ名 */
    public static final String CLASSNAME_SIMPLIFY = JAVELIN_PREFIX + "className.simplify";

    /** Collectionのメモリリーク検出を行うかどうか */
    public static final String COLLECTION_MONITOR = JAVELIN_PREFIX + "leak.collection.monitor";

    /** コレクションの数を監視する際の閾値 */
    public static final String COLLECTION_SIZE_THRESHOLD = JAVELIN_PREFIX
            + "leak.collectionSizeThreshold";

    /** コレクションの数を監視する際の出力チェックの間隔 */
    public static final String COLLECTION_INTERVAL = JAVELIN_PREFIX + "leak.interval";

    /** コレクションの数を監視する際に保持するスタックトレースの数。 */
    public static final String COLLECTION_TRACE_MAX = JAVELIN_PREFIX + "leak.traceMax";

    /** スタックトレースの表示に使う深さ。 */
    public static final String TRACE_DEPTH = JAVELIN_PREFIX + "traceDepth";

    /** コレクションの数を監視する際に保持するスタックトレースの深さ。 */
    public static final String COLLECTION_LEAKDETECT_DEPTH = JAVELIN_PREFIX
            + "leak.detect.traceDepth";

    /** クラスヒストグラムを取得する際に、GCを行うかどうか。 */
    public static final String CLASS_HISTO_GC = JAVELIN_PREFIX + "leak.class.histo.gc";

    /** クラスヒストグラムの上位何件を取得するか */
    public static final String CLASS_HISTO_MAX = JAVELIN_PREFIX + "leak.class.histo.max";

    /** クラスヒストグラム取得間隔(ミリ秒) */
    public static final String CLASS_HISTO_INTERVAL = JAVELIN_PREFIX + "leak.class.histo.interval";

    /** 線形検索を行うかどうか */
    public static final String LINEARSEARCH_ENABLED_KEY = JAVELIN_PREFIX + "linearsearch.monitor";

    /** 線形検索検出を行うリストサイズの閾値 */
    public static final String LINEARSEARCH_SIZE = JAVELIN_PREFIX + "linearsearch.size";

    /** 線形検索対象となる、リストに対する線形アクセス回数の割合の閾値 */
    public static final String LINEARSEARCH_RATIO = JAVELIN_PREFIX + "linearsearch.ratio";

    /** クラスヒストグラムを取得するかどうか　 */
    public static final String CLASS_HISTO = JAVELIN_PREFIX + "leak.class.histo";

    /** クラス変換後、detachするかどうか(非公開パラメータ)　 */
    public static final String DETACH = JAVELIN_PREFIX + "detach";

    /** スレッド監視を行うかどうか　 */
    public static final String THREAD_MONITOR = JAVELIN_PREFIX + "thread.monitor";

    /** スレッド監視を行う周期(ミリ秒)　 */
    public static final String THREAD_MONITOR_INTERVAL = JAVELIN_PREFIX + "thread.monitor.interval";

    /** スレッド監視の際に出力するスタックトレースの深さ　 */
    public static final String THREAD_MONITOR_DEPTH = JAVELIN_PREFIX + "thread.monitor.depth";

    /** フルスレッドダンプを出力するかどうか　 */
    public static final String THREAD_DUMP_MONITOR = JAVELIN_PREFIX + "thread.dump.monitor";

    /** スレッドダンプ取得の間隔 */
    public static final String THREAD_DUMP_INTERVAL = JAVELIN_PREFIX + "thread.dump.interval";

    /** フルスレッドダンプ出力に利用するスレッド数の閾値　 */
    public static final String THREAD_DUMP_THREAD = JAVELIN_PREFIX + "thread.dump.threadnum";

    /** フルスレッドダンプ出力に利用するCPU使用率の閾値　 */
    public static final String THREAD_DUMP_CPU = JAVELIN_PREFIX + "thread.dump.cpu";

    /** フルGCを検出するかどうか */
    public static final String FULLGC_MONITOR = JAVELIN_PREFIX + "fullgc.monitor";

    /** フルGCを行うGarbageCollector名のリスト */
    public static final String FULLGC_LIST = JAVELIN_PREFIX + "fullgc.list";

    /** フルGC時間の閾値 */
    public static final String FULLGC_THREASHOLD = JAVELIN_PREFIX + "fullgc.threshold";

    /** ネットワーク入力量を取得するかどうかのフラグのプロパティ名　 */
    public static final String NET_INPUT_MONITOR = JAVELIN_PREFIX + "net.input" + ".monitor";

    /** ネットワーク出力量を取得するかどうかのフラグのプロパティ名　 */
    public static final String NET_OUTPUT_MONITOR = JAVELIN_PREFIX + "net.output" + ".monitor";

    /** ファイル入力量を取得するかどうかのフラグのプロパティ名　 */
    public static final String FILE_INPUT_MONITOR = JAVELIN_PREFIX + "file.input" + ".monitor";

    /** ファイル出力量を取得するかどうかのフラグのプロパティ名　 */
    public static final String FILE_OUTPUT_MONITOR = JAVELIN_PREFIX + "file.output" + ".monitor";

    /** ファイナライズ待ちオブジェクト数を取得するかどうかのフラグのプロパティ名　 */
    public static final String FINALIZATION_COUNT_MONITOR = JAVELIN_PREFIX
            + "finalizationCount.monitor";

    /** メソッド呼び出し間隔超過を監視するかどうかを決定するプロパティ名 */
    public static final String INTERVAL_ERROR_MONITOR = JAVELIN_PREFIX + "interval.monitor";

    /** 継承を調べる深さの最大値のプロパティ名　 */
    public static final String INHERITANCE_DEPTH = JAVELIN_PREFIX + "inheritance" + ".depth";

    /** ブロック回数が多すぎるかどうかの閾値 */
    public static final String THREAD_BLOCK_THRESHOLD = JAVELIN_PREFIX + "thread.block"
            + ".threshold";

    /** ブロック継続イベントを出力する際のブロック継続時間の閾値 */
    public static final String THREAD_BLOCKTIME_THRESHOLD = JAVELIN_PREFIX + "thread.blocktime"
            + ".threshold";

    /** ブロック回数が閾値を超えた際に取得するスレッド情報の数。 */
    public static final String THREAD_BLOCK_THREADINFO_NUM = JAVELIN_PREFIX + "thread.block"
            + ".threadinfo.num";

    /** Javaレベルデッドロックの監視を行うか */
    public static final String THREAD_DEADLOCK_MONITOR = JAVELIN_PREFIX + "thread.deadlock.monitor";

    /** メソッドに対する呼び出し間隔の閾値定義。 */
    public static final String INTERVAL_THRESHOLD = JAVELIN_PREFIX + "interval" + ".threshold";

    /** メソッドに対する、引数の値ごとの呼び出し間隔の閾値定義。 */
    public static final String INTERVAL_PER_ARGS_THRESHOLD = JAVELIN_PREFIX + "interval.perargs"
            + ".threshold";

    /** HttpSessionのインスタンス数を取得するかどうかのフラグのプロパティ名　 */
    public static final String HTTP_SESSION_COUNT_MONITOR = JAVELIN_PREFIX + "httpSessionCount"
            + ".monitor";

    /** HttpSessionへの登録オブジェクト総サイズを取得するかどうかのフラグのプロパティ名　 */
    public static final String HTTP_SESSION_SIZE_MONITOR = JAVELIN_PREFIX + "httpSessionSize"
            + ".monitor";

    /** 引数情報にHTTPセッション情報を出力するかどうかを決定するプロパティ */
    public static final String LOG_HTTP_SESSION_KEY = JAVELIN_PREFIX + "log.http.session";

    /** HTTPセッションの詳細情報を出力するかどうかを決定するプロパティ */
    public static final String HTTP_SESSION_DETAIL_KEY = JAVELIN_PREFIX + "log.http.session.detail";

    /** HTTPセッションの詳細情報の深さを表すプロパティ */
    public static final String HTTP_SESSION_DETAIL_DEPTH_KEY = JAVELIN_PREFIX
            + "log.http.session.detail.depth";

    /** 計測対象から自動除外する呼び出し回数の閾値 */
    private static final String AUTO_EXCLUDE_THRESHOLD_COUNT = JAVELIN_PREFIX
            + "autoExcludeThreshold.count";

    /** 計測対象から自動除外する実行時間の閾値 */
    private static final String AUTO_EXCLUDE_THRESHOLD_TIME = JAVELIN_PREFIX
            + "autoExcludeThreshold.time";

    /** メモリリーク検出時に、リークしたコレクションのサイズを出力するかどうか */
    public static final String LEAK_COLLECTIONSIZE_OUT = JAVELIN_PREFIX + "leak.collectionSizeOut";

    /** Log4Jのログ出力の際、スタックトレースを出力するレベルの閾値 */
    public static final String LOG4J_PRINTSTACK_LEVEL = JAVELIN_PREFIX + "log4j.printstack.level";

    /** EJBのセッションBeanの呼び出し／応答までの時間の監視を行うかどうか */
    public static final String EJB_SESSION_MONITOR = JAVELIN_PREFIX + "ejb.session.monitor";

    /** CPU時間にかける数。デフォルトでは1 */
    private static final String CPU_TIME_UNIT_KEY = JAVELIN_PREFIX + "cpu.unit";

    /** ログ出力をする対象を記述したフィルタファイル名のデフォルト値 */
    public static final String DEF_INCLUDE = "include.conf";

    /** ログ出力から除外する対象を記述したフィルタファイル名のデフォルト値 */
    public static final String DEF_EXCLUDE = "exclude.conf";

    /** JVNファイルダウンロード時の最大バイト数のプロパティ名 */
    public static final int DEF_JVN_DOWNLOAD_MAX = 1024 * 1024;

    /** 警告を発生させるＣＰＵ時間のデフォルト値 */
    public static final long DEF_ALARM_CPUTHRESHOLD = 1000;

    /** クラス変換後、detachするかどうかのデフォルト値(非公開パラメータ)　 */
    public static final boolean DEF_DETACH = true;

    /** ライセンスファイルパスのデフォルト値 */
    public static final String DEF_LICENSEPATH = "../license/ENdoSnipeLicense.dat";

    /** クラス名簡略化を表すフラグのデフォルト値 */
    public static final boolean DEF_CLASSNAME_SIMPLIFY = false;

    /** Collectionのメモリリーク検出を行うかどうかのデフォルト値 */
    public static final boolean DEF_COLLECTION_MONITOR = true;

    /** メモリリークとして検出するCollection、Mapのサイズの閾値 */
    public static final int DEF_COLLECTION_SIZE = 2000;

    /** コレクションの数を監視する際の出力チェックの間隔 */
    public static final int DEF_COLLECTION_INTERVAL = 11;

    /** コレクションの数を監視する際に保持するスタックトレースの数。 */
    public static final int DEF_COLLECTION_TRACE_MAX = 20;

    /** スタックトレースの表示に使う深さ。 */
    public static final int DEF_COLLECTION_TRACE_DEPTH = 15;

    /** コレクションの数を監視する際に保持するスタックトレースの深さ。 */
    public static final int DEF_COLLECTION_LEAKDETECT_DEPTH = 5;

    /** クラスヒストグラムを取得する際に、GCを行うかどうかのデフォルト値。 */
    public static final boolean DEF_CLASS_HISTO_GC = false;

    /** クラスヒストグラムの上位何件を取得するかのデフォルト値 */
    public static final int DEF_CLASS_HISTO_MAX = 15;

    /** クラスヒストグラム取得間隔(ミリ秒)のデフォルト値 */
    public static final int DEF_CLASS_HISTO_INTERVAL = 60000;

    /** クラスヒストグラムを取得するかどうかのデフォルト値 */
    public static final boolean DEF_CLASS_HISTO = true;

    /** 線形検索検出を監視するかどうかのデフォルト値 */
    private static final boolean DEF_LINEARSEARCH_ENABLED = true;

    /** 線形検索を行うリストサイズのデフォルト値 */
    public static final int DEF_LINEARSEARCH_SIZE = 100;

    /** 線形検索対象となる、リストに対する線形アクセス回数の割合のデフォルト値 */
    public static final double DEF_LINEARSEARCH_RATIO = 5;

    /** スレッド監視を行うかどうかのデフォルト値　 */
    public static final boolean DEF_THREAD_MONITOR = true;

    /** スレッド監視を行う周期(ミリ秒)のデフォルト値　 */
    public static final long DEF_THREAD_MON_INTERVAL = 1000;

    /** スレッド監視の際に出力するスタックトレースの深さのデフォルト値　 */
    public static final int DEF_THREAD_MON_DEPTH = 10;

    /** フルスレッドダンプを出力するかどうかのデフォルト値 */
    public static final boolean DEF_THREAD_DUMP_MONITOR = false;

    /** フルスレッドダンプ出力間隔のデフォルト値 */
    public static final int DEF_THREAD_DUMP_INTERVAL = 10000;

    /** フルスレッドダンプを出力するスレッド数の閾値のデフォルト値 */
    public static final int DEF_THREAD_DUMP_THREAD = 100;

    /** フルスレッドダンプを出力するCPU使用率の閾値のデフォルト値 */
    public static final int DEF_THREAD_DUMP_CPU = 50;

    /** フルGCを出力するかどうかのデフォルト値 */
    public static final boolean DEF_FULLGC_MONITOR = true;

    /** フルGCを行うGarbageCollector名のリストのデフォルト値 */
    public static final String DEF_FULLGC_LIST =
            "MarkSweepCompact,Garbage collection optimized for throughput Old Collector";

    /** フルGC検出を行うGC時間の閾値のデフォルト値 */
    public static final int DEF_FULLGC_THRESHOLD = 5000;

    /** Javaレベルデッドロックの監視を行うかのデフォルト値 */
    public static final boolean DEF_THREAD_DEADLOCK_MONITOR = false;

    /** ネットワーク入力量を取得するかどうかのフラグのデフォルト値 */
    public static final boolean DEF_NET_INPUT_MONITOR = false;

    /** ネットワーク出力量を取得するかどうかのフラグのデフォルト値 */
    public static final boolean DEF_NET_OUTPUT_MONITOR = false;

    /** ファイル入力量を取得するかどうかのフラグのデフォルト値 */
    public static final boolean DEF_FILE_INPUT_MONITOR = false;

    /** ファイル出力量を取得するかどうかのフラグのデフォルト値 */
    public static final boolean DEF_FILE_OUTPUT_MONITOR = false;

    /** ファイナライズ待ちオブジェクト数を取得するかどうかのフラグのデフォルト値 */
    public static final boolean DEF_FINALIZATION_COUNT_MONITOR = true;

    /** メソッド呼び出し間隔超過を監視するかどうかのフラグのデフォルト値 */
    public static final boolean DEF_INTERVAL_ERROR_MONITOR = true;

    /** HttpSessionのインスタンス数を監視するかどうかのフラグのデフォルト値 */
    public static final boolean DEF_HTTP_SESSION_COUNT_MONITOR = true;

    /** HttpSessionへの登録オブジェクト総サイズを監視するかどうかのフラグのデフォルト値 */
    public static final boolean DEF_HTTP_SESSION_SIZE_MONITOR = true;

    /** 継承を調べる深さの最大値のデフォルト値　 */
    public static final int DEF_INHERITANCE_DEPTH = 3;

    /** ブロック回数が多すぎるかどうかの閾値のデフォルト値。 */
    public static final long DEF_THREAD_BLOCK_THRESHOLD = 10;

    /** ブロック回数が閾値を超えた際に取得するスレッド情報の数のデフォルト値。 */
    public static final int DEF_THREAD_BLOCK_THREADINFO_NUM = 10;

    /** ブロック継続イベントを出力する際のブロック継続時間の閾値のデフォルト値 */
    public static final long DEF_THREAD_BLOCKTIME_THRESHOLD = 2000;

    /** メソッドの呼び出し間隔に対するデフォルト値(対象なし)。 */
    public static final String DEF_INTERVAL_THRESHOLD = "";

    /** メソッドの、引数の値ごとの呼び出し間隔に対するデフォルト値(対象なし)。 */
    public static final String DEF_INTERVAL_PER_ARGS_THRESHOLD = "";

    /** 計測対象に含めるか判定するための回数の閾値 */
    public static final int DEF_AUTO_EXCLUDE_THRESHOLD_COUNT = 10;

    /** 計測対象に含めるか判定するための時間の閾値(単位:ミリ秒) */
    public static final int DEF_AUTO_EXCLUDE_THRESHOLD_TIME = 100;

    /** メモリリーク検出時に、リークしたコレクションのサイズを出力するかのフラグのデフォルト値 */
    public static final boolean DEF_LEAK_COLLECTIONSIZE_OUT = false;

    /** BottleNeckEyeとの通信に使用するポートを再取得する間隔のデフォルト値 */
    public static final int DEF_JAVELIN_BIND_INTERVAL = 5000;

    /** Log4Jのログ出力の際、スタックトレースを出力するレベルの閾値のデフォルト値 */
    public static final String DEF_LOG4J_PRINTSTACK_LEVEL = "ERROR";

    /** EJBのセッションBeanの呼び出し／応答までの時間の監視を行うかどうかのデフォルト値 */
    public static final boolean DEF_EJB_SESSION_MONITOR = false;

    /** システムのリソースデータを取得するかどうかのデフォルト値 */
    public static final boolean DEF_COLLECT_SYSTEM_RESOURCES = true;

    /** InvocationFullEventを送信するかどうかのデフォルト値 */
    public static final boolean DEF_SEND_INVOCATION_FULL_EVENT = true;

    /** JMXのリソースデータを取得するかどうかのデフォルト値 */
    public static final boolean DEF_COLLECT_JMX_RESOURCES = true;

    /** Javelinの設定値を保存するオブジェクト */
    private static final JavelinConfigUtil CONFIGUTIL;

    /** CPU時間にかける数。デフォルトでは1 */
    private static final int DEF_CPU_TIME_UNIT = 1;

    /** コールツリーが溢れた場合に全てを記録するかどうか */
    private static final String CALL_TREE_ALL_KEY = JAVELIN_PREFIX + "call.tree.all";

    /** コールツリーが溢れた場合に全てを記録するかどうか */
    private static final boolean DEF_CALL_TREE_ALL = false;

    /** コールツリーを記録するかどうか */
    public static final String CALL_TREE_ENABLE_KEY = JAVELIN_PREFIX + "call.tree.enable";

    /** コールツリーを記録するかどうか */
    private static final boolean DEF_CALL_TREE_ENABLE = true;

    /** ストールメソッドを監視するかどうかのデフォルト値 */
    public static final boolean DEF_METHOD_STALL_MONITOR = false;

    /** ストールメソッドを監視する周期のデフォルト値 */
    public static final int DEF_METHOD_STALL_INTERVAL = 10000;

    /** ストールメソッドと判断する閾値のデフォルト値 */
    public static final int DEF_METHOD_STALL_THRESHOLD = 60000;

    /** ストールメソッド検出時に出力するスタックトレースの深さのデフォルト値 */
    public static final int DEF_METHOD_STALL_TRACE_DEPTH = 30;

    /** ストールメソッド検出時に出力するスタックトレースの深さのデフォルト値 */
    public static final String DEF_CONNNECTION_MODE = "server";

    /** 項目名に付与する接頭辞の文字列。 */
    private static final String DEF_ITEMNAME_PREFIX = "";

    /** 項目名に接頭辞を付与しないパターン。 */
    private static final String DEF_ITEMNAME_NOPREFIX_LIST = "/common/";

    /** HadoopAgentから取得する。 */
    private static final boolean DEF_COLLECT_HADOOP_AGENT_RESOURCES = false;

    /** HBaseAgentから取得する。 */
    private static final boolean DEF_COLLECT_HBASE_AGENT_RESOURCES = false;

    /** 複数ポート接続時にフォルダにつける名前 */
    private static String logFolderName__;

    /** 複数ポート接続時にJavelinログファイル保存先を初期化しているかどうか */
    private static boolean isJvnDirInit__ = false;

    /** 複数ポート接続時にシステムログファイル保存先を初期化しているかどうか */
    private static boolean isSysLogDirInit__ = false;

    static
    {
        CONFIGUTIL = JavelinConfigUtil.getInstance();
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        try
        {
            logFolderName__ = bean.getName().replaceAll("[^0-9a-zA-Z]", "_");
        }
        catch (Exception ex)
        {
            Random random = new Random();
            final int RANDOMNUM = 10000;
            logFolderName__ = String.valueOf(random.nextInt(RANDOMNUM));
        }
    }

    /**
     * {@link JavelinConfig} を構築します。<br />
     */
    public JavelinConfigBase()
    {
        // Do Nothing.
    }

    /**
     * 指定したキーに対するBoolean値の更新を確実に反映させる。
     * 
     * @param key 更新反映対象のキー
     */
    public void updateBooleanValue(final String key)
    {
        CONFIGUTIL.updateBooleanValue(key);
    }

    /**
     * 指定したキーに対するInteger値の更新を確実に反映させる。
     * 
     * @param key 更新反映対象のキー
     */
    public void updateIntValue(final String key)
    {
        CONFIGUTIL.updateIntValue(key);
    }

    /**
     * 指定したキーに対するLong値の更新を確実に反映させる。
     * 
     * @param key 更新反映対象のキー
     */
    public void updateLongValue(final String key)
    {
        CONFIGUTIL.updateLongValue(key);
    }

    /**
     * ConcurrentAccessMonitor や CollectionMonitor 動作中にクラスロードが起ったときに、
     * バイトコード変換を行うかどうかを返します。<br />
     *
     * @return 変換を行わない場合は <code>true</code> 、変換を行う場合は <code>false</code>
     */
    public boolean isSkipClassOnProcessing()
    {
        return CONFIGUTIL.getBoolean(SKIPCLASS_ONPROCESSING_KEY, DEFAULT_SKIPCLASS_ONPROCESSING);
    }

    /**
     * ConcurrentAccessMonitor や CollectionMonitor 動作中にクラスロードが起ったときに、
     * バイトコード変換を行うかどうかを設定します。<br />
     *
     * @param skipClass 変換を行わない場合は <code>true</code> 、変換を行う場合は <code>false</code>
     */
    public void setSkipClassOnProcessing(final boolean skipClass)
    {
        CONFIGUTIL.setBoolean(SKIPCLASS_ONPROCESSING_KEY, skipClass);
    }

    /**
     * 呼び出し情報を記録する際の閾値を返す。
     *
     * @return 閾値（ミリ秒）
     */
    public long getAlarmThreshold()
    {
        return CONFIGUTIL.getLong(ALARMTHRESHOLD_KEY, DEFAULT_ALARMTHRESHOLD);
    }

    /**
     * 呼び出し情報を記録する際の閾値が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetAlarmThreshold()
    {
        return isKeyExist(ALARMTHRESHOLD_KEY);
    }

    /**
     * 呼び出し情報を記録する際の閾値を返す。
     *
     * @param alarmThreshold 閾値（ミリ秒）
     */
    public void setAlarmThreshold(final long alarmThreshold)
    {
        CONFIGUTIL.setLong(ALARMTHRESHOLD_KEY, alarmThreshold);
    }

    /**
     * JMX通信による情報公開を行うかどうかの設定を返す。
     *
     * @return JMX通信による情報公開を行うならtrue
     */
    public boolean isRecordJMX()
    {
        return CONFIGUTIL.getBoolean(RECORD_JMX_KEY, DEFAULT_RECORD_JMX);
    }

    /**
     * JMX通信による情報公開を行うかどうかを設定する。
     *
     * @param isRecordJMX JMX通信による情報公開を行うならtrue
     */
    public void setRecordJMX(final boolean isRecordJMX)
    {
        CONFIGUTIL.setBoolean(RECORD_JMX_KEY, isRecordJMX);
    }

    /**
     * 呼び出し情報を記録する最大件数を返す。
     *
     * @return 件数
     */
    public int getIntervalMax()
    {
        return CONFIGUTIL.getInteger(INTERVALMAX_KEY, DEFAULT_INTERVALMAX);
    }

    /**
     * 呼び出し情報を記録する最大件数が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetIntervalMax()
    {
        return isKeyExist(INTERVALMAX_KEY);
    }

    /**
     * 呼び出し情報を記録する最大件数をセットする。
     *
     * @param intervalMax 件数
     */
    public void setIntervalMax(final int intervalMax)
    {
        CONFIGUTIL.setInteger(INTERVALMAX_KEY, intervalMax);
    }

    /**
     * Javelinログファイルの出力先を取得する。
     *
     * @return 出力先パス
     */
    public String getJavelinFileDir()
    {
        String relativePath = CONFIGUTIL.getString(JAVELINFILEDIR_KEY, DEFAULT_JAVELINFILEDIR);

        // 複数ポート接続設定を行う場合、Javelinログファイルの保存場所を再設定する。
        if (isJvnDirInit__ == false)
        {
            if (isAcceptPortIsRange())
            {
                relativePath = relativePath + File.separator + logFolderName__;
                setJavelinFileDir(relativePath);
            }
            isJvnDirInit__ = true;
        }
        return CONFIGUTIL.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * Javelinログファイルの出力先が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetJavelinFileDir()
    {
        return isKeyExist(JAVELINFILEDIR_KEY);
    }

    /**
     * Javelinログファイルの出力先をセットする。
     *
     * @param javelinFileDir 出力先パス
     */
    public void setJavelinFileDir(final String javelinFileDir)
    {
        CONFIGUTIL.setString(JAVELINFILEDIR_KEY, javelinFileDir);
    }

    /**
     * 例外の発生履歴を記録する最大件数を返す。
     *
     * @return 件数
     */
    public int getThrowableMax()
    {
        return CONFIGUTIL.getInteger(THROWABLEMAX_KEY, DEFAULT_THROWABLEMAX);
    }

    /**
     * Javelinの情報取得対象から除外する、最大のBytecode長を返す。
     *
     * @return Javelinの情報取得対象から除外する、最大のBytecode長。
     */
    public int getBytecodeLengthMax()
    {
        return CONFIGUTIL.getInteger(BYTECODE_EXCLUDE_LENGTH_KEY, DEFAULT_BYTECODE_LENGTH_MIN_KEY);
    }

    /**
     * Javelinの情報取得対象から除外する、最大の制御命令数を返す。
     *
     * @return Javelinの情報取得対象から除外する、最大の制御命令数。
     */
    public int getBytecodeControlCountMax()
    {
        return CONFIGUTIL.getInteger(BYTECODE_EXCLUDE_CONTROLCOUNT_MAX_KEY,
                                     DEFAULT_BYTECODE_EXCLUDE_CONTROLCOUNT_MAX_KEY);
    }

    /**
     * Bytecodeの内容を元に計測対象から除外する際のポリシーを取得する。 
     * 
     * @return Bytecodeの内容を元に計測対象から除外する際のポリシー。
     */
    public int getByteCodeExcludePolicy()
    {
        return CONFIGUTIL.getInteger(BYTECODE_EXCLUDE_POLICY_KEY,
                                     DEFAULT_BYTECODE_EXCLUDE_POLICY_KEY);
    }

    /**
     * 呼び出し情報を記録する際の閾値が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetThrowableMax()
    {
        return isKeyExist(THROWABLEMAX_KEY);
    }

    /**
     * 例外の発生履歴を記録する最大件数をセットする。
     *
     * @param throwableMax 件数
     */
    public void setThrowableMax(final int throwableMax)
    {
        CONFIGUTIL.setInteger(THROWABLEMAX_KEY, throwableMax);
    }

    /**
     * 呼び出し先につける名称を返す。
     *
     * @return 名称
     */
    public String getEndCalleeName()
    {
        return CONFIGUTIL.getString(ENDCALLEENAME_KEY, DEFAULT_ENDCALLEENAME);
    }

    /**
     * 呼び出し先につける名称が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetEndCalleeName()
    {
        return isKeyExist(ENDCALLEENAME_KEY);
    }

    /**
     * 呼び出し先につける名称をセットする。
     *
     * @param endCalleeName 名称
     */
    public void setEndCalleeName(final String endCalleeName)
    {
        CONFIGUTIL.setString(ENDCALLEENAME_KEY, endCalleeName);
    }

    /**
     * 呼び出し元につける名称を返す。
     *
     * @return 名称
     */
    public String getRootCallerName()
    {
        return CONFIGUTIL.getString(ROOTCALLERNAME_KEY, DEFAULT_ROOTCALLERNAME);
    }

    /**
     * 呼び出し元につける名称が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetRootCallerName()
    {
        return isKeyExist(ROOTCALLERNAME_KEY);
    }

    /**
     * 呼び出し元につける名称をセットする。
     *
     * @param rootCallerName 名称
     */
    public void setRootCallerName(final String rootCallerName)
    {
        CONFIGUTIL.setString(ROOTCALLERNAME_KEY, rootCallerName);
    }

    /**
     * スタックトレースを出力するかどうかの設定を返す。
     *
     * @return スタックトレースを出力するならtrue
     */
    public boolean isLogStacktrace()
    {
        return CONFIGUTIL.getBoolean(LOG_STACKTRACE_KEY, DEFAULT_LOG_STACKTRACE);
    }

    /**
     * スタックトレースを出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetLogStacktrace()
    {
        return isKeyExist(LOG_STACKTRACE_KEY);
    }

    /**
     * スタックトレースを出力するかどうかを設定する。
     *
     * @param isLogStacktrace スタックトレースを出力するならtrue
     */
    public void setLogStacktrace(final boolean isLogStacktrace)
    {
        CONFIGUTIL.setBoolean(LOG_STACKTRACE_KEY, isLogStacktrace);
    }

    /**
     * 引数を出力するかどうかの設定を返す。
     *
     * @return 引数を出力するならtrue
     */
    public boolean isLogArgs()
    {
        return CONFIGUTIL.getBoolean(LOG_ARGS_KEY, DEFAULT_LOG_ARGS);
    }

    /**
     * HTTPセッションを出力するかどうかの設定を返す。
     *
     * @return HTTPセッションを出力するならtrue
     */
    public boolean isLogHttpSession()
    {
        return CONFIGUTIL.getBoolean(LOG_HTTP_SESSION_KEY, DEFAULT_LOG_HTTP_SESSION);
    }

    /**
     * スレッドコンテンション監視を行うかどうかの設定を返す。
     * 
     * @return スレッドコンテンション監視を行うかどうか。
     */
    public boolean isThreadContentionMonitor()
    {

        return CONFIGUTIL.getBoolean(THREAD_CONTENTION_KEY, DEFAULT_THREAD_CONTENTION);
    }

    /**
     * MBeanによって取得した情報を出力するかどうかの設定を返す。
     *
     * @return MBeanによって取得した情報を出力するならtrue
     */
    public boolean isLogMBeanInfo()
    {
        return CONFIGUTIL.getBoolean(LOG_MBEANINFO_KEY, DEFAULT_LOG_MBEANINFO);
    }

    /**
     * 端点で、MBeanによって取得した情報を出力するかどうかの設定を返す。
     *
     * @return 端点で、MBeanによって取得した情報を出力するならtrue
     */
    public boolean isLogMBeanInfoRoot()
    {
        return CONFIGUTIL.getBoolean(LOG_MBEANINFO_ROOT_KEY, DEFAULT_LOG_MBEANINFO_ROOT);
    }

    /**
     * 引数を出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetLogArgs()
    {
        return isKeyExist(LOG_ARGS_KEY);
    }

    /**
     * 引数を出力するかどうかを設定する。
     *
     * @param isLogArgs 引数を出力するならtrue
     */
    public void setLogArgs(final boolean isLogArgs)
    {
        CONFIGUTIL.setBoolean(LOG_ARGS_KEY, isLogArgs);
    }

    /**
     * セッションを引数として出力するかどうかを設定する。
     *
     * @param isLogArgs 引数を出力するならtrue
     */
    public void setLogHttpSession(final boolean isLogArgs)
    {
        CONFIGUTIL.setBoolean(LOG_HTTP_SESSION_KEY, isLogArgs);
    }

    /**
     * MBeanによって取得した情報を出力するかどうかを設定する。
     *
     * @param isLogMBeanInfo MBeanによって取得した情報を出力するならtrue
     */
    public void setLogMBeanInfo(final boolean isLogMBeanInfo)
    {
        CONFIGUTIL.setBoolean(LOG_MBEANINFO_KEY, isLogMBeanInfo);
    }

    /**
     * MBeanによって取得した情報（ルートノード）を出力するかどうかを設定する。
     *
     * @param isLogMBeanInfo MBeanによって取得した情報を出力するならtrue
     */
    public void setLogMBeanInfoRoot(final boolean isLogMBeanInfo)
    {
        CONFIGUTIL.setBoolean(LOG_MBEANINFO_ROOT_KEY, isLogMBeanInfo);
    }

    /**
     * 戻り値を出力するかどうかの設定を返す。
     *
     * @return 戻り値を出力するならtrue
     */
    public boolean isLogReturn()
    {
        return CONFIGUTIL.getBoolean(LOG_RETURN_KEY, DEFAULT_LOG_RETURN);
    }

    /**
     * 戻り値を出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetLogReturn()
    {
        return isKeyExist(LOG_RETURN_KEY);
    }

    /**
     * 戻り値を出力するかどうかを設定する。
     *
     * @param isLogReturn 戻り値を出力するならtrue
     */
    public void setLogReturn(final boolean isLogReturn)
    {
        CONFIGUTIL.setBoolean(LOG_RETURN_KEY, isLogReturn);
    }

    /**
     * 引数の詳細を出力するかどうかの設定を返す。
     *
     * @return 引数の詳細を出力するならtrue
     */
    public boolean isArgsDetail()
    {
        return CONFIGUTIL.getBoolean(ARGS_DETAIL_KEY, DEFAULT_ARGS_DETAIL);
    }

    /**
     * 引数の詳細を出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetArgsDetail()
    {
        return isKeyExist(ARGS_DETAIL_KEY);
    }

    /**
     * 引数の詳細を出力するかどうかを設定する。
     *
     * @param isArgsDetail 引数の詳細を出力するならtrue
     */
    public void setArgsDetail(final boolean isArgsDetail)
    {
        CONFIGUTIL.setBoolean(ARGS_DETAIL_KEY, isArgsDetail);
    }

    /**
     * 戻り値の詳細を出力するかどうかの設定を返す。
     *
     * @return 戻り値の詳細を出力するならtrue
     */
    public boolean isReturnDetail()
    {
        return CONFIGUTIL.getBoolean(RETURN_DETAIL_KEY, DEFAULT_RETURN_DETAIL);
    }

    /**
     * 戻り値の詳細を出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetReturnDetail()
    {
        return isKeyExist(RETURN_DETAIL_KEY);
    }

    /**
     * 戻り値の詳細を出力するかどうかを設定する。
     *
     * @param isReturnDetail 戻り値の詳細を出力するならtrue
     */
    public void setReturnDetail(final boolean isReturnDetail)
    {
        CONFIGUTIL.setBoolean(RETURN_DETAIL_KEY, isReturnDetail);
    }

    /**
     * 引数の詳細を出力する階層数の設定を返す。
     *
     * @return 引数の詳細を出力する階層数
     */
    public int getArgsDetailDepth()
    {
        return CONFIGUTIL.getInteger(ARGS_DETAIL_DEPTH_KEY, DEFAULT_ARGS_DETAIL_DEPTH);
    }

    /**
     * 引数の詳細を出力する階層数が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetArgsDetailDepth()
    {
        return isKeyExist(ARGS_DETAIL_DEPTH_KEY);
    }

    /**
     * 引数の詳細を出力する階層数を設定する。
     *
     * @param detailDepth 引数の詳細を出力する階層数
     */
    public void setArgsDetailDepth(final int detailDepth)
    {
        CONFIGUTIL.setInteger(ARGS_DETAIL_DEPTH_KEY, detailDepth);
    }

    /**
     * 戻り値の詳細を出力する階層数の設定を返す。
     *
     * @return 詳細を出力する階層数
     */
    public int getReturnDetailDepth()
    {
        return CONFIGUTIL.getInteger(RETURN_DETAIL_DEPTH_KEY, DEFAULT_RETURN_DETAIL_DEPTH);
    }

    /**
     * 戻り値の詳細を出力する階層数が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetReturnDetailDepth()
    {
        return isKeyExist(RETURN_DETAIL_DEPTH_KEY);
    }

    /**
     * 戻り値の詳細を出力する階層数を設定する。
     *
     * @param returnDetailDepth 戻り値の詳細を出力する階層数
     */
    public void setReturnDetailDepth(final int returnDetailDepth)
    {
        CONFIGUTIL.setInteger(RETURN_DETAIL_DEPTH_KEY, returnDetailDepth);
    }

    /**
     * HTTPセッションの詳細を出力するかどうかの設定を返す。
     *
     * @return HTTPセッションの詳細を出力するならtrue
     */
    public boolean isHttpSessionDetail()
    {
        return CONFIGUTIL.getBoolean(HTTP_SESSION_DETAIL_KEY, DEFAULT_HTTP_SESSION_DETAIL);
    }

    /**
     * HTTPセッションの詳細を出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetHttpSessionDetail()
    {
        return isKeyExist(HTTP_SESSION_DETAIL_KEY);
    }

    /**
     * HTTPセッションの詳細を出力するかどうかを設定する。
     *
     * @param isHttpSessionDetail HTTPセッションの詳細を出力するならtrue
     */
    public void setHttpSessionDetail(final boolean isHttpSessionDetail)
    {
        CONFIGUTIL.setBoolean(HTTP_SESSION_DETAIL_KEY, isHttpSessionDetail);
    }

    /**
     * HTTPセッションの詳細を出力する階層数の設定を返す。
     *
     * @return HTTPセッションの詳細を出力する階層数
     */
    public int getHttpSessionDetailDepth()
    {
        return CONFIGUTIL.getInteger(HTTP_SESSION_DETAIL_DEPTH_KEY,
                                     DEFAULT_HTTP_SESSION_DETAIL_DEPTH);
    }

    /**
     * HTTPセッションの詳細を出力する階層数が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetHttpSessionDetailDepth()
    {
        return isKeyExist(HTTP_SESSION_DETAIL_DEPTH_KEY);
    }

    /**
     * HTTPセッションの詳細を出力する階層数を設定する。
     *
     * @param detailDepth HTTPセッションの詳細を出力する階層数
     */
    public void setHttpSessionDetailDepth(final int detailDepth)
    {
        CONFIGUTIL.setInteger(HTTP_SESSION_DETAIL_DEPTH_KEY, detailDepth);
    }

    /**
     * スレッドモデルを返す。
     *
     * @return スレッドモデル
     */
    public int getThreadModel()
    {
        return CONFIGUTIL.getInteger(THREADMODEL_KEY, DEFAULT_THREADMODEL);
    }

    /**
     * スレッドモデルが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetThreadModel()
    {
        return isKeyExist(THREADMODEL_KEY);
    }

    /**
     * スレッドモデルをセットする。
     *
     * @param threadModel スレッドモデル
     */
    public void setThreadModel(final int threadModel)
    {
        CONFIGUTIL.setInteger(THREADMODEL_KEY, threadModel);
    }

    /**
     * キーに対応する値がセットされているかどうかを調べる。
     *
     * @param key キー
     * @return 値がセットされていればtrue
     */
    private boolean isKeyExist(final String key)
    {
        return CONFIGUTIL.isKeyExist(key);
    }

    /**
     * メモリに保存する閾値を取得する。
     * @return メモリに保存する閾値
     */
    public long getStatisticsThreshold()
    {
        return CONFIGUTIL.getLong(STATISTICSTHRESHOLD_KEY, DEFAULT_STATISTICSTHRESHOLD);
    }

    /**
     * メモリに保存する閾値を設定する。
     * @param statisticsThreshold メモリに保存する閾値
     */
    public void setStatisticsThreshold(final long statisticsThreshold)
    {
        CONFIGUTIL.setLong(STATISTICSTHRESHOLD_KEY, statisticsThreshold);
    }

    /**
     * ログに出力するArgsの長さの閾値を取得する。
     * @return Argsの長さの閾値
     */
    public int getStringLimitLength()
    {
        return CONFIGUTIL.getInteger(STRINGLIMITLENGTH_KEY, DEFAULT_STRINGLIMITLENGTH);
    }

    /**
     * ログに出力するArgsの長さの閾値を設定する。
     * @param stringLimitLength Argsの長さの閾値
     */
    public void setStringLimitLength(final int stringLimitLength)
    {
        CONFIGUTIL.setLong(STRINGLIMITLENGTH_KEY, stringLimitLength);
    }

    /**
     * 待ちうけポート番号を返す。
     *
     * @return ポート番号
     */
    public int getAcceptPort()
    {
        return CONFIGUTIL.getInteger(ACCEPTPORT_KEY, DEFAULT_ACCEPTPORT);
    }

    /**
     * 接続ホスト名を返す。
     *
     * @return ホスト名
     */
    public String getConnectHost()
    {
        return CONFIGUTIL.getString(CONNECTHOST_KEY, DEFAULT_CONNECTHOST);
    }

    /**
     * 接続ポート番号を返す。
     *
     * @return ポート番号
     */
    public int getConnectPort()
    {
        return CONFIGUTIL.getInteger(CONNECTPORT_KEY, DEFAULT_CONNECTPORT);
    }

    /**
     * データベース名を返す。
     *
     * @return データベース名
     */
    public String getDatabaseName()
    {
        return CONFIGUTIL.getString(DATABASENAME_KEY, DEFAULT_DATABASENAME);
    }

    /**
     * Javelinシステムログの出力先ディレクトリを返す。
     *
     * @return Javelinシステムログの出力先ディレクトリ。
     */
    public String getSystemLog()
    {
        String relativePath = CONFIGUTIL.getString(SYSTEMLOG_KEY, DEFAULT_SYSTEMLOG);
        if (isSysLogDirInit__ == false)
        {
            if (isAcceptPortIsRange())
            {
                relativePath = relativePath + File.separator + logFolderName__;
                setSystemLog(relativePath);
            }
            isSysLogDirInit__ = true;
        }
        return CONFIGUTIL.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * Javelinシステムログの出力先ディレクトリを設定します。<br />
     *
     * @param sysLogDir Javelinシステムログの出力先ディレクトリ。
     */
    public void setSystemLog(final String sysLogDir)
    {
        CONFIGUTIL.setString(SYSTEMLOG_KEY, sysLogDir);
    }

    /**
     * HeapDumpファイルの出力先ディレクトリを返す。
     *
     * @return HeapDumpファイルの出力先ディレクトリ。
     */
    public String getHeapDumpDir()
    {
        String relativePath = CONFIGUTIL.getString(HEAPDUMPDIR_KEY, DEFAULT_HEAPDUMP_DIR);
        return CONFIGUTIL.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * 利用するAlarmListener名を返す。
     * ","区切りで複数指定することができる。
     *
     * @return 利用するAlarmListener名
     */
    public String getAlarmListeners()
    {
        return CONFIGUTIL.getString(ALARM_LISTENERS_KEY, DEFAULT_ALARM_LISTENERS);
    }

    /**
     * 利用するAlarmListener名が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetAlarmListeners()
    {
        return isKeyExist(ALARM_LISTENERS_KEY);
    }

    /**
     * 利用するAlarmListener名をセットする。
     * ","区切りで複数指定することができる。
     *
     * @param alarmListeners 利用するAlarmListener名
     */
    public void setAlarmListeners(final String alarmListeners)
    {
        CONFIGUTIL.setString(ALARM_LISTENERS_KEY, alarmListeners);
    }

    /**
     * ログサイズの最大値を取得する。
     * @return ログサイズの最大値
     */
    public int getLogJvnMax()
    {
        return CONFIGUTIL.getInteger(LOG_JVN_MAX_KEY, DEFAULT_LOG_JVN_MAX);
    }

    /**
     * Zip化するログのファイル数を取得する。
     * @return Zip化するログのファイル数
     */
    public int getLogZipMax()
    {
        return CONFIGUTIL.getInteger(LOG_ZIP_MAX_KEY, DEFAULT_LOG_ZIP_MAX);
    }

    /**
     * Jvnログファイルを出力するかどうかを返す。
     * @return ログファイルを出力するかどうか
     */
    public boolean isLogJvnFile()
    {
        return CONFIGUTIL.getBoolean(LOG_JVN_FILE, DEFAULT_LOG_JVN_FILE);
    }

    /**
     * Jvnログファイルを出力するかどうかを設定する。
     * @param logJvnFile ログファイルを出力するかどうか
     */
    public void setLogJvnFile(final boolean logJvnFile)
    {
        CONFIGUTIL.setBoolean(LOG_JVN_FILE, logJvnFile);
    }

    /**
     * ログをZip化するかどうかを返す。
     * @return true:ログをZip化する、false:ログをZip化しない。
     */
    public boolean isLogZipMax()
    {
        return isKeyExist(LOG_ZIP_MAX_KEY);
    }

    /**
     * 記録条件判定クラス名を返す
     *
     * @return クラス名
     */
    public String getRecordStrategy()
    {
        return CONFIGUTIL.getString(RECORDSTRATEGY_KEY, DEFAULT_RECORDSTRATEGY);
    }

    /**
     * 記録条件判定クラス名が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isRecordStrategy()
    {
        return isKeyExist(RECORDSTRATEGY_KEY);
    }

    /**
     * 利用するTelegramListener名を返す。
     * ","区切りで複数指定することができる。
     *
     * @return 利用するTelegramListener名
     */
    public String getTelegramListeners()
    {
        return CONFIGUTIL.getString(TELERAM_LISTENERS_KEY, DEFAULT_TELEGEAM_LISTENERS);
    }

    /**
     * 利用するTelegramListener名が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetTelegramListener()
    {
        return isKeyExist(TELERAM_LISTENERS_KEY);
    }

    /**
     * Javelinのシステムログの最大ファイル数を取得する。
     *
     * @return Javelinのシステムログの最大ファイル数。
     */
    public int getSystemLogNumMax()
    {
        return CONFIGUTIL.getInteger(SYSTEM_LOG_NUM_MAX_KEY, DEFAULT_SYSTEM_LOG_NUM_MAX);
    }

    /**
     * MBeanManagerが持つ情報をシリアライズするファイル名を返す。
     *
     * @return 利用するファイル名
     */
    public String getSerializeFile()
    {
        String relativePath = CONFIGUTIL.getString(SERIALIZE_FILE_KEY, DEFAULT_SERIALIZE_FILE);
        return CONFIGUTIL.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * MBeanManagerが持つ情報をシリアライズするファイル名が設定されているかどうかを調べる。
     *
     * @return 利用するファイル名
     */
    public boolean isSetSerializeFile()
    {
        return isKeyExist(SERIALIZE_FILE_KEY);
    }

    /**
     * Javelinのシステムログの最大ファイルサイズを取得する。
     *
     * @return Javelinのシステムログの最大ファイルサイズ。
     */
    public int getSystemLogSizeMax()
    {
        return CONFIGUTIL.getInteger(SYSTEM_LOG_SIZE_MAX_KEY, DEFAULT_SYSTEM_LOG_SIZE_MAX);
    }

    /**
     * システムログのレベルを取得する。
     * @return システムログのレベル
     */
    public String getSystemLogLevel()
    {
        return CONFIGUTIL.getLogLevel(SYSTEM_LOG_LEVEL_KEY, DEFAULT_SYSTEM_LOG_LEVEL);
    }

    /**
     * Javelinのイベントレベルを取得します。
     * @return Javelinのイベントレベル
     */
    public String getEventLevel()
    {
        return CONFIGUTIL.getEventLevel(EVENT_LEVEL_KEY, DEFAULT_EVENT_LEVEL).toUpperCase();
    }

    /**
     * Javelinのイベントレベルを設定します。
     * @param eventLevel Javelinのイベントレベル
     */
    public void setEventLevel(final String eventLevel)
    {
        CONFIGUTIL.setString(EVENT_LEVEL_KEY, eventLevel);
    }

    /**
     * CallTreeの最大値を取得する。
     * @return CallTreeの最大値
     */
    public int getCallTreeMax()
    {
        return CONFIGUTIL.getInteger(CALL_TREE_MAX_KEY, DEFAULT_CALL_TREE_MAX);
    }

    /**
     * CallTreeの最大値を設定する。
     * @param callTreeMax CallTreeの最大値
     */
    public void setCallTreeMax(final int callTreeMax)
    {
        CONFIGUTIL.setInteger(CALL_TREE_MAX_KEY, callTreeMax);
    }

    /**
     * アプリケーション実行時に例外をアラーム通知するかどうか。
     * @return true:アラーム通知する、false:アラーム通知しない。
     */
    public boolean isAlarmException()
    {
        return CONFIGUTIL.getBoolean(ALARM_EXCEPTION_KEY, DEFAULT_ALARM_EXCEPTION);
    }

    /**
     * アプリケーション実行時に例外をアラーム通知するかどうか設定する。
     *
     * @param isAlarmException 例外をアラーム通知するならtrue
     */
    public void setAlarmException(final boolean isAlarmException)
    {
        CONFIGUTIL.setBoolean(ALARM_EXCEPTION_KEY, isAlarmException);
    }

    /**
     * １クラス辺り保持するInvocation（メソッド呼び出し）最大数を取得する。
     * @return １クラス辺り保持するInvocation（メソッド呼び出し）最大数
     */
    public int getRecordInvocationMax()
    {
        return CONFIGUTIL.getInteger(RECORD_INVOCATION_MAX_KEY, DEFAULT_REC_INVOCATION_MAX);
    }

    /**
     * １クラス辺り保持するInvocation（メソッド呼び出し）最大数を取得する。
     * @param recInvocationMax １クラス辺り保持するInvocation（メソッド呼び出し）最大数
     */
    public void setRecordInvocationMax(final int recInvocationMax)
    {
        CONFIGUTIL.setInteger(RECORD_INVOCATION_MAX_KEY, recInvocationMax);
    }

    /**
     * アラーム送信間隔の最小値を取得する。
     * 
     * 前回アラーム送信・Javelinログ出力を行った際から
     * 経過した時間がこの最小値を超えていた場合のみ、アラーム送信・Javelinログ出力を行う。
     * @return アラーム送信間隔の最小値。
     */
    public long getAlarmMinimumInterval()
    {
        return CONFIGUTIL.getLong(ALARM_MINIMUM_INTERVAL_KEY, DEFAULT_ALARM_MINIMUM_INTERVAL);
    }

    /**
     * 同一のイベントを検出する間隔を取得する。
     * 
     * @return 同一のイベントを検出する間隔。
     */
    public long getEventInterval()
    {
        return CONFIGUTIL.getLong(EVENT_INTERVAL_KEY, DEFAULT_EVENT_INTERVAL);
    }

    /**
     * アラーム送信間隔の最小値を設定する。
     * 
     * 前回アラーム送信・Javelinログ出力を行った際から
     * 経過した時間がこの最小を超えていた場合のみ、アラーム送信・Javelinログ出力を行う。
     * 
     * @param alarmMinimumInterval 閾値。
     */
    public void setAlarmMinimumInterval(final long alarmMinimumInterval)
    {
        CONFIGUTIL.setLong(ALARM_MINIMUM_INTERVAL_KEY, alarmMinimumInterval);
    }

    /**
     * Turn Around Timeを計測するかどうかを設定する。
     *
     * @param tatEnabled Turn Around Timeを計測するならtrue
     */
    public void setTatEnabled(final boolean tatEnabled)
    {
        CONFIGUTIL.setBoolean(TAT_ENABLED_KEY, tatEnabled);
    }

    /**
     * Turn Around Timeを計測するかどうか。
     * @return true:計測する、false:計測しない。
     */
    public boolean isTatEnabled()
    {
        return CONFIGUTIL.getBoolean(TAT_ENABLED_KEY, DEFAULT_TAT_ENABLED);
    }

    /**
     * Turn Around Timeの保持期間をセットする。
     *
     * @param tatKeepTime Turn Around Timeの保持期間
     */
    public void setTatKeepTime(final long tatKeepTime)
    {
        CONFIGUTIL.setLong(TAT_KEEP_TIME_KEY, tatKeepTime);
    }

    /**
     * Turn Around Timeの保持期間を取得する。
     * 
     * @return Turn Around Timeの保持期間
     */
    public long getTatKeepTime()
    {
        return CONFIGUTIL.getLong(TAT_KEEP_TIME_KEY, DEFAULT_TAT_KEEP_TIME);
    }

    /**
     * Turn Around Timeの値が　0　の場合に、　0　の出力を継続する時間をセットする。
     *
     * @param tatZeroKeepTime Turn Around Timeの値が　0　の場合に、　0　の出力を継続する時間
     */
    public void setTatZeroKeepTime(final long tatZeroKeepTime)
    {
        CONFIGUTIL.setLong(TAT_ZERO_KEEP_TIME_KEY, tatZeroKeepTime);
    }

    /**
     * Turn Around Timeの値が　0　の場合に、　0　の出力を継続する時間を取得する。
     *
     * @return Turn Around Timeの値が　0　の場合に、　0　の出力を継続する時間
     */
    public long getTatZeroKeepTime()
    {
        return CONFIGUTIL.getLong(TAT_ZERO_KEEP_TIME_KEY, DEFAULT_TAT_ZERO_KEEP_TIME);
    }

    /**
     * ネットワーク入力量を取得するかどうかを取得する。
     * 
     * @return ネットワーク入力量を取得するかどうか
     */
    public boolean isNetInputMonitor()
    {
        return CONFIGUTIL.getBoolean(NET_INPUT_MONITOR, DEF_NET_INPUT_MONITOR);
    }

    /**
     * ネットワーク入力量を取得するかどうかを設定する。
     * 
     * @param isNetInputMonitor ネットワーク入力量を取得するかどうか
     */
    public void setNetInputMonitor(final boolean isNetInputMonitor)
    {
        CONFIGUTIL.setBoolean(NET_INPUT_MONITOR, isNetInputMonitor);
    }

    /**
     * ネットワーク出力量を取得するかどうかを取得する。
     * 
     * @return ネットワーク出力量を取得するかどうか
     */
    public boolean isNetOutputMonitor()
    {
        return CONFIGUTIL.getBoolean(NET_OUTPUT_MONITOR, DEF_NET_OUTPUT_MONITOR);
    }

    /**
     * ネットワーク出力量を取得するかどうかを設定する。
     * 
     * @param isNetOutputMonitor ネットワーク出力量を取得するかどうか
     */
    public void setNetOutputMonitor(final boolean isNetOutputMonitor)
    {
        CONFIGUTIL.setBoolean(NET_OUTPUT_MONITOR, isNetOutputMonitor);
    }

    /**
     * ファイル入力量を取得するかどうかを取得する。
     * 
     * @return ファイル入力量を取得するかどうか
     */
    public boolean isFileInputMonitor()
    {
        return CONFIGUTIL.getBoolean(FILE_INPUT_MONITOR, DEF_FILE_INPUT_MONITOR);
    }

    /**
     * ファイル入力量を取得するかどうかを設定する。
     * 
     * @param isFileInputMonitor ファイル入力量を取得するかどうか
     */
    public void setFileInputMonitor(final boolean isFileInputMonitor)
    {
        CONFIGUTIL.setBoolean(FILE_INPUT_MONITOR, isFileInputMonitor);
    }

    /**
     * ファイル出力量を取得するかどうかを取得する。
     * 
     * @return ファイル出力量を取得するかどうか
     */
    public boolean isFileOutputMonitor()
    {
        return CONFIGUTIL.getBoolean(FILE_OUTPUT_MONITOR, DEF_FILE_OUTPUT_MONITOR);
    }

    /**
     * ファイル出力量を取得するかどうかを設定する。
     * 
     * @param isFileOutputMonitor ファイル出力量を取得するかどうか
     */
    public void setFileOutputMonitor(final boolean isFileOutputMonitor)
    {
        CONFIGUTIL.setBoolean(FILE_OUTPUT_MONITOR, isFileOutputMonitor);
    }

    /**
     * ファイナライズ待ちオブジェクト数を取得するかどうかを取得する。
     * 
     * @return ファイナライズ待ちオブジェクト数を取得するかどうか
     */
    public boolean isFinalizationCount()
    {
        return CONFIGUTIL.getBoolean(FINALIZATION_COUNT_MONITOR, DEF_FINALIZATION_COUNT_MONITOR);
    }

    /**
     * ファイナライズ待ちオブジェクト数を取得するかどうかを設定する。
     * 
     * @param isFinalizationCount ファイナライズ待ちオブジェクト数を取得するかどうか
     */
    public void setFinalizationCount(final boolean isFinalizationCount)
    {
        CONFIGUTIL.setBoolean(FINALIZATION_COUNT_MONITOR, isFinalizationCount);
    }

    /**
     * メソッド呼び出し間隔超過を監視するかどうかを取得する。
     * 
     * @return メソッド呼び出し間隔超過を監視するかどうか
     */
    public boolean isIntervalMonitor()
    {
        return CONFIGUTIL.getBoolean(INTERVAL_ERROR_MONITOR, DEF_INTERVAL_ERROR_MONITOR);
    }

    /**
     * メソッド呼び出し間隔超過を監視するかどうかを設定する。
     * 
     * @param isIntervalMonitor メソッド呼び出し間隔超過を監視するかどうか
     */
    public void setIntervalMonitor(final boolean isIntervalMonitor)
    {
        CONFIGUTIL.setBoolean(INTERVAL_ERROR_MONITOR, isIntervalMonitor);
    }

    /**
     * HttpSessionのインスタンス数を監視するかどうかを取得する。
     * 
     * @return HttpSessionのインスタンス数を監視するかどうか
     */
    public boolean isHttpSessionCount()
    {
        return CONFIGUTIL.getBoolean(HTTP_SESSION_COUNT_MONITOR, DEF_HTTP_SESSION_COUNT_MONITOR);
    }

    /**
     * HttpSessionのインスタンス数を監視するかどうかを設定する。
     * 
     * @param isHttpSessionCount HttpSessionのインスタンス数を監視するかどうか
     */
    public void setHttpSessionCount(final boolean isHttpSessionCount)
    {
        CONFIGUTIL.setBoolean(HTTP_SESSION_COUNT_MONITOR, isHttpSessionCount);
    }

    /**
     * HttpSessionへの登録オブジェクト総サイズを監視するかどうかを取得する。
     * 
     * @return HttpSessionへの登録オブジェクト総サイズを監視するかどうか
     */
    public boolean isHttpSessionSize()
    {
        return CONFIGUTIL.getBoolean(HTTP_SESSION_SIZE_MONITOR, DEF_HTTP_SESSION_SIZE_MONITOR);
    }

    /**
     * HttpSessionへの登録オブジェクト総サイズを監視するかどうかを設定する。
     * 
     * @param isHttpSessionSize HttpSessionへの登録オブジェクト総サイズを監視するかどうか
     */
    public void setHttpSessionSize(final boolean isHttpSessionSize)
    {
        CONFIGUTIL.setBoolean(HTTP_SESSION_SIZE_MONITOR, isHttpSessionSize);
    }

    /**
     * Javelinの設定オブジェクトを作成する。 Javelin起動時にのみ呼び出される。
     * 
     * @param absoluteJarDirectory Jarが存在する絶対パス
     */
    public JavelinConfigBase(final String absoluteJarDirectory)
    {
        CONFIGUTIL.setAbsoluteJarDirectory(absoluteJarDirectory);
    }

    /**
     * ログ出力から除外する対象を記述したフィルタファイル名を返す。
     * 
     * @return ファイル名
     */
    public String getExclude()
    {
        String relativePath = CONFIGUTIL.getString(EXCLUDE, DEF_EXCLUDE);
        return CONFIGUTIL.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * ログ出力をする対象を記述したフィルタファイル名を返す。
     * 
     * @return ファイル名
     */
    public String getInclude()
    {
        String relativePath = CONFIGUTIL.getString(INCLUDE, DEF_INCLUDE);
        return CONFIGUTIL.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * JVNファイルダウンロード時の最大バイト数を返す。
     * 
     * @return JVNファイルダウンロード時の最大バイト数。
     */
    public int getJvnDownloadMax()
    {
        return CONFIGUTIL.getInteger(JVN_DOWNLOAD_MAX, DEF_JVN_DOWNLOAD_MAX);
    }

    /**
     * JVNファイルダウンロード時の最大バイト数を返す。
     * 
     * @param jvnDownloadMax JVNファイルダウンロード時の最大バイト数。
     */
    public void setJvnDownloadMax(final int jvnDownloadMax)
    {
        CONFIGUTIL.setInteger(JVN_DOWNLOAD_MAX, jvnDownloadMax);
    }

    /**
     * "javelin." で開始するオプションを上書きする。
     * 
     * @param properties オプションリスト
     */
    public void overwriteProperty(final Properties properties)
    {
        Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements())
        {
            String propertyName = (String)propertyNames.nextElement();
            if (propertyName.startsWith(JAVELIN_PREFIX))
            {
                String propertyValue = properties.getProperty(propertyName);
                CONFIGUTIL.setString(propertyName, propertyValue);
            }
        }
    }

    /**
     * 警告を発生させるCPU時間の閾値を設定する。
     * 
     * @param cpuTime CPU時間
     */
    public void setAlarmCpuThreashold(final long cpuTime)
    {
        CONFIGUTIL.setLong(ALARM_CPUTHRESHOLD, cpuTime);
    }

    /**
     * 警告を発生させるCPU時間の閾値を取得する。
     * 
     * @return 警告を発生させるCPU時間の閾値
     */
    public long getAlarmCpuThreashold()
    {
        return CONFIGUTIL.getLong(ALARM_CPUTHRESHOLD, DEF_ALARM_CPUTHRESHOLD);
    }

    /**
     * ライセンスファイルパスを返す。
     * 
     * @return ライセンスファイルパス。
     */
    public String getLicensePath()
    {
        String relativePath = CONFIGUTIL.getString(LICENSEPATH, DEF_LICENSEPATH);
        return CONFIGUTIL.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * クラス名を単純化するかどうかのフラグ。
     * 
     * @return 単純化する場合はtrue。
     */
    public boolean isClassNameSimplify()
    {
        boolean result = CONFIGUTIL.getBoolean(CLASSNAME_SIMPLIFY, DEF_CLASSNAME_SIMPLIFY);
        return result;
    }

    /**
     * Collectionのメモリリーク検出を行うかどうかを設定します。<br />
     * 
     * @param collectionMonitor Collectionのメモリリーク検出を行う場合、<code>true</code>
     */
    public void setCollectionMonitor(final boolean collectionMonitor)
    {
        CONFIGUTIL.setBoolean(COLLECTION_MONITOR, collectionMonitor);
    }

    /**
     * Collectionのメモリリーク検出を行うかどうかを返します。<br />
     * 
     * @return Collectionのメモリリーク検出を行う場合、<code>true</code>
     */
    public boolean isCollectionMonitor()
    {
        return CONFIGUTIL.getBoolean(COLLECTION_MONITOR, DEF_COLLECTION_MONITOR);
    }

    /**
     * Collection、Mapのサイズを記録する閾値を設定する。
     * 
     * @param collectionSizeThreshold COLLECTION_SIZE_THRESHOLD
     */
    public void setCollectionSizeThreshold(final int collectionSizeThreshold)
    {
        CONFIGUTIL.setInteger(COLLECTION_SIZE_THRESHOLD, collectionSizeThreshold);
    }

    /**
     * Collection、Mapのサイズを記録する閾値を設定する。
     * 
     * @return Collection、Mapのサイズを記録する閾値。
     */
    public int getCollectionSizeThreshold()
    {
        return CONFIGUTIL.getInteger(COLLECTION_SIZE_THRESHOLD, DEF_COLLECTION_SIZE);
    }

    /**
     * コレクションの数を監視する際の出力チェックの間隔を取得する。
     * 
     * @return コレクションの数を監視する際の出力チェックの間隔。
     */
    public int getCollectionInterval()
    {
        return CONFIGUTIL.getInteger(COLLECTION_INTERVAL, DEF_COLLECTION_INTERVAL);
    }

    /**
     * コレクションの数を監視する際に保持するスタックトレースの数。
     * 
     * @return コレクションの数を監視する際に保持するスタックトレースの数。
     */
    public int getCollectionTraceMax()
    {
        return CONFIGUTIL.getInteger(COLLECTION_TRACE_MAX, DEF_COLLECTION_TRACE_MAX);
    }

    /**
     * スタックトレースの表示に使う深さです。<br />
     * 
     * @return スタックトレースの表示に使う深さ。
     */
    public int getTraceDepth()
    {
        return CONFIGUTIL.getInteger(TRACE_DEPTH, DEF_COLLECTION_TRACE_DEPTH);
    }

    /**
     * コレクションの数を監視する際に保持するスタックトレースの深さ。
     * 
     * @return コレクションの数を監視する際に保持するスタックトレースの深さ。
     */
    public int getCollectionLeakDetectDepth()
    {
        return CONFIGUTIL.getInteger(COLLECTION_LEAKDETECT_DEPTH, DEF_COLLECTION_LEAKDETECT_DEPTH);
    }

    /**
     * クラスヒストグラムを取得する際に、GCを行うかどうかを取得する。
     * 
     * @return クラスヒストグラムを取得する際に、GCを行うかどうか。
     */
    public boolean getClassHistoGC()
    {
        return CONFIGUTIL.getBoolean(CLASS_HISTO_GC, DEF_CLASS_HISTO_GC);
    }

    /**
     * クラスヒストグラムを取得する際に、GCを行うかどうかを設定する。
     * 
     * @param classHistoGC クラスヒストグラムを取得する際に、GCを行うかどうか。
     */
    public void setClassHistoGC(final boolean classHistoGC)
    {
        CONFIGUTIL.setBoolean(CLASS_HISTO_GC, classHistoGC);
    }

    /**
     * クラスヒストグラムの上位何件を取得するかを取得する。
     * 
     * @return クラスヒストグラムの上位何件を取得するか。
     */
    public int getClassHistoMax()
    {
        return CONFIGUTIL.getInteger(CLASS_HISTO_MAX, DEF_CLASS_HISTO_MAX);
    }

    /**
     * クラスヒストグラムの上位何件を取得するかを設定する。
     * 
     * @param classHistoMax クラスヒストグラムの上位何件を取得するか。
     */
    public void setClassHistoMax(final int classHistoMax)
    {
        CONFIGUTIL.setInteger(CLASS_HISTO_MAX, classHistoMax);
    }

    /**
     * クラスヒストグラム取得間隔(ミリ秒)を取得する。
     * 
     * @return クラスヒストグラム取得間隔(ミリ秒)。
     */
    public int getClassHistoInterval()
    {
        return CONFIGUTIL.getInteger(CLASS_HISTO_INTERVAL, DEF_CLASS_HISTO_INTERVAL);
    }

    /**
     * クラスヒストグラム取得間隔(ミリ秒)を設定する。
     * 
     * @param classHistoInterval クラスヒストグラム取得間隔(ミリ秒)。
     */
    public void setClassHistoInterval(final int classHistoInterval)
    {
        CONFIGUTIL.setInteger(CLASS_HISTO_INTERVAL, classHistoInterval);
    }

    /**
     * クラスヒストグラムを取得するかどうかを取得する。
     * 
     * @return クラスヒストグラムを取得するかどうか。
     */
    public boolean getClassHisto()
    {
        return CONFIGUTIL.getBoolean(CLASS_HISTO, DEF_CLASS_HISTO);
    }

    /**
     * クラスヒストグラムを取得するかどうかを設定する。
     * 
     * @param classHisto クラスヒストグラムを取得するかどうか。
     */
    public void setClassHisto(final boolean classHisto)
    {
        CONFIGUTIL.setBoolean(CLASS_HISTO, classHisto);
    }

    /**
     * クラスヒストグラムを取得するかどうかを取得する。
     * 
     * @return クラスヒストグラムを取得するかどうか。
     */
    public boolean getDetach()
    {
        return CONFIGUTIL.getBoolean(DETACH, DEF_DETACH);
    }

    /**
     * 線形検索検出を行うかどうかを返します。<br />
     * 
     * @return 線形検索の検出を行う場合、<code>true</code>
     */
    public boolean isLinearSearchMonitor()
    {
        return CONFIGUTIL.getBoolean(LINEARSEARCH_ENABLED_KEY, DEF_LINEARSEARCH_ENABLED);
    }

    /**
     * 線形検索検出を行うかどうかを設定します。<br />
     * 
     * @param linearSearchEnabled 線形検索検出を行うかどうか
     */
    public void setLinearSearchMonitor(final boolean linearSearchEnabled)
    {
        CONFIGUTIL.setBoolean(LINEARSEARCH_ENABLED_KEY, linearSearchEnabled);
    }

    /**
     * 線形検索対象となるリストサイズの閾値を取得します。<br />
     * 
     * @return 線形検索対象となるリストサイズの閾値
     */
    public int getLinearSearchListSize()
    {
        return CONFIGUTIL.getInteger(LINEARSEARCH_SIZE, DEF_LINEARSEARCH_SIZE);
    }

    /**
     * 線形検索対象とするリストサイズの閾値を設定します。<br />
     * 
     * @param size 線形検索対象とするリストサイズの閾値
     */
    public void setLinearSearchListSize(final int size)
    {
        CONFIGUTIL.setInteger(LINEARSEARCH_SIZE, size);
    }

    /**
     * 線形検索対象となる、リストに対する線形アクセス回数の割合の閾値を取得します。<br />
     * 
     * @return リストに対する線形アクセス回数の閾値
     */
    public double getLinearSearchListRatio()
    {
        return CONFIGUTIL.getDouble(LINEARSEARCH_RATIO, DEF_LINEARSEARCH_RATIO);
    }

    /**
     * 線形検索対象となる、リストに対する線形アクセス回数の割合の閾値を取得します。<br />
     * 
     * @param linearSearchRatio リストに対する線形アクセス回数の割合の閾値
     */
    public void setLinearSearchListRatio(final double linearSearchRatio)
    {
        CONFIGUTIL.setDouble(LINEARSEARCH_RATIO, linearSearchRatio);
    }

    /**
     * スレッド監視を行うかどうかを取得する。
     * 
     * @return スレッド監視を行う場合は<code>true</code>。
     */
    public boolean getThreadMonitor()
    {
        return CONFIGUTIL.getBoolean(THREAD_MONITOR, DEF_THREAD_MONITOR);
    }

    /**
     * スレッド監視を行う間隔(ミリ秒)を取得する。
     * 
     * @return スレッド監視を行う間隔(ミリ秒)。
     */
    public long getThreadMonitorInterval()
    {
        return CONFIGUTIL.getLong(THREAD_MONITOR_INTERVAL, DEF_THREAD_MON_INTERVAL);
    }

    /**
     * スレッド監視の際に出力するスタックトレースの深さを取得する。
     * 
     * @return スレッド監視の際に出力するスタックトレースの深さ。
     */
    public int getThreadMonitorDepth()
    {
        return CONFIGUTIL.getInteger(THREAD_MONITOR_DEPTH, DEF_THREAD_MON_DEPTH);
    }

    /**
     * フルスレッドダンプを出力するかどうかを返します。<br />
     * 
     * @return フルスレッドダンプを出力するときに<code>true</code>。
     */
    public boolean isThreadDump()
    {
        return CONFIGUTIL.getBoolean(THREAD_DUMP_MONITOR, DEF_THREAD_DUMP_MONITOR);
    }

    /**
     * フルスレッドダンプを出力するかどうかを設定ます。<br />
     * 
     * @param threadDumpMonitor フルスレッドダンプを出力するかどうか。
     */
    public void setThreadDumpMonitor(final boolean threadDumpMonitor)
    {
        CONFIGUTIL.setBoolean(THREAD_DUMP_MONITOR, threadDumpMonitor);
    }

    /**
     * フルスレッドダンプ出力間隔を返します。<br />
     * 
     * @return フルスレッドダンプ出力間隔。
     */
    public int getThreadDumpInterval()
    {
        return CONFIGUTIL.getInteger(THREAD_DUMP_INTERVAL, DEF_THREAD_DUMP_INTERVAL);
    }

    /**
     * フルスレッドダンプ出力間隔を設定ます。<br />
     * 
     * @param threadDumpInterval フルスレッドダンプ出力間隔。
     */
    public void setThreadDumpInterval(final int threadDumpInterval)
    {
        CONFIGUTIL.setInteger(THREAD_DUMP_INTERVAL, threadDumpInterval);
    }

    /**
     * フルスレッドダンプ出力のスレッド数の閾値を返します。<br />
     * 
     * @return フルスレッドダンプ出力のスレッド数の閾値
     */
    public int getThreadDumpThreadNum()
    {
        return CONFIGUTIL.getInteger(THREAD_DUMP_THREAD, DEF_THREAD_DUMP_THREAD);
    }

    /**
     * フルスレッドダンプ出力のスレッド数の閾値を設定します。<br />
     * 
     * @param threadDumpNum フルスレッドダンプ出力のスレッド数の閾値
     */
    public void setThreadDumpThreadNum(final int threadDumpNum)
    {
        CONFIGUTIL.setInteger(THREAD_DUMP_THREAD, threadDumpNum);
    }

    /**
     * フルスレッドダンプ出力のCPU使用率の閾値を返します。<br />
     * 
     * @return フルスレッドダンプ出力のCPU使用率の閾値
     */
    public int getThreadDumpCpu()
    {
        return CONFIGUTIL.getInteger(THREAD_DUMP_CPU, DEF_THREAD_DUMP_CPU);
    }

    /**
     * フルスレッドダンプ出力のCPU使用率の閾値を設定します。<br />
     * 
     * @param threadDumpCpu フルスレッドダンプ出力のCPU使用率の閾値
     */
    public void setThreadDumpCpu(final int threadDumpCpu)
    {
        CONFIGUTIL.setInteger(THREAD_DUMP_CPU, threadDumpCpu);
    }

    /**
     * フルGCを検出するかどうかを設定します。<br />
     * 
     * @param fullGCMonitor フルGCを検出する場合、<code>true</code>
     */
    public void setFullGCMonitor(final boolean fullGCMonitor)
    {
        CONFIGUTIL.setBoolean(FULLGC_MONITOR, fullGCMonitor);
    }

    /**
     * フルGCを検出するかどうかを取得します。<br />
     * 
     * @return フルGCを検出する場合、<code>true</code>
     */
    public boolean isFullGCMonitor()
    {
        return CONFIGUTIL.getBoolean(FULLGC_MONITOR, DEF_FULLGC_MONITOR);
    }

    /**
     * フルGCを行うGarbageCollector名のリストを取得します。<br />
     * 
     * @return フルGCを検出する場合、<code>true</code>
     */
    public String getFullGCList()
    {
        return CONFIGUTIL.getString(FULLGC_LIST, DEF_FULLGC_LIST);
    }

    /**
     * フルGC検出を行うGC時間の閾値を取得します。<br />
     * 
     * @return フルGC検出を行うGC時間の閾値
     */
    public int getFullGCThreshold()
    {
        return CONFIGUTIL.getInteger(FULLGC_THREASHOLD, DEF_FULLGC_THRESHOLD);
    }

    /**
     * フルGC検出を行うGC時間の閾値を設定します。<br />
     * 
     * @param threshold フルGC検出を行うGC時間の閾値
     */
    public void setFullGCThreshold(final int threshold)
    {
        CONFIGUTIL.setInteger(FULLGC_THREASHOLD, threshold);
    }

    /**
     * 継承を調べる深さの最大値を取得する。
     * 
     * @return クラスヒストグラムを取得するかどうか。
     */
    public int getInheritanceDepth()
    {
        return CONFIGUTIL.getInteger(INHERITANCE_DEPTH, DEF_INHERITANCE_DEPTH);
    }

    /**
     * スレッド監視を行うかどうかを設定する。
     * 
     * @param threadMonitor スレッド監視を行う場合はtrue。
     */
    public void setThreadMonitor(final boolean threadMonitor)
    {
        CONFIGUTIL.setBoolean(THREAD_MONITOR, threadMonitor);
    }

    /**
     * スレッド監視を行う間隔(ミリ秒)を設定する。
     * 
     * @param threadMonitorInterval スレッド監視を行う間隔(ミリ秒)。
     */
    public void setThreadMonitorInterval(final long threadMonitorInterval)
    {
        CONFIGUTIL.setLong(THREAD_MONITOR_INTERVAL, threadMonitorInterval);
    }

    /**
     * スレッド監視の際に出力するスタックトレースの深さを取得する。
     * 
     * @param threadMonitorDepth スレッド監視の際に出力するスタックトレースの深さ。
     */
    public void setThreadMonitorDepth(final int threadMonitorDepth)
    {
        CONFIGUTIL.setInteger(THREAD_MONITOR_DEPTH, threadMonitorDepth);
    }

    /**
     * ブロック回数が多すぎるかどうかの閾値を取得する。
     * 
     * @return ブロック回数が多すぎるかどうかの閾値。
     */
    public long getBlockThreshold()
    {
        return CONFIGUTIL.getLong(THREAD_BLOCK_THRESHOLD, DEF_THREAD_BLOCK_THRESHOLD);
    }

    /**
     * ブロック回数が多すぎるかどうかの閾値を設定する。
     * 
     * @param blockThreshold ブロック回数が多すぎるかどうかの閾値。
     */
    public void setBlockThreshold(final long blockThreshold)
    {
        CONFIGUTIL.setLong(THREAD_BLOCK_THRESHOLD, blockThreshold);
    }

    /**
     * ブロック継続イベントを出力する際のブロック継続時間の閾値を取得する。
     * 
     * @return ブロック継続イベントを出力する際のブロック継続時間の閾値
     */
    public long getBlockTimeThreshold()
    {
        return CONFIGUTIL.getLong(THREAD_BLOCKTIME_THRESHOLD, DEF_THREAD_BLOCKTIME_THRESHOLD);
    }

    /**
     * ブロック継続イベントを出力する際のブロック継続時間の閾値を設定する。
     * 
     * @param blockTimeThreshold ブロック継続イベントを出力する際のブロック継続時間の閾値
     */
    public void setBlockTimeThreshold(final long blockTimeThreshold)
    {
        CONFIGUTIL.setLong(THREAD_BLOCKTIME_THRESHOLD, blockTimeThreshold);
    }

    /**
     * ブロック回数が閾値を超えた際に取得するスレッド情報の数を取得する。
     * 
     * @return ブロック回数が閾値を超えた際に取得するスレッド情報の数。
     */
    public int getBlockThreadInfoNum()
    {
        return CONFIGUTIL.getInteger(THREAD_BLOCK_THREADINFO_NUM, DEF_THREAD_BLOCK_THREADINFO_NUM);
    }

    /**
     * ブロック回数が閾値を超えた際に取得するスレッド情報の数を設定する。
     * 
     * @param blockThreadInfoNum ブロック回数が閾値を超えた際に取得するスレッド情報の数。
     */
    public void setBlockThreadInfoNum(final int blockThreadInfoNum)
    {
        CONFIGUTIL.setInteger(THREAD_BLOCK_THREADINFO_NUM, blockThreadInfoNum);
    }

    /**
     * メソッドに対する呼び出し間隔の閾値定義を取得する。
     * 
     * @return メソッドに対する呼び出し間隔の閾値定義。
     */
    public String getIntervalThreshold()
    {
        return CONFIGUTIL.getString(INTERVAL_THRESHOLD, DEF_INTERVAL_THRESHOLD);
    }

    /**
     * メソッドに対する呼び出し間隔の閾値定義を設定する。
     * 
     * @param callCountThreshold メソッドに対する呼び出し間隔の閾値定義。
     */
    public void setIntervalThreshold(final String callCountThreshold)
    {
        CONFIGUTIL.setString(INTERVAL_THRESHOLD, callCountThreshold);
    }

    /**
     * メソッドに対する、引数の値ごとの呼び出し間隔の閾値定義を取得する。
     * 
     * @return メソッドに対する、引数の値ごとの呼び出し間隔の閾値定義。
     */
    public String getIntervalPerArgsThreshold()
    {
        return CONFIGUTIL.getString(INTERVAL_PER_ARGS_THRESHOLD, DEF_INTERVAL_PER_ARGS_THRESHOLD);
    }

    /**
     * メソッドに対する、引数の値ごとの呼び出し間隔の閾値定義を設定する。
     * 
     * @param callCountThreshold メソッドに対する、引数の値ごとの呼び出し間隔の閾値定義。
     */
    public void setIntervalPerArgsThreshold(final String callCountThreshold)
    {
        CONFIGUTIL.setString(INTERVAL_PER_ARGS_THRESHOLD, callCountThreshold);
    }

    /**
     * 複数スレッドアクセス監視を行うかどうかを設定します。<br />
     *
     * @param concurrentEnabled 複数スレッドアクセス監視を行うならtrue
     */
    public void setConcurrentAccessMonitored(final boolean concurrentEnabled)
    {
        CONFIGUTIL.setBoolean(CONCURRENT_ENABLED_KEY, concurrentEnabled);
    }

    /**
     * 複数スレッドアクセス監視を行うどうかを返します。<br />
     * 
     * @return true:監視する、false:監視しない。
     */
    public boolean isConcurrentAccessMonitored()
    {
        return CONFIGUTIL.getBoolean(CONCURRENT_ENABLED_KEY, DEFAULT_CONCURRENT_ENABLED);
    }

    /**
     * タイムアウト値設定の監視を行うかどうかを設定します。<br />
     *
     * @param timeoutMonitor タイムアウト値の設定の監視を行う場合、<code>true</code>
     */
    public void setTimeoutMonitor(final boolean timeoutMonitor)
    {
        CONFIGUTIL.setBoolean(TIMEOUT_MONITOR, timeoutMonitor);
    }

    /**
     * タイムアウト値設定の監視を行うかどうかを返します。<br />
     * 
     * @return タイムアウト値の設定の監視を行う場合、<code>true</code>
     */
    public boolean isTimeoutMonitor()
    {
        return CONFIGUTIL.getBoolean(TIMEOUT_MONITOR, DEF_TIMEOUT_MONITOR);
    }

    /**
     * 計測対象から自動除外する呼び出し回数の閾値を返します。<br />
     * 
     * @return 計測対象から自動除外する呼び出し回数の閾値
     */
    public int getAutoExcludeThresholdCount()
    {
        return CONFIGUTIL.getInteger(AUTO_EXCLUDE_THRESHOLD_COUNT, DEF_AUTO_EXCLUDE_THRESHOLD_COUNT);
    }

    /**
     * 計測対象から自動除外する実行時間の閾値を返します。<br />
     * 
     * @return 計測対象から自動除外する実行時間の閾値(単位:ミリ秒)
     */
    public int getAutoExcludeThresholdTime()
    {
        return CONFIGUTIL.getInteger(AUTO_EXCLUDE_THRESHOLD_TIME, DEF_AUTO_EXCLUDE_THRESHOLD_TIME);
    }

    /**
     * メモリリーク検出時に、リークしたコレクションのサイズを出力するかどうかを返します。<br /> 
     * 
     * @return メモリリーク検出時に、リークしたコレクションのサイズを出力するかどうか
     */
    public boolean isLeakCollectionSizePrint()
    {
        return CONFIGUTIL.getBoolean(LEAK_COLLECTIONSIZE_OUT, DEF_LEAK_COLLECTIONSIZE_OUT);
    }

    /**
     * メモリリーク検出時に、リークしたコレクションのサイズを出力するかどうかを設定する。
     * 
     * @param leakCollectionSizePrint メモリリーク検出時に、リークしたコレクションのサイズを出力するかどうか
     */
    public void setLeakCollectionSizePrint(final boolean leakCollectionSizePrint)
    {
        CONFIGUTIL.setBoolean(LEAK_COLLECTIONSIZE_OUT, leakCollectionSizePrint);
    }

    /**
     * Javelinの再接続間隔を取得
     * 
     * @return Javelinの再接続間隔
     */
    public int getJavelinBindInterval()
    {
        return CONFIGUTIL.getInteger(JAVELIN_BIND_INTERVAL, DEF_JAVELIN_BIND_INTERVAL);
    }

    /**
     * Javelinの再接続間隔を設定
     * 
     * @param javelinBindInterval 再接続間隔
     */
    public void setJavelinBindInterval(final int javelinBindInterval)
    {
        CONFIGUTIL.setInteger(JAVELIN_BIND_INTERVAL, javelinBindInterval);
    }

    /**
     * Log4Jのログ出力において、スタックトレースを出力する閾値レベルを取得する。
     * 
     * @return スタックトレースを出力する閾値レベル
     */
    public String getLog4jPrintStackLevel()
    {
        return CONFIGUTIL.getLogLevel(LOG4J_PRINTSTACK_LEVEL, DEF_LOG4J_PRINTSTACK_LEVEL);
    }

    /**
     * Log4Jのログ出力において、スタックトレースを出力する閾値レベルを設定する。
     * 
     * @param log4jPrintStackLevel スタックトレースを出力する閾値レベル
     */
    public void setLog4jPrintStackLevel(final String log4jPrintStackLevel)
    {
        CONFIGUTIL.setString(LOG4J_PRINTSTACK_LEVEL, log4jPrintStackLevel);
    }

    /**
     * デッドロックの監視を行うか、を取得する。<br /> 
     * 
     * @return デッドロックの監視を行うか
     */
    public boolean isDeadLockMonitor()
    {
        return CONFIGUTIL.getBoolean(THREAD_DEADLOCK_MONITOR, DEF_THREAD_DEADLOCK_MONITOR);
    }

    /**
     * デッドロックの監視を行うか、を設定する。<br /> 
     * 
     * @param deadLockMonitor デッドロックの監視を行うか
     */
    public void setDeadLockMonitor(final boolean deadLockMonitor)
    {
        CONFIGUTIL.setBoolean(THREAD_DEADLOCK_MONITOR, deadLockMonitor);
    }

    /**
     * EJBのセッションBeanの呼び出し／応答までの時間の監視を行うかどうかを取得する。<br /> 
     * 
     * @return EJBのセッションBeanの呼び出し／応答までの時間の監視を行うかどうか
     */
    public boolean isEjbSessionMonitor()
    {
        return CONFIGUTIL.getBoolean(EJB_SESSION_MONITOR, DEF_EJB_SESSION_MONITOR);
    }

    /**
     * EJBのセッションBeanの呼び出し／応答までの時間の監視を行うかどうかを設定する。<br /> 
     * 
     * @param ejbSessionMonitor EJBのセッションBeanの呼び出し／応答までの時間の監視を行うかどうか
     */
    public void setEjbSessionMonitor(final boolean ejbSessionMonitor)
    {
        CONFIGUTIL.setBoolean(EJB_SESSION_MONITOR, ejbSessionMonitor);
    }

    /**
     * BottleNeckEye/DataCollectorとの通信用ポートを範囲指定するかを取得する。<br /> 
     * 
     * @return BottleNeckEye/DataCollectorとの通信用ポートを範囲指定するか
     */
    public boolean isAcceptPortIsRange()
    {
        return CONFIGUTIL.getBoolean(ACCEPTPORT_ISRANGE, DEF_ACCEPTPORT_ISRANGE);
    }

    /**
     * BottleNeckEye/DataCollectorとの通信用ポートを範囲指定する際の最大値を取得
     * 
     * @return BottleNeckEye/DataCollectorとの通信用ポートを範囲指定する際の最大値
     */
    public int getAcceptPortRangeMax()
    {
        return CONFIGUTIL.getInteger(ACCEPTPORT_RANGEMAX, DEF_ACCEPTPORT_RANGEMAX);
    }

    public long getAcceptDelay()
    {
        return CONFIGUTIL.getLong(ACCEPT_DELAY_KEY, DEFAULT_ACCEPT_DELAY);
    }

    public int getCpuTimeUnit()
    {
        return CONFIGUTIL.getInteger(CPU_TIME_UNIT_KEY, DEF_CPU_TIME_UNIT);
    }

    public boolean isCallTreeAll()
    {
        return CONFIGUTIL.getBoolean(CALL_TREE_ALL_KEY, DEF_CALL_TREE_ALL);
    }

    public boolean isCallTreeEnabled()
    {
        return CONFIGUTIL.getBoolean(CALL_TREE_ENABLE_KEY, DEF_CALL_TREE_ENABLE);
    }

    /**
     * システムのリソースデータを取得するかどうかを返す。
     * @return システムのリソースデータを取得するかどうか
     */
    public boolean getCollectSystemResources()
    {
        return CONFIGUTIL.getBoolean(COLLECT_SYSTEM_RESOURCES, DEF_COLLECT_SYSTEM_RESOURCES);
    }

    /**
     * システムのリソースデータを取得するかどうかを設定する。
     * @param collectSystemResources システムのリソースデータを取得するかどうか
     */
    public void setCollectSystemResources(final boolean collectSystemResources)
    {
        CONFIGUTIL.setBoolean(COLLECT_SYSTEM_RESOURCES, collectSystemResources);
    }

    /**
     * InvocationFullEventを送信するかどうかを返す。
     * @return InvocationFullEventを送信するかどうか
     */
    public boolean getSendInvocationFullEvent()
    {
        return CONFIGUTIL.getBoolean(SEND_INVOCATION_FULL_EVENT, DEF_SEND_INVOCATION_FULL_EVENT);
    }

    /**
     * InvocationFullEventを送信するかどうかを設定する。
     * @param collectSystemResources InvocationFullEventを送信するかどうか
     */
    public void setSendInvocationFullEvent(final boolean sendInvocationFullEvent)
    {
        CONFIGUTIL.setBoolean(SEND_INVOCATION_FULL_EVENT, sendInvocationFullEvent);
    }

    /**
     * JMXのリソースデータを取得するかどうかを返す。
     * @return JMXのリソースデータを取得するかどうか
     */
    public boolean getCollectJmxResources()
    {
        return CONFIGUTIL.getBoolean(COLLECT_JMX_RESOURCES, DEF_COLLECT_JMX_RESOURCES);
    }

    /**
     * シJMXのリソースデータを取得するかどうかを設定する。
     * @param collectJmxResources JMXのリソースデータを取得するかどうか
     */
    public void setCollectJmxResources(final boolean collectJmxResources)
    {
        CONFIGUTIL.setBoolean(COLLECT_JMX_RESOURCES, collectJmxResources);
    }

    /**
     * ストールメソッドを監視するかどうかを返します。<br />
     * 
     * @return ストールメソッドを監視するときに<code>true</code>。
     */
    public boolean isMethodStallMonitor()
    {
        return CONFIGUTIL.getBoolean(METHOD_STALL_MONITOR, DEF_METHOD_STALL_MONITOR);
    }

    /**
     * ストールメソッドを監視するかどうかを設定ます。<br />
     * 
     * @param methodStallMonitor ストールメソッドを監視するかどうか。
     */
    public void setMethodStallMonitor(final boolean methodStallMonitor)
    {
        CONFIGUTIL.setBoolean(METHOD_STALL_MONITOR, methodStallMonitor);
    }

    /**
     * ストールメソッドを監視する周期を返します。<br />
     * 
     * @return ストールメソッドを監視する周期。
     */
    public int getMethodStallInterval()
    {
        return CONFIGUTIL.getInteger(METHOD_STALL_INTERVAL, DEF_METHOD_STALL_INTERVAL);
    }

    /**
     * ストールメソッドを監視する周期を設定ます。<br />
     * 
     * @param methodStallInterval ストールメソッドを監視する周期。
     */
    public void setMethodStallInterval(final int methodStallInterval)
    {
        CONFIGUTIL.setInteger(METHOD_STALL_INTERVAL, methodStallInterval);
    }

    /**
     * ストールメソッドと判断する閾値を返します。<br />
     * 
     * @return ストールメソッドと判断する閾値。
     */
    public int getMethodStallThreshold()
    {
        return CONFIGUTIL.getInteger(METHOD_STALL_THRESHOLD, DEF_METHOD_STALL_THRESHOLD);
    }

    /**
     * ストールメソッドと判断する閾値を設定ます。<br />
     * 
     * @param methodStallThreshold ストールメソッドと判断する閾値。
     */
    public void setMethodStallThreshold(final int methodStallThreshold)
    {
        CONFIGUTIL.setInteger(METHOD_STALL_THRESHOLD, methodStallThreshold);
    }

    /**
     * ストールメソッド検出時に出力するスタックトレースの深さを返します。<br />
     * 
     * @return ストールメソッド検出時に出力するスタックトレースの深さ。
     */
    public int getMethodStallTraceDepth()
    {
        return CONFIGUTIL.getInteger(METHOD_STALL_TRACE_DEPTH, DEF_METHOD_STALL_TRACE_DEPTH);
    }

    /**
     * ストールメソッド検出時に出力するスタックトレースの深さを設定ます。<br />
     * 
     * @param methodStallTraceDepth ストールメソッド検出時に出力するスタックトレースの深さ。
     */
    public void setMethodStallTraceDepth(final int methodStallTraceDepth)
    {
        CONFIGUTIL.setInteger(METHOD_STALL_TRACE_DEPTH, methodStallTraceDepth);
    }

    /**
     * HTTPステータス出力可否を設定します。
     * 
     * @param isHttpStatusError HTTPステータスエラーを通知するかどうか
     */
    public void setHttpStatusError(final boolean isHttpStatusError)
    {
        CONFIGUTIL.setBoolean(HTTP_STATUS_ERROR_KEY, isHttpStatusError);
    }

    /**
     * HTTPステータス出力可否を取得します。
     * 
     * @return HTTPステータスエラーの通知可否
     */
    public boolean isHttpStatusError()
    {
        return CONFIGUTIL.getBoolean(HTTP_STATUS_ERROR_KEY, DEFAULT_HTTP_STATUS_ERROR);
    }

    /**
     * MBeanサーバのホスト名を取得します。
     * 
     * @return MBeanサーバのホスト名
     */
    public String getJMXHost()
    {
        return CONFIGUTIL.getString(JMX_HOST, DEF_JMX_HOST);
    }

    /**
     * MBeanサーバのホスト名を設定します。
     * 
     * @param hostname MBeanサーバのホスト名
     */
    public void setJMXHost(final String hostname)
    {
        CONFIGUTIL.setString(JMX_HOST, hostname);
    }

    /**
     * MBeanサーバのポート番号を取得します。
     * 
     * @return MBeanサーバのポート番号
     */
    public int getJMXPort()
    {
        return CONFIGUTIL.getInteger(JMX_PORT, DEF_JMX_PORT);
    }

    /**
     * MBeanサーバのポート番号を設定します。
     * 
     * @param port MBeanサーバのポート番号
     */
    public void setJMXPort(final int port)
    {
        CONFIGUTIL.setInteger(JMX_PORT, port);
    }

    /**
     * MBeanサーバの認証ユーザ名を取得します。
     * 
     * @return MBeanサーバの認証ユーザ名
     */
    public String getJMXUserName()
    {
        return CONFIGUTIL.getString(JMX_USER_NAME, DEF_JMX_USER_NAME);
    }

    /**
     * MBeanサーバの認証ユーザ名を設定します。
     * 
     * @param userName MBeanサーバの認証ユーザ名
     */
    public void setJMXUserName(final String userName)
    {
        CONFIGUTIL.setString(JMX_USER_NAME, userName);
    }

    /**
     * MBeanサーバの認証パスワードを取得します。
     * 
     * @return MBeanサーバの認証パスワード
     */
    public String getJMXPassword()
    {
        return CONFIGUTIL.getString(JMX_PASSWORD, DEF_JMX_PASSWORD);
    }

    /**
     * MBeanサーバの認証パスワードを設定します。
     * 
     * @param password MBeanサーバの認証パスワード
     */
    public void setJMXPassword(final String password)
    {
        CONFIGUTIL.setString(JMX_PASSWORD, password);
    }

    /**
     * 接続モード(server/client)を返します。<br />
     * 
     * @return 接続モード(server/client)。
     */
    public String getConnectionMode()
    {
        return CONFIGUTIL.getString(CONNECTION_MODE_KEY, DEF_CONNNECTION_MODE);
    }

    /**
     * 接続モード(server/client)を返します。<br />
     * 
     * @param connectionMode 接続モード(server/client)。
     */
    public void setConnectionMode(final String connectionMode)
    {
        CONFIGUTIL.setString(CONNECTION_MODE_KEY, connectionMode);
    }

    /**
     * 項目名に付与する接頭辞の文字列を取得します。
     * 
     * @return 項目名に付与する接頭辞。
     */
    public String getItemNamePrefix()
    {
        return CONFIGUTIL.getString(ITEMNAME_PREFIX, DEF_ITEMNAME_PREFIX);
    }

    /**
     * 項目名に接頭辞を付与しないパターンを取得します。
     * 
     * @return 項目名に接頭辞を付与しないパターン(前方一致)。
     */
    public String getItemNameNoPrefixList()
    {
        return CONFIGUTIL.getString(ITEMNAME_NOPREFIX_LIST, DEF_ITEMNAME_NOPREFIX_LIST);
    }

    /**
     * HadoopAgentからリソース値を取得するかどうかを指定します。
     * 
     * @return HadoopAgentからリソース値を取得する場合はtrue、そうでない場合はfalse。
     */
    public boolean isCollectHadoopAgentResources()
    {
        return CONFIGUTIL.getBoolean(COLLECT_HADOOP_AGENT_RESOURCES,
                                     DEF_COLLECT_HADOOP_AGENT_RESOURCES);
    }

    /**
     * HBaseAgentからリソース値を取得するかどうかを指定します。
     * 
     * @return HBaseAgentからリソース値を取得する場合はtrue、そうでない場合はfalse。
     */
    public boolean isCollectHBaseAgentResources()
    {
        return CONFIGUTIL.getBoolean(COLLECT_HBASE_AGENT_RESOURCES,
                                     DEF_COLLECT_HBASE_AGENT_RESOURCES);
    }
}
