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
package jp.co.acroquest.endosnipe.javelin.event;

import jp.co.acroquest.endosnipe.common.event.EventConstants;
import jp.co.acroquest.endosnipe.javelin.util.StatsUtil;

/**
 * 複数スレッドの同時アクセス検出イベント。
 * 
 * @author eriguchi
 */
public class ConcurrentAccessEvent extends AbstractStackTraceCheckEvent
{
    /** equals実行時に比較に用いる。 */
    private static final int EQUALS_LENGTH = 100;

    /** 比較のためのスタックトレース。 */
    private String stackTraceToCompare_;

    /**
     * コンストラクタ。
     */
    public ConcurrentAccessEvent()
    {
        super();
        this.setName(EventConstants.NAME_CONCURRENT_ACCESS);
    }

    /**
     * 値を設定する。
     * 
     * @param key キー。
     * @param value 値。
     */
    public void addParam(String key, String value)
    {
        if (key == null)
        {
            return;
        }

        String[] keyArray = key.split("\\.");
        if (keyArray.length == 2)
        {
            if (EventConstants.PARAM_CONCURRENT_IDENTIFIER.equals(keyArray[0]))
            {
                this.stackTraceToCompare_ += StatsUtil.substring(value, EQUALS_LENGTH);
            }
        }

        super.addParam(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setStackTraceCompare(String stackTrace)
    {
        // Do Nothing.
    }
}
