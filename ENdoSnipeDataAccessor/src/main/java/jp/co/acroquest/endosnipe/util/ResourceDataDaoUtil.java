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
package jp.co.acroquest.endosnipe.util;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import jp.co.acroquest.endosnipe.common.entity.MeasurementData;
import jp.co.acroquest.endosnipe.common.entity.MeasurementDetail;
import jp.co.acroquest.endosnipe.common.entity.ResourceData;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.data.ENdoSnipeDataAccessorPluginProvider;
import jp.co.acroquest.endosnipe.data.LogMessageCodes;
import jp.co.acroquest.endosnipe.data.dao.JavelinMeasurementItemDao;
import jp.co.acroquest.endosnipe.data.dao.MeasurementValueDao;
import jp.co.acroquest.endosnipe.data.db.DBManager;
import jp.co.acroquest.endosnipe.data.entity.JavelinMeasurementItem;
import jp.co.acroquest.endosnipe.data.entity.MeasurementValue;

/**
 * データベースの計測値情報を {@link ResourceData} でやりとりするためのユーティリティクラス。
 *
 * @author sakamoto
 */
public class ResourceDataDaoUtil
{
    /** ロガー */
    private static final ENdoSnipeLogger logger__ =
        ENdoSnipeLogger.getLogger(ResourceDataDaoUtil.class,
                                  ENdoSnipeDataAccessorPluginProvider.INSTANCE);

    /** データベース名をキーとする、前回データを挿入したテーブルインデックスを保持するマップ */
    private static Map<String, Integer> prevTableIndexMap__ =
            new ConcurrentHashMap<String, Integer>();

    /** データベース名をキーとする、系列番号とその最終挿入時刻のマップを保持するマップ */
    private static Map<String, Map<Integer, Timestamp>> measurementItemUpdatedMap__ =
            new ConcurrentHashMap<String, Map<Integer, Timestamp>>();

    /** データベース名をキーとする、measurement_typeとitem_nameを":"で区切って連結した文字列をキーとする、item_idのマップを保持するマップ*/
    private static Map<String, Map<String, Integer>> measurementItemIdMap__ =
            new ConcurrentHashMap<String, Map<String, Integer>>();
    
    /** １週間の日数 */
    public static final int DAY_OF_WEEK = 7;

    /** １年間の最大日数 */
    private static final int DAY_OF_YEAR_MAX = 366;

    /** パーティショニングされたテーブルの数（最後のテーブルは7日以上のデータが入る） */
    public static final int PARTITION_TABLE_COUNT = DAY_OF_YEAR_MAX / DAY_OF_WEEK;

    /** JAVELIN_MEASUREMENT_ITEMテーブルのLAST_INSERTEDフィールドを更新する間隔（ミリ秒） */
    private static final int ITEM_UPDATE_INTERVAL = 24 * 60 * 60 * 1000;

    /** バッチのサイズ */
    private static final int DEF_BATCH_SIZE = 100;

    /** itemIdのキャッシュサイズ */
    private static final int DEF_ITEMID_CACHE_SIZE = 50000;

    /** MEASUREMENT_VALUE テーブルを truncate するコールバックメソッド */
    private static final RotateCallback measurementRotateCallback__ = new RotateCallback() {
        /**
         * {@inheritDoc}
         */
        public String getTableType()
        {
            return "MEASUREMENT_VALUE";
        }

        /**
         * {@inheritDoc}
         */
        public void truncate(final String database, final int tableIndex, final int year)
            throws SQLException
        {
            MeasurementValueDao.truncate(database, tableIndex, year);
        }
    };

    /**
     * デフォルトコンストラクタを隠蔽します。
     */
    private ResourceDataDaoUtil()
    {
        // Do nothing.
    }

    /**
     * データを挿入するテーブルの名前を返します。
     *
     * @param date 挿入するデータの日付
     * @param tableNameBase テーブル名のベース
     * @return テーブル名
     */
    public static String getTableNameToInsert(final Date date, final String tableNameBase)
    {
        String tableName = null;
        if (DBManager.isDefaultDb())
        {
            tableName = tableNameBase;
        }
        else
        {
            // H2以外のデータベースの場合は、パーティショニング処理を行う
            int weekOfYear = getTableIndexToInsert(date);
            tableName = getTableName(tableNameBase, weekOfYear);
        }
        return tableName;
    }

