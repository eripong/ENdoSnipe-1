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
package jp.co.acroquest.endosnipe.perfdoctor.rule;

import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.Messages;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRule;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnitUtil;

/**
 * PerformanceDoctorルールの抽象クラス。
 * ルール開発者は、このクラスを継承してルールを作成する。
 * 
 * @author tanimoto
 * 
 */
public abstract class AbstractRule implements PerformanceRule
{
    private static final ENdoSnipeLogger LOGGER             =
                                                              ENdoSnipeLogger.getLogger(AbstractRule.class,
                                                                                        null);

    /** ルールID */
    public String                        id;

    /** ルールが有効かどうか */
    public boolean                       active;

    /** ルールの問題レベル */
    public String                        level;

    /** ルールを有効にするdurationの閾値 */
    public long                          durationThreshold;

    /** エラーが起きたJavelinLogElementのリスト */
    private List<JavelinLogElement>      errorJavelinLogElementList_;

    /** エラーの引数リスト。要素数は必ずjavelinLogElementList_と一致する。 */
    private List<Object[]>               argsList_;

    /** 検出した警告のリスト。 */
    private List<WarningUnit>            warningList_;

    /** duration閾値として利用する文字列を抽出するStrategy。 */
    private ThresholdStrategy            thresholdStrategy_ = new DefaultThresholdStrategy();

    /**
     * ルールの判定処理を行う。<br>
     * ただし、ルールが無効な場合、引数がnullである場合には判定そのものを行わず、要素数0のListを返す。
     * また、durationが閾値を下回っているJavelinLogElementについては判定処理を行わない。
     * 
     * @param javelinLogElementList JavelinLogElementのリスト
     * @return 警告ユニットの一覧を表すリスト
     */
    public List<WarningUnit> judge(final List<JavelinLogElement> javelinLogElementList)
    {
        if (this.active == false)
        {
            return new ArrayList<WarningUnit>(0);
        }

        // TODO: durationThresholdによるチェック(暫定実装)
        List<JavelinLogElement> targetList = new ArrayList<JavelinLogElement>();
        for (JavelinLogElement javelinLogElement : javelinLogElementList)
        {

            String durationString =
                                    getThresholdStrategy().extractDurationThreshold(javelinLogElement);

            // TATの値が取得できなかった場合は、0として扱う
            if (durationString == null)
            {
                durationString = "0";
            }

            long duration = 0;
            try
            {
                duration = Long.parseLong(durationString);
            }
            //durationStringがlong型に変換できない文字列だった場合は判定を行わない。
            catch (NumberFormatException exception)
            {
                targetList.add(javelinLogElement);
                continue;
            }

            if (this.durationThreshold <= duration)
            {
                targetList.add(javelinLogElement);
            }
        }

        this.errorJavelinLogElementList_ = new ArrayList<JavelinLogElement>();
        this.argsList_ = new ArrayList<Object[]>();
        this.warningList_ = new ArrayList<WarningUnit>();

        doJudge(targetList);

        List<WarningUnit> warningList = createWarningUnitList();
        return warningList;
    }

    /**
     * 追加されたエラーより、警告ユニットの一覧を表すリストを作成します。<br />
     * 
     * @return 警告ユニット一覧
     */
    protected List<WarningUnit> createWarningUnitList()
    {
        List<WarningUnit> warningList = new ArrayList<WarningUnit>(this.warningList_);
        return warningList;
    }

    /**
     * ルールの設定値に対して初期化を行います。<br />
     *
     * 本メソッドは、フィールドの値がセットされた後、doJudgeが呼ばれる前に呼び出されます。<br />
     * ルールクラス実装者は、必要であれば本メソッドをオーバーライドしてください。<br />
     */
    public void init()
    {
        // Do Nothing.
    }

    /**
     * ルールの判定処理を行います。<br />
     * 
     * @param javelinLogElementList JavelinLogElementのリスト
     */
    public abstract void doJudge(List<JavelinLogElement> javelinLogElementList);

    /**
     * エラーを追加します。<br />
     * このメソッドはイベント以外の警告を発生させるために利用するメソッドです。<br />
     * イベントによる警告を発生させる場合には利用しないでください。<br />
     * 
     * @param element {@link JavelinLogElement}オブジェクト
     * @param args メッセージの引数
     */
    protected synchronized void addError(final JavelinLogElement element, final Object... args)
    {
        addError(element, true, args);
    }

    /**
     * エラーを追加します。<br />
     * 
     * @param element {@link JavelinLogElement}オブジェクト
     * @param isDescend フィルタ時に降順に並べるかどうかを表すフラグ
     * @param args メッセージの引数
     */
    protected synchronized void addError(final JavelinLogElement element, final boolean isDescend,
            final Object... args)
    {
        String unitId = element.getLogFileName() + "#" + element.getStartLogLine();
        addError(unitId, element, isDescend, args);
    }

    /**
     * エラーを追加します。<br />
     * 
     * @param unitId 警告のID
     * @param element {@link JavelinLogElement}オブジェクト
     * @param args メッセージの引数
     */
    protected synchronized void addError(final String unitId, final JavelinLogElement element,
            final Object... args)
    {
        addError(unitId, element, true, args);
    }

