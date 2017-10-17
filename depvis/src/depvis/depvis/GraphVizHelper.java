package depvis;

import static org.kohsuke.graphviz.Attribute.COLOR;
import static org.kohsuke.graphviz.Attribute.FONTCOLOR;
import static org.kohsuke.graphviz.Attribute.FONTNAME;
import static org.kohsuke.graphviz.Attribute.SHAPE;
import static org.kohsuke.graphviz.Attribute.SIZE;
import static org.kohsuke.graphviz.Attribute.STYLE;
import static org.kohsuke.graphviz.Attribute.WEIGHT;
import static org.kohsuke.graphviz.Shape.ELLIPSE;
import static org.kohsuke.graphviz.Shape.HEXAGON;
import static org.kohsuke.graphviz.Shape.OCTAGON;
import static org.kohsuke.graphviz.StyleAttr.DASHED;
import static org.kohsuke.graphviz.StyleAttr.DOTTED;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.module.ModuleDescriptor;
import java.util.Date;

import org.kohsuke.graphviz.Graph;
import org.kohsuke.graphviz.Style;

/**
 * DepVis visualization tool for Java 9 Jigsaw modules
 * see https://github.com/accso/java9-jigsaw-depvis
 * 
 * Helper class for the GraphViz output
 */
class GraphVizHelper {
    static Graph graph = new Graph();

    private static Style nodeStyle          = new Style();
    private static Style nodeAutomaticStyle = new Style();
    private static Style nodeOpenStyle      = new Style();

    static Style graphStyle         = new Style();
    static Style reqStyle           = new Style();
    static Style reqMandatedStyle   = new Style();
    static Style reqStaticStyle     = new Style();
    static Style reqTransitiveStyle = new Style();
    static Style exportsToStyle     = new Style();
    static Style opensToStyle       = new Style();

    // GraphViz styles for the nodes (= modules) and the edges (= requires,exports,opens relationships)
    static {
        graphStyle.attr(SIZE, Float.valueOf(320.0f));
        graph.id("Java9 Dependency Visualizer");

        nodeStyle.attr(FONTNAME,"Consolas");
        nodeStyle.attr(SHAPE, ELLIPSE);

        nodeAutomaticStyle.attr(FONTNAME,"Consolas");     
        nodeAutomaticStyle.attr(SHAPE, HEXAGON);

        nodeOpenStyle.attr(FONTNAME,"Consolas");     
        nodeOpenStyle.attr(SHAPE, OCTAGON);

        reqMandatedStyle.attr(COLOR, Color.BLUE);
        reqMandatedStyle.attr(FONTNAME,"Consolas");	   
        reqMandatedStyle.attr(FONTCOLOR, Color.BLUE);
        reqMandatedStyle.attr(STYLE, DASHED);
        reqMandatedStyle.attr(WEIGHT, 16);

        reqStaticStyle.attr(COLOR, Color.BLUE);
        reqStaticStyle.attr(FONTNAME,"Consolas");	   
        reqStaticStyle.attr(FONTCOLOR, Color.BLUE);
        reqStaticStyle.attr(STYLE, DOTTED);
        reqStaticStyle.attr(WEIGHT, 16);

        reqStyle.attr(COLOR, Color.BLUE);
        reqStyle.attr(FONTNAME,"Consolas");	   
        reqStyle.attr(FONTCOLOR, Color.BLUE);
        reqStyle.attr(WEIGHT, 8);

        reqTransitiveStyle.attr(COLOR, Color.GREEN);
        reqTransitiveStyle.attr(FONTNAME,"Consolas");	   
        reqTransitiveStyle.attr(FONTCOLOR, Color.GREEN);
        reqTransitiveStyle.attr(WEIGHT, 4);

        exportsToStyle.attr(FONTNAME,"Consolas");	   
        exportsToStyle.attr(FONTCOLOR, Color.RED);
        exportsToStyle.attr(COLOR, Color.RED);
        exportsToStyle.attr(WEIGHT, 2);

        opensToStyle.attr(FONTNAME,"Consolas");	   
        opensToStyle.attr(FONTCOLOR, Color.ORANGE);
        opensToStyle.attr(COLOR, Color.ORANGE);
        opensToStyle.attr(WEIGHT, 2);
    }

