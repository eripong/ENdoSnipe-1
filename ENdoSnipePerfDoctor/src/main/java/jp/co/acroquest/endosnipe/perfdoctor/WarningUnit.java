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
package jp.co.acroquest.endosnipe.perfdoctor;

/**
 * パフォーマンスドクターの診断結果の一項目。
 * 
 * @author eriguchi
 * 
 */
public class WarningUnit
{
    /** 警告ID */
    private final String   unitId_;

    /** ルールID */
    private final String   id_;

    /** 内容 */
    private final String   description_;

    /** 警告対象のクラス名 */
    private final String   className_;

    /** 警告対象のメソッド名 */
    private final String   methodName_;

    /** 警告の重要度 */
    private final String   level_;

    /** 警告対象のログファイル名 */
    private final String   logFileName_;

    /** 警告対象のログファイルの行番号 */
    private final int      logFileLineNumber_;

    /** 開始時間 */
    private final long     startTime_;

    /** 終了時間 */
    private final long     endTime_;

    /** 降順フラグ(フィルタ時に降順に並べるかどうか) */
    private final boolean  isDescend_;

    /** 変数リスト */
    private final Object[] args_;

    /** スタックトレース */
    private String         stackTrace_ = "";

    /** イベントによる警告であるかどうか。 */
    private boolean        isEvent_    = false;

    /**
     * コンストラクタ。
     * 
     * @param unitId 警告のID
     * @param id ルールのID
     * @param description 警告の説明。
     * @param className クラス名。
     * @param methodName メソッド名。
     * @param level 重要度
     * @param logFileName ログファイル名。
     * @param logFileLineNumber 行番号。
     * @param startTime 開始時刻
     * @param endTime 終了時刻
     * @param isDescend 警告の優先度を降順にするかどうか。
     * @param args 閾値、検出値などの引数。
     */
    WarningUnit(final String unitId, final String id, final String description,
            final String className, final String methodName, final String level,
            final String logFileName, final int logFileLineNumber, final long startTime,
            final long endTime, final boolean isDescend, final Object[] args)
    {
        super();
        this.unitId_ = unitId;
        this.id_ = id;
        this.description_ = description;
        this.className_ = className;
        this.methodName_ = methodName;
        this.level_ = level;
        this.logFileName_ = logFileName;
        this.logFileLineNumber_ = logFileLineNumber;
        this.startTime_ = startTime;
        this.endTime_ = endTime;
        this.isDescend_ = isDescend;
        this.args_ = args;
    }

    /**
     * コンストラクタ。
     * 
     * @param unitId 警告のID
     * @param id ルールのID
     * @param description 警告の説明。
     * @param className クラス名。
     * @param methodName メソッド名。
     * @param level 重要度
     * @param logFileName ログファイル名。
     * @param logFileLineNumber 行番号。
     * @param startTime 開始時刻
     * @param endTime 終了時刻
     * @param isDescend 警告の優先度を降順にするかどうか。
     * @param isEvent イベントであるかどうか。
     * @param stackTrace スタックトレース
     * @param args 閾値、検出値などの引数。
     */
    WarningUnit(final String unitId, final String id, final String description,
            final String className, final String methodName, final String level,
            final String logFileName, final int logFileLineNumber, final long startTime,
            final long endTime, final boolean isDescend, final boolean isEvent,
            final String stackTrace, final Object[] args)
    {
        this(unitId, id, description, className, methodName, level, logFileName, logFileLineNumber,
                startTime, endTime, isDescend, args);
        this.isEvent_ = isEvent;
        this.stackTrace_ = stackTrace;
    }

    /**
     * スタックトレースを取得します。<br />
     * 
     * @return スタックトレース
     */
    public String getStackTrace()
    {
        return stackTrace_;
    }

    /**
     * イベントであるかどうかを返します。<br />
     * 
     * @return この警告オブジェクトがイベントであれば、<code>true</code>
     */
    public boolean isEvent()
    {
        return isEvent_;
    }

    /**
     * @return クラス名。
     */
    public String getClassName()
    {
        return this.className_;
    }

    /**
     * @return 説明。。
     */
    public String getDescription()
    {
        return this.description_;
    }

    /**
     * @return unitId
     */
    public String getUnitId()
    {
        return this.unitId_;
    }

    /**
     * @return ID。
     */
    public String getId()
    {
        return this.id_;
    }

    /**
     * @return 行番号。
     */
    public int getLogFileLineNumber()
    {
        return this.logFileLineNumber_;
    }

    /**
     * @return ログファイル名。
     */
    public String getLogFileName()
    {
        return this.logFileName_;
    }

    /**
     * @return メソッド名。
     */
    public String getMethodName()
    {
        return this.methodName_;
    }

    /**
     * @return 重要度。
     */
    public String getLevel()
    {
        return this.level_;
    }

    /**
     * 変数のリストを配列として返す。
     * @return 変数リスト
     */
    public Object[] getArgs()
    {
        return this.args_;
    }

    /**
     * 開始時間を取得します。<br />
     * 
     * @return 開始時間
     */
    public long getStartTime()
    {
        return this.startTime_;
    }

    /**
     * 終了時間を取得します。<br />
     * 
     * @return 終了時間
     */
    public long getEndTime()
    {
        return this.endTime_;
    }

    /**
     * 降順フラグを設定します。<br />
     * 
     * @return フィルタ時に降順に並べるときには<code>trud</code>
     */
    public boolean isDescend()
    {
        return this.isDescend_;
    }
}