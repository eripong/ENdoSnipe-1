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
package jp.co.acroquest.endosnipe.collector.request;

import jp.co.acroquest.endosnipe.communicator.TelegramSender;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;

/**
 * Javelin 通信用オブジェクトを管理するインタフェース。
 *
 * @author sakamoto
 */
public interface CommunicationClientRepository
{
    /**
     * 指定されたクライアントIDのJavelinと紐付いている制御クライアントに
     * 電文を送信する。
     *
     * @param clientId クライアント ID
     * @param telegram 送信する電文
     */
    void sendTelegramToClient(final String clientId, final Telegram telegram);

    /**
     * 指定された ID に対応する、 Javelin 送信用オブジェクトを取得します。
     *
     * @param clientId Javelin 通信用 ID
     * @return 送信用オブジェクト（オブジェクトが存在しない場合は <code>null</code> ）
     */
    TelegramSender getTelegramSender(String clientId);
}
