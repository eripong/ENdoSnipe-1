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
package jp.co.acroquest.endosnipe.javelin.resource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxCpuArrayGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxCpuIoWaitGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxCpuSystemGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxCpuTimeIoWaitGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxCpuTimeSysGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxCpuTimeTotalGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxCpuTotalGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxMajfltGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxMemFreeGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxMemTotalGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxNumThreadsGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxPageInGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxPageOutGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxProcFileInputGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxProcFileOutputGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxProcParser;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxRSSGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxSwapFreeGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxSwapTotalGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxSystemFileInputGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxSystemFileOutputGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.LinuxVSizeGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.ProcFdCountGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.ProcParser;
import jp.co.acroquest.endosnipe.javelin.resource.proc.SolarisProcParser;
import jp.co.acroquest.endosnipe.javelin.resource.proc.SysFdCountGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.WindowsProcParser;
import jp.co.acroquest.endosnipe.javelin.resource.sun.CpuTimeGetter;
import jp.co.acroquest.endosnipe.javelin.resource.sun.PhysicalMemoryCapacityGetter;
import jp.co.acroquest.endosnipe.javelin.resource.sun.PhysicalMemoryFreeGetter;
import jp.co.acroquest.endosnipe.javelin.resource.sun.SwapSpaceCapacityGetter;
import jp.co.acroquest.endosnipe.javelin.resource.sun.SwapSpaceFreeGetter;
import jp.co.acroquest.endosnipe.javelin.resource.sun.VirutalMemorySizeGetter;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import jp.co.acroquest.endosnipe.javelin.util.HashMap;

/**
 * リソース情報を収集するクラス。
 * 
 * getMultiResourcegetterMap、getResourceGroupGetterListは、
 * リファクタし、そのまま公開しない様にするべきですが、未対応です。
 * 
 * @author eriguchi
 * @author ochiai
 */
public class ResourceCollector implements TelegramConstants
{
    /** ベンダーがIBMであることを表す文字列 */
    public static final String                    VENDER_IBM = "IBM";

    /** ベンダーがBEAであることを表す文字列 */
    public static final String                    VENDER_BEA = "BEA";

    /** OSがLinuxであることを表す文字列 */
    private static final String                    OS_LINUX   = "Linux";

    /** ベンダーがOracleであることを表す文字列 */
    public static final String                     VENDER_ORACLE = "Oracle";

    /** OSがWindowsであることを表す文字列 */
    private static final String                    OS_WINDOWS   = "Windows";

    /** OSがSolarisであることを表す文字列 */
    private static final String                    OS_SOLARIS   = "SunOS";

    /** リソース取得オブジェクトのマップ */
    private final Map<String, ResourceGetter>      resourceGetterMap_;

    /** リソース取得オブジェクトのマップ */
    private final Map<String, MultiResourceGetter> multiResourceGetterMap_;

    /** グループ化された複数リソース取得オブジェクトのリスト */
    private final List<ResourceGroupGetter>        resourceGroupGetterList_;

    private static ResourceCollector               instance__ = new ResourceCollector();
    
    /** Windows または Linux でリソース情報を取得するために用いる */
    private ProcParser procParser_ = null;

    private ResourceCollector()
    {
        Map<String, ResourceGetter> resourceMap = new HashMap<String, ResourceGetter>();
        Map<String, MultiResourceGetter> multiResourceMap =
                                                            new HashMap<String, MultiResourceGetter>();

        try
        {
            this.procParser_ = createProcParser();
            SystemLogger.getInstance().info("ProcParser not found. Default parser selected.");
            setResouceGetters(resourceMap, multiResourceMap, this.procParser_);
        }
        catch (Throwable th)
        {
            SystemLogger.getInstance().warn(th);
        }

        this.resourceGroupGetterList_ = new ArrayList<ResourceGroupGetter>();
        this.resourceGroupGetterList_.add(new TurnAroundTimeGroupGetter());

        this.resourceGetterMap_ = resourceMap;
        this.multiResourceGetterMap_ = multiResourceMap;

    }

