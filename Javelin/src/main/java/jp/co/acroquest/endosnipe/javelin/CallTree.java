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

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import jp.co.acroquest.endosnipe.javelin.util.HashMap;
import jp.co.acroquest.endosnipe.javelin.util.HashSet;
import jp.co.acroquest.endosnipe.javelin.util.LinkedHashMap;
import jp.co.acroquest.endosnipe.javelin.util.StatsUtil;
import jp.co.acroquest.endosnipe.javelin.util.TreeMap;

/**
 * Javelinログ出力用にコールスタックを記録するための、ツリークラス。
 * 
 * @author yamasaki
 */
public class CallTree
{
    /** CallTreeノード */
    private CallTreeNode rootNode_;

    /** ThreadID */
    private String threadID_;

    /** メソッド呼び出しのルートノードにつける名前。 */
    private String rootCallerName_ = "unknown";

    /** メソッド呼び出しのエンドノードの名前が決定できない場合につける名前。 */
    private String endCalleeName_ = "unknown";

    /** 例外の原因 */
    private Throwable cause_;

    /** CallBackのリスト */
    private final List<Callback> callbackList_;

    /** フラグ値を保存するMap */
    private final Map<String, Object> flagMap_;

    /** ログ値を保存するMap */
    private final Map<String, Object> loggingValueMap_;

    /** イベントのリスト */
    private final List<CallTreeNode> eventList_;

    /** 有効な深さのセット。 */
    private Set<Integer> depthSet_ = new HashSet<Integer>();

    /** ツリーに所属するCallTreeNodeの数 */
    private int nodeCount_ = 0;

    /** ツリーのトータルのノード数 */
    private int totalNodeCount_ = 0;

    /**
     * トランザクション中の変更を防止するためのコピーを実施したかどうか
     */
    private boolean                           isConfigCopied_                    = false;
    
    /**
     * javelin.leak.collection.monitorのコピーフィールド
     * (トランザクション中の変更を防止する。)
     */
    private boolean isCollectionMonitorEnabled_ = config_.isCollectionMonitor();
    
    /** javelin.concurrent.monitorのコピーフィールド */
    private boolean isConcurrentMonitorEnabled_ = config_.isConcurrentAccessMonitored();
    
    /** javelin.call.tree.enabledのコピーフィールド */
    private boolean isCallTreeEnabled_ = false;
    
    /** javelin.jdbc.enableのコピーフィールド */
    private boolean isJdbcEnabled_ = false;
    
    /** javelin.jdbc.recordDuplJdbcCallのコピーフィールド */
    private boolean isRecordDuplJdbcCallEnabled_ = false;
    
    /** ツリーに記録するイベントの最大数 */
    private static final int MAX_EVENT = 100;
    
    /** ノード数の集計方法を判定するための設定参照用。  */
    private static final JavelinConfig config_ = new JavelinConfig();

    /**
     * StatsJavelinRecorderで閾値判定を行う際に、CallTreeNode固有の判定を行うクラス。
     * (key, RecordStrategy)のマップとして複数持つことができる。
     * このRecordStrategyの判定処理はStatsJavelinRecorder自体に設定した
     * RecordStrategyより優先して実行される。
     */
    private final Map<String, RecordStrategy> highStrategyMap_;

    /**
     * StatsJavelinRecorderで閾値判定を行う際に、CallTreeNode固有の判定を行うクラス。
     * (key, RecordStrategy)のマップとして複数持つことができる。
     * このRecordStrategyの判定処理はStatsJavelinRecorder自体に設定した
     * RecordStrategyと同一レベルの優先度で実行される。
     */
    private final Map<String, RecordStrategy> normalStrategyMap_;

    /**
     * コンストラクタ。 スレッドIDを設定します。<br />
     */
    public CallTree()
    {
        this.threadID_ = StatsUtil.createThreadIDText();
        this.callbackList_ = new ArrayList<Callback>(5);
        this.flagMap_ = new HashMap<String, Object>();
        this.loggingValueMap_ = new TreeMap<String, Object>();
        this.eventList_ = new ArrayList<CallTreeNode>();
        this.highStrategyMap_ = new LinkedHashMap<String, RecordStrategy>();
        this.normalStrategyMap_ = new LinkedHashMap<String, RecordStrategy>();
    }

