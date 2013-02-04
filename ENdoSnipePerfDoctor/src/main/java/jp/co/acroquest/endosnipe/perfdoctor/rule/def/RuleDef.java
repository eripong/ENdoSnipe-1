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

/**
 * ルール定義クラス。
 * @author tanimoto
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class RuleDef implements Serializable
{
    /** シリアルID */
    private static final long  serialVersionUID = 1L;

    /** ルールID */
    @XmlAttribute
    private String             id_;

    /** ルールのクラス名 */
    @XmlAttribute
    private String             className_;

    /** ルールが有効かどうか */
    @XmlAttribute
    private String             enabled_;

    /** レベル別定義 */
    @XmlElement(name = "ruleLevel")
    private List<RuleLevelDef> ruleLevelDefs_;

    /**
     * デフォルトコンストラクタ。<br />
     */
    public RuleDef()
    {
        // Do nothing.
    }

    /**
     * コピーコンストラクタ。<br />
     *
     * @param ruleDef コピー元
     */
    public RuleDef(final RuleDef ruleDef)
    {
        this.id_ = ruleDef.id_;
        this.className_ = ruleDef.className_;
        this.enabled_ = ruleDef.enabled_;
        if (ruleDef.ruleLevelDefs_ != null)
        {
            this.ruleLevelDefs_ = new ArrayList<RuleLevelDef>();
            for (RuleLevelDef ruleLevelDef : ruleDef.ruleLevelDefs_)
            {
                this.ruleLevelDefs_.add(new RuleLevelDef(ruleLevelDef));
            }
        }
    }

    /**
     * ルールのクラス名を取得する。
     * @return ルールのクラス名
     */
    public String getClassName()
    {
        return this.className_;
    }

    /**
     * ルールのクラス名を設定する。
     * @param className ルールのクラス名
     */
    public void setClassName(final String className)
    {
        this.className_ = className;
    }

    /**
     * ルールが有効かどうかを取得する。
     * @return ルールが有効かどうか
     */
    public String getEnabled()
    {
        return this.enabled_;
    }

    /**
     * ルールが有効かどうかを設定する。
     * @param enabled ルールが有効かどうか
     */
    public void setEnabled(final String enabled)
    {
        this.enabled_ = enabled;
    }

    /**
     * ルールIDを取得する。
     * @return ルールID
     */
    public String getId()
    {
        return this.id_;
    }

    /**
     * ルールIDを設定する。
     * @param id ルールID
     */
    public void setId(final String id)
    {
        this.id_ = id;
    }

    /**
     * レベル別定義を取得する。
     * @return ruleLevelDefs レベル別定義
     */
    public List<RuleLevelDef> getRuleLevelDefs()
    {
        return this.ruleLevelDefs_;
    }

    /**
     * レベル別定義を設定する。
     * @param ruleLevelDefs レベル別定義
     */
    public void setRuleLevelDefs(final List<RuleLevelDef> ruleLevelDefs)
    {
        this.ruleLevelDefs_ = ruleLevelDefs;
    }
}
