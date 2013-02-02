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
package jp.co.acroquest.endosnipe.common.db;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DBの実行計画の調査を定義する基底クラス。
 * 
 * @param <T> 実行計画の1要素
 * @author iida
 */
public abstract class AbstractExecutePlanChecker<T>
{
    /** 実行計画のプレフィックス */
    public static final String PLAN_PREFIX = "[PLAN] ";

    /**
     * 指定された実行計画のリストから、実行計画の文字列を作成して、返します。<br>
     * 
     * @param executePlanList 実行計画のリスト
     * @return 実行計画の文字列
     */
    public String parseExecutePlan(final List<String> executePlanList)
    {
        String[] args = executePlanList.toArray(new String[executePlanList.size()]);
        String planString = "";

        for (int index = 0; index < args.length; index++)
        {
            String arg = args[index];
            int planIndex = arg.indexOf(PLAN_PREFIX);
            if (planIndex >= 0)
            {
                planString = args[index].substring(planIndex + PLAN_PREFIX.length());
                break;
            }
        }

        return planString;
    }

    /**
     * 指定された実行計画の文字列から、Mapのリストを作成して、返します。<br>
     * 
     * @param executePlan 実行計画の文字列
     * @return Mapのリスト
     */
    public abstract List<T> parseExecutePlanList(String executePlan);

    /**
     * 指定された実行計画のリストから、Mapのリストを作成して、返します。<br>
     * 
     * @param executePlanList 実行計画のリスト
     * @return Mapのリスト
     */
    public List<T> parseExecutePlanList(final List<String> executePlanList)
    {
        String executePlan = this.parseExecutePlan(executePlanList);
        return this.parseExecutePlanList(executePlan);
    }

    /**
     * 指定された実行計画の文字列を調査し、その中でFull Scanを行っているTableの名前のセットを作成して、返します。<br>
     * ただし、指定された除外パターンの文字列とマッチするものは、そのセットに含まれません。<br>
     * 
     * @param executePlan 実行計画の文字列
     * @param excludeString 除外パターンの文字列。
     * @return Full Scanを行っているTableの名前のセット
     */
    public abstract Set<String> getFullScanTableNameSet(String executePlan, String excludeString);

    /**
     * 指定されたテーブル名が、指定された除外パターンにマッチするかどうかを判定し、返します。<br>
     * 引数の中にnullのものがある場合、falseを返します。<br>
     * 
     * @param tableName 文字列
     * @param excludePattern 除外パターン
     * @return 除外パターンにマッチする場合はtrue、そうでない場合はfalse
     */
    protected boolean isExclude(final String tableName, final Pattern excludePattern)
    {
        if (tableName == null || excludePattern == null)
        {
            return false;
        }

        Matcher matcher = excludePattern.matcher(tableName);
        if (matcher.matches())
        {
            return true;
        }

        return false;
    }
}
