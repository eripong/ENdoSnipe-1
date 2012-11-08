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
package jp.co.acroquest.endosnipe.javelin.event;

import java.util.Map;

import jp.co.acroquest.endosnipe.javelin.util.LinkedHashMap;

/**
 * Javelinの共通イベントクラス。
 * 
 * @author eriguchi
 */
public class CommonEvent
{
    /** イベントの警告レベル(INFO) */
    public static final int LEVEL_INFO = 20;

    /** イベントの警告レベル(WARN) */
    public static final int LEVEL_WARN = 30;

    /** イベントの警告レベル(ERROR) */
    public static final int LEVEL_ERROR = 40;

    /** イベント発生時刻。 */
    protected long time_;

    /** イベント名。 */
    protected String name_;

    /** パラメータのマップ。。 */
    protected Map<String, String> paramMap_;

    /** イベントのレベル。 */
    protected int level_;

    /**
     *　イベント出力時刻、イベント名、イベントレベルのデフォルト値を設定します。<br />
     *
     */
    public CommonEvent()
    {
        this.time_ = System.currentTimeMillis();
        this.name_ = "unknown";
        this.paramMap_ = new LinkedHashMap<String, String>();
        this.level_ = LEVEL_WARN;
    }

    /**
     * イベント発生時刻を取得します。<br />
     * 
     * @return イベント発生時刻
     */
    public long getTime()
    {
        return this.time_;
    }

    /**
     * イベント発生時刻を設定します。<br />
     * 
     * @param time イベント発生時刻
     */
    public void setTime(long time)
    {
        this.time_ = time;
    }

    /**
     * イベント名を取得します。<br />
     * 
     * @return イベント名
     */
    public String getName()
    {
        return this.name_;
    }

    /**
     * イベント名を設定します。<br />
     * 
     * @param name イベント名
     */
    public void setName(String name)
    {
        this.name_ = name;
    }

    /**
     * イベントパラメータを保存するマップを取得します。<br />
     * 
     * @return イベントパラメータを保存するマップ
     */
    public Map<String, String> getParamMap()
    {
        return this.paramMap_;
    }

    /**
     * イベントパラメータを設定します。<br />
     * 
     * @param key キー
     * @param value 値
     */
    public void addParam(String key, String value)
    {
        this.paramMap_.put(key, value);
    }

    /**
     * イベントパラメータのマップから指定したキーに対応する値を取得します。<br />
     * 
     * @param key キー
     * @return 指定したキーに対応するイベントパラメータのマップから取得した値
     */
    public String getParam(String key)
    {
        return this.paramMap_.get(key);
    }

    /**
     * イベントレベルを取得します。<br />
     * 
     * @return イベントレベル
     */
    public int getLevel()
    {
        return this.level_;
    }

    /**
     * イベントレベルを設定します。<br />
     * 
     * @param level イベントレベル
     */
    public void setLevel(int level)
    {
        this.level_ = level;
    }

}
