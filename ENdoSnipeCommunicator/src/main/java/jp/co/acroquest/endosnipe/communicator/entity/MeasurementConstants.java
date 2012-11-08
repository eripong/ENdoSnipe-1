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
package jp.co.acroquest.endosnipe.communicator.entity;

/**
 * 計測値種別の定数インターフェースです。<br />
 * 本インターフェースで定義される計測値種別（<code>TYPE_</code>で始まる定数）は、
 * 基本設計仕様書「表5-1 計測値情報テーブルの初期値」で規定された値です。<br />
 * 追加・変更の際は、基本設計仕様書、 measurementInfo.tsv も修正してください。
 * 
 * @author fujii
 */
public interface MeasurementConstants
{
    /** 計測値種別(現在時刻) */
    int TYPE_TIME = 1;

    /** 計測値種別(取得時刻) */
    int TYPE_ACQUIREDTIME = 2;

    /** 計測値種別(CPU時間) */
    int TYPE_CPUTIME = 3;

    /** 計測値種別(ヒープメモリコミット量) */
    int TYPE_HEAPMEMORY_COMMITTED = 4;

    /** 計測値種別(ヒープメモリ使用量) */
    int TYPE_HEAPMEMORY_USED = 5;

    /** 計測値種別(ヒープメモリ最大) */
    int TYPE_HEAPMEMORY_MAX = 6;

    /** 計測値種別(Java稼動時間) */
    int TYPE_JAVAUPTIME = 7;

    /** 計測値種別(ヒープ以外のメモリコミット量) */
    int TYPE_NONHEAPMEMORY_COMMITTED = 8;

    /** 計測値種別(ヒープ以外のメモリ使用量) */
    int TYPE_NONHEAPMEMORY_USED = 9;

    /** 計測値種別(ヒープ以外のメモリ最大) */
    int TYPE_NONHEAPMEMORY_MAX = 10;

    /** 計測値種別(物理メモリ容量) */
    int TYPE_PHYSICALMEMORY_CAPACITY = 11;

    /** 計測値種別(物理メモリ空き容量) */
    int TYPE_PHYSICALMEMORY_FREE = 12;

    /** 計測値種別(プロセッサ数) */
    int TYPE_PROCESSORCOUNT = 13;

    /** 計測値種別(スワップ領域容量) */
    int TYPE_SWAPSPACE_CAPACITY = 14;

    /** 計測値種別(スワップ領域空き容量) */
    int TYPE_SWAPSPACE_FREE = 15;

    /** 計測値種別(仮想マシンメモリ容量) */
    int TYPE_VIRTUALMACHINEMEMORY_CAPACITY = 16;

    /** 計測値種別(仮想マシンメモリ空き容量) */
    int TYPE_VIRTUALMACHINEMEMORY_FREE = 17;

    /** 計測値種別(仮想メモリ容量) */
    int TYPE_VIRTUALMEMORY_SIZE = 18;

    /** 計測値種別(ネットワークデータ受信量) */
    int TYPE_NETWORK_INPUTSIZEOFPROCESS = 19;

    /** 計測値種別(ネットワークデータ送信量) */
    int TYPE_NETWORK_OUTPUTSIZEOFPROCESS = 20;

    /** 計測値種別(ファイル入力量) */
    int TYPE_FILE_INPUTSIZEOFPROCESS = 21;

    /** 計測値種別(ファイル出力量) */
    int TYPE_FILE_OUTPUTSIZEOFPROCESS = 22;

    /** 計測値種別(スレッド数) */
    int TYPE_THREADCOUNT = 23;

    /** 計測値種別(GCトータル時間) */
    int TYPE_GARBAGETOTALTIME = 24;

    /** 計測値種別(リストコレクションの数) */
    int TYPE_LISTCOUNT = 25;

    /** 計測値種別(Queueコレクションの数) */
    int TYPE_QUEUECOUNT = 26;

    /** 計測値種別(Setコレクションの数) */
    int TYPE_SETCOUNT = 27;

