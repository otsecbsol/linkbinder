#!/bin/bash

DOCKER_MACHINE_NAME=default

ORA_SYS_PASSWD=oracle
ORA_SYSTEM_PASSWD=oracle

CONTAINER_DB=db
ORA_HOME=/u01/app/oracle/product/11.2.0/xe
ORA_SID=XE
DIR_DDL=/root/ddl

echo -n "Oracle sysユーザーのパスワードを入力してください [デフォルト：$ORA_SYS_PASSWD]: "
read ora_sys_passwd
if [ "$ora_sys_passwd" == "" ]; then
    ora_sys_passwd=$ORA_SYS_PASSWD
fi

echo -n "Oracle systemユーザーのパスワードを入力してください  [デフォルト：$ORA_SYSTEM_PASSWD]: "
read ora_system_passwd
if [ "$ora_system_passwd" == "" ]; then
    ora_system_passwd=$ORA_SYSTEM_PASSWD
fi

echo "docker machineを起動します...."
# start docker machine
docker-machine start $DOCKER_MACHINE_NAME
eval $(docker-machine env $DOCKER_MACHINE_NAME)

echo "データベースを初期化します...."
# initdb
ORA_HOST=`docker-machine ip $DOCKER_MACHINE_NAME`
docker-compose run \
    -e ORACLE_HOME=$ORA_HOME \
    -e ORACLE_SID=$ORA_SID \
    $CONTAINER_DB \
   $ORA_HOME/bin/sqlplus sys/$ora_sys_passwd@//${ORA_HOST}/${ORA_SID} as sysdba @${DIR_DDL}/create_tablespace.sql
docker-compose run \
    -e ORACLE_HOME=$ORA_HOME \
    -e ORACLE_SID=$ORA_SID \
    $CONTAINER_DB \
   $ORA_HOME/bin/sqlplus sys/$ora_sys_passwd@//${ORA_HOST}/${ORA_SID} as sysdba @${DIR_DDL}/create_user.sql

# FIXME
# Windowsのdocker-composeでは上記では動作しなかったためシェルスクリプトを呼び出す
# ただし以下も動作しない...
# docker-compose run \
    # -d \
    # -e ORACLE_HOME=$ORA_HOME \
    # -e ORACLE_SID=$ORA_SID \
    # $CONTAINER_DB \
   # bash -c ${DIR_DDL}/execInitdb.sh
echo "データベースの初期化が完了しました"

