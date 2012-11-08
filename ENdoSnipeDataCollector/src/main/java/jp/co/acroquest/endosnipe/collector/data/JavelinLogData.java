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
package jp.co.acroquest.endosnipe.collector.data;

import java.io.File;

/**
 * Javelin ログを表す {@link JavelinData} です。<br />
 * Javelin ログは {@link String} オブジェクトでオンメモリに保持するか、
 * 保存された一時ファイル {@link File} オブジェクトとして保持します。<br />
 * 
 * @author y-komori
 */
public class JavelinLogData extends AbstractJavelinData
{
    private String logFileName_;

    private File   file_;

    private String contents_;

    /** アラーム閾値 */
    private long   alarmThreshold_;

    /** アラームCPU閾値 */
    private long   cpuAlarmThreshold_;

    /**
     * ログ内容を渡して {@link JavelinLogData} を構築します。<br />
     * 
     * @param contents ログ内容
     */
    public JavelinLogData(final String contents)
    {
        if (contents == null)
        {
            throw new IllegalArgumentException("contents can't be null");
        }

        this.contents_ = contents;
    }

    /**
     * ログファイルを {@link JavelinLogData} を構築します。<br />
     * 
     * @param file Javelin ログファイルを表す {@link File} オブジェクト
     */
    public JavelinLogData(final File file)
    {
        if (file == null)
        {
            throw new IllegalArgumentException("file can't be null");
        }

        this.file_ = file;
    }

    /**
     * ログ保存先の {@link File} オブジェクトを返します。<br />
     * 
     * @return ログ保存先の {@link File} オブジェクト
     */
    public File getFile()
    {
        return file_;
    }

    /**
     * 保持している一時ファイルを削除します。<br />
     * 
     * @return 削除に成功した場合は <code>true</code>
     */
    public boolean deleteFile()
    {
        if (file_ != null)
        {
            boolean result = file_.delete();
            return result;
        }
        else
        {
            return true;
        }
    }

    /**
     * ログ内容を返します。<br />
     * 
     * @return ログ内容
     */
    public String getContents()
    {
        return contents_;
    }

    /**
     * ログファイル名を返します。<br />
     * 
     * @return ログファイル名
     */
    public String getLogFileName()
    {
        return logFileName_;
    }

    /**
     * ログファイル名を設定します。<br />
     * 
     * @param logFileName ログファイル名
     */
    public void setLogFileName(final String logFileName)
    {
        logFileName_ = logFileName;
    }

    public long getAlarmThreshold()
    {
        return alarmThreshold_;
    }

    public void setAlarmThreshold(final long alarmThreshold)
    {
        alarmThreshold_ = alarmThreshold;
    }

    public long getCpuAlarmThreshold()
    {
        return cpuAlarmThreshold_;
    }

    public void setCpuAlarmThreshold(final long cpuAlarmThreshold)
    {
        cpuAlarmThreshold_ = cpuAlarmThreshold;
    }

    /* (non-Javadoc)
     * @see jp.co.acroquest.endosnipe.collector.data.AbstractJavelinData#getAdditionalString()
     */
    @Override
    protected String getAdditionalString()
    {
        StringBuilder builder = new StringBuilder(64);
        if (logFileName_ != null)
        {
            builder.append(logFileName_);
        }
        if (file_ != null)
        {
            builder.append(" ");
            builder.append(file_.getAbsolutePath());
        }
        return builder.toString();
    }
}
