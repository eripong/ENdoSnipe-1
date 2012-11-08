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
package jp.co.acroquest.endosnipe.javelin.resource.proc;

import java.util.Map;

import jp.co.acroquest.endosnipe.javelin.bean.proc.DiskStats;
import jp.co.acroquest.endosnipe.javelin.bean.proc.MemInfo;
import jp.co.acroquest.endosnipe.javelin.bean.proc.ProcInfo;
import jp.co.acroquest.endosnipe.javelin.bean.proc.SelfStatInfo;
import jp.co.acroquest.endosnipe.javelin.bean.proc.StatInfo;
import jp.co.acroquest.endosnipe.javelin.resource.ProcessorCountGetter;

/**
 * Windowsのリソース情報を読み込むProcParser。
 * 
 * @author ochiai
 */
public class WindowsProcParser implements ProcParser
{

    /** 秒をナノ秒に直すための定数：1000 * 1000 * 1000 */
    private static final int SECONDS_TO_NANO_SECONDS = 1000 * 1000 * 1000;
    
    /** パーセント値を小数に直すための定数：100 */
    private static final double PERCENT_TO_DECIMAL = 100;
    
    /** 取得したリソース値 */
    private ProcInfo procInfo_;
    
    /** リソース値の取得 */
    private PerfCounter perfCounter_ = null;

    /**
     * 初期化を行う。成功した場合にのみtrue
     * 
     * @return 成功した場合にのみtrue
     */
    public boolean init()
    {
        // 準備
        this.perfCounter_ = new PerfCounter();
        return this.perfCounter_.init();        
    }
    
/**
     *      /proc/meminfo、/proc/stat、/proc/self/statから読み込み、
     *    ProcInfoに格納する。
     *    
     *    @return PocInfo
     */
    public ProcInfo load()
    {
        ProcInfo procInfo = parseStatInfo();
        this.procInfo_ = procInfo;
        return procInfo;
    }

    /**
     * /proc/statの以下の情報をStatInfoにセットし、返す。<br>
     * <ul>
     *   <li>cpu(nano秒)</li>
     *   <li>cpu0,cpu1,cpu2,・・・(nano秒)</li>
     *   <li>pgpgin(byte)</li>
     *   <li>pgpgout(byte)</li>
     * </ul>
     * @return SelfStatInfo /proc/stat,/proc/vmstatの情報
     */
    private ProcInfo parseStatInfo()
    {
        ProcInfo procInfo = new ProcInfo();
        
        MemInfo memInfo = new MemInfo();
        StatInfo statInfo = new StatInfo();
        SelfStatInfo selfStatInfo = new SelfStatInfo();
        DiskStats diskStats = new DiskStats();

        if (this.perfCounter_ == null)
        {
            // 準備
            this.perfCounter_ = new PerfCounter();
            this.perfCounter_.init();
        }
        
        Map<String, Double> perfData = this.perfCounter_.getPerfData();
        Double userobj = perfData.get(PerfCounter.PROCESSOR_TOTAL_USER_TIME);
        Double sysobj = perfData.get(PerfCounter.PROCESSOR_TOTAL_PRIVILEGED_TIME);
        Double memTotal = perfData.get(PerfCounter.MEMORY_TOTAL);
        Double memAvailable = perfData.get(PerfCounter.MEMORY_AVAILABLE_BYTES);
        Double pageUsage = perfData.get(PerfCounter.PAGING_FILE_USAGE);
        Double pageBytes = perfData.get(PerfCounter.PROCESS_TOTAL_PAGE_FILE_BYTES);
        Double pageIn = perfData.get(PerfCounter.MEMORY_PAGES_INPUT_SEC);
        Double pageOut = perfData.get(PerfCounter.MEMORY_PAGES_OUTPUT_SEC);
        Double userTime = perfData.get(PerfCounter.PROCESS_USER_TIME);
        Double privilegedTime = perfData.get(PerfCounter.PROCESS_PRIVILEGED_TIME);
        Double majFlt = perfData.get(PerfCounter.PROCESS_PAGE_FAULTS_SEC);
        Double vsize = perfData.get(PerfCounter.PROCESS_VIRTUAL_BYTES);
        Double rss = perfData.get(PerfCounter.PROCESS_WORKING_SET);
        Double numThreads = perfData.get(PerfCounter.PROCESS_THREAD_COUNT);
        Double procFDCount = perfData.get(PerfCounter.PROCESS_NUMBER_FDS);
        Double systemFDCount = perfData.get(PerfCounter.PROCESS_TOTAL_NUMBER_FDS);
        
        // 積算値を渡すために変換する
        ProcessorCountGetter procCountGetter = new ProcessorCountGetter();
        int procCount = procCountGetter.getValue().intValue();
        double interval = perfData.get(PerfCounter.INTERVAL);
        cpuTimeUser__ +=
            procCount * userobj
            / PERCENT_TO_DECIMAL * interval * SECONDS_TO_NANO_SECONDS;
        cpuTimeSys__ +=
            procCount * sysobj
            / PERCENT_TO_DECIMAL * interval * SECONDS_TO_NANO_SECONDS;
        procUTime__ += userTime / PERCENT_TO_DECIMAL * interval * SECONDS_TO_NANO_SECONDS;
        procSTime__ += privilegedTime / PERCENT_TO_DECIMAL * interval * SECONDS_TO_NANO_SECONDS;
        
        Double pageInTotal = pageIn * interval;
        Double pageOutTotal = pageOut * interval;
        Double majFltTotal = majFlt * interval;
        
        Double swapTotal = pageBytes / pageUsage * PERCENT_TO_DECIMAL;
        Double swapFree = swapTotal - pageBytes;
        
        statInfo.setCpuSystem(cpuTimeSys__);
        statInfo.setCpuUser(cpuTimeUser__);
        //statInfo.setCpuTask(cpuTask);
        statInfo.setPageIn(pageInTotal.longValue());
        statInfo.setPageOut(pageOutTotal.longValue());
        statInfo.setFdCount(systemFDCount.intValue());
        
        selfStatInfo.setMajflt(majFltTotal.longValue());
        selfStatInfo.setVsize(vsize.longValue());
        selfStatInfo.setRss(rss.longValue());
        selfStatInfo.setNumThreads(numThreads.longValue());
        selfStatInfo.setUtime(procUTime__);
        selfStatInfo.setStime(procSTime__);
        selfStatInfo.setFdCount(procFDCount.intValue());

        memInfo.setMemTotal(memTotal.longValue());
        memInfo.setMemFree(memAvailable.longValue());
        memInfo.setBufferes(0);
        memInfo.setCached(0);
        memInfo.setSwapTotal(swapTotal.longValue());
        memInfo.setSwapFree(swapFree.longValue());
        //memInfo.setVmallocTotal(0);
        
        
        procInfo.setMemInfo(memInfo);
        procInfo.setStatInfo(statInfo);
        procInfo.setSelfStatInfo(selfStatInfo);
        procInfo.setDiskStats(diskStats);

        return procInfo;
    }
    
    
    static long cpuTimeSys__ = 0;
    static long cpuTimeUser__ = 0;
    static long procUTime__ = 0;
    static long procSTime__ = 0;
    

    /**
     * リソース使用状況のデータ procInfo を返す
     * @return ProcInfo
     */
    public ProcInfo getProcInfo()
    {
        return this.procInfo_;
    }
}
