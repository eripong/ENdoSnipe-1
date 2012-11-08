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
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.event.CommonEvent;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import jp.co.acroquest.endosnipe.javelin.util.TreeMap;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.CopyOnWriteArrayList;

/**
 * メソッド呼び出し情報
 * @author eriguchi
 *
 */
public class CallTreeNode
{
    /**
     * コンストラクタ
     */
    public CallTreeNode()
    {
        // Do Nothing.
    }

    /**  */
    private Invocation          invocation_;

    /** 戻り値 */
    private String              returnValue_;

    /** 例外 */
    private Throwable           throwable_;

    /** 例外発生時刻 */
    private long                throwTime_;

    /** 開始時刻 */
    private long                startTime_;

    /** 終了時刻 */
    private long                endTime_  = -1;

    /** 累積時間 */
    private long                accumulatedTime_;

    /** CPU時間 */
    private long                cpuTime_;

    /** ユーザ時間 */
    private long                userTime_;

    /** 開始時のVMのステータス */
    private VMStatus            startVmStatus_;

    /** 終了時のVMのステータス */
    private VMStatus            endVmStatus_;

    /** 引数 */
    private String[]            args_;

    /** スタックトレース */
    private StackTraceElement[] stacktrace_;

    /** 自分が所属しているCallTree */
    private CallTree            tree_;

    /** CallTreeNodeの親ノード */
    private CallTreeNode        parent_;

    /** CallTreeNodeの子ノード */
    private List<CallTreeNode>  children_ = new ArrayList<CallTreeNode>();

    /** フィールドアクセス */
    private boolean             isFieldAccess_;

    /** 深さ */
    private int                 depth_;

    /** 子ノードで消費された処理時間 */
    private long                childrenTime_;

    /** 子ノードで消費されたCPU時間 */
    private long                childrenCpuTime_;

    /** 子ノードで消費されたユーザ時間 */
    private long                childrenUserTime_;

    /** １つのノードに記録するイベントの最大数 */
    private static final int    MAX_EVENT = 100;

    /** Invocationに記録したかどうかを示すフラグ。 */
    private boolean             isRecoreded_= false;

    /** ストールメソッドとして検出されたかどうかを示すフラグ */
    private boolean isStalled_ = false;

    /**
     * 深さを取得する。
     *
     * @return 深さ
     */
    public int getDepth()
    {
        return depth_;
    }

    /**
     * 深さを設定する。
     *
     * @param depth 深さ
     */
    public void setDepth(int depth)
    {
        depth_ = depth;
    }

    /**  */
    private final Map<String, Object> loggingValueMap_ = new TreeMap<String, Object>();

    /** CallTreeNodeがrootかどうか(true:root、false:not root。デフォルト値はfalse) */
    private boolean                   isRoot_          = false;

    /** 発生したイベント。 */
    private final List<CommonEvent>   eventList_       = new CopyOnWriteArrayList<CommonEvent>();

    /** 重複呼び出し削除用のカウンタ(ServletMonitorより使用) */
    public int                        count_;

    /**
     * Javelinの計測にかかった時間(現在の実装ではJDBC Javelinの実行計画取得のみ)
     */
    private long                      javelinTime_;

    /**
     * InvoCation
     * invocationを取得する。
     * @return Invocation
     */
    public Invocation getInvocation()
    {
        return this.invocation_;
    }

    /**
     * Invocationを設定する。
     * @param invocation Invocation
     */
    public void setInvocation(final Invocation invocation)
    {
        this.invocation_ = invocation;
    }

    /**
     * 戻り値を取得する。
     * @return 戻り値
     */
    public String getReturnValue()
    {
        return this.returnValue_;
    }

    /**
     * 戻り値を設定する。
     * @param returnValue 戻り値
     */
    public void setReturnValue(final String returnValue)
    {
        this.returnValue_ = returnValue;
    }

    /**
     * メソッド開始時刻を取得する。
     * @return メソッド開始時刻
     */
    public long getStartTime()
    {
        return this.startTime_;
    }

