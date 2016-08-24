package org.kohsuke.graphviz;

/**
 * @author Kohsuke Kawaguchi
 */
public class Edge extends GraphObject<Edge> {
    public final Node src,dst;

    public Edge(Node src, Node dst) {
        this.src = src;
        this.dst = dst;
    }

    Edge self() {
        return this;
    }

    void write(Printer out) {
        out.print(out.id(src)+" -> "+out.id(dst));
        writeAttributes(out);
        out.println();
    }
}
