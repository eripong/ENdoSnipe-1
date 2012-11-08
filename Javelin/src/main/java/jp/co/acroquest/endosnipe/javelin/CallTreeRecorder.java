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

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import jp.co.acroquest.endosnipe.javelin.util.StatsUtil;
import jp.co.acroquest.endosnipe.javelin.util.ThreadUtil;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ConcurrentHashMap;

/**
 * コールツリーを記録します。
 *
 * @author eriguchi
 */
public class CallTreeRecorder
{
    /** スレッドIDがセットされていないことを表す値。 */
    private static final long THREAD_ID_NOT_SET = -1;

    /** インスタンス */
    private static ThreadLocal<CallTreeRecorder> recorder__ = new ThreadLocal<CallTreeRecorder>() {
        @Override
        protected synchronized CallTreeRecorder initialValue()
        {
            return new CallTreeRecorder();
        }
    };

    /** CallTreeNodeをスレッド毎に格納するマップ。 */
    private static Map<Long, WeakReference<CallTreeNode>> currentNodeMap__ =
            new ConcurrentHashMap<Long, WeakReference<CallTreeNode>>();

    /**
     * メソッコールツリーの記録用オブジェクト。<br />
     * ここで <code>new {@link CallTree}()</code> としないこと。
     * {@link CallTree} のコンストラクタ内で、 {@link ThreadLocal} の初期化処理が行われており、
     * 正しく初期化されないことがあるため。
     */
    private CallTree callTree_ = null;

    /**
     * メソッドの呼び出し元オブジェクト。
     */
    private CallTreeNode callerNode_ =  null;

    /** メソッド呼び出しの深さ */
    private int depth_ = 0;

    /** レコードメソッドが呼び出されたか */
    boolean isRecordMethodCalled_;

    /** 例外が発生したか */
    boolean isExceptionOccured_;

    /**
     * スレッドID 。<br />
     * ここで <code>{@link ThreadUtil}.getThreadId()</code> としないこと。
     * {@link ThreadUtil}.getThreadId() 内で、 {@link ThreadLocal} の初期化処理が行われており、
     * 正しく初期化されないことがあるため。
     */
    private long threadId_ = THREAD_ID_NOT_SET;

    /**
     * 勝手なインスタンス化を禁じるためのコンストラクタです。
     */
    protected CallTreeRecorder()
    {
        // インスタンス化を禁じる。
    }

    /**
     * スレッドごとのCallTreeReocrderのインスタンスを取得する。
     * @return CallTreeRecorder　
     */
    public static CallTreeRecorder getInstance()
    {
        return recorder__.get();
    }


    /**
     * 保存していたCallTreeNodeをクリアします。<br />
     */
    public static void clearNode()
    {
        List<Long> deleteList = new ArrayList<Long>();
        synchronized (currentNodeMap__)
        {
            for (Map.Entry<Long, WeakReference<CallTreeNode>> entry : currentNodeMap__.entrySet())
            {
                if (entry.getValue() == null)
                {
                    deleteList.add(entry.getKey());
                }
            }
        }

        for (int index = deleteList.size() - 1; index >= 0; index--)
        {
            deleteList.remove(index);
        }
    }

    /**
     * 引数で指定されたスレッドIDに対応するCallTreeNode を取得します。<br />
     *
     * @param id スレッドID
     * @return {@link CallTreeNode}オブジェクト
     */
    public static CallTreeNode getNode(final Long id)
    {
        WeakReference<CallTreeNode> weakReference = currentNodeMap__.get(id);
        if (weakReference == null)
        {
            return null;
        }

        CallTreeNode callTreeNode = weakReference.get();
        return callTreeNode;
    }

    /**
     * CallTreeを取得します。
     *
     * @return CallTree。
     */
    public CallTree getCallTree()
    {
        if (this.callTree_ == null)
        {
            this.callTree_ = new CallTree();
        }
        return this.callTree_;
    }

    /**
     * CallTreeNodeを取得します。
     *
     * @return CallTree。
     */
    public CallTreeNode getCallTreeNode()
    {
        return this.callerNode_;
    }

    /**
     * CallTreeをクリアし、新しいインスタンスを設定します。
     */
    public void clearCallTree()
    {
        CallTree callTree = getCallTree();
        callTree.init();
        this.depth_ = 0;
        clearCallerNode();
    }

    /**
     * CallTreeNodeをクリアします。
     */
    public void clearCallerNode()
    {
        this.callerNode_ = null;
        long threadId = this.getThreadId();
        currentNodeMap__.remove(threadId);
    }

    /**
     * CallTreeNodeを設定します。
     *
     * @param node CallTreeNode。
     */
    public void setCallerNode(final CallTreeNode node)
    {
        this.callerNode_ = node;

        long threadId = this.getThreadId();
        if (node == null)
        {
            currentNodeMap__.remove(threadId);
        }
        else
        {
            // Mapに格納する。
            currentNodeMap__.put(threadId, new WeakReference<CallTreeNode>(node));
        }
    }

    /**
     * メソッド呼び出しツリーを初期化する。
     *
     * @param callTree メソッド呼び出しツリー（ <code>null</code> は非許容）
     */
    public void setCallTree(final CallTree callTree)
    {
        this.callTree_ = callTree;
    }

