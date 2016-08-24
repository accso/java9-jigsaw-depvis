. ./env.sh

if [ ! "${DEPVIS_MOD_PATH}" == "" ]; then
rm -rf ${DEPVIS_MOD_PATH}/*
mkdir -p ${DEPVIS_MOD_PATH}
fi

if [ ! "${DEPVIS_MODULE_PATH}" == "" ]; then
rm -rf ${DEPVIS_MODULE_PATH}/*.jar
mkdir -p ${DEPVIS_MODULE_PATH}
fi

if [ ! "${DEPVIS_AUTOMATIC_MODULE_PATH}" == "" ]; then
rm -rf ${DEPVIS_AUTOMATIC_MODULE_PATH}/*.jar
mkdir -p ${DEPVIS_AUTOMATIC_MODULE_PATH}
fi
