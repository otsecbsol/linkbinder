#!/bin/bash

DOCKER_MACHINE_NAME=default
# settings for Oracle
ORA_PORT=1521
ORA_APEX_PORT=18080

DOCKER_COMPOSE_CONF=./docker-compose.yml

echo " --------------------------------------------------"
echo "  DOCKER-LINKBINDER setup tool"
echo " "
echo "  Please specify your prefer settings."
echo " --------------------------------------------------"

echo -n " Oracleのポート番号を入力してください [デフォルト：$ORA_PORT]: "
read ora_port
if [ "$ora_port" == "" ]; then
    ora_port=$ORA_PORT
fi

echo -n " Oracle Application Expressのポート番号を入力してください  [デフォルト：$ORA_APEX_PORT]: "
read ora_apex_port
if [ "$ora_apex_port" == "" ]; then
    ora_apex_port=$ORA_APEX_PORT
fi

cat ${DOCKER_COMPOSE_CONF}.tmpl | sed -e "s#%%ORA_PORT%%#$ora_port#g" > ${DOCKER_COMPOSE_CONF}
sed -i -e "s#%%ORA_APEX_PORT%%#$ora_apex_port#g" ${DOCKER_COMPOSE_CONF}

echo "docker machineを起動します...."
# start docker machine
docker-machine start $DOCKER_MACHINE_NAME
eval $(docker-machine env $DOCKER_MACHINE_NAME)

echo "dockerコンテナをビルドします...."
# build and start containers
docker-compose build
docker-compose up -d

echo "dockerコンテナを起動しました"

