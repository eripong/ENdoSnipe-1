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
package jp.co.acroquest.endosnipe.data.entity;

/**
 * 計測対象ホスト情報アーカイブテーブルに対するエンティティクラスです。<br />
 *
 * @author y-sakamoto
 */
public class HostInfo
{

    /**
     * ホスト名を識別する ID 。<br />
     */
    public int hostId;

    /**
     * 計測対象ホスト名。<br />
     */
    public String hostName;

    /**
     * ホストの IP アドレス。<br />
     */
    public String ipAddress;

    /**
     * 接続先 Javelin の待ち受けポート番号。<br />
     *
     * 同一ホスト上で複数の Javelin が動作している場合に識別するために用います。
     */
    public int port;

    /**
     * ホストに関する説明。<br />
     *
     * 解析時にユーザが入力することを想定。
     */
    public String description;

    /**
     * {@link HostInfo} オブジェクトを生成します。<br />
     */
    public HostInfo()
    {
        this.hostId = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format("HostID:%d HostName:%s IP:%s Port:%d Desc:%s", hostId, hostName,
                             ipAddress, port, description);
    }
}
