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
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.test.util.JavelinTestUtil;

/**
 * 計測対象自動絞込み機能のテストクラス
 * (S2JavelinBridgeConverterの一機能となるが、別のクラスに切り出して試験を行う)
 * 
 * @author M.Yoshida
 *
 */
public class AutoFilterTargetClassTest extends DJUnitTestCase
{
    /** Javelin設定ファイルのパス */
    private static final String JAVELIN_CONFIG_PATH = "/ver4_1_test/conf/javelin.properties";

    protected void setUp()
        throws Exception
    {
        JavelinTestUtil.camouflageJavelinConfig(getClass(), JAVELIN_CONFIG_PATH);
        SystemLogger.initSystemLog(new JavelinConfig());
        super.setUp();
    }

    /**
     * [試験内容]
     * バイトコードが200以上、制御コード(goto)が3以上のメソッドの実行
     * 
     * [確認内容]
     * ・preProcess、postProcessメソッドが呼ばれること。(DJUnitにより確認)
     * ・Javelinトレースログでメソッドが変換されていることを確認する。(ログより目視で確認)
     */
    public void testConvertFilter1()
    {
        try
        {
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.AutoFilterTarget");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
        
        AutoFilterTarget.largeAndComplexMethod();
        
        assertCalled(StatsJavelinRecorder.class, "preProcess");
        assertCalled(StatsJavelinRecorder.class, "postProcess");
    }
    
    /**
     * [試験内容]
     * バイトコードが200未満、制御コード(goto)が3以上のメソッドの実行
     * 
     * [確認内容]
     * ・preProcess、postProcessメソッドが呼ばれること。(DJUnitにより確認)
     * ・Javelinトレースログでメソッドが変換されていることを確認する。(ログより目視で確認)
     */
    public void testConvertFilter2()
    {
        try
        {
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.AutoFilterTarget");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
        
        AutoFilterTarget.smallAndComplexMethod();
        
        assertCalled(StatsJavelinRecorder.class, "preProcess");
        assertCalled(StatsJavelinRecorder.class, "postProcess");
    }

    /**
     * [試験内容]
     * バイトコードが200以上、制御コード(goto)が3未満のメソッドの実行
     * 
     * [確認内容]
     * ・preProcess、postProcessメソッドが呼ばれること。(DJUnitにより確認)
     * ・Javelinトレースログでメソッドが変換されていることを確認する。(ログより目視で確認)
     */
    public void testConvertFilter3()
    {
        try
        {
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.AutoFilterTarget");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
        
        AutoFilterTarget.largeAndSimpleMethod();
        
        assertCalled(StatsJavelinRecorder.class, "preProcess");
        assertCalled(StatsJavelinRecorder.class, "postProcess");
    }

    /**
     * [試験内容]
     * バイトコードが200未満、制御コード(goto)が3未満のメソッドの実行
     * 
     * [確認内容]
     * ・preProcess、postProcessメソッドが呼ばれないこと。(DJUnitにより確認)
     * ・Javelinトレースログでメソッドが変換されていることを確認する。(ログより目視で確認)
     */
    public void testConvertFilter4()
    {
        try
        {
            JavelinTestUtil.applyMonitor(
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter",
                "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.AutoFilterTarget");
        }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }
        
        assertNotCalled(StatsJavelinRecorder.class, "preProcess");
        assertNotCalled(StatsJavelinRecorder.class, "postProcess");
    }
    
}
