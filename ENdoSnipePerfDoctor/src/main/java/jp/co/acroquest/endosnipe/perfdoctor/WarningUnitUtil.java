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
package jp.co.acroquest.endosnipe.perfdoctor;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogConstants;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;

/**
 * パフォーマンスドクターで使用するユーティリティ
 * 
 * @author eriguchi
 * @author tanimoto
 * 
 */
public class WarningUnitUtil
{
    private static final ENdoSnipeLogger LOGGER              =
                                                               ENdoSnipeLogger.getLogger(WarningUnitUtil.class,
                                                                                         null);

    // リソースバンドル名
    private static final String          PERFDOCTOR_MESSAGES = "PerfDoctorMessages";

    // リソースバンドルからメッセージを取得する際に
    // ルールIDの後ろにつけるsuffix
    private static final String          MESSAGE_SUFFIX      = "_message";

    /** 日付のフォーマット */
    private static final String          DATE_FORMAT         = "yyyy/M/d HH:mm:ss.SSS";

    /**
     * インスタンス化を防止するためのプライベートコンストラクタです。<br />
     * 
     */
    private WarningUnitUtil()
    {
        // Do Nothing.
    }

    /**
     * 警告を生成します。<br />
     * WarningUnitの生成には必ずこのメソッドを利用してください。<br />
     * 
     * @param unitId 警告のID
     * @param rule 判定ルール
     * @param javelinLogElement {@link JavelinLogElement}オブジェクト
     * @param isDescend フィルタ時に降順に並べるかどうかを表すフラグ
     * @param args メッセージの引数
     * @return 警告。
     */
    public static WarningUnit createWarningUnit(final String unitId, final PerformanceRule rule,
            final JavelinLogElement javelinLogElement, final boolean isDescend, final Object[] args)
    {
        return createWarningUnit(false, "", unitId, rule, javelinLogElement, isDescend, args);
    }

    /**
     * 警告を生成します。<br />
     * WarningUnitの生成には必ずこのメソッドを利用してください。<br />
     * 
     * @param isEvent イベントによるWarningUnitかどうか
     * @param stackTrace スタックトレース
     * @param unitId 警告のID
     * @param rule 判定ルール
     * @param javelinLogElement {@link JavelinLogElement}オブジェクト
     * @param isDescend フィルタ時に降順に並べるかどうかを表すフラグ
     * @param args メッセージの引数
     * @return 警告。
     */
    public static WarningUnit createWarningUnit(final boolean isEvent, final String stackTrace,
            final String unitId, final PerformanceRule rule,
            final JavelinLogElement javelinLogElement, final boolean isDescend, final Object[] args)
    {
        String id = rule.getId();
        String message = getMessage(id + MESSAGE_SUFFIX, args);
        String className = getClassName(javelinLogElement);
        String methodName = getMethodName(javelinLogElement);
        String level = rule.getLevel();
        String logFileName = javelinLogElement.getLogFileName();
        int startLogLine = javelinLogElement.getStartLogLine();

        //callのログのみendTimeをはかり、その他のログはendTimeをstartTimeにあわせる。
        long startTime = calculateStartTime(javelinLogElement);
        long endTime = startTime;

        List<?> baseInfo = javelinLogElement.getBaseInfo();
        String baseInfoId = (String)baseInfo.get(JavelinLogColumnNum.ID);
        if (JavelinConstants.MSG_CALL.equals(baseInfoId) == true)
        {
            endTime = calculateEndTime(javelinLogElement, startTime);
        }

        return new WarningUnit(unitId, id, message, className, methodName, level, logFileName,
                               startLogLine, startTime, endTime, isDescend, isEvent, stackTrace,
                               args);
    }

    /**
     * 警告を出力するログの開始時間のlong値を計算する。<br>
     * "yyyy/mm/dd hh:mm:ss.SSS" の形式の時刻データをlong値に変換して、時刻を計算する。<br>
     * 
     * @param javelinLogElement
     *            JavelinLogElement
     * @return 時刻データのlong値
     */
    private static long calculateStartTime(final JavelinLogElement javelinLogElement)
    {
        List<String> baseInfoList = javelinLogElement.getBaseInfo();
        if (baseInfoList == null || baseInfoList.size() <= JavelinLogColumnNum.CALL_TIME)
        {
            String text = Messages.getMessage("endosnipe.perfdoctor.rule.DateGetter.ErrorLabel");
            LOGGER.error(text);
            return 0;
        }

        String startTimeStr = baseInfoList.get(JavelinLogColumnNum.CALL_TIME);
        if (startTimeStr == null)
        {
            String text = Messages.getMessage("endosnipe.perfdoctor.rule.DateGetter.ErrorLabel");
            LOGGER.error(text);
            return 0;
        }
        // SimleDateFormatを利用し、時刻を取得する。
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setLenient(true);
        try
        {
            Date startDate = formatter.parse(startTimeStr);
            return startDate.getTime();
        }
        catch (ParseException ex1)
        {
            String text = Messages.getMessage("endosnipe.perfdoctor.rule.DateGetter.ErrorLabel");
            LOGGER.error(text);
        }
        return 0;
    }

