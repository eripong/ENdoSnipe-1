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
 * Javelin ログを表すインターフェースです。<br />
 * 
 * @author y-komori
 */
public interface JavelinData
{
    /**
     * データベース名を返します。<br />
     *
     * @return データベース名
     */
    String getDatabaseName();

    /**
     * データベース名を設定します。<br />
     *
     * @param databaseName データベース名
     */
    void setDatabaseName(String databaseName);

    /**
     * ホスト名を返します。<br />
     * 
     * @return ホスト名
     */
    String getHost();

    /**
     * ホスト名を設定します。<br />
     * 
     * @param host ホスト名
     */
    void setHost(String host);

    /**
     * IP アドレスを返します。<br />
     * 
     * @return IPアドレス
     */
    String getIpAddress();

    /**
     * IP アドレスを設定します。<br />
     * 
     * @param ipAddress IP アドレス
     */
    void setIpAddress(String ipAddress);

    /**
     * ポート番号を返します。<br />
     * 
     * @return ポート番号
     */
    int getPort();

    /**
     * ポート番号を設定します。<br />
     * 
     * @param port ポート番号
     */
    void setPort(int port);

    /**
     * クライアントIDを返します。
     * @return クライアントID
     */
    String getClientId();

    /**
     * クライアントIDを設定します。
     * @param clientId クライアントID
     */
    void setClientId(String clientId);

    /**
     * 電文 ID を返します。
     *
     * @return 電文 ID
     */
    long getTelegramId();

    /**
     * 電文 ID を設定します。
     *
     * @param telegramId 電文 ID
     */
    void setTelegramId(long telegramId);
}
