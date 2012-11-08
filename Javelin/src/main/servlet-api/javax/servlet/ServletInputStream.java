package javax.servlet;

import java.io.IOException;
import java.io.InputStream;

public abstract class ServletInputStream extends InputStream
{

    protected ServletInputStream()
    {
    }

    public int readLine(byte b[], int off, int len)
        throws IOException
    {
        if (len <= 0)
            return 0;
        int count = 0;
        int c;
        do
        {
            if ((c = read()) == -1)
                break;
            b[off++] = (byte)c;
            count++;
        }
        while (c != 10 && count != len);
        return count <= 0 ? -1 : count;
    }
}