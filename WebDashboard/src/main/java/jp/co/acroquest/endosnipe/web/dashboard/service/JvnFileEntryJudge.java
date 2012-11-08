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
package jp.co.acroquest.endosnipe.web.dashboard.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.arrowvision.data.JavelinLogInputStreamAccessor;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.communicator.accessor.JvnFileNotifyAccessor.JvnFileEntry;
import jp.co.acroquest.endosnipe.data.entity.JavelinLog;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.javelin.parser.ParseException;
import jp.co.acroquest.endosnipe.perfdoctor.PerfDoctorIgnoreMonitor;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.PerformanceDoctorFilter;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.UnifiedFilter;
import jp.co.acroquest.endosnipe.perfdoctor.exception.RuleCreateException;
import jp.co.acroquest.endosnipe.perfdoctor.exception.RuleNotFoundException;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;

/**
 * JvnFileEntry の解析を行うクラスです。<br />
 * 
 * @author iida
 */
public class JvnFileEntryJudge
{
    /** ロガー */
    private static final ENdoSnipeLogger LOGGER =
                                                  ENdoSnipeLogger.getLogger(JvnFileEntryJudge.class);

    /**
     * 指定された JvnFileEntry を PerformanceDoctor で解析し、
     * その結果を WarningUnit のリストで返します。<br />
     * 
     * @param entries 解析対象の JvnFileEntry
     * @param eliminateSameCause 同一原因の警告を除外するかどうか
     * @param summarySameRule 同一ルールで絞り込むかどうか
     * @return 解析結果の WarningUnit のリスト
     */
    public List<WarningUnit> judge(JvnFileEntry[] entries, boolean eliminateSameCause,
            boolean summarySameRule)
    {

        List<JavelinLogElement> javelinLogElementList = new ArrayList<JavelinLogElement>();

        createJvnLogElementList(entries, javelinLogElementList);

        List<WarningUnit> warningUnitList =
                                            judgeJvnLogElement(eliminateSameCause, summarySameRule,
                                                               javelinLogElementList);

        return warningUnitList;
    }

    /**
     * 指定された JvnFileEntry を PerformanceDoctor で解析し、
     * その結果を WarningUnit のリストで返します。<br />
     * 
     * @param jvnLogList 解析対象の{@link JavelinLog}のリスト
     * @param eliminateSameCause 同一原因の警告を除外するかどうか
     * @param summarySameRule 同一ルールで絞り込むかどうか
     * @return 解析結果の WarningUnit のリスト
     */
    public List<WarningUnit> judge(List<JavelinLog> jvnLogList, boolean eliminateSameCause,
            boolean summarySameRule)
    {

        List<JavelinLogElement> javelinLogElementList = new ArrayList<JavelinLogElement>();

        createJvnLogElementList(jvnLogList, javelinLogElementList);

        List<WarningUnit> warningUnitList =
                                            judgeJvnLogElement(eliminateSameCause, summarySameRule,
                                                               javelinLogElementList);
        return warningUnitList;
    }

    /**
     * JvnFileEntryからJavelinLogElementのリストを生成します。
     * @param entries {@link JvnFileEntry}の配列
     * @param javelinLogElementList {@link JavelinLogElement}のリスト
     */
    private void createJvnLogElementList(JvnFileEntry[] entries,
            List<JavelinLogElement> javelinLogElementList)
    {
        for (JvnFileEntry entry : entries)
        {

            // JavelinLog → JavelinLogElement
            InputStream javelinLogStream = null;
            try
            {
                javelinLogStream = new ByteArrayInputStream(entry.contents.getBytes("UTF-8"));
            }
            catch (UnsupportedEncodingException ex)
            {
                LOGGER.log(LogMessageCodes.UNSUPPORTED_CHARSET, ex);
                continue;
            }

            JavelinLogInputStreamAccessor javelinLogMemoryAccessor =
                                                                     new JavelinLogInputStreamAccessor(
                                                                                                       entry.fileName,
                                                                                                       javelinLogStream);

            parseJvnLogStream(javelinLogElementList, javelinLogStream, javelinLogMemoryAccessor);
        }
    }

