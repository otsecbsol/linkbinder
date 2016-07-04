#!/bin/bash

. ./env.sh

LB_OPTS="--port ${LB_PORT} --context-prefix ${LB_PREFIX} --resource-dir ${LB_RESOURCE_DIR}"
if [ "$OS" = "Windows_NT" ]; then
    LB_RESOURCE_DIR=`{ cd $LB_RESOURCE_DIR && pwd -W; }`
    LB_OPTS="--port ${LB_PORT} --context-prefix /${LB_PREFIX} --resource-dir ${LB_RESOURCE_DIR}"
fi

java -jar linkbinder-web/build/libs/linkbinder-web*.war $LB_OPTS

