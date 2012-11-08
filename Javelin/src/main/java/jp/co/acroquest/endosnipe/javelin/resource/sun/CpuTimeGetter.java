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
package jp.co.acroquest.endosnipe.javelin.resource.sun;

import java.lang.management.ManagementFactory;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.javelin.resource.AbstractResourceGetter;

import com.sun.management.OperatingSystemMXBean;

/**
 * CPUéûä‘ÇéÊìæÇ∑ÇÈÉNÉâÉXÅB
 * 
 * @author Sakamoto
 */
public class CpuTimeGetter extends AbstractResourceGetter 
{
    
    /** ê›íË */
    private JavelinConfig config_ = new JavelinConfig();

    /**
     * {@inheritDoc}
     */
    public Number getValue()
    {
        OperatingSystemMXBean bean =
                (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        return Long.valueOf(bean.getProcessCpuTime() * this.config_.getCpuTimeUnit());
    }

    /**
     * {@inheritDoc}
     */
    public ItemType getItemType()
    {
        return ItemType.ITEMTYPE_LONG;
    }
}
