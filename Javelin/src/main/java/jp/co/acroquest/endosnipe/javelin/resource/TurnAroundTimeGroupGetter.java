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

import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.entity.ResourceItem;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.javelin.RootInvocationManager;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.bean.TurnAroundTimeInfo;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import jp.co.acroquest.endosnipe.javelin.util.HashSet;
import jp.co.acroquest.endosnipe.javelin.util.LinkedHashMap;

/**
 * Turn Around Timeのグループを取得するクラス。
 * 
 * @author tsukano
 */
public class TurnAroundTimeGroupGetter implements ResourceGroupGetter, TelegramConstants
{
    private JavelinConfig config_ = new JavelinConfig();

    /**
     * {@inheritDoc}
     */
    public Set<String> getItemNameSet()
    {
        // javelin.tat.monitorの値がfalseの時には、
        // グラフの値を取得しないようにする。
        if (this.config_.isTatEnabled() == false)
        {
            return new HashSet<String>();
        }

        Set<String> retVal = new HashSet<String>();
        retVal.add(ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE);
        retVal.add(ITEMNAME_PROCESS_RESPONSE_TIME_MAX);
        retVal.add(ITEMNAME_PROCESS_RESPONSE_TIME_MIN);
        retVal.add(ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT);
        retVal.add(ITEMNAME_JAVAPROCESS_EXCEPTION_OCCURENCE_COUNT);
        retVal.add(ITEMNAME_JAVAPROCESS_STALL_OCCURENCE_COUNT);
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, MultiResourceGetter> getResourceGroup()
    {
        long currentTime = System.currentTimeMillis();
        long tatZeroKeepTime = this.config_.getTatZeroKeepTime();

        // Turn Around Time情報(平均値)
        List<ResourceItem> tatEntryList = new ArrayList<ResourceItem>();
        // Turn Around Time情報(最大値)
        List<ResourceItem> tatMaxEntryList = new ArrayList<ResourceItem>();
        // Turn Around Time情報(最小値)
        List<ResourceItem> tatMinEntryList = new ArrayList<ResourceItem>();
        // Turn Around Time呼び出し回数情報
        List<ResourceItem> tstCountEntryList = new ArrayList<ResourceItem>();
        // 例外発生回数情報
        List<ResourceItem> throwableCountEntryList = new ArrayList<ResourceItem>();

        // HTTPステータスエラー発生回数情報
        List<ResourceItem> httpStatusCountEntryList = new ArrayList<ResourceItem>();
        // ストール検出回数情報
        List<ResourceItem> methodStallCountEntryList = new ArrayList<ResourceItem>();
        
        Invocation[] invocations = RootInvocationManager.getAllRootInvocations();

        for (Invocation invocation : invocations)
        {
            boolean output = invocation.isResponseGraphOutputTarget();
            if (!output)
            {
                continue;
            }
            TurnAroundTimeInfo info = invocation.resetAccumulatedTimeCount();

            // resetAccumulatedTimeCount() 呼出し後に、呼び出し回数が 0 である期間を調べる
            long tatCallZeroValueStartTime = invocation.getTatCallZeroValueStartTime();
            if (tatCallZeroValueStartTime != Invocation.TAT_ZERO_KEEP_TIME_NULL_VALUE
                    && currentTime > tatCallZeroValueStartTime + tatZeroKeepTime)
            {
                // 呼び出し回数が 0 である時間が閾値を超えた場合は、クライアントに通知しない
                continue;
            }

            // 戻り値で返すResourceEntryを作成する
            ResourceItem tatEntry = new ResourceItem();
            ResourceItem tatMaxEntry = new ResourceItem();
            ResourceItem tatMinEntry = new ResourceItem();
            ResourceItem tatCountEntry = new ResourceItem();

            // ResourceEntryにNameを設定する。
            // スラッシュは区切り文字のため、スラッシュがある場合には文字参照に変換する
            String name = invocation.getRootInvocationManagerKey().replace("/", "&#47;");

            tatEntry.setName(ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE + "/" + name);
            tatMaxEntry.setName(ITEMNAME_PROCESS_RESPONSE_TIME_MAX + "/" + name);
            tatMinEntry.setName(ITEMNAME_PROCESS_RESPONSE_TIME_MIN + "/" + name);
            tatCountEntry.setName(ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT + "/" + name);

            // ResourceEntryにValueを設定する
            int count = info.getCallCount();
            tatCountEntry.setValue(String.valueOf(count));
            if (count == 0)
            {
                tatEntry.setValue(String.valueOf(Long.valueOf(0)));
            }
            else
            {
                tatEntry.setValue(String.valueOf(info.getTurnAroundTime() / count));
            }
            tatMaxEntry.setValue(String.valueOf(info.getTurnAroundTimeMax()));
            tatMinEntry.setValue(String.valueOf(info.getTurnAroundTimeMin()));

            // ResourceEntryをリストに設定する。
            tatEntryList.add(tatEntry);
            tatMaxEntryList.add(tatMaxEntry);
            tatMinEntryList.add(tatMinEntry);
            tstCountEntryList.add(tatCountEntry);

            //例外発生情報を設定
            Map<String, Integer> throwableCountMap = info.getThrowableCountMap();
            for(Map.Entry<String, Integer> entry: throwableCountMap.entrySet())
            {
                String throwableName = entry.getKey();
                Integer throwableCount = entry.getValue();

                ResourceItem throwableCountEntry = new ResourceItem();
                throwableCountEntry.setName(ITEMNAME_JAVAPROCESS_EXCEPTION_OCCURENCE_COUNT + "/"
                        + name + ":" + throwableName);
                throwableCountEntry.setValue(String.valueOf(throwableCount));
                throwableCountEntryList.add(throwableCountEntry);
            }
            
            //HTTPエラー発生情報を設定
            Map<String, Integer> httpStatusCountMap = info.getHttpStatusCountMap();
            for(Map.Entry<String, Integer> entry: httpStatusCountMap.entrySet())
            {
                String httpStatusName = entry.getKey();
                Integer httpStatusCount = entry.getValue();

                ResourceItem httpStatusCountEntry = new ResourceItem();
                httpStatusCountEntry.setName(ITEMNAME_JAVAPROCESS_EXCEPTION_OCCURENCE_COUNT + "/"
                        + name + ":" + httpStatusName);
                httpStatusCountEntry.setValue(String.valueOf(httpStatusCount));
                httpStatusCountEntryList.add(httpStatusCountEntry);
            }
            
            // ストール検出情報を設定
            ResourceItem methodStallCountEntry = new ResourceItem();
            methodStallCountEntry.setName(ITEMNAME_JAVAPROCESS_STALL_OCCURENCE_COUNT + "/" + name);
            methodStallCountEntry.setValue(String.valueOf(info.getMethodStallCount()));
            methodStallCountEntryList.add(methodStallCountEntry);
        }

        // 戻り値となるMultiResourceGetterのリストを設定する
        Map<String, MultiResourceGetter> retVal = new LinkedHashMap<String, MultiResourceGetter>();
        retVal.put(ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE, new TurnAroundTimeGetter(tatEntryList));
        retVal.put(ITEMNAME_PROCESS_RESPONSE_TIME_MAX, new TurnAroundTimeGetter(tatMaxEntryList));
        retVal.put(ITEMNAME_PROCESS_RESPONSE_TIME_MIN, new TurnAroundTimeGetter(tatMinEntryList));
        retVal.put(ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT,
                   new TurnAroundTimeCountGetter(tstCountEntryList));
        retVal.put(ITEMNAME_JAVAPROCESS_EXCEPTION_OCCURENCE_COUNT,
                   new TurnAroundTimeCountGetter(throwableCountEntryList));
        retVal.put(ITEMNAME_JAVAPROCESS_STALL_OCCURENCE_COUNT,
                   new TurnAroundTimeCountGetter(methodStallCountEntryList));
        return retVal;
    }
}
