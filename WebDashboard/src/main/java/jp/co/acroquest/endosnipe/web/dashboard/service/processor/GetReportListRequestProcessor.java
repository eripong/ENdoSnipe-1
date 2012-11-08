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
package jp.co.acroquest.endosnipe.web.dashboard.service.processor;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.web.dashboard.constants.EventConstants;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.entity.ReportListEntity;
import jp.co.acroquest.endosnipe.web.dashboard.util.ResponseUtil;

/**
 * Report画面で、レポート一覧取得要求イベントを処理するプロセッサ。
 * 
 * @author akiba
 */
public class GetReportListRequestProcessor implements EventProcessor
{
    /** ロガー */
    private static final ENdoSnipeLogger LOGGER                                      =
                                                                                       ENdoSnipeLogger.getLogger(GetReportListRequestProcessor.class);

    /** レポート出力先(このプロセッサが読み込む)ディレクトリ。 */
    private String                       reportDir_                                  =
                                                                                       DEFAULT_REPORT_DIR;

    /** レポート出力先ディレクトリのデフォルト値。 */
    private static final String          DEFAULT_REPORT_DIR                          =
                                                                                       "C:/SMGWorks/VM/ENdoSnipe/report";

    /** レポート出力ファイルの測定開始日を示す文字列の一文字目の位置 */
    private static final int             FIRST_CHARACTER_POSITION_OF_START_DATE      = 0;

    /** レポート出力ファイルの測定終了日を示す文字列の一文字目の位置 */
    private static final int             FIRST_CHARACTER_POSITION_OF_END_DATE        = 16;

    /** レポート出力ファイルの日付を示す文字列の内、年を示す部分の一文字目の位置 */
    private static final int             FIRST_CHARACTER_POSITION_OF_YEAR_IN_DATE    = 0;

    /** レポート出力ファイルの日付を示す文字列の内、年を示す部分の最後の文字位置 */
    private static final int             LAST_CHARACTER_POSITION_OF_YEAR_IN_DATE     = 4;

    /** レポート出力ファイルの日付を示す文字列の内、月を示す部分の一文字目の位置 */
    private static final int             FIRST_CHARACTER_POSITION_OF_MONTH_IN_DATE   = 4;

    /** レポート出力ファイルの日付を示す文字列の内、月を示す部分の最後の文字位置 */
    private static final int             LAST_CHARACTER_POSITION_OF_MONTH_IN_DATE    = 6;

    /** レポート出力ファイルの日付を示す文字列の内、日を示す部分の一文字目の位置 */
    private static final int             FIRST_CHARACTER_POSITION_OF_DAY_IN_DATE     = 6;

    /** レポート出力ファイルの日付を示す文字列の内、日を示す部分の最後の文字位置 */
    private static final int             LAST_CHARACTER_POSITION_OF_DAY_IN_DATE      = 8;

    /** レポート出力ファイルの日付を示す文字列の内、日を示す部分の一文字目の位置 */
    private static final int             FIRST_CHARACTER_POSITION_OF_HOUR_IN_DATE    = 9;

    /** レポート出力ファイルの日付を示す文字列の内、日を示す部分の最後の文字位置 */
    private static final int             LAST_CHARACTER_POSITION_OF_HOUR_IN_DATE     = 11;

    /** レポート出力ファイルの日付を示す文字列の内、日を示す部分の一文字目の位置 */
    private static final int             FIRST_CHARACTER_POSITION_OF_MINITES_IN_DATE = 11;

    /** レポート出力ファイルの日付を示す文字列の内、日を示す部分の最後の文字位置 */
    private static final int             LAST_CHARACTER_POSITION_OF_MINITES_IN_DATE  = 13;

    /** レポート出力ファイルの日付を示す文字列の内、日を示す部分の一文字目の位置 */
    private static final int             FIRST_CHARACTER_POSITION_OF_SECOND_IN_DATE  = 13;

    /** レポート出力ファイルの日付を示す文字列の内、日を示す部分の最後の文字位置 */
    private static final int             LAST_CHARACTER_POSITION_OF_SECOND_IN_DATE   = 15;

    /** レポート出力ファイルの日付パターンを示す文字列 */
    private static final String          REPORT_FILE_DATE_PATTERN                    = "yyyyMMdd";

