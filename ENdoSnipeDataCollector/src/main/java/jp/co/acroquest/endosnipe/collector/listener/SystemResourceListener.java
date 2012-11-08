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

import jp.co.acroquest.endosnipe.collector.ENdoSnipeDataCollectorPluginProvider;
import jp.co.acroquest.endosnipe.collector.JavelinDataQueue;
import jp.co.acroquest.endosnipe.collector.LogMessageCodes;
import jp.co.acroquest.endosnipe.collector.data.JavelinData;
import jp.co.acroquest.endosnipe.collector.data.JavelinMeasurementData;
import jp.co.acroquest.endosnipe.common.entity.ResourceData;
import jp.co.acroquest.endosnipe.common.logger.CommonLogMessageCodes;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.communicator.AbstractTelegramListener;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.accessor.ResourceNotifyAccessor;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

import org.seasar.framework.log.Logger;

/**
 * リソース通知応答電文受信用のリスナクラスです。<br />
 * 受信したリソース情報をデータベース格納用のキューへ追加します。<br />
 * 
 * @author fujii
 * @author nagai
 * @author y-komori
 */
public class SystemResourceListener extends AbstractTelegramListener implements TelegramListener,
        LogMessageCodes, CommonLogMessageCodes
{
    /** ロガー */
    private static final ENdoSnipeLogger logger__;
    static
    {
        logger__ =
                   ENdoSnipeLogger.getLogger(SystemResourceListener.class,
                                             ENdoSnipeDataCollectorPluginProvider.INSTANCE);
    }

    /** データベース名。<br /> */
    private String                       databaseName_;

    /** 接続先ホスト名。<br /> */
    private String                       hostName_;

    /** 接続先ホストの IP アドレス。<br /> */
    private String                       ipAddress_;

    /** 接続先ポート番号。<br /> */
    private int                          port_;

    /** 接続先のクライアントID */
    private String                       clientId_;

    /** 受信データ格納用キュー */
    private final JavelinDataQueue       queue_;

    /**
     * {@link SystemResourceListener} を構築します。<br />
     * @param queue キュー
     */
    public SystemResourceListener(final JavelinDataQueue queue)
    {
        queue_ = queue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Telegram doReceiveTelegram(final Telegram telegram)
    {
        ResourceData resourceData =
                                    ResourceNotifyAccessor.createResourceData(telegram,
                                                                              this.databaseName_);
        resourceData.hostName = this.hostName_;
        resourceData.ipAddress = this.ipAddress_;
        resourceData.portNum = this.port_;
        resourceData.clientId = this.clientId_;

        if (logger__.isDebugEnabled() == true)
        {
            logger__.log(RESOURCE_NOTIFY_RECEIVED, resourceData.hostName, resourceData.ipAddress,
                         resourceData.portNum);
        }

        // 計測値データをキューへ格納する
        JavelinMeasurementData measurementData = new JavelinMeasurementData(resourceData);
        setProperties(measurementData, telegram.getObjHeader().getId());
        queue_.offer(measurementData);
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
        return TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY;
    }

    /**
     * データベース名をセットします。<br />
     *
     * @param databaseName データベース名
     */
    public void setDatabaseName(final String databaseName)
    {
        this.databaseName_ = databaseName;
    }

    /**
     * 接続先ホスト名をセットします。<br />
     *
     * @param hostName 接続先ホスト名
     */
    public void setHostName(final String hostName)
    {
        this.hostName_ = hostName;
    }

    /**
     * 接続先ホストの IP アドレスをセットします。<br />
     *
     * @param ipAddress IP アドレス
     */
    public void setIpAddress(final String ipAddress)
    {
        this.ipAddress_ = ipAddress;
    }

    /**
     * 接続先ポート番号をセットします。<br />
     *
     * @param port ポート番号
     */
    public void setPort(final int port)
    {
        this.port_ = port;
    }

    /**
     * 接続先のクライアントIDを設定します。
     * @param clientId クライアントID
     */
    public void setClientId(final String clientId)
    {
        clientId_ = clientId;
    }

    private void setProperties(final JavelinData javelinData, final long telegramId)
    {
        javelinData.setDatabaseName(this.databaseName_);
        javelinData.setHost(this.hostName_);
        javelinData.setIpAddress(this.ipAddress_);
        javelinData.setPort(this.port_);
        javelinData.setClientId(this.clientId_);
        javelinData.setTelegramId(telegramId);
    }
}