    /**
     * {@link ProcParser} インスタンスを生成します。
     *
     * @return 成功した場合はインスタンス、対応していないOSの場合は <code>null</code>
     */
    public static ProcParser createProcParser()
    {
        ProcParser procParser = null;
        if (System.getProperty("os.name").contains(OS_LINUX))
        {
            procParser = new LinuxProcParser();
        }
        else if (System.getProperty("os.name").contains(OS_WINDOWS))
        {
            procParser = new WindowsProcParser();
        }
        else if (System.getProperty("os.name").contains(OS_SOLARIS))
        {
            procParser = new SolarisProcParser();
        }
        if (procParser != null && procParser.init() == false)
        {
            procParser = null;
        }
        
        return procParser;
    }

    /**
     * リソース取得インスタンスをマップに登録します。
     *
     * @param resourceMap リソース取得インスタンスを登録するマップ
     * @param multiResourceMap リソース取得インスタンスを登録するマップ（可変系列用）
     * @param procParser リソース取得ベースインスタンス
     */
    public static void setResouceGetters(Map<String, ResourceGetter> resourceMap,
            Map<String, MultiResourceGetter> multiResourceMap, ProcParser procParser)
    {
        String vendor = System.getProperty("java.vendor");
        if (vendor != null)
        {
            resourceMap.put(ITEMNAME_PROCESS_CPU_TOTAL_TIME, new CpuTimeGetter());
            resourceMap.put(ITEMNAME_PROCESS_MEMORY_PHYSICAL_MAX,
                            new PhysicalMemoryCapacityGetter());
            resourceMap.put(ITEMNAME_PROCESS_MEMORY_PHYSICAL_FREE, new PhysicalMemoryFreeGetter());
            resourceMap.put(ITEMNAME_SYSTEM_MEMORY_SWAP_MAX, new SwapSpaceCapacityGetter());
            resourceMap.put(ITEMNAME_SYSTEM_MEMORY_SWAP_FREE, new SwapSpaceFreeGetter());
            resourceMap.put(ITEMNAME_SYSTEM_MEMORY_VIRTUAL_USED, new VirutalMemorySizeGetter());
            multiResourceMap.put(ITEMNAME_SERVER_POOL, new TomcatPoolCounter());
        }

        resourceMap.put(ITEMNAME_ACQUIREDTIME, new TimeGetter());
        resourceMap.put(ITEMNAME_JAVAUPTIME, new JavaUpTimeGetter());
        resourceMap.put(ITEMNAME_SYSTEM_CPU_PROCESSOR_COUNT, new ProcessorCountGetter());
        resourceMap.put(ITEMNAME_PROCESS_MEMORY_VIRTUALMACHINE_MAX,
                        new VirtualMachineCapacityGetter());
        resourceMap.put(ITEMNAME_PROCESS_MEMORY_VIRTUALMACHINE_FREE, new VirtualMachineFreeGetter());

        resourceMap.put(ITEMNAME_JAVAPROCESS_MEMORY_HEAP_COMMIT, new HeapMemoryCommittedGetter());
        resourceMap.put(ITEMNAME_JAVAPROCESS_MEMORY_HEAP_USED, new HeapMemoryUsedGetter());
        resourceMap.put(ITEMNAME_JAVAPROCESS_MEMORY_HEAP_MAX, new HeapMemoryMaxGetter());
        resourceMap.put(ITEMNAME_JAVAPROCESS_MEMORY_NONHEAP_MAX, new NonHeapMemoryMaxGetter());
        resourceMap.put(ITEMNAME_JAVAPROCESS_MEMORY_NONHEAP_COMMIT, new NonHeapMemoryCommittedGetter());
        resourceMap.put(ITEMNAME_JAVAPROCESS_MEMORY_NONHEAP_USED, new NonHeapMemoryUsedGetter());
        resourceMap.put(ITEMNAME_JAVAPROCESS_GC_FINALIZEQUEUE_COUNT, new FinalizationCountGetter());

        resourceMap.put(ITEMNAME_JAVAPROCESS_THREAD_TOTAL_COUNT, new ThreadCountGetter());
        resourceMap.put(ITEMNAME_JAVAPROCESS_GC_TIME_TOTAL, new GCTotalTimeGetter());

        resourceMap.put(ITEMNAME_CONVERTEDMETHOD, new ConvertedMethodCountGetter());
        resourceMap.put(ITEMNAME_EXCLUDEDMETHOD, new ExcludedMethodCountGetter());

        resourceMap.put(ITEMNAME_JAVAPROCESS_CLASSLOADER_CLASS_TOTAL, new LoadedClassTotalCountGetter());
        resourceMap.put(ITEMNAME_JAVAPROCESS_CLASSLOADER_CLASS_CURRENT, new LoadedClassCountGetter());
        resourceMap.put(ITEMNAME_CALLEDMETHODCOUNT, new CalledMethodCountGetter());

        if (procParser != null)
        {
            resourceMap.put(ITEMNAME_SYSTEM_MEMORY_PHYSICAL_MAX,
                            new LinuxMemTotalGetter(procParser));
            resourceMap.put(ITEMNAME_SYSTEM_MEMORY_PHYSICAL_FREE,
                            new LinuxMemFreeGetter(procParser));
            resourceMap.put(ITEMNAME_SYSTEM_MEMORY_SWAP_MAX, new LinuxSwapTotalGetter(procParser));
            resourceMap.put(ITEMNAME_SYSTEM_MEMORY_SWAP_FREE, new LinuxSwapFreeGetter(procParser));
            resourceMap.put(ITEMNAME_SYSTEM_CPU_USERMODE_TIME, new LinuxCpuTotalGetter(procParser));
            resourceMap.put(ITEMNAME_SYSTEM_CPU_SYSTEM_TIME, new LinuxCpuSystemGetter(procParser));
            resourceMap.put(ITEMNAME_SYSTEM_CPU_IOWAIT_TIME, new LinuxCpuIoWaitGetter(procParser));
            resourceMap.put(ITEMNAME_CPU_ARRAY, new LinuxCpuArrayGetter(procParser));
            resourceMap.put(ITEMNAME_SYSTEM_MEMORY_PAGEIN_COUNT, new LinuxPageInGetter(procParser));
            resourceMap.put(ITEMNAME_SYSTEM_MEMORY_PAGEOUT_COUNT,
                            new LinuxPageOutGetter(procParser));
            resourceMap.put(ITEMNAME_PROCESS_CPU_SYSTEM_TIME, new LinuxCpuTimeSysGetter(procParser));
            resourceMap.put(ITEMNAME_PROCESS_CPU_IOWAIT_TIME,
                            new LinuxCpuTimeIoWaitGetter(procParser));
            resourceMap.put(ITEMNAME_PROCESS_CPU_TOTAL_TIME,
                            new LinuxCpuTimeTotalGetter(procParser));
            resourceMap.put(ITEMNAME_PROCESS_MEMORY_VIRTUAL_USED, new LinuxVSizeGetter(procParser));
            resourceMap.put(ITEMNAME_PROCESS_MEMORY_PHYSICAL_USED, new LinuxRSSGetter(procParser));
            resourceMap.put(ITEMNAME_PROCESS_THREAD_TOTAL_COUNT,
                            new LinuxNumThreadsGetter(procParser));
            resourceMap.put(ITEMNAME_PROCESS_MEMORY_MAJORFAULT_COUNT,
                            new LinuxMajfltGetter(procParser));
            resourceMap.put(ITEMNAME_PROCESS_HANDLE_TOTAL_NUMBER, new ProcFdCountGetter(procParser));
            resourceMap.put(ITEMNAME_SYSTEM_HANDLE_TOTAL_NUMBER, new SysFdCountGetter(procParser));

            resourceMap.put(ITEMNAME_FILEINPUTSIZEOFPROCESS, new LinuxProcFileInputGetter(procParser));
            resourceMap.put(ITEMNAME_FILEOUTPUTSIZEOFPROCESS, new LinuxProcFileOutputGetter(procParser));
            resourceMap.put(ITEMNAME_FILEINPUTSIZEOFSYSTEM, new LinuxSystemFileInputGetter(procParser));
            resourceMap.put(ITEMNAME_FILEOUTPUTSIZEOFSYSTEM, new LinuxSystemFileOutputGetter(procParser));
        }

        multiResourceMap.put(ITEMNAME_NODECOUNT, new CallTreeNodeCountGetter());
        multiResourceMap.put(ITEMNAME_EVENT_COUNT, new EventCountGetter());

    }
    
