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
package jp.co.acroquest.endosnipe.common.util;

import junit.framework.TestCase;

/**
 * {@link PathUtil} のためのテストクラスです。<br />
 * 
 * @author y-komori
 */
public class PathUtilTest extends TestCase
{
    public void testConvertPath()
    {
        assertEquals("jp/co/acroquest/endosnipe/common/util/test",
                     PathUtil.convertPath(getClass(), "test"));
        assertNull(PathUtil.convertPath(null, "test"));
        assertNull(PathUtil.convertPath(getClass(), null));
    }

    public void testIsRelativePath()
    {
        if (OSUtil.isWindows())
        {
            assertFalse("1", PathUtil.isRelativePath("C:\\test\\test.txt"));
            assertFalse("2", PathUtil.isRelativePath("d:\\test\\test.txt"));
            assertFalse("3", PathUtil.isRelativePath("\\\\servername\\path"));
            assertTrue("4", PathUtil.isRelativePath(".\\conf"));
            assertTrue("5", PathUtil.isRelativePath("..\\conf"));
            assertTrue("6", PathUtil.isRelativePath("conf"));
            assertFalse("7", PathUtil.isRelativePath("C:/test/test.txt"));
            assertFalse("8", PathUtil.isRelativePath("d:/test/test.txt"));
            assertFalse("9", PathUtil.isRelativePath("//servername/path"));
            assertTrue("10", PathUtil.isRelativePath("./conf"));
            assertTrue("11", PathUtil.isRelativePath("../conf"));
        }
    }
}
