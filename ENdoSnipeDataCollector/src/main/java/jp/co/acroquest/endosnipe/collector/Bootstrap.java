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
package jp.co.acroquest.endosnipe.collector;

import java.io.IOException;
import java.util.List;

import jp.co.acroquest.endosnipe.collector.config.AgentSetting;
import jp.co.acroquest.endosnipe.collector.config.ConfigurationReader;
import jp.co.acroquest.endosnipe.collector.config.DataCollectorConfig;
import jp.co.acroquest.endosnipe.collector.config.DisplayNameManager;
import jp.co.acroquest.endosnipe.collector.exception.InitializeException;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.PathUtil;
import jp.co.acroquest.endosnipe.data.db.ConnectionManager;

/**
 * DataCollector サービス用のメインクラスです。<br />
 * <ul>
 * <li>Windows版 commons-daemon (procrun) の場合
 *   <dl>
 *     <dt>開始時<dd>main() メソッドが引数「start」で呼び出されます。<br />
 *     <dt>終了時<dd>main() メソッドが引数「stop」で呼び出されます。<br />
 *   </dl>
 * </li>
 * <li>Linux版 commons-daemon (jsvc) の場合
 *   <dl>
 *     <dt>開始時<dd>init()、start() の順に呼び出されます。<br />
 *     <dt>終了時<dd>stop()、destroy() の順に呼び出されます。<br />
 *   </dl>
 * </li>
 * </ul>
 * 
 * @author y-komori
 */
public class Bootstrap implements LogMessageCodes
{
    private static Bootstrap            main__                 = null;

    public static final ENdoSnipeLogger logger_                =
                                                                 ENdoSnipeLogger.getLogger(Bootstrap.class,
                                                                                           ENdoSnipeDataCollectorPluginProvider.INSTANCE);

    // 設定ファイルを指定するためのプロパティ名
    private static final String         COLLECTOR_PROP_NAME    = "collector.property";

    // デフォルトの設定ファイル
    private static final String         DEF_COLLECTOR_PROPERTY = "../conf/collector.properties";

    // 異常終了時のステータス
    private static final int            STATUS_ERROR           = 1;

    // 開始処理中を表すフラグ
    private volatile boolean            starting_;

    // 終了処理中を表すフラグ
    private volatile boolean            stopping_;

    private ENdoSnipeDataCollector      collector_;

    /**
     * エントリポイントメソッドです。<br />
     * 
     * @param args コマンドライン引数
     */
    public static void main(final String[] args)
    {
        if (args.length < 1)
        {
            printUsage();
            System.exit(STATUS_ERROR);
        }
        if (main__ == null)
        {
            main__ = new Bootstrap();
        }

        String cmd = args[0];
        if (cmd.equals("start"))
        {
            try
            {
                main__.start();
            }
            catch (InitializeException ex)
            {
                logger_.log(ERROR_OCCURED_ON_STARTING, ex);
                System.exit(STATUS_ERROR);
            }
            catch (Exception ex)
            {
                logger_.log(ERROR_OCCURED_ON_STARTING, ex);
                System.exit(STATUS_ERROR);
            }
        }
        else if (cmd.equals("stop"))
        {
            main__.stop();
        }
    }

    /**
     * サービスを開始します。<br />
     * @throws InitializeException サービスの初期化に失敗
     */
    public void start()
        throws InitializeException
    {
        if (starting_ == true)
        {
            // 既に開始処理中の場合は何も行わない
            throw new InitializeException(DATA_COLLECTOR_ALREADY_STARTING);
        }
        this.starting_ = true;

        initContextClassLoader();

        logger_.log(DATA_COLLECTOR_SERVICE_STARTING);
        // シャットダウンフックの登録
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        DataCollectorConfig config = loadConfig();

        try
        {
            this.collector_ = new ENdoSnipeDataCollector();
            this.collector_.init(config);
            this.collector_.startService();

            if (config.getConnectionMode().equals(DataCollectorConfig.MODE_SERVER))
            {
                this.collector_.startServer();
            }
            else
            {
                this.collector_.connectAll();
            }

            logger_.log(DATA_COLLECTOR_SERVICE_STARTED);
            this.starting_ = false;

            // 終了するまでスレッドをブロックする
            this.collector_.blockTillStop();
        }
        catch (Throwable ex)
        {
            logger_.log(EXCEPTION_OCCURED_WITH_RESASON, ex, ex.getMessage());
            this.starting_ = false;

            stop();
            throw new InitializeException(ex);
        }
    }

