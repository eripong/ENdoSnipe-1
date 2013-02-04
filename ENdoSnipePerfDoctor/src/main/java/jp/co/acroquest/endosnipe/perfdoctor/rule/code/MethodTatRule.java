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
import jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule;

/**
 * メソッドのTATルール
 * 
 * @author tooru
 * 
 */
public class MethodTatRule extends SingleElementRule
{

    /** メソッドのTATを表す文字列 */
    private static final String DURATION          = "duration";

    /** ExtraInfoを表す文字列 */
    private static final String EXTRA_INFO        = "ExtraInfo";

    /** Callログの開始タグ */
    private static final String CALL_TAG          = JavelinConstants.MSG_CALL;

    /** 警告と判断するOR/UNION回数のデフォルト値。 */
    private static final int    DEFAULT_THRESHOLD = 5000;

    /**
     * TATの閾値。この値を超えた際に警告を生成する。
     */
    public long                 threshold         = DEFAULT_THRESHOLD;

    /**
     * CALLログ中のメソッドのTATの値を調査し、 閾値を超えていた際には警告する。
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
        boolean isCall = CALL_TAG.equals(type);

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

        // ExtraInfoの内容を表すMapを取得する。
        Map<String, String> map = JavelinLogUtil.parseDetailInfo(javelinLogElement, EXTRA_INFO);

        // メソッドのTATの値を得る。
        String durationString = map.get(DURATION);
        long duration = 0;

        duration = Long.parseLong(durationString);

        // もし検出値が閾値に達するのであれば、警告を出す。
        if (duration >= this.threshold)
        {
            addError(javelinLogElement, this.threshold, duration);
        }
    }

}
