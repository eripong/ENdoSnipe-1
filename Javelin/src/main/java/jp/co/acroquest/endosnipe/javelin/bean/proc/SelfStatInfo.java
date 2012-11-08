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
package jp.co.acroquest.endosnipe.javelin.bean.proc;

/**
 *Å@/proc/self/statÇÃì‡óeÇï€éùÇ∑ÇÈBean
 * 
 * @author eriguchi
 */
public class SelfStatInfo
{
    private long utime_;

    private long stime_;

    private long cutime_;

    private long cstime_;

    private long vsize_;

    private long rss_;

    private long numThreads_;

    private long majflt_;
    
    private int fdCount_;

    public long getUtime()
    {
        return utime_;
    }

    public void setUtime(long utime)
    {
        utime_ = utime;
    }

    public long getStime()
    {
        return stime_;
    }

    public void setStime(long stime)
    {
        stime_ = stime;
    }

    public long getCutime()
    {
        return cutime_;
    }

    public void setCutime(long cutime)
    {
        cutime_ = cutime;
    }

    public long getCstime()
    {
        return cstime_;
    }

    public void setCstime(long cstime)
    {
        cstime_ = cstime;
    }

    public long getVsize()
    {
        return vsize_;
    }

    public void setVsize(long vsize)
    {
        vsize_ = vsize;
    }

    public long getRss()
    {
        return rss_;
    }

    public void setRss(long rss)
    {
        rss_ = rss;
    }

    public long getNumThreads()
    {
        return numThreads_;
    }

    public void setNumThreads(long numThreads)
    {
        numThreads_ = numThreads;
    }

    public long getMajflt()
    {
        return majflt_;
    }

    public void setMajflt(long majflt)
    {
        majflt_ = majflt;
    }

    public int getFdCount()
    {
        return fdCount_;
    }

    public void setFdCount(int fdcount)
    {
        fdCount_ = fdcount;
    }

}
