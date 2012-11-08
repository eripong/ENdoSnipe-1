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
package jp.co.acroquest.endosnipe.javelin.resource.proc;

import java.util.Map;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import junit.framework.TestCase;

public class PerfCounterTest extends TestCase
{
    private static final String BASE_DIR = ".";
    
    /** Javelinの設定ファイル */
    private JavelinConfig config_;
    
    /** テストでループさせる回数 */
    private static int loopCount = 5;

    /** テストでループさせる間隔（ms） */
    private static int loopInterval = 1000;

    public void setUp()
    {
        // オプションファイルから、オプション設定を読み込む。
        this.config_ = new JavelinConfig(BASE_DIR + "/lib");
        this.config_.setJavelinFileDir(BASE_DIR);
    }
    
    public void testGetPerfData()
    {
        // 準備
        PerfCounter perfCounter = new PerfCounter();
        perfCounter.init();

        // 実施
        Map<String, Double> perfData = perfCounter.getPerfData();

        // 検証
        Object processorTime = perfData.get(PerfCounter.PROCESSOR_TOTAL_PROCESSOR_TIME);
        System.out.println(processorTime);
    }

    public void testGetPerfDataLoop()
    {
        // 準備
        PerfCounter perfCounter = new PerfCounter();
        perfCounter.init();

        for (int index = 0; index < loopCount; index++)
        {
            try
            {
                Thread.sleep(loopInterval);
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            // 実施
            Map<String, Double> perfData = perfCounter.getPerfData();

            // 検証
            Object processorTime = perfData.get(PerfCounter.PROCESSOR_TOTAL_PRIVILEGED_TIME);
            Object userTime = perfData.get(PerfCounter.PROCESSOR_TOTAL_USER_TIME);
            System.out.println(processorTime);
            System.out.println(userTime);
        }
    }

}
