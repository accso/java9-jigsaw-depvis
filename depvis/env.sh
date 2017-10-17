# DepVis environment settings

# configure paths here
JAVA_HOME=TODO/path/to/java9-jdk/goes/here
GRAPHVIZ_HOME=TODO/path/to/graphviz2.38/goes/here
ECLIPSE_HOME=TODO/path/to/eclipse4.7.1.a/goes/here

# ---------------------------------------------------------
# no need to change anything beyond this line

PATH=$JAVA_HOME/bin:$PATH 

DEPVIS_VERSION=0.3
DEPVIS_JAR=depvis-${DEPVIS_VERSION}.jar

DEPVIS_MOD_PATH=./mods
DEPVIS_MODULE_PATH=./mlib
DEPVIS_AUTOMATIC_MODULE_PATH=./amlib

GRAPHVIZ_API_VERSION=1.2.1
GRAPHVIZ_API_JAR=graphviz-api-${GRAPHVIZ_API_VERSION}.jar
