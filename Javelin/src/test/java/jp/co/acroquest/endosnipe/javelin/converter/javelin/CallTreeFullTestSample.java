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

/**
 * CallTreeFull イベントに関するテストのサンプル。
 *
 * @author sakamoto
 */
public class CallTreeFullTestSample
{
    /**
     * 指定されたサイズの CallTreeNode が生成されるようメソッドを繰り返し呼び出します。
     *
     * @param treeSize 生成される CallTreeNode のサイズ
     *                 （厳密には entry() も含めて +1 の CallTreeNode が生成される）
     */
    public void entry(final int treeSize)
    {
        for (int loop = 0; loop < treeSize; loop++)
        {
            internalMethod1(loop);
        }
    }

    private void internalMethod1(int value)
    {
        internalMethod2(value);
        sleep();
    }

    private long internalMethod2(int value)
    {
        sleep();
        return System.currentTimeMillis();
    }

    private void sleep()
    {
        try
        {
            Thread.sleep(1);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
}
