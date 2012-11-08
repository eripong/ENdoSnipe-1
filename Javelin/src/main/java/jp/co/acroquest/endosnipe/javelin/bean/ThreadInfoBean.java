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

import java.lang.management.ThreadInfo;

/**
 * スレッド情報と記録時刻をフィールドとして持つBean
 * 
 * @author S.Kimura
 */
public class ThreadInfoBean
{
    /** 記録時刻 */
    private long recordTime_;
    
    /** スレッド情報 */
    private ThreadInfo threadInfo_;

    /**
     * コンストラクタ
     * 
     * @param recordTime 記録時刻
     * @param threadInfo スレッド情報
     */
    public ThreadInfoBean(long recordTime, ThreadInfo threadInfo)
    {
        this.recordTime_ = recordTime;
        this.threadInfo_ = threadInfo;
    }

    /**
     * @return recordTime
     */
    public long getRecordTime()
    {
        return recordTime_;
    }

    /**
     * @return threadInfo
     */
    public ThreadInfo getThreadInfo()
    {
        return threadInfo_;
    }
}