    public void addMultiResource(String itemName, MultiResourceGetter multiResourceGetter)
    {
        synchronized(this.multiResourceGetterMap_)
        {
            this.multiResourceGetterMap_.put(itemName, multiResourceGetter);
        }
    }

    public void addSingleResource(String itemName, ResourceGetter resourceGetter)
    {
        synchronized(this.resourceGetterMap_)
        {
            this.resourceGetterMap_.put(itemName, resourceGetter);
        }
    }

    /**
     * インスタンスを取得します。
     * 
     * @return インスタンス。
     */
    public static ResourceCollector getInstance()
    {
        return instance__;
    }

    /**
     * 指定したリソース情報を取得します。
     * 
     * @param itemName リソースの名称。
     * @return リソース情報。
     */
    public Number getResource(String itemName)
    {
        ResourceGetter getter = this.resourceGetterMap_.get(itemName);
        Number value = null;
        if (getter != null && getter.isEnable() == true)
        {
            try
            {
                value = getter.getValue();
            }
            catch (Throwable th)
            {
                SystemLogger.getInstance().debug(th);
            }
        }

        return value;
    }

    /**
     * リソース取得オブジェクトのマップを取得する。
     * 
     * @return リソース取得オブジェクトのマップ
     */
    public Map<String, ResourceGetter> getResourceGetterMap()
    {
        return Collections.unmodifiableMap(resourceGetterMap_);
    }

