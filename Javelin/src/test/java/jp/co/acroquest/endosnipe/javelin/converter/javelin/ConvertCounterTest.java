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
package jp.co.acroquest.endosnipe.javelin.converter.javelin;

import jp.co.dgic.testing.framework.DJUnitTestCase;
import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.converter.util.ConvertedMethodCounter;
import jp.co.acroquest.test.util.JavelinTestUtil;

public class ConvertCounterTest extends DJUnitTestCase
{
    /** Javelin設定ファイルのパス */
    private static final String JAVELIN_CONFIG_PATH = "/ver4_1_test/conf/javelin.properties";

    protected void setUp()
        throws Exception
    {
        super.setUp();
        JavelinTestUtil.camouflageJavelinConfig(getClass(), JAVELIN_CONFIG_PATH);
        SystemLogger.initSystemLog(new JavelinConfig());
    }
    
    public void testConvertCount1()
    {
        JavelinTestUtil.camouflageJavelinConfig("javelin.bytecode.exclude.length", Integer.valueOf(0));
        JavelinTestUtil.camouflageJavelinConfig("javelin.bytecode.exclude.controlCount", Integer.valueOf(0));
        JavelinTestUtil.camouflageJavelinConfig("javelin.bytecode.exclude.policy", Integer.valueOf(0));

        resetCounter();
        try
        {
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.model.ConvertTargetA");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(1, ConvertedMethodCounter.getConvertedCount());
        assertEquals(0, ConvertedMethodCounter.getExcludedCount());
    }
    
    public void testConvertCount2()
    {
        resetCounter();
        try
        {
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.model.ConvertTargetB");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(0, ConvertedMethodCounter.getConvertedCount());
        assertEquals(1, ConvertedMethodCounter.getExcludedCount());
    }
    
    public void testConvertCount3()
    {
        resetCounter();
        try
        {
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.model.ConvertTargetC");
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.model.ConvertTargetD");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(1, ConvertedMethodCounter.getConvertedCount());
        assertEquals(1, ConvertedMethodCounter.getExcludedCount());
    }
    
    public void testConvertCount4()
    {
        JavelinTestUtil.camouflageJavelinConfig("javelin.bytecode.exclude.length", Integer.valueOf(0));
        JavelinTestUtil.camouflageJavelinConfig("javelin.bytecode.exclude.controlCount", Integer.valueOf(0));
        JavelinTestUtil.camouflageJavelinConfig("javelin.bytecode.exclude.policy", Integer.valueOf(0));

        resetCounter();
        try
        {
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.model.ConvertTargetE");
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.model.ConvertTargetF");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(2, ConvertedMethodCounter.getConvertedCount());
        assertEquals(0, ConvertedMethodCounter.getExcludedCount());
    }

    public void testConvertCount5()
    {
        resetCounter();
        try
        {
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.model.ConvertTargetG");
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.model.ConvertTargetH");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(0, ConvertedMethodCounter.getConvertedCount());
        assertEquals(2, ConvertedMethodCounter.getExcludedCount());
    }
    
    
    private void resetCounter()
    {
        JavelinTestUtil.setNonAccessibleField(
            ConvertedMethodCounter.class,
            "convertedMethodCount__",
            null,
            0);
        JavelinTestUtil.setNonAccessibleField(
            ConvertedMethodCounter.class,
            "excludedMethodCount__",
            null,
            0);
    }
}
