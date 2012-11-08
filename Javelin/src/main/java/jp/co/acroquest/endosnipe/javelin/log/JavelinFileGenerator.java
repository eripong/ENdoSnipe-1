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
package jp.co.acroquest.endosnipe.javelin.log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.javelin.CallTree;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.conf.JavelinMessages;
import jp.co.acroquest.endosnipe.javelin.event.CommonEvent;
import jp.co.acroquest.endosnipe.javelin.util.ThreadUtil;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ArrayBlockingQueue;

/**
 * Javelinのログファイル名を管理する
 *
 * @author fujii
 *
 */
public class JavelinFileGenerator implements JavelinConstants
{
    private static final int DEF_QUEUE_SIZE = 1000;

    private final JavelinConfig javelinConfig_;

    /** 出力データを蓄積するキュー */
    private static BlockingQueue<JavelinLogTask> queue__ =
            new ArrayBlockingQueue<JavelinLogTask>(DEF_QUEUE_SIZE);

    private static boolean isInitialized__ = false;

    /** QueueがFullになったかどうかを表すフラグです。 */
    private boolean isQueueFull_ = false;

    /** ログ出力用StringBuilder */
    private StringBuilder logBuilder_ = new StringBuilder();

    /**
     * コンストラクタ。
     *
     * @param config Javelin設定。
     *
     */
    public JavelinFileGenerator(final JavelinConfig config)
    {
        javelinConfig_ = config;
    }

    /**
     * Javelinログ出力のタスクを、Javelinログ出力キューに追加します。<br />
     *
     * @param tree Javelinログを出力するCallTree。
     * @param callback Javelinログ出力後に呼び出す。
     * @param endNode ログに出力する CallTree の最後のノード（このノードまで出力される）
     * @param telegramId 電文 ID
     * @return 出力するJavelinログのファイル名。
     */
    public String generateJaveinFile(final CallTree tree, final JavelinLogCallback callback,
            final CallTreeNode endNode, final long telegramId)
    {
        final CallTreeNode TREE_NODE = tree.getRootNode();
        return generateJaveinFile(tree, TREE_NODE, callback, endNode, telegramId);
    }

    /**
     * Javelinログ出力のタスクを、Javelinログ出力キューに追加します。<br />
     *
     * @param tree Javelinログを出力するCallTree。
     * @param node CallTreeNode
     * @param callback Javelinログ出力後に呼び出す。
     * @param endNode ログに出力する CallTree の最後のノード（このノードまで出力される）
     * @param telegramId 電文 ID
     * @return 出力するJavelinログのファイル名。
     */
    public String generateJaveinFile(final CallTree tree, final CallTreeNode node,
            final JavelinLogCallback callback, final CallTreeNode endNode, final long telegramId)
    {
        synchronized (JavelinFileGenerator.class)
        {
            if (isInitialized__ == false)
            {
                JavelinLoggerThread thread = new JavelinLoggerThread(javelinConfig_, queue__);
                isInitialized__ = true;
                thread.start();
            }
        }

        Date date = new Date();
        String jvnFileName = JavelinLoggerThread.createJvnFileName(date);
        JavelinLogTask task = new JavelinLogTask(date, jvnFileName, tree, node, callback, endNode,
                                                 telegramId);

        // キューにタスクを追加する。
        boolean result = queue__.offer(task);
        synchronized (this.logBuilder_)
        {
            if (result != true)
            {
                if (this.isQueueFull_ == false)
                {
                    this.logBuilder_.append(jvnFileName);
                    this.isQueueFull_ = true;
                }
                else
                {
                    this.logBuilder_.append(',' + jvnFileName);
                }
                jvnFileName = null;
            }
            else if (this.isQueueFull_)
            {
                String message =
                        JavelinMessages.getMessage("javelin.log.JavelinFileGenerator.queueFull",
                                                   this.logBuilder_.toString());
                SystemLogger.getInstance().warn(message);
                this.logBuilder_ = new StringBuilder();
                this.isQueueFull_ = false;
            }
        }
        return jvnFileName;
    }

