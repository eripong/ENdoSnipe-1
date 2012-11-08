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
package jp.co.acroquest.endosnipe.collector.rotate;

import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.collector.config.RotateConfig;

/**
 * ログローテート用スレッド
 * 
 * @author S.Kimura
 */
public class LogRotator implements Runnable
{
    /** ログローテートを行う間隔(1日に2回)*/
    private static final int   ROTATE_INTERVAL = 300 * 1000;

    /** ログローテート用の設定リスト */
    private List<RotateConfig> configList_;

    /** 起動しているか */
    private volatile boolean   isRunning_;

    public void run()
    {
        init();

        List<LogRotateTask> taskList = new ArrayList<LogRotateTask>();
        synchronized (this.configList_)
        {
            for (RotateConfig config : this.configList_)
            {
                taskList.add(new JavelinLogRotateTask(config));
                taskList.add(new MeasureLogRotateTask(config));
            }
        }

        while (this.isRunning_)
        {
            for (LogRotateTask task : taskList)
            {
                task.rotate();
            }

            sleep();
        }
    }

    /**
     * 初期化します。<br />
     */
    protected void init()
    {
        this.isRunning_ = true;
    }

    /**
     * スレッドを停止します。<br />
     * 
     */
    public void stop()
    {
        this.isRunning_ = false;
    }

    /**
     * ローテートの間隔分、スリープ
     */
    private void sleep()
    {
        try
        {
            Thread.sleep(ROTATE_INTERVAL);
        }
        catch (InterruptedException ex)
        {
            // Do Nothing.
        }
    }

    /**
     * @param configList セットする configList
     */
    public void setConfig(final List<RotateConfig> configList)
    {
        this.configList_ = configList;
    }

}
