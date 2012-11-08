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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.util.EclipseUtil;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.seasar.framework.message.MessageFormatter;
import org.seasar.framework.util.AssertionUtil;

/**
 * ログ出力を提供するクラスです。<br />
 * 本クラスでは、Eclipse プラグイン環境であるかどうかを自動判定して、
 * ログ出力先を自動的に切り替えます。<br />
 * <ul>
 * <li>Eclipse 環境でない場合、log4j を利用してログ出力します。</li>
 * <li>Eclipse 環境の場合、エラーログ・ビューへログ出力します。
 * (エラーログ・ビューは Eclipse for RCP/Plug-in Developers で使用できます)</li>
 * </ul>
 * 
 * 本クラスを利用してログ出力を行うには、以下のようにしてください。
 * 
 * <h2>ENdoSnipeLogger インスタンスの取得方法</h2>
 * <p> 
 * 以下のように定数として用意してください。引数の {@link Class} オブジェクトは
 * ロガーを使用するクラスの {@link Class} オブジェクトで、log4j のカテゴリと
 * して使用します。
 * </p>
 * 
 * <pre>
 * private static final ENdoSnipeLogger LOGGER =
 *   ENdoSnipeLogger.getLogger(OutputAction.class);
 * </pre>
 * 
 * <p>
 * なお、Eclipse プラグイン環境で使用される可能性がある場合、
 * 以下のように第2引数に {@link PluginProvider} の static インスタンスを渡してください。
 * これは、エラーログ・ビューに発生プラグインを出力する際に使用します。
 * </p>
 * 
 * <pre>
 * private static final ENdoSnipeLogger LOGGER =
 *   ENdoSnipeLogger.getLogger(OutputAction.class, ArrowVisionPluginProvider.INSTANCE);
 * </pre>
 * </li>
 * 
 * <h2>メッセージコードを使用したログ出力方法</h2>
 * <p>
 * 以下の各メソッドでは、メッセージコードを使用したログ出力を行うことができます。
 * </p>
 * <ul>
 *   <li>{@link #log(String, Object...)}</li>
 *   <li>{@link #log(String, Throwable, Object...)}</li>
 * </ul>
 * <ol>
 * <li>
 *   サブシステム ID を決める。<br />
 *   3 文字の大文字アルファベットでサブシステム ID を決めます。<br />
 *   サブシステム ID の単位で、メッセージリソースファイルを作成することになります。<br />
 *   (例) ENdoSnipeDataCollector → <code>EDC</code>
 * </li>
 * <li>
 *   メッセージリソースファイルを作成する。<br />
 *   <code><i>サブシステム ID</i>Messages</code>というバンドル名称で
 *   メッセージリソースファイルを作成します。<br />
 *   ファイルはクラスパスのルート(通常は src/main/resources 直下)に作成してください。<br />
 *   (例) <code>EDCMessages_ja.properties</code>
 * </li>
 * <li>
 *   メッセージを記述する。<br />
 *   <p>以下の形式でメッセージリソースファイルにメッセージ定義を追加します。</p>
 *   
 *   <i>メッセージコード</i>=<i>メッセージ</i>
 *   
 *   <p>メッセージコードは以下の規則で決定します。</p>
 *   
 *   <i>ログレベル</i><i>サブシステム ID</i><i>エラー番号</i>
 *   
 *   <p>
 *   最初の 1 文字はログレベルで、(F,E,W,I,D,T) のいずれかです。<br />
 *   次のの 3 文字はサブシステム ID を指定します。<br />
 *   最後の 4 桁の数字はサブシステム内で一意なエラー番号を表します。<br />
 *   </p>
 *   
 *   (例) <code>IEDC0001=ENdoSnipe DataCollector を開始します.</code>
 *   
 *   <p>
 *   メッセージに引数を指定する場合、{0}、{1}・・・のようにプレースホルダを指定できます。<br />
 *   </p>
 *   
 *   (例) <code>IEDC0008=Javelin に接続しました.(接続先: {0}:{1})</code>
 * </li>
 * </ol>
 * 
 * @author y-komori
 * @author nagai
 */
public abstract class ENdoSnipeLogger
{
    private static final Map<Class<?>, ENdoSnipeLogger> LOGGERS =
            new HashMap<Class<?>, ENdoSnipeLogger>();

    private static boolean initialized__;

    private static boolean isEclipseAvailable__;

    private static boolean useSystemLogger__;

    /**
     * {@link ENdoSnipeLogger} の暗黙のコンストラクタです。<br />
     */
    protected ENdoSnipeLogger()
    {
        // Do nothing.
    }