    /**
     * リソース取得オブジェクトのマップを取得します。
     * 
     * @return リソース取得オブジェクトのマップ。
     */
    public Map<String, MultiResourceGetter> getMultiResourceGetterMap()
    {
        return multiResourceGetterMap_;
    }

    /**
     * リソース取得オブジェクトのリストを取得します。
     * 
     * @return リソース取得オブジェクトのマップ。
     */
    public List<ResourceGroupGetter> getResourceGroupGetterList()
    {
        return resourceGroupGetterList_;
    }

    /**
     * 種別を取得します。
     * 
     * @param itemName リソースの名称。
     * @return 種別。
     */
    public ItemType getResourceType(String itemName)
    {
        ResourceGetter getter = this.resourceGetterMap_.get(itemName);
        if (getter != null && getter.isEnable() == true)
        {
            return getter.getItemType();
        }
        return ItemType.ITEMTYPE_UNKNOWN;
    }

    /**
     * 複数系列の種別を取得します。
     * 
     * @param itemName リソースの名称。
     * @return 種別。
     */
    public ItemType getMultiResourceType(String itemName)
    {
        MultiResourceGetter getter = this.multiResourceGetterMap_.get(itemName);
        if (getter != null)
        {
            return getter.getItemType();
        }
        return ItemType.ITEMTYPE_UNKNOWN;
    }

    /**
     * 複数系列のitemIDを取得します。
     * 
     * @return 複数系列のitemID。
     */
    public Set<String> getMultiResourceItemId()
    {
        return this.multiResourceGetterMap_.keySet();
    }

    /**
     * 系列のitemIDを取得します。
     * 
     * @return 系列のitemID。
     */
    public Set<String> getResourceItemId()
    {
        return this.resourceGetterMap_.keySet();
    }
    
    /**
     * procParser の load() メソッドを呼び出して初期化する
     */
    public void load()
    {
        if (this.procParser_ != null)
        {
            this.procParser_.load();
        }
    }
}
