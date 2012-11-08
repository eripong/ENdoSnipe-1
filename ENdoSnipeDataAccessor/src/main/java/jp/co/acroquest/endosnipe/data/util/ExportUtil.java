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
package jp.co.acroquest.endosnipe.data.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import jp.co.acroquest.endosnipe.common.util.IOUtil;
import jp.co.acroquest.endosnipe.data.dao.JavelinLogDao;
import jp.co.acroquest.endosnipe.data.db.DBManager;
import jp.co.acroquest.endosnipe.data.entity.JavelinLog;

public class ExportUtil
{
    /** 開始／終了時刻を指定する文字列形式。 */
    private static final String TIME_FORMAT = "yyyyMMdd_HHmmss";

    public static void main(String[] args)
    {
        if (args.length < 5)
        {
            System.out.println("usage: ExportUtil db_host db_port db_user db_password dbName startTime endTime");
            System.out.println("startTime format=\"" + TIME_FORMAT + "\"");
            System.out.println("endTime format=\"" + TIME_FORMAT + "\"");
            return;
        }

        // DBの諸設定を取得
        String dbHost = args[0];
        String dbPort = args[1];
        String dbUser = args[2];
        String dbPass = args[3];
        String dbName = args[4];

        String startTime = null;
        String endTime = null;

        if (args.length > 5)
        {
            startTime = args[5];
        }
        if (args.length > 6)
        {
            endTime = args[6];
        }

        // レポートの出力先設定
        String reportPath = "./jvn_logs";

        // レポート作成に使用するDBを指定する
        DBManager.updateSettings(false, "", dbHost, dbPort, dbUser, dbPass);

        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
        Timestamp start = null;
        try
        {
            if(startTime != null)
            {
                start = new Timestamp(format.parse(startTime).getTime());
            }
        }
        catch (ParseException ex)
        {
            System.out.println("start time format invalid:" + args[5]);
        }
        Timestamp end = null;
        try
        {
            if(endTime != null)
            {
                end = new Timestamp(format.parse(endTime).getTime());
            }
        }
        catch (ParseException ex)
        {
            System.out.println("end time format invalid:" + args[6]);
        }

        // Javelinログを出力する。
        outputJvnLog(dbName, start, end, reportPath);
    }

    /**
     * Javelinログをデータベースから読み込み、ファイル出力する。
     * 
     * @param database データベース名
     * @param start 開始日時
     * @param end 開始日時
     * @param outputDir 出力先ディレクトリ
     * 
     * @return {@code true}成功/{@code false}失敗
     */
    public static boolean outputJvnLog(String database, Timestamp start, Timestamp end,
        String outputDir)
    {
        File outputDirFile = new File(outputDir);

        if (outputDirFile.exists() == false)
        {
            boolean isSuccess = outputDirFile.mkdirs();
            if (isSuccess == false)
            {
                System.err.println("jvnログ出力ディレクトリの作成に失敗しました。");
                return false;
            }
        }

        try
        {
            List<JavelinLog> jvnLogList = JavelinLogDao.selectByTermWithLog(database, start, end);
            for (JavelinLog log : jvnLogList)
            {
                String fileName = log.logFileName;
                OutputStream output = null;
                try
                {
                    Timestamp startTime = log.startTime;
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(startTime.getTime());
                    
                    String outputSubDirName =
                        MessageFormat.format(
                                             "{0,number,0000}{1,number,00}{2,number,00}\\{3,number,00}{4,number,00}",
                                             cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                                             cal.get(Calendar.DAY_OF_MONTH),
                                             cal.get(Calendar.HOUR_OF_DAY),
                                             cal.get(Calendar.MINUTE));
                    
                    File ouputSubDir = new File(outputDir + File.separator + outputSubDirName);
                    if(ouputSubDir.exists() == false)
                    {
                        boolean isSuccess = ouputSubDir.mkdirs();
                        if (isSuccess == false)
                        {
                            System.err.println("jvnログ出力ディレクトリの作成に失敗しました。");
                            return false;
                        }
                    }
                    output =
                        new BufferedOutputStream(new FileOutputStream(outputDir + File.separator
                            + outputSubDirName + File.separator + fileName));
                    IOUtil.copy(log.javelinLog, output);
                    System.out.println("outpu jvn:" + fileName);
                }
                catch (FileNotFoundException fnfe)
                {
                    System.err.println("jvnログ出力ディレクトリが見つかりません。");
                }
                catch (IOException ioe)
                {
                    System.err.println("jvnログ出力中に例外が発生しました。");
                }
                finally
                {
                    if (output != null)
                    {
                        try
                        {
                            output.close();
                        }
                        catch (IOException ioe)
                        {
                            System.err.println("jvnログクローズ中に例外が発生しました。");
                        }
                    }
                }
            }

        }
        catch (SQLException sqle)
        {
            System.err.println("DBからのjvnログ読み込み中に例外が発生しました。");
            return false;
        }

        return true;
    }

}
