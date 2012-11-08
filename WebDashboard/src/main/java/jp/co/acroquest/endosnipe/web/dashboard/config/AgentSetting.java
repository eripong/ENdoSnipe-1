/* ENdoSnipe 5.0 - (https://github.com/endosnipe)
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
package jp.co.acroquest.endosnipe.web.dashboard.config;

/**
 * Javelin エージェントへの接続設定を保持するクラスです。<br />
 * 
 * @author fujii
 */
public class AgentSetting
{
    /** 接続先ポート番号のデフォルト値 */
    private static final int    DEF_PORT                              = 18000;

    /** BottleneckEye からの接続待ち受けポート番号のデフォルト値 */
    private static final int    DEF_ACCEPT_PORT                       = DEF_PORT + 10000;

    /** エージェント ID */
    public int                  agentId;

    /** データベース名 */
    public String               databaseName;

    /** 接続先ホスト名 */
    public String               hostName;

    /** 接続先ポート番号 */
    public int                  port                                  = DEF_PORT;

    /** BottleneckEye からの接続待ち受けホスト名 */
    public String               acceptHost;

    /** BottleneckEye からの接続待ち受けポート番号 */
    public int                  acceptPort                            = DEF_ACCEPT_PORT;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "Host:" + hostName + " Port:" + port;
    }

}
