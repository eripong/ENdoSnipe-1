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

/**
 * InvocationのBean
 * @author eriguchi
 *
 */
public interface InvocationMBean
{
    /**
     * クラス名を取得する。
     * @return クラス名
     */
    String getClassName();

    /**
     * メソッド名を取得する。
     * @return メソッド名
     */
    String getMethodName();

    /**
     * 呼び出し回数を取得する。
     * @return 呼び出し回数
     */
    long getCount();

    /**
     * 最小値を取得する。
     * @return 最小値
     */
    long getMinimum();

    /**
     * 最大値を取得する。
     * @return 最大値
     */
    long getMaximum();

    /**
     * 平均値を取得する。
     * @return 平均値
     */
    long getAverage();

    /**
     * CPU時間の最小値を取得する。
     * @return CPU時間の最小値
     */
    long getCpuMinimum();

    /**
     * CPU時間の最大値を取得する。
     * @return CPU時間の最大値
     */
    long getCpuMaximum();

    /**
     * CPU時間の平均値を取得する。
     * @return CPU時間の平均値
     */
    long getCpuAverage();

    /**
     * 例外発生回数を取得する。
     * @return 例外発生回数
     */
    long getThrowableCount();

    /**
     * 最終更新時刻を取得する。
     * @return 最終更新時刻
     */
    long getLastUpdatedTime();

    /**
     * アラーム発生判定のための閾値を返す。
     * @return アラーム発生判定のための閾値
     */
    long getAlarmThreshold();

    /**
     * アラーム発生判定のための閾値を返す。
     * @param alarmThreshold アラーム発生判定のための閾値
     */
    void setAlarmThreshold(long alarmThreshold);

    /**
     * リセットする。
     */
    void reset();
}
