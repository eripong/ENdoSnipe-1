package javax.servlet.http;

import java.util.Enumeration;
import javax.servlet.ServletContext;

// Referenced classes of package javax.servlet.http:
//            HttpSessionContext

public interface HttpSession
{

    public abstract long getCreationTime();

    public abstract String getId();

    public abstract long getLastAccessedTime();

    public abstract ServletContext getServletContext();

    public abstract void setMaxInactiveInterval(int i);

    public abstract int getMaxInactiveInterval();

    /**
     * @deprecated Method getSessionContext is deprecated
     */

    public abstract HttpSessionContext getSessionContext();

    public abstract Object getAttribute(String s);

    /**
     * @deprecated Method getValue is deprecated
     */

    public abstract Object getValue(String s);

    public abstract Enumeration getAttributeNames();

    /**
     * @deprecated Method getValueNames is deprecated
     */

    public abstract String[] getValueNames();

    public abstract void setAttribute(String s, Object obj);

    /**
     * @deprecated Method putValue is deprecated
     */

    public abstract void putValue(String s, Object obj);

    public abstract void removeAttribute(String s);

    /**
     * @deprecated Method removeValue is deprecated
     */

    public abstract void removeValue(String s);

    public abstract void invalidate();

    public abstract boolean isNew();
}
