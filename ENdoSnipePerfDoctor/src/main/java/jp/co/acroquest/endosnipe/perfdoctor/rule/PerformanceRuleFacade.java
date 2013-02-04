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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRule;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;

/**
 * PerformanceRuleのファサードクラス。<br>
 * 「ERROR」「WARN」「INFO」のPerformanceRuleを保持し、順に実行する。
 * @author tanimoto
 *
 */
public class PerformanceRuleFacade implements PerformanceRule
{
    private PerformanceRule errorRule_;

    private PerformanceRule warnRule_;

    private PerformanceRule infoRule_;

    private String          id_;

    private String          level_;

    /**
     * {@inheritDoc}
     */
    public void init()
    {
        this.infoRule_.init();
        this.warnRule_.init();
        this.errorRule_.init();
    }

    /**
     * {@inheritDoc}<br>
     * 「INFO」「WARN」「ERROR」の順で判定を行い、
     * 同一のJavelinLogElementについて、後でより高いレベルの検査でも閾値を超えた場合には
     * 結果はレベルの高いものに上書きされる。<br>
     * たとえば同一のJavelinLogElementでINFO、WARNで問題を検出した場合は、
     * 最初に記録したINFOの結果がWARNにより上書きされる。
     */
    public List<WarningUnit> judge(final List<JavelinLogElement> javelinLogElementList)
    {
        PerformanceRule[] rules = {this.infoRule_, this.warnRule_, this.errorRule_};

        // 各レベルの診断結果を表すリストを格納するリスト。
        List<List<WarningUnit>> resultList = new ArrayList<List<WarningUnit>>();

        // 各レベルの診断結果を得る。
        for (PerformanceRule rule : rules)
        {
            if (rule == null)
            {
                continue;
            }

            List<WarningUnit> result = rule.judge(javelinLogElementList);

            if (result != null && result.isEmpty() == false)
            {
                resultList.add(result);
            }
        }

        if (resultList.isEmpty() == true)
        {
            return new ArrayList<WarningUnit>();
        }

        // 結果をまとめるMap。
        // 値はWarningUnit、キーは「ファイル名」と「発生したJavelinLogElementの開始行」を連結した文字列。
        Map<String, WarningUnit> totalMap = new HashMap<String, WarningUnit>();

        // 低いレベルの結果から順に記録する。
        for (List<WarningUnit> result : resultList)
        {
            for (WarningUnit unit : result)
            {
                // 後から同じ行でより高レベルの警告が発生したら上書きされる。
                String unitID = unit.getUnitId();
                totalMap.put(unitID, unit);
            }
        }

        Collection<WarningUnit> value = totalMap.values();
        return new ArrayList<WarningUnit>(value);
    }

    /**
     * {@inheritDoc}
     */
    public String getId()
    {
        return this.id_;
    }

    /**
     * {@inheritDoc}
     */
    public String getLevel()
    {
        return this.level_;
    }

    /**
     * errorRuleを設定する。
     * @param errorRule errorRule
     */
    public void setErrorRule(final PerformanceRule errorRule)
    {
        this.errorRule_ = errorRule;
    }

    /**
     * infoRuleを設定する。
     * @param infoRule infoRule
     */
    public void setInfoRule(final PerformanceRule infoRule)
    {
        this.infoRule_ = infoRule;
    }

    /**
     * warnRuleを設定する。
     * @param warnRule warnRule
     */
    public void setWarnRule(final PerformanceRule warnRule)
    {
        this.warnRule_ = warnRule;
    }
}