    /**
     * コピーコンストラクタ。 <br />
     * loggingValueMap のみディープコピー、その他のフィールドはシャローコピーします。<br />
     *
     * loggingValueMap は Javelin 適用アプリケーションのスレッドとは別のスレッドで使用されるため、
     * ディープコピーが必要です。
     *
     * @param callTree コピー元 CallTree
     */
    public CallTree(final CallTree callTree)
    {
        this.rootNode_ = callTree.rootNode_;
        this.threadID_ = callTree.threadID_;
        this.rootCallerName_ = callTree.rootCallerName_;
        this.endCalleeName_ = callTree.endCalleeName_;
        this.cause_ = callTree.cause_;
        this.callbackList_ = callTree.callbackList_;
        this.flagMap_ = callTree.flagMap_;
        this.loggingValueMap_ = new TreeMap<String, Object>(callTree.loggingValueMap_);
        this.eventList_ = callTree.eventList_;
        this.depthSet_ = callTree.depthSet_;
        this.nodeCount_ = callTree.nodeCount_;
        this.totalNodeCount_ = callTree.totalNodeCount_;
        this.isConfigCopied_ = callTree.isConfigCopied_;
        this.isCollectionMonitorEnabled_ = callTree.isCollectionMonitorEnabled_;
        this.isConcurrentMonitorEnabled_ = callTree.isConcurrentMonitorEnabled_;
        this.isCallTreeEnabled_ = callTree.isCallTreeEnabled_;
        this.isJdbcEnabled_ = callTree.isJdbcEnabled_;
        this.isRecordDuplJdbcCallEnabled_ = callTree.isRecordDuplJdbcCallEnabled_;
        this.highStrategyMap_ = callTree.highStrategyMap_;
        this.normalStrategyMap_ = callTree.normalStrategyMap_;
    }

    /**
     * 初期化します。
     */
    public void init()
    {
        this.rootNode_ = null;
        this.cause_ = null;
        this.callbackList_.clear();
        this.flagMap_.clear();
        this.loggingValueMap_.clear();
        this.eventList_.clear();
        this.depthSet_.clear();
        this.nodeCount_ = 0;
        this.totalNodeCount_ = 0;
        this.highStrategyMap_ .clear();
        this.normalStrategyMap_.clear();
        
        this.isConfigCopied_ = false;
        this.isCollectionMonitorEnabled_  = config_.isCollectionMonitor();
        this.isConcurrentMonitorEnabled_  = config_.isConcurrentAccessMonitored();
     }
    
    /**
     * ルートノードを取得します。<br />
     * 
     * @return ルートノード
     */
    public CallTreeNode getRootNode()
    {
        return this.rootNode_;
    }

    /**
     * ルートノードを設定します。<br />
     * 
     * @param rootNode ルートノード
     */
    public void setRootNode(final CallTreeNode rootNode)
    {
        if (rootNode == null)
        {
            return;
        }

        this.rootNode_ = rootNode;
        this.rootNode_.setRoot(true);
        
        loadConfig();
        
        RootInvocationManager.addRootInvocation(this.rootNode_.getInvocation());
    }

    /**
     * 設定をCallTreeに反映する。
     */
    public void loadConfig()
    {
        if (this.isConfigCopied_)
        {
            return;
        }
        this.isConfigCopied_ = true;
        this.isCollectionMonitorEnabled_  = config_.isCollectionMonitor();
        this.isConcurrentMonitorEnabled_  = config_.isConcurrentAccessMonitored();
        this.isCallTreeEnabled_           = config_.isCallTreeEnabled();
    }

    /**
     * ThreadIDを取得します。<br />
     * 
     * @return ThreadID
     */
    public String getThreadID()
    {
        return this.threadID_;
    }

    /**
     * ThreadIDを設定します。<br />
     * 
     * @param threadID スレッドID
     */
    public void setThreadID(final String threadID)
    {
        this.threadID_ = threadID;
    }

    /**
     * エンドノードを取得します。<br />
     * 
     * @return エンドノード
     */
    public String getEndCalleeName()
    {
        return this.endCalleeName_;
    }

    /**
     * エンドノードを設定します。<br />
     * 
     * @param endCalleeName エンドノード
     */
    public void setEndCalleeName(final String endCalleeName)
    {
        if (endCalleeName == null)
        {
            return;
        }
        this.endCalleeName_ = endCalleeName;
    }

    /**
     * 呼び出し元のルートノード名を取得します。<br />
     * 
     * @return 呼び出し元のルートノード名
     */
    public String getRootCallerName()
    {
        return this.rootCallerName_;
    }

    /**
     * 呼び出し元のルートノード名を設定します。<br />
     * 
     * @param rootCallerName 呼び出し元のルートノード名。
     */
    public void setRootCallerName(final String rootCallerName)
    {
        if (rootCallerName == null)
        {
            return;
        }
        this.rootCallerName_ = rootCallerName;
    }

