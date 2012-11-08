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
package jp.co.acroquest.endosnipe.javelin.log;

import java.util.Date;

import jp.co.acroquest.endosnipe.javelin.CallTree;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;

/**
 * Javelinログのタスクです。<br />
 * 
 * @author eriguchi
 *
 */
class JavelinLogTask
{
    private final Date date_;

    private final CallTree tree_;

    private final CallTreeNode node_;

    private final String jvnFileName_;

    private final JavelinLogCallback jvelinLogCallback_;

    private final CallTreeNode endNode_;

    private final long telegramId_;

    /**
     * コンストラクタです。引数で指定したパラメータを設定します。<br />
     * 
     * @param date 日時
     * @param jvnFileName Javelinファイル名
     * @param tree CallTree
     * @param node CallTreeNode
     * @param jvelinLogCallback コールバックオブジェクト
     * @param telegramId 電文 ID
     * @param endNode 最終ノード
     */
    public JavelinLogTask(final Date date, final String jvnFileName, final CallTree tree,
            final CallTreeNode node, final JavelinLogCallback jvelinLogCallback,
            final CallTreeNode endNode, final long telegramId)
    {
        this.date_ = date;
        this.jvnFileName_ = jvnFileName;
        this.tree_ = tree.copy();
        this.node_ = node;
        this.jvelinLogCallback_ = jvelinLogCallback;
        this.endNode_ = endNode;
        this.telegramId_ = telegramId;
    }

    /**
     * 日時を取得します。<br />
     * 
     * @return 日時
     */
    public Date getDate()
    {
        return this.date_;
    }

    /**
     * Javelinファイル名を取得します。<br />
     * 
     * @return Javelinファイル名
     */
    public String getJvnFileName()
    {
        return this.jvnFileName_;
    }

    /**
     * CallTreeを取得します。<br />
     * 
     * @return CallTree
     */
    public CallTree getTree()
    {
        return this.tree_;
    }

    /**
     * CallTreeNodeを取得します。<br />
     * 
     * @return CallTreeNode
     */
    public CallTreeNode getNode()
    {
        return this.node_;
    }

    /**
     * コールバックオブジェクトを取得します。<br />
     * 
     * @return コールバックオブジェクト
     */
    public JavelinLogCallback getJavelinLogCallback()
    {
        return this.jvelinLogCallback_;
    }

    /**
     * 最終ノードを取得します。<br />
     * 
     * @return 最終ノード
     */
    public CallTreeNode getEndNode()
    {
        return this.endNode_;
    }

    /**
     * 電文 ID を取得します。<br />
     *
     * @return 電文 ID
     */
    public long getTelegramId()
    {
        return this.telegramId_;
    }
}
