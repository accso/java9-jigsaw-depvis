. ./env.sh

# Config properties file
PROPS_FILENAME=depvis.properties

$JAVA_HOME/bin/java --module-path ${DEPVIS_MODULE_PATH}\;${DEPVIS_AUTOMATIC_MODULE_PATH} --module depvis/depvis.JigsawDepPrinter ${PROPS_FILENAME}
