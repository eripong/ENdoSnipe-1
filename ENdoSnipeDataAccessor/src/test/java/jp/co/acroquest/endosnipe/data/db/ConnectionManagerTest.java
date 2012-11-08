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

import java.io.File;
import java.sql.Connection;

import jp.co.dgic.testing.framework.DJUnitTestCase;
import jp.co.acroquest.endosnipe.common.util.IOUtil;

/**
 * ConnectionManagerのテストコード。
 * Ver4.5 リグレッション試験用コード
 * 
 * @author eriguchi
 */
public class ConnectionManagerTest extends DJUnitTestCase
{
    /** データベースファイルを保存するディレクトリ。 */
    private static final String   BASE_DIR = IOUtil.getTmpDirFile().getAbsolutePath();

    /** テストで使用するデータベースの名前。 */
    protected static final String DB_NAME  = "endosnipedb";

    /**
     * Ver4.5 リグレッション試験用コード(1-1-1)
     * コネクションを取得した状態でConnectionManagerでコネクションを全切断すると、
     * WEDA0106のメッセージを出力することを確認する。
     */
    public void testCloseAllMessage()
        throws Exception
    {
        // 準備
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

        // 実行
        connectionManager.getConnection(DB_NAME, false);
        connectionManager.closeAll();

        // 検証
        assertArgumentPassed("org.apache.commons.logging.Log", "warn", 0,
                             "[WEDA0106]データベースからアイドルコネクションを全切断しましたが、アクティブコネクションが残っています.(コネクション数:1)");
    }
}
