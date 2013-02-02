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
package jp.co.acroquest.endosnipe.javelin.jdbc.stats;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.javelin.CallTreeRecorder;
import jp.co.acroquest.endosnipe.javelin.util.HashMap;
import jp.co.acroquest.endosnipe.javelin.util.HashSet;

/**
 * JDBCJavelinがスレッド毎に持つ状態値です。
 * 
 * @author eriguchi
 */
public class JdbcJvnStatus
{
    /**
     * 実行計画取得中に、それ用のStatementを登録するフィールド。
     * 実行計画取得中でなければnullを設定しておきます。
     */
    private Statement nowExpalaining_;

    /** メソッド呼び出しの深さ */
    private int depth_;

    /** メソッド呼び出しの深さの最大値 */
    private int depthMax_;

    /** callDepth_をキーとし、SQLが保存されていないかどうかを値とするマップ。 */
    private Map<Integer, Boolean> noSqlArgsMap_;

    /** JdbcJavelinRecorderの呼び出しの深さ。  */
    private int callDepth_;

    /** SQLの一時保存先 */
    private String[] execPlanSql_;

    /** Preprocessした深さ。  */
    private Set<Integer> preprocessedDepthSet_;
    
    /** コールツリーレコーダ */
    private CallTreeRecorder callTreeRecorder_;

    /**
     * 実行計画取得中に、それ用のStatementを登録するフィールド。
     * 実行計画取得中でなければnullを設定しておきます。
     */
    private Connection nowCalling_;

    /** JDBC Javelinがスレッド毎に持つ状態。 */
    private static ThreadLocal<JdbcJvnStatus> jdbcJvnStatus__ = new ThreadLocal<JdbcJvnStatus>() {
        @Override
        protected synchronized JdbcJvnStatus initialValue()
        {
            return new JdbcJvnStatus();
        }
    };

    /**
     * コンストラクタ。
     */
    private JdbcJvnStatus()
    {
        this.nowExpalaining_ = null;
        this.depth_ = 0;
        this.depthMax_ = 0;
        this.noSqlArgsMap_ = new HashMap<Integer, Boolean>();
        this.callDepth_ = 0;
        this.execPlanSql_ = null;
        this.preprocessedDepthSet_ = new HashSet<Integer>();
        this.nowCalling_ = null;
    }

    /**
     * コールツリーレコーダを取得する。
     * 
     * @return コールツリーレコーダ
     */
    public CallTreeRecorder getCallTreeRecorder()
    {
        if (this.callTreeRecorder_ == null)
        {
            this.callTreeRecorder_ = CallTreeRecorder.getInstance();
        }

        return callTreeRecorder_;
    }

    /**
     * 該当するスレッドに属するインスタンスを取得します。
     * 
     * @return インスタンス。
     */
    public static JdbcJvnStatus getInstance()
    {
        return jdbcJvnStatus__.get();
    }

    /**
     * 深さを1増やします。
     * 
     * @return 更新後の深さ。
     */
    public int incrementDepth()
    {
        this.depth_++;
        this.depthMax_ =  this.depth_;
        return this.depth_;
    }

    /**
     * JdbcJavelinRecorderの呼び出しの深さをインクリメントします。
     */
    public void incrementCallDepth()
    {
        this.callDepth_++;
    }

    /**
     * JdbcJavelinRecorderの呼び出しの深さをデクリメントします。
     */
    public void decrementCallDepth()
    {
        this.callDepth_--;
    }

    /**
     * 事前処理を行ったかどうかを取得します。
     * 
     * @return 事前処理を行ったかどうか。
     */
    public boolean isPreprocessDepth()
    {
        Set<Integer> preprocessedDepthSet = this.preprocessedDepthSet_;
        if (preprocessedDepthSet.isEmpty() == true)
        {
            return false;
        }
        return preprocessedDepthSet.contains(this.callDepth_);
    }

    /**
     * 事前処理を行ったかどうかのフラグを、クリアします。
     */
    public void removePreProcessDepth()
    {
        this.preprocessedDepthSet_.remove(this.callDepth_);
    }

    /**
     * ThreadLocalに実行計画取得用のSQLを格納する.
     * @param sql 一時保存するSQL
     */
    public void setExecPlanSql(final Object[] sql)
    {

        // 既に設定されている場合は何もしない。
        String[] execPlanSql = this.execPlanSql_;
        if (execPlanSql != null && execPlanSql.length > 0)
        {
            return;
        }

        if (sql == null || sql.length == 0)
        {
            this.setExecPlanSql(null);
            return;
        }
        String[] strSql = new String[sql.length];
        System.arraycopy(sql, 0, strSql, 0, sql.length);
        this.setExecPlanSql(strSql);
    }

    public void setNoSql(Boolean noSql)
    {
        this.noSqlArgsMap_.put(this.callDepth_, noSql);
    }

    public void savePreprocessDepth()
    {
        this.preprocessedDepthSet_.add(this.callDepth_);
    }

    public Boolean isNoSql()
    {
        return this.noSqlArgsMap_.get(this.callDepth_);
    }

    public void decrementDepth()
    {
        this.depth_--;
    }

    public void clearPreprocessedDepthSet()
    {
        this.preprocessedDepthSet_.clear();
    }

    public Statement getNowExpalaining()
    {
        return nowExpalaining_;
    }

    public void setNowExpalaining(Statement nowExpalaining)
    {
        nowExpalaining_ = nowExpalaining;
    }

    public int getDepth()
    {
        return depth_;
    }

    public void setDepth(int depth)
    {
        depth_ = depth;
    }

    public int getDepthMax()
    {
        return depthMax_;
    }

    public void setDepthMax(int depthMax)
    {
        depthMax_ = depthMax;
    }

    public int getCallDepth()
    {
        return callDepth_;
    }

    public void setCallDepth(int callDepth)
    {
        callDepth_ = callDepth;
    }

    public String[] getExecPlanSql()
    {
        return execPlanSql_;
    }

    public void setExecPlanSql(String[] execPlanSql)
    {
        execPlanSql_ = execPlanSql;
    }

    public Connection getNowCalling_()
    {
        return nowCalling_;
    }

    public void setNowCalling_(Connection nowCalling)
    {
        this.nowCalling_ = nowCalling;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append("[nowExplaining=");
        builder.append(this.nowExpalaining_);
        builder.append(", depth=");
        builder.append(this.depth_);
        builder.append(", depthMax=");
        builder.append(this.depthMax_);
        builder.append(", noSqlArgsMap=");
        builder.append(this.noSqlArgsMap_);
        builder.append(", callDepth=");
        builder.append(this.callDepth_);
        builder.append(", execPlanSql=");
        builder.append(Arrays.toString(this.execPlanSql_));
        builder.append(", preprocessedDepthSet=");
        builder.append(this.preprocessedDepthSet_);
        if (this.getCallTreeRecorder() != null)
        {
            builder.append(", callerNode=");
            builder.append(this.getCallTreeRecorder().getCallTreeNode());
        }
        builder.append(", callTreeRecorder=");
        builder.append(this.callTreeRecorder_);
        builder.append("]");
        return builder.toString();
    }
}
