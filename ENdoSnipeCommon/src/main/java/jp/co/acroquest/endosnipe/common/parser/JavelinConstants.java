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
 * Javelinで使用する定数インターフェースです。<br />
 * 
 * @author eriguchi
 */
public interface JavelinConstants
{
    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Call"のID。 <br />
     */
    int ID_CALL = 0;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Return"のID。 <br />
     */
    int ID_RETURN = 1;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Read"のID。 <br />
     */
    int ID_FIELD_READ = 2;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Write"のID。 <br />
     */
    int ID_FIELD_WRITE = 3;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Catch"のID。 <br />
     */
    int ID_CATCH = 4;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Throw"のID。 <br />
     */
    int ID_THROW = 5;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Event"のID。 <br />
     */
    int ID_EVENT = 6;

    /**
     * 動作ログに出力する"Return"を表す文字列。<br />
     */
    String MSG_RETURN = "Return";

    /**
     * 動作ログに出力する"Call"を表す文字列。<br />
     */
    String MSG_CALL = "Call  ";

    /**
     * 動作ログに出力する"Read"を表す文字列。<br />
     */
    String MSG_FIELD_READ = "Read  ";

    /**
     * 動作ログに出力する"Write"を表す文字列。<br />
     */
    String MSG_FIELD_WRITE = "Write ";

    /**
     * 動作ログに出力する"Catch"を表す文字列。<br />
     */
    String MSG_CATCH = "Catch ";

    /**
     * 動作ログに出力する"Throw"を表す文字列。<br />
     */
    String MSG_THROW = "Throw ";

    /**
     * 動作ログに出力する"Throw"を表す文字列。<br />
     */
    String MSG_EVENT = "Event ";
}
