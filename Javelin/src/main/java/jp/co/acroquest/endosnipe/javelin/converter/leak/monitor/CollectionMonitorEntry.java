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
package jp.co.acroquest.endosnipe.javelin.converter.leak.monitor;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.javelin.util.LinkedHashSet;

/**
 * CollectionTraceManagerにおけるCollectionのエントリ。
 * 
 * @author kimura
 *
 */
public class CollectionMonitorEntry
{
    /** 対象の識別子 */
    private String entryIdentifier_;

    /** 検出回数 */
    private int detectCount_;

    /** スタックトレース識別 */
    private final Set<Integer> traceSet_;

    /** 対象のコレクション */
    private WeakReference<Collection<?>> targetCollection_;

    /** 対象のマップ */
    private WeakReference<Map<?, ?>> targetMap_;

    /** 対象のサイズ */
    private int entryNumber_;
    
    /** 検出時の要素数。 */
    private int detectedSize_;

    /**
     * コンストラクタ
     * 
     * @param entryIdentifier 対象の識別子
     * @param target 対象
     */
    public CollectionMonitorEntry(final String entryIdentifier, final Collection<?> target)
    {
        this.entryIdentifier_ = entryIdentifier;
        this.detectCount_ = 0;
        this.traceSet_ = new LinkedHashSet<Integer>();
        this.targetCollection_ = new WeakReference<Collection<?>>(target);
        this.entryNumber_ = target.size();
    }

    /**
     * コンストラクタ
     * 
     * @param entryIdentifier 対象の識別子
     * @param target 対象
     */
    public CollectionMonitorEntry(final String entryIdentifier, final Map<?, ?> target)
    {
        this.entryIdentifier_ = entryIdentifier;
        this.detectCount_ = 0;
        this.traceSet_ = new LinkedHashSet<Integer>();
        this.targetMap_ = new WeakReference<Map<?, ?>>(target);
        this.entryNumber_ = target.size();
    }

    /**
     * 対象のサイズを更新する。
     */
    public void updateEntryNumber()
    {
        if (this.targetCollection_ != null)
        {
            Collection<?> collection = this.targetCollection_.get();
            if (collection != null)
            {
                this.entryNumber_ = collection.size();
            }
        }

        if (this.targetMap_ != null)
        {
            Map<?, ?> map = this.targetMap_.get();
            if (map != null)
            {
                this.entryNumber_ = map.size();
            }
        }
    }

    /**
     * 対象の識別子を取得する
     * 
     * @return 対象の識別子
     */
    public String getEntryIdentifier()
    {
        return this.entryIdentifier_;
    }

    /**
     * 対象の識別子を設定する
     * 
     * @param entryIdentifier 対象の識別子
     */
    public void setEntryIdentifier(final String entryIdentifier)
    {
        this.entryIdentifier_ = entryIdentifier;
    }

    /**
     * 対象のサイズを取得する
     * 
     * @return 対象のサイズ
     */
    public int getEntryNumber()
    {
        return this.entryNumber_;
    }

    /**
     * リーク検出回数を取得する。
     * @return リーク検出回数
     */
    public int getDetectCount()
    {
        return this.detectCount_;
    }

    /**
     * リーク検出回数を設定する。
     * @param detectCount リーク検出回数
     */
    public void setDetectCount(final int detectCount)
    {
        this.detectCount_ = detectCount;
    }

    /**
     * スタックトレース取得回数を取得する。
     * @return スタックトレース取得回数
     */
    public int getTraceCount()
    {
        return this.traceSet_.size();
    }

    /**
     * 保存しているスタックトレースの先頭要素を削除する。
     */
    public void removeTrace()
    {
        Integer traceHashCode = this.traceSet_.iterator().next();
        this.traceSet_.remove(traceHashCode);
    }

    /**
     * スタックトレースのhashCodeを保存する。
    * @param hashCode スタックトレースのhashCode。
     */
    public void addTrace(final int hashCode)
    {
        this.traceSet_.add(hashCode);
    }

    /**
     * 保存しているスタックトレースのを削除する。
     * @param hashCode スタックトレースのhashCode。
     */
    public void removeTrace(final int hashCode)
    {
        this.traceSet_.remove(Integer.valueOf(hashCode));
    }
    
    /**
     * 指定したスタックトレースが既に保存されているスタックトレースに
     * 一致するものがあるかどうかを判定する。
     * 
     * @param hashCode スタックトレースのhashCode。
     * @return 一致するものがあるかどうか。
     */
    public boolean containsTrace(final int hashCode)
    {
        return this.traceSet_.contains(hashCode);
    }

    /**
     * 監視対象のコレクションオブジェクトを返します。<br />
     *
     * コレクションを監視していない、もしくは監視対象コレクションがすでに GC で回収されている場合は
     * <code>null</code> を返します。<br />
     *
     * @return 監視対象のコレクション
     */
    public Collection<?> getCollection()
    {
        if (this.targetCollection_ != null)
        {
            return this.targetCollection_.get();
        }
        return null;
    }

    /**
     * 監視対象のマップオブジェクトを返します。<br />
     *
     * マップを監視していない、もしくは監視対象マップがすでに GC で回収されている場合は
     * <code>null</code> を返します。<br />
     *
     * @return 監視対象のマップ
     */
    public Map<?, ?> getMap()
    {
        if (this.targetMap_ != null)
        {
            return this.targetMap_.get();
        }
        return null;
    }

    /**
     * 対象のコレクション、マップがまだGCにより回収されていないかを取得する。
     * 
     * @return 対象のコレクション、マップがまだGCにより回収されていないか。
     */
    public boolean exists()
    {
        return (this.targetCollection_ != null && this.targetCollection_.get() != null)
                || (this.targetMap_ != null && this.targetMap_.get() != null);
    }

    /**
     * 検出時の要素数を設定する。
     * @param detectedSize 検出時の要素数
     */
    public void setDetectedSize(int detectedSize)
    {
        detectedSize_ = detectedSize;
    }

    /**
     * 検出時の要素数を設定する。
     * @return 検出時の要素数
     */
    public int getDetectedSize()
    {
        return detectedSize_;
    }

    /**
     * 保存しているスタックトレースを削除する。
     */
    public void clearAllTrace()
    {
        this.traceSet_.clear();
    }
}
