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
package jp.co.acroquest.endosnipe.perfdoctor.rule;

import java.util.Map;

import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;

/**
 * 単一の{@link JavelinLogElement}中の、 単一の値が閾値と比較することでチェックするシンプルなルールのための基底ルール
 * 以下の３つの要素を指定して使用する。
 * 
 * JavelinLogElementの種別（ID） 情報分類タグ 検証対象値の名称
 * 
 * エラー出力時の引数には「閾値」と「実測値」を指定可能
 * 
 * @author S.Kimura
 * 
 */
public abstract class AbstractSingleValueLimitRule extends SingleElementRule
{
    /** 閾値 */
    public long threshold;

    /**
     * 対象値が閾値以上だった場合はエラーを出力する 対象値は指定されたID、タグ、名称を指定して取得する
     * 
     * @param element
     *            検証対象LogElement
     */
    @Override
    protected void doJudgeElement(final JavelinLogElement element)
    {
        // 種別をチェック
        String type = element.getBaseInfo().get(JavelinLogColumnNum.ID);
        String targetType = getTargetID();

        boolean isTarget = targetType.equals(type);

        // 指定された種別以外だった場合は検証終了
        if (false == isTarget)
        {
            return;
        }

        // 情報分類タグを指定して、情報を取得
        String targetInfoTag = getTargetInfoTag();
        Map<String, String> targetInfoMap = JavelinLogUtil.parseDetailInfo(element, targetInfoTag);

        // 指定情報が取得できなかった場合は検証終了
        if (targetInfoMap == null)
        {
            return;
        }

        // 検証対象値を取得
        String targetValueName = getTargetValueName();
        String targetValueStr = targetInfoMap.get(targetValueName);

        // 取得できなかった場合は検証終了
        if (targetValueStr == null)
        {
            return;
        }

        long targetValueLong = Long.parseLong(targetValueStr);

        if (targetValueLong >= this.threshold)
        {
            addError(element, this.threshold, targetValueLong);
        }
    }

    /**
     * 対象とするログ種別を返す。
     * 
     * @return 対象ログ種別
     */
    protected abstract String getTargetID();

    /**
     * 対象とする情報分類タグを取得する
     * 
     * @return 対象情報分類タグ
     */
    protected abstract String getTargetInfoTag();

    /**
     * 対象とする値の名称を取得する
     * 
     * @return 対象値名称
     */
    protected abstract String getTargetValueName();
}
