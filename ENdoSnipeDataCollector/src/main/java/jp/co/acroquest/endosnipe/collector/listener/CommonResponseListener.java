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
package jp.co.acroquest.endosnipe.collector.listener;

import jp.co.acroquest.endosnipe.collector.JavelinDataQueue;
import jp.co.acroquest.endosnipe.collector.data.CommonResponseData;
import jp.co.acroquest.endosnipe.communicator.AbstractTelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * 応答電文を処理するクラス。
 *
 * @author sakamoto
 */
public class CommonResponseListener extends AbstractTelegramListener
{
    private final JavelinDataQueue queue_;

    private final byte             telegramKind_;

    /**
     * 応答電文を処理するオブジェクトを生成します。
     *
     * @param queue キュー
     * @param telegramKind 処理する応答電文の種類
     */
    public CommonResponseListener(final JavelinDataQueue queue,
            final byte telegramKind)
    {
        this.queue_ = queue;
        this.telegramKind_ = telegramKind;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Telegram doReceiveTelegram(final Telegram telegram)
    {
        CommonResponseData responseData = new CommonResponseData();
        responseData.setTelegramId(telegram.getObjHeader().getId());
        this.queue_.offer(responseData);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte getByteRequestKind()
    {
        return TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte getByteTelegramKind()
    {
        return this.telegramKind_;
    }

}
