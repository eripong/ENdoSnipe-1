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
package jp.co.acroquest.endosnipe.data.preference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jp.co.acroquest.endosnipe.data.util.DataAccessorMessages;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * {@link DatabaseItem} の内容をテーブルビューワに表示するクラス。<br />
 *
 * @author sakamoto
 */
public class DatabaseItemLabelProvider extends LabelProvider implements ITableLabelProvider
{
    private static final int COLUMN_DBNAME = 0;

    private static final int COLUMN_HOSTNAME = 1;

    private static final int COLUMN_IPADDRESS = 2;

    private static final int COLUMN_PORT = 3;

    private static final int COLUMN_START_TIME = 4;

    private static final int COLUMN_END_TIME = 5;

    /**
     * {@inheritDoc}
     */
    public String getColumnText(Object element, int columnIndex)
    {
        DatabaseItem databaseItem = (DatabaseItem)element;
        String result = "";
        switch (columnIndex)
        {
        case COLUMN_DBNAME:
            result = databaseItem.getDatabaseName();
            break;
        case COLUMN_HOSTNAME:
            result = databaseItem.getHostName();
            break;
        case COLUMN_IPADDRESS:
            result = databaseItem.getIpAddress();
            break;
        case COLUMN_PORT:
            int port = databaseItem.getPort();
            if (port != -1)
            {
                result = String.valueOf(port);
            }
            break;
        case COLUMN_START_TIME:
            long startTime = databaseItem.getStartTime();
            long endTime = databaseItem.getEndTime();
            if (startTime != 0 && endTime != 0)
            {
                result = getStoreTermString(startTime, endTime);
            }
            break;
        case COLUMN_END_TIME:
            result = databaseItem.getDescription();
            break;
        default:
            break;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Image getColumnImage(Object element, int columnIndex)
    {
        return null;
    }

    private String getStoreTermString(long startTime, long endTime)
    {
        String key = "data.accessor.accumurationPeriodValue";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String start = dateFormat.format(startTime);
        String end = dateFormat.format(endTime);
        String message = DataAccessorMessages.getMessage(key, start, end);
        return message;
    }

}
