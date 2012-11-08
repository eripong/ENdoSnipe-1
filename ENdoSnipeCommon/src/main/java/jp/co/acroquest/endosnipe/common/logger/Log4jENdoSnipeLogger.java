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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Log4j を使用して出力するための {@link ENdoSnipeLogger} です。<br />
 * 
 * @author y-komori
 */
public class Log4jENdoSnipeLogger extends ENdoSnipeLogger
{
    private final Log log_;

    /**
     * Logオブジェクトを構築します。<br />
     * 
     * @param clazz Class
     */
    protected Log4jENdoSnipeLogger(final Class<?> clazz)
    {
        if (clazz == null)
        {
            throw new IllegalArgumentException("clazz can't be null.");
        }
        log_ = LogFactory.getLog(clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(final Object message, final Throwable throwable)
    {
        log_.debug(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(final Object message)
    {
        log_.debug(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(final Object message, final Throwable throwable)
    {
        log_.error(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(final Object message)
    {
        log_.error(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(final Object message, final Throwable throwable)
    {
        log_.fatal(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(final Object message)
    {
        log_.fatal(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(final Object message, final Throwable throwable)
    {
        log_.info(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(final Object message)
    {
        log_.info(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled()
    {
        return log_.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled()
    {
        return log_.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraceEnabled()
    {
        return log_.isTraceEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(final Object message, final Throwable throwable)
    {
        log_.trace(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(final Object message)
    {
        log_.trace(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(final Object message, final Throwable throwable)
    {
        log_.warn(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(final Object message)
    {
        log_.warn(message);
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
            return log_.isTraceEnabled();
        case 'D':
            return log_.isDebugEnabled();
        case 'I':
            return log_.isInfoEnabled();
        case 'W':
            return log_.isWarnEnabled();
        case 'E':
            return log_.isErrorEnabled();
        case 'F':
            return log_.isFatalEnabled();
        default:
            throw new IllegalArgumentException(String.valueOf(messageType));
        }
    }

}
