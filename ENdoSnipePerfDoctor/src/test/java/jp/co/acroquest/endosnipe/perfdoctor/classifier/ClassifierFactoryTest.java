package jp.co.acroquest.endosnipe.perfdoctor.classifier;

import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.Classifier;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.ClassifierFactory;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.KmeansClassifier;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.SimpleClassifier;
import junit.framework.TestCase;

/**
 * ClassifierFactoryのテストクラス
 * @author fujii
 *
 */
public class ClassifierFactoryTest extends TestCase
{

    /**
     * [項番] 1-1-9 getClassifierのテスト。 <br />
     * ・1つの要素からなるWarningUnitのリストに対して、
     *  getClassifierメソッドを呼ぶ。<br />
     * 
     * →SimpleClassifierが返ってくること。
     * 
     */
    public void testGetClassifier_ListSizeOne()
    {
        // 準備
        ClassifierFactory factory = createFactory();

        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        warningUnitList.add(unit);

        // 実行
        Classifier classifier = factory.getClassifier(warningUnitList);

        // 検証
        assertTrue(classifier instanceof SimpleClassifier);
    }

    /**
     * [項番] 1-1-10 getClassifierのテスト。 <br />
     * ・10個の要素からなるWarningUnitのリストに対して、
     *  getClassifierメソッドを呼ぶ。<br />
     * 
     * →SimpleClassifierが返ってくること。
     * 
     */
    public void testGetClassifier_ListSizeTen()
    {
        // 準備
        ClassifierFactory factory = createFactory();

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
        Classifier classifier = factory.getClassifier(warningUnitList);

        // 検証
        assertTrue(classifier instanceof SimpleClassifier);
    }

    /**
     * [項番] 1-1-11 getClassifierのテスト。 <br />
     * ・11個の要素からなるWarningUnitのリストに対して、
     *  getClassifierメソッドを呼ぶ。<br />
     * 
     * →KmeansClassifierが返ってくること。
     * 
     */
    public void testGetClassifier_ListSizeEleven()
    {
        // 準備
        ClassifierFactory factory = createFactory();

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

        // 実行
        Classifier classifier = factory.getClassifier(warningUnitList);

        // 検証
        assertTrue(classifier instanceof KmeansClassifier);
    }

    /**
     * [項番] 1-1-12 getClassifierのテスト。 <br />
     * ・20個の要素からなるWarningUnitのリストに対して、
     *  getClassifierメソッドを呼ぶ。<br />
     * 
     * →KmeansClassifierが返ってくること。
     * 
     */
    public void testGetClassifier_ListSizeTwenty()
    {
        // 準備
        ClassifierFactory factory = createFactory();

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
        Classifier classifier = factory.getClassifier(warningUnitList);

        // 検証
        assertTrue(classifier instanceof KmeansClassifier);
    }

    /**
     * ClassifierFactoryを生成する。
     * @return ClassifierFactory
     */
    public ClassifierFactory createFactory()
    {
        return ClassifierFactory.getInstance();
    }
}
