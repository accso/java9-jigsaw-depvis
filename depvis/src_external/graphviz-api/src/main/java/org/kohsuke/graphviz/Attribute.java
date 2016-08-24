package org.kohsuke.graphviz;

import java.awt.*;

/**
 * Type safe representation of graph attributes.
 *
 * <p>
 * See http://www.graphviz.org/doc/info/attrs.html
 * for more documentation and what each attribute means.
 * 
 * @author Kohsuke Kawaguchi
 */
public abstract class Attribute<V> {
    public final String name;

    Attribute(String name) {
        this.name = name;
    }

    public abstract String toString(V value);
    public abstract V fromString(String value);

    private static final class EnumAttribute<T extends Enum<T>> extends Attribute<T> {
        private final Class<T> type;

        EnumAttribute(String name, Class<T> type) {
            super(name);
            this.type = type;
        }

        public String toString(T value) {
            if(value==null)     return null;
            return value.toString().toLowerCase();
        }

        public T fromString(String value) {
            if(value==null) return null;
            return Enum.valueOf(type,name.toUpperCase());
        }
    }

    private static final class StringAttribute extends Attribute<String> {
        StringAttribute(String name) {
            super(name);
        }

        public String toString(String value) {
            return value;
        }

        public String fromString(String value) {
            return value;
        }
    }

    private static final class FloatAttribute extends Attribute<Float> {
        FloatAttribute(String name) {
            super(name);
        }

        public String toString(Float value) {
            if(value==null)     return null;
            return value.toString();
        }

        public Float fromString(String value) {
            if(value==null)     return null;
            return Float.valueOf(value);
        }
    }

    private static final class IntAttribute extends Attribute<Integer> {
        IntAttribute(String name) {
            super(name);
        }

        public String toString(Integer value) {
            if(value==null)     return null;
            return value.toString();
        }

        public Integer fromString(String value) {
            if(value==null)     return null;
            return Integer.valueOf(value);
        }
    }

    private static final class BooleanAttribute extends Attribute<Boolean> {
        BooleanAttribute(String name) {
            super(name);
        }

        public String toString(Boolean value) {
            if(value==null)     return null;
            return value.toString();
        }

        public Boolean fromString(String value) {
            if(value==null)     return null;
            return Boolean.valueOf(value);
        }
    }

    private static final class ColorAttribute extends Attribute<Color> {
        ColorAttribute(String name) {
            super(name);
        }

        public String toString(Color value) {
            if(value==null)     return null;
            return String.format("#%02x%02x%02x",value.getRed(), value.getGreen(), value.getBlue());
        }

        public Color fromString(String value) {
            if(value==null)     return null;
            return Color.decode(value.substring(1));
        }
    }

    //
    //  Node attributes
    //
    public static final ColorAttribute COLOR = new ColorAttribute("color");
    public static final StringAttribute COMMENT = new StringAttribute("comment");
    public static final FloatAttribute DISTORTION = new FloatAttribute("distortion");
    public static final ColorAttribute FILLCOLOR = new ColorAttribute("fillcolor");
    public static final BooleanAttribute FIXEDSIZE = new BooleanAttribute("fixedsize");
    public static final ColorAttribute FONTCOLOR = new ColorAttribute("fontcolor");
    public static final StringAttribute FONTNAME = new StringAttribute("fontname");
    public static final FloatAttribute FONTSIZE = new FloatAttribute("fontsize");
    public static final StringAttribute GROUP = new StringAttribute("group");
    public static final FloatAttribute HEIGHT = new FloatAttribute("height");
    public static final StringAttribute LABEL = new StringAttribute("label");
    public static final StringAttribute LAYER = new StringAttribute("layer");
    public static final FloatAttribute NODE_ORIENTATION = new FloatAttribute("orientation");
    public static final BooleanAttribute REGULAR = new BooleanAttribute("regular");
    public static final EnumAttribute<Shape> SHAPE = new EnumAttribute<Shape>("shape",Shape.class);
    public static final IntAttribute SIDES = new IntAttribute("sides");
    public static final FloatAttribute SKEW = new FloatAttribute("skew");
    public static final EnumAttribute<StyleAttr> STYLE = new EnumAttribute<StyleAttr>("style",StyleAttr.class);
    public static final StringAttribute URL = new StringAttribute("URL");
    public static final FloatAttribute WIDTH = new FloatAttribute("width");
    public static final FloatAttribute Z = new FloatAttribute("z");


