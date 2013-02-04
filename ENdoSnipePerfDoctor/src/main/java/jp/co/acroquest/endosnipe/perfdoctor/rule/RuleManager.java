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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.perfdoctor.Messages;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRule;
import jp.co.acroquest.endosnipe.perfdoctor.exception.RuleCreateException;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleDef;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleSetConfig;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleSetDef;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SerializationUtils;

/**
 * ルールの管理（追加、変更、削除、参照）を行うクラス。
 * @author tanimoto
 *
 */
public class RuleManager
{
    private static final ENdoSnipeLogger   LOGGER                     =
                                                                        ENdoSnipeLogger.getLogger(RuleManager.class,
                                                                                                  null);

    /** ルール定義ファイルの読み込みに失敗した際に投げられる例外に渡す文字列。 */
    private static final String            RULE_CREATE_ERROR          = "RuleCreateError";

    /** デフォルトのルールセットのID。 */
    public static final String             DEFAULT_RULESET_ID         = "PERFDOCTOR_DEFAULT";

    /** デフォルトのルールセットの名前。 */
    private static final String            DEFAULT_RULESET_NAME       =
                                                                        Messages.getMessage("endosnipe.perfdoctor.rule.RuleManager.DefaultRuleSetName");

    /** デフォルトのルールセットのファイル名。 */
    private static final String            DEFAULT_RULESET_FILE       = "/perfdoctor_rule.xml";

    /** Java用のデフォルトのルールセットのID。 */
    public static final String             DEFAULT_JAVA_RULESET_ID    = "PERFDOCTOR_JAVA_DEFAULT";

    /** Java用のデフォルトのルールセットの名前。 */
    private static final String            DEFAULT_JAVA_RULESET_NAME  =
                                                                        Messages.getMessage("endosnipe.perfdoctor.rule.RuleManager.DefaultJavaRuleSetName");

    /** Java用のデフォルトのルールセットのファイル名。*/
    private static final String            DEFAULT_JAVA_RULESET_FILE  = "/perfdoctor_Java_rule.xml";

    /** DB用のデフォルトのルールセットのID。 */
    public static final String             DEFAULT_DB_RULESET_ID      = "PERFDOCTOR_DB_DEFAULT";

    /** DB用のデフォルトのルールセットの名前。 */
    private static final String            DEFAULT_DB_RULESET_NAME    =
                                                                        Messages.getMessage("endosnipe.perfdoctor.rule.RuleManager.DefaultDBRuleSetName");

    /** DB用のデフォルトのルールセットのファイル名。*/
    private static final String            DEFAULT_DB_RULESET_FILE    = "/perfdoctor_DB_rule.xml";

    /** HP-UX用のデフォルトのルールセットのID。 */
    public static final String             DEFAULT_HP_UX_RULESET_ID   = "PERFDOCTOR_HP_UX_DEFAULT";

    /** HP-UX用のデフォルトのルールセットの名前。 */
    private static final String            DEFAULT_HP_UX_RULESET_NAME =
                                                                        Messages.getMessage("endosnipe.perfdoctor.rule.RuleManager.DefaultHPUXRuleSetName");

    /**　HP-UX用のデフォルトのルールセットのファイル名。*/
    private static final String            DEFAULT_HP_UX_RULESET_FILE =
                                                                        "/perfdoctor_HP_UX_rule.xml";

    /** ルールの管理を行うインスタンス。 */
    private static RuleManager             instance__;

    /** ルール定義のインタフェース。 */
    private final RuleDefAccessor          accessor_                  = new XmlRuleDefAccessor();

    /** リスナーのセット */
    private final Set<RuleChangeListener>  listenerSet_               =
                                                                        new LinkedHashSet<RuleChangeListener>();

    // ※ロールバック対象。
    /** 
     * 利用可能なRuleSetConfigを保持するMap。 
     * キーはRuleSetConfigのID、値はRuleSetConfig本体。
     */
    private HashMap<String, RuleSetConfig> ruleSetConfigMap_;

    /** ファイル削除対象ルール */
    private List<RuleSetConfig>            removeList_;

    // ※ロールバック対象。
    /** 
     * RuleSetDefを保持するMap。 
     * キーはRuleSetDefのID、値はRuleSetDef本体。
     */
    private HashMap<String, RuleSetDef>    ruleSetMap_;

    /**
     * 有効となっているルールセットのID。
     */
    private String                         activeRuleSetId_;

