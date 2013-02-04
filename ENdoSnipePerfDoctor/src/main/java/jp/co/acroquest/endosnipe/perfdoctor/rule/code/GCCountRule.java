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

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogConstants;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule;

/**
 * プロセスにおけるGCの実行回数が、閾値を超えたことを検出するRule。</br>
 * 
 * Performance Doctorに出力する内容</br> <li>閾値に指定した実行回数を越えた場合、閾値を出力する。
 * 
 * @author S.Kimura
 * @author fujii
 */
public class GCCountRule extends SingleElementRule
{
    private static final ENdoSnipeLogger LOGGER =
                                                  ENdoSnipeLogger.getLogger(GCCountRule.class, null);

    /** 警告と判断するGC実行頻度の閾値(単位:回数/秒) */
    public int                           threshold;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doJudgeElement(final JavelinLogElement element)
    {
        // CallログからGC実行回数差分、CPU処理時間差分を取得し、
        // 『GC実行回数差分／CPU処理時間差分』＝『GC実行頻度』を算出、閾値との比較を行う

        // 種別をチェックする。Call以外であれば次の要素へ。

        String type = element.getBaseInfo().get(JavelinLogColumnNum.ID);
        boolean isReturn = JavelinConstants.MSG_CALL.equals(type);
        if (isReturn == false)
        {
            return;
        }

        // JMX情報を取得し、さらにその中からgetCheckParamName()の名前に対応する値を取得する。
        Map<String, String> jmxInfoMap =
                                         JavelinLogUtil.parseDetailInfo(element,
                                                                        JavelinParser.TAG_TYPE_JMXINFO);

        int gcCount =
                      getIntValue(jmxInfoMap,
                                  JavelinLogConstants.JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT_DELTA);
        double gcTime =
                        getDoubleValue(jmxInfoMap,
                                       JavelinLogConstants.JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME_DELTA);

        if (gcCount >= this.threshold)
        {
            addError(element, this.threshold, gcCount, gcTime);
        }
    }

    /**
     * Mapからkeyに対応する値をint値として取得します。<br />
     * 
     * @param jmxInfoMap 検索対象Map
     * @param key 取得キー
     * @return 対応する値
     */
    protected int getIntValue(final Map<String, String> jmxInfoMap, final String key)
    {
        String valueStr = jmxInfoMap.get(key);

        int value = 0;
        if (valueStr == null)
        {
            return 0;
        }
        try
        {
            value = Integer.parseInt(valueStr);
        }
        catch (NumberFormatException ex)
        {
            LOGGER.warn(ex);
        }
        return value;
    }

    /**
     * Mapからkeyに対応する値をDouble値として取得します。<br />
     * 
     * @param jmxInfoMap 検索対象Map
     * @param key 取得キー
     * @return 対応する値
     */
    protected double getDoubleValue(final Map<String, String> jmxInfoMap, final String key)
    {
        String valueStr = jmxInfoMap.get(key);

        double value = 0;
        if (valueStr == null)
        {
            return 0.0;
        }
        try
        {
            value = Double.parseDouble(valueStr);
        }
        catch (NumberFormatException ex)
        {
            LOGGER.warn(ex);
        }
        return value;
    }
}
