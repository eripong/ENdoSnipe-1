package javax.servlet.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.servlet.ServletOutputStream;

// Referenced classes of package javax.servlet.http:
//            NoBodyOutputStream, HttpServletResponse, Cookie

class NoBodyResponse implements HttpServletResponse
{

    NoBodyResponse(HttpServletResponse r)
    {
        resp = r;
        noBody = new NoBodyOutputStream();
    }

    void setContentLength()
    {
        if (!didSetContentLength)
            resp.setContentLength(noBody.getContentLength());
    }

    public void setContentLength(int len)
    {
        resp.setContentLength(len);
        didSetContentLength = true;
    }

    public void setCharacterEncoding(String charset)
    {
        resp.setCharacterEncoding(charset);
    }

    public void setContentType(String type)
    {
        resp.setContentType(type);
    }

    public String getContentType()
    {
        return resp.getContentType();
    }

    public ServletOutputStream getOutputStream()
        throws IOException
    {
        return noBody;
    }

    public String getCharacterEncoding()
    {
        return resp.getCharacterEncoding();
    }

    public PrintWriter getWriter()
        throws UnsupportedEncodingException
    {
        if (writer == null)
        {
            OutputStreamWriter w = new OutputStreamWriter(noBody, getCharacterEncoding());
            writer = new PrintWriter(w);
        }
        return writer;
    }

    public void setBufferSize(int size)
        throws IllegalStateException
    {
        resp.setBufferSize(size);
    }

    public int getBufferSize()
    {
        return resp.getBufferSize();
    }

    public void reset()
        throws IllegalStateException
    {
        resp.reset();
    }

    public void resetBuffer()
        throws IllegalStateException
    {
        resp.resetBuffer();
    }

    public boolean isCommitted()
    {
        return resp.isCommitted();
    }

    public void flushBuffer()
        throws IOException
    {
        resp.flushBuffer();
    }

    public void setLocale(Locale loc)
    {
        resp.setLocale(loc);
    }

    public Locale getLocale()
    {
        return resp.getLocale();
    }

    public void addCookie(Cookie cookie)
    {
        resp.addCookie(cookie);
    }

    public boolean containsHeader(String name)
    {
        return resp.containsHeader(name);
    }

    /**
     * @deprecated Method setStatus is deprecated
     */

    public void setStatus(int sc, String sm)
    {
        resp.setStatus(sc, sm);
    }

    public void setStatus(int sc)
    {
        resp.setStatus(sc);
    }

    public void setHeader(String name, String value)
    {
        resp.setHeader(name, value);
    }

    public void setIntHeader(String name, int value)
    {
        resp.setIntHeader(name, value);
    }

    public void setDateHeader(String name, long date)
    {
        resp.setDateHeader(name, date);
    }

    public void sendError(int sc, String msg)
        throws IOException
    {
        resp.sendError(sc, msg);
    }

    public void sendError(int sc)
        throws IOException
    {
        resp.sendError(sc);
    }

    public void sendRedirect(String location)
        throws IOException
    {
        resp.sendRedirect(location);
    }

    public String encodeURL(String url)
    {
        return resp.encodeURL(url);
    }

    public String encodeRedirectURL(String url)
    {
        return resp.encodeRedirectURL(url);
    }

    public void addHeader(String name, String value)
    {
        resp.addHeader(name, value);
    }

    public void addDateHeader(String name, long value)
    {
        resp.addDateHeader(name, value);
    }

    public void addIntHeader(String name, int value)
    {
        resp.addIntHeader(name, value);
    }

    /**
     * @deprecated Method encodeUrl is deprecated
     */

    public String encodeUrl(String url)
    {
        return encodeURL(url);
    }

    /**
     * @deprecated Method encodeRedirectUrl is deprecated
     */

    public String encodeRedirectUrl(String url)
    {
        return encodeRedirectURL(url);
    }

    private HttpServletResponse resp;

    private NoBodyOutputStream  noBody;

    private PrintWriter         writer;

    private boolean             didSetContentLength;
}
