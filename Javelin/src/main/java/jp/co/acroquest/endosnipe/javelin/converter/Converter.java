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
package jp.co.acroquest.endosnipe.javelin.converter;

import java.util.List;

import jp.co.smg.endosnipe.javassist.ClassPool;
import jp.co.smg.endosnipe.javassist.CtClass;
import jp.co.acroquest.endosnipe.javelin.conf.ExcludeConversionConfig;
import jp.co.acroquest.endosnipe.javelin.conf.IncludeConversionConfig;

/**
 * コード埋め込みのインターフェース
 * 
 * @author yamasaki
 */
public interface Converter
{
    /**
     * コード埋め込みを行う。
     * 
     * @param className クラス名
     * @param classfileBuffer クラスファイルのバッファ
     * @param pool ClassPool
     * @param ctClass CtClass
     * @param config Includeの設定
     * @param excludeConfigList Excludeの設定リスト
     * @return コード埋め込み後のクラスファイルのバッファ
     */
    byte[] convert(String className, byte[] classfileBuffer, ClassPool pool, CtClass ctClass,
            IncludeConversionConfig config, List<ExcludeConversionConfig> excludeConfigList);

    /**
     * コード埋め込み後の結果を返す。
     * @return コード埋め込み後のクラスファイルのバッファ
     */
    byte[] getResult();

    /**
     * 初期化メソッド
     */
    void init();
}
