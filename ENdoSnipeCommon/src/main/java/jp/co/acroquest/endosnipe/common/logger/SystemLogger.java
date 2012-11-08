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
package jp.co.acroquest.endosnipe.common.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.util.IOUtil;

/**
 * Javelinのシステムロガー。<br>
 * 
 * @author eriguchi
 */
public class SystemLogger
{
    /** 初期化状態を表すフラグ */
    private static volatile boolean initialized__ = false;

    /** エラーログ出力日時のフォーマット */
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

    /** 改行文字 */
    public static final String NEW_LINE = System.getProperty("line.separator");

    /** システムログファイルの拡張子 */
    private static final String EXTENTION = ".log";

    /** システムログファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
    private static final String LOG_FILE_FORMAT =
            "jvn_sys_{0,date,yyyy_MM_dd_HHmmss_SSS}" + EXTENTION;

    /** システムログファイルの最大数 */
    private int systemLogNumMax_;

    /** システムログファイルの最大サイズ */
    private int systemLogSizeMax_;

    /** システムログのログレベル */
    private LogLevel systemLogLevel_ = LogLevel.WARN;

    /** 本クラスのインスタンス */
    private static SystemLogger instance__ = new SystemLogger();

    /** 書き込んだ文字数 */
    private long writeCount_ = 0;

    /** システムログファイルの出力先ディレクトリのパス */
    private String logPath_;

    /** システムログファイルのファイル名 */
    private String logFileName_;

    /** システムログ出力用ThreadPoolExecutor。 */
    private final ThreadPoolExecutor executor_ =
            new ThreadPoolExecutor(1, 1, 1, TimeUnit.MILLISECONDS,
                                   new ArrayBlockingQueue<Runnable>(1000), new ThreadFactory() {
                                       public Thread newThread(final Runnable runnable)
                                       {
                                           Thread thread =
                                                   new Thread(runnable, "Javelin-SystemLogger");
                                           thread.setDaemon(true);
                                           return thread;
                                       }
                                   }, new ThreadPoolExecutor.CallerRunsPolicy());

    private SystemLogger()
    {
        // Do nothing.
    }

    /**
     * 本クラスのインスタンスを取得します。<br />
     * 
     * @return インスタンス
     */
    public static SystemLogger getInstance()
    {
        return instance__;
    }

    /**
     * システムログファイル名を生成します。<br />
     * ファイル名のフォーマットは以下のとおりです。<br>
     * jvn_sys_yyyyMMddHHmmssSSS.log
     * 
     * @return システムログファイル名
     */
    private String createLogFileName()
    {
        return MessageFormat.format(LOG_FILE_FORMAT, new Date());
    }

    /**
     * ログメッセージをフォーマットします。<br />
     * 
     * @param level ログレベル
     * @param threadName スレッド名。
     * @param message ログメッセージ
     * @param throwable 例外オブジェクト
     * @return フォーマットしたログメッセージ
     */
    private String formatMessage(final LogLevel level, final String threadName,
            final String message, final Throwable throwable)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateMessage = dateFormat.format(new Date());

        StringBuffer messageBuffer = new StringBuffer();

        messageBuffer.append(dateMessage);
        messageBuffer.append(" [");
        messageBuffer.append(level.getLevelStr());
        messageBuffer.append("] ");
        messageBuffer.append("[");
        messageBuffer.append(threadName);
        messageBuffer.append("] ");
        messageBuffer.append("[Javelin] ");

