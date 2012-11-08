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
package jp.co.acroquest.endosnipe.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.data.dao.HostInfoDao;
import jp.co.acroquest.endosnipe.data.dao.JavelinLogDao;
import jp.co.acroquest.endosnipe.data.dao.JavelinMeasurementItemDao;
import jp.co.acroquest.endosnipe.data.dao.MeasurementInfoDao;
import jp.co.acroquest.endosnipe.data.dao.MeasurementValueDao;
import jp.co.acroquest.endosnipe.data.dto.MeasurementValueDto;
import jp.co.acroquest.endosnipe.data.entity.HostInfo;
import jp.co.acroquest.endosnipe.data.entity.JavelinLog;
import jp.co.acroquest.endosnipe.data.entity.JavelinMeasurementItem;
import jp.co.acroquest.endosnipe.data.entity.MeasurementInfo;
import jp.co.acroquest.endosnipe.data.entity.MeasurementValue;
import junit.framework.Assert;

/**
 * DataAccessorで利用できるユーティリティメソッドのクラス
 * 
 * @author M.Yoshida
 */
public class DataAccessorTestUtil
{
    protected static final String      DB_NAME                             = "endosnipedb";

    private static Map<String, Method> parseMethodMap;

    private static String[]            JAVELIN_LOG_FIELD_LIST              = {"logId", "sessionId",
            "sequenceId", "javelinLog", "logFileName", "startTime", "endTime", "sessionDesc",
            "logType", "calleeName", "calleeSignature", "calleeClass", "calleeFieldType",
            "calleeObjectId", "callerName", "callerSignature", "callerClass", "callerObjectId",
            "eventLevel", "elapsedTime", "modifier", "threadName", "threadClass", "threadObjectId"};

    private static Set<String>         JAVELIN_LOG_EXCLUDE                 = new HashSet<String>();

    private static final String[]      MEASUREMENT_VALUE_FIELD_LIST        = {"measurementValueId",
            "measurementNum", "measurementTime", "measurementType", "measurementItemId", "value"};

    private static Set<String>         MEASUREMENT_VALUE_EXCLUDE           = new HashSet<String>();

    private static final String[]      JAVELIN_MEASUREMENT_ITEM_FIELD_LIST = {"measurementItemId",
            "measurementType", "itemName", "lastInserted"                  };

    private static Set<String>         JAVELIN_MEASUREMENT_ITEM_EXCLUDE    = new HashSet<String>();

    private static final String[]      MEASUREMENT_INFO_FIELD_LIST         = {"measurementType_",
            "itemName_", "displayName_", "description_"                    };

    private static Set<String>         MEASUREMENT_INFO_EXCLUDE            = new HashSet<String>();

    private static final String[]      MEASUREMENT_VALUE_DTO_FIELD_LIST    = {"measurementValueId",
            "measurementNum", "measurementTime", "measurementType", "measurementItemId", "value",
            "measurementTypeItemName", "measurementTypeDisplayName", "measurementItemName"};

    private static Set<String>         MEASUREMENT_VALUE_DTO_EXCLUDE       = new HashSet<String>();

    static
    {
        parseMethodMap = new HashMap<String, Method>();
        try
        {
            parseMethodMap.put("boolean", Boolean.class.getMethod("parseBoolean", String.class));
            parseMethodMap.put("byte", Byte.class.getMethod("parseByte", String.class));
            parseMethodMap.put("double", Double.class.getMethod("parseDouble", String.class));
            parseMethodMap.put("float", Float.class.getMethod("parseFloat", String.class));
            parseMethodMap.put("int", Integer.class.getMethod("parseInt", String.class));
            parseMethodMap.put("long", Long.class.getMethod("parseLong", String.class));
            parseMethodMap.put("short", Short.class.getMethod("parseShort", String.class));
        }
        catch (SecurityException ex)
        {
        }
        catch (NoSuchMethodException ex)
        {
        }

        JAVELIN_LOG_EXCLUDE.add("logId");
        JAVELIN_LOG_EXCLUDE.add("javelinLog");

        MEASUREMENT_VALUE_EXCLUDE.add("measurementValueId");
        JAVELIN_MEASUREMENT_ITEM_EXCLUDE.add("measurementItemId");
        MEASUREMENT_VALUE_DTO_EXCLUDE.add("measurementValueId");
        MEASUREMENT_VALUE_DTO_EXCLUDE.add("measurementNum");
        MEASUREMENT_VALUE_DTO_EXCLUDE.add("measurementTime");
        MEASUREMENT_VALUE_DTO_EXCLUDE.add("measurementType");
        MEASUREMENT_VALUE_DTO_EXCLUDE.add("measurementItemId");
    }

