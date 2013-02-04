package jp.co.acroquest.endosnipe.perfdoctor.classifier;

import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.Classifier;
import jp.co.acroquest.endosnipe.perfdoctor.classfier.SimpleClassifier;
import junit.framework.TestCase;

/**
 * SimpleClassifierのテストデータ
 * @author fujii
 *
 */
public class SimpleClassifierTest extends TestCase
{
    /**
     * [項番] 1-1-1 convertのテスト。 <br />
     * ・データが一つのWarningUnitのリストに対して、
     *  SimpleClassifierを適用する。<br />
     * 
     * →リストのデータがそのまま返ってくること。
     * 
     */
    public void testClassify_OneData()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        warningUnitList.add(unit);

        // 実行
        List<WarningUnit> resultList = classifier.classify(warningUnitList);
        
        // 検証
        ClassifierUtil.assertWarningUnitList(unit, resultList.get(0));
    }

    /**
     * [項番] 1-1-2 convertのテスト。 <br />
     * ・10個のWarningUnitの要素からなるリストに対して、
     *  SimpleClassifierを適用する。<br />
     * 
     * →フィルターがかかって、リストが返ってくること(ここでは、2行目、6行目、10行目)。
     * 
     */
    public void testClassify_TenData()
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
        List<WarningUnit> resultList = classifier.classify(warningUnitList);
        
        // 検証
        assertEquals(3, resultList.size());
        ClassifierUtil.assertWarningUnitList(unit2, resultList.get(0));
        ClassifierUtil.assertWarningUnitList(unit6, resultList.get(1));
        ClassifierUtil.assertWarningUnitList(unit10, resultList.get(2));
    }

    /**
     * [項番] 1-1-3 convertのテスト。 <br />
     * ・argsの値に0を含んでいるWarningUnitの要素を持つリストに対して、
     *  SimpleClassifierを適用する。<br />
     * 
     * →1行目のデータが返ってくる。
     * 
     */
    public void testClassify_ContainsZero()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{0, 0});
                
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
     * [項番] 1-1-4 convertのテスト。 <br />
     * ・argsの値に文字列を含んでいるWarningUnitの要素を持つリストに対して、
     *  SimpleClassifierを適用する。<br />
     * 
     * →1行目のデータが返ってくる。
     * 
     */
    public void testClassify_ContainsString()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new String[]{"test", "test"});
                
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
     * [項番] 1-1-26 convertのテスト。 <br />
     * ・argsの値に-5を含んでいるWarningUnitの要素を持つリストに対して、
     *  SimpleClassifierを適用する。<br />
     * 
     * →1行目のデータが返ってくる。
     * 
     */
    public void testClassify_ContainsMinus()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, -5});
                
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
     * [項番] 1-1-27 convertのテスト。 <br />
     * ・argsの長さが1であるようなWarningUnitの要素を持つリストに対して、
     *  SimpleClassifierを適用する。<br />
     * 
     * →1行目のデータが返ってくる。
     * 
     */
    public void testClassify_argsLengthOne()
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
     * [項番] 1-1-30 convertのテスト。 <br />
     * ・argsの値に0を含んでいるWarningUnitの要素を持つリストに対して、
     *  SimpleClassifierを適用する。<br />
     * 
     * →21行目のデータが返ってくる。
     * 
     */
    public void testClassify_ContainsZero_AnotherOrder()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{0, 0});
                
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
     * [項番] 1-1-31 convertのテスト。 <br />
     * ・argsの値に文字列を含んでいるWarningUnitの要素を持つリストに対して、
     *  SimpleClassifierを適用する。<br />
     * 
     * →22行目のデータが返ってくる。
     * 
     */
    public void testClassify_ContainsString_AnotherOrder()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new String[]{"test", "test"});
                
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
     * [項番] 1-1-34 convertのテスト。 <br />
     * ・argsの値に-5を含んでいるWarningUnitの要素を持つリストに対して、
     *  SimpleClassifierを適用する。<br />
     * 
     * →48行目のデータが返ってくる。
     * 
     */
    public void testClassify_ContainsMinus_AnotherOrder()
    {
        // 準備
        Classifier classifier = createClassifier();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        WarningUnit unit1 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 10});
        WarningUnit unit2 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 15});
        WarningUnit unit3 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 20});
        WarningUnit unit4 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, 25});
        WarningUnit unit5 = ClassifierUtil.createDefaultWarningUnit(new Integer[]{5, -5});
                
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
     * [項番] 1-1-35 convertのテスト。 <br />
     * ・argsの長さが1であるようなWarningUnitの要素を持つリストに対して、
     *  SimpleClassifierを適用する。<br />
     * 
     * →47行目のデータが返ってくる。
     * 
     */
    public void testClassify_argsLengthOne_AnotherOrder()
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
     * SimpleClassifierを作成する。
     * @return SimpleClassifier
     */
    public Classifier createClassifier()
    {
        return new SimpleClassifier();
    }
}
