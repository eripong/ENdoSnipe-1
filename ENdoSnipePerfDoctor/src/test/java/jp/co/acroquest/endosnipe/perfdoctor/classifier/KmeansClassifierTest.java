package jp.co.acroquest.endosnipe.perfdoctor.classifier;

import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.Classifier;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.KmeansClassifier;
import junit.framework.TestCase;

/**
 * KmeansClassifierのテストクラス
 * @author fujii
 *
 */
public class KmeansClassifierTest extends TestCase
{
    /**
     * [項番] 1-1-5 classifyのテスト。 <br />
     * ・5つの要素からなるWarningUnitのリストに対して、
     *  KmeansClassifierを適用する。<br />
     * 
     * →リストのデータがそのまま返ってくること。
     * 
     */
    public void testClassify_FiveData()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);

        // 実行
        List<WarningUnit> resultList = classifier.classify(warningUnitList);

        // 検証
        assertEquals(5, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit1, resultList.get(0));
        ClassifierUtil.assertWarningUnitList(unit2, resultList.get(1));
        ClassifierUtil.assertWarningUnitList(unit3, resultList.get(2));
        ClassifierUtil.assertWarningUnitList(unit4, resultList.get(3));
        ClassifierUtil.assertWarningUnitList(unit5, resultList.get(4));
    }

    /**
     * [項番] 1-1-6 classifyのテスト。 <br />
     * ・20個の要素からなるWarningUnitのリストに対して、
     *  KmeansClassifierを適用する。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、4行目、8行目、12行目、16行目、20行目)。
     * 
     */
    public void testClassify_TwentyData()
    {
        // 準備
        Classifier classifier = createClassifier();
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
        List<WarningUnit> resultList = classifier.classify(warningUnitList);

        // 検証
        assertEquals(5, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit4, resultList.get(0));
        ClassifierUtil.assertWarningUnitList(unit8, resultList.get(1));
        ClassifierUtil.assertWarningUnitList(unit12, resultList.get(2));
        ClassifierUtil.assertWarningUnitList(unit16, resultList.get(3));
        ClassifierUtil.assertWarningUnitList(unit20, resultList.get(4));
    }

    /**
     * [項番] 1-1-7 classifyのテスト。 <br />
     * ・Argsの値が0を含んだWarningUnitのリストに対して、
     *  KmeansClassifierを適用する。<br />
     * 
     * →4,8,12,16,20行目のデータが返ってくること。
     * 
     */
    public void testClassify_ContainsZero()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit11 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit12 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit13 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit14 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit15 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit16 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit17 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit18 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit19 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});
        WarningUnit unit20 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{0, 0});

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
        List<WarningUnit> resultList = classifier.classify(warningUnitList);

        // 検証
        assertEquals(5, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit3, resultList.get(0));
        ClassifierUtil.assertWarningUnitList(unit7, resultList.get(1));
        ClassifierUtil.assertWarningUnitList(unit11, resultList.get(2));
        ClassifierUtil.assertWarningUnitList(unit15, resultList.get(3));
        ClassifierUtil.assertWarningUnitList(unit19, resultList.get(4));
    }

    /**
     * [項番] 1-1-8 classifyのテスト。 <br />
     * ・argsの値に文字列を含んでいるWarningUnitの要素を持つリストに対して、
     *  KmeansClassifierを適用する。<br />
     * 
     * →1行目のデータが返ってくること。
     * 
     */
    public void testClassify_ContainsString()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit11 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit12 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit13 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit14 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit15 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit16 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit17 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit18 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit19 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});
        WarningUnit unit20 = ClassifierUtil.createDefaultWarningUnit(new String[]{"test", "test"});

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
        List<WarningUnit> resultList = classifier.classify(warningUnitList);

        // 検証
        assertEquals(1, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit1, resultList.get(0));
    }
    
    /**
     * [項番] 1-1-28 classifyのテスト。 <br />
     * ・Argsの値が-5を含んだWarningUnitのリストに対して、
     *  KmeansClassifierを適用する。<br />
     * 
     * →4,8,12,16,20行目のデータが返ってくること。
     * 
     */
    public void testClassify_ContainsMinus()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit11 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit12 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit13 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit14 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit15 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit16 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit17 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit18 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit19 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});
        WarningUnit unit20 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{0, -5});

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
        List<WarningUnit> resultList = classifier.classify(warningUnitList);

        // 検証
        assertEquals(5, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit3, resultList.get(0));
        ClassifierUtil.assertWarningUnitList(unit7, resultList.get(1));
        ClassifierUtil.assertWarningUnitList(unit11, resultList.get(2));
        ClassifierUtil.assertWarningUnitList(unit15, resultList.get(3));
        ClassifierUtil.assertWarningUnitList(unit19, resultList.get(4));
    }

    /**
     * [項番] 1-1-29 classifyのテスト。 <br />
     * ・argsの長さが1であるようなWarningUnitの要素を持つリストに対して、
     *  KmeansClassifierを適用する。<br />
     * 
     * →1行目のデータが返ってくること。
     * 
     */
    public void testClassify_ArgsLendthOne()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5});

        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);
        warningUnitList.add(unit5);

        // 実行
        List<WarningUnit> resultList = classifier.classify(warningUnitList);

        // 検証
        assertEquals(1, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit1, resultList.get(0));
    }

    /**
     * [項番] 1-1-32 classifyのテスト。 <br />
     * ・Argsの値が0を含んだWarningUnitのリストに対して、
     *  KmeansClassifierを適用する。<br />
     * 
     * →4,8,12,16,20行目のデータが返ってくること。
     * 
     */
    public void testClassify_ContainsZero_AnotherOrder()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit11 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit12 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit13 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit14 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit15 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit16 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit17 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit18 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit19 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});
        WarningUnit unit20 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{0, 0});

        warningUnitList.add(unit20);
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

        // 実行
        List<WarningUnit> resultList = classifier.classify(warningUnitList);

        // 検証
        assertEquals(5, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit3, resultList.get(0));
        ClassifierUtil.assertWarningUnitList(unit7, resultList.get(1));
        ClassifierUtil.assertWarningUnitList(unit11, resultList.get(2));
        ClassifierUtil.assertWarningUnitList(unit15, resultList.get(3));
        ClassifierUtil.assertWarningUnitList(unit19, resultList.get(4));
    }

    /**
     * [項番] 1-1-33 classifyのテスト。 <br />
     * ・argsの値に文字列を含んでいるWarningUnitの要素を持つリストに対して、
     *  KmeansClassifierを適用する。<br />
     * 
     * →1行目のデータが返ってくること。
     * 
     */
    public void testClassify_ContainsString_AnotherOrder()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit11 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit12 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit13 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit14 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit15 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit16 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit17 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit18 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit19 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});
        WarningUnit unit20 = ClassifierUtil.createDefaultWarningUnit(new String[]{"test", "test"});

        warningUnitList.add(unit20);
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

        // 実行
        List<WarningUnit> resultList = classifier.classify(warningUnitList);

        // 検証
        assertEquals(1, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit20, resultList.get(0));
    }
    
    /**
     * [項番] 1-1-36 classifyのテスト。 <br />
     * ・Argsの値が-5を含んだWarningUnitのリストに対して、
     *  KmeansClassifierを適用する。<br />
     * 
     * →4,8,12,16,20行目のデータが返ってくること。
     * 
     */
    public void testClassify_ContainsMinus_AnotherOrder()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 30});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 35});
        WarningUnit unit6 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 40});
        WarningUnit unit7 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 45});
        WarningUnit unit8 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 50});
        WarningUnit unit9 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 55});
        WarningUnit unit10 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 60});
        WarningUnit unit11 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 65});
        WarningUnit unit12 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 70});
        WarningUnit unit13 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 75});
        WarningUnit unit14 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 80});
        WarningUnit unit15 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 85});
        WarningUnit unit16 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 90});
        WarningUnit unit17 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 95});
        WarningUnit unit18 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 100});
        WarningUnit unit19 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 105});
        WarningUnit unit20 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{0, -5});

        warningUnitList.add(unit20);
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

        // 実行
        List<WarningUnit> resultList = classifier.classify(warningUnitList);

        // 検証
        assertEquals(5, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit3, resultList.get(0));
        ClassifierUtil.assertWarningUnitList(unit7, resultList.get(1));
        ClassifierUtil.assertWarningUnitList(unit11, resultList.get(2));
        ClassifierUtil.assertWarningUnitList(unit15, resultList.get(3));
        ClassifierUtil.assertWarningUnitList(unit19, resultList.get(4));
    }

    /**
     * [項番] 1-1-37 classifyのテスト。 <br />
     * ・argsの長さが1であるようなWarningUnitの要素を持つリストに対して、
     *  KmeansClassifierを適用する。<br />
     * 
     * →1行目のデータが返ってくること。
     * 
     */
    public void testClassify_ArgsLendthOne_AnotherOrder()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5});

        warningUnitList.add(unit5);
        warningUnitList.add(unit1);
        warningUnitList.add(unit2);
        warningUnitList.add(unit3);
        warningUnitList.add(unit4);

        // 実行
        List<WarningUnit> resultList = classifier.classify(warningUnitList);

        // 検証
        assertEquals(1, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit5, resultList.get(0));
    }

    /**
     * KmeansClassifierを作成する。
     * @return KmeansClassifier
     */
    public Classifier createClassifier()
    {
        return new KmeansClassifier();
    }
}
