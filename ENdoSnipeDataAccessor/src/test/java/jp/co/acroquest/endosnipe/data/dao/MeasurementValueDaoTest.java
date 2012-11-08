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
package jp.co.acroquest.endosnipe.data.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import jp.co.acroquest.endosnipe.data.entity.JavelinMeasurementItem;
import jp.co.acroquest.endosnipe.data.entity.MeasurementValue;

/**
 * {@link MeasurementValueDao} クラスのテストケース。<br />
 *
 * @author y-sakamoto
 */
public class MeasurementValueDaoTest extends AbstractDaoTest
{
    /** スリープ時間 */
    private static final long SLEEP_TIME = 100;

    /** Javelin 計測項目テーブルに登録されているレコードのリスト */
    private List<JavelinMeasurementItem> javelinMeasurementItemList_;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        initJavelinMeasurementItemTable();
    }

    /**
     * {@link MeasurementValue} の値を比較します。<br />
     *
     * @param expected 期待する値
     * @param actual 実際の値
     */
    private static void assertEquals(final MeasurementValue expected, final MeasurementValue actual)
    {
        assertEquals(expected.measurementTime, actual.measurementTime);
        assertEquals(expected.measurementItemId, actual.measurementItemId);
        assertEquals(expected.value, actual.value);
    }

    /**
     * Javelin 計測項目テーブルを初期化します。<br />
     *
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    private void initJavelinMeasurementItemTable()
        throws SQLException
    {
        for (int index = 1; index <= 3; index++)
        {
            JavelinMeasurementItem javelinMeasurementItem = new JavelinMeasurementItem();
            //javelinMeasurementItem.measurementType = 1;
            javelinMeasurementItem.itemName = "ItemName" + index;
            javelinMeasurementItem.lastInserted = new Timestamp(System.currentTimeMillis());
            JavelinMeasurementItemDao.insert(DB_NAME, javelinMeasurementItem);
        }
        this.javelinMeasurementItemList_ = JavelinMeasurementItemDao.selectAll(DB_NAME);
    }

    /**
     * Javelin 計測値テーブルにレコードを追加します。<br />
     *
     * @return 追加したレコード
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    private MeasurementValue insertMeasurementValue()
        throws SQLException
    {
        JavelinMeasurementItem javelinMeasurementItem = this.javelinMeasurementItemList_.get(0);
        MeasurementValue measurementValue = new MeasurementValue();
        measurementValue.measurementTime = new Timestamp(System.currentTimeMillis());
        //measurementValue.measurementType = javelinMeasurementItem.measurementType;
        measurementValue.measurementItemId = javelinMeasurementItem.measurementItemId;
        measurementValue.value = "1";
        MeasurementValueDao.insert(DB_NAME, measurementValue);
        return measurementValue;
    }

    /**
     * @target testCount_notExist
     * @test レコード数の取得
     *   condition:: レコードが存在しない。
     *   result:: 0 が返ること。
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testCount_notExist()
        throws SQLException
    {
        // 実行
        int actual = MeasurementValueDao.count(DB_NAME);

        // 検証
        assertEquals(0, actual);
    }

    /**
     * @target testCount_exist
     * @test レコード数の取得
     *   condition:: レコードが存在する。
     *   result:: レコード数が返ること。
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testCount_exist()
        throws SQLException
    {
        // 準備
        initJavelinMeasurementItemTable();
        insertMeasurementValue();

        // 実行
        int actual = MeasurementValueDao.count(DB_NAME);

        // 検証
        assertEquals(1, actual);
    }

    /**
     * @target testSelectByTerm_inRange
     * @test 指定した範囲の取得
     *   condition:: 範囲内のデータが存在する。
     *   result:: レコードが返ること。
     * @throws InterruptedException スリープが中断した場合
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testSelectByTerm_inRange()
        throws InterruptedException,
            SQLException
    {
        // 準備
        insertMeasurementValue();
        Thread.sleep(SLEEP_TIME);
        Timestamp start = new Timestamp(System.currentTimeMillis());
        MeasurementValue measurementValue1 = insertMeasurementValue();
        Thread.sleep(SLEEP_TIME);
        MeasurementValue measurementValue2 = insertMeasurementValue();
        Timestamp end = new Timestamp(System.currentTimeMillis());
        Thread.sleep(SLEEP_TIME);
        insertMeasurementValue();

        // 実行
        List<MeasurementValue> actual =
                MeasurementValueDao.selectByTerm(DB_NAME, start, end);

        // 検証
        assertEquals(2, actual.size());
        assertEquals(measurementValue1, actual.get(0));
        assertEquals(measurementValue2, actual.get(1));
    }

    /**
     * @target testSelectByTerm_lowerRange
     * @test 指定した範囲の取得
     *   condition:: 範囲が下限を下回っている。
     *   result:: レコードが返らないこと。
     * @throws InterruptedException スリープが中断した場合
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testSelectByTerm_lowerRange()
        throws InterruptedException,
            SQLException
    {
        // 準備
        Timestamp start = new Timestamp(System.currentTimeMillis());
        Thread.sleep(SLEEP_TIME);
        Timestamp end = new Timestamp(System.currentTimeMillis());
        Thread.sleep(SLEEP_TIME);
        insertMeasurementValue();
        insertMeasurementValue();
        insertMeasurementValue();
        insertMeasurementValue();

        // 実行
        List<MeasurementValue> actual =
                MeasurementValueDao.selectByTerm(DB_NAME, start, end);

        // 検証
        assertEquals(0, actual.size());
    }

    /**
     * @target testSelectByTerm_upperRange
     * @test 指定した範囲の取得
     *   condition:: 範囲が上限を上回っている。
     *   result:: レコードが返らないこと。
     * @throws InterruptedException スリープが中断した場合
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testSelectByTerm_upperRange()
        throws InterruptedException,
            SQLException
    {
        // 準備
        insertMeasurementValue();
        insertMeasurementValue();
        insertMeasurementValue();
        insertMeasurementValue();
        Thread.sleep(SLEEP_TIME);
        Timestamp start = new Timestamp(System.currentTimeMillis());
        Thread.sleep(SLEEP_TIME);
        Timestamp end = new Timestamp(System.currentTimeMillis());

        // 実行
        List<MeasurementValue> actual =
                MeasurementValueDao.selectByTerm(DB_NAME, start, end);

        // 検証
        assertEquals(0, actual.size());
    }

    /**
     * @target testSelectAll_exist
     * @test すべて取得
     *   condition:: レコードが存在する。
     *   result:: レコードが返ること。
     * @throws InterruptedException スリープが中断した場合
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testSelectAll_exist()
        throws InterruptedException,
            SQLException
    {
        // 準備
        MeasurementValue measurementValue1 = insertMeasurementValue();
        Thread.sleep(SLEEP_TIME);
        MeasurementValue measurementValue2 = insertMeasurementValue();

        // 実行
        List<MeasurementValue> actual = MeasurementValueDao.selectAll(DB_NAME);

        // 検証
        assertEquals(2, actual.size());
        assertEquals(measurementValue1, actual.get(0));
        assertEquals(measurementValue2, actual.get(1));
    }

    /**
     * @target testSelectAll_notExist
     * @test すべて取得
     *   condition:: レコードが存在しない。
     *   result:: レコードが返らないこと。
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testSelectAll_notExist()
        throws SQLException
    {
        // 実行
        List<MeasurementValue> actual = MeasurementValueDao.selectAll(DB_NAME);

        // 検証
        assertEquals(0, actual.size());
    }

    /**
     * @target testGetTerm_existOne
     * @test 時刻の範囲取得
     *   condition:: レコードが 1 つ存在する。
     *   result:: 時刻の範囲が返ること。
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testGetTerm_existOne()
        throws SQLException
    {
        // 準備
        MeasurementValue measurementValue = insertMeasurementValue();

        // 実行
        Timestamp[] actual = MeasurementValueDao.getTerm(DB_NAME);

        // 検証
        assertNotNull(actual);
        assertEquals(measurementValue.measurementTime, actual[0]);
        assertEquals(measurementValue.measurementTime, actual[1]);
    }

    /**
     * @target testGetTerm_existMany
     * @test 時刻の範囲取得
     *   condition:: レコードが複数存在する。
     *   result:: 時刻の範囲が返ること。
     * @throws InterruptedException スリープが中断した場合
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testGetTerm_existMany()
        throws InterruptedException,
            SQLException
    {
        // 準備
        MeasurementValue measurementValue1 = insertMeasurementValue();
        Thread.sleep(SLEEP_TIME);
        insertMeasurementValue();
        Thread.sleep(SLEEP_TIME);
        MeasurementValue measurementValue2 = insertMeasurementValue();

        // 実行
        Timestamp[] actual = MeasurementValueDao.getTerm(DB_NAME);

        // 検証
        assertNotNull(actual);
        assertEquals(measurementValue1.measurementTime, actual[0]);
        assertEquals(measurementValue2.measurementTime, actual[1]);
    }

    /**
     * @target testGetTerm_notExist
     * @test 時刻の範囲取得
     *   condition:: レコードが存在しない。
     *   result:: <code>null</code> が返ること。
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testGetTerm_notExist()
        throws SQLException
    {
        // 実行
        Timestamp[] actual = MeasurementValueDao.getTerm(DB_NAME);

        // 検証
        assertNotNull(actual);
        assertNull(actual[0]);
        assertNull(actual[1]);
    }

}
