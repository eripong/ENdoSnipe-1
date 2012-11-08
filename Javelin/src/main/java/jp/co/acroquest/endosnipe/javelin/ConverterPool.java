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
package jp.co.acroquest.endosnipe.javelin;

import java.util.Map;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.converter.Converter;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ConcurrentHashMap;

/**
 * コンバータをキャッシュするプールです。
 * 
 * 同一インスタンスのConverterを同時に利用してしまうことが無いようにすることが目的です。
 * requestした際にプール内に対象のクラスのインスタンスがなければ新たに生成して返し、
 * releaseした際にはプールにインスタンスを登録します。
 * 
 * @author eriguchi
 */
public class ConverterPool
{
    /** コンバータを登録するMap */
    private final Map<String, Converter> converterMap_ = new ConcurrentHashMap<String, Converter>();

    /**
     * プールからConverterを取得します。
     * プール内に指定したクラスのインスタンスがなければ、新たに生成して返し、
     * インスタンスがあれば、プールから削除してそのインスタンスを返します。
     * 
     * @param converterClassName Converterクラス名。
     * @return Converterのインスタンス。
     */
    public synchronized Converter request(String converterClassName)
    {
        if (this.converterMap_.containsKey(converterClassName) == false)
        {
            loadConverter(converterClassName);
        }

        Converter converter = this.converterMap_.remove(converterClassName);
        return converter;
    }

    /**
     * 指定したConverterのインスタンスをプールに返します。
     * 
     * @param converter Converterのインスタンス。
     */
    public synchronized void release(Converter converter)
    {
        this.converterMap_.put(converter.getClass().getCanonicalName(), converter);
    }

    /**
     * コンバータをロードします。<br />
     * 
     * @param converterClassName コンバータのクラス名
     */
    private void loadConverter(final String converterClassName)
    {
        try
        {
            Converter converter = this.converterMap_.get(converterClassName);
            if (converter == null)
            {
                Class<Converter> clazz = (Class<Converter>)Class.forName(converterClassName);
                converter = clazz.newInstance();
                converter.init();
                this.converterMap_.put(converterClassName, converter);
            }
        }
        catch (ClassNotFoundException ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        catch (InstantiationException ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        catch (IllegalAccessException ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }
}
