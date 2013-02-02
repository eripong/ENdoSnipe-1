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
package jp.co.acroquest.endosnipe.javelin.event;

import jp.co.acroquest.endosnipe.common.event.EventConstants;

/**
 * SQL実行回数超過イベント。
 * 
 * @author eriguchi
 */
public class SqlCountOverEvent extends CommonEvent
{
    /** SQL */
    protected String sql_;

    /** イベント名とスタックトレースのセパレータ */
    private static final char SEPARATOR = '#';

    /**
     * {@inheritDoc}
     */
    @Override
    public void addParam(String key, String value)
    {
        super.addParam(key, value);
        if (EventConstants.PARAM_SQLCOUNT_SQL.equals(key))
        {
            setSql(value);
        }
    }

    /**
     * コンストラクタ。
     */
    public SqlCountOverEvent()
    {
        super();
        this.setName(EventConstants.NAME_SQLCOUNT);
    }

    /**
     * 比較用SQLを設定する。
     * 
     * @param sql SQL
     */
    private void setSql(String sql)
    {
        this.sql_ = sql;
    }

    /**
     * イベント名とSQLでハッシュコードを計算する。
     * 
     * @return ハッシュコード。
     */
    public int hashCode()
    {
        String nameAndSql = this.name_ + SEPARATOR + this.sql_;

        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((nameAndSql == null) ? 0 : nameAndSql.hashCode());
        return result;
    }

    /**
     * イベント名とSQLで比較する。
     * 
     * @param obj 比較対象。
     * @return 比較結果。
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        SqlCountOverEvent other = (SqlCountOverEvent)obj;

        String nameAndSql = this.name_ + SEPARATOR + this.sql_;
        String nameAndSqlOther = other.getName() + SEPARATOR + other.sql_;

        return nameAndSql.equals(nameAndSqlOther);
    }
}
