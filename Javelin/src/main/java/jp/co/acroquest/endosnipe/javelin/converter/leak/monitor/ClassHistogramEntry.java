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

/**
 * クラスヒストグラムの一要素。
 * 
 * @author eriguchi
 */
public class ClassHistogramEntry
{
    /** インスタンス数。 */
    private int instances_;

    /** サイズ(byte)。 */
    private int bytes_;

    /** クラス名。 */
    private String className_;

    /**
     * インスタンス数を取得する。
     * 
     * @return インスタンス数。
     */
    public int getInstances()
    {
        return instances_;
    }

    /**
     * インスタンス数を設定する。
     * 
     * @param instances インスタンス数。
     */
    public void setInstances(final int instances)
    {
        this.instances_ = instances;
    }

    /**
     * サイズ(byte)を取得する。
     * 
     * @return サイズ(byte)。
     */
    public int getBytes()
    {
        return bytes_;
    }

    /**
     * サイズ(byte)を設定する。
     * 
     * @param bytes サイズ(byte)。
     */
    public void setBytes(final int bytes)
    {
        this.bytes_ = bytes;
    }

    /**
     * クラス名を取得する。
     * 
     * @return クラス名。
     */
    public String getClassName()
    {
        return className_;
    }

    /**
     * クラス名を設定する。
     * 
     * @param className クラス名。
     */
    public void setClassName(final String className)
    {
        this.className_ = className;
    }
}
