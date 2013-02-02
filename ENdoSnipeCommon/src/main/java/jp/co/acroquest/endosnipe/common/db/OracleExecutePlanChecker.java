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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Oracleの実行計画の調査を行います。
 * 
 * @author iida
 */
public class OracleExecutePlanChecker extends AbstractExecutePlanChecker<Map<String, String>>
{
    /** フルスキャンが行われたことを示す文字列 */
    private static final String FULL_SCAN = "TABLE ACCESS FULL";

    /** 実行計画中の命令を表す列のヘッダー */
    private static final String OPERATION = "Operation";

    /** テーブル名を表す列のヘッダー */
    private static final String TABLE_NAME = "Name";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, String>> parseExecutePlanList(final String executePlan)
    {
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

        int startIndex = executePlan.indexOf("--------------------------------------------------");
        if (startIndex < 0)
        {
            return mapList;
        }

        // 改行で区切る
        String[] lines = executePlan.split("[\r\n]{1,}");

        // ヘッダ行の解析→Mapのキーを取得する
        List<String> header = new ArrayList<String>();
        String line;
        int index = 0;
        for (index = 0; index < lines.length; index++)
        {
            line = lines[index];

            if (line.contains("|"))
            {
                // String[] columns = StringUtils.split(line, "\\|");
                String[] columns = line.split("\\|");
                for (String column : columns)
                {
                    column = column.trim();
                    if (column.length() > 0)
                    {
                        header.add(column);
                    }
                }

                break;
            }
        }

        if (index >= lines.length)
        {
            // ヘッダが見つからなかった
            return mapList;
        }

        // データ部の検索→リストへの追加
        for (int dataIndex = index + 1; dataIndex < lines.length; dataIndex++)
        {
            String dataLine = lines[dataIndex];
            if (dataLine.contains("|"))
            {
                // ヘッダ文字列に対応するMapを作成し、リストに追加する
                Map<String, String> map = new HashMap<String, String>();
                // String[] columns = StringUtils.split(dataLine, "\\|");
                String[] columns = dataLine.split("\\|");
                // TODO StringUtils#splitを使用していた処理を、String#splitに変更した。
                // 本来の意図通りとなっているかどうかを確認する必要有り。
                for (int colIndex = 0, listIndex = 0; colIndex < columns.length; colIndex++)
                {
                    if (columns[colIndex] == null)
                    {
                        break;
                    }

                    String column = columns[colIndex].trim();
                    if (column.length() > 0)
                    {
                        map.put(header.get(listIndex), column);
                    }

                    if (0 < columns[colIndex].length())
                    {
                        listIndex++;
                    }
                }
                mapList.add(map);
            }
        }

        return mapList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getFullScanTableNameSet(final String executePlan, final String excludeString)
    {
        List<Map<String, String>> mapList = this.parseExecutePlanList(executePlan);

        Set<String> fullScanTableSet = new HashSet<String>();

        for (Map<String, String> map : mapList)
        {
            String operation = map.get(OPERATION);
            if (operation == null)
            {
                // 正常な動作をする限りここには到達しない。
                return fullScanTableSet;
            }

            int fullAccessIndex = operation.indexOf(FULL_SCAN);
            // FULL_SCANのキーワードを含まない場合
            if (fullAccessIndex < 0)
            {
                continue;
            }

            String tableName = map.get(TABLE_NAME);

            // 除外対象で無い場合のみ、保存する。
            Pattern excludePattern = null;
            if (excludeString != null)
            {
                excludePattern = Pattern.compile(excludeString);
            }
            if (super.isExclude(tableName, excludePattern) == false)
            {
                fullScanTableSet.add(tableName);
            }
        }
        return fullScanTableSet;
    }
}
