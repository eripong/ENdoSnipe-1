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
package jp.co.acroquest.endosnipe.javelin.jdbc.common;

import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import jp.co.acroquest.endosnipe.javelin.jdbc.stats.JdbcJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.JdbcJavelinStatement;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import jp.co.acroquest.endosnipe.javelin.util.TreeMap;

/**
 * SQL文を操作するユーティリティ。
 * 
 * @author eriguchi
 */
public class SqlUtil
{
    private static final int DML_KEY_STR_LENGTH = 6;

    /**
     * インスタンス化を避けるためのコンストラクタ。
     */
    private SqlUtil()
    {
        // Do Nothing
        return;
    }

    /**
     * sqlを大文字に変更し、先頭にあるコメントを除去する。
     * @param sql コメント除去を行うSQL
     * @return 処理を行ったSQL
     */
    public static String removeHeadComment(final String sql)
    {
        String rawSql = sql;
        while (true)
        {
            rawSql = rawSql.trim();
            // コメント(--)であるかを調べ、改行までを取り除く
            if (rawSql.startsWith("--"))
            {
                rawSql = rawSql.substring(rawSql.indexOf('\n') + 1);
                continue;
            }

            // コメント(/* ～ */)であるかを調べ、/* ～ */ までを取り除く。
            if (rawSql.startsWith("/*"))
            {
                int nextEnd = rawSql.indexOf("*/", JdbcJavelinRecorder.COMMENT_HEADER_LENGTH);
                // 次に"*/"が出現する場所を探し、コメントを削除し、処理を繰り返す。
                // 見つからなかったら、nullを返す。

                if (nextEnd != JdbcJavelinRecorder.NOT_FOUND)
                {
                    rawSql = rawSql.substring(nextEnd + JdbcJavelinRecorder.COMMENT_FOOTER_LENGTH);
                    continue;
                }
                return null;
            }
            break;
        }
        return rawSql;
    }

    /**
     * SQLがDMLであるかチェックする。
     * @param sql SQL文
     * @return SQLがDMLであればtrueを、それ以外はfalseを返す。
     */
    public static boolean checkDml(final String sql)
    {
        String rawSql = null;
        // SQL分の前のコメントを全て取り除くまでループ。
        // コメントが不正な場合に限りfalseが返される。
        rawSql = SqlUtil.removeHeadComment(sql);
        if (rawSql == null)
        {
            return false;
        }

        // DMLならtrueを返す
        if(rawSql.length() < DML_KEY_STR_LENGTH)
        {
            return false;
        }
        
        String keyStr = rawSql.substring(0, DML_KEY_STR_LENGTH);
        if (keyStr.equalsIgnoreCase("SELECT") || keyStr.equalsIgnoreCase("UPDATE")
                || keyStr.equalsIgnoreCase("INSERT") || keyStr.equalsIgnoreCase("DELETE"))
        {
            return true;
        }
        return false;
    }

    /**
     * セミコロンで区切られたSQL文をセミコロンで分割する。
     *
     * @param sql 複合SQL文
     * @return 分割されたSQL文
     */
    public static List<String> splitSqlStatement(String sql)
    {
        List<String> sqllist = new ArrayList<String>();
        // シングルクォーテーションの中にいるはtrue
        boolean singlequoteFlag = false;
        // ダブルクォーテーションの中にいる場合はtrue
        boolean doublequoteFlag = false;

        for (int index = 0; index < sql.length(); index++)
        {
            char checkchar = sql.charAt(index);

            if (checkchar == '\'')
            {
                // シングルクォーテーションがきたときは、
                // ダブルクォーテーションの中でなければ
                // シングルクォーテーションフラグを切り替える。
                if (doublequoteFlag == false)
                {
                    singlequoteFlag = !singlequoteFlag;
                }
            }
            else if (checkchar == '"')
            {
                // ダブルクォーテーションがきたときは、
                // シングルクォーテーションの中でなければ
                // ダブルクォーテーションフラグを切り替える。
                if (singlequoteFlag == false)
                {
                    doublequoteFlag = !doublequoteFlag;
                }
            }
            else if (checkchar == ';')
            {
                // セミコロンがきたときは、
                // シングルクォーテーションの中でもダブルクォーテーションの中でもない場合に
                // 分割する。
                if (singlequoteFlag == false && doublequoteFlag == false)
                {
                    String sqlOne = sql.substring(0, index);
                    sqllist.add(sqlOne);
                    sql = sql.substring(index + 1);
                    index = -1;
                }
            }
        }

        // 残りを追加する
        sqllist.add(sql);

        // 空白の要素を削除する
        Iterator<String> iterator = sqllist.iterator();
        while (iterator.hasNext())
        {
            String sqlOne = iterator.next();
            sqlOne = sqlOne.trim();
            if (sqlOne.length() == 0)
            {
                iterator.remove();
            }
        }

        return sqllist;
    }

    /**
     * バインド変数のCSVを生成する。
     * 
     * @param bindList バインド変数のリスト（<code>null</code> も可）
     * @param count バインド変数のリスト中の位置
     * @return 対応するSQLのバインド変数のCSV。
     *          bindList が <code>null</code> なら <code>null</code> を返す。
     */
    public static String getBindValCsv(final List<?> bindList, final int count)
    {
        //バインド変数取得
        String bindVals = null;
        if (bindList != null)
        {
            try
            {
                // バインド変数のListから、対応するSQLのTreeMapを取得する。
                TreeMap<?, ?> bindMap = (TreeMap<?, ?>)bindList.get(count);
                Object lastKey = bindMap.lastKey();
                if (lastKey != null)
                {
                    // バインド変数のカンマ区切り文字列を生成する
                    int maxIdx = ((Integer)lastKey).intValue();
                    StringBuffer csv = new StringBuffer();
                    for (int bindIdx = 1; bindIdx <= maxIdx; bindIdx++)
                    {
                        if (csv.length() != 0)
                        {
                            csv.append(",");
                        }
                        Object val = bindMap.get(Integer.valueOf(bindIdx));
                        if (val != null)
                        {
                            csv.append(val.toString());
                        }
                        else
                        {
                            csv.append("(ERROR)");
                        }
                    }
                    bindVals = csv.toString();
                }
            }
            catch (IndexOutOfBoundsException ex)
            {
                // バインド変数は登録されていない
            }
        }

        return bindVals;
    }

    /**
     * リフレクションにてPreparedStatementのgetJdbcJavelinBindValメソッドを呼び出す。
     * @param stmt Statementオブジェクト
     * @return バインド変数のリスト
     */
    public static List<?> getJdbcJavelinBindValByRef(final Statement stmt)
    {
        // JdbcJavelinStatementではない場合、nullを返す
        if (stmt instanceof JdbcJavelinStatement == false)
        {
            return null;
        }

        JdbcJavelinStatement jStatement = (JdbcJavelinStatement)stmt;

        List<?> ret = jStatement.getJdbcJavelinBindVal();
        return (List<?>)ret;
    }

    /**
     * <code>PreparedStatement</code> で
     * <code>addBatch</code> メソッドが呼び出された回数を返す。
     *
     * @param stmt PreparedStatement
     * @return <code>addBatch</code> メソッドが呼び出された回数
     * @throws Exception 例外
     */
    public static int getPreparedStatementAddBatchCount(final Statement stmt)
        throws Exception
    {
        JdbcJavelinStatement jStatement = (JdbcJavelinStatement)stmt;
        return jStatement.getJdbcJavelinBindIndex();
    }
}
