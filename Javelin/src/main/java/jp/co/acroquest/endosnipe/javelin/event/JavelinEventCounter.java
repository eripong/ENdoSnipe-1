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
package jp.co.acroquest.endosnipe.javelin.event;

import java.util.HashMap;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.javelin.bean.FastInteger;

/**
 * イベント種別毎のイベント発生数を数えるクラス。<br />
 *
 * @author Sakamoto
 */
public class JavelinEventCounter implements JavelinConstants
{
    private long                             poolStorePeriod_;

    /** イベント名をキーにしたイベント発生回数のマップ */
    private Map<String, FastInteger>         eventCountMap_;

    private Map<String, FastInteger>         prevEventCountMap_;

    private long                             lastClearTime_;

    private static final JavelinEventCounter INSTANCE = new JavelinEventCounter();

    /**
     * コンストラクタを隠蔽します。<br />
     */
    private JavelinEventCounter()
    {
        this.eventCountMap_ = new HashMap<String, FastInteger>();
        this.prevEventCountMap_ = new HashMap<String, FastInteger>();
        this.lastClearTime_ = System.currentTimeMillis();
        JavelinConfig config = new JavelinConfig();
        this.poolStorePeriod_ = config.getTatKeepTime();
    }

    /**
     * このクラスのインスタンスを返します。<br />
     *
     * @return インスタンス
     */
    public static JavelinEventCounter getInstance()
    {
        return INSTANCE;
    }

    /**
     * イベント蓄積期間をセットします。<br />
     *
     * イベント追加時に、すでにこの値を超えてイベントを蓄積されていた場合、
     * 蓄積したイベント発生数をクリアします。<br />
     *
     * @param period 期間（ミリ秒）
     */
    public void setPoolStorePeriod(final long period)
    {
        this.poolStorePeriod_ = period;
    }

    /**
     * イベントを追加します。<br />
     *
     * 前回プールをクリアした時刻からイベント蓄積期間が過ぎている場合は、
     * プールをクリアした後にイベントを追加します。<br />
     *
     * @param event Javelin イベント
     */
    public synchronized void addEvent(final CommonEvent event)
    {
        clearOldEvents();

        FastInteger count = this.eventCountMap_.get(event.getName());
        if (count == null)
        {
            count = new FastInteger();
            this.eventCountMap_.put(event.getName(), count);
        }
        count.increment();
    }

    /**
     * イベント種別毎のイベント発生数を取得します。<br />
     *
     * 取得後、イベント発生数はクリアされます。<br />
     *
     * @return イベント発生数のマップ
     */
    public synchronized Map<String, FastInteger> takeEventCount()
    {
        clearOldEvents();

        Map<String, FastInteger> eventCountMapCopy = new HashMap<String, FastInteger>(this.eventCountMap_);
        addZeroCount(eventCountMapCopy);
        this.prevEventCountMap_ = this.eventCountMap_;
        this.eventCountMap_ = new HashMap<String, FastInteger>();
        this.lastClearTime_ = System.currentTimeMillis();
        return eventCountMapCopy;
    }

    /**
     * 前回発生していたイベントのうち、今回は発生しなかったイベントの発生数を <code>0</code> にします。<br />
     *
     * イベントが発生しなかった場合はクライアント側に発生数を通知しませんが、
     * 前回イベントが発生していた場合、 <code>0</code> を追加することにより、
     * グラフ表示で <code>0</code> を表現できるようになります。<br />
     *
     * @param currentCount 現在の発生数
     */
    private void addZeroCount(Map<String, FastInteger> currentCount)
    {
        for (Map.Entry<String, FastInteger> entry : this.prevEventCountMap_.entrySet())
        {
            if (!currentCount.containsKey(entry.getKey()) && entry.getValue().getValue() != 0)
            {
                // 発生数 0 を追加する
                currentCount.put(entry.getKey(), new FastInteger());
            }
        }
    }

    /**
     * イベント蓄積期間を超えたイベントをクリアします。<br />
     */
    private void clearOldEvents()
    {
        long nowTime = System.currentTimeMillis();
        if (nowTime > this.lastClearTime_ + this.poolStorePeriod_)
        {
            this.eventCountMap_.clear();
            this.lastClearTime_ = nowTime;
        }
    }
}
