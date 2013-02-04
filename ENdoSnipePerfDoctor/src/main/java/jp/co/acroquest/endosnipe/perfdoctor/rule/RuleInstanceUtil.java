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
package jp.co.acroquest.endosnipe.perfdoctor.rule;

import java.lang.reflect.Field;
import java.util.List;

import jp.co.acroquest.endosnipe.perfdoctor.PerfConstants;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRule;
import jp.co.acroquest.endosnipe.perfdoctor.exception.RuleCreateException;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.PropertyDef;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleDef;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleLevelDef;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;

/**
 * ルールのインスタンス生成を行うユーティリティクラス。
 * @author tanimoto
 *
 */
public class RuleInstanceUtil
{
    private static final String ERROR = "ERROR";

    private static final String WARN  = "WARN";

    private static final String INFO  = "INFO";

    /**
     * ルールのインスタンスを作成し、プロパティを設定する。<br>
     * 定義されたレベルの数だけインスタンスを作成する。
     * ruleDef中にあるルール名が不正であるために、
     * インスタンス生成に失敗した場合には、RuleCreateExceptionをスローする。
     * @param ruleDef ルール定義
     * @return ルールのインスタンス
     * @throws RuleCreateException ルールのインスタンス作成か、値の設定に失敗した場合に発生する。
     */
    public static PerformanceRule createRuleInstance(final RuleDef ruleDef)
        throws RuleCreateException
    {
        if (checkEnabled(ruleDef.getEnabled()) == false)
        {
            return null;
        }

        List<RuleLevelDef> ruleLevelDefs = ruleDef.getRuleLevelDefs();
        if (ruleLevelDefs == null)
        {
            return null;
        }

        PerformanceRule rule = createRuleFacade(ruleDef);

        return rule;
    }

    /**
     * ルールのファサードを作成する。<br>
     * @param ruleDef ルール定義
     * @return ルールのファサード
     * @throws RuleCreateException ルールのインスタンス作成か、値の設定に失敗した場合に発生する。
     */
    protected static PerformanceRule createRuleFacade(final RuleDef ruleDef)
        throws RuleCreateException
    {
        PerformanceRuleFacade ruleFacade = new PerformanceRuleFacade();

        List<RuleLevelDef> ruleLevelDefs = ruleDef.getRuleLevelDefs();
        for (RuleLevelDef ruleLevelDef : ruleLevelDefs)
        {
            if (checkEnabled(ruleLevelDef.getEnabled()) == false)
            {
                continue;
            }

            PerformanceRule rule = createNewInstance(ruleDef.getClassName());
            String level = ruleLevelDef.getLevel();
            if (ERROR.equalsIgnoreCase(level))
            {
                ruleFacade.setErrorRule(rule);
            }
            else if (WARN.equalsIgnoreCase(level))
            {
                ruleFacade.setWarnRule(rule);
            }
            else if (INFO.equalsIgnoreCase(level))
            {
                ruleFacade.setInfoRule(rule);
            }
            else
            {
                continue;
            }

            setValue(rule, "id", ruleDef.getId());
            setValue(rule, "active", "true");
            setValue(rule, "level", level.toUpperCase());
            setValue(rule, "durationThreshold", ruleLevelDef.getDurationThreshold());

            if (ruleLevelDef.getPropertyDefs() != null)
            {
                for (PropertyDef propertyDef : ruleLevelDef.getPropertyDefs())
                {
                    setValue(rule, propertyDef.getName(), propertyDef.getValue());
                }
            }

            rule.init();
        }
        return ruleFacade;
    }

    /**
     * 引数の値が「true」かどうかを判定する。<br>
     * org.apache.commons.beanUtils.ConvertUtilsのデフォルト変換ルールに従って、<br>
     * 文字列をBooleanに変換し、その結果を返す。<br>
     * ただし、デフォルト値をtrueとして判断するため、引数の値が空かnullの場合もtrueを返す。<br>
     * @param value 文字列
     * @return 文字列がtrueかどうかを返す。
     */
    protected static boolean checkEnabled(final String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return true;
        }

