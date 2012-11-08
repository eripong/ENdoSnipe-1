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
package jp.co.acroquest.endosnipe.javelin.converter;

import java.io.IOException;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import java.util.List;

import jp.co.smg.endosnipe.javassist.CannotCompileException;
import jp.co.smg.endosnipe.javassist.ClassPool;
import jp.co.smg.endosnipe.javassist.CtBehavior;
import jp.co.smg.endosnipe.javassist.CtClass;
import jp.co.smg.endosnipe.javassist.CtConstructor;
import jp.co.smg.endosnipe.javassist.CtMember;
import jp.co.smg.endosnipe.javassist.Modifier;
import jp.co.smg.endosnipe.javassist.NotFoundException;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.conf.ExcludeConversionConfig;
import jp.co.acroquest.endosnipe.javelin.conf.IncludeConversionConfig;
import jp.co.acroquest.endosnipe.javelin.conf.JavelinMessages;
import jp.co.acroquest.endosnipe.javelin.converter.util.ConverterUtil;

/**
 * コンバータの抽象クラス
 * 
 * @author eriguchi
 * 
 */
public abstract class AbstractConverter implements Converter
{
    /** コンストラクタ用のメソッド識別子 */
    private static final String CONSTRUCTOR_IDENTIFIER = "<CONSTRUCTOR>";

    /** クラスファイルのバッファ */
    private byte[] classfileBuffer_;

    /** コード埋め込み後のクラスファイルのバッファ */
    private byte[] newClassfileBuffer_;

    /** クラス名 */
    private String className_;

    /** Includeの設定 */
    private IncludeConversionConfig includeConfig_;

    /** Excludeの設定リスト */
    private List<ExcludeConversionConfig> excludeConfigList_;

    /** CtClass */
    private CtClass ctClass_;

    /** ClassPool */
    private ClassPool pool_;

