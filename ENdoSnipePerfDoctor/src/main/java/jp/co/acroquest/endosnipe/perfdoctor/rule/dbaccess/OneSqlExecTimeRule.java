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
package jp.co.acroquest.endosnipe.perfdoctor.rule.dbaccess;

import java.util.HashMap;
import java.util.Map;

import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;

/**
 * 同一SQLの総実行時間をカウントするルール。
 * 
 * @author tooru
 */
public class OneSqlExecTimeRule extends AbstractDbAccessRule
{
    /** SQLの開始タグ */
    private static final String                SQL_TAG = "[SQL]";

    /** 警告と判断する SQL 実行回数の閾値 */
    public long                                threshold;

    /** 同一SQL文の総実行時間を記録するMap */
    private final Map<SqlEntry, SqlCountEntry> sqlCounts_;

    /**
     * コンストラクタ。
     */
    public OneSqlExecTimeRule()
    {
        super();
        this.sqlCounts_ = new HashMap<SqlEntry, SqlCountEntry>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doJudgeContent(final JavelinLogElement element, final String content,
            final String bindVal)
    {
        // TODO validationを実装する際にはそちらでチェックするようにする
        if (this.threshold < 1)
        {
            this.threshold = 1;
        }

        String threadName = element.getThreadName();

        String sql = content;
        SqlEntry key = new SqlEntry(threadName, sql);
        SqlCountEntry countEntry = this.sqlCounts_.get(key);
        String[] args = JavelinLogUtil.getArgs(element);
        String timeStr = JavelinLogUtil.getArgContent(args[0], "[Time]");

        long count;
        if (countEntry != null)
        {
            count = countEntry.getCount() + Long.parseLong(timeStr);

        }
        else
        {
            countEntry = new SqlCountEntry();
            count = Long.parseLong(timeStr);
            this.sqlCounts_.put(key, countEntry);
        }
        countEntry.setCount(count);
        countEntry.addBindValCount(bindVal);

        if (countEntry.getErrorElement() == null && count >= this.threshold)
        {
            countEntry.setErrorElement(element);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTagName()
    {
        return SQL_TAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doJudgeEnd()
    {
        for (Map.Entry<SqlEntry, SqlCountEntry> entry : this.sqlCounts_.entrySet())
        {
            SqlEntry sqlEntry = entry.getKey();
            SqlCountEntry countEntry = entry.getValue();
            Long count = countEntry.getCount();
            JavelinLogElement errorElement = countEntry.getErrorElement();
            if (errorElement == null)
            {
                continue;
            }
            String threadName = errorElement.getThreadName();
            String sql = sqlEntry.getSql();
            addError(sql + "@" + threadName, errorElement, this.threshold, count,
                     countEntry.getBindValCount(), threadName);
        }
    }

    /**
     * スレッドごとのSQL
     */
    private static class SqlEntry
    {
        /** スレッド名称 */
        private final String threadName_;

        /** SQL の内容 */
        private final String sql_;

        /**
         * コンストラクタ。
         * @param threadName
         * @param sql
         */
        public SqlEntry(final String threadName, final String sql)
        {
            this.threadName_ = threadName;
            this.sql_ = sql;
        }

        /**
         * SQLの内容を返す。
         * @return SQL文
         */
        public String getSql()
        {
            return this.sql_;
        }

        /**
         * スレッド名を返す。
         * @return スレッド名
         */
        public String getThreadName()
        {
            return this.threadName_;
        }

        @Override
        public int hashCode()
        {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((this.sql_ == null) ? 0 : this.sql_.hashCode());
            result =
                     PRIME * result
                             + ((this.threadName_ == null) ? 0 : this.threadName_.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            final SqlEntry OTHER = (SqlEntry)obj;
            if (this.sql_ == null)
            {
                if (OTHER.sql_ != null)
                {
                    return false;
                }
            }
            else if (this.sql_.equals(OTHER.sql_) == false)
            {
                return false;
            }
            if (this.threadName_ == null)
            {
                if (OTHER.threadName_ != null)
                {
                    return false;
                }
            }
            else if (this.threadName_.equals(OTHER.threadName_) == false)
            {
                return false;
            }
            return true;
        }
    }
}
