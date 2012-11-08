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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.bean.Component;
import jp.co.acroquest.endosnipe.javelin.util.HashMap;

/**
 * シリアライズ処理を行うクラス。<br />
 *
 * @author acroquest
 */
public class MBeanManagerSerializer
{

    /**
     * コンストラクタを隠蔽します。<br />
     */
    private MBeanManagerSerializer()
    {
        // Do nothing.
    }

    /**
     * デシリアライズを行います。<br />
     *
     * @return デシアライズしたコンポーネントのマップ
     */
    public static Map<String, Component> deserialize()
    {
        JavelinConfig config = new JavelinConfig();
        if (!config.isSetSerializeFile())
        {
            return new HashMap<String, Component>();
        }

        String serializeFile = config.getSerializeFile();
        File file = new File(serializeFile);

        // ファイルが存在しない場合はデシリアライズをスキップする。
        if (!file.exists())
        {
            return new HashMap<String, Component>();
        }

        return deserializeFile(serializeFile);
    }

    /**
     * デシリアライズを行います。<br />
     *
     * @return デシアライズしたコンポーネントのマップ
     */
    public static Map<String, Component> deserializeFile(String serializeFile)
    {
        ObjectInputStream inObject = null;
        try
        {
            FileInputStream inFile = new FileInputStream(serializeFile);
            inObject = new ObjectInputStream(inFile);
            Map<String, Component> map = castToMap(inObject.readObject());
            return map;
        }
        catch (Exception e)
        {
            SystemLogger.getInstance().warn("Failed to deserializing MBeanManager. "
                                            + "Start without it. Deserializing source:" + serializeFile, e);
        }
        finally
        {
            if (inObject != null)
            {
                try
                {
                    inObject.close();
                }
                catch (IOException ioe)
                {
                    SystemLogger.getInstance().warn(ioe);
                }
            }
        }

        return new HashMap<String, Component>();
    }

    /**
     * シリアライズを行います。<br />
     *
     * @param map シリアライズを行うコンポーネントのマップ
     */
    public static void serialize(final Map<String, Component> map)
    {
        JavelinConfig config = new JavelinConfig();
        if (config.isSetSerializeFile())
        {
            String serializeFile = config.getSerializeFile();

            try
            {
                FileOutputStream outFile = new FileOutputStream(serializeFile);
                ObjectOutputStream outObject = new ObjectOutputStream(outFile);
                outObject.writeObject(map);
                outObject.close();
                outFile.close();
            }
            catch (Exception e)
            {
                SystemLogger.getInstance().warn("Failed to serializing MBeanManager. Serializing dest.:"
                                                + serializeFile,e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Component> castToMap(final Object object)
    {
        return (Map<String, Component>)object;
    }
}
