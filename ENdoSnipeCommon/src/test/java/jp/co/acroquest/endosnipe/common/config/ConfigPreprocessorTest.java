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
package jp.co.acroquest.endosnipe.common.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.co.acroquest.endosnipe.common.util.ResourceUtil;
import junit.framework.TestCase;

public class ConfigPreprocessorTest extends TestCase
{
    private static final String LINE_BREAK = System.getProperty("line.separator");

    public void testProcess_import_descendant()
    {
        try
        {
            File confFile = ResourceUtil.getResourceAsFile(getClass(), "include_descendant.conf");

            InputStream stream = ConfigPreprocessor.process(confFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuilder result = new StringBuilder();
            while (true)
            {
                String line = br.readLine();
                if (line == null)
                {
                    break;
                }
                result.append(line);
                result.append(LINE_BREAK);
            }
            br.close();

            File resultFile =
                    ResourceUtil.getResourceAsFile(getClass(), "include_descendant_result.conf");
            BufferedReader br2 = new BufferedReader(new FileReader(resultFile));
            StringBuilder expected = new StringBuilder();
            while (true)
            {
                String line = br2.readLine();
                if (line == null)
                {
                    break;
                }
                expected.append(line);
                expected.append(LINE_BREAK);
            }
            br2.close();

            assertEquals(expected.toString(), result.toString());
        }
        catch (IOException ex)
        {
            fail();
        }

    }

}
