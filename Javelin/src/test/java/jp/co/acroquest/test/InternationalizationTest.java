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
package jp.co.acroquest.test;

import java.lang.management.RuntimeMXBean;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.conf.AbstractConversionConfig;
import jp.co.acroquest.endosnipe.javelin.conf.ExcludeConversionConfig;
import jp.co.acroquest.test.util.JavelinTestUtil;
import jp.co.dgic.testing.framework.DJUnitTestCase;

/**
 * Javelinの国際化対応用テストクラス
 * @author tooru
 *
 */
public class InternationalizationTest extends DJUnitTestCase
{
    /** Javelinの設定ファイル */
    private JavelinConfig config_;

    /**
     * 初期化メソッド<br />
     * システムログの初期化を行う。
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        // オプションファイルから、オプション設定を読み込む。
        JavelinTestUtil.camouflageJavelinConfig(getClass(), "/telegram/conf/javelin.properties");
        this.config_ = new JavelinConfig();
        SystemLogger.initSystemLog(this.config_);
    }

    /**
     * 項番:
     * 4-1-5
     */
    public void testFailGetProcessID()
    {
        addReturnValue(RuntimeMXBean.class, "getName", "");
        String str1 = (String)getArgument(SystemLogger.class, "warn", 0, 0);
        assertEquals("Failed to get process ID.", str1);
    }

    // メール通知機能がDataCollectorに移ったため、
    // それを使用していた、以下の項番のテストコードを削除した。
    // 4-1-9, 16, 17, 18, 19, 20, 21, 22

    /**
     * 項番:
     * 4-1-23
     */
    public void testAnIllegalConfigurationLabel1()
    {
        AbstractConversionConfig config = new ExcludeConversionConfig();
        String str1 = "この文字列が(上書きされること無く)比較されたら失敗。";
        try
        {
            config.readConfig("");
        }
        catch (IllegalArgumentException ex)
        {
            str1 = ex.getMessage();
        }
        assertEquals("An illegal configuration().", str1);
    }

    /**
     * 項番:
     * 4-1-24
     */
    public void testAnIllegalConfigurationLabel2()
    {
        AbstractConversionConfig config = new ExcludeConversionConfig();
        addReturnValue("AbstractConversionConfig", "parseConfig", new Exception("この例外が出るはず。"));
        String str1 = "この文字列が(上書きされること無く)比較されたら失敗。";
        try
        {
            config.readConfig("<内容>");
        }
        catch (IllegalArgumentException ex)
        {
            str1 = ex.getMessage();
        }
        assertEquals("An illegal configuration(<内容>).", str1);
    }

}
