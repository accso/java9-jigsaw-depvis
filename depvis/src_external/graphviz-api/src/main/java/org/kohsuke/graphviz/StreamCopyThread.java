package org.kohsuke.graphviz;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * {@link Thread} that copies {@link InputStream} to {@link OutputStream}.
 *
 * @author Kohsuke Kawaguchi
 */
class StreamCopyThread extends Thread {
    private final InputStream in;
    private final OutputStream out;

    public StreamCopyThread(String threadName, InputStream in, OutputStream out) {
        super(threadName);
        this.in = in;
        this.out = out;
    }

    public void run() {
        try {
            try {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0)
                    out.write(buf, 0, len);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            // TODO: what to do?
        }
    }
}
