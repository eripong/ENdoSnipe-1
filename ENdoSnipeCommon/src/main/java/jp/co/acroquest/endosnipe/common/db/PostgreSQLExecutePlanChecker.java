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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * PostgreSQLの実行計画の調査を行います。
 * 
 * @author iida
 */
public class PostgreSQLExecutePlanChecker extends AbstractExecutePlanChecker<Map<String, String>>
{
    /** フルスキャンが行われたことを示す文字列。 */
    private static final String FULL_SCAN = "Seq Scan on ";

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getFullScanTableNameSet(final String executePlan, final String excludeString)
    {
        // フルスキャンが発生したテーブルの名前をまとめるSet。
        Set<String> fullScanTableSet = new HashSet<String>();

        // 最初にフルスキャンが発生する位置を取得する。
        int fullAccessIndex = executePlan.indexOf(FULL_SCAN);
        // フルスキャンが発生する限りループする。
        while (fullAccessIndex >= 0)
        {
            // フルスキャンが発生したテーブルの名前を登録する。
            String tableName = getTableName(executePlan, fullAccessIndex);

            // 除外対象で無い場合のみ、保存する。
            Pattern excludePattern = null;
            if (excludeString != null)
            {
                excludePattern = Pattern.compile(excludeString);
            }
            if (isExclude(tableName, excludePattern) == false)
            {
                fullScanTableSet.add(tableName);
            }

            // 次のフルスキャンを探す。
            fullAccessIndex = executePlan.indexOf(FULL_SCAN, fullAccessIndex + 1);
        }

        return fullScanTableSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, String>> parseExecutePlanList(final String executePlan)
    {
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        return mapList;
    }

    /**
     * フルスキャンが発生したテーブル名を実行計画から取得する。
     * 
     * @param plan 実行計画の文字列。
     * @param index "Seq Scan on "文字列が見つかった位置。
     * @return テーブル名。
     */
    private String getTableName(final String plan, final int index)
    {
        String tableName = "";

        int tableIndex = index + FULL_SCAN.length();
        int spaceIndex = plan.indexOf(" ", tableIndex);
        if (spaceIndex < 0)
        {
            tableName = plan.substring(tableIndex);
        }
        else
        {
            tableName = plan.substring(tableIndex, spaceIndex);
        }
        tableName = tableName.trim();

        return tableName;
    }
}