    /**
     * 現在の呼び出し元CallTreeNodeの子ノードを削除する。
     */
    public void clearChildren()
    {
        CallTreeNode node = callerNode_;
        if (node != null)
        {
            node.clearChildren();
        }
    }

    /**
     * CallTreeのサイズが最大値に達しているかどうかを判定します。<br />
     *
     * @param callTree コールツリー
     * @param config Javelin.propetiesの設定値
     * @return CallTreeのサイズがJavelin.propertiesに設定していた値を超えているなら、<code>true</code>
     */
    public static boolean isCallTreeFull(CallTree callTree, JavelinConfig config)
    {
        return callTree.getNodeCount() >= config.getCallTreeMax();
    }

    /**
     * CallTreeNodeを追加します。
     *
     * @param parent 親ノード。
     * @param tree コールツリー。
     * @param node 子ノード。
     * @param config 設定。
     */
    public static void addCallTreeNode(CallTreeNode parent, final CallTree tree, final CallTreeNode node,
            final JavelinConfig config)
    {
        if (parent != null)
        {
            parent.addChild(node);
            node.getInvocation().addCaller(parent.getInvocation());
        }
    }

    /**
     * CallTreeNodeを生成します。
     *
     * @param invocation Invocation。
     * @param args 引数。
     * @param stacktrace スタックトレース。
     * @param config 設定。
     * @return CallTreeNode。
     */
    public static CallTreeNode createNode(
            Invocation invocation,
            final Object[] args,
            final StackTraceElement[] stacktrace,
            final JavelinConfig config)
    {
        CallTreeNode node = new CallTreeNode();

        node.setStacktrace(stacktrace);

        // パラメータ設定が行われているとき、ノードにパラメータを設定する
        if (args != null)
        {
            CallTreeRecorder.addLogArgs(node, args, config);
        }

        node.setInvocation(invocation);

        return node;
    }

    public static void addLogArgs(
            final CallTreeNode node,
            final Object[] args,
            final JavelinConfig config)
    {
        String[] argStrings = new String[args.length];
        for (int index = 0; index < args.length; index++)
        {
            if (config.isArgsDetail())
            {
                int argsDetailDepth = config.getArgsDetailDepth();
                argStrings[index] =
                    StatsUtil.buildDetailString(args[index], argsDetailDepth);
            }
            else
            {
                argStrings[index] =
                    StatsUtil.toStr(args[index], config.getStringLimitLength());
            }
        }

        node.setArgs(argStrings);
    }

    /**
     * 既に終了したCallTreeNodeを取得する。
     *
     * @return 既に終了したCallTreeNodeのリスト。
     */
    public List<CallTreeNode> removeFinishedNode()
    {
        List<CallTreeNode> removedList = new ArrayList<CallTreeNode>();

        CallTree tree = getCallTree();
        CallTreeNode rootNode = tree.getRootNode();
        removeFinishedNodeInternal(removedList, rootNode);

        return removedList;
    }

    /**
     * 指定したCallTreeNodeの子が終了していれば削除し、削除したCallTreeNodeはリストに格納する。
     *
     * @param list 削除したCallTreeNodeのリスト。
     * @param node 操作対象のCallTreeNode。
     */
    private static void removeFinishedNodeInternal(List<CallTreeNode> list, CallTreeNode node)
    {
        List<CallTreeNode> children = node.getChildren();
        for (int index = 0; index < children.size(); index++)
        {
            CallTreeNode child = children.get(index);
            if (child.getEndTime() != -1)
            {
                list.add(child);
                node.removeChild(child);
                index--;
            }
            else
            {
                removeFinishedNodeInternal(list, child);
            }
        }
    }

    /**
     * 深さを取得する。
     *
     * @return 深さ
     */
    public int getDepth()
    {
        return this.depth_;
    }

    /**
     * 深さを設定する。
     *
     * @param depth 深さ
     */
    public void setDepth(int depth)
    {
        this.depth_ = depth;
    }

    /**
     * レコードメソッドが呼び出されたかを取得する。
     * @return レコードメソッドが呼び出されたか
     */
    public boolean isRecordMethodCalled()
    {
        return this.isRecordMethodCalled_;
    }

    /**
     * レコードメソッドが呼び出されたかを取得する。
     * @param isRecordMethodCalled レコードメソッドが呼び出されたか
     */
    public void setRecordMethodCalled(boolean isRecordMethodCalled)
    {
        this.isRecordMethodCalled_ = isRecordMethodCalled;
    }

    /**
     * 例外が発生したかを取得する。
     *
     * @return 例外が発生したか
     */
    public boolean isExceptionOccured()
    {
        return this.isExceptionOccured_;
    }

    /**
     * 例外が発生したかを取得する。
     *
     * @param isExceptionOccured 例外が発生したか
     */
    public void setExceptionOccured(boolean isExceptionOccured)
    {
        this.isExceptionOccured_ = isExceptionOccured;
    }

    /**
     * スレッドIDを取得する。
     *
     * @return スレッドID
     */
    public long getThreadId()
    {
        if (this.threadId_ == THREAD_ID_NOT_SET)
        {
            this.threadId_ = ThreadUtil.getThreadId();
        }
        return this.threadId_;
    }

    /**
     * 子ノードを削除する。
     *
     * @param parent 親ノード
     * @param node 子ノード
     */
    public void removeChildNode(CallTreeNode parent, CallTreeNode node)
    {
        parent.removeChild(node);
        setDepth(getDepth() - 1);
    }

}
