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
import jp.co.acroquest.test.util.JavelinTestUtil;
import junit.framework.TestCase;

/**
 * CPU時間、TAT時間で閾値判定するクラスのテスト。
 * @author fujii
 *
 */
public class CpuTimeRecordStrategyTest extends TestCase
{
    private static final int MILLI_TO_NANO = 1000000;

    /** Javelin設定ファイルのパス */
    private static final String JAVELIN_CONFIG_PATH = "/strategy/conf/javelin.properties";

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
    }

    /**
     * [項番] 4-2-5 judgeSendExceedThresholdAlarmのテスト。 <br />
     * ・CPU時間:1000(ミリ秒) TAT時間：5000(ミリ秒)、
     *  CPU時間のアラーム閾値:500(ミリ秒)、TAT時間のアラーム閾値：2000(ミリ秒)で、<br />
     *  judgeSendExceedThresholdAlarmを呼ぶ。<br />
     * →trueが返る。
     * 
     * @throws Exception 例外
     */
    public void testJudgeSendExceedThresholdAlarm_CpuOver_TATOver()
        throws Exception
    {
        // 準備
        CallTreeNode node = createCallTreeNode();

        // TATのアラーム閾値を設定する。(CPUのアラーム閾値はjavelin.propertiesにて設定)
        node.getInvocation().setAlarmThreshold(2000);

        // CPU時間を設定する。
        node.setCpuTime(1000 * MILLI_TO_NANO);

        // TAT時間を設定する。
        node.setStartTime(0);
        node.setEndTime(5000);

        CpuTimeRecordStrategy strategy = new CpuTimeRecordStrategy();

        // 実行
        boolean isAlarm = strategy.judgeSendExceedThresholdAlarm(node);

        // 検証
        assertTrue(isAlarm);
    }

    /**
     * [項番] 4-2-6 judgeSendExceedThresholdAlarmのテスト。 <br />
     * ・CPU時間:100(ミリ秒) TAT時間：5000(ミリ秒)、
     *  CPU時間のアラーム閾値:500(ミリ秒)、TAT時間のアラーム閾値：2000(ミリ秒)で、<br />
     *  judgeSendExceedThresholdAlarmを呼ぶ。<br />
     * →trueが返る。
     * 
     * @throws Exception 例外
     */
    public void testJudgeSendExceedThresholdAlarm_CpuUnder_TATOver()
        throws Exception
    {
        // 準備
        CallTreeNode node = createCallTreeNode();

        // TATのアラーム閾値を設定する。(CPUのアラーム閾値はjavelin.propertiesにて設定)
        node.getInvocation().setAlarmThreshold(2000);

        // CPU時間を設定する。
        node.setCpuTime(100 * MILLI_TO_NANO);

        // TAT時間を設定する。
        node.setStartTime(0);
        node.setEndTime(5000);

        CpuTimeRecordStrategy strategy = new CpuTimeRecordStrategy();

        // 実行
        boolean isAlarm = strategy.judgeSendExceedThresholdAlarm(node);

        // 検証
        assertTrue(isAlarm);
    }

    /**
     * [項番] 4-2-7 judgeSendExceedThresholdAlarmのテスト。 <br />
     * ・CPU時間:1000(ミリ秒) TAT時間：1000(ミリ秒)、
     *  CPU時間のアラーム閾値:500(ミリ秒)、TAT時間のアラーム閾値：2000(ミリ秒)で、<br />
     *  judgeSendExceedThresholdAlarmを呼ぶ。<br />
     * →trueが返る。
     * 
     * @throws Exception 例外
     */
    public void testJudgeSendExceedThresholdAlarm_CpuOver_TATUnder()
        throws Exception
    {
        // 準備
        CallTreeNode node = createCallTreeNode();

        // TATのアラーム閾値を設定する。(CPUのアラーム閾値はjavelin.propertiesにて設定)
        node.getInvocation().setAlarmThreshold(2000);
        //node.getInvocation().setAlarmCpuThreshold(500);

        // CPU時間を設定する。
        node.setCpuTime(1000 * MILLI_TO_NANO);

        // TAT時間を設定する。
        node.setStartTime(0);
        node.setEndTime(1000);

        CpuTimeRecordStrategy strategy = new CpuTimeRecordStrategy();

        // 実行
        boolean isAlarm = strategy.judgeSendExceedThresholdAlarm(node);

        // 検証
        assertTrue(isAlarm);
    }

    /**
     * [項番] 4-2-8 judgeSendExceedThresholdAlarmのテスト。 <br />
     * ・CPU時間:100(ミリ秒) TAT時間：1000(ミリ秒)、
     *  CPU時間のアラーム閾値:500(ミリ秒)、TAT時間のアラーム閾値：2000(ミリ秒)で、<br />
     *  judgeSendExceedThresholdAlarmを呼ぶ。<br />
     * →falseが返る。
     * 
     * @throws Exception 例外
     */
    public void testJudgeSendExceedThresholdAlarm_CpuUnder_TATUnder()
        throws Exception
    {
        // 準備
        CallTreeNode node = createCallTreeNode();

        // TATのアラーム閾値を設定にする。(CPUのアラーム閾値はjavelin.propertiesにて設定)
        node.getInvocation().setAlarmThreshold(2000);

        // CPU時間を設定する。
        node.setCpuTime(100 * MILLI_TO_NANO);

        // TAT時間を1設定する。
        node.setStartTime(0);
        node.setEndTime(1000);

        CpuTimeRecordStrategy strategy = new CpuTimeRecordStrategy();

        // 実行
        boolean isAlarm = strategy.judgeSendExceedThresholdAlarm(node);

        // 検証
        assertFalse(isAlarm);
    }

    /**
    /**
     * [項番] 4-2-9 judgeGenerateJaveinFileのテスト。 <br />
     * ・CPU時間:100(ミリ秒) TAT時間：1000(ミリ秒)、
     *  CPU時間の記録閾値:500(ミリ秒)、TAT時間の記録閾値：2000(ミリ秒)で、<br />
     *  judgeGenerateJaveinFileを呼ぶ。<br />
     * →JvnCallBackオブジェクトが返る。
     * 
     * @throws Exception 例外
     */
    public void testCreateCallback_Under()
        throws Exception
    {
        // 準備
        CallTreeNode node = createCallTreeNode();

        // TATのアラーム閾値を設定する。(CPUのアラーム閾値はjavelin.propertiesにて設定)
        node.getInvocation().setAlarmThreshold(2000);

        // CPU時間を設定する。
        node.setCpuTime(100 * MILLI_TO_NANO);

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
     * [項番] 4-2-10 judgeGenerateJaveinFileのテスト。 <br />
     * ・CPU時間:1000(ミリ秒) TAT時間：5000(ミリ秒)、
     *  CPU時間の記録閾値:500(ミリ秒)、TAT時間の記録閾値：2000(ミリ秒)で、<br />
     *  judgeGenerateJaveinFileを呼ぶ。<br />
     * →JvnCallBackオブジェクトが返る。
     * 
     * @throws Exception 例外
     */
    public void testCreateCallback_Over()
        throws Exception
    {
        // 準備
        CallTreeNode node = createCallTreeNode();

        // TATのアラーム閾値を設定する。(CPUのアラーム閾値はjavelin.propertiesにて設定)
        node.getInvocation().setAlarmThreshold(2000);

        // CPU時間を設定する。
        node.setCpuTime(100 * MILLI_TO_NANO);

        // TAT時間を設定する。
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
