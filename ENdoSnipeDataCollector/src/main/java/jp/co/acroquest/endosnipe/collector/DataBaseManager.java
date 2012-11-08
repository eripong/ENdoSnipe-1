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
package jp.co.acroquest.endosnipe.collector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * データベースの情報を管理するクラスです。<br />
 * 
 * @author fujii
 *
 */
public class DataBaseManager
{
    /** {@link DataBaseManager}のシングルトンインスタンス */
    private static final DataBaseManager manager__ = new DataBaseManager();

    /** DBのパスと接続先を保存したマップ */
    private final Map<String, String> dbMap_ = new ConcurrentHashMap<String, String>();

    /**
     * インスタンス化を阻止するプライベートコンストラクタ。
     */
    private DataBaseManager()
    {
        // Do Nothing.
    }

    /**
     * シングルトンインスタンスを取得します。<br />
     * 
     * @return インスタンス
     */
    public static DataBaseManager getInstance()
    {
        return manager__;
    }

    /**
     * DBの保存先とホスト情報をMapとして保存します。<br />
     * 
     * @param dbName DB名
     * @param hostInfo ホスト情報
     */
    public void addDbInfo(final String dbName, final String hostInfo)
    {
        this.dbMap_.put(dbName, hostInfo);
    }

    /**
     * ホスト情報を取得します。<br />
     * 
     * @param dbName DB名
     * @return ホスト情報
     */
    public String getHostInfo(final String dbName)
    {
        return this.dbMap_.get(dbName);
    }

    /**
     * DBのホスト情報を削除します。<br />
     * 
     * @param dbName データベース名
     */
    public void removeDbInfo(final String dbName)
    {
        this.dbMap_.remove(dbName);
    }

}
