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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * プロパティ定義クラス。
 * @author tanimoto
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PropertyDef implements Serializable
{
    /** シリアルID */
    private static final long serialVersionUID = 1L;

    @XmlAttribute
    private String            name_;

    @XmlValue
    private String            value_;

    /**
     * デフォルトコンストラクタ。<br />
     */
    public PropertyDef()
    {
        // Do nothing.
    }

    /**
     * コピーコンストラクタ。<br />
     *
     * @param propertyDef コピー元
     */
    public PropertyDef(final PropertyDef propertyDef)
    {
        this.name_ = propertyDef.name_;
        this.value_ = propertyDef.value_;
    }

    /**
     * プロパティ名を取得する。
     * @return プロパティ名
     */
    public String getName()
    {
        return this.name_;
    }

    /**
     * プロパティ名を設定する。
     * @param name プロパティ名
     */
    public void setName(final String name)
    {
        this.name_ = name;
    }

    /**
     * 値を取得する。
     * @return 値
     */
    public String getValue()
    {
        return this.value_;
    }

    /**
     * 値を設定する。
     * @param value 値
     */
    public void setValue(final String value)
    {
        this.value_ = value;
    }
}