    // finally write the GraphViz output to the output file
    static void writeGraphToFile() throws Exception {
        File dotFile = new File(JigsawDepConfiguration.outputFileName);
        File legendFile = new File(dotFile.getCanonicalFile().getParent(), 
                                   dotFile.getCanonicalFile().getName() . replace (".dot", "-withlegend.dot"));

        String comment = "# Created by " + JigsawDepVisualizer.class.getCanonicalName() + " on " + new Date();

        graph.writeTo(new FileOutputStream(dotFile),    comment, "");
        graph.writeTo(new FileOutputStream(legendFile), comment, getLegend());
    }

    // get the node style for a Jigsaw module
    static Style getNodeStyle(ModuleDescriptor modDesc) {
        if (modDesc!=null && modDesc.isAutomatic()) {
            return GraphVizHelper.nodeAutomaticStyle;
        }
        if (modDesc!=null && modDesc.isOpen()) {
            return GraphVizHelper.nodeOpenStyle;
        }
        return GraphVizHelper.nodeStyle;
    }
    
    // create a GraphViz legend
    //   note that this flips the output because of rankdir=LR
    private static String getLegend() {
        StringBuilder sb = new StringBuilder();

        String diagramTitle = JigsawDepConfiguration.diagramTitle + "\n" + new Date().toString();

        sb.append("rankdir=LR\n");
        sb.append("node [shape=plaintext]\n");
        sb.append("subgraph cluster_01 { \n");
        sb.append("  label = \"" + diagramTitle + "\"\n");
        sb.append("  fontname=\"Consolas\"\n");
        sb.append("  key [label=<<table border=\"0\" cellpadding=\"2\" cellspacing=\"0\" cellborder=\"0\">\n");
        sb.append("    <tr><td align=\"right\" port=\"i1\">requires</td></tr>\n");
        sb.append("    <tr><td align=\"right\" port=\"i2\">requires (mandated)</td></tr>\n");
        sb.append("    <tr><td align=\"right\" port=\"i3\">requires static</td></tr>\n");
        sb.append("    <tr><td align=\"right\" port=\"i4\">requires transitive</td></tr>\n");
        sb.append("    <tr><td align=\"right\" port=\"i5\">exports to</td></tr>\n");
        sb.append("    <tr><td align=\"right\" port=\"i6\">opens to</td></tr>\n");
        sb.append("    </table>>,fontname=\"Consolas\"]\n");
        sb.append("  key2 [label=<<table border=\"0\" cellpadding=\"2\" cellspacing=\"0\" cellborder=\"0\">\n");
        sb.append("    <tr><td port=\"i1\">&nbsp;</td></tr>\n");
        sb.append("    <tr><td port=\"i2\">&nbsp;</td></tr>\n");
        sb.append("    <tr><td port=\"i3\">&nbsp;</td></tr>\n");
        sb.append("    <tr><td port=\"i4\">&nbsp;</td></tr>\n");
        sb.append("    <tr><td port=\"i5\">&nbsp;</td></tr>\n");
        sb.append("    <tr><td port=\"i6\">&nbsp;</td></tr>\n");
        sb.append("    </table>>,fontname=\"Consolas\"]\n");
        sb.append("  key:i1:e -> key2:i1:w [color=BLUE]\n");
        sb.append("  key:i2:e -> key2:i2:w [color=BLUE, style=dashed]\n");
        sb.append("  key:i3:e -> key2:i3:w [color=BLUE, style=dotted]\n");
        sb.append("  key:i4:e -> key2:i4:w [color=GREEN]\n");
        sb.append("  key:i5:e -> key2:i5:w [color=RED]\n");
        sb.append("  key:i6:e -> key2:i6:w [color=ORANGE]\n");
        sb.append("}");

        return sb.toString();
    }
}