    public static void initializeHostInfoTable(String[] datarows)
        throws SQLException
    {
        if (datarows == null)
        {
            return;
        }

        for (String row : datarows)
        {
            String[] elements = row.split(",");

            HostInfo info = new HostInfo();
            info.hostName = elements[0].trim();
            info.ipAddress = elements[1].trim();
            info.port = Integer.parseInt(elements[2].trim());
            info.description = elements[3].trim();

            HostInfoDao.insert(DB_NAME, info);
        }
    }

    // -------------------------------------------------------------------------------
    // JavelinLogテーブル対象ユーティリティ
    // -------------------------------------------------------------------------------

    /**
     * JavelinLogテーブルをパラメータのデータに合わせて初期化する。
     * 
     * @param datarows   初期化に使用するデータ
     */
    public static void initializeJavelinLogTable(String[] datarows)
    {
        List<Object> javelinLogEntities = null;
        try
        {
            javelinLogEntities = createJavelinEntities(datarows);
        }
        catch (Exception ex1)
        {
            throw new RuntimeException(ex1);
        }

        if (javelinLogEntities == null)
        {
            return;
        }

        for (Object entity : javelinLogEntities)
        {
            try
            {
                JavelinLogDao.insert(DB_NAME, (JavelinLog)entity);
            }
            catch (SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 二つのJavelinLogエンティティリストが等しい事を確認する。
     * 
     * @param expects 予測値
     * @param actuals 実際値
     */
    public static void assertJavelinLog(List<Object> expects, List<Object> actuals)
    {
        Collections.sort(expects, new Comparator<Object>() {
            public int compare(Object log1, Object log2)
            {
                if (((JavelinLog)log1).logId > ((JavelinLog)log2).logId)
                {
                    return 1;
                }
                else if (((JavelinLog)log1).logId == ((JavelinLog)log2).logId)
                {
                    return 0;
                }

                return -1;
            }
        });

        Collections.sort(actuals, new Comparator<Object>() {
            public int compare(Object log1, Object log2)
            {
                if (((JavelinLog)log1).logId > ((JavelinLog)log2).logId)
                {
                    return 1;
                }
                else if (((JavelinLog)log1).logId == ((JavelinLog)log2).logId)
                {
                    return 0;
                }

                return -1;
            }
        });

        try
        {
            assertEntitiesEquals(expects, actuals, JAVELIN_LOG_EXCLUDE);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * JavelinLogのエンティティリストを生成する。
     * 
     * @param datarows  エンティティリストに設定するCSV
     * @return　エンティティリスト
     */
    public static List<Object> createJavelinEntities(String[] datarows)
    {
        if (datarows == null)
        {
            return null;
        }

        List<Object> javelinLogEntities;

        try
        {
            javelinLogEntities =
                    createEntityList(JavelinLog.class, JAVELIN_LOG_FIELD_LIST, datarows);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return javelinLogEntities;

    }

    /**
     * 指定したファイルのパスからデータを読み込み、JavelinLogデータを生成する。
     * 
     * @param jvnLogFilePath JavelinLogのパス
     * @return データにアクセスするためのストリーム
     */
    public static InputStream convertStreamJavelinFile(String jvnLogFilePath)
    {
        File jvnLogFile = new File(jvnLogFilePath);

        if (jvnLogFile.exists() == false)
        {
            return new ByteArrayInputStream(new byte[0]);
        }

        FileInputStream jvnLogStream = null;

        try
        {
            jvnLogStream = new FileInputStream(jvnLogFile);
        }
        catch (FileNotFoundException ex)
        {
            try
            {
                jvnLogStream.close();
            }
            catch (IOException iex)
            {
            }

            return new ByteArrayInputStream(new byte[0]);
        }

        BufferedInputStream bufStream = new BufferedInputStream(jvnLogStream);
        ByteArrayOutputStream byteOStream = new ByteArrayOutputStream();

        while (true)
        {
            int readData = 0;
            try
            {
                readData = bufStream.read();
            }
            catch (IOException ex)
            {
                try
                {
                    bufStream.close();
                }
                catch (IOException iex)
                {
                }
                try
                {
                    byteOStream.close();
                }
                catch (IOException iex)
                {
                }
                return new ByteArrayInputStream(new byte[0]);
            }

            if (readData == -1)
            {
                break;
            }

            byteOStream.write(readData);
        }

        ByteArrayInputStream retStream = new ByteArrayInputStream(byteOStream.toByteArray());

        try
        {
            bufStream.close();
        }
        catch (IOException ex)
        {
        }

        try
        {
            byteOStream.close();
        }
        catch (IOException ex)
        {
        }

        return retStream;
    }

    // -------------------------------------------------------------------------------
    // MeasurementValue テーブル対象ユーティリティ
    // -------------------------------------------------------------------------------

    /**
     * MeasurementValueテーブルをパラメータのデータに合わせて初期化する。
     * 
     * @param datarows   初期化に使用するデータ
     */
    public static void initializeMeasurementValueTable(String[] datarows)
    {
        List<Object> measurementValueEntities = null;
        try
        {
            measurementValueEntities = createMeasurementValueEntities(datarows);
        }
        catch (Exception ex1)
        {
            throw new RuntimeException(ex1);
        }

        if (measurementValueEntities == null)
        {
            return;
        }

        for (Object entity : measurementValueEntities)
        {
            try
            {
                MeasurementValueDao.insert(DB_NAME, (MeasurementValue)entity);
            }
            catch (SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 二つのMeasurementValueエンティティリストが等しい事を確認する。
     * 
     * @param expects 予測値
     * @param actuals 実際値
     */
    public static void assertMeasurementValue(List<Object> expects, List<Object> actuals)
    {
        try
        {
            assertEntitiesEquals(expects, actuals, MEASUREMENT_VALUE_EXCLUDE);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * MeasurementValueのエンティティリストを生成する。
     * 
     * @param datarows  エンティティリストに設定するCSV
     * @return　エンティティリスト
     */
    public static List<Object> createMeasurementValueEntities(String[] datarows)
    {
        if (datarows == null)
        {
            return null;
        }

        List<Object> measurementValueEntities;

        try
        {
            measurementValueEntities =
                    createEntityList(MeasurementValue.class, MEASUREMENT_VALUE_FIELD_LIST, datarows);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return measurementValueEntities;
    }

    /**
     * MeasurementValueDtoのエンティティをCSV形式のデータから生成する。
     * 
     * @param datarows CSV形式のデータリスト
     * @return 生成したデータインスタンスのリスト
     */
    public static List<Object> createMeasurementValueDtoEntities(String[] datarows)
    {
        if (datarows == null)
        {
            return null;
        }

        List<Object> measurementValueDtoEntities;

        try
        {
            measurementValueDtoEntities =
                    createEntityList(MeasurementValueDto.class, MEASUREMENT_VALUE_DTO_FIELD_LIST,
                                     datarows);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return measurementValueDtoEntities;
    }

    public static void assertMeasurementValueDto(List<Object> expects, List<Object> actuals)
    {
        Collections.sort(expects, new Comparator<Object>() {
            public int compare(Object log1, Object log2)
            {

                MeasurementValueDto obj1 = (MeasurementValueDto)log1;
                MeasurementValueDto obj2 = (MeasurementValueDto)log2;

                if (obj1.measurementItemName.compareTo(obj2.measurementItemName) > 0)
                {
                    return 1;
                }
                else if (obj1.measurementItemName.compareTo(obj2.measurementItemName) < 0)
                {
                    return -1;
                }

                long obj1LongValue = Long.valueOf(obj1.value).longValue();
                long obj2LongValue = Long.valueOf(obj2.value).longValue();
                if (obj1LongValue > obj2LongValue)
                {
                    return 1;
                }
                else if (obj1LongValue < obj2LongValue)
                {
                    return -1;
                }

                return 0;
            }
        });

        Collections.sort(actuals, new Comparator<Object>() {
            public int compare(Object log1, Object log2)
            {

                MeasurementValueDto obj1 = (MeasurementValueDto)log1;
                MeasurementValueDto obj2 = (MeasurementValueDto)log2;

                if (obj1.measurementItemName.compareTo(obj2.measurementItemName) > 0)
                {
                    return 1;
                }
                else if (obj1.measurementItemName.compareTo(obj2.measurementItemName) < 0)
                {
                    return -1;
                }

                long obj1LongValue = Long.valueOf(obj1.value).longValue();
                long obj2LongValue = Long.valueOf(obj2.value).longValue();
                if (obj1LongValue > obj2LongValue)
                {
                    return 1;
                }
                else if (obj1LongValue < obj2LongValue)
                {
                    return -1;
                }

                return 0;
            }
        });

        try
        {
            assertEntitiesEquals(expects, actuals, MEASUREMENT_VALUE_EXCLUDE);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

    }

    // -------------------------------------------------------------------------------
    // JavelinMeasurementItem テーブル対象ユーティリティ
    // -------------------------------------------------------------------------------

    /**
     * JavelinMeasurementItemテーブルをパラメータのデータに合わせて初期化する。
     * 
     * @param datarows   初期化に使用するデータ
     */
    public static void initializeJavelinMeasurementItemTable(String[] datarows)
    {
        List<Object> javelinMeasurementItemEntities = null;
        try
        {
            javelinMeasurementItemEntities = createJavelinMeasurementItemEntities(datarows);
        }
        catch (Exception ex1)
        {
            throw new RuntimeException(ex1);
        }

        if (javelinMeasurementItemEntities == null)
        {
            return;
        }

        for (Object entity : javelinMeasurementItemEntities)
        {
            try
            {
                JavelinMeasurementItemDao.insert(DB_NAME, (JavelinMeasurementItem)entity);
            }
            catch (SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 二つのJavelinMeasurementItemエンティティリストが等しい事を確認する。
     * 
     * @param expects 予測値
     * @param actuals 実際値
     */
    public static void assertJavelinMeasurementItem(List<Object> expects, List<Object> actuals)
    {
        Collections.sort(expects, new Comparator<Object>() {
            public int compare(Object log1, Object log2)
            {
                if (((JavelinMeasurementItem)log1).measurementItemId > ((JavelinMeasurementItem)log2).measurementItemId)
                {
                    return 1;
                }
                else if (((JavelinMeasurementItem)log1).measurementItemId == ((JavelinMeasurementItem)log2).measurementItemId)
                {
                    return 0;
                }

                return -1;
            }
        });

        Collections.sort(actuals, new Comparator<Object>() {
            public int compare(Object log1, Object log2)
            {
                if (((JavelinMeasurementItem)log1).measurementItemId > ((JavelinMeasurementItem)log2).measurementItemId)
                {
                    return 1;
                }
                else if (((JavelinMeasurementItem)log1).measurementItemId == ((JavelinMeasurementItem)log2).measurementItemId)
                {
                    return 0;
                }

                return -1;
            }
        });

        try
        {
            assertEntitiesEquals(expects, actuals, JAVELIN_MEASUREMENT_ITEM_EXCLUDE);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * JavelinMeasurementItemのエンティティリストを生成する。
     * 
     * @param datarows  エンティティリストに設定するCSV
     * @return　エンティティリスト
     */
    public static List<Object> createJavelinMeasurementItemEntities(String[] datarows)
    {
        if (datarows == null)
        {
            return null;
        }

        List<Object> javelinMeasurementItemEntities;

        try
        {
            javelinMeasurementItemEntities =
                    createEntityList(JavelinMeasurementItem.class,
                                     JAVELIN_MEASUREMENT_ITEM_FIELD_LIST, datarows);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return javelinMeasurementItemEntities;
    }

    // -------------------------------------------------------------------------------
    // MeasurementInfo テーブル対象ユーティリティ
    // -------------------------------------------------------------------------------

    /**
     * MeasurementInfoテーブルをパラメータのデータに合わせて初期化する。
     * 
     * @param datarows   初期化に使用するデータ
     */
    public static void initializeMeasurementInfoTable(String[] datarows)
    {
        List<Object> measurementInfoEntities = null;
        try
        {
            measurementInfoEntities = createMeasurementInfoEntities(datarows);
        }
        catch (Exception ex1)
        {
            throw new RuntimeException(ex1);
        }

        if (measurementInfoEntities == null)
        {
            return;
        }

        for (Object entity : measurementInfoEntities)
        {
            try
            {
                MeasurementInfoDao.insert(DB_NAME, (MeasurementInfo)entity);
            }
            catch (SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 二つのMeasurementInfoエンティティリストが等しい事を確認する。
     * 
     * @param expects 予測値
     * @param actuals 実際値
     */
    public static void assertMeasurementInfo(List<Object> expects, List<Object> actuals)
    {
        Collections.sort(expects, new Comparator<Object>() {
            public int compare(Object log1, Object log2)
            {
                if (((MeasurementInfo)log1).getMeasurementType() > ((MeasurementInfo)log2).getMeasurementType())
                {
                    return 1;
                }
                else if (((MeasurementInfo)log1).getMeasurementType() == ((MeasurementInfo)log2).getMeasurementType())
                {
                    return 0;
                }

                return -1;
            }
        });

        Collections.sort(actuals, new Comparator<Object>() {
            public int compare(Object log1, Object log2)
            {
                if (((MeasurementInfo)log1).getMeasurementType() > ((MeasurementInfo)log2).getMeasurementType())
                {
                    return 1;
                }
                else if (((MeasurementInfo)log1).getMeasurementType() == ((MeasurementInfo)log2).getMeasurementType())
                {
                    return 0;
                }

                return -1;
            }
        });

        try
        {
            assertEntitiesEquals(expects, actuals, MEASUREMENT_INFO_EXCLUDE);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * MeasurementInfoのエンティティリストを生成する。
     * 
     * @param datarows  エンティティリストに設定するCSV
     * @return　エンティティリスト
     */
    public static List<Object> createMeasurementInfoEntities(String[] datarows)
    {
        if (datarows == null)
        {
            return null;
        }

        List<Object> measurementInfoEntities = new ArrayList<Object>();

        try
        {
            for (String datarow : datarows)
            {
                String[] dataElements = datarow.split(",");

                Field mType = MeasurementInfo.class.getDeclaredField("measurementType_");
                Field iName = MeasurementInfo.class.getDeclaredField("itemName_");
                Field dName = MeasurementInfo.class.getDeclaredField("displayName_");
                Field descr = MeasurementInfo.class.getDeclaredField("description_");
                mType.setAccessible(true);
                iName.setAccessible(true);
                dName.setAccessible(true);
                descr.setAccessible(true);

                Object mTypeVal = parseString(mType, dataElements[0]);
                Object iNameVal = parseString(iName, dataElements[1]);
                Object dNameVal = parseString(dName, dataElements[2]);
                Object descrVal = parseString(descr, dataElements[3]);

                Object infoEntity =
                        MeasurementInfo.class.getConstructors()[0].newInstance(mTypeVal, iNameVal,
                                                                               dNameVal, descrVal);
                measurementInfoEntities.add(infoEntity);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return measurementInfoEntities;
    }

    // -------------------------------------------------------------------------------
    // 共通ユーティリティ
    // -------------------------------------------------------------------------------

    /**
     * 各エンティティのリストに格納された値がそれぞれ等しいか否かを判定する。
     * 
     * @param expects  予測値
     * @param actuals  実際値
     * @param exclude  チェックから除外するフィールドの名前
     */
    public static void assertEntitiesEquals(List<Object> expects, List<Object> actuals,
            Set<String> exclude)
        throws Exception
    {
        if (expects.size() != actuals.size())
        {
            Assert.fail("actual datasize different expect datasize !! : expect<" +
                        expects.size() + "> but actual<" + actuals.size() + ">");
        }

        for (int cnt = 0; cnt < expects.size(); cnt++)
        {
            assertEntityEquals(expects.get(cnt), actuals.get(cnt), exclude);
        }
    }

    /**
     * 各エンティティ同士のフィールドが等しいか否かを判定する。
     * 
     * @param expect  予測値
     * @param actual  実際値
     * @param exclude チェックを行わないフィールドのセット
     */
    public static void assertEntityEquals(Object expect, Object actual, Set<String> exclude)
        throws Exception
    {
        if (expect == null ||
            actual == null)
        {
            Assert.fail("expect or actual is NULL !!");
        }

        Class<?> chkTargetClass = expect.getClass();
        Field[] chkFields = chkTargetClass.getDeclaredFields();

        Assert.assertEquals(chkTargetClass, actual.getClass());

        for (Field chkField : chkFields)
        {
            if (exclude.contains(chkField.getName()))
            {
                continue;
            }

            if (chkField.isAccessible() == false)
            {
                chkField.setAccessible(true);
            }

            Object expectFieldValue = chkField.get(expect);
            Object actualFieldValue = chkField.get(actual);

            if (Number.class.getName().equals(chkField.getType().getName()))
            {
                expectFieldValue = new BigDecimal(expectFieldValue.toString());
                actualFieldValue = new BigDecimal(actualFieldValue.toString());

                if (((BigDecimal)expectFieldValue).scale() > ((BigDecimal)actualFieldValue).scale())
                {
                    actualFieldValue =
                            ((BigDecimal)actualFieldValue).setScale(((BigDecimal)expectFieldValue).scale());
                }
                else
                {
                    expectFieldValue =
                            ((BigDecimal)expectFieldValue).setScale(((BigDecimal)actualFieldValue).scale());
                }
            }

            if (expectFieldValue == null &&
                actualFieldValue == null)
            {
                continue;
            }

            if (expectFieldValue == null)
            {
                Assert.assertNull(actualFieldValue);
            }
            else
            {
                Assert.assertNotNull(actualFieldValue);
            }

            Assert.assertEquals(expectFieldValue, actualFieldValue);
        }
    }

    /**
     * CSV形式に定義されたデータを、指定したクラスのインスタンスに変換する。
     * ただし、static final宣言された変数を指定することは出来ない。
     * 
     * @param clazz     対象となるクラス
     * @param fieldList 設定対象とするフィールド
     * @param datarows  設定するデータ(CSV形式)のリスト
     * @return　生成したクラスインスタンスのリスト
     * @throws Exception 変換中に例外が発生した場合
     */
    public static List<Object> createEntityList(Class<?> clazz, String[] fieldList,
            String[] datarows)
        throws Exception
    {
        if (clazz == null ||
            fieldList == null || fieldList.length < 1)
        {
            return null;
        }

        List<Object> entityList = new ArrayList<Object>();

        for (String datarow : datarows)
        {
            Object settingEntity = createEntity(clazz, fieldList, datarow);
            entityList.add(settingEntity);
        }

        return entityList;
    }

    /**
     * CSV形式に定義されたデータを、指定したクラスのインスタンスに変換する。
     * ただし、static final宣言された変数を指定することは出来ない。
     * 
     * @param clazz     対象となるクラス
     * @param fieldList 設定対象とするフィールド
     * @param datarow   設定するデータ(CSV形式)
     * @return　生成したクラスインスタンス
     * @throws Exception 変換中に例外が発生した場合
     */
    public static Object createEntity(Class<?> clazz, String[] fieldList, String datarow)
        throws Exception
    {
        Object entityObj = clazz.newInstance();
        String[] dataElements = datarow.split(",");

        for (int fieldCnt = 0; fieldCnt < fieldList.length; fieldCnt++)
        {
            Field settingField = clazz.getField(fieldList[fieldCnt]);
            String settingData = null;

            if (fieldCnt < dataElements.length)
            {
                settingData = dataElements[fieldCnt].trim();
            }

            Object settingVal = parseString(settingField, settingData);

            if (settingVal != null)
            {
                if (settingField.isAccessible() == false)
                {
                    settingField.setAccessible(true);
                }

                settingField.set(entityObj, settingVal);
            }
        }

        return entityObj;
    }

    /**
     * 文字列表現で表されたデータを、指定したフィールドに設定可能なオブジェクトに変換する。
     * 
     * @param field 値の設定対象となるフィールド
     * @param data  対象フィールドに設定するデータの文字列表現
     * @return      型変換された設定値
     */
    public static Object parseString(Field field, String data)
        throws Exception
    {
        String fieldTypeName = field.getType().getName();

        if (data == null)
        {
            if (InputStream.class.getName().equals(fieldTypeName))
            {
                return new ByteArrayInputStream(new byte[0]);
            }

            return null;
        }

        if (String.class.getName().equals(fieldTypeName))
        {
            return data;
        }

        if ("".equals(data))
        {
            return null;
        }

        Method parseMethod = parseMethodMap.get(fieldTypeName);

        if (parseMethod != null)
        {
            return parseMethod.invoke(null, data);
        }

        if (Timestamp.class.getName().equals(fieldTypeName))
        {
            DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return new Timestamp(format.parse(data).getTime());
        }

        if (Date.class.getName().equals(fieldTypeName))
        {
            DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return format.parse(data);
        }

        if (InputStream.class.getName().equals(fieldTypeName))
        {
            return convertStreamJavelinFile(data);
        }

        if (Number.class.getName().equals(fieldTypeName))
        {
            Method parseNumberMethod;

            if (data.indexOf(".") == -1)
            {
                parseNumberMethod = parseMethodMap.get("long");
            }
            else
            {
                parseNumberMethod = parseMethodMap.get("double");
            }

            return parseNumberMethod.invoke(null, data);
        }

        return null;
    }

}
