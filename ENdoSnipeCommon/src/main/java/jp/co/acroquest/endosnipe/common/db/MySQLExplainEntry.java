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
package jp.co.acroquest.endosnipe.common.db;

/**
 * MySQLの実行計画の1要素。
 * 
 * http://dev.mysql.com/doc/refman/5.1/ja/explain.html
 * 
 * @author eriguchi
 *
 */
public class MySQLExplainEntry
{
    /** SELECT識別子。クエリ内におけるこの SELECTの順序番号。 */
    private String id_;

    /** SELECT節の種類。 */
    private String selectType_;

    /** 結果を得るために参照するテーブル。  */
    private String table_;

    /** 結合型。 */
    private String type_;

    /** このテーブル内のレコードの検索に MySQL で使用可能なインデックス。 */
    private String possibleKeys_;

    /** MySQL が実際に使用を決定したキー（インデックス）。  */
    private String key_;

    /** MySQL が実際に使用を決定したキーの長さ。 keyが NULLの場合、この長さは NULLになる。*/
    private String keyLen_;

    /** テーブルからレコードを選択する際に keyとともに使用されるカラムまたは定数。  */
    private String ref_;

    /** クエリの実行に際して調べる必要があると MySQL によって判定されたレコードの数。 */
    private int rows_;

    /** MySQL でどのようにクエリが解決されるかに関する追加情報が記載される。 */
    private String extra_;

    /**
     * コンストラクタ。
     */
    public MySQLExplainEntry()
    {
    }

    /**
     * SELECT識別子を取得する。
     * @return SELECT識別子。
     */
    public String getId()
    {
        return id_;
    }

    /**
     * SELECT節の種類を取得する。
     * @return SELECT節の種類。
     */
    public String getSelectType()
    {
        return selectType_;
    }

    /**
     * 結果を得るために参照するテーブルを取得する。
     * @return 結果を得るために参照するテーブル。
     */
    public String getTable()
    {
        return table_;
    }

    /**
     * 結合型を取得する。
     * @return 結合型。
     */

    public String getType()
    {
        return type_;
    }

    /**
     * 使用可能なインデックスを取得する。
     * @return 使用可能なインデックス。
     */
    public String getPossibleKeys()
    {
        return possibleKeys_;
    }

    /**
     * 実際に使用を決定したキー（インデックス）を取得する。
     * @return 実際に使用を決定したキー（インデックス）。
     */
    public String getKey()
    {
        return key_;
    }

    /**
     * 実際に使用を決定したキーの長さを取得する。
     * @return 実際に使用を決定したキーの長さ。
     */
    public String getKeyLen()
    {
        return keyLen_;
    }

    /**
     * テーブルからレコードを選択する際に keyとともに使用されるカラムまたは定数を取得する。
     * @return テーブルからレコードを選択する際に keyとともに使用されるカラムまたは定数。
     */
    public String getRef()
    {
        return ref_;
    }

    /**
     * レコードの数を取得する。
     * @return レコードの数。
     */
    public int getRows()
    {
        return rows_;
    }

    /**
     * 追加情報を取得する。
     * @return 追加情報。
     */
    public String getExtra()
    {
        return extra_;
    }

    /**
     * 
     * @param id SELECT識別子。クエリ内におけるこの SELECTの順序番号。
     */
    public void setId(final String id)
    {
        id_ = id;
    }

    /**
     * 
     * @param selectType SELECT節の種類。
     */
    public void setSelectType(final String selectType)
    {
        selectType_ = selectType;
    }

    /**
     * 
     * @param table 結果を得るために参照するテーブル。
     */
    public void setTable(final String table)
    {
        table_ = table;
    }

    /**
     * 
     * @param type 結合型。
     */
    public void setType(final String type)
    {
        type_ = type;
    }

    /**
     * 
     * @param possibleKeys このテーブル内のレコードの検索に MySQL で使用可能なインデックス。
     */
    public void setPossibleKeys(final String possibleKeys)
    {
        possibleKeys_ = possibleKeys;
    }

    /**
     * 
     * @param key MySQL が実際に使用を決定したキー（インデックス）。
     */
    public void setKey(final String key)
    {
        key_ = key;
    }

    /**
     * 
     * 
     * @param keyLen MySQL が実際に使用を決定したキーの長さ。 keyが NULLの場合、この長さは NULLになる。
     */
    public void setKeyLen(final String keyLen)
    {
        keyLen_ = keyLen;
    }

    /**
     * 
     * @param ref テーブルからレコードを選択する際に keyとともに使用されるカラムまたは定数。
     */
    public void setRef(final String ref)
    {
        ref_ = ref;
    }

    /**
     * 
     * @param rows クエリの実行に際して調べる必要があると MySQL によって判定されたレコードの数。
     */
    public void setRows(final int rows)
    {
        rows_ = rows;
    }

    /**
     * 
     * @param extra MySQL でどのようにクエリが解決されるかに関する追加情報が記載される。
     */
    public void setExtra(final String extra)
    {
        extra_ = extra;
    }
}
