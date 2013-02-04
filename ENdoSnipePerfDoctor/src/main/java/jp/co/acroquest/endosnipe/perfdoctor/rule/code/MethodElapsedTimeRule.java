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
import jp.co.acroquest.endosnipe.common.parser.JavelinLogConstants;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule;

/**
 * メソッドの実処理時間ルール
 * 
 * @author Sakamoto
 * 
 */
public class MethodElapsedTimeRule extends SingleElementRule
{

    /** 警告と判断する閾値のデフォルト値。 */
    private static final int DEFAULT_THRESHOLD = 5000;

    /**
     * 実処理時間の閾値。この値に達した際に警告を生成する。
     */
    public long              threshold         = DEFAULT_THRESHOLD;

    /**
     * CALLログ中のメソッドの実処理時間の値を調査し、 閾値に達するのであれば警告する。
     * 
     * @param javelinLogElement
     *            ログの要素
     * 
     */
    @Override
    public void doJudgeElement(final JavelinLogElement javelinLogElement)
    {
        // ログの種別をチェックする。Call以外であれば次の要素へ。
        List<String> baseInfo = javelinLogElement.getBaseInfo();
        String type = baseInfo.get(JavelinLogColumnNum.ID);
        boolean isCall = JavelinConstants.MSG_CALL.equals(type);

        if (isCall == false)
        {
            return;
        }

        // SQL実行は除外する
        String className = baseInfo.get(JavelinLogColumnNum.CALL_CALLEE_CLASS);
        if (isSqlExec(className) == true)
        {
            return;
        }

        // メソッドの実処理時間の値を得る。
        Map<String, String> extraInfo =
                                        JavelinLogUtil.parseDetailInfo(javelinLogElement,
                                                                       JavelinParser.TAG_TYPE_EXTRAINFO);
        String durationString = extraInfo.get(JavelinLogConstants.EXTRAPARAM_ELAPSEDTIME);

        // 実処理時間が無ければ次の要素に進む
        if (durationString == null)
        {
            return;
        }

        double duration = Double.parseDouble(durationString);

        // もし検出値が閾値に達するのであれば、警告を出す。
        if (duration >= this.threshold)
        {
            addError(javelinLogElement, this.threshold, duration);
        }
    }

}
