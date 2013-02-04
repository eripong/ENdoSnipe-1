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
package jp.co.acroquest.endosnipe.perfdoctor.rule.def;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ルールセット定義クラス。
 * @author tanimoto
 *
 */
@XmlRootElement(name = "ruleSet")
@XmlAccessorType(XmlAccessType.NONE)
public class RuleSetDef implements Serializable
{
    /** シリアルID */
    private static final long serialVersionUID = 1L;

    /** ルールセット名 */
    @XmlAttribute
    private String            name_;

    /** ルール定義一覧 */
    @XmlElement(name = "rule")
    private List<RuleDef>     ruleDefs_;

    /**
     * デフォルトコンストラクタ。<br />
     */
    public RuleSetDef()
    {
        // Do nothing.
    }

    /**
     * コピーコンストラクタ。<br />
     *
     * @param ruleSetDef コピー元
     */
    public RuleSetDef(final RuleSetDef ruleSetDef)
    {
        this.name_ = ruleSetDef.name_;
        if (ruleSetDef.ruleDefs_ != null)
        {
            this.ruleDefs_ = new ArrayList<RuleDef>();
            for (RuleDef ruleDef : ruleSetDef.ruleDefs_)
            {
                this.ruleDefs_.add(new RuleDef(ruleDef));
            }
        }
    }

    /**
     * ルールセット名を取得する。
     * @return ルールセット名
     */
    public String getName()
    {
        return this.name_;
    }

    /**
     * ルールセット名を設定する。
     * @param name ルールセット名
     */
    public void setName(final String name)
    {
        this.name_ = name;
    }

    /**
     * ルール定義一覧を取得する。
     * @return ruleDefs
     */
    public List<RuleDef> getRuleDefs()
    {
        return this.ruleDefs_;
    }

    /**
     * ルール定義一覧を設定する。
     * @param ruleDefs ルール定義一覧
     */
    public void setRuleDefs(final List<RuleDef> ruleDefs)
    {
        this.ruleDefs_ = ruleDefs;
    }
}