    /**
     * JvnFileEntryからJavelinLogElementのリストを生成します。
     * @param entries {@link JvnFileEntry}の配列
     * @param javelinLogElementList {@link JavelinLogElement}のリスト
     */
    private void createJvnLogElementList(List<JavelinLog> jvnLogList,
            List<JavelinLogElement> javelinLogElementList)
    {
        for (JavelinLog jvnLog : jvnLogList)
        {

            InputStream javelinLogStream = jvnLog.javelinLog;
            if (javelinLogStream == null)
            {
                continue;
            }

            JavelinLogInputStreamAccessor javelinLogMemoryAccessor =
                                                                     new JavelinLogInputStreamAccessor(
                                                                                                       jvnLog.logFileName,
                                                                                                       javelinLogStream);

            parseJvnLogStream(javelinLogElementList, javelinLogStream, javelinLogMemoryAccessor);
        }
    }

    /**
     * JavelinLogElementのリストに対して、パフォーマンスドクターを実行する。
     * @param eliminateSameCause 同一原因の警告を除外するかどうか
     * @param summarySameRule 同一ルールで絞り込むかどうか
     * @param javelinLogElementList {@link JavelinLogElement}のリスト
     * @return 解析結果の WarningUnit のリスト
     */
    private List<WarningUnit> judgeJvnLogElement(boolean eliminateSameCause,
            boolean sumamrySameRule, List<JavelinLogElement> javelinLogElementList)
    {
        JavelinParser.initDetailInfo(javelinLogElementList);
        // 得られた JvelinLogElement を PerformanceDoctor で解析し、結果を保持します。
        PerfDoctorIgnoreMonitor perfDoctor = new PerfDoctorIgnoreMonitor();
        try
        {
            perfDoctor.init();
        }
        catch (RuleNotFoundException ex)
        {
            LOGGER.log(LogMessageCodes.CANNOT_FIND_PERFRULE, ex);
            return new ArrayList<WarningUnit>();
        }
        catch (RuleCreateException ex)
        {
            LOGGER.log(LogMessageCodes.FAIL_TO_CREATE_PERFRULE, ex);
            return new ArrayList<WarningUnit>();
        }
        List<WarningUnit> warningUnitList = perfDoctor.judgeJavelinLog(javelinLogElementList);
        warningUnitList = this.doFilter(warningUnitList, eliminateSameCause, sumamrySameRule);
        return warningUnitList;
    }

    /**
     * Javelinログのストリームを解析し、JavelinLogElementオブジェクトを作成します。
     * @param javelinLogElementList {@link JavelinLogElement}オブジェクトのリスト
     * @param javelinLogStream Javelinログのストリーム
     * @param javelinLogMemoryAccessor {@link JavelinLogInputStreamAccessor}オブジェクト
     */
    private void parseJvnLogStream(List<JavelinLogElement> javelinLogElementList,
            InputStream javelinLogStream, JavelinLogInputStreamAccessor javelinLogMemoryAccessor)
    {
        JavelinParser javelinParser = new JavelinParser(javelinLogMemoryAccessor);

        try
        {
            javelinParser.init();
            JavelinLogElement javelinLogElement;
            while ((javelinLogElement = javelinParser.nextElement()) != null)
            {
                javelinLogElementList.add(javelinLogElement);
            }
        }
        catch (ParseException ex)
        {
            LOGGER.log(LogMessageCodes.FAIL_PARSE_JVNLOG, ex);
        }
        catch (IOException ex)
        {
            LOGGER.log(LogMessageCodes.IO_ERROR, ex);
		}
        finally
        {
            if (javelinLogStream != null)
            {
                try
                {
                    javelinLogStream.close();
                }
                catch (IOException ex)
                {
                    LOGGER.log(LogMessageCodes.IO_ERROR, ex);
                }
            }
        }
    }

    /**
     * リストにフィルターをかける。
     * 
     * @param resultList 測定結果のリスト
     * @param cond データの出力条件
     * @param eliminateSameCause 同要因フィルタを設定するかどうか
     * @param summarySameRule 同種警告フィルタを設定するかどうか
     * @return フィルターをかけた後の、測定結果のリスト
     */
    private List<WarningUnit> doFilter(final List<WarningUnit> resultList,
            final boolean eliminateSameCause, final boolean summarySameRule)
    {
        PerformanceDoctorFilter sameFilter = new PerformanceDoctorFilter();
        UnifiedFilter unifiedFilter = new UnifiedFilter();
        List<WarningUnit> filteredList = resultList;

        // 同要因フィルタと同種警告フィルタを設定する。
        if (eliminateSameCause)
        {
            filteredList = unifiedFilter.doFilter(filteredList);
        }
        if (summarySameRule)
        {
            filteredList = sameFilter.doFilter(filteredList);
        }

        return filteredList;
    }
}
