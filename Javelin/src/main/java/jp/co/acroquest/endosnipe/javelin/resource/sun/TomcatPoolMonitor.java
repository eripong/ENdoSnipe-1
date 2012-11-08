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
package jp.co.acroquest.endosnipe.javelin.resource.sun;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import jp.co.acroquest.endosnipe.common.entity.ResourceItem;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;

/**
 * Tomcatのワーカスレッドの最大数と稼動数を返すクラス
 * 
 * @author fujii
 * 
 */
public class TomcatPoolMonitor
{
    /** ワーカスレッドのオブジェクト名 */
    private static final String THREAD_POOL_STR = "*:type=ThreadPool,*";

    /**
     * プライベートコンストラクタ
     */
    private TomcatPoolMonitor()
    {
        // Do Nothing.
    }

    /**
     * Tomcatのワーカスレッドの最大数と稼動数を数える。
     * @return Tomcatのワーカスレッドの最大数と稼動数のリスト
     */
    public static List<ResourceItem> getThreadCount()
    {
        // MBeanサーバを取得する。
        MBeanServer mBeanServer = getMBeanServer();

        if (mBeanServer == null)
        {
            return new ArrayList<ResourceItem>();
        }

        List<ResourceItem> list = new ArrayList<ResourceItem>();
        ObjectName queryObjectName = null;

        // ワーカスレッド数のObjectNameを取得する。
        try
        {
            queryObjectName = new ObjectName(THREAD_POOL_STR);
        }
        catch (MalformedObjectNameException ex)
        {
            SystemLogger.getInstance().warn(ex);
            return new ArrayList<ResourceItem>();
        }
        Set<?> set = mBeanServer.queryMBeans(queryObjectName, null);
        Iterator<?> iterator = set.iterator();
        List<ObjectName> threadPools = new ArrayList<ObjectName>();
        while (iterator.hasNext())
        {
            ObjectInstance oi = (ObjectInstance)iterator.next();
            threadPools.add(oi.getObjectName());
        }

        // 各ポートごとのワーカスレッドの最大数、稼動数を取得する。
        for (ObjectName objectName : threadPools)
        {
            ResourceItem maxThreadEntry = new ResourceItem();
            ResourceItem currentThreadEntry = new ResourceItem();
            ResourceItem waitThreadEntry = new ResourceItem();
            try
            {
                String name = objectName.getKeyProperty("name");

                // ワーカスレッド数の最大数を取得する。
                Number maxThreads = (Number)mBeanServer.getAttribute(objectName, "maxThreads");
                maxThreadEntry.setName(name + "_max");
                maxThreadEntry.setValue(String.valueOf(maxThreads));

                // ワーカスレッド数の稼動数を取得する。
                Number currentServerPool =
                        (Number)mBeanServer.getAttribute(objectName, "currentThreadsBusy");
                currentThreadEntry.setName(name + "_current");
                currentThreadEntry.setValue(String.valueOf(currentServerPool));

                // 待機中のワーカスレッド数を取得する。
                Number waitServerPool =
                        (Number)mBeanServer.getAttribute(objectName, "currentThreadCount");
                waitThreadEntry.setName(name + "_wait");
                waitThreadEntry.setValue(String.valueOf(waitServerPool));

                list.add(maxThreadEntry);
                list.add(currentThreadEntry);
                list.add(waitThreadEntry);

            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }
        return list;
    }

    /**
     * MBeanServerを取得する。
     * @return MBeanServer
     */
    private static synchronized MBeanServer getMBeanServer()
    {
        List<?> mbServers = MBeanServerFactory.findMBeanServer(null);
        ObjectName queryObjectName = null;
        try
        {
            queryObjectName = new ObjectName(THREAD_POOL_STR);
        }
        catch (MalformedObjectNameException ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        for (Object obj : mbServers)
        {
            MBeanServer server = (MBeanServer)obj;
            Set<?> set = server.queryMBeans(queryObjectName, null);
            if (set.size() != 0)
            {
                return server;
            }
        }
        return null;

    }
}
