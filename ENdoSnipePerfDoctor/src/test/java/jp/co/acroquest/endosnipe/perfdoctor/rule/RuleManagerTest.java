package jp.co.acroquest.endosnipe.perfdoctor.rule;

import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRule;
import junit.framework.TestCase;

public class RuleManagerTest extends TestCase
{
    public void testGetActiveRules()
        throws Exception
    {
        List<PerformanceRule> ruleList = RuleManager.getInstance().getActiveRules();
        System.out.println(ruleList);
    }
}