    //
    //  Edge attributes
    //
    public static final EnumAttribute<Arrow> ARROWHEAD = new EnumAttribute<Arrow>("arrowhead",Arrow.class);
    public static final FloatAttribute ARROWSIZE = new FloatAttribute("arrowsize");
    public static final EnumAttribute<Arrow> ARROWTAIL = new EnumAttribute<Arrow>("arrowtail",Arrow.class);
    // COLOR
    // COMMENT
    public static final BooleanAttribute CONSTRAINT = new BooleanAttribute("constraint");
    public static final BooleanAttribute DECORATE = new BooleanAttribute("decorate");
    public static final EnumAttribute<Dir> DIR = new EnumAttribute<Dir>("dir",Dir.class);
    // FONTCOLOR
    // FONTNAME
    // FONTSIZE
    public static final StringAttribute HEADLABEL = new StringAttribute("headlabel");
    public static final EnumAttribute<Port> HEADPORT = new EnumAttribute<Port>("headport",Port.class);
    public static final StringAttribute HEADURL = new StringAttribute("headurl");
    // LABEL
    public static final FloatAttribute LABELANGLE = new FloatAttribute("labelangle");
    public static final FloatAttribute LABELDISTANCE = new FloatAttribute("labeldistance");
    public static final BooleanAttribute LABELFLOAT = new BooleanAttribute("labelfloat");
    public static final ColorAttribute LABELFONTCOLOR = new ColorAttribute("labelfontcolor");
    public static final StringAttribute LABELFONTNAME = new StringAttribute("labelfontname");
    public static final FloatAttribute LABELFONTSIZE = new FloatAttribute("labelfontsize");
    // LAYER
    public static final StringAttribute LHEAD = new StringAttribute("lhead");
    public static final StringAttribute LTAIL = new StringAttribute("ltail");
    public static final IntAttribute MINLEN = new IntAttribute("minlen");
    public static final StringAttribute SAMEHEAD = new StringAttribute("samehead");
    public static final StringAttribute SAMETAIL = new StringAttribute("sametail");
    // STYLE
    public static final StringAttribute TAILLABEL = new StringAttribute("taillabel");
    public static final EnumAttribute<Port> TAILPORT = new EnumAttribute<Port>("tailport",Port.class);
    public static final StringAttribute TAILURL = new StringAttribute("tailurl");
    public static final IntAttribute WEIGHT = new IntAttribute("weight");


    //
    //  Graph attributes
    //
    public static final ColorAttribute BGCOLOR = new ColorAttribute("bgcolor");
    public static final BooleanAttribute CENTER = new BooleanAttribute("center");
    public static final EnumAttribute<ClusterRank> CLUSTERRANK = new EnumAttribute<ClusterRank>("clusterrank",ClusterRank.class);
    // COLOR
    // COMMENT
    public static final BooleanAttribute COMPOUND = new BooleanAttribute("compund");
    // CONCENTRATE
    // FILLCOLOR
    // FONTCOLOR
    // FONTNAME
    public static final StringAttribute FONTPATH = new StringAttribute("fontpath");
    // FONTSIZE
    // LABEL
    public static final StringAttribute LABELJUST = new StringAttribute("labeljust");
    public static final StringAttribute LABELLOC = new StringAttribute("labelloc");
    // LAYERS
    public static final FloatAttribute MARGIN = new FloatAttribute("margin");
    public static final FloatAttribute MCLIMIT = new FloatAttribute("mclimit");
    public static final FloatAttribute NODESEP = new FloatAttribute("nodesep");
    public static final FloatAttribute NSLIMIT = new FloatAttribute("nslimit");
    public static final FloatAttribute NSLIMIT1 = new FloatAttribute("nslimit1");
    public static final StringAttribute ORDERING = new StringAttribute("ordering");
    public static final EnumAttribute<Orientation> GRAPH_ORIENTATION = new EnumAttribute<Orientation>("orientation",Orientation.class);
    public static final StringAttribute PAGE = new StringAttribute("page");
    public static final EnumAttribute<PageDir> PAGEDIR = new EnumAttribute<PageDir>("pagedir",PageDir.class);
    public static final EnumAttribute<Rank> RANK = new EnumAttribute<Rank>("rank",Rank.class);
    public static final EnumAttribute<RankDir> RANKDIR = new EnumAttribute<RankDir>("rankdir",RankDir.class);
    public static final FloatAttribute RANKSEP = new FloatAttribute("ranksep");
    // RATIO??
    public static final BooleanAttribute REMINCROSS = new BooleanAttribute("remincross");
    public static final BooleanAttribute ROTATE = new BooleanAttribute("rotate");
    public static final IntAttribute SAMPLEPOINTS = new IntAttribute("samplepoints");
    public static final IntAttribute SEARCHSIZE = new IntAttribute("searchsize");
    public static final FloatAttribute SIZE = new FloatAttribute("size");
    // STYLE
    // URL

}
