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
package jp.co.acroquest.endosnipe.communicator;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;

/**
 * 電文処理のための基底クラスです。<br />
 * 
 * @author y-komori
 */
public abstract class AbstractTelegramListener implements TelegramListener
{
    /** ロガークラス */
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(AbstractTelegramListener.class,
                                      ENdoSnipeCommunicatorPluginProvider.INSTANCE);

    /**
     * {@inheritDoc}
     */
    public Telegram receiveTelegram(final Telegram telegram)
    {
        if (judgeTelegram(telegram) == true)
        {
            try
            {
                Telegram response = doReceiveTelegram(telegram);
                return response;
            }
            catch (Throwable ex)
            {
                LOGGER.log("WECC0103", ex, ex.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * 受信した電文を処理するかどうかを判定します。<br />
     * 判定処理をカスタマイズしたい場合は、本メソッドをサブクラスでオーバーライドしてください。
     * 
     * @param telegram 受信電文
     * @return 電文処理を行う場合、<code>true</code>。
     */
    protected boolean judgeTelegram(final Telegram telegram)
    {
        Header header = telegram.getObjHeader();
        return header.getByteRequestKind() == getByteRequestKind()
                && header.getByteTelegramKind() == getByteTelegramKind();
    }

    /**
     * 電文処理を行います。<br />
     * 本メソッドはサブクラスで実装してください。<br />
     * 
     * @param telegram 受信した電文オブジェクト
     * @return 応答電文オブジェクト。応答を行わない場合は <code>null</code>。
     */
    protected abstract Telegram doReceiveTelegram(Telegram telegram);

    /**
     * 処理する電文の要求応答種別を返します。<br />
     * 本メソッドはサブクラスで実装してください。
     * 
     * @return 処理する電文の要求応答種別
     */

    protected abstract byte getByteRequestKind();

    /**
     * 処理する電文の種別を返します。<br />
     * 本メソッドはサブクラスで実装してください。
     * 
     * @return 処理する電文の種別
     */
    protected abstract byte getByteTelegramKind();
}
