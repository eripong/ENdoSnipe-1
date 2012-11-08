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
package jp.co.acroquest.endosnipe.collector;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import jp.co.acroquest.endosnipe.collector.data.JavelinData;
import jp.co.acroquest.endosnipe.common.logger.CommonLogMessageCodes;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;

/**
 * Javelin ログをキューイングするためのキューです。<br />
 * 
 * @author y-komori
 */
public class JavelinDataQueue implements LogMessageCodes, CommonLogMessageCodes
{
    /** キューの最大要素数 */
    private static final int                 QUEUE_SIZE       = 100;

    /** キューへデータを投入する際のタイムアウト時間(ミリ秒) */
    private static final int                 OFFERING_TIMEOUT = 10000;

    /** キューからデータを取り出す際のタイムアウト時間(ミリ秒) */
    private static final int                 TAKING_TIMEOUT   = 1000;

    private static ENdoSnipeLogger           logger_          =
                                                                ENdoSnipeLogger.getLogger(
                                                                                          JavelinDataQueue.class,
                                                                                          ENdoSnipeDataCollectorPluginProvider.INSTANCE);

    private final BlockingQueue<JavelinData> queue_           =
                                                                new ArrayBlockingQueue<JavelinData>(
                                                                                                    QUEUE_SIZE);

    /**
     * キューに {@link JavelinData} を追加します。<br />
     * 
     * @param data {@link JavelinData} オブジェクト
     */
    public void offer(final JavelinData data)
    {
        if (data != null)
        {
            try
            {
                queue_.offer(data, OFFERING_TIMEOUT, TimeUnit.MILLISECONDS);
                if (logger_.isDebugEnabled())
                {
                    logger_.log(QUEUE_OFFERED, data.toString());
                }
            }
            catch (InterruptedException ex)
            {
                logger_.log(EXCEPTION_OCCURED_WITH_RESASON, ex, ex.getMessage());
            }
        }
    }

    /**
     * キューから {@link JavelinData} を取り出します。<br />
     * 
     * キューにデータが存在しない場合、スレッドはブロックします。<br />
     * 
     * @return {@link JavelinData} オブジェクト
     */
    public JavelinData take()
    {
        try
        {
            JavelinData data = queue_.poll(TAKING_TIMEOUT, TimeUnit.MILLISECONDS);
            if (logger_.isDebugEnabled() && data != null)
            {
                logger_.log(QUEUE_TAKEN, data.toString());
            }
            return data;
        }
        catch (InterruptedException ex)
        {
            logger_.log(EXCEPTION_OCCURED_WITH_RESASON, ex, ex.getMessage());
            return null;
        }
    }

    /**
     * キューに残っているデータの数を返します。<br />
     * 
     * @return データの数
     */
    public int size()
    {
        return queue_.size();
    }
}
