package org.kohsuke.graphviz;

/**
 * Shape of the arrow head.
 *
 * <p>
 * See http://www.graphviz.org/doc/info/arrows.html for the complete list.
 *
 * @author Kohsuke Kawaguchi
 * @see Attribute#ARROWHEAD
 * @see Attribute#ARROWTAIL
 */
public enum Arrow {
    BOX, CROW, DIAMOND, DOT, INV, NONE, NORMAL, TEE, VEE,
    OBOX,OCROW,ODIAMOND,ODOT,OINV,      ONORMAL,OTEE,OVEE
}
