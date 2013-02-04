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
package jp.co.acroquest.endosnipe.perfdoctor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogAccessor;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogFileAccessor;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.javelin.parser.ParseException;
import jp.co.acroquest.endosnipe.perfdoctor.rule.AbstractRule;
import junit.framework.TestCase;

/**
 * PerformanceRuleの抽象テストケースクラス。<br>
 * PerformanceRuleのテストケースは、このクラスを継承して作成する。
 * 
 * @author tanimoto
 */
public abstract class PerformanceRuleTestCase extends TestCase
{
    private static final ENdoSnipeLogger LOGGER;

    private AbstractRule                 rule_          = null;

    private static final Field           JAVELIN_LOG_ELEMENT_LIST;

    private static final Field           ARGS_LIST;

    private static Field                 warningList__;

    private static final String          MESSAGE_FORMAT = "{0} message:({1})";

    static
    {
        Field elementListField = null;
        Field argsListField = null;
        Field warningListField = null;
        LOGGER = getLogger();

        try
        {

            elementListField = AbstractRule.class.getDeclaredField("errorJavelinLogElementList_");
            elementListField.setAccessible(true);
            argsListField = AbstractRule.class.getDeclaredField("argsList_");
            argsListField.setAccessible(true);
            warningListField = AbstractRule.class.getDeclaredField("warningList_");
            warningListField.setAccessible(true);
        }
        catch (Exception ex)
        {
            // ignore
            LOGGER.error(ex.getMessage(), ex);
        }

        JAVELIN_LOG_ELEMENT_LIST = elementListField;
        ARGS_LIST = argsListField;
        warningList__ = warningListField;
    }

    private static ENdoSnipeLogger getLogger()
    {
        return ENdoSnipeLogger.getLogger(PerformanceRuleTestCase.class);
    }

    /**
     * ルールクラスのインスタンスを作成する。<br>
     * TestCaseを作成する場合には、必ずこのメソッドを利用してルールを作成すること。
     * 
     * @param <T> ルール型
     * @param clazz クラス
     * @return ルールクラスのインスタンス
     */
    protected <T extends AbstractRule> T createInstance(final Class<? extends T> clazz)
    {
        T t = null;
        try
        {
            t = clazz.newInstance();
        }
        catch (Exception ex)
        {
            // ignore
            LOGGER.error(ex.getMessage(), ex);
        }

        this.rule_ = t;

        try
        {
            ARGS_LIST.set(this.rule_, new ArrayList());
            JAVELIN_LOG_ELEMENT_LIST.set(this.rule_, new ArrayList());
            warningList__.set(this.rule_, new ArrayList());
        }
        catch (Exception ex)
        {
            // ignore
            LOGGER.error(ex.getMessage(), ex);
        }

        return t;
    }

    /**
     * 指定されたJavelinログファイルを読み込んで、ルールの実行を行う。
     * 
     * @param fileName Javelinログファイル名
     */
    protected void doJudgeFromJavelinLog(final String fileName)
    {
        List<JavelinLogElement> elementList = createJavelinLogElement(fileName);
        this.rule_.judge(elementList);
    }

