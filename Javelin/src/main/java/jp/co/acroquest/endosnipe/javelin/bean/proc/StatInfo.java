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
 *Å@/proc/statÇÃì‡óeÇï€éùÇ∑ÇÈBean
 * 
 * @author eriguchi
 */
public class StatInfo
{
    private long   cpuUser_;

    private long   cpuSystem_;

    private long   cpuTask_;

    private long[] cpuArray_;

    private long   pageIn_;

    private long   pageOut_;

    private long   fdCount_;

    private long cpuIoWait_;
    
    public long getCpuUser()
    {
        return cpuUser_;
    }

    public void setCpuUser(long cpuUser)
    {
        cpuUser_ = cpuUser;
    }

    public long getCpuSystem()
    {
        return cpuSystem_;
    }

    public void setCpuSystem(long cpuSystem)
    {
        cpuSystem_ = cpuSystem;
    }

    public long getCpuTask()
    {
        return cpuTask_;
    }

    public void setCpuTask(long cpuTask)
    {
        cpuTask_ = cpuTask;
    }

    public long[] getCpuArray()
    {
        return cpuArray_;
    }

    public void setCpuArray(long[] cpuArray)
    {
        cpuArray_ = cpuArray;
    }

    public long getPageIn()
    {
        return pageIn_;
    }

    public void setPageIn(long pageIn)
    {
        pageIn_ = pageIn;
    }

    public long getPageOut()
    {
        return pageOut_;
    }

    public void setPageOut(long pageOut)
    {
        pageOut_ = pageOut;
    }

    public long getFdCount()
    {
        return fdCount_;
    }

    public void setFdCount(long fdCount)
    {
        fdCount_ = fdCount;
    }

    public void setCpuIoWait(long cpuIoWait)
    {
        this.cpuIoWait_ = cpuIoWait;
    }

    public long getCpuIoWait()
    {
        return cpuIoWait_;
    }

}
