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
package jp.co.acroquest.endosnipe.communicator.accessor;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.communicator.ENdoSnipeCommunicatorPluginProvider;
import jp.co.acroquest.endosnipe.communicator.TelegramSender;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * システムリソースを取得するスレッド。
 *
 * @author Sakamoto
 */
public class SystemResourceGetter extends TimerTask implements TelegramConstants
{
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(SystemResourceGetter.class,
                                      ENdoSnipeCommunicatorPluginProvider.INSTANCE);

    /** システムリソースを取得する要求電文 */
    private final Telegram requestResourceTelegram_;

    /** 電文送信クラス。 */
    private final List<TelegramSender> senderList_;

    /** タイマー実行頻度の最短間隔（ミリ秒） */
    private long minimumInterval_;

    /** 前回に実行した時刻 */
    private long prevExecTime_;

    /** 初期化されているかどうか。 */
    private boolean isInitialized_ = false;

    /**
     * システムリソースを取得するスレッドオブジェクトを生成する。
     */
    public SystemResourceGetter()
    {
        this.senderList_ = new ArrayList<TelegramSender>();
        this.minimumInterval_ = 0L;
        this.prevExecTime_ = 0L;

        // システムリソースを取得する要求電文を作成する
        Header header = new Header();
        header.setByteTelegramKind(BYTE_TELEGRAM_KIND_RESOURCENOTIFY);
        header.setByteRequestKind(BYTE_REQUEST_KIND_REQUEST);

        
        this.requestResourceTelegram_ = new Telegram();
        this.requestResourceTelegram_.setObjHeader(header);
        this.requestResourceTelegram_.setObjBody(new Body[]{});
    }

    /**
     * 送信用オブジェクトを追加します。<br />
     * 
     * @param sender {@link TelegramSender}オブジェクト
     */
    public void addTelegramSenderList(final TelegramSender sender)
    {
        this.senderList_.add(sender);
    }

    /**
     * タイマー実行間隔の最小時間をセットする。
     *
     * @param minimumInterval 最小時間（ミリ秒）
     */
    public void setMinimumInterval(final long minimumInterval)
    {
        this.minimumInterval_ = minimumInterval;
    }

    /**
     * システムリソースを取得するループ。
     */
    @Override
    public void run()
    {
        long currentTime = System.currentTimeMillis();
        if (this.isInitialized_ == false)
        {
            Telegram initializeTelegram = null;
            Body initializeBody = ResourceNotifyAccessor.makeResourceRequestBody(ITEMNAME_INITIALIZE);
            try
            {
                initializeTelegram = this.requestResourceTelegram_.clone();
            }
            catch (CloneNotSupportedException ex)
            {
                LOGGER.log("WECC0104", ex);
                return;
            }
            initializeTelegram.addObjBody(initializeBody);
            sendTelegram(initializeTelegram);
            this.prevExecTime_ = currentTime;
            this.isInitialized_ = true;
        }

        if (currentTime - this.prevExecTime_ >= this.minimumInterval_)
        {
            sendTelegram(this.requestResourceTelegram_);
            this.prevExecTime_ = currentTime;
        }
    }

    private void sendTelegram(final Telegram telegram)
    {
        // リソース取得要求を送る
        for (TelegramSender sender : this.senderList_)
        {
            sender.sendTelegram(telegram);
        }
    }

}