    /**
     * 実テーブルの名前を返します。
     *
     * @param tableNameBase テーブル名のベース
     * @param index テーブルインデックス
     * @return 実テーブルの名前
     */
    public static String getTableName(final String tableNameBase, final int index)
    {
        String tableName = String.format("%s_%02d", tableNameBase, Integer.valueOf(index));
        return tableName;
    }

    /**
     * データを挿入するテーブルのインデックスを返します。
     *
     * @param date 挿入するデータの日付（ <code>null</code> の場合は現在の日付）
     * @return テーブルインデックス
     */
    public static int getTableIndexToInsert(final Date date)
    {
        Calendar calendar = Calendar.getInstance();
        if (date != null)
        {
            calendar.setTime(date);
        }
        int index = (calendar.get(Calendar.DAY_OF_YEAR) - 1) / DAY_OF_WEEK + 1;
        if (index > PARTITION_TABLE_COUNT)
        {
            index = PARTITION_TABLE_COUNT;
        }
        return index;
    }

    /**
     * {@link ResourceData} をデータベースに登録します。<br />
     *
     * {@link ResourceData} が保持するホスト情報が計測対象ホスト情報テーブルに存在しない場合は、
     * データベースに登録せず、エラーログを出力します。<br />
     *
     *　該当する計測値の項目（系列）が Javelin 計測項目テーブルに存在しない場合は、
     * 該当するレコードを Javelin 計測項目テーブルに挿入します。<br />
     *
     *　計測値種別が計測値情報テーブルに存在しない場合は、
     * 該当するレコードを計測値情報テーブルに挿入します。<br />
     *
     * 挿入対象のテーブルが前回挿入時から変わった場合、ローテート処理を行います。
     *
     * @param database データベース名
     * @param resourceData 登録するデータ
     * @param rotatePeriod ローテート期間
     * @param rotatePeriodUnit ローテート期間の単位（ Calendar クラスの DAY または MONTH の値）
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void insert(final String database, final ResourceData resourceData,
            final int rotatePeriod, final int rotatePeriodUnit)
        throws SQLException
    {
        insert(database, resourceData, rotatePeriod, rotatePeriodUnit, DEF_BATCH_SIZE,
               DEF_ITEMID_CACHE_SIZE);
    }

    /**
     * {@link ResourceData} をデータベースに登録します。<br />
     *
     * {@link ResourceData} が保持するホスト情報が計測対象ホスト情報テーブルに存在しない場合は、
     * データベースに登録せず、エラーログを出力します。<br />
     *
     *　該当する計測値の項目（系列）が Javelin 計測項目テーブルに存在しない場合は、
     * 該当するレコードを Javelin 計測項目テーブルに挿入します。<br />
     *
     *　計測値種別が計測値情報テーブルに存在しない場合は、
     * 該当するレコードを計測値情報テーブルに挿入します。<br />
     *
     * 挿入対象のテーブルが前回挿入時から変わった場合、ローテート処理を行います。
     *
     * @param database データベース名
     * @param resourceData 登録するデータ
     * @param rotatePeriod ローテート期間
     * @param rotatePeriodUnit ローテート期間の単位（ Calendar クラスの DAY または MONTH の値）
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void insert(final String database, final ResourceData resourceData,
            final int rotatePeriod, final int rotatePeriodUnit, final int insertUnit)
        throws SQLException
    {
        insert(database, resourceData, rotatePeriod, rotatePeriodUnit, insertUnit,
               DEF_ITEMID_CACHE_SIZE);
    }        
    
    /**
     * {@link ResourceData} をデータベースに登録します。<br />
     *
     * {@link ResourceData} が保持するホスト情報が計測対象ホスト情報テーブルに存在しない場合は、
     * データベースに登録せず、エラーログを出力します。<br />
     *
     *　該当する計測値の項目（系列）が Javelin 計測項目テーブルに存在しない場合は、
     * 該当するレコードを Javelin 計測項目テーブルに挿入します。<br />
     *
     *　計測値種別が計測値情報テーブルに存在しない場合は、
     * 該当するレコードを計測値情報テーブルに挿入します。<br />
     *
     * 挿入対象のテーブルが前回挿入時から変わった場合、ローテート処理を行います。
     *
     * @param database データベース名
     * @param resourceData 登録するデータ
     * @param rotatePeriod ローテート期間
     * @param rotatePeriodUnit ローテート期間の単位（ Calendar クラスの DAY または MONTH の値）
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static InsertResult insert(final String database, final ResourceData resourceData,
        final int rotatePeriod, final int rotatePeriodUnit, final int batchUnit,
        final int itemIdCacheSize)
        throws SQLException
    {
        InsertResult result = new InsertResult();
        MeasurementValue baseMeasurementValue = new MeasurementValue();
        baseMeasurementValue.measurementTime = new Timestamp(resourceData.measurementTime);

        if (DBManager.isDefaultDb() == false)
        {
            // H2以外のデータベースの場合は、パーティショニング処理を行う
            Integer tableIndex =
                    getTableIndexToInsert(baseMeasurementValue.measurementTime);
            Integer prevTableIndex = prevTableIndexMap__.get(database);
            if (tableIndex.equals(prevTableIndex) == false)
            {
                Timestamp[] range = MeasurementValueDao.getTerm(database);
                if (range.length == 2
                        && (range[1] == null || range[1].before(baseMeasurementValue.measurementTime)))
                {
                    // 前回の挿入データと今回の挿入データで挿入先テーブルが異なる場合に、ローテート処理を行う
                    // ただし、すでにDBに入っているデータのうち、最新のデータよりも古いデータが入ってきた場合はローテート処理しない
                    boolean truncateCurrent = (prevTableIndex != null);
                    rotateTable(database, tableIndex, baseMeasurementValue.measurementTime,
                                rotatePeriod, rotatePeriodUnit, truncateCurrent,
                                measurementRotateCallback__);
                    prevTableIndexMap__.put(database, tableIndex);
                }
                deleteOldMeasurementItems(database, baseMeasurementValue.measurementTime, rotatePeriod,
                                          rotatePeriodUnit, batchUnit);
            }
        }
        Map<String, Integer> itemMap = measurementItemIdMap__.get(database);
        if (itemMap == null)
        {
            itemMap = loadMeasurementItemIdMap(database, itemIdCacheSize);
            measurementItemIdMap__.put(database, itemMap);
        }
        

        String prevTableName = null;
        
        Map<String, Timestamp> updateTargetMap = new HashMap<String, Timestamp>();
        Map<Integer, Timestamp> updatedMap = getUpdatedMap(database);
        List<MeasurementValue> updateValueList = new ArrayList<MeasurementValue>();
        for (MeasurementData measurementData : resourceData.getMeasurementMap().values())
        {
            for (MeasurementDetail detail : measurementData.getMeasurementDetailMap().values())
            {
                String measurementItemName;
                if (detail.itemName != null && detail.itemName.length() > 0)
                {
                    if (measurementData.itemName != null && measurementData.itemName.length() > 0)
                    {
                        measurementItemName = measurementData.itemName + "/" + detail.itemName;
                    }
                    else
                    {
                        measurementItemName = detail.itemName;
                    }
                }
                else
                {
                    measurementItemName = measurementData.itemName;
                }
                
                if (measurementItemName.startsWith("/") == false)
                {
                    measurementItemName = "/" + measurementItemName;
                }
                
                MeasurementValue measurementValue = new MeasurementValue();
                measurementValue.measurementTime = baseMeasurementValue.measurementTime;
                measurementValue.value  = baseMeasurementValue.value;
                
                int itemId;
                String itemMapKey = measurementItemName;
                itemId = getItemId(database, itemMap, detail, itemMapKey);
                if (itemId != -1)
                {
                    measurementValue.measurementItemId = itemId;
                }
                else
                {
                    result.setCacheMissCount(result.getCacheMissCount() + 1);
                    
                    // 系列が Javelin 計測項目テーブルに登録されていない場合は追加する
                    measurementValue.measurementItemId =
                            insertJavelinMeasurementItem(database,
                                                         measurementItemName,
                                                         measurementValue.measurementTime);
                }

                int overflowCount =
                    addItemIdToCache(itemMap, itemMapKey, measurementValue, itemIdCacheSize);
                result.setCacheOverflowCount(result.getCacheOverflowCount() + overflowCount);

                measurementValue.value = detail.value;

                String tableName =
                    MeasurementValueDao.getTableNameToInsert(measurementValue.measurementTime);
                if ((prevTableName != null && prevTableName.endsWith(tableName) == false)
                    || updateValueList.size() > batchUnit)
                {
                    int insertCount =
                        MeasurementValueDao.insertBatch(database, prevTableName, updateValueList);
                    result.setInsertCount(result.getInsertCount() + insertCount);
                    updateValueList = new ArrayList<MeasurementValue>();
                }

                updateValueList.add(measurementValue);

                // 前回JAVELIN_MEASUREMENT_ITEMテーブルのLAST_INSERTEDフィールド更新から
                // 一定期間が経過した場合に、LAST_INSERTEDフィールドを更新する対象に含める。
                Timestamp beforeTime = updatedMap.get(measurementValue.measurementItemId);
                updatedMap.put(measurementValue.measurementItemId, measurementValue.measurementTime);
                if (beforeTime == null
                    || measurementValue.measurementTime.getTime() > beforeTime.getTime()
                        + ITEM_UPDATE_INTERVAL)
                {
                    updateTargetMap.put(measurementItemName, measurementValue.measurementTime);
                }

                prevTableName = tableName;
             }
        }
        
        if (prevTableName != null && updateValueList.size() > 0)
        {
            int insertCount =
                MeasurementValueDao.insertBatch(database, prevTableName, updateValueList);
            result.setInsertCount(result.getInsertCount()
                                  + insertCount);
        }

        // LAST_INSERTEDフィールドを更新する必要がある系列に対して更新を実施する。
        if (updateTargetMap.size() > 0)
        {
            JavelinMeasurementItemDao.updateLastInserted(database, updateTargetMap);
        }
        
        return result;
    }

    private static LinkedHashMap<String, Integer> loadMeasurementItemIdMap(String database,
        int itemIdCacheSize)
        throws SQLException
    {
        LinkedHashMap<String, Integer> itemIdMap = new LinkedHashMap<String, Integer>();
        List<JavelinMeasurementItem> itemList =
            JavelinMeasurementItemDao.selectAll(database, itemIdCacheSize);
        for (JavelinMeasurementItem item : itemList)
        {
            itemIdMap.put(item.itemName, item.measurementItemId);
        }
        
        return itemIdMap;
    }

    private static int getItemId(final String database, Map<String, Integer> itemMap,
        MeasurementDetail detail, String itemMapKey)
        throws SQLException
    {
        synchronized (itemMap)
        {
            int itemId;
            if (itemMap.containsKey(itemMapKey))
            {
                itemId = itemMap.get(itemMapKey);
            }
            else
            {
                String displayName = detail.displayName;
                itemId =
                    JavelinMeasurementItemDao.selectMeasurementItemIdFromItemName(database,
                                                                                  displayName);
            }
            return itemId;
        }
    }

    private static int addItemIdToCache(Map<String, Integer> itemMap, String itemMapKey,
        MeasurementValue measurementValue, final int itemIdCacheSize)
    {
        int overflowCount = 0;
        synchronized (itemMap)
        {
            itemMap.put(itemMapKey, measurementValue.measurementItemId);
            if (itemMap.size() > itemIdCacheSize)
            {
                overflowCount = 1;
                
                // 最初の要素を削除する。
                Iterator<Entry<String, Integer>> iterator = itemMap.entrySet().iterator();
                if (iterator.hasNext())
                {
                    iterator.next();
                    iterator.remove();
                }
            }
        }
        return overflowCount;
    }
    
    /**
     * ローテートを実施します。
     *
     * @param database データベース名
     * @param tableIndex データが挿入されたテーブルインデックス
     * @param date 挿入するデータの時刻
     * @param rotatePeriod ローテート期間
     * @param rotatePeriodUnit ローテート期間の単位（Calendar.DATE or Calendar.MONTH）
     * @param truncateCurrent 現在のデータの挿入対象を truncate する場合は <code>true</code>
     * @param rotateCallback truncate 処理を行うコールバックメソッド
     * @throws SQLException truncate 実行時に例外が発生した場合
     */
    public static void rotateTable(final String database, final Integer tableIndex,
            final Timestamp date, final int rotatePeriod, final int rotatePeriodUnit,
            final boolean truncateCurrent, final RotateCallback rotateCallback)
        throws SQLException
    {
        Calendar calendarToInsert = Calendar.getInstance();
        calendarToInsert.setTime(date);
        int yearToInsert = calendarToInsert.get(Calendar.YEAR);

        if (truncateCurrent)
        {
            rotateCallback.truncate(database, tableIndex, yearToInsert);
        }

        Calendar deleteTime = getBeforeDate(date, rotatePeriod, rotatePeriodUnit);
        // remainStartIndex以降（この値を含む）のインデックスのテーブルはtruncateしない
        int remainStartIndex = getTableIndexToInsert(deleteTime.getTime());
        if (remainStartIndex <= tableIndex)
        {
            // テーブルインデックスが等しい場合、
            // ローテート期間が1週間未満の場合と約1年の場合の2パターンある。
            // それぞれによって処理が異なる。
            // ローテート期間が1週間未満の場合は、挿入テーブル以外のテーブルをtruncateしてよいが、
            // ローテート期間が約1年の場合は、他のテーブルをtruncateしてはいけない。
            if (remainStartIndex < tableIndex
                    || isShorterThanOneWeek(rotatePeriod, rotatePeriodUnit))
            {
                // remainStartIndexより前、もしくはtableIndexより後のテーブルをtruncateする
                for (int index = 1; index < remainStartIndex; index++)
                {
                    rotateCallback.truncate(database, index, yearToInsert + 1);
                }
                for (int index = tableIndex + 1; index <= PARTITION_TABLE_COUNT; index++)
                {
                    rotateCallback.truncate(database, index, yearToInsert);
                }
            }
        }
        else
        {
            for (int index = tableIndex + 1; index <= remainStartIndex - 1; index++)
            {
                rotateCallback.truncate(database, index, yearToInsert);
            }
        }

        if (logger__.isInfoEnabled())
        {
            logger__.log(LogMessageCodes.ROTATE_TABLE_PERFORMED, database,
                         rotateCallback.getTableType());
        }
    }

