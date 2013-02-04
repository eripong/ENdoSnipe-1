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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.Messages;
import jp.co.acroquest.endosnipe.perfdoctor.PerfConstants;
import jp.co.acroquest.endosnipe.perfdoctor.rule.AbstractRule;

/**
 * メソッドの呼び出し回数が閾値を超えた場合に警告を表示するルール。
 * 
 * @author tooru
 * 
 */
public class MethodCallCountRule extends AbstractRule
{
    /** 警告と判断するOR/UNION回数のデフォルト値。 */
    private static final int DEFAULT_THRESHOLD = 1000;

    /**
     * 呼び出し回数の閾値。この値を超えた際に警告を生成する。
     */
    public int               threshold         = DEFAULT_THRESHOLD;

    /**
     * クラス、メソッド、スレッドID毎に呼び出し回数を集計し、 閾値を超えた際に警告する。
     * 
     * @param javelinLogElementList
     *            ログデータ。
     */
    @Override
    public void doJudge(final List<JavelinLogElement> javelinLogElementList)
    {

        /**
         * 呼び出し回数を記録するマップ。 クラス名#メソッド名#スレッドIDをキーとし、呼び出し回数を値とする。
         */
        Map<String, Integer> callCountMap = new HashMap<String, Integer>();

        /**
         * 呼び出された回数が閾値を超えたメソッドを記録するマップ。
         * クラス名#メソッド名#スレッドIDをキーとし、閾値を超えたときのJavelinLogElementを値とする。
         */
        Map<String, JavelinLogElement> overThresholdMethodMap =
                                                                new HashMap<String, JavelinLogElement>();

        // メソッド毎に「呼び出し回数」をカウントする。
        for (JavelinLogElement javelinLogElement : javelinLogElementList)
        {
            try
            {
                doJudgeSingleElement(javelinLogElement, callCountMap, overThresholdMethodMap);
            }
            catch (RuntimeException ex)
            {
                log(Messages.getMessage(PerfConstants.PERF_DOCTOR_RUNTIME_EXCEPTION),
                    javelinLogElement, ex);
            }
        }

        // 閾値を超えたものがある場合には警告を出す。
        addErrorElements(callCountMap, overThresholdMethodMap);
    }

    /**
     * 閾値を超えたものがある場合には警告を出す。
     * 
     * @param callCountMap
     *            呼び出し回数を記録するマップ。
     * @param overThresholdMethodMap
     *            呼び出された回数が閾値を超えたメソッドを記録するマップ。
     */
    private void addErrorElements(final Map<String, Integer> callCountMap,
            final Map<String, JavelinLogElement> overThresholdMethodMap)
    {
        Set<Map.Entry<String, JavelinLogElement>> entries = overThresholdMethodMap.entrySet();

        for (Map.Entry<String, JavelinLogElement> entry : entries)
        {
            JavelinLogElement element = entry.getValue();

            String key = entry.getKey();
            int count = callCountMap.get(key);

            addError(key, element, this.threshold, count);
        }
    }

    /**
     * 各メソッドの呼び出し回数をカウントする。 閾値を超えたものがある場合には記録する。
     * 
     * @param javelinLogElement
     *            ログデータ。
     * @param callCountMap
     *            呼び出し回数を記録するマップ。
     * @param overThresholdMethodMap
     *            呼び出された回数が閾値を超えたメソッドを記録するマップ。
     */
    private void doJudgeSingleElement(final JavelinLogElement javelinLogElement,
            final Map<String, Integer> callCountMap,
            final Map<String, JavelinLogElement> overThresholdMethodMap)
    {
        List<String> baseInfo = javelinLogElement.getBaseInfo();

        // Callログのみを対象とする。
        String id = baseInfo.get(JavelinLogColumnNum.ID);
        if (JavelinConstants.MSG_CALL.equals(id) == false)
        {
            return;
        }

        // Mapのキーを作成する。
        String className = baseInfo.get(JavelinLogColumnNum.CALL_CALLEE_CLASS);
        String methodName = baseInfo.get(JavelinLogColumnNum.CALL_CALLEE_METHOD);
        String threadId = baseInfo.get(JavelinLogColumnNum.CALL_THREADID);

        // SQL実行は除外する
        if (isSqlExec(className) == true)
        {
            return;
        }

        String key = className + "#" + methodName + "#" + threadId;

        // カウントに1を加える。
        // まだ一度も実行されていない場合には新たにオブジェクトを作り、値を1にする。
        Integer count = callCountMap.get(key);
        if (count == null)
        {
            count = Integer.valueOf(1);
        }
        else
        {
            count = Integer.valueOf(count.intValue() + 1);
        }
        callCountMap.put(key, count);

        // もしカウントが閾値に達するのであれば、overThresholdMethodMap_に記録する。
        if (count.intValue() == this.threshold)
        {
            overThresholdMethodMap.put(key, javelinLogElement);
        }
    }
}
