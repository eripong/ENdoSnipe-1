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

import java.util.List;

import junit.framework.TestCase;

public class StringUtilTest extends TestCase
{
    public void testSplit()
    {
        List<String> result = StringUtil.split("a,b", ",");
        assertEquals(2, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
    }

    public void testSplit2()
    {
        List<String> result = StringUtil.split("a,", ",");
        assertEquals(2, result.size());
        assertEquals("a", result.get(0));
        assertEquals("", result.get(1));
    }

    public void testSplit3()
    {
        List<String> result = StringUtil.split(",b", ",");
        assertEquals(2, result.size());
        assertEquals("", result.get(0));
        assertEquals("b", result.get(1));
    }

    public void testSplit4()
    {
        List<String> result = StringUtil.split(",", ",");
        assertEquals(2, result.size());
        assertEquals("", result.get(0));
        assertEquals("", result.get(1));
    }

    public void testSplit5()
    {
        List<String> result = StringUtil.split("a,b,c", ",");
        assertEquals(3, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));
    }

    public void testSplit6()
    {
        List<String> result = StringUtil.split("a,b,,c", ",");
        assertEquals(4, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("", result.get(2));
        assertEquals("c", result.get(3));
    }
}
