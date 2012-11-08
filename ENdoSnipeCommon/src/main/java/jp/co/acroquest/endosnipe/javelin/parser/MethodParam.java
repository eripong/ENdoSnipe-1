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
package jp.co.acroquest.endosnipe.javelin.parser;

import java.util.Map;

/**
 * 純粋値を保持するメソッド情報
 *
 * @author Sakamoto
 */
public class MethodParam
{
    /** メソッド情報 */
    private JavelinLogElement javelinLogElement_;

    /** メソッド実行時間 */
    private long duration_;

    /** ログに出力されている値 */
    private Map<String, Double> originalDataMap_;

    /** メソッド純粋値 */
    private Map<String, Double> pureDataMap_;

    /** メソッド開始時刻 */
    private long startTime_;

    /** メソッド終了時刻 */
    private long endTime_;

    /**
     * メソッド１つの情報を表すオブジェクトを作成する。
     */
    public MethodParam()
    {
        //doNothing
    }

    /**
     * JavelinLogElementを取得する。
     *
     * @return JavelinLogElement
     */
    public JavelinLogElement getJavelinLogElement()
    {
        return this.javelinLogElement_;
    }

    /**
     * メソッド実行時間を取得する。
     *
     * @return メソッド実行時間
     */
    public long getDuration()
    {
        return this.duration_;
    }

    /**
     * メソッド開始時刻を取得する。
     *
     * @return メソッド開始時刻
     */
    public long getStartTime()
    {
        return this.startTime_;
    }

    /**
     * メソッド終了時刻を取得する。
     *
     * @return メソッド終了時刻
     */
    public long getEndTime()
    {
        return this.endTime_;
    }

    /**
     * ログに出力されている値を取得する。
     *
     * @return ログに出力されている値のマップ
     */
    public Map<String, Double> getOriginalDataMap()
    {
        return this.originalDataMap_;
    }

    /**
     * メソッドの純粋値を取得する。
     *
     * @return メソッドの純粋値のマップ
     */
    public Map<String, Double> getPureDataMap()
    {
        return this.pureDataMap_;
    }

    /**
     * JavelinLogElementを設定します。<br />
     * 
     * @param javelinLogElement {@link JavelinLogElement}オブジェクト
     */
    public void setJavelinLogElement(final JavelinLogElement javelinLogElement)
    {
        this.javelinLogElement_ = javelinLogElement;
    }

    /**
     * Durationを設定します。<br />
     * 
     * @param duration Duration
     */
    public void setDuration(final long duration)
    {
        this.duration_ = duration;
    }

    /**
     * Javelinログに出力された値のMapを設定します。<br />
     * 
     * @param originalDataMap Javelinログに出力された値のMap
     */
    public void setOriginalDataMap(final Map<String, Double> originalDataMap)
    {
        this.originalDataMap_ = originalDataMap;
    }

    /**
     * 純粋値を保存したMapを設定します。<br />
     * 
     * @param pureDataMap 純粋値を保存したMap
     */
    public void setPureDataMap(final Map<String, Double> pureDataMap)
    {
        this.pureDataMap_ = pureDataMap;
    }

    /**
     * 開始時刻を設定します。<br />
     * 
     * @param startTime 開始時刻
     */
    public void setStartTime(final long startTime)
    {
        this.startTime_ = startTime;
    }

    /**
     * 終了時刻を設定します。<br />
     * 
     * @param endTime 終了時刻
     */
    public void setEndTime(final long endTime)
    {
        this.endTime_ = endTime;
    }

    /**
     * 純粋値から子メソッドの値を引く。
     *
     * @param childMethod 子メソッド
     */
    public void subtractData(final MethodParam childMethod)
    {
        for (Map.Entry<String, Double> entrySet : childMethod.getOriginalDataMap().entrySet())
        {
            String key = entrySet.getKey();
            if (this.pureDataMap_.containsKey(key) == true)
            {
                double parentValue = this.pureDataMap_.get(key);
                double childValue = entrySet.getValue();
                this.pureDataMap_.put(key, parentValue - childValue);
            }
        }
    }
}