    /**
     * ローテート期間が1週間未満かどうかを判定します。
     *
     * @param rotatePeriod ローテート期間
     * @param rotatePeriodUnit ローテート期間の単位
     * @return 1週間（7日）未満の場合は <code>true</code> 、1週間以上の場合は <code>false</code>
     */
    private static boolean isShorterThanOneWeek(final int rotatePeriod, final int rotatePeriodUnit)
    {
        boolean shorter = false;
        if (rotatePeriodUnit == Calendar.MONTH)
        {
            shorter = (rotatePeriod == 0);
        }
        else if (rotatePeriodUnit == Calendar.DATE)
        {
            shorter = (rotatePeriod < DAY_OF_WEEK);
        }
        return shorter;
    }

    /**
     * 古い系列情報（ITEM）を削除します。
     *
     * @param database データベース名
     * @param date 挿入するデータの時刻
     * @param rotatePeriod ローテート期間
     * @param rotatePeriodUnit ローテート期間の単位（Calendar.DATE or Calendar.MONTH）
     */
    private static void deleteOldMeasurementItems(final String database, final Timestamp date,
        final int rotatePeriod, final int rotatePeriodUnit, final int insertUnit)
    {
        List<Integer> deleteIdList = new ArrayList<Integer>();
        
        long deleteTime = getBeforeDate(date, rotatePeriod, rotatePeriodUnit).getTimeInMillis();
        Map<Integer, Timestamp> updatedMap = getUpdatedMap(database);
        Iterator<Map.Entry<Integer, Timestamp>> iterator = updatedMap.entrySet().iterator();
        int removedItems = 0;
        while (iterator.hasNext())
        {
            Map.Entry<Integer, Timestamp> entry = iterator.next();
            if (entry.getValue().getTime() < deleteTime)
            {
                // 最終更新時刻がローテート期間より前の場合は、
                // そのMEASUREMENT_ITEMを削除する
                deleteIdList.add(entry.getKey());

                if (deleteIdList.size() >= insertUnit)
                {
                    try
                    {
                        JavelinMeasurementItemDao.deleteByMeasurementItemId(database, deleteIdList);
                        removedItems += deleteIdList.size();
                        deleteIdList.clear();
                    }
                    catch (SQLException ex)
                    {
                        // 削除に失敗した場合はまだITEMが使用されているため、
                        // Mapからも削除しない
                    }
                }
            }
        }
        if (deleteIdList.size() > 0)
        {
            try
            {
                JavelinMeasurementItemDao.deleteByMeasurementItemId(database, deleteIdList);
                removedItems += deleteIdList.size();
                deleteIdList.clear();
            }
            catch (SQLException ex)
            {
                // 削除に失敗した場合はまだITEMが使用されているため、
                // Mapからも削除しない
            }
        }

        
        if (logger__.isInfoEnabled())
        {
            logger__.log(LogMessageCodes.NO_NEEDED_SERIES_REMOVED, database,
                         Integer.valueOf(removedItems));
        }
    }