    /**
     * メソッド開始時刻を取得する。
     * @param startTime メソッド開始時刻
     */
    public void setStartTime(final long startTime)
    {
        this.startTime_ = startTime;
    }

    /**
     * メソッドの終了時刻を取得する。
     * @return メソッドの終了時刻
     */
    public long getEndTime()
    {
        return this.endTime_;
    }

    /**
     * メソッドの終了時刻を設定する。
     * @param endTime メソッドの終了時刻
     */
    public void setEndTime(final long endTime)
    {
        this.endTime_ = endTime;
        this.accumulatedTime_ = this.endTime_ - this.startTime_ - this.javelinTime_;
        this.invocation_.setAccumulatedTime(this.accumulatedTime_);
    }

    /**
     * 累積時間を取得する。
     * @return 累積時間
     */
    public long getAccumulatedTime()
    {
        return this.accumulatedTime_;
    }

    /**
     * CPU時間を取得す。る
     * @param cpuTime CPU時間
     */
    public void setCpuTime(final long cpuTime)
    {
        this.cpuTime_ = cpuTime;
    }

    /**
     * CPU時間を取得する。
     * @return CPU時間
     */
    public long getCpuTime()
    {
        return this.cpuTime_;
    }

    /**
     * StackTraceを取得する。
     * @return StackTrace
     */
    public StackTraceElement[] getStacktrace()
    {
        return this.stacktrace_;
    }

    /**
     * StackTraceを設定する。
     * @param stacktrace StackTrace
     */
    public void setStacktrace(final StackTraceElement[] stacktrace)
    {
        this.stacktrace_ = stacktrace;
    }

    /**
     * 自分が所属するCallTreeを取得する。
     *
     * @return CallTree
     */
    public CallTree getTree()
    {
        return tree_;
    }

    /**
     * 自分が所属するCallTreeを設定する。
     * @param tree CallTree
     */
    public void setTree(CallTree tree)
    {
        tree_ = tree;
    }

    /**
     * CallTreeNodeの親を取得する。
     * @return CallTreeNodeの親
     */
    public CallTreeNode getParent()
    {
        return this.parent_;
    }

    /**
     * CallTreeNodeの親を設定する。
     * @param parent CallTreeNodeの親
     */
    public void setParent(final CallTreeNode parent)
    {
        this.parent_ = parent;
    }

    /**
     * CallTreeNOdeの子を設定する。
     * @param children CallTreeNodeの子
     */
    public void setChildren(List<CallTreeNode> children)
    {
        this.children_ = children;
    }

    /**
     * CallTreeNOdeの子を取得する。
     * @return CallTreeNodeの子
     */
    public List<CallTreeNode> getChildren()
    {
        return this.children_;
    }

    /**
     * CallTreeNOdeの子を追加する。
     * @param node CallTreeNodeの子
     */
    public void addChild(final CallTreeNode node)
    {
        this.children_.add(node);
        node.setTree(tree_);
        node.setParent(this);

        if (tree_ != null)
        {
            tree_.incrementNodeCount();
        }
    }

    /**
     * 全てのCallTreeNodeの子を削除する。
     */
    public void clearChildren()
    {
        int size = children_.size();
        children_.clear();
        if (tree_ != null)
        {
            tree_.decrementNodeCount(size);
        }
    }

    /**
     * CallTreeNodeの子を削除する。
     * @param node CallTreeNodeの子
     */
    public void removeChild(CallTreeNode node)
    {
        if (children_.remove(node) && tree_ != null)
        {
            tree_.decrementNodeCount(node.countChildren() + 1);
        }
    }

    /**
     * 配下のノードの数を数える。
     *
     * @return　配下のノードの数
     */
    private int countChildren()
    {
        int count = children_.size();
        for (int index = 0; index < children_.size(); index++)
        {
            count = count + children_.get(index).countChildren();
        }

        return count;
    }

