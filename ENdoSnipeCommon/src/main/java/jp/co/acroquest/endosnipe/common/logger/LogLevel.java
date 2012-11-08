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
package jp.co.acroquest.endosnipe.common.logger;

/**
 * システムログのログレベルを表すクラスです。<br />
 * 
 * @author eriguchi
 */
public class LogLevel
{
    /** FATALレベルのint値。 */
    public static final int FATAL_INT = 50000;

    /** ERRORレベルのint値。 */
    public static final int ERROR_INT = 40000;

    /** WARNレベルのint値。 */
    public static final int WARN_INT = 30000;

    /** INFOレベルのint値。 */
    public static final int INFO_INT = 20000;

    /** DEBUGレベルのint値。 */
    public static final int DEBUG_INT = 10000;

    /** FATALレベル。 */
    public static final LogLevel FATAL = new LogLevel(FATAL_INT, "FATAL");

    /** ERRORレベル。 */
    public static final LogLevel ERROR = new LogLevel(ERROR_INT, "ERROR");

    /** WARNレベル。 */
    public static final LogLevel WARN = new LogLevel(WARN_INT, "WARN");

    /** INFOレベル。 */
    public static final LogLevel INFO = new LogLevel(INFO_INT, "INFO");

    /** DEBUGレベル。 */
    public static final LogLevel DEBUG = new LogLevel(DEBUG_INT, "DEBUG");

    /** ログレベルのint値。 */
    private final int level_;

    /** ログレベルの名称。 */
    private final String levelStr_;

    /**
     * {@link LogLevel} を構築します。<br />
     * 
     * @param level ログレベルの値
     * @param levelStr ログレベルの名称
     */
    public LogLevel(final int level, final String levelStr)
    {
        this.level_ = level;
        this.levelStr_ = levelStr;
    }

    /**
     * ログレベルの名称を取得します。<br />
     * 
     * @return ログレベルの名称
     */
    public String getLevelStr()
    {
        return levelStr_;
    }

    /**
     * ログレベルの値を取得します。<br />
     * 
     * @return ログレベルのin値
     */
    public int getLevel()
    {
        return level_;
    }
}
