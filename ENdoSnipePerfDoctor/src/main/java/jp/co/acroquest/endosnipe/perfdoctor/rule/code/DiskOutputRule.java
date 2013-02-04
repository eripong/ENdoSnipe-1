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
package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogConstants;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.rule.AbstractSingleValueLimitRule;

/**
 * １回の処理辺りのディスク出力量が閾値以上になった場合に出力するルール
 * 
 * @author S.Kimura
 */
public class DiskOutputRule extends AbstractSingleValueLimitRule
{
    /**
     * RETURNログを示す種別を返す。
     * 
     * @return RETURNログ種別
     */
    @Override
    protected String getTargetID()
    {
        return JavelinConstants.MSG_CALL;
    }

    /**
     * 入出力情報を示す情報タグを返す。
     * 
     * @return 入出力情報タグ
     */
    @Override
    protected String getTargetInfoTag()
    {
        return JavelinParser.TAG_TYPE_EXTRAINFO;
    }

    /**
     * ディスク出力量を示すパラメータ名称を返す
     * 
     * @return ディスク出力量パラメータ名称
     */
    @Override
    protected String getTargetValueName()
    {
        return JavelinLogConstants.IOPARAM_DISK_OUTPUT;
    }
}