    /** 計測値種別(Mapコレクションの数) */
    int TYPE_MAPCOUNT = 28;

    /** 計測値種別(クラスヒストグラムから取得したオブジェクトのサイズ) */
    int TYPE_CLASSHISTOGRAM_SIZE = 29;

    /** 計測値種別(クラスヒストグラムから取得したオブジェクトの数) */
    int TYPE_CLASSHISTOGRAM_COUNT = 30;

    /** 計測値種別(Turn Around Time:平均値) */
    int TYPE_TURNAROUNDTIME = 31;

    /** 計測値種別(Turn Around Time呼び出し回数) */
    int TYPE_TURNAROUNDTIMECOUNT = 32;

    /** 計測値種別(プールの最大数、稼働数) */
    int TYPE_POOLSIZE = 33;

    /** 計測値種別(Finalizeに登録されているオブジェクト数) */
    int TYPE_FINALIZATIONCOUNT = 34;

    /** 計測値種別(HTTPセッションのインスタンス数) */
    int TYPE_HTTPSESSION_NUMBER = 35;

    /** 計測値種別(HTTPセッションのオブジェクト登録数) */
    int TYPE_HTTPSESSION_TOTALSIZE = 36;

    /** 計測値種別(ワーカスレッドの最大数、稼動数) */
    int TYPE_SERVERPOOL = 37;

    /** 計測値種別(Turn Around Time:最大値) */
    int TYPE_TURNAROUNDTIMEMAX = 38;

    /** 計測値種別(Turn Around Time:最小値) */
    int TYPE_TURNAROUNDTIMEMIN = 39;

    /** 計測値種別(CallTreeNode数) */
    int TYPE_CALLTREENODE_COUNT = 40;

    /** 計測値種別(JavelinConverterで変換したメソッド数) */
    int TYPE_CONVERTEDMOTHOD_COUNT = 41;

    /** 計測値種別(JavelinConverterで変換したメソッド数) */
    int TYPE_EXCLUDEDMOTHOD_COUNT = 42;

    /** 計測値種別(Java 仮想マシンが実行を開始してからロードされたクラスの合計数) */
    int TYPE_TOTAL_LOADEDCLASSCOUNT = 43;

    /** 計測値種別(Java 仮想マシンに現在ロードされているクラスの数) */
    int TYPE_LOADEDCLASSCOUNT = 44;

    /** 計測値種別(JavelinConverterで変換を行ったメソッドのうち、呼び出されたメソッド数) */
    int TYPE_CALLEDMETHODCOUNT = 45;

    /** 計測値種別(例外発生回数) */
    int TYPE_THROWABLECOUNT = 46;

    /** 計測値種別(HTTPエラー発生回数) */
    int TYPE_HTTP_ERRCOUNT = 47;

    /** 計測値種別(イベント種別毎のイベント発生回数) */
    int TYPE_EVENT_COUNT = 48;

    /** 計測値種別(システム全体のメモリ最大値) */
    int TYPE_SYS_PHYSICALMEM_MAX = 49;

    /** 計測値種別(システム全体の空きメモリ) */
    int TYPE_SYS_PHYSICALMEM_FREE = 50;

    /** 計測値種別(システムのユーザモードのCPU使用量) */
    int TYPE_SYS_CPUTIME_TOTAL = 51;

    /** 計測値種別(システムのシステムモードでのCPU使用量) */
    int TYPE_SYS_CPUTIME_SYS = 52;

    /** 計測値種別(CPUごとの負荷) */
    int TYPE_SYSTEM_CPUARRAY = 53;

    /** 計測値種別(システム全体のページイン) */
    int TYPE_SYS_PAGE_IN = 54;

    /** 計測値種別(システム全体のページアウト) */
    int TYPE_SYS_PAGE_OUT = 55;

    /** 計測値種別(プロセス毎のCpuTime) */
    int TYPE_PROC_CPUTIME_SYS = 56;

    /** 計測値種別(プロセス毎の仮想メモリ使用量) */
    int TYPE_PROC_VIRTUALMEM_USE = 57;

