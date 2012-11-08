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

import java.io.File;

import jp.co.acroquest.endosnipe.collector.ENdoSnipeDataCollectorPluginProvider;
import jp.co.acroquest.endosnipe.collector.JavelinDataQueue;
import jp.co.acroquest.endosnipe.collector.LogMessageCodes;
import jp.co.acroquest.endosnipe.collector.data.JavelinData;
import jp.co.acroquest.endosnipe.collector.data.JavelinLogData;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.communicator.AbstractTelegramListener;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.accessor.JvnFileNotifyAccessor;
import jp.co.acroquest.endosnipe.communicator.accessor.JvnFileNotifyAccessor.JvnFileEntry;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * Javelin ログ通知電文を受信するためのクラスです。<br />
 * 
 * @author y-komori
 * @author nagai
 */
public class JvnFileNotifyListener extends AbstractTelegramListener implements TelegramListener,
        LogMessageCodes
{
    private static final ENdoSnipeLogger LOGGER        =
                                                         ENdoSnipeLogger.getLogger(
                                                                                   JvnFileNotifyListener.class,
                                                                                   ENdoSnipeDataCollectorPluginProvider.INSTANCE);

    private final String                 tempDir_;

    private final JavelinDataQueue       queue_;

    private String                       databaseName_ = "";

    private String                       hostName_     = null;

    private String                       ipAddress_    = "";

    private int                          port_         = -1;

    private String                       clientId_     = null;

    /**
     * {@link JvnFileNotifyListener} を構築します。<br />
     * @param queue キュー
     */
    public JvnFileNotifyListener(final JavelinDataQueue queue)
    {
        this.queue_ = queue;

        String iotemp = System.getProperty("java.io.tmpdir");
        tempDir_ =
                   iotemp + System.getProperty("file.separator") + "collector"
                           + System.getProperty("file.separator");

        File tempFile = new File(tempDir_);
        if (tempFile.exists() == false)
        {
            if (tempFile.mkdir() == false)
            {
                // テンポラリディレクトリ生成に失敗した
                LOGGER.log(MAKING_DIR_FAILED, tempFile.getAbsolutePath());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Telegram doReceiveTelegram(final Telegram telegram)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.log(JVN_FILE_NOTIFY_RECEIVED);
        }

        JvnFileEntry[] entries = JvnFileNotifyAccessor.getJvnFileEntries(telegram);

        for (JvnFileEntry entry : entries)
        {
            JavelinLogData logData = createJavelinLogDataByString(entry);
            if (logData != null)
            {
                setProperties(logData, telegram.getObjHeader().getId());
                this.queue_.offer(logData);
            }
        }
        return null;
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

    private JavelinLogData createJavelinLogDataByString(final JvnFileEntry entry)
    {
        JavelinLogData javelinLogData = new JavelinLogData(entry.contents);
        javelinLogData.setLogFileName(entry.fileName);
        javelinLogData.setAlarmThreshold(entry.alarmThreshold);
        javelinLogData.setCpuAlarmThreshold(entry.cpuAlarmThreshold);
        return javelinLogData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte getByteRequestKind()
    {
        return TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte getByteTelegramKind()
    {
        return TelegramConstants.BYTE_TELEGRAM_KIND_JVN_FILE;
    }

    /**
     * {@link JavelinData} 用のデータベース名を設定します。<br />
     *
     * @param databaseName データベース名
     */
    public void setDatabaseName(final String databaseName)
    {
        this.databaseName_ = databaseName;
    }

    /**
     * {@link JavelinData} 用の接続先ホスト名を設定します。<br />
     * @param hostName 接続先ホスト名
     */
    public void setHostName(final String hostName)
    {
        this.hostName_ = hostName;
    }

    /**
     * {@link JavelinData} 用の接続先 IP アドレスを設定します。<br />
     * 
     * @param ipAddress 接続先 IP アドレス
     */
    public void setIpAddress(final String ipAddress)
    {
        this.ipAddress_ = ipAddress;
    }

    /**
     * {@link JavelinData} 用の接続先ポート番号を設定します。<br />
     * 
     * @param port 接続先ポート番号
     */
    public void setPort(final int port)
    {
        this.port_ = port;
    }

    /**
     * {@link JavelinData} 用のクライアントIDを設定します。
     * @param clientId クライアントID
     */
    public void setClientId(final String clientId)
    {
        clientId_ = clientId;
    }
}