        if (message != null)
        {
            messageBuffer.append(message);
        }
        if (throwable != null)
        {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            throwable.printStackTrace(printWriter);
            String stackTrace = stringWriter.toString();

            messageBuffer.append(NEW_LINE);
            messageBuffer.append(stackTrace);
        }
        messageBuffer.append(NEW_LINE);
        String buildMessage = messageBuffer.toString();
        return buildMessage;
    }

    /**
     * ログを出力します。<br />
     * 
     * @param message エラーメッセージ
     * @param throwable 例外オブジェクト
     */
    private void log(final LogLevel level, final String message, final Throwable throwable)
    {
        if (level.getLevel() < SystemLogger.this.systemLogLevel_.getLevel())
        {
            return;
        }

        Thread currentThread = Thread.currentThread();
        final String THREAD_NAME = currentThread.getName() + "(" + currentThread.getId() + ")";
        executor_.execute(new Runnable() {
            public void run()
            {
                if (initialized__ == false)
                {
                    return;
                }

                String formattedMessage = formatMessage(level, THREAD_NAME, message, throwable);

                String logPath = SystemLogger.this.logPath_;
                if (logPath == null)
                {
                    System.err.println(formattedMessage);
                    return;
                }

                if (SystemLogger.this.logFileName_ == null)
                {
                    SystemLogger.this.logFileName_ = createLogFileName();
                }

                OutputStreamWriter writer = null;
                try
                {
                    // 親ディレクトリを作成する。
                    IOUtil.createDirs(logPath_);

                    FileOutputStream fileOutputStream =
                            new FileOutputStream(logPath_ + File.separator + logFileName_, true);
                    writer = new OutputStreamWriter(fileOutputStream);

                    writer.write(formattedMessage);
                    writeCount_ += formattedMessage.length();
                }
                catch (Exception ex)
                {
                    // 出力できなかった場合は標準エラーに出力する。。
                    String errMessage =
                            "Javelin実行エラー出力ファイルへの書き込みが行えなかったため、標準エラー出力を使用します。" + NEW_LINE
                                    + "(javelin.error.log=" + logPath + File.separator
                                    + logFileName_ + ")";
                    System.err.println(errMessage);
                    ex.printStackTrace();
                    System.err.println(formattedMessage);
                }
                finally
                {
                    try
                    {
                        if (writer != null)
                        {
                            writer.close();
                        }
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                }

                // ローテートが必要な場合はローテートする。
                if (writeCount_ > systemLogSizeMax_)
                {
                    File logFile =
                            new File(logPath + File.separator + SystemLogger.this.logFileName_);
                    if (logFile.length() > systemLogSizeMax_)
                    {
                        logFileName_ = createLogFileName();
                        IOUtil.removeFiles(systemLogNumMax_ - 1, logPath, EXTENTION);
                        writeCount_ = 0;
                    }
                }
            }
        });
    }

    /**
     * FATAL レベルのログを出力します。<br />
     * 
     * @param message メッセージ
     */
    public void fatal(final String message)
    {
        this.fatal(message, null);
    }

    /**
     * FATAL レベルのログを出力します。<br />
     * 
     * @param throwable 例外オブジェクト
     */
    public void fatal(final Throwable throwable)
    {
        this.fatal(null, throwable);
    }

    /**
     * FATAL レベルのログを出力します。<br />
     *
     * @param message メッセージ
     * @param throwable 例外オブジェクト
     */
    public void fatal(final String message, final Throwable throwable)
    {
        this.log(LogLevel.FATAL, message, throwable);
    }

    /**
     * ERROR レベルのログを出力します。<br />
     * 
     * @param message メッセージ
     */
    public void error(final String message)
    {
        this.error(message, null);
    }

    /**
     * ERROR レベルのログを出力します。<br />
     * 
     * @param throwable 例外オブジェクト
     */
    public void error(final Throwable throwable)
    {
        this.error(null, throwable);
    }

    /**
     * ERROR レベルのログを出力します。<br />
     * 
     * @param message メッセージ
     * @param throwable 例外オブジェクト
     */
    public void error(final String message, final Throwable throwable)
    {
        this.log(LogLevel.ERROR, message, throwable);
    }

    /**
     * WARN レベルのログを出力します。<br />
     * 
     * @param message メッセージ
     */
    public void warn(final String message)
    {
        this.warn(message, null);
    }

    /**
     * WARN レベルのログを出力します。<br />
     * 
     * @param throwable 例外オブジェクト
     */
    public void warn(final Throwable throwable)
    {
        this.warn(null, throwable);
    }

    /**
     * WARN レベルのログを出力します。<br />
     * 
     * @param message メッセージ
     * @param throwable 例外オブジェクト
     */
    public void warn(final String message, final Throwable throwable)
    {
        this.log(LogLevel.WARN, message, throwable);
    }

    /**
     * INFO レベルのログを出力します。<br />
     * 
     * @param message メッセージ
     */
    public void info(final String message)
    {
        this.info(message, null);
    }

    /**
     * INFO レベルのログを出力します。<br />
     * 
     * @param throwable 例外オブジェクト
     */
    public void info(final Throwable throwable)
    {
        this.info(null, throwable);
    }

    /**
     * INFO レベルのログを出力します。<br />
     * 
     * @param message メッセージ
     * @param throwable 例外オブジェクト
     */
    public void info(final String message, final Throwable throwable)
    {
        this.log(LogLevel.INFO, message, throwable);
    }

    /**
     * DEBUG レベルのログを出力します。<br />
     * 
     * @param message メッセージ
     */
    public void debug(final String message)
    {
        this.debug(message, null);
    }

    /**
     * DEBUG レベルのログを出力します。<br />
     * 
     * @param throwable 例外オブジェクト
     */
    public void debug(final Throwable throwable)
    {
        this.debug(null, throwable);
    }

    /**
     * DEBUG レベルのログを出力します。<br />
     * 
     * @param message メッセージ
     * @param throwable 例外オブジェクト
     */
    public void debug(final String message, final Throwable throwable)
    {
        this.log(LogLevel.DEBUG, message, throwable);
    }

    /**
     * DEBUG レベルが有効かどうかを返します。<br />
     * 
     * @return 有効である場合、<code>true</code>
     */
    public boolean isDebugEnabled()
    {
        if (LogLevel.DEBUG.getLevel() < this.systemLogLevel_.getLevel())
        {
            return false;
        }
        return true;
    }

    /**
     * INFO レベルが有効かどうかを返します。<br />
     * 
     * @return 有効である場合、<code>true</code>
     */
    public boolean isInfoEnabled()
    {
        if (LogLevel.INFO.getLevel() < this.systemLogLevel_.getLevel())
        {
            return false;
        }
        return true;
    }

    /**
     * WARN レベルが有効かどうかを返します。<br />
     * 
     * @return 有効である場合、<code>true</code>
     */
    public boolean isWarnEnabled()
    {
        if (LogLevel.WARN.getLevel() < this.systemLogLevel_.getLevel())
        {
            return false;
        }
        return true;
    }

    /**
     * ERROR レベルが有効かどうかを返します。<br />
     * 
     * @return 有効である場合、<code>true</code>
     */
    public boolean isErrorEnabled()
    {
        if (LogLevel.ERROR.getLevel() < this.systemLogLevel_.getLevel())
        {
            return false;
        }
        return true;
    }

    /**
     * FATAL レベルが有効かどうかを返します。<br />
     * 
     * @return 有効である場合、<code>true</code>
     */
    public boolean isFatalEnabled()
    {
        if (LogLevel.FATAL.getLevel() < this.systemLogLevel_.getLevel())
        {
            return false;
        }
        return true;
    }

    /**
     * システムログを初期化します。<br />
     * 
     * @param config 初期化パラメータオブジェクト
     */
    public static synchronized void initSystemLog(final JavelinConfig config)
    {
        SystemLogger instance = getInstance();
        instance.init(config);

        initialized__ = true;
    }

    private void init(final JavelinConfig config)
    {
        this.logPath_ = config.getSystemLog();
        this.systemLogNumMax_ = config.getSystemLogNumMax();
        this.systemLogSizeMax_ = config.getSystemLogSizeMax();
        this.systemLogLevel_ = toLogLevel(config.getSystemLogLevel());

        // 起動時にログファイルが多数ある場合は削除する。
        IOUtil.removeFiles(this.systemLogNumMax_ - 1, this.logPath_, EXTENTION);
    }

    /**
     * {@link SystemLogger} が初期化済みであるかどうかを返します。<br />
     * 
     * @return 初期化済みの場合は <code>true</code>。
     */
    public static boolean isInitialized()
    {
        return initialized__;
    }

    /**
     * ログレベルの文字列を {@link LogLevel} オブジェクトに変換します。<br />
     * 
     * @param logLevelStr ログレベルの文字列。
     * @return LogLevel {@link LogLevel} オブジェクト
     */
    private static LogLevel toLogLevel(final String logLevelStr)
    {
        if ("DEBUG".equals(logLevelStr))
        {
            return LogLevel.DEBUG;
        }
        else if ("INFO".equals(logLevelStr))
        {
            return LogLevel.INFO;
        }
        else if ("WARN".equals(logLevelStr))
        {
            return LogLevel.WARN;
        }
        else if ("ERROR".equals(logLevelStr))
        {
            return LogLevel.ERROR;
        }
        else if ("FATAL".equals(logLevelStr))
        {
            return LogLevel.FATAL;
        }
        return LogLevel.WARN;
    }
}
