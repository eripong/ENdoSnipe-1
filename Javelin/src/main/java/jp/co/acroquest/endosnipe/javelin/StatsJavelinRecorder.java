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
package jp.co.acroquest.endosnipe.javelin;

import java.util.List;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.event.EventConstants;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.bean.Component;
import jp.co.acroquest.endosnipe.javelin.bean.ExcludeMonitor;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.bean.TripleState;
import jp.co.acroquest.endosnipe.javelin.communicate.AlarmListener;
import jp.co.acroquest.endosnipe.javelin.communicate.JavelinAcceptThread;
import jp.co.acroquest.endosnipe.javelin.communicate.JavelinConnectThread;
import jp.co.acroquest.endosnipe.javelin.event.CallTreeEventCreator;
import jp.co.acroquest.endosnipe.javelin.event.CommonEvent;
import jp.co.acroquest.endosnipe.javelin.event.EventRepository;
import jp.co.acroquest.endosnipe.javelin.event.InvocationFullEvent;
import jp.co.acroquest.endosnipe.javelin.event.JavelinEventCounter;
import jp.co.acroquest.endosnipe.javelin.helper.VMStatusHelper;
import jp.co.acroquest.endosnipe.javelin.log.JavelinFileGenerator;
import jp.co.acroquest.endosnipe.javelin.log.JavelinLogCallback;
import jp.co.acroquest.endosnipe.javelin.record.AllRecordStrategy;
import jp.co.acroquest.endosnipe.javelin.record.JvnFileNotifyCallback;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import jp.co.acroquest.endosnipe.javelin.util.StatsUtil;
import jp.co.acroquest.endosnipe.javelin.util.ThreadUtil;

/**
 * メソッド呼び出し情報の取得を行うクラスです。
 *
 * @author acroquest
 */
public class StatsJavelinRecorder
{
    /** 初期化判定フラグ */
    private static boolean                   initialized__;

    private static VMStatusHelper            vmStatusHelper__    = new VMStatusHelper();

    /** 記録条件判定クラス */
    private static RecordStrategy            recordStrategy__;

    /** Javelinログ出力クラス。 */
    private static JavelinFileGenerator      generator__;

    /** アラームリスナのリスト */
    private static final List<AlarmListener> ALARM_LISTENER_LIST = new ArrayList<AlarmListener>();

    /** バッファサイズのデフォルト値 */
    private static final int                 DEF_BUFFER_SIZE     = 1024;

    /** アラーム削除メッセージを保存します。 */
    private static StringBuffer              discardBuffer__     = new StringBuffer(
                                                                         DEF_BUFFER_SIZE);

    /** 前回削除を通知した時間を保存します。 */
    private static long                      lastDiscardTime__   = 0;

    /** イベントの重複をチェックするためのリポジトリ。 */
    private static EventRepository           eventRepository__   = new EventRepository();

    /** クライアントモード */
    private static final String CONNECTION_MODE_CLIENT = "client";

    /**
     * インスタンス化を阻止するプライベートコンストラクタです。<br />
     */
    private StatsJavelinRecorder()
    {
        // Do Nothing.
    }

