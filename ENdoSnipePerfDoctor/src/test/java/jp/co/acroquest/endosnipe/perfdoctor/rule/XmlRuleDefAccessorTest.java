package jp.co.acroquest.endosnipe.perfdoctor.rule;

import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.rule.def.PropertyDef;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleDef;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleLevelDef;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleSetDef;
import junit.framework.TestCase;

public class XmlRuleDefAccessorTest extends TestCase
{
    XmlRuleDefAccessor accessor_ = new XmlRuleDefAccessor();

    public void testFindRuleSet()
        throws Exception
    {
        RuleSetDef def = this.accessor_.findRuleSet("/testFindRuleSet.xml");

        assertNotNull(def);

        assertEquals("testFindRuleSet", def.getName());

        // 1‚Â–Ú‚Ì’è‹`
        RuleDef ruleDef1 = def.getRuleDefs().get(0);
        assertEquals("TEST01_NORMAL", ruleDef1.getId());
        assertEquals("jp.co.acroquest.endosnipe.perfdoctor.rule.Test01", ruleDef1.getClassName());
        assertEquals("FALSE", ruleDef1.getEnabled());

        List<RuleLevelDef> ruleLevelDefs = ruleDef1.getRuleLevelDefs();

        assertEquals("INFO", ruleLevelDefs.get(0).getLevel());
        assertEquals("100", ruleLevelDefs.get(0).getDurationThreshold());
        assertEquals("TRUE", ruleLevelDefs.get(0).getEnabled());

        PropertyDef propertyDef1 = ruleLevelDefs.get(0).getPropertyDefs().get(0);
        assertEquals("PARAM01", propertyDef1.getName());
        assertEquals("\"aaa\"", propertyDef1.getValue());

        PropertyDef propertyDef2 = ruleLevelDefs.get(0).getPropertyDefs().get(1);
        assertEquals("PARAM02", propertyDef2.getName());
        assertEquals("123", propertyDef2.getValue());

        assertEquals("ERROR", ruleLevelDefs.get(1).getLevel());
        assertEquals("0", ruleLevelDefs.get(1).getDurationThreshold());
        assertEquals("FALSE", ruleLevelDefs.get(1).getEnabled());
        assertNull(ruleLevelDefs.get(1).getPropertyDefs());

        // 2‚Â–Ú‚Ì’è‹`
        RuleDef ruleDef2 = def.getRuleDefs().get(1);
        assertEquals("TEST02_USE_DEFAULT", ruleDef2.getId());
        assertEquals("jp.co.acroquest.endosnipe.perfdoctor.rule.Test02", ruleDef2.getClassName());
        assertNull(ruleDef2.getEnabled());
        assertNull(ruleDef2.getRuleLevelDefs());
    }
}