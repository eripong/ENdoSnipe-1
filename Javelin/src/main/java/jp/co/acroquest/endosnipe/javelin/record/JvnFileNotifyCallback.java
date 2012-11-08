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
package jp.co.acroquest.endosnipe.javelin.record;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.TelegramCreator;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.javelin.communicate.JavelinAcceptThread;
import jp.co.acroquest.endosnipe.javelin.communicate.JavelinConnectThread;
import jp.co.acroquest.endosnipe.javelin.log.JavelinLogCallback;

/**
 * Javelinログ通知電文を送信するコールバック。
 * 
 * @author eriguchi
 */
public class JvnFileNotifyCallback implements JavelinLogCallback
{
    /**
     * ログ通知電文を送信する。
     * @param jvnFileName JVNログファイル名
     * @param jvnLogContent JVNログファイルの内容
     * @param telegramId 電文 ID
     */
    public void execute(final String jvnFileName, final String jvnLogContent, final long telegramId)
    {
        // クライアントがいない場合は電文を作成しない。
        if (JavelinAcceptThread.getInstance().hasClient() == false
                && JavelinConnectThread.getInstance().isConnected() == false)
        {
            return;
        }

        // 通知電文を作成する。
        Telegram telegram = null;
        try
        {
            telegram =
                       TelegramCreator.createJvnFileNotifyTelegram(jvnFileName, jvnLogContent,
                                                                   telegramId);
        }
        catch (IllegalArgumentException ex)
        {
            SystemLogger logger = SystemLogger.getInstance();
            logger.warn(ex);
        }

        if (telegram == null)
        {
            return;
        }

        if (JavelinAcceptThread.getInstance().hasClient())
        {
            JavelinAcceptThread.getInstance().sendTelegram(telegram);
        }
        else 
        {
            JavelinConnectThread.getInstance().sendTelegram(telegram);
        }
    }
}