    /**
     * 初期化処理。 AlarmListenerの登録を行う。 RecordStrategyを初期化する。
     * MBeanServerへのContainerMBeanの登録を行う。
     * 公開用HTTPポートが指定されていた場合は、HttpAdaptorの生成と登録も行う。
     * @param config パラメータの設定値を保存するオブジェクト
     */
    public static void javelinInit(final JavelinConfig config)
    {
        if (initialized__ == true)
        {
            return;
        }
        try
        {
            // エラーロガーを初期化する。
            SystemLogger.initSystemLog(config);

            generator__ = new JavelinFileGenerator(config);

            // AlarmListenerを登録する
            registerAlarmListeners(config);

            // RecordStrategyを初期化する
            String strategyName = config.getRecordStrategy();
            try
            {
                recordStrategy__ = (RecordStrategy)loadClass(strategyName).newInstance();
            }
            catch (ClassNotFoundException cfne)
            {
                String defaultRecordstrategy = JavelinConfig.DEFAULT_RECORDSTRATEGY;
                SystemLogger.getInstance().info(
                        "Failed to load " + strategyName
                                + ". Use default value "
                                + defaultRecordstrategy
                                + " as javelin.recordStrategy.");
                recordStrategy__ = (RecordStrategy) loadClass(
                        defaultRecordstrategy).newInstance();
            }

            // スレッドの監視を開始する。
            vmStatusHelper__.init();

            // クライアントモードの場合のみ、TCPでの接続を開始する。
            // クライアントモードでない場合、TCPでの接続受付を開始する。
            if (CONNECTION_MODE_CLIENT.equals(config.getConnectionMode()))
            {
                JavelinConnectThread.getInstance().connect();
            }
            else
            {
                // TCPでの接続受付を開始する。
                JavelinAcceptThread.getInstance().start();
            }

            initialized__ = true;

        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }

    /**
     * AlarmListenerのクラスをJavelin設定から読み込み、登録する。<br />
     * クラスのロードは、以下の順でクラスローダでのロードを試みる。
     *
     * <ol>
     * <li>StatsJavelinRecorderをロードしたクラスローダ</li>
     * <li>コンテキストクラスローダ</li>
     * </ol>
     *
     * @param config パラメータの設定値を保存するオブジェクト
     */
    private static void registerAlarmListeners(final JavelinConfig config)
    {
        String[] alarmListeners = config.getAlarmListeners().split(",");
        for (String alarmListenerName : alarmListeners)
        {
            try
            {
                if ("".equals(alarmListenerName))
                {
                    continue;
                }

                Class<?> alarmListenerClass = loadClass(alarmListenerName);
                Object listener = alarmListenerClass.newInstance();
                if (listener instanceof AlarmListener)
                {
                    AlarmListener alarmListener = (AlarmListener)listener;
                    alarmListener.init();
                    addListener(alarmListener);
                    String message = "Register " + alarmListenerName + " for AlarmListener.";
                    SystemLogger.getInstance().debug(message);
                }
                else
                {
                    String message = alarmListenerName
                            + " is not used for sending alarms because it doesn't implement AlarmListener.";
                    SystemLogger.getInstance().info(message);
                }
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(
                        alarmListenerName
                                + " is not used for sending alarms because of failing to be registered.",
                        ex);
            }
        }
    }

    /**
     * クラスをロードする。 以下の順でクラスローダでのロードを試みる。
     * <ol>
     * <li>StatsJavelinRecorderをロードしたクラスローダ</li>
     * <li>コンテキストクラスローダ</li>
     * </ol>
     *
     * @param className ロードするクラスの名前。
     * @return ロードしたクラス。
     * @throws ClassNotFoundException 全てのクラスローダでクラスが見つからない場合
     */
    private static Class<?> loadClass(final String className)
        throws ClassNotFoundException
    {
        Class<?> clazz;
        try
        {
            clazz = Class.forName(className);
        }
        catch (ClassNotFoundException cnfe)
        {
            SystemLogger.getInstance().info(
                    "Load classes from context class loader because of failing to load "
                            + className + ".");
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        return clazz;
    }

    /**
     * JavelinRecorder, JDBCJavelinRecorderから呼び出したときの前処理。
     * @param className クラス名
     * @param methodName メソッド名
     * @param config パラメータの設定値を保存するオブジェクト
     * @param args 引数
     * @param doExcludeProcess 除外対象処理を行うかどうか
     */
    public static void preProcess(final String className, final String methodName,
            final Object[] args, final JavelinConfig config, final boolean doExcludeProcess)
    {
        StackTraceElement[] stacktrace = null;
        if (config.isLogStacktrace())
        {
            stacktrace = ThreadUtil.getCurrentStackTrace();
        }
        String normalizedMethodName = methodName;
        if (methodName.length() > config.getInvocationNameLimitLength())
        {
            normalizedMethodName = methodName.substring(0, config.getInvocationNameLimitLength());
        }
        
        preProcess(null, null, className, normalizedMethodName, args, stacktrace, config, doExcludeProcess);
    }

    /**
     * JavelinRecorder, JDBCJavelinRecorderから呼び出したときの前処理。
     * @param component クラス名
     * @param invocation メソッド名
     * @param config パラメータの設定値を保存するオブジェクト
     * @param args 引数
     * @param doExcludeProcess 除外対象処理を行うかどうか
     */
    public static void preProcess(final Component component, final Invocation invocation,
            final Object[] args, final JavelinConfig config, final boolean doExcludeProcess)
    {
        StackTraceElement[] stacktrace = null;
        if (config.isLogStacktrace())
        {
            stacktrace = ThreadUtil.getCurrentStackTrace();
        }
        preProcess(component, invocation, component.getClassName(), invocation.getMethodName(),
                   args, stacktrace, config, doExcludeProcess);
    }

    /**
     * 前処理。
     * @param className  クラス名
     * @param methodName メソッド名
     * @param args 引数
     * @param stacktrace スタックトレース
     * @param config パラメータの設定値を保存するオブジェクト
     */
    public static void preProcess(final String className, final String methodName,
            final Object[] args, final StackTraceElement[] stacktrace, final JavelinConfig config)
    {
        preProcess(null, null, className, methodName, args, stacktrace, config, true);
    }

    /**
     * 前処理。
     * @param className  クラス名
     * @param methodName メソッド名
     * @param args 引数
     * @param stacktrace スタックトレース
     * @param config パラメータの設定値を保存するオブジェクト
     * @param doExcludeProcess 除外対象処理を行うかどうか
     */
    public static void preProcess(final String className, final String methodName,
            final Object[] args, final StackTraceElement[] stacktrace, final JavelinConfig config,
            final boolean doExcludeProcess)
    {
        String normalizedMethodName = methodName;
        if (methodName.length() > config.getInvocationNameLimitLength())
        {
            normalizedMethodName = methodName.substring(0, config.getInvocationNameLimitLength());
        }
        
        preProcess(null, null, className, normalizedMethodName, args, stacktrace, config, doExcludeProcess,
                   false);
    }

    /**
     * 前処理。
     * @param className  クラス名
     * @param methodName メソッド名
     * @param args 引数
     * @param stacktrace スタックトレース
     * @param config パラメータの設定値を保存するオブジェクト
     * @param doExcludeProcess 除外対象処理を行うかどうか
     */
    public static void preProcess(Component component, Invocation invocation,
            final String className, final String methodName, final Object[] args,
            final StackTraceElement[] stacktrace, final JavelinConfig config,
            final boolean doExcludeProcess)
    {
        preProcess(component, invocation, className, methodName, args, stacktrace, config,
                   doExcludeProcess, false);
    }

    /**
     * 前処理。
     * @param className  クラス名
     * @param methodName メソッド名
     * @param args 引数
     * @param stacktrace スタックトレース
     * @param config パラメータの設定値を保存するオブジェクト
     * @param doExcludeProcess 除外対象処理を行うかどうか
     * @param isResponse デフォルトでレスポンスを記録するかどうか。
     */
    public static void preProcess(final String className, final String methodName,
            final Object[] args, final StackTraceElement[] stacktrace, final JavelinConfig config,
            final boolean doExcludeProcess, final boolean isResponse)
    {
        String normalizedMethodName = methodName;
        if (methodName.length() > config.getInvocationNameLimitLength())
        {
            normalizedMethodName = methodName.substring(0, config.getInvocationNameLimitLength());
        }
   
        preProcess(null, null, className, normalizedMethodName, args, stacktrace, config, doExcludeProcess,
                   isResponse);
    }

    /**
     * 前処理。
     * @param component コンポーネント
     * @param invocation Invocation
     * @param className  クラス名
     * @param methodName メソッド名
     * @param args 引数
     * @param stacktrace スタックトレース
     * @param config パラメータの設定値を保存するオブジェクト
     * @param doExcludeProcess 除外対象処理を行うかどうか
     * @param isResponse デフォルトでレスポンスを記録するかどうか。
     */
    public static void preProcess(Component component, Invocation invocation,
            final String className, final String methodName, final Object[] args,
            final StackTraceElement[] stacktrace, final JavelinConfig config,
            final boolean doExcludeProcess, final boolean isResponse)
    {
        synchronized (StatsJavelinRecorder.class)
        {
            // 初期化処理
            if (initialized__ == false)
            {
                javelinInit(config);
            }
        }
        CallTreeRecorder callTreeRecorder = CallTreeRecorder.getInstance();

        // Javelinのログ出力処理が呼び出されている場合、処理を行わない
        if (callTreeRecorder.isRecordMethodCalled_)
        {
            return;
        }

        // Javelinのログ出力処理呼び出しステータスをセット
        callTreeRecorder.isRecordMethodCalled_ = true;

        try
        {
            boolean isRecorded =
                                 recordPreInvocation(component, invocation, className, methodName,
                                                     args, stacktrace, config, doExcludeProcess,
                                                     isResponse, callTreeRecorder);

            if (isRecorded)
            {
                // 有効だったnodeの深さを保存する。
                callTreeRecorder.getCallTree().addDepth(callTreeRecorder.getDepth());
            }
        }
        finally
        {
            // Javelinのログ出力処理呼び出しステータスを解除
            callTreeRecorder.isRecordMethodCalled_ = false;
            callTreeRecorder.setDepth(callTreeRecorder.getDepth() + 1);
        }
    }

    /**
     * 前処理。
     * @param component コンポーネント
     * @param invocation Invocation
     * @param className  クラス名
     * @param methodName メソッド名
     * @param args 引数
     * @param stacktrace スタックトレース
     * @param config パラメータの設定値を保存するオブジェクト
     * @param doExcludeProcess 除外対象処理を行うかどうか
     * @param isResponse デフォルトでレスポンスを記録するかどうか。
     * @param callTreeRecorder コールツリー
     *
     * @return 記録したかどうか
     */
    private static boolean recordPreInvocation(Component component, Invocation invocation,
            final String className, final String methodName, final Object[] args,
            final StackTraceElement[] stacktrace, final JavelinConfig config,
            final boolean doExcludeProcess, final boolean isResponse,
            CallTreeRecorder callTreeRecorder)
    {
        if (component == null)
        {
            component = MBeanManager.getComponent(className);
        }
        if (invocation == null)
        {
            invocation = getInvocation(component, methodName);
        }

        boolean isExclude = ExcludeMonitor.isExclude(invocation);
        if (doExcludeProcess == true && isExclude == true)
        {
            return false;
        }

        CallTree callTree = callTreeRecorder.getCallTree();
        CallTreeNode newNode = null;
        CallTreeNode parent = callTreeRecorder.getCallTreeNode();
        if (parent == null)
        {
            // ルート呼び出し時に、例外発生フラグをクリアする
            callTreeRecorder.isExceptionOccured_ = false;
            if (invocation == null)
            {
                invocation =
                             registerInvocation(component, className, methodName, config,
                                                isResponse);
            }

            // 一度でもルートから呼ばれたことのあるメソッドを保存する。
            ExcludeMonitor.addTargetPreferred(invocation);
            ExcludeMonitor.removeExcludePreferred(invocation);

            // 最初の呼び出しなので、CallTreeを初期化しておく。
            initCallTree(callTree, methodName, config, callTreeRecorder);
            newNode = CallTreeRecorder.createNode(invocation, args, stacktrace, config);
            newNode.setDepth(0);
            newNode.setTree(callTree);
            callTree.setRootNode(newNode);
            callTreeRecorder.setDepth(0);
        }
        else
        {
            // CallTreeNodeが多い場合はイベントを送信する。
            boolean isCallTreeFull =
                sendCallTreeEvent(callTree, className, methodName, config,
                                                       callTreeRecorder);

            if (invocation == null)
            {
                // 一度も呼び出されていない場合は、記録する。
                invocation =
                             registerInvocation(component, className, methodName, config,
                                                isResponse);
            }
            else if (doExcludeProcess == true)
            {
                // 除外対象モニタから呼ばれるメソッドに対して、
                // ルートから呼ばれたことがない、かつ　除外対象リストにあるメソッドを除外する。
                boolean isTargetPreferred = ExcludeMonitor.isTargetPreferred(invocation);
                if (isTargetPreferred == false)
                {
                    boolean isExcludePreferred = ExcludeMonitor.isExcludePreffered(invocation);
                    if (isExcludePreferred == true)
                    {
                        return false;
                    }
                }
            }
        }

        try
        {
            try
            {
                if (newNode == null)
                {
                    newNode = CallTreeRecorder.createNode(invocation, args, stacktrace, config);
                    newNode.setDepth(callTreeRecorder.getDepth());
                }

                CallTreeRecorder.addCallTreeNode(parent, callTree, newNode, config);
                VMStatus vmStatus = createVMStatus(parent, newNode, config, callTreeRecorder);
                newNode.setStartTime(System.currentTimeMillis());
                newNode.setStartVmStatus(vmStatus);

                callTreeRecorder.setCallerNode(newNode);
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(ex);
            }

            boolean isTarget = ExcludeMonitor.isTarget(invocation);
            if (isTarget == false)
            {
                judgeExclude(doExcludeProcess, invocation);
            }
            else
            {
                boolean isTargetPreferred = ExcludeMonitor.isTargetPreferred(invocation);
                if (isTargetPreferred == false)
                {
                    judgeExclude(doExcludeProcess, invocation);
                }
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
            return false;
        }

        return true;
    }

    private static void judgeExclude(final boolean doExcludeProcess, Invocation invocation)
    {
        if (doExcludeProcess == true)
        {
            judgeExclude(invocation);
        }
        else
        {
            ExcludeMonitor.removeExcludePreferred(invocation);
        }
    }

    private static void judgeExclude(Invocation invocation)
    {
        ExcludeMonitor.judgeExclude(invocation);
    }

    /**
     * 指定したノードparentの子ノードの情報をjvnログに出力し、Invocationに反映する。
     *
     * @param tree コールツリー。
     * @param finishedNodeList ノード。
     * @param callTreeRecorder callTreeRecorder
     */
    private static void recordChildNodes(CallTree tree, List<CallTreeNode> finishedNodeList,
            CallTreeRecorder callTreeRecorder, JavelinConfig config)
    {
        CallTreeNode dummyParent = createDummyNode(finishedNodeList);
        List<CallTreeNode> children = dummyParent.getChildren();

        if (children.size() == 0)
        {
            return;
        }

        generator__.generateJaveinFile(tree, dummyParent, new JvnFileNotifyCallback(),
                                       children.get(children.size() - 1), 0);

        // 子ノードの情報を記録する。
        for (CallTreeNode child : children)
        {
            recordTransaction(child);
        }

        // 子ノードをクリアする。
        callTreeRecorder.clearChildren();
    }

    /**
     * 計測対象の分割表示のために、ダミーのCallTreeNodeを生成する。
     *
     * @param finishedNodeList 元となるCallTreeNode
     * @return ダミーのCallTreeNode。
     */
    private static CallTreeNode createDummyNode(List<CallTreeNode> finishedNodeList)
    {
        CallTreeNode dummyNode = new CallTreeNode();
        dummyNode.setChildren(new java.util.ArrayList<CallTreeNode>());
        dummyNode.getChildren().addAll(finishedNodeList);
        return dummyNode;
    }

    /**
     * コールツリーを初期化します。
     *
     * @param callTree callTree
     * @param methodName メソッド名
     * @param config 設定
     * @param callTreeRecorder callTreeRecorder
     */
    private static void initCallTree(CallTree callTree, final String methodName,
            final JavelinConfig config, CallTreeRecorder callTreeRecorder)
    {
        // 初回呼び出し時はコールツリーを初期化する。
        callTree.clearDepth();

        callTree.setRootCallerName(config.getRootCallerName());
        callTree.setEndCalleeName(config.getEndCalleeName());

        String threadId = createThreadId(methodName, config, callTreeRecorder);
        if (threadId != null)
        {
            callTree.setThreadID(threadId);
        }
    }

    private static VMStatus createVMStatus(CallTreeNode parent, CallTreeNode newNode,
            final JavelinConfig config, CallTreeRecorder callTreeRecorder)
    {
        VMStatus vmStatus;

        if (parent == null && config.isLogMBeanInfoRoot())
        {
            vmStatus = vmStatusHelper__.createVMStatus(callTreeRecorder);
        }
        else if (config.isLogMBeanInfo())
        {
            vmStatus = vmStatusHelper__.createVMStatus(callTreeRecorder);
        }
        else
        {
            vmStatus = VMStatus.EMPTY_STATUS;
        }

        return vmStatus;
    }

    /**
     * CallTreeNodeの数が閾値を超えている場合に、イベントを送信します。
     *
     * @param callTree コールツリー。
     * @param className クラス名。
     * @param methodName メソッド名。
     * @param config 設定。
     * @param callTreeRecorder callTreeRecorder
     */
    private static boolean sendCallTreeEvent(
            CallTree callTree,
            final String className,
            final String methodName,
            final JavelinConfig config,
            CallTreeRecorder callTreeRecorder)
    {
        boolean isCallTreeFull = false;

        if (CallTreeRecorder.isCallTreeFull(callTree, config))
        {
            if (callTree.getFlag(EventConstants.NAME_CALLTREE_FULL) == null)
            {
                // 完了したCallTreeNodeをファイルに書き出す。
                CommonEvent event;
                event = CallTreeEventCreator.createTreeFullEvent(className,
                                                                 methodName,
                                                                 config.getCallTreeMax());
                addEvent(event);
                callTree.setFlag(EventConstants.NAME_CALLTREE_FULL, "");
            }

            if (config.isCallTreeAll())
            {
                List<CallTreeNode> finishedNodeList = callTreeRecorder.removeFinishedNode();
                recordChildNodes(callTree, finishedNodeList, callTreeRecorder, config);
            }
            else
            {
                isCallTreeFull = true;
            }
        }

        return isCallTreeFull;
    }

    /**
     * スレッドIDを生成する。
     *
     * @param methodName メソッド名。
     * @param config 設定。
     * @return スレッドID。
     */
    private static String createThreadId(final String methodName, final JavelinConfig config,
            final CallTreeRecorder callTreeRecorder)
    {
        String threadId = null;
        switch (config.getThreadModel())
        {
        case JavelinConfig.TM_THREAD_ID:
            threadId = "" + callTreeRecorder.getThreadId();
            break;
        case JavelinConfig.TM_THREAD_NAME:
            threadId = Thread.currentThread().getName();
            break;
        case JavelinConfig.TM_CONTEXT_PATH:
            threadId = methodName;
            break;
        default:
            break;
        }
        return threadId;
    }

    /**
     * Invocationを取得する。
     *
     * @param component クラス名
     * @param methodName メソッド名
     * @return Invocation
     */
    public static Invocation getInvocation(final Component component, final String methodName)
    {
        if (component == null)
        {
            return null;
        }

        Invocation invocation = component.getInvocation(methodName);
        return invocation;
    }

    /**
     * Invocationを登録する。
     *
     * @param component コンポーネント
     * @param className クラス名
     * @param methodName メソッド名
     * @param config 設定
     * @param isResponse レスポンス
     * @return 登録したInvocation
     */
    public static Invocation registerInvocation(Component component, final String className,
            final String methodName, final JavelinConfig config, boolean isResponse)
    {
        if (component == null)
        {
            try
            {
                component = new Component(className);

                MBeanManager.setComponent(className, component);
            }
            catch (NullPointerException ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }

        Invocation invocation = component.getInvocation(methodName);

        if (invocation == null)
        {
            invocation = registerInvocation(component, methodName, config, isResponse);
        }
        return invocation;
    }

    /**
     * Invocation をコンポーネントに登録します。
     *
     * @param component 登録対象コンポーネント
     * @param methodName 登録するメソッド
     * @param config 設定
     * @param isResponse レスポンスグラフに表示する場合は <code>true</code>
     * @return 登録した Invocation
     */
    public static Invocation registerInvocation(final Component component, final String methodName,
            final JavelinConfig config, final boolean isResponse)
    {
        String className = component.getClassName();
        Invocation invocation;
        int recordedInvocationNum = component.getRecordedInvocationNum();

        String processName = VMStatusHelper.getProcessName();
        invocation =
                     new Invocation(processName, className, methodName,
                                    Invocation.THRESHOLD_NOT_SPECIFIED);

        // Invocationの数が最大値に達しており、かつInvocationFullEventを送信する設定の場合、
        // InvocationFullEvent送信の処理を行う。
        if (config.getSendInvocationFullEvent() == true
                && recordedInvocationNum >= config.getRecordInvocationMax())
        {
            Invocation removedInvoction = component.addAndDeleteOldestInvocation(invocation);
            sendInvocationFullEvent(component, className, recordedInvocationNum, invocation,
                                    removedInvoction);
        }
        else
        {
            component.addInvocation(invocation);
        }

        if (isResponse)
        {
            invocation.setResponseGraphOutput(TripleState.ON);
            RootInvocationManager.addRootInvocation(invocation);
        }
        return invocation;
    }

    /**
     * InvocationFullEventを送信する。
     *
     * @param component コンポーネント。
     * @param className クラス名
     * @param invocationNum Invocationの数
     */
    private static void sendInvocationFullEvent(Component component, String className,
            int invocationNum, Invocation addInvocation, Invocation removedInvocation)
    {
        CommonEvent event = new InvocationFullEvent();
        event.addParam(EventConstants.PARAM_INVOCATION, String.valueOf(invocationNum));
        event.addParam(EventConstants.PARAM_INVOCATION_CLASS, className);
        boolean containsEvent = eventRepository__.containsEvent(event);
        if (containsEvent == false)
        {
            if (addInvocation != null)
            {
                event.addParam(EventConstants.PARAM_INVOCATION_METHOD_ADD,
                               addInvocation.getMethodName());
            }
            else
            {
                event.addParam(EventConstants.PARAM_INVOCATION_METHOD_ADD, "");
            }

            if (removedInvocation != null)
            {
                event.addParam(EventConstants.PARAM_INVOCATION_METHOD_REMOVE,
                               removedInvocation.getMethodName());
            }
            else
            {
                event.addParam(EventConstants.PARAM_INVOCATION_METHOD_REMOVE, "");
            }

            String stackTrace = ThreadUtil.getStackTrace(ThreadUtil.getCurrentStackTrace());
            event.addParam(EventConstants.PARAM_INVOCATION_STACKTRACE, stackTrace);
            addEvent(event);
        }
    }

    /**
     * 後処理（本処理成功時）。
     * @param className クラス名
     * @param methodName メソッド名
     * @param returnValue 戻り値
     * @param config パラメータの設定値を保存するオブジェクト
     */
    public static void postProcess(String className, String methodName, final Object returnValue,
            final JavelinConfig config)
    {
        postProcess(className, methodName, returnValue, config, true);
    }

    /**
    * 後処理（本処理成功時）。<br />
    *
    * @param className クラス名
    * @param methodName メソッド名
    * @param returnValue 戻り値
    * @param config パラメータの設定値を保存するオブジェクト
    * @param doExcludeProcess 除外対象処理を行うかどうか
    */
    public static void postProcess(String className, String methodName, final Object returnValue,
            final JavelinConfig config, boolean doExcludeProcess)
    {
        postProcessCommon(returnValue, null, config);
    }

    /**
    * 後処理（本処理成功時）。<br />
    *
    * @param className クラス名
    * @param methodName メソッド名
    * @param returnValue 戻り値
    * @param config パラメータの設定値を保存するオブジェクト
    * @param doExcludeProcess 除外対象処理を行うかどうか
    * @param telegramId 電文 ID
    */
    public static void postProcess(String className, String methodName, final Object returnValue,
            final JavelinConfig config, boolean doExcludeProcess, final long telegramId)
    {
        postProcessCommon(returnValue, null, config, telegramId);
    }

    /**
     * 後処理の共通処理。<br />
     *
     *　CallTree に情報を格納します。
     * また、必要に応じて Javelin ログ出力とアラーム通知を行います。<br />
     *
     * @param returnValue 戻り値（ <code>null</code> も可）
     * @param cause 例外発生オブジェクト（ <code>null</code> も可）
     * @param config パラメータの設定値を保存するオブジェクト
     */
    private static void postProcessCommon(final Object returnValue, final Throwable cause,
            final JavelinConfig config)
    {
        postProcessCommon(returnValue, cause, config, 0);
    }

    /**
     * 後処理の共通処理。<br />
     *
     *　CallTree に情報を格納します。
     * また、必要に応じて Javelin ログ出力とアラーム通知を行います。<br />
     *
     * @param returnValue 戻り値（ <code>null</code> も可）
     * @param cause 例外発生オブジェクト（ <code>null</code> も可）
     * @param config パラメータの設定値を保存するオブジェクト
    * @param telegramId 電文 ID
     */
    private static void postProcessCommon(final Object returnValue, final Throwable cause,
            final JavelinConfig config, final long telegramId)
    {
        CallTreeRecorder callTreeRecorder = CallTreeRecorder.getInstance();

        // Javelinのログ出力処理が呼び出されている場合、処理を行わない
        if (callTreeRecorder.isRecordMethodCalled_)
        {
            return;
        }

        // Javelinのログ出力処理呼び出しステータスをセット
        callTreeRecorder.isRecordMethodCalled_ = true;

        try
        {
            Integer depth = callTreeRecorder.getDepth() - 1;
            callTreeRecorder.setDepth(depth);
            CallTree callTree = callTreeRecorder.getCallTree();
            if (callTree.containsDepth(depth) == false)
            {
                return;
            }
            callTree.removeDepth(depth);

            recordPostInvocation(returnValue, cause, config, callTreeRecorder, telegramId);
        }
        finally
        {
            // Javelinのログ出力処理呼び出しステータスを解除
            callTreeRecorder.isRecordMethodCalled_ = false;
        }
    }

    /**
     * 後処理の共通処理。<br />
     *
     *　CallTree に情報を格納します。
     * また、必要に応じて Javelin ログ出力とアラーム通知を行います。<br />
     *
     * @param returnValue 戻り値（ <code>null</code> も可）
     * @param cause 例外発生オブジェクト（ <code>null</code> も可）
     * @param config パラメータの設定値を保存するオブジェクト
     * @param callTreeRecorder コールツリーレコーダ
     * @param telegramId 電文 ID
     */
    private static boolean recordPostInvocation(final Object returnValue, final Throwable cause,
            final JavelinConfig config, CallTreeRecorder callTreeRecorder, long telegramId)
    {
        CallTree callTree = callTreeRecorder.getCallTree();

        try
        {
            // アラーム通知処理、イベント出力処理を行う。
            recordAndAlarmEvents(callTree, callTreeRecorder, telegramId);

            // 呼び出し元情報取得。
            CallTreeNode node = callTreeRecorder.getCallTreeNode();
            if (node == null)
            {
                // 呼び出し元情報が取得できない場合は処理をキャンセルする。
                // (下位レイヤで例外が発生した場合のため。)
                return false;
            }

            node.setEndTime(System.currentTimeMillis());

            CallTreeNode parent = node.getParent();
            addEndVMStatus(node, parent, config, callTreeRecorder);

            Invocation invocation = node.getInvocation();

            if (cause != null && callTree.getCause() != cause)
            {
                callTreeRecorder.isExceptionOccured_ = true;

                invocation.addThrowable(cause);
                callTree.setCause(cause);

                if (config.isAlarmException())
                {
                    // 発生した例外を記録しておく
                    node.setThrowable(cause);
                    node.setThrowTime(System.currentTimeMillis());
                }
            }

            if (returnValue != null && config.isLogReturn())
            {
                // 戻り値を取得する
                String returnString = getReturnValueString(returnValue, config);
                node.setReturnValue(returnString);
            }

            if (parent != null)
            {
                parent.addChildrenTime(node.getAccumulatedTime());
                parent.addChildrenCpuTime(node.getCpuTime());
                parent.addChildrenUserTime(node.getUserTime());

                callTreeRecorder.setCallerNode(parent);
                boolean isTarget = ExcludeMonitor.isTarget(invocation);
                boolean isTargetPreferred = ExcludeMonitor.isTargetPreferred(invocation);
                if (isTarget == false || isTargetPreferred == false)
                {
                    judgeExclude(invocation);
                }

                recordTransaction(node);

                // CallTree無効の場合またはノード数が上限に達している場合は、
                // 処理が完了した子ノードはTreeから削除する。
                if (callTree.isCallTreeEnabled() == false
                        || CallTreeRecorder.isCallTreeFull(callTree, config))
                {
                    parent.removeChild(node);
                }

            }

            if (parent == null
                || invocation.getAlarmThreshold()    != Invocation.THRESHOLD_NOT_SPECIFIED
                || invocation.getAlarmCpuThreshold() != Invocation.THRESHOLD_NOT_SPECIFIED)
            {
                // 以下、CallTreeNodeがrootの場合、または閾値が個別に指定されている場合の処理。
                // CallTreeNodeがrootで、統計値記録の閾値を超えていた場合に、トランザクションを記録する。
                if (parent == null && node.getAccumulatedTime() >= config.getStatisticsThreshold())
                {
                    recordTransaction(node);
                }

                try
                {
                    // 必要に応じて、Javelinログへの出力、アラーム通知処理を行う
                    recordAndAlarmProcedure(config, callTree, node, callTreeRecorder, telegramId);
                }
                finally
                {
                    if (parent == null)
                    {
                        // ルートノードの場合
                        postProcessOnRootNode(callTree, node, callTreeRecorder);
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            SystemLogger.getInstance().warn(ex);
            return false;
        }
        return true;
    }

    /**
     * ルートノードの場合の後処理を行います。<br />
     *
     * @param callTree CallTree
     * @param node CallTreeNode
     * @param callTreeRecorder callTreeRecorder
     */
    private static void postProcessOnRootNode(CallTree callTree, CallTreeNode node,
            CallTreeRecorder callTreeRecorder)
    {
        // Strategyインタフェースを利用した判定後の後処理を行う
        postJudge(callTree, node, recordStrategy__);

        callTree.executeCallback();
        callTreeRecorder.clearCallerNode();

        // CallTreeに保持されていたNode数を記録する
        int totalNodeCount = callTree.getTotalNodeCount();
        CallTreeNodeMonitor.add(totalNodeCount);
        callTreeRecorder.clearCallTree();
    }

    /**
     * イベントが発生している場合に、 Javelin ログへの出力とアラーム通知を行います。<br />
     *
     * @param callTree CallTree
     * @param callTreeRecorder callTreeRecorder
     * @param telegramId 電文 ID
     */
    private static void recordAndAlarmEvents(CallTree callTree,
            CallTreeRecorder callTreeRecorder, long telegramId)
    {
        // CallTreeに対してEventNodeが存在しない状況でEventが発生した場合のみ、
        //  getEventNodeListに要素が追加されている。
        List<CallTreeNode> eventList = callTree.getEventNodeList();
        int size = eventList.size();
        if (size != 0)
        {
            // イベント処理が存在する場合、ログ出力処理と アラーム通知処理を行う。
            for (int num = 0; num < size; num++)
            {
                // Javelinの初期化が済んでいない場合、ログファイル作成とアラーム通知の処理を飛ばす。
                if (generator__ != null)
                {
                    generator__.generateJaveinFile(callTree, eventList.get(num),
                                                   new JvnFileNotifyCallback(), null,
                                                   telegramId);
                    sendEventAlarm();
                }

                // CallTreeに保持されていたNode数を記録する
                int totalNodeCount = callTree.getTotalNodeCount();
                CallTreeNodeMonitor.add(totalNodeCount);

            }

            if (callTreeRecorder != null)
            {
                callTreeRecorder.clearCallTree();
            }
        }
    }

    /**
     * 必要に応じて、 Javelin ログへの出力、アラーム通知処理を行います。<br />
     *
     * @param config パラメータの設定値を保存するオブジェクト
     * @param callTree CallTree
     * @param node CallTreeNode
     * @param telegramId 電文 ID
     */
    private static void recordAndAlarmProcedure(final JavelinConfig config, CallTree callTree,
            CallTreeNode node, CallTreeRecorder callTreeRecorder, final long telegramId)
    {
        // アラーム通知の有無を判定する(判定優先度：高)
        boolean judgeHighSendAlarm = judgeHighPrioritySendExceedThresholdAlarm(callTree, node);

        // アラーム通知の有無を判定する
        boolean judgeSendAlarm =
                                 judgeSendExceedThresholdAlarm(callTree, node, config,
                                                               recordStrategy__, callTreeRecorder);

        boolean isLastAlarmTooNear = false;
        if (judgeHighSendAlarm || judgeSendAlarm)
        {
            isLastAlarmTooNear = checkLastAlarmTime(node, config, judgeHighSendAlarm);
        }

        // アラームの閾値を超えていた場合に、アラームを通知する。
        if (judgeHighSendAlarm == true || (judgeSendAlarm == true && isLastAlarmTooNear == false))
        {
            callTree.addHighPriorityRecordStrategy("AllRecordStrategy", new AllRecordStrategy());

            if (node.getParent() == null)
            {
                generator__.generateJaveinFile(callTree, createCallback(
                        callTree, node), node, telegramId);
                sendAlarm(node, callTreeRecorder);
            }
        }
    }

    /**
     * メソッドの戻り値を文字列で取得します。<br />
     *
     * @param returnValue 戻り値オブジェクト
     * @param config パラメータの設定値を保存するオブジェクト
     * @return 戻り値の文字列表現
     */
    private static String getReturnValueString(final Object returnValue, final JavelinConfig config)
    {
        String returnString;
        if (config.isReturnDetail())
        {
            int returnDetailDepth = config.getReturnDetailDepth();
            returnString = StatsUtil.buildDetailString(returnValue, returnDetailDepth);
            returnString = StatsUtil.toStr(returnString, config.getStringLimitLength());
        }
        else
        {
            returnString = StatsUtil.toStr(returnValue, config.getStringLimitLength());
        }
        return returnString;
    }

    private static void addEndVMStatus(CallTreeNode node, CallTreeNode parent,
            final JavelinConfig config, CallTreeRecorder callTreeRecorder)
    {
        long duration = node.getEndTime() - node.getStartTime();
        if (duration == 0)
        {
            node.setEndVmStatus(node.getStartVmStatus());
            return;
        }

        VMStatus vmStatus = createVMStatus(parent, node, config, callTreeRecorder);
        node.setEndVmStatus(vmStatus);

        setCpuTime(node);
        setUserTime(node);
    }

    private static void setUserTime(CallTreeNode node)
    {
        long endUserTime = node.getEndVmStatus().getUserTime();
        long startUserTime = node.getStartVmStatus().getUserTime();
        long userTime = endUserTime - startUserTime;
        if (userTime < 0)
        {
            userTime = 0;
        }
        node.setUserTime(userTime);
    }

    /**
     * nodeにイベントを追加します。<br />
     * CallTreeが無い場合は、新規作成します。
     * 必ず発報します。
     *
     * @param event イベント。
     * @param config 設定。
     * @param telegramId 電文 ID
     *
     * @return 追加したNode。
     */
    public static CallTreeNode addEvent(CommonEvent event, JavelinConfig config, long telegramId)
    {
        return addEvent(event, false, config, telegramId);
    }

    /**
     * nodeにイベントを追加します。<br />
     * CallTreeが無い場合は、新規作成します。
     * 必ず発報する。
     *
     * @param event イベント。
     * @param clear 既にイベントがある場合も発報する。
     *
     * @return 追加したNode。
     */
    public static CallTreeNode addEvent(CommonEvent event, boolean clear)
    {
        return addEvent(event, clear, null, 0);
    }

    /**
     * nodeにイベントを追加します。<br />
     * CallTreeが無い場合は、新規作成します。
     * 必ず発報する。
     *
     * @param event イベント。
     * @param clear 既にイベントがある場合も発報する。
     * @param config 設定。
     * @param telegramId 電文 ID
     *
     * @return 追加したNode。
     */
    public static CallTreeNode addEvent(CommonEvent event, boolean clear, JavelinConfig config,
            long telegramId)
    {
        JavelinEventCounter.getInstance().addEvent(event);

        boolean containsEvent = eventRepository__.containsEvent(event);
        if (containsEvent && clear == false)
        {
            return null;
        }

        if (config == null)
        {
            config = new JavelinConfig();
        }

        // イベントの出力設定レベルが、引数で指定したイベントのレベルよりも大きい場合は、
        // イベントを出力しない。
        int outputEventLevel = convertEventLevel(config.getEventLevel());
        if (outputEventLevel > event.getLevel())
        {
            return null;
        }

        eventRepository__.putEvent(event);

        CallTreeRecorder callTreeRecorder = CallTreeRecorder.getInstance();
        CallTreeNode callTreeNode = callTreeRecorder.getCallTreeNode();
        CallTree tree = callTreeRecorder.getCallTree();

        // イベントのレベルがエラーの場合、即座にアラームを上げる。
        if(event.getLevel() >= CommonEvent.LEVEL_ERROR)
        {
            Invocation invocation = null;
            if( callTreeNode != null)
            {
                invocation = callTreeNode.getInvocation();
            }
            sendEventImmediately(event, config, invocation, callTreeRecorder, telegramId);
            if (tree != null)
            {
                tree.addHighPriorityRecordStrategy("AllRecordStrategy", new AllRecordStrategy());
            }

            return null;
        }

        boolean isNewCallTree = false;
        if (tree == null)
        {
            callTreeRecorder.clearCallTree();
        }
        if (callTreeNode == null)
        {
            isNewCallTree = true;

            CallTreeNode node = createEventNode(event, config, callTreeRecorder, tree);

            tree.addEventNode(node);

            tree.clearDepth();
            tree.addDepth(0);
            callTreeRecorder.setDepth(1);
        }

        CallTree callTree = callTreeRecorder.getCallTree();
        CallTreeNode rootNode = callTree.getRootNode();

        CallTreeNode node = null;
        if (rootNode != null)
        {
            tree.addHighPriorityRecordStrategy("AllRecordStrategy", new AllRecordStrategy());

            node = callTreeNode;
            event.setTime(System.currentTimeMillis());
            node.addEvent(event);
        }
        if (isNewCallTree)
        {
            postProcess(null, null, (Object)null, config, false, telegramId);
        }

        return node;
    }

    /**
     * イベント用のノードを作成する。
     *
     * @param event イベント
     * @param config 設定
     * @param callTreeRecorder CallTreeRecorder
     * @param tree ツリー
     * @return イベント用のノード。
     */
    private static CallTreeNode createEventNode(CommonEvent event, JavelinConfig config,
            CallTreeRecorder callTreeRecorder, CallTree tree)
    {
        return createEventNode(event, config, callTreeRecorder, tree, null);
    }


    /**
     * イベント用のノードを作成する。
     *
     * @param event イベント
     * @param config 設定
     * @param callTreeRecorder CallTreeRecorder
     * @param tree ツリー
     * @param invocation Invocation
     * @return イベント用のノード。
     */
    private static CallTreeNode createEventNode(CommonEvent event, JavelinConfig config,
            CallTreeRecorder callTreeRecorder, CallTree tree, Invocation invocation)
    {
        if (invocation == null)
        {
            String className = config.getRootCallerName();
            String methodName = "";

            // CallTreeにスレッド名を設定する。
            String threadId = createThreadId(methodName, config, callTreeRecorder);
            if (threadId != null)
            {
                tree.setThreadID(threadId);
            }

            String processName = VMStatusHelper.getProcessName();
            invocation = new Invocation(processName, className, methodName, 0);
        }

        event.setTime(System.currentTimeMillis());

        CallTreeNode node = new CallTreeNode();
        node.setInvocation(invocation);
        node.addEvent(event);
        return node;
    }

    /**
     * 即座にイベントを送信する。
     *
     * @param event 送信するイベント
     */
    private static void sendEventImmediately(CommonEvent event, JavelinConfig config,
            Invocation invocation, CallTreeRecorder callTreeRecorder, long telegramId)
    {
        CallTree callTree = new CallTree();
        callTree.init();
        CallTreeNode node = createEventNode(event, config, callTreeRecorder, callTree, invocation);
        callTree.addEventNode(node);
        recordAndAlarmEvents(callTree, null, telegramId);
    }

    /**
     * nodeにイベントを追加します。<br />
     * CallTreeが無い場合は、新規作成します。
     * 必ず発報する。
     *
     * @param event イベント。
     * @return 追加したCallTreeNode。
     */
    public static CallTreeNode addEvent(CommonEvent event)
    {
        return addEvent(event, null, 0);
    }

    /**
     * nodeにイベントを追加します。<br />
     * CallTreeが無い場合は、新規作成します。
     * 必ず発報します。
     *
     * @param event イベント
     * @param telegramId 電文 ID
     * @return 追加した CallTreeNode
     */
    public static CallTreeNode addEvent(CommonEvent event, long telegramId)
    {
        return addEvent(event, null, telegramId);
    }

    /**
     * CallTreeNodeに設定された判定クラス(判定優先度：高)を利用して、
     * アラームを通知するかどうか判定する。
     *
     * @param node CallTreeNode
     * @return true:通知する、false:通知しない
     */
    private static boolean judgeHighPrioritySendExceedThresholdAlarm(final CallTree callTree,
            final CallTreeNode node)
    {
        // CallTreeに設定されていた判定クラスでの判定結果が
        // 1つでもtrueであれば、それを戻り値とする
        RecordStrategy[] strategyList = callTree.getHighPriorityRecordStrategy();
        for (RecordStrategy str : strategyList)
        {
            if (str.judgeSendExceedThresholdAlarm(node))
            {
                return true;
            }
        }

        // 判定がすべてfalseの場合
        return false;
    }

    /**
     * S2JavelinConfigとCallTreeNodeに設定された判定クラスを利用して、
     * アラームを通知するかどうか判定する。
     *
     * @param tree CallTree
     * @param node CallTreeNode
     * @param config パラメータの設定値を保存するオブジェクト
     * @param strategy S2JavelinConfigに設定された判定クラス。
     * @return true:通知する、false:通知しない
     */
    private static boolean judgeSendExceedThresholdAlarm(final CallTree tree,
            final CallTreeNode node, final JavelinConfig config, final RecordStrategy strategy,
            final CallTreeRecorder callTreeRecorder)
    {
        // 例外が発生していて、例外発生時にアラーム通知する設定であれば、必ずアラーム通知を行う
        if (config.isAlarmException() && callTreeRecorder.isExceptionOccured_)
        {
            return true;
        }

        // 引数に設定されていた判定クラスでの判定結果がtrueであれば、それを戻り値とする
        if (strategy.judgeSendExceedThresholdAlarm(node))
        {
            return true;
        }

        // CallTreeNodeに設定されていた判定クラスでの判定結果が
        // 1つでもtrueであれば、それを戻り値とする
        RecordStrategy[] strategyList = tree.getRecordStrategy();
        for (RecordStrategy str : strategyList)
        {
            if (str.judgeSendExceedThresholdAlarm(node))
            {
                return true;
            }
        }

        // 判定がすべてfalseの場合
        return false;
    }

    /**
     * S2JavelinConfigとCallTreeに設定された判定クラスに対して、
     * 判定後に後処理を行う。
     *
     * @param callTree CallTree
     * @param node CallTreeNode
     * @param strategy S2JavelinConfigに設定された判定クラス。
     */
    public static void postJudge(final CallTree callTree, final CallTreeNode node,
            final RecordStrategy strategy)
    {
        strategy.postJudge();

        RecordStrategy[] highStrategyList = callTree.getHighPriorityRecordStrategy();
        for (RecordStrategy str : highStrategyList)
        {
            str.postJudge();
        }

        RecordStrategy[] strategyList = callTree.getRecordStrategy();
        for (RecordStrategy str : strategyList)
        {
            str.postJudge();
        }
    }

    /**
     * 最終アラーム送信時刻を確認し、更新する。
     * アラーム送信時刻から現在までの経過時間が閾値(javelin.alarmIntervalThreshold)を
     * 超えていた場合には、ログを出力しtrueを返す。
     * ただし、判定優先度：高の実行結果がtrueの場合は、経過時間に関係なく、ログ出力しtrueを返す。
     *
     * @param node 対象のノード。
     * @param config 設定。
     * @param judgeHigh 判定優先度：高の実行結果
     * @return アラーム送信時刻から現在までの経過時間が閾値を超えていた場合に限りtrueを返す。
     */
    private static boolean checkLastAlarmTime(final CallTreeNode node, final JavelinConfig config,
            final boolean judgeHigh)
    {
        boolean isLastAlarmTooNear = false;
        Invocation invocation = node.getInvocation();
        long lastAlarmTime = invocation.getLastAlarmTime();
        long currentTime = System.currentTimeMillis();
        long alarmIntervalThreshold = config.getAlarmMinimumInterval();
        if (judgeHigh == false
                && currentTime - lastAlarmTime <= alarmIntervalThreshold)
        {
            isLastAlarmTooNear = true;

            // アラーム削除メッセージは、アラーム送信間隔と同じ間隔で出力する。
            // また、アラーム削除メッセージサイズが、"DEF_BUFFER_SIZE"を超える場合、
            // 超えた文字列分を削除する。
            synchronized (discardBuffer__)
            {
                if (discardBuffer__.length() < DEF_BUFFER_SIZE)
                {
                    discardBuffer__.append(invocation.getClassName());
                    discardBuffer__.append('#');
                    discardBuffer__.append(invocation.getMethodName());
                }
                if (currentTime - lastDiscardTime__ >= alarmIntervalThreshold)
                {
                    String discardMessage = createDiscardMessage(discardBuffer__);
                    SystemLogger.getInstance().warn(discardMessage);
                    discardBuffer__ = new StringBuffer(DEF_BUFFER_SIZE);
                    lastDiscardTime__ = currentTime;
                }
                else
                {
                    if (discardBuffer__.length() < DEF_BUFFER_SIZE)
                    {
                        discardBuffer__.append(", ");
                    }
                }
            }
        }
        else
        {
            invocation.setLastAlarmTime(currentTime);
        }

        return isLastAlarmTooNear;
    }

    private static String createDiscardMessage(StringBuffer discardBuffer)
    {
        String header = "Alarm Discard: ";
        if (discardBuffer.length() >= DEF_BUFFER_SIZE)
        {
            String message = discardBuffer.substring(0, DEF_BUFFER_SIZE - 1) + "...";
            return header + message;
        }
        return header + discardBuffer.toString();
    }

    /**
     * 後処理（本処理失敗時）。
     *
     * @param className クラス名
     * @param methodName メソッド名
     * @param cause 例外オブジェクト
     * @param config パラメータの設定値を保存するオブジェクト
     */
    public static void postProcess(String className, String methodName, final Throwable cause,
            final JavelinConfig config)
    {
        postProcess(className, methodName, cause, config, true);
    }

    /**
     * 後処理（本処理失敗時）。
     *
     * @param className クラス名
     * @param methodName メソッド名
     * @param cause 例外オブジェクト
     * @param config パラメータの設定値を保存するオブジェクト
     * @param doExcludeProcess 除外対象処理を行うかどうか
     */
    public static void postProcess(String className, String methodName, final Throwable cause,
            final JavelinConfig config, final boolean doExcludeProcess)
    {
        postProcessCommon(null, cause, config);
    }

    private static void setCpuTime(CallTreeNode node)
    {
        long endCpuTime = node.getEndVmStatus().getCpuTime();
        long startCpuTime = node.getStartVmStatus().getCpuTime();
        long cpuTime = endCpuTime - startCpuTime;
        if (cpuTime < 0)
        {
            cpuTime = 0;
        }
        node.setCpuTime(cpuTime);
    }

    /**
     * Javelinログファイルを出力する。
     * @param config パラメータの設定値を保存するオブジェクト
     * @return Javelinログファイル
     */
    public static String dumpJavelinLog(final JavelinConfig config)
    {
        String fileName = "";
        // Javelinログファイルを出力する。
        JavelinFileGenerator generator = new JavelinFileGenerator(config);

        CallTree callTree = CallTreeRecorder.getInstance().getCallTree();
        if (callTree == null)
        {
            return fileName;
        }

        CallTreeNode root = callTree.getRootNode();
        if (root != null)
        {
            fileName =
                       generator.generateJaveinFile(callTree, recordStrategy__.createCallback(),
                                                    null, 0);
        }
        return fileName;
    }

    /**
     * トランザクションを記録する。
     *
     * @param node CallTreeNode
     */
    public static void recordTransaction(final CallTreeNode node)
    {
        if (node.isRecoreded() == false)
        {
            node.setRecoreded(true);

            Invocation invocation = node.getInvocation();
            if (invocation != null)
            {
                long elapsedTime     = node.getAccumulatedTime();
                long elapsedCpuTime  = node.getCpuTime();
                long elapsedUserTime = node.getUserTime();

                elapsedTime     = elapsedTime     - node.getChildrenTime();
                if (elapsedTime < 0)
                {
                    elapsedTime = 0;
                }
                elapsedCpuTime  = elapsedCpuTime  - node.getChildrenCpuTime();
                if (elapsedCpuTime < 0)
                {
                    elapsedCpuTime = 0;
                }
                elapsedUserTime = elapsedUserTime - node.getChildrenUserTime();
                if (elapsedUserTime < 0)
                {
                    elapsedUserTime = 0;
                }

                invocation.addInterval(node, elapsedTime, elapsedCpuTime, elapsedUserTime);

                if (node.getParent() != null)
                {
                    invocation.addCaller(node.getParent().getInvocation());
                }
            }

            List<CallTreeNode> children = node.getChildren();
            int size = children.size();
            for (int index = 0; index < size; index++)
            {
                CallTreeNode child = children.get(index);
                recordTransaction(child);
            }
        }
    }

    /**
     * Alarm通知する。
     * @param node CallTreeNode
     * @param callTreeRecorder callTreeRecorder
     */
    public static void sendAlarm(final CallTreeNode node, final CallTreeRecorder callTreeRecorder)
    {
        synchronized (ALARM_LISTENER_LIST)
        {
            for (AlarmListener alarmListener : ALARM_LISTENER_LIST)
            {
                // ルートノードのみAlarmを送信するAlarmListenerは、
                // 親を持つノードを無視する。
                boolean sendingRootOnly = alarmListener.isSendingRootOnly();
                if (sendingRootOnly == true && node.getParent() != null)
                {
                    continue;
                }

                try
                {
                    // AlarmListenerにはCallTreeNodeをそのまま渡す
                    // →アラーム通知で累積時間を使用するものがある為
                    alarmListener.sendExceedThresholdAlarm(node);
                }
                catch (Throwable ex)
                {
                    SystemLogger.getInstance().warn(ex);
                }
            }
        }
        CallTree tree = callTreeRecorder.getCallTree();
        List<CallTreeNode> eventList = tree.getEventNodeList();
        if (eventList.size() > 0)
        {
            sendEventAlarm();
        }
    }

    private static void sendEventAlarm()
    {
        CallTreeNode eventNode = new CallTreeNode();
        String processName = VMStatusHelper.getProcessName();
        Invocation invocation =
                                new Invocation(processName, EventConstants.EVENT_CLASSNAME,
                                               EventConstants.EVENT_METHODNAME, 0);
        eventNode.setInvocation(invocation);
        sendAlarmImpl(eventNode);
    }

    /**
     * Alarm通知する。
     * @param node CallTreeNode
     * @param telegramId 電文 ID
     */
    private static void sendAlarmImpl(final CallTreeNode node)
    {
        synchronized (ALARM_LISTENER_LIST)
        {
            for (AlarmListener alarmListener : ALARM_LISTENER_LIST)
            {
                // ルートノードのみAlarmを送信するAlarmListenerは、
                // 親を持つノードを無視する。
                boolean sendingRootOnly = alarmListener.isSendingRootOnly();
                if (sendingRootOnly == true && node.getParent() != null)
                {
                    continue;
                }

                try
                {
                    // AlarmListenerにはCallTreeNodeをそのまま渡す
                    // →アラーム通知で累積時間を使用するものがある為
                    alarmListener.sendExceedThresholdAlarm(node);
                }
                catch (Throwable ex)
                {
                    SystemLogger.getInstance().warn(ex);
                }
            }
        }
    }

    /**
     * Alarm通知に利用するAlarmListenerを登録する
     *
     * @param alarmListener Alarm通知に利用するAlarmListener
     */
    public static void addListener(final AlarmListener alarmListener)
    {
        synchronized (ALARM_LISTENER_LIST)
        {
            ALARM_LISTENER_LIST.add(alarmListener);
        }
    }

    /**
     * スレッドのIDを設定する
     * @param threadId スレッドID
     */
    public static void setThreadId(final String threadId)
    {
        CallTree callTree = CallTreeRecorder.getInstance().getCallTree();
        callTree.setThreadID(threadId);
    }

    /**
     * 初期化されているかを返す。
     * @return true:初期化されている、false:初期化されていない.
     */
    public static boolean isInitialized()
    {
        return initialized__;
    }

    /**
     * CallTreeNodeに設定された判定クラス(判定優先度：高)を利用して、
     * Javelinログをファイルに出力するかどうか判定する。
     *
     * @param node CallTreeNode
     * @return true:出力する、false:出力しない
     */
    private static JavelinLogCallback createCallback(final CallTree tree, final CallTreeNode node)
    {
        // CallTreeNodeに設定されていた判定クラスでの判定結果が
        // 1つでもtrueであれば、それを戻り値とする
        RecordStrategy[] strategyList = tree.getHighPriorityRecordStrategy();
        for (RecordStrategy str : strategyList)
        {
            JavelinLogCallback callback = str.createCallback();
            if (callback != null)
            {
                return callback;
            }
        }
        // 判定がすべてfalseの場合
        return recordStrategy__.createCallback();
    }

    /**
     * イベントレベルを文字列から数値に変換します。<br />
     *
     * @param eventLevelStr イベントレベル(文字列)
     * @return イベントレベル(数値)
     */
    private static int convertEventLevel(final String eventLevelStr)
    {
        if ("ERROR".equals(eventLevelStr))
        {
            return CommonEvent.LEVEL_ERROR;
        }
        if ("WARN".equals(eventLevelStr))
        {
            return CommonEvent.LEVEL_WARN;
        }
        if ("INFO".equals(eventLevelStr))
        {
            return CommonEvent.LEVEL_INFO;
        }
        return CommonEvent.LEVEL_WARN;
    }
}