    /**
     * 引数を取得する。
     * @return 引数
     */
    public String[] getArgs()
    {
        return this.args_;
    }

    /**
     * 引数を設定する。
     * @param args 引数
     */
    public void setArgs(final String[] args)
    {
        this.args_ = args;
    }

    /**
     * ノードがフィールドへのアクセスかどうかを示すフラグを取得する。
     *
     * @return フィールドアクセスならtrue、そうでなければfalseを返す。
     */
    public boolean isFieldAccess()
    {
        return this.isFieldAccess_;
    }

    /**
     * ノードがフィールドへのアクセスかどうかを示すフラグを取得する。
     *
     * @param isFieldAccess フィールドアクセスならtrue、そうでなければfalse。
     */
    public void setFieldAccess(final boolean isFieldAccess)
    {
        this.isFieldAccess_ = isFieldAccess;
    }

    /**
     * 例外を取得する。
     * @return 例外
     */
    public Throwable getThrowable()
    {
        return this.throwable_;
    }

    /**
     * 例外を設定する。
     * @param throwable 例外
     */
    public void setThrowable(final Throwable throwable)
    {
        this.throwable_ = throwable;
    }

    /**
     * 例外発生時刻を取得する。
     * @return 例外発生時刻
     */
    public long getThrowTime()
    {
        return this.throwTime_;
    }

    /**
     * 例外発生時刻を設定する。
     * @param throwTime 例外発生時刻。
     */
    public void setThrowTime(final long throwTime)
    {
        this.throwTime_ = throwTime;
    }

    /**
     * VMのステータスを取得する。
     * @return VMのステータス
     */
    public VMStatus getEndVmStatus()
    {
        return this.endVmStatus_;
    }

    /**
     * VMのステータスを設定する。
     * @return VMのステータス
     */
    public VMStatus getStartVmStatus()
    {
        return this.startVmStatus_;
    }

    /**
     * 終了時のVMのステータス設定する。
     * @param endVmStatus 終了時のVMのステータス
     */
    public void setEndVmStatus(final VMStatus endVmStatus)
    {
        this.endVmStatus_ = endVmStatus;
    }

    /**
     * 開始時のVMのステータス設定する。
     * @param startVmStatus 開始時のVMのステータス
     */
    public void setStartVmStatus(final VMStatus startVmStatus)
    {
        this.startVmStatus_ = startVmStatus;
    }

    /**
     * ユーザ時間を取得する。
     * @return ユーザ時間
     */
    public long getUserTime()
    {
        return this.userTime_;
    }

    /**
     * ユーザ時間を設定する。
     * @param userTime ユーザ時間
     */
    public void setUserTime(final long userTime)
    {
        this.userTime_ = userTime;
    }

    /**
     * ログ値を設定する。
     *
     * @param key キー
     * @param value 値
     */
    public void setLoggingValue(final String key, final Object value)
    {
        // Call Treeがdisableの場合かつRootNodeが存在する場合、
        // RootNodeに保存する。
        if (isRoot_ == false && !tree_.isCallTreeEnabled())
        {
            CallTreeNode rootNode = tree_.getRootNode();
            if (rootNode != null)
            {
                rootNode.setLoggingValue(key, value);
            }
        }

        synchronized (this.loggingValueMap_)
        {
            this.loggingValueMap_.put(key, value);
        }
    }

    /**
     * Mapからキーを取得する。
     *
     * @return キー配列
     */
    public String[] getLoggingKeys()
    {
        synchronized (this.loggingValueMap_)
        {
            Set<String> keySet = this.loggingValueMap_.keySet();
            String[] keys = keySet.toArray(new String[keySet.size()]);

            return keys;
        }
    }

    /**
     * Mapからキーに対応する値を取得する。
     *
     * @param key キー
     * @return キーの値
     */
    public Object getLoggingValue(final String key)
    {
        synchronized (this.loggingValueMap_)
        {
            return this.loggingValueMap_.get(key);
        }
    }

