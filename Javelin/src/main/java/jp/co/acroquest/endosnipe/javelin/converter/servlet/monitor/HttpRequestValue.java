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
package jp.co.acroquest.endosnipe.javelin.converter.servlet.monitor;
import java.util.Map;

public class HttpRequestValue
{
    private String pathInfo_;
    private String contextPath_;
    private String servletPath_;

    private String remoteHost_;
    private int    remotePort_;
    private String method_;
    private String queryString_;
    private String characterEncoding_;
    @SuppressWarnings("rawtypes")
    private Map    parameterMap_;
    
    public String getPathInfo()
    {
        return pathInfo_;
    }
    public void setPathInfo(String pathInfo)
    {
        pathInfo_ = pathInfo;
    }
    
    public String getContextPath()
    {
        return contextPath_;
    }
    public void setContextPath(String contextPath)
    {
        contextPath_ = contextPath;
    }
    
    public String getServletPath()
    {
        return servletPath_;
    }
    public void setServletPath(String servletPath)
    {
        servletPath_ = servletPath;
    }

    public String getRemoteHost()
    {
        return remoteHost_;
    }
    public void setRemoteHost(String remoteHost)
    {
        remoteHost_ = remoteHost;
    }

    public int getRemotePort()
    {
        return remotePort_;
    }
    public void setRemotePort(int remotePort)
    {
        remotePort_ = remotePort;
    }
    
    public String getMethod()
    {
        return method_;
    }
    public void setMethod(String method)
    {
        method_ = method;
    }
    
    public String getQueryString()
    {
        return queryString_;
    }
    public void setQueryString(String queryString)
    {
        queryString_ = queryString;
    }
    
    public String getCharacterEncoding()
    {
        return characterEncoding_;
    }
    public void setCharacterEncoding(String characterEncoding)
    {
        characterEncoding_ = characterEncoding;
    }
    
    @SuppressWarnings("rawtypes")
    public Map getParameterMap()
    {
        return parameterMap_;
    }
    @SuppressWarnings("rawtypes")
    public void setParameterMap(Map parameterMap)
    {
        parameterMap_ = parameterMap;
    }
}
