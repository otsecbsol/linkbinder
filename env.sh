#!/bin/bash

DOCKER_MACHINE_NAME=default

# -------------------------
# Link Binder options
# -------------------------
LB_PORT="8080"
LB_PREFIX="/"
LB_RESOURCE_DIR="/var/lib/linkbinder"
if [ "$OS" = "Windows_NT" ]; then
    LB_RESOURCE_DIR="/c/var/lib/linkbinder"
fi

# -------------------------------------------------
# database configurations for docker container
# -------------------------------------------------
JDBC_PROP="$LB_RESOURCE_DIR/jdbc.properties"
DB_HOST=`docker-machine ip default`
DB_PORT="1521"
DB_USER="linkbinder"
DB_PASS="linkbinder"

# create properties
if [ ! -f $JDBC_PROP ]; then
    echo "jdbc.propertiesを作成します。場所：${JDBC_PROP}"

    mkdir -p $LB_RESOURCE_DIR
    echo "jdbc.driverClassName=oracle.jdbc.driver.OracleDriver" > $JDBC_PROP
    echo "jdbc.url=jdbc:oracle:thin:${DB_USER}/${DB_PASS}@${DB_HOST}:${DB_PORT}" >> $JDBC_PROP
    echo "jdbc.username=${DB_USER}" >> $JDBC_PROP
    echo "jdbc.password=${DB_PASS}" >> $JDBC_PROP
fi



# -------------------------------------------------
# log configuration
# -------------------------------------------------
LOG_CONF="$LB_RESOURCE_DIR/logback.xml"

# copy config
if [ -f logback.xml ]; then
    echo "logback.xmlを配置します。場所：${LB_RESOURCE_DIR}"

    mkdir -p $LB_RESOURCE_DIR
    cp logback.xml $LB_RESOURCE_DIR/logback.xml
fi
if [ -f logback.groovy ]; then
    echo "logback.groovyを配置します。場所：${LB_RESOURCE_DIR}"

    mkdir -p $LB_RESOURCE_DIR
    cp logback.groovy $LB_RESOURCE_DIR/logback.groovy
fi

# -------------------------------------------------
# run
# -------------------------------------------------
docker-machine start $DOCKER_MACHINE_NAME
eval $(docker-machine env $DOCKER_MACHINE_NAME)
docker-compose up -d
