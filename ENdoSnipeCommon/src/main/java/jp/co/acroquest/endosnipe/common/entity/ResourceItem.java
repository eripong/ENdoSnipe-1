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
package jp.co.acroquest.endosnipe.common.entity;

/**
 * リソース名と値を保存するクラス
 *
 * @author eriguchi
 */
public class ResourceItem
{
    /** リソース名(JMX計測値の場合はAttribute名) */
    private String      name_              = "";

    /** JMX計測値を取得した場合のオブジェクト名 */
    private String      objectName_        = "";

    /** 計測値の型 */
    private ItemType    itemType_;

    /** 計測値 */
    private String      value_             = null;

    /** オブジェクトの表示名 */
    private String      objectDisplayNeme_ = "";

    /** attributeの表示名 */
    private String      displayName_       = "";

    /** 計測値の表示型 */
    private DisplayType displayType_;

    /**
     * リソース名を設定する。
     * @param name リソース名
     */
    public void setName(final String name)
    {
        this.name_ = name;
    }

    /**
     * リソース名を取得する。
     * @return リソース名
     */
    public String getName()
    {
        return this.name_;
    }

    /**
     * MBean名を取得します。
     *
     * @return MBean名
     */
    public String getObjectName()
    {
        return objectName_;
    }

    /**
     * MBean名を設定します。
     *
     * @param objectName MBean名
     */
    public void setObjectName(final String objectName)
    {
        objectName_ = objectName;
    }

    /**
     * 値を設定する。
     * @param value 値
     */
    public void setValue(final String value)
    {
        this.value_ = value;
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
     * リソース値の型を返す。
     *
     * @return 型
     */
    public ItemType getItemType()
    {
        return this.itemType_;
    }

    /**
     * リソース値の型を設定する。
     *
     * @param itemType 型
     */
    public void setItemType(final ItemType itemType)
    {
        itemType_ = itemType;
    }

    /**
     * オブジェクトの表示名を取得する。
     *
     * @return オブジェクトの表示名
     */
    public String getObjectDisplayNeme()
    {
        return objectDisplayNeme_;
    }

    /**
     * オブジェクトの表示名を設定する。
     *
     * @param objectDisplayNeme オブジェクトの表示名
     */
    public void setObjectDisplayNeme(final String objectDisplayNeme)
    {
        objectDisplayNeme_ = objectDisplayNeme;
    }

    /**
     * 表示名を取得する。
     *
     * @return 表示名
     */
    public String getDisplayName()
    {
        return displayName_;
    }

    /**
     * 表示名を設定する。
     *
     * @param displayName 表示名
     */
    public void setDisplayName(final String displayName)
    {
        displayName_ = displayName;
    }

    /**
     * 表示型を取得する。
     * @return 表示型
     */
    public DisplayType getDisplayType()
    {
        return displayType_;
    }

    /**
     * 表示型を取得する。
     * @param displayType 表示型
     */
    public void setDisplayType(final DisplayType displayType)
    {
        displayType_ = displayType;
    }

    /**
     * 文字列を返す。
     *
     * @return 文字列
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(this.name_);
        builder.append("=(");
        builder.append(this.value_);
        builder.append(")");

        return builder.toString();
    }
}
