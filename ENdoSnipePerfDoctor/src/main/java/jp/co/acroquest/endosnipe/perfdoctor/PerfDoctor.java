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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogAccessor;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.javelin.parser.ParseException;
import jp.co.acroquest.endosnipe.perfdoctor.exception.RuleCreateException;
import jp.co.acroquest.endosnipe.perfdoctor.exception.RuleNotFoundException;
import jp.co.acroquest.endosnipe.perfdoctor.rule.RuleManager;

/**
 * パフォーマンスドクター
 * 
 * @author eriguchi
 */
public class PerfDoctor
{
    private static final ENdoSnipeLogger LOGGER = ENdoSnipeLogger.getLogger(PerfDoctor.class, null);

    /**
     * パフォーマンスの判定を実施するルールのリスト。
     */
    private List<PerformanceRule>        ruleList_;

    /**
     * デフォルトコンストラクタ。
     */
    public PerfDoctor()
    {
        // Do nothing.
    }

    /**
     * オブジェクトを初期化します。<br />
     *
     * @throws RuleNotFoundException ファイルが見つからない場合
     * @throws RuleCreateException ルールの読み込みに失敗した場合
     */
    public void init()
        throws RuleNotFoundException,
            RuleCreateException
    {
        this.ruleList_ = RuleManager.getInstance().getActiveRules();
    }

    /**
     * ルールの個数を返します。<br />
     *
     * @return ルールの個数
     */
    public int getRuleCount()
    {
        if (this.ruleList_ == null)
        {
            return 0;
        }
        return this.ruleList_.size();
    }

    /**
     * アクティブなルールにデフォルトルールをコピーしてファイルを作成し、
     * オブジェクトを初期化します。<br />
     *
     * @throws RuleNotFoundException ファイルが見つからない場合
     * @throws RuleCreateException ルールの読み込みに失敗した場合
     */
    public void copyDefaultToActiveRule()
        throws RuleNotFoundException,
            RuleCreateException
    {
        RuleManager manager = RuleManager.getInstance();
        manager.commit();
        init();
    }

    /**
     * 指定したJavelinログファイルを読み込み、
     * 解析を行う。
     * 解析した結果、警告となったものを返す。
     * 
     * @param elementList ログファイルの内容
     * @return 警告となった結果の要素のリスト
     */
    public List<WarningUnit> judgeJavelinLog(final List<JavelinLogElement> elementList)
    {
        List<WarningUnit> result = new ArrayList<WarningUnit>();

        if (elementList == null)
        {
            return result;
        }

        for (PerformanceRule rule : this.ruleList_)
        {
            try
            {
                List<WarningUnit> warningList = null;
                warningList = rule.judge(elementList);
                if (warningList != null)
                {
                    result.addAll(warningList);
                }
            }
            catch (RuntimeException ex)
            {
                LOGGER.error(Messages.getMessage(PerfConstants.PERF_DOCTOR_RUNTIME_EXCEPTION), ex);
            }
        }

        return result;
    }

    /**
     * ログファイルを {@link JavelinLogElement} のリストに変換します。<br />
     *
     * @param logAccessor Javelin ログデータ取得オブジェクト
     * @return 変換したリスト
     */
    public List<JavelinLogElement> parseJavelinLogFile(final JavelinLogAccessor logAccessor)
    {
        JavelinParser javelinParser = new JavelinParser(logAccessor);
        return parseJavelinLogFileInternal(javelinParser);
    }

    /**
     * ログファイルを {@link JavelinLogElement} のリストに変換します。<br />
     *
     * @param javelinParser Javelin ログパーサ
     * @return 変換したリスト
     */
    private List<JavelinLogElement> parseJavelinLogFileInternal(final JavelinParser javelinParser)
    {
        List<JavelinLogElement> elementList = null;
        try
        {
            // パーサを初期化する。
            javelinParser.init();

            // 一要素ずつ取得し、パフォーマンスのルール違反を
            // リストに格納する。
            elementList = new ArrayList<JavelinLogElement>();
            JavelinLogElement javelinLogElement;
            while ((javelinLogElement = javelinParser.nextElement()) != null)
            {
                elementList.add(javelinLogElement);
            }

            JavelinParser.initDetailInfo(elementList);
        }
        catch (ParseException pe)
        {
            LOGGER.error(pe.getMessage(), pe);
        }
        catch (IOException ex)
        {
            LOGGER.error(ex.getMessage(), ex);
        }
        finally
        {
            try
            {
                javelinParser.close();
            }
            catch (IOException ioe)
            {
                LOGGER.error(ioe.getMessage(), ioe);
            }
        }
        return elementList;
    }
}
