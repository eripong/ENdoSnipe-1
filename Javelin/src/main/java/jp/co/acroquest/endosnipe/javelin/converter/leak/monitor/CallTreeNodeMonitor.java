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
package jp.co.acroquest.endosnipe.javelin.converter.leak.monitor;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Javelin中で保持されているCallTreeNode数の最大値、平均値を算出するクラス
 * CallTreeの利用が終わった際にCallTreeの値を保持し、
 * リソース取得要求電文受信ごとに取得した値を送信する。
 * 
 * @author S.Kimura
 * @author sakamoto
 */
public class CallTreeNodeMonitor
{

    private static AtomicLong callTreeCount__ = new AtomicLong();

    private static AtomicLong maxNodeCount__ = new AtomicLong();

    private static AtomicLong allNodeCount__ = new AtomicLong();

    /**
     * インスタンス化防止のためのコンストラクタ
     */
    private CallTreeNodeMonitor()
    {
        // Do nothing.
    }

    /**
     * 終了したCallTreeが保持していたnodeCountを追加
     * 
     * @param nodeCount 追加するnodeCount
     */
    public static void add(long nodeCount)
    {
        synchronized (maxNodeCount__)
        {
            if (nodeCount > maxNodeCount__.get())
            {
                maxNodeCount__.set(nodeCount);
            }
        }
        allNodeCount__.addAndGet(nodeCount);
        callTreeCount__.incrementAndGet();
    }

    /**
     * CallTree保持数を取得する
     * 
     * @return CallTree保持数
     */
    public static long getCallTreeCount()
    {
        return callTreeCount__.get();
    }

    /**
     * CallTreeの最大Node保持数を取得する
     * 
     * @return 最大Node保持数
     */
    public static long getMaxNodeCount()
    {
        return maxNodeCount__.get();
    }

    /**
     * CallTreeの総Node保持数を取得する
     * 
     * @return 総Node保持数
     */
    public static long getAllNodeCount()
    {
        return allNodeCount__.get();
    }

    /**
     * 保持しているnodeCountを初期化
     */
    public static void clear()
    {
        callTreeCount__.set(0);
        maxNodeCount__.set(0);
        allNodeCount__.set(0);
    }
}