    /**
     * 設定が変更されたルールセットのIDのリスト。
     */
    private Set<String>                    dirtyRuleSetIds_;

    /**
     * RuleManagerインスタンスの取得。
     * @return RuleManagerインスタンス。
     */
    public static synchronized RuleManager getInstance()
    {
        if (instance__ == null)
        {
            instance__ = new RuleManager();
        }
        return instance__;
    }

    /**
     * コンストラクタ。外部からの呼び出しを禁止する。
     */
    private RuleManager()
    {
        initialize();
    }

    /**
     * インスタンスの初期化を行う。
     */
    private void initialize()
    {
        this.ruleSetMap_ = new HashMap<String, RuleSetDef>();
        this.ruleSetConfigMap_ = loadConfigurations();
        this.activeRuleSetId_ = loadActiveRuleSetId();
        this.dirtyRuleSetIds_ = new HashSet<String>();
        this.removeList_ = Collections.synchronizedList(new ArrayList<RuleSetConfig>());
    }

    /**
     * ルールセット定義を読み込む。
     * プリファレンスストアにルールセットのIDが一つも保存されていない場合には
     * デフォルトのルールセット定義マップを返す。
     * @return ルールセット定義マップ（定義読み込み済み）
     */
    private HashMap<String, RuleSetConfig> loadConfigurations()
    {
        HashMap<String, RuleSetConfig> map = createDefaultConfigMap();

        String[] ruleSetIds = RulePreferenceUtil.loadRuleSetIds();
        for (String ruleSetId : ruleSetIds)
        {
            RuleSetConfig config = RulePreferenceUtil.loadRuleSet(ruleSetId);
            map.put(ruleSetId, config);
        }

        return map;
    }

    /**
     * 初期ルールセット定義マップを作成する。
     * @return ルールセット定義マップ（デフォルト定義のみ）
     */
    private HashMap<String, RuleSetConfig> createDefaultConfigMap()
    {
        HashMap<String, RuleSetConfig> map = new LinkedHashMap<String, RuleSetConfig>();
        RuleSetConfig config = new RuleSetConfig();
        RuleSetConfig hpUxRuleSetConfig = new RuleSetConfig();
        RuleSetConfig javaRuleSetConfig = new RuleSetConfig();
        RuleSetConfig dbRuleSetConfig = new RuleSetConfig();

        //デフォルトのルールセットを定義する。
        config.setId(DEFAULT_RULESET_ID);
        config.setName(DEFAULT_RULESET_NAME);
        config.setFileName(DEFAULT_RULESET_FILE);
        map.put(DEFAULT_RULESET_ID, config);

        //HP_UX用のルールセットを定義する。
        hpUxRuleSetConfig.setId(DEFAULT_HP_UX_RULESET_ID);
        hpUxRuleSetConfig.setName(DEFAULT_HP_UX_RULESET_NAME);
        hpUxRuleSetConfig.setFileName(DEFAULT_HP_UX_RULESET_FILE);
        map.put(DEFAULT_HP_UX_RULESET_ID, hpUxRuleSetConfig);

        //java用のルールセットを定義する。
        javaRuleSetConfig.setId(DEFAULT_JAVA_RULESET_ID);
        javaRuleSetConfig.setName(DEFAULT_JAVA_RULESET_NAME);
        javaRuleSetConfig.setFileName(DEFAULT_JAVA_RULESET_FILE);
        map.put(DEFAULT_JAVA_RULESET_ID, javaRuleSetConfig);

        //DB用のルールセットを定義する。
        dbRuleSetConfig.setId(DEFAULT_DB_RULESET_ID);
        dbRuleSetConfig.setName(DEFAULT_DB_RULESET_NAME);
        dbRuleSetConfig.setFileName(DEFAULT_DB_RULESET_FILE);
        map.put(DEFAULT_DB_RULESET_ID, dbRuleSetConfig);

        return map;
    }

    /**
     * 現在アクティブなルールセットIDを取得する。
     * プリファレンスストアに保存されていたルールセットのIDがnullであるか、
     * 長さ0であった場合には、デフォルトのルールセットのIDを返す。
     * @return アクティブなルールセットID
     */
    private String loadActiveRuleSetId()
    {
        String str = RulePreferenceUtil.loadActiveRuleSetId();

        if (str == null || str.length() == 0)
        {
            str = DEFAULT_RULESET_ID;
        }

        return str;
    }