    /**
     * ログ出力に {@link SystemLogger} を使用するかどうかを設定します。<br />
     * Javelin 内部では {@link SystemLogger} を使用してログ出力を行うため、
     * 最初に本メソッドを使用して {@link SystemLogger} を使用するようにしてください。<br />
     * {@link SystemLogger} を使用するように設定した場合、以降の
     * {@link ENdoSnipeLogger} による出力はすべて {@link SystemLogger} によって行われます。
     * @param useSystemLogger {@link SystemLogger} を使用する場合、<code>true</code>
     */
    public static synchronized void setSystemLoggerMode(final boolean useSystemLogger)
    {
        useSystemLogger__ = useSystemLogger;
    }

    /**
     * {@link ENdoSnipeLogger} を返します。<br />
     * 
     * @param clazz {@link Class} オブジェクト
     * @return {@link ENdoSnipeLogger} オブジェクト
     */
    public static synchronized ENdoSnipeLogger getLogger(final Class<?> clazz)
    {
        return getLogger(clazz, null);
    }

    /**
     * {@link ENdoSnipeLogger} を返します。<br />
     * 
     * @param clazz {@link Class} オブジェクト
     * @param provider {@link PluginProvider} オブジェクト
     * @return {@link ENdoSnipeLogger} オブジェクト
     */
    public static synchronized ENdoSnipeLogger getLogger(final Class<?> clazz,
            final PluginProvider provider)
    {
        if (!initialized__)
        {
            initialize();
        }
        ENdoSnipeLogger logger = LOGGERS.get(clazz);
        if (logger == null)
        {
            logger = createENdoSnpeLogger(clazz, provider);
            LOGGERS.put(clazz, logger);
        }

        return logger;
    }

    /**
     * {@link ENdoSnipeLogger} を初期化します。<br />
     */
    protected static synchronized void initialize()
    {
        initialized__ = true;

        // Eclipse 環境かどうかをチェックする
        if (EclipseUtil.isEclipseAvailable() == true)
        {
            // Eclipse 環境である
            isEclipseAvailable__ = true;
            EclipseENdoSnipeLogger.initialize();
        }
        else
        {
            // Eclipse 環境ではない場合
            isEclipseAvailable__ = false;
        }
    }

    /**
     * リソースを開放します。<br />
     */
    public static synchronized void dispose()
    {
        LogFactory.releaseAll();
        LOGGERS.clear();
        initialized__ = false;
        isEclipseAvailable__ = false;
    }

    private static ENdoSnipeLogger createENdoSnpeLogger(final Class<?> clazz,
            final PluginProvider provider)
    {
        if (useSystemLogger__ == true)
        {
            // SystemLogger を利用する場合
            return new SystemENdoSnipeLogger();
        }
        else if (isEclipseAvailable__)
        {
            // Eclipse 環境の場合
            return new EclipseENdoSnipeLogger(provider);
        }
        else
        {
            // どちらでもない場合
            return new Log4jENdoSnipeLogger(clazz);
        }
    }

    /**
     * XML ファイル形式のコンフィグレーションを追加します。<br />
     * コンフィグレーションファイルの書式は log4j に準じます。
     * 
     * @param config コンフィグレーションファイルの {@link URL}
     */
    public void addXmlConfig(final URL config)
    {
        AssertionUtil.assertNotNull("config", config);
        DOMConfigurator.configure(config);
    }

    /**
     * properties ファイル形式のコンフィグレーションを追加します。<br />
     * コンフィグレーションファイルの書式は log4j に準じます。
     * 
     * @param config コンフィグレーションファイルの {@link URL}
     */
    public void addPropertyConfig(final URL config)
    {
        AssertionUtil.assertNotNull("config", config);
        PropertyConfigurator.configure(config);
    }

    /**
     * TRACE情報が出力されるかどうかを返します。<br />
     * {@link SystemLogger} が利用されている場合、DEBUG情報が出力されるかどうかを返します。
     * 
     * @return TRACE情報が出力されるかどうか
     */
    public abstract boolean isTraceEnabled();

    /**
     * TRACE情報を出力します。<br />
     * 
     * @param message メッセージ
     * @param throwable {@link Throwable} オブジェクト
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void trace(final Object message, final Throwable throwable);

    /**
     * TRACE情報を出力します。<br />
     * 
     * @param message メッセージ
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void trace(final Object message);

    /**
     * DEBUG情報が出力されるかどうかを返します。<br />
     * 
     * @return DEBUG情報が出力されるかどうか
     */
    public abstract boolean isDebugEnabled();

