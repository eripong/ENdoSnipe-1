package javax.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class ServletResponseWrapper implements ServletResponse
{

    public ServletResponseWrapper(ServletResponse response)
    {
        if (response == null)
        {
            throw new IllegalArgumentException("Response cannot be null");
        }
        else
        {
            this.response = response;
            return;
        }
    }

    public ServletResponse getResponse()
    {
        return response;
    }

    public void setResponse(ServletResponse response)
    {
        if (response == null)
        {
            throw new IllegalArgumentException("Response cannot be null");
        }
        else
        {
            this.response = response;
            return;
        }
    }

    public void setCharacterEncoding(String charset)
    {
        response.setCharacterEncoding(charset);
    }

    public String getCharacterEncoding()
    {
        return response.getCharacterEncoding();
    }

    public ServletOutputStream getOutputStream()
        throws IOException
    {
        return response.getOutputStream();
    }

    public PrintWriter getWriter()
        throws IOException
    {
        return response.getWriter();
    }

    public void setContentLength(int len)
    {
        response.setContentLength(len);
    }

    public void setContentType(String type)
    {
        response.setContentType(type);
    }

    public String getContentType()
    {
        return response.getContentType();
    }

    public void setBufferSize(int size)
    {
        response.setBufferSize(size);
    }

    public int getBufferSize()
    {
        return response.getBufferSize();
    }

    public void flushBuffer()
        throws IOException
    {
        response.flushBuffer();
    }

    public boolean isCommitted()
    {
        return response.isCommitted();
    }

    public void reset()
    {
        response.reset();
    }

    public void resetBuffer()
    {
        response.resetBuffer();
    }

    public void setLocale(Locale loc)
    {
        response.setLocale(loc);
    }

    public Locale getLocale()
    {
        return response.getLocale();
    }

    private ServletResponse response;
}
