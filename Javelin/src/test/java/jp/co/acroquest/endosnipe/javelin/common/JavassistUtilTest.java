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
package jp.co.acroquest.endosnipe.javelin.common;

import jp.co.smg.endosnipe.javassist.ClassPool;
import jp.co.smg.endosnipe.javassist.CtClass;
import jp.co.acroquest.endosnipe.javelin.testutil.PrivateAccessor;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ConcurrentHashMap;
import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.test.util.JavelinTestUtil;
import junit.framework.TestCase;

/**
 * JavassistUtilのテストクラス。
 * @author fujii
 *
 */
public class JavassistUtilTest extends TestCase
{
    /** Javelin設定ファイルのパス */
    private static final String JAVELIN_CONFIG_PATH = "/common/conf/javelin.properties";

    /**
     * 初期化メソッド<br />
     * システムログの初期化を行う。
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        // オプションファイルから、オプション設定を読み込む。
        JavelinTestUtil.camouflageJavelinConfig(getClass(), JAVELIN_CONFIG_PATH);
        JavelinConfig config = new JavelinConfig();
        SystemLogger.initSystemLog(config);

        // 継承関係のキャッシュをクリアする
        PrivateAccessor.setField(JavassistUtil.class, "inheritedMap__",
                                 new ConcurrentHashMap<String, Boolean>());
        PrivateAccessor.setField(JavassistUtil.class, "maximumDepth__",
                                 config.getInheritanceDepth());
    }

    /**
     * [項番] 1-3-1 isInheritedのテスト。 <br />
     * ・inheritedClassNameを継承しているクラスに対して、<br />
     *  isInheritedメソッドを実行する。<br />
     * →trueが返る。<br />
     * 
     * @throws Exception 例外
     */
    public void testIsInherited_ineritate()
        throws Exception
    {

        // 準備
        // クラスを呼び出す。
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("jp.co.acroquest.endosnipe.javelin.common.TestInherit1");

        // 実行
        boolean isInherited =
                JavassistUtil.isInherited(ctClass, pool,
                                          "jp.co.acroquest.endosnipe.javelin.common.TestRootClass");

        // 検証
        assertTrue(isInherited);

    }

    /**
     * [項番] 1-3-2 isInheritedのテスト。 <br />
     * ・存在しないクラスの名前をinheritedClassNameにして、<br />
     *  isInheritedメソッドを実行する。<br />
     * →falseが返る。<br />
     * 
     * @throws Exception 例外
     */
    public void testIsInherited_NotExistClass()
        throws Exception
    {
        // 準備
        // クラスを呼び出す。
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("jp.co.acroquest.endosnipe.javelin.common.TestInherit1");

        // 実行
        boolean isInherited =
                JavassistUtil.isInherited(ctClass, pool,
                                          "jp.co.acroquest.endosnipe.javelin.common.NotExistClass");

        // 検証
        assertFalse(isInherited);

    }

    /**
     * [項番] 1-3-5 isInheritedのテスト。 <br />
     * ・inheritedClassNameを3階層下で継承しているクラスに対して、<br />
     *  isInheritedメソッドを実行する。<br />
     * →trueが返る。<br />
     * 
     * @throws Exception 例外
     */
    public void testIsInherited_MaxDepth()
        throws Exception
    {

        // 準備
        // クラスを呼び出す。
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("jp.co.acroquest.endosnipe.javelin.common.TestInherit3");

        // 実行
        boolean isInherited =
                JavassistUtil.isInherited(ctClass, pool,
                                          "jp.co.acroquest.endosnipe.javelin.common.TestRootClass");
        System.out.println(new JavelinConfig().getInheritanceDepth());

        // 検証
        assertTrue(isInherited);
    }

    /**
     * [項番] 1-3-6 isInheritedのテスト。 <br />
     * ・inheritedClassNameを4階層下で継承しているクラスに対して、<br />
     *  isInheritedメソッドを実行する。<br />
     * →falseが返る。<br />
     * 
     * @throws Exception 例外
     */
    public void testIsInherited_OverDepth()
        throws Exception
    {

        // 準備
        // クラスを呼び出す。
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("jp.co.acroquest.endosnipe.javelin.common.TestInherit4");

        // 実行
        boolean isInherited =
                JavassistUtil.isInherited(ctClass, pool,
                                          "jp.co.acroquest.endosnipe.javelin.common.TestRootClass");

        // 検証
        assertFalse(isInherited);
    }

}