    /**
     * CallTreeNodeがrootかどうかを表す値を返す。
     * @return true:root、false:not root
     */
    public boolean isRoot()
    {
        return this.isRoot_;
    }

    /**
     * CallTreeNodeがrootかどうかを表す値を設定する。
     * @param isRoot CallTreeNodeがrootかどうかを表す値
     */
    public void setRoot(final boolean isRoot)
    {
        this.isRoot_ = isRoot;
    }

    /**
     * このCallTreeNodeのrootにあたるCallTreeNodeを返す。
     * @return rootにあたるCallTreeNode
     */
    public CallTreeNode getRootNode()
    {
        CallTreeNode tmp = this;
        CallTreeNode parent = tmp.getParent();
        while (parent != null)
        {
            tmp = parent;
            parent = tmp.getParent();
        }
        return tmp;
    }

    /**
     * イベントを追加する。
     *
     * @param event イベント。
     */
    public void addEvent(CommonEvent event)
    {
        // Call Treeがdisableの場合はTreeに保存する。
        if (tree_ != null && !tree_.isCallTreeEnabled())
        {
            CallTreeNode node = new CallTreeNode();
            node.setInvocation(getInvocation());
            node.addEventForce(event);
            tree_.addEventNode(node);
        }
        else
        {
            addEventForce(event);
        }
    }

    /**
     * イベントを追加する。
     *
     * @param event イベント。
     */
    private void addEventForce(CommonEvent event)
    {
        if (this.eventList_.size() < MAX_EVENT)
        {
            this.eventList_.add(event);
        }
    }

    /**
     * イベントリストを取得する。
     *
     * @return イベント。
     */
    public CommonEvent[] getEventList()
    {
        return this.eventList_.toArray(new CommonEvent[this.eventList_.size()]);
    }

    /**
     * 指定したイベントを削除する。
     *
     * @param event イベント。
     */
    public void removeEvent(CommonEvent event)
    {
        this.eventList_.remove(event);
    }

    /**
     * Javelinの計測にかかった時間(現在の実装ではJDBC Javelinの実行計画取得のみ)に
     * 値を追加する。
     *
     * @param javelinTime Javelinの計測にかかった時間
     */
    public void addJavelinTime(long javelinTime)
    {
        if (javelinTime == 0)
        {
            return;
        }

        this.javelinTime_ += javelinTime;
        CallTreeNode parent = this.parent_;

        if (parent != null)
        {
            parent.addJavelinTime(javelinTime);
        }
    }

    /**
     * Javelinの計測にかかった時間(現在の実装ではJDBC Javelinの実行計画取得のみ)
     * を取得する。
     *
     * @return Javelinの計測にかかった時間
     */
    public long getJavelinTime()
    {
        return this.javelinTime_;
    }

    /**
     * Javelinの計測にかかった時間(現在の実装ではJDBC Javelinの実行計画取得のみ)
     * を設定する。
     *
     * @param javelinTime Javelinの計測にかかった時間
     */
    public void setJavelinTime(long javelinTime)
    {
        javelinTime_ = javelinTime;
    }

    public long getChildrenTime()
    {
        return childrenTime_;
    }

    public long getChildrenCpuTime()
    {
        return childrenCpuTime_;
    }

    public long getChildrenUserTime()
    {
        return childrenUserTime_;
    }

    public void addChildrenTime(long childrenTime)
    {
        childrenTime_ += childrenTime;
    }

    public void addChildrenCpuTime(long childrenCpuTime)
    {
        childrenCpuTime_ += childrenCpuTime;
    }

    public void addChildrenUserTime(long childrenUserTime)
    {
        childrenUserTime_ += childrenUserTime;
    }

    public boolean isRecoreded()
    {
        return isRecoreded_;
    }

    public void setRecoreded(boolean isRecoreded)
    {
        isRecoreded_ = isRecoreded;
    }

    public boolean isStalled()
    {
        return isStalled_;
    }

    public void setStalled(boolean isStalled)
    {
        isStalled_ = isStalled;
    }

}
