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
package jp.co.acroquest.endosnipe.javelin.record;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.log.JavelinLogCallback;
import junit.framework.TestCase;

/**
 * TAT時間で閾値判定するクラスのテスト。
 * @author fujii
 *
 */
public class DefaultRecordStrategyTest extends TestCase
{
    /** Javelin設定ファイルのパス */
    private static final String JAVELIN_CONFIG_PATH = "test/strategy/conf";

    /**
     * 初期化メソッド<br />
     * システムログの初期化を行う。
     */
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        // オプションファイルから、オプション設定を読み込む。
        JavelinConfig config = new JavelinConfig(JAVELIN_CONFIG_PATH);
        SystemLogger.initSystemLog(config);
    }

    /**
    /**
     * [項番] 4-2-9 createCallbackのテスト。 <br />
     * ・TAT時間：1000(ミリ秒)、TATのアラーム閾値：2000(ミリ秒)で、<br />
     *  createCallbackを呼ぶ。<br />
     * →nullが返る。
     * 
     * @throws Exception 例外
     */
    public void testCreateCallback_Under()
        throws Exception
    {
        // 準備
        CallTreeNode node = createCallTreeNode();

        // TATのアラーム閾値を設定する。
        node.getInvocation().setAlarmThreshold(2000);

        // TAT時間を設定する。
        node.setStartTime(0);
        node.setEndTime(1000);

        CpuTimeRecordStrategy strategy = new CpuTimeRecordStrategy();

        // 実行
        JavelinLogCallback callback = strategy.createCallback(node);

        // 検証
        assertNull(callback);
    }

    /**
    /**
     * [項番] 4-2-10 createCallbackのテスト。 <br />
     * ・TAT時間：5000(ミリ秒)、TATのアラーム閾値：2000(ミリ秒)で、<br />
     *  createCallbackを呼ぶ。<br />
     * →JavelinLogCallbackオブジェクトが返る。
     * 
     * @throws Exception 例外
     */
    public void testCreateCallback_Over()
        throws Exception
    {
        // 準備
        CallTreeNode node = createCallTreeNode();

        // TATのアラーム閾値を設定する。
        node.getInvocation().setAlarmThreshold(2000);

        // TAT時間を設定にする。
        node.setStartTime(0);
        node.setEndTime(5000);

        CpuTimeRecordStrategy strategy = new CpuTimeRecordStrategy();

        // 実行
        JavelinLogCallback callback = strategy.createCallback(node);

        // 検証
        assertNotNull(callback);
    }

    /**
     * デフォルトのCallTreeNodeを作成する。
     * @return CallTreeNode
     * @throws Exception　例外
     */
    private CallTreeNode createCallTreeNode()
        throws Exception
    {
        // Invocation設定
        Invocation invocation =
                new Invocation("pid@host", "RootCallerName", "callerMethod", 0);
        CallTreeNode node = new CallTreeNode();
        node.setInvocation(invocation);
        return node;
    }

}
