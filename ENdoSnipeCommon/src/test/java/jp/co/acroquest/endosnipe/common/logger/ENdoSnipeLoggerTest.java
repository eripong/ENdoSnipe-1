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

import junit.framework.TestCase;

public class ENdoSnipeLoggerTest extends TestCase
{
    public void testLog()
    {
        ENdoSnipeLogger logger =
                ENdoSnipeLogger.getLogger(ENdoSnipeLoggerTest.class,
                                          ENdoSnipeCommonPluginProvider.INSTANCE);
        logger.error("ErrorLogTest");
    }

    /**
     * ENdoSnipeLogger#createMessage(Object)メソッドのテストケースです。<br />
     * 引数にnullを指定した場合、予期せぬエラーが発生したというメッセージが返される事を確認します。<br />
     */
    public void testCreateMessage_MessageIsNull()
    {
        // 準備
        ENdoSnipeLogger logger =
                ENdoSnipeLogger.getLogger(ENdoSnipeLoggerTest.class,
                                          ENdoSnipeCommonPluginProvider.INSTANCE);

        // 実施・検証
        try
        {
            String result = logger.createMessage(null);
            assertEquals(result, "[EECM0005]予期せぬエラーが発生しました.");
        }
        catch (Exception ex)
        {
            assertTrue(false);
        }
    }

    /**
     * ENdoSnipeLogger#createMessage(Object)メソッドのテストケースです。<br />
     * 引数に空文字を指定した場合、""が返される事を確認します。<br />
     */
    public void testCreateMessage_MessageIsBlank()
    {
        // 準備
        ENdoSnipeLogger logger =
                ENdoSnipeLogger.getLogger(ENdoSnipeLoggerTest.class,
                                          ENdoSnipeCommonPluginProvider.INSTANCE);

        // 実施・検証
        try
        {
            String result = logger.createMessage("");
            assertEquals(result, "");
        }
        catch (Exception ex)
        {
            assertTrue(false);
        }
    }

    /**
     * ENdoSnipeLogger#createMessage(Object)メソッドのテストケースです。<br />
     * 引数にString型を指定した場合、その文字列がそのまま返される事を確認します。<br />
     */
    public void testCreateMessage_MessageIsNotNull()
    {
        // 準備
        ENdoSnipeLogger logger =
                ENdoSnipeLogger.getLogger(ENdoSnipeLoggerTest.class,
                                          ENdoSnipeCommonPluginProvider.INSTANCE);

        // 実施・検証
        try
        {
            String result = logger.createMessage("String");
            assertEquals(result, "String");
        }
        catch (Exception ex)
        {
            assertTrue(false);
        }
    }

    /**
     * ENdoSnipeLogger#createMessage(Object)メソッドのテストケースです。<br />
     * 引数にObject型を指定した場合、そのtoString()の結果が返される事を確認します。<br />
     */
    public void testCreateMessage_Object()
    {
        // 準備
        ENdoSnipeLogger logger =
                ENdoSnipeLogger.getLogger(ENdoSnipeLoggerTest.class,
                                          ENdoSnipeCommonPluginProvider.INSTANCE);
        Object messageObject = new Object() {
            @Override
            public String toString()
            {
                return "Object#toString() is Called.";
            }
        };

        // 実施・検証
        try
        {
            String result = logger.createMessage(messageObject);
            assertEquals(result, "Object#toString() is Called.");
        }
        catch (Exception ex)
        {
            assertTrue(false);
        }
    }
}
