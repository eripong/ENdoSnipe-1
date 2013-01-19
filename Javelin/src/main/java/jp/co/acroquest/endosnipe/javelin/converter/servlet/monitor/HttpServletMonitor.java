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
package jp.co.acroquest.endosnipe.javelin.converter.servlet.monitor;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.javelin.CallTree;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.CallTreeRecorder;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.event.HttpStatusEvent;
import jp.co.acroquest.endosnipe.javelin.util.ThreadUtil;

/**
 * ServletJavelinを実行する
 * 
 * @author yamasaki
 * 
 */
public class HttpServletMonitor
{
    /**
     * デフォルトコンストラクタ
     */
    private HttpServletMonitor()
    {
        // Do Nothing.
    }

    /** ホスト名を入力する引数番号 */
    private static final int ARGS_HOST_NUM = 0;

    /** ポート番号を入力する引数番号 */
    private static final int ARGS_PORT_NUM = 1;

    /** コンテクストパスを入力する引数番号 */
    private static final int ARGS_CONTEXTPATH_NUM = 2;

    /** サーブレットパスを入力する引数番号 */
    private static final int ARGS_SERVLETPATH_NUM = 3;

    /** メソッド名を入力する引数番号 */
    private static final int ARGS_METHOD_NUM = 4;

    /** クエリ文字列を入力する引数番号 */
    private static final int ARGS_QUERY_STRING_NUM = 5;

    /** パラメータのマップを入力する引数番号 */
    private static final int ARGS_PARAMETER_MAP_NUM = 6;

    /** セッションを入力する引数番号 */
    private static final int ARGS_SESSION_NUM = 7;

    /** 引数の数. */
    private static final int ARGS_NUM = 8;

    /** Javelinの設定 */
    private static JavelinConfig config__ = new JavelinConfig();

    /**
     * 前処理
     * 
     * @param request リクエスト
     */
    public static void preProcess(HttpRequestValue request)
    {
        try
        {
            CallTreeRecorder callTreeRecorder = CallTreeRecorder.getInstance();
            
            // ThreadLocalからクラス名、メソッド名を呼び出す。 クラス名、メソッド名が存在しない場合、 
            // HttpRequestのコンテキストパス、サーブレットパスを呼び出す。
            String[] paths = getPaths(request);
            String contextPath = paths[0];
            String servletPath = paths[1];
            
            // 親ノードと同一のパスならば、二重に記録しない。
            CallTreeNode parent = callTreeRecorder.getCallTreeNode();
            if (parent != null)
            {
                Invocation invocation = parent.getInvocation();
                if (invocation != null 
                    && contextPath.equals(invocation.getClassName())
                    && servletPath.equals(invocation.getMethodName()))
                {
                    parent.count_++;
                    return;
                }
            }
            
            Object[] args = null;

            // log.argsがtrueのとき、引数情報を表示する。
            if (config__.isLogArgs())
            {
                args = new Object[ARGS_NUM];
                args[ARGS_HOST_NUM] = request.getRemoteHost();
                args[ARGS_PORT_NUM] = request.getRemotePort();
                args[ARGS_CONTEXTPATH_NUM] = contextPath;
                args[ARGS_SERVLETPATH_NUM] = servletPath;
                args[ARGS_METHOD_NUM] = request.getMethod();
                if (request.getQueryString() == null)
                {
                    args[ARGS_QUERY_STRING_NUM] = "";
                }
                else
                {
                    args[ARGS_QUERY_STRING_NUM] = request.getQueryString();
                }
                if (request.getCharacterEncoding() == null)
                {
                    // Character Encodingが指定されていない場合、
                    // 文字化けが発生してしまうため、パラメータマップは取得しない。
                    args[ARGS_PARAMETER_MAP_NUM] = "Parameter map is unavailable.";
                }
                else
                {
                    args[ARGS_PARAMETER_MAP_NUM] = request.getParameterMap();
                }
            }
            

            StackTraceElement[] stacktrace = null;
            if (config__.isLogStacktrace())
            {
                stacktrace = ThreadUtil.getCurrentStackTrace();
            }
            
            StatsJavelinRecorder.preProcess(contextPath, 
                                              servletPath, 
                                              args, 
                                              stacktrace, 
                                              config__,
                                              true, 
                                              true);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }
    }

    /**
     * 後処理。
     * @param request リクエスト
     * @param response レスポンス
     */
    public static void postProcess(HttpRequestValue request, HttpResponseValue response)
    {
        String[] paths = getPaths(request);
        String contextPath = paths[0];
        String servletPath = paths[1];
        
        CallTreeRecorder callTreeRecorder = CallTreeRecorder.getInstance();
        CallTreeNode node = callTreeRecorder.getCallTreeNode();
        
        if (node != null)
        {
            if (node.count_ > 0)
            {
                node.count_--;
                return;
            }
        }
        
        try
        {
            int status = response.getStatus();
            if (status / 100 >= 4 && config__.isHttpStatusError())
            {
                HttpStatusEvent event =
                                        new HttpStatusEvent(contextPath, servletPath, status,
                                                            response.getThrowable(),
                                                            config__.getTraceDepth());
                StatsJavelinRecorder.addEvent(event);
                
                Invocation invocation = node.getInvocation();
                invocation.addHttpStatusCount(String.valueOf(status));
            }
            
            Object returnValue = null;
            if (config__.isLogReturn())
            {
                returnValue = status;
            }

              StatsJavelinRecorder.postProcess(contextPath, 
                                                 servletPath, 
                                                 returnValue, 
                                                 config__,
                                                 true);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }
    }

    /**
     * 後処理(例外)。
     * @param request リクエスト
     * @param throwable 例外。
     */
    public static void postProcessNG(
            HttpRequestValue request,
            final Throwable  throwable)
    {
        String[] paths = getPaths(request);
        String contextPath = paths[0];
        String servletPath = paths[1];

        // Invocationに例外が記録されない場合でも発生した、という記録は行う
        CallTreeRecorder callTreeRecorder = CallTreeRecorder.getInstance();
        CallTreeNode node = callTreeRecorder.getCallTreeNode();
        
        if (node != null)
        {
            if (node.count_ > 0)
            {
                node.count_--;
                return;
            }
        }
        
        CallTree callTree = callTreeRecorder.getCallTree();
        if (node != null && callTree != null)
        {
            if (throwable != null && callTree.getCause() == throwable)
            {
                Invocation invocation = node.getInvocation();
                invocation.addThrowable(throwable);
            }
        }

        try
        {
            StatsJavelinRecorder.postProcess(contextPath, 
                                               servletPath, 
                                               throwable, 
                                               config__, 
                                               true);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }
    }

    /**
     * HTTPリクエストからクラス名、メソッド名となるパス情報を取得する。
     * 
     * @param request
     * @return
     */
    private static String[] getPaths(final HttpRequestValue request)
    {
        String contextPath;
        String servletPath;
        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
        {

            contextPath = request.getContextPath();
            servletPath = request.getServletPath();
        }
        else
        {
            contextPath = request.getContextPath() + request.getServletPath();
            servletPath = pathInfo;
        }

        // コンテキストパスが空の場合は"/"を利用する。
        if ("".equals(contextPath))
        {
            contextPath = "/";
        }
        
        String[] paths = {contextPath, servletPath};
        return paths;
    }
}
