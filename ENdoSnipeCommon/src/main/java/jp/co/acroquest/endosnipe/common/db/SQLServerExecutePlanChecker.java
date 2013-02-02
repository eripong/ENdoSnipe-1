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

/**
 * SQLのServer実行計画の調査を行います。<br>
 * 現状、フルスキャンを行った時に出力される文字列が<br>
 * 実行計画の文字列の中に含まれるか含まれないかのみで、フルスキャンの判定を行っている。<br>
 * また、フルスキャンを起こしているテーブル名を取得していない。<br>
 * 
 * @author iida
 */
public class SQLServerExecutePlanChecker extends AbstractExecutePlanChecker<Map<String, String>>
{
    /** フルスキャンが行われたことを示す文字列。 */
    private static final String PARAMETER_TABLE_SCAN = "Parameter Table Scan";

    /** フルスキャンが行われたことを示す文字列。 */
    private static final String TABLE_SCAN = "Table Scan";

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
     * {@inheritDoc}
     */
    @Override
    public Set<String> getFullScanTableNameSet(final String executePlan, final String excludeString)
    {
        // フルスキャンが発生したテーブルの名前をまとめるSet。
        Set<String> fullScanTableSet = new HashSet<String>();

        // フルスキャンを示す文字列が、実行計画中に存在するかどうかを調べる。
        int parameterTableScanIndex = executePlan.indexOf(PARAMETER_TABLE_SCAN);
        int tableScanIndex = executePlan.indexOf(TABLE_SCAN);

        if (-1 < parameterTableScanIndex || -1 < tableScanIndex)
        {
            fullScanTableSet.add("Table Full Scan Occured.");
        }

        return fullScanTableSet;
    }

}
