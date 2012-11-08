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
package jp.co.acroquest.endosnipe.javelin.communicate;

import jp.co.acroquest.endosnipe.javelin.CallTreeNode;

/**
 * アラームを通知する処理が実装するインタフェース。
 * 
 * @author unknown
 */
public interface AlarmListener
{
    /**
     * しきい値超過のアラームを通知する際に使用する。
     * 
     * @param node しきい値超過した呼び出しの情報。
     * @param telegramId 電文 ID
     */
    void sendExceedThresholdAlarm(CallTreeNode node);

    /**
     * ルートノード(コールツリーの頂点)のみをAlarmの対象とするかどうかを指定する。
     * 
     * @return trueならばルートノードのみを対象とする。falseならば全てのAlarmを対象とする。
     */
    boolean isSendingRootOnly();

    /**
     * 初期化メソッド
     * 
     * @throws Exception 初期化中に発生した例外
     */
    void init() throws Exception;
}
