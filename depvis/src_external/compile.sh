. ../env.sh

mkdir -p  ./graphviz-api/target

echo "javac -d ./graphviz-api/target -sourcepath graphviz-api/src \$(find graphviz-api/src -name \"*.java\")"
$JAVA_HOME/bin/javac -d ./graphviz-api/target -sourcepath graphviz-api/src $(find graphviz-api/src -name "*.java")

echo "jar --create --file=../${DEPVIS_AUTOMATIC_MODULE_PATH}/${GRAPHVIZ_API_JAR} -C ./graphviz-api/target ."
$JAVA_HOME/bin/jar --create --file=../${DEPVIS_AUTOMATIC_MODULE_PATH}/${GRAPHVIZ_API_JAR} -C ./graphviz-api/target .
