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

import java.util.Map;

import jp.co.acroquest.endosnipe.common.event.EventConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule;

/**
 * 同一SQLの発行回数をカウントするルール。<br />
 * 
 * Javelinからのイベントによる通知を検出する。
 * 
 * @author eriguchi
 */
public class OneSqlCountEventRule extends SingleElementRule
{
    /** 警告と判断する SQL 実行回数の閾値 */
    public int threshold;

    /**
     * コンストラクタ。
     */
    public OneSqlCountEventRule()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doJudgeElement(final JavelinLogElement element)
    {
        String type = element.getBaseInfo().get(JavelinLogColumnNum.ID);
        boolean isEvent = JavelinConstants.MSG_EVENT.equals(type);

        if (!isEvent)
        {
            return;
        }

        String eventName = element.getBaseInfo().get(JavelinLogColumnNum.EVENT_NAME);

        if (EventConstants.NAME_SQLCOUNT.equals(eventName))
        {
            Map<String, String> eventMap;
            eventMap = JavelinLogUtil.parseDetailInfo(element, JavelinParser.TAG_TYPE_EVENTINFO);
            String countStr = eventMap.get(EventConstants.PARAM_SQLCOUNT_ACTUAL);
            String thresholdStr = eventMap.get(EventConstants.PARAM_SQLCOUNT_THRESHOLD);
            String thread = element.getBaseInfo().get(JavelinLogColumnNum.EVENT_THREADID);

            int count = Integer.parseInt(countStr);
            if (this.threshold <= count)
            {
                addError(element, this.threshold, thresholdStr, count, thread);
            }
        }
    }
}
