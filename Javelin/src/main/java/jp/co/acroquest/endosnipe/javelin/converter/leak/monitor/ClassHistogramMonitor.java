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
package jp.co.acroquest.endosnipe.javelin.converter.leak.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;

public abstract class ClassHistogramMonitor
{

    /** クラスヒストグラムのリスト。 */
    private List<ClassHistogramEntry> histgramList_;

    /** 前回情報取得時の時刻。 */
    private volatile long             prevTime_;

    /** 設定 */
    private JavelinConfig             javelinConfig_;

    public ClassHistogramMonitor()
    {
        histgramList_ = new ArrayList<ClassHistogramEntry>();
        prevTime_ = 0L;
        javelinConfig_ = new JavelinConfig();
    }

    /**
     * クラスヒストグラムを取得し、結果を返す。<br>
     * 負荷を減らすため、前回取得した時刻から設定した時間経過している場合のみ再取得を行う。<br>
     * それ以外の場合は前回取得した情報をそのまま返す。<br>
     * 設定パラメータは以下の通り。<br>
     * <ul>
     * <li>javelin.leak.class.histo クラスヒストグラムを取得するかどうか。
     * <li>javelin.leak.class.histo.interval クラスヒストグラム取得間隔(ミリ秒)。
     * <li>javelin.leak.class.histo.max クラスヒストグラムの上位何件を取得するか。
     * </ul>
     *
     * ヒープヒストグラム取得が OFF の場合は、 <code>null</code> を返す。<br />
     *
     * @return 取得したクラスヒストグラムの結果。
     */
    public List<ClassHistogramEntry> getHistogramList()
    {
        if (javelinConfig_.getClassHisto() == false)
        {
            return new ArrayList<ClassHistogramEntry>();
        }

        synchronized (histgramList_)
        {
            long currentTime = System.currentTimeMillis();
            if (currentTime - prevTime_ > javelinConfig_.getClassHistoInterval())
            {
                updateHistogram();
                prevTime_ = currentTime;
            }
        }

        return histgramList_;
    }

    private void updateHistogram()
    {
        if (javelinConfig_.getClassHisto() == false)
        {
            return;
        }

        synchronized (histgramList_)
        {
            histgramList_.clear();
            BufferedReader heapHistoReader = null;
            try
            {
                boolean classHistoGC = javelinConfig_.getClassHistoGC();
                heapHistoReader = newReader(classHistoGC);
                if (heapHistoReader == null)
                {
                    // ヒープヒストグラムが取得できない場合は、GCのみ実施する。
                    if (javelinConfig_.getClassHistoGC())
                    {
                        System.gc();
                    }

                    return;
                }

                String line;
                while ((line = heapHistoReader.readLine()) != null)
                {
                    if (SystemLogger.getInstance().isDebugEnabled())
                    {
                        SystemLogger.getInstance().debug(line);
                    }

                    String[] splitLine = line.trim().split(" +");
                    try
                    {
                        ClassHistogramEntry entry = parseEntry(splitLine);
                        if (entry == null)
                        {
                            continue;
                        }
                        histgramList_.add(entry);

                        if (histgramList_.size() >= javelinConfig_.getClassHistoMax())
                        {
                            break;
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                        SystemLogger.getInstance().warn(nfe);
                    }
                }
            }
            catch (IOException ioe)
            {
                SystemLogger.getInstance().warn(ioe);
            }
            finally
            {
                if (heapHistoReader != null)
                {
                    try
                    {
                        heapHistoReader.close();
                    }
                    catch (IOException ioe)
                    {
                        SystemLogger.getInstance().warn(ioe);
                    }
                }
            }
        }
    }

    /**
     * ヒストグラムの文字列を読み込むReaderを生成する。
     * 
     * @param classHistoGC ヒストグラム取得時にGCするかどうか
     * @return　ヒストグラムの文字列を読み込むReader。
     * @throws IOException ヒストグラム取得時にIOエラーが発生
     */
    public abstract BufferedReader newReader(boolean classHistoGC)
        throws IOException;

    /**
     * 1行をパースして、ClassHistogramEntryを生成する。
     * @param splitLine 1行
     * @return ClassHistogramEntry、パースに失敗した場合はnull
     */
    protected abstract ClassHistogramEntry parseEntry(final String[] splitLine);
}
