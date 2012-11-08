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
package jp.co.acroquest.endosnipe.common.ui;

import jp.co.acroquest.endosnipe.common.Constants;
import jp.co.acroquest.endosnipe.common.ENdoSnipeCommonPlugin;
import jp.co.acroquest.endosnipe.common.Messages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * ENdoSnipeCommon の設定を行うためのプリファレンスページです。<br />
 * 
 * @author y-komori
 */
public class ENdoSnipeCommonPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage, Constants
{
    private static final String[][] LOG_LEVELS =
            {{Messages.ENDOSNIPECOMMON_LOGLEVEL_DEBUG, Integer.toString(IStatus.OK)},
                    {Messages.ENDOSNIPECOMMON_LOGLEVEL_INFO, Integer.toString(IStatus.INFO)},
                    {Messages.ENDOSNIPECOMMON_LOGLEVEL_WARNING, Integer.toString(IStatus.WARNING)},
                    {Messages.ENDOSNIPECOMMON_LOGLEVEL_ERROR, Integer.toString(IStatus.ERROR)}};

    /**
     * {@link ENdoSnipePreferencePage} を構築します。<br />
     */
    public ENdoSnipeCommonPreferencePage()
    {
        super(GRID);
        setPreferenceStore(ENdoSnipeCommonPlugin.getDefault().getPreferenceStore());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createFieldEditors()
    {
        Composite parent = getFieldEditorParent();

        addField(new ComboFieldEditor(PREF_LOG_LEVEL, Messages.ENDOSNIPECOMMON_LOGLEVEL,
                                      LOG_LEVELS, parent));
    }

    /**
     * {@inheritDoc}
     */
    public void init(final IWorkbench workbench)
    {
        // Do nothing.
    }
}
