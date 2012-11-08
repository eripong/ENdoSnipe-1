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
package jp.co.acroquest.endosnipe.collector;

import jp.co.acroquest.endosnipe.collector.data.JavelinData;

/**
 * {@link JavelinData}オブジェクトのMockクラス
 * @author fujii
 *
 */
public class MockJavelinData implements JavelinData
{

    public String getDatabaseName()
    {
        return null;
    }

    public String getHost()
    {
        return null;
    }

    public String getIpAddress()
    {
        return null;
    }

    public int getPort()
    {
        return 0;
    }

    public long getTelegramId()
    {
        return 0;
    }

    public void setDatabaseName(final String databaseName)
    {
        // Do Nothing.
    }

    public void setHost(final String host)
    {
        // Do Nothing.
    }

    public void setIpAddress(final String ipAddress)
    {
        // Do Nothing.
    }

    public void setPort(final int port)
    {
        // Do Nothing.
    }

    public void setTelegramId(final long telegramId)
    {
        // Do Nothing.
    }

    public String getClientId()
    {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    public void setClientId(final String clientId)
    {
        // TODO 自動生成されたメソッド・スタブ

    }
}