    /**
     * DEBUG情報を出力します。<br />
     * 
     * @param message メッセージ
     * @param throwable {@link Throwable} オブジェクト
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void debug(final Object message, final Throwable throwable);

    /**
     * DEBUG情報を出力します。<br />
     * 
     * @param message メッセージ
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void debug(final Object message);

    /**
     * INFO情報が出力されるかどうかを返します。<br />
     * 
     * @return INFO情報が出力されるかどうか
     */
    public abstract boolean isInfoEnabled();

    /**
     * INFO情報を出力します。<br />
     * 
     * @param message メッセージ
     * @param throwable {@link Throwable} オブジェクト
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void info(final Object message, final Throwable throwable);

    /**
     * INFO情報を出力します。<br />
     * 
     * @param message メッセージ
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void info(final Object message);

    /**
     * WARN情報を出力します。<br />
     * 
     * @param message メッセージ
     * @param throwable {@link Throwable} オブジェクト
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void warn(final Object message, final Throwable throwable);

    /**
     * WARN情報を出力します。<br />
     * 
     * @param throwable メッセージ
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public void warn(final Throwable throwable)
    {
        this.warn(throwable.toString(), throwable);
    }

    /**
     * WARN情報を出力します。<br />
     * 
     * @param message メッセージ
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void warn(final Object message);

    /**
     * ERROR情報を出力します。<br />
     * 
     * @param message メッセージ
     * @param throwable {@link Throwable} オブジェクト
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void error(final Object message, final Throwable throwable);

    /**
     * ERROR情報を出力します。<br />
     * 
     * @param message メッセージ
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void error(final Object message);

    /**
     * FATAL情報を出力します。<br />
     * 
     * @param message メッセージ
     * @param throwable {@link Throwable} オブジェクト
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void fatal(final Object message, final Throwable throwable);

    /**
     * FATAL情報を出力します。<br />
     * 
     * @param message メッセージ
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。替わりにに log() メソッドを使用してください。
     */
    @Deprecated
    public abstract void fatal(final Object message);

    /**
     * ログを出力します。<br />
     * 
     * @param throwable {@link Throwable} オブジェクト
     * @deprecated 本メソッドは旧コードの互換性のために用意されています。
     * 替わりに {@link #log(String, Throwable, Object...)} メソッドを使用してください。
     */
    @Deprecated
    public void log(final Throwable throwable)
    {
        error(throwable.getClass().getName() + " : " + throwable.getMessage(), throwable);
    }

    /**
     * メッセージコードを使用してログを出力します。<br />
     * 
     * @param messageCode メッセージコード
     * @param args 引数
     */
    public void log(final String messageCode, final Object... args)
    {
        log(messageCode, null, args);
    }

    /**
     * メッセージコードを使用してログを出力します。<br />
     * 
     * @param messageCode メッセージコード
     * @param throwable {@link Throwable} オブジェクト
     * @param args 引数
     */
    public void log(final String messageCode, final Throwable throwable, final Object... args)
    {
        char messageType = messageCode.charAt(0);
        if (isEnabledFor(messageType))
        {
            String message = getMessage(messageCode, args);

            switch (messageType)
            {
            case 'T':
                trace(message, throwable);
                break;
            case 'D':
                debug(message, throwable);
                break;
            case 'I':
                info(message, throwable);
                break;
            case 'W':
                warn(message, throwable);
                break;
            case 'E':
                error(message, throwable);
                break;
            case 'F':
                fatal(message, throwable);
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(messageType));
            }
        }
    }

    /**
     * propertiesファイルに記述されたメッセージを取得します。<br />
     * 
     * @param messageCode メッセージのコード
     * @param args 置換するメッセージ
     * @return メッセージ
     */
    protected String getMessage(final String messageCode, final Object... args)
    {
        return MessageFormatter.getMessage(messageCode, args);
    }

    /**
     * 
     * @param messageType メッセージタイプ
     * @return 
     */
    protected abstract boolean isEnabledFor(final char messageType);

    /**
     * オブジェクトの詳細情報を返します。<br />
     * 
     * @param obj オブジェクト
     * @return 詳細情報
     */
    public static String getObjectDescription(final Object obj)
    {
        if (obj != null)
        {
            return obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode());
        }
        else
        {
            return "NULL";
        }
    }

    /**
     * messageのtoStringを呼び出す。
     * nullの場合は"Unknown Error"を出力する。
     * 
     * @param message メッセージオブジェクト。
     * @return messageのtoString結果。nullの場合は"Unknown Error"
     */
    protected String createMessage(final Object message)
    {
        String messageString;

        if (message != null)
        {
            messageString = message.toString();
        }
        else
        {
            messageString = getMessage(CommonLogMessageCodes.UNEXPECTED_ERROR);
        }
        return messageString;
    }
}
