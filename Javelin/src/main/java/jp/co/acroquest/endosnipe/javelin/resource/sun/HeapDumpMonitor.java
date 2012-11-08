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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.common.util.IOUtil;

/**
 * HeapDumpをファイル出力するクラスです。<br />
 * 
 * @author fujii
 *
 */
public class HeapDumpMonitor
{
    /** Javelinの設定*/
    private static JavelinConfig config__ = new JavelinConfig();

    /** HotSpotDiagnosticMXBeanを登録しているかどうか。 */
    private static boolean isSearch__ = false;

    /** ファイルにつけるシーケンスナンバー */
    private static int sequenceNumber__ = 0;

    /** ヒープダンプファイルの拡張子 */
    private static final String EXTENTION_LOG = ".hprof";

    /** ヒープダンプファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
    private static final String HEAPDUMP_FILE_FORMAT =
            "heapdump_{0,date,yyyy_MM_dd_HHmmss_SSS}_{1,number,00000}" + EXTENTION_LOG;

    /** HeapDumpを取得するHotSpotDiagnosticMXBeanのオブジェクト名 */
    private static final String OBJECT_NAME = "com.sun.management:type=HotSpotDiagnostic";

    /** HotSpotDiagnosticオブジェクト */
    private static Object mxBeanInstance__;

    /** com.sun.management.HotSpotDiagnosticMXBean#dumpHeap　の実装メソッド */
    private static Method dumpHeapMethod__ = null;

    static
    {
        try
        {
            Class<?> hotSpotDiagnosticClass = Class.forName("sun.management.HotSpotDiagnostic");
            mxBeanInstance__ = hotSpotDiagnosticClass.newInstance();
            dumpHeapMethod__ =
                    hotSpotDiagnosticClass.getDeclaredMethod("dumpHeap", String.class,
                                                             boolean.class);
        }
        catch (ClassNotFoundException ex)
        {
            SystemLogger.getInstance().debug(ex);
        }
        catch (NoSuchMethodException ex)
        {
            SystemLogger.getInstance().debug(ex);
        }
        catch (IllegalAccessException ex)
        {
            SystemLogger.getInstance().debug(ex);
        }
        catch (InstantiationException ex)
        {
            SystemLogger.getInstance().debug(ex);
        }
    }

    /**
     * インスタンス化を阻止するプライベートコンストラクタです。<br />
     */
    private HeapDumpMonitor()
    {
        // Do Nothing.
    }

    /**
     * ヒープダンプを作成します。<br />
     * 
     */
    public static void createHeapDump()
    {
        if (dumpHeapMethod__ == null)
        {
            return;
        }
        String filePath = getDumpFilePath();
        if (isSearch__ == false)
        {
            ObjectName oName = getMBeanServer();
            if (oName == null)
            {
                SystemLogger.getInstance().warn("Not found HotSpotDiagnosticMXBean.");
                return;
            }
        }
        try
        {
            dumpHeapMethod__.invoke(mxBeanInstance__, filePath, false);
        }
        catch (UnsupportedOperationException ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        catch (IllegalAccessException ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        catch (InvocationTargetException ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }

    /**
     * 作成するヒープダンプファイルの絶対パスを取得します。<br />
     * 
     * @return
     */
    private static String getDumpFilePath()
    {
        String folderPath = config__.getHeapDumpDir();
        // 親ディレクトリを作成する。
        IOUtil.createDirs(folderPath);
        Date date = new Date();
        String fileName = MessageFormat.format(HEAPDUMP_FILE_FORMAT, date, (sequenceNumber__++));

        return folderPath + File.separator + fileName;
    }

    /**
     * MBeanServerを取得する。
     * @return MBeanServer
     */
    private static synchronized ObjectName getMBeanServer()
    {
        MBeanServer pfServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> oNames = pfServer.queryNames(null, null);
        for (ObjectName oName : oNames)
        {
            if (oName.toString().equals(OBJECT_NAME))
            {
                isSearch__ = true;
                return oName;
            }
        }
        isSearch__ = true;
        return null;
    }
}
