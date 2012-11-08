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
package jp.co.acroquest.endosnipe.collector.preference;

import jp.co.acroquest.endosnipe.collector.ENdoSnipeDataCollectorPlugin;
import jp.co.acroquest.endosnipe.collector.config.DataCollectorConfig;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * DataCollector 用のプリファレンス初期化クラス。<br />
 *
 * @author S.Kimura
 */
public class DataCollectorPreferenceInitializer extends AbstractPreferenceInitializer
{
    /**
     * Default constructor for the class.
     */
    public DataCollectorPreferenceInitializer()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = ENdoSnipeDataCollectorPlugin.getDefault().getPreferenceStore();
        store.setDefault(DataCollectorConfig.JAVELIN_LOG_MAX_KEY,
                         DataCollectorConfig.DEF_JAVELIN_LOG_MAX);
        store.setDefault(DataCollectorConfig.MEASUREMENT_LOG_MAX_KEY,
                         DataCollectorConfig.DEF_MEASUREMENT_LOG_MAX);
    }
}
