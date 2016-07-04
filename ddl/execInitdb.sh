#!/bin/bash

DOCKER_MACHINE_NAME=default

ORA_SYS_PASSWD=oracle
ORA_HOME=/u01/app/oracle/product/11.2.0/xe
export ORACLE_HOME=$ORA_HOME
ORA_SID=XE
export ORACLE_SID=$ORA_HOME
DIR_DDL=/root/ddl

$ORA_HOME/bin/sqlplus sys/$ORA_SYS_PASSWD as sysdba @${DIR_DDL}/create_tablespace.sql >> ${DIR_DDL}/initdb.log
$ORA_HOME/bin/sqlplus sys/$ORA_SYS_PASSWD as sysdba @${DIR_DDL}/create_user.sql >> ${DIR_DDL}/initdb.log

# $ORA_HOME/sqlplus sys/$ORA_SYS_PASSWD@//${ORA_HOST}/${ORA_SID} as sysdba @${DIR_DDL}/create_tablespace.sql
# $ORA_HOME/sqlplus sys/$ORA_SYS_PASSWD@//${ORA_HOST}/${ORA_SID} as sysdba @${DIR_DDL}/create_user.sql
