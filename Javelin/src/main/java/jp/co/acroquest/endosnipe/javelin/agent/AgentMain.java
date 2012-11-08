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
package jp.co.acroquest.endosnipe.javelin.agent;

import jp.co.acroquest.endosnipe.common.util.ResourceUtil;

/**
 * Javelin Agent.<br />
 *
 * Javelin を動作させるためのダミープログラム。<br />
 * bin にある javelinAgentStart.bat / javelinAgentStart.sh を実行することで起動します。<br />
 * Ctrl+C で動作終了します。
 *
 * @author sakamoto
 */
public class AgentMain
{
    private static final long ONE_TIME_WAIT = 60 * 1000;

    /**
     * エントリポイント。
     *
     * @param args コマンドライン引数（使用しない）
     */
    public static void main(final String[] args)
    {
        AgentMain agentMain = new AgentMain();
        agentMain.start();
    }

    /**
     * エージェントを開始します。<br />
     *
     * 割り込みがない限り、スリープを継続します。
     */
    public void start()
    {
        // Javelinのバージョンを取得する
        String version = ResourceUtil.getJarVersion(getClass());

        System.out.println("ENdoSnipe Agent " + version);
        System.out.println("Copyright(C) Acroquest Technology Co.,Ltd. All Rights Reserved.");
        System.out.println("ENdoSnipe Agent started.");

        try
        {
            while (true)
            {
                Thread.sleep(ONE_TIME_WAIT);
            }
        }
        catch (InterruptedException ex)
        {
            System.out.println("Interrupted.");
        }

        System.out.println("ENdoSnipe Agent is finished.");
    }
}
