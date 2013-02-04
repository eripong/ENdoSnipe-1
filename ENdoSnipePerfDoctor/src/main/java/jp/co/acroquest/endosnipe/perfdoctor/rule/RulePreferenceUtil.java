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

import java.util.HashMap;
import java.util.Map;

import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleSetConfig;

import org.apache.commons.lang.StringUtils;

/**
 * Eclipseよりルール設定を取得するユーティリティクラス。
 * TODO 設定の永続化
 * 
 * @author tanimoto
 *
 */
public class RulePreferenceUtil
{
    /** ルールセットのIDを表す項目の名前。 */
    private static final String        CONFIG_RULESET_IDS         = "perfdoctor.ruleSetIds";

    /** ルールセット名を表す項目の名前。 */
    private static final String        CONFIG_RULESET_NAME_PREFIX = "perfdoctor.ruleSetName_";

    /** ルールセット定義ファイルの名前を表す項目の名前。 */
    private static final String        CONFIG_RULESET_FILE_PREFIX = "perfdoctor.ruleSetFile_";

    /** 有効なルールセットを表す項目の名前。 */
    private static final String        CONFIG_ACTIVE_RULESET_ID   = "perfdoctor.activeRuleSetId";

    private static Map<String, String> preferenceMap__            = new HashMap<String, String>();

    /**
     * プリファレンスストアからルールセット定義(RuleSetConfigインスタンス)を取得する。
     * @param ruleSetId ルールセットID
     * @return ルールセット定義。定義が見つからない場合でも、ルールセット定義を返す。
     */
    public static RuleSetConfig loadRuleSet(final String ruleSetId)
    {
        String name = preferenceMap__.get(CONFIG_RULESET_NAME_PREFIX + ruleSetId);
        String fileName = preferenceMap__.get(CONFIG_RULESET_FILE_PREFIX + ruleSetId);

        RuleSetConfig config = new RuleSetConfig();
        config.setId(ruleSetId);
        config.setName(name);
        config.setFileName(fileName);

        return config;
    }

    /**
     * プリファレンスストアにルールセット定義を保存する。
     * @param config ルールセット定義
     */
    public static void saveRuleSet(final RuleSetConfig config)
    {
        String id = config.getId();

        preferenceMap__.put(CONFIG_RULESET_NAME_PREFIX + id, config.getName());
        preferenceMap__.put(CONFIG_RULESET_FILE_PREFIX + id, config.getFileName());
    }

    /**
     * プリファレンスストアからルールセットID一覧を取得する。
     * @return ルールセットID一覧。見つからない場合は、長さ0の配列を返す。
     */
    public static String[] loadRuleSetIds()
    {
        String ids = preferenceMap__.get(CONFIG_RULESET_IDS);

        if (ids == null || ids.length() == 0)
        {
            return new String[0];
        }

        String[] ruleIds = StringUtils.split(ids, ",");
        return ruleIds;
    }

    /**
     * プリファレンスストアにルールセットID一覧を保存する。
     * @param ruleSetIds ルールセットID一覧
     */
    public static void saveRuleSetIds(final String[] ruleSetIds)
    {
        String ruleIds = StringUtils.join(ruleSetIds, ",");
        preferenceMap__.put(CONFIG_RULESET_IDS, ruleIds);
    }

    /**
     * プリファレンスストアからアクティブなルールセットIDを取得する。
     * @return ルールセットID。見つからない場合は、空の文字列を返す。
     */
    public static String loadActiveRuleSetId()
    {
        String str = preferenceMap__.get(CONFIG_ACTIVE_RULESET_ID);

        return str;
    }

    /**
     * プリファレンスストアにアクティブなルールセットIDを保存する。
     * @param ruleSetId ルールセットID
     */
    public static void saveActiveRuleSetId(final String ruleSetId)
    {
        preferenceMap__.put(CONFIG_ACTIVE_RULESET_ID, ruleSetId);
    }

    private RulePreferenceUtil()
    {
        // Do Nothing.
    }
}
