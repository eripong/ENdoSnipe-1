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
package jp.co.acroquest.endosnipe.communicator.impl;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;

/**
 * 電文送信用スレッドです。<br />
 * @author eriguchi
 *
 */
public class JavelinClientSendRunnable implements Runnable
{
    /** {@link JavelinClientConnection}オブジェクト */
    JavelinClientConnection clientConnection_;

    /**
     * {@link JavelinClientConnection}オブジェクトを競ってします。<br />
     * 
     * @param clientConnection {@link JavelinClientConnection}オブジェクト
     */
    public JavelinClientSendRunnable(final JavelinClientConnection clientConnection)
    {
        this.clientConnection_ = clientConnection;
    }

    /**
     * Queueから電文を取得し、送信します。<br />
     * 
     */
    public void run()
    {
        try
        {
            while (true)
            {
                byte[] telegramArray = null;
                try
                {
                    telegramArray = clientConnection_.take();
                }
                catch (InterruptedException ex)
                {
                    if (!clientConnection_.isClosed())
                    {
                        SystemLogger.getInstance().warn(ex);
                        continue;
                    }
                }

                if (telegramArray == null || telegramArray.length == 0)
                {
                    // 切断し、スレッドを終了する。
                    this.clientConnection_.close();
                    return;
                }

                this.clientConnection_.send(telegramArray);
            }
        }
        catch (Throwable th)
        {
            String key = "javelin.communicate.commonMessage.sendTelegramError";
            String message = CommunicatorMessages.getMessage(key);
            SystemLogger.getInstance().warn(message, th);
            this.clientConnection_.close();
        }
    }
}
