/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elasticscript.factor.test;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import org.junit.Test;

/**
 *
 * @author ben
 */
public class IOUtil {
    @Test
    public void test(){}
    
    public static StringBuffer readResource(final URL resource)
    {
        StringBuffer result = null;
        InputStream is = null;

        try
        {
            is = resource.openStream();
            result = readResourceStream(is);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException ex)
                {
                }
            }
        }

        return (result == null) ? new StringBuffer() : result;
    }

    /**
     * Perform a Stream copy.
     * @param is
     * @param os
     */
    @SuppressWarnings("NestedAssignment")
    public static void copyStream(final InputStream is, final OutputStream os)
    {
        byte[] buf = new byte[1024];
        int num = 0;

        try
        {
            while ((num = is.read(buf)) != -1)
                os.write(buf, 0, num);
        }
        catch (IOException ex)
        {
        }
    }

    /**
     * @param cl
     */
    public static void closeStream(final Closeable cl)
    {
        try
        {
            if (cl != null)
                cl.close();
        }
        catch (IOException ex)
        {
        }
    }

    public static void closeStreamQuietly(final Closeable cl)
    {
        closeQuietly(cl);
    }
    
    public static void closeQuietly(final Closeable cl)
    {
        try
        {
            if (cl != null)
                cl.close();
        }
        catch (IOException ex)
        {
            // eat it
        }
    }

    public static void readAll(
        final InputStream is, final byte[] out, int off, int buflen)
        throws IOException
    {
        int pos = off;
        int len = buflen;

        while (len > 0)
        {
            final int read = is.read(out, off, len);

            if (read <= 0)
                throw new IOException("Premature end of stream!");

            pos += read;
            len -= read;
        }
    }
    
    /**
     * Read a resource into a StringBuffer
     * @param is
     * @return
     */
    public static StringBuffer readResourceStream(final InputStream is)
    {
        StringBuffer buffer = null;
        BufferedReader rdr = null;

        if (is != null)
        {
            InputStreamReader isr = new InputStreamReader(is);
            rdr = new BufferedReader(isr);
            buffer = readStream(rdr);
        }

        return buffer;
    }

    /**
     * Read a resource into a StringBuffer
     * @param rdr
     * @return
     */
    public static StringBuffer readStream(final BufferedReader rdr)
    {
        return readStream(rdr, false);
    }

    /**
     * Read a resource into a StringBuffer
     * @param rdr
     * @param preserveLine
     * @return
     */
    public static StringBuffer readStream(
        final BufferedReader rdr, boolean preserveLine)
    {
        StringBuffer buffer = new StringBuffer();

        try
        {
            if (rdr != null)
            {
                String line = rdr.readLine();

                while (line != null)
                {
                    buffer.append(line);
                    line = rdr.readLine();

                    if (preserveLine && (line != null))
                        buffer.append('\n');
                }

                rdr.close();
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            closeStream(rdr);
        }

        return buffer;
    }
}
