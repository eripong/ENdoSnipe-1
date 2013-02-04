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
package jp.co.acroquest.endosnipe.perfdoctor.rule.dbaccess;

import java.util.HashMap;
import java.util.Map;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;

/**
 * SQLの回数/時間を数えた結果を保存する。
 * 
 * @author eriguchi
 *
 */
public class SqlCountEntry
{
    /** SQLの回数 */
    private long                       count_;

    /** バインド引数 */
    private final Map<String, Integer> bindValCountMap_;

    /** 発行回数を最初に超えたJavelinLogElement */
    JavelinLogElement                  errorElement_;

    /**
     * コンストラクタ。
     */
    public SqlCountEntry()
    {
        count_ = 1;
        this.bindValCountMap_ = new HashMap<String, Integer>();
    }

    /**
     * 発行回数を最初に超えたJavelinLogElementを取得する。
     * 
     * @return 発行回数を最初に超えたJavelinLogElement
     */
    public JavelinLogElement getErrorElement()
    {
        return errorElement_;
    }

    /***
     * バインド引数のパターン数を取得する。
     * @return バインド引数のパターン数
     */
    public int getBindValCount()
    {
        return this.bindValCountMap_.size();
    }

    /**
     * 発行回数を最初に超えたJavelinLogElementを取得する。
     * 
     * @param errorElement 発行回数を最初に超えたJavelinLogElement
     */
    public void setErrorElement(final JavelinLogElement errorElement)
    {
        errorElement_ = errorElement;
    }

    /**
     * 実行回数を取得する。
     * 
     * @return 実行回数
     */
    public long getCount()
    {
        return count_;
    }

    /**
     * 実行回数を取得する。
     * 
     * @param count 実行回数
     */
    public void setCount(final long count)
    {
        count_ = count;
    }

    /***
     * バインド引数を追加する。
     * @param bindVal バインド引数
     */
    public void addBindValCount(final String bindVal)
    {
        Integer bindValCount = this.bindValCountMap_.get(bindVal);

        if (bindValCount == null)
        {
            this.bindValCountMap_.put(bindVal, Integer.valueOf(1));
        }
        else
        {
            this.bindValCountMap_.put(bindVal, Integer.valueOf(bindValCount.intValue() + 1));
        }
    }

}
