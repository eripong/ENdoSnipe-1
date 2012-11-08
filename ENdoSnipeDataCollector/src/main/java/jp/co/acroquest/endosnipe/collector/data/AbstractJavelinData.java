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
package jp.co.acroquest.endosnipe.collector.data;

/**
 * {@link JavelinData} のための基底クラスです。<br />
 * 
 * @author y-komori
 */
public abstract class AbstractJavelinData implements JavelinData
{
    private String databaseName_ = "";

    private String host_         = "";

    private String ipAddress_    = "";

    private int    port_         = -1;

    private String clientId_     = "";

    private long   telegramId_   = 0;

    /**
     * {@inheritDoc}
     */
    public String getDatabaseName()
    {
        return this.databaseName_;
    }

    /**
     * {@inheritDoc}
     */
    public String getHost()
    {
        return host_;
    }

    /**
     * {@inheritDoc}
     */
    public String getIpAddress()
    {
        return ipAddress_;
    }

    /**
     * {@inheritDoc}
     */
    public int getPort()
    {
        return port_;
    }

    /**
     * {@inheritDoc}
     */
    public String getClientId()
    {
        return clientId_;
    }

    /**
     * {@inheritDoc}
     */
    public long getTelegramId()
    {
        return this.telegramId_;
    }

    /**
     * {@inheritDoc}
     */
    public void setDatabaseName(final String databaseName)
    {
        this.databaseName_ = databaseName;
    }

    /**
     * {@inheritDoc}
     */
    public void setHost(final String host)
    {
        this.host_ = host;
    }

    /**
     * {@inheritDoc}
     */
    public void setIpAddress(final String ipAddress)
    {
        this.ipAddress_ = ipAddress;
    }

    /**
     * {@inheritDoc}
     */
    public void setPort(final int port)
    {
        this.port_ = port;
    }

    /**
     * {@inheritDoc}
     */
    public void setClientId(final String clientId)
    {
        this.clientId_ = clientId;
    }

    /**
     * {@inheritDoc}
     */
    public void setTelegramId(final long telegramId)
    {
        this.telegramId_ = telegramId;
    }

    /**
     * ログ出力用の詳細情報を返します。<br />
     * サブクラスで任意に実装してください。
     * 
     * @return 詳細情報
     */
    protected String getAdditionalString()
    {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final int BUFFER = 128;
        StringBuilder builder = new StringBuilder(BUFFER);
        builder.append(getClass().getSimpleName());
        builder.append(" Host:");
        builder.append(host_);
        builder.append(" IpAddr:");
        builder.append(ipAddress_);
        builder.append(" Port:");
        if (port_ != -1)
        {
            builder.append(port_);
        }
        else
        {
            builder.append("");
        }
        builder.append(" ClientId:");
        builder.append(clientId_);
        builder.append(" telegramId:");
        builder.append(telegramId_);
        builder.append(" ");
        builder.append(getAdditionalString());
        return builder.toString();
    }
}
