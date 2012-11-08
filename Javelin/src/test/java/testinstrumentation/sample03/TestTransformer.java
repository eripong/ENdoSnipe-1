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
package testinstrumentation.sample03;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import jp.co.smg.endosnipe.javassist.CannotCompileException;
import jp.co.smg.endosnipe.javassist.ClassPool;
import jp.co.smg.endosnipe.javassist.CtClass;
import jp.co.smg.endosnipe.javassist.CtMethod;
import jp.co.smg.endosnipe.javassist.NotFoundException;

public class TestTransformer implements ClassFileTransformer
{

    public byte[] transform(final ClassLoader loader, final String className,
            final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain,
            final byte[] classfileBuffer)
        throws IllegalClassFormatException
    {
        // クラスプールを取る
        ClassPool objClassPool = ClassPool.getDefault();
        // Javassistで使えるように、変換する
        String toJavassistClassName = className.replace("/", ".");

        // 変換用オブジェクト
        CtClass objCtClass = null;
        try
        {
            objCtClass = objClassPool.get(toJavassistClassName);
        }
        catch (NotFoundException objNotFoundException)
        {
            objNotFoundException.printStackTrace();
        }

        // 全てメソッドを取得して、変換する
        CtMethod[] objCtMethodArr = objCtClass.getDeclaredMethods();
        for (int index = 0; index < objCtMethodArr.length; index++)
        {
            try
            {
                // 前処理コードを作る
                StringBuffer beforeCode = new StringBuffer();
                beforeCode.append("{ System.out.println(");
                beforeCode.append("\"▼▼▼メソッド『");
                beforeCode.append(toJavassistClassName);
                beforeCode.append(".\"");
                beforeCode.append("+");
                beforeCode.append("\"");
                beforeCode.append(objCtMethodArr[index].getName());
                beforeCode.append("』が呼び出された。\"");
                beforeCode.append("); }");

                // 前処理コードを埋め込む
                objCtMethodArr[index].insertBefore(beforeCode.toString());

                // 後処理コードを作る
                StringBuffer afterCode = new StringBuffer();
                afterCode.append("{ System.out.println(");
                afterCode.append("\"▲▲▲メソッド『");
                afterCode.append(toJavassistClassName);
                afterCode.append(".\"");
                afterCode.append("+");
                afterCode.append("\"");
                afterCode.append(objCtMethodArr[index].getName());
                afterCode.append("』が終りました。\"");
                afterCode.append("); }");
                afterCode.append("{ System.out.println(");
                afterCode.append("); }");

                // 後処理コードを埋め込む
                objCtMethodArr[index].insertAfter(afterCode.toString());
            }
            catch (CannotCompileException objCannotCompileException)
            {
                objCannotCompileException.printStackTrace();
            }
        }

        // 変換したクラスを返却する
        byte[] byteNewClassArr = null;
        try
        {
            byteNewClassArr = objCtClass.toBytecode();
        }
        catch (IOException objIOException)
        {
            objIOException.printStackTrace();
        }
        catch (CannotCompileException objCannotCompileException)
        {
            objCannotCompileException.printStackTrace();
        }
        return byteNewClassArr;
    }
}
