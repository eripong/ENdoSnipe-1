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
package jp.co.acroquest.endosnipe.javelin.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import jp.co.acroquest.endosnipe.javelin.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;

/**
 * パラメータ詳細化を行うクラス。
 * 
 * @author kato
 */
public class DetailStringBuilder
{
    private static final int DEFAULT_DEPTH = 1;

    private static final String VALUE_HEADER = "[";

    private static final String VALUE_FOOTER = "]";

    private static final String KEY_VALUE_SEPARATOR = " = ";

    private static final String VALUE_SEPARATOR = " , ";

    private static final String DEFAULT_VALUE = "????";

    private static final String NULL_VALUE = "null";

    private static final String INNER_CLASS_SEPARATOR_CHAR = "$";

    private static final String OBJECT_ID_SEPARATOR = ":";

    private static final String CLASS_NAME_SEPARATOR = "@";

    // 即出力対象とするクラス群
    private static final Set<String> PRINT_CLASS_SET = new HashSet<String>();
    static
    {
        PRINT_CLASS_SET.add("Short");
        PRINT_CLASS_SET.add("Integer");
        PRINT_CLASS_SET.add("Long");
        PRINT_CLASS_SET.add("String");
        PRINT_CLASS_SET.add("Boolean");
        PRINT_CLASS_SET.add("Byte");
        PRINT_CLASS_SET.add("Character");
        PRINT_CLASS_SET.add("Float");
        PRINT_CLASS_SET.add("Double");
    }

    private DetailStringBuilder()
    {

    }

    /**
     * Objectの情報出力を行う 
     * 入力された深度情報にあわせ、フィールドを辿るかその場で出力するか判定する
     * 
     * @param object 出力対象オブジェクト
     * @return 出力結果
     */
    public static String buildDetailString(final Object object)
    {
        // 出力深度1で呼び出す
        String detailString = buildDetailString(object, DEFAULT_DEPTH);

        return detailString;
    }

    /**
     * Objectの情報出力を行う 
     * 入力された深度情報にあわせ、フィールドを辿るかその場で出力するか判定する
     * 
     * @param object 出力対象オブジェクト
     * @param detailDepth 設定深度
     * @return 出力結果
     */
    protected static String buildDetailString(final Object object, final int detailDepth)
    {
        // nullの場合は"null"と出力する。
        if (object == null)
        {
            return NULL_VALUE;
        }

        // Stringの場合はそのまま出力する。
        if (object instanceof String)
        {
            return (String)object;
        }

        //先頭にヘッダ文字列をつける
        String detailString = toDetailString(object, detailDepth, 0);
        StringBuilder detailBuilder = new StringBuilder();
        detailBuilder.append(object.getClass().getName());
        detailBuilder.append(CLASS_NAME_SEPARATOR);
        detailBuilder.append(Integer.toHexString(System.identityHashCode(object)));
        detailBuilder.append(OBJECT_ID_SEPARATOR);
        detailBuilder.append(detailString);
        return detailBuilder.toString();

    }

