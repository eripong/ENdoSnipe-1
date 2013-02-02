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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import jp.co.acroquest.endosnipe.common.util.CSVTokenizer;

/**
 * MySQLの実行計画の調査を行います。
 * 
 * @author iida
 */
public class MySQLExecutePlanChecker extends AbstractExecutePlanChecker<MySQLExplainEntry>
{
    /** フルスキャンが行われたことを示す文字列。 */
    private static final String FULL_SCAN = "ALL";

    private static final int INDEX_ID = 0;

    private static final int INDEX_SELECTTYPE = 1;

    private static final int INDEX_TABLE = 2;

    private static final int INDEX_TYPE = 3;

    private static final int INDEX_POSSIBLE_KEYS = 4;

    private static final int INDEX_KEY = 5;

    private static final int INDEX_KEYLEN = 6;

    private static final int INDEX_REF = 7;

    private static final int INDEX_ROWS = 8;

    private static final int INDEX_EXTRA = 9;

    /** 実行計画の列数。 */
    private static final int EXPLAIN_PLAN_COLNUM = 10;

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getFullScanTableNameSet(final String executePlan, final String excludeString)
    {
        List<MySQLExplainEntry> execPlanList = parseExecutePlanList(executePlan);
        Set<String> fullScanTableSet = new HashSet<String>();

        for (MySQLExplainEntry entry : execPlanList)
        {
            if (FULL_SCAN.equals(entry.getType()))
            {
                // フルスキャンが発生したテーブルの名前を登録する。
                String tableName = entry.getTable();

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
            }
        }

        return fullScanTableSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MySQLExplainEntry> parseExecutePlanList(final String executePlan)
    {
        List<MySQLExplainEntry> list = new ArrayList<MySQLExplainEntry>();

        BufferedReader reader = new BufferedReader(new StringReader(executePlan));

        String line;
        try
        {
            // 1行目にタイトル、2行目に列名が記述されている。
            // 3行目にパラメータが入っているので、前の2行は読み飛ばす。
            reader.readLine();
            reader.readLine();

            while ((line = reader.readLine()) != null)
            {
                List<String> tokenList = parseLineToToken(line);

                if (tokenList.size() < EXPLAIN_PLAN_COLNUM)
                {
                    continue;
                }

                String id = getString(tokenList, INDEX_ID);
                String selectType = getString(tokenList, INDEX_SELECTTYPE);
                String table = getString(tokenList, INDEX_TABLE);
                String type = getString(tokenList, INDEX_TYPE);
                String possibleKeys = getString(tokenList, INDEX_POSSIBLE_KEYS);
                String key = getString(tokenList, INDEX_KEY);
                String keyLen = getString(tokenList, INDEX_KEYLEN);
                String ref = getString(tokenList, INDEX_REF);
                int rows = getInteger(tokenList, INDEX_ROWS);
                String extra = getString(tokenList, INDEX_EXTRA);

                MySQLExplainEntry entry = new MySQLExplainEntry();
                entry.setId(id);
                entry.setSelectType(selectType);
                entry.setTable(table);
                entry.setType(type);
                entry.setPossibleKeys(possibleKeys);
                entry.setKey(key);
                entry.setKeyLen(keyLen);
                entry.setRef(ref);
                entry.setRows(rows);
                entry.setExtra(extra);

                list.add(entry);
            }
        }
        catch (IOException ioe)
        {
            // 文字列からの読み込みのため、発生しない。
        }

        return list;
    }

    private int getInteger(final List<String> tokenList, final int index)
    {
        String token = getString(tokenList, index);
        if (token == null)
        {
            return 0;
        }

        return Integer.parseInt(token);
    }

    private String getString(final List<String> tokenList, final int index)
    {
        String token = tokenList.get(index);
        if ("null".equals(token))
        {
            token = null;
        }

        return token;
    }

    /**
     * 1行をTokenに分ける。
     * 
     * @param line
     * @return
     */
    private List<String> parseLineToToken(final String line)
    {
        CSVTokenizer csvTokenizer = new CSVTokenizer(line);
        List<String> tokenList = new ArrayList<String>();
        while (csvTokenizer.hasMoreTokens())
        {
            String token = csvTokenizer.nextToken();
            tokenList.add(token);
        }
        return tokenList;
    }
}
