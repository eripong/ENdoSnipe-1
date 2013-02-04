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
 * レベルごとのプロパティ定義クラス。
 * @author tanimoto
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class RuleLevelDef implements Serializable
{
    /** シリアルID */
    private static final long serialVersionUID = 1L;

    /** ルールの問題レベル */
    @XmlAttribute
    private String            level_;

    /** レベルが有効かどうか */
    @XmlAttribute
    private String            enabled_;

    /** durationの閾値 */
    @XmlAttribute
    private String            durationThreshold_;

    /** プロパティ定義一覧 */
    @XmlElement(name = "property")
    private List<PropertyDef> propertyDefs_;

    /**
     * デフォルトコンストラクタ。<br />
     */
    public RuleLevelDef()
    {
        // Do nothing.
    }

    /**
     * コピーコンストラクタ。<br />
     *
     * @param ruleLevelDef コピー元
     */
    public RuleLevelDef(final RuleLevelDef ruleLevelDef)
    {
        this.level_ = ruleLevelDef.level_;
        this.enabled_ = ruleLevelDef.enabled_;
        this.durationThreshold_ = ruleLevelDef.durationThreshold_;
        if (ruleLevelDef.propertyDefs_ != null)
        {
            this.propertyDefs_ = new ArrayList<PropertyDef>();
            for (PropertyDef propertyDef : ruleLevelDef.propertyDefs_)
            {
                this.propertyDefs_.add(new PropertyDef(propertyDef));
            }
        }
    }

    /**
     * durationの閾値を取得する。
     * @return durationの閾値
     */
    public String getDurationThreshold()
    {
        return this.durationThreshold_;
    }

    /**
     * durationの閾値を設定する。
     * @param durationThreshold durationの閾値
     */
    public void setDurationThreshold(final String durationThreshold)
    {
        this.durationThreshold_ = durationThreshold;
    }

    /**
     * レベルが有効かどうかを取得する。
     * @return レベルが有効かどうか
     */
    public String getEnabled()
    {
        return this.enabled_;
    }

    /**
     * レベルが有効かどうかを設定する。
     * @param enabled レベルが有効かどうか
     */
    public void setEnabled(final String enabled)
    {
        this.enabled_ = enabled;
    }

    /**
     * ルールの問題レベルを取得する。
     * @return ルールの問題レベル
     */
    public String getLevel()
    {
        return this.level_;
    }

    /**
     * ルールの問題レベルを設定する。
     * @param level ルールの問題レベル
     */
    public void setLevel(final String level)
    {
        this.level_ = level;
    }

    /**
     * プロパティ定義一覧を取得する。
     * @return propertyDefs
     */
    public List<PropertyDef> getPropertyDefs()
    {
        return this.propertyDefs_;
    }

    /**
     * プロパティ定義一覧を設定する。
     * @param propertyDefs プロパティ定義一覧
     */
    public void setPropertyDefs(final List<PropertyDef> propertyDefs)
    {
        this.propertyDefs_ = propertyDefs;
    }
}
