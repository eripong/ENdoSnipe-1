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

import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * DataSource作成のためのインタフェースです
 * 
 * @author fujii
 */
public interface DataSourceCreator
{
    /**
     * ベースディレクトリを設定します。
     * 
     * @param baseDir ベースディレクトリ
     */
    void setBaseDir(String baseDir);

    /**
     * {@link DataSource}オブジェクトを作成します。<br />
     * 
     * @param dbname データベース名
     * @param connectOnlyExists コネクションが存在しているかどうか。
     * 
     * @return {@link DataSource}
     * @throws SQLException SQL発行時に例外が発生した場合
     */
    DataSource createPoolingDataSource(String dbname, boolean connectOnlyExists)
        throws SQLException;

    /**
     * ターゲットであるかどうか。
     * @return ターゲットである場合、<code>true</code>
     */
    boolean isTarget();

    /**
     * シーケンス番号を取得するSQLを取得します。<br />
     * 
     * @param sequenceName シーケンス名
     * @return シーケンス番号取得用SQL
     */
    String getSequenceSql(String sequenceName);

    /**
     * 指定されたデータベースが存在するか確認する。
     * 
     * @param dbName データベース名
     * @return 存在する場合は <code>true</code>、そうでない場合は <code>false</code>
     */
    boolean existsDatabase(String dbName);

}
