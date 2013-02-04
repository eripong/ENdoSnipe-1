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
import java.util.Collections;
import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;

/**
 * 警告リストの中から、検出値をキーとして、Kmeans法を利用して警告リストを分類する。<br />
 * 分類するクラスタ数は5(CLASSTERNUM)とする。
 * 検出値が文字列の場合、警告リストの先頭を返す。<br />
 * 
 * @author fujii
 *
 */
public class KmeansClassifier implements Classifier
{
    /** 分類するクラスタの最大数。(多くの場合この個数になる。) */
    private static final int CLASSTERNUM = 5;

    /**
     * {@inheritDoc}
     * 
     * 注:warningUnitListに対し副作用を起こすので注意する。
     */
    public List<WarningUnit> classify(final List<WarningUnit> warningUnitList)
    {

        // 検出値のチェックを行う。
        // 検出値が数値でない場合、リストの先頭を抽出して作成したリストを返す。
        if (isNumber(warningUnitList) == false)
        {
            List<WarningUnit> resultList = new ArrayList<WarningUnit>();
            resultList.add(warningUnitList.get(0));
            return resultList;
        }
        // 検出値順をキーに並びかえる。
        Collections.sort(warningUnitList, new DetectionValueComparator());

        WarningUnitCluster[] oldClusters = new WarningUnitCluster[CLASSTERNUM];
        // クラスタの初期化を行う。
        initCluster(warningUnitList, oldClusters);
        while (true)
        {
            // 新規クラスタの初期化を行う。
            WarningUnitCluster[] clusters = new WarningUnitCluster[CLASSTERNUM];
            for (int clusterNum = 0; clusterNum < CLASSTERNUM; clusterNum++)
            {
                clusters[clusterNum] = new WarningUnitCluster();
            }

            // クラスタ毎の平均値を求める。
            double[] averages = getAverages(oldClusters);

            // WarningUnitをクラスタに配置する。
            for (WarningUnit unit : warningUnitList)
            {
                dispatch(unit, clusters, averages);
            }

            // 前回のクラスタと今回のクラスタを比較し、
            // 同じクラスタのときなら、処理を終了する。
            // 異なるクラスタの場合、クラスタの再配置を行う。
            if (compare(oldClusters, clusters))
            {
                break;
            }
            oldClusters = clusters;
        }

        // クラスタからWarningUnitを抽出し、リストを作成する。
        List<WarningUnit> resultList = clusterToList(oldClusters);
        return resultList;
    }

    /**
     * 検出値が数字であるかチェックする。
     * @param warningUnitList WarningUnitのリスト
     * @return 検出値が数字であるか(数字であるならtrue)
     */
    private boolean isNumber(final List<WarningUnit> warningUnitList)
    {
        for (WarningUnit unit : warningUnitList)
        {
            Object[] args = unit.getArgs();

            // 検出値が数字以外のデータについては、falseを返す。
            // また配列の長さが、検出値が格納されるindexに及ばない場合にもfalseを返す。
            if (args.length < PerformanceDoctorFilter.TARGET_VALUE_INDEX + 1)
            {
                return false;
            }
            try
            {
                Double.parseDouble(args[PerformanceDoctorFilter.TARGET_VALUE_INDEX].toString());
            }
            catch (NumberFormatException ex)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * クラスタを初期化する。
     * @param warningUnitList WarningUnitのリスト
     * @param clusters クラスタ
     */
    private void initCluster(final List<WarningUnit> warningUnitList,
            final WarningUnitCluster[] clusters)
    {
        for (int clusterNum = 0; clusterNum < CLASSTERNUM; clusterNum++)
        {
            clusters[clusterNum] = new WarningUnitCluster();
        }

        // 分割点を作成する。
        double cutPoint = (double)warningUnitList.size() / CLASSTERNUM;
        int count = 0;

        // 分割点を基に、WarningUnitをクラスタに分割する。
        for (WarningUnit unit : warningUnitList)
        {
            clusters[(int)(count / cutPoint)].add(unit);
            count++;
        }
    }

    /**
     * クラスタの平均値を取得する。
     * @param clusters クラスタ
     * @return　平均値の配列
     */
    private double[] getAverages(final WarningUnitCluster[] clusters)
    {
        double[] averages = new double[CLASSTERNUM];
        for (int num = 0; num < clusters.length; num++)
        {
            averages[num] = clusters[num].average();
        }
        return averages;
    }

    /**
     * クラスタにWarningUnitを配置する。
     * @param clusters
     * @param average
     */
    private void dispatch(final WarningUnit warningUnit, final WarningUnitCluster[] clusters,
            final double[] average)
    {
        int position = 0;
        double minimizeDistant = Double.MAX_VALUE;

        //　WarningUnitとクラスタの重心との距離が最も近くなるように、
        // WarningUnitを配置する。
        for (int num = 0; num < clusters.length; num++)
        {
            String argsString =
                                warningUnit.getArgs()[PerformanceDoctorFilter.TARGET_VALUE_INDEX].toString();
            double argsNum = Double.parseDouble(argsString);
            double distant = Math.abs(average[num] - argsNum);
            if (distant < minimizeDistant)
            {
                minimizeDistant = distant;
                position = num;
            }
        }
        clusters[position].add(warningUnit);
    }

    /**
     * 2つのクラスタを比較し、同じかどうかを調べる。<br />
     * 同じかどうかは、クラスタに含まれる要素数で比較する。
     * @param beforeClusters 再配置前のクラスタ
     * @param afterClusters 再配置後のクラスタ
     * @return true:2つのクラスタが同じ。false:2つのクラスタが異なる。
     */
    private boolean compare(final WarningUnitCluster[] beforeClusters,
            final WarningUnitCluster[] afterClusters)
    {
        for (int num = 0; num < CLASSTERNUM; num++)
        {
            if (beforeClusters[num].getSize() != afterClusters[num].getSize())
            {
                return false;
            }
        }
        return true;

    }

    /**
     * クラスタからWarningUnitのリストを作成する。
     * @param clusters クラスタ配列。
     * @return 作成したリスト
     */
    private List<WarningUnit> clusterToList(final WarningUnitCluster[] clusters)
    {
        List<WarningUnit> list = new ArrayList<WarningUnit>();

        // 各クラスタの最後尾を取得し、リストを作成する。
        for (WarningUnitCluster cluster : clusters)
        {
            WarningUnit warningUnit = cluster.getLastWarningUnit();
            if (warningUnit != null)
            {
                list.add(warningUnit);
            }
        }
        return list;
    }
}
