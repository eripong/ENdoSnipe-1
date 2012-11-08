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
 * Invocationが超過したときに発生するイベントです。<br />
 * 
 * @author eriguchi
 */
public class InvocationFullEvent extends CommonEvent
{
    /**
     * コンストラクタ。イベント名(CallTreeFull)を設定します。<br />
     */
    public InvocationFullEvent()
    {
        super();
        this.setName(EventConstants.NAME_INVOCATION_FULL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        String className = this.getParam(EventConstants.PARAM_INVOCATION_CLASS);
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((className == null) ? 0 : className.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        InvocationFullEvent other = (InvocationFullEvent)obj;
        String className = this.getParam(EventConstants.PARAM_INVOCATION_CLASS);
        String otherClassName = other.getParam(EventConstants.PARAM_INVOCATION_CLASS);
        if (className == null)
        {
            if (otherClassName != null)
            {
                return false;
            }
        }
        else if (className.equals(otherClassName) == false)
        {
            return false;
        }
        return true;
    }

}
