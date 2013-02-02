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
package jp.co.acroquest.endosnipe.javelin.jdbc.instrument;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PostgreSQLのPreparedStatementの実行計画を取るために使用するクラス。
 *
 * @author sakamoto
 */
public class PreparedStatementPair
{

    /** 実行計画取得用PreparedStatement */
    private final PreparedStatement pstmtForPlan_;

    /** バインド変数の数 */
    private final int bindValCount_;

    /** DMLなら<code>true</code> */
    private final boolean isDml_;

    /**
     * 実行計画取得用PreparedStatementを作成する。
     *
     * @param connect DBコネクション
     * @param sql SQL文
     * @param isDml 指定したSQL文がDMLなら<code>true</code>
     * @throws SQLException SQL文にエラーがあったとき
     */
    public PreparedStatementPair(final Connection connect, final String sql, final boolean isDml)
        throws SQLException
    {
        this.pstmtForPlan_ = connect.prepareStatement(sql);
        this.bindValCount_ = countBindVal(sql);
        this.isDml_ = isDml;
    }

    /**
     * 実行計画取得用PreparedStatementを返す。
     *
     * @return 実行計画取得用PreparedStatement
     */
    public PreparedStatement getPreparedStatement()
    {
        return this.pstmtForPlan_;
    }

    /**
     * 実行計画取得用PreparedStatementに使われるバインド変数の数を返す。
     *
     * @return バインド変数の数
     */
    public int getBindValCount()
    {
        return this.bindValCount_;
    }

    /**
     * DMLかどうかを返す。
     *
     * @return DMLなら<code>true</code>
     */
    public boolean isDml()
    {
        return this.isDml_;
    }

    /**
     * 指定されたSQL文のバインド変数の数を数える。
     *
     * @param sql SQL文
     * @return バインド変数の数
     */
    private int countBindVal(final String sql)
    {
        // バインド変数の数
        int count = 0;
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
            else if (checkchar == '?')
            {
                // クエスチョンがきたときは、
                // シングルクォーテーションの中でもダブルクォーテーションの中でもない場合に
                // カウントする。
                if (singlequoteFlag == false && doublequoteFlag == false)
                {
                    count++;
                }
            }
        }

        return count;
    }

}