        Boolean b = (Boolean)ConvertUtils.convert(value, Boolean.TYPE);
        return b.booleanValue();
    }

    /**
     * ルールクラスの名前からルールのインスタンスを生成する。
     * クラスが見つからない、引数が表すクラスがルールクラスでないなどの理由で
     * 例外が発生した場合には、RuleCreateExceptionをスローする。
     * @param className クラス名
     * @return ルールのインスタンス
     * @throws RuleCreateException インスタンスの生成に失敗した場合に発生する。
     */
    protected static PerformanceRule createNewInstance(final String className)
        throws RuleCreateException
    {
        PerformanceRule rule;
        try
        {
            Class clazz = Class.forName(className);
            // PerformanceRuleのインスタンスでなければ、ClassCastExceptionと同様の処理を行う。
            if (PerformanceRule.class.isAssignableFrom(clazz) == false)
            {
                throw new RuleCreateException(PerfConstants.CLASS_TYPE_ERROR,
                                              new Object[]{className});
            }
            rule = (PerformanceRule)clazz.newInstance();
        }
        catch (ClassNotFoundException ex)
        {
            throw new RuleCreateException(PerfConstants.CLASS_NOT_FOUND, new Object[]{className});
        }
        catch (InstantiationException ex)
        {
            throw new RuleCreateException(PerfConstants.NEW_INSTANCE_ERROR, new Object[]{className});
        }
        catch (IllegalAccessException ex)
        {
            throw new RuleCreateException(PerfConstants.NEW_INSTANCE_ERROR, new Object[]{className});
        }
        catch (ClassCastException ex)
        {
            throw new RuleCreateException(PerfConstants.CLASS_TYPE_ERROR, new Object[]{className});
        }

        return rule;
    }

    /**
     * 値の設定を行う。
     * @param obj 設定対象のオブジェクト
     * @param fieldName フィールド名
     * @param value 値
     * @throws RuleCreateException 値の設定に失敗した場合に発生する。
     */
    protected static void setValue(final Object obj, final String fieldName, final String value)
        throws RuleCreateException
    {
        Class clazz = obj.getClass();
        Object[] args = new Object[]{clazz.getCanonicalName(), fieldName, value};

        // TODO: クラスキャッシュ、フィールドキャッシュの検討
        try
        {
            Field field = clazz.getField(fieldName);

            Object convertedValue = ConvertUtils.convert(value, field.getType());
            field.set(obj, convertedValue);
        }
        catch (NoSuchFieldException ex)
        {
            throw new RuleCreateException(PerfConstants.PROPERTY_NOT_FOUND, args);
        }
        catch (SecurityException ex)
        {
            throw new RuleCreateException(PerfConstants.PROPERTY_ERROR, args);
        }
        catch (IllegalArgumentException ex)
        {
            throw new RuleCreateException(PerfConstants.PROPERTY_TYPE_ERROR, args);
        }
        catch (IllegalAccessException ex)
        {
            throw new RuleCreateException(PerfConstants.PROPERTY_ACCESS_ERROR, args);
        }
    }

    /**
     * レベルが選択肢(ERROR、WARN、INFO)に含まれているかどうか確認する。
     * @param level レベル
     * @return 選択肢に含まれていればtrue、そうでない場合はfalseを返す。
     */
    protected static boolean isValidLevel(final String level)
    {
        if (PerfConstants.LEVEL_ERROR.equalsIgnoreCase(level)
                || PerfConstants.LEVEL_INFO.equalsIgnoreCase(level)
                || PerfConstants.LEVEL_WARN.equalsIgnoreCase(level))
        {
            return true;
        }

        return false;
    }

    private RuleInstanceUtil()
    {
        // Do Nothing.
    }
}