    /**
     * 終了時刻を計算する。<br>
     * 開始時刻+durationで計算するが、例外が発生した場合は、開始時刻を返す。
     * 
     * @param javelinLogElement
     *            JavelinLogElement
     * @param startTime
     *            開始時刻
     * @return
     */
    private static long calculateEndTime(final JavelinLogElement javelinLogElement,
            final long startTime)
    {
        Map<String, String> extraInfoMap;
        extraInfoMap =
                       JavelinLogUtil.parseDetailInfo(javelinLogElement,
                                                      JavelinParser.TAG_TYPE_EXTRAINFO);

        // Durationを登録するMapが見つからない場合は開始時刻を返す。
        if (extraInfoMap == null)
        {
            String text = Messages.getMessage("endosnipe.perfdoctor.rule.DateGetter.ErrorLabel");
            LOGGER.error(text);
            return startTime;
        }

        // Durationが見つからない場合は開始時刻を返す。
        String durationStr = extraInfoMap.get(JavelinLogConstants.EXTRAPARAM_DURATION);
        if (durationStr == null)
        {
            String text = Messages.getMessage("endosnipe.perfdoctor.rule.DateGetter.ErrorLabel");
            LOGGER.error(text);
            return startTime;
        }

        try
        {
            return startTime + Long.valueOf(durationStr);
        }
        catch (NumberFormatException ex)
        {
            String text = Messages.getMessage("endosnipe.perfdoctor.rule.DateGetter.ErrorLabel");
            LOGGER.error(text);
            return startTime;
        }
    }

    /**
     * JavelinLogElementからクラス名を取得する。
     * 
     * @param element {@link JavelinLogElement}オブジェクト
     * @return クラス名
     */
    private static String getClassName(final JavelinLogElement element)
    {
        List<?> baseInfo = element.getBaseInfo();

        String id = (String)baseInfo.get(JavelinLogColumnNum.ID);
        if (JavelinConstants.MSG_CALL.equals(id) == true)
        {
            return (String)baseInfo.get(JavelinLogColumnNum.CALL_CALLEE_CLASS);
        }
        else if (JavelinConstants.MSG_RETURN.equals(id) == true)
        {
            return (String)baseInfo.get(JavelinLogColumnNum.RETURN_CALLEE_CLASS);
        }
        else if (JavelinConstants.MSG_THROW.equals(id) == true)
        {
            return (String)baseInfo.get(JavelinLogColumnNum.THROW_THROWER_CLASS);
        }
        else if (JavelinConstants.MSG_EVENT.equals(id) == true)
        {
            return (String)baseInfo.get(JavelinLogColumnNum.EVENT_CLASS);
        }

        return "unknown";
    }

    /**
     * JavelinLogElementからメソッド名を取得する。
     * 
     * @param element {@link JavelinLogElement}オブジェクト
     * @return メソッド名
     */
    private static String getMethodName(final JavelinLogElement element)
    {
        List<?> baseInfo = element.getBaseInfo();

        String id = (String)baseInfo.get(JavelinLogColumnNum.ID);
        if (JavelinConstants.MSG_CALL.equals(id) == true)
        {
            return (String)baseInfo.get(JavelinLogColumnNum.CALL_CALLEE_METHOD);
        }
        else if (JavelinConstants.MSG_RETURN.equals(id) == true)
        {
            return (String)baseInfo.get(JavelinLogColumnNum.RETURN_CALLEE_METHOD);
        }
        else if (JavelinConstants.MSG_THROW.equals(id) == true)
        {
            return (String)baseInfo.get(JavelinLogColumnNum.THROW_THROWER_METHOD);
        }
        else if (JavelinConstants.MSG_EVENT.equals(id) == true)
        {
            return (String)baseInfo.get(JavelinLogColumnNum.EVENT_METHOD);
        }

        return "unknown";
    }

    /**
     * メッセージを取得する。
     * 
     * @param messageId
     *            メッセージID。
     * @param args
     *            メッセージの引数。
     * @return メッセージ。
     */
    static String getMessage(final String messageId, final Object[] args)
    {
        ResourceBundle bundle = ResourceBundle.getBundle(PERFDOCTOR_MESSAGES);

        try
        {
            String pattern = bundle.getString(messageId);
            MessageFormat format = new MessageFormat(pattern);
            String message = format.format(args);

            return message;
        }
        catch (MissingResourceException mre)
        {
            LOGGER.error(mre.getMessage(), mre);
        }

        return "";
    }
}
