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

import jp.co.acroquest.endosnipe.common.logger.CommonLogMessageCodes;

/**
 * ENdoSnipe DataAccessor のためのメッセージコードです。<br />
 * 
 * @author y-komori
 */
public interface LogMessageCodes extends CommonLogMessageCodes
{
    // -------------------------------------------------------------------------
    // データアクセスメッセージコード (01xx)
    // -------------------------------------------------------------------------
    /** データベース初期化 */
    String DB_INITIALIZED = "IEDA0100";

    /** ホスト用テーブル初期化時 */
    String HOST_TABLE_INITIALIZED = "IEDA0101";

    /** ホスト情報登録 */
    String HOST_REGISTERED = "IEDA0102";

    /** データベースアクセス失敗 */
    String DB_ACCESS_ERROR = "EEDA0103";

    /** コネクション接続 */
    String DB_CONNECTED = "TEDA0104";

    /** コネクション切断 */
    String DB_DICONNECTED = "TEDA0105";

    /** コネクション全切断時にアクティブコネクションが残っている */
    String ACTIVE_CONNECTIONS_REMAINED = "WEDA0106";

    /** ローテートを実施した */
    String ROTATE_TABLE_PERFORMED = "IEDA0107";

    /** 不要な系列情報を削除した */
    String NO_NEEDED_SERIES_REMOVED = "IEDA0108";
}