    /**
     * サービスの停止を行います。<br />
     */
    public void stop()
    {
        if (this.collector_ != null)
        {
            if (stopping_ == true)
            {
                // 既に終了処理中の場合は何も行わない
                return;
            }
            this.stopping_ = true;

            logger_.log(DATA_COLLECTOR_SERVICE_STOPPING);

            this.collector_.stop();
            ConnectionManager.getInstance().closeAll();
            this.collector_ = null;

            logger_.log(DATA_COLLECTOR_SERVICE_STOPPED);
            this.stopping_ = false;
        }
    }

    /**
     * Linux 版 Commons-daemon から呼び出される初期化メソッドです。<br />
     * 削除しないでください。<br />
     */
    public void init(final String[] args)
    {
        // Do nothing.
    }

    /**
     * Linux 版 Commons-daemon から呼び出される破棄メソッドです。<br />
     * 削除しないでください。<br />
     */
    public void destroy()
    {
        // Do nothing.
    }

    private DataCollectorConfig loadConfig()
        throws InitializeException
    {
        String fileName = System.getProperty(COLLECTOR_PROP_NAME);
        if (fileName == null)
        {
            fileName = DEF_COLLECTOR_PROPERTY;
        }

        // 設定ファイルが相対パス指定の場合、絶対パスに変換する
        if (PathUtil.isRelativePath(fileName))
        {
            String jarPath = PathUtil.getJarDir(Bootstrap.class);
            fileName = jarPath + fileName;
        }

        DataCollectorConfig config = null;
        try
        {
            config = ConfigurationReader.load(fileName);
        }
        catch (IOException ex)
        {
            throw new InitializeException(CANNOT_FIND_PROPERTY,
                                          ConfigurationReader.getAbsoluteFilePath());
        }
        List<AgentSetting> agentList = config.getAgentSettingList();
        if (agentList == null || agentList.size() == 0)
        {
            throw new InitializeException(CANNOT_FIND_HOST,
                                          ConfigurationReader.getAbsoluteFilePath());
        }

        // データベース基準ディレクトリが相対パス指定の場合、
        // Jar が存在するディレクトリからの相対パスと見なして
        // 絶対パスに変換する
        String baseDir = config.getBaseDir();
        if (PathUtil.isRelativePath(baseDir) == true)
        {
            String jarPath = PathUtil.getJarDir(Bootstrap.class);
            config.setBaseDir(jarPath + baseDir);
        }

        // リソースモニタリングの設定ファイル名が相対パス指定の場合、
        // Jar が存在するディレクトリからの相対パスと見なして
        // 絶対パスに変換する
        String resourceMonitoringConf = config.getResourceMonitoringConf();
        if (PathUtil.isRelativePath(resourceMonitoringConf) == true)
        {
            String jarPath = PathUtil.getJarDir(Bootstrap.class);
            config.setResourceMonitoringConf(jarPath + resourceMonitoringConf);

        }

        // 言語別のDisplayName設定ファイルを読み込む
        DisplayNameManager.getManager().init(config.getLanguage());

        return config;
    }

    private void initContextClassLoader()
    {
        // CommonsDaemon から起動した場合、スレッドにコンテクストクラスローダ
        // が設定されていないため、強制的に指定する
        ClassLoader loader = getClass().getClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
    }

    private static void printUsage()
    {
        System.err.println("Usage: java -D" + COLLECTOR_PROP_NAME
                + "=PROPFILENAME -jar endosnipe-datacollector.jar {start|stop}");
    }

    /**
     * シャットダウンフッククラスです。<br />
     * 
     * @author fujii
     */
    private static class ShutdownHook extends Thread
    {
        @Override
        public void run()
        {
            if (main__ != null)
            {
                main__.stop();
            }
        }
    }
}
