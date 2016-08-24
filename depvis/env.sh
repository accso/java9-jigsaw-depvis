#JAVA_HOME=/a/JDK/jdk1.9.0_ea-b122-x64
JAVA_HOME=/a/JDK/jdk1.9.0_ea-b127-x64_20160721_build5308
#JAVA_HOME=/a/JDK/jdk1.9.0_ea-b131-x64_20160812_build5378
PATH=$JAVA_HOME/bin:$PATH 
GRAPHVIZ_HOME=/a/GraphViz/graphviz-2.38

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
