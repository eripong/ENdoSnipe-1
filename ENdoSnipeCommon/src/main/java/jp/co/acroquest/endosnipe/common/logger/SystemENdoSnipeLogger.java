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
package jp.co.acroquest.endosnipe.common.logger;

/**
 * {@link SystemLogger} を利用して出力するための {@link ENdoSnipeLogger} です。<br />
 * 
 * @author y-komori
 */
public class SystemENdoSnipeLogger extends ENdoSnipeLogger
{
    private static SystemLogger systemLogger__;

    /**
     * トレースログインスタンスを構築します。<br />
     */
    protected SystemENdoSnipeLogger()
    {
        systemLogger__ = SystemLogger.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(final Object message, final Throwable throwable)
    {
        systemLogger__.debug(createMessage(message), throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(final Object message)
    {
        systemLogger__.debug(createMessage(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(final Object message, final Throwable throwable)
    {
        systemLogger__.error(createMessage(message), throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(final Object message)
    {
        systemLogger__.error(createMessage(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(final Object message, final Throwable throwable)
    {
        systemLogger__.fatal(createMessage(message), throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(final Object message)
    {
        systemLogger__.fatal(createMessage(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(final Object message, final Throwable throwable)
    {
        systemLogger__.info(createMessage(message), throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(final Object message)
    {
        systemLogger__.info(createMessage(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled()
    {
        return systemLogger__.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled()
    {
        return systemLogger__.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraceEnabled()
    {
        return systemLogger__.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(final Object message, final Throwable throwable)
    {
        systemLogger__.debug(createMessage(message), throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(final Object message)
    {
        systemLogger__.debug(createMessage(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(final Object message, final Throwable throwable)
    {
        systemLogger__.warn(createMessage(message), throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(final Object message)
    {
        systemLogger__.warn(createMessage(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isEnabledFor(final char messageType)
    {
        switch (messageType)
        {
        case 'T':
            return systemLogger__.isDebugEnabled();
        case 'D':
            return systemLogger__.isDebugEnabled();
        case 'I':
            return systemLogger__.isInfoEnabled();
        case 'W':
            return systemLogger__.isWarnEnabled();
        case 'E':
            return systemLogger__.isErrorEnabled();
        case 'F':
            return systemLogger__.isFatalEnabled();
        default:
            throw new IllegalArgumentException(String.valueOf(messageType));
        }
    }
}
