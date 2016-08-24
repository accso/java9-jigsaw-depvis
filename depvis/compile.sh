. ./env.sh

mkdir -p ${DEPVIS_MOD_PATH}
mkdir -p ${DEPVIS_MODULE_PATH}
mkdir -p ${DEPVIS_AUTOMATIC_MODULE_PATH}

# compile graphviz-api first, result is jar file in ./amlib/graphviz-api-1.2.1.jar
pushd src_external > /dev/null 2>&1
. ./compile.sh
popd > /dev/null 2>&1 

# now compile depvis
echo "javac --module-path ${DEPVIS_MODULE_PATH}\;${DEPVIS_AUTOMATIC_MODULE_PATH} -d ${DEPVIS_MOD_PATH} -modulesourcepath src \$(find src -name \"*.java\")"
$JAVA_HOME/bin/javac --module-path ${DEPVIS_MODULE_PATH}\;${DEPVIS_AUTOMATIC_MODULE_PATH} -d ${DEPVIS_MOD_PATH} -modulesourcepath src $(find src -name "*.java")

# ... and create a JAR file ./mlib/depvis-0.1.jar
pushd mods > /dev/null 2>&1
MODDIR=depvis
echo "jar --create --file=../${DEPVIS_MODULE_PATH}/${DEPVIS_JAR} --module-version ${DEPVIS_VERSION} -C ${MODDIR} ."
$JAVA_HOME/bin/jar --create --file=../${DEPVIS_MODULE_PATH}/${DEPVIS_JAR} --module-version ${DEPVIS_VERSION} -C ${MODDIR} . 
popd >/dev/null 2>&1
