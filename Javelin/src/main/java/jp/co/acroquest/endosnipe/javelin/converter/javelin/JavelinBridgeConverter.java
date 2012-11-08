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
package jp.co.acroquest.endosnipe.javelin.converter.javelin;

import java.io.IOException;
import java.util.List;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.converter.AbstractConverter;
import jp.co.acroquest.endosnipe.javelin.converter.util.ConvertedMethodCounter;
import jp.co.smg.endosnipe.javassist.CannotCompileException;
import jp.co.smg.endosnipe.javassist.CtBehavior;
import jp.co.smg.endosnipe.javassist.CtClass;
import jp.co.smg.endosnipe.javassist.NotFoundException;
import jp.co.smg.endosnipe.javassist.bytecode.BadBytecode;
import jp.co.smg.endosnipe.javassist.bytecode.CodeAttribute;
import jp.co.smg.endosnipe.javassist.bytecode.CodeIterator;
import jp.co.smg.endosnipe.javassist.bytecode.Opcode;

/**
 * Javelin用コンバータ
 * 
 * @author yamasaki
 */
public class JavelinBridgeConverter extends AbstractConverter
{
    private static final String CONVERTER_NAME = "JavelinConverter";

    /** JavelinRecorder名 */
    private static final String JAVELIN_RECORDER_NAME = JavelinRecorder.class.getName();

    /** 実行前処理として追加するpreProcessのコード(前)。 */
    private static final String PREPROCESS_CODE_BEFORE = JAVELIN_RECORDER_NAME + ".preProcess(";

    /** 実行前処理として追加するpreProcessのコード(後)。 */
    private static final String PREPROCESS_CODE_AFTER = "\", $args);";

    /** 実行後処理として追加するpostProcessNGのコード(前)。 */
    private static final String POSTPROCESS_NG_CODE_BEFORE =
            JAVELIN_RECORDER_NAME + ".postProcessNG(";

    /** 実行後処理として追加するpostProcessNGのコード(後)。 */
    private static final String POSTPROCESS_NG_CODE_AFTER = "\",$e);throw $e;";

    /** 実行後処理として追加するpostProcessOKのコード(前)。 */
    private static final String POSTPROCESS_OK_CODE_BEFORE =
            JAVELIN_RECORDER_NAME + ".postProcessOK(";

    /** 実行後処理として追加するpostProcessOKのコード(後)。 */
    private static final String POSTPROCESS_OK_CODE_AFTER = "\",($w)$_);";

    /** 実行前処理として追加されるコードの固定部分の文字列長 */
    private static final int PREPROCESS_CODE_FIXEDLENGTH =
            PREPROCESS_CODE_BEFORE.length() + PREPROCESS_CODE_AFTER.length();

    /** 実行後処理として追加されるコードの固定部分の文字列長 */
    private static final int POSTPROCESS_CODE_FIXEDLENGTH =
            POSTPROCESS_OK_CODE_BEFORE.length() + POSTPROCESS_OK_CODE_AFTER.length();

    /** 実行後処理として追加されるNGコードの固定部分の文字列長 */
    private static final int NG_CODE_FIXEDLENGTH =
            POSTPROCESS_NG_CODE_BEFORE.length() + POSTPROCESS_NG_CODE_AFTER.length();

    /** bytecodeの内容により除外する場合の処理内容：除外しない。 */
    private static final int POLICY_NOT_EXCLUDE = 0;

    /** bytecodeの内容により除外する場合の処理内容：BCIしない。 */
    private static final int POLICY_NO_BCI = 1;

    /** Javelin設定。 */
    private final JavelinConfig config_ = new JavelinConfig();

    /** 処理中のメソッドのバイトコード情報。 */
    private BytecodeInfo info_;

