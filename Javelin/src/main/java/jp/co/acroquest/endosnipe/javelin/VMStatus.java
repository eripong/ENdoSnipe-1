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
package jp.co.acroquest.endosnipe.javelin;

/**
 * VMの状態を表すクラス
 * @author eriguchi
 */
public class VMStatus
{
    public static final VMStatus EMPTY_STATUS = new VMStatus();
    
    /** CPU時間 */
    private long cpuTime_;

    /** ユーザ時間 */
    private long userTime_;

    /** ブロック時間 */
    private long blockedTime_;

    /** wait時間 */
    private long waitedTime_;

    /** GC実行時間 */
    private long collectionTime_;

    /** ブロック回数 */
    private long blockedCount_;

    /** wait回数 */
    private long waitedCount_;

    /** GC回数 */
    private long collectionCount_;

    /**
     * EMPTY_STATUS生成用コンストラクタ。
     */
    public VMStatus()
    {
        
    }
    
    /**
     * コンストラクタ。
     * 
     * @param cpuTime CPU時間
     * @param userTime ユーザ時間
     * @param blockedCount ブロック回数
     * @param blockedTime ブロック時間
     * @param waitedCount ウェイト回数
     * @param waitedTime ウェイト時間
     * @param collectionCount GC実行回数
     * @param collectionTime GC実行時間
     */
    public VMStatus(
            long cpuTime, 
            long userTime, 
            long blockedCount, 
            long blockedTime, 
            long waitedCount, 
            long waitedTime, 
            long collectionCount,
            long collectionTime)
    {
        this.cpuTime_         = cpuTime;
        this.userTime_        = userTime;
        this.blockedCount_    = blockedCount;
        this.blockedTime_     = blockedTime;
        this.waitedCount_     = waitedCount;
        this.waitedTime_      = waitedTime;
        this.collectionCount_ = collectionCount;
        this.collectionTime_  = collectionTime;
    }
    
    /**
     * ブロック時間を取得する。
     * @return ブロック時間
     */
    public long getBlockedTime()
    {
        return this.blockedTime_;
    }

    /**
     * ブロック時間を設定する。
     * @param blockedTime ブロック時間
     */
//    public void setBlockedTime(final long blockedTime)
//    {
//        this.blockedTime_ = blockedTime;
//    }

    /**
     * CPU時間を取得する。
     * @return CPU時間
     */
    public long getCpuTime()
    {
        return this.cpuTime_;
    }

    /**
     * CPU時間を設定する。
     * @param cpuTime CPU時間
     */
//    public void setCpuTime(final long cpuTime)
//    {
//        this.cpuTime_ = cpuTime;
//    }

    /**
     * ユーザ時間を取得する。
     * @return ユーザ時間
     */
    public long getUserTime()
    {
        return this.userTime_;
    }

    /**
     * ユーザ時間を設定する。
     * @param userTime ユーザ時間
     */
//    public void setUserTime(final long userTime)
//    {
//        this.userTime_ = userTime;
//    }

    /**
     * wait時間を取得する。
     * @return wait時間
     */
    public long getWaitedTime()
    {
        return this.waitedTime_;
    }

    /**
     * wait時間を設定する。
     * @param waitedTime wait時間
     */
//    public void setWaitedTime(final long waitedTime)
//    {
//        this.waitedTime_ = waitedTime;
//    }

    /**
     * GC実行時間を取得する。
     * @return GC実行時間
     */
    public long getCollectionTime()
    {
        return this.collectionTime_;
    }

    /**
     * GC実行時間を設定する。
     * @param collectionTime GC実行時間
     */
//    public void setCollectionTime(final long collectionTime)
//    {
//        this.collectionTime_ = collectionTime;
//    }

    /**
     * ブロック回数を取得する。
     * @return ブロック回数
     */
    public long getBlockedCount()
    {
        return this.blockedCount_;
    }

    /**
     * ブロック回数を設定する。
     * @param blockedCount ブロック回数
     */
//    public void setBlockedCount(final long blockedCount)
//    {
//        this.blockedCount_ = blockedCount;
//    }

    /**
     * wait回数を取得する。
     * @return wait回数
     */
    public long getWaitedCount()
    {
        return this.waitedCount_;
    }

    /**
     * wait回数を設定する。
     * @param waitedCount wait回数
     */
//    public void setWaitedCount(final long waitedCount)
//    {
//        this.waitedCount_ = waitedCount;
//    }

    /**
     * GC回数を取得する。
     * @return GC回数
     */
    public long getCollectionCount()
    {
        return this.collectionCount_;
    }

    /**
     * GC回数を設定する。
     * @param collectionCount GC回数
     */
//    public void setCollectionCount(final long collectionCount)
//    {
//        this.collectionCount_ = collectionCount;
//    }
}
