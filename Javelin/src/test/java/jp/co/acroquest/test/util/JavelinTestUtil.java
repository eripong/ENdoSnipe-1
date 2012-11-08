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
package jp.co.acroquest.test.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.config.ConfigPreprocessor;
import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.config.JavelinConfigUtil;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.CallTreeRecorder;
import jp.co.acroquest.endosnipe.javelin.conf.ExcludeConversionConfig;
import jp.co.acroquest.endosnipe.javelin.conf.IncludeConversionConfig;
import jp.co.acroquest.endosnipe.javelin.conf.JavelinTransformConfig;
import jp.co.acroquest.endosnipe.javelin.converter.Converter;
import jp.co.acroquest.endosnipe.javelin.resource.MultiResourceGetter;
import jp.co.acroquest.endosnipe.javelin.resource.ResourceCollector;
import jp.co.acroquest.endosnipe.javelin.resource.ResourceGetter;
import jp.co.acroquest.endosnipe.javelin.resource.proc.ProcParser;
import jp.co.acroquest.endosnipe.javelin.testutil.PrivateAccessor;
import jp.co.dgic.testing.common.virtualmock.MockObjectManager;
import jp.co.smg.endosnipe.javassist.ClassPool;
import jp.co.smg.endosnipe.javassist.CtClass;
import junit.framework.Assert;

/**
 * Javelinのテストコードに用いるユーティリティメソッド群クラス
 * 
 * @author M.Yoshida
 */
public class JavelinTestUtil
{
    private static Map<String, String>      propertyMap__ ;
    private static Map<String, Set<String>> convertedClass__;
    private static JavelinConfig            jvnConfig__;
    private static JavelinTransformConfig   jvnTransformConfig__;
    private static Map<String, Class<?>>    resourceGetterClass__;
    
    
    static
    {
        propertyMap__ = new HashMap<String, String>(); 
        propertyMap__.put("javelin.call.tree.enable", "isCallTreeEnabled");
        propertyMap__.put("javelin.call.tree.max", "getCallTreeMax");
        propertyMap__.put("javelin.timeout.monitor", "isTimeoutMonitor");
        propertyMap__.put("javelin.log4j.printstack.level", "getLog4jPrintStackLevel");
        propertyMap__.put("javelin.bytecode.exclude.length", "getBytecodeLengthMax");
        propertyMap__.put("javelin.bytecode.exclude.controlCount", "getBytecodeControlCountMax");
        propertyMap__.put("javelin.bytecode.exclude.policy", "getByteCodeExcludePolicy");
        propertyMap__.put("javelin.autoExcludeThreshold.count","getAutoExcludeThresholdCount");
        propertyMap__.put("javelin.autoExcludeThreshold.time","getAutoExcludeThresholdTime");
        propertyMap__.put("javelin.thread.dump.cpu", "getThreadDumpCpu");
        propertyMap__.put("javelin.thread.dump.threadnum", "getThreadDumpThreadNum");
        propertyMap__.put("javelin.thread.dump.monitor", "isThreadDump");
        propertyMap__.put("javelin.leak.collectionSizeThreshold", "getCollectionSizeThreshold");
        propertyMap__.put("javelin.thread.blocktime.threshold", "getBlockTimeThreshold");
        propertyMap__.put("javelin.leak.collectionSizeOut", "isLeakCollectionSizePrint");
        propertyMap__.put("javelin.fullgc.threshold", "getFullGCThreshold");
        convertedClass__ = new HashMap<String, Set<String>>();
        jvnConfig__ = null;
    }

    /**
     * リソース取得クラスを作る。
     */
    private static void initResource()
    {
        if (resourceGetterClass__ != null)
        {
            return;
        }

        resourceGetterClass__ = new HashMap<String, Class<?>>();
        Map<String, ResourceGetter> resourceMap = new HashMap<String, ResourceGetter>();
        Map<String, MultiResourceGetter> multiResourceMap =
            new HashMap<String, MultiResourceGetter>();
        ProcParser procParser = ResourceCollector.createProcParser();
        ResourceCollector.setResouceGetters(resourceMap, multiResourceMap, procParser);
        for (Map.Entry<String, ResourceGetter> entry : resourceMap.entrySet())
        {
            resourceGetterClass__.put(entry.getKey(), entry.getValue().getClass());
        }
    }

