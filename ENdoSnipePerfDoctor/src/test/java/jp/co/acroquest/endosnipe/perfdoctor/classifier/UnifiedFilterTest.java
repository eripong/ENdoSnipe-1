package jp.co.acroquest.endosnipe.perfdoctor.classifier;

import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.UnifiedFilter;
import junit.framework.TestCase;

/**
 * UnifiedFilterのテストケース
 * @author fujii
 *
 */
public class UnifiedFilterTest extends TestCase
{
    /** TATの警告を出すルールID */
    private static final String RULEID_TAT     = "COD.MTRC.METHOD_TAT";

    /** 実処理時間の警告を出すルールID */
    private static final String RULEID_ELAPSED = "COD.MTRC.METHOD_ELAPSEDTIME";

    /** 複数の警告ルールを絞るフィルタ */
    private UnifiedFilter       filter_        = new UnifiedFilter();

    /**
     * [項番] <br>
     * <br>
     * doFilterのテスト<br>
     * ・ルールID：{"COD.MTRC.METHOD_ELAPSEDTIME","COD.MTRC.METHOD_TAT"}<br>
     * ・警告リストを１つ作成する<br>
     * →作成した警告リストがそのまま返る。<br>
     */
    public void testDoFilterTatOneData()
    {
        // 準備
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit =
                ClassifierUtil.createWarningUnit(RULEID_ELAPSED, "testFile", 0, 100, new Integer[]{
                        5, 10});
        warningUnitList.add(unit);

        // 実行
        List<WarningUnit> resultList = this.filter_.doFilter(warningUnitList);

        // 検証
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(0), resultList.get(0));

    }

    /**
     * [項番] <br>
     * <br>
     * doFilterのテスト<br>
     * ・ルールID：{"COD.MTRC.METHOD_ELAPSEDTIME","COD.MTRC.METHOD_TAT"}<br>
     * ・TATの開始時間が実時間の範囲に含まれている<br>
     * →警告のリストのサイズが1であること。
     * →ELAPSEDTIMEの警告ルールのみ表示される。<br>
     */
    public void testDoFilterTatStartIn()
    {
        // 準備
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 =
                ClassifierUtil.createWarningUnit(RULEID_ELAPSED, "testFile", 10, 30, new Integer[]{
                        5, 10});
        WarningUnit unit2 =
                ClassifierUtil.createWarningUnit(RULEID_TAT, "testFile", 20, 70, new Integer[]{5,
                        10});
        warningUnitList.add(unit1);
        warningUnitList.add(unit2);

        // 実行
        List<WarningUnit> resultList = this.filter_.doFilter(warningUnitList);

        // 検証
        assertEquals(1, resultList.size());
        ClassifierUtil.assertWarningUnitList(warningUnitList.get(0), resultList.get(0));
    }

    /**
     * [項番] <br>
     * <br>
     * doFilterのテスト<br>
     * ・ルールID：{"COD.MTRC.METHOD_ELAPSEDTIME","COD.MTRC.METHOD_TAT"}<br>
     * ・TATの終了時間が実時間の範囲に含まれている<br>
     * →警告のリストのサイズが1であること。
     * →ELAPSEDTIMEの警告ルールのみ表示される。<br>
     */

    /**
     * [項番] <br>
     * <br>
     * doFilterのテスト<br>
     * ・ルールID：{"COD.MTRC.METHOD_ELAPSEDTIME","COD.MTRC.METHOD_TAT"}<br>
     * ・TATの終了時間が実時間の範囲に含まれていない<br>
     * →ELAPSEDTIME、TATの警告ルールの両方が表示される。<br>
     */

}
