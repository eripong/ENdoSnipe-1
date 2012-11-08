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
package jp.co.acroquest.endosnipe.javelin.resource.proc;


import jp.co.acroquest.endosnipe.common.config.JavelinConfigUtil;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;

/**
 * Solarisのリソース情報をJNI経由で取得するクラス.
 * 
 * libkstat, /procファイルシステムを利用してリソース情報を取得する。
 * OpenSolaris2009.06(32bit)で動作確認済み。
 * 
 * @author hashimoto
 */
public class SolarisResourceReader
{
    // dll ファイルをロードする
    static
    {
        SystemLogger logger = SystemLogger.getInstance();

        // ライブラリをロードします
        JavelinConfigUtil javelinConfigUtil = JavelinConfigUtil.getInstance();

        String libraryPrefix = "./libresource_reader_solaris_";
        String libraryPostfix = ".so";
        
        // CPU arch
        String arch = System.getProperty("os.arch");
        
        // CPU bit数
        String bit = System.getProperty("sun.arch.data.model");
        if (bit == null || bit.length() == 0)
        {
            logger.warn("you have to set \"sun.arch.data.model\" system properties.");
            bit = "";
        }
        
        String libPath = libraryPrefix + arch + "_" + bit + libraryPostfix;
        libPath = javelinConfigUtil.convertRelPathFromJartoAbsPath(libPath);

        if (logger.isDebugEnabled())
        {
            logger.debug("loading so for read system resource : " + libPath);
        }
        
        try 
        {
            System.load(libPath);
        }
        catch (SecurityException se)
        {
            logger.error("Can't load so library : " + libPath, se);
        }
        catch (UnsatisfiedLinkError ule)
        {
            logger.error("Can't load so library : " + libPath, ule);
        }
    }
    
    /**
     * 新規クエリーを作成
     * @return 作成に成功したら true
     */
    private native boolean openQuery();

    /**
     * 計測
     * @return 計測に成功したらtrue
     */
    private native boolean collectQueryData();

    /**
     * システムのCPU時間（System）を取得
     * @return 取得した値(nano second)
     */
    public native long getSystemCPUSys();

    /**
     * システムのCPU時間（User）を取得
     * @return 取得した値(nano second)
     */
    public native long getSystemCPUUser();

    /**
     * システムのCPU時間（Total）を取得
     * @return 取得した値(nano second)
     */
    public native long getSystemCPUTotal();

    /**
     * 物理メモリ（フリー）を取得
     * @return 取得した値(byte)
     */
    public native long getSystemMemoryFree();

    /**
     * 物理メモリ（最大）を取得
     * @return 取得した値(byte)
     */
    public native long getSystemMemoryTotal();

    /**
     * スワップ（使用中）を取得
     * @return 取得した値(byte)
     */
    public native long getSystemSwapFree();

    /**
     * スワップ（最大）を取得
     * @return 取得した値(byte)
     */
    public native long getSystemSwapTotal();


    /**
     * ページインを取得
     * @return 取得した値
     */
    public native long getSystemPageIn();

    /**
     * ページアウトを取得
     * @return 取得した値
     */
    public native long getSystemPageOut();

    /**
     * プロセスのCPU時間(User)を取得
     * @return 取得した値(nano second)
     */
    public native long getProcessCPUUser();

    /**
     * プロセスのCPU時間(System)を取得
     * @return 取得した値(nano second)
     */
    public native long getProcessCPUSys();

    /**
     * メジャーフォールトを取得
     * @return 取得した値
     */
    public native long getProcessMajFlt();

    /**
     * 仮想メモリ使用量を取得
     * @return 取得した値(byte)
     */
    public native long getProcessMemoryVirtual();

    /**
     * 物理メモリ使用量を取得
     * @return 取得した値(byte)
     */
    public native long getProcessMemoryPhysical();

    /**
     * スレッド数を取得
     * @return 取得した値
     */
    public native int getNumThreads();

    /**
     * クエリーの使用を終了
     * @return 終了に成功したら true
     */
    private native boolean closeQuery();

    /**
     * コンストラクタ
     */
    public SolarisResourceReader()
    {
    }
    
    /**
     * システムリソース取得処理の初期化メソッド
     * @return 初期化に成功したらtrue
     */
    public boolean init()
    {
        boolean result = openQuery();
        return result;
    }
    
    public boolean refresh()
    {
        boolean result = collectQueryData();
        return result;
    }

    /**
     * システムリソース取得処理の終了メソッド
     * @return 終了に成功したらtrue
     */
    public boolean destroy()
    {
        closeQuery();
        return true;
    }
    
}
