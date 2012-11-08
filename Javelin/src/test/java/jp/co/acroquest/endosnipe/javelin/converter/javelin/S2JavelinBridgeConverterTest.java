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
package jp.co.acroquest.endosnipe.javelin.converter.javelin;

import java.util.ArrayList;

import jp.co.dgic.testing.common.virtualmock.MockObjectManager;
import jp.co.dgic.testing.framework.DJUnitTestCase;
import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.smg.endosnipe.javassist.ClassPool;
import jp.co.smg.endosnipe.javassist.CtClass;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.conf.ExcludeConversionConfig;
import jp.co.acroquest.endosnipe.javelin.conf.IncludeConversionConfig;
import jp.co.acroquest.endosnipe.javelin.converter.Converter;
import jp.co.acroquest.endosnipe.javelin.converter.javelin.JavelinBridgeConverter;
import jp.co.acroquest.endosnipe.javelin.event.CommonEvent;
import jp.co.acroquest.test.util.JavelinTestUtil;

/**
 * S2JaelinBridgeコンバータのテストクラス
 *
 * @author fujii
 */
public class S2JavelinBridgeConverterTest extends DJUnitTestCase
{

    /**
     * 初期化メソッド<br />
     * システムログの初期化を行う。
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        MockObjectManager.initialize();

        // オプションファイルから、オプション設定を読み込む。
        JavelinTestUtil.camouflageJavelinConfig(getClass(), "/ver4_1_test/conf/javelin.properties");
        JavelinTestUtil.camouflageJavelinConfig("javelin.bytecode.exclude.policy", 0);
        JavelinTestUtil.camouflageJavelinConfig("javelin.call.tree.enable", true);

        JavelinConfig config = new JavelinConfig();
        SystemLogger.initSystemLog(config);
    }

    /**
     * [項番] 2-1-1 convertのテスト。 <br />
     * ・publicメソッドを実装するJavelinTestPublicクラスに対して、<br />
     *  S2JavelinBridgeコンバータを適用する。<br />
     * 
     * →システムログにメソッド変換情報が出力されることを目視する。 
     * 
     * @throws Exception 例外
     */
    public void testConvertImpl_convert_Public()
        throws Exception
    {
        // 準備
        // コンバータの作成
        Converter converter = createConverter();

        // ログコード埋め込み対象クラスの呼び出し
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass =
                pool.get("jp.co.acroquest.endosnipe.javelin.converter.s2javelin.JavelinTestPublic");
        String className = "JavelinTestPublic";

        // 引数の初期設定
        IncludeConversionConfig includeConfig = new IncludeConversionConfig();
        includeConfig.readConfig("jp.co.acroquest.endosnipe.javelin.converter.s2javelin.JavelinTestPublic,S2Converter");
        java.util.List<ExcludeConversionConfig> excludeConfig =
                new ArrayList<ExcludeConversionConfig>();
        ExcludeConversionConfig exclude = new ExcludeConversionConfig();
        exclude.readConfig("test#test");
        excludeConfig.add(exclude);

        // 実行
        converter.convert(className, null, pool, ctClass, includeConfig, excludeConfig);
        // 検証
        // 目視
    }

    /**
     * [項番] 2-1-2 convertのテスト。 <br />
     * ・protectedメソッドを実装するJavelinTestProtectedクラスに対して、<br />
     *  S2JavelinBridgeコンバータを適用する。<br />
     * 
     * →システムログにメソッド変換情報が出力されることを目視する。 
     * 
     * @throws Exception 例外
     */
    public void testConvertImpl_convert_Protected()
        throws Exception
    {
        // 準備
        // コンバータの作成
        Converter converter = createConverter();

        // ログコード埋め込み対象クラスの呼び出し
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass =
                pool.get("jp.co.acroquest.endosnipe.javelin.converter.s2javelin.JavelinTestProtected");
        String className = "JavelinTestProtected";

        // 引数の初期設定
        IncludeConversionConfig includeConfig = new IncludeConversionConfig();
        includeConfig.readConfig("jp.co.acroquest.endosnipe.javelin.converter.s2javelin.JavelinTestProtected,S2Converter");
        java.util.List<ExcludeConversionConfig> excludeConfig =
                new ArrayList<ExcludeConversionConfig>();
        ExcludeConversionConfig exclude = new ExcludeConversionConfig();
        exclude.readConfig("test#test");
        excludeConfig.add(exclude);

        // 実行
        converter.convert(className, null, pool, ctClass, includeConfig, excludeConfig);
        // 検証
        // 目視
    }

