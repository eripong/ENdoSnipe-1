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
package jp.co.acroquest.endosnipe.common.logger;

import java.io.IOException;
import java.io.OutputStreamWriter;

import jp.co.dgic.testing.common.virtualmock.MockObjectManager;
import jp.co.dgic.testing.framework.DJUnitTestCase;
import jp.co.acroquest.endosnipe.common.config.JavelinConfig;

/**
 * SystemLoggerのテストケースです。<br>
 * 
 * @author iida
 */
public class SystemLoggerTest extends DJUnitTestCase
{
    /**
     * Javelinのtraceログへの書き込みが不可能である場合のメッセージ出力を確認します。<br>
     * JUnitの試験結果の他、Consoleに以下のメッセージ出力があるかどうかを確認する必要があります。<br>
     * "Javelin実行エラー出力ファイルへ書き込めなかったため、標準エラー出力を使用します。"<br>
     */
    public void testLog_IOException()
    {
        // 準備
        // OutputStreamWriter#writeでIOExceptionが発生するようにします。
        MockObjectManager.addReturnValue(OutputStreamWriter.class, "write", new IOException(""));
        JavelinConfig config = new JavelinConfig();
        SystemLogger.initSystemLog(config);
        SystemLogger logger = SystemLogger.getInstance();

        // 実施
        logger.error("");

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
}