    /** 計測値種別(プロセス毎の物理メモリ使用量) */
    int TYPE_PROC_PHYSICALMEM_USE = 58;

    /** 計測値種別(プロセス毎のスレッド数) */
    int TYPE_PROC_THREAD_OS = 59;

    /** 計測値種別(プロセス毎のメジャーフォールト数) */
    int TYPE_PROC_MAJFLT = 60;

    /** 計測値種別(プロセス毎のfd/ハンドル数) */
    int TYPE_SYS_FD_COUNT = 61;
    
    /** 計測値種別(プロセス毎のfd/ハンドル数) */
    int TYPE_PROC_FD_COUNT = 62;
    
    /** CPU使用率（システム）の合計 */
    int TYPE_SYS_CPU_TOTAL_USAGE = 63;
    
    /** CPU使用率（システム）の中のシステムの使用率 */
    int TYPE_SYS_CPU_SYS_USAGE = 64;
    
    /** CPU使用率（プロセス）の合計 */
    int TYPE_PROC_CPU_TOTAL_USAGE = 65;
    
    /** カバレッジ */
    int TYPE_PROC_CPU_SYS_USAGE = 66;
    
    /** CPU使用率（プロセス）の合計 */
    int TYPE_COVERAGE = 67;

    /** 計測値種別(システム全体の空きメモリ) */
    int TYPE_SYS_PHYSICALMEM_USED = 68;
    
    /** 計測値種別(レスポンス回数(sqlを除く)) */
    int TYPE_PROC_RES_TOTAL_COUNT_EXCLUSION_SQL = 69;
    
    /** 計測値種別(レスポンス回数(sqlのみ)) */
    int TYPE_PROC_RES_TOTAL_COUNT_ONLY_SQL = 70;
    
    /** 計測値種別(システム全体の空きメモリ) */
    int TYPE_PROC_RES_TIME_AVERAGE_EXCLUSION_SQL = 71;
    
    /** 計測値種別(システム全体の空きメモリ) */
    int TYPE_PROC_RES_TIME_AVERAGE_ONLY_SQL = 72;
    
    /** 計測値種別(JMXの計測値) */
    int TYPE_JMX = 73;
    
    /** CPU使用率（システム）の中のIOWAITの使用率 */
    int TYPE_SYS_CPU_IOWAIT_TIME = 75;
    
    /** CPU使用率（プロセス）の中のIOWAITの使用率 */
    int TYPE_PROC_CPU_IOWAIT_TIME = 76;
    
    /** CPU使用率（システム）の中のIOWAITの使用率 */
    int TYPE_SYS_CPU_IOWAIT_USAGE = 77;
    
    /** CPU使用率（プロセス）の中のIOWAITの使用率 */
    int TYPE_PROC_CPU_IOWAIT_USAGE = 78;

    /** 計測値種別(ファイル入力量) */
    int TYPE_FILE_INPUTSIZEOFSYSTEM = 79;

    /** 計測値種別(ファイル出力量) */
    int TYPE_FILE_OUTPUTSIZEOFSYSTEM = 80;
    
    /** 計測値種別(HTTPエラー発生回数) */
    int TYPE_HTTP_ERRPR_RESPONSE = 81;
    
    /** 計測値種別(ストール検出回数) */
    int TYPE_METHODSTALLCOUNT = 82;

    /** 計測値種別(NameNode) */
    int TYPE_HADOOP_NAMENODE = 83;
    
    /** 計測値種別(DataNode) */
    int TYPE_HADOOP_DATANODE = 84;
    
    /** 計測値種別(JobTracker) */
    int TYPE_HAOOP_JOBTRACKER = 85;
    
    /** 計測値種別(TaskTracker) */
    int TYPE_HADOOP_TASKTRACKER = 86;
    
    /** 計測値種別(HMaster) */
    int TYPE_HBASE_HMASTER = 87;
    
    /** 計測値種別(HRegionServer) */
    int TYPE_HBASE_HREGIONSERVER = 88;
}
