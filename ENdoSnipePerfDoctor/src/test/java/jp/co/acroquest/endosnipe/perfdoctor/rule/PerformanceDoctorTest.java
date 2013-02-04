package jp.co.acroquest.endosnipe.perfdoctor.rule;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRule;
import jp.co.acroquest.endosnipe.perfdoctor.exception.RuleCreateException;
import jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule;
import jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodTatRule;
import jp.co.acroquest.endosnipe.perfdoctor.rule.dbaccess.AllSqlCountRule;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.PropertyDef;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleDef;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleLevelDef;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleSetConfig;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleSetDef;
import jp.co.dgic.testing.common.virtualmock.MockObjectManager;
import junit.framework.TestCase;

/**
 * パフォーマンスドクターの単体試験に使用するテストケース。<br>
 * <br>
 * ※以下の設定でdjUnitを実行すること。<br>
 * <ul>
 * <li>Virtual Mock Objectsを使用する</li>
 * <li>バイトコード操作ライブラリにはBCEL</li>
 * <li>-noverify(VMオプション)を使用する</li>
 * </ul>
 * @author tooru
 */
public class PerformanceDoctorTest extends TestCase
{
    /**
     * 項番2-1-1<br>
     * 項番2-1-2 Java6.0<br>
     * 項番2-1-3<br>
     * 項番2-1-4
     */
    public void testReadXml_Normal()
    {
        XmlRuleDefAccessor accessor = new XmlRuleDefAccessor();
        RuleSetDef ruleSetDef = new RuleSetDef();

        try
        {
            String fileName = getResourcePath("data_testReadXml_Normal.xml");
            ruleSetDef = accessor.findRuleSet(fileName);
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            fail();
        }

        String id = "COD.MTRC.METHOD_TAT";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodTatRule";
        String enabled = "TRUE";
        String[] infoLevelDefArgs = {"INFO", "FALSE", "0", "0"};
        String[] warnLevelDefArgs = {"WARN", "TRUE", "0", "5000"};
        String[] errorLevelDefArgs = {"ERROR", "FALSE", "100", "20000"};
        RuleDef ruleDef =
                          createRuleDef(id, className, enabled, infoLevelDefArgs, warnLevelDefArgs,
                                        errorLevelDefArgs);

        List<RuleDef> ruleDefs = new ArrayList<RuleDef>();
        ruleDefs.add(ruleDef);

        RuleSetDef ruleSetDef2 = new RuleSetDef();
        ruleSetDef2.setRuleDefs(ruleDefs);
        ruleSetDef2.setName("setA");

        if (compareRuleSetDef(ruleSetDef, ruleSetDef2) == false)
        {
            fail();
        }
    }

    /**
     * 項番2-1-6
     */

    public void testReadXml_NotRuleDefXml()
    {
        XmlRuleDefAccessor accessor = new XmlRuleDefAccessor();

        try
        {
            String fileName = getResourcePath("data_testReadXml_NotRuleDefXml.xml");
            accessor.findRuleSet(fileName);
        }
        // 例外が発生すれば成功。
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            return;
        }

