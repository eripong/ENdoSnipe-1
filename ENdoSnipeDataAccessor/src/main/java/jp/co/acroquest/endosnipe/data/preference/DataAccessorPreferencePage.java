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

import java.util.List;

import jp.co.acroquest.endosnipe.data.db.ConnectionManager;
import jp.co.acroquest.endosnipe.data.db.DBManager;
import jp.co.acroquest.endosnipe.data.util.DataAccessorConfigUtil;
import jp.co.acroquest.endosnipe.data.util.DataAccessorMessages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * DataAccessorのプリファレンスページです。<br />
 * 
 * @author fujii
 *
 */
public class DataAccessorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    /** 設定用ボタンのラベル用メッセージキー */
    private static final String PREFERENCE_KEY          = "data.accessor.preferencePage.preference";

    /** 接続先設定ボタン */
    private Button              settingButton_;

    /** データベース一覧を表示するテーブル */
    private TableViewer         tableDatabase_;

    /** テーブルのカラム数 */
    private static final int    GRID_COLUMNS            = 3;

    /** テーブルの横幅 */
    private static final int    COLUMN_WIDTH            = 120;

    /** データベース一覧のマージン（縦） */
    private static final int    DATABASELIST_MARGIN_TOP = 10;

    /**
     * コントローラを構築します。
     */
    public DataAccessorPreferencePage()
    {
    }

    /**
     *  {@inheritDoc}
     */
    public void init(final IWorkbench workbench)
    {
        DBPreferenceUtil.initDb();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(Composite parent)
    {
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        Composite composite = new Composite(parent, SWT.NONE);

        GridLayout buttonLayout = new GridLayout();
        buttonLayout.numColumns = 1;
        buttonComposite.setLayout(buttonLayout);

        this.settingButton_ = new Button(buttonComposite, SWT.PUSH);
        this.settingButton_.setText(DataAccessorMessages.getMessage(PREFERENCE_KEY));
        this.settingButton_.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                openSettingDialog();
            }
        });

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        Composite mainContents = new Composite(composite, SWT.NONE);
        mainContents.setLayout(new GridLayout(GRID_COLUMNS, false));
        mainContents.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label databaseListLabel = new Label(mainContents, SWT.NONE);
        databaseListLabel.setText(DataAccessorMessages.getMessage("data.accessor.dbList"));
        GridData databaseListLabelGridData = new GridData();
        databaseListLabelGridData.horizontalSpan = GRID_COLUMNS;
        databaseListLabelGridData.verticalAlignment = GridData.VERTICAL_ALIGN_END;
        databaseListLabelGridData.verticalIndent = DATABASELIST_MARGIN_TOP;
        databaseListLabel.setLayoutData(databaseListLabelGridData);

        createTable(mainContents);

        updateDatabaseList();

        return composite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(final Composite parent)
    {
        super.createControl(parent);

        this.settingButton_.setEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performOk()
    {
        if (DataAccessorConfigUtil.isBaseDirectoryChangeAllowed())
        {
            ConnectionManager manager = ConnectionManager.getInstance();
            manager.closeAll();
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performDefaults()
    {
        DBPreferenceUtil.initPreference();
        DBManager.readPreference();
        updateDatabaseList();
    }

    /**
     * データベース一覧を表示するテーブルを作成します。<br />
     *
     * @param mainContents 親コンポジット
     */
    private void createTable(Composite mainContents)
    {
        this.tableDatabase_ = new TableViewer(mainContents, SWT.BORDER | SWT.FULL_SELECTION);
        this.tableDatabase_.setContentProvider(new ArrayContentProvider());
        this.tableDatabase_.setLabelProvider(new DatabaseItemLabelProvider());

        Table table = this.tableDatabase_.getTable();

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        addTableHeader(table, "data.accessor.column.dbName", COLUMN_WIDTH);
        addTableHeader(table, "data.accessor.column.hostName", COLUMN_WIDTH);
        addTableHeader(table, "data.accessor.column.ipAddress", COLUMN_WIDTH);
        addTableHeader(table, "data.accessor.column.port", COLUMN_WIDTH);
        addTableHeader(table, "data.accessor.column.accumurationPeriod", COLUMN_WIDTH);
        addTableHeader(table, "data.accessor.column.description", COLUMN_WIDTH);

        GridData listFilesGridData = new GridData();
        listFilesGridData.horizontalSpan = GRID_COLUMNS;
        listFilesGridData.verticalAlignment = GridData.FILL;
        listFilesGridData.grabExcessVerticalSpace = true;
        table.setLayoutData(listFilesGridData);
    }

    /**
     * テーブルのヘッダを 1 列追加します。<br />
     *
     * @param table ヘッダを追加するテーブル 
     * @param nameKey ヘッダの文字列のキー
     * @param width ヘッダの幅
     */
    private void addTableHeader(Table table, String nameKey, int width)
    {
        TableColumn column = new TableColumn(table, SWT.NULL);
        column.setText(DataAccessorMessages.getMessage(nameKey));
        column.setWidth(width);
    }

    /**
     * DB設定ダイアログを開きます。<br />
     */
    public void openSettingDialog()
    {
        DBSettingDialog dialog = new DBSettingDialog(getShell(), this);
        dialog.setBlockOnOpen(true);

        dialog.open();
    }

    /**
     * データベース一覧を更新します。
     */
    public void updateDatabaseList()
    {
        this.tableDatabase_.getTable().removeAll();
        List<DatabaseItem> databaseList = DataAccessorConfigUtil.getDatabaseList();
        this.tableDatabase_.setInput(databaseList);
    }
}