    /**
     * リソース情報の取得メソッドの返り値を偽装し、任意のリソース情報を取得できるようにする。
     * 
     * @param resourceKey リソース情報を一意に定めるキー(TelegramConstantsのメンバ)
     * @param value       返り値（偽装する）
     * @throws Exception  処理の途中にエラーが発生した場合
     */
    public static void camouflageResourceInfo(String resourceKey, Number value) throws Exception
    {
        initResource();
        Class<?> resGetter = resourceGetterClass__.get(resourceKey);
        
        MockObjectManager.setReturnValueAtAllTimes(resGetter, "getValue", value);
    }
    
    /**
     * テスト用に作成したJavelin設定ファイルを読み込み、Javelinが内部参照する際の設定値を偽装する。
     *
     * @param baseClass ディレクトリ基準クラス
     * @param fileName 偽装する設定値が書かれたファイル
     * @throws Exception 処理の最中エラーが発生した場合
     */
    public static void camouflageJavelinConfig(Class<?> baseClass, String fileName) throws Exception
    {
        PrivateAccessor.setField(JavelinConfig.class, "isInitialized__", false);
        PrivateAccessor.setField(JavelinConfigUtil.getInstance(), "properties_", null);
        PrivateAccessor.setField(ConfigPreprocessor.class, "canonicalFiles__", new HashMap<Integer, File>());
        jvnTransformConfig__ = null;
        String configPath = getAbsolutePath(baseClass, fileName);
        MockObjectManager.setReturnValueAtAllTimes(JavelinConfigUtil.class, "getFileName1", configPath);
        JavelinConfigUtil.getInstance().update();
        JavelinConfig config = new JavelinConfig();
        jvnConfig__ = config;
    }
    
    /**
     * Javelinが内部参照する際の設定値を偽装する。すでに偽装の設定がある場合は、
     * このメソッドの実行による偽装値を上書きする。
     * 
     * @param propKey JavelinのConfigファイルに記載されるキー
     * @param value   キーに設定される値（偽装値）
     */
    public static void camouflageJavelinConfig(String propKey, Object value)
    {
        String camouflageMethod = propertyMap__.get(propKey);
        
        if (camouflageMethod == null)
        {
            return ;
        }
        
        MockObjectManager.setReturnValueAtAllTimes(JavelinConfig.class, camouflageMethod, value);
    }

    /**
     * 予め配置している設定ファイルのデータを取得する。
     * 本メソッドを呼び出す前に、camouflageJavelinConfig()メソッドを呼び出す必要がある。
     * 
     * @return 設定ファイルのデータが入っているオブジェクト
     * @throws IOException ファイルの読込に失敗した場合
     */
    public static JavelinTransformConfig readTransformConfig() throws IOException
    {
        if(jvnConfig__ == null)
        {
            return null;
        }
        
        if(jvnTransformConfig__ != null)
        {
            return jvnTransformConfig__;
        }
        
        JavelinTransformConfig transformConfig_ = new JavelinTransformConfig();

        InputStream includeStream = ConfigPreprocessor.process(new File(jvnConfig__.getInclude()));
        InputStream excludeStream = ConfigPreprocessor.process(new File(jvnConfig__.getExclude()));

        try
        {
            transformConfig_.readConfig(includeStream, excludeStream);
        }
        finally
        {
            includeStream.close();
            excludeStream.close();
        }
        
        jvnTransformConfig__ = transformConfig_;
        
        return transformConfig_;
    }

