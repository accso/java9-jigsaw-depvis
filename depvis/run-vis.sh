. ./env.sh

# DepVis config properties file
PROPS_FILENAME=depvis.properties

# Names of the dot files produced by JigsawDepVisualizer (without and with legend)
DOT_OUTPUTFILENAME=$(grep outputFileName ${PROPS_FILENAME} | sed s/'depvis.outputFileName='//g | sed s/'.dot'/''/g)
DOT_OUTPUTFILENAME_LEGEND=$(grep outputFileName ${PROPS_FILENAME} | sed s/'depvis.outputFileName='//g | sed s/'.dot'/'-withlegend'/g)


# run the module dependency visualizer (producing a GraphViz dot file)
echo "Running JigsawDepVisualizer to produce ${DOT_OUTPUTFILENAME}.dot and ${DOT_OUTPUTFILENAME_LEGEND}.dot"
$JAVA_HOME/bin/java --module-path ${DEPVIS_MODULE_PATH}${PATH_SEPARATOR}${DEPVIS_AUTOMATIC_MODULE_PATH} --module depvis/depvis.JigsawDepVisualizer ${PROPS_FILENAME}

# ... and then run GraphViz to render to two PNG image files
IMAGE_OUTPUTFORMAT=png
echo "Running Graphviz to produce ${DOT_OUTPUTFILENAME}.${IMAGE_OUTPUTFORMAT}"
${GRAPHVIZ_HOME}/bin/dot -T${IMAGE_OUTPUTFORMAT} ${DOT_OUTPUTFILENAME}.dot -o ${DOT_OUTPUTFILENAME}.${IMAGE_OUTPUTFORMAT}
echo "Running Graphviz to produce ${DOT_OUTPUTFILENAME_LEGEND}.${IMAGE_OUTPUTFORMAT}"
${GRAPHVIZ_HOME}/bin/dot -T${IMAGE_OUTPUTFORMAT} ${DOT_OUTPUTFILENAME_LEGEND}.dot -o ${DOT_OUTPUTFILENAME_LEGEND}.${IMAGE_OUTPUTFORMAT}
