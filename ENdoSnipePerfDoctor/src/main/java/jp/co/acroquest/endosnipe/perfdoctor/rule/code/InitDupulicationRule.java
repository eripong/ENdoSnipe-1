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
package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jp.co.acroquest.endosnipe.common.event.EventConstants;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule;

/**
 * 初期化を複数回行っていないかチェックするルールです。<br />
 *
 * 通常、クラス名とメソッド名を指定しない場合は、
 * 他の {@link InitDupulicationRule} に登録されていないクラス・メソッドで閾値を下回ったものすべてを、
 * IntervalError として出力します。<br />
 *
 * @author fujii
 * @author sakamoto
 */
public class InitDupulicationRule extends SingleElementRule implements JavelinConstants
{
    /** ロガー */
    private static final ENdoSnipeLogger         LOGGER                =
                                                                         ENdoSnipeLogger.getLogger(InitDupulicationRule.class,
                                                                                                   null);

    private static final String                  ID_LEVEL_SEPARATOR    = ":";

    /** 閾値 */
    public long                                  threshold;

    /** クラス名（カンマ区切りで複数指定可能／メソッド名と対応する） */
    public String                                classNameList;

    /** メソッド名（カンマ区切りで複数指定可能／クラス名と対応する） */
    public String                                methodNameList;

    /** 「ID + ":" + レベル」をキー、メソッド一覧を値に持つマップ */
    private static Map<String, ClassMethodPairs> classMethodPairsMap__ =
                                                                         new ConcurrentHashMap<String, ClassMethodPairs>();

    /**
     * このオブジェクトにセットされているクラス・メソッドをマップに登録します。<br />
     *
     * クラス・メソッドが登録されていない {@link InitDupulicationRule} オブジェクトは、
     * マップに登録されていないクラス・メソッドを IntervalError として出力します。<br />
     */
    @Override
    public void init()
    {
        if (this.classNameList != null && this.methodNameList != null)
        {
            String key = getId() + ID_LEVEL_SEPARATOR + getLevel();
            ClassMethodPairs pairs = new ClassMethodPairs(this.classNameList, this.methodNameList);
            classMethodPairsMap__.put(key, pairs);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doJudgeElement(final JavelinLogElement element)
    {
        // 識別子が"Event"でない場合は、処理しない。
        String type = element.getBaseInfo().get(JavelinLogColumnNum.ID);
        boolean isEvent = MSG_EVENT.equals(type);

        if (isEvent == false)
        {
            return;
        }

        String eventName = element.getBaseInfo().get(JavelinLogColumnNum.EVENT_NAME);

        // イベント名が "IntervalError" の場合、検出を行う。
        if (EventConstants.NAME_INTERVALERROR.equals(eventName) == false)
        {
            return;
        }

        Map<String, String> eventInfoMap =
                                           JavelinLogUtil.parseDetailInfo(element,
                                                                          JavelinParser.TAG_TYPE_EVENTINFO);
        String actual = eventInfoMap.get(EventConstants.PARAM_INTERVALERROR_ACTUAL_INTERVAL);

        // 実際にかかった時間が閾値以下の場合は、処理を終了する。
        long actualTime = Long.MAX_VALUE;
        try
        {
            actualTime = Long.parseLong(actual);
        }
        catch (NumberFormatException ex)
        {
            LOGGER.warn(ex);
        }
        if (actualTime > this.threshold)
        {
            return;
        }

        classMethodMatching(element, eventInfoMap, actualTime);
    }

    /**
     * クラス名とメソッド名をマッチングし、IntervalError を出力します。<br />
     *
     * @param element {@link JavelinLogElement} オブジェクト
     * @param eventInfoMap イベント情報
     * @param actualTime 実際にかかった時間（ミリ秒）
     */
    private void classMethodMatching(final JavelinLogElement element,
            final Map<String, String> eventInfoMap, final long actualTime)
    {
        String eventClassName = eventInfoMap.get(EventConstants.PARAM_INTERVALERROR_CLASSNAME);
        String eventMethodName = eventInfoMap.get(EventConstants.PARAM_INTERVALERROR_METHODNAME);

        if (this.classNameList == null || this.methodNameList == null)
        {
            // ルールでクラス名もメソッド名も指定されていなければ、
            // マップに登録されていないクラス・メソッドの場合は IntervalError を出力する。
            for (Map.Entry<String, ClassMethodPairs> entry : classMethodPairsMap__.entrySet())
            {
                ClassMethodPairs pairs = entry.getValue();
                if (pairs.contains(eventClassName, eventMethodName))
                {
                    // マップに登録されている場合は、何もしない。
                    return;
                }
            }
            // マップに登録されていなかったので、IntervalError を出力する。
            String stackTrace = eventInfoMap.get(EventConstants.PARAM_INTERVALERROR_STACKTRACE);
            addError(true, stackTrace, element, false, new Object[]{this.threshold, actualTime,
                    eventClassName, eventMethodName});
            return;
        }

        String[] classArray = this.classNameList.split(",");
        String[] methodArray = this.methodNameList.split(",");
        int repeatTime = Math.min(classArray.length, methodArray.length);

        for (int num = 0; num < repeatTime; num++)
        {
            // クラス名、メソッド名がリストと一致したときのみ出力する。
            if (classArray[num].equals(eventClassName) && methodArray[num].equals(eventMethodName))
            {
                String stackTrace = eventInfoMap.get(EventConstants.PARAM_INTERVALERROR_STACKTRACE);
                addError(true, stackTrace, element, false, new Object[]{this.threshold, actualTime,
                        eventClassName, eventMethodName});
                return;
            }
        }
    }

    /**
     * メソッドのリストを保持するクラス。<br />
     *
     * いずれかの {@link InitDupulicationRule} オブジェクトのフィールドに持っているクラス・メソッドは、
     * このクラスに登録されます。<br />
     *
     * @author Sakamoto
     */
    private static class ClassMethodPairs
    {
        private final Set<String>   classAndMethodSet_;

        private static final String SEPARATOR = "###";

        /**
         * 登録するメソッドを指定してオブジェクトを初期化します。<br />
         *
         * @param classNameList クラス名をカンマで区切った文字列
         * @param methodNameList メソッド名をカンマで区切った文字列
         */
        public ClassMethodPairs(final String classNameList, final String methodNameList)
        {
            String[] classNameArray = classNameList.split(",");
            String[] methodNameArray = methodNameList.split(",");
            int count = Math.min(classNameArray.length, methodNameArray.length);
            this.classAndMethodSet_ = new HashSet<String>();
            for (int index = 0; index < count; index++)
            {
                String className = classNameArray[index];
                String methodName = methodNameArray[index];
                this.classAndMethodSet_.add(className + SEPARATOR + methodName);
            }
        }

        /**
         * 指定されたクラスのメソッドが、このオブジェクトに登録されているかどうかを調べます。<br />
         *
         * @param className 検索するメソッドのクラス名
         * @param methodName 検索するメソッド
         * @return 登録されている場合は <code>true</code> 、登録されていない場合は <code>false</code>
         */
        public boolean contains(final String className, final String methodName)
        {
            return this.classAndMethodSet_.contains(className + SEPARATOR + methodName);
        }
    }
}
