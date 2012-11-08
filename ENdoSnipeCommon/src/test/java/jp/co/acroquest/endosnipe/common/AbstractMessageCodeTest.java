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
package jp.co.acroquest.endosnipe.common;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.seasar.framework.message.MessageFormatter;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;

/**
 * メッセージコード用定数クラスとプロパティファイルの対応をチェックする
 * テストクラスのための基底クラスです。<br />
 * 
 * @author y-komori
 */
public abstract class AbstractMessageCodeTest extends TestCase
{
    /** テスト対象クラス */
    private Class<?> messageCodeClass_;

    /** テスト対象バンドル名 */
    private String resourceBundleName_;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp()
        throws Exception
    {
        String testClassName = getClass().getName();
        String targetClassName =
                testClassName.substring(0, testClassName.length() - "Test".length());
        messageCodeClass_ = Class.forName(targetClassName);
        resourceBundleName_ = getResourceBundleName();
    }

    /**
     * 定数クラスに定義されている定数に対応するメッセージがプロパティファイル
     * に登録されているかどうかをテストします。<br />
     */
    public void testConstants()
    {
        Field[] fields = messageCodeClass_.getDeclaredFields();
        for (Field field : fields)
        {
            if (field.getDeclaringClass() == messageCodeClass_)
            {
                String code = FieldUtil.getString(field);
                String message = MessageFormatter.getSimpleMessage(code, null);
                assertFalse(code + " is not found in " + resourceBundleName_ + ".",
                            StringUtil.isEmpty(message));
            }
        }
    }

    /**
     * プロパティファイルに登録されているキーがすべて定数クラスに
     * に登録されているかどうかをテストします。<br />
     */
    public void testMessages()
    {
        Map<String, String> constantMap = new HashMap<String, String>();

        Field[] fields = messageCodeClass_.getDeclaredFields();
        for (Field field : fields)
        {
            constantMap.put(FieldUtil.getString(field), field.getName());
        }

        ResourceBundle bundle = ResourceBundle.getBundle(resourceBundleName_);
        assertNotNull("1", bundle);

        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements())
        {
            String key = keys.nextElement();
            assertTrue(key + " is not found in " + messageCodeClass_.getName(),
                       constantMap.containsKey(key));
        }
    }

    /**
     * テスト対象のリソースバンドル名を返します。<br />
     * 
     * @return テスト対象のリソースバンドル名
     */
    abstract protected String getResourceBundleName();
}
