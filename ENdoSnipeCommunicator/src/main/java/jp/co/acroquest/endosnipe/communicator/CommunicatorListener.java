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
package jp.co.acroquest.endosnipe.communicator;


/**
 * {@link AbstractCommunicator} の状態変化を受信するためのインターフェースです。<br />
 * 
 * @author matsuoka
 */
public interface CommunicatorListener
{
    /**
     * クライアントがサーバへ接続したときに呼び出されるメソッドです。<br />
     *
     * @param hostName ホスト名（ <code>null</code> の可能性あり）
     * @param ipAddress IP アドレス
     * @param port ポート番号
     */
    void clientConnected(String hostName, String ipAddress, int port);

    /**
     * クライアントがサーバから切断したときに呼び出されるメソッドです。<br />
     *
     * @param forceDisconnected 強制切断された場合は <code>true</code>
     */
    void clientDisconnected(boolean forceDisconnected);
}