    /**
     * Objectの詳細文字列化を行う 
     * 入力された深度情報にあわせ、フィールドを辿るかその場で出力するか判定する
     * 
     * @param object 出力対象オブジェクト
     * @param detailDepth  出力深度
     * @param currentDepth 現在深度
     * @return 出力結果
     */
    protected static String toDetailString(final Object object, final int detailDepth,
            final int currentDepth)
    {
        // 現在の階層の深さが設定値以上の場合、toStringの結果を返す
        if (currentDepth >= detailDepth)
        {
            return buildString(object);
        }

        // 即出力対象となるオブジェクトの場合、toStringの結果を返す
        if (isPrintable(object))
        {
            return buildString(object);
        }

        Class<?> clazz = object.getClass();
        // 配列の場合、配列専用処理で出力を行う
        if (clazz.isArray())
        {
            return toStringArrayObject(object, detailDepth, currentDepth);
        }
        // コレクションの場合、コレクション専用処理で出力を行う
        if (object instanceof Collection)
        {
            Collection<?> collectionObject = (Collection<?>)object;
            return toStringCollectionObject(collectionObject, detailDepth, currentDepth);
        }
        // Mapの場合、Map専用処理で出力を行う
        if (object instanceof Map<?, ?>)
        {
            Map<?, ?> mapObject = (Map<?, ?>)object;
            return toStringMapObject(mapObject, detailDepth, currentDepth);
        }

        Field[] fields = clazz.getDeclaredFields();
        try
        {
            AccessibleObject.setAccessible(fields, true);
        }
        catch (SecurityException scex)
        {
            return buildString(object);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(VALUE_HEADER);
        boolean separatorFlag = false;
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];

            boolean printableFlag = isPrintable(field);

            if (printableFlag)
            {
                if (separatorFlag)
                {
                    builder.append(VALUE_SEPARATOR);
                }
                String fieldName = field.getName();
                builder.append(fieldName).append(KEY_VALUE_SEPARATOR);

                Object fieldValue = null;
                try
                {
                    fieldValue = field.get(object);
                    builder.append(toDetailString(fieldValue, detailDepth, currentDepth + 1));

                }
                // エラーが発生した場合はデフォルト文字列とする
                catch (IllegalAccessException iaex)
                {
                    builder.append(DEFAULT_VALUE);
                }
                separatorFlag = true;
            }
        }
        builder.append(VALUE_FOOTER);
        return builder.toString();
    }

    /**
     * Mapの文字列出力を行う。
     * [key1 = value1 , key2 = value2 ..... keyn = valuen]の形式で出力する
     * 
     * @param mapObject 対象となるMap
     * @param detailDepth 出力深度
     * @param currentDepth 現在深度
     * @return Mapの文字列表現
     */
    protected static String toStringMapObject(final Map<?, ?> mapObject, final int detailDepth,
            final int currentDepth)
    {
        // 配列がnullの時は"null"を返す。
        if (mapObject == null)
        {
            return NULL_VALUE;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(VALUE_HEADER);

        Object[] keys = mapObject.keySet().toArray();
        int length = keys.length;

        for (int i = 0; i < length; i++)
        {
            if (i > 0)
            {
                builder.append(VALUE_SEPARATOR);
            }

            Object item = mapObject.get(keys[i]);
            builder.append(buildString(keys[i])).append(KEY_VALUE_SEPARATOR);

            if (item == null)
            {
                builder.append(NULL_VALUE);
            }
            else
            {
                builder.append(toDetailString(item, detailDepth, currentDepth + 1));
            }
        }

        builder.append(VALUE_FOOTER);
        return builder.toString();
    }

    /**
     * 配列オブジェクトのログ出力を行う
     * 
     * @param array 出力対象（配列）
     * @param detailDepth 出力深度
     * @param currentDepth 現在深度
     * @return 出力文字列
     */
    protected static String toStringArrayObject(final Object array, final int detailDepth,
            final int currentDepth)
    {
        // 配列がnullの時は"null"を返す。
        if (array == null)
        {
            return NULL_VALUE;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(VALUE_HEADER);
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++)
        {
            Object item = Array.get(array, i);
            if (i > 0)
            {
                builder.append(VALUE_SEPARATOR);
            }

            if (item == null)
            {
                builder.append(NULL_VALUE);
            }
            else
            {
                builder.append(toDetailString(item, detailDepth, currentDepth + 1));
            }
        }
        builder.append(VALUE_FOOTER);
        return builder.toString();
    }

    /**
     * コレクションオブジェクトのログ出力を行う
     * 
     * @param collection 出力対象（配列）
     * @param detailDepth 出力深度
     * @param currentDepth 現在深度
     * @return 出力文字列
     */
    protected static String toStringCollectionObject(final Collection<?> collection,
            final int detailDepth, final int currentDepth)
    {
        // コレクションがnullの時は"null"を返す。
        if (collection == null)
        {
            return NULL_VALUE;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(VALUE_HEADER);

        boolean separatorFlag = false;
        for (Object item : collection)
        {
            if (separatorFlag)
            {
                builder.append(VALUE_SEPARATOR);
            }

            if (item == null)
            {
                builder.append(NULL_VALUE);
            }
            else
            {
                builder.append(toDetailString(item, detailDepth, currentDepth + 1));
            }
            separatorFlag = true;
        }

        builder.append(VALUE_FOOTER);
        return builder.toString();
    }

    /**
     * ToStringの結果を返す
     * 
     * @param object 変換対象
     * @return ToStringの結果
     */
    public static String buildString(final Object object)
    {
        // オブジェクトがnullの時は"null"を返す。
        if (object == null)
        {
            return NULL_VALUE;
        }

        // toStringは例外を発生させることがあるため、発生時は
        // "????"という文字列を返すようにする。
        try
        {
            return object.toString();
        }
        catch (Throwable th)
        {
            return DEFAULT_VALUE;
        }
    }

    /**
     * 出力対象のオブジェクトか判定を行う
     * 
     * @param object 判定対象
     * @return 判定結果
     */
    protected static boolean isPrintable(final Object object)
    {
        if (object == null)
        {
            return true;
        }

        // Fieldオブジェクトの場合、インナークラス、transientフィールド、staticフィールド
        // の時には出力を行わない。
        if (object instanceof Field)
        {
            Field field = (Field)object;
            if (field.getType().getName().indexOf(INNER_CLASS_SEPARATOR_CHAR) != -1)
            {
                return false;
            }
            if (Modifier.isTransient(field.getModifiers()))
            {
                return false;
            }
            if (Modifier.isStatic(field.getModifiers()))
            {
                return false;
            }
            return true;
        }

        // Fieldオブジェクトではない場合、プリミティブ型またはプリミティブ型のラッパークラス
        // の時には即出力対象とする。
        Class<?> clazz = object.getClass();

        if (clazz.isPrimitive())
        {
            return true;
        }

        try
        {
            String className = clazz.getSimpleName();
            if (PRINT_CLASS_SET.contains(className))
            {
                return true;
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        return false;
    }

}
