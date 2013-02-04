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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;

/**
 * SQL の発行回数をカウントするルール。
 * 
 * @author y-komori
 */
public class AllSqlCountRule extends AbstractDbAccessRule
{
    /** SQLの開始タグ */
    private static final String           SQL_TAG           = "[SQL]";

    private final Map<String, Integer>    sqlCounts_        = new HashMap<String, Integer>();

    /** 警告と判断する SQL 実行回数の閾値 */
    public int                            threshold;

    /** 発行回数を最初に超えたJavelinLogElement */
    private final List<JavelinLogElement> errorElementList_ = new ArrayList<JavelinLogElement>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doJudgeContent(final JavelinLogElement element, final String content,
            final String bindVal)
    {
        String logFileName = element.getLogFileName();

        Integer count = this.sqlCounts_.get(logFileName);
        if (count != null)
        {
            count = count + 1;
        }
        else
        {
            count = 1;
        }
        this.sqlCounts_.put(logFileName, count);
        if (count.equals(this.threshold))
        {
            this.errorElementList_.add(element);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doJudgeEnd()
    {
        for (JavelinLogElement errorElement : this.errorElementList_)
        {
            String threadName = errorElement.getThreadName();
            String logFileName = errorElement.getLogFileName();
            Integer count = this.sqlCounts_.get(logFileName);
            addError(logFileName, errorElement, this.threshold, count, threadName);
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
}
