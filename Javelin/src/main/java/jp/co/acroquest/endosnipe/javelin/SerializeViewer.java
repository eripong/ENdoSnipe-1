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
package jp.co.acroquest.endosnipe.javelin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

import jp.co.acroquest.endosnipe.javelin.bean.Component;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;

/**
 * シリアライズビューアー
 * シリアライズされたファイルを読み込み、情報を同じフォルダ内に書き出す。
 * @author acroquest
 *
 */
public class SerializeViewer
{
    private static Map<String, Component> BeanMap_;

    private static final long MILLIS = 1000 * 1000;

    /**
     * シリアライズされたファイルの中身を別ファイルに書き出すことで、Viewする。
     * @param args シリアライズされたファイル
     * 
     */
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("ファイルを絶対パスで指定してください。");
            return;
        }
        File inputFilePath = new File(args[0]);
        if (!inputFilePath.getName().equals("serialize.dat"))
        {
            System.out.println("ファイルの名称が間違っています。（serialize.dat）");
            return;
        }
        else if (inputFilePath.isFile() == false)
        {
            System.out.println("ファイルが存在するか、あるいはファイル名が間違っていないか確認してください。");
            return;
        }

        FileWriter fw = null;
        File deserializeFile = new File(inputFilePath.getParent() + "/deserializedFile.csv");

        //引数に指定されたdatファイルをデシリアライズする。
        try
        {
            BeanMap_ = MBeanManagerSerializer.deserializeFile(args[0]);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return;
        }
        try
        {
            fw = new FileWriter(deserializeFile.getPath());
        }
        catch (IOException ioex)
        {
            ioex.printStackTrace();
            return;
        }
        BufferedWriter bw = new BufferedWriter(fw);
        try
        {
            String titles =
                    new String("レスポンス," + "計測対象," + "クラス名," + "メソッド名," + "TAT閾値," + "CPU閾値,"
                            + "呼び出し回数,"                    
                            +"合計処理時間(積算),"+"平均処理時間(積算),"+"最大処理時間(積算)," +
                            "最小処理時間(積算),"+"合計CPU時間(積算),"+" 平均CPU時間(積算),"+
                            "最大CPU時間(積算),"+" 最小CPU時間(積算),"+"合計USER時間(積算),"+"平均USER時間(積算),"
                            +"最大USER時間(積算),"+"最小USER時間(積算),"
                            +"合計処理時間," + "平均処理時間," + "最大処理時間," + "最小処理時間,"
                            + "合計CPU時間," + "平均CPU時間," + "最大CPU時間," + "最小CPU時間," + "合計USER時間,"
                            + "平均USER時間," + "最大USER時間," + "最小USER時間," + "例外発生回数\n");
            bw.write(titles);

            //デシリアライズされたファイルに保存されている全Componentに対して、処理を行う。
            for (Component component : BeanMap_.values())
            {
                //１つのcomponent中に存在する全Invocationに対して、処理を行う。
                for (Invocation invocation : component.getAllInvocation())
                {
                    //Invocationから各要素を読み込む。
                    String str = getContentsFromInvocation(invocation).toString();
                    bw.write(str);
                    bw.flush();
                }
            }
        }
        catch (IOException ioex)
        {
            ioex.printStackTrace();
            return;
        }
        try
        {
            bw.close();
            fw.close();
        }
        catch (IOException ioex)
        {
            ioex.printStackTrace();
            return;
        }
    }

    /**Invocationから各要素を読み込む。*/
    private static StringBuilder getContentsFromInvocation(Invocation invocation)
    {
        StringBuilder sb = new StringBuilder();

        //レスポンスは、trueをON、falseをOFFとして出力する。
        if (invocation.isResponseGraphOutputTarget() == true)
        {
            sb.append("ON,");
        }
        else
        {
            sb.append("OFF,");
        }
        //計測対象は、「ON」「OFF」「NOT_SPECIFIED」の3種類を出力する。
        sb.append(invocation.getMeasurementTarget() + ",");
        sb.append(invocation.getClassName() + ",");
        sb.append("\"" + invocation.getMethodName() + "\"" + ",");

        //アラーム発生判定のTATの閾値の値が-1のときは、「未指定」と出力する。それ以外は、閾値の値を出力する。
        long TATThreshold = invocation.getAlarmThreshold();
        if (TATThreshold == -1)
        {
            sb.append("未指定,");
        }
        else
        {
            sb.append(TATThreshold + ",");
        }

        //警告を発生させるCPU時間の閾値の値が-1のときは、「未指定」と出力する。それ以外は、閾値の値を出力する。
        long CPUThreshold = invocation.getAlarmCpuThreshold();
        if (CPUThreshold == -1)
        {
            sb.append("未指定,");
        }
        else
        {
            sb.append(CPUThreshold + ",");
        }
        sb.append(invocation.getCount() + ",");
        
        //平均処理時間,平均CPU時間,平均USER時間は、小数３位まで出力する。それ以外は整数値を出力する。
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        
        sb.append(invocation.getAccumulatedTotal()+ ",");
       
        String accumulatedAverage = "0";
        if(invocation.getCount() == 0)
        {
            accumulatedAverage = decimalFormat.format(0);
        }
        else
        {
            accumulatedAverage = decimalFormat.format((double)invocation.getAccumulatedTotal()/invocation.getCount());
        }
        sb.append(accumulatedAverage+ ",");
        sb.append(invocation.getAccumulatedMaximum()+ ",");
        sb.append(invocation.getAccumulatedMinimum()+ ",");
        sb.append(invocation.getAccumulatedCpuTotal()/MILLIS+ ",");
        String accumulatedCpuAverage = decimalFormat.format((double)invocation.getAccumulatedCpuAverage()/MILLIS);
        sb.append(accumulatedCpuAverage+ ",");
        sb.append(invocation.getAccumulatedCpuMaximum()/MILLIS+ ",");
        sb.append(invocation.getAccumulatedCpuMinimum()/MILLIS+ ",");
        sb.append(invocation.getAccumulatedUserTotal()/MILLIS+ ",");
        String accumulatedUserAverage = decimalFormat.format((double)invocation.getAccumulatedUserAverage()/MILLIS);
        sb.append(accumulatedUserAverage + ",");
        sb.append(invocation.getAccumulatedUserMaximum()/MILLIS+ ",");
        sb.append(invocation.getAccumulatedUserMinimum()/MILLIS+ ",");
        
        sb.append(invocation.getTotal() + ",");

      
        //平均処理時間,平均CPU時間,平均USER時間は、小数３位まで出力する。
        String average = "0";
        if(invocation.getCount() == 0)
        {
            average = decimalFormat.format(0);
        }
        else
        {
            average = decimalFormat.format((double)invocation.getTotal() / invocation.getCount());
        }
        sb.append(average + ",");
        sb.append(invocation.getMaximum() + ",");
        sb.append(invocation.getMinimum() + ",");
        sb.append(invocation.getCpuTotal() / MILLIS + ",");
        //平均処理時間,平均CPU時間,平均USER時間は、小数３位まで出力する。
        String cpuAverage = decimalFormat.format((double)invocation.getCpuAverage() / MILLIS);
        sb.append(cpuAverage + ",");
        sb.append(invocation.getCpuMaximum() / MILLIS + ",");
        sb.append(invocation.getCpuMinimum() / MILLIS + ",");
        sb.append(invocation.getUserTotal() / MILLIS + ",");
        //平均処理時間,平均CPU時間,平均USER時間は、小数３位まで出力する。
        String userAverage = decimalFormat.format((double)invocation.getUserAverage() / MILLIS);
        sb.append(userAverage + ",");
        sb.append(invocation.getUserMaximum() / MILLIS + ",");
        sb.append(invocation.getUserMinimum() / MILLIS + ",");
        sb.append(invocation.getThrowableCount());
        sb.append("\n");
        return sb;
    }
}
