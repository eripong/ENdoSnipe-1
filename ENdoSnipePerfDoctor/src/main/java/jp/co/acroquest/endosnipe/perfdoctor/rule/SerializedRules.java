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
package jp.co.acroquest.endosnipe.perfdoctor.rule;

/**
 * シリアライズ化されたルールを格納するクラス。<br />
 *
 * @author sakamoto
 */
public class SerializedRules
{

    /** ruleSetConfigMap_の内容を一時的に保存しておく配列 */
    private final byte[] ruleSetConfigMapData_;

    /** ruleSetMap_の内容を一時的に保存しておく配列。 */
    private final byte[] ruleSetMapData_;

    /**
     * シリアライズ化されたルールを格納するオブジェクトを生成します。<br />
     *
     * @param ruleSetConfigMapData ruleSetConfigMap をシリアライズ化したデータ
     * @param ruleSetMapData ruleSetMap をシリアライズ化したデータ
     */
    public SerializedRules(final byte[] ruleSetConfigMapData, final byte[] ruleSetMapData)
    {
        this.ruleSetConfigMapData_ = ruleSetConfigMapData;
        this.ruleSetMapData_ = ruleSetMapData;
    }

    /**
     * ruleSetConfigMap をシリアライズ化したデータを返します。<br />
     *
     * @return ruleSetConfigMap をシリアライズ化したデータ
     */
    public byte[] getRuleSetConfigMapData()
    {
        return this.ruleSetConfigMapData_;
    }

    /**
     * ruleSetMap をシリアライズ化したデータを返します。<br />
     *
     * @return ruleSetMap をシリアライズ化したデータ
     */
    public byte[] getRuleMapData()
    {
        return this.ruleSetMapData_;
    }
}
