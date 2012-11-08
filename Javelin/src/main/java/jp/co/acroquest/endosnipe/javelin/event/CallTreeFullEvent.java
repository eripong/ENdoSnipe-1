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

/**
 * CallTreeが超過したときに発生するイベントです。<br />
 * 
 * @author fujii
 *
 */
public class CallTreeFullEvent extends AbstractStackTraceCheckEvent
{
    /**
     * コンストラクタ。イベント名(CallTreeFull)を設定します。<br />
     */
    public CallTreeFullEvent()
    {
        super();
        this.setName(EventConstants.NAME_CALLTREE_FULL);
        this.paramStackTrace_ = EventConstants.PARAM_CALLTREE_STACKTRACE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setStackTraceCompare(String stackTrace)
    {
        this.stackTraceToCompare_ = stackTrace;
    }
}
