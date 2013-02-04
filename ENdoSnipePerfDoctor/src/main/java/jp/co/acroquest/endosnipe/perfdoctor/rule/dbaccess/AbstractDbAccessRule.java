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
package jp.co.acroquest.endosnipe.perfdoctor.rule.dbaccess;

import java.util.List;

import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.rule.SQLThresholdStrategy;
import jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule;

/**
 * DB アクセスに関する判定を行うための基底クラス。
 * 
 * @author y-komori
 */
public abstract class AbstractDbAccessRule extends SingleElementRule
{
    /**
     * コンストラクタ。
     */
    public AbstractDbAccessRule()
    {
        this.setThresholdStrategy(new SQLThresholdStrategy());
    }

    /**
     * @param element JavelinLogElement
     * @see jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule#doJudgeElement
     * (jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement)
     */
    @Override
    protected void doJudgeElement(final JavelinLogElement element)
    {
        List<String> baseInfoList = element.getBaseInfo();

        // クラス名に、SQL発行を表す文字列が書かれているかどうかを調べる。
        // 書かれていない場合、処理を終了する。
        String className = baseInfoList.get(JavelinLogColumnNum.CALL_CALLEE_CLASS);
        if (!super.isSqlExec(className))
        {
            return;
        }

        // ログの種別をチェックする。Call以外であれば処理を終了する。
        String type = baseInfoList.get(JavelinLogColumnNum.ID);
        boolean isCall = JavelinConstants.MSG_CALL.equals(type);

        if (isCall == false)
        {
            return;
        }

        // JavelinLogElementの冒頭からSQLを取得する。
        String sqlStatement = baseInfoList.get(JavelinLogColumnNum.CALL_CALLEE_METHOD);

        // SQLの判定を行う。
        // argsの中に[VALUE]がある場合は、その値も用いる。
        String[] args = JavelinLogUtil.getArgs(element);
        String bindVal = null;
        for (int i = 0; i < args.length; i++)
        {
            bindVal = JavelinLogUtil.getArgContent(args[i], "[VALUE]");
            if (bindVal != null)
            {
                break;
            }
        }

        if (sqlStatement != null)
        {
            doJudgeContent(element, sqlStatement, bindVal);
        }

    }

    /**
     * コンテントの判定を行う。<br />
     * {@link #getTagName()} メソッドの返すタグに一致するコンテントの判定を行う。
     * 
     * @param element {@link JavelinLogElement} オブジェクト
     * @param content コンテント
     * @param bindVal 引数
     */
    protected abstract void doJudgeContent(JavelinLogElement element, String content, String bindVal);

    /**
     * タグ名称を返す。<br />
     * <code>[Time]</code> の形式でタグ名称を返す。<br />
     * サブクラスで実装してください。
     * 
     * @return タグ名称
     */
    protected abstract String getTagName();
}
