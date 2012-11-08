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
package jp.co.acroquest.endosnipe.common.logger;

import jp.co.acroquest.endosnipe.common.Constants;
import jp.co.acroquest.endosnipe.common.ENdoSnipeCommonPlugin;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Eclipse エラー・ログ・ビューへ出力するための {@link ENdoSnipeLogger} です。<br />
 * 
 * @author y-komori
 */
public class EclipseENdoSnipeLogger extends ENdoSnipeLogger
{
    // Eclipse エラー・ログへ出力する際のログレベル
    private static int currentLogLevel__;

    private final Plugin plugin_;

    private final ILog ilog_;

    /**
     * プラグインオブジェクトを構築します。<br />
     * @param provider {@link PluginProvider}オブジェクト
     */
    protected EclipseENdoSnipeLogger(final PluginProvider provider)
    {
        if (provider == null)
        {
            throw new IllegalArgumentException("provider can't be null.");
        }

        Object pluginObj = provider.getPlugin();
        if (pluginObj != null && pluginObj instanceof Plugin)
        {
            plugin_ = (Plugin)pluginObj;
        }
        else
        {
            plugin_ = ResourcesPlugin.getPlugin();
        }

        if (plugin_ != null)
        {
            ilog_ = plugin_.getLog();
        }
        else
        {
            // クラスパス上にクラスが存在するだけで、Eclipseが起動していない場合
            ilog_ = null;
        }
    }

