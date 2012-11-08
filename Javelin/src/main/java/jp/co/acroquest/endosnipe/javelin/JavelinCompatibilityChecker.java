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

import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.entity.ResourceItem;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.resource.MultiResourceGetter;
import jp.co.acroquest.endosnipe.javelin.resource.ResourceCollector;
import jp.co.acroquest.endosnipe.javelin.resource.ResourceGetter;

/**
 * Javelinが計測に使用するAPIを実行する。
 * 
 * @author eriguchi
 */
public class JavelinCompatibilityChecker
{
    /**
     * チェックする。
     */
    public void check()
    {
        ResourceCollector collector = ResourceCollector.getInstance();

        try
        {
            collector.load();
        }
        catch (Throwable th)
        {
            SystemLogger.getInstance().warn("[Resource Get Check] load failed", th);
        }
        
        // リソース取得し、値を表示する。
        SystemLogger.getInstance().warn("[Resource Get Check] Start");
        Map<String, ResourceGetter> resourceGetterMap = collector.getResourceGetterMap();
        for (Map.Entry<String, ResourceGetter> entry : resourceGetterMap.entrySet())
        {
            String resourceName = entry.getKey();
            ResourceGetter resourceValueGetter = entry.getValue();

            try
            {
                Number value = resourceValueGetter.getValue();
                SystemLogger.getInstance().warn(
                                                "[Resource Get Check] " + resourceName + ":["
                                                        + value + "]");
            }
            catch (Throwable th)
            {
                SystemLogger.getInstance().warn("[Resource Get Check] " + resourceName + ":failed",
                                                th);
            }
        }

        Map<String, MultiResourceGetter> multiGetterMap = collector.getMultiResourceGetterMap();
        for (Map.Entry<String, MultiResourceGetter> entry : multiGetterMap.entrySet())
        {
            String resourceName = entry.getKey();
            MultiResourceGetter resourceValueGetter = entry.getValue();

            try
            {
                List<ResourceItem> values = resourceValueGetter.getValues();
                SystemLogger.getInstance().warn(
                                                "[Resource Get Check] " + resourceName + ":"
                                                        + values + "");
            }
            catch (Throwable th)
            {
                SystemLogger.getInstance().warn("[Resource Get Check] " + resourceName + ":failed",
                                                th);
            }
        }

        SystemLogger.getInstance().warn("[Resource Get Check] End");

    }

}
