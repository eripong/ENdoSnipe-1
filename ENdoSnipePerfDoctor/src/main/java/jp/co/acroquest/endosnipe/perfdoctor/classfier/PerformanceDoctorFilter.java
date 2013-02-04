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
package jp.co.acroquest.endosnipe.perfdoctor.classfier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;

/**
 * 警告リストの中から、ルールID,クラス名、メソッド名、重要度が同じ警告に対して、フィルターをかける。
 * @author fujii
 *
 */
public class PerformanceDoctorFilter
{
    /** セパレータ */
    private static final String SEPARATOR          = ",";

    /** 警告メッセージのargs中にある、検出値のindex */
    public static final int     TARGET_VALUE_INDEX = 1;

    /** 比較するスタックトレースの長さ */
    private static final int    COMPARE_LENGTH     = 200;

    /**
     * PerformanceDoctorの結果をフィルタリングします。<br />
     * 
     * @param warningUnitList WarningUnitのリスト
     * @return フィルター後の結果
     */
    public List<WarningUnit> doFilter(final List<WarningUnit> warningUnitList)
    {
        // ルールID、クラス名、メソッド名、重要度をキーにMapを作成する。
        Map<String, List<WarningUnit>> warningMap = makeMap(warningUnitList);
        List<WarningUnit> resultList = new ArrayList<WarningUnit>();

        // Mapに入力されたWarningUnitのリストを出力する。
        Collection<List<WarningUnit>> col = warningMap.values();
        for (List<WarningUnit> list : col)
        {
            // WarningUnitのリストサイズがFILTER_THRESHOLD以下のとき、
            // SimpleClassifierを利用して分類する。
            // それ以外のときは、KMeans法を利用して分類する。
            Classifier classifier = ClassifierFactory.getInstance().getClassifier(list);
            List<WarningUnit> unitList = classifier.classify(list);

            // 分類した結果を結合する。
            joinList(resultList, unitList);
        }
        return resultList;
    }

    /**
     * WarningUnitのリストから、識別のためのマップを作成する。
     * @param warningUnitList 識別情報を保存する対象のwarningUnitのリスト 
     * @return 情報を保存したMap
     */
    private Map<String, List<WarningUnit>> makeMap(final List<WarningUnit> warningUnitList)
    {
        Map<String, List<WarningUnit>> map = new LinkedHashMap<String, List<WarningUnit>>();
        List<WarningUnit> unitList;
        // 全てのwarningUnitを取り出し、ルールID、クラス名、メソッド名、重要度をキーに
        // Mapを作成する。
        for (WarningUnit warningUnit : warningUnitList)
        {
            String key =
                         warningUnit.getId() + SEPARATOR + warningUnit.getClassName() + SEPARATOR
                                 + warningUnit.getMethodName() + SEPARATOR + warningUnit.getLevel();
            // イベントによる警告の場合、スタックトレースも比較対象とする。
            if (warningUnit.isEvent())
            {
                String stackTrace = warningUnit.getStackTrace();
                if (stackTrace != null)
                {
                    int length = Math.min(stackTrace.length(), COMPARE_LENGTH);
                    String stackTraceCompare = stackTrace.substring(0, length);
                    key += SEPARATOR + stackTraceCompare;
                }
            }
            unitList = map.get(key);
            // Mapに指定したキーが存在しないとき、新しくリストを作成する。
            // キーが存在する場合は、キーに対応するリストに、warningUnitを追加する。
            if (unitList == null)
            {
                unitList = new ArrayList<WarningUnit>();
                map.put(key, unitList);
            }
            unitList.add(warningUnit);
        }
        return map;
    }

    /**
     * 2つのリストを結合する。
     * <code>oldList</code> が <code>null</code> の場合は何もしない。
     *
     * @param oldList 結合先のリスト
     * @param unitList 新規で追加するリスト
     */
    public void joinList(final List<WarningUnit> oldList, final List<WarningUnit> unitList)
    {
        if (oldList != null && unitList != null)
        {
            oldList.addAll(unitList);
        }
    }
}
