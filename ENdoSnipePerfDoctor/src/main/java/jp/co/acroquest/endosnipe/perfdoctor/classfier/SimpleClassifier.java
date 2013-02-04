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
 * 警告リストの中から、検出値の最小値の2倍の値をキーとして、<br />
 * 警告リストを分類する。<br />
 * 検出値が文字列の場合、あるいは検出値が0の場合、警告リストの先頭を返す。<br />
 * 
 * @author fujii
 * 
 */
public class SimpleClassifier implements Classifier
{
    /** Double型の最大値 */
    private static final double MAX_VALUE = Double.MAX_VALUE;

    /**
     * 入力したリストを分類して、検出値が最大となるものを返す。
     * @param warningUnitList WarningUnitのリスト
     * @return WarningUnitのリストにフィルターをかけたリスト
     */
    public List<WarningUnit> classify(final List<WarningUnit> warningUnitList)
    {
        List<WarningUnit> resultList;
        // 基準となるキーを取得する。
        // ただし、キーが数字でない場合(テーブル名など)のときは、
        // Listの先頭だけを取得したListを返す。
        double keyvalue = getKeyValue(warningUnitList);

        if (keyvalue > 0)
        {
            resultList = selectList(warningUnitList, keyvalue);
        }
        else
        {
            resultList = new ArrayList<WarningUnit>();
            resultList.add(warningUnitList.get(0));
        }

        return resultList;
    }

    /**
     * 分類するために必要なキーの値を返す。
     * このメソッドでは、検出値の最小値の2倍を返す。
     * @param warningUnitList WarningUnitのリスト
     * @return キーの値
     * @throws NumberFormatException　検出値が数字以外のものであるとき。
     */
    private double getKeyValue(final List<WarningUnit> warningUnitList)
    {
        double min = MAX_VALUE;
        // 全てのWaningUnitを見て、最小のものを抽出する。
        for (WarningUnit unit : warningUnitList)
        {
            Object[] args = unit.getArgs();

            int targetValueIndex = PerformanceDoctorFilter.TARGET_VALUE_INDEX;
            if (args == null || args.length < targetValueIndex + 1)
            {
                min = 0;
                break;
            }

            // 検出値が数字以外のデータについては、例外を返す。
            double argNum;
            try
            {
                String argString = args[targetValueIndex].toString();
                argNum = Double.parseDouble(argString);
            }
            catch (NumberFormatException ex)
            {
                min = 0;
                break;
            }
            if (min > argNum)
            {
                min = argNum;
            }
        }
        return min * 2;
    }

    /**
     * フィルター後のリストを抽出する。
     * @param warningUnitList WarningUnitのリスト
     * @param keyValue 分類でキーとなる値。
     * @return フィルター後のリスト
     */
    private List<WarningUnit> selectList(final List<WarningUnit> warningUnitList,
            final double keyValue)
    {

        Map<Integer, WarningUnit> resultMap = new LinkedHashMap<Integer, WarningUnit>();
        for (WarningUnit unit : warningUnitList)
        {
            Object[] args = unit.getArgs();
            // try-catch はgetKeyValueで実行済みのため、省略する。
            double argNum =
                            Double.parseDouble(args[PerformanceDoctorFilter.TARGET_VALUE_INDEX].toString());
            int type = (int)(argNum / keyValue);
            WarningUnit oldUnit = resultMap.get(type);
            if (oldUnit == null)
            {
                resultMap.put(type, unit);
            }
            else
            {
                updateWarningUnit(type, resultMap, oldUnit, unit);
            }
        }
        List<WarningUnit> resultList = convertMapToList(resultMap);
        return resultList;
    }

    /**
     * 保存されているUnit(それまでの最大の検出値を持つUnit)と比較対象のUnitを比較し、<br/>
     * 比較対象の方の値が大きければ、resultMap中のキーtypeに対する値を更新する。
     * @param type キー
     * @param resultMap 結果を出力するMap
     * @param oldUnit キーtypeに対する値(WarningUnit)
     * @param comparedUnit 比較するWarningUnit
     */
    private void updateWarningUnit(final int type, final Map<Integer, WarningUnit> resultMap,
            final WarningUnit oldUnit, final WarningUnit comparedUnit)
    {
        String oldValueString =
                                (oldUnit.getArgs()[PerformanceDoctorFilter.TARGET_VALUE_INDEX]).toString();
        String compareValueString =
                                    (comparedUnit.getArgs()[PerformanceDoctorFilter.TARGET_VALUE_INDEX]).toString();
        double oldValue = Double.parseDouble(oldValueString);
        double comparedValue = Double.parseDouble(compareValueString);

        // 検出値が大きいものが優先されるルールの場合には検出値が大きい警告を登録し、
        // 検出値が小さいものが優先されるルールの場合には検出値が小さい警告を登録する。
        boolean isDescend = oldUnit.isDescend();
        if (isDescend)
        {
            if (oldValue < comparedValue)
            {
                resultMap.put(type, comparedUnit);
            }
        }
        else
        {
            if (oldValue > comparedValue)
            {
                resultMap.put(type, comparedUnit);
            }
        }
    }

    /**
     * フィルター後のマップからリスト型に変換する。
     * @param resultMap フィルター後のマップ 
     * @return　フィルター後のWarningUnitのリスト
     */
    private List<WarningUnit> convertMapToList(final Map<Integer, WarningUnit> resultMap)
    {
        List<WarningUnit> resultList = new ArrayList<WarningUnit>();

        Collection<WarningUnit> col = resultMap.values();
        resultList.addAll(col);
        return resultList;
    }
}
