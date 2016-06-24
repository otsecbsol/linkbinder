#!/bin/bash

. ./env.sh

LB_OPTS="--resource-dir ${LB_RESOURCE_DIR}"
if [ "$OS" = "Windows_NT" ]; then
    LB_RESOURCE_DIR=`{ cd $LB_RESOURCE_DIR && pwd -W; }`
    LB_OPTS="--resource-dir ${LB_RESOURCE_DIR}"
fi

java -jar linkbinder-subscriber/build/libs/linkbinder-subscriber*.jar $LB_OPTS