    /**
     * Javelinログファイルを解析し、JavelinLogElementのリストを作成する。
     * 
     * @param fileName Javelinログファイル名
     * @return JavelinLogElementのリスト
     */
    protected List<JavelinLogElement> createJavelinLogElement(final String fileName)
    {
        List<JavelinLogElement> elementList = new ArrayList<JavelinLogElement>();
        String absolutePath = getAbsolutePath(fileName);
        JavelinLogAccessor logAccessor = new JavelinLogFileAccessor(absolutePath);
        JavelinParser parser = new JavelinParser(logAccessor);

        try
        {
            parser.init();
            JavelinLogElement javelinLogElement;
            while ((javelinLogElement = parser.nextElement()) != null)
            {
                elementList.add(javelinLogElement);
            }
        }
        catch (ParseException ex)
        {
            LOGGER.error(ex.getMessage(), ex);
        }
        catch (IOException ex)
        {
            LOGGER.error(ex.getMessage(), ex);
        }
        finally
        {
            try
            {
                parser.close();
            }
            catch (IOException ex)
            {
                // ignore
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        return elementList;
    }

    /**
     * ファイルのパスを取得する。<br>
     * ファイルがクラスパス内にあれば、そのファイルのURLを返す。<br>
     * クラスパス内にない場合は、ファイルの絶対パスを返す。<br>
     * 
     * @param fileName ファイル名
     * @return ファイルのURLもしくは絶対パス。
     */
    protected String getAbsolutePath(final String fileName)
    {
        URL url = this.getClass().getResource(fileName);
        if (url == null)
        {
            File file = new File(fileName);
            return file.getAbsolutePath();
        }

        return url.getFile();
    }

    /**
     * 引数で指定したJavelinLogElementおよび引数に一致するエラーが<br>
     * 発生していたことをチェックする。
     * 
     * @param element JavelinLogElement
     * @param args エラーメッセージ引数
     */
    protected void assertErrorOccurred(final JavelinLogElement element, final Object... args)
    {
        if (containsError(element, args))
        {
            return;
        }

        String message = getMessage("Error with specified args not occurred.", element, args);
        fail(message);
    }

    /**
     * 引数で指定したJavelinLogElementおよび引数に一致する問題が<br>
     * 発生していないことを確認する。
     * 
     * @param element JavelinLogElement
     * @param args エラーメッセージ引数
     */
    protected void assertErrorNotOccurred(final JavelinLogElement element, final Object... args)
    {
        if (containsError(element, args))
        {
            String message = getMessage("Error with specified args occurred.", element, args);
            fail(message);
        }

        return;
    }

    /**
     * 引数で指定したJavelinLogElementおよび引数に一致する問題が<br>
     * 発生していたかどうかを確認する。
     * 
     * @param element JavelinLogElement
     * @param args エラーメッセージ引数
     * @return エラーが発生していた場合はtrue、そうでない場合はfalse。
     */
    private boolean containsError(final JavelinLogElement element, final Object... args)
    {
        List<JavelinLogElement> errorElementList = getErrorJavelinLogElements();
        List<Object[]> errorArgsList = getErrorArgs();

        for (int index = 0; index < errorElementList.size(); index++)
        {
            if (errorElementList.get(index) == element
                    && Arrays.equals(errorArgsList.get(index), args))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * 引数で指定したJavelinLogElementに対応するエラーがある場合に、
     * エラーメッセージ引数を返す。
     * 
     * @param element JavelinLogElement
     * @return 対応するエラーがある場合はエラーメッセージ引数。対応するエラーがない場合はnull。
     */
    public Object[] getErrorArgs(final JavelinLogElement element)
    {
        List<JavelinLogElement> errorElementList = getErrorJavelinLogElements();
        List<Object[]> errorArgsList = getErrorArgs();

        for (int index = 0; index < errorElementList.size(); index++)
        {
            if (errorElementList.get(index) == element)
            {
                return errorArgsList.get(index);
            }
        }

        return null;
    }

    /**
     * 問題を検出したJavelinLogElement一覧を返す。
     * @return JavelinLogElement一覧
     */
    protected List<JavelinLogElement> getErrorJavelinLogElements()
    {
        try
        {
            return (List<JavelinLogElement>)JAVELIN_LOG_ELEMENT_LIST.get(this.rule_);
        }
        catch (Exception ex)
        {
            // ignore
            LOGGER.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 問題を検出した際のargs一覧を返す。
     * @return args一覧
     */
    protected List<Object[]> getErrorArgs()
    {
        try
        {
            return (List<Object[]>)ARGS_LIST.get(this.rule_);
        }
        catch (Exception ex)
        {
            // ignore
            LOGGER.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 引数で指定したエラーメッセージに一致する問題が<br>
     * 発生していたかどうかを確認する。
     * 
     * @param message エラーメッセージ
     */
    protected void assertContainsMessage(final String message)
    {
        List<String> messages = getErrorMessages();
        if (messages.contains(message))
        {
            return;
        }

        fail("Error messages do not contain message <" + message + ">.");
    }

    /**
     * 引数で指定したエラーメッセージに一致する問題が<br>
     * 発生していないことを確認する。
     * 
     * @param message エラーメッセージ
     */
    protected void assertNotContainsMessage(final String message)
    {
        List<String> messages = getErrorMessages();
        if (messages.contains(message))
        {
            fail("Error messages contain message <" + message + ">.");
        }

        return;
    }

    /**
     * 検出した問題のエラーメッセージ一覧を取得する。
     * @return エラーメッセージ一覧
     */
    protected List<String> getErrorMessages()
    {
        List<JavelinLogElement> errorElementList = getErrorJavelinLogElements();
        List<Object[]> errorArgsList = getErrorArgs();

        List<String> messageList = new ArrayList<String>(errorElementList.size());
        for (int index = 0; index < errorElementList.size(); index++)
        {
            JavelinLogElement javelinLogElement = errorElementList.get(index);
            Object[] args = errorArgsList.get(index);

            WarningUnit unit =
                               WarningUnitUtil.createWarningUnit(null, this.rule_,
                                                                 javelinLogElement, true, args);
            messageList.add(unit.getDescription());
        }

        return messageList;
    }

    /**
     * 警告を、メッセージ文字列順にソートします。
     *
     * @param list 警告のリスト
     */
    protected void sortWarningUnit(final List<WarningUnit> list)
    {
        Collections.sort(list, new Comparator<WarningUnit>() {
            public int compare(final WarningUnit unit1, final WarningUnit unit2)
            {
                return unit1.getDescription().compareTo(unit2.getDescription());
            }
        });
    }

    private String getMessage(final String mainMessage, final JavelinLogElement element,
            final Object... args)
    {
        String description = getDescription(element, args);
        String message = MessageFormat.format(MESSAGE_FORMAT, mainMessage, description);
        return message;
    }

    private String getDescription(final JavelinLogElement element, final Object... args)
    {
        WarningUnit warningUnit =
                                  WarningUnitUtil.createWarningUnit(null, this.rule_, element,
                                                                    true, args);
        String description = warningUnit.getDescription();
        return description;
    }
}
