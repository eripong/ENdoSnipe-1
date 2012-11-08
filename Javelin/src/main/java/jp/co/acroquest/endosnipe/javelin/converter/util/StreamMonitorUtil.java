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
package jp.co.acroquest.endosnipe.javelin.converter.util;

import jp.co.acroquest.endosnipe.javelin.CallTree;
import jp.co.acroquest.endosnipe.javelin.CallTreeRecorder;
import jp.co.acroquest.endosnipe.javelin.SystemStatusManager;

/**
 * ストリームの受信量／送信量監視用のユーティリティクラス
 * 
 * @author kimura
 *
 */
public class StreamMonitorUtil
{
    /**
     * コンストラクタ
     */
    private StreamMonitorUtil()
    {
        // Do Nothing.
    }

    /**
     * 蓄積対象の識別子と加算量を指定し、累算する。
     * 累算対象はスレッド内での値と、プロセス全体としての値の２つ。
     * 
     * @param recordAmount          加算される値
     * @param recordTargetThreadKey スレッドの累算値に加算する際の識別子
     * @param recordTargetKey       プロセス全体の累算値に加算する際の識別子
     */
    public static void recordStreamAmount(final long recordAmount,
            final String recordTargetThreadKey, final String recordTargetKey)
    {
        CallTree tree = CallTreeRecorder.getInstance().getCallTree();

        long oldSize;
        long newSize;
        Object value;

        oldSize = 0;
        value = tree.getLoggingValue(recordTargetThreadKey);
        if (value != null)
        {
            oldSize = (Long)value;
        }
        newSize = oldSize + recordAmount;
        tree.setLoggingValue(recordTargetThreadKey, newSize);

        synchronized (SystemStatusManager.class)
        {
            oldSize = 0;
            value = SystemStatusManager.getValue(recordTargetKey);
            if (value != null)
            {
                oldSize = (Long)value;
            }
            newSize = oldSize + recordAmount;
            SystemStatusManager.setValue(recordTargetKey, newSize);
        }
    }
}
