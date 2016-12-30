# DepVis environment settings

# configure paths here
# JAVA_HOME=/a/JDK/jdk1.9.0_ea-b144-x64_20161111_build5709
JAVA_HOME=/a/JDK/jdk1.9.0_ea-b148-x64_20161213_build5846
GRAPHVIZ_HOME=/a/GraphViz/graphviz-2.38 

# ---------------------------------------------------------
# no need to change anything beyond this line

PATH=$JAVA_HOME/bin:$PATH 

DEPVIS_VERSION=0.2
DEPVIS_JAR=depvis-${DEPVIS_VERSION}.jar

DEPVIS_MOD_PATH=./mods
DEPVIS_MODULE_PATH=./mlib
DEPVIS_AUTOMATIC_MODULE_PATH=./amlib

GRAPHVIZ_API_VERSION=1.2.1
GRAPHVIZ_API_JAR=graphviz-api-${GRAPHVIZ_API_VERSION}.jar
