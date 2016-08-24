# DepVis environment settings

# configure paths here
JAVA_HOME=TODO/path/to/java/home/goes/here
GRAPHVIZ_HOME=TODO/path/to/graphviz2.38/home/goes/here

# ---------------------------------------------------------
# no need to change anything beyond this line

PATH=$JAVA_HOME/bin:$PATH 

DEPVIS_VERSION=0.1
DEPVIS_JAR=depvis-${DEPVIS_VERSION}.jar

DEPVIS_MOD_PATH=./mods
DEPVIS_MODULE_PATH=./mlib
DEPVIS_AUTOMATIC_MODULE_PATH=./amlib

GRAPHVIZ_API_VERSION=1.2.1
GRAPHVIZ_API_JAR=graphviz-api-${GRAPHVIZ_API_VERSION}.jar