    /**
     * エラーを追加します。<br />
     * 
     * @param unitId 警告のID
     * @param element {@link JavelinLogElement}オブジェクト
     * @param isDescend フィルタ時に降順に並べるかどうかを表すフラグ
     * @param args メッセージの引数
     */
    protected synchronized void addError(final String unitId, final JavelinLogElement element,
            final boolean isDescend, final Object[] args)
    {
        this.errorJavelinLogElementList_.add(element);
        this.argsList_.add(args);

        WarningUnit unit =
                           WarningUnitUtil.createWarningUnit(unitId, this, element, isDescend, args);
        this.warningList_.add(unit);
    }

    /**
     * エラーを追加します。<br />
     * イベントによる警告を発生させる場合には、このメソッドを利用してください。 
     * 
     * @param isEvent イベントによる警告であるかどうか。
     * @param stackTrace スタックトレース
     * @param element {@link JavelinLogElement}オブジェクト
     * @param isDescend フィルタ時に降順に並べるかどうかを表すフラグ
     * @param args メッセージの引数
     */
    protected synchronized void addError(final boolean isEvent, final String stackTrace,
            final JavelinLogElement element, final boolean isDescend, final Object... args)
    {
        String unitId = element.getLogFileName() + "#" + element.getStartLogLine();
        addError(isEvent, stackTrace, unitId, element, isDescend, args);
    }

    /**
     * エラーを追加します。<br />
     * イベントによる警告を発生させる場合には、このメソッドを利用してください。 
     * 
     * @param isEvent イベントによる警告であるかどうか。
     * @param stackTrace スタックトレース
     * @param element {@link JavelinLogElement}オブジェクト
     * @param args メッセージの引数
     */
    protected synchronized void addError(final boolean isEvent, final String stackTrace,
            final JavelinLogElement element, final Object... args)
    {
        addError(isEvent, stackTrace, element, true, args);
    }

    /**
     * エラーを追加します。<br />
     * イベントによる警告を発生させる場合には、このメソッドを利用してください。 
     * 
     * @param isEvent イベントによる警告であるかどうか。
     * @param stackTrace スタックトレース
     * @param unitId 警告のID
     * @param element {@link JavelinLogElement}オブジェクト
     * @param isDescend フィルタ時に降順に並べるかどうかを表すフラグ
     * @param args メッセージの引数
     */
    protected synchronized void addError(final boolean isEvent, final String stackTrace,
            final String unitId, final JavelinLogElement element, final boolean isDescend,
            final Object[] args)
    {
        this.errorJavelinLogElementList_.add(element);
        this.argsList_.add(args);

        WarningUnit unit =
                           WarningUnitUtil.createWarningUnit(isEvent, stackTrace, unitId, this,
                                                             element, isDescend, args);
        this.warningList_.add(unit);
    }

    /**
     * エラーを追加します。<br />
     * イベントによる警告を発生させる場合には、このメソッドを利用してください。 
     *
     * @param warningUnitList エラーのリスト
     */
    protected synchronized void addError(final List<WarningUnit> warningUnitList)
    {
        this.warningList_.addAll(warningUnitList);
    }

    /**
     * エラーを追加します。<br />
     * 
     * @param messageId メッセージID
     * @param args メッセージの引数。
     */
    protected synchronized void addValidationError(final String messageId, final Object... args)
    {
        // TODO: 実装追加
    }

    /**
     * ルールIDを取得します。<br />
     * 
     * @return ルールID
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * ルールの問題レベルを取得します。<br />
     * 
     * @return ルールの問題レベル
     */
    public String getLevel()
    {
        return this.level;
    }

    /**
     * duration閾値として利用する文字列を抽出するStrategyを取得します。<br />
     * 
     * @return duration閾値として利用する文字列を抽出するStrategy。
     */
    public ThresholdStrategy getThresholdStrategy()
    {
        return this.thresholdStrategy_;
    }

    /**
     * duration閾値として利用する文字列を抽出するStrategyを設定する。
     * 
     * @param thresholdStrategy duration閾値として利用する文字列を抽出するStrategy。
     */
    public void setThresholdStrategy(final ThresholdStrategy thresholdStrategy)
    {
        this.thresholdStrategy_ = thresholdStrategy;
    }

    /**
     * ルールクラスで発生した例外情報をログに出力する
     * @param message 出力するメッセージ
     * @param element メッセージに関連したJavelinLogElement
     * @param throwable 出力するThrowable
     */
    protected void log(final String message, final JavelinLogElement element,
            final Throwable throwable)
    {
        String text = "";

        if (message == null)
        {
            text = Messages.getMessage("endosnipe.perfdoctor.rule.AbstractRule.RuleLabel", this.id);
        }
        else if (element == null)
        {
            text =
                   Messages.getMessage("endosnipe.perfdoctor.rule.AbstractRule.NoElementLabel",
                                       message, this.id);
        }
        else
        {
            text =
                   Messages.getMessage("endosnipe.perfdoctor.rule.AbstractRule.ElementLabels",
                                       message, this.id, element.getLogFileName(),
                                       element.getStartLogLine());
        }
        LOGGER.error(text);
    }

    /**
     * SQL発行を表すかどうかを調べる。
     * 
     * @param className クラス名
     * @return SQL発行を表すなら <code>true</code>
     */
    public boolean isSqlExec(final String className)
    {
        return className.startsWith("jdbc:");
    }
}