    /**
     * パラメータで指定したコンバータにより変換されたクラスのクラス情報を取得する。。
     * 
     * @param converterName 適用するコンバータのクラス(完全限定名)
     * @param targetClass   コンバータにより変換するクラス(完全限定名)
     * @return　コンバータにより変換されたクラスのクラス情報
     * @throws Exception エラーが発生した場合（原因に因らず）
     */
    public static Class<?> applyMonitor(String converterName, String targetClass) throws Exception
    {
        // 既にコンバートを実行したクラスの場合は、変換後クラスを返して終了する。
        Set<String> convertedClassSet = convertedClass__.get(converterName);
        
        if(convertedClassSet != null && convertedClassSet.contains(targetClass) == true)
        {
            return Class.forName(targetClass);
        }
        
        if(convertedClassSet == null)
        {
            convertedClassSet = new HashSet<String>();
            convertedClass__.put(converterName, convertedClassSet);
        }
        
        // コンバート対象クラスの情報を取得する。
        ClassPool pool            = ClassPool.getDefault();
        CtClass   targetClassInfo = pool.get(targetClass);
        targetClassInfo.stopPruning(true);
        
        // 設定ファイルから、指定したコンバータの適用対象かどうかを判定する。
        JavelinTransformConfig transformConfig = readTransformConfig();
        
        IncludeConversionConfig includeConversionInfo 
            = findMatchConversionConfig(
                 targetClass, converterName, pool, targetClassInfo, transformConfig);
        
        List<ExcludeConversionConfig> excludeConverionInfo 
            = transformConfig.matchesToExclude(targetClass);
        
        if(includeConversionInfo == null)
        {
            return null;
        }
        
        // 適用するコンバータを生成する。
        Class<?>  convertClassInfo = Class.forName(converterName);
        Converter converter        = (Converter)convertClassInfo.newInstance();

        converter.init();
        
        // コンバートを実施する。
        byte[] convertedByteCodeBuffer
            = converter.convert(targetClass,
                                null,
                                pool,
                                targetClassInfo,
                                includeConversionInfo,
                                excludeConverionInfo);
        
        // コンバートが行われなかった場合は元のクラスを返す。
        if(convertedByteCodeBuffer == null || convertedByteCodeBuffer.length <= 0)
        {
            return Class.forName(targetClass);
        }
        
        ByteArrayInputStream byteCodeStream 
            = new ByteArrayInputStream(convertedByteCodeBuffer);

        targetClassInfo.defrost();
        CtClass convertedClass    = pool.makeClass(byteCodeStream);
        convertedClass.freeze();
        
        convertedClassSet.add(targetClass);
        return convertedClass.toClass();
    }
    
    
    /**
     * パラメータで指定したコンバータにより変換されたクラスのインスタンスを取得する。
     * 
     * @param converterName 適用するコンバータのクラス(完全限定名)
     * @param targetClass   コンバータにより変換するクラス(完全限定名)
     * @return　コンバータにより変換されたクラスのインスタンス
     * @throws Exception エラーが発生した場合（原因に因らず）
     */
    public static Object createMonitoredObject(String converterName, String targetClass) throws Exception
    {
        Class<?> convertedClass = applyMonitor(converterName, targetClass);
        
        if(convertedClass == null)
        {
            return null;
        }
        
        Object convertedInstance = convertedClass.newInstance();
        return convertedInstance;
    }
    
    /**
     * 指定したクラスのメソッドが、想定している回数コールされているか判定する。
     * 
     * @param clazz      判定対象のクラスのクラス情報
     * @param methodName 判定対象のメソッド名
     * @param num        呼び出される回数の期待値
     */
    public static void assertRecordCallNum(Class<?> clazz, String methodName, int num)
    {
        int makeCallNode = MockObjectManager.getCallCount(CallTreeRecorder.class, "addCallTreeNode");
        
        List<CallTreeNode> nodeList = new ArrayList<CallTreeNode>();
        
        for(int cnt = 0; cnt < makeCallNode; cnt ++)
        {
            nodeList.add(
                (CallTreeNode)MockObjectManager.getArgument(
                    CallTreeRecorder.class, "addCallTreeNode",cnt, 2));
        }

        int callCount = 0;
        
        for(CallTreeNode ite : nodeList)
        {
            String calledClass = ite.getInvocation().getClassName();
            String calledMethod = ite.getInvocation().getMethodName();
            
            if(clazz.getName().equals(calledClass) && methodName.equals(calledMethod))
            {
                callCount++;
            }
        }
        
        Assert.assertEquals(num, callCount);
    }
    
