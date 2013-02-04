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
package jp.co.acroquest.endosnipe.perfdoctor;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;

/**
 * PerformanceDoctorルールのインタフェース。
 * @author tanimoto
 *
 */
public interface PerformanceRule
{
    /**
     * ログの判定を行う。
     * @param javelinLogElementList ログ要素
     * @return 警告リスト
     */
    List<WarningUnit> judge(List<JavelinLogElement> javelinLogElementList);

    /**
     * ルールIDを取得する。
     * @return ルールID
     */
    String getId();

    /**
     * 問題レベルを取得する。
     * @return 問題レベル
     */
    String getLevel();

    /**
     * ルールの設定値に対して初期化を行います。<br />
     */
    void init();
}
