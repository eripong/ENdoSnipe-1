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

import jp.co.acroquest.endosnipe.communicator.CommunicatorListener;
import jp.co.acroquest.endosnipe.communicator.TelegramSender;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;

/**
 * DataCollector からクライアントに通知するためのリスナ。<br />
 *
 * このインタフェースを実装するインスタンスを JavelinClient に登録することで、
 * そのインスタンスにアラーム通知されます。
 *
 * @author sakamoto
 */
public interface TelegramNotifyListener extends CommunicatorListener
{
    /**
     * クライアントに通知します。
     *
     * @param telegram 通知するデータ
     */
    void receiveTelegram(Telegram telegram);

    /**
     * 電文送信オブジェクトをセットします。
     *
     * @param telegramSender 電文送信オブジェクトをセットします。
     */
    void setTelegramSender(TelegramSender telegramSender);

    /**
     * 受信した電文そのままを通知するか、
     * 変換処理を行ったデータを通知するか、のどちらかを決定します。<br />
     *
     * このメソッドが <code>true</code> を返した場合、
     * {@link #receiveTelegram(Telegram)} の引数には、
     * 受信した電文がそのまま渡されます。<br />
     * このメソッドが <code>false</code> を返した場合、
     * {@link #receiveTelegram(Telegram)} の引数には、
     * DataCollector によって変換されたデータが渡されます。<br />
     *
     * @return 受信した電文そのままを通知する場合は <code>true</code> 、
     */
    boolean isRawTelegramNeeded();
}