    /** 一日をミリ秒で見積もった場合の値。 */
    private static final long            DURATION_FOR_ONEDAY_OF_MILLISECOND          = 86400000;

    /**
     * HttpRequestを処理する。
     * 
     * @param request  HTTPリクエスト。
     * @param response HTTPレスポンス
     */
    public void process(HttpServletRequest request, HttpServletResponse response)
    {
        String clientId = request.getParameter(EventConstants.CLIENT_ID);
        if (clientId == null)
        {
            LOGGER.log(LogMessageCodes.NO_CLIENT_ID);
            return;
        }

        // レポートのzipファイルがある一覧を取得する

        String agent_id = request.getParameter("agent_id");
        if (agent_id == null)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_REPORT_AGENT_ID);
            return;
        }

        // web.xml の context-param/param-name(report.directory) に記述したディレクトリを参照する
        String reportsDir = getReportDir();
        if (reportsDir == null)
        {
            reportsDir = DEFAULT_REPORT_DIR;
        }

        File reportDir = new File(reportsDir + File.separator + agent_id + File.separator);
        File[] fileList = reportDir.listFiles();

        if (fileList == null)
        {
            // 指定したagent_idのディレクトリが無い場合
            LOGGER.log(LogMessageCodes.UNKNOWN_REPORT_FILE_NAME, "Report directory is not found.");
            return;
        }
        else if (fileList.length == 0)
        {
            // 指定したagent_idのディレクトリ配下が空の場合
            LOGGER.log(LogMessageCodes.UNKNOWN_REPORT_FILE_NAME,
                       "There is no files and directories.");
            return;
        }
        else
        {
            // ファイル名(＝レポート開始時刻)の降順でソートする
            Arrays.sort(fileList, new Comparator<File>() {
                public int compare(File data1, File data2)
                {
                    String name1 = data1.getName();
                    String name2 = data2.getName();
                    return name1.compareTo(name2);
                }
            });

            // ファイルの一覧を、Entityに変換する
            List<String> fileNames = new ArrayList<String>();
            List<String> startDates = new ArrayList<String>();
            List<String> endDates = new ArrayList<String>();
            List<Integer> durations = new ArrayList<Integer>();

            int index = 0;
            for (File file : fileList)
            {
                String fileName = file.getName();
                //ファイル形式が正しいかどうかを判定する。
                boolean isReportFile = isFileFormat(fileName);
                if (isReportFile)
                {
                    try
                    {
                        //期間が負なら出力しない。
                        int duration = calcDuration(fileName);
                        if (duration <= 0)
                        {
                            continue;
                        }
                        durations.add(duration);
                        if (LOGGER.isDebugEnabled())
                        {
                            LOGGER.log(LogMessageCodes.REPORT_FILE_DURATION, fileName, durations.get(index));
                        }
                    }
                    catch (NumberFormatException ex)
                    {
                        // ファイル名を解析できなかったレポートは無視する
                        LOGGER.log(LogMessageCodes.UNSUPPORTED_REPORT_FILE_DURATION_FORMAT, fileName);                        
                        continue;
                    }
                    fileNames.add(fileName);
                    startDates.add(getDateStr(fileNames.get(index),
                                              FIRST_CHARACTER_POSITION_OF_START_DATE));
                    endDates.add(getDateStr(fileNames.get(index),
                                            FIRST_CHARACTER_POSITION_OF_END_DATE));
                }
                else
                {
                    continue;
                }

                index++;
            }

            ReportListEntity entity = new ReportListEntity();
            entity.event_id = EventConstants.EVENT_REPORT_LIST_RESPONSE;
            entity.file_name = fileNames;
            entity.start_date = startDates;
            entity.end_date = endDates;
            entity.duration = durations;

            ResponseUtil.sendMessageOfJSONCode(response, entity, clientId);
        }
    }

    /**
     * レポート出力先ディレクトリを設定する。
     * 
     * @param dir レポート出力先ディレクトリ。
     */
    public void setReportDir(String dir)
    {
        this.reportDir_ = dir;
    }

    /**
     * レポート出力先ディレクトリを取得する。
     * 
     * @return レポート出力先ディレクトリ。
     */
    public String getReportDir()
    {
        return this.reportDir_;
    }

    /**
     * ファイル名の開始日時と終了日時から期間を取得する。<br/>
     * 期間は日数の切り上げで計算される。<br/>
     * ファイル名は yyyyMMdd_HHmmss-yyyyMMdd_HHmmss 形式であることが前提。
     * 
     * @param fileName レポートファイル名。
     * @return 期間。
     */
    private int calcDuration(String fileName)
    {
        String startDateString =
                                 fileName.substring(FIRST_CHARACTER_POSITION_OF_YEAR_IN_DATE,
                                                    LAST_CHARACTER_POSITION_OF_DAY_IN_DATE);
        int startTime =
                        Integer.valueOf(fileName.substring(
                                                           FIRST_CHARACTER_POSITION_OF_HOUR_IN_DATE,
                                                           LAST_CHARACTER_POSITION_OF_SECOND_IN_DATE));
        String endDateString =
                               fileName.substring(FIRST_CHARACTER_POSITION_OF_END_DATE,
                                                  FIRST_CHARACTER_POSITION_OF_END_DATE
                                                          + LAST_CHARACTER_POSITION_OF_DAY_IN_DATE);
        int endTime =
                      Integer.valueOf(fileName.substring(
                                                         FIRST_CHARACTER_POSITION_OF_END_DATE
                                                                 + FIRST_CHARACTER_POSITION_OF_HOUR_IN_DATE,
                                                         FIRST_CHARACTER_POSITION_OF_END_DATE
                                                                 + LAST_CHARACTER_POSITION_OF_SECOND_IN_DATE));

        //Date型に変換してから期間を計算する。
        SimpleDateFormat format = new SimpleDateFormat(REPORT_FILE_DATE_PATTERN);

        long durationOfMilliSecond = 0;
        try
        {
            Date startDate = format.parse(startDateString);
            Date endDate = format.parse(endDateString);
            durationOfMilliSecond = endDate.getTime() - startDate.getTime();
        }
        catch (ParseException parseEx)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_REPORT_FILE_NAME, fileName + " is wrong format.");
        }

        int duration = (int)(durationOfMilliSecond / DURATION_FOR_ONEDAY_OF_MILLISECOND);
        if (startTime < endTime)
        {
            duration++;
        }

        return duration;
    }

    /**
     * 指定した位置から日付の文字列を取得する。<br/>
     * yyyyMMdd_HHmmss 形式であることが前提。<br/>
     * レポートのファイル名は、(開始日時)-(終了日時)であるため、
     * 開始位置を0または16とすることでどちらかの値を選ぶことができる。
     * 
     * @param fileName レポートファイル名。
     * @param startpos 開始位置。
     * @return 日付文字列。
     */
    private String getDateStr(String fileName, int startpos)
    {
        String str = fileName.substring(startpos);

        StringBuilder builder = new StringBuilder();
        builder.append(str.substring(FIRST_CHARACTER_POSITION_OF_YEAR_IN_DATE,
                                     LAST_CHARACTER_POSITION_OF_YEAR_IN_DATE));
        builder.append("/");
        builder.append(str.substring(FIRST_CHARACTER_POSITION_OF_MONTH_IN_DATE,
                                     LAST_CHARACTER_POSITION_OF_MONTH_IN_DATE));
        builder.append("/");
        builder.append(str.substring(FIRST_CHARACTER_POSITION_OF_DAY_IN_DATE,
                                     LAST_CHARACTER_POSITION_OF_DAY_IN_DATE));
        builder.append(" ");
        builder.append(str.substring(FIRST_CHARACTER_POSITION_OF_HOUR_IN_DATE,
                                     LAST_CHARACTER_POSITION_OF_HOUR_IN_DATE));
        builder.append(":");
        builder.append(str.substring(FIRST_CHARACTER_POSITION_OF_MINITES_IN_DATE,
                                     LAST_CHARACTER_POSITION_OF_MINITES_IN_DATE));
        builder.append(":");
        builder.append(str.substring(FIRST_CHARACTER_POSITION_OF_SECOND_IN_DATE,
                                     LAST_CHARACTER_POSITION_OF_SECOND_IN_DATE));

        return builder.toString();
    }

    /**
     * ファイル形式がレポートファイル形式と等しければtrueを返す。
     * 
     * @param fileName 取得したいファイル名
     * @return
     */
    private boolean isFileFormat(String fileName)
    {
        //正規表現で比較する。
        String patternString = "[0-9]{8}_[0-9]{6}-[0-9]{8}_[0-9]{6}.zip$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();

    }
}