    /**
     * 指定された時刻から、指定した時間だけ前の時刻を返します。
     *
     * @param baseTime 時刻
     * @param period 期間（正の値）
     * @param unit 単位（Calendarクラスのインデックス）
     * @return Calendarインスタンス
     */
    private static Calendar getBeforeDate(final Timestamp baseTime,
                                          final int period,
                                          final int unit)
    {
        Calendar deleteTimeCalendar = Calendar.getInstance();
        deleteTimeCalendar.setTime(baseTime);
        deleteTimeCalendar.add(unit, -1 * period);
        return deleteTimeCalendar;
    }

    /**
     * Javelin 計測項目テーブルにレコードを追加します。<br />
     *
     * @param database データベース名
     * @param measurementType 計測値種別
     * @param itemName 項目名称
     * @param lastInserted 計測データ最終挿入時刻
     * @return 挿入したレコードの計測項目 ID
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    private static int insertJavelinMeasurementItem(final String database,
                                                    final String itemName,
                                                    final Timestamp lastInserted)
        throws SQLException
    {
        JavelinMeasurementItem item = new JavelinMeasurementItem();
        item.itemName = itemName;
        item.lastInserted = lastInserted;
        JavelinMeasurementItemDao.insert(database, item);

        int measurementItemId =
                JavelinMeasurementItemDao.selectMeasurementItemIdFromItemName(database, itemName);
        return measurementItemId;
    }

    /**
     * 指定されたデータベースに対応する、系列毎の最終更新時刻のマップを返します。<br />
     *
     * もしマップが存在しない場合は新たに作成します。
     * このとき、データベースに存在する系列毎の最終更新時刻情報を取得します。
     *
     * @param database データベース名
     * @return 系列毎の最終更新時刻のマップ
     */
    private static Map<Integer, Timestamp> getUpdatedMap(String database)
    {
        Map<Integer, Timestamp> updatedMap = measurementItemUpdatedMap__.get(database);
        if (updatedMap == null)
        {
            updatedMap = new ConcurrentHashMap<Integer, Timestamp>();
            measurementItemUpdatedMap__.put(database, updatedMap);

            // データベースに存在する系列毎の最終更新時刻情報を取得する
            try
            {
                List<JavelinMeasurementItem> itemList =
                    JavelinMeasurementItemDao.selectAll(database);
                for (JavelinMeasurementItem item : itemList)
                {
                    updatedMap.put(item.measurementItemId, item.lastInserted);
                }
            }
            catch (SQLException ex)
            {
                logger__.log(LogMessageCodes.DB_ACCESS_ERROR, database, ex.getMessage());
            }
        }
        return updatedMap;
    }
}
