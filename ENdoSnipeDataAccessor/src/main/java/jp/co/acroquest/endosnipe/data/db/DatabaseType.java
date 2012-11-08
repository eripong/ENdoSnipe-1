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
package jp.co.acroquest.endosnipe.data.db;

/**
 * データベースの種類を表す列挙体。<br /> 
 *
 * @author sakamoto
 */
public enum DatabaseType
{
    /** H2 */
    H2("h2"),

    /** PostgreSQL */
    POSTGRES("postgres");

    /** データベースの種類を表す ID */
    private final String id_;

    /**
     * データベースの種類を表す項目を生成します。<br />
     *
     * @param id データベースの種類を表す ID
     */
    private DatabaseType(final String id)
    {
        this.id_ = id;
    }

    /**
     * データベースの種類を表す ID を返します。<br />
     *
     * @return ID 文字列
     */
    public String getId()
    {
        return this.id_;
    }

    /**
     * データベースの種類を表す ID から、オブジェクトを返します。<br />
     *
     * @param id データベースの種類を表す ID
     * @return ID に対応する種類が存在する場合はオブジェクト、存在しない場合は <code>null</code>
     */
    public static DatabaseType fromId(final String id)
    {
        for (DatabaseType databaseType : values())
        {
            if (databaseType.getId().equals(id))
            {
                return databaseType;
            }
        }
        return null;
    }
}
