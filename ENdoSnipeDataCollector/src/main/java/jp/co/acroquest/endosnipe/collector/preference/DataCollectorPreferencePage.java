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
import jp.co.acroquest.endosnipe.collector.util.DataCollectorMessages;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * DataCollector設定ページ
 * 
 * @author S.Kimura
 */
public class DataCollectorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    /** Javelinログ最大蓄積件数ラベル */
    private Label labelMaxJavelinlogRecords_;

    /** Javelinログ最大蓄積件数入力フィールド */
    private Text textMaxJavelinlogRecords_;

    /** 計測データ最大蓄積件数ラベル */
    private Label labelMaxMeasurementlogRecords_;

    /** 計測データ最大蓄積件数入力フィールド */
    private Text textMaxMeasurementlogRecords_;

    /**
     * {@inheritDoc}
     */
    public void init(final IWorkbench workbench)
    {
        // Do Nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(final Composite parent)
    {
        IPreferenceStore store = ENdoSnipeDataCollectorPlugin.getDefault().getPreferenceStore();

        Composite backgroundComposite = new Composite(parent, SWT.NONE);
        backgroundComposite.setLayout(new GridLayout(1, false));
        backgroundComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite mainComposite = new Composite(backgroundComposite, SWT.NONE);
        mainComposite.setLayout(new GridLayout(2, false));
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        this.labelMaxJavelinlogRecords_ = new Label(mainComposite, SWT.NONE);
        this.labelMaxJavelinlogRecords_.setText(DataCollectorMessages.getMessage("collector.javelin.log.max.record")
                + ":");
        this.labelMaxJavelinlogRecords_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        this.textMaxJavelinlogRecords_ = new Text(mainComposite, SWT.BORDER);
        int maxJavelinlogRecords = store.getInt(DataCollectorConfig.JAVELIN_LOG_MAX_KEY);
        this.textMaxJavelinlogRecords_.setText(Integer.toString(maxJavelinlogRecords));
        this.textMaxJavelinlogRecords_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        this.labelMaxMeasurementlogRecords_ = new Label(mainComposite, SWT.NONE);
        this.labelMaxMeasurementlogRecords_.setText(DataCollectorMessages.getMessage("collector.measurement.log.max.record")
                + ":");
        this.labelMaxMeasurementlogRecords_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        this.textMaxMeasurementlogRecords_ = new Text(mainComposite, SWT.BORDER);
        int maxMeasurementlogRecords = store.getInt(DataCollectorConfig.MEASUREMENT_LOG_MAX_KEY);
        this.textMaxMeasurementlogRecords_.setText(Integer.toString(maxMeasurementlogRecords));
        this.textMaxMeasurementlogRecords_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return mainComposite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performDefaults()
    {
        IPreferenceStore store = ENdoSnipeDataCollectorPlugin.getDefault().getPreferenceStore();
        int defaultMaxJavelinlogRecords =
                store.getDefaultInt(DataCollectorConfig.JAVELIN_LOG_MAX_KEY);
        this.textMaxJavelinlogRecords_.setText(Integer.toString(defaultMaxJavelinlogRecords));

        int defaultMaxMeasurementlogRecords =
                store.getDefaultInt(DataCollectorConfig.MEASUREMENT_LOG_MAX_KEY);
        this.textMaxMeasurementlogRecords_.setText(Integer.toString(defaultMaxMeasurementlogRecords));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performApply()
    {
        performOk();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performOk()
    {
        //入力値制限チェック
        if (isValidInt(this.textMaxJavelinlogRecords_, DataCollectorConfig.MIN_JAVELIN_LOG_MAX,
                       Integer.MAX_VALUE) == false)
        {

            showInputErrorDialog(DataCollectorMessages.getMessage("collector.javelin.log.max.record"));
            return false;
        }

        if (isValidInt(this.textMaxMeasurementlogRecords_,
                       DataCollectorConfig.MIN_MEASUREMENT_LOG_MAX, Integer.MAX_VALUE) == false)
        {

            showInputErrorDialog(DataCollectorMessages.getMessage("collector.measurement.log.max.record"));
            return false;
        }

        //設定値反映
        IPreferenceStore store = ENdoSnipeDataCollectorPlugin.getDefault().getPreferenceStore();
        store.setValue(DataCollectorConfig.JAVELIN_LOG_MAX_KEY,
                       Integer.parseInt(this.textMaxJavelinlogRecords_.getText()));
        store.setValue(DataCollectorConfig.MEASUREMENT_LOG_MAX_KEY,
                       Integer.parseInt(this.textMaxMeasurementlogRecords_.getText()));
        return true;
    }

    /**
     * 引数に指定したパラメータを用いて入力エラーダイアログを表示
     * 
     * @param target 対象パラメータ
     */
    private void showInputErrorDialog(final String target)
    {
        Object[] args = {target};
        String errorMessage =
                DataCollectorMessages.getMessage("collector.preference.inputErrorMessage", args);

        Shell shell = getShell();
        if (shell == null)
        {
            shell = new Shell();
        }
        MessageDialog.openError(
                                shell,
                                DataCollectorMessages.getMessage("collector.preference.inputErrorWindow"),
                                errorMessage);
    }

    /**
     * 対象のテキストに格納されている値が入力制限を満たしているかを判定
     * 
     * @param target 対象テキストフィールド
     * @param min 最大値 
     * @param max 最小値
     * @return true 入力制限を満たしている場合
     *         false 入力制限を満たしていない場合
     */
    private boolean isValidInt(final Text target, final int min, final int max)
    {
        String targetText = target.getText();
        if (targetText == null)
        {
            return false;
        }

        int targetInt;

        try
        {
            targetInt = Integer.parseInt(targetText);
        }
        catch (NumberFormatException nfex)
        {
            return false;
        }

        if (targetInt < min || max < targetInt)
        {
            return false;
        }

        return true;
    }
}
