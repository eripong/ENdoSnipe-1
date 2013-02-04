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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;

/**
 * 複数のルールを共通化するフィルタ
 * @author fujii
 *
 */
public class UnifiedFilter
{
    /** セパレータ */
    private static final String  SEPARATOR = ",";

    /** 統一するルールのリスト */
    private final List<String[]> unifiedRulesList_;

    /**
     * コンストラクタ<br />
     * 
     */
    public UnifiedFilter()
    {
        this.unifiedRulesList_ = new ArrayList<String[]>();
        this.unifiedRulesList_.add(new String[]{"COD.MTRC.METHOD_ELAPSEDTIME",
                "COD.MTRC.METHOD_TAT"});
        this.unifiedRulesList_.add(new String[]{"COD.MTRC.METHOD_PURE_CPU", "COD.MTRC.METHOD_CPU"});
        this.unifiedRulesList_.add(new String[]{"COD.MTRC.METHOD_PURE_WAIT", "COD.THRD.WAIT_TIME"});
        this.unifiedRulesList_.add(new String[]{"COD.THRD.BLK_TIME", "COD.THRD.BLK_TIME"});
    }

    /**
     * フィルターをかける。
     * @param warningUnitList WarningUnitのリスト
     * @return フィルター後の結果
     */
    public List<WarningUnit> doFilter(final List<WarningUnit> warningUnitList)
    {
        List<WarningUnit> deleteList = new ArrayList<WarningUnit>();
        List<WarningUnit> copyList = new ArrayList<WarningUnit>(warningUnitList);

        Map<String, List<WarningUnit>> warningMap = new LinkedHashMap<String, List<WarningUnit>>();

        // ルールID、クラス名、メソッド名、重要度をキーにMapを作成する。
        for (WarningUnit warningUnit : warningUnitList)
        {
            String[] idArray = searchUnifiedId(warningUnit.getId(), this.unifiedRulesList_);
            if (idArray != null)
            {
                createWarningMap(warningUnit, idArray, warningMap);
            }
        }

        // Mapに入力されたWarningUnitのリストを出力する。
        Collection<List<WarningUnit>> col = warningMap.values();
        for (List<WarningUnit> list : col)
        {
            // 優先度の高いルールのIDによる警告をすべてリストに入れ、
            // そのときの時刻をMapに登録する。
            List<WarningUnit> deleteTargetList = createDeleteList(list);

            // 分類した結果を結合する。
            joinList(deleteList, deleteTargetList);
        }
        copyList.removeAll(deleteList);

        return copyList;
    }

    /**
     * 指定したWarningUnitのIDが統一するWarningUnitのIDのリストと一致するか判定する。
     * @param id WarningUnitのID
     * @param unifiedList 統一するWarningUnitのリスト
     * @return 引数で指定したIDが含まれている統一するIDの組(見つからない場合はnullを返す)
     */
    private String[] searchUnifiedId(final String id, final List<String[]> unifiedList)
    {
        for (Iterator<String[]> iterator = unifiedList.iterator(); iterator.hasNext();)
        {
            String[] idArray = iterator.next();
            for (String unifiedId : idArray)
            {
                if (id.equals(unifiedId))
                {
                    return idArray;
                }
            }
        }
        return null;
    }

