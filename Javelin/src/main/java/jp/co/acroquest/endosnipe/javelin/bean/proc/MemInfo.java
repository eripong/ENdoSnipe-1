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
 *Å@/proc/meminfoÇÃì‡óeÇï€éùÇ∑ÇÈBean
 * 
 * @author eriguchi
 */
public class MemInfo
{
    private long memTotal_;
    private long memFree_;
    private long bufferes_;
    private long cached_;
    private long swapTotal_;
    private long swapFree_;
    private long vmallocTotal_;
    
    public long getMemTotal()
    {
        return memTotal_;
    }
    public void setMemTotal(long memTotal)
    {
        memTotal_ = memTotal;
    }
    public long getMemFree()
    {
        return memFree_;
    }
    public void setMemFree(long memFree)
    {
        memFree_ = memFree;
    }
    public long getBufferes()
    {
        return bufferes_;
    }
    public void setBufferes(long bufferes)
    {
        bufferes_ = bufferes;
    }
    public long getCached()
    {
        return cached_;
    }
    public void setCached(long cached)
    {
        cached_ = cached;
    }
    public long getSwapTotal()
    {
        return swapTotal_;
    }
    public void setSwapTotal(long swapTotal)
    {
        swapTotal_ = swapTotal;
    }
    public long getSwapFree()
    {
        return swapFree_;
    }
    public void setSwapFree(long swapFree)
    {
        swapFree_ = swapFree;
    }
    public long getVmallocTotal()
    {
        return vmallocTotal_;
    }
    public void setVmallocTotal(long vmallocTotal)
    {
        vmallocTotal_ = vmallocTotal;
    }
    
}