    /**
     * ルールセット定義(RuleSetConfigインスタンス)を利用可能なルールセットに追加する。
     * @param config ルールセット定義
     */
    public void addRuleSetConfig(final RuleSetConfig config)
    {
        this.ruleSetConfigMap_.put(config.getId(), config);
    }

    /**
     * ルールセット定義一覧(利用可能なルール一覧)を取得する。
     * @return ルールセット定義一覧
     */
    public RuleSetConfig[] getRuleSetConfigs()
    {
        RuleSetConfig[] array = new RuleSetConfig[this.ruleSetConfigMap_.size()];
        return this.ruleSetConfigMap_.values().toArray(array);
    }

    /**
     * ルールセット定義を利用可能なルールセット一覧から削除する。
     * @param id 削除するルールセットID
     */
    public synchronized void removeRuleSetConfig(final String id)
    {
        RuleSetConfig removeConfig = this.ruleSetConfigMap_.get(id);
        this.removeList_.add(removeConfig);
        this.ruleSetConfigMap_.remove(id);
    }

    /**
     * 現在アクティブなルールセット定義(RuleSetConfigインスタンス)を取得する。
     * @return アクティブなルールセット定義
     */
    public RuleSetConfig getActiveRuleSetConfig()
    {
        return this.ruleSetConfigMap_.get(this.activeRuleSetId_);
    }

    /**
     * アクティブなルールセットを設定する。
     * @param ruleSetConfig ルールセット定義
     */
    public void setActiveRuleSetConfig(final RuleSetConfig ruleSetConfig)
    {
        this.activeRuleSetId_ = ruleSetConfig.getId();
    }