    /**
     * Javelinログとして、ファイルに出力する。
     * javelin.download.maxを超える場合には、分割して送信する。
     *
     *
     * @param jvnLogBuilder ライター
     * @param tree {@link CallTree}オブジェクト
     * @param node ノード。
     * @param endNode ログに出力する CallTree の最後のノード（このノードまで出力される）
     * @param callback JavelinCallback。
     * @param jvnFileFullPath jvnファイルのフルパス。
     * @param jvnFileName jvnファイル名。
     * @param telegramId 電文 ID
     * @return 引き続きノードを出力する場合は <code>true</code> 、ノード出力を終了する場合は <code>false</code>
     */
    public static boolean generateJavelinFileImpl(final StringBuilder jvnLogBuilder,
            final CallTree tree, final CallTreeNode node, final CallTreeNode endNode,
            JavelinLogCallback callback, String jvnFileName, String jvnFileFullPath,
            final long telegramId)
    {
        JavelinConfig config = new JavelinConfig();
        if (jvnLogBuilder.length() > config.getJvnDownloadMax())
        {
            flushBuffer(jvnLogBuilder, jvnFileName, jvnFileFullPath, callback,
                    config, telegramId);
        }

        if (node == null)
        {
            StackTraceElement[] stacktraces = ThreadUtil.getCurrentStackTrace();
            String stackTraceStr = "(JavelinFileGenerator#generateJavelinFileImpl) node is NULL.\n";
            stackTraceStr += ThreadUtil.getStackTrace(stacktraces, stacktraces.length);
            SystemLogger.getInstance().warn(stackTraceStr);
            return true;
        }

        // ファイルに1メッセージを書き込む。
        if (node.getInvocation() != null)
        {
            String jvnCallMessage = createLogMessage(tree, node);
            if (jvnCallMessage != null)
            {
                jvnLogBuilder.append(jvnCallMessage);
            }
        }

        List<CallTreeNode> children = node.getChildren();
        boolean continuePrint = true;
        for (int index = 0; index < children.size(); index++)
        {
            CallTreeNode child = children.get(index);
            continuePrint =
                    generateJavelinFileImpl(jvnLogBuilder, tree, child, endNode, callback,
                                            jvnFileName, jvnFileFullPath, telegramId);
            if (continuePrint == false || child == endNode)
            {
                continuePrint = false;
                break;
            }
        }

        // Throwログを書き込む。
        if (node.getThrowable() != null)
        {
            writeThrowLog(jvnLogBuilder, tree, node);
        }

        // Eventログを書き込む。
        CommonEvent[] eventList = node.getEventList();
        if (eventList != null)
        {
            for (CommonEvent event : eventList)
            {
                writeEventLog(jvnLogBuilder, tree, node, event);
            }
        }

        String jvnReturnMessage = "";
        if (node.getEndTime() >= 0)
        {
            if (node.isFieldAccess())
            {
                jvnReturnMessage =
                        JavelinLogMaker.createJavelinLog(ID_FIELD_WRITE, node.getEndTime(), tree,
                                                         node);
            }
            else
            {
                jvnReturnMessage =
                        JavelinLogMaker.createJavelinLog(ID_RETURN, node.getEndTime(), tree, node);
            }
        }

        // ファイルに1メッセージを書き込む。
        if (jvnReturnMessage != null)
        {
            jvnLogBuilder.append(jvnReturnMessage);
        }

        return continuePrint;
    }

    private static void writeEventLog(StringBuilder writer, CallTree tree, CallTreeNode node,
            CommonEvent event)
    {
        String jvnThrowMessage = JavelinLogMaker.createEventLog(event, tree, node);

        // ファイルに1メッセージを書き込む。
        if (jvnThrowMessage != null)
        {
            writer.append(jvnThrowMessage);
        }
    }

    private static String createLogMessage(final CallTree tree, final CallTreeNode node)
    {
        String jvnCallMessage;
        if (node.isFieldAccess())
        {
            jvnCallMessage = JavelinLogMaker.createJavelinLog(ID_FIELD_READ,
                    node.getStartTime(), tree, node);
        }
        else
        {
            jvnCallMessage =
                    JavelinLogMaker.createJavelinLog(ID_CALL, node.getStartTime(), tree, node);
        }
        return jvnCallMessage;
    }

    private static void writeThrowLog(final StringBuilder writer, final CallTree tree,
            final CallTreeNode node)
    {
        String jvnThrowMessage =
                JavelinLogMaker.createJavelinLog(ID_THROW, node.getThrowTime(), tree, node);

        // ファイルに1メッセージを書き込む。
        if (jvnThrowMessage != null)
        {
            writer.append(jvnThrowMessage);
        }
    }

    /**
     * バッファの内容をjvnログファイル、通知として送信する。
     *
     * @param builder バッファ内容。
     * @param jvnFileName jvnファイル名。
     * @param jvnFileFullPath ｊvnファイルのフルパス。
     * @param callback Callbackオブジェクト。
     * @param telegramId 電文 ID
     * @param config 設定。
     */
    static void flushBuffer(StringBuilder builder, String jvnFileName, String jvnFileFullPath,
            JavelinLogCallback callback, JavelinConfig config, long telegramId)
    {

        try
        {
            String content = builder.toString();
            if (config.isLogJvnFile())
            {
                if (jvnFileFullPath != null)
                {
                    writeToFile(jvnFileFullPath, content);
                }
            }

            if (jvnFileName != null && callback != null)
            {
                callback.execute(jvnFileName, content, telegramId);
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }

        if (builder.length() > 0)
        {
            builder.delete(0, builder.length());
        }
    }

    private static void writeToFile(final String jvnFileName, final String content)
    {
        Writer writer = null;
        try
        {
            writer = new FileWriter(jvnFileName, true);
            writer.write(content);
            writer.flush();
        }
        catch (IOException ioEx)
        {
            SystemLogger.getInstance().warn(ioEx);
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException ioEx)
                {
                    SystemLogger.getInstance().warn(ioEx);
                }
            }
        }
    }

}