    /**
     * 指定したWarningUnitのIDが優先ルールのIDであるかを判定する。
     * @param id WarningUnitのID
     * @param unifiedList 統一するためのWarningUnitのリスト
     * @return 引数で指定したIDが優先ルールである、かつ同ルールに対するフィルタ処理でない。
     */
    private boolean containsSupperiorId(final String id, final List<String[]> unifiedList)
    {
        for (Iterator<String[]> iterator = unifiedList.iterator(); iterator.hasNext();)
        {
            String[] idArray = iterator.next();
            boolean isSupperiorRule = id.equals(idArray[0]);
            boolean isSameRule = idArray[0].equals(idArray[1]);
            if (isSupperiorRule == true && isSameRule == false)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 優先度に基づき、警告リストから削除するリストを作成する。<br>
     * 以下の順に引数で与えたリストを除くことで、削除対象のリストを作成する。<br>
     * <ol>
     * <li>優先度の高いルールに対する警告</li>
     * <li>階層の深い警告</li>
     * </ol>
     * なお、優先度の低いルールに対する警告は、すでに登録した警告の時間と重複が発生する場合は、
     * 削除対象のリストに加える。<br />
     * <br />
     * @param list 削除前の警告のリスト
     * @return 削除対象の警告のリスト
     * 
     */
    private List<WarningUnit> createDeleteList(final List<WarningUnit> list)
    {
        List<WarningUnit> inferriorList = new ArrayList<WarningUnit>();
        List<long[]> timeList = new ArrayList<long[]>();

        // 優先度の高いルールIDに対応する警告のリストと優先度の低いルールIDに対応する警告のリストに分類する。
        for (WarningUnit warningUnit : list)
        {
            String ruleId = warningUnit.getId();
            boolean isContains = containsSupperiorId(ruleId, this.unifiedRulesList_);
            if (isContains)
            {
                long[] time = {warningUnit.getStartTime(), warningUnit.getEndTime()};
                timeList.add(time);
            }
            else
            {
                inferriorList.add(warningUnit);
            }
        }

        // 優先度の低いルールIDに対応する警告のリストが空の場合、空のまま返す。
        if (inferriorList.size() == 0)
        {
            return inferriorList;
        }

        // 検出値順をキーに並びかえる。
        Collections.sort(inferriorList, new FileLineComparator());

        List<WarningUnit> deleteList = new ArrayList<WarningUnit>();

        // 優先度の低いリストを警告リストに加えるか判定する。
        for (WarningUnit warningUnit : inferriorList)
        {
            if (isTimeContains(warningUnit, timeList))
            {
                deleteList.add(warningUnit);
            }
            else
            {
                long[] time = {warningUnit.getStartTime(), warningUnit.getEndTime()};
                timeList.add(time);
            }
        }
        return deleteList;
    }

    /**
     * 指定した警告の時間が有効化どうか。<br />
     * すでに登録した警告の時間と重なるかで判断する。<br />
     * @param warningUnit 警告
     * @param timeList すでに登録した時間のリスト
     * @return true:すでに登録した警告の時間と重なる、false:時間が重ならない。
     */
    private boolean isTimeContains(final WarningUnit warningUnit, final List<long[]> timeList)
    {
        long startTime = warningUnit.getStartTime();
        long endTime = warningUnit.getEndTime();

        for (long[] time : timeList)
        {
            // 開始時間か終了時間が今まで登録した警告の時間内に含まれるかどうかで判断する。
            if ((time[0] <= endTime && startTime <= time[1]))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * WarningUnitのリストから、識別のためのMapを作成する。<br />
     * ルールID、ログファイル名、重要度をキーにMapを作成する。<br />
     * @param warningUnitList 識別情報を保存する対象のwarningUnitのリスト 
     * @param idArray 2つのルールの結果を統一するためのリスト
     * @param warningMap 警告を格納するためのMap
     */
    private void createWarningMap(final WarningUnit warningUnit, final String[] idArray,
            final Map<String, List<WarningUnit>> warningMap)
    {
        List<WarningUnit> unitList;
        // 全てのwarningUnitを取り出し、ログファイル名、重要度をキーにMapを作成する。
        String key =
                     idArray[0] + SEPARATOR + warningUnit.getLogFileName() + SEPARATOR
                             + warningUnit.getLevel();
        unitList = warningMap.get(key);
        // Mapに指定したキーが存在しないとき、新しくリストを作成する。
        // キーが存在する場合は、キーに対応するリストに、warningUnitを追加する。
        if (unitList == null)
        {
            unitList = new ArrayList<WarningUnit>();
            warningMap.put(key, unitList);
        }
        unitList.add(warningUnit);
    }

    /**
     * 2つのリストを結合する。
     * <code>oldList</code> が <code>null</code> の場合は何もしない。
     *
     * @param oldList 結合先のリスト
     * @param unitList 新規で追加するリスト
     */
    private void joinList(final List<WarningUnit> oldList, final List<WarningUnit> unitList)
    {
        if (oldList != null && unitList != null)
        {
            oldList.addAll(unitList);
        }
    }
}
