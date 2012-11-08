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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ロードされたクラスを保存しておくクラス
 * 
 * @author eriguchi
 *
 */
public class ClassReserveTransformer implements ClassFileTransformer
{
    /** ロードされたクラス。 */
    private Map<String, Class<?>> classMap_ = new ConcurrentHashMap<String, Class<?>>();

    /**
     * ロードされたクラスを保存する。
     * @param loader loader
     * @param className className
     * @param classBeingRedefined classBeingRedefined
     * @param protectionDomain protectionDomain
     * @param classfileBuffer classfileBuffer
     * 
     * @return 常にnullを返す。
     * @throws IllegalClassFormatException 発生しない
     * 
     */
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer)
        throws IllegalClassFormatException
    {
        this.classMap_.put(className, classBeingRedefined);
        return null;
    }

    /**
     * ロードされたクラスを取得する。
     * 
     * @return ロードされたクラスの配列。
     */
    public Class<?>[] getLoadedClasses()
    {
        List<Class<?>> classList = new ArrayList<Class<?>>(this.classMap_.size());
        for (Class<?> clazz : this.classMap_.values())
        {
            classList.add(clazz);
        }

        Class<?>[] loadedClasses = classList.toArray(new Class<?>[classList.size()]);

        return loadedClasses;
    }

}
