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
package jp.co.acroquest.endosnipe.collector.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import jp.co.acroquest.endosnipe.collector.Bootstrap;
import jp.co.acroquest.endosnipe.collector.ENdoSnipeDataCollectorPluginProvider;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.PathUtil;

public class DisplayNameManager
{
    /** singletonインスタンス */
    private static DisplayNameManager     manager__ = new DisplayNameManager();

    /** 言語変換用マップ */
    private final HashMap<String, String> convMap_;

    /** プロパティファイルのプレフィクス */
    private final String                  PREFIX    = "../conf/displayname_";

    /** プロパティファイルの拡張子 */
    private final String                  EXTENSION = ".properties";

    /** コメント文字 */
    private final String                  COMMENT   = "#";

    /**
     * インスタンス化を防ぐprivateコンストラクタ
     */
    private DisplayNameManager()
    {
        convMap_ = new HashMap<String, String>(0);
    }

    /**
     * インスタンスを取得します。
     * 
     * @return インスタンス
     */
    public static DisplayNameManager getManager()
    {
        return manager__;
    }

    /**
     * 言語変換用のプロパティファイルを読み込み、変換マップを初期化します。<br />
     * 引数で指定された言語コードに合わせて<br />
     * プロパティファイル displayname_xx.properties を読み込みます。
     * 
     * @param lang 指定言語
     */
    public void init(final String lang)
    {
        // ログ出力用インスタンス
        final ENdoSnipeLogger logger =
                                       ENdoSnipeLogger.getLogger(
                                                                 Bootstrap.class,
                                                                 ENdoSnipeDataCollectorPluginProvider.INSTANCE);

        if (lang == null || "".equals(lang))
        {
            logger.error("表示言語が設定されていません。");
            return;
        }

        // プロパティファイルの絶対パスを取得
        String filename = PathUtil.getJarDir(Bootstrap.class) + PREFIX + lang + EXTENSION;
        File file = new File(filename);
        if (!file.exists())
        {
            logger.error("表示名設定ファイルが存在しません。");
            return;
        }
        else
        {
            logger.info("表示名設定ファイル " + filename + " を読み込みます。");
        }

        // プロパティファイルの読み込み
        BufferedReader in = null;
        try
        {
            in =
                 new BufferedReader(new InputStreamReader(new FileInputStream(file), "Windows-31J"));

            String line = "";
            // １行ずつ読み込み解析
            while (null != (line = in.readLine()))
            {
                // コメントは読み飛ばす
                if (line.startsWith(COMMENT))
                {
                    continue;
                }

                // イコールで分解
                String[] elements = line.split("=");
                if (elements.length != 2)
                {
                    continue;
                }

                logger.info("項目名[" + elements[0] + "] : 表示名[" + elements[1] + "]");
                this.convMap_.put(elements[0], elements[1]);
            }

            in.close();
        }
        catch (IOException e)
        {
            logger.error("表示名設定ファイルの読み込みに失敗しました。");
        }

        return;
    }

    /**
     * 表示名変換マップを取得します。
     * 
     * @return 表示名変換マップ
     */
    public HashMap<String, String> getConvMap()
    {
        return convMap_;
    }
}
