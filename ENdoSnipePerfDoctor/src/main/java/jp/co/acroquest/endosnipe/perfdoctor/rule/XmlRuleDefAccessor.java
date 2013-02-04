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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.perfdoctor.exception.RuleCreateException;
import jp.co.acroquest.endosnipe.perfdoctor.exception.RuleNotFoundException;
import jp.co.acroquest.endosnipe.perfdoctor.rule.def.RuleSetDef;

/**
 * ルール定義の追加、変更、削除、参照を行うクラス。XMLファイルを利用する。
 * @author tanimoto
 *
 */
public class XmlRuleDefAccessor implements RuleDefAccessor
{
    private static final ENdoSnipeLogger LOGGER         =
                                                          ENdoSnipeLogger.getLogger(RuleDefAccessor.class,
                                                                                    null);

    /** 文字コードUTF-8を表す文字列 */
    private static final String          ENCODING_UTF_8 = "utf-8";

    /** データをXMLに戻す際の制御を行うオブジェクト */
    private Marshaller                   marshaller_;

    /** データをXMLから取得する際の制御を行うオブジェクト */
    private Unmarshaller                 unmarshaller_;

    /**
     * コンストラクタ。marshaller/unmarshallerを初期化する。
     */
    public XmlRuleDefAccessor()
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(RuleSetDef.class);
            this.marshaller_ = context.createMarshaller();
            this.marshaller_.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            this.unmarshaller_ = context.createUnmarshaller();
        }
        catch (JAXBException ex)
        {
            // ignore
            ex.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}<br>
     * XMLファイルより定義を読み込む。
     * @param fileName 読み込むファイルの名前
     * @return ルールセット定義(RuleSetDef)
     * @throws RuleNotFoundException ファイルが見つからない場合
     * @throws RuleCreateException 読み込みに失敗した場合
     */
    public RuleSetDef findRuleSet(final String fileName)
        throws RuleNotFoundException,
            RuleCreateException
    {
        URL url = createURL(fileName);
        if (url == null)
        {
            throw new RuleNotFoundException("ResourceNotFoundMessage", new Object[]{fileName});
        }

        InputStream stream = null;
        try
        {
            stream = url.openStream();
            return (RuleSetDef)this.unmarshaller_.unmarshal(stream);
        }
        catch (IOException ex)
        {
            throw new RuleCreateException("ResourceReadError", new Object[]{url});
        }
        catch (JAXBException ex)
        {
            throw new RuleCreateException("ResourceReadError", new Object[]{url});
        }
        finally
        {
            if (stream != null)
            {
                try
                {
                    stream.close();
                }
                catch (IOException ex)
                {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * ファイル名よりURLを作成する。<br>
     * 物理ファイルが存在した場合は、file://から始まるURLを作成し、<br>
     * そうでない場合はクラスローダーがファイルを探してURLを作成する。<br>
     * ファイルが見つからない場合はnullを返す。
     * @param fileName ファイル名
     * @return URL
     */
    protected URL createURL(final String fileName)
    {
        File file = new File(fileName);

        URL url = null;
        if (file.exists() && file.isFile())
        {
            try
            {
                url = new URL("file", "", file.getAbsolutePath());
            }
            catch (MalformedURLException ex)
            {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        else
        {
            url = XmlRuleDefAccessor.class.getResource(fileName);
        }

        return url;
    }

    /**
     * {@inheritDoc}<br>
     * XMLファイルに定義を書き込む。
     * @param ruleSetDef ルールセット定義(RuleSetDef)
     * @param fileName 書き込むファイルの名前
     */
    public void updateRuleSet(final RuleSetDef ruleSetDef, final String fileName)
    {
        File file = new File(fileName);
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;

        try
        {
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, ENCODING_UTF_8);
            this.marshaller_.marshal(ruleSetDef, outputStreamWriter);
        }
        catch (JAXBException ex)
        {
            LOGGER.error(ex.getMessage(), ex);
        }
        catch (UnsupportedEncodingException ex)
        {
            LOGGER.error(ex.getMessage(), ex);
        }
        catch (FileNotFoundException ex)
        {
            LOGGER.error(ex.getMessage(), ex);
        }
        finally
        {
            if (outputStreamWriter != null)
            {
                try
                {
                    outputStreamWriter.close();
                }
                catch (IOException ex)
                {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }

        return;
    }
}
