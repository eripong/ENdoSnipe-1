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

import java.util.Map;

import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogConstants;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule;

/**
 * プロセスにおけるGCの実行時間が、閾値を超えたことを検出するRule。</br>
 * 
 * Performance Doctorに出力する内容</br> <li>閾値に指定した実行時間を越えた場合、閾値と実行頻度を出力する。
 * 
 * @author fujii
 */
public class GCTimeRule extends SingleElementRule
{
    /** 警告と判断するGC実行時間の閾値(単位:ミリ秒) */
    public double            threshold;

    /** ミリ秒から秒への単位変換定数 */
    private static final int CONVERT_TO_SEC = 1000;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doJudgeElement(final JavelinLogElement element)
    {
        // ReturnログからGC実行時間差分を取得し、閾値との比較を行う。
        // CPU処理時間差分を取得し、『GC実行回数差分／CPU処理時間差分』＝『GC実行頻度』を算出
        // 種別をチェックする。Call以外であれば次の要素へ。

        String type = element.getBaseInfo().get(JavelinLogColumnNum.ID);
        boolean isCall = JavelinConstants.MSG_CALL.equals(type);
        if (isCall == false)
        {
            return;
        }

        // JMX情報を取得し、さらにその中からgetCheckParamName()の名前に対応する値を取得する。
        Map<String, String> jmxInfoMap =
                                         JavelinLogUtil.parseDetailInfo(element,
                                                                        JavelinParser.TAG_TYPE_JMXINFO);

        double gcTime =
                        getDoubleValue(jmxInfoMap,
                                       JavelinLogConstants.JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME_DELTA);
        double gcCount =
                         getDoubleValue(jmxInfoMap,
                                        JavelinLogConstants.JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT_DELTA);

        int duration = getDuration(element);
        if (duration == 0)
        {
            return;
        }
        // 1000をかけることにより単位を「回/sec」にする
        double gcFrequency = CONVERT_TO_SEC * gcCount / duration;
        if (gcTime >= this.threshold)
        {
            addError(element, this.threshold, gcTime, gcFrequency);
        }
    }

    /**
     * durationを取得する。
     * 
     * @param element
     *            JavelinLogElement
     * @return duration
     */
    private int getDuration(final JavelinLogElement element)
    {
        // ExtraInfoの内容を表すMapを取得する。
        Map<String, String> map =
                                  JavelinLogUtil.parseDetailInfo(element,
                                                                 JavelinParser.TAG_TYPE_EXTRAINFO);

        // メソッドのTATの値を得る。
        String durationString = map.get(JavelinLogConstants.EXTRAPARAM_DURATION);
        if (durationString == null)
        {
            return 0;
        }

        int duration = Integer.parseInt(durationString);
        return duration;
    }

    /**
     * Mapからkeyに対応する値をDouble型として取得する。
     * 
     * @param jmxInfoMap
     *            検索対象Map
     * @param key
     *            取得キー
     * @return 対応する値
     */
    protected double getDoubleValue(final Map<String, String> jmxInfoMap, final String key)
    {
        String valueStr = jmxInfoMap.get(key);

        if (valueStr == null)
        {
            return 0.0;
        }
        double value = Double.parseDouble(valueStr);

        return value;
    }
}