        fail();
    }

    /**
     * 項番2-1-8
     */
    public void testReadXml_ReadTextFile()
    {
        XmlRuleDefAccessor accessor = new XmlRuleDefAccessor();
        RuleSetDef ruleSetDef = new RuleSetDef();

        try
        {
            String fileName = getResourcePath("data_testReadXml_ReadTextFile.txt");
            ruleSetDef = accessor.findRuleSet(fileName);
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            fail();
        }

        String id = "COD.MTRC.METHOD_TAT";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodTatRule";
        String enabled = "TRUE";
        String[] infoLevelDefArgs = {"INFO", "FALSE", "0", "0"};
        String[] warnLevelDefArgs = {"WARN", "TRUE", "0", "5000"};
        String[] errorLevelDefArgs = {"ERROR", "FALSE", "100", "20000"};
        RuleDef ruleDef =
                          createRuleDef(id, className, enabled, infoLevelDefArgs, warnLevelDefArgs,
                                        errorLevelDefArgs);

        List<RuleDef> ruleDefs = new ArrayList<RuleDef>();
        ruleDefs.add(ruleDef);

        RuleSetDef ruleSetDef2 = new RuleSetDef();
        ruleSetDef2.setRuleDefs(ruleDefs);
        ruleSetDef2.setName("setA");

        if (compareRuleSetDef(ruleSetDef, ruleSetDef2) == false)
        {
            fail();
        }
    }

    /**
     * 項番2-1-9
     */

    public void testReadXml_NotXml()
    {
        XmlRuleDefAccessor accessor = new XmlRuleDefAccessor();

        try
        {
            String fileName = getResourcePath("data_testReadXml_NotXml.txt");
            accessor.findRuleSet(fileName);
        }
        // 例外が発生すれば成功。
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            return;
        }

        fail();
    }

    /**
     * 項番2-1-10
     */

    public void testReadXml_NotFound()
    {
        XmlRuleDefAccessor accessor = new XmlRuleDefAccessor();

        try
        {
            accessor.findRuleSet("testReadXml_NotFound.xml");
        }
        // 例外が発生すれば成功。
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            return;
        }

        fail();
    }

    /**
     * 項番2-1-12<br>
     * 項番2-1-13 Java6.0<br>
     * 項番2-1-14<br>
     * 項番2-1-15
     */
    public void testGetRuleSetFromID_normal()
    {
        RuleManager manager = initRuleManager();

        RuleSetConfig config1 = new RuleSetConfig();
        config1.setId("setAID");
        config1.setName("setA");
        String fileName1 = getResourcePath("data_testGetRuleSetFromID_normal_setA.xml");
        config1.setFileName(fileName1);

        RuleSetConfig config2 = new RuleSetConfig();
        config2.setId("setBID");
        config2.setName("setB");
        String fileName2 = getResourcePath("data_testGetRuleSetFromID_normal_setB.xml");
        config2.setFileName(fileName2);

        manager.addRuleSetConfig(config1);
        manager.addRuleSetConfig(config2);
        RuleSetDef ruleSetDef1 = new RuleSetDef();
        RuleSetDef ruleSetDef2 = createRuleSetDef_Data1();

        try
        {
            ruleSetDef1 = manager.getRuleSetDef("setAID");
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
        }

        if (compareRuleSetDef(ruleSetDef1, ruleSetDef2) == false)
        {
            fail();
        }

        // ruleSetMap_に書き込まれていることを確認。
        // 読込先のファイル名のデータを変更。

        config1.setFileName("");
        config2.setFileName("");
        ruleSetDef1 = new RuleSetDef();

        try
        {
            ruleSetDef1 = manager.getRuleSetDef("setAID");
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
        }

        if (compareRuleSetDef(ruleSetDef1, ruleSetDef2) == false)
        {
            fail();
        }
    }

    /**
     * 項番2-1-16
     */
    public void testGetRuleSetFromID_Another()
    {
        RuleManager manager = RuleManager.getInstance();

        RuleSetConfig config1 = new RuleSetConfig();
        config1.setId("setAID");
        config1.setName("setA");
        String fileName1 = getResourcePath("data_testGetRuleSetFromID_normal_setA.xml");
        config1.setFileName(fileName1);

        RuleSetConfig config2 = new RuleSetConfig();
        config2.setId("setBID");
        config2.setName("setB");
        String fileName2 = getResourcePath("data_testGetRuleSetFromID_normal_setB.xml");
        config2.setFileName(fileName2);

        manager.addRuleSetConfig(config1);
        manager.addRuleSetConfig(config2);
        RuleSetDef ruleSetDef1 = new RuleSetDef();
        RuleSetDef ruleSetDef2 = createRuleSetDef_Data2();

        try
        {
            ruleSetDef1 = manager.getRuleSetDef("setBID");
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
        }

        if (compareRuleSetDef(ruleSetDef1, ruleSetDef2) == false)
        {
            fail();
        }

    }

    /**
     * 項番2-1-17
     */
    public void testGetRuleSetFromID_NotFound()
    {
        RuleManager manager = RuleManager.getInstance();

        RuleSetConfig config1 = new RuleSetConfig();
        config1.setId("setAID");
        config1.setName("setA");
        String fileName1 = getResourcePath("data_testGetRuleSetFromID_normal_setA.xml");
        config1.setFileName(fileName1);

        RuleSetConfig config2 = new RuleSetConfig();
        config2.setId("setBID");
        config2.setName("setB");
        String fileName2 = getResourcePath("data_testGetRuleSetFromID_normal_setB.xml");
        config2.setFileName(fileName2);

        manager.addRuleSetConfig(config1);
        manager.addRuleSetConfig(config2);

        try
        {
            manager.getRuleSetDef("setCID");
        }
        catch (RuleCreateException ex)
        {
            // 例外が発生すれば成功
            ex.printStackTrace();
            return;
        }

        fail();
    }

    /**
     * 項番2-1-18
     */
    public void testGetRuleSetFromID_Null()
    {
        RuleManager manager = RuleManager.getInstance();

        RuleSetConfig config1 = new RuleSetConfig();
        config1.setId("setAID");
        config1.setName("setA");
        String fileName1 = getResourcePath("data_testGetRuleSetFromID_normal_setA.xml");
        config1.setFileName(fileName1);

        RuleSetConfig config2 = new RuleSetConfig();
        config2.setId("setBID");
        config2.setName("setB");
        String fileName2 = getResourcePath("data_testGetRuleSetFromID_normal_setB.xml");
        config2.setFileName(fileName2);

        manager.addRuleSetConfig(config1);
        manager.addRuleSetConfig(config2);
        RuleSetDef ruleSetDef1 = new RuleSetDef();
        RuleSetDef ruleSetDef2 = createRuleSetDef_Data1();

        try
        {
            ruleSetDef1 = manager.getRuleSetDef(null);
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            fail();
        }
        catch (NullPointerException e)
        {
            // 要UT仕様書の修正。
            return;
        }

        fail();
    }

    /**
     * 項番2-1-19
     */
    public void testGetRuleSetFromActive_normal()
    {
        RuleManager manager = RuleManager.getInstance();

        RuleSetConfig config1 = new RuleSetConfig();
        config1.setId("setAID");
        config1.setName("setA");
        String fileName1 = getResourcePath("data_testGetRuleSetFromID_normal_setA.xml");
        config1.setFileName(fileName1);

        RuleSetConfig config2 = new RuleSetConfig();
        config2.setId("setBID");
        config2.setName("setB");
        String fileName2 = getResourcePath("data_testGetRuleSetFromID_normal_setB.xml");
        config2.setFileName(fileName2);

        manager.addRuleSetConfig(config1);
        manager.addRuleSetConfig(config2);
        RuleSetDef ruleSetDef1 = new RuleSetDef();
        RuleSetDef ruleSetDef2 = createRuleSetDef_Data1();

        manager.setActiveRuleSetConfig(config1);

        try
        {
            ruleSetDef1 = manager.getActiveRuleSetDef();
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
        }

        if (compareRuleSetDef(ruleSetDef1, ruleSetDef2) == false)
        {
            fail();
        }

        // ruleSetMap_に書き込まれていることを確認。
        // 読込先のファイル名のデータを変更。

        config1.setFileName("");
        config2.setFileName("");
        ruleSetDef1 = new RuleSetDef();

        try
        {
            ruleSetDef1 = manager.getActiveRuleSetDef();
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
        }

        if (compareRuleSetDef(ruleSetDef1, ruleSetDef2) == false)
        {
            fail();
        }
    }

    /**
     * 項番2-1-20
     */
    public void testGetRuleSetFromActive_Another()
    {
        RuleManager manager = RuleManager.getInstance();

        RuleSetConfig config1 = new RuleSetConfig();
        config1.setId("setAID");
        config1.setName("setA");
        String fileName1 = getResourcePath("data_testGetRuleSetFromID_normal_setA.xml");
        config1.setFileName(fileName1);

        RuleSetConfig config2 = new RuleSetConfig();
        config2.setId("setBID");
        config2.setName("setB");
        String fileName2 = getResourcePath("data_testGetRuleSetFromID_normal_setB.xml");
        config2.setFileName(fileName2);

        manager.addRuleSetConfig(config1);
        manager.addRuleSetConfig(config2);
        RuleSetDef ruleSetDef1 = new RuleSetDef();
        RuleSetDef ruleSetDef2 = createRuleSetDef_Data2();

        manager.setActiveRuleSetConfig(config2);

        try
        {
            ruleSetDef1 = manager.getActiveRuleSetDef();
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
        }

        if (compareRuleSetDef(ruleSetDef1, ruleSetDef2) == false)
        {
            fail();
        }
    }

    /**
     * 項番2-1-21
     */
    public void testGetRuleSetFromActive_Anothe()
    {
        RuleManager manager = RuleManager.getInstance();

        RuleSetConfig config1 = new RuleSetConfig();
        config1.setId("setAID");
        config1.setName("setA");
        String fileName1 = getResourcePath("data_testGetRuleSetFromID_normal_setA.xml");
        config1.setFileName(fileName1);

        RuleSetConfig config2 = new RuleSetConfig();
        config2.setId("setBID");
        config2.setName("setB");
        String fileName2 = getResourcePath("data_testGetRuleSetFromID_normal_setB.xml");
        config2.setFileName(fileName2);

        manager.addRuleSetConfig(config1);
        manager.addRuleSetConfig(config2);
        RuleSetDef ruleSetDef1 = new RuleSetDef();

        manager.setActiveRuleSetConfig(config1);

        try
        {
            ruleSetDef1 = manager.getActiveRuleSetDef();
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            fail();
        }

        // 名前がconfig1ルールセットと異なったら失敗。
        assertEquals(config1.getName(), ruleSetDef1.getName());
    }

    /**
     * 項番2-2-1
     */
    public void testCreateRuleInstanceFromClassName_Normal()
    {
        PerformanceRule rule = null;

        try
        {
            String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodTatRule";
            rule = RuleInstanceUtil.createNewInstance(className);
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            fail();
        }

        if (rule == null)
        {
            fail();
        }

        // MethodTatRule型でなければ失敗。
        if (rule instanceof MethodTatRule == false)
        {
            fail();
        }
    }

    /**
     * 項番2-2-2<br>
     * 項番2-2-3 Java6.0<br>
     * 項番2-2-4<br>
     * 項番2-2-5
     */
    public void testCreateRuleInstanceFromClassName_Another()
    {
        PerformanceRule rule = null;

        try
        {
            String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.dbaccess.AllSqlCountRule";
            rule = RuleInstanceUtil.createNewInstance(className);
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            fail();
        }

        if (rule == null)
        {
            fail();
        }

        // AllSqlCountRule型でなければ失敗。
        if (rule instanceof AllSqlCountRule == false)
        {
            fail();
        }
    }

    /**
     * 項番2-2-6<br>
     * 項番2-2-7 Java6.0<br>
     * 項番2-2-8<br>
     * 項番2-2-9
     */
    public void testCreateRuleInstanceFromClassName_NotRule()
    {
        try
        {
            RuleInstanceUtil.createNewInstance("jp.co.acroquest.endosnipe.perfdoctor.PerfDoctor");
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();

            if ("クラスがPerformanceRuleインタフェースの実装ではありません。(クラス名:jp.co.acroquest.endosnipe.perfdoctor.PerfDoctor)".equals(ex.getMessage()))
            {
                return;
            }

            fail();
        }

        // 例外が発生しなければ失敗。
        fail();
    }

    /**
     * 項番2-2-10
     */
    public void testCreateRuleInstanceFromClassName_ClassNotFound()
    {
        try
        {
            RuleInstanceUtil.createNewInstance("jp.co.acroquest.endosnipe.perfdoctor.rule.NotExist");
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();

            if ("クラスファイルが存在しません。(クラス名:jp.co.acroquest.endosnipe.perfdoctor.rule.NotExist)".equals(ex.getMessage()))
            {
                return;
            }

            fail();
        }

        // 例外が発生しなければ失敗。
        fail();
    }

    /**
     * 項番2-2-12
     */
    public void testCreateRuleInstanceFromRuleDef_Normal()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "TRUE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule";
        String[] infoLevelArgs = new String[]{"INFO", "TRUE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "TRUE", "100", "5000"};

        mainCreateRuleInstanceFromRuleDef(id, enabled, className, infoLevelArgs, warnLevelArgs,
                                          errorLevelArgs);
    }

    /**
     * 項番2-2-13
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_13()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "FALSE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule";
        String[] infoLevelArgs = new String[]{"INFO", "TRUE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "TRUE", "100", "5000"};

        mainCreateRuleInstanceFromRuleDef(id, enabled, className, infoLevelArgs, warnLevelArgs,
                                          errorLevelArgs);
    }

    /**
     * 項番2-2-14<br>
     * 項番2-2-15 Java6.0
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_14()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "TRUE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule";
        String[] infoLevelArgs = new String[]{"INFO", "FALSE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "FALSE", "100", "5000"};

        mainCreateRuleInstanceFromRuleDef(id, enabled, className, infoLevelArgs, warnLevelArgs,
                                          errorLevelArgs);
    }

    /**
     * 項番2-2-18
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_18()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "TRUE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule";
        String[] infoLevelArgs = new String[]{"INFO", "FALSE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "FALSE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "FALSE", "100", "5000"};

        mainCreateRuleInstanceFromRuleDef(id, enabled, className, infoLevelArgs, warnLevelArgs,
                                          errorLevelArgs);
    }

    /**
     * 項番2-2-20
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_20()
    {
        String id = "";
        String enabled = "TRUE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule";
        String[] infoLevelArgs = new String[]{"INFO", "TRUE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "TRUE", "100", "5000"};

        mainCreateRuleInstanceFromRuleDef(id, enabled, className, infoLevelArgs, warnLevelArgs,
                                          errorLevelArgs);
    }

    /**
     * 項番2-2-22<br>
     * 項番2-2-23 Java6.0
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_22()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "TRUE";
        String className = "";
        String[] infoLevelArgs = new String[]{"INFO", "TRUE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "TRUE", "100", "5000"};

        mainCreateRuleInstanceFromRuleDef_Fail(id, enabled, className, infoLevelArgs,
                                               warnLevelArgs, errorLevelArgs);
    }

    /**
     * 
     * 項番2-2-26
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_26()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "TRUE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.NotFound";
        String[] infoLevelArgs = new String[]{"INFO", "TRUE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "TRUE", "100", "5000"};

        mainCreateRuleInstanceFromRuleDef_Fail(id, enabled, className, infoLevelArgs,
                                               warnLevelArgs, errorLevelArgs);
    }

    /**
     * 
     * 項番2-2-27
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_27()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "TRUE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.PerfDoctor";
        String[] infoLevelArgs = new String[]{"INFO", "TRUE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "TRUE", "100", "5000"};

        mainCreateRuleInstanceFromRuleDef_Fail(id, enabled, className, infoLevelArgs,
                                               warnLevelArgs, errorLevelArgs);
    }

    /**
     * 
     * 項番2-2-32
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_32()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "TRUE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule";
        String[] infoLevelArgs = new String[]{"INFO", "TRUE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "TRUE", "duration", "5000"};

        mainCreateRuleInstanceFromRuleDef(id, enabled, className, infoLevelArgs, warnLevelArgs,
                                          errorLevelArgs);
    }

    /**
     * 
     * 項番2-2-33
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_33()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "TRUE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule";
        String[] infoLevelArgs = new String[]{"INFO", "TRUE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "TRUE", "", "5000"};

        mainCreateRuleInstanceFromRuleDef(id, enabled, className, infoLevelArgs, warnLevelArgs,
                                          errorLevelArgs);
    }

    /**
     * 項番2-2-35<br>
     * 項番2-2-36 Java6.0
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_35()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "TRUE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule";
        String[] infoLevelArgs = new String[]{"INFO", "TRUE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "TRUE", "100", "threshold"};

        mainCreateRuleInstanceFromRuleDef(id, enabled, className, infoLevelArgs, warnLevelArgs,
                                          errorLevelArgs);
    }

    /**
     * 
     * 項番2-2-39
     */
    public void testCreateRuleInstanceFromRuleDef_Normal_2_2_39()
    {
        String id = "COD.MTRC.METHOD_CNT";
        String enabled = "TRUE";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule";
        String[] infoLevelArgs = new String[]{"INFO", "TRUE", "100", "200"};
        String[] warnLevelArgs = new String[]{"WARN", "TRUE", "100", "1000"};
        String[] errorLevelArgs = new String[]{"ERROR", "TRUE", "100", ""};

        mainCreateRuleInstanceFromRuleDef(id, enabled, className, infoLevelArgs, warnLevelArgs,
                                          errorLevelArgs);
    }

    /**
     * 項番2-2-41<br>
     * 項番2-2-42 Java6.0
     */
    public void testGetActiveRules_2_2_41()
        throws Exception
    {
        String params[] =
                          {
                                  "COD.MTRC.METHOD_CPU",
                                  "TRUE",
                                  "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule",
                                  "INFO", "TRUE", "0", "0", "WARN", "TRUE", "0", "5000", "ERROR",
                                  "TRUE", "100", "20000"};

        String params2[] =
                           {
                                   "COD.TRHD.WAIT_TIME",
                                   "TRUE",
                                   "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule",
                                   "INFO", "TRUE", "500", "2000", "WARN", "TRUE", "500", "60000",
                                   "ERROR", "TRUE", "500", "200000"};

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * 
     * 項番2-2-45
     */
    public void testGetActiveRules_2_2_45()
        throws Exception
    {
        String params[] =
                          {"COD.MTRC.METHOD_TAT", "TRUE",
                                  "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodTatRule",
                                  "INFO", "TRUE", "0", "0", "WARN", "TRUE", "0", "5000", "ERROR",
                                  "TRUE", "0", "0"};

        String params2[] =
                           {
                                   "COD.MTRC.METHOD_CNT",
                                   "TRUE",
                                   "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule",
                                   "INFO", "TRUE", "100", "0", "WARN", "TRUE", "100", "5000",
                                   "ERROR", "TRUE", "100", "20000"};

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * 
     * 項番2-2-48
     */
    public void testGetActiveRules_2_2_48()
        throws Exception
    {
        String params[] = null;

        String params2[] = null;

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * 
     * 項番2-2-49
     */
    public void testGetActiveRules_2_2_49()
        throws Exception
    {
        String params[] =
                          {
                                  "COD.MTRC.METHOD_CPU",
                                  "TRUE",
                                  "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCpuUsageRule",
                                  "INFO", "TRUE", "0", "0", "WARN", "TRUE", "0", "5000", "ERROR",
                                  "TRUE", "100", "20000"};

        String params2[] =
                           {
                                   "COD.THRD.WAIT_TIME",
                                   "FALSE",
                                   "jp.co.acroquest.endosnipe.perfdoctor.rule.code.ThreadWaitTimeRule",
                                   "INFO", "TRUE", "500", "2000", "WARN", "TRUE", "500", "60000",
                                   "ERROR", "TRUE", "500", "200000"};

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * 項番2-2-50<br>
     * 項番2-2-51 Java6.0
     */
    public void testGetActiveRules_2_2_50()
        throws Exception
    {
        String params[] =
                          {
                                  "COD.MTRC.METHOD_CPU",
                                  "FALSE",
                                  "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCpuUsageRule",
                                  "INFO", "TRUE", "0", "0", "WARN", "TRUE", "0", "5000", "ERROR",
                                  "TRUE", "100", "20000"};

        String params2[] =
                           {
                                   "COD.THRD.WAIT_TIME",
                                   "FALSE",
                                   "jp.co.acroquest.endosnipe.perfdoctor.rule.code.ThreadWaitTimeRule",
                                   "INFO", "TRUE", "500", "2000", "WARN", "TRUE", "500", "60000",
                                   "ERROR", "TRUE", "500", "200000"};

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * 
     * 項番2-2-54
     */
    public void testGetActiveRules_2_2_54()
        throws Exception
    {
        String params[] =
                          {
                                  "COD.MTRC.METHOD_CPU",
                                  "TRUE",
                                  "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCpuUsageRule",
                                  "INFO", "TRUE", "0", "0", "WARN", "TRUE", "0", "5000", "ERROR",
                                  "TRUE", "100", "20000"};

        String params2[] =
                           {
                                   "COD.THRD.WAIT_TIME",
                                   "TRUE",
                                   "jp.co.acroquest.endosnipe.perfdoctor.rule.code.ThreadWaitTimeRule",
                                   "INFO", "FALSE", "500", "2000", "WARN", "TRUE", "500", "60000",
                                   "ERROR", "TRUE", "500", "200000"};

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * 
     * 項番2-2-56
     */
    public void testGetActiveRules_2_2_56()
        throws Exception
    {
        String params[] =
                          {
                                  "COD.MTRC.METHOD_CPU",
                                  "TRUE",
                                  "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCpuUsageRule",
                                  "INFO", "TRUE", "0", "0", "WARN", "TRUE", "0", "5000", "ERROR",
                                  "TRUE", "100", "20000"};

        String params2[] =
                           {
                                   "",
                                   "TRUE",
                                   "jp.co.acroquest.endosnipe.perfdoctor.rule.code.ThreadWaitTimeRule",
                                   "INFO", "TRUE", "500", "2000", "WARN", "TRUE", "500", "60000",
                                   "ERROR", "TRUE", "500", "200000"};

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * 
     * 項番2-2-65<br>
     * 項番2-2-66 Java6.0
     */
    public void testGetActiveRules_2_2_65()
        throws Exception
    {
        String params[] =
                          {
                                  "COD.MTRC.METHOD_CPU",
                                  "TRUE",
                                  "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCpuUsageRule",
                                  "INFO", "TRUE", "0", "0", "WARN", "TRUE", "0", "5000", "ERROR",
                                  "TRUE", "100", "20000"};

        String params2[] =
                           {
                                   "COD.THRD.WAIT_TIME",
                                   "TRUE",
                                   "jp.co.acroquest.endosnipe.perfdoctor.rule.code.ThreadWaitTimeRule",
                                   "INFO", "TRUE", "500", "2000", "WARN", "TRUE", "500", "60000",
                                   "ERROR", "TRUE", "duration", "200000"};

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * 
     * 項番2-2-69
     */
    public void testGetActiveRules_2_2_69()
        throws Exception
    {
        String params[] =
                          {
                                  "COD.MTRC.METHOD_CPU",
                                  "TRUE",
                                  "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCpuUsageRule",
                                  "INFO", "TRUE", "0", "0", "WARN", "TRUE", "0", "5000", "ERROR",
                                  "TRUE", "100", "20000"};

        String params2[] =
                           {
                                   "COD.THRD.WAIT_TIME",
                                   "TRUE",
                                   "jp.co.acroquest.endosnipe.perfdoctor.rule.code.ThreadWaitTimeRule",
                                   "INFO", "TRUE", "500", "2000", "WARN", "TRUE", "500", "60000",
                                   "ERROR", "TRUE", "", "200000"};

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * 
     * 項番2-2-71
     */
    public void testGetActiveRules_2_2_71()
        throws Exception
    {
        String params[] =
                          {
                                  "COD.MTRC.METHOD_CPU",
                                  "TRUE",
                                  "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCpuUsageRule",
                                  "INFO", "TRUE", "0", "0", "WARN", "TRUE", "0", "5000", "ERROR",
                                  "TRUE", "100", "20000"};

        String params2[] =
                           {
                                   "COD.THRD.WAIT_TIME",
                                   "TRUE",
                                   "jp.co.acroquest.endosnipe.perfdoctor.rule.code.ThreadWaitTimeRule",
                                   "INFO", "TRUE", "500", "2000", "WARN", "TRUE", "500", "60000",
                                   "ERROR", "TRUE", "duration", "200000"};

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * 
     * 項番2-2-72
     */
    public void testGetActiveRules_2_2_72()
        throws Exception
    {
        String params[] =
                          {
                                  "COD.MTRC.METHOD_CPU",
                                  "TRUE",
                                  "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCpuUsageRule",
                                  "INFO", "TRUE", "0", "0", "WARN", "TRUE", "0", "5000", "ERROR",
                                  "TRUE", "100", "20000"};

        String params2[] =
                           {
                                   "COD.THRD.WAIT_TIME",
                                   "TRUE",
                                   "jp.co.acroquest.endosnipe.perfdoctor.rule.code.ThreadWaitTimeRule",
                                   "INFO", "TRUE", "500", "2000", "WARN", "TRUE", "500", "60000",
                                   "ERROR", "TRUE", "", "200000"};

        // 試験実施
        mainGetActiveRules(params, params2);
    }

    /**
     * RuleSetDefの内容を比較する。
     * 一致するならtrueを返す。
     * 
     * @param ruleSetDef1 比較対象のRuleSetDef。
     * @param ruleSetDef2 比較対象のRuleSetDef。
     * 
     * @return 一致するかどうか。
     */
    private boolean compareRuleSetDef(final RuleSetDef ruleSetDef1, final RuleSetDef ruleSetDef2)
    {
        if (ruleSetDef1 == ruleSetDef2)
        {
            return true;
        }
        if (ruleSetDef1 == null || ruleSetDef2 == null)
        {
            return false;
        }

        List<RuleDef> ruleDefs1 = ruleSetDef1.getRuleDefs();
        List<RuleDef> ruleDefs2 = ruleSetDef2.getRuleDefs();
        int size1 = ruleDefs1.size();
        int size2 = ruleDefs2.size();

        if (size1 != size2)
        {
            return false;
        }

        for (int index = 0; index < size1; index++)
        {
            if (compareRuleDef(ruleDefs1.get(index), ruleDefs2.get(index)) == false)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * RuleDefの内容を比較する。
     * 一致するならtrueを返す。
     * 
     * @param ruleDef1 比較対象のRuleDef。
     * @param ruleDef2 比較対象のRuleDef。
     * 
     * @return 一致するかどうか。
     */
    private boolean compareRuleDef(final RuleDef ruleDef1, final RuleDef ruleDef2)
    {
        if (ruleDef1.getId().equals(ruleDef2.getId()) == false)
        {
            return false;
        }
        if (ruleDef1.getClassName().equals(ruleDef2.getClassName()) == false)
        {
            return false;
        }
        if (ruleDef1.getEnabled().equals(ruleDef2.getEnabled()) == false)
        {
            return false;
        }

        List<RuleLevelDef> ruleLevelDefs1 = ruleDef1.getRuleLevelDefs();
        List<RuleLevelDef> ruleLevelDefs2 = ruleDef2.getRuleLevelDefs();
        int size1 = ruleLevelDefs1.size();
        int size2 = ruleLevelDefs2.size();

        if (size1 != size2)
        {
            return false;
        }

        for (int index = 0; index < size1; index++)
        {
            if (compareRuleLevelDef(ruleLevelDefs1.get(index), ruleLevelDefs2.get(index)) == false)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * RuleLevelDefの内容を比較する。
     * 一致するならtrueを返す。
     * @param ruleLevelDef1 比較対象のRuleLevelDef。
     * @param ruleLevelDef2 比較対象のRuleLevelDef。
     * 
     * @return 一致するかどうか。
     */
    private boolean compareRuleLevelDef(final RuleLevelDef ruleLevelDef1,
            final RuleLevelDef ruleLevelDef2)
    {
        if (ruleLevelDef1.getEnabled().equals(ruleLevelDef2.getEnabled()) == false)
        {
            return false;
        }
        if (ruleLevelDef1.getLevel().equals(ruleLevelDef2.getLevel()) == false)
        {
            return false;
        }

        List<PropertyDef> propertyDefs1 = ruleLevelDef1.getPropertyDefs();
        List<PropertyDef> propertyDefs2 = ruleLevelDef2.getPropertyDefs();
        int size1 = propertyDefs1.size();
        int size2 = propertyDefs2.size();

        if (size1 != size2)
        {
            return false;
        }

        for (int index = 0; index < size1; index++)
        {
            if (comparePropertyDef(propertyDefs1.get(index), propertyDefs2.get(index)) == false)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * PropertyDefの内容を比較する。
     * 一致するならtrueを返す。
     * 
     * @param propertyDef1 比較対象のPropertyDef。
     * @param propertyDef2 比較対象のPropertyDef。
     * 
     * @return 一致するかどうか。
     */
    private boolean comparePropertyDef(final PropertyDef propertyDef1,
            final PropertyDef propertyDef2)
    {
        boolean areEqual = true;
        if (propertyDef1.getValue().equals(propertyDef2.getValue()) == false)
        {
            areEqual = false;
        }
        return areEqual;
    }

    /**
     * PerformanceRuleFacadeの内容を比較する。
     * 一致するならtrueを返す。
     * 
     * @param facade1 比較対象のPerformanceRuleFacade。
     * @param facade2 比較対象のPerformanceRuleFacade。
     * 
     * @return 一致するかどうか。
     */
    private boolean compareRuleFacade(final PerformanceRuleFacade facade1,
            final PerformanceRuleFacade facade2)
    {
        if (compareRule(getInfoRule(facade1), getInfoRule(facade2)) == false)
        {
            return false;
        }
        if (compareRule(getWarnRule(facade1), getWarnRule(facade2)) == false)
        {
            return false;
        }
        if (compareRule(getErrorRule(facade1), getErrorRule(facade2)) == false)
        {
            return false;
        }

        return true;
    }

    /**
     * PerformanceRuleの内容を比較する。
     * 一致するならtrueを返す。
     * 
     * @param rule1 比較対象のPerformanceRule。
     * @param rule2 比較対象のPerformanceRule。
     * 
     * @return 一致するかどうか。
     */
    private boolean compareRule(final PerformanceRule rule1, final PerformanceRule rule2)
    {
        if (rule1 == rule2)
        {
            return true;
        }

        if (rule1 == null || rule2 == null)
        {
            return false;
        }

        if (rule1.getClass() != rule2.getClass())
        {
            return false;
        }

        AbstractRule abRule1 = ((AbstractRule)rule1);
        AbstractRule abRule2 = ((AbstractRule)rule2);

        if (abRule1.active != abRule2.active)
        {
            return false;
        }
        if (abRule1.id.equals(abRule2.id) == false)
        {
            return false;
        }
        if (abRule1.durationThreshold != abRule2.durationThreshold)
        {
            return false;
        }
        if (abRule1.level.equals(abRule2.level) == false)
        {
            return false;
        }

        return true;
    }

    /**
     * PerformanceRuleFacadeからINFOのルールを取得する。
     * 
     * @param facade1 取得元のPerformanceRuleFacade。
     * 
     * @return INFOのルール。
     */
    private PerformanceRule getInfoRule(final PerformanceRuleFacade facade1)
    {
        return getRule(facade1, "infoRule_");
    }

    /**
     * PerformanceRuleFacadeからWARNのルールを取得する。
     * 
     * @param facade1 取得元のPerformanceRuleFacade。
     * 
     * @return WARNのルール。
     */
    private PerformanceRule getWarnRule(final PerformanceRuleFacade facade1)
    {
        return getRule(facade1, "warnRule_");
    }

    /**
     * PerformanceRuleFacadeからERRORのルールを取得する。
     * 
     * @param facade1 取得元のPerformanceRuleFacade。
     * 
     * @return ERRORのルール。
     */
    private PerformanceRule getErrorRule(final PerformanceRuleFacade facade1)
    {
        return getRule(facade1, "errorRule_");
    }

    /**
     * ルールのフィールド名を指定し、PerformanceRuleFacadeからルールを取得する。
     * 
     * 
     * @param performanceRuleFacade 取得元のPerformanceRuleFacade。
     * @param fieldName ルールのフィールド名。
     * 
     * @return ルール。
     */
    private PerformanceRule getRule(final PerformanceRuleFacade performanceRuleFacade,
            final String fieldName)
    {
        return (PerformanceRule)getField(performanceRuleFacade, fieldName);
    }

    /**
     * フィールドを取得する。
     * @param performanceRuleFacade 取得元のPerformanceRuleFacade。
     * @param fieldName フィールド名。
     * @return フィールドの値。
     */
    private Object getField(final PerformanceRuleFacade performanceRuleFacade,
            final String fieldName)
    {
        try
        {
            Field field = PerformanceRuleFacade.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(performanceRuleFacade);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * RuleSetDefを試験するためのcreator
     * 
     * @return RuleSetDef。
     */
    private RuleSetDef createRuleSetDef_Data1()
    {
        String id = "COD.MTRC.METHOD_TAT";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodTatRule";
        String enabled = "TRUE";
        String[] infoLevelDefArgs = {"INFO", "FALSE", "0", "0"};
        String[] warnLevelDefArgs = {"WARN", "TRUE", "0", "5000"};
        String[] errorLevelDefArgs = {"ERROR", "FALSE", "0", "0"};
        RuleDef ruleDef =
                          createRuleDef(id, className, enabled, infoLevelDefArgs, warnLevelDefArgs,
                                        errorLevelDefArgs);

        String id2 = "COD.MTRC.METHOD_CNT";
        String className2 = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCallCountRule";
        String enabled2 = "TRUE";
        String[] infoLevelDefArgs2 = {"INFO", "TRUE", "100", "0"};
        String[] warnLevelDefArgs2 = {"WARN", "TRUE", "100", "5000"};
        String[] errorLevelDefArgs2 = {"ERROR", "TRUE", "100", "20000"};
        RuleDef ruleDef2 =
                           createRuleDef(id2, className2, enabled2, infoLevelDefArgs2,
                                         warnLevelDefArgs2, errorLevelDefArgs2);

        // ルールセット一つ目
        List<RuleDef> ruleDefs = new ArrayList<RuleDef>();
        ruleDefs.add(ruleDef);
        ruleDefs.add(ruleDef2);

        RuleSetDef ruleSetDef = new RuleSetDef();
        ruleSetDef.setRuleDefs(ruleDefs);
        ruleSetDef.setName("setA");

        return ruleSetDef;
    }

    /**
     * RuleSetDefを試験するためのcreator
     * 
     * @return RuleSetDef。
     */
    private RuleSetDef createRuleSetDef_Data2()
    {
        String id = "COD.MTRC.METHOD_CPU";
        String className = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.MethodCpuUsageRule";
        String enabled = "TRUE";
        String[] infoLevelDefArgs = {"INFO", "FALSE", "0", "0"};
        String[] warnLevelDefArgs = {"WARN", "TRUE", "0", "5000"};
        String[] errorLevelDefArgs = {"ERROR", "FALSE", "0", "20000"};
        RuleDef ruleDef =
                          createRuleDef(id, className, enabled, infoLevelDefArgs, warnLevelDefArgs,
                                        errorLevelDefArgs);

        String id2 = "COD.THRD.WAIT_TIME";
        String className2 = "jp.co.acroquest.endosnipe.perfdoctor.rule.code.ThreadWaitTimeRule";
        String enabled2 = "FALSE";
        String[] infoLevelDefArgs2 = {"INFO", "FALSE", "500", "2000"};
        String[] warnLevelDefArgs2 = {"WARN", "TRUE", "500", "60000"};
        String[] errorLevelDefArgs2 = {"ERROR", "FALSE", "500", "200000"};
        RuleDef ruleDef2 =
                           createRuleDef(id2, className2, enabled2, infoLevelDefArgs2,
                                         warnLevelDefArgs2, errorLevelDefArgs2);

        // ルールセットの生成        
        List<RuleDef> ruleDefs = new ArrayList<RuleDef>();
        ruleDefs.add(ruleDef);
        ruleDefs.add(ruleDef2);

        RuleSetDef ruleSetDef = new RuleSetDef();
        ruleSetDef.setRuleDefs(ruleDefs);
        ruleSetDef.setName("setB");

        return ruleSetDef;
    }

    /**
     * 指定したパラメータでPerformanceRuleFacadeを生成する。
     * 
     * @param id ID。
     * @param infoLevelArgs INFOレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @param warnLevelArgs WARNレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @param errorLevelArgs ERRORレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @return PerformanceRuleFacade。
     */
    private PerformanceRuleFacade createRuleFacade(final String id, final String[] infoLevelArgs,
            final String[] warnLevelArgs, final String[] errorLevelArgs)
    {
        AbstractRule infoRule = new MethodCallCountRule();
        infoRule.id = id;
        infoRule.level = infoLevelArgs[0];
        infoRule.active = Boolean.valueOf(infoLevelArgs[1]);
        infoRule.durationThreshold = Integer.parseInt(infoLevelArgs[2]);
        ((MethodCallCountRule)infoRule).threshold = Integer.parseInt(infoLevelArgs[3]);

        AbstractRule warnRule = new MethodCallCountRule();
        warnRule.id = id;
        warnRule.level = warnLevelArgs[0];
        warnRule.active = Boolean.valueOf(warnLevelArgs[1]);
        warnRule.durationThreshold = Integer.parseInt(warnLevelArgs[2]);
        ((MethodCallCountRule)warnRule).threshold = Integer.parseInt(warnLevelArgs[3]);

        AbstractRule errorRule = new MethodCallCountRule();
        errorRule.id = id;
        errorRule.level = errorLevelArgs[0];
        errorRule.active = Boolean.valueOf(errorLevelArgs[1]);
        try
        {
            errorRule.durationThreshold = Integer.parseInt(errorLevelArgs[2]);
        }
        catch (NumberFormatException nfe)
        {
            errorRule.durationThreshold = 0;
        }
        try
        {
            ((MethodCallCountRule)errorRule).threshold = Integer.parseInt(errorLevelArgs[3]);
        }
        catch (NumberFormatException nfe)
        {
            ((MethodCallCountRule)errorRule).threshold = 0;
        }

        PerformanceRuleFacade facade = new PerformanceRuleFacade();
        if (infoRule.active)
        {
            facade.setInfoRule(infoRule);
        }
        if (warnRule.active)
        {
            facade.setWarnRule(warnRule);
        }

        if (errorRule.active)
        {
            facade.setErrorRule(errorRule);
        }

        return facade;
    }

    /**
     * 指定したパラメータでRuleLevelDefを生成する。
     * 
     * @param level レベル
     * @param enabled 有効・無効。
     * @param durationThreshold durationThresholdプロパティ。
     * @param thresholdValue thresholdプロパティ。
     * 
     * @return RuleLevelDef。
     */
    private RuleLevelDef createRunLevelDef(final String level, final String enabled,
            final String durationThreshold, final String thresholdValue)
    {
        PropertyDef propertyDefInfoThreshold = new PropertyDef();
        propertyDefInfoThreshold.setName("threshold");
        propertyDefInfoThreshold.setValue(thresholdValue);

        List<PropertyDef> propertyDefsInfo = new ArrayList<PropertyDef>();
        propertyDefsInfo.add(propertyDefInfoThreshold);

        RuleLevelDef infoLevelDef = new RuleLevelDef();
        infoLevelDef.setPropertyDefs(propertyDefsInfo);
        infoLevelDef.setLevel(level);
        infoLevelDef.setEnabled(enabled);
        infoLevelDef.setDurationThreshold(durationThreshold);
        return infoLevelDef;
    }

    /**
     * 指定したパラメータでRuleDefを生成する。
     * 
     * @param id ID。
     * @param enabled 有効・無効。
     * @param className クラス名。
     * @param infoLevelDefArgs INFOレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @param warnLevelDefArgs WARNレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @param errorLevelDefArgs ERRORレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @return RuleDef。
     */
    private RuleDef createRuleDef(final String id, final String className, final String enabled,
            final String[] infoLevelDefArgs, final String[] warnLevelDefArgs,
            final String[] errorLevelDefArgs)
    {
        RuleLevelDef infoLevelDef =
                                    createRunLevelDef(infoLevelDefArgs[0], infoLevelDefArgs[1],
                                                      infoLevelDefArgs[2], infoLevelDefArgs[3]);
        RuleLevelDef warnLevelDef =
                                    createRunLevelDef(warnLevelDefArgs[0], warnLevelDefArgs[1],
                                                      warnLevelDefArgs[2], warnLevelDefArgs[3]);
        RuleLevelDef errorLevelDef =
                                     createRunLevelDef(errorLevelDefArgs[0], errorLevelDefArgs[1],
                                                       errorLevelDefArgs[2], errorLevelDefArgs[3]);

        List<RuleLevelDef> ruleLevelDefs = new ArrayList<RuleLevelDef>();
        ruleLevelDefs.add(infoLevelDef);
        ruleLevelDefs.add(warnLevelDef);
        ruleLevelDefs.add(errorLevelDef);

        RuleDef ruleDef2 = new RuleDef();
        ruleDef2.setRuleLevelDefs(ruleLevelDefs);
        ruleDef2.setEnabled(enabled);
        ruleDef2.setId(id);
        ruleDef2.setClassName(className);
        return ruleDef2;
    }

    /**
     * RuleManagerにRuleSetDefを設定する。
     * 
     * @param ruleManager 設定対象のRuleManager。
     * @param id 
     * @param ruleSetDef  RuleManagerに設定するruleSetDef。
     * @throws Exception
     */
    private void setRuleSetDef(final RuleManager ruleManager, final String id,
            final RuleSetDef ruleSetDef)
        throws Exception
    {
        Field ruleSetMapField = RuleManager.class.getDeclaredField("ruleSetMap_");
        ruleSetMapField.setAccessible(true);
        Map<String, RuleSetDef> ruleSetMap;
        ruleSetMap = (Map<String, RuleSetDef>)ruleSetMapField.get(ruleManager);
        ruleSetMap.put(id, ruleSetDef);
    }

    /**
     * RuleManagerを初期化する。
     * 
     * @return 初期化したRuleManager。
     */
    private RuleManager initRuleManager()
    {
        MockObjectManager.initialize();
        MockObjectManager.addReturnValue(RulePreferenceUtil.class, "loadRuleSetIds",
                                         new String[]{"setA"});
        MockObjectManager.addReturnValue(RulePreferenceUtil.class, "loadRuleSet",
                                         new RuleSetConfig());
        MockObjectManager.addReturnValue(RulePreferenceUtil.class, "loadActiveRuleSetId", "setA");

        try
        {
            Field ruleManagerField = RuleManager.class.getDeclaredField("instance__");
            ruleManagerField.setAccessible(true);
            ruleManagerField.set(null, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        RuleManager ruleManager = RuleManager.getInstance();
        return ruleManager;
    }

    /**
     * getActiveRulesの試験を行う。
     * 
     * @param params Ruleの内容(ID、enabled、クラス名、(レベル、enabled、durationThreshold、threshold)×3)
     * @param params2 Ruleの内容(ID、enabled、クラス名、(レベル、enabled、durationThreshold、threshold)×3)
     * @throws Exception
     */
    private void mainGetActiveRules(final String[] params, final String[] params2)
        throws Exception
    {
        // パラメータ指定
        RuleDef ruleDef = null;
        if (params != null)
        {
            String id = params[0];
            String enabled = params[1];
            String className = params[2];
            String[] infoLevelArgs = new String[]{params[3], params[4], params[5], params[6]};
            String[] warnLevelArgs = new String[]{params[7], params[8], params[9], params[10]};
            String[] errorLevelArgs = new String[]{params[11], params[12], params[13], params[14]};
            ruleDef = createRuleDef(id, className, enabled, //
                                    infoLevelArgs, //
                                    warnLevelArgs, // 
                                    errorLevelArgs);
        }

        RuleDef ruleDef2 = null;
        if (params2 != null)
        {
            String id2 = params2[0];
            String enabled2 = params2[1];
            String className2 = params2[2];
            String[] infoLevelArgs2 = new String[]{params2[3], params2[4], params2[5], params2[6]};
            String[] warnLevelArgs2 = new String[]{params2[7], params2[8], params2[9], params2[10]};
            String[] errorLevelArgs2 =
                                       new String[]{params2[11], params2[12], params2[13],
                                               params2[14]};

            ruleDef2 = createRuleDef(id2, className2, enabled2, //
                                     infoLevelArgs2, //
                                     warnLevelArgs2, // 
                                     errorLevelArgs2);
        }

        // RuleSetDefインスタンスの作成
        RuleSetDef ruleSetDef = new RuleSetDef();
        List<RuleDef> ruleDefs = new ArrayList<RuleDef>();
        if (ruleDef != null)
        {
            ruleDefs.add(ruleDef);
        }
        if (ruleDef2 != null)
        {
            ruleDefs.add(ruleDef2);
        }
        ruleSetDef.setRuleDefs(ruleDefs);
        ruleSetDef.setName("setA");

        // RuleMangerインスタンスの生成
        RuleManager ruleManager = initRuleManager();

        // RuleMangerへのルールの設定
        setRuleSetDef(ruleManager, "setA", ruleSetDef);
        RuleSetConfig config = new RuleSetConfig();
        config.setId("setA");
        config.setName("newRuleAName");
        ruleManager.addRuleSetConfig(config);
        ruleManager.setActiveRuleSetConfig(config);

        List<PerformanceRule> expectedRules = new ArrayList<PerformanceRule>();
        for (RuleDef currentRuleDef : ruleDefs)
        {
            try
            {
                if (currentRuleDef.getEnabled().equals("TRUE"))
                {
                    PerformanceRule createRuleFacade;
                    createRuleFacade = RuleInstanceUtil.createRuleFacade(currentRuleDef);
                    expectedRules.add(createRuleFacade);
                }
            }
            catch (RuleCreateException rce)
            {
                // 何もしない。
                rce.printStackTrace();
            }
        }

        // テスト対象の実行
        List<PerformanceRule> actualRules = ruleManager.getActiveRules();

        // 結果確認
        assertEquals(expectedRules.size(), actualRules.size());
        for (int index = 0; index < expectedRules.size(); index++)
        {
            PerformanceRule actualRule = actualRules.get(index);
            assertTrue(compareRuleFacade((PerformanceRuleFacade)expectedRules.get(index),
                                         (PerformanceRuleFacade)actualRule));
        }
    }

    /**
     * CreateRuleInstanceFromRuleDef(正常)の試験を実施する。
     * 
     * @param id ID。
     * @param enabled 有効・無効。
     * @param className クラス名。
     * @param infoLevelArgs INFOレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @param warnLevelArgs WARNレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @param errorLevelArgs ERRORレベルの内容(レベル、enabled、durationThreshold、threshold)。
     */
    private void mainCreateRuleInstanceFromRuleDef(final String id, final String enabled,
            final String className, final String[] infoLevelArgs, final String[] warnLevelArgs,
            final String[] errorLevelArgs)
    {
        // RuleDefインスタンスの作成
        RuleDef ruleDef = createRuleDef(id, className, enabled, //
                                        infoLevelArgs, //
                                        warnLevelArgs, // 
                                        errorLevelArgs);

        // RuleFacadeクラスの作成
        PerformanceRuleFacade expectedFacade =
                                               createRuleFacade(id, infoLevelArgs, warnLevelArgs,
                                                                errorLevelArgs);
        try
        {
            PerformanceRule actualFacade = RuleInstanceUtil.createRuleFacade(ruleDef);
            assertTrue(compareRuleFacade(expectedFacade, (PerformanceRuleFacade)actualFacade));
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            fail();
        }
    }

    /**
     * CreateRuleInstanceFromRuleDef(戻り値null)の試験を実施する。
     * 
     * @param id ID。
     * @param enabled 有効・無効。
     * @param className クラス名。
     * @param infoLevelArgs INFOレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @param warnLevelArgs WARNレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @param errorLevelArgs ERRORレベルの内容(レベル、enabled、durationThreshold、threshold)。
     */
    private void mainCreateRuleInstanceFromRuleDef_Null(final String id, final String enabled,
            final String className, final String[] infoLevelArgs, final String[] warnLevelArgs,
            final String[] errorLevelArgs)
    {
        // RuleDefインスタンスの作成
        RuleDef ruleDef = createRuleDef(id, className, enabled, //
                                        infoLevelArgs, //
                                        warnLevelArgs, // 
                                        errorLevelArgs);

        // RuleFacadeクラスの作成
        try
        {
            PerformanceRule actualFacade = RuleInstanceUtil.createRuleFacade(ruleDef);
            assertNull(actualFacade);
        }
        catch (RuleCreateException ex)
        {
            ex.printStackTrace();
            fail();
        }
    }

    /**
     * CreateRuleInstanceFromRuleDef(RuleCreateException発生)の試験を実施する。
     * 
     * @param id ID。
     * @param enabled 有効・無効。
     * @param className クラス名。
     * @param infoLevelArgs INFOレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @param warnLevelArgs WARNレベルの内容(レベル、enabled、durationThreshold、threshold)。
     * @param errorLevelArgs ERRORレベルの内容(レベル、enabled、durationThreshold、threshold)。
     */
    private void mainCreateRuleInstanceFromRuleDef_Fail(final String id, final String enabled,
            final String className, final String[] infoLevelArgs, final String[] warnLevelArgs,
            final String[] errorLevelArgs)
    {
        // RuleDefインスタンスの作成
        RuleDef ruleDef = createRuleDef(id, className, enabled, //
                                        infoLevelArgs, //
                                        warnLevelArgs, // 
                                        errorLevelArgs);

        try
        {
            PerformanceRule actualFacade = RuleInstanceUtil.createRuleFacade(ruleDef);

            // 実行結果の作成
            PerformanceRuleFacade expectedFacade =
                                                   createRuleFacade(id, infoLevelArgs,
                                                                    warnLevelArgs, errorLevelArgs);

            assertTrue(compareRuleFacade(expectedFacade, (PerformanceRuleFacade)actualFacade));
        }
        catch (RuleCreateException ex)
        {
            return;
        }
        fail();
    }

    private String getResourcePath(final String fileName)
    {
        URL resourceUrl = PerformanceDoctorTest.class.getResource(fileName);
        return resourceUrl.getFile();
    }

}