    /**
     * {@inheritDoc}
     */
    public byte[] convert(final String className, final byte[] classfileBuffer,
            final ClassPool pool, final CtClass ctClass,
            final IncludeConversionConfig includeConfig,
            final List<ExcludeConversionConfig> excludeConfigList)
    {
        this.classfileBuffer_ = classfileBuffer;
        this.className_ = className;
        this.includeConfig_ = includeConfig;
        this.excludeConfigList_ = excludeConfigList;
        this.pool_ = pool;
        this.ctClass_ = ctClass;
        this.newClassfileBuffer_ = null;

        prepare();

        try
        {
            convertImpl();
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }

        return getResult();
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getResult()
    {
        if (this.newClassfileBuffer_ != null)
        {
            return this.newClassfileBuffer_;
        }
        return this.classfileBuffer_;
    }

    /**
     * 前準備
     */
    protected void prepare()
    {
        // 何もしない
    }

    /**
     * コード埋め込み対象メソッドのリストを作成する。
     * @return コード埋め込み対象メソッドのリスト
     */
    protected List<CtBehavior> getMatcheDeclaredBehavior()
    {
        List<CtBehavior> list = new ArrayList<CtBehavior>();

        for (CtBehavior ctBehavior : this.ctClass_.getDeclaredBehaviors())
        {
            // 空のメソッドは除外する。
            if (ctBehavior.isEmpty())
            {
                continue;
            }

            // 修飾子がAbstract又はNativeのメソッドは除外する。
            int modifiers = ctBehavior.getModifiers();
            if (Modifier.isAbstract(modifiers) || Modifier.isNative(modifiers))
            {
                continue;
            }

            // Includeに設定されていて、Excludeに設定されていないメソッドをリストに追加する。
            String methodName = ctBehavior.getName();

            //コンストラクタの場合、パターンが<Constructor>の場合も追加で判定を行う
            boolean isConstructor =
                                    ctBehavior instanceof CtConstructor;
            boolean isConstInclude = false;
            boolean isConstExclude = false;

            IncludeConversionConfig includeConfig = this.includeConfig_;
            String methodNamePattern = includeConfig.getMethodNamePattern();
            if (isConstructor)
            {
                CtConstructor constructor = (CtConstructor)ctBehavior;
                if (constructor.isClassInitializer() == false)
                {
                    isConstInclude =
                                     CONSTRUCTOR_IDENTIFIER.matches(methodNamePattern);
                    isConstExclude = isExcludeTarget(CONSTRUCTOR_IDENTIFIER);
                }
            }

            //メソッド名での判定結果取得
            boolean isNameInclude = methodName.matches(methodNamePattern);
            boolean isNameExclude = isExcludeTarget(methodName);

            //コンストラクタ、メソッド名の判定のどちらかで「include」となり、
            //かつコンストラクタ、メソッド名の判定で「exclude」となっていない場合、変換対象に追加
            if ((isConstInclude || isNameInclude) && (!isConstExclude && !isNameExclude))
            {
                list.add(ctBehavior);
            }
        }

        return list;
    }

    /**
     * 引数で指定したメソッドがコード埋め込み対象から除外されるか判定する。
     * @param methodName メソッド名
     * @return true:コード埋め込み対象から除外する、false:コード埋め込み対象から除外しない
     */
    private boolean isExcludeTarget(final String methodName)
    {
        for (ExcludeConversionConfig excludeConfig : this.excludeConfigList_)
        {
            if (methodName.matches(excludeConfig.getMethodNamePattern()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * コンバータの実装
     * @throws CannotCompileException コンパイルができない場合
     * @throws NotFoundException クラスが見つからない場合
     * @throws IOException 入出力例外
     */
    public abstract void convertImpl()
        throws CannotCompileException,
            NotFoundException,
            IOException;

    /**
     * クラスファイルのバッファを取得する。
     * @return クラスファイルのバッファ
     */
    public byte[] getClassfileBuffer()
    {
        return this.classfileBuffer_;
    }

    /**
     * コード埋め込み後のクラスファイルのバッファを取得する。
     * @return コード埋め込み後のクラスファイルのバッファ
     */
    public byte[] getNewClassfileBuffer()
    {
        return this.newClassfileBuffer_;
    }

    /**
     * コード埋め込み後のクラスファイルのバッファを設定する。
     * @param newClassfileBuffer コード埋め込み後のクラスファイルのバッファ
     */
    public void setNewClassfileBuffer(final byte[] newClassfileBuffer)
    {
        this.newClassfileBuffer_ = newClassfileBuffer;
    }

    /**
     * クラス名を取得する.
     * 
     * @return クラス名
     */
    public String getClassName()
    {
        return this.className_;
    }

    /**
     * Configを取得する.
     * 
     * @return Config
     */
    public IncludeConversionConfig getConfig()
    {
        return this.includeConfig_;
    }

    /**
     * CｔClassを取得する.
     * 
     * @return CｔClass
     */
    public CtClass getCtClass()
    {
        return this.ctClass_;
    }

    /**
     * ClassPoolを取得する.
     * 
     * @return ClassPool
     */
    public ClassPool getClassPool()
    {
        return this.pool_;
    }

    /**
     * 変換クラス名の取得
     * 
     * @return 変換クラス名
     */
    protected String simpleName()
    {
        String className = getClassName();
        className = ConverterUtil.toSimpleName(className);
        return className;
    }

    /**
     * 変換情報を出力する
     * 
     * @param converterName コンバータ名。
     * @param ctMember 変換対象
     */
    protected void logModifiedMethod(final String converterName, final CtMember ctMember)
    {
        this.logModifiedMethod(converterName, ctMember, null);
    }

    /**
     * 変換情報を出力する
     * 
     * @param converterName コンバータ名。
     * @param ctMember 変換対象
     * @param message メッセージ。
     */
    protected void logModifiedMethod(final String converterName, final CtMember ctMember,
            final String message)
    {
        // 処理結果をログに出力する。
        String methodName = ctMember.getName();

        String key = "javelin.converter.AbstractConverter.ModifiedMethodLabel";
        String modifiedMethodTag = JavelinMessages.getMessage(key, converterName);
        StringBuilder messageBuilder = new StringBuilder(modifiedMethodTag);
        messageBuilder.append(simpleName());
        messageBuilder.append("#");
        messageBuilder.append(methodName);

        if (message != null)
        {
            messageBuilder.append(message);
        }
        
        SystemLogger.getInstance().info(messageBuilder.toString());
    }
}
