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
package jp.co.acroquest.endosnipe.data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import jp.co.dgic.testing.framework.DJUnitTestCase;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.IOUtil;
import jp.co.acroquest.endosnipe.common.util.SQLUtil;
import jp.co.acroquest.endosnipe.data.db.ConnectionManager;

import org.apache.commons.io.FileUtils;

/**
 * データベースに関するテストを行うための基底クラスです。<br />
 * 
 * @author y-komori
 */
public abstract class AbstractDBTest extends DJUnitTestCase
{
    private static final ENdoSnipeLogger LOGGER = ENdoSnipeLogger.getLogger(AbstractDBTest.class);

    /** データベースファイルを保存するディレクトリ。 */
    private static final String BASE_DIR = IOUtil.getTmpDirFile().getAbsolutePath();

    /** テストで使用するデータベースの名前。 */
    protected static final String DB_NAME = "endosnipedb";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        LOGGER.info("テスト開始:" + getName());
        initConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
        
        try
        {
            deleteDB();
        }
        catch (IOException ex)
        {
            LOGGER.info("DBの削除に失敗しました。" + getName());
        }
        LOGGER.info("テスト終了:" + getName());
    }

    protected void initConnection()
    {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        File tempDir = new File(BASE_DIR);
        if (tempDir.exists() == false)
        {
            if (tempDir.mkdir() == false)
            {
                fail("ディレクトリの作成に失敗しました. ディレクトリ名:" + tempDir.getAbsolutePath());
            }
        }
        connectionManager.setBaseDir(BASE_DIR);
    }

    protected void initDB()
        throws SQLException,
            IOException
    {
        initConnection();
        Connection conn = getConnection();
        DBInitializer.initialize(conn);
        SQLUtil.closeConnection(conn);
    }

    /**
     * データベースファイルを削除します。<br />
     * 
     * @throws IOException ファイルを削除できない場合
     */
    protected void deleteDB()
        throws IOException
    {
        ConnectionManager.getInstance().closeAll();

        File dbDir = new File(BASE_DIR + "/" + DB_NAME);
        if (dbDir.exists())
        {
            FileUtils.deleteDirectory(dbDir);
        }
    }

    /**
     * テストで使用するデータベースコネクションを取得します。<br />
     * 
     * @return コネクション
     * @throws SQLException
     */
    protected Connection getConnection()
        throws SQLException
    {
        return ConnectionManager.getInstance().getConnection(DB_NAME, false);
    }
}
