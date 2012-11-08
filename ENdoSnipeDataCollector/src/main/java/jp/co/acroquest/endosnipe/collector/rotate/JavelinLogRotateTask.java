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
package jp.co.acroquest.endosnipe.collector.rotate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import jp.co.acroquest.endosnipe.collector.ENdoSnipeDataCollectorPluginProvider;
import jp.co.acroquest.endosnipe.collector.LogMessageCodes;
import jp.co.acroquest.endosnipe.collector.config.RotateConfig;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.data.dao.JavelinLogDao;
import jp.co.acroquest.endosnipe.data.db.ConnectionManager;

/**
 * Javelinログのローテートを行うタスク
 * 
 * @author S.Kimura
 */
public class JavelinLogRotateTask implements LogRotateTask
{
    /** ロガー。 */
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(JavelinLogRotateTask.class,
                                      ENdoSnipeDataCollectorPluginProvider.INSTANCE);

    /** ローテート用設定 */
    private final RotateConfig config_;

    /**
     * コンストラクタ
     * 
     * @param config ローテート用設定
     */
    public JavelinLogRotateTask(final RotateConfig config)
    {
        this.config_ = config;
    }

    /**
     * {@inheritDoc}
     */
    public void rotate()
    {
        int period = this.config_.getJavelinRotatePeriod();
        if (period <= 0)
        {
            return;
        }

        Calendar deleteTimeCalender =
                RotateUtil.getBeforeDate(this.config_.getJavelinUnitByCalendar(), period);

        Timestamp deleteLimit = new Timestamp(deleteTimeCalender.getTimeInMillis());

        String databaseName = this.config_.getDatabase();
        if (ConnectionManager.getInstance().existsDatabase(databaseName))
        {
            try
            {
                JavelinLogDao.deleteOldRecordByTime(databaseName, deleteLimit);
                LOGGER.log(LogMessageCodes.JAVELINLOG_ROTATE, new Object[]{databaseName,
                        deleteLimit});
            }
            catch (SQLException ex)
            {
                LOGGER.log(LogMessageCodes.JAVELINLOG_ROTATE_FAIL, ex, new Object[]{databaseName,
                        deleteLimit});
            }
        }
    }
}
