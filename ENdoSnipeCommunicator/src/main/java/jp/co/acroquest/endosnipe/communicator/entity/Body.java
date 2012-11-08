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
package jp.co.acroquest.endosnipe.communicator.entity;

import java.util.Arrays;

import jp.co.acroquest.endosnipe.common.entity.ItemType;

/**
 * 電文本体のためのエンティティクラスです。<br />
 * 
 * @author y-komori
 */
public class Body
{
    /** オブジェクト名 */
    private String strObjName_ = "";

    /** オブジェクトの表示名 */
    private String strObjDispName_ = "";

    /** 項目名 */
    private String strItemName_ = "";

    /** 項目型 */
    private ItemType byteItemType_ = ItemType.ITEMTYPE_BYTE;

    /** 繰り返し回数 */
    private int intLoopCount_ = 0;

    /** 説明 */
    private Object[] objItemValueArr_ = null;

    /**
     * オブジェクト名を取得します。<br />
     * 
     * @return オブジェクト名
     */
    public String getStrObjName()
    {
        return this.strObjName_;
    }

    /**
     * オブジェクト名を設定します。<br />
     * 
     * @param strObjName オブジェクト名
     */
    public void setStrObjName(final String strObjName)
    {
        this.strObjName_ = strObjName;
    }

    /**
     * 項目名を取得します。<br />
     * 
     * @return 項目名
     */
    public String getStrItemName()
    {
        return this.strItemName_;
    }

    /**
     * 項目名を設定します。<br />
     * 
     * @param strItemName 項目名
     */
    public void setStrItemName(final String strItemName)
    {
        this.strItemName_ = strItemName;
    }

    /**
     * 項目型を取得します。<br />
     * 
     * @return 項目型
     */
    public ItemType getByteItemMode()
    {
        return this.byteItemType_;
    }

    /**
     * 項目型を設定します。<br />
     * 
     * @param byteItemMode 項目型
     */
    public void setByteItemMode(final ItemType byteItemMode)
    {
        this.byteItemType_ = byteItemMode;
    }

    /**
     * 繰り返し回数を取得します。<br />
     * 
     * @return 繰り返し回数
     */
    public int getIntLoopCount()
    {
        return this.intLoopCount_;
    }

    /**
     * 繰り返し回数を設定します。<br />
     * 
     * @param intLoopCount 繰り返し回数
     */
    public void setIntLoopCount(final int intLoopCount)
    {
        this.intLoopCount_ = intLoopCount;
    }

    /**
     * 説明を取得します。<br />
     * 
     * @return 説明
     */
    public Object[] getObjItemValueArr()
    {
        return this.objItemValueArr_;
    }

    /**
     * 説明を設定します。<br />
     * 
     * @param objItemValueArr 説明
     */
    public void setObjItemValueArr(final Object[] objItemValueArr)
    {
        this.objItemValueArr_ = objItemValueArr;
    }

    /**
     * オブジェクトの表示名を取得します。
     * 
     * @return オブジェクトの表示名
     */
    public String getStrObjDispName()
    {
        return strObjDispName_;
    }

    /**
     * オブジェクトの表示名を設定します。
     * 
     * @param strObjDispName オブジェクトの表示名
     */
    public void setStrObjDispName(String strObjDispName)
    {
        strObjDispName_ = strObjDispName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ItemName:" + this.strItemName_);
        builder.append(", ObjectName:" + this.strObjName_);
        builder.append(", ObjectDisplayName:" + this.strObjDispName_);
        builder.append(Arrays.toString(getObjItemValueArr()));
        return builder.toString();
    }
}
