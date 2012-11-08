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
package jp.co.acroquest.endosnipe.common.parser;

/**
 * 動作ログの各識別子における基本情報を表す定数インターフェースです。<br />
 * 
 * @author kameda
 */
public interface JavelinLogColumnNum
{
    /** 識別子 */
    int ID = 0;

    /** CALLログのログ出力時刻 */
    int CALL_TIME = 1;

    /** CALLログの呼び出し先メソッド名 */
    int CALL_CALLEE_METHOD = 2;

    /** CALLログの呼び出し先クラス名 */
    int CALL_CALLEE_CLASS = 3;

    /** CALLログの呼び出し先オブジェクトID */
    int CALL_CALLEE_OBJECTID = 4;

    /** CALLログの呼び出し元メソッド名 */
    int CALL_CALLER_METHOD = 5;

    /** CALLログの呼び出し元クラス名 */
    int CALL_CALLER_CLASS = 6;

    /** CALLログの呼び出し元オブジェクトID */
    int CALL_CALLER_OBJECTID = 7;

    /** CALLログの呼び出し先メソッドのモディファイア */
    int CALL_CALLEE_METHOD_MODIFIER = 8;

    /** CALLログのスレッドID */
    int CALL_THREADID = 9;

    /** RETURNログのログ出力時刻 */
    int RETURN_TIME = 1;

    /** RETURNログの呼び出し先メソッド名 */
    int RETURN_CALLEE_METHOD = 2;

    /** RETURNログの呼び出し先クラス名 */
    int RETURN_CALLEE_CLASS = 3;

    /** RETURNログの呼び出し先オブジェクトID */
    int RETURN_CALLEE_OBJECTID = 4;

    /** RETURNログの呼び出し元メソッド名 */
    int RETURN_CALLER_METHOD = 5;

    /** RETURNログの呼び出し元クラス名 */
    int RETURN_CALLER_CLASS = 6;

    /** RETURNログの呼び出し元オブジェクトID */
    int RETURN_CALLER_OBJECTID = 7;

    /** RETURNログの呼び出し先メソッドのモディファイア */
    int RETURN_CALLEE_METHOD_MODIFIER = 8;

    /** RETURNログのスレッドID */
    int RETURN_THREADID = 9;

    /** THROWログのログ出力時刻 */
    int THROW_TIME = 1;

    /** THROWログの例外クラス名 */
    int THROW_EX_CLASS = 2;

    /** THROWログの例外オブジェクトID */
    int THROW_EX_OBJECTID = 3;

    /** THROWログのthrow元メソッド名 */
    int THROW_THROWER_METHOD = 4;

    /** THROWログのthrow元クラス名 */
    int THROW_THROWER_CLASS = 5;

    /** THROWログのthrow元オブジェクトID */
    int THROW_THROWER_OBJECTID = 6;

    /** THROWログのスレッドID */
    int THROW_THREADID = 7;

    /** CATCHログのログ出力時刻 */
    int CATCH_TIME = 1;

    /** CATCHログの例外クラス名 */
    int CATCH_EX_CLASS = 2;

    /** CATCHログの例外オブジェクトID */
    int CATCH_EX_OBJECTID = 3;

    /** CATCHログのcatch先メソッド名 */
    int CATCH_CATCHER_METHOD = 4;

    /** CATCHログのcatch先クラス名 */
    int CATCH_CATCHER_CLASS = 5;

    /** CATCHログのcatch先オブジェクトID */
    int CATCH_CATCHER_OBJECTID = 6;

    /** CATCHログのスレッドID */
    int CATCH_THREADID = 7;

    /** READ,WRITEログのログ出力時刻 */
    int READ_WRITE_TIME = 1;

    /** READ,WRITEログのアクセス先フィールド名 */
    int READ_WRITE_ACCESSEE_FIELD = 2;

    /** READ,WRITEログのアクセス先クラス名 */
    int READ_WRITE_ACCESSEE_CLASS = 3;

    /** READ,WRITEログのアクセス先オブジェクトID */
    int READ_WRITE_ACCESSEE_OBJECTID = 4;

    /** READ,WRITEログのアクセス元メソッド名 */
    int READ_WRITE_ACCESSOR_METHOD = 5;

    /** READ,WRITEログのアクセス元クラス名 */
    int READ_WRITE_ACCESSOR_CLASS = 6;

    /** READ,WRITEログのアクセス元オブジェクトID */
    int READ_WRITE_ACCESSOR_OBJECTID = 7;

    /** READ,WRITEログのアクセス先フィールドの型 */
    int READ_WRITE_CALLEE_METHOD_MODIFIER = 8;

    /** READ,WRITEログのスレッドID */
    int READ_WRITE_THREADID = 9;

    /** EVENTログのログ出力時刻 */
    int EVENT_TIME = 1;

    /** EVENTログのイベント名 */
    int EVENT_NAME = 2;

    /** EVENTログの発生したメソッド名 */
    int EVENT_METHOD = 3;

    /** EVENTログの発生したクラス名 */
    int EVENT_CLASS = 4;

    /** EVENTログの発生したクラス名 */
    int EVENT_LEVEL = 5;

    /** EVENTログのスレッドID */
    int EVENT_THREADID = 6;
}
