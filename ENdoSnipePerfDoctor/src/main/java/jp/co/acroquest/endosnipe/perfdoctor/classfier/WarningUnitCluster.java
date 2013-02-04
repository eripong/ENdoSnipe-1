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
import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;

/**
 * WarningUnitのクラスタ
 * @author fujii
 *
 */
public class WarningUnitCluster
{
    /** クラスタに含まれるWarningUnitのリスト。 */
    private final List<WarningUnit> clusterList_ = new ArrayList<WarningUnit>();

    /**
     * クラスタにWarningUnitを追加する。
     * @param warningUnit クラスタに追加するWarningUnit。
     */
    public void add(final WarningUnit warningUnit)
    {
        this.clusterList_.add(warningUnit);
    }

    /**
     * クラスタ数を取得する。
     * @return クラスター数
     */
    public int getSize()
    {
        return this.clusterList_.size();
    }

    /**
     * クラスタの平均値を求める。
     * @return 平均値
     */
    public double average()
    {
        double sum = 0;
        for (WarningUnit unit : this.clusterList_)
        {
            String argsString =
                                unit.getArgs()[PerformanceDoctorFilter.TARGET_VALUE_INDEX].toString();
            double argsNum = Double.parseDouble(argsString);
            sum += argsNum;
        }
        return sum / getSize();
    }

    /**
     * クラスタ中で優先度の高い検出値を持つものを返します。<br />
     * 
     * @return 最後尾のWarningUnit
     */
    public WarningUnit getLastWarningUnit()
    {
        int size = this.clusterList_.size();
        if (size == 0)
        {
            return null;
        }

        // 検出値が大きいものが優先されるルールの場合にはリストの最後尾を返し、
        // 検出値が小さいものが優先されるルールの場合にはリストの先頭を返す。
        boolean isDescend = this.clusterList_.get(0).isDescend();
        if (isDescend)
        {
            return this.clusterList_.get(size - 1);
        }
        return this.clusterList_.get(0);
    }
}
