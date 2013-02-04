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

import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;

/**
 * 受け取ったリストのサイズを基に、各ルールに基づいたWarningUnitの分類器を生成する。<br />
 * リストのサイズが10以下のときは、SimpleClassifierを生成し、<br />
 * リストのサイズが10よりも大きいときは、KmeansClassifierを生成する。<br />
 * 
 * @author fujii
 * 
 */
public class ClassifierFactory
{
    /** 分類器の生成 */
    private static ClassifierFactory factory__        = new ClassifierFactory();

    /** フィルタを切り替える */
    private static final int         FILTER_THRESHOLD = 10;

    /**
     * ClassfierFactoryオブジェクトを返す。
     * 
     * @return ClassfierFactoryオブジェクト
     */
    public static ClassifierFactory getInstance()
    {
        return factory__;
    }

    /**
     * リストの大きさによって、異なる分類器を取得する。
     * @param list WariningUnitのリスト
     * @return フィルター
     */
    public Classifier getClassifier(final List<WarningUnit> list)
    {
        if (list.size() <= FILTER_THRESHOLD)
        {
            return new SimpleClassifier();
        }
        return new KmeansClassifier();
    }
}