    /**
     * ルールセット定義などをプリファレンスストア、xmlファイルに保存する。
     */
    public synchronized void commit()
    {
        // アクティブなルールセットIDの保存。
        RulePreferenceUtil.saveActiveRuleSetId(this.activeRuleSetId_);

        // ルールセット詳細一覧の保存。
        List<String> ruleSetIdList = new ArrayList<String>();
        Collection<RuleSetConfig> ruleSetConfigs = this.ruleSetConfigMap_.values();

        for (RuleSetConfig config : ruleSetConfigs)
        {
            String id = config.getId();
            if (isDefaultRuleSet(id))
            {
                continue;
            }

            RulePreferenceUtil.saveRuleSet(config);

            // 
            ruleSetIdList.add(id);
        }

        // ルールセットID一覧の保存。
        String[] ruleSetIds = ruleSetIdList.toArray(new String[ruleSetIdList.size()]);
        RulePreferenceUtil.saveRuleSetIds(ruleSetIds);

        // ルールセットの保存。
        // 変更があったルールセットのみ保存する。
        for (String ruleId : this.dirtyRuleSetIds_)
        {
            if (isDefaultRuleSet(ruleId))
            {
                continue;
            }

            RuleSetConfig config = this.ruleSetConfigMap_.get(ruleId);
            if (config == null)
            {
                continue;
            }
            RuleSetDef def = this.ruleSetMap_.get(ruleId);
            this.accessor_.updateRuleSet(def, config.getFileName());
        }

        // ルールセットの保存。
        // ファイルが存在しないルールセットについて、
        // デフォルトのルールを元にファイルを作成する。
        for (RuleSetConfig config : ruleSetConfigs)
        {
            String id = config.getId();
            if (isDefaultRuleSet(id))
            {
                continue;
            }

            File file = new File(config.getFileName());
            if (file.exists() && file.isFile())
            {
                continue;
            }

            File parentFile = file.getParentFile();

            if (parentFile != null && parentFile.exists() == false)
            {
                try
                {
                    parentFile.mkdirs();
                }
                catch (SecurityException ex)
                {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }

            // デフォルトのルールをコピーして保存する
            try
            {
                RuleSetDef defaultRuleSetClone = new RuleSetDef(getRuleSetDef(DEFAULT_RULESET_ID));
                defaultRuleSetClone.setName(config.getName());
                this.accessor_.updateRuleSet(defaultRuleSetClone, config.getFileName());
            }
            catch (RuleCreateException ex)
            {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        // ルールファイルを削除する。
        for (RuleSetConfig config : this.removeList_)
        {
            File file = new File(config.getFileName());
            if (file.exists())
            {
                try
                {
                    file.delete();
                }
                catch (SecurityException ex)
                {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
        this.removeList_ = Collections.synchronizedList(new ArrayList<RuleSetConfig>());
    }

    /**
     * ルールセット定義を取得する。<br>
     * 指定されたルールセットIDに対応する設定ファイルが見つからない場合は、<br>
     * デフォルトのルールセット定義を取得する。
     * @param id ルールセットID
     * @return ルールセット定義
     * @throws RuleCreateException ルールセット定義ファイル読み込みに失敗した場合
     */
    public RuleSetDef getRuleSetDef(final String id)
        throws RuleCreateException
    {
        RuleSetDef def = this.ruleSetMap_.get(id);
        if (def != null)
        {
            return def;
        }

        if (id.equals(DEFAULT_RULESET_ID))
        {
            def = this.accessor_.findRuleSet(DEFAULT_RULESET_FILE);
            def.setName(this.ruleSetConfigMap_.get(id).getName());
        }
        else if (id.equals(DEFAULT_HP_UX_RULESET_ID))
        {
            def = this.accessor_.findRuleSet(DEFAULT_HP_UX_RULESET_FILE);
            def.setName(this.ruleSetConfigMap_.get(id).getName());
        }
        else if (id.equals(DEFAULT_JAVA_RULESET_ID))
        {
            def = this.accessor_.findRuleSet(DEFAULT_JAVA_RULESET_FILE);
            def.setName(this.ruleSetConfigMap_.get(id).getName());
        }
        else if (id.equals(DEFAULT_DB_RULESET_ID))
        {
            def = this.accessor_.findRuleSet(DEFAULT_DB_RULESET_FILE);
            def.setName(this.ruleSetConfigMap_.get(id).getName());
        }
        else
        {
            RuleSetConfig config = this.ruleSetConfigMap_.get(id);
            if (config == null)
            {
                throw new RuleCreateException("InvalidRuleSetDef", new Object[]{id});
            }
            String fileName = config.getFileName();
            def = this.accessor_.findRuleSet(fileName);
        }

        this.ruleSetMap_.put(id, def);

        return def;
    }

    /**
     * ルールをコピーします。<br />
     *
     * @param orgId コピー元 ID
     * @param dstId コピー先 ID
     * @param dstName コピー先ルールセットの名前
     * @throws RuleCreateException ルールセット定義ファイル読み込みに失敗した場合
     */
    public void copyRuleSetDef(final String orgId, final String dstId, final String dstName)
        throws RuleCreateException
    {
        RuleSetDef orgDef = getRuleSetDef(orgId);
        RuleSetDef dstDef = new RuleSetDef(orgDef);
        dstDef.setName(dstName);
        this.ruleSetMap_.put(dstId, dstDef);
    }

    /**
     * ルールセット定義を一時的に保存する。<br>
     * rollbackRuleSetメソッドが実行された際に、ルールセット定義を巻き戻すために利用する。
     *
     * @return シリアライズ化されたルールデータ
     */
    public synchronized SerializedRules saveRuleSet()
    {
        byte[] ruleSetConfigMapData = SerializationUtils.serialize(this.ruleSetConfigMap_);
        byte[] ruleSetMapData = SerializationUtils.serialize(this.ruleSetMap_);
        return new SerializedRules(ruleSetConfigMapData, ruleSetMapData);
    }

    /**
     * ルールセット定義をロールバックする。<br>
     *
     * @param serializedRules シリアライズ化されたルールデータ
     */
    public synchronized void rollbackRuleSet(final SerializedRules serializedRules)
    {
        byte[] ruleSetConfigMapData = serializedRules.getRuleSetConfigMapData();
        byte[] ruleMapData = serializedRules.getRuleMapData();
        if (ruleSetConfigMapData == null || ruleSetConfigMapData.length == 0 || ruleMapData == null
                || ruleMapData.length == 0)
        {
            return;
        }

        this.ruleSetConfigMap_ =
                                 (HashMap<String, RuleSetConfig>)SerializationUtils.deserialize(ruleSetConfigMapData);
        this.ruleSetMap_ = (HashMap<String, RuleSetDef>)SerializationUtils.deserialize(ruleMapData);
        this.removeList_ = Collections.synchronizedList(new ArrayList<RuleSetConfig>());
    }

    /**
     * 変更があったルールセットIDを保存する。<br>
     * commitメソッドが実行された際に、このメソッドで指定した<br>
     * ルールセットIDに対するルールセット定義のみ保存する。
     * @param ruleSetId ルールセットID
     */
    public void addDirty(final String ruleSetId)
    {
        this.dirtyRuleSetIds_.add(ruleSetId);
    }

    /**
     * アクティブなルールセット定義を取得する。
     * @return ルールセット定義
     * @throws RuleCreateException ルール定義ファイルの読み込みに失敗した際に発生する。
     */
    public RuleSetDef getActiveRuleSetDef()
        throws RuleCreateException
    {
        return getRuleSetDef(this.activeRuleSetId_);
    }

    /**
     * アクティブなルールセットに含まれる、ルールインスタンスの一覧を取得する。
     * アクティブなルールセット中の要素中にあるルール名が不正であるために、
     * インスタンス生成に失敗した場合には、RuleCreateExceptionをスローする。
     * @return ルールインスタンスの一覧
     * @throws RuleCreateException ルール定義ファイルの読み込みに失敗した場合
     */
    public List<PerformanceRule> getActiveRules()
        throws RuleCreateException
    {
        List<PerformanceRule> ruleList = new ArrayList<PerformanceRule>();
        List<String> errorMessageList = new ArrayList<String>();

        RuleSetDef ruleSetDef = getActiveRuleSetDef();

        for (RuleDef ruleDef : ruleSetDef.getRuleDefs())
        {
            try
            {
                PerformanceRule rule = RuleInstanceUtil.createRuleInstance(ruleDef);
                if (rule != null)
                {
                    ruleList.add(rule);
                }
            }
            catch (RuleCreateException ex)
            {
                errorMessageList.add(ex.getMessage());
            }
        }

        if (errorMessageList.size() > 0)
        {
            String[] messages = errorMessageList.toArray(new String[errorMessageList.size()]);
            throw new RuleCreateException(RULE_CREATE_ERROR, null, messages);
        }

        return ruleList;
    }

    /**
     * ユニークなルールセットIDを取得する。
     * @return ルールセットID
     */
    public String createUniqueId()
    {
        String[] ruleSetIds = RulePreferenceUtil.loadRuleSetIds();

        String ruleSetId;
        do
        {
            ruleSetId = UUID.randomUUID().toString();
        }
        while (ArrayUtils.contains(ruleSetIds, ruleSetId));

        return ruleSetId;
    }

    /**
     * デフォルトルールセットをアクティブにする。
     */
    public void setActiveRuleSetDefault()
    {
        this.activeRuleSetId_ = RuleManager.DEFAULT_RULESET_ID;
        RuleSetConfig config = this.ruleSetConfigMap_.get(RuleManager.DEFAULT_RULESET_ID);
        setActiveRuleSetConfig(config);
    }

    /**
     * 有効となっているルールセットのIDを取得する。
     * @return 有効となっているルールセットID
     */
    public String getActiveRuleSetID()
    {
        return this.activeRuleSetId_;
    }

    /**
     * ルールセットIDを指定して、有効となっているルールセットを切り換える。
     * @param ruleSetID 有効化するルールセットID
     */
    public void changeActiveRuleSetByID(final String ruleSetID)
    {
        RuleSetConfig config = this.ruleSetConfigMap_.get(ruleSetID);

        if (config != null)
        {
            setActiveRuleSetConfig(config);
        }
    }

    /**
     * ルール変更リスナを追加する。
     * @param listener リスナ
     */
    public void addListener(final RuleChangeListener listener)
    {
        this.listenerSet_.add(listener);
    }

    /**
     * ルール変更リスナを削除する。
     * @param listener リスナ
     */
    public void removeListener(final RuleChangeListener listener)
    {
        this.listenerSet_.remove(listener);
    }

    /**
     * 更新を通知する。
     */
    public void notifyChanged()
    {
        for (RuleChangeListener listener : this.listenerSet_)
        {
            listener.ruleChangePerformed();
        }
    }

    /**
     * 指定されたルール ID のルールがデフォルトルールかどうかをチェックします。<br />
     *
     * @param ruleId ルール ID
     * @return デフォルトルールの場合は <code>true</code> 、デフォルトルールでない場合は <code>false</code>
     */
    private boolean isDefaultRuleSet(final String ruleId)
    {
        if (DEFAULT_RULESET_ID.equals(ruleId) || DEFAULT_HP_UX_RULESET_ID.equals(ruleId)
                || DEFAULT_JAVA_RULESET_ID.equals(ruleId) || DEFAULT_DB_RULESET_ID.equals(ruleId))
        {
            return true;
        }
        return false;
    }

}
