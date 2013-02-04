package jp.co.acroquest.endosnipe.perfdoctor.classifier;

import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnitGetter;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.PerformanceDoctorFilter;
import junit.framework.TestCase;

/**
 * PerformanceDoctorFilterのテストクラス
 * @author fujii
 *
 */
public class PerformanceDoctorFilterTest extends TestCase
{
    /**
     * [項番] 1-1-13 doFilterのテスト。 <br />
     * ・データが一つのWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     * 
     * →リストのデータがそのまま返ってくること。
     * 
     */
    public void testDoFilter_ListSizeOne()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        warningUnitList.add(unit);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(0), resultList.get(0));
    }

    /**
     * [項番] 1-1-14 doFilterのテスト。 <br />
     * ・データが10個のWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、2行目、6行目、10行目)。
     * 
     */
    public void testDoFilter_ListSizeTen()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);
        warningUnitList.add(unit6);
        warningUnitList.add(unit7);
        warningUnitList.add(unit8);
        warningUnitList.add(unit9);
        warningUnitList.add(unit10);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(3, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(1), resultList.get(0));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(5), resultList.get(1));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(9), resultList.get(2));
    }

    /**
     * [項番] 1-1-15 doFilterのテスト。 <br />
     * ・データが20個のWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、4行目、8行目、12行目、16行目、20行目)。
     * 
     */
    public void testDoFilter_ListSizeTwelve()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{0, 0});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit11 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit12 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit13 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit14 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit15 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit16 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit17 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit18 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit19 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit20 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);
        warningUnitList.add(unit6);
        warningUnitList.add(unit7);
        warningUnitList.add(unit8);
        warningUnitList.add(unit9);
        warningUnitList.add(unit10);
        warningUnitList.add(unit11);
        warningUnitList.add(unit12);
        warningUnitList.add(unit13);
        warningUnitList.add(unit14);
        warningUnitList.add(unit15);
        warningUnitList.add(unit16);
        warningUnitList.add(unit17);
        warningUnitList.add(unit18);
        warningUnitList.add(unit19);
        warningUnitList.add(unit20);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(5, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(3), resultList.get(0));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(7), resultList.get(1));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(11), resultList.get(2));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(15), resultList.get(3));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(19), resultList.get(4));
    }

    /**
     * [項番] 1-1-16 doFilterのテスト。 <br />
     * ・それぞれの要素が10個以下の2種類のルールIDが含まれたWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、2行目、5行目、24行目、27行目)。
     * 
     */
    public void testDoFilter_TwoSimpleClassifier()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});

        ClassifierUtil.ID = "testRuleId2";
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);
        warningUnitList.add(unit6);
        warningUnitList.add(unit7);
        warningUnitList.add(unit8);
        warningUnitList.add(unit9);
        warningUnitList.add(unit10);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(4, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(1), resultList.get(0));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(4), resultList.get(1));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(6), resultList.get(2));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(9), resultList.get(3));
    }

    /**
     * [項番] 1-1-17 doFilterのテスト。 <br />
     * ・要素数が10個以下のルールIDと11個以上のルールIDが含まれたWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、2行目、5行目、26行目、30行目、34行目、38行目、42行目)。
     * 
     */
    public void testDoFilter_OneSimpleClassifierOneKmeanClassifier()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        ClassifierUtil.ID = "testRuleId";
        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});

        ClassifierUtil.ID = "testRuleId2";
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit11 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit12 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit13 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit14 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit15 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit16 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit17 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit18 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit19 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit20 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit21 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit22 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit23 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit24 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit25 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);
        warningUnitList.add(unit6);
        warningUnitList.add(unit7);
        warningUnitList.add(unit8);
        warningUnitList.add(unit9);
        warningUnitList.add(unit10);
        warningUnitList.add(unit11);
        warningUnitList.add(unit12);
        warningUnitList.add(unit13);
        warningUnitList.add(unit14);
        warningUnitList.add(unit15);
        warningUnitList.add(unit16);
        warningUnitList.add(unit17);
        warningUnitList.add(unit18);
        warningUnitList.add(unit19);
        warningUnitList.add(unit20);
        warningUnitList.add(unit21);
        warningUnitList.add(unit22);
        warningUnitList.add(unit23);
        warningUnitList.add(unit24);
        warningUnitList.add(unit25);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(7, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(1), resultList.get(0));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(4), resultList.get(1));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(8), resultList.get(2));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(12), resultList.get(3));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(16), resultList.get(4));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(20), resultList.get(5));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(24), resultList.get(6));
    }

    /**
     * [項番] 1-1-18 doFilterのテスト。 <br />
     * ・要素数が11個以上のルールIDと10個以下のルールIDが含まれたWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、4行目、8行目、12行目、16行目、20行目、24行目、27行目)。
     * 
     */
    public void testDoFilter_OneKmeanClassifierOneSimpleClassifier()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        ClassifierUtil.ID = "testRuleId";
        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit11 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit12 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit13 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit14 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit15 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit16 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit17 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit18 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit19 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit20 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});

        ClassifierUtil.ID = "testRuleId2";
        WarningUnit unit21 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit22 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit23 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit24 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit25 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);
        warningUnitList.add(unit6);
        warningUnitList.add(unit7);
        warningUnitList.add(unit8);
        warningUnitList.add(unit9);
        warningUnitList.add(unit10);
        warningUnitList.add(unit11);
        warningUnitList.add(unit12);
        warningUnitList.add(unit13);
        warningUnitList.add(unit14);
        warningUnitList.add(unit15);
        warningUnitList.add(unit16);
        warningUnitList.add(unit17);
        warningUnitList.add(unit18);
        warningUnitList.add(unit19);
        warningUnitList.add(unit20);
        warningUnitList.add(unit21);
        warningUnitList.add(unit22);
        warningUnitList.add(unit23);
        warningUnitList.add(unit24);
        warningUnitList.add(unit25);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(7, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(3), resultList.get(0));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(7), resultList.get(1));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(11), resultList.get(2));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(15), resultList.get(3));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(19), resultList.get(4));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(21), resultList.get(5));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(24), resultList.get(6));
    }

    /**
     * [項番] 1-1-18 doFilterのテスト。 <br />
     * ・要素数が11個以上のルールIDと10個以下のルールIDが含まれたWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、4、8、12、16、20、26、30、34,38、42行目)。
     * 
     */
    public void testDoFilter_TwoKmeanClassifier()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        ClassifierUtil.ID = "testRuleId";
        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit11 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit12 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit13 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit14 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit15 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit16 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit17 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit18 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit19 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit20 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});

        ClassifierUtil.ID = "testRuleId2";
        WarningUnit unit21 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit22 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit23 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit24 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit25 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit26 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit27 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit28 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit29 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit30 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit31 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit32 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit33 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit34 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit35 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit36 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit37 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit38 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit39 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit40 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);
        warningUnitList.add(unit6);
        warningUnitList.add(unit7);
        warningUnitList.add(unit8);
        warningUnitList.add(unit9);
        warningUnitList.add(unit10);
        warningUnitList.add(unit11);
        warningUnitList.add(unit12);
        warningUnitList.add(unit13);
        warningUnitList.add(unit14);
        warningUnitList.add(unit15);
        warningUnitList.add(unit16);
        warningUnitList.add(unit17);
        warningUnitList.add(unit18);
        warningUnitList.add(unit19);
        warningUnitList.add(unit20);
        warningUnitList.add(unit21);
        warningUnitList.add(unit22);
        warningUnitList.add(unit23);
        warningUnitList.add(unit24);
        warningUnitList.add(unit25);
        warningUnitList.add(unit26);
        warningUnitList.add(unit27);
        warningUnitList.add(unit28);
        warningUnitList.add(unit29);
        warningUnitList.add(unit30);
        warningUnitList.add(unit31);
        warningUnitList.add(unit32);
        warningUnitList.add(unit33);
        warningUnitList.add(unit34);
        warningUnitList.add(unit35);
        warningUnitList.add(unit36);
        warningUnitList.add(unit37);
        warningUnitList.add(unit38);
        warningUnitList.add(unit39);
        warningUnitList.add(unit40);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(10, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(3), resultList.get(0));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(7), resultList.get(1));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(11), resultList.get(2));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(15), resultList.get(3));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(19), resultList.get(4));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(23), resultList.get(5));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(27), resultList.get(6));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(31), resultList.get(7));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(35), resultList.get(8));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(39), resultList.get(9));
    }

    /**
     * [項番] 1-1-20 doFilterのテスト。 <br />
     * ・ルールID、クラス名、メソッド名、重要度の異なるWarinigUnitをリストに持つときに、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、1、23、43、44、45、46行目)。
     * 
     */
    public void testDoFilter_DifferenceRule()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        String ruleIdDef = "testRuleId";
        String idDef = "testWarningId";
        String descriptionDef = "This is a testWarningUnit";
        String classNameDef = "testClass";
        String methodNameDef = "testMethod";
        String levelDef = "ERROR";
        String fileNameDef = "file";
        WarningUnit unit1 =
                WarningUnitGetter.createWarningUnit(ruleIdDef, idDef, descriptionDef, classNameDef,
                                                    methodNameDef, levelDef, fileNameDef, 1, 0, 0,
                                                    new Integer[]{5, 10});
        WarningUnit unit2 =
                WarningUnitGetter.createWarningUnit(ruleIdDef, "testRuleId2", descriptionDef,
                                                    classNameDef, methodNameDef, levelDef,
                                                    fileNameDef, 1, 0, 0, new Integer[]{5, 10});
        WarningUnit unit3 =
                WarningUnitGetter.createWarningUnit(ruleIdDef, idDef, descriptionDef, "testClass2",
                                                    methodNameDef, levelDef, fileNameDef, 1, 0, 0,
                                                    new Integer[]{5, 10});
        WarningUnit unit4 =
                WarningUnitGetter.createWarningUnit(ruleIdDef, idDef, descriptionDef, classNameDef,
                                                    "testMethod2", levelDef, fileNameDef, 1, 0, 0,
                                                    new Integer[]{5, 10});
        WarningUnit unit5 =
                WarningUnitGetter.createWarningUnit(ruleIdDef, idDef, descriptionDef, classNameDef,
                                                    methodNameDef, "WARN", fileNameDef, 1, 0, 0,
                                                    new Integer[]{5, 10});
        WarningUnit unit6 =
                WarningUnitGetter.createWarningUnit(ruleIdDef, idDef, descriptionDef, classNameDef,
                                                    methodNameDef, "INFO", fileNameDef, 1, 0, 0,
                                                    new Integer[]{5, 10});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);
        warningUnitList.add(unit6);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(6, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(0), resultList.get(0));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(1), resultList.get(1));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(2), resultList.get(2));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(3), resultList.get(3));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(4), resultList.get(4));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(5), resultList.get(5));
    }

    /**
     * [項番] <br />
     * ・データが一つのイベント用のWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     * 
     * →リストのデータがそのまま返ってくること。
     * 
     */
    public void testDoFilterEventListSizeOne()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        String stackTrace = "aaa";

        WarningUnit unit =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 10});
        warningUnitList.add(unit);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(0), resultList.get(0));
    }

    /**
     * [項番]  <br />
     * ・データが5個のイベント用のWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     *  WarningUnitに保存するスタックトレースはすべて同じであるとする。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、5行目)。
     * 
     */
    public void testDoFilterEventSameStackTrace()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        String stackTrace = "test1";

        WarningUnit unit1 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 10});
        WarningUnit unit2 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 11});
        WarningUnit unit3 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 12});
        WarningUnit unit4 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 13});
        WarningUnit unit5 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 14});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(1, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(4), resultList.get(0));
    }

    /**
     * [項番]  <br />
     * ・データが5個のイベント用のWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     *  WarningUnitに保存するスタックトレースはすべて異なる。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(1行目、2行目、3行目、4行目、5行目)。
     * 
     */
    public void testDoFilterEventDiffStackTrace()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        String stackTrace1 = "test1";
        String stackTrace2 = "test2";
        String stackTrace3 = "test3";
        String stackTrace4 = "test4";
        String stackTrace5 = "test5";

        WarningUnit unit1 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace1, new Integer[]{5, 10});
        WarningUnit unit2 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace2, new Integer[]{5, 11});
        WarningUnit unit3 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace3, new Integer[]{5, 12});
        WarningUnit unit4 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace4, new Integer[]{5, 13});
        WarningUnit unit5 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace5, new Integer[]{5, 14});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(5, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(0), resultList.get(0));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(1), resultList.get(1));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(2), resultList.get(2));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(3), resultList.get(3));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(4), resultList.get(4));
    }

    /**
     * [項番]  <br />
     * ・データが5個のイベント用のWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     *  WarningUnitに保存するスタックトレースが同じものと異なるものが混じっている。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、3行目、4行目、2行目)。
     * 
     */
    public void testDoFilterEventSomeStackTrace()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        String stackTrace1 = "test1";
        String stackTrace2 = "test2";
        String stackTrace3 = "test3";

        WarningUnit unit1 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace1, new Integer[]{5, 10});
        WarningUnit unit2 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace2, new Integer[]{5, 11});
        WarningUnit unit3 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace3, new Integer[]{5, 12});
        WarningUnit unit4 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace1, new Integer[]{5, 13});
        WarningUnit unit5 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace2, new Integer[]{5, 14});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(3, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(3), resultList.get(0));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(4), resultList.get(1));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(2), resultList.get(2));
    }

    /**
     * [項番]  <br />
     * ・要素数が11個以上のルールIDが含まれたWarningUnitのリストに対して、
     *  PerformanceDoctorFilter#doFilterを呼び出す。<br />
     *  WarningUnitに保存するスタックトレースはすべて同じであるとする。
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、4行目、8行目、12行目、16行目、20行目)。
     * 
     */
    public void testDoFilterEventOneKmeanClassifier()
    {
        // 準備
        PerformanceDoctorFilter filter = createFilter();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        String stackTrace = "test";

        ClassifierUtil.ID = "testRuleId";
        WarningUnit unit1 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 10});
        WarningUnit unit2 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 15});
        WarningUnit unit3 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 20});
        WarningUnit unit4 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 25});
        WarningUnit unit5 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 30});
        WarningUnit unit6 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 35});
        WarningUnit unit7 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 40});
        WarningUnit unit8 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 45});
        WarningUnit unit9 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 50});
        WarningUnit unit10 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 55});
        WarningUnit unit11 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 60});
        WarningUnit unit12 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 65});
        WarningUnit unit13 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 70});
        WarningUnit unit14 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 75});
        WarningUnit unit15 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 80});
        WarningUnit unit16 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 85});
        WarningUnit unit17 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 90});
        WarningUnit unit18 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 95});
        WarningUnit unit19 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 100});
        WarningUnit unit20 =
                ClassifierUtil.createDefaultEventWarningUnit(stackTrace, new Integer[]{5, 105});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);
        warningUnitList.add(unit6);
        warningUnitList.add(unit7);
        warningUnitList.add(unit8);
        warningUnitList.add(unit9);
        warningUnitList.add(unit10);
        warningUnitList.add(unit11);
        warningUnitList.add(unit12);
        warningUnitList.add(unit13);
        warningUnitList.add(unit14);
        warningUnitList.add(unit15);
        warningUnitList.add(unit16);
        warningUnitList.add(unit17);
        warningUnitList.add(unit18);
        warningUnitList.add(unit19);
        warningUnitList.add(unit20);

        // 実行
        List<WarningUnit> resultList = filter.doFilter(warningUnitList);

        // 検証
        assertEquals(5, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(3), resultList.get(0));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(7), resultList.get(1));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(11), resultList.get(2));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(15), resultList.get(3));
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(19), resultList.get(4));
    }

    /**
     * PerformaceDoctroFilterを作成する。
     * @return PerformanceDoctorFilter
     */
    public PerformanceDoctorFilter createFilter()
    {
        return new PerformanceDoctorFilter();
    }
}
