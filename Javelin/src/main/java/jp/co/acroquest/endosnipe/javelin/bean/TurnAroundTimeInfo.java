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
package jp.co.acroquest.endosnipe.javelin.bean;

import java.util.Collections;
import java.util.Map;


/**
 * Turn Around Time情報。
 * 
 * @author tsukano
 */
public class TurnAroundTimeInfo
{
    /** Turn Around Timeの平均値(単位：ミリ秒) */
    private final long turnAroundTime_;

    /** Turn Around Timeの最大値(単位：ミリ秒) */
    private final long turnAroundTimeMax_;

    /** Turn Around Timeの最小値(単位：ミリ秒) */
    private final long turnAroundTimeMin_;

    /** 呼び出し回数 */
    private final int callCount_;

    /** 例外発生回数 */
    private final Map<String, Integer> throwableCountMap_;
    
    /** HTTPエラー発生回数 */
    private final Map<String, Integer> httpStatusCountMap_;
    
    /** ストール検出回数 */
    private final int methodStallCount_;

    /**
     * Turn Around Time情報を設定する。
     * 
     * @param turnAroundTime Turn Around Time(平均値)
     * @param turnAroundTimeMax Turn Around Time(最大値)
     * @param turnAroundTimeMin Turn Around Time(最小値)
     * @param callCount 呼び出し回数
     * @param throwableCountMap 例外発生回数
     * @param httpStatusCountMap httpステータスエラー発生回数
     * @param methodStallCount ストール検出回数
     */
    public TurnAroundTimeInfo(final long turnAroundTime, final long turnAroundTimeMax,
            final long turnAroundTimeMin, final int callCount, final Map<String, Integer> throwableCountMap,
            final Map<String, Integer> httpStatusCountMap, final int methodStallCount)
    {
        this.turnAroundTime_ = turnAroundTime;
        this.turnAroundTimeMax_ = turnAroundTimeMax;
        this.turnAroundTimeMin_ = turnAroundTimeMin;
        this.callCount_ = callCount;
        this.throwableCountMap_ = throwableCountMap;
        this.httpStatusCountMap_ = httpStatusCountMap;
        this.methodStallCount_ = methodStallCount;
    }

    /**
     * Turn Around Time(平均値)を返す。
     * 
     * @return Turn Around Timeの平均値
     */
    public long getTurnAroundTime()
    {
        return this.turnAroundTime_;
    }

    /**
     * Turn Around Time(最大値)を返す。
     * 
     * @return Turn Around Timeの最大値
     */
    public long getTurnAroundTimeMax()
    {
        return this.turnAroundTimeMax_;
    }

    /**
     * Turn Around Timeを返す。
     * 
     * @return Turn Around Timeの最小値
     */
    public long getTurnAroundTimeMin()
    {
        return this.turnAroundTimeMin_;
    }

    /**
     * 呼び出し回数を返す。
     * 
     * @return 呼び出し回数
     */
    public int getCallCount()
    {
        return this.callCount_;
    }

    /**
     * 例外発生回数を返す。
     * 
     * @return 例外発生回数
     */
    public Map<String, Integer> getThrowableCountMap()
    {
        return Collections.unmodifiableMap(this.throwableCountMap_);
    } 
    
    /**
     * HTTPステータスエラー発生回数を返す。
     * 
     * @return HTTPステータスエラー発生回数
     */
    public Map<String, Integer> getHttpStatusCountMap()
    {
        return Collections.unmodifiableMap(this.httpStatusCountMap_);
    } 
    
    /**
     * ストール検出回数を返す。
     * 
     * @return ストール検出回数
     */
    public int getMethodStallCount()
    {
        return this.methodStallCount_;
    }
}
