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
package jp.co.acroquest.endosnipe.javelin.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.zip.ZipOutputStream;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.common.util.IOUtil;

/**
 * Javelinログを出力するスレッドです。
 * @author eriguchi
 *
 */
class JavelinLoggerThread extends Thread
{
    /** ファイルにつけるシーケンスナンバー */
    private static int sequenceNumber__ = 0;

    /** zipファイルにつけるシーケンスナンバー */
    private static int zipSequenceNumber__ = 0;

    /** 次に Javelin ログファイルを削除するシーケンスナンバー */
    private static int nextDeleteSequenceNumber__ = 0;

    private static final String EXTENTION_JVN = ".jvn";

    private static final String EXTENTION_ZIP = ".zip";

    /** jvnファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
    private static final String JVN_FILE_FORMAT =
            "javelin_{0,date,yyyy_MM_dd_HHmmss_SSS}_{1,number,00000}" + EXTENTION_JVN;

    /** zipファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
    private static final String ZIP_FILE_FORMAT =
            "{0}" + File.separator + "javelin_{1,date,yyyy_MM_dd_HHmmss_SSS}_{2,number,00000}"
                    + EXTENTION_ZIP;

    private final JavelinConfig javelinConfig_;

    private final BlockingQueue<JavelinLogTask> queue_;

    /**
     * Javelinの設定値とキューをセットします。<br />
     *
     * @param javelinConfig {@link JavelinConfig}オブジェクト
     * @param queue キュー
     */
    public JavelinLoggerThread(final JavelinConfig javelinConfig,
            final BlockingQueue<JavelinLogTask> queue)
    {
        super();
        setName("S2Javelin-LoggerThread-" + getId());
        setDaemon(true);
        this.javelinConfig_ = javelinConfig;
        this.queue_ = queue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        boolean isZipFileMax = this.javelinConfig_.isLogZipMax();
        int jvnFileMax = this.javelinConfig_.getLogJvnMax();
        int zipFileMax = this.javelinConfig_.getLogZipMax();
        if (nextDeleteSequenceNumber__ == 0)
        {
            nextDeleteSequenceNumber__ = jvnFileMax;
        }

        String javelinFileDir = this.javelinConfig_.getJavelinFileDir();

        // jvnログ出力先ディレクトリを作成する。
        File javelinFileDirFile = new File(javelinFileDir);
        if (javelinFileDirFile.exists() == false)
        {
            boolean mkdirs = javelinFileDirFile.mkdirs();
            if (mkdirs == false)
            {
                SystemLogger.getInstance().warn(
                                                "mkdir failed: dir name "
                                                        + javelinFileDirFile.getAbsolutePath());
            }
        }

        while (true)
        {
            try
            {
                JavelinLogTask task;
                try
                {
                    task = this.queue_.take();
                }
                catch (InterruptedException ex)
                {
                    SystemLogger.getInstance().warn(ex);
                    continue;
                }

                // ログのzip圧縮、ファイル数制限を行う。
                if (sequenceNumber__ > nextDeleteSequenceNumber__)
                {
                    nextDeleteSequenceNumber__ += jvnFileMax;
                    if (isZipFileMax)
                    {
                        zipAndDeleteLogFiles(jvnFileMax, javelinFileDir, EXTENTION_JVN);
                        IOUtil.removeFiles(zipFileMax, javelinFileDir, EXTENTION_ZIP);
                    }
                    else
                    {
                        IOUtil.removeFiles(jvnFileMax, javelinFileDir, EXTENTION_JVN);
                    }
                }

                StringBuilder stringBuilder = new StringBuilder();

                String jvnFileDir = this.javelinConfig_.getJavelinFileDir();
                String jvnFileName = task.getJvnFileName();
                String jvnFileFullPath = jvnFileDir + File.separator + jvnFileName;

                JavelinLogCallback callback = task.getJavelinLogCallback();
                long telegramId = task.getTelegramId();

                // 再帰的にwriterに書き込みを行う。
                JavelinFileGenerator.generateJavelinFileImpl(stringBuilder, task.getTree(),
                                                             task.getNode(), task.getEndNode(),
                                                             callback, jvnFileName,
                                                             jvnFileFullPath, telegramId);

                // 出力すべきメッセージがあればバッファフラッシュ
                if (stringBuilder.length() > 0)
                {
                    JavelinFileGenerator.flushBuffer(stringBuilder, jvnFileName,
                                                     jvnFileFullPath,
                                                     callback, this.javelinConfig_,
                                                     telegramId);
                }
            }
            catch (Throwable ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }
    }

    /**
     * Javelinログファイル名を生成します。<br />
     *
     * @param date 日付
     * @return jvnファイル名
     */
    public static String createJvnFileName(final Date date)
    {
        String fileName;
        fileName = MessageFormat.format(JVN_FILE_FORMAT, date, (sequenceNumber__++));

        return fileName;
    }

    /**
     * ファイル名を生成します。<br />
     *
     * @return ファイル名
     */
    private String createZipFileName(final Date date)
    {
        String fileName;
        fileName =
                MessageFormat.format(ZIP_FILE_FORMAT, this.javelinConfig_.getJavelinFileDir(),
                                     date, (zipSequenceNumber__++));

        return fileName;
    }

    private void zipAndDeleteLogFiles(final int maxFileCount, final String dirName,
            final String extention)
    {
        File[] files = IOUtil.listFile(dirName, extention);

        if (files == null || files.length == 0)
        {
            return;
        }

        String fileName = createZipFileName(new Date());
        FileOutputStream fileOutputStream;
        ZipOutputStream zStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(fileName);
            zStream = new ZipOutputStream(fileOutputStream);

            for (int index = 0; index < files.length; index++)
            {
                File file = files[index];
                IOUtil.zipFile(zStream, file);
                SystemLogger.getInstance().debug(
                                                 "zip file name = " + file.getName() + " to "
                                                         + fileName);

                boolean success = file.delete();
                if (success == false)
                {
                    SystemLogger.getInstance().warn("Remove failed. file name = " + file.getName());
                }
                else
                {
                    if (SystemLogger.getInstance().isDebugEnabled())
                    {
                        SystemLogger.getInstance().debug("Remove file name = " + file.getName());
                    }
                }
            }

            zStream.finish();
        }
        catch (FileNotFoundException fnfe)
        {
            SystemLogger.getInstance().warn(fnfe);
        }
        catch (IOException ioe)
        {
            SystemLogger.getInstance().warn(ioe);
        }
        finally
        {
            if (zStream != null)
            {
                try
                {
                    zStream.close();
                }
                catch (IOException ioe)
                {
                    SystemLogger.getInstance().warn(ioe);
                }
            }
        }
    }

}
