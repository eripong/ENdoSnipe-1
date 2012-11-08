package javax.servlet.http;

import java.util.Enumeration;

// Referenced classes of package javax.servlet.http:
//            HttpSession

/**
 * @deprecated Interface HttpSessionContext is deprecated
 */

public interface HttpSessionContext
{

    /**
     * @deprecated Method getSession is deprecated
     */

    public abstract HttpSession getSession(String s);

    /**
     * @deprecated Method getIds is deprecated
     */

    public abstract Enumeration getIds();
}