    /**
     * privateフィールドなど、アクセスできないフィールドに対して、値を設定する。
     * static privateに対してのアクセスは不可能。
     * 
     * @param clazz     設定対象のクラスのクラス情報
     * @param fieldName 設定対象のフィールド名
     * @param instance  設定対象のインスタンス
     * @param value     設定する値
     */
    public static void setNonAccessibleField(
        Class<?> clazz, String fieldName, Object instance, Object value)
    {
        if (instance != null && clazz.equals(instance.getClass()) == false)
        {
            throw new RuntimeException("設定対象クラスのインスタンスが一致しません");
        }
        
        try
        {
            Field targetField = clazz.getDeclaredField(fieldName);
            targetField.setAccessible(true);
            targetField.set(instance, value);
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * privateフィールドなど、アクセスできないフィールドの値を取得する。
     * 
     * @param clazz     設定対象クラスのクラス情報
     * @param fieldName 設定対象のフィールド名
     * @param instance  設定対象のインスタンス
     * @return          設定する値
     */
    public static Object getNonAccessibleField(
        Class<?> clazz, String fieldName, Object instance)
    {
        if (instance != null && clazz.equals(instance.getClass()) == false)
        {
            throw new RuntimeException("取得対象クラスのインスタンスが一致しません");
        }
        
        Object result = null;
        try
        {
            Field targetField = clazz.getDeclaredField(fieldName);
            targetField.setAccessible(true);
            result = targetField.get(instance);
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex);
        }
        
        return result;
    }
    
    /**
     * privateメソッドなど、直接アクセスできないメソッドを実行する。
     * 
     * @param clazz      対象のメソッドが定義されているクラスのクラス情報
     * @param methodName 呼び出す対象のメソッドの名称
     * @param instance   メソッドを呼び出すインスタンス。staticメソッドの場合はnull。
     * @param params     メソッドに指定するパラメータ(プリミティブ型の場合はラッパークラスを使用する)
     * @return　指定したメソッドの呼出結果
     */
    public static Object invokeNonAccessibleMethod(
        Class<?> clazz, String methodName, Object instance, Object ... params)
    {
        List<Class<?>> targetParamTypes = new ArrayList<Class<?>>();
        
        if(params != null)
        {
            for(Object param : params)
            {
                targetParamTypes.add(param.getClass());
            }
        }
        Object retVal = null;
        
        try
        {
            Method targetMethod = getParamTypesMatchMethod(clazz, methodName, params);
            targetMethod.setAccessible(true);
            retVal = targetMethod.invoke(instance, params);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
        
        return retVal;
    }
    
    /**
     * 指定した引数に、型が対応するメソッドを取得する。
     * 
     * @param clazz      メソッドを検索する型
     * @param methodName メソッドの名前
     * @param params     メソッドに適用する引数
     * @return 適合したメソッド。見付からなかった場合はnull。
     */
    private static Method getParamTypesMatchMethod(Class<?> clazz, String methodName, Object ... params)
    {
        Method[] methods = clazz.getDeclaredMethods();
        
        for(Method method : methods)
        {
            if(!method.getName().equals(methodName))
            {
                continue;
            }
            
            Class<?>[] paramTypes = method.getParameterTypes();

            if(paramTypes.length != params.length)
            {
                continue;
            }
            
            boolean matchParamType = true;
            for(int index = 0; index < params.length; index ++)
            {
                if(!paramTypes[index].isInstance(params[index]))
                {
                    matchParamType = false;
                    break;
                }
            }
            
            if(matchParamType)
            {
                return method;
            }
        }
        
        return null;
    }
    
    
    /**
     * 設定ファイル内にあるinclude設定情報から、指定したコンバータ／対象クラスと合致する設定を
     * 取得する。
     * @param convertedTargetName コンバート対象クラス
     * @param convertClassName    コンバータのクラス
     * @param pool                クラスプール
     * @param convertCtClass      コンバート対象クラスのクラスファイル情報
     * @param transformConfig     ファイルから読み込んだ設定情報
     * @return 条件に合致するinclude設定情報
     */
    private static IncludeConversionConfig findMatchConversionConfig(
            String    convertedTargetName,
            String    convertClassName,
            ClassPool pool,
            CtClass   convertCtClass,
            JavelinTransformConfig transformConfig)
    {
        List<IncludeConversionConfig> includeClassList = 
            transformConfig.matchesToInclude(convertedTargetName, convertCtClass, pool);
        
        for(IncludeConversionConfig inConfig : includeClassList)
        {
            List<String> converterList = inConfig.getConverterNameList();
            
            for(String converterId : converterList)
            {
                List<String> converterClassList = transformConfig.getConverterClassNames(converterId);
            
                if(converterClassList.contains(convertClassName))
                {
                    return inConfig;
                }
            }
        }
        
        return null;
    }

    /**
     * 指定されたクラスからの相対パスで指定されたファイルまたはディレクトリを、絶対パスに変換します。
     *
     * @param clazz 基準クラス
     * @param relative 相対パス
     * @return 絶対パス
     */
    public static String getAbsolutePath(final Class<?> clazz, final String relative)
    {
        URL url = clazz.getResource(relative);
        String absolutePath = url.getFile().replaceAll("^/+", "");
        return absolutePath;
    }
}
