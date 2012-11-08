package javax.servlet.http;

import java.security.Principal;
import java.util.Enumeration;
import javax.servlet.ServletRequest;

// Referenced classes of package javax.servlet.http:
//            Cookie, HttpSession

public interface HttpServletRequest
    extends ServletRequest
{

    public abstract String getAuthType();

    public abstract Cookie[] getCookies();

    public abstract long getDateHeader(String s);

    public abstract String getHeader(String s);

    public abstract Enumeration getHeaders(String s);

    public abstract Enumeration getHeaderNames();

    public abstract int getIntHeader(String s);

    public abstract String getMethod();

    public abstract String getPathInfo();

    public abstract String getPathTranslated();

    public abstract String getContextPath();

    public abstract String getQueryString();

    public abstract String getRemoteUser();

    public abstract boolean isUserInRole(String s);

    public abstract Principal getUserPrincipal();

    public abstract String getRequestedSessionId();

    public abstract String getRequestURI();

    public abstract StringBuffer getRequestURL();

    public abstract String getServletPath();

    public abstract HttpSession getSession(boolean flag);

    public abstract HttpSession getSession();

    public abstract boolean isRequestedSessionIdValid();

    public abstract boolean isRequestedSessionIdFromCookie();

    public abstract boolean isRequestedSessionIdFromURL();

    /**
     * @deprecated Method isRequestedSessionIdFromUrl is deprecated
     */

    public abstract boolean isRequestedSessionIdFromUrl();

    public static final String BASIC_AUTH = "BASIC";
    public static final String FORM_AUTH = "FORM";
    public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";
    public static final String DIGEST_AUTH = "DIGEST";
}
