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
package jp.co.acroquest.endosnipe.web.dashboard.entity.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import jp.co.acroquest.endosnipe.web.dashboard.entity.AlarmNotifyEntity;

/**
 * AlarmEntityのComparatorクラスです。
 * @author fujii
 *
 */
public class AlarmEntityComparator implements Comparator<AlarmNotifyEntity>, Serializable
{
    /** シリアルID */
    private static final long serialVersionUID = 2683239014865567L;

    /**
     * {@inheritDoc}
     */
    public int compare(AlarmNotifyEntity entity1, AlarmNotifyEntity entity2)
    {
        Date date1 = entity1.timestamp;
        Date date2 = entity2.timestamp;
        if (date1.getTime() > date2.getTime())
        {
            return 1;
        }
        else if (date1.getTime() < date2.getTime())
        {
            return -1;
        }
        return 0;
    }

}