    /**
     * {@inheritDoc}
     */
    public void init()
    {
        // StatsJavelinRecorderを初期化する
        synchronized (StatsJavelinRecorder.class)
        {
            if (StatsJavelinRecorder.isInitialized() == false)
            {
                StatsJavelinRecorder.javelinInit(new JavelinConfig());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void convertImpl()
        throws CannotCompileException,
            NotFoundException,
            IOException
    {
        List<CtBehavior> behaviorList = getMatcheDeclaredBehavior();
        for (CtBehavior ctBehavior : behaviorList)
        {
            if (isExcludeTarget(ctBehavior))
            {
                processExcludeTarget(ctBehavior);
                //JavelinConverterで変換対象から除外されたメソッド数を記録
                ConvertedMethodCounter.incrementExcludedCount();
            }
            else
            {
                convertBehavior(ctBehavior);
                //JavelinConverterで変換を行ったメソッド数を記録
                ConvertedMethodCounter.incrementConvertedCount();
            }

        }

        setNewClassfileBuffer(getCtClass().toBytecode());
    }

    /**
     * 除外処理を実装する。
     * 今のところ、ログを出力するのみ。
     * 
     * @param ctBehavior 除外対象。
     */
    private void processExcludeTarget(final CtBehavior ctBehavior)
    {
        int policy = config_.getByteCodeExcludePolicy();
        if (policy == POLICY_NO_BCI)
        {
            SystemLogger.getInstance().info(
                                            "(" + CONVERTER_NAME + ") excluded method:"
                                                    + getCtClass().getName() + "#"
                                                    + ctBehavior.getName() + createByteCodeInfo());
        }
    }

    /**
     * 除外するかどうかを判定する。
     * 
     * 以下の条件を満たす場合に除外する。
     * <ol>
     * <li>bytecode長がbytecode.exclude.lengthより短い。</li>
     * <li>bytecodeにgotoが含まれていない。</li>
     * </ol>
     * 
     * @param behavior 判定対象。
     * @return 除外するかどうか。
     */
    private boolean isExcludeTarget(final CtBehavior behavior)
    {
        if (config_.getByteCodeExcludePolicy() == POLICY_NOT_EXCLUDE)
        {
            return false;
        }

        int bytecodeLengthMax = config_.getBytecodeLengthMax();
        int controlCountMax = config_.getBytecodeControlCountMax();
        this.info_ = getBytecodeInfo(behavior, bytecodeLengthMax, controlCountMax);

        return this.info_.length_ <= bytecodeLengthMax
                && this.info_.controlCount_ <= controlCountMax;
    }

    private BytecodeInfo getBytecodeInfo(final CtBehavior behavior, final int bytecodeLengthMin,
            final int controlCountMax)
    {
        BytecodeInfo info = new BytecodeInfo();
        try
        {
            CodeAttribute codeAttribute = behavior.getMethodInfo2().getCodeAttribute();
            info.length_ = codeAttribute.getCodeLength();
            if (info.length_ >= bytecodeLengthMin)
            {
                return info;
            }

            info.controlCount_ = 0;
            CodeIterator iterator = codeAttribute.iterator();
            while (iterator.hasNext())
            {
                try
                {
                    int pos = iterator.next();
                    int code = iterator.byteAt(pos);
                    if ((code == Opcode.GOTO))
                    {
                        info.controlCount_++;
                    }

                    if (info.controlCount_ > controlCountMax)
                    {
                        break;
                    }
                }
                catch (BadBytecode bb)
                {
                    SystemLogger.getInstance().warn(bb);
                }
            }
        }
        catch (Throwable th)
        {
            SystemLogger.getInstance().warn(th);
        }
        return info;
    }

    /**
     * メソッドの振る舞いを修正する。
     * @param ctBehavior CtBehavior
     * @throws CannotCompileException コンパイルできない場合
     * @throws NotFoundException クラスが見つからない場合
     */
    private void convertBehavior(final CtBehavior ctBehavior)
        throws CannotCompileException,
            NotFoundException
    {
        String className = getClassName();
        String methodName = ctBehavior.getName();
        String argClassMethod = "\"" + className + "\",\"" + methodName;
        int argLength = argClassMethod.length();

        // 実行前処理を追加する。
        int preProcessCodeLength = argLength + PREPROCESS_CODE_FIXEDLENGTH;
        StringBuilder preProcessCodeBuffer = new StringBuilder(preProcessCodeLength);
        preProcessCodeBuffer.append(PREPROCESS_CODE_BEFORE);
        preProcessCodeBuffer.append(argClassMethod);
        preProcessCodeBuffer.append(PREPROCESS_CODE_AFTER);
        String callPreProcessCode = preProcessCodeBuffer.toString();
        ctBehavior.insertBefore(callPreProcessCode);

        // 実行後処理を追加する。
        int postProcessCodeLength = argLength + POSTPROCESS_CODE_FIXEDLENGTH;
        StringBuilder postProcessCodeBuffer = new StringBuilder(postProcessCodeLength);
        postProcessCodeBuffer.append(POSTPROCESS_OK_CODE_BEFORE);
        postProcessCodeBuffer.append(argClassMethod);
        postProcessCodeBuffer.append(POSTPROCESS_OK_CODE_AFTER);
        String callPostProcessCode = postProcessCodeBuffer.toString();

        ctBehavior.insertAfter(callPostProcessCode);

        // 例外ハンドリングを追加する。
        CtClass throwable = getClassPool().get(Throwable.class.getName());
        // 実行前処理を追加する。
        int ngCodeLength = argLength + NG_CODE_FIXEDLENGTH;
        StringBuilder ngCodeBuffer = new StringBuilder(ngCodeLength);
        ngCodeBuffer.append(POSTPROCESS_NG_CODE_BEFORE);
        ngCodeBuffer.append(argClassMethod);
        ngCodeBuffer.append(POSTPROCESS_NG_CODE_AFTER);
        String ngCode = ngCodeBuffer.toString();
        ctBehavior.addCatch(ngCode, throwable);

        // 処理結果をログに出力する。
        logModifiedMethod(CONVERTER_NAME, ctBehavior, createByteCodeInfo());
    }

    private String createByteCodeInfo()
    {
        return this.info_ != null ? ":" + this.info_ : "";
    }

    private static class BytecodeInfo
    {
        int length_ = -1;

        int controlCount_ = -1;

        @Override
        public String toString()
        {
            String result;
            if (controlCount_ == -1)
            {
                result = "codeLength=" + length_;
            }
            else
            {
                result = "codeLength=" + length_ + ", controlCount=" + controlCount_;
            }
            
            return result;
        }
    }
}