    /**
     * [項番] 2-1-3convertのテスト。 <br />
     * ・privateメソッドを実装するJavelinTestPrivateクラスに対して、<br />
     *  S2JavelinBridgeコンバータを適用する。<br />
     * 
     * →システムログにメソッド変換情報が出力されないことを目視する。 
     * 
     * @throws Exception 例外
     */
    public void testConvertImpl_convert_Private()
        throws Exception
    {
        // 準備
        // コンバータの作成
        Converter converter = createConverter();

        // ログコード埋め込み対象クラスの呼び出し
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass =
                pool.get("jp.co.acroquest.endosnipe.javelin.converter.s2javelin.JavelinTestPrivate");
        String className = "JavelinTestPrivate";

        // 引数の初期設定
        IncludeConversionConfig includeConfig = new IncludeConversionConfig();
        includeConfig.readConfig("jp.co.acroquest.endosnipe.javelin.converter.s2javelin.JavelinTestPrivate,S2Converter");
        java.util.List<ExcludeConversionConfig> excludeConfig =
                new ArrayList<ExcludeConversionConfig>();
        ExcludeConversionConfig exclude = new ExcludeConversionConfig();
        exclude.readConfig("test#test");
        excludeConfig.add(exclude);

        // 実行
        converter.convert(className, null, pool, ctClass, includeConfig, excludeConfig);
        // 検証
        // 目視
    }

    /**
     * [項番] 2-1-4convertのテスト。 <br />
     * ・staticメソッドのみを実装するJavelinTestStaticクラスに対して、<br />
     *  S2JavelinBridgeコンバータを適用する。<br />
     * 
     * →システムログにメソッド変換情報が出力されていることを目視する。 
     * 
     * @throws Exception 例外
     */
    public void testConvertImpl_convert_Static()
        throws Exception
    {
        // 準備
        // コンバータの作成
        Converter converter = createConverter();

        // ログコード埋め込み対象クラスの呼び出し
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass =
                pool.get("jp.co.acroquest.endosnipe.javelin.converter.s2javelin.JavelinTestStatic");
        String className = "JavelinTestStatic";

        // 引数の初期設定
        IncludeConversionConfig includeConfig = new IncludeConversionConfig();
        includeConfig.readConfig("jp.co.acroquest.endosnipe.javelin.converter.s2javelin.JavelinTestStatic,S2Converter");
        java.util.List<ExcludeConversionConfig> excludeConfig =
                new ArrayList<ExcludeConversionConfig>();
        ExcludeConversionConfig exclude = new ExcludeConversionConfig();
        exclude.readConfig("test#test");
        excludeConfig.add(exclude);

        // 実行
        converter.convert(className, null, pool, ctClass, includeConfig, excludeConfig);
        // 検証
        // 目視
    }

    /**
     * [項番] 2-1-5 convertのテスト。 <br />
     * ・staticメソッドのみを実装するJavelinTestStaticクラスに対して、<br />
     *  S2JavelinBridgeコンバータを適用する。<br />
     * 
     * →システムログにメソッド変換情報が出力されないことを目視する。 
     * 
     * @throws Exception 例外
     */
    public void testConvertImpl_convert_Constructor()
        throws Exception
    {
        // 準備
        // コンバータの作成
        Converter converter = createConverter();

        // ログコード埋め込み対象クラスの呼び出し
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass =
                pool.get("jp.co.acroquest.endosnipe.javelin.converter.s2javelin.JavelinTestConstructor");
        String className = "JavelinTestConstructor";

        // 引数の初期設定
        IncludeConversionConfig includeConfig = new IncludeConversionConfig();
        includeConfig.readConfig("jp.co.acroquest.endosnipe.javelin.converter.s2javelin.JavelinTestConstructor,S2Converter");
        java.util.List<ExcludeConversionConfig> excludeConfig =
                new ArrayList<ExcludeConversionConfig>();
        ExcludeConversionConfig exclude = new ExcludeConversionConfig();
        exclude.readConfig("test#test");
        excludeConfig.add(exclude);

        // 実行
        converter.convert(className, null, pool, ctClass, includeConfig, excludeConfig);
        // 検証
        // 目視
    }

    /**
     * CallTreeFullイベントが発生することを確認する。
     */
    public void testCallTreeFull_閾値超過()
    {
        // 準備
        CallTreeFullTestSample sample = null; 
        try
        {
            // サンプルコードの変換を行う。
            // ver4_1_test/conf/include.confに、サンプルコードのinclude設定を記述してある。
            sample = (CallTreeFullTestSample)JavelinTestUtil.createMonitoredObject(
                                         "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.S2JavelinBridgeConverter", 
                                         "jp.co.acroquest.endosnipe.javelin.converter.s2javelin.CallTreeFullTestSample");
            JavelinTestUtil.camouflageJavelinConfig("javelin.call.tree.enable", true);
            JavelinTestUtil.camouflageJavelinConfig("javelin.call.tree.max", 500);
            JavelinTestUtil.camouflageJavelinConfig("javelin.autoExcludeThreshold.time", 0);
                }
        catch (Exception ex)
        {
            fail(ex.getMessage());
        }

        // 実行
        sample.entry(500);

        // 検証
        assertCalled(StatsJavelinRecorder.class, "postProcess");
        assertCalled(StatsJavelinRecorder.class, "sendCallTreeEvent");
        assertCalled(StatsJavelinRecorder.class, "addEvent");
        CommonEvent event = (CommonEvent)getArgument(StatsJavelinRecorder.class, "addEvent", 0);
        assertEquals("CallTreeFull", event.getName());
    }

    /**
     * コンバータを作成する。
     * 
     * @return S2JavelinBridgeConverter
     */
    private Converter createConverter()
    {
        return new JavelinBridgeConverter();
    }
}
