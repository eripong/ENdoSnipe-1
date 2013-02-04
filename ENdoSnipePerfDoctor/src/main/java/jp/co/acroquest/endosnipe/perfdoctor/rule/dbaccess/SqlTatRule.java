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

import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;

/**
 * SQLのTATを判定するルール
 * 
 * @author y-komori
 */
public class SqlTatRule extends AbstractDbAccessRule
{
    /** SQL実行時間の開始タグ */
    private static final String TIME_TAG = "[Time]";

    /** 警告と判断するSQL実行時間の閾値 */
    public long                 threshold;

    /**
     * @param element JavelinLogElement
     * @see jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule#doJudgeElement
     * (jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement)
     */
    @Override
    protected void doJudgeElement(final JavelinLogElement element)
    {
        String[] args = JavelinLogUtil.getArgs(element);
        String prevContent = null;
        String bindVal = null;
        for (int i = 0; i < args.length; i++)
        {

            if (prevContent != null)
            {
                bindVal = JavelinLogUtil.getArgContent(args[i], "[VALUE]");
                if (bindVal != null)
                {
                    doJudgeContent(element, prevContent, bindVal);
                    bindVal = null;
                }
                else if (prevContent != null)
                {
                    doJudgeContent(element, prevContent, null);
                }
            }
            String content = JavelinLogUtil.getArgContent(args[i], getTagName());
            prevContent = content;
        }

        if (prevContent != null)
        {
            doJudgeContent(element, prevContent, bindVal);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doJudgeContent(final JavelinLogElement element, final String content,
            final String bindVal)
    {
        long time = Long.parseLong(content);
        if (time >= this.threshold)
        {
            addError(element, this.threshold, time);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTagName()
    {
        return TIME_TAG;
    }
}
