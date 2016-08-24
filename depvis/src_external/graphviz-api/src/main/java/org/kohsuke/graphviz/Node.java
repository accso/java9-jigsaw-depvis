package org.kohsuke.graphviz;

/**
 * @author Kohsuke Kawaguchi
 */
public class Node extends GraphObject<Node> {
    public Node(String name) {
        id(name);
        this.attr("label", name);
    }

    Node self() {
        return this;
    }

    void write(Printer out) {
        out.print(out.id(this));
        writeAttributes(out);
        out.println(';');
    }
}