    /**
     * ログレベルの更新とログレベル変更時のリスナを登録します。<br />
     * 
     */
    protected static synchronized void initialize()
    {
        updateCurrentLogLevel();

        // ログレベル変更時のリスナを登録
        ENdoSnipeCommonPlugin plugin = ENdoSnipeCommonPlugin.getDefault();
        if (plugin != null)
        {
            IPreferenceStore store = plugin.getPreferenceStore();
            store.addPropertyChangeListener(new LogLevelChangeListener());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(final Object message, final Throwable throwable)
    {
        if (EclipseENdoSnipeLogger.isILogOkEnabled())
        {
            String messageString = createMessage(message);
            if (ilog_ != null)
            {
                ilog_.log(createStatus(Status.OK, messageString, throwable));
            }
            else
            {
                System.out.println(messageString);
                throwable.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(final Object message)
    {
        if (EclipseENdoSnipeLogger.isILogOkEnabled())
        {
            String messageString = createMessage(message);
            if (ilog_ != null)
            {
                ilog_.log(createStatus(Status.OK, messageString));
            }
            else
            {
                System.out.println(messageString);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(final Object message, final Throwable throwable)
    {
        if (EclipseENdoSnipeLogger.isILogErrorEnabled())
        {
            String messageString = createMessage(message);
            if (ilog_ != null)
            {
                ilog_.log(createStatus(Status.ERROR, messageString, throwable));
            }
            else
            {
                System.out.println(messageString);
                throwable.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(final Object message)
    {
        if (EclipseENdoSnipeLogger.isILogErrorEnabled())
        {
            String messageString = createMessage(message);
            if (ilog_ != null)
            {
                ilog_.log(createStatus(Status.ERROR, messageString));
            }
            else
            {
                System.out.println(messageString);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatal(final Object message, final Throwable throwable)
    {
        error(message, throwable);
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void fatal(final Object message)
    {
        error(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(final Object message, final Throwable throwable)
    {
        if (EclipseENdoSnipeLogger.isILogInfoEnabled())
        {
            String messageString = createMessage(message);
            if (ilog_ != null)
            {
                ilog_.log(createStatus(Status.INFO, messageString, throwable));
            }
            else
            {
                System.out.println(messageString);
                throwable.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(final Object message)
    {
        if (EclipseENdoSnipeLogger.isILogInfoEnabled())
        {
            String messageString = createMessage(message);
            if (ilog_ != null)
            {
                ilog_.log(createStatus(Status.INFO, messageString));
            }
            else
            {
                System.out.println(messageString);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled()
    {
        return EclipseENdoSnipeLogger.isILogOkEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled()
    {
        return EclipseENdoSnipeLogger.isILogInfoEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraceEnabled()
    {
        return isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(final Object message, final Throwable throwable)
    {
        if (EclipseENdoSnipeLogger.isILogOkEnabled())
        {
            String messageString = createMessage(message);
            if (ilog_ != null)
            {
                ilog_.log(createStatus(Status.OK, messageString, throwable));
            }
            else
            {
                System.out.println(messageString);
                throwable.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(final Object message)
    {
        if (EclipseENdoSnipeLogger.isILogOkEnabled())
        {
            String messageString = createMessage(message);
            if (ilog_ != null)
            {
                ilog_.log(createStatus(Status.OK, messageString));
            }
            else
            {
                System.out.println(messageString);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(final Object message, final Throwable throwable)
    {
        if (EclipseENdoSnipeLogger.isILogWarnEnabled())
        {
            String messageString = createMessage(message);
            if (ilog_ != null)
            {
                ilog_.log(createStatus(Status.WARNING, messageString, throwable));
            }
            else
            {
                System.out.println(messageString);
                throwable.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(final Object message)
    {
        if (EclipseENdoSnipeLogger.isILogWarnEnabled())
        {
            String messageString = createMessage(message);
            if (ilog_ != null)
            {
                ilog_.log(createStatus(Status.WARNING, messageString));
            }
            else
            {
                System.out.println(messageString);
            }
        }
    }

    /**
     * propertiesファイルに記述されたメッセージを取得します。<br />
     * Eclipseプラグインとして用いられた場合は、
     * そのプラグインのクラスローダを用いてpropertiesファイルを検索し、
     * 見つかったpropertiesファイルからメッセージを取得します。
     * 
     * @param messageCode メッセージのコード
     * @param args 置換するメッセージ
     * @return メッセージ
     */
    @Override
    protected String getMessage(final String messageCode, final Object... args)
    {
        ClassLoader currentClassLoader = null;

        if (this.plugin_ != null)
        {
            //Eclipseプラグインの場合、コンテキストクラスローダを差し替える。
            ClassLoader pluginClassLoader = this.plugin_.getClass().getClassLoader();
            Thread currentThread = Thread.currentThread();
            currentClassLoader = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader(pluginClassLoader);
        }
        String message = super.getMessage(messageCode, args);
        if (this.plugin_ != null)
        {
            //Eclipseプラグインの場合、コンテキストクラスローダを元に戻す。
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isEnabledFor(final char messageType)
    {
        switch (messageType)
        {
        case 'T':
            return EclipseENdoSnipeLogger.isILogOkEnabled();
        case 'D':
            return EclipseENdoSnipeLogger.isILogOkEnabled();
        case 'I':
            return EclipseENdoSnipeLogger.isILogInfoEnabled();
        case 'W':
            return EclipseENdoSnipeLogger.isILogWarnEnabled();
        case 'E':
            return EclipseENdoSnipeLogger.isILogErrorEnabled();
        case 'F':
            return EclipseENdoSnipeLogger.isILogErrorEnabled();
        default:
            throw new IllegalArgumentException(String.valueOf(messageType));
        }
    }

    private static boolean isILogOkEnabled()
    {
        return currentLogLevel__ <= IStatus.OK;
    }

    private static boolean isILogInfoEnabled()
    {
        return currentLogLevel__ <= IStatus.INFO;
    }

    private static boolean isILogWarnEnabled()
    {
        return currentLogLevel__ <= IStatus.WARNING;
    }

    private static boolean isILogErrorEnabled()
    {
        return currentLogLevel__ <= IStatus.ERROR;
    }

    private IStatus createStatus(final int severity, final String messageStr,
            final Throwable throwable)
    {
        String pluginId = plugin_ != null ? plugin_.getBundle().getSymbolicName() : "unknown";
        return new Status(severity, pluginId, messageStr, throwable);
    }

    private IStatus createStatus(final int sevirity, final String message)
    {
        String pluginId = plugin_ != null ? plugin_.getBundle().getSymbolicName() : "unknown";
        return new Status(sevirity, pluginId, message);
    }

    /**
     * Eclipse 環境で出力するログレベルをプリファレンスストアから取得して更新します。<br />
     */
    protected static void updateCurrentLogLevel()
    {
        ENdoSnipeCommonPlugin plugin = ENdoSnipeCommonPlugin.getDefault();
        if (plugin != null)
        {
            IPreferenceStore store = plugin.getPreferenceStore();
            currentLogLevel__ = store.getInt(Constants.PREF_LOG_LEVEL);
        }
    }
}
