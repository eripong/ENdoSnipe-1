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

public class AutoFilterTarget
{
    private static Object lock__ = new Object();
    
    public static void largeAndComplexMethod()
    {
        boolean flag1 = false;
        boolean flag2 = true;
        int     data1 = 0;
        int     data2 = 0;
        
        data1 = (int)(Math.random() * 10000);
        data2 = (int)(Math.random() * 10000);
        
        flag1 = (data1 % 2 == 0) ? false : true;
        flag2 = (data2 % 3 == 0) ? false : true;
        
        if (flag1 == flag2)
        {
            if (data1 == data2)
            {
                if(data1 < 5000)
                {
                    if(flag1 == true)
                    {
                        System.out.println("result 1");
                        
                        int i = 1;
                        int j = 1;
                        
                        while(j < 100)
                        {
                            j = (int)(Math.random() * i);
                            i = i + (int)(Math.random() * 10);
                        }
                        
                        StringBuilder str = new StringBuilder();
                        str.append("abc");
                        str.append("bde");
                        str.append("ddd");
                        str.append("fff");
                        str.append("qqq");
                        str.append("adb");
                        str.append("r4e");
                        str.append("hrf");
                        str.append("adfge");
                        str.append("r43");
                        str.append(str.toString());
                        str.append(str.toString().substring(3, 7));
                        str.append(str.append("acb").toString());
                        str.append("");
                        str.append("abc");
                        str.append("bde");
                        str.append("ddd");
                        str.append("fff");
                        str.append("qqq");
                        str.append("adb");
                        str.append("r4e");
                        str.append("hrf");
                        str.append("adfge");
                        str.append("r43");
                        str.append("");
                        str.append("abc");
                        str.append("bde");
                        str.append("ddd");
                        str.append("fff");
                        str.append("qqq");
                        str.append("adb");
                        str.append("r4e");
                        str.append("hrf");
                        str.append("adfge");
                        str.append("r43");
                    }
                }
                else if(data2 > 5000)
                {
                    if(flag2 == false)
                    {
                        System.out.println("result 2");
                    }
                    
                    System.out.println("result 3");
                }
            }
            else
            {
                System.out.println("result 4");
            }
        }
    }
    
    public static void smallAndComplexMethod()
    {
        boolean flag1 = false;
        boolean flag2 = true;
        int     data1 = 0;
        int     data2 = 0;
        
        data1 = (int)(Math.random() * 10000);
        data2 = (int)(Math.random() * 10000);
        
        flag1 = (data1 % 2 == 0) ? false : true;
        flag2 = (data2 % 3 == 0) ? false : true;
        
        if (flag1 == flag2)
        {
            if (data1 == data2)
            {
                if(data1 < 5000)
                {
                    if(flag1 == true)
                    {
                        System.out.println("result 1");
                        
                        int i = 1;
                        int j = 1;
                        
                        while(j < 100)
                        {
                            j = (int)(Math.random() * i);
                            i = i + (int)(Math.random() * 10);
                        }
                    }
                }
                else if(data2 > 5000)
                {
                    if(flag2 == false)
                    {
                        System.out.println("result 2");
                    }
                    
                    System.out.println("result 3");
                }
            }
            else
            {
                System.out.println("result 4");
            }
        }
    }

    public static void largeAndSimpleMethod()
    {
        boolean flag1 = false;
        boolean flag2 = true;
        int     data1 = 0;
        int     data2 = 0;
        
        data1 = (int)(Math.random() * 10000);
        data2 = (int)(Math.random() * 10000);
        
        flag1 = (data1 % 2 == 0) ? false : true;
        flag2 = true;
        
        if(data1 < 5000)
        {
            if(flag1 == true)
            {
                System.out.println("result 1");
                
                int i = 1;
                int j = 1;
                
                while(j < 100)
                {
                    j = (int)(Math.random() * i);
                    i = i + (int)(Math.random() * 10);
                }
                
                StringBuilder str = new StringBuilder();
                str.append("abc");
                str.append("bde");
                str.append("ddd");
                str.append("fff");
                str.append("qqq");
                str.append("adb");
                str.append("r4e");
                str.append("hrf");
                str.append("adfge");
                str.append("r43");
                str.append(str.toString());
                str.append(str.toString().substring(3, 7));
                str.append(str.append("acb").toString());
                str.append("");
                str.append("abc");
                str.append("bde");
                str.append("ddd");
                str.append("fff");
                str.append("qqq");
                str.append("adb");
                str.append("r4e");
                str.append("hrf");
                str.append("adfge");
                str.append("r43");
                str.append("");
                str.append("abc");
                str.append("bde");
                str.append("ddd");
                str.append("fff");
                str.append("qqq");
                str.append("adb");
                str.append("r4e");
                str.append("hrf");
                str.append("adfge");
                str.append("r43");
            }
        }
    }

    
    public static void smallAndSimpleMethod()
    {
        boolean flag1 = false;
        boolean flag2 = true;
        int     data1 = 0;
        int     data2 = 0;
        
        data1 = (int)(Math.random() * 10000);
        data2 = (int)(Math.random() * 10000);
        
        flag1 = false;
        flag2 = false;
        
        if (flag1 == flag2)
        {
            if(data1 < 5000)
            {
                if(flag1 == true)
                {
                    System.out.println("result 1");
                    
                    int i = 1;
                    int j = 1;
                    
                    while(j < 100)
                    {
                        j = (int)(Math.random() * i);
                        i = i + (int)(Math.random() * 10);
                    }
                }
            }
            else if(data2 > 5000)
            {
                if(flag2 == false)
                {
                    System.out.println("result 2");
                }
                
                System.out.println("result 3");
            }
        }
    }
    
    public static void callSmallAndTatSmall()
    {
        for (int cnt = 0; cnt < 4; cnt ++)
        {
            AutoFilterTarget.tat800Method();
        }
        
        AutoFilterTarget.tat800Method();
    }
    
    public static void callSmallAndTatBig()
    {
        for (int cnt = 0; cnt < 5; cnt ++)
        {
            AutoFilterTarget.tat1200Method();
        }
        
        AutoFilterTarget.tat1200Method();
    }
    
    public static void callBigAndTatSmall()
    {
        for (int cnt = 0; cnt < 20; cnt ++)
        {
            AutoFilterTarget.tat100Method();
        }
        
        AutoFilterTarget.tat100Method();
    }

    public static void callBigAndTatBig()
    {
        for (int cnt = 0; cnt < 12; cnt ++)
        {
            AutoFilterTarget.tat900Method();
        }
        
        AutoFilterTarget.tat900Method();
    }
    
    public static void tat800Method()
    {
        tatXxxMethod(800);
    }

    public static void tat1200Method()
    {
        tatXxxMethod(1200);
    }

    public static void tat100Method()
    {
        tatXxxMethod(100);
    }

    public static void tat900Method()
    {
        tatXxxMethod(900);
    }

    
    public static void callTree60Num()
    {
        callTreeXxxNum(58);
    }
    
    public static void callTree120Num()
    {
        callTreeXxxNum(118);
    }

    public static void callTree450Num()
    {
        callTreeXxxNum(448);
    }
    
    private static void callTreeXxxNum(int num)
    {
        for(int cnt = 0; cnt < num; cnt ++)
        {
            innerMethod();
        }
    }
    
    private static void innerMethod()
    {
        synchronized (lock__)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException ex)
            {
            }
        }
    }
    
    private static void tatXxxMethod(long sleepMills)
    {
        synchronized (lock__)
        {
            try
            {
                Thread.sleep(sleepMills);
            }
            catch (InterruptedException ex)
            {
            }
        }
    }
}