    /**
     * CallBackを追加します。<br />
     * 
     * @param callback CallBack
     */
    public void addCallback(final Callback callback)
    {
        this.callbackList_.add(callback);
    }

    /**
     * CallBackを実行します。<br />
     */
    public void executeCallback()
    {
        int size = this.callbackList_.size();
        for (int index = 0; index < size; index++)
        {
            Callback callback = this.callbackList_.get(index);
            try
            {
                callback.execute();
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }

        if (size != 0)
        {
            this.callbackList_.clear();
        }
    }

    /**
     * フラグを設定します。<br />
     * 
     * @param flag フラグ
     * @param value 値
     * @return フラグ
     */
    public boolean setFlag(final String flag, final Object value)
    {
        if (SystemLogger.getInstance().isDebugEnabled())
        {
            SystemLogger.getInstance().debug(flag + " flag become valid.");
        }
        return (this.flagMap_.put(flag, value) != null);
    }

    /**
     * フラグを取得します。<br />
     * 
     * @param flag フラグ
     * @return フラグ
     */
    public Object getFlag(final String flag)
    {
        return this.flagMap_.get(flag);
    }

    /**
     * flagがMapに登録されているか返します。<br />
     * 
     * @param flag フラグ
     * @return true:キーがMapに登録されている、false:キーがMapに登録されていない。
     */
    public boolean containsFlag(final String flag)
    {
        return this.flagMap_.containsKey(flag);
    }

    /**
     * フラグの値をMapから除外します。<br />
     * 
     * @param flag フラグ
     * @return true:除外される、false:除外されない。
     */
    public boolean removeFlag(final String flag)
    {
        return (this.flagMap_.remove(flag) != null);
    }

    /**
     * ログ値を設定します。<br />
     * 
     * @param key キー
     * @param value 値
     */
    public void setLoggingValue(final String key, final Object value)
    {
        this.loggingValueMap_.put(key, value);
    }

    /**
     * Mapからキーを取得します。<br />
     * 
     * @return キー配列
     */
    public String[] getLoggingKeys()
    {
        Set<String> keySet = this.loggingValueMap_.keySet();
        String[] keys = keySet.toArray(new String[keySet.size()]);
        return keys;
    }

    /**
     * Mapからキーに対応する値を取得します。<br />
     * 
     * @param key キー
     * @return キーの値
     */
    public Object getLoggingValue(final String key)
    {
        return this.loggingValueMap_.get(key);
    }

    /**
     * 例外の原因を取得します。<br />
     * 
     * @return 例外の原因
     */
    public Throwable getCause()
    {
        return this.cause_;
    }

    /**
     * 例外の原因を設定します。<br />
     * 
     * @param cause 例外の原因
     */
    public void setCause(final Throwable cause)
    {
        this.cause_ = cause;
    }

    /**
     * イベント発生時のCallTreeNodeを追加します。<br />
     * 
     * @param node イベント発生時のCallTreeNode
     */
    public void addEventNode(final CallTreeNode node)
    {
        if (eventList_.size() < MAX_EVENT)
        {
            this.eventList_.add(node);
        }
    }

    /**
     * イベント発生時のCallTreeNodeのリストを取得します。<br />
     * 
     * @return イベント発生時のCallTreeNodeのリスト
     */
    public List<CallTreeNode> getEventNodeList()
    {
        return this.eventList_;
    }

    /**
     * CallTreeNode数を１増やします。
     */
    public void incrementNodeCount()
    {
        this.nodeCount_++;
        if (isCallTreeEnabled_)
        {
            this.totalNodeCount_++;
        }
        else if (totalNodeCount_ < nodeCount_)
        {
            // javelin.call.tree.enable=falseの場合、
            // nodeCount_が最高値を上回った場合のみ、
            // totalNodeCount_にnodeCount_を代入する。
            this.totalNodeCount_ = this.nodeCount_;
        }
    }

    /**
     * CallTreeNode数を１減らします。
     */
    public void decrementNodeCount()
    {
        this.nodeCount_--;
    }

    /**
     * CallTreeNode数を指定された数だけ減らします。
     *
     * @param count 減らす数
     */
    public void decrementNodeCount(int count)
    {
        this.nodeCount_ -= count;
    }
    
    /**
     * CallTreeNode数を取得します。<br />
     * 
     * @return CallTreeNode数
     */
    public int getNodeCount()
    {
        return this.nodeCount_;
    }

    /**
     * トータルのノード数を取得する。
     * 
     * @return トータルのノード数。
     */
    public int getTotalNodeCount()
    {
        return totalNodeCount_;
    }
    
    /**
     * CallTreeの深さを初期化します。<br />
     */
    public void clearDepth()
    {
        this.depthSet_ = new HashSet<Integer>();
    }

    /**
     * 計測したCallTreeの深さを保存します。<br />
     * 
     * @param depth CallTreeの深さ
     */
    public void addDepth(Integer depth)
    {
        this.depthSet_.add(depth);
    }

    /**
     * 引数で指定した深さが、計測対象であるかどうかを返します。<br />
     * 
     * @param depth CallTreeの深さ
     * @return 指定した深さが計測対象であるなら、<code>true</code>
     */
    public boolean containsDepth(Integer depth)
    {
        return this.depthSet_.contains(depth);
    }

    /**
     * 引数で指定した深さを計測対象から除外します。<br />
     * 
     * @param depth CallTreeの深さ
     */
    public void removeDepth(Integer depth)
    {
        this.depthSet_.remove(depth);
    }

    /**
     * 閾値判定用クラス(判定優先度：高)を返す。
     * @param key 閾値判定用クラスのキー
     * @return 閾値判定用クラス
     */
    public RecordStrategy getHighPriorityRecordStrategy(final String key)
    {
        return highStrategyMap_.get(key);
    }

    /**
     * 設定されている閾値判定用クラス(判定優先度：高)のリストを返す。
     * @return 閾値判定用クラスのリスト
     */
    public RecordStrategy[] getHighPriorityRecordStrategy()
    {
        return highStrategyMap_.values().toArray(new RecordStrategy[highStrategyMap_.size()]);
    }

    /**
     * 閾値判定用クラス(判定優先度：高)を設定する。既に同一のキーが登録されている場合は、登録しない。
     * @param key 閾値判定用クラスのキー
     * @param strategy 閾値判定用クラス
     */
    public void addHighPriorityRecordStrategy(final String key, final RecordStrategy strategy)
    {
        if (!highStrategyMap_.containsKey(key))
        {
            highStrategyMap_.put(key, strategy);
        }
    }

    /**
     * 閾値判定用クラス(判定優先度：通常)を設定する。既に同一のキーが登録されている場合は、登録しない。
     * @param key 閾値判定用クラスのキー
     * @param strategy 閾値判定用クラス
     */
    public void addRecordStrategy(final String key, final RecordStrategy strategy)
    {
        if (!normalStrategyMap_.containsKey(key))
        {
            normalStrategyMap_.put(key, strategy);
        }
    }

    /**
     * 閾値判定用クラス(判定優先度：通常)を返す。
     * @param key 閾値判定用クラスのキー
     * @return 閾値判定用クラス
     */
    public RecordStrategy getRecordStrategy(final String key)
    {
        return normalStrategyMap_.get(key);
    }

    /**
     * 設定されている閾値判定用クラス(判定優先度：通常)のリストを返す。
     * @return 閾値判定用クラスのリスト
     */
    public RecordStrategy[] getRecordStrategy()
    {
        return normalStrategyMap_.values().toArray(new RecordStrategy[normalStrategyMap_.size()]);
    }

    /**
     * 本ツリー内でCollectionMonitorが有効かどうかを示すフラグ。
     * @return trueならば、CollectionMonitorが有効。
     */
    public boolean isCollectionMonitorEnabled()
    {
        return isCollectionMonitorEnabled_;
    }

    /**
     * 本ツリー内でConcurrentMonitorが有効かどうかを示すフラグ。
     * @return trueならば、ConcurrentMonitorが有効。
     */
    public boolean isConcurrentMonitorEnabled()
    {
        return isConcurrentMonitorEnabled_;
    }

    /**
     * 本ツリー内でコールツリーが有効かどうかを示すフラグ。
     * @return trueならば、コールツリーが有効。
     */
    public boolean isCallTreeEnabled()
    {
        return isCallTreeEnabled_;
    }

    /**
     * 本ツリー内でJDBC Javelinが有効かどうかを示すフラグ。
     * @return trueならば、JDBC Javelinが有効。
     */
    public boolean isJdbcEnabled()
    {
        return isJdbcEnabled_;
    }

    /**
     * 本ツリー内でjavelin.jdbc.recordDuplJdbcCallが有効かどうかを示すフラグ。
     * @return trueならば、javelin.jdbc.recordDuplJdbcCallが有効。
     */
    public boolean isRecordDuplJdbcCallEnabled()
    {
        return isRecordDuplJdbcCallEnabled_;
    }
    
    /**
     * コピーコンストラクタにより自分のコピーを作る。
     * @return 自分のコピー。
     */
    public CallTree copy()
    {
        return new CallTree(this);
    }
}
