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
package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.Messages;
import jp.co.acroquest.endosnipe.perfdoctor.PerfConstants;
import jp.co.acroquest.endosnipe.perfdoctor.rule.AbstractRule;

/**
 * ログ内のJMX情報からスレッドに関する値を取得し、閾値をオーバーしているか 検出するルール。
 * 
 * @author fujii
 */
public abstract class AbstractExtraInfoRule extends AbstractRule
{
    /** 閾値 */
    public long threshold;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doJudge(final List<JavelinLogElement> javelinLogElementList)
    {

        for (JavelinLogElement element : javelinLogElementList)
        {
            try
            {
                // 種別をチェックする。Call以外であれば次の要素へ。
                String type = element.getBaseInfo().get(JavelinLogColumnNum.ID);

                boolean callOrReturn = JavelinConstants.MSG_CALL.equals(type);
                if (callOrReturn == false)
                {
                    continue;
                }

                // スレッド名を取得する。
                String threadName = element.getThreadName();

                // パラメータの値の取得
                long value = getParamValueLong(element);

                // パラメータの値が閾値を超えた場合にはエラーを発生させる
                if (value >= this.threshold)
                {
                    addError(element, this.threshold, value, threadName);
                }
            }
            catch (RuntimeException ex)
            {
                log(Messages.getMessage(PerfConstants.PERF_DOCTOR_RUNTIME_EXCEPTION), element, ex);
            }
        }
    }

    /**
     * パラメータの値を表すlongのインスタンスを返す。
     * 
     * @param element
     *            パラメータの検索対象のJavelinLogElement
     * @return パラメータの値
     */
    protected long getParamValueLong(final JavelinLogElement element)
    {
        long value = 0;

        Map<String, String> extraInfoMap;
        extraInfoMap = JavelinLogUtil.parseDetailInfo(element, JavelinParser.TAG_TYPE_EXTRAINFO);

        String valueStr = extraInfoMap.get(getCheckParamName());

        if (valueStr != null)
        {
            try
            {
                value = Double.valueOf(valueStr).longValue() / conversionValue();
            }
            catch (NumberFormatException exception)
            {
                log(Messages.getMessage(PerfConstants.PERF_DOCTOR_RUNTIME_EXCEPTION), element,
                    exception);
            }

        }

        return value;
    }

    /**
     * このルールでチェックすべきJMX情報内のパラメータ名を返す。
     * 
     * @return パラメータ名
     */
    protected abstract String getCheckParamName();

    /**
     * 単位変換を行う。
     * 
     * @return 変換に必要な値。
     */
    protected int conversionValue()
    {
        return 1;
    }
